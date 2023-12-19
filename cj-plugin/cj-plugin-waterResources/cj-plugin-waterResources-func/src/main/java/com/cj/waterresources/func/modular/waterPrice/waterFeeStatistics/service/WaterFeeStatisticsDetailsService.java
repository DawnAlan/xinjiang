package com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.bean.req.WaterFeeStatisticsDetailsSelectListReq;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.entity.WaterFeeStatisticsDetails;

import java.util.List;
import java.util.Map;

/**
 * 水费统计详情(WaterFeeStatisticsDetails)表服务接口
 *
 * @author makejava
 * @since 2023-11-29 17:15:44
 */
public interface WaterFeeStatisticsDetailsService extends IService<WaterFeeStatisticsDetails> {

    RestResponse add(List<WaterFeeStatisticsDetails> waterFeeStatisticsDetails);

    RestResponse updateInfo(List<WaterFeeStatisticsDetails> waterFeeStatisticsDetails);
    RestResponse update(List<WaterFeeStatisticsDetails> waterFeeStatisticsDetails);

    RestResponse<Map<String, List<WaterFeeStatisticsDetails>>> selectList(WaterFeeStatisticsDetailsSelectListReq req);

    RestResponse clearTable(WaterFeeStatisticsDetailsSelectListReq req);



}

