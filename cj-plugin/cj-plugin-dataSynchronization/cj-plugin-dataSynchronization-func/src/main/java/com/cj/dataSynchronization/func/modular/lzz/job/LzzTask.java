package com.cj.dataSynchronization.func.modular.lzz.job;

import com.cj.dataSynchronization.func.modular.lzz.service.LzzPlatformService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@EnableScheduling//开启定时任务
@Component
@Slf4j
public class LzzTask {

    @Autowired
    private LzzPlatformService lzzPlatformService;


    @Scheduled(cron="0 0 0/1 * * ?")//每小时执行一次，以空格分隔
    public  void saveAffluentLevelByOneHour(){
        try {
            lzzPlatformService.insertReservoirLevel(new Date());
            lzzPlatformService.insertRainfallStationInfo(new Date());
            lzzPlatformService.insertGaugingStation(new Date());
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getLocalizedMessage());
        }
    }
}
