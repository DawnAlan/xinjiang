package com.cj.data.modular.dataResult.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ReflectUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cj.data.modular.dataResult.entity.DataRecord;
import com.cj.data.modular.dataResult.entity.DataValue;
import com.cj.data.modular.dataResult.param.DataRecordAddParam;
import com.cj.data.modular.dataResult.param.DataRecordIdParam;
import com.cj.data.modular.dataResult.param.DataRecordPageParam;
import com.cj.data.modular.dataResult.param.DataRecordQueryParam;
import com.cj.data.modular.dataResult.service.DataManageService;
import com.cj.data.modular.dataResult.service.DataRecordService;
import com.cj.data.modular.dataResult.service.DataValueService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据查询Service接口实现类
 *
 * @author Lb
 * @date  2023/10/12 17:01
 **/
@Service
public class DataManageServiceImpl implements DataManageService {

    @Resource
    private DataRecordService dataRecordService;
    @Resource
    private DataValueService dataValueService;

    @Override
    public Page<DataRecord> page(DataRecordPageParam dataRecordPageParam) {
        return null;
    }

    @Override
    public List<Map<String, Object>> getList(DataRecordQueryParam dataRecordQueryParam) {
        List<Map<String, Object>> result = new ArrayList<>();
        //List<DataRecord>
        List<DataRecord> dataRecordList = dataRecordService.getList(dataRecordQueryParam);

        //List<DataValue>
        List<String> dataIds = dataRecordList.stream().map(s->s.getId()).collect(Collectors.toList());
        List<DataValue> dataValueList = dataValueService.getList(dataIds);
        for (DataRecord dataRecord : dataRecordList
             ) {
            Map<String, Object> dataMaps = new LinkedHashMap<>();
            dataMaps = BeanUtil.beanToMap(dataRecord);
            //行存储字段查询
            //后续可能需根据字段配置查出该仪器所有数据字段，对无数据的字段进行null填充
            List<DataValue> dataValueListSelect = dataValueList.stream().filter(s->s.getDataId().equals(dataRecord.getId())).collect(Collectors.toList());
            for (DataValue dataValue : dataValueListSelect
                 ) {
                dataMaps.put(dataValue.getFieldKey(),dataValue.getFieldValue());
            }
            result.add(dataMaps);
        }

        return result;
    }

    @Override
    public void add(DataRecordAddParam dataRecordAddParam) {

    }

    @Override
    public void delete(List<DataRecordIdParam> dataRecordIdParamList) {

    }
}
