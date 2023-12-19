package com.cj.dataSynchronization.func.modular.tth.service.impl;


import com.cj.common.model.RestResponse;
import com.cj.dataSynchronization.func.modular.tth.IrrigatedAreaInvoke;
import com.cj.dataSynchronization.func.modular.tth.dtos.AllHistoryDataDto;
import com.cj.dataSynchronization.func.modular.tth.dtos.AllTreeDto;
import com.cj.dataSynchronization.func.modular.tth.dtos.QueryRealTimeDataDto;
import com.cj.dataSynchronization.func.modular.tth.service.IrrigatedAreaService;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformData.entity.IrrigatedPlatformData;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformData.service.IrrigatedPlatformDataService;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.entity.IrrigatedPlatformDataInfo;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.service.IrrigatedPlatformDataInfoService;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformTree.entity.IrrigatedPlatformTree;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformTree.service.IrrigatedPlatformTreeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Slf4j
@Service
public class IrrigatedAreaServiceImpl implements IrrigatedAreaService {

    @Autowired
    private IrrigatedPlatformTreeService irrigatedPlatformTreeService;

    @Autowired
    private IrrigatedPlatformDataService irrigatedPlatformDataService;

    @Autowired
    private IrrigatedPlatformDataInfoService irrigatedPlatformDataInfoService;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    @Override
    public RestResponse getAllTree() {
        List<AllTreeDto> allTree = IrrigatedAreaInvoke.getAllTree();
        if(null == allTree && allTree.size()<0){
            return RestResponse.no("平台无数据");
        }
        List<IrrigatedPlatformTree> irrigatedPlatformTreeList = new ArrayList<>();
        for(AllTreeDto dto : allTree){
            IrrigatedPlatformTree tree = new IrrigatedPlatformTree();
            tree.setId(dto.getID());
            tree.setName(dto.getNAME());
            tree.setParentId(dto.getPARENT_ID());
            tree.setBeginTime(dto.getBEGIN_TIME());
            tree.setBeginTimeMark(dto.getBEGIN_TIME_MARK());
            tree.setElevation(dto.getELEVATION());
            tree.setIsWaterLevel(dto.getIS_WATER_LEVEL());
            tree.setLocationType(dto.getLOCATION_TYPE());
            tree.setLocationTypeName(dto.getLOCATION_TYPE_NAME());
            tree.setMeasureType(dto.getMEASURE_TYPE());
            tree.setMonitorType(dto.getMONITOR_TYPE());
            tree.setNodetype(dto.getNODETYPE());
            tree.setSelfCode(dto.getSELF_CODE());
            tree.setWaterlevelNotnormal(dto.getWATERLEVEL_NOTNORMAL());
            irrigatedPlatformTreeList.add(tree);
        }
        boolean b = irrigatedPlatformTreeService.saveOrUpdateBatch(irrigatedPlatformTreeList);
        if(b){
            return RestResponse.ok("ok");
        }else {
            return RestResponse.no("error");
        }
    }

