package com.cj.waterresources.func.modular.quotaStatisticsManagement.irrigationQuota.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.irrigationQuota.bean.req.IrrigationQuotaListReq;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.irrigationQuota.entity.IrrigationQuota;

import java.util.List;

/**
 * 灌溉额度表(IrrigationQuota)表服务接口
 *
 * @author makejava
 * @since 2023-12-22 12:49:39
 */
public interface IrrigationQuotaService extends IService<IrrigationQuota> {
    RestResponse add(List<IrrigationQuota> input);

    RestResponse delete(String id);

    RestResponse update(IrrigationQuota irrigationQuota);

    RestResponse<List<IrrigationQuota>> selectList(IrrigationQuotaListReq req);

}

