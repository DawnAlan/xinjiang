package com.cj.flood.func.modular.rollUpdate.job;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cj.common.feign.WaterSituationClient;
import com.cj.common.feign.entity.DropDown;
import com.cj.common.feign.entity.ExternStations;
import com.cj.common.feign.entity.QuXT;
import com.cj.common.feign.entity.RRs;
import com.cj.common.model.RestResponse;
import com.cj.common.util.ExcelUtils;
import com.cj.common.util.UUIDUtils;
import com.cj.flood.func.modular.dispatch.entity.FloodControlOperation;
import com.cj.flood.func.modular.prediction.entity.BasinParam;
import com.cj.flood.func.modular.prediction.entity.IncomingWaterForecast;
import com.cj.flood.func.modular.prediction.service.IncomingWaterForecastService;
import com.cj.flood.func.modular.rollUpdate.bean.dto.RealTimeEngineeringSituationDataDto;
import com.cj.flood.func.modular.rollUpdate.entity.ModelRollUpdate;
import com.cj.flood.func.modular.rollUpdate.entity.RollUpdateFloodControl;
import com.cj.flood.func.modular.rollUpdate.entity.RollUpdateIncomingWater;
import com.cj.flood.func.modular.rollUpdate.service.ModelRollUpdateService;
import com.cj.flood.func.modular.rollUpdate.service.RollUpdateFloodControlService;
import com.cj.flood.func.modular.rollUpdate.service.RollUpdateIncomingWaterService;
import com.cj.model.func.core.util.MinioUtils;
import com.cj.model.func.core.util.MultipartFileUtil;
import com.cj.model.func.modular.FloodPrevent.bean.req.ReqCurve;
import com.cj.model.func.modular.FloodPrevent.bean.req.ReqFloodPrevent;
import com.cj.model.func.modular.FloodPrevent.bean.res.ResOption;
import com.cj.model.func.modular.FloodPrevent.entity.CurveParam;
import com.cj.model.func.modular.FloodPrevent.entity.DataFloodPrevent;
import com.cj.model.func.modular.FloodPrevent.function.Cascade;
import com.cj.model.func.modular.entity.Flood;
import com.cj.waterresources.api.WaterResourceApi;
import com.xxl.job.core.handler.annotation.XxlJob;
import io.minio.ObjectWriteResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

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
                        incomingWaterId = rollUpdateIncomingWaterService.add(date, modelRollUpdate.getPeriodTimeCount(), modelRollUpdate.getId(),modelRollUpdate.getCreateBy());
                    }catch (Exception e) {
                        log.error("来水预报模执行失败，原因：{}", e.getMessage());
                        String remark = modelRollUpdate.getRemark();
                        modelRollUpdate.setRemark(StringUtils.isEmpty(remark)?"":" || " +"来水预报模型第"+modelRollUpdate.getCurrentRunCount()+"次执行失败");
                    }
                    //滚动新增防洪调度
                    if(StringUtils.isNotEmpty(incomingWaterId)){
                        try {
                            rollUpdateFloodControlService.add(incomingWaterId, modelRollUpdate);
                        }catch (Exception e) {
                            log.error("防洪调度执行失败，原因：{}", e.getMessage());
                            String remark = modelRollUpdate.getRemark();
                            modelRollUpdate.setRemark(StringUtils.isEmpty(remark)?"":" || " +"防洪调度模型第"+modelRollUpdate.getCurrentRunCount()+"次执行失败");
                        }
                    }
                    modelRollUpdateService.updateById(modelRollUpdate);
                }
            }
        }
        log.info("--------------------------------滚动更新结束----------------------------");
    }


}
