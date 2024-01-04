package com.cj.project.modular.instruments.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.dev.api.DevDictApi;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.cj.common.enums.CommonSortOrderEnum;
import com.cj.common.exception.CommonException;
import com.cj.common.page.CommonPageRequest;
import com.cj.project.modular.instruments.entity.ProjectInstruments;
import com.cj.project.modular.instruments.mapper.ProjectInstrumentsMapper;
import com.cj.project.modular.instruments.param.ProjectInstrumentsAddParam;
import com.cj.project.modular.instruments.param.ProjectInstrumentsEditParam;
import com.cj.project.modular.instruments.param.ProjectInstrumentsIdParam;
import com.cj.project.modular.instruments.param.ProjectInstrumentsPageParam;
import com.cj.project.modular.instruments.service.ProjectInstrumentsService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 项目仪器表Service接口实现类
 *
 * @author Lb
 * @date  2023/09/02 18:12
 **/
@Service
public class ProjectInstrumentsServiceImpl extends ServiceImpl<ProjectInstrumentsMapper, ProjectInstruments> implements ProjectInstrumentsService {

    @Resource
    private DevDictApi devDictApi;

    @Override
    public Page<ProjectInstruments> page(ProjectInstrumentsPageParam projectInstrumentsPageParam) {
        QueryWrapper<ProjectInstruments> queryWrapper = new QueryWrapper<>();
        if(ObjectUtil.isNotEmpty(projectInstrumentsPageParam.getProjectCode())) {
            queryWrapper.lambda().eq(ProjectInstruments::getProjectCode, projectInstrumentsPageParam.getProjectCode());
        }
        if(ObjectUtil.isNotEmpty(projectInstrumentsPageParam.getInstrumentType())) {
            queryWrapper.lambda().eq(ProjectInstruments::getInstrumentType, projectInstrumentsPageParam.getInstrumentType());
        }
        if(ObjectUtil.isAllNotEmpty(projectInstrumentsPageParam.getSortField(), projectInstrumentsPageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(projectInstrumentsPageParam.getSortOrder());
            queryWrapper.orderBy(true, projectInstrumentsPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(projectInstrumentsPageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(ProjectInstruments::getSortCode);
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Override
    public List<ProjectInstruments> getList(String projectCode, String monitorName, String instrumentType, String instrumentMetaType) {
        QueryWrapper<ProjectInstruments> queryWrapper = new QueryWrapper<>();
        if(ObjectUtil.isNotEmpty(projectCode)) {
            queryWrapper.lambda().eq(ProjectInstruments::getProjectCode, projectCode);
        }
        if(ObjectUtil.isNotEmpty(monitorName)) {
            queryWrapper.lambda().eq(ProjectInstruments::getMonitorName, monitorName);
        }
        if(ObjectUtil.isNotEmpty(instrumentType)) {
            queryWrapper.lambda().eq(ProjectInstruments::getInstrumentType, instrumentType);
        }
        if(ObjectUtil.isNotEmpty(instrumentMetaType)) {
            queryWrapper.lambda().eq(ProjectInstruments::getInstrumentMetaType, instrumentMetaType);
        }
        queryWrapper.lambda().orderByAsc(ProjectInstruments::getSortCode);

        return this.list(queryWrapper);
    }

    @Override
    public List<Tree<String>> tree(String projectCode) {
        List<ProjectInstruments> instruments = new ArrayList<>();
        instruments = this.list(new QueryWrapper<ProjectInstruments>().lambda().eq(ProjectInstruments::getProjectCode, projectCode));
        //monitor
        List<JSONObject> monitorDict = devDictApi.getDictByValue("INSTRUMENTMETATYPE");
        if(ObjectUtil.isEmpty(monitorDict))
            throw new CommonException("找不到平台仪器类型字典");
        for (JSONObject monitor : monitorDict
        ) {
            String monitorKey = monitor.getStr("dictLabel");
            String monitorCode = monitor.getStr("dictValue");
            String monitorId = monitor.getStr("id");
            Integer sortCode = Integer.valueOf(monitor.getStr("sortCode"));
            instruments.add(new ProjectInstruments(monitorId,"0",projectCode,monitorKey,monitorKey,monitorKey,monitorCode,
                    sortCode,null,null));
        }
        for (ProjectInstruments instrument : instruments
             ) {
            if("0".equals(instrument.getParentId()))
                continue;
            Optional<ProjectInstruments> monitor = instruments.stream().filter(s -> s.getInstrumentType().equals(instrument.getMonitorName()) && s.getParentId().equals("0")).findFirst();
            if(monitor.isPresent())
                instrument.setParentId(monitor.get().getId());
        }
        List<TreeNode<String>> treeNodeList = instruments.stream().map(instrument ->
                        new TreeNode<>(instrument.getId(), instrument.getParentId(),
                                instrument.getInstrumentType(), instrument.getSortCode()).setExtra(JSONUtil.parseObj(instrument)))
                .collect(Collectors.toList());
        System.out.println(JSON.toJSON(treeNodeList) + "/n");
        return TreeUtil.build(treeNodeList, "0");
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(ProjectInstrumentsAddParam projectInstrumentsAddParam) {
        //查询字典
        projectInstrumentsAddParam = CheckInstrumentName(projectInstrumentsAddParam);
        ProjectInstruments projectInstruments = BeanUtil.toBean(projectInstrumentsAddParam, ProjectInstruments.class);
        this.save(projectInstruments);
    }
    //查询检校平台仪器名称
    private ProjectInstrumentsAddParam CheckInstrumentName(ProjectInstrumentsAddParam projectInstrumentsAddParam)
    {
        List<JSONObject> monitorDict = devDictApi.getDictByValue("INSTRUMENTMETATYPE");
        if(ObjectUtil.isEmpty(monitorDict))
            throw new CommonException("找不到平台仪器类型字典");
        String monitorName = projectInstrumentsAddParam.getMonitorName();
        String instrumentMetaType = projectInstrumentsAddParam.getInstrumentMetaType();
        for (JSONObject monitor : monitorDict
        ) {
            String monitorKey = monitor.getStr("dictLabel");
            if(! monitorName.equals(monitorKey))
                continue;
            String monitorId = monitor.getStr("id");
            List<JSONObject>  devInstrumentDict = devDictApi.getDictByParentId(monitorId);
            for (JSONObject devInstrument : devInstrumentDict
            ) {
                String devInstrumentKey = devInstrument.getStr("dictLabel");
                if(! instrumentMetaType.equals(devInstrumentKey))
                    continue;
                String devInstrumentValue = devInstrument.getStr("dictValue");
                projectInstrumentsAddParam.setInstrumentCode(devInstrumentValue);
                break;
            }
            break;
        }
        if (ObjectUtil.isEmpty(projectInstrumentsAddParam.getInstrumentCode()))
            throw new CommonException("找不到该平台仪器类型，请检查监测类型及平台仪器名称");
        return projectInstrumentsAddParam;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(ProjectInstrumentsEditParam projectInstrumentsEditParam) {
        ProjectInstruments projectInstruments = this.queryEntity(projectInstrumentsEditParam.getId());
        BeanUtil.copyProperties(projectInstrumentsEditParam, projectInstruments);
        this.updateById(projectInstruments);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<ProjectInstrumentsIdParam> projectInstrumentsIdParamList) {
        // 执行删除
        this.removeByIds(CollStreamUtil.toList(projectInstrumentsIdParamList, ProjectInstrumentsIdParam::getId));
    }

    @Override
    public ProjectInstruments detail(ProjectInstrumentsIdParam projectInstrumentsIdParam) {
        return this.queryEntity(projectInstrumentsIdParam.getId());
    }

    @Override
    public ProjectInstruments queryEntity(String id) {
        ProjectInstruments projectInstruments = this.getById(id);
        if(ObjectUtil.isEmpty(projectInstruments)) {
            throw new CommonException("项目仪器表不存在，id值为：{}", id);
        }
        return projectInstruments;
    }
}
