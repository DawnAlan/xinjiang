package com.cj.data.modular.dataResult.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.data.modular.dataResult.entity.DataRecord;
import com.cj.data.modular.dataResult.entity.DataValue;
import com.cj.data.modular.dataResult.param.DataRecordQueryParam;
import com.cj.data.modular.dataResult.param.DataValueAddParam;

import java.util.List;

/**
 * 数据成果表Service接口
 *
 * @author Lb
 * @date  2023/10/23 16:51
 **/
public interface DataValueService extends IService<DataValue> {

    /**
     * 获取数据成果表分页
     *
     * @author Lb
     * @date  2023/10/23 16:51
     */
    Page<DataValue> page(List<String> dataIdParamList, Integer current, Integer size);

    List<DataValue> getList(List<String> dataIdParamList);
    /**
     * 添加数据成果表
     *
     * @author Lb
     * @date  2023/10/23 16:51
     */
    void add(DataValueAddParam dataValueAddParam);


    /**
     * 删除数据成果表
     *
     * @author Lb
     * @date  2023/10/23 16:51
     */
    void delete(List<String> dataIdParamList);


}
