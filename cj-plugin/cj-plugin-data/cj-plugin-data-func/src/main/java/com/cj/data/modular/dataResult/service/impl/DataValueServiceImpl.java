package com.cj.data.modular.dataResult.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.data.modular.dataResult.entity.DataValue;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.cj.common.page.CommonPageRequest;
import com.cj.data.modular.dataResult.mapper.DataResultMapper;
import com.cj.data.modular.dataResult.param.DataValueAddParam;
import com.cj.data.modular.dataResult.service.DataValueService;

import java.util.List;

/**
 * 数据成果表Service接口实现类
 *
 * @author Lb
 * @date  2023/10/23 16:51
 **/
@Service
public class DataValueServiceImpl extends ServiceImpl<DataResultMapper, DataValue> implements DataValueService {

    @Override
    public Page<DataValue> page(List<String> dataIdParamList, Integer current, Integer size) {
        QueryWrapper<DataValue> queryWrapper = new QueryWrapper<>();
        if(ObjectUtil.isNotEmpty(dataIdParamList)) {
            queryWrapper.lambda().in(DataValue::getDataId, dataIdParamList);
        }
        queryWrapper.lambda().orderByAsc(DataValue::getDataId);
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Override
    public List<DataValue> getList(List<String> dataIdParamList) {
        List<DataValue> dataValue = this.list(new QueryWrapper<DataValue>().lambda()
                .in(DataValue::getDataId,dataIdParamList).orderByDesc(DataValue::getDataId));
        return dataValue;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(DataValueAddParam dataValueAddParam) {
        DataValue dataValue = BeanUtil.toBean(dataValueAddParam, DataValue.class);
        this.save(dataValue);
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<String> dataIdParamList) {
        // 执行删除
        this.remove(new QueryWrapper<DataValue>().lambda().in(DataValue::getDataId,dataIdParamList));
    }

}
