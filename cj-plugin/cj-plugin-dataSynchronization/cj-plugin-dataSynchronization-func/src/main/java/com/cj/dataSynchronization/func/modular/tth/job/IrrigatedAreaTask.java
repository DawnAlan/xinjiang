package com.cj.dataSynchronization.func.modular.tth.job;

import com.cj.dataSynchronization.func.modular.tth.service.IrrigatedAreaService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class IrrigatedAreaTask {

    @Autowired
    private IrrigatedAreaService irrigatedAreaService;


    @XxlJob("saveTthData")
    public  void saveTthData(){
        try {
           irrigatedAreaService.getDataById();
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getLocalizedMessage());
        }
    }
}
