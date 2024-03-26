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
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.dkl.entity.DayWaterSituationStatisticsTableDkl;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.hd.entity.DayWaterSituationStatisticsTableHd;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.lzz.bean.res.LzzReportFormsRes;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.lzz.entity.DayWaterSituationStatisticsTableLzz;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.qs.entity.DayWaterSituationStatisticsTableQs;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.tth.bean.res.TthReportFormsRes;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.tth.mapper.DayWaterSituationStatisticsTableTthMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.tth.entity.DayWaterSituationStatisticsTableTth;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.tth.service.DayWaterSituationStatisticsTableTthService;
import lombok.SneakyThrows;
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
        List<DayWaterSituationStatisticsTableTth> todayData = this.baseMapper.selectListHave(dayWaterSituationStatisticsTableTthList.get(0).getTime(),sdf.format(dayWaterSituationStatisticsTableTthList.get(0).getRecordTime()));
        if(!todayData.isEmpty()){
            return RestResponse.no(dayWaterSituationStatisticsTableTthList.get(0).getTime()+"数据已创建");
        }
        for(DayWaterSituationStatisticsTableTth t:dayWaterSituationStatisticsTableTthList){
            t.setId(UUIDUtils.getUUID());
            String paramName = (String)redisUtil.get("trendsTableParam:name:"+t.getTableHeadId());
            if(paramName.equals("库水位")){
                Double waterLevel = (Double) redisUtil.get("irrigatedPlatform:sq:tth:waterLevel:"+sdf.format(t.getRecordTime())+" "+t.getTime());
                t.setV(waterLevel==null?null:waterLevel);
            }
            if(paramName.equals("水库库容")){
                Double capacity = (Double)redisUtil.get("irrigatedPlatform:sq:tth:capacity:"+sdf.format(t.getRecordTime())+" "+t.getTime());
                t.setV(capacity==null?null:capacity);
            }
            if(paramName.equals("进库流量")){
                Double capacity = (Double)redisUtil.get("irrigatedPlatform:sq:tth:input:"+sdf.format(t.getRecordTime())+" "+t.getTime());
                t.setV(capacity==null?null:capacity);
            }
            if(paramName.equals("河道流量")){
                Double capacity = (Double)redisUtil.get("irrigatedPlatform:sq:tth:out:"+sdf.format(t.getRecordTime())+" "+t.getTime());
                t.setV(capacity==null?null:capacity);
            }
            if(paramName.equals("暗渠流量")){
                Double capacity = (Double)redisUtil.get("irrigatedPlatform:sq:tth:aq:"+sdf.format(t.getRecordTime())+" "+t.getTime());
                t.setV(capacity==null?null:capacity);
            }
        }
        dayWaterSituationStatisticsTableTthList.forEach(t->t.setId(UUIDUtils.getUUID()));
        List<DayWaterSituationStatisticsTableTth> result = new ArrayList<>();
        List<DayWaterSituationStatisticsTableTth> list = this.baseMapper.selectList(sdf.format(dayWaterSituationStatisticsTableTthList.get(0).getRecordTime()));
        List<DayWaterSituationStatisticsTableTth> tempList = list.stream().filter(t -> t.getTime().equals("昨日均")).collect(Collectors.toList());
        if(null != tempList && tempList.size()==0){
            List<DayWaterSituationStatisticsTableTth> dayWaterSituationStatisticsTableTths = this.baseMapper.selectInfoList(getDate(dayWaterSituationStatisticsTableTthList.get(0).getRecordTime(),-1));
            DayWaterSituationStatisticsTableTth tth = dayWaterSituationStatisticsTableTthList.get(0);
            String endTableList = tth.getEndTableList();
            String[] split = endTableList.split(",");
            for(String s:split){
                DayWaterSituationStatisticsTableTth yesterdayBean = new DayWaterSituationStatisticsTableTth();
                yesterdayBean.setTime("昨日均");
                yesterdayBean.setId(UUIDUtils.getUUID());
                yesterdayBean.setTableHeadId(s);
                yesterdayBean.setRecordTime(dayWaterSituationStatisticsTableTthList.get(0).getRecordTime());
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
                if(!tableParam.getPId().equals("0")){
                    List<TrendsTableParam> noTotalList = trendsTableParamList.stream().filter(t->t.getPId().equals(tableParam.getPId()) && !t.getParamName().equals("合计")).collect(Collectors.toList());
                    for(TrendsTableParam param:noTotalList){
                        for(DayWaterSituationStatisticsTableTth t:dayWaterSituationStatisticsTableTthList){
                            if(t.getTableHeadId().equals(param.getId())){
                                value+=t.getV()==null?0.0:t.getV();
                            }
                            List<TrendsTableParam> listed = trendsTableParamList.stream().filter(p->p.getPId().equals(param.getId()) && !p.getParamName().equals("合计")).collect(Collectors.toList());
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
            if(dayWaterSituationStatisticsTableTthList.get(0).getTime().equals("今日均")){
                updateYesterdayData(dayWaterSituationStatisticsTableTthList.get(0).getRecordTime(),dayWaterSituationStatisticsTableTthList);
            }
            return RestResponse.ok();
        }else {
            return RestResponse.no("error");
        }
    }

    @Override
    public RestResponse insertTodayMeanValue() {
        List<DayWaterSituationStatisticsTableTth> todayData = this.lambdaQuery().eq(DayWaterSituationStatisticsTableTth::getTime, "今日均").list();
        if(!todayData.isEmpty()){
            return RestResponse.no("今日均数据已创建");
        }
        List<DayWaterSituationStatisticsTableTth> dayWaterSituationStatisticsTableTthList = new ArrayList<>();
        List<DayWaterSituationStatisticsTableTth> dayWaterSituationStatisticsTableTths = this.baseMapper.selectList(sdf.format(new Date()));
        if(null!=dayWaterSituationStatisticsTableTths && dayWaterSituationStatisticsTableTths.size()>0){
            DayWaterSituationStatisticsTableTth dayWaterSituationStatisticsTableTth = dayWaterSituationStatisticsTableTths.get(0);
            String endTableList = dayWaterSituationStatisticsTableTth.getEndTableList();
            String[] split = endTableList.split(",");
            for(String t :split){
                DayWaterSituationStatisticsTableTth tth = new DayWaterSituationStatisticsTableTth();
                tth.setId(UUIDUtils.getUUID());
                String tableParamString = (String)redisUtil.get("trendsTableParam:object:"+t);
                TrendsTableParam tableParam = JSONObject.parseObject(tableParamString, TrendsTableParam.class);
                Double flow = (Double) redisUtil.get("irrigatedPlatform:today:"+tableParam.getUnitId());
                tth.setV(flow==null?null:flow);
                tth.setTime("今日均");
                tth.setRecordTime(new Date());
                tth.setTableHeadId(t);
                tth.setFrontTableList(dayWaterSituationStatisticsTableTth.getFrontTableList());
                tth.setEndTableList(dayWaterSituationStatisticsTableTth.getEndTableList());
                dayWaterSituationStatisticsTableTthList.add(tth);
            }
        }
        if(dayWaterSituationStatisticsTableTthList.isEmpty()){
            return RestResponse.no("今日无数据");
        }
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
                if(!tableParam.getPId().equals("0")){
                    List<TrendsTableParam> noTotalList = trendsTableParamList.stream().filter(t->t.getPId().equals(tableParam.getPId()) && !t.getParamName().equals("合计")).collect(Collectors.toList());
                    for(TrendsTableParam param:noTotalList){
                        for(DayWaterSituationStatisticsTableTth t:dayWaterSituationStatisticsTableTthList){
                            if(t.getTableHeadId().equals(param.getId())){
                                value+=t.getV()==null?0.0:t.getV();
                            }
                            List<TrendsTableParam> listed = trendsTableParamList.stream().filter(p->p.getPId().equals(param.getId()) && !p.getParamName().equals("合计")).collect(Collectors.toList());
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
            if(null != one){
                for(DayWaterSituationStatisticsTableTth t:dayWaterSituationStatisticsTableTthList){
                    if(t.getTableHeadId().equals(one.getId())){
                        t.setV(total);
                    }
                }
            }
        }
        boolean b = this.saveBatch(dayWaterSituationStatisticsTableTthList);
        if (b) {
            return RestResponse.ok();
        }else {
            return RestResponse.no("error");
        }
    }

    @SneakyThrows
    @Override
    public RestResponse selectReportForms(String startTime, String endTime) {
        List<TthReportFormsRes> resList = new ArrayList<>();
        List<DayWaterSituationStatisticsTableTth> dayWaterSituationStatisticsListThisYear = this.baseMapper.selectReportForms(startTime, endTime);
        String lastYearStartTime = getLastYearTime(startTime);
        String lastYearEndTime = getLastYearTime(endTime);
        List<DayWaterSituationStatisticsTableTth> dayWaterSituationStatisticsListLastYear = this.baseMapper.selectReportForms(lastYearStartTime, lastYearEndTime);
        String mk = (String) redisUtil.get("trendsTableParam:list");
        if(StringUtils.isEmpty(mk)){
            updateCache();
            mk = (String) redisUtil.get("trendsTableParam:list");
        }
        List<TrendsTableParam> trendsTableParamListTemp = JSONObject.parseArray(mk, TrendsTableParam.class);
        List<TrendsTableParam> tthTableParamList = trendsTableParamListTemp.stream().filter(t -> t.getUseType() == 1 && t.getUseStation().equals("头屯河水库")).collect(Collectors.toList());
        String jkllTableId = tthTableParamList.stream().filter(t -> t.getParamName().equals("进库流量") && !t.getPId().equals("0")).collect(Collectors.toList()).get(0).getId();
        String ckllTableId = tthTableParamList.stream().filter(t -> t.getParamName().equals("河道流量")).collect(Collectors.toList()).get(0).getId();
        String bgTempTableId = tthTableParamList.stream().filter(t -> t.getParamName().equals("八钢流量")).collect(Collectors.toList()).get(0).getId();
        String hyTableId = tthTableParamList.stream().filter(t -> t.getParamName().equals("红岩流量")).collect(Collectors.toList()).get(0).getId();
        String bgTableId = tthTableParamList.stream().filter(t -> t.getParamName().equals("八钢流量") && t.getPId().equals(bgTempTableId)).collect(Collectors.toList()).get(0).getId();
        String sldLlTableId = tthTableParamList.stream().filter(t -> t.getParamName().equals("渗流点流量")).collect(Collectors.toList()).get(0).getId();
        String kswTableId = tthTableParamList.stream().filter(t -> t.getParamName().equals("库水位")).collect(Collectors.toList()).get(0).getId();
        String krTableId = tthTableParamList.stream().filter(t -> t.getParamName().equals("水库库容")).collect(Collectors.toList()).get(0).getId();

        Map<Date, List<DayWaterSituationStatisticsTableTth>> collect1 = dayWaterSituationStatisticsListThisYear.stream().collect(Collectors.groupingBy(DayWaterSituationStatisticsTableTth::getRecordTime));
        Set<Date> dates = collect1.keySet();
        for(Date date: dates){
            Date lastYearDate = sdf.parse(getLastYearTime(sdf.format(date)));
            TthReportFormsRes res = new TthReportFormsRes();
            res.setDate(date);
            res.setWaterLevel(dayWaterSituationStatisticsListThisYear.stream().filter(t->t.getTableHeadId().equals(kswTableId) && t.getTime().equals("08:00") && t.getV()!=null && t.getRecordTime().compareTo(date)==0).map(DayWaterSituationStatisticsTableTth::getV).reduce(Double::sum).orElse(0.00));
            res.setStorageCapacity(dayWaterSituationStatisticsListThisYear.stream().filter(t->t.getTableHeadId().equals(krTableId) && t.getTime().equals("08:00") && t.getV()!=null && t.getRecordTime().compareTo(date)==0).map(DayWaterSituationStatisticsTableTth::getV).reduce(Double::sum).orElse(0.00));
            res.setInputFlowThisYear(dayWaterSituationStatisticsListThisYear.stream().filter(t->t.getTableHeadId().equals(jkllTableId) && t.getTime().equals("今日均") && t.getV()!=null && t.getRecordTime().compareTo(date)==0).map(DayWaterSituationStatisticsTableTth::getV).reduce(Double::sum).orElse(0.00));
            res.setInputFlowLastYear(dayWaterSituationStatisticsListLastYear.stream().filter(t->t.getTableHeadId().equals(jkllTableId) && t.getTime().equals("今日均") && t.getV()!=null && t.getRecordTime().compareTo(lastYearDate)==0).map(DayWaterSituationStatisticsTableTth::getV).reduce(Double::sum).orElse(0.00));
            res.setOutputFlowThisYear(dayWaterSituationStatisticsListThisYear.stream().filter(t->t.getTableHeadId().equals(ckllTableId) && t.getTime().equals("今日均") && t.getV()!=null && t.getRecordTime().compareTo(date)==0).map(DayWaterSituationStatisticsTableTth::getV).reduce(Double::sum).orElse(0.00));
            res.setOutputFlowLastYear(dayWaterSituationStatisticsListLastYear.stream().filter(t->t.getTableHeadId().equals(ckllTableId) && t.getTime().equals("今日均") && t.getV()!=null && t.getRecordTime().compareTo(lastYearDate)==0).map(DayWaterSituationStatisticsTableTth::getV).reduce(Double::sum).orElse(0.00));
            res.setBgThisYear(dayWaterSituationStatisticsListThisYear.stream().filter(t->t.getTableHeadId().equals(bgTableId) && t.getTime().equals("今日均") && t.getV()!=null && t.getRecordTime().compareTo(date)==0).map(DayWaterSituationStatisticsTableTth::getV).reduce(Double::sum).orElse(0.00));
            res.setBgLastYear(dayWaterSituationStatisticsListLastYear.stream().filter(t->t.getTableHeadId().equals(bgTableId) && t.getTime().equals("今日均") && t.getV()!=null && t.getRecordTime().compareTo(lastYearDate)==0).map(DayWaterSituationStatisticsTableTth::getV).reduce(Double::sum).orElse(0.00));
            res.setHyThisYear(dayWaterSituationStatisticsListThisYear.stream().filter(t->t.getTableHeadId().equals(hyTableId) && t.getTime().equals("今日均") && t.getV()!=null && t.getRecordTime().compareTo(date)==0).map(DayWaterSituationStatisticsTableTth::getV).reduce(Double::sum).orElse(0.00));
            res.setHyLastYear(dayWaterSituationStatisticsListLastYear.stream().filter(t->t.getTableHeadId().equals(hyTableId) && t.getTime().equals("今日均") && t.getV()!=null && t.getRecordTime().compareTo(lastYearDate)==0).map(DayWaterSituationStatisticsTableTth::getV).reduce(Double::sum).orElse(0.00));
            res.setSlThisYear(dayWaterSituationStatisticsListThisYear.stream().filter(t->t.getTableHeadId().equals(sldLlTableId) && t.getTime().equals("今日均") && t.getV()!=null && t.getRecordTime().compareTo(date)==0).map(DayWaterSituationStatisticsTableTth::getV).reduce(Double::sum).orElse(0.00));
            res.setSlLastYear(dayWaterSituationStatisticsListLastYear.stream().filter(t->t.getTableHeadId().equals(sldLlTableId) && t.getTime().equals("今日均") && t.getV()!=null && t.getRecordTime().compareTo(lastYearDate)==0).map(DayWaterSituationStatisticsTableTth::getV).reduce(Double::sum).orElse(0.00));
            resList.add(res);
        }
        if(resList.isEmpty()){
            return RestResponse.no("所选时间段暂无数据！");
        }else {
            return RestResponse.ok(resList);
        }
    }

    private void updateYesterdayData(Date now,List<DayWaterSituationStatisticsTableTth> tthList){

        List<DayWaterSituationStatisticsTableTth> dayWaterSituationStatisticsTableTths1 = this.baseMapper.selectInfoAfterDayList(getDate(now,1));
        if(!dayWaterSituationStatisticsTableTths1.isEmpty()){
            dayWaterSituationStatisticsTableTths1.forEach(t->{
                t.setV(tthList.stream().filter(p->p.getTableHeadId().equals(t.getTableHeadId()) && p.getV() !=null).map(DayWaterSituationStatisticsTableTth::getV).reduce(Double::sum).orElse(0.00));
            });
            this.updateBatchById(dayWaterSituationStatisticsTableTths1);
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

    private String getDate(Date date, Integer num){
        // 创建 Calendar 对象并设置为当前时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        // 将日期向前调整一天（即昨天）
        calendar.add(Calendar.DAY_OF_MONTH, num);
        // 格式化日期输出
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String result = dateFormat.format(calendar.getTime());
        return result;
    }

    @SneakyThrows
    private String getLastYearTime(String time){
        Calendar calendar = Calendar.getInstance();
        Date parse = sdf.parse(time);
        calendar.setTime(parse);
        // 将日期向前调整一天（即昨天）
        calendar.add(Calendar.YEAR, -1);
        // 格式化日期输出
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String result = dateFormat.format(calendar.getTime());
        return result;
    }
}

