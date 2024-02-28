package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.tth.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.common.model.RestResponse;
import com.cj.common.util.RedisUtil;
import com.cj.common.util.UUIDUtils;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.entity.IrrigatedPlatformDataInfo;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.service.IrrigatedPlatformDataInfoService;
import com.cj.middleDatabase.func.modular.lzz.lzzGaugingStation.entity.LzzGaugingStation;
import com.cj.waterresources.func.modular.trendsTable.entity.TrendsTableParam;
import com.cj.waterresources.func.modular.trendsTable.service.TrendsTableParamService;
import com.cj.waterresources.func.modular.waterPrice.totalIdToStation.entity.TotalIdToStation;
import com.cj.waterresources.func.modular.waterPrice.totalIdToStation.service.TotalIdToStationService;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.hd.entity.DayWaterSituationStatisticsTableHd;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.lzz.entity.DayWaterSituationStatisticsTableLzz;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.tth.mapper.DayWaterSituationStatisticsTableTthMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.tth.entity.DayWaterSituationStatisticsTableTth;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.tth.service.DayWaterSituationStatisticsTableTthService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 头屯河水库日水情统计表(DayWaterSituationStatisticsTableTth)表服务实现类
 *
 * @author makejava
 * @since 2023-12-23 16:01:12
 */
@Service("dayWaterSituationStatisticsTableTthService")
public class DayWaterSituationStatisticsTableTthServiceImpl extends ServiceImpl<DayWaterSituationStatisticsTableTthMapper, DayWaterSituationStatisticsTableTth> implements DayWaterSituationStatisticsTableTthService {

    @Autowired
    private TotalIdToStationService totalIdToStationService;

    @Autowired
    private TrendsTableParamService trendsTableParamService;

    @Autowired
    private IrrigatedPlatformDataInfoService irrigatedPlatformDataInfoService;

    @Autowired
    private RedisUtil redisUtil;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public RestResponse<Map<String, List<DayWaterSituationStatisticsTableTth>>> selectList(String date) {
        try {
            List<DayWaterSituationStatisticsTableTth> list = this.baseMapper.selectList(date);
            if(null != list && list.size()>0) {
                Map<String, List<DayWaterSituationStatisticsTableTth>> collect = list.stream().collect(Collectors.groupingBy(DayWaterSituationStatisticsTableTth::getTime));
                return RestResponse.ok(collect);
            }else {
                return RestResponse.no("fail");
            }
        }catch (Exception e){
            e.printStackTrace();
            return RestResponse.no("error");
        }
    }

