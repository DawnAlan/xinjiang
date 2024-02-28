package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.hd.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.common.model.RestResponse;
import com.cj.common.util.RedisUtil;
import com.cj.common.util.UUIDUtils;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.bean.res.SelectInfoByIrrigationNameListRes;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.service.IrrigatedPlatformDataInfoService;
import com.cj.waterresources.func.modular.overallSituationUnitMgr.entity.OverallSituationUnitMgr;
import com.cj.waterresources.func.modular.overallSituationUnitMgr.service.OverallSituationUnitMgrService;
import com.cj.waterresources.func.modular.trendsTable.entity.TrendsTableParam;
import com.cj.waterresources.func.modular.trendsTable.service.TrendsTableParamService;
import com.cj.waterresources.func.modular.waterPrice.totalIdToStation.entity.TotalIdToStation;
import com.cj.waterresources.func.modular.waterPrice.totalIdToStation.service.TotalIdToStationService;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.dkl.entity.DayWaterSituationStatisticsTableDkl;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.hd.mapper.DayWaterSituationStatisticsTableHdMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.hd.entity.DayWaterSituationStatisticsTableHd;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.hd.service.DayWaterSituationStatisticsTableHdService;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.lzz.entity.DayWaterSituationStatisticsTableLzz;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 河东管理站日水情统计表(DayWaterSituationStatisticsTableHd)表服务实现类
 *
 * @author makejava
 * @since 2023-12-23 15:58:48
 */
@Service("dayWaterSituationStatisticsTableHdService")
public class DayWaterSituationStatisticsTableHdServiceImpl extends ServiceImpl<DayWaterSituationStatisticsTableHdMapper, DayWaterSituationStatisticsTableHd> implements DayWaterSituationStatisticsTableHdService {

    @Autowired
    private TotalIdToStationService totalIdToStationService;

    @Autowired
    private TrendsTableParamService trendsTableParamService;

