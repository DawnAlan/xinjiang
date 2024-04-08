package com.cj.waterresources.func.modular.waterPrice.industrialWaterFee.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.waterPrice.industrialWaterFee.bean.req.SelectPaymentHistoryReq;
import com.cj.waterresources.func.modular.waterPrice.industrialWaterFee.bean.req.SelectPaymentReq;
import com.cj.waterresources.func.modular.waterPrice.industrialWaterFee.bean.res.SelectPaymentHistoryRes;
import com.cj.waterresources.func.modular.waterPrice.industrialWaterFee.entity.IndustrialWaterFee;
import com.cj.waterresources.func.modular.waterPrice.industrialWaterFee.entity.WaterManagementUrbanIndustry;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.bean.req.UseWaterTypeStatisticsReq;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.bean.res.UseWaterTypeStatisticsRes;
import com.cj.waterresources.func.modular.waterSituationDataMaintenance.bean.req.SelectInfoListReq;
import com.cj.waterresources.func.modular.waterSituationDataMaintenance.bean.res.HydrographRes;

import java.util.List;

/**
 * 工业水费(IndustrialWaterFee)表服务接口
 *
 * @author makejava
 * @since 2024-01-31 20:11:19
 */
public interface IndustrialWaterFeeService extends IService<IndustrialWaterFee> {

    List<UseWaterTypeStatisticsRes> statistics(UseWaterTypeStatisticsReq req);

    RestResponse<List<HydrographRes>> selectInfoList(SelectInfoListReq req);

    WaterManagementUrbanIndustry selectPayment(SelectPaymentReq input);

    SelectPaymentHistoryRes selectPaymentHistory(SelectPaymentHistoryReq input);
}

