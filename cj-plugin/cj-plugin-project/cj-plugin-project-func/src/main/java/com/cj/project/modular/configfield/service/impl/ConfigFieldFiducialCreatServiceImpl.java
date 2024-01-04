package com.cj.project.modular.configfield.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.project.modular.configfield.entity.ConfigFieldFiducial;
import com.cj.project.modular.configfield.enums.ConfigFieldFiducialOutEnum;
import com.cj.project.modular.configfield.mapper.ConfigFieldFiducialMapper;
import com.cj.project.modular.configfield.service.ConfigFieldFiducialGreatService;
import com.cj.project.modular.fiducial.entity.FiducialBase;
import com.cj.project.modular.instruments.entity.ProjectInstruments;
import com.cj.project.modular.instruments.service.ProjectInstrumentsService;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 考证字段批量配置Service接口实现类
 *
 * @author Lb
 * @date  2023/08/31 19:28
 **/
@Service
public class ConfigFieldFiducialCreatServiceImpl extends ServiceImpl<ConfigFieldFiducialMapper, ConfigFieldFiducial>  implements ConfigFieldFiducialGreatService {

    @Resource
    private ProjectInstrumentsService projectInstrumentsService;

    @Override
    public void Create(String projectCode, String instrumentMetaType, String instrumentType) {
        //ProjectInstrument
        List<ProjectInstruments> instruments = projectInstrumentsService.getList(projectCode, null, instrumentType, instrumentMetaType);
        for (ProjectInstruments instrument : instruments
             ) {
            //已存在keyList
            QueryWrapper<ConfigFieldFiducial> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(ConfigFieldFiducial::getProjectCode, instrument.getProjectCode());
            queryWrapper.lambda().eq(ConfigFieldFiducial::getInstrumentType, instrument.getInstrumentType());
            queryWrapper.lambda().eq(ConfigFieldFiducial::getInstrumentMetaType, instrument.getInstrumentMetaType());
            List<ConfigFieldFiducial> fieldsFiducial = this.list(queryWrapper);
            List<String> keyList = new ArrayList<>();
            if(fieldsFiducial.stream().count() > 0)
            {
                keyList = fieldsFiducial.stream().map(ConfigFieldFiducial::getFieldKey).collect(Collectors.toList());
            }

            List<ConfigFieldFiducial> createList = new ArrayList<>();
            //baseFields
            Field[] baseFields = ReflectUtil.getFieldsDirectly(FiducialBase.class,false);
            for (Field field : baseFields
            ) {
                if(! keyList.contains(field.getName())) {
                    // System.out.println(field.getName());
                    if(EnumUtils.isValidEnum(ConfigFieldFiducialOutEnum.class, field.getName()))
                        continue;
                    ConfigFieldFiducial fieldFiducial = new ConfigFieldFiducial();
                    fieldFiducial.setProjectCode(instrument.getProjectCode());
                    fieldFiducial.setInstrumentType(instrument.getInstrumentType());
                    fieldFiducial.setInstrumentMetaType(instrument.getInstrumentMetaType());
                    fieldFiducial.setFieldKey(field.getName());
                    if (field.getAnnotation(ApiModelProperty.class) != null) {
                        fieldFiducial.setFieldText(field.getAnnotation(ApiModelProperty.class).value());
                        fieldFiducial.setFieldConfig(field.getAnnotation(ApiModelProperty.class).notes());
                    }
                    fieldFiducial.setIsDisplay("1");
                    fieldFiducial.setSortCode(1);
                    fieldFiducial.setSystemType(field.getType().getSimpleName());
                    createList.add(fieldFiducial);
                }
            }
            this.saveBatch(createList);
        }

    }

    @Override
    public void UpdateFieldDisplay(String projectCode, String instrumentType, String[] Fields,String isDisplay) {
        for (String field : Fields
             ) {
            System.out.println(field);
            UpdateWrapper<ConfigFieldFiducial> updateWrapper = new UpdateWrapper<>();
            if(ObjectUtil.isNotEmpty(projectCode)) {
                updateWrapper.lambda().eq(ConfigFieldFiducial::getProjectCode, projectCode);
            }
            if(ObjectUtil.isNotEmpty(instrumentType)) {
                updateWrapper.lambda().eq(ConfigFieldFiducial::getInstrumentType, instrumentType);
            }

            updateWrapper.lambda().eq(ConfigFieldFiducial::getFieldKey, field);
            updateWrapper.set("IS_DISPLAY", isDisplay);
            this.update(null,updateWrapper);
        }
    }

    @Override
    public void BatchAddField(String projectCode, String instrumentType, String[] Fields) {
        //ProjectInstrument
        List<ProjectInstruments> instruments = projectInstrumentsService.getList(projectCode, null, instrumentType, null);
        for (ProjectInstruments instrument : instruments
        ) {
            //已存在keyList
            QueryWrapper<ConfigFieldFiducial> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(ConfigFieldFiducial::getProjectCode, instrument.getProjectCode());
            queryWrapper.lambda().eq(ConfigFieldFiducial::getInstrumentType, instrument.getInstrumentType());
            queryWrapper.lambda().eq(ConfigFieldFiducial::getInstrumentMetaType, instrument.getInstrumentMetaType());
            List<ConfigFieldFiducial> fieldsFiducial = this.list(queryWrapper);
            List<String> keyList = new ArrayList<>();
            if(fieldsFiducial.stream().count() > 0)
            {
                keyList = fieldsFiducial.stream().map(ConfigFieldFiducial::getFieldKey).collect(Collectors.toList());
            }

            List<ConfigFieldFiducial> createList = new ArrayList<>();
            //baseFields
            Field[] baseFields = ReflectUtil.getFieldsDirectly(FiducialBase.class,false);
            for (Field field : baseFields
            ) {
                if(Arrays.asList(Fields).contains(field.getName()) && ! keyList.contains(field.getName())){
                    // System.out.println(field.getName());
                    if(EnumUtils.isValidEnum(ConfigFieldFiducialOutEnum.class, field.getName()))
                        continue;
                    ConfigFieldFiducial fieldFiducial = new ConfigFieldFiducial();
                    fieldFiducial.setProjectCode(instrument.getProjectCode());
                    fieldFiducial.setInstrumentType(instrument.getInstrumentType());
                    fieldFiducial.setInstrumentMetaType(instrument.getInstrumentMetaType());
                    fieldFiducial.setFieldKey(field.getName());
                    if (field.getAnnotation(ApiModelProperty.class) != null) {
                        fieldFiducial.setFieldText(field.getAnnotation(ApiModelProperty.class).value());
                        fieldFiducial.setFieldConfig(field.getAnnotation(ApiModelProperty.class).notes());
                    }
                    fieldFiducial.setIsDisplay("1");
                    fieldFiducial.setSortCode(1);
                    fieldFiducial.setSystemType(field.getType().getSimpleName());
                    createList.add(fieldFiducial);
                }
            }
            this.saveBatch(createList);
        }

    }
}
