package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.tjc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.lzz.entity.DayWaterSituationStatisticsTableLzz;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.tjc.entity.DayWaterSituationStatisticsTableTjc;

import java.util.List;
import java.util.Map;

/**
 * 调节池日水情统计表(DayWaterSituationStatisticsTableTjc)表服务接口
 *
 * @author makejava
 * @since 2023-12-23 16:00:35
 */
public interface DayWaterSituationStatisticsTableTjcService extends IService<DayWaterSituationStatisticsTableTjc> {

    RestResponse<Map<String, List<DayWaterSituationStatisticsTableTjc>>> selectList(String date);

    RestResponse add(List<DayWaterSituationStatisticsTableTjc> dayWaterSituationStatisticsTableTjcList);

    RestResponse delete(String ids);

    RestResponse update(List<DayWaterSituationStatisticsTableTjc> dayWaterSituationStatisticsTableTjcList);
}

