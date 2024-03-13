package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.job;

import com.alibaba.fastjson.JSONObject;
import com.cj.common.model.RestResponse;
import com.cj.common.util.RedisUtil;
import com.cj.waterresources.func.modular.trendsTable.bean.res.WaterDailyParamSelectRes;
import com.cj.waterresources.func.modular.trendsTable.entity.TrendsTableParam;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.bean.res.JobRes;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.entity.WaterFeeStatisticsDetails;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.service.WaterFeeStatisticsDetailsService;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.hd.service.DayWaterSituationStatisticsTableHdService;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.hx.service.DayWaterSituationStatisticsTableHxService;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.lzz.service.DayWaterSituationStatisticsTableLzzService;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.qs.service.DayWaterSituationStatisticsTableQsService;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.tth.service.DayWaterSituationStatisticsTableTthService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@EnableScheduling//开启定时任务
@Component
@Slf4j
public class A3Task {
    @Autowired
    private DayWaterSituationStatisticsTableTthService tthService;

    @Autowired
    private DayWaterSituationStatisticsTableLzzService lzzService;

    @Autowired
    private DayWaterSituationStatisticsTableQsService qsService;

    @Autowired
    private DayWaterSituationStatisticsTableHdService hdService;

    @Autowired
    private DayWaterSituationStatisticsTableHxService hxService;



    @Scheduled(cron="0 30 20 * * ?")//每天20:30
    public void createWaterFeeTableNoHaveQs(){
        log.info("--------------------------------执行定时插入A3 完8点后生成今日均数据----------------------------");
        tthService.insertTodayMeanValue();
        lzzService.insertTodayMeanValue();
        qsService.insertTodayMeanValue();
        hdService.insertTodayMeanValue();
        hxService.insertTodayMeanValue();
    }
}
