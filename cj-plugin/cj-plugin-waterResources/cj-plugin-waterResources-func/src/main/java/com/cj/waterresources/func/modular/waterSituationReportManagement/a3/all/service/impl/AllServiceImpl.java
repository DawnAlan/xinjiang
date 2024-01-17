package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.service.impl;

import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.service.AllService;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.dkl.mapper.DayWaterSituationStatisticsTableDklMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.hd.mapper.DayWaterSituationStatisticsTableHdMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.hx.mapper.DayWaterSituationStatisticsTableHxMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.lzz.mapper.DayWaterSituationStatisticsTableLzzMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.qs.mapper.DayWaterSituationStatisticsTableQsMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.syyl.mapper.DayWaterSituationStatisticsTableSyylMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.tjc.mapper.DayWaterSituationStatisticsTableTjcMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.tth.mapper.DayWaterSituationStatisticsTableTthMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.zcc.mapper.DayWaterSituationStatisticsTableZccMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class AllServiceImpl implements AllService {

    private final DayWaterSituationStatisticsTableDklMapper dayWaterSituationStatisticsTableDklMapper;
    private final DayWaterSituationStatisticsTableHdMapper dayWaterSituationStatisticsTableHdMapper;
    private final DayWaterSituationStatisticsTableHxMapper dayWaterSituationStatisticsTableHxMapper;
    private final DayWaterSituationStatisticsTableLzzMapper dayWaterSituationStatisticsTableLzzMapper;
    private final DayWaterSituationStatisticsTableQsMapper dayWaterSituationStatisticsTableQsMapper;
    private final DayWaterSituationStatisticsTableSyylMapper dayWaterSituationStatisticsTableSyylMapper;
    private final DayWaterSituationStatisticsTableTjcMapper dayWaterSituationStatisticsTableTjcMapper;
    private final DayWaterSituationStatisticsTableTthMapper dayWaterSituationStatisticsTableTthMapper;
    private final DayWaterSituationStatisticsTableZccMapper dayWaterSituationStatisticsTableZccMapper;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse deleteAll(String date) {
        try {
            dayWaterSituationStatisticsTableDklMapper.deleteByTime(date);
            dayWaterSituationStatisticsTableHdMapper.deleteByTime(date);
            dayWaterSituationStatisticsTableHxMapper.deleteByTime(date);
            dayWaterSituationStatisticsTableLzzMapper.deleteByTime(date);
            dayWaterSituationStatisticsTableQsMapper.deleteByTime(date);
            dayWaterSituationStatisticsTableSyylMapper.deleteByTime(date);
            dayWaterSituationStatisticsTableTjcMapper.deleteByTime(date);
            dayWaterSituationStatisticsTableTthMapper.deleteByTime(date);
            dayWaterSituationStatisticsTableZccMapper.deleteByTime(date);
            return RestResponse.ok();
        } catch (Exception e){
            return RestResponse.no("error");
        }
    }
}
