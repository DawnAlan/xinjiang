package com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.bean.req.WaterFeeStatisticsDetailsSelectListReq;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.entity.WaterFeeStatisticsTotal;

import java.util.List;

/**
 * 水费统计总计(WaterFeeStatisticsTotal)表服务接口
 *
 * @author makejava
 * @since 2023-11-29 17:16:56
 */
public interface WaterFeeStatisticsTotalService extends IService<WaterFeeStatisticsTotal> {

    RestResponse<List<WaterFeeStatisticsTotal>> selectInfoList(WaterFeeStatisticsDetailsSelectListReq req);

    RestResponse selectTotalForIndex(String time);

}

