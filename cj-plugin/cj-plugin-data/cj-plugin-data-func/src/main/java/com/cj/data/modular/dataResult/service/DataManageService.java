package com.cj.data.modular.dataResult.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cj.data.modular.dataResult.entity.DataRecord;
import com.cj.data.modular.dataResult.param.DataRecordAddParam;
import com.cj.data.modular.dataResult.param.DataRecordIdParam;
import com.cj.data.modular.dataResult.param.DataRecordPageParam;
import com.cj.data.modular.dataResult.param.DataRecordQueryParam;

import java.util.List;
import java.util.Map;

/**
 * 数据查询Service接口
 *
 * @author Lb
 * @date  2023/10/12 17:20
 **/
public interface DataManageService {
    /**
     * 获取数据分页
     *
     * @author Lb
     * @date  2023/10/12 17:01
     */
    Page<DataRecord> page(DataRecordPageParam dataRecordPageParam);

    /**
     * 查询所有数据
     *
     * @author : lb
     * @date : 2023/10/13 09:57
     */
    List<Map<String, Object>> getList(DataRecordQueryParam dataRecordQueryParam);

    /**
     * 添加数据
     *
     * @author Lb
     * @date  2023/10/12 17:01
     */
    void add(DataRecordAddParam dataRecordAddParam);


    /**
     * 删除数据
     *
     * @author Lb
     * @date  2023/10/12 17:01
     */
    void delete(List<DataRecordIdParam> dataRecordIdParamList);


}
