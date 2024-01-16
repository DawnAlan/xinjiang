package com.cj.project.modular.configfield.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.common.util.FormatCheckUtil;
import com.cj.common.util.MapTransformUtil;
import com.cj.project.api.configfield.dto.ConfigFieldFiducialPageDto;
import com.cj.project.api.configfield.dto.ConfigFieldFiducialQueryDto;
import com.cj.project.api.configfield.entity.ConfigFieldFiducial;
import com.cj.project.modular.configfield.result.ConfigFieldFiducialResult;
import com.cj.project.modular.fiducial.service.FiducialBaseService;
import com.cj.project.modular.fiducial.service.FiducialParaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.cj.common.enums.CommonSortOrderEnum;
import com.cj.common.exception.CommonException;
import com.cj.common.page.CommonPageRequest;
import com.cj.project.modular.configfield.mapper.ConfigFieldFiducialMapper;
import com.cj.project.modular.configfield.service.ConfigFieldFiducialService;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 考证字段配置Service接口实现类
 *
 * @author Lb
 * @date  2023/08/31 19:28
 **/
@Service
public class ConfigFieldFiducialServiceImpl extends ServiceImpl<ConfigFieldFiducialMapper, ConfigFieldFiducial> implements ConfigFieldFiducialService {



    @Autowired
    MapTransformUtil mapTransformUtil;

    @Autowired
    FiducialBaseService fiducialBaseService;

    @Autowired
    FiducialParaService fiducialParaService;

    @Autowired
    FormatCheckUtil formatCheckUtil;


    @Override
    public List<ConfigFieldFiducialResult> getList(ConfigFieldFiducialQueryDto configFieldFiducialQueryDto) {
        List<ConfigFieldFiducialResult> fieldFiducialResults = new ArrayList<>();
        //List
        QueryWrapper<ConfigFieldFiducial> queryWrapper = new QueryWrapper<>();
        if(ObjectUtil.isNotEmpty(configFieldFiducialQueryDto.getProjectCode())) {
            queryWrapper.lambda().eq(ConfigFieldFiducial::getProjectCode, configFieldFiducialQueryDto.getProjectCode());
        }
        if(ObjectUtil.isNotEmpty(configFieldFiducialQueryDto.getInstrumentType())) {
            queryWrapper.lambda().eq(ConfigFieldFiducial::getInstrumentType, configFieldFiducialQueryDto.getInstrumentType());
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
    public Page<ConfigFieldFiducial> page(ConfigFieldFiducialPageDto configFieldFiducialPageDto) {
        QueryWrapper<ConfigFieldFiducial> queryWrapper = new QueryWrapper<>();
        if(ObjectUtil.isNotEmpty(configFieldFiducialPageDto.getProjectCode())) {
            queryWrapper.lambda().eq(ConfigFieldFiducial::getProjectCode, configFieldFiducialPageDto.getProjectCode());
        }
        if(ObjectUtil.isNotEmpty(configFieldFiducialPageDto.getInstrumentType())) {
            queryWrapper.lambda().eq(ConfigFieldFiducial::getInstrumentType, configFieldFiducialPageDto.getInstrumentType());
        }
        if(ObjectUtil.isAllNotEmpty(configFieldFiducialPageDto.getSortField(), configFieldFiducialPageDto.getSortOrder())) {
            CommonSortOrderEnum.validate(configFieldFiducialPageDto.getSortOrder());
            queryWrapper.orderBy(true, configFieldFiducialPageDto.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(configFieldFiducialPageDto.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(ConfigFieldFiducial::getId);
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(ConfigFieldFiducial configFieldFiducial) {
        this.save(configFieldFiducial);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(ConfigFieldFiducial configFieldFiducial) {
        this.queryEntity(configFieldFiducial.getId());
        this.updateById(configFieldFiducial);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<String> idList) {
        // 执行删除
        this.removeByIds(idList);
    }

    @Override
    public ConfigFieldFiducial detail(String id) {
        return this.queryEntity(id);
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
