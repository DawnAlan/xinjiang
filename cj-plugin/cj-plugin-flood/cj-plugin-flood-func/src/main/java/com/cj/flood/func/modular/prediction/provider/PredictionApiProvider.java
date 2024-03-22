package com.cj.flood.func.modular.prediction.provider;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.cj.common.model.RestResponse;
import com.cj.common.util.RedisUtil;
import com.cj.flood.api.PredictionApi;
import com.cj.flood.func.modular.dispatch.entity.FloodControlOperation;
import com.cj.flood.func.modular.dispatch.service.FloodControlOperationService;
import com.cj.flood.func.modular.homePage.service.FloodHomePageService;
import com.cj.flood.func.modular.prediction.bean.dto.PredictionFrontViewDto;
import com.cj.flood.func.modular.prediction.bean.dto.PredictionProcessDto;
import com.cj.flood.func.modular.prediction.bean.res.*;
import com.cj.flood.func.modular.prediction.entity.IncomingWaterForecast;
import com.cj.flood.func.modular.prediction.service.IncomingWaterForecastService;
import com.cj.middleDatabase.func.modular.dto.RealTimeRainfallRes;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.entity.IrrigatedPlatformDataInfo;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.service.IrrigatedPlatformDataInfoService;
import com.cj.middleDatabase.func.modular.lzz.lzzGaugingStation.entity.LzzGaugingStation;
import com.cj.middleDatabase.func.modular.lzz.lzzGaugingStation.service.LzzGaugingStationService;
import com.cj.middleDatabase.func.modular.lzz.lzzRainfallStation.entity.LzzRainfallStation;
import com.cj.middleDatabase.func.modular.lzz.lzzRainfallStation.service.LzzRainfallStationService;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PredictionApiProvider implements PredictionApi {

    @Resource
    private IncomingWaterForecastService incomingWaterForecastService;

    @Resource
    private LzzRainfallStationService lzzRainfallStationService;

    @Resource
    private LzzGaugingStationService lzzGaugingStationService;

    @Resource
    private FloodControlOperationService floodControlOperationService;

    @Resource
    private FloodHomePageService floodHomePageService;

    @Resource
    private RedisUtil redisUtil;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    @Resource
    private IrrigatedPlatformDataInfoService irrigatedPlatformDataInfoService;
    @Override
    public String getProgrammeListByTime(String startTime, String endTime) {
        List<IncomingWaterForecast> list = incomingWaterForecastService.lambdaQuery().between(IncomingWaterForecast::getPredictionTime, startTime, endTime).list();
        if(null != list && list.size() > 0) {
            return JSONObject.toJSONString(list);
        }
        return null;
    }

    @Override
    public String getProgrammeList() {
        List<IncomingWaterForecast> list = incomingWaterForecastService.lambdaQuery().eq(IncomingWaterForecast::getModelType,3).list();
        if(null != list && list.size() > 0) {
            return JSONObject.toJSONString(list);
        }
        return null;
    }

    @Override
    public String getProgrammeDetails(String id) {
        RestResponse<IncomingWaterForecastDetailsRes> incomingWaterForecastDetailsResRestResponse = incomingWaterForecastService.selectDetails(id);
        if (incomingWaterForecastDetailsResRestResponse.getCode()==200){
            return JSONObject.toJSONString(incomingWaterForecastDetailsResRestResponse.getData());
        }
        return null;
    }

    @Override
    public String getRealTimeRainfall(String startTime, String endTime) {
        List<RealTimeRainfallRes> result = new ArrayList<>();
        List<RealTimeRainfallRes> lzzRealTimeRainfall = lzzRainfallStationService.getRealTimeRainfall(startTime, endTime);
        List<RealTimeRainfallRes> irrigatedRealTimeRainfall1 = irrigatedPlatformDataInfoService.getRealTimeRainfall(startTime, endTime);
        result.addAll(lzzRealTimeRainfall);
        result.addAll(irrigatedRealTimeRainfall1);
        if(result.size()>0){
            return JSONObject.toJSONString(result);
        }
        return null;
    }

    @SneakyThrows
    @Override
    public String getRealTimeWaterLevelData(String date) {
        List<RealTimeEngineeringSituationDataRes> result = new ArrayList<>();
        LzzGaugingStation lzzGaugingStation = lzzGaugingStationService.selectInfoByTime(date.split(":")[0],"楼庄子库水位站");
        RealTimeEngineeringSituationDataRes lzzData = new RealTimeEngineeringSituationDataRes();
        if(null!=lzzGaugingStation){
            lzzData.setReservoirName(lzzGaugingStation.getStationName());
            lzzData.setFloodControlLevel(1394.50);
            if(lzzGaugingStation.getRelativeWaterLevel()<0){
                Set<String> allKeys = redisUtil.getAllKeys("lzz:time:waterLevel:true");
                List<Date> dateList = new ArrayList<>();
                for(String s:allKeys){
                    String[] split1 = s.split(" ");
                    String[] split2 = split1[0].split(":");
                    String dateTemp =split2[split2.length-1]+" "+split1[split1.length-1];
                    Date parse = sdf1.parse(dateTemp);
                    dateList.add(parse);
                }
                List<Date> collect = dateList.stream().sorted(Comparator.comparing(Date::getDate, Comparator.reverseOrder())).collect(Collectors.toList());
                Double v = (Double) redisUtil.get("lzz:time:waterLevel:true:"+sdf1.format(collect.get(0)));
                lzzData.setRealTimeWaterLevel(v);
            }else {
                lzzData.setRealTimeWaterLevel(lzzGaugingStation.getRelativeWaterLevel());
            }
            lzzData.setUsedStorageCapacity(lzzGaugingStation.getStorageCapacity());
            lzzData.setRemainingStorageCapacity(7374.0 - lzzData.getUsedStorageCapacity());
        }else {
            Set<String> allKeys = redisUtil.getAllKeys("lzz:time:waterLevel:true");
            List<Date> dateList = new ArrayList<>();
            for(String s:allKeys){
                if(s.contains("日均")){
                    continue;
                }
                String[] split1 = s.split(" ");
                String[] split2 = split1[0].split(":");
                String dateTemp =split2[split2.length-1]+" "+split1[split1.length-1];
                Date parse = sdf1.parse(dateTemp);
                dateList.add(parse);
            }
            List<Date> collect = dateList.stream().sorted(Comparator.comparing(Date::getDate, Comparator.reverseOrder())).collect(Collectors.toList());
            Double v = (Double) redisUtil.get("lzz:time:waterLevel:true:"+sdf1.format(collect.get(0)));
            lzzData.setRealTimeWaterLevel(v);
            lzzData.setReservoirName("楼庄子库水位站");
            lzzData.setFloodControlLevel(1394.50);
            lzzData.setUsedStorageCapacity(null);
            lzzData.setRemainingStorageCapacity(null);
        }
        result.add(lzzData);
        List<IrrigatedPlatformDataInfo>  irrigatedPlatformDataInfoList = irrigatedPlatformDataInfoService.selectInfoByTime(date,"头屯河水库水位");
        IrrigatedPlatformDataInfo irrigatedPlatformDataInfo;
        if(null!= irrigatedPlatformDataInfoList && irrigatedPlatformDataInfoList.size()>0){
            irrigatedPlatformDataInfo = irrigatedPlatformDataInfoList.get(0);
        }else {
            irrigatedPlatformDataInfo = null;
        }
        RealTimeEngineeringSituationDataRes tthData = new RealTimeEngineeringSituationDataRes();
        if(null!=irrigatedPlatformDataInfo){
            tthData.setReservoirName(irrigatedPlatformDataInfo.getMonitorName());
            tthData.setFloodControlLevel(988.0);
            tthData.setRealTimeWaterLevel(irrigatedPlatformDataInfo.getSqWaterLevel());
            tthData.setUsedStorageCapacity(irrigatedPlatformDataInfo.getSqCapacity());
            tthData.setRemainingStorageCapacity(2030.0 - tthData.getUsedStorageCapacity());
        }else {
            tthData.setReservoirName("头屯河水库水位");
            tthData.setFloodControlLevel(988.0);
            tthData.setRealTimeWaterLevel(null);
            tthData.setUsedStorageCapacity(null);
            tthData.setRemainingStorageCapacity(null);
        }
        result.add(tthData);
        return JSONObject.toJSONString(result);
    }

    @Override
    public String getRealTimeReservoirLevelData(String date) {
        List<RealTimeWaterLevelDataRes> result = new ArrayList<>();
        List<IrrigatedPlatformDataInfo>  tthInputList = irrigatedPlatformDataInfoService.selectInfoByTime(date,"入库流量");
        IrrigatedPlatformDataInfo tthInput ;
        if(null != tthInputList && tthInputList.size()>0){
            tthInput = tthInputList.get(0);
        }else {
            tthInput = null;
        }
        RealTimeWaterLevelDataRes tthInputData = new RealTimeWaterLevelDataRes();
        if(tthInput != null){
            tthInputData.setFlow(tthInput.getSqMonitorFlow());
            tthInputData.setStationName(tthInput.getMonitorName());
        }else {
            tthInputData.setFlow(null);
            tthInputData.setStationName("入库流量");
        }
        result.add(tthInputData);
        List<IrrigatedPlatformDataInfo>  tthOutputList  = irrigatedPlatformDataInfoService.selectInfoByTime(date,"出库流量");
        IrrigatedPlatformDataInfo tthOutput ;
        if(null != tthOutputList && tthOutputList.size()>0){
            tthOutput = tthOutputList.get(0);
        }else {
            tthOutput = null;
        }
        RealTimeWaterLevelDataRes tthOutputData = new RealTimeWaterLevelDataRes();
        if(tthOutput != null){
            tthOutputData.setFlow(tthOutputData.getFlow());
            tthOutputData.setStationName(tthOutput.getMonitorName());
        }else {
            tthOutputData.setFlow(null);
            tthOutputData.setStationName("出库流量");
        }
        result.add(tthOutputData);
        LzzGaugingStation lzzOutput = lzzGaugingStationService.selectInfoByTime(date.split(":")[0],"楼庄子出库水位站");
        RealTimeWaterLevelDataRes lzzOutputData = new RealTimeWaterLevelDataRes();
        if(lzzOutput != null){
            lzzOutputData.setFlow(lzzOutput.getFlow());
            lzzOutputData.setStationName(lzzOutput.getStationName());
        }else {
            lzzOutputData.setFlow(null);
            lzzOutputData.setStationName("楼庄子出库水位站");
        }
        result.add(lzzOutputData);
        LzzGaugingStation lzzInput = lzzGaugingStationService.selectInfoByTime(date.split(":")[0],"楼庄子入库水位站");
        RealTimeWaterLevelDataRes lzzInputData = new RealTimeWaterLevelDataRes();
        if(lzzInput != null){
            lzzInputData.setFlow(lzzInput.getFlow());
            lzzInputData.setStationName(lzzInput.getStationName());
        }else {
            lzzInputData.setFlow(null);
            lzzInputData.setStationName("楼庄子入库水位站");
        }
        result.add(lzzInputData);
        LzzGaugingStation lzzThreeBridge = lzzGaugingStationService.selectInfoByTime(date.split(":")[0],"3号桥水位站");
        RealTimeWaterLevelDataRes lzzThreeBridgeData = new RealTimeWaterLevelDataRes();
        if(lzzThreeBridge != null){
            lzzThreeBridgeData.setFlow(lzzThreeBridge.getFlow());
            lzzThreeBridgeData.setStationName(lzzThreeBridge.getStationName());
        }else {
            lzzThreeBridgeData.setFlow(null);
            lzzThreeBridgeData.setStationName("三号桥水位站");
        }
        result.add(lzzThreeBridgeData);
        return JSONObject.toJSONString(result);
    }

    //查询雨量历史数据
    @Override
    public String getRainfallStationsHistoricalData(String name, String startTime, String endTime) {
        List<RainfallStationsHistoricalDataRes> result = new ArrayList<>();
        List<LzzRainfallStation> lzzRainfallStations = lzzRainfallStationService.selectHistoryList(name, startTime, endTime);
        if(null != lzzRainfallStations && lzzRainfallStations.size()>0){
            for(LzzRainfallStation station:lzzRainfallStations){
                RainfallStationsHistoricalDataRes res = new RainfallStationsHistoricalDataRes();
                res.setTime(sdf.format(station.getTime()));
                res.setRainfall(station.getRainfall().doubleValue());
                result.add(res);
            }
        }else {
            List<IrrigatedPlatformDataInfo> irrigatedPlatformDataInfos = irrigatedPlatformDataInfoService.selectHistoryList(name, startTime, endTime);
            if(null != irrigatedPlatformDataInfos && irrigatedPlatformDataInfos.size()>0){
                for(IrrigatedPlatformDataInfo info:irrigatedPlatformDataInfos){
                    RainfallStationsHistoricalDataRes res = new RainfallStationsHistoricalDataRes();
                    res.setTime(info.getMonitorTime());
                    res.setRainfall(info.getYqRainFallOne());
                    result.add(res);
                }
            }
        }
        if(result.size()>0){
            return JSONObject.toJSONString(result);
        }
        return null;
    }

    //查询水库水位历史数据
    @Override
    public String getReservoirLevel(String name, String startTime, String endTime) {
        List<ReservoirLevelRes> result = new ArrayList<>();
        List<LzzGaugingStation> lzzGaugingStationList = lzzGaugingStationService.selectHistoryList(name, startTime, endTime);
        if(null != lzzGaugingStationList && lzzGaugingStationList.size()>0){
            for(LzzGaugingStation station:lzzGaugingStationList){
                ReservoirLevelRes res = new ReservoirLevelRes();
                res.setTime(sdf.format(station.getGatherTime()));
                res.setWaterLevel(station.getRelativeWaterLevel());
                result.add(res);
            }
        }else {
            List<IrrigatedPlatformDataInfo> irrigatedPlatformDataInfos = irrigatedPlatformDataInfoService.selectHistoryList(name, startTime, endTime);
            if(null != irrigatedPlatformDataInfos && irrigatedPlatformDataInfos.size()>0){
                for(IrrigatedPlatformDataInfo info:irrigatedPlatformDataInfos){
                    ReservoirLevelRes res = new ReservoirLevelRes();
                    res.setTime(info.getMonitorTime());
                    res.setWaterLevel(info.getSqWaterLevel());
                    result.add(res);
                }
            }
        }
        if(result.size()>0){
            return JSONObject.toJSONString(result);
        }
        return null;
    }

    //查询水位站历史数据
    @Override
    public String getWaterLevelData(String name, String startTime, String endTime) {
        List<WaterLevelDataRes> result = new ArrayList<>();
        List<LzzGaugingStation> lzzGaugingStationList = lzzGaugingStationService.selectHistoryList(name, startTime, endTime);
        if(null != lzzGaugingStationList && lzzGaugingStationList.size()>0){
            for(LzzGaugingStation station:lzzGaugingStationList){
                WaterLevelDataRes res = new WaterLevelDataRes();
                res.setTime(sdf.format(station.getGatherTime()));
                res.setFlow(station.getFlow());
                result.add(res);
            }
        }else {
            List<IrrigatedPlatformDataInfo> irrigatedPlatformDataInfos = irrigatedPlatformDataInfoService.selectHistoryList(name, startTime, endTime);
            if(null != irrigatedPlatformDataInfos && irrigatedPlatformDataInfos.size()>0){
                for(IrrigatedPlatformDataInfo info:irrigatedPlatformDataInfos){
                    WaterLevelDataRes res = new WaterLevelDataRes();
                    res.setTime(info.getMonitorTime());
                    res.setFlow(info.getSqMonitorFlow());
                    result.add(res);
                }
            }
        }
        if(result.size()>0){
            return JSONObject.toJSONString(result);
        }
        return null;
    }

    //根据来水预报id查询洪水调度方案列表
    @Override
    public String getFloodControlOperationListById(String id) {
        List<FloodControlOperation> list = floodControlOperationService.lambdaQuery().eq(FloodControlOperation::getForecastingSchemeId, id).list();
        if(null != list && list.size()>0){
            return JSONObject.toJSONString(list);
        }
        return null;
    }

    //根据洪水调度方案id查询洪水调度方案部分信息
    @Override
    public String getFloodControlOperationFrontViewById(String id) {
        Map<String, Object> result = new HashMap<>();
        RestResponse<Map<String, List<PredictionProcessDto>>> mapRestResponse = floodControlOperationService.selectDetails(id);
        if(mapRestResponse.getCode()==200){
            Map<String, List<PredictionProcessDto>> data = mapRestResponse.getData();
            List<PredictionProcessDto> tth = data.get("头屯河");
            List<PredictionProcessDto> tthMaxWaterLevelCollect = tth.stream().sorted(Comparator.comparing(PredictionProcessDto::getWaterLevel).reversed()).collect(Collectors.toList());
            if(null != tthMaxWaterLevelCollect && tthMaxWaterLevelCollect.size()>0){
                PredictionFrontViewDto dto = new PredictionFrontViewDto();
                PredictionProcessDto dataTemp = tthMaxWaterLevelCollect.get(0);
                dto.setTime(sdf.format(dataTemp.getTime()));
                dto.setValue(dataTemp.getWaterLevel());
                result.put("tthMaxWaterLevel",dto);
            }else {
                result.put("tthMaxWaterLevel",null);
            }

            List<PredictionProcessDto> tthMinWaterLevelCollect = tth.stream().sorted(Comparator.comparing(PredictionProcessDto::getWaterLevel)).collect(Collectors.toList());
            if(null != tthMinWaterLevelCollect && tthMinWaterLevelCollect.size()>0){
                PredictionFrontViewDto dto = new PredictionFrontViewDto();
                PredictionProcessDto dataTemp = tthMinWaterLevelCollect.get(0);
                dto.setTime(sdf.format(dataTemp.getTime()));
                dto.setValue(dataTemp.getWaterLevel());
                result.put("tthMinWaterLevel",dto);
            }else {
                result.put("tthMinWaterLevel",null);
            }

            List<PredictionProcessDto> tthMaxFlowCollect = tth.stream().sorted(Comparator.comparing(PredictionProcessDto::getPreQ).reversed()).collect(Collectors.toList());
            if(null != tthMaxFlowCollect && tthMaxFlowCollect.size()>0){
                PredictionFrontViewDto dto = new PredictionFrontViewDto();
                PredictionProcessDto dataTemp = tthMaxFlowCollect.get(0);
                dto.setTime(sdf.format(dataTemp.getTime()));
                dto.setValue(dataTemp.getPreQ());
                result.put("tthMaxFlow",dto);
            }else {
                result.put("tthMaxFlow",null);
            }

            List<PredictionProcessDto> tthMaxInQCollect = tth.stream().sorted(Comparator.comparing(PredictionProcessDto::getQIn).reversed()).collect(Collectors.toList());
            if(null != tthMaxInQCollect && tthMaxInQCollect.size()>0){
                PredictionFrontViewDto dto = new PredictionFrontViewDto();
                PredictionProcessDto dataTemp = tthMaxInQCollect.get(0);
                dto.setTime(sdf.format(dataTemp.getTime()));
                dto.setValue(dataTemp.getQIn());
                result.put("tthMaxInQ",dto);
            }else {
                result.put("tthMaxInQ",null);
            }

            List<PredictionProcessDto> tthMinInQCollect = tth.stream().sorted(Comparator.comparing(PredictionProcessDto::getQIn)).collect(Collectors.toList());
            if(null != tthMinInQCollect && tthMinInQCollect.size()>0){
                PredictionFrontViewDto dto = new PredictionFrontViewDto();
                PredictionProcessDto dataTemp = tthMinInQCollect.get(0);
                dto.setTime(sdf.format(dataTemp.getTime()));
                dto.setValue(dataTemp.getQIn());
                result.put("tthMinInQ",dto);
            }else {
                result.put("tthMinInQ",null);
            }

            List<PredictionProcessDto> tthMaxOutQCollect = tth.stream().sorted(Comparator.comparing(PredictionProcessDto::getQOut).reversed()).collect(Collectors.toList());
            if(null != tthMaxOutQCollect && tthMaxOutQCollect.size()>0){
                PredictionFrontViewDto dto = new PredictionFrontViewDto();
                PredictionProcessDto dataTemp = tthMaxOutQCollect.get(0);
                dto.setTime(sdf.format(dataTemp.getTime()));
                dto.setValue(dataTemp.getQOut());
                result.put("tthMaxOutQ",dto);
            }else {
                result.put("tthMaxOutQ",null);
            }

            List<PredictionProcessDto> tthMinOutQCollect = tth.stream().sorted(Comparator.comparing(PredictionProcessDto::getQOut)).collect(Collectors.toList());
            if(null != tthMinOutQCollect && tthMinOutQCollect.size()>0){
                PredictionFrontViewDto dto = new PredictionFrontViewDto();
                PredictionProcessDto dataTemp = tthMinOutQCollect.get(0);
                dto.setTime(sdf.format(dataTemp.getTime()));
                dto.setValue(dataTemp.getQOut());
                result.put("tthMinOutQ",dto);
            }else {
                result.put("tthMinOutQ",null);
            }

            Double tthSumRetain = tth.stream().map(PredictionProcessDto::getRetain).reduce(Double::sum).orElse(0.00);
            if(null !=tthSumRetain){
                result.put("tthSumRetain",tthSumRetain);
            }else {
                result.put("tthSumRetain",null);
            }

            List<PredictionProcessDto> lzz = data.get("楼庄子");
            List<PredictionProcessDto> lzzMaxWaterLevelCollect = lzz.stream().sorted(Comparator.comparing(PredictionProcessDto::getWaterLevel).reversed()).collect(Collectors.toList());
            if(null != lzzMaxWaterLevelCollect && lzzMaxWaterLevelCollect.size()>0){
                PredictionFrontViewDto dto = new PredictionFrontViewDto();
                PredictionProcessDto dataTemp = lzzMaxWaterLevelCollect.get(0);
                dto.setTime(sdf.format(dataTemp.getTime()));
                dto.setValue(dataTemp.getWaterLevel());
                result.put("lzzMaxWaterLevel",dto);
            }else {
                result.put("lzzMaxWaterLevel",null);
            }

            List<PredictionProcessDto> lzzMinWaterLevelCollect = lzz.stream().sorted(Comparator.comparing(PredictionProcessDto::getWaterLevel)).collect(Collectors.toList());
            if(null != lzzMinWaterLevelCollect && lzzMinWaterLevelCollect.size()>0){
                PredictionFrontViewDto dto = new PredictionFrontViewDto();
                PredictionProcessDto dataTemp = lzzMinWaterLevelCollect.get(0);
                dto.setTime(sdf.format(dataTemp.getTime()));
                dto.setValue(dataTemp.getWaterLevel());
                result.put("lzzMinWaterLevel",dto);
            }else {
                result.put("lzzMinWaterLevel",null);
            }

            List<PredictionProcessDto> lzzMaxFlowCollect = lzz.stream().sorted(Comparator.comparing(PredictionProcessDto::getPreQ).reversed()).collect(Collectors.toList());
            if(null != lzzMaxFlowCollect && lzzMaxFlowCollect.size()>0){
                PredictionFrontViewDto dto = new PredictionFrontViewDto();
                PredictionProcessDto dataTemp = lzzMaxFlowCollect.get(0);
                dto.setTime(sdf.format(dataTemp.getTime()));
                dto.setValue(dataTemp.getPreQ());
                result.put("lzzMaxFlow",dto);
            }else {
                result.put("lzzMaxFlow",null);
            }

            List<PredictionProcessDto> lzzMaxInQCollect = lzz.stream().sorted(Comparator.comparing(PredictionProcessDto::getQIn).reversed()).collect(Collectors.toList());
            if(null != lzzMaxInQCollect && lzzMaxInQCollect.size()>0){
                PredictionFrontViewDto dto = new PredictionFrontViewDto();
                PredictionProcessDto dataTemp = lzzMaxInQCollect.get(0);
                dto.setTime(sdf.format(dataTemp.getTime()));
                dto.setValue(dataTemp.getQIn());
                result.put("lzzMaxInQ",dto);
            }else {
                result.put("lzzMaxInQ",null);
            }

            List<PredictionProcessDto> lzzMinInQCollect = lzz.stream().sorted(Comparator.comparing(PredictionProcessDto::getQIn)).collect(Collectors.toList());
            if(null != lzzMinInQCollect && lzzMinInQCollect.size()>0){
                PredictionFrontViewDto dto = new PredictionFrontViewDto();
                PredictionProcessDto dataTemp = lzzMinInQCollect.get(0);
                dto.setTime(sdf.format(dataTemp.getTime()));
                dto.setValue(dataTemp.getQIn());
                result.put("lzzMinInQ",dto);
            }else {
                result.put("lzzMinInQ",null);
            }

            List<PredictionProcessDto> lzzMaxOutQCollect = lzz.stream().sorted(Comparator.comparing(PredictionProcessDto::getQOut).reversed()).collect(Collectors.toList());
            if(null != lzzMaxOutQCollect && lzzMaxOutQCollect.size()>0){
                PredictionFrontViewDto dto = new PredictionFrontViewDto();
                PredictionProcessDto dataTemp = lzzMaxOutQCollect.get(0);
                dto.setTime(sdf.format(dataTemp.getTime()));
                dto.setValue(dataTemp.getQOut());
                result.put("lzzMaxOutQ",dto);
            }else {
                result.put("lzzMaxOutQ",null);
            }

            List<PredictionProcessDto> lzzMinOutQCollect = lzz.stream().sorted(Comparator.comparing(PredictionProcessDto::getQOut)).collect(Collectors.toList());
            if(null != lzzMinOutQCollect && lzzMinOutQCollect.size()>0){
                PredictionFrontViewDto dto = new PredictionFrontViewDto();
                PredictionProcessDto dataTemp = lzzMinOutQCollect.get(0);
                dto.setTime(sdf.format(dataTemp.getTime()));
                dto.setValue(dataTemp.getQOut());
                result.put("lzzMinOutQ",dto);
            }else {
                result.put("lzzMinOutQ",null);
            }

            Double lzzSumRetain = lzz.stream().map(PredictionProcessDto::getRetain).reduce(Double::sum).orElse(0.00);
            if(null != lzzSumRetain){
                result.put("lzzSumRetain",lzzSumRetain);
            }else {
                result.put("lzzSumRetain",null);
            }

            return JSONObject.toJSONString(result);
        }
        return null;
    }

    //根据洪水调度方案id查询洪水调度方案详情
    @Override
    public String getFloodControlOperationDetails(String id) {
        RestResponse<Map<String, List<PredictionProcessDto>>> mapRestResponse = floodControlOperationService.selectDetails(id);
        if(mapRestResponse.getCode()==200){
            return JSONObject.toJSONString(mapRestResponse.getData());
        }
        return null;
    }

    //根据洪水调度方案ids，比选方案结果
    @Override
    public String getPlansComparison(String ids) {
        RestResponse<Map<String, Object>> mapRestResponse = floodControlOperationService.containmentCalculator(ids);
        if(mapRestResponse.getCode()==200){
            return JSONObject.toJSONString(mapRestResponse.getData());
        }
        return null;
    }

    @Override
    public String getProgrammeListForFloodControlOperation() {
        List<IncomingWaterForecast> list = incomingWaterForecastService.lambdaQuery().eq(IncomingWaterForecast::getModelType, 3).list();
        if(null != list && list.size()>0){
            return JSONObject.toJSONString(list);
        }
        return null;
    }

    @Override
    public String getPredictionListByTimeType(Integer timeType) {
        List<IncomingWaterForecast> predictionListByTimeType = incomingWaterForecastService.getPredictionListByTimeType(timeType);
        if(null != predictionListByTimeType && predictionListByTimeType.size()>0){
            return JSONObject.toJSONString(predictionListByTimeType);
        }
        return null;
    }

    @Override
    public String getPredictionListByName(String id, String reservoir) {
        Map<String, Object> predictionListByName = incomingWaterForecastService.getPredictionListByName(id, reservoir);
        if(null != predictionListByName && predictionListByName.size()>0){
            return JSONObject.toJSONString(predictionListByName);
        }
        return null;
    }

    @Override
    public String getWaterStorageOverview(String dateTime) {
        return JSONObject.toJSONString(floodHomePageService.waterStorageOverview(DateUtil.parse(dateTime)));
    }

    @Override
    public void refreshWaterStorageOverview() {
        floodHomePageService.waterStorageOverviewSchedule(new Date());
    }
}
