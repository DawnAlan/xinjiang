package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.qs.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.common.model.RestResponse;
import com.cj.common.util.RedisUtil;
import com.cj.common.util.UUIDUtils;
import com.cj.waterresources.func.modular.trendsTable.entity.TrendsTableParam;
import com.cj.waterresources.func.modular.trendsTable.service.TrendsTableParamService;
import com.cj.waterresources.func.modular.waterPrice.totalIdToStation.entity.TotalIdToStation;
import com.cj.waterresources.func.modular.waterPrice.totalIdToStation.service.TotalIdToStationService;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.lzz.entity.DayWaterSituationStatisticsTableLzz;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.qs.entity.DayWaterSituationStatisticsTableQs;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.qs.mapper.DayWaterSituationStatisticsTableQsLhMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.qs.entity.DayWaterSituationStatisticsTableQsLh;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.qs.mapper.DayWaterSituationStatisticsTableQsMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.qs.service.DayWaterSituationStatisticsTableQsLhService;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.qs.service.DayWaterSituationStatisticsTableQsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * (DayWaterSituationStatisticsTableQsLh)表服务实现类
 *
 * @author makejava
 * @since 2024-03-21 10:58:52
 */
@Service("dayWaterSituationStatisticsTableQsLhService")
public class DayWaterSituationStatisticsTableQsLhServiceImpl extends ServiceImpl<DayWaterSituationStatisticsTableQsLhMapper, DayWaterSituationStatisticsTableQsLh> implements DayWaterSituationStatisticsTableQsLhService {

    @Autowired
    private TotalIdToStationService totalIdToStationService;

    @Autowired
    private TrendsTableParamService trendsTableParamService;

    @Autowired
    private DayWaterSituationStatisticsTableQsMapper dayWaterSituationStatisticsTableQsMapper;

    @Autowired
    private DayWaterSituationStatisticsTableQsService dayWaterSituationStatisticsTableQsService;

    @Autowired
    private RedisUtil redisUtil;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Value("${dayUseWaterPlanChoseTime}")
    private String dayUseWaterPlanChoseTime;

    @Value("${dayWaterBalanceChoseTime}")
    private String dayWaterBalanceChoseTime;

