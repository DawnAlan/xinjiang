package com.cj.project.modular.treemodel.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.project.modular.fiducial.entity.FiducialBase;
import com.cj.project.modular.fiducial.service.FiducialBaseService;
import com.cj.project.modular.treemodel.param.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.cj.common.exception.CommonException;
import com.cj.project.modular.treemodel.entity.TreeModel;
import com.cj.project.modular.treemodel.mapper.TreeModelMapper;
import com.cj.project.modular.treemodel.service.TreeModelService;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 测点树Service接口实现类
 *
 * @author Lb
 * @date 2023/09/14 16:41
 **/
@Service
public class TreeModelServiceImpl extends ServiceImpl<TreeModelMapper, TreeModel> implements TreeModelService {


    private static final String ROOT_PARENT_ID = "0";

    @Resource
    private FiducialBaseService fiducialBaseService;

    @Override
    public List<Tree<String>> tree(TreeModelTreeParam treeModelTreeParam) {
        List<TreeModel> treeModelList = this.list(new LambdaQueryWrapper<TreeModel>()
                //项目code
                .eq(ObjectUtil.isNotEmpty(treeModelTreeParam.getProjectCode()), TreeModel::getProjectCode, treeModelTreeParam.getProjectCode())
                //树目录类型
                .eq(ObjectUtil.isNotEmpty(treeModelTreeParam.getCategory()), TreeModel::getCategory, treeModelTreeParam.getCategory())
                //绑定的测点id
                .eq(ObjectUtil.isNotEmpty(treeModelTreeParam.getPointId()),TreeModel::getPointId, treeModelTreeParam.getPointId())
                .orderByAsc(TreeModel::getSortCode)
        );
        List<TreeNode<String>> treeNodeList = treeModelList.stream().map(treeModel ->
                        new TreeNode<>(treeModel.getId(), treeModel.getParentId(),
                                treeModel.getNodeName(), treeModel.getSortCode()).setExtra(JSONUtil.parseObj(treeModel)))
                .collect(Collectors.toList());
        return TreeUtil.build(treeNodeList, "0");
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(TreeModelAddParam treeModelAddParam) {
        TreeModel treeModel = BeanUtil.toBean(treeModelAddParam, TreeModel.class);
        this.save(treeModel);
    }

    @Override
    public void addPointNode(TreePointNodeAddParam pointNodeAddParam) {
        FiducialBase fiducialBase = fiducialBaseService.queryEntity(pointNodeAddParam.getPointId());
        if (ObjectUtil.isEmpty(fiducialBase))
            throw new CommonException("找不到该测点ID");
        TreeModel treeModel = BeanUtil.toBean(pointNodeAddParam, TreeModel.class);
        treeModel.setNodeName(fiducialBase.getId());
        treeModel.setIsEnd(1);
        treeModel.setNodeInfo("测点编号");
        this.save(treeModel);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(TreeModelEditParam treeModelEditParam) {
        TreeModel treeModel = this.queryEntity(treeModelEditParam.getId());
        BeanUtil.copyProperties(treeModelEditParam, treeModel);
        this.updateById(treeModel);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<TreeModelIdParam> treeModelIdParamList) {
        List<String> treeNodeIds = CollStreamUtil.toList(treeModelIdParamList, TreeModelIdParam::getId);
        for (String treeNode : treeNodeIds
        ) {
            long childNodeCount = this.count(new LambdaQueryWrapper<TreeModel>().eq(TreeModel::getParentId, treeNode));
            if (childNodeCount > 0) {
                throw new CommonException("该层级下包含子集，请先删除子集再操作删除本层(" + treeNode + ")");
            }
        }
        // 执行删除
        this.removeByIds(CollStreamUtil.toList(treeModelIdParamList, TreeModelIdParam::getId));
    }

    @Override
    public void deletePointNode(List<String> pointIdList, String category) {
        LambdaQueryWrapper<TreeModel> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (ObjectUtil.isNotEmpty(category)) {
            lambdaQueryWrapper.eq(TreeModel::getCategory, category);
        }
        List<TreeModel> treeNodes = this.list(lambdaQueryWrapper.in(TreeModel::getPointId, pointIdList));
        // 执行删除
        this.removeByIds(CollStreamUtil.toList(treeNodes, TreeModel::getId));
    }

    @Override
    public TreeModel detail(TreeModelIdParam treeModelIdParam) {
        return this.queryEntity(treeModelIdParam.getId());
    }

    @Override
    public TreeModel queryEntity(String id) {
        TreeModel treeModel = this.getById(id);
        if (ObjectUtil.isEmpty(treeModel)) {
            throw new CommonException("测点树不存在，id值为：{}", id);
        }
        return treeModel;
    }

}
