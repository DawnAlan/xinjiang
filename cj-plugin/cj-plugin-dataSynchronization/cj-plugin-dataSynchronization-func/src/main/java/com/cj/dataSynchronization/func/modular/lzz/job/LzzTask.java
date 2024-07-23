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
            Thread.sleep(10000);
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
        LzzGaugingStation one = lzzGaugingStationService.lambdaQuery().eq(LzzGaugingStation::getTreeId, station.getTreeId()).orderByDesc(LzzGaugingStation::getGatherTime).last("limit 1").one();
        station.setRelativeWaterLevel(station.getRelativeWaterLevel()==null? one.getRelativeWaterLevel() : station.getRelativeWaterLevel());
        station.setFlow(station.getFlow()==null? one.getFlow() : station.getFlow());
        station.setFlowRate(station.getFlowRate()==null? one.getFlowRate() : station.getFlowRate());
        station.setTotalFlow(station.getTotalFlow()==null? one.getTotalFlow() : station.getTotalFlow());
        station.setRelativeWaterLevelTwo(station.getRelativeWaterLevelTwo()==null? one.getRelativeWaterLevelTwo() : station.getRelativeWaterLevelTwo());
        station.setFlowTwo(station.getFlowTwo()==null? one.getFlowTwo() : station.getFlowTwo());
        station.setFlowRateTwo(station.getFlowRateTwo()==null? one.getFlowRateTwo() : station.getFlowRateTwo());
        station.setRelativeWaterLevelThree(station.getRelativeWaterLevelThree()==null? one.getRelativeWaterLevelThree() : station.getRelativeWaterLevelThree());
        station.setFlowThree(station.getFlowThree()==null? one.getFlowThree() : station.getFlowThree());
        station.setFlowRateThree(station.getFlowRateThree()==null? one.getFlowRateThree() : station.getFlowRateThree());
        station.setTotalFlowTwo(station.getTotalFlowTwo()==null? one.getTotalFlowTwo() : station.getTotalFlowTwo());
        station.setTotalFlowThree(station.getTotalFlowThree()==null? one.getTotalFlowThree() : station.getTotalFlowThree());
        boolean b = lzzGaugingStationService.saveOrUpdate(station);

        log.info("存入数据库的结果：" + b);
    }
}
