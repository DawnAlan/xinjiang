package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.dutyRecords.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.dutyRecords.bean.req.DutyRecordsSelectListReq;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.dutyRecords.entity.DutyRecords;

import java.util.List;

/**
 * 值班记录(DutyRecords)表服务接口
 *
 * @author makejava
 * @since 2023-12-25 16:59:50
 */
public interface DutyRecordsService extends IService<DutyRecords> {

    RestResponse add(DutyRecords dutyRecords);

    RestResponse insert(DutyRecords dutyRecords);

    RestResponse delete(String id);

    RestResponse update(DutyRecords dutyRecords);

    RestResponse<List<DutyRecords>> selectList(DutyRecordsSelectListReq req);
}

