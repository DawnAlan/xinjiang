package com.cj.project.modular.configfield.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.project.api.configfield.dto.ConfigFieldFiducialDto;
import com.cj.project.api.configfield.entity.ConfigFieldFiducial;
import com.cj.project.api.fiducial.entity.FiducialBase;
import com.cj.project.modular.configfield.enums.ConfigFieldFiducialOutEnum;
import com.cj.project.modular.configfield.mapper.ConfigFieldFiducialMapper;
import com.cj.project.modular.configfield.service.ConfigFieldFiducialGreatService;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 考证字段配置Service接口实现类
 *
 * @author Lb
 * @date  2023/08/31 19:28
 **/
@Service
public class ConfigFieldFiducialCreatServiceImpl extends ServiceImpl<ConfigFieldFiducialMapper, ConfigFieldFiducial>  implements ConfigFieldFiducialGreatService {


    @Override
    public void Create(ConfigFieldFiducialDto configFieldFiducialExportDto) {
        //已存在keyList
        QueryWrapper<ConfigFieldFiducial> queryWrapper = new QueryWrapper<>();
        if(ObjectUtil.isNotEmpty(configFieldFiducialExportDto.getProjectCode())) {
            queryWrapper.lambda().eq(ConfigFieldFiducial::getProjectCode, configFieldFiducialExportDto.getProjectCode());
        }
        if(ObjectUtil.isNotEmpty(configFieldFiducialExportDto.getInstrumentType())) {
            queryWrapper.lambda().eq(ConfigFieldFiducial::getInstrumentType, configFieldFiducialExportDto.getInstrumentType());
        }
        queryWrapper.lambda().orderByAsc(ConfigFieldFiducial::getSortCode);
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
                if(EnumUtils.isValidEnum(ConfigFieldFiducialOutEnum.class, field.getName()))
                    continue;
                ConfigFieldFiducial fieldFiducial = new ConfigFieldFiducial();
                fieldFiducial.setProjectCode(configFieldFiducialExportDto.getProjectCode());
                fieldFiducial.setInstrumentType(configFieldFiducialExportDto.getInstrumentType());
                fieldFiducial.setInstrumentMetaType(configFieldFiducialExportDto.getInstrumentMetaType());
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

        if(configFieldFiducialExportDto.getProjectCode().equals("000"))
        {

        }else
        {

        }

    }


}
