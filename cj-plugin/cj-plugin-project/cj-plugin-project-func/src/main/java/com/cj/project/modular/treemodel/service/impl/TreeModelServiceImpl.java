package com.cj.project.modular.treemodel.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.common.pojo.CommonEntity;
import com.cj.project.api.fiducial.entity.FiducialBase;
import com.cj.project.modular.fiducial.service.FiducialBaseService;
import com.cj.project.modular.treemodel.enums.TreeModelEnum;
import com.cj.project.modular.treemodel.param.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.cj.common.exception.CommonException;
import com.cj.project.modular.treemodel.entity.TreeModel;
import com.cj.project.modular.treemodel.mapper.TreeModelMapper;
import com.cj.project.modular.treemodel.service.TreeModelService;

import javax.annotation.Resource;
import java.util.*;
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
                .eq(CommonEntity::getDeleteFlag, TreeModelEnum.NOT_DELETE)
                //绑定的测点id
                .eq(ObjectUtil.isNotEmpty(treeModelTreeParam.getPointId()), TreeModel::getPointId, treeModelTreeParam.getPointId())
                .orderByAsc(TreeModel::getSortCode)
        );
        if (StringUtils.isNotEmpty(treeModelTreeParam.getNodeName())) {
            List<TreeModel> list = this.list(new LambdaQueryWrapper<TreeModel>()
                    //项目code
                    .eq(ObjectUtil.isNotEmpty(treeModelTreeParam.getProjectCode()), TreeModel::getProjectCode, treeModelTreeParam.getProjectCode())
                    //树目录类型
                    .eq(ObjectUtil.isNotEmpty(treeModelTreeParam.getCategory()), TreeModel::getCategory, treeModelTreeParam.getCategory())
                    //绑定的测点id
                    .eq(ObjectUtil.isNotEmpty(treeModelTreeParam.getPointId()), TreeModel::getPointId, treeModelTreeParam.getPointId())
                    .like(TreeModel::getNodeName, treeModelTreeParam.getNodeName())
                    .orderByAsc(TreeModel::getSortCode)
            );
            List<TreeModel> treeModels = new ArrayList<>();
            for (TreeModel treeModel : list) {
                treeModels.addAll(queryParentIds(treeModel.getId(), treeModelList));
            }
            System.out.println("筛选后的集合：" + JSON.toJSONString(treeModels));
            List<TreeNode<String>> queryTreeNodeList = treeModels.stream().map(treeModel ->
                            new TreeNode<>(treeModel.getId(), treeModel.getParentId(),
                                    treeModel.getNodeName(), treeModel.getSortCode()).setExtra(JSONUtil.parseObj(treeModel)))
                    .collect(Collectors.toList());
            return TreeUtil.build(queryTreeNodeList, ROOT_PARENT_ID);
        }
        List<TreeNode<String>> treeNodeList = treeModelList.stream().map(treeModel ->
                        new TreeNode<>(treeModel.getId(), treeModel.getParentId(),
                                treeModel.getNodeName(), treeModel.getSortCode()).setExtra(JSONUtil.parseObj(treeModel)))
                .collect(Collectors.toList());
        return TreeUtil.build(treeNodeList, ROOT_PARENT_ID);
    }

    /**
     * 递归遍历获取指定菜单的所有父节点
     *
     * @param treeModelList 当前菜单
     * @param treeId        子菜单ID
     */
    public List<TreeModel> queryParentIds(String treeId, List<TreeModel> treeModelList) {
        //递归获取父级节点,不包含自己  第一次传值为当前节点id，递归时传入的是父节点id，所以是完整的
        List<TreeModel> resultTreeModelList = new ArrayList<>();
        this.treeOrgParent(treeModelList, treeId, resultTreeModelList);
        return resultTreeModelList;
    }

    /**
     * 递归获取父级ids
     *
     * @param treeModelList
     * @param treeId
     * @param resultTreeModelList
     */
    public void treeOrgParent(List<TreeModel> treeModelList, String treeId, List<TreeModel> resultTreeModelList) {
        for (TreeModel treeModel : treeModelList) {
            if (StringUtils.isEmpty(treeModel.getParentId())) {
                continue;
            }
            //判断是否有父节点
            if (treeId.equals(treeModel.getId())) {
                resultTreeModelList.add(treeModel);
                treeOrgParent(treeModelList, treeModel.getParentId(), resultTreeModelList);
            }
        }
    }

    /**
     * 添加节点
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(TreeModelDto treeModelDto) {
        String nodeName = treeModelDto.getNodeName();
        boolean exists = baseMapper.exists(new LambdaQueryWrapper<TreeModel>()
                .eq(TreeModel::getParentId, treeModelDto.getParentId())
                .eq(TreeModel::getDeleteFlag, TreeModelEnum.NOT_DELETE)
                .eq(TreeModel::getNodeName, nodeName)
                .last("limit 1"));
        if (exists) {
            throw new CommonException(300, "已存在同名节点 请勿重复创建");
        }
        TreeModel treeModel = BeanUtil.toBean(treeModelDto, TreeModel.class);
        this.save(treeModel);
        boolean isEndBoolean = baseMapper.exists(new LambdaQueryWrapper<TreeModel>()
                .eq(TreeModel::getParentId, treeModelDto.getParentId())
                .eq(CommonEntity::getDeleteFlag, TreeModelEnum.NOT_DELETE)
                .eq(TreeModel::getIsEnd, 1));
        if (isEndBoolean) {
            baseMapper.update(null, new LambdaUpdateWrapper<TreeModel>()
                    .eq(TreeModel::getId, treeModelDto.getParentId())
                    .set(TreeModel::getIsEnd, 0));
        }
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
    public void edit(TreeModelDto treeModelEditParam) {
        TreeModel treeModel = this.queryEntity(treeModelEditParam.getId());
        BeanUtil.copyProperties(treeModelEditParam, treeModel);
        this.updateById(treeModel);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<TreeModelTreeParam> treeModelIdParamList) {
        List<String> treeNodeIds = CollStreamUtil.toList(treeModelIdParamList, TreeModelTreeParam::getId);
        for (String treeNode : treeNodeIds
        ) {
            long childNodeCount = this.count(new LambdaQueryWrapper<TreeModel>().eq(TreeModel::getParentId, treeNode));
            if (childNodeCount > 0) {
                throw new CommonException("该层级下包含子集，请先删除子集再操作删除本层(" + treeNode + ")");
            }
        }
        // 执行删除
        this.removeByIds(CollStreamUtil.toList(treeModelIdParamList, TreeModelTreeParam::getId));
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
    public TreeModel detail(TreeModelTreeParam treeModelIdParam) {
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
