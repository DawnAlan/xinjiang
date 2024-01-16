package com.cj.project.modular.treemodel.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.common.pojo.CommonEntity;
import com.cj.project.api.fiducial.entity.FiducialBase;
import com.cj.project.api.treemodel.dto.TreeModelDto;
import com.cj.project.api.treemodel.dto.TreeModelTreeDto;
import com.cj.project.api.treemodel.dto.TreePointNodeAddDto;
import com.cj.project.api.treemodel.vo.TreeBaseVo;
import com.cj.project.modular.fiducial.service.FiducialBaseService;
import com.cj.project.modular.treemodel.enums.TreeModelEnum;
import com.cj.project.modular.treemodel.enums.TreeNodeTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.cj.common.exception.CommonException;
import com.cj.project.api.treemodel.entity.TreeModel;
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
    public List<Tree<String>> tree(TreeModelTreeDto treeModelTreeParam) {
        List<TreeModel> treeModelList = this.list(new LambdaQueryWrapper<TreeModel>()
                //项目code
                .eq(ObjectUtil.isNotEmpty(treeModelTreeParam.getProjectCode()), TreeModel::getProjectCode, treeModelTreeParam.getProjectCode())
                //树目录类型
                .eq(ObjectUtil.isNotEmpty(treeModelTreeParam.getCategory()), TreeModel::getCategory, treeModelTreeParam.getCategory())
                .eq(CommonEntity::getDeleteFlag, TreeModelEnum.NOT_DELETE.getValue())
                .orderByAsc(TreeModel::getSortCode)
        );
        if (StringUtils.isNotEmpty(treeModelTreeParam.getNodeName()) || ObjectUtil.isNotEmpty(treeModelTreeParam.getPointId())) {
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
                .eq(TreeModel::getDeleteFlag, TreeModelEnum.NOT_DELETE.getValue())
                .eq(TreeModel::getNodeName, nodeName)
                .last("limit 1"));
        if (exists) {
            throw new CommonException(300, "已存在同名节点，请勿重复创建");
        }
        TreeModel treeModel = BeanUtil.toBean(treeModelDto, TreeModel.class);
        this.save(treeModel);
        boolean isEndBoolean = baseMapper.exists(new LambdaQueryWrapper<TreeModel>()
                .eq(TreeModel::getParentId, treeModelDto.getParentId())
                .eq(CommonEntity::getDeleteFlag, TreeModelEnum.NOT_DELETE.getValue())
                .eq(TreeModel::getIsEnd, 1));
        if (isEndBoolean) {
            baseMapper.update(null, new LambdaUpdateWrapper<TreeModel>()
                    .eq(TreeModel::getId, treeModelDto.getParentId())
                    .set(TreeModel::getIsEnd, 0));
        }
        //todo 初始化树形结构数据是根据测点考证表来的 那么添加节点 是否也要保存到测点考证表一份
    }

    @Override
    public void addPointNode(TreePointNodeAddDto pointNodeAddParam) {
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
    public void delete(List<TreeModelTreeDto> treeModelIdParamList) {
        List<String> treeNodeIds = CollStreamUtil.toList(treeModelIdParamList, TreeModelTreeDto::getId);
        for (String treeNode : treeNodeIds
        ) {
            long childNodeCount = this.count(new LambdaQueryWrapper<TreeModel>().eq(TreeModel::getParentId, treeNode));
            if (childNodeCount > 0) {
                throw new CommonException("该层级下包含子集，请先删除子集再操作删除本层(" + treeNode + ")");
            }
        }
        // 执行删除
        this.removeByIds(CollStreamUtil.toList(treeModelIdParamList, TreeModelTreeDto::getId));
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
    public TreeModel detail(TreeModelTreeDto treeModelIdParam) {
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

    /**
     * 初始化树数据
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void generateTree(String projectCode) {
        //（1）查出所有的测点考证数据
        List<FiducialBase> list = fiducialBaseService.list(new LambdaQueryWrapper<FiducialBase>()
                .eq(CommonEntity::getDeleteFlag, TreeModelEnum.NOT_DELETE.getValue())
                .eq(FiducialBase::getProjectCode, projectCode));
        //（2）分别根据字段进行分组
        //分项工程->分部工程->仪器类型->测点编号
        /*log.debug(JSON.toJSONString(list));*/
        if (CollUtil.isEmpty(list) || list.isEmpty()) {
            throw new CommonException(300, "初始化数据失败:未查询到未删除的,项目编号为：" + projectCode + " 的数据");
        }
        //分项工程
        Map<String, List<FiducialBase>> subProjectListMap = list.stream().collect(Collectors.groupingBy(FiducialBase::getSubProject));
        //分部工程
        Set<TreeBaseVo> collect = list.stream().map(item -> {
            TreeBaseVo treeBaseVo = new TreeBaseVo();
            treeBaseVo.setSubProject(item.getSubProject());
            treeBaseVo.setItemProject(item.getItemProject());
            treeBaseVo.setInstrumentType(item.getInstrumentType());
            return treeBaseVo;
        }).collect(Collectors.toSet());
        //仪器类型
        Set<TreeBaseVo> treeBaseVoSet = list.stream().map(item -> {
            TreeBaseVo treeBaseVo = new TreeBaseVo();
            treeBaseVo.setSubProject(item.getSubProject());
            treeBaseVo.setItemProject(item.getItemProject());
            treeBaseVo.setInstrumentType(item.getInstrumentType());
            treeBaseVo.setPointName(item.getPointName());
            treeBaseVo.setMonitorName(item.getMonitorName());
            return treeBaseVo;
        }).collect(Collectors.toSet());
        //（3）拼装数据，
        //一级分项工程
        List<TreeModel> treeModelList = new ArrayList<>();
        TreeModel treeModel;
        //存储相关  key为节点名称  只为保存的id
        Map<String, String> idInfo = new HashMap<>();
        for (Map.Entry<String, List<FiducialBase>> stringListEntry : subProjectListMap.entrySet()) {
            //分项工程
            String subProject = stringListEntry.getKey();
            List<FiducialBase> valueList = stringListEntry.getValue();
            FiducialBase fiducialBase = valueList.stream().findFirst().get();
            treeModel = new TreeModel();
            long id = IdUtil.getSnowflakeNextId();
            treeModel.setId(String.valueOf(id));
            treeModel.setProjectCode(projectCode);
            treeModel.setIsEnd(0);
            treeModel.setParentId("0");
            treeModel.setNodeName(subProject);
            treeModel.setNodeInfo(fiducialBase.getInstrumentType());
            //工程结构树
            treeModel.setCategory("1");
            treeModel.setDeleteFlag(TreeModelEnum.NOT_DELETE.getValue());
            TreeNodeTypeEnum typeValue = TreeNodeTypeEnum.getTypeValue(fiducialBase.getInstrumentType());
            Integer type = BeanUtil.isNotEmpty(typeValue) ? typeValue.getType() : null;
            treeModel.setNodeType(type);
            idInfo.put(subProject + "..", String.valueOf(id));
            treeModel.setDeleteFlag("0");
            treeModelList.add(treeModel);
        }
        //分部工程
        for (TreeBaseVo baseVo : collect) {
            String subProject = baseVo.getSubProject();
            String itemProject = baseVo.getItemProject();
            treeModel = new TreeModel();
            long id = IdUtil.getSnowflakeNextId();
            treeModel.setId(String.valueOf(id));
            treeModel.setProjectCode(projectCode);
            String mapKey = subProject + "..";
            String parentId = idInfo.get(mapKey);
            if (parentId == null || StringUtils.isEmpty(parentId)) {
                continue;
            }
            treeModel.setParentId(parentId);
            treeModel.setNodeName(itemProject);
            treeModel.setNodeInfo(baseVo.getInstrumentType());
            //工程结构树
            treeModel.setCategory("1");
            TreeNodeTypeEnum typeValue = TreeNodeTypeEnum.getTypeValue(baseVo.getInstrumentType());
            Integer nodeType = BeanUtil.isNotEmpty(typeValue) ? typeValue.getType() : null;
            treeModel.setNodeType(nodeType);
            treeModel.setIsEnd(0);
            idInfo.put(mapKey + itemProject, String.valueOf(id));
            treeModel.setDeleteFlag("0");
            treeModelList.add(treeModel);
        }

        //仪器类型
        for (TreeBaseVo baseVo : collect) {
            String instrumentType = baseVo.getInstrumentType();
            String subProject = baseVo.getSubProject();
            String itemProject = baseVo.getItemProject();
            String mapKey = subProject + ".." + itemProject;
            String parentId = idInfo.get(mapKey);
            if (parentId == null || StringUtils.isEmpty(parentId)) {
                continue;
            }
            treeModel = new TreeModel();
            long id = IdUtil.getSnowflakeNextId();
            treeModel.setId(String.valueOf(id));
            treeModel.setProjectCode(projectCode);
            treeModel.setParentId(parentId);
            treeModel.setNodeName(instrumentType);
            treeModel.setNodeInfo(baseVo.getInstrumentType());
            //工程结构树
            treeModel.setCategory("1");
            TreeNodeTypeEnum typeValue = TreeNodeTypeEnum.getTypeValue(instrumentType);
            Integer type = BeanUtil.isNotEmpty(typeValue) ? typeValue.getType() : null;
            treeModel.setNodeType(type);
            treeModel.setIsEnd(0);
            idInfo.put(mapKey + ".." + instrumentType, String.valueOf(id));
            treeModel.setDeleteFlag("0");
            treeModelList.add(treeModel);
        }
        //测点编号
        for (TreeBaseVo baseVo : treeBaseVoSet) {
            String instrumentType = baseVo.getInstrumentType();
            String subProject = baseVo.getSubProject();
            String itemProject = baseVo.getItemProject();
            String pointName = baseVo.getPointName();
            String mapKey = subProject + ".." + itemProject + ".." + instrumentType;
            String parentId = idInfo.get(mapKey);
            if (parentId == null || StringUtils.isEmpty(parentId)) {
                continue;
            }
            treeModel = new TreeModel();
            long id = IdUtil.getSnowflakeNextId();
            treeModel.setId(String.valueOf(id));
            treeModel.setProjectCode(projectCode);
            treeModel.setParentId(parentId);
            treeModel.setNodeName(pointName);
            treeModel.setNodeInfo(baseVo.getInstrumentType());
            //工程结构树
            treeModel.setCategory("1");
            TreeNodeTypeEnum typeValue = TreeNodeTypeEnum.getTypeValue(instrumentType);
            Integer type = BeanUtil.isNotEmpty(typeValue) ? typeValue.getType() : null;
            treeModel.setNodeType(type);
            treeModel.setIsEnd(1);
            treeModel.setMonitorName(baseVo.getMonitorName());
            treeModel.setDeleteFlag("0");
            treeModelList.add(treeModel);
        }
        /*log.error("组装的数据有：" + JSON.toJSONString(treeModelList));
        List<TreeNode<String>> queryTreeNodeList = treeModelList.stream().map(treeModel1 ->
                        new TreeNode<>(treeModel1.getId(), treeModel1.getParentId(),
                                treeModel1.getNodeName(), treeModel1.getSortCode()).setExtra(JSONUtil.parseObj(treeModel1)))
                .collect(Collectors.toList());
        List<Tree<String>> build = TreeUtil.build(queryTreeNodeList, ROOT_PARENT_ID);
        log.error("组装的数据结构：" + JSON.toJSONString(build));*/
        //（4）逻辑删除历史数据
        this.update(new LambdaUpdateWrapper<TreeModel>()
                .eq(CommonEntity::getDeleteFlag, TreeModelEnum.NOT_DELETE.getValue())
                .eq(TreeModel::getProjectCode, projectCode)
                .set(CommonEntity::getDeleteFlag, TreeModelEnum.DELETE.getValue()));
        //（5）入库
        this.saveBatch(treeModelList);
    }

    /**
     * 拖拽变更同级节点排序
     *
     * @param treeModelDtoList 前端处理好的顺序结构
     * @return 变更状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean changeTreeSort(List<TreeModelDto> treeModelDtoList) {
        List<TreeModel> treeModelList = new ArrayList<>();
        for (int i = 0; i < treeModelDtoList.size(); i++) {
            TreeModelDto treeModelDto = treeModelDtoList.get(i);
            TreeModel treeModel = BeanUtil.copyProperties(treeModelDto, TreeModel.class);
            treeModel.setSortCode(i);
            treeModelList.add(treeModel);
        }
        return this.updateBatchById(treeModelList);
    }
}
