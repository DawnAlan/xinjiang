package com.cj.waterresources.func.modular.quotaStatisticsManagement.irrigationQuotaDetails.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.irrigationQuota.bean.req.IrrigationQuotaContrastReq;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.irrigationQuota.bean.res.IrrigationQuotaContrastRes;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.irrigationQuotaDetails.bean.req.StatisticsReq;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.irrigationQuotaDetails.entity.IrrigationQuotaDetails;

import java.util.List;
import java.util.Map;

/**
 * 灌溉明细表(IrrigationQuotaDetails)表服务接口
 *
 * @author makejava
 * @since 2024-02-02 10:59:15
 */
public interface IrrigationQuotaDetailsService extends IService<IrrigationQuotaDetails> {

    RestResponse<Map<String,List<IrrigationQuotaDetails>>> statistics(StatisticsReq req);

    Map<String, List<IrrigationQuotaContrastRes>> contrast(IrrigationQuotaContrastReq req);
}

