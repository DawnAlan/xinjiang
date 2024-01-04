package com.cj.project.modular.configfield.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.project.modular.configfield.entity.ConfigFieldData;
import com.cj.project.modular.configfield.entity.ConfigFieldFiducial;
import com.cj.project.modular.configfield.enums.ConfigFieldFiducialOutEnum;
import com.cj.project.modular.configfield.mapper.ConfigFieldDataMapper;
import com.cj.project.modular.configfield.service.ConfigFieldDataGreatService;
import com.cj.project.modular.fiducial.entity.FiducialBase;
import com.cj.project.modular.instruments.entity.ProjectInstruments;
import com.cj.project.modular.instruments.service.ProjectInstrumentsService;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConfigFieldDataGreatServiceImpl extends ServiceImpl<ConfigFieldDataMapper, ConfigFieldData> implements ConfigFieldDataGreatService {

    @Resource
    private ProjectInstrumentsService projectInstrumentsService;

    @Override
    public void Create(String projectCode, String instrumentMetaType, String instrumentType) {
        //ProjectInstrument
        List<ProjectInstruments> instruments = projectInstrumentsService.getList(projectCode, null, instrumentType, instrumentMetaType);
        for (ProjectInstruments instrument : instruments
        ) {
            //已存在keyList
            QueryWrapper<ConfigFieldData> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(ConfigFieldData::getProjectCode, instrument.getProjectCode());
            queryWrapper.lambda().eq(ConfigFieldData::getInstrumentType, instrument.getInstrumentType());
            queryWrapper.lambda().eq(ConfigFieldData::getInstrumentMetaType, instrument.getInstrumentMetaType());
            List<ConfigFieldData> fieldsData = this.list(queryWrapper);
            List<String> keyList = new ArrayList<>();
            if (fieldsData.stream().count() > 0) {
                keyList = fieldsData.stream().map(ConfigFieldData::getFieldKey).collect(Collectors.toList());
            }

            List<ConfigFieldData> createList = new ArrayList<>();
            //Fields


            this.saveBatch(createList);
        }
    }

    @Override
    public void UpdateFieldDisplay(String projectCode, String instrumentType, String[] Fields, String isDisplay) {
        for (String field : Fields
        ) {
            UpdateWrapper<ConfigFieldData> updateWrapper = new UpdateWrapper<>();
            if(ObjectUtil.isNotEmpty(projectCode)) {
                updateWrapper.lambda().eq(ConfigFieldData::getProjectCode, projectCode);
            }
            if(ObjectUtil.isNotEmpty(instrumentType)) {
                updateWrapper.lambda().eq(ConfigFieldData::getInstrumentType, instrumentType);
            }

            updateWrapper.lambda().eq(ConfigFieldData::getFieldKey, field);
            updateWrapper.set("IS_DISPLAY", isDisplay);
            this.update(null,updateWrapper);
        }
    }
}