    @Override
    public RestResponse getDataByIdAndTime(Integer flag, String time) {
        try {
            String[] split = time.split(" ");
            List<IrrigatedPlatformTree> list = irrigatedPlatformTreeService.list();
            List<IrrigatedPlatformData> result = new ArrayList<>();
            Integer i = 1;
            if(null == list || list.size()<0){
                return RestResponse.no("平台无数据");
            }
            for(IrrigatedPlatformTree tree: list){
                if(StringUtils.isEmpty(tree.getMonitorType())){
                    continue;
                }
                List<AllHistoryDataDto> allHistoryData = IrrigatedAreaInvoke.getAllHistoryData(tree.getId(), split[0], split[1],tree.getMonitorType());
                if(null == allHistoryData  || allHistoryData.size()<0){
                    continue;
                }
                if(flag==1){
                    AllHistoryDataDto dto = allHistoryData.get(0);
                    IrrigatedPlatformData data = new IrrigatedPlatformData();
                    data.setMonitorName(tree.getName());
                    data.setRegionId(dto.getREGION_ID());
                    data.setVoltage(dto.getVOLTAGE());
                    data.setPTotalFlow(dto.getP_TOTAL_FLOW());
                    data.setUserId(dto.getUSER_ID());
                    data.setDownWater(dto.getDOWN_WATER()==null?"":dto.getDOWN_WATER().toString());
                    data.setIgCo(dto.getIG_CO()==null?"":dto.getIG_CO().toString());
                    data.setMonitorFlow(dto.getMONITOR_FLOW());
                    data.setMonitorId(dto.getMONITOR_ID());
                    data.setOperateTime(dto.getOPERATE_TIME());
                    data.setMonitorTime(dto.getMONITOR_TIME());
                    data.setGateOpenHoles(dto.getGATE_OPEN_HOLES()==null?"":dto.getGATE_OPEN_HOLES().toString());
                    data.setId(dto.getID());
                    data.setRemark(dto.getREMARK());
                    data.setWater(dto.getWATER());
                    data.setCapacity(dto.getCAPACITY());
                    data.setNTotalFlow(dto.getN_TOTAL_FLOW());
                    data.setIsSurpass(dto.getIS_SURPASS());
                    data.setUserName(dto.getUSER_NAME());
                    data.setInputFlow(dto.getINPUT_FLOW());
                    data.setGateHeight(dto.getGATE_HEIGHT());
                    data.setWaterLevel(dto.getWATER_LEVEL());
                    data.setTotalFlow(dto.getTOTAL_FLOW());
                    data.setMonitorFlowRate(dto.getMONITOR_FLOW_RATE());
                    data.setIgSp(dto.getIG_SP()==null?"":dto.getIG_SP().toString());
                    data.setDownLevel(dto.getDOWN_LEVEL()==null?"":dto.getDOWN_LEVEL().toString());
                    data.setGateHeightShow(dto.getGATE_HEIGHT_SHOW());
                    data.setIsNullPipe(dto.getIS_NULL_PIPE()==null?"":dto.getIS_NULL_PIPE().toString());
                    data.setPipePressure(dto.getPIPE_PRESSURE()==null?"":dto.getPIPE_PRESSURE().toString());
                    result.add(data);
                }else {
                    for(AllHistoryDataDto dto:allHistoryData){
                        IrrigatedPlatformData data = new IrrigatedPlatformData();
                        data.setMonitorName(tree.getName());
                        data.setRegionId(dto.getREGION_ID());
                        data.setVoltage(dto.getVOLTAGE());
                        data.setPTotalFlow(dto.getP_TOTAL_FLOW());
                        data.setUserId(dto.getUSER_ID());
                        data.setDownWater(dto.getDOWN_WATER()==null?"":dto.getDOWN_WATER().toString());
                        data.setIgCo(dto.getIG_CO()==null?"":dto.getIG_CO().toString());
                        data.setMonitorFlow(dto.getMONITOR_FLOW());
                        data.setMonitorId(dto.getMONITOR_ID());
                        data.setOperateTime(dto.getOPERATE_TIME());
                        data.setMonitorTime(dto.getMONITOR_TIME());
                        data.setGateOpenHoles(dto.getGATE_OPEN_HOLES()==null?"":dto.getGATE_OPEN_HOLES().toString());
                        data.setId(dto.getID());
                        data.setRemark(dto.getREMARK());
                        data.setWater(dto.getWATER());
                        data.setCapacity(dto.getCAPACITY());
                        data.setNTotalFlow(dto.getN_TOTAL_FLOW());
                        data.setIsSurpass(dto.getIS_SURPASS());
                        data.setUserName(dto.getUSER_NAME());
                        data.setInputFlow(dto.getINPUT_FLOW());
                        data.setGateHeight(dto.getGATE_HEIGHT());
                        data.setWaterLevel(dto.getWATER_LEVEL());
                        data.setTotalFlow(dto.getTOTAL_FLOW());
                        data.setMonitorFlowRate(dto.getMONITOR_FLOW_RATE());
                        data.setIgSp(dto.getIG_SP()==null?"":dto.getIG_SP().toString());
                        data.setDownLevel(dto.getDOWN_LEVEL()==null?"":dto.getDOWN_LEVEL().toString());
                        data.setGateHeightShow(dto.getGATE_HEIGHT_SHOW());
                        data.setIsNullPipe(dto.getIS_NULL_PIPE()==null?"":dto.getIS_NULL_PIPE().toString());
                        data.setPipePressure(dto.getPIPE_PRESSURE()==null?"":dto.getPIPE_PRESSURE().toString());
                        result.add(data);
                    }
                }
                i++;
            }
            log.info("count:"+i);
            boolean b = irrigatedPlatformDataService.saveOrUpdateBatch(result);
            if(b){
                return RestResponse.ok("ok");
            }else {
                return RestResponse.no("error");
            }
        }catch (Exception e) {
            e.printStackTrace();
            return RestResponse.no("error");
        }
    }

