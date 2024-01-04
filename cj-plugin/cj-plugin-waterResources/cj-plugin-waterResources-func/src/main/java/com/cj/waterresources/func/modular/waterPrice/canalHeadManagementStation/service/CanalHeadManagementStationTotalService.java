package com.cj.waterresources.func.modular.waterPrice.canalHeadManagementStation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.waterPrice.canalHeadManagementStation.entity.CanalHeadManagementStationTotal;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.bean.req.WaterFeeStatisticsDetailsSelectListReq;

/**
 * 渠首管理站总计表(CanalHeadManagementStationTotal)表服务接口
 *
 * @author makejava
 * @since 2023-12-15 18:08:09
 */
public interface CanalHeadManagementStationTotalService extends IService<CanalHeadManagementStationTotal> {

    RestResponse selectList(WaterFeeStatisticsDetailsSelectListReq req);
}

