package com.cj.waterresources.func.modular.waterSituationReportManagement.a4.yesterday.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a4.yesterday.entity.WaterSituationStatisticsTableYesterday;

import java.util.List;

/**
 * 昨日水情日报表(WaterSituationStatisticsTableYesterday)表服务接口
 *
 * @author makejava
 * @since 2023-12-23 19:10:46
 */
public interface WaterSituationStatisticsTableYesterdayService extends IService<WaterSituationStatisticsTableYesterday> {

    RestResponse add(WaterSituationStatisticsTableYesterday waterSituationStatisticsTableYesterday);

    RestResponse delete(String id);

    RestResponse update(WaterSituationStatisticsTableYesterday waterSituationStatisticsTableYesterday);

    RestResponse<List<WaterSituationStatisticsTableYesterday>> select(String date);

}

