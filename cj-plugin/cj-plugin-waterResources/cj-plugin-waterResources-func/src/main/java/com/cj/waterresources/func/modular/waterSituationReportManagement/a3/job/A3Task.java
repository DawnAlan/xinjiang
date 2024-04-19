package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.job;

import com.cj.common.util.RedisUtil;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.dkl.service.DayWaterSituationStatisticsTableDklService;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.hd.service.DayWaterSituationStatisticsTableHdService;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.hx.service.DayWaterSituationStatisticsTableHxService;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.lzz.service.DayWaterSituationStatisticsTableLzzService;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.qs.service.DayWaterSituationStatisticsTableQsLhService;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.qs.service.DayWaterSituationStatisticsTableQsService;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.tth.service.DayWaterSituationStatisticsTableTthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
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

    @Autowired
    private DayWaterSituationStatisticsTableDklService dklService;

    @Autowired
    private DayWaterSituationStatisticsTableQsLhService qsLhService;

    @Autowired
    private RedisUtil redisUtil;



    @Scheduled(cron="0 08 13 * * ?")//每天20:30
    public void createA3NotDkl(){
        log.info("--------------------------------执行定时插入A3（不包含对口率） 完8点后生成今日均数据----------------------------");
        tthService.insertTodayMeanValue();
        lzzService.insertTodayMeanValue();
        qsService.insertTodayMeanValue();
        hdService.insertTodayMeanValue();
        hxService.insertTodayMeanValue();
    }

    @Scheduled(cron="0 35 20 * * ?")//每天20:30
    public void createA3QsLh(){
        log.info("--------------------------------执行定时插入A3（灯笼渠绿化） 完8点后生成今日均数据----------------------------");
        qsLhService.insertTodayMeanValue();
    }

    @Scheduled(cron="0 00 23 * * ?")//每天23:00
    public void createA3HaveDkl(){
        log.info("--------------------------------执行定时插入A3（仅对口率） 完8点后生成今日均数据----------------------------");
        dklService.insertTodayMeanValue();
    }

    @Scheduled(cron="0 59 23 * * ?")//每天23:59
    public void deleteDayUseWaterPlan(){
        Set<String> allKeys = redisUtil.getAllKeys("A3:dayUseWaterPlanChoseTime");
        if(!allKeys.isEmpty()){
            allKeys.forEach(t->redisUtil.del(t));
            log.info("删除redis中A3:dayUseWaterPlanChoseTime数据成功");
        }
    }
}
