package com.cj.dataSynchronization.func.modular.lzz.job;

import com.cj.common.model.RestResponse;
import com.cj.common.util.RedisUtil;
import com.cj.dataSynchronization.func.modular.lzz.bean.ParamDto;
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
import java.util.Map;

@EnableScheduling//开启定时任务
@Component
@Slf4j
public class LzzTask {

    @Autowired
    private LzzPlatformService lzzPlatformService;

    @Autowired
    private RedisUtil redisUtil;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH");


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
            lzzPlatformService.insertLzzBetweenTime(startTime,endTime);
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getLocalizedMessage());
        }
    }
    @Scheduled(cron="0 0 0/1 * * ?")//每小时执行一次，以空格分隔
    //@Scheduled(cron="0 */5 * * * ?")
    public  void getReservoirLevelBetweenTime(){
        try {
            Date date = new Date();
            RestResponse<LzzGaugingStation> reservoirLevelWaterLevelByTime = lzzPlatformService.getReservoirLevelWaterLevelByTime(date);
            if(reservoirLevelWaterLevelByTime.getCode() ==200){
                LzzGaugingStation data = reservoirLevelWaterLevelByTime.getData();
                redisUtil.set("lzz:waterLevel:"+sdf.format(date),data.getRelativeWaterLevel());
                redisUtil.set("lzz:capacity:"+sdf.format(date),data.getStorageCapacity());
            }
            RestResponse<Map<String, ParamDto>> lzzInfoByTime = lzzPlatformService.getLzzInfoByTime(date);
            if(lzzInfoByTime.getCode() ==200){
                Map<String, ParamDto> data = lzzInfoByTime.getData();
                redisUtil.set("lzz:waterworks:1:"+sdf.format(date),data.get("one").getV());
                redisUtil.set("lzz:waterworks:2:"+sdf.format(date),data.get("two").getV());
            }
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getLocalizedMessage());
        }
    }
}
