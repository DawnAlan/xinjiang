package com.cj.waterresources.func.modular.waterSituationReportManagement.a4.today.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a4.today.entity.WaterSituationStatisticsTableToday;

import java.util.List;

/**
 * 今日水情日报表(WaterSituationStatisticsTableToday)表服务接口
 *
 * @author makejava
 * @since 2023-12-23 19:11:07
 */
public interface WaterSituationStatisticsTableTodayService extends IService<WaterSituationStatisticsTableToday> {

    RestResponse add(WaterSituationStatisticsTableToday waterSituationStatisticsTableToday);

    RestResponse delete(String id);

    RestResponse update(WaterSituationStatisticsTableToday waterSituationStatisticsTableToday);

    RestResponse<List<WaterSituationStatisticsTableToday>> select(String date);

}

