package com.cj.waterresources.func.modular.waterPrice.canalHeadManagementStation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.waterPrice.canalHeadManagementStation.entity.CanalHeadManagementStationDetails;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.bean.req.WaterFeeStatisticsDetailsSelectListReq;

import java.util.Map;

/**
 * 渠首管理站明细表(CanalHeadManagementStationDetails)表服务接口
 *
 * @author makejava
 * @since 2023-12-15 18:07:49
 */
public interface CanalHeadManagementStationDetailsService extends IService<CanalHeadManagementStationDetails> {

    Map<String,Double> getLanternCanalInfoByDate(String date);

    RestResponse addDetails(CanalHeadManagementStationDetails canalHeadManagementStationDetails);
    RestResponse add(CanalHeadManagementStationDetails canalHeadManagementStationDetails);

    RestResponse delete(String id);

    RestResponse updateDetails(CanalHeadManagementStationDetails canalHeadManagementStationDetails);
    RestResponse update(CanalHeadManagementStationDetails canalHeadManagementStationDetails);

    RestResponse selectList(WaterFeeStatisticsDetailsSelectListReq req);

}