    @Override
    public RestResponse add(List<DayWaterSituationStatisticsTableTth> dayWaterSituationStatisticsTableTthList) {
        for(DayWaterSituationStatisticsTableTth t:dayWaterSituationStatisticsTableTthList){
            t.setId(UUIDUtils.getUUID());
            String paramName = (String)redisUtil.get("trendsTableParam:name:"+t.getTableHeadId());
            //String paramName = trendsTableParamService.getById(t.getTableHeadId()).getParamName();
            if(paramName.equals("水位")){
                IrrigatedPlatformDataInfo waterLevel = irrigatedPlatformDataInfoService.selectOneByCondition1(sdf.format(t.getRecordTime()) + " " + t.getTime(),"头屯河水库水位");
                t.setV(waterLevel==null?null:waterLevel.getAvgWaterLevel()==null?null:waterLevel.getAvgWaterLevel());
            }
            if(paramName.equals("库容")){
                IrrigatedPlatformDataInfo waterLevel = irrigatedPlatformDataInfoService.selectOneByCondition1(sdf.format(t.getRecordTime()) + " " + t.getTime(),"头屯河水库水位");
                t.setV(waterLevel==null?null:waterLevel.getSqCapacity()==null?null:waterLevel.getSqCapacity());
            }
            /*if(paramName.equals("进库流量")){
                IrrigatedPlatformDataInfo waterLevel = irrigatedPlatformDataInfoService.selectOneByCondition1(sdf.format(t.getRecordTime()) + " " + t.getTime(),"头屯河水库水位");
                t.setV(waterLevel==null?null:waterLevel.getStorageCapacity()==null?null:waterLevel.getStorageCapacity());
            }*/
            if(paramName.equals("暗管")){
                IrrigatedPlatformDataInfo waterLevel = irrigatedPlatformDataInfoService.selectOneByCondition1(sdf.format(t.getRecordTime()) + " " + t.getTime(),"八钢工业用水取水口(渠道)");
                t.setV(waterLevel==null?null:waterLevel.getSqMonitorFlow()==null?null:waterLevel.getSqMonitorFlow());
            }
        }
        dayWaterSituationStatisticsTableTthList.forEach(t->t.setId(UUIDUtils.getUUID()));
        List<DayWaterSituationStatisticsTableTth> result = new ArrayList<>();
        List<DayWaterSituationStatisticsTableTth> list = this.baseMapper.selectList(sdf.format(dayWaterSituationStatisticsTableTthList.get(0).getRecordTime()));
        List<DayWaterSituationStatisticsTableTth> tempList = list.stream().filter(t -> t.getTime().equals("昨日均")).collect(Collectors.toList());
        if(null != tempList && tempList.size()==0){
            List<DayWaterSituationStatisticsTableTth> dayWaterSituationStatisticsTableTths = this.baseMapper.selectYesterdayList();
            DayWaterSituationStatisticsTableTth tth = dayWaterSituationStatisticsTableTthList.get(0);
            String endTableList = tth.getEndTableList();
            String[] split = endTableList.split(",");
            for(String s:split){
                DayWaterSituationStatisticsTableTth yesterdayBean = new DayWaterSituationStatisticsTableTth();
                yesterdayBean.setTime("昨日均");
                yesterdayBean.setId(UUIDUtils.getUUID());
                yesterdayBean.setTableHeadId(s);
                yesterdayBean.setRecordTime(new Date());
                yesterdayBean.setV(dayWaterSituationStatisticsTableTths.stream().filter(t->t.getTableHeadId().equals(s) && t.getV()!=null).map(DayWaterSituationStatisticsTableTth::getV).reduce(Double::sum).orElse(0.00));
                yesterdayBean.setEndTableList(tth.getEndTableList());
                yesterdayBean.setFrontTableList(tth.getFrontTableList());
                result.add(yesterdayBean);
            }
        }
        String mk = (String) redisUtil.get("trendsTableParam:list");
        if(StringUtils.isEmpty(mk)){
            updateCache();
            mk = (String) redisUtil.get("trendsTableParam:list");
        }
        List<TrendsTableParam> trendsTableParamListTemp = JSONObject.parseArray(mk, TrendsTableParam.class);
        List<TrendsTableParam> trendsTableParamList = trendsTableParamListTemp.stream().filter(t -> t.getUseType() == 1).collect(Collectors.toList());
        List<TotalIdToStation> totalIdToStationList = totalIdToStationService.lambdaQuery().eq(TotalIdToStation::getUseType, 1).eq(TotalIdToStation::getStation, "头屯河水库").list();
        //计算行合计
        if(null != totalIdToStationList && totalIdToStationList.size()>0){
            Double total = 0.0;
            List<String> totalCollect = totalIdToStationList.stream().filter(t->t.getName().equals("合计")).map(TotalIdToStation::getTotalId).collect(Collectors.toList());
            for(String id:totalCollect){
                Double value = 0.0;
                String tableParamString = (String)redisUtil.get("trendsTableParam:object:"+id);
                TrendsTableParam tableParam = JSONObject.parseObject(tableParamString, TrendsTableParam.class);
                //TrendsTableParam tableParam = trendsTableParamService.getById(id);
                if(!tableParam.getPId().equals("0")){
                    List<TrendsTableParam> noTotalList = trendsTableParamList.stream().filter(t->t.getPId().equals(tableParam.getPId()) && !t.getParamName().equals("合计")).collect(Collectors.toList());
                    //List<TrendsTableParam> noTotalList = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, tableParam.getPId()).ne(TrendsTableParam::getParamName, "合计").list();
                    for(TrendsTableParam param:noTotalList){
                        for(DayWaterSituationStatisticsTableTth t:dayWaterSituationStatisticsTableTthList){
                            if(t.getTableHeadId().equals(param.getId())){
                                value+=t.getV()==null?0.0:t.getV();
                            }
                            List<TrendsTableParam> listed = trendsTableParamList.stream().filter(p->p.getPId().equals(param.getId()) && !p.getParamName().equals("合计")).collect(Collectors.toList());
                            //List<TrendsTableParam> listed = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, param.getId()).ne(TrendsTableParam::getParamName, "合计").list();
                            if(null != listed && listed.size()>0){
                                for (TrendsTableParam param1:listed){
                                    if(t.getTableHeadId().equals(param1.getId())){
                                        value+=t.getV()==null?0.0:t.getV();
                                    }
                                }
                            }
                        }
                    }
                    for(DayWaterSituationStatisticsTableTth t:dayWaterSituationStatisticsTableTthList){
                        if(t.getTableHeadId().equals(id)){
                            t.setV(value);
                        }
                    }
                }
            }
            for(DayWaterSituationStatisticsTableTth t:dayWaterSituationStatisticsTableTthList){
                if(!totalIdToStationList.stream().map(TotalIdToStation::getTotalId).collect(Collectors.toList()).contains(t.getTableHeadId())){
                    total+=t.getV()==null?0.0:t.getV();
                }
            }
            TrendsTableParam one = null;
            List<TrendsTableParam> TrendsTableParamTemp = trendsTableParamList.stream().filter(t -> t.getPId().equals("0") && t.getUseType() == 1).collect(Collectors.toList());
            for(TrendsTableParam param : TrendsTableParamTemp){
                for(String s:totalCollect){
                    if(param.getId().equals(s)){
                        one = param;
                    }
                }
            }
            //TrendsTableParam one = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, "0").eq(TrendsTableParam::getUseType,1).in(TrendsTableParam::getId, totalCollect).one();
            if(null != one){
                for(DayWaterSituationStatisticsTableTth t:dayWaterSituationStatisticsTableTthList){
                    if(t.getTableHeadId().equals(one.getId())){
                        t.setV(total);
                    }
                }
            }
        }
        result.addAll(dayWaterSituationStatisticsTableTthList);
        boolean b = this.saveBatch(result);
        if (b) {
            return RestResponse.ok();
        }else {
            return RestResponse.no("error");
        }
    }

