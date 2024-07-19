package com.cj.dataSynchronization.func.modular.lzz.job;

import com.alibaba.fastjson.JSONObject;
import com.cj.dataSynchronization.func.modular.lzz.service.LzzPlatformService;
import com.cj.middleDatabase.func.modular.lzz.lzzGaugingStation.entity.LzzGaugingStation;
import com.cj.middleDatabase.func.modular.lzz.lzzGaugingStation.service.LzzGaugingStationService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Calendar;
import java.util.Date;

//@EnableScheduling//开启定时任务
@Component
@Slf4j
public class LzzTask {

    @Autowired
    private LzzPlatformService lzzPlatformService;

    @Autowired
    private LzzGaugingStationService lzzGaugingStationService;


    @XxlJob("saveLzzDta")
    public  void saveLzzDta(){
        try {
            Date time = new Date();
            Date startTime = calculateTime(time,-1);
            Date endTime = calculateTime(time,1);
            lzzPlatformService.insertRainfallStationInfoBetweenTime(startTime,endTime,"");
            lzzPlatformService.insertReservoirLevelBetweenTime(startTime,endTime);
            lzzPlatformService.insertGaugingStationBetweenTime(startTime,endTime);
            lzzPlatformService.insertLzzBetweenTime(startTime,endTime);
            lzzPlatformService.insertLzzKqRailBetweenTime(startTime,endTime);
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getLocalizedMessage());
        }
    }

    @XxlJob("saveLzzInputFlow")
    private void saveLzzInputFlow(){
        try {
            lzzPlatformService.insertLzzInputFlow();
        }catch (Exception e) {
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

    @RabbitListener(queues = "lzzMsgQueue")
    public void receive(String msg) {
        log.info("接收到消息--" + msg);
        LzzGaugingStation station = JSONObject.parseObject(msg, LzzGaugingStation.class);
        boolean b = lzzGaugingStationService.saveOrUpdate(station);
        log.info("存入数据库的结果：" + b);
    }
}
