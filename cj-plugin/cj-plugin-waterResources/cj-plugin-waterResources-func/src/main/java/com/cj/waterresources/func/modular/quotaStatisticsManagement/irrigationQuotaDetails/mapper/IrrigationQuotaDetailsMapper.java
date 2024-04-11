package com.cj.waterresources.func.modular.quotaStatisticsManagement.irrigationQuotaDetails.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.irrigationQuota.bean.req.IrrigationQuotaContrastReq;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.irrigationQuota.bean.res.IrrigationQuotaContrastRes;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.irrigationQuotaDetails.bean.req.StatisticsReq;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.irrigationQuotaDetails.entity.IrrigationQuotaDetails;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 灌溉明细表(IrrigationQuotaDetails)表数据库访问层
 *
 * @author makejava
 * @since 2024-02-02 10:59:14
 */
public interface IrrigationQuotaDetailsMapper extends BaseMapper<IrrigationQuotaDetails> {
    List<IrrigationQuotaDetails> statistics(@Param("req") StatisticsReq req);


    List<IrrigationQuotaContrastRes> contrast(@Param("req") IrrigationQuotaContrastReq req);
}

