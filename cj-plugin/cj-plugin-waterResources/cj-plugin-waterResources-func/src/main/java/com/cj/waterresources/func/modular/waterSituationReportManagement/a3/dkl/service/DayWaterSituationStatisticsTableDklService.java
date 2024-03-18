package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.dkl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.dkl.entity.DayWaterSituationStatisticsTableDkl;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.lzz.entity.DayWaterSituationStatisticsTableLzz;

import java.util.List;
import java.util.Map;

/**
 * 对口率日水情统计表(DayWaterSituationStatisticsTableDkl)表服务接口
 *
 * @author makejava
 * @since 2023-12-23 15:58:23
 */
public interface DayWaterSituationStatisticsTableDklService extends IService<DayWaterSituationStatisticsTableDkl> {
    RestResponse<Map<String, List<DayWaterSituationStatisticsTableDkl>>> selectList(String date);

    RestResponse add(List<DayWaterSituationStatisticsTableDkl> dayWaterSituationStatisticsTableDklList);

    RestResponse delete(String ids);

    RestResponse update(List<DayWaterSituationStatisticsTableDkl> dayWaterSituationStatisticsTableDklList);

    RestResponse insertTodayMeanValue();
}

