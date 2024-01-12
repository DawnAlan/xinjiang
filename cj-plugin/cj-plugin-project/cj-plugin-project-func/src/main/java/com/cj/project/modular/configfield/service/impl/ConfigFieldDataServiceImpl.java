package com.cj.project.modular.configfield.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.common.exception.CommonException;
import com.cj.project.api.configfield.entity.ConfigFieldData;
import com.cj.project.modular.configfield.mapper.ConfigFieldDataMapper;
import com.cj.project.api.configfield.dto.ConfigFieldQueryDto;
import com.cj.project.modular.configfield.result.ConfigFieldDataResult;
import com.cj.project.modular.configfield.service.ConfigFieldDataService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ConfigFieldDataServiceImpl extends ServiceImpl<ConfigFieldDataMapper, ConfigFieldData> implements ConfigFieldDataService {
    @Override
    public List<ConfigFieldDataResult> getList(ConfigFieldQueryDto configFieldQueryDto) {
        List<ConfigFieldDataResult> fieldDataResults = new ArrayList<>();
        //List
        QueryWrapper<ConfigFieldData> queryWrapper = new QueryWrapper<>();
        if(ObjectUtil.isNotEmpty(configFieldQueryDto.getProjectCode())) {
            queryWrapper.lambda().eq(ConfigFieldData::getProjectCode, configFieldQueryDto.getProjectCode());
        }
        else
        {
            queryWrapper.lambda().eq(ConfigFieldData::getProjectCode, "000");
        }
        if(ObjectUtil.isNotEmpty(configFieldQueryDto.getInstrumentType())) {
            queryWrapper.lambda().eq(ConfigFieldData::getInstrumentType, configFieldQueryDto.getInstrumentType());
        }
        queryWrapper.lambda().orderByAsc(ConfigFieldData::getSortCode);
        List<ConfigFieldData> configFieldDatas = this.list(queryWrapper);
        //Map
        Map<String,List<ConfigFieldData>> fieldmap = configFieldDatas.stream().collect(Collectors.groupingBy(ConfigFieldData::getInstrumentType));
        if(ObjectUtil.isNotEmpty(configFieldQueryDto.getProjectCode()))
        {
            fieldmap = configFieldDatas.stream().collect(Collectors.groupingBy(ConfigFieldData::getInstrumentMetaType));
        }
        for (String instrumenttype : fieldmap.keySet()
        ) {
            ConfigFieldData defaultFieldData = fieldmap.get(instrumenttype).stream().findFirst().get();
            ConfigFieldDataResult fieldResult = new ConfigFieldDataResult();
            fieldResult.setProjectCode(defaultFieldData.getProjectCode());
            fieldResult.setInstrumentType(instrumenttype);
            fieldResult.setInstrumentMetaType(defaultFieldData.getInstrumentMetaType());
            fieldResult.setFieldConfigs(fieldmap.get(instrumenttype));
            fieldDataResults.add(fieldResult);
        }
        return fieldDataResults;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(ConfigFieldData configFieldData) {
        this.save(configFieldData);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(ConfigFieldData configFieldData) {
        this.queryEntity(configFieldData.getId());
        this.updateById(configFieldData);
    }

    @Override
    public void delete(List<String> idList) {
        // 执行删除
        this.removeByIds(idList);
    }

    @Override
    public ConfigFieldData detail(String id) {
        return this.queryEntity(id);
    }

    @Override
    public ConfigFieldData queryEntity(String id) {
        ConfigFieldData configFieldData = this.getById(id);
        if(ObjectUtil.isEmpty(configFieldData)) {
            throw new CommonException("数据字段配置不存在，id值为：{}", id);
        }
        return configFieldData;
    }
}
