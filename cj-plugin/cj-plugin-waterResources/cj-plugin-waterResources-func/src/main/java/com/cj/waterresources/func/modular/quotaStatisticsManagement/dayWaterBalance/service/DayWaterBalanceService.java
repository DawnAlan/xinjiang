package com.cj.waterresources.func.modular.quotaStatisticsManagement.dayWaterBalance.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.dayWaterBalance.bean.req.DayWaterBalanceSelectListReq;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.dayWaterBalance.entity.DayWaterBalance;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.entity.WaterFeeStatisticsDetails;

import java.util.List;

/**
 * 日水量平衡表(DayWaterBalance)表服务接口
 *
 * @author makejava
 * @since 2023-12-22 18:39:38
 */
public interface DayWaterBalanceService extends IService<DayWaterBalance> {

    RestResponse<List<DayWaterBalance>> selectList(DayWaterBalanceSelectListReq req);

    RestResponse addFirst(List<WaterFeeStatisticsDetails> detailsList, Integer day);
    RestResponse update(List<WaterFeeStatisticsDetails> detailsList,Integer day);

    Double getStationTotalValue(String station,String time);

}

