package com.cj.project.modular.configfield.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.project.modular.configfield.param.*;
import com.cj.project.modular.configfield.result.ConfigFieldFiducialResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.cj.common.enums.CommonSortOrderEnum;
import com.cj.common.exception.CommonException;
import com.cj.common.page.CommonPageRequest;
import com.cj.project.modular.configfield.entity.ConfigFieldFiducial;
import com.cj.project.modular.configfield.mapper.ConfigFieldFiducialMapper;
import com.cj.project.modular.configfield.service.ConfigFieldFiducialService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 考证字段配置Service接口实现类
 *
 * @author Lb
 * @date  2023/08/31 19:28
 **/
@Service
public class ConfigFieldFiducialServiceImpl extends ServiceImpl<ConfigFieldFiducialMapper, ConfigFieldFiducial> implements ConfigFieldFiducialService {

    @Override
    public List<ConfigFieldFiducialResult> getList(ConfigFieldFiducialQueryParam configFieldFiducialQueryParam) {
        List<ConfigFieldFiducialResult> fieldFiducialResults = new ArrayList<>();
        //List
        QueryWrapper<ConfigFieldFiducial> queryWrapper = new QueryWrapper<>();
        if(ObjectUtil.isNotEmpty(configFieldFiducialQueryParam.getProjectCode())) {
            queryWrapper.lambda().eq(ConfigFieldFiducial::getProjectCode, configFieldFiducialQueryParam.getProjectCode());
        }
        if(ObjectUtil.isNotEmpty(configFieldFiducialQueryParam.getInstrumentType())) {
            queryWrapper.lambda().eq(ConfigFieldFiducial::getInstrumentType, configFieldFiducialQueryParam.getInstrumentType());
        }
        queryWrapper.lambda().orderByAsc(ConfigFieldFiducial::getSortCode);
        List<ConfigFieldFiducial> configFieldFiducials = this.list(queryWrapper);
        //Map
        Map<String,List<ConfigFieldFiducial>> fieldmap = configFieldFiducials.stream().collect(Collectors.groupingBy(ConfigFieldFiducial::getInstrumentType));
        for (String instrumenttype : fieldmap.keySet()
             ) {
            ConfigFieldFiducial defaultFieldFiducial = fieldmap.get(instrumenttype).stream().findFirst().get();
            ConfigFieldFiducialResult fiducialResult = new ConfigFieldFiducialResult();
            fiducialResult.setProjectCode(defaultFieldFiducial.getProjectCode());
            fiducialResult.setInstrumentType(instrumenttype);
            fiducialResult.setInstrumentMetaType(defaultFieldFiducial.getInstrumentMetaType());
            fiducialResult.setFieldConfigs(fieldmap.get(instrumenttype));
            fieldFiducialResults.add(fiducialResult);
        }
        return fieldFiducialResults;
    }

    @Override
    public Page<ConfigFieldFiducial> page(ConfigFieldFiducialPageParam configFieldFiducialPageParam) {
        QueryWrapper<ConfigFieldFiducial> queryWrapper = new QueryWrapper<>();
        if(ObjectUtil.isNotEmpty(configFieldFiducialPageParam.getProjectCode())) {
            queryWrapper.lambda().eq(ConfigFieldFiducial::getProjectCode, configFieldFiducialPageParam.getProjectCode());
        }
        if(ObjectUtil.isNotEmpty(configFieldFiducialPageParam.getInstrumentType())) {
            queryWrapper.lambda().eq(ConfigFieldFiducial::getInstrumentType, configFieldFiducialPageParam.getInstrumentType());
        }
        if(ObjectUtil.isAllNotEmpty(configFieldFiducialPageParam.getSortField(), configFieldFiducialPageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(configFieldFiducialPageParam.getSortOrder());
            queryWrapper.orderBy(true, configFieldFiducialPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(configFieldFiducialPageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(ConfigFieldFiducial::getId);
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(ConfigFieldFiducialAddParam configFieldFiducialAddParam) {
        ConfigFieldFiducial configFieldFiducial = BeanUtil.toBean(configFieldFiducialAddParam, ConfigFieldFiducial.class);
        this.save(configFieldFiducial);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(ConfigFieldFiducialEditParam configFieldFiducialEditParam) {
        ConfigFieldFiducial configFieldFiducial = this.queryEntity(configFieldFiducialEditParam.getId());
        BeanUtil.copyProperties(configFieldFiducialEditParam, configFieldFiducial);
        this.updateById(configFieldFiducial);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<ConfigFieldFiducialIdParam> configFieldFiducialIdParamList) {
        // 执行删除
        this.removeByIds(CollStreamUtil.toList(configFieldFiducialIdParamList, ConfigFieldFiducialIdParam::getId));
    }

    @Override
    public ConfigFieldFiducial detail(ConfigFieldFiducialIdParam configFieldFiducialIdParam) {
        return this.queryEntity(configFieldFiducialIdParam.getId());
    }

    @Override
    public ConfigFieldFiducial queryEntity(String id) {
        ConfigFieldFiducial configFieldFiducial = this.getById(id);
        if(ObjectUtil.isEmpty(configFieldFiducial)) {
            throw new CommonException("考证字段配置不存在，id值为：{}", id);
        }
        return configFieldFiducial;
    }
}
