package com.cj.data.modular.dataResult.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.data.modular.dataResult.param.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.cj.common.enums.CommonSortOrderEnum;
import com.cj.common.exception.CommonException;
import com.cj.common.page.CommonPageRequest;
import com.cj.data.modular.dataResult.entity.DataRecord;
import com.cj.data.modular.dataResult.mapper.DataRecordMapper;
import com.cj.data.modular.dataResult.service.DataRecordService;

import java.util.List;

/**
 * 数据基础表Service接口实现类
 *
 * @author Lb
 * @date  2023/10/12 17:01
 **/
@Service
public class DataRecordServiceImpl extends ServiceImpl<DataRecordMapper, DataRecord> implements DataRecordService {

    @Override
    public Page<DataRecord> page(DataRecordPageParam dataRecordPageParam) {
        QueryWrapper<DataRecord> queryWrapper = new QueryWrapper<>();
        if(ObjectUtil.isNotEmpty(dataRecordPageParam.getPointId())) {
            queryWrapper.lambda().eq(DataRecord::getPointId, dataRecordPageParam.getPointId());
        }
        if(ObjectUtil.isNotEmpty(dataRecordPageParam.getStartObservationDate())) {
            queryWrapper.lambda().ge(DataRecord::getObservationDate, dataRecordPageParam.getStartObservationDate());
        }
        if(ObjectUtil.isNotEmpty(dataRecordPageParam.getEndObservationDate())) {
            queryWrapper.lambda().le(DataRecord::getObservationDate, dataRecordPageParam.getEndObservationDate());
        }
        if(ObjectUtil.isNotEmpty(dataRecordPageParam.getRecordMethod())) {
            queryWrapper.lambda().eq(DataRecord::getRecordMethod, dataRecordPageParam.getRecordMethod());
        }
        if(ObjectUtil.isAllNotEmpty(dataRecordPageParam.getSortField(), dataRecordPageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(dataRecordPageParam.getSortOrder());
            queryWrapper.orderBy(true, dataRecordPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(dataRecordPageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(DataRecord::getId);
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Override
    public List<DataRecord> getList(DataRecordQueryParam dataRecordQueryParam) {
        QueryWrapper<DataRecord> queryWrapper = new QueryWrapper<>();
        if(ObjectUtil.isNotEmpty(dataRecordQueryParam.getPointId())) {
            queryWrapper.lambda().eq(DataRecord::getPointId, dataRecordQueryParam.getPointId());
        }
        if(ObjectUtil.isNotEmpty(dataRecordQueryParam.getStartObservationDate())) {
            queryWrapper.lambda().ge(DataRecord::getObservationDate, dataRecordQueryParam.getStartObservationDate());
        }
        if(ObjectUtil.isNotEmpty(dataRecordQueryParam.getEndObservationDate())) {
            queryWrapper.lambda().le(DataRecord::getObservationDate, dataRecordQueryParam.getEndObservationDate());
        }
        if(ObjectUtil.isNotEmpty(dataRecordQueryParam.getRecordMethod())) {
            queryWrapper.lambda().eq(DataRecord::getRecordMethod, dataRecordQueryParam.getRecordMethod());
        }
        if(ObjectUtil.isAllNotEmpty(dataRecordQueryParam.getSortField(), dataRecordQueryParam.getSortOrder())) {
            CommonSortOrderEnum.validate(dataRecordQueryParam.getSortOrder());
            queryWrapper.orderBy(true, dataRecordQueryParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(dataRecordQueryParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(DataRecord::getId);
        }
        return this.list(queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(DataRecordAddParam dataRecordAddParam) {
        DataRecord dataRecord = BeanUtil.toBean(dataRecordAddParam, DataRecord.class);
        this.save(dataRecord);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(DataRecordEditParam dataRecordEditParam) {
        DataRecord dataRecord = this.queryEntity(dataRecordEditParam.getId());
        BeanUtil.copyProperties(dataRecordEditParam, dataRecord);
        this.updateById(dataRecord);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<DataRecordIdParam> dataRecordIdParamList) {
        // 执行删除
        this.removeByIds(CollStreamUtil.toList(dataRecordIdParamList, DataRecordIdParam::getId));
    }

    @Override
    public DataRecord detail(DataRecordIdParam dataRecordIdParam) {
        return this.queryEntity(dataRecordIdParam.getId());
    }

    @Override
    public DataRecord queryEntity(String id) {
        DataRecord dataRecord = this.getById(id);
        if(ObjectUtil.isEmpty(dataRecord)) {
            throw new CommonException("数据基础表不存在，id值为：{}", id);
        }
        return dataRecord;
    }
}