    @Override
    public RestResponse delete(String ids) {
        List<String> collect = Arrays.stream(ids.split(",")).collect(Collectors.toList());
        boolean remove = this.lambdaUpdate().in(DayWaterSituationStatisticsTableTth::getId, collect).remove();
        if (remove) {
            return RestResponse.ok();
        }else {
            return RestResponse.no("error");
        }
    }

    @Override
    public RestResponse update(List<DayWaterSituationStatisticsTableTth> dayWaterSituationStatisticsTableTthList) {
        List<TotalIdToStation> totalIdToStationList = totalIdToStationService.lambdaQuery().eq(TotalIdToStation::getUseType, 1).eq(TotalIdToStation::getStation, "头屯河水库").list();
        String mk = (String) redisUtil.get("trendsTableParam:list");
        if(StringUtils.isEmpty(mk)){
            updateCache();
            mk = (String) redisUtil.get("trendsTableParam:list");
        }
        List<TrendsTableParam> trendsTableParamListTemp = JSONObject.parseArray(mk, TrendsTableParam.class);
        List<TrendsTableParam> trendsTableParamList = trendsTableParamListTemp.stream().filter(t -> t.getUseType() == 1).collect(Collectors.toList());
        //计算行合计
        if(null != totalIdToStationList && totalIdToStationList.size()>0){
            Double total = 0.0;
            List<String> totalCollect = totalIdToStationList.stream().filter(t->t.getName().equals("合计")).map(TotalIdToStation::getTotalId).collect(Collectors.toList());
            for(String id:totalCollect){
                Double value = 0.0;
                String tableParamString = (String)redisUtil.get("trendsTableParam:object:"+id);
                TrendsTableParam tableParam = JSONObject.parseObject(tableParamString, TrendsTableParam.class);
                //TrendsTableParam tableParam = trendsTableParamService.getById(id);
                if(!tableParam.getPId().equals("0")){
                    List<TrendsTableParam> noTotalList = trendsTableParamList.stream().filter(t->t.getPId().equals(tableParam.getPId()) && !t.getParamName().equals("合计")).collect(Collectors.toList());
                    //List<TrendsTableParam> noTotalList = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, tableParam.getPId()).ne(TrendsTableParam::getParamName, "合计").list();
                    for(TrendsTableParam param:noTotalList){
                        for(DayWaterSituationStatisticsTableTth t:dayWaterSituationStatisticsTableTthList){
                            if(t.getTableHeadId().equals(param.getId())){
                                value+=t.getV()==null?0.0:t.getV();
                            }
                            List<TrendsTableParam> listed = trendsTableParamList.stream().filter(p->p.getPId().equals(param.getId()) && !p.getParamName().equals("合计")).collect(Collectors.toList());
                            //List<TrendsTableParam> listed = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, param.getId()).ne(TrendsTableParam::getParamName, "合计").list();
                            if(null != listed && listed.size()>0){
                                for (TrendsTableParam param1:listed){
                                    if(t.getTableHeadId().equals(param1.getId())){
                                        value+=t.getV()==null?0.0:t.getV();
                                    }
                                }
                            }
                        }
                    }
                    for(DayWaterSituationStatisticsTableTth t:dayWaterSituationStatisticsTableTthList){
                        if(t.getTableHeadId().equals(id)){
                            t.setV(value);
                        }
                    }

                }
            }
            for(DayWaterSituationStatisticsTableTth t:dayWaterSituationStatisticsTableTthList){
                if(!totalIdToStationList.stream().map(TotalIdToStation::getTotalId).collect(Collectors.toList()).contains(t.getTableHeadId())){
                    total+=t.getV()==null?0.0:t.getV();
                }
            }
            TrendsTableParam one = null;
            List<TrendsTableParam> TrendsTableParamTemp = trendsTableParamList.stream().filter(t -> t.getPId().equals("0") && t.getUseType() == 1).collect(Collectors.toList());
            for(TrendsTableParam param : TrendsTableParamTemp){
                for(String s:totalCollect){
                    if(param.getId().equals(s)){
                        one = param;
                    }
                }
            }
            //TrendsTableParam one = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, "0").eq(TrendsTableParam::getUseType,1).in(TrendsTableParam::getId, totalCollect).one();
            if(null != one){
                for(DayWaterSituationStatisticsTableTth t:dayWaterSituationStatisticsTableTthList){
                    if(t.getTableHeadId().equals(one.getId())){
                        t.setV(total);
                    }
                }
            }
        }
        boolean b = this.updateBatchById(dayWaterSituationStatisticsTableTthList);
        if (b) {
            return RestResponse.ok();
        }else {
            return RestResponse.no("error");
        }
    }

    public void updateCache(){
        List<TrendsTableParam> listed = trendsTableParamService.list();
        redisUtil.set("trendsTableParam:list", JSONObject.toJSONString(listed));
        for (TrendsTableParam param:listed){
            redisUtil.set("trendsTableParam:name:"+param.getId(), param.getParamName());
            redisUtil.set("trendsTableParam:object:"+param.getId(), JSONObject.toJSONString(param));
        }
    }
}

