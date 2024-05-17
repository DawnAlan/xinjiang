package com.cj.dataSynchronization.func.modular.lzz.job;

import com.cj.common.model.RestResponse;
import com.cj.common.util.RedisUtil;
import com.cj.dataSynchronization.func.modular.lzz.bean.ParamDto;
import com.cj.dataSynchronization.func.modular.lzz.service.LzzPlatformService;
import com.cj.middleDatabase.func.modular.lzz.lzzGaugingStation.entity.LzzGaugingStation;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
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
import java.util.concurrent.TimeUnit;

//@EnableScheduling//开启定时任务
@Component
@Slf4j
public class LzzTask {

    @Autowired
    private LzzPlatformService lzzPlatformService;


    @XxlJob("saveLzzDta")
    public  void saveLzzDta(){
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
        calendar.add(Calendar.DATE,hour);
        Date date = calendar.getTime();
        return date;
    }
}