    @Autowired
    private RedisUtil redisUtil;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public RestResponse<Map<String, List<DayWaterSituationStatisticsTableHd>>> selectList(String date) {
        try {
            List<DayWaterSituationStatisticsTableHd> list = this.baseMapper.selectList(date);
            if(null != list && list.size()>0) {
                Map<String, List<DayWaterSituationStatisticsTableHd>> collect = list.stream().collect(Collectors.groupingBy(DayWaterSituationStatisticsTableHd::getTime));
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
    public RestResponse add(List<DayWaterSituationStatisticsTableHd> dayWaterSituationStatisticsTableHdList) {
        dayWaterSituationStatisticsTableHdList.forEach(t->{
            t.setId(UUIDUtils.getUUID());
            String tableParamString = (String)redisUtil.get("trendsTableParam:object:"+t.getTableHeadId());
            TrendsTableParam tableParam = JSONObject.parseObject(tableParamString, TrendsTableParam.class);
            Double flow = (Double) redisUtil.get("irrigatedPlatform:sq:date:id"+sdf.format(t.getRecordTime())+" "+t.getTime()+"|"+tableParam.getUnitId());
            t.setV(flow==null?null:flow);
        });
        List<DayWaterSituationStatisticsTableHd> result = new ArrayList<>();
        List<DayWaterSituationStatisticsTableHd> list = this.baseMapper.selectList(sdf.format(dayWaterSituationStatisticsTableHdList.get(0).getRecordTime()));
        List<DayWaterSituationStatisticsTableHd> tempList = list.stream().filter(t -> t.getTime().equals("昨日均")).collect(Collectors.toList());
        if(null != tempList && tempList.size()==0){
            List<DayWaterSituationStatisticsTableHd> dayWaterSituationStatisticsTableHds = this.baseMapper.selectYesterdayList();
            DayWaterSituationStatisticsTableHd hd = dayWaterSituationStatisticsTableHdList.get(0);
            String[] split = hd.getEndTableList().split(",");
            for(String s:split){
                DayWaterSituationStatisticsTableHd yesterdayBean = new DayWaterSituationStatisticsTableHd();
                yesterdayBean.setTime("昨日均");
                yesterdayBean.setId(UUIDUtils.getUUID());
                yesterdayBean.setTableHeadId(s);
                yesterdayBean.setRecordTime(new Date());
                yesterdayBean.setV(dayWaterSituationStatisticsTableHds.stream().filter(t->t.getTableHeadId().equals(s) && t.getV()!=null).map(DayWaterSituationStatisticsTableHd::getV).reduce(Double::sum).orElse(0.00));
                yesterdayBean.setEndTableList(hd.getEndTableList());
                yesterdayBean.setFrontTableList(hd.getFrontTableList());
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
        List<TotalIdToStation> totalIdToStationList = totalIdToStationService.lambdaQuery().eq(TotalIdToStation::getUseType, 1).eq(TotalIdToStation::getStation, "河东管理站").list();
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
                        for(DayWaterSituationStatisticsTableHd t:dayWaterSituationStatisticsTableHdList){
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
                    for(DayWaterSituationStatisticsTableHd t:dayWaterSituationStatisticsTableHdList){
                        if(t.getTableHeadId().equals(id)){
                            t.setV(value);
                        }
                    }
                }
            }
            for(DayWaterSituationStatisticsTableHd t:dayWaterSituationStatisticsTableHdList){
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
                for(DayWaterSituationStatisticsTableHd t:dayWaterSituationStatisticsTableHdList){
                    if(t.getTableHeadId().equals(one.getId())){
                        t.setV(total);
                    }
                }
            }
        }
        result.addAll(dayWaterSituationStatisticsTableHdList);
        boolean b = this.saveBatch(result);
        if (b) {
            Set<String> allKeys = redisUtil.getAllKeys("A3:data:hd:yesterday:");
            if(allKeys.isEmpty()){
                List<DayWaterSituationStatisticsTableHd> yesterdayData = result.stream().filter(t -> t.getTime().equals("昨日均")).collect(Collectors.toList());
                yesterdayData.forEach(t->{
                    redisUtil.set("A3:data:hd:yesterday:"+t.getTableHeadId(),t.getV());
                });
            }
            return RestResponse.ok();
        }else {
            return RestResponse.no("error");
        }
    }

    @Override
    public RestResponse delete(String ids) {
        List<String> collect = Arrays.stream(ids.split(",")).collect(Collectors.toList());
        boolean remove = this.lambdaUpdate().in(DayWaterSituationStatisticsTableHd::getId, collect).remove();
        if (remove) {
            return RestResponse.ok();
        }else {
            return RestResponse.no("error");
        }
    }

    @Override
    public RestResponse update(List<DayWaterSituationStatisticsTableHd> dayWaterSituationStatisticsTableHdList) {
        List<TotalIdToStation> totalIdToStationList = totalIdToStationService.lambdaQuery().eq(TotalIdToStation::getUseType, 1).eq(TotalIdToStation::getStation, "河东管理站").list();
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
                        for(DayWaterSituationStatisticsTableHd t:dayWaterSituationStatisticsTableHdList){
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
                    for(DayWaterSituationStatisticsTableHd t:dayWaterSituationStatisticsTableHdList){
                        if(t.getTableHeadId().equals(id)){
                            t.setV(value);
                        }
                    }

                }
            }
            for(DayWaterSituationStatisticsTableHd t:dayWaterSituationStatisticsTableHdList){
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
                for(DayWaterSituationStatisticsTableHd t:dayWaterSituationStatisticsTableHdList){
                    if(t.getTableHeadId().equals(one.getId())){
                        t.setV(total);
                    }
                }
            }
        }
        boolean b = this.updateBatchById(dayWaterSituationStatisticsTableHdList);
        if (b) {
            if(dayWaterSituationStatisticsTableHdList.get(0).getTime().equals("昨日均")){
                dayWaterSituationStatisticsTableHdList.forEach(t->{
                    redisUtil.set("A3:data:hd:yesterday:"+t.getTableHeadId(),t.getV());
                });
            }
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