    @Override
    public RestResponse getDataById(){
        try {
            List<IrrigatedPlatformTree> list = irrigatedPlatformTreeService.list();
            List<IrrigatedPlatformDataInfo> result = new ArrayList<>();
            if(null == list || list.size()<0){
                return RestResponse.no("平台无数据");
            }
            for (IrrigatedPlatformTree node : list){
                if(StringUtils.isEmpty(node.getMonitorType())){
                    continue;
                }
                List<QueryRealTimeDataDto> queryRealTimeData = IrrigatedAreaInvoke.getQueryRealTimeData(node.getId());
                if(null != queryRealTimeData && queryRealTimeData.size()>0){
                    IrrigatedPlatformDataInfo info = new IrrigatedPlatformDataInfo();
                    QueryRealTimeDataDto dto = queryRealTimeData.get(0);
                    info.setId(dto.getMONITOR_NAME()+"-"+sdf.parse(dto.getMONITOR_TIME()).getTime());
                    info.setMonitorId(dto.getID());
                    info.setWaterDaily(dto.getWATER_DAILY());
                    info.setYesterdayAvgFlow(dto.getYESTERDAY_AVG_FLOW());
                    info.setMonitorName(dto.getMONITOR_NAME());
                    info.setSqTotalFlow(dto.getSQ_TOTAL_FLOW());
                    info.setSqMonitorFlow(dto.getSQ_MONITOR_FLOW());
                    info.setAvgWaterLevel(dto.getAVG_WATER_LEVEL());
                    info.setAvgWaterDeep(dto.getAVG_WATER_DEEP());
                    info.setSqMonitorFlowRate(dto.getSQ_MONITOR_FLOW_RATE());
                    info.setBeginTime(dto.getBEGIN_TIME());
                    info.setYesterdayWaterDaily(dto.getYESTERDAY_WATER_DAILY());
                    info.setMonitorTime(dto.getMONITOR_TIME());
                    info.setSqWaterLevel(dto.getSQ_WATER_LEVEL());
                    info.setVoltage(dto.getVOLTAGE());
                    info.setAvgFlow(dto.getAVG_FLOW());
                    info.setYearWaterDaily(dto.getYEAR_WATER_DAILY());
                    info.setQxRainFall(StringUtils.isEmpty(dto.getQX_RAIN_FALL())?null:Double.valueOf(dto.getQX_RAIN_FALL()));
                    info.setYqRainFallOne(StringUtils.isEmpty(dto.getYQ_RAIN_FALL_1())?null:Double.valueOf(dto.getYQ_RAIN_FALL_1()));
                    info.setYqRainFallThree(StringUtils.isEmpty(dto.getYQ_RAIN_FALL_3())?null:Double.valueOf(dto.getYQ_RAIN_FALL_3()));
                    info.setYqRainFallSix(StringUtils.isEmpty(dto.getYQ_RAIN_FALL_6())?null:Double.valueOf(dto.getYQ_RAIN_FALL_6()));
                    info.setYqRainFallTwelve(StringUtils.isEmpty(dto.getYQ_RAIN_FALL_12())?null:Double.valueOf(dto.getYQ_RAIN_FALL_12()));
                    info.setYqRainFallTwentyFour(StringUtils.isEmpty(dto.getYQ_RAIN_FALL_24())?null:Double.valueOf(dto.getYQ_RAIN_FALL_24()));
                    info.setGdIsNullPipe(dto.getGD_IS_NULL_PIPE());
                    info.setGdPipePressure(StringUtils.isEmpty(dto.getGD_PIPE_PRESSURE())?null:Double.valueOf(dto.getGD_PIPE_PRESSURE()));
                    info.setGdMonitorFlow(dto.getGD_MONITOR_FLOW());
                    info.setGdMonitorFlowRate(dto.getGD_MONITOR_FLOW_RATE());
                    info.setGdTotalFlow(dto.getGD_TOTAL_FLOW());
                    result.add(info);
                }
            }
            boolean b = irrigatedPlatformDataInfoService.saveOrUpdateBatch(result);
            if(b){
                return RestResponse.ok("ok");
            }else {
                return RestResponse.no("error");
            }
        }catch (Exception e) {
            e.printStackTrace();
            return RestResponse.no("error");
        }
    }
}
