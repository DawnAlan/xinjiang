package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.tth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.lzz.entity.DayWaterSituationStatisticsTableLzz;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.tth.entity.DayWaterSituationStatisticsTableTth;

import java.util.List;
import java.util.Map;

/**
 * 头屯河水库日水情统计表(DayWaterSituationStatisticsTableTth)表服务接口
 *
 * @author makejava
 * @since 2023-12-23 16:01:12
 */
public interface DayWaterSituationStatisticsTableTthService extends IService<DayWaterSituationStatisticsTableTth> {

    RestResponse<Map<String, List<DayWaterSituationStatisticsTableTth>>> selectList(String date);

    RestResponse add(List<DayWaterSituationStatisticsTableTth> dayWaterSituationStatisticsTableTthList);

    RestResponse delete(String ids);

    RestResponse update(List<DayWaterSituationStatisticsTableTth> dayWaterSituationStatisticsTableTthList);

    RestResponse insertTodayMeanValue();
}

