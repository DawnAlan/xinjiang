package com.cj.project.modular.FiducialEnterA.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.project.modular.FiducialEnterA.entity.ConfigProjectPoint;
import com.cj.project.modular.FiducialEnterA.mapper.ConfigProjectPointMapper;
import com.cj.project.modular.FiducialEnterA.param.ConfigProjectPointAddParam;
import com.cj.project.modular.FiducialEnterA.param.ConfigProjectPointEditParam;
import com.cj.project.modular.FiducialEnterA.param.ConfigProjectPointIdParam;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.cj.common.enums.CommonSortOrderEnum;
import com.cj.common.exception.CommonException;
import com.cj.common.page.CommonPageRequest;
import com.cj.project.modular.FiducialEnterA.service.ConfigProjectPointService;

import java.util.List;

/**
 * 考证集成测点配置接口实现类
 *
 * @author Lb
 * @date  2023/11/23 10:20
 **/
@Service
public class ConfigProjectPointServiceImpl extends ServiceImpl<ConfigProjectPointMapper, ConfigProjectPoint> implements ConfigProjectPointService {

    @Override
    public List<ConfigProjectPoint> getList(String projectcode,String instrumentName) {
        QueryWrapper<ConfigProjectPoint> queryWrapper = new QueryWrapper<>();
        if(ObjectUtil.isNotEmpty(projectcode)) {
            queryWrapper.lambda().eq(ConfigProjectPoint::getProjectcode, projectcode);
        }
        if(ObjectUtil.isNotEmpty(instrumentName)) {
            queryWrapper.lambda().eq(ConfigProjectPoint::getInstrumentName, instrumentName);
        }
        queryWrapper.lambda().orderByAsc(ConfigProjectPoint::getInstrumentType);

        return this.list(queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(ConfigProjectPointAddParam configProjectPointAddParam) {
        ConfigProjectPoint configProjectPoint = BeanUtil.toBean(configProjectPointAddParam, ConfigProjectPoint.class);
        this.save(configProjectPoint);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(ConfigProjectPointEditParam configProjectPointEditParam) {
        ConfigProjectPoint configProjectPoint = this.queryEntity(configProjectPointEditParam.getId());
        BeanUtil.copyProperties(configProjectPointEditParam, configProjectPoint);
        this.updateById(configProjectPoint);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<ConfigProjectPointIdParam> configProjectPointIdParamList) {
        // 执行删除
        this.removeByIds(CollStreamUtil.toList(configProjectPointIdParamList, ConfigProjectPointIdParam::getId));
    }

    @Override
    public ConfigProjectPoint detail(ConfigProjectPointIdParam configProjectPointIdParam) {
        return this.queryEntity(configProjectPointIdParam.getId());
    }

    @Override
    public ConfigProjectPoint queryEntity(String id) {
        ConfigProjectPoint configProjectPoint = this.getById(id);
        if(ObjectUtil.isEmpty(configProjectPoint)) {
            throw new CommonException("FiducialEnterA不存在，id值为：{}", id);
        }
        return configProjectPoint;
    }
}
