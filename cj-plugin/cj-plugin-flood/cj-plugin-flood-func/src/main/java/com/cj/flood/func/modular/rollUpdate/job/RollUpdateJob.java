package com.cj.flood.func.modular.rollUpdate.job;

import com.cj.flood.func.modular.rollUpdate.entity.ModelRollUpdate;
import com.cj.flood.func.modular.rollUpdate.entity.RollUpdateIncomingWater;
import com.cj.flood.func.modular.rollUpdate.service.ModelRollUpdateService;
import com.cj.flood.func.modular.rollUpdate.service.RollUpdateFloodControlService;
import com.cj.flood.func.modular.rollUpdate.service.RollUpdateIncomingWaterService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import java.util.*;

@EnableScheduling//开启定时任务
@Component
@Slf4j
public class RollUpdateJob {

    @Autowired
    private ModelRollUpdateService modelRollUpdateService;

    @Autowired
    private RollUpdateIncomingWaterService rollUpdateIncomingWaterService;

    @Autowired
    private RollUpdateFloodControlService rollUpdateFloodControlService;


    @XxlJob("modelRollUpdate")
    public void modelRollUpdate(){
        log.info("--------------------------------滚动更新开始----------------------------");
        List<ModelRollUpdate> list = modelRollUpdateService.lambdaQuery().eq(ModelRollUpdate::getRunStatus, 0).list();
        for (ModelRollUpdate modelRollUpdate : list) {
            if(modelRollUpdate.getCurrentRunCount()==modelRollUpdate.getRefreshCount()){
                modelRollUpdateService.lambdaUpdate().set(ModelRollUpdate::getRunStatus, 1).eq(ModelRollUpdate::getId,modelRollUpdate.getId()).update();
            }else {
                Date date = new Date();
                RollUpdateIncomingWater one = rollUpdateIncomingWaterService.lambdaQuery().orderByDesc(RollUpdateIncomingWater::getCreateTime).last("limit 1").one();
                long difference  = date.getTime()-one.getCreateTime().getTime();
                long minutes = difference / (60 * 1000);
                if(minutes==modelRollUpdate.getRefreshFrequency()){
                    modelRollUpdate.setCurrentRunCount(modelRollUpdate.getCurrentRunCount()+1);
                    String incomingWaterId ="";
                    //滚动新增来水预报
                    try {
                        incomingWaterId = rollUpdateIncomingWaterService.add(date, modelRollUpdate,modelRollUpdate.getCreateBy());
                    }catch (Exception e) {
                        log.error("来水预报模执行失败，原因：{}", e.getMessage());
                        String remark = modelRollUpdate.getRemark();
                        modelRollUpdate.setRemark(StringUtils.isEmpty(remark)?""+"来水预报模型第"+modelRollUpdate.getCurrentRunCount()+"次执行失败":remark+" || " +"来水预报模型第"+modelRollUpdate.getCurrentRunCount()+"次执行失败");
                    }
                    //滚动新增防洪调度
                    if(StringUtils.isNotEmpty(incomingWaterId)){
                        try {
                            rollUpdateFloodControlService.add(incomingWaterId, modelRollUpdate);
                        }catch (Exception e) {
                            log.error("防洪调度执行失败，原因：{}", e.getMessage());
                            String remark = modelRollUpdate.getRemark();
                            modelRollUpdate.setRemark(StringUtils.isEmpty(remark)?""+"防洪调度模型第"+modelRollUpdate.getCurrentRunCount()+"次执行失败":remark+" || " +"防洪调度模型第"+modelRollUpdate.getCurrentRunCount()+"次执行失败");
                        }
                    }
                    modelRollUpdateService.updateById(modelRollUpdate);
                }
            }
        }
        log.info("--------------------------------滚动更新结束----------------------------");
    }
}
