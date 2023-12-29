package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.zcc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.tth.entity.DayWaterSituationStatisticsTableTth;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.zcc.entity.DayWaterSituationStatisticsTableZcc;

import java.util.List;
import java.util.Map;

/**
 * 制材厂日水情统计表(DayWaterSituationStatisticsTableZcc)表服务接口
 *
 * @author makejava
 * @since 2023-12-23 16:01:31
 */
public interface DayWaterSituationStatisticsTableZccService extends IService<DayWaterSituationStatisticsTableZcc> {

    RestResponse<List<DayWaterSituationStatisticsTableZcc>> selectList(String date);

    RestResponse add(List<DayWaterSituationStatisticsTableZcc> dayWaterSituationStatisticsTableZccList);

    RestResponse delete(String ids);

    RestResponse update(List<DayWaterSituationStatisticsTableZcc> dayWaterSituationStatisticsTableZccList);

}

