package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.qs.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.qs.entity.DayWaterSituationStatisticsTableQs;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.qs.entity.DayWaterSituationStatisticsTableQsLh;

import java.util.List;
import java.util.Map;

/**
 * (DayWaterSituationStatisticsTableQsLh)表服务接口
 *
 * @author makejava
 * @since 2024-03-21 10:58:52
 */
public interface DayWaterSituationStatisticsTableQsLhService extends IService<DayWaterSituationStatisticsTableQsLh> {

    RestResponse<Map<String, List<DayWaterSituationStatisticsTableQsLh>>> selectList(String date);

    RestResponse add(List<DayWaterSituationStatisticsTableQsLh> dayWaterSituationStatisticsTableQsLhList);

    RestResponse delete(String ids);

    RestResponse update(List<DayWaterSituationStatisticsTableQsLh> dayWaterSituationStatisticsTableQsLhList);

    RestResponse insertTodayMeanValue();
}

