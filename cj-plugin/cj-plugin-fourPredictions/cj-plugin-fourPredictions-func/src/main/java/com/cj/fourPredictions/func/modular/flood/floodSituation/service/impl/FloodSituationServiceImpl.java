package com.cj.fourPredictions.func.modular.flood.floodSituation.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.cj.common.model.RestResponse;
import com.cj.flood.api.PredictionApi;
import com.cj.fourPredictions.func.modular.flood.floodSituation.bean.res.*;
import com.cj.fourPredictions.func.modular.flood.floodSituation.bean.req.SelectHistoryReq;
import com.cj.fourPredictions.func.modular.flood.floodSituation.service.FloodSituationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class FloodSituationServiceImpl implements FloodSituationService {

    @Resource
    private PredictionApi predictionApi;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH");

    @Override
    public RestResponse<List<RealTimeRainfallRes>> getRealTimeRainfall(String date, Integer hour) {
        try {
            Date endTime = null;
            if (StringUtils.isEmpty(date)){
                endTime = new Date();
            }else {
                endTime = sdf.parse(date);
            }
            Date startTime = calculateTime(sdf.format(endTime),-hour);
            String realTimeRainfall = predictionApi.getRealTimeRainfall(sdf.format(startTime), sdf.format(endTime));
            if(StringUtils.isNotEmpty(realTimeRainfall)){
                return RestResponse.ok(JSONObject.parseArray(realTimeRainfall, RealTimeRainfallRes.class));
            }else {
                return RestResponse.no("blank");
            }
        }catch (Exception e){
            return RestResponse.no("select error");
        }
    }

    @Override
    public RestResponse<List<RealTimeEngineeringSituationDataRes>> getRealTimeWaterLevelData(String date) {
        String realTimeWaterLevelData = predictionApi.getRealTimeWaterLevelData(date);
        if(StringUtils.isNotEmpty(realTimeWaterLevelData)){
            return RestResponse.ok(JSONObject.parseArray(realTimeWaterLevelData, RealTimeEngineeringSituationDataRes.class));
        }else {
            return RestResponse.no("blank");
        }
    }

    @Override
    public RestResponse<List<RealTimeWaterLevelDataRes>> getRealTimeReservoirLevelData(String date) {
        String realTimeReservoirLevelData = predictionApi.getRealTimeReservoirLevelData(date);
        if(StringUtils.isNotEmpty(realTimeReservoirLevelData)){
            return RestResponse.ok(JSONObject.parseArray(realTimeReservoirLevelData, RealTimeWaterLevelDataRes.class));
        }else {
            return RestResponse.no("blank");
        }
    }

    @Override
    public RestResponse<List<RainfallStationsHistoricalDataRes>> getRainfallStationsHistoricalData(SelectHistoryReq req) {
        String data = predictionApi.getRainfallStationsHistoricalData(req.getName(),req.getStartTime(),req.getEndTime());
        if(StringUtils.isNotEmpty(data)){
            return RestResponse.ok(JSONObject.parseArray(data, RainfallStationsHistoricalDataRes.class));
        }else {
            return RestResponse.no("blank");
        }
    }

    @Override
    public RestResponse<List<ReservoirLevelRes>> getReservoirLevel(SelectHistoryReq req) {
        String data = predictionApi.getReservoirLevel(req.getName(),req.getStartTime(),req.getEndTime());
        if(StringUtils.isNotEmpty(data)){
            return RestResponse.ok(JSONObject.parseArray(data, ReservoirLevelRes.class));
        }else {
            return RestResponse.no("blank");
        }
    }

    @Override
    public RestResponse<List<WaterLevelDataRes>> getWaterLevelData(SelectHistoryReq req) {
        String data = predictionApi.getWaterLevelData(req.getName(),req.getStartTime(),req.getEndTime());
        if(StringUtils.isNotEmpty(data)){
            return RestResponse.ok(JSONObject.parseArray(data, WaterLevelDataRes.class));
        }else {
            return RestResponse.no("blank");
        }
    }

    private Date calculateTime(String date, int hour){
        try {
            if(StringUtils.isEmpty(date)){
                date = sdf.format(new Date());
            }
            Calendar c1 = Calendar.getInstance();
            try {
                c1.setTime(sdf.parse(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            c1.add(Calendar.HOUR,hour);
            Date back=c1.getTime();
            return sdf.parse(sdf.format(back));
        }catch (Exception e) {
            return new Date();
        }
    }
}
