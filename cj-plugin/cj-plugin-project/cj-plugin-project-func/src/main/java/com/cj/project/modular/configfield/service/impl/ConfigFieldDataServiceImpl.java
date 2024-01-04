package com.cj.project.modular.configfield.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.common.exception.CommonException;
import com.cj.project.modular.configfield.entity.ConfigFieldData;
import com.cj.project.modular.configfield.mapper.ConfigFieldDataMapper;
import com.cj.project.modular.configfield.param.ConfigFieldDataAddParam;
import com.cj.project.modular.configfield.param.ConfigFieldDataEditParam;
import com.cj.project.modular.configfield.param.ConfigFieldIdParam;
import com.cj.project.modular.configfield.param.ConfigFieldQueryParam;
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
    public List<ConfigFieldDataResult> getList(ConfigFieldQueryParam configFieldQueryParam) {
        List<ConfigFieldDataResult> fieldDataResults = new ArrayList<>();
        //List
        QueryWrapper<ConfigFieldData> queryWrapper = new QueryWrapper<>();
        if(ObjectUtil.isNotEmpty(configFieldQueryParam.getProjectCode())) {
            queryWrapper.lambda().eq(ConfigFieldData::getProjectCode, configFieldQueryParam.getProjectCode());
        }
        if(ObjectUtil.isNotEmpty(configFieldQueryParam.getInstrumentType())) {
            queryWrapper.lambda().eq(ConfigFieldData::getInstrumentType, configFieldQueryParam.getInstrumentType());
        }
        queryWrapper.lambda().orderByAsc(ConfigFieldData::getSortCode);
        List<ConfigFieldData> configFieldDatas = this.list(queryWrapper);
        //Map
        Map<String,List<ConfigFieldData>> fieldmap = configFieldDatas.stream().collect(Collectors.groupingBy(ConfigFieldData::getInstrumentType));
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
    public void add(ConfigFieldDataAddParam configFieldDataAddParam) {
        ConfigFieldData configFielddata = BeanUtil.toBean(configFieldDataAddParam, ConfigFieldData.class);
        this.save(configFielddata);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(ConfigFieldDataEditParam configFieldDataEditParam) {
        ConfigFieldData configFielddata = this.queryEntity(configFieldDataEditParam.getId());
        BeanUtil.copyProperties(configFieldDataEditParam, configFielddata);
        this.updateById(configFielddata);
    }

    @Override
    public void delete(List<ConfigFieldIdParam> configFieldIdParamList) {
        // 执行删除
        this.removeByIds(CollStreamUtil.toList(configFieldIdParamList, ConfigFieldIdParam::getId));
    }

    @Override
    public ConfigFieldData detail(ConfigFieldIdParam configFieldIdParam) {
        return this.queryEntity(configFieldIdParam.getId());
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
