package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.lzz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.lzz.entity.DayWaterSituationStatisticsTableLzz;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 楼庄子水库日水情统计表(DayWaterSituationStatisticsTableLzz)表服务接口
 *
 * @author makejava
 * @since 2023-12-23 15:59:33
 */
public interface DayWaterSituationStatisticsTableLzzService extends IService<DayWaterSituationStatisticsTableLzz> {

    RestResponse<Map<String,List<DayWaterSituationStatisticsTableLzz>>> selectList(String date);

    RestResponse add(List<DayWaterSituationStatisticsTableLzz> dayWaterSituationStatisticsTableLzzList);

    RestResponse delete(String ids);

    RestResponse update(List<DayWaterSituationStatisticsTableLzz> dayWaterSituationStatisticsTableLzzList);

}

