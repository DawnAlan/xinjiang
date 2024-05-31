package com.cj.waterresources.func.modular.quotaStatisticsManagement.tenDaysWaterBalance.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.tenDaysWaterBalance.bean.req.TenDaysWaterBalanceSelectListReq;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.tenDaysWaterBalance.entity.TenDaysWaterBalance;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.entity.WaterFeeStatisticsTotal;

import java.util.List;

/**
 * 旬水量平衡(TenDaysWaterBalance)表服务接口
 *
 * @author makejava
 * @since 2023-12-22 18:40:02
 */
public interface TenDaysWaterBalanceService extends IService<TenDaysWaterBalance> {

    RestResponse<List<TenDaysWaterBalance>> selectList(TenDaysWaterBalanceSelectListReq req);

    RestResponse addFirst(List<WaterFeeStatisticsTotal> totalList);
    RestResponse add(List<WaterFeeStatisticsTotal> totalList);

    RestResponse selectTotalForIndexWarning(String stationName,String time);

    Double getStationTotalValue(String station,Integer year,Integer month,String tenDays);

}

