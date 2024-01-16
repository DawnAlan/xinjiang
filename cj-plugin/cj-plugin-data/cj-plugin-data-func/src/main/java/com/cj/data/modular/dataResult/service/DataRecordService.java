package com.cj.data.modular.dataResult.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.data.modular.dataResult.entity.DataRecord;
import com.cj.data.modular.dataResult.param.*;

import java.util.List;

/**
 * 数据基础表Service接口
 *
 * @author Lb
 * @date  2023/10/12 17:01
 **/
public interface DataRecordService extends IService<DataRecord> {

    /**
     * 获取数据基础表分页
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
    List<DataRecord> getList(DataRecordQueryParam dataRecordQueryParam);

    /**
     * 添加数据基础表
     *
     * @author Lb
     * @date  2023/10/12 17:01
     * dataRecordAddParam ：DataState、IsReview、Order、WarnMark
     * startTime、endTime
     * name : "测压管"、points、projCode、recordMethod、state
     */
    void add(DataRecordAddParam dataRecordAddParam);

    /**
     * 编辑数据基础表
     *
     * @author Lb
     * @date  2023/10/12 17:01
     */
    void edit(DataRecordEditParam dataRecordEditParam);

    /**
     * 删除数据基础表
     *
     * @author Lb
     * @date  2023/10/12 17:01
     */
    void delete(List<DataRecordIdParam> dataRecordIdParamList);

    /**
     * 获取数据基础表详情
     *
     * @author Lb
     * @date  2023/10/12 17:01
     */
    DataRecord detail(DataRecordIdParam dataRecordIdParam);

    /**
     * 获取数据基础表详情
     *
     * @author Lb
     * @date  2023/10/12 17:01
     **/
    DataRecord queryEntity(String id);
}
