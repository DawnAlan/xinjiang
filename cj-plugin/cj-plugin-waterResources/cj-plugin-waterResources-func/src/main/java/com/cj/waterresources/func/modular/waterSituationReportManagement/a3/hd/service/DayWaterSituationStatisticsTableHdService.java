package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.hd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.hd.entity.DayWaterSituationStatisticsTableHd;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.lzz.entity.DayWaterSituationStatisticsTableLzz;

import java.util.List;
import java.util.Map;

/**
 * 河东管理站日水情统计表(DayWaterSituationStatisticsTableHd)表服务接口
 *
 * @author makejava
 * @since 2023-12-23 15:58:47
 */
public interface DayWaterSituationStatisticsTableHdService extends IService<DayWaterSituationStatisticsTableHd> {

    RestResponse<Map<String, List<DayWaterSituationStatisticsTableHd>>> selectList(String date);

    RestResponse add(List<DayWaterSituationStatisticsTableHd> dayWaterSituationStatisticsTableHdList);

    RestResponse delete(String ids);

    RestResponse update(List<DayWaterSituationStatisticsTableHd> dayWaterSituationStatisticsTableHdList);

}