    @Override
    public RestResponse<Map<String, List<DayWaterSituationStatisticsTableQsLh>>> selectList(String date) {
        try {
            List<DayWaterSituationStatisticsTableQsLh> list = this.baseMapper.selectList(date);
            if(null != list && list.size()>0) {
                Map<String, List<DayWaterSituationStatisticsTableQsLh>> collect = list.stream().collect(Collectors.groupingBy(DayWaterSituationStatisticsTableQsLh::getTime));
                return RestResponse.ok(collect);
            }else {
                return RestResponse.no("暂无数据");
            }
        }catch (Exception e){
            e.printStackTrace();
            return RestResponse.no("error");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse add(List<DayWaterSituationStatisticsTableQsLh> dayWaterSituationStatisticsTableQsLhList) {
        List<DayWaterSituationStatisticsTableQsLh> todayData = this.baseMapper.selectListHave(dayWaterSituationStatisticsTableQsLhList.get(0).getTime(),sdf.format(dayWaterSituationStatisticsTableQsLhList.get(0).getRecordTime()));
        if(!todayData.isEmpty()){
            return RestResponse.no(dayWaterSituationStatisticsTableQsLhList.get(0).getTime()+"数据已创建");
        }
        dayWaterSituationStatisticsTableQsLhList.forEach(t->{
            t.setId(UUIDUtils.getUUID());
            String tableParamString = (String)redisUtil.get("trendsTableParam:object:"+t.getTableHeadId());
            TrendsTableParam tableParam = JSONObject.parseObject(tableParamString, TrendsTableParam.class);
            Double flow = 0.00;
            if(t.getTime().equals("今日均")){
                flow = (Double) redisUtil.get("irrigatedPlatform:yesterday:"+tableParam.getUnitId());
            }else {
                flow = (Double) redisUtil.get("irrigatedPlatform:sq:date:id:"+sdf.format(t.getRecordTime())+" "+t.getTime()+":"+tableParam.getUnitId());
            }
            t.setV(flow==null?null:flow);
        });
        List<DayWaterSituationStatisticsTableQsLh> result = new ArrayList<>();
        List<DayWaterSituationStatisticsTableQsLh> list = this.baseMapper.selectList(sdf.format(dayWaterSituationStatisticsTableQsLhList.get(0).getRecordTime()));
        List<DayWaterSituationStatisticsTableQsLh> tempList = list.stream().filter(t -> t.getTime().equals("昨日均")).collect(Collectors.toList());
        DayWaterSituationStatisticsTableQsLh qsLh = dayWaterSituationStatisticsTableQsLhList.get(0);
        if(null != tempList && tempList.size()==0){
            List<DayWaterSituationStatisticsTableQsLh> dayWaterSituationStatisticsTableQsLhs = this.baseMapper.selectInfoList(getDate(dayWaterSituationStatisticsTableQsLhList.get(0).getRecordTime(),-1));
            String[] split = qsLh.getEndTableList().split(",");
            for(String s:split){
                DayWaterSituationStatisticsTableQsLh yesterdayBean = new DayWaterSituationStatisticsTableQsLh();
                yesterdayBean.setTime("昨日均");
                yesterdayBean.setId(UUIDUtils.getUUID());
                yesterdayBean.setTableHeadId(s);
                yesterdayBean.setRecordTime(dayWaterSituationStatisticsTableQsLhList.get(0).getRecordTime());
                yesterdayBean.setV(dayWaterSituationStatisticsTableQsLhs.stream().filter(t->t.getTableHeadId().equals(s) && t.getV()!=null).map(DayWaterSituationStatisticsTableQsLh::getV).reduce(Double::sum).orElse(0.00));
                yesterdayBean.setEndTableList(qsLh.getEndTableList());
                yesterdayBean.setFrontTableList(qsLh.getFrontTableList());
                result.add(yesterdayBean);
                String tableParamString = (String)redisUtil.get("trendsTableParam:object:"+yesterdayBean.getTableHeadId());
                TrendsTableParam tableParam = JSONObject.parseObject(tableParamString, TrendsTableParam.class);
                if(null != tableParam && !tableParam.getParamName().equals("合计")){
                    redisUtil.set("A3:data:qs:yesterday:forPlan:"+tableParam.getParamName(),yesterdayBean.getV());
                }
            }
        }
        String mk = (String) redisUtil.get("trendsTableParam:list");
        if(StringUtils.isEmpty(mk)){
            updateCache();
            mk = (String) redisUtil.get("trendsTableParam:list");
        }
        List<TrendsTableParam> trendsTableParamListTemp = JSONObject.parseArray(mk, TrendsTableParam.class);
        List<TrendsTableParam> trendsTableParamList = trendsTableParamListTemp.stream().filter(t -> t.getUseType() == 1 && t.getUseStation().equals("灯笼渠绿化")).collect(Collectors.toList());
        List<TotalIdToStation> totalIdToStationList = totalIdToStationService.lambdaQuery().eq(TotalIdToStation::getUseType, 1).eq(TotalIdToStation::getStation, "灯笼渠绿化").list();
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
                        if(param.getParamName().equals("东干渠水位")){
                            continue;
                        }
                        for(DayWaterSituationStatisticsTableQsLh t:dayWaterSituationStatisticsTableQsLhList){
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
                    for(DayWaterSituationStatisticsTableQsLh t:dayWaterSituationStatisticsTableQsLhList){
                        if(t.getTableHeadId().equals(id)){
                            t.setV(value);
                        }
                    }
                }
            }
            for(DayWaterSituationStatisticsTableQsLh t:dayWaterSituationStatisticsTableQsLhList){
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
                for(DayWaterSituationStatisticsTableQsLh t:dayWaterSituationStatisticsTableQsLhList){
                    if(t.getTableHeadId().equals(one.getId())){
                        t.setV(total);
                    }
                }
            }
        }
        result.addAll(dayWaterSituationStatisticsTableQsLhList);
        boolean b = this.saveBatch(result);
        if (b) {
            List<DayWaterSituationStatisticsTableQs> qdList = dayWaterSituationStatisticsTableQsMapper.selectListForLh(qsLh.getTime(),sdf.format(qsLh.getRecordTime()));
            if(qdList.isEmpty()){
                return RestResponse.no("请先创建对应的渠首管理站记录！");
            }
            if(result.get(0).getTime().equals(dayUseWaterPlanChoseTime+":00")){
                dayWaterSituationStatisticsTableQsLhList.forEach(t->{
                    redisUtil.set("A3:dayUseWaterPlanChoseTime:qs:"+t.getTableHeadId(),t.getV());
                });
            }
            if(result.get(0).getTime().equals(dayWaterBalanceChoseTime.length()==1?"0"+dayWaterBalanceChoseTime+":00":dayWaterBalanceChoseTime+":00")){
                dayWaterSituationStatisticsTableQsLhList.forEach(t->{
                    String tableParamString = (String)redisUtil.get("trendsTableParam:object:"+t.getTableHeadId());
                    TrendsTableParam tableParam = JSONObject.parseObject(tableParamString, TrendsTableParam.class);
                    redisUtil.set("A3:dayWaterBalanceChoseTime:qs:"+tableParam.getUnitId(),t.getV());
                });
            }
            List<TrendsTableParam> trendsTableParamQsList = trendsTableParamListTemp.stream().filter(t -> t.getUseType() == 1 && t.getUseStation().equals("渠首管理站")).collect(Collectors.toList());
            TrendsTableParam dlqlhTableParam = trendsTableParamQsList.stream().filter(t -> t.getParamName().equals("灯笼渠绿化")).collect(Collectors.toList()).get(0);
            TrendsTableParam hjTableParam = trendsTableParamList.stream().filter(t -> t.getParamName().equals("合计") && t.getPId().equals("0")).collect(Collectors.toList()).get(0);
            DayWaterSituationStatisticsTableQsLh qsLhObj = dayWaterSituationStatisticsTableQsLhList.stream().filter(t -> t.getTableHeadId().equals(hjTableParam.getId())).collect(Collectors.toList()).get(0);
            qdList.forEach(t->{
                if(t.getTableHeadId().equals(dlqlhTableParam.getId())){
                    t.setV(qsLhObj==null?null:qsLhObj.getV()==null?null:qsLhObj.getV());
                }
            });
            RestResponse update = dayWaterSituationStatisticsTableQsService.update(qdList);
            if(update.getCode()==200){
                return RestResponse.ok();
            }else {
                return RestResponse.no("修改渠首管理站灯笼渠绿化结果值失败");
            }
        }else {
            return RestResponse.no("error");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse delete(String ids) {
        List<String> collect = Arrays.stream(ids.split(",")).collect(Collectors.toList());
        boolean remove = this.lambdaUpdate().in(DayWaterSituationStatisticsTableQsLh::getId, collect).remove();
        if (remove) {
            return RestResponse.ok();
        }else {
            return RestResponse.no("error");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse update(List<DayWaterSituationStatisticsTableQsLh> dayWaterSituationStatisticsTableQsLhList) {
        List<TotalIdToStation> totalIdToStationList = totalIdToStationService.lambdaQuery().eq(TotalIdToStation::getUseType, 1).eq(TotalIdToStation::getStation, "灯笼渠绿化").list();
        String mk = (String) redisUtil.get("trendsTableParam:list");
        if(StringUtils.isEmpty(mk)){
            updateCache();
            mk = (String) redisUtil.get("trendsTableParam:list");
        }
        List<TrendsTableParam> trendsTableParamListTemp = JSONObject.parseArray(mk, TrendsTableParam.class);
        DayWaterSituationStatisticsTableQsLh qsLh = dayWaterSituationStatisticsTableQsLhList.get(0);
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
                        for(DayWaterSituationStatisticsTableQsLh t:dayWaterSituationStatisticsTableQsLhList){
                            String nameTemp = (String)redisUtil.get("trendsTableParam:name:"+t.getTableHeadId());
                            if(nameTemp.equals("东干渠水位")){
                                continue;
                            }
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
                    for(DayWaterSituationStatisticsTableQsLh t:dayWaterSituationStatisticsTableQsLhList){
                        if(t.getTableHeadId().equals(id)){
                            t.setV(value);
                        }
                    }

                }
            }
            for(DayWaterSituationStatisticsTableQsLh t:dayWaterSituationStatisticsTableQsLhList){
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
                for(DayWaterSituationStatisticsTableQsLh t:dayWaterSituationStatisticsTableQsLhList){
                    if(t.getTableHeadId().equals(one.getId())){
                        t.setV(total);
                    }
                }
            }
        }

        boolean b = this.updateBatchById(dayWaterSituationStatisticsTableQsLhList);
        if (b) {
            if(dayWaterSituationStatisticsTableQsLhList.get(0).getTime().equals("今日均")){
                dayWaterSituationStatisticsTableQsLhList.forEach(t->{
                    String tableParamString = (String)redisUtil.get("trendsTableParam:object:"+t.getTableHeadId());
                    TrendsTableParam tableParam = JSONObject.parseObject(tableParamString, TrendsTableParam.class);
                    if(null != tableParam && !tableParam.getParamName().equals("合计")){
                        redisUtil.set("A3:data:qs:waterFee:today:"+sdf.format(t.getRecordTime())+":"+tableParam.getUnitId(),t.getV());
                    }
                });
                updateYesterdayData(dayWaterSituationStatisticsTableQsLhList.get(0).getRecordTime(),dayWaterSituationStatisticsTableQsLhList);
            }
            if(dayWaterSituationStatisticsTableQsLhList.get(0).getTime().equals(dayUseWaterPlanChoseTime+":00")){
                dayWaterSituationStatisticsTableQsLhList.forEach(t->{
                    redisUtil.set("A3:dayUseWaterPlanChoseTime:qs:"+t.getTableHeadId(),t.getV());
                });
            }
            if(dayWaterSituationStatisticsTableQsLhList.get(0).getTime().equals(dayWaterBalanceChoseTime.length()==1?"0"+dayWaterBalanceChoseTime+":00":dayWaterBalanceChoseTime+":00")){
                dayWaterSituationStatisticsTableQsLhList.forEach(t->{
                    String tableParamString = (String)redisUtil.get("trendsTableParam:object:"+t.getTableHeadId());
                    TrendsTableParam tableParam = JSONObject.parseObject(tableParamString, TrendsTableParam.class);
                    redisUtil.set("A3:dayWaterBalanceChoseTime:qs:"+tableParam.getUnitId(),t.getV());
                });
            }
            List<DayWaterSituationStatisticsTableQs> qdList = dayWaterSituationStatisticsTableQsMapper.selectListForLh(qsLh.getTime(),sdf.format(qsLh.getRecordTime()));
            if(qdList.isEmpty()){
                return RestResponse.no("请先创建对应的渠首管理站记录！");
            }
            List<TrendsTableParam> trendsTableParamQsList = trendsTableParamListTemp.stream().filter(t -> t.getUseType() == 1 && t.getUseStation().equals("渠首管理站")).collect(Collectors.toList());
            TrendsTableParam dlqlhTableParam = trendsTableParamQsList.stream().filter(t -> t.getParamName().equals("灯笼渠绿化")).collect(Collectors.toList()).get(0);
            TrendsTableParam hjTableParam = trendsTableParamList.stream().filter(t -> t.getParamName().equals("合计") && t.getPId().equals("0") && t.getUseStation().equals("灯笼渠绿化")).collect(Collectors.toList()).get(0);
            DayWaterSituationStatisticsTableQsLh qsLhObj = dayWaterSituationStatisticsTableQsLhList.stream().filter(t -> t.getTableHeadId().equals(hjTableParam.getId())).collect(Collectors.toList()).get(0);
            qdList.forEach(t->{
                if(t.getTableHeadId().equals(dlqlhTableParam.getId())){
                    t.setV(qsLhObj==null?null:qsLhObj.getV()==null?null:qsLhObj.getV());
                }
                String tableParamString = (String)redisUtil.get("trendsTableParam:object:"+t.getTableHeadId());
                TrendsTableParam tableParam = JSONObject.parseObject(tableParamString, TrendsTableParam.class);
                if(null != tableParam && !tableParam.getParamName().equals("合计")){
                    redisUtil.set("A3:data:qs:waterFee:today:"+t.getRecordTime()+":"+tableParam.getUnitId(),t.getV());
                }
            });
            RestResponse update = dayWaterSituationStatisticsTableQsService.update(qdList);
            if(update.getCode()==200){
                return RestResponse.ok();
            }else {
                return RestResponse.no("修改渠首管理站灯笼渠绿化结果值失败");
            }
        }else {
            return RestResponse.no("error");
        }
    }

    @Override
    public RestResponse insertTodayMeanValue() {
        List<DayWaterSituationStatisticsTableQsLh> dayWaterSituationStatisticsTableQsLhs = this.baseMapper.selectList(sdf.format(new Date()));
        List<DayWaterSituationStatisticsTableQsLh> todayData = dayWaterSituationStatisticsTableQsLhs.stream().filter(t -> t.getTime().equals("今日均")).collect(Collectors.toList());
        if(!todayData.isEmpty()){
            return RestResponse.no("今日均数据已创建");
        }
        List<DayWaterSituationStatisticsTableQsLh> dayWaterSituationStatisticsTableQsLhList = new ArrayList<>();
        if(null!=dayWaterSituationStatisticsTableQsLhs && dayWaterSituationStatisticsTableQsLhs.size()>0){
            DayWaterSituationStatisticsTableQsLh dayWaterSituationStatisticsTableQs = dayWaterSituationStatisticsTableQsLhs.get(0);
            String endTableList = dayWaterSituationStatisticsTableQs.getEndTableList();
            String[] split = endTableList.split(",");
            for(String t :split){
                DayWaterSituationStatisticsTableQsLh qsLh = new DayWaterSituationStatisticsTableQsLh();
                qsLh.setId(UUIDUtils.getUUID());
                String tableParamString = (String)redisUtil.get("trendsTableParam:object:"+t);
                TrendsTableParam tableParam = JSONObject.parseObject(tableParamString, TrendsTableParam.class);
                Double flow = (Double) redisUtil.get("irrigatedPlatform:yesterday:"+tableParam.getUnitId());
                qsLh.setV(flow==null?null:flow);
                qsLh.setTime("今日均");
                qsLh.setRecordTime(new Date());
                qsLh.setTableHeadId(t);
                qsLh.setFrontTableList(dayWaterSituationStatisticsTableQs.getFrontTableList());
                qsLh.setEndTableList(dayWaterSituationStatisticsTableQs.getEndTableList());
                dayWaterSituationStatisticsTableQsLhList.add(qsLh);
            }
        }
        if(dayWaterSituationStatisticsTableQsLhList.isEmpty()){
            return RestResponse.no("今日无数据");
        }
        List<TotalIdToStation> totalIdToStationList = totalIdToStationService.lambdaQuery().eq(TotalIdToStation::getUseType, 1).eq(TotalIdToStation::getStation, "灯笼渠绿化").list();
        String mk = (String) redisUtil.get("trendsTableParam:list");
        if(StringUtils.isEmpty(mk)){
            updateCache();
            mk = (String) redisUtil.get("trendsTableParam:list");
        }
        List<TrendsTableParam> trendsTableParamListTemp = JSONObject.parseArray(mk, TrendsTableParam.class);
        List<TrendsTableParam> trendsTableParamList = trendsTableParamListTemp.stream().filter(t -> t.getUseType() == 1 && t.getUseStation().equals("灯笼渠绿化")).collect(Collectors.toList());
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
                        for(DayWaterSituationStatisticsTableQsLh t:dayWaterSituationStatisticsTableQsLhList){
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
                    for(DayWaterSituationStatisticsTableQsLh t:dayWaterSituationStatisticsTableQsLhList){
                        if(t.getTableHeadId().equals(id)){
                            t.setV(value);
                        }
                    }

                }
            }
            for(DayWaterSituationStatisticsTableQsLh t:dayWaterSituationStatisticsTableQsLhList){
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
                for(DayWaterSituationStatisticsTableQsLh t:dayWaterSituationStatisticsTableQsLhList){
                    if(t.getTableHeadId().equals(one.getId())){
                        t.setV(total);
                    }
                }
            }
        }
        boolean b = this.saveBatch(dayWaterSituationStatisticsTableQsLhList);
        if (b) {
            DayWaterSituationStatisticsTableQsLh qsLh = dayWaterSituationStatisticsTableQsLhList.get(0);
            List<DayWaterSituationStatisticsTableQs> qdList = dayWaterSituationStatisticsTableQsMapper.selectListForLh(qsLh.getTime(),sdf.format(qsLh.getRecordTime()));
            if(qdList.isEmpty()){
                return RestResponse.no("请先创建对应的渠首管理站记录！");
            }
            List<TrendsTableParam> trendsTableParamQsList = trendsTableParamListTemp.stream().filter(t -> t.getUseType() == 1 && t.getUseStation().equals("渠首管理站")).collect(Collectors.toList());
            TrendsTableParam dlqlhTableParam = trendsTableParamQsList.stream().filter(t -> t.getParamName().equals("灯笼渠绿化")).collect(Collectors.toList()).get(0);
            TrendsTableParam hjTableParam = trendsTableParamList.stream().filter(t -> t.getParamName().equals("合计") && t.getPId().equals("0")).collect(Collectors.toList()).get(0);
            DayWaterSituationStatisticsTableQsLh qsLhObj = dayWaterSituationStatisticsTableQsLhList.stream().filter(t -> t.getTableHeadId().equals(hjTableParam.getId())).collect(Collectors.toList()).get(0);
            qdList.forEach(t->{
                if(t.getTableHeadId().equals(dlqlhTableParam.getId())){
                    t.setV(qsLhObj==null?null:qsLhObj.getV()==null?null:qsLhObj.getV());
                }
            });
            RestResponse update = dayWaterSituationStatisticsTableQsService.update(qdList);
            if(update.getCode()==200){
                return RestResponse.ok();
            }else {
                return RestResponse.no("修改渠首管理站灯笼渠绿化结果值失败");
            }
        }else {
            return RestResponse.no("error");
        }
    }

