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
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.hd.entity.DayWaterSituationStatisticsTableHd;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.lzz.entity.DayWaterSituationStatisticsTableLzz;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.qs.mapper.DayWaterSituationStatisticsTableQsMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.qs.entity.DayWaterSituationStatisticsTableQs;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.qs.service.DayWaterSituationStatisticsTableQsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 渠首管理站日水情统计表(DayWaterSituationStatisticsTableQs)表服务实现类
 *
 * @author makejava
 * @since 2023-12-23 15:59:55
 */
@Service("dayWaterSituationStatisticsTableQsService")
public class DayWaterSituationStatisticsTableQsServiceImpl extends ServiceImpl<DayWaterSituationStatisticsTableQsMapper, DayWaterSituationStatisticsTableQs> implements DayWaterSituationStatisticsTableQsService {

    @Autowired
    private TotalIdToStationService totalIdToStationService;

    @Autowired
    private TrendsTableParamService trendsTableParamService;

    @Autowired
    private RedisUtil redisUtil;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public RestResponse<Map<String, List<DayWaterSituationStatisticsTableQs>>> selectList(String date) {
        try {
            List<DayWaterSituationStatisticsTableQs> list = this.baseMapper.selectList(date);
            if(null != list && list.size()>0) {
                Map<String, List<DayWaterSituationStatisticsTableQs>> collect = list.stream().collect(Collectors.groupingBy(DayWaterSituationStatisticsTableQs::getTime));
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
    public RestResponse add(List<DayWaterSituationStatisticsTableQs> dayWaterSituationStatisticsTableQsList) {
        dayWaterSituationStatisticsTableQsList.forEach(t->{
            t.setId(UUIDUtils.getUUID());
            String tableParamString = (String)redisUtil.get("trendsTableParam:object:"+t.getTableHeadId());
            TrendsTableParam tableParam = JSONObject.parseObject(tableParamString, TrendsTableParam.class);
            Double flow = (Double) redisUtil.get("irrigatedPlatform:sq:date:id:"+sdf.format(t.getRecordTime())+" "+t.getTime()+":"+tableParam.getUnitId());
            t.setV(flow==null?null:flow);
        });
        List<DayWaterSituationStatisticsTableQs> result = new ArrayList<>();
        List<DayWaterSituationStatisticsTableQs> list = this.baseMapper.selectList(sdf.format(dayWaterSituationStatisticsTableQsList.get(0).getRecordTime()));
        List<DayWaterSituationStatisticsTableQs> tempList = list.stream().filter(t -> t.getTime().equals("昨日均")).collect(Collectors.toList());
        if(null != tempList && tempList.size()==0){
            List<DayWaterSituationStatisticsTableQs> dayWaterSituationStatisticsTableQss = this.baseMapper.selectInfoList(getDate(dayWaterSituationStatisticsTableQsList.get(0).getRecordTime(),-1));
            DayWaterSituationStatisticsTableQs qs = dayWaterSituationStatisticsTableQsList.get(0);
            String[] split = qs.getEndTableList().split(",");
            for(String s:split){
                DayWaterSituationStatisticsTableQs yesterdayBean = new DayWaterSituationStatisticsTableQs();
                yesterdayBean.setTime("昨日均");
                yesterdayBean.setId(UUIDUtils.getUUID());
                yesterdayBean.setTableHeadId(s);
                yesterdayBean.setRecordTime(dayWaterSituationStatisticsTableQsList.get(0).getRecordTime());
                yesterdayBean.setV(dayWaterSituationStatisticsTableQss.stream().filter(t->t.getTableHeadId().equals(s) && t.getV()!=null).map(DayWaterSituationStatisticsTableQs::getV).reduce(Double::sum).orElse(0.00));
                yesterdayBean.setEndTableList(qs.getEndTableList());
                yesterdayBean.setFrontTableList(qs.getFrontTableList());
                result.add(yesterdayBean);
                String tableParamString = (String)redisUtil.get("trendsTableParam:object:"+yesterdayBean.getTableHeadId());
                TrendsTableParam tableParam = JSONObject.parseObject(tableParamString, TrendsTableParam.class);
                if(StringUtils.isNotEmpty(tableParam.getUnitId())){
                    redisUtil.set("A3:data:qs:yesterday:"+getDate(qs.getRecordTime(),-1)+":"+tableParam.getUnitId(),yesterdayBean.getV());
                }
            }
        }
        String mk = (String) redisUtil.get("trendsTableParam:list");
        if(StringUtils.isEmpty(mk)){
            updateCache();
            mk = (String) redisUtil.get("trendsTableParam:list");
        }
        List<TrendsTableParam> trendsTableParamListTemp = JSONObject.parseArray(mk, TrendsTableParam.class);
        List<TrendsTableParam> trendsTableParamList = trendsTableParamListTemp.stream().filter(t -> t.getUseType() == 1).collect(Collectors.toList());
        List<TotalIdToStation> totalIdToStationList = totalIdToStationService.lambdaQuery().eq(TotalIdToStation::getUseType, 1).eq(TotalIdToStation::getStation, "渠首管理站").list();
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
                        for(DayWaterSituationStatisticsTableQs t:dayWaterSituationStatisticsTableQsList){
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
                    for(DayWaterSituationStatisticsTableQs t:dayWaterSituationStatisticsTableQsList){
                        if(t.getTableHeadId().equals(id)){
                            t.setV(value);
                        }
                    }
                }
            }
            for(DayWaterSituationStatisticsTableQs t:dayWaterSituationStatisticsTableQsList){
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
                for(DayWaterSituationStatisticsTableQs t:dayWaterSituationStatisticsTableQsList){
                    if(t.getTableHeadId().equals(one.getId())){
                        t.setV(total);
                    }
                }
            }
        }
        result.addAll(dayWaterSituationStatisticsTableQsList);
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
        boolean remove = this.lambdaUpdate().in(DayWaterSituationStatisticsTableQs::getId, collect).remove();
        if (remove) {
            return RestResponse.ok();
        }else {
            return RestResponse.no("error");
        }
    }

    @Override
    public RestResponse update(List<DayWaterSituationStatisticsTableQs> dayWaterSituationStatisticsTableQsList) {
        List<TotalIdToStation> totalIdToStationList = totalIdToStationService.lambdaQuery().eq(TotalIdToStation::getUseType, 1).eq(TotalIdToStation::getStation, "渠首管理站").list();
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
                        for(DayWaterSituationStatisticsTableQs t:dayWaterSituationStatisticsTableQsList){
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
                    for(DayWaterSituationStatisticsTableQs t:dayWaterSituationStatisticsTableQsList){
                        if(t.getTableHeadId().equals(id)){
                            t.setV(value);
                        }
                    }

                }
            }
            for(DayWaterSituationStatisticsTableQs t:dayWaterSituationStatisticsTableQsList){
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
                for(DayWaterSituationStatisticsTableQs t:dayWaterSituationStatisticsTableQsList){
                    if(t.getTableHeadId().equals(one.getId())){
                        t.setV(total);
                    }
                }
            }
        }
        boolean b = this.updateBatchById(dayWaterSituationStatisticsTableQsList);
        if (b) {
            DayWaterSituationStatisticsTableQs dayWaterSituationStatisticsTableQs = dayWaterSituationStatisticsTableQsList.get(0);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dayWaterSituationStatisticsTableQs.getRecordTime());
            calendar.add(Calendar.DAY_OF_MONTH,-1);
            Date time = calendar.getTime();
            if(dayWaterSituationStatisticsTableQsList.get(0).getTime().equals("昨日均")){
                dayWaterSituationStatisticsTableQsList.forEach(t->{
                    String tableParamString = (String)redisUtil.get("trendsTableParam:object:"+t.getTableHeadId());
                    TrendsTableParam tableParam = JSONObject.parseObject(tableParamString, TrendsTableParam.class);
                    if(StringUtils.isNotEmpty(tableParam.getUnitId())){
                        redisUtil.set("A3:data:qs:yesterday:"+sdf.format(time)+":"+tableParam.getUnitId(),t.getV());
                    }
                });
            }
            updateYesterdayData(dayWaterSituationStatisticsTableQsList.get(0).getRecordTime());
            return RestResponse.ok();
        }else {
            return RestResponse.no("error");
        }
    }

    private void updateYesterdayData(Date now){
        List<DayWaterSituationStatisticsTableQs> dayWaterSituationStatisticsTableQsList = this.baseMapper.selectInfoList(sdf.format(now));
        List<DayWaterSituationStatisticsTableQs> dayWaterSituationStatisticsTableQss = this.baseMapper.selectList(getDate(now, 1));
        if(!dayWaterSituationStatisticsTableQss.isEmpty()){
            List<DayWaterSituationStatisticsTableQs> hdList = dayWaterSituationStatisticsTableQss.stream().filter(t -> t.getTime().equals("昨日均")).collect(Collectors.toList());
            hdList.forEach(t->{
                t.setV(dayWaterSituationStatisticsTableQsList.stream().filter(p->p.getTableHeadId().equals(t.getTableHeadId()) && p.getV() !=null).map(DayWaterSituationStatisticsTableQs::getV).reduce(Double::sum).orElse(0.00));
            });
            this.updateBatchById(hdList);
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

