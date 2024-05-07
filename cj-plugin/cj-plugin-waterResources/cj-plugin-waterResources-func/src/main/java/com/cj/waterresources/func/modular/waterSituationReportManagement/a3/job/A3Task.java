package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.job;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.cj.common.util.RedisUtil;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.dkl.entity.DayWaterSituationStatisticsTableDkl;
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

import java.text.SimpleDateFormat;
import java.util.Date;
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



    @Scheduled(cron="0 30 20 * * ?")//每天20:30
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

    //@Scheduled(cron="0 00 23 * * ?")//每天23:00
    //@Scheduled(cron="0 0 0/1 * * ?")//每小时执行一次，以空格分隔
    public void createA3HaveDklForTime(){
        log.info("--------------------------------执行定时插入A3（仅对口率） 当前时刻生成数据----------------------------");
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        dklService.insertTodayMeanValue(date,sdf.format(date));
        log.info("--------------------------------完成定时插入A3（仅对口率） 当前时刻生成数据----------------------------");
    }

    @Scheduled(cron="0 00 23 * * ?")//每天23:00
    //@Scheduled(cron="0 0 0/1 * * ?")//每小时执行一次，以空格分隔
    public void createA3HaveDklForToday(){
        log.info("--------------------------------执行定时插入A3（仅对口率） 晚8点后生成今日均数据----------------------------");
        Date date = new Date();
        dklService.insertTodayMeanValue(date,"今日均");
        log.info("--------------------------------完成定时插入A3（仅对口率） 晚8点后生成今日均数据----------------------------");
    }

    @Scheduled(cron="0 00 09 * * ?")//每天08:40
    public void createA3EightDkl(){
        log.info("--------------------------------执行定时插入A3对口率早8点数据----------------------------");
        List<DayWaterSituationStatisticsTableDkl> dayWaterSituationStatisticsTableDkls = JSON.parseArray(dklJson, DayWaterSituationStatisticsTableDkl.class);
        dayWaterSituationStatisticsTableDkls.forEach(t->t.setRecordTime(new Date()));
        dklService.add(dayWaterSituationStatisticsTableDkls);
        log.info("执行定时插入A3对口率早8点数据成功");
    }

    @Scheduled(cron="0 59 23 * * ?")//每天23:59
    public void deleteDayUseWaterPlan(){
        Set<String> allKeys = redisUtil.getAllKeys("A3:dayUseWaterPlanChoseTime");
        if(!allKeys.isEmpty()){
            allKeys.forEach(t->redisUtil.del(t));
            log.info("删除redis中A3:dayUseWaterPlanChoseTime数据成功");
        }
    }

    private String dklJson = "[\n" +
            "  {\n" +
            "    \"frontTableList\": \"[{\\\"id\\\":\\\"c6961e6ce3374a61a9bd82e7b8fc2d57\\\",\\\"paramName\\\":\\\"楼庄子-头屯河\\\",\\\"paramCode\\\":null,\\\"isParent\\\":null,\\\"orderNum\\\":1,\\\"useWaterType\\\":\\\"\\\",\\\"area\\\":null,\\\"category\\\":null,\\\"children\\\":null,\\\"pid\\\":\\\"0\\\"},{\\\"id\\\":\\\"5d4d47000d914df1b3ff963da4ac3ed3\\\",\\\"paramName\\\":\\\"头屯河-渠首\\\",\\\"paramCode\\\":null,\\\"isParent\\\":null,\\\"orderNum\\\":2,\\\"useWaterType\\\":\\\"\\\",\\\"area\\\":null,\\\"category\\\":null,\\\"children\\\":null,\\\"pid\\\":\\\"0\\\"},{\\\"id\\\":\\\"ff9116ab54b14f329f0f030546f84911\\\",\\\"paramName\\\":\\\"渠首-河东\\\",\\\"paramCode\\\":null,\\\"isParent\\\":null,\\\"orderNum\\\":3,\\\"useWaterType\\\":\\\"\\\",\\\"area\\\":null,\\\"category\\\":null,\\\"children\\\":null,\\\"pid\\\":\\\"0\\\"},{\\\"id\\\":\\\"e1b6ae78e830472dbdb366f0c732c945\\\",\\\"paramName\\\":\\\"渠首-河西\\\",\\\"paramCode\\\":null,\\\"isParent\\\":null,\\\"orderNum\\\":4,\\\"useWaterType\\\":\\\"\\\",\\\"area\\\":null,\\\"category\\\":null,\\\"children\\\":null,\\\"pid\\\":\\\"0\\\"}]\",\n" +
            "    \"endTableList\": \"c6961e6ce3374a61a9bd82e7b8fc2d57,5d4d47000d914df1b3ff963da4ac3ed3,ff9116ab54b14f329f0f030546f84911,e1b6ae78e830472dbdb366f0c732c945\",\n" +
            "    \"recordTime\": \"2024-04-24\",\n" +
            "    \"tableHeadId\": \"c6961e6ce3374a61a9bd82e7b8fc2d57\",\n" +
            "    \"time\": \"08:00\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"frontTableList\": \"[{\\\"id\\\":\\\"c6961e6ce3374a61a9bd82e7b8fc2d57\\\",\\\"paramName\\\":\\\"楼庄子-头屯河\\\",\\\"paramCode\\\":null,\\\"isParent\\\":null,\\\"orderNum\\\":1,\\\"useWaterType\\\":\\\"\\\",\\\"area\\\":null,\\\"category\\\":null,\\\"children\\\":null,\\\"pid\\\":\\\"0\\\"},{\\\"id\\\":\\\"5d4d47000d914df1b3ff963da4ac3ed3\\\",\\\"paramName\\\":\\\"头屯河-渠首\\\",\\\"paramCode\\\":null,\\\"isParent\\\":null,\\\"orderNum\\\":2,\\\"useWaterType\\\":\\\"\\\",\\\"area\\\":null,\\\"category\\\":null,\\\"children\\\":null,\\\"pid\\\":\\\"0\\\"},{\\\"id\\\":\\\"ff9116ab54b14f329f0f030546f84911\\\",\\\"paramName\\\":\\\"渠首-河东\\\",\\\"paramCode\\\":null,\\\"isParent\\\":null,\\\"orderNum\\\":3,\\\"useWaterType\\\":\\\"\\\",\\\"area\\\":null,\\\"category\\\":null,\\\"children\\\":null,\\\"pid\\\":\\\"0\\\"},{\\\"id\\\":\\\"e1b6ae78e830472dbdb366f0c732c945\\\",\\\"paramName\\\":\\\"渠首-河西\\\",\\\"paramCode\\\":null,\\\"isParent\\\":null,\\\"orderNum\\\":4,\\\"useWaterType\\\":\\\"\\\",\\\"area\\\":null,\\\"category\\\":null,\\\"children\\\":null,\\\"pid\\\":\\\"0\\\"}]\",\n" +
            "    \"endTableList\": \"c6961e6ce3374a61a9bd82e7b8fc2d57,5d4d47000d914df1b3ff963da4ac3ed3,ff9116ab54b14f329f0f030546f84911,e1b6ae78e830472dbdb366f0c732c945\",\n" +
            "    \"recordTime\": \"2024-04-24\",\n" +
            "    \"tableHeadId\": \"5d4d47000d914df1b3ff963da4ac3ed3\",\n" +
            "    \"time\": \"08:00\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"frontTableList\": \"[{\\\"id\\\":\\\"c6961e6ce3374a61a9bd82e7b8fc2d57\\\",\\\"paramName\\\":\\\"楼庄子-头屯河\\\",\\\"paramCode\\\":null,\\\"isParent\\\":null,\\\"orderNum\\\":1,\\\"useWaterType\\\":\\\"\\\",\\\"area\\\":null,\\\"category\\\":null,\\\"children\\\":null,\\\"pid\\\":\\\"0\\\"},{\\\"id\\\":\\\"5d4d47000d914df1b3ff963da4ac3ed3\\\",\\\"paramName\\\":\\\"头屯河-渠首\\\",\\\"paramCode\\\":null,\\\"isParent\\\":null,\\\"orderNum\\\":2,\\\"useWaterType\\\":\\\"\\\",\\\"area\\\":null,\\\"category\\\":null,\\\"children\\\":null,\\\"pid\\\":\\\"0\\\"},{\\\"id\\\":\\\"ff9116ab54b14f329f0f030546f84911\\\",\\\"paramName\\\":\\\"渠首-河东\\\",\\\"paramCode\\\":null,\\\"isParent\\\":null,\\\"orderNum\\\":3,\\\"useWaterType\\\":\\\"\\\",\\\"area\\\":null,\\\"category\\\":null,\\\"children\\\":null,\\\"pid\\\":\\\"0\\\"},{\\\"id\\\":\\\"e1b6ae78e830472dbdb366f0c732c945\\\",\\\"paramName\\\":\\\"渠首-河西\\\",\\\"paramCode\\\":null,\\\"isParent\\\":null,\\\"orderNum\\\":4,\\\"useWaterType\\\":\\\"\\\",\\\"area\\\":null,\\\"category\\\":null,\\\"children\\\":null,\\\"pid\\\":\\\"0\\\"}]\",\n" +
            "    \"endTableList\": \"c6961e6ce3374a61a9bd82e7b8fc2d57,5d4d47000d914df1b3ff963da4ac3ed3,ff9116ab54b14f329f0f030546f84911,e1b6ae78e830472dbdb366f0c732c945\",\n" +
            "    \"recordTime\": \"2024-04-24\",\n" +
            "    \"tableHeadId\": \"ff9116ab54b14f329f0f030546f84911\",\n" +
            "    \"time\": \"08:00\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"frontTableList\": \"[{\\\"id\\\":\\\"c6961e6ce3374a61a9bd82e7b8fc2d57\\\",\\\"paramName\\\":\\\"楼庄子-头屯河\\\",\\\"paramCode\\\":null,\\\"isParent\\\":null,\\\"orderNum\\\":1,\\\"useWaterType\\\":\\\"\\\",\\\"area\\\":null,\\\"category\\\":null,\\\"children\\\":null,\\\"pid\\\":\\\"0\\\"},{\\\"id\\\":\\\"5d4d47000d914df1b3ff963da4ac3ed3\\\",\\\"paramName\\\":\\\"头屯河-渠首\\\",\\\"paramCode\\\":null,\\\"isParent\\\":null,\\\"orderNum\\\":2,\\\"useWaterType\\\":\\\"\\\",\\\"area\\\":null,\\\"category\\\":null,\\\"children\\\":null,\\\"pid\\\":\\\"0\\\"},{\\\"id\\\":\\\"ff9116ab54b14f329f0f030546f84911\\\",\\\"paramName\\\":\\\"渠首-河东\\\",\\\"paramCode\\\":null,\\\"isParent\\\":null,\\\"orderNum\\\":3,\\\"useWaterType\\\":\\\"\\\",\\\"area\\\":null,\\\"category\\\":null,\\\"children\\\":null,\\\"pid\\\":\\\"0\\\"},{\\\"id\\\":\\\"e1b6ae78e830472dbdb366f0c732c945\\\",\\\"paramName\\\":\\\"渠首-河西\\\",\\\"paramCode\\\":null,\\\"isParent\\\":null,\\\"orderNum\\\":4,\\\"useWaterType\\\":\\\"\\\",\\\"area\\\":null,\\\"category\\\":null,\\\"children\\\":null,\\\"pid\\\":\\\"0\\\"}]\",\n" +
            "    \"endTableList\": \"c6961e6ce3374a61a9bd82e7b8fc2d57,5d4d47000d914df1b3ff963da4ac3ed3,ff9116ab54b14f329f0f030546f84911,e1b6ae78e830472dbdb366f0c732c945\",\n" +
            "    \"recordTime\": \"2024-04-24\",\n" +
            "    \"tableHeadId\": \"e1b6ae78e830472dbdb366f0c732c945\",\n" +
            "    \"time\": \"08:00\"\n" +
            "  }\n" +
            "]";
}