    private void updateYesterdayData(Date now,List<DayWaterSituationStatisticsTableQsLh> qsList){
        String mk = (String) redisUtil.get("trendsTableParam:list");
        if(StringUtils.isEmpty(mk)){
            updateCache();
            mk = (String) redisUtil.get("trendsTableParam:list");
        }
        List<TrendsTableParam> trendsTableParamListTemp = JSONObject.parseArray(mk, TrendsTableParam.class);
        List<TrendsTableParam> trendsTableParamList = trendsTableParamListTemp.stream().filter(t -> t.getUseType() == 1 && t.getUseStation().equals("灯笼渠绿化")).collect(Collectors.toList());
        List<DayWaterSituationStatisticsTableQsLh> dayWaterSituationStatisticsTableQsLhList = this.baseMapper.selectInfoAfterDayList(getDate(now,1));
        if(!dayWaterSituationStatisticsTableQsLhList.isEmpty()){
            dayWaterSituationStatisticsTableQsLhList.forEach(t->{
                t.setV(qsList.stream().filter(p->p.getTableHeadId().equals(t.getTableHeadId()) && p.getV() !=null).map(DayWaterSituationStatisticsTableQsLh::getV).reduce(Double::sum).orElse(0.00));
                String tableParamString = (String)redisUtil.get("trendsTableParam:object:"+t.getTableHeadId());
                TrendsTableParam tableParam = JSONObject.parseObject(tableParamString, TrendsTableParam.class);
                if(null != tableParam && !tableParam.getParamName().equals("合计")){
                    redisUtil.set("A3:data:qs:yesterday:forPlan:"+tableParam.getParamName(),t.getV());
                }
            });
            this.updateBatchById(dayWaterSituationStatisticsTableQsLhList);

            //修改渠首管理站--灯笼渠绿化对应的值
            DayWaterSituationStatisticsTableQsLh qsLh = dayWaterSituationStatisticsTableQsLhList.get(0);
            List<DayWaterSituationStatisticsTableQs> qdList = dayWaterSituationStatisticsTableQsMapper.selectListForLh(qsLh.getTime(),sdf.format(qsLh.getRecordTime()));
            if(!qdList.isEmpty()){
                List<TrendsTableParam> trendsTableParamQsList = trendsTableParamListTemp.stream().filter(t -> t.getUseType() == 1 && t.getUseStation().equals("渠首管理站")).collect(Collectors.toList());
                TrendsTableParam dlqlhTableParam = trendsTableParamQsList.stream().filter(t -> t.getParamName().equals("灯笼渠绿化")).collect(Collectors.toList()).get(0);
                TrendsTableParam hjTableParam = trendsTableParamList.stream().filter(t -> t.getParamName().equals("合计") && t.getPId().equals("0")).collect(Collectors.toList()).get(0);
                DayWaterSituationStatisticsTableQsLh qsLhObj = dayWaterSituationStatisticsTableQsLhList.stream().filter(t -> t.getTableHeadId().equals(hjTableParam.getId())).collect(Collectors.toList()).get(0);
                qdList.forEach(t->{
                    if(t.getTableHeadId().equals(dlqlhTableParam.getId())){
                        t.setV(qsLhObj==null?null:qsLhObj.getV()==null?null:qsLhObj.getV());
                        String tableParamString = (String)redisUtil.get("trendsTableParam:object:"+t.getTableHeadId());
                        TrendsTableParam tableParam = JSONObject.parseObject(tableParamString, TrendsTableParam.class);
                        if(null != tableParam && !tableParam.getParamName().equals("合计")){
                            redisUtil.set("A3:data:qs:yesterday:forPlan:"+tableParam.getParamName(),t.getV());
                        }
                    }
                });
                dayWaterSituationStatisticsTableQsService.update(qdList);
            }
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
}

