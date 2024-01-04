package com.cj.dataSynchronization.func.modular.tth.job;

import com.cj.dataSynchronization.func.modular.tth.service.IrrigatedAreaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@EnableScheduling//开启定时任务
@Component
@Slf4j
public class IrrigatedAreaTask {

    @Autowired
    private IrrigatedAreaService irrigatedAreaService;


    @Scheduled(cron="0 */6 * * * ?")//每小时执行一次，以空格分隔
    public  void saveAffluentLevelByOneHour(){
        try {
           irrigatedAreaService.getDataById();
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getLocalizedMessage());
        }
    }
}
