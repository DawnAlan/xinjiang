package com.cj.dataSynchronization.func.modular.lzz.job;

import com.cj.dataSynchronization.func.modular.lzz.service.LzzPlatformService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@EnableScheduling//开启定时任务
@Component
@Slf4j
public class LzzTask {

    @Autowired
    private LzzPlatformService lzzPlatformService;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    @Scheduled(cron="0 0 0/1 * * ?")//每小时执行一次，以空格分隔
    //@Scheduled(cron="0 */5 * * * ?")
    public  void saveAffluentLevelByOneHour(){
        try {
            Date endTime = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(endTime);
            calendar.add(Calendar.HOUR,-2);
            Date startTime = calendar.getTime();
            lzzPlatformService.insertRainfallStationInfoBetweenTime(startTime,endTime);
            lzzPlatformService.insertReservoirLevelBetweenTime(startTime,endTime);
            lzzPlatformService.insertGaugingStationBetweenTime(startTime,endTime);
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getLocalizedMessage());
        }
    }
}
