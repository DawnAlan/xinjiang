package com.cj.project.modular.fiducial.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.text.StrSplitter;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.project.api.fiducial.entity.FiducialBase;
import com.cj.project.api.fiducial.param.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.cj.common.enums.CommonSortOrderEnum;
import com.cj.common.exception.CommonException;
import com.cj.common.page.CommonPageRequest;
import com.cj.project.modular.fiducial.mapper.FiducialBaseMapper;
import com.cj.project.modular.fiducial.service.FiducialBaseService;

import java.util.List;

/**
 * 测点考证Service接口实现类
 *
 * @author Lb
 * @date  2023/09/04 12:25
 **/
@Service
public class FiducialBaseServiceImpl extends ServiceImpl<FiducialBaseMapper, FiducialBase> implements FiducialBaseService {

    @Override
    public Page<FiducialBase> page(FiducialPageParam fiducialPageParam) {
        QueryWrapper<FiducialBase> queryWrapper = new QueryWrapper<>();
        if(ObjectUtil.isNotEmpty(fiducialPageParam.getProjectCode())) {
            queryWrapper.lambda().eq(FiducialBase::getProjectCode, fiducialPageParam.getProjectCode());
        }
        if(ObjectUtil.isNotEmpty(fiducialPageParam.getInstrumentType())) {
            queryWrapper.lambda().eq(FiducialBase::getInstrumentType, fiducialPageParam.getInstrumentType());
        }
        if(ObjectUtil.isNotEmpty(fiducialPageParam.getPointId())) {
            queryWrapper.lambda().eq(FiducialBase::getId, fiducialPageParam.getPointId());
        }
        if(ObjectUtil.isNotEmpty(fiducialPageParam.getPointName())) {
            queryWrapper.lambda().like(FiducialBase::getPointName, fiducialPageParam.getPointName());
        }
        if(ObjectUtil.isAllNotEmpty(fiducialPageParam.getSortField(), fiducialPageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(fiducialPageParam.getSortOrder());
            queryWrapper.orderBy(true, fiducialPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(fiducialPageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(FiducialBase::getId);
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(FiducialBase fiducialBase) {
        // FiducialBase fiducialBase = BeanUtil.toBean(fiducialBaseAddParam, FiducialBase.class);
        this.save(fiducialBase);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(FiducialBaseEditParam fiducialBaseEditParam) {
        FiducialBase fiducialBase = this.queryEntity(fiducialBaseEditParam.getId());
        BeanUtil.copyProperties(fiducialBaseEditParam, fiducialBase);
        this.updateById(fiducialBase);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<FiducialIdParam> fiducialIdParamList) {
        // 执行删除
        this.removeByIds(CollStreamUtil.toList(fiducialIdParamList, FiducialIdParam::getId));
    }

    @Override
    public FiducialBase detail(FiducialIdParam fiducialIdParam) {
        return this.queryEntity(fiducialIdParam.getId());
    }

    @Override
    public FiducialBase queryEntity(String id) {
        FiducialBase fiducialBase = this.getById(id);
        if(ObjectUtil.isEmpty(fiducialBase)) {
            throw new CommonException("测点考证不存在，id值为：{}", id);
        }
        return fiducialBase;
    }

    @Override
    public void adds(FiducialBaseAddParam fiducialBaseAddParam) {

    }

    @Override
    public FiducialBase getOne(FiducialQueryParam fiducialQueryParam) {
        QueryWrapper<FiducialBase> queryWrapper = new QueryWrapper<>();
        if(ObjectUtil.isNotEmpty(fiducialQueryParam.getProjectCode())) {
            queryWrapper.lambda().eq(FiducialBase::getProjectCode, fiducialQueryParam.getProjectCode());
        }
        if(ObjectUtil.isNotEmpty(fiducialQueryParam.getInstrumentType())) {
            queryWrapper.lambda().eq(FiducialBase::getInstrumentType, fiducialQueryParam.getInstrumentType());
        }
        if(ObjectUtil.isNotEmpty(fiducialQueryParam.getPoints()))
        {
            String defualtPoint = fiducialQueryParam.getPoints().get(0);
            queryWrapper.lambda().eq(FiducialBase::getId, defualtPoint);
        }
        queryWrapper.lambda().orderByAsc(FiducialBase::getId).last("limit 1");

        return this.getOne(queryWrapper,false);
    }

    public List<FiducialBase> getBatch(FiducialQueryParam fiducialQueryParam) {
        QueryWrapper<FiducialBase> queryWrapper = new QueryWrapper<>();
        if(ObjectUtil.isNotEmpty(fiducialQueryParam.getProjectCode())) {
            queryWrapper.lambda().eq(FiducialBase::getProjectCode, fiducialQueryParam.getProjectCode());
        }
        if(ObjectUtil.isNotEmpty(fiducialQueryParam.getInstrumentType())) {
            queryWrapper.lambda().eq(FiducialBase::getInstrumentType, fiducialQueryParam.getInstrumentType());
        }
        if(ObjectUtil.isNotEmpty(fiducialQueryParam.getPoints()))
        {
            queryWrapper.lambda().in(FiducialBase::getId, fiducialQueryParam.getPoints());
        }
        queryWrapper.lambda().orderByAsc(FiducialBase::getId);

        return this.list(queryWrapper);
    }

    @Override
    public List<FiducialBase> getBatch(String projectCode, String instrumentStr) {
        QueryWrapper<FiducialBase> queryWrapper = new QueryWrapper<>();
        if(ObjectUtil.isNotEmpty(projectCode)) {
            queryWrapper.lambda().eq(FiducialBase::getProjectCode, projectCode);
        }
        List<String> instruments = StrSplitter.split(instrumentStr, ',', 0, true, true);
        if(ObjectUtil.isNotEmpty(instruments)) {
            queryWrapper.lambda().in(FiducialBase::getInstrumentType, instruments);
        }
        queryWrapper.lambda().orderByAsc(FiducialBase::getId);

        return this.list(queryWrapper);
    }

}
