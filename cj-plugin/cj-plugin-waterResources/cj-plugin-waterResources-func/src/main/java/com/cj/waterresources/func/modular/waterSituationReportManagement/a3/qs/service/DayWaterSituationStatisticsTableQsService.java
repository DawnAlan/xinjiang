package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.qs.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.qs.entity.DayWaterSituationStatisticsTableQs;

import java.util.List;
import java.util.Map;

/**
 * 渠首管理站日水情统计表(DayWaterSituationStatisticsTableQs)表服务接口
 *
 * @author makejava
 * @since 2023-12-23 15:59:55
 */
public interface DayWaterSituationStatisticsTableQsService extends IService<DayWaterSituationStatisticsTableQs> {

    RestResponse<Map<String, List<DayWaterSituationStatisticsTableQs>>> selectList(String date);

    RestResponse add(List<DayWaterSituationStatisticsTableQs> dayWaterSituationStatisticsTableQsList);

    RestResponse delete(String ids);

    RestResponse update(List<DayWaterSituationStatisticsTableQs> dayWaterSituationStatisticsTableQsList);

    RestResponse insertTodayMeanValue();

}

