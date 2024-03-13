package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.hx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.hx.entity.DayWaterSituationStatisticsTableHx;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.lzz.entity.DayWaterSituationStatisticsTableLzz;

import java.util.List;
import java.util.Map;

/**
 * 河西管理站日水情统计表(DayWaterSituationStatisticsTableHx)表服务接口
 *
 * @author makejava
 * @since 2023-12-23 15:59:12
 */
public interface DayWaterSituationStatisticsTableHxService extends IService<DayWaterSituationStatisticsTableHx> {

    RestResponse<Map<String, List<DayWaterSituationStatisticsTableHx>>> selectList(String date);

    RestResponse add(List<DayWaterSituationStatisticsTableHx> dayWaterSituationStatisticsTableHxList);

    RestResponse delete(String ids);

    RestResponse update(List<DayWaterSituationStatisticsTableHx> dayWaterSituationStatisticsTableHxList);

    RestResponse insertTodayMeanValue();
}

