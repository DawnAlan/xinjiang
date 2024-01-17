package com.cj.dataSynchronization.func.modular.lzz.job;

import com.cj.common.model.RestResponse;
import com.cj.common.util.RedisUtil;
import com.cj.dataSynchronization.func.modular.lzz.service.LzzPlatformService;
import com.cj.middleDatabase.func.modular.lzz.lzzGaugingStation.entity.LzzGaugingStation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@EnableScheduling//开启定时任务
@Component
@Slf4j
public class LzzTask {

    @Autowired
    private LzzPlatformService lzzPlatformService;

    @Autowired
    private RedisUtil redisUtil;


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
    @Scheduled(cron="0 05 8 ? * *")//每小时执行一次，以空格分隔
    //@Scheduled(cron="0 */5 * * * ?")
    public  void getReservoirLevelBetweenTime(){
        try {
            Date endTime = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(endTime);
            calendar.add(Calendar.DAY_OF_MONTH,-1);
            Date startTime = calendar.getTime();
            RestResponse<List<LzzGaugingStation>> reservoirLevelWaterLevelBetweenTime = lzzPlatformService.getReservoirLevelWaterLevelBetweenTime(startTime, endTime);
            if(reservoirLevelWaterLevelBetweenTime.getCode() ==200){
                List<LzzGaugingStation> data = reservoirLevelWaterLevelBetweenTime.getData();
                double waterLevel = data.stream().mapToDouble(LzzGaugingStation::getRelativeWaterLevel).average().orElseThrow(() -> new RuntimeException("Empty list"));
                double capacity = data.stream().mapToDouble(LzzGaugingStation::getStorageCapacity).average().orElseThrow(() -> new RuntimeException("Empty list"));
                redisUtil.set("lzz:average:waterLevel",waterLevel);
                redisUtil.set("lzz:average:capacity",capacity);
            }
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getLocalizedMessage());
        }
    }
}
