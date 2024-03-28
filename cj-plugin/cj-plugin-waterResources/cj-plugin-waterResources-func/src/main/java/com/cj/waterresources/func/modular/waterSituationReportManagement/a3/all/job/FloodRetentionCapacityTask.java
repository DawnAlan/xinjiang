package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.job;

import com.cj.common.util.RedisUtil;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.lzz.bean.res.LzzReportFormsRes;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.lzz.service.DayWaterSituationStatisticsTableLzzService;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.tth.bean.res.TthReportFormsRes;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.tth.service.DayWaterSituationStatisticsTableTthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@EnableScheduling//开启定时任务
@Component
@Slf4j
public class FloodRetentionCapacityTask {

    @Autowired
    private DayWaterSituationStatisticsTableLzzService dayWaterSituationStatisticsTableLzzService;

    @Autowired
    private DayWaterSituationStatisticsTableTthService dayWaterSituationStatisticsTableTthService;

    @Autowired
    private RedisUtil redisUtil;

    @Scheduled(cron="0 30 08 * * ?")//每天20:30
    public void floodRetentionCapacity(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String startTime = "2021-01-01";
        String endTime = sdf.format(new Date());
        List<LzzReportFormsRes> lzzReportFormsResList = dayWaterSituationStatisticsTableLzzService.selectReportForms(startTime, endTime).getData();
        List<TthReportFormsRes> tthReportFormsResList = dayWaterSituationStatisticsTableTthService.selectReportForms(startTime, endTime).getData();
        Double lzzYearFloodRetentionCapacity = 0.0;
        for(int i=lzzReportFormsResList.size()-1;i==0;i--){
            Double tempValue = lzzReportFormsResList.get(i).getStorageCapacity()-lzzReportFormsResList.get(i-1).getStorageCapacity();
            if(tempValue>0){
                lzzYearFloodRetentionCapacity+=tempValue;
            }
        }
        redisUtil.set("floodRetentionCapacity:lzz",lzzYearFloodRetentionCapacity);
        Double tthYearFloodRetentionCapacity = 0.0;
        for(int i=tthReportFormsResList.size()-1;i==0;i--){
            Double tempValue = tthReportFormsResList.get(i).getStorageCapacity()-tthReportFormsResList.get(i-1).getStorageCapacity();
            if(tempValue>0){
                tthYearFloodRetentionCapacity+=tempValue;
            }
        }
        redisUtil.set("floodRetentionCapacity:tth",tthYearFloodRetentionCapacity);
    }
}
