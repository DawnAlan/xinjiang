package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.syyl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.syyl.entity.DayWaterSituationStatisticsTableSyyl;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.zcc.entity.DayWaterSituationStatisticsTableZcc;

import java.util.List;

/**
 * 上游雨量日水情统计表(DayWaterSituationStatisticsTableSyyl)表服务接口
 *
 * @author makejava
 * @since 2023-12-23 16:00:16
 */
public interface DayWaterSituationStatisticsTableSyylService extends IService<DayWaterSituationStatisticsTableSyyl> {


    RestResponse<List<DayWaterSituationStatisticsTableSyyl>> selectList(String date);

    RestResponse add(List<DayWaterSituationStatisticsTableSyyl> dayWaterSituationStatisticsTableSyylList);

    RestResponse delete(String ids);

    RestResponse update(List<DayWaterSituationStatisticsTableSyyl> dayWaterSituationStatisticsTableSyylList);
}

