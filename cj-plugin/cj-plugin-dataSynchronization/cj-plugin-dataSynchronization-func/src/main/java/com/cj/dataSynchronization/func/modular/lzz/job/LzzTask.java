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

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");


    @Scheduled(cron="0 0 0/1 * * ?")//每小时执行一次，以空格分隔
    //@Scheduled(cron="0 */5 * * * ?")
    public  void saveAffluentLevelByOneHour(){
        try {
            Date time = new Date();
            Date startTime = calculateTime(time,-1);
            Date endTime = calculateTime(time,1);
            lzzPlatformService.insertRainfallStationInfoBetweenTime(startTime,endTime);
            lzzPlatformService.insertReservoirLevelBetweenTime(startTime,endTime);
            lzzPlatformService.insertGaugingStationBetweenTime(startTime,endTime);
            lzzPlatformService.insertLzzBetweenTime(startTime,endTime);
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getLocalizedMessage());
        }
    }

    private Date calculateTime(Date time,int hour){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        calendar.add(Calendar.HOUR,hour);
        Date date = calendar.getTime();
        return date;
    }
}
