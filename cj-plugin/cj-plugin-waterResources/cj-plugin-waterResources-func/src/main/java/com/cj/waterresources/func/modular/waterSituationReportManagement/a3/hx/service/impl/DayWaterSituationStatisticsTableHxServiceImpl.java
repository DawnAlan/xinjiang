package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.hx.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.common.model.RestResponse;
import com.cj.common.util.RedisUtil;
import com.cj.common.util.UUIDUtils;
import com.cj.waterresources.func.modular.trendsTable.entity.TrendsTableParam;
import com.cj.waterresources.func.modular.trendsTable.service.TrendsTableParamService;
import com.cj.waterresources.func.modular.waterPrice.totalIdToStation.entity.TotalIdToStation;
import com.cj.waterresources.func.modular.waterPrice.totalIdToStation.service.TotalIdToStationService;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.dkl.entity.DayWaterSituationStatisticsTableDkl;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.hd.entity.DayWaterSituationStatisticsTableHd;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.hx.mapper.DayWaterSituationStatisticsTableHxMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.hx.entity.DayWaterSituationStatisticsTableHx;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.hx.service.DayWaterSituationStatisticsTableHxService;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.lzz.entity.DayWaterSituationStatisticsTableLzz;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 河西管理站日水情统计表(DayWaterSituationStatisticsTableHx)表服务实现类
 *
 * @author makejava
 * @since 2023-12-23 15:59:13
 */
@Service("dayWaterSituationStatisticsTableHxService")
public class DayWaterSituationStatisticsTableHxServiceImpl extends ServiceImpl<DayWaterSituationStatisticsTableHxMapper, DayWaterSituationStatisticsTableHx> implements DayWaterSituationStatisticsTableHxService {

    @Autowired
    private TotalIdToStationService totalIdToStationService;

    @Autowired
    private TrendsTableParamService trendsTableParamService;

    @Autowired
    private RedisUtil redisUtil;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Value("${dayUseWaterPlanChoseTime}")
    private String dayUseWaterPlanChoseTime;

    @Override
    public RestResponse<Map<String, List<DayWaterSituationStatisticsTableHx>>> selectList(String date) {
        try {
            List<DayWaterSituationStatisticsTableHx> list = this.baseMapper.selectList(date);
            if(null != list && list.size()>0) {
                Map<String, List<DayWaterSituationStatisticsTableHx>> collect = list.stream().collect(Collectors.groupingBy(DayWaterSituationStatisticsTableHx::getTime));
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
    public RestResponse add(List<DayWaterSituationStatisticsTableHx> dayWaterSituationStatisticsTableHxList) {
        List<DayWaterSituationStatisticsTableHx> todayData = this.baseMapper.selectListHave(dayWaterSituationStatisticsTableHxList.get(0).getTime(),sdf.format(dayWaterSituationStatisticsTableHxList.get(0).getRecordTime()));
        if(!todayData.isEmpty()){
            return RestResponse.no(sdf.format(dayWaterSituationStatisticsTableHxList.get(0).getRecordTime())+" "+dayWaterSituationStatisticsTableHxList.get(0).getTime()+"数据已创建");
        }
        dayWaterSituationStatisticsTableHxList.forEach(t->{
            t.setId(UUIDUtils.getUUID());
            String tableParamString = (String)redisUtil.get("trendsTableParam:object:"+t.getTableHeadId());
            TrendsTableParam tableParam = JSONObject.parseObject(tableParamString, TrendsTableParam.class);
            Double flow = (Double) redisUtil.get("irrigatedPlatform:sq:date:id:"+sdf.format(t.getRecordTime())+" "+t.getTime()+":"+tableParam.getUnitId());
            t.setV(flow==null?null:flow);
        });
        List<DayWaterSituationStatisticsTableHx> result = new ArrayList<>();
        List<DayWaterSituationStatisticsTableHx> list = this.baseMapper.selectList(sdf.format(dayWaterSituationStatisticsTableHxList.get(0).getRecordTime()));
        List<DayWaterSituationStatisticsTableHx> tempList = list.stream().filter(t -> t.getTime().equals("昨日均")).collect(Collectors.toList());
        if(null != tempList && tempList.size()==0){
            List<DayWaterSituationStatisticsTableHx> dayWaterSituationStatisticsTableHxes = this.baseMapper.selectInfoList(getDate(dayWaterSituationStatisticsTableHxList.get(0).getRecordTime(),-1));
            DayWaterSituationStatisticsTableHx hx = dayWaterSituationStatisticsTableHxList.get(0);
            String endTableList = hx.getEndTableList();
            String[] split = endTableList.split(",");
            for(String s:split){
                DayWaterSituationStatisticsTableHx yesterdayBean = new DayWaterSituationStatisticsTableHx();
                yesterdayBean.setTime("昨日均");
                yesterdayBean.setId(UUIDUtils.getUUID());
                yesterdayBean.setTableHeadId(s);
                yesterdayBean.setRecordTime(hx.getRecordTime());
                yesterdayBean.setV(dayWaterSituationStatisticsTableHxes.stream().filter(t->t.getTableHeadId().equals(s) && t.getV()!=null).map(DayWaterSituationStatisticsTableHx::getV).reduce(Double::sum).orElse(0.00));
                yesterdayBean.setEndTableList(hx.getEndTableList());
                yesterdayBean.setFrontTableList(hx.getFrontTableList());
                result.add(yesterdayBean);
                String tableParamString = (String)redisUtil.get("trendsTableParam:object:"+yesterdayBean.getTableHeadId());
                TrendsTableParam tableParam = JSONObject.parseObject(tableParamString, TrendsTableParam.class);
                if(null != tableParam && !tableParam.getParamName().equals("合计")){
                    redisUtil.set("A3:data:hx:yesterday:"+getDate(hx.getRecordTime(),-1)+":"+tableParam.getUnitId(),yesterdayBean.getV());
                    redisUtil.set("A3:data:hx:yesterday:forPlan:"+tableParam.getParamName(),yesterdayBean.getV());
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
        List<TotalIdToStation> totalIdToStationList = totalIdToStationService.lambdaQuery().eq(TotalIdToStation::getUseType, 1).eq(TotalIdToStation::getStation, "河西管理站").list();
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
                        for(DayWaterSituationStatisticsTableHx t:dayWaterSituationStatisticsTableHxList){
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
                    for(DayWaterSituationStatisticsTableHx t:dayWaterSituationStatisticsTableHxList){
                        if(t.getTableHeadId().equals(id)){
                            t.setV(value);
                        }
                    }
                }
            }
            for(DayWaterSituationStatisticsTableHx t:dayWaterSituationStatisticsTableHxList){
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
                for(DayWaterSituationStatisticsTableHx t:dayWaterSituationStatisticsTableHxList){
                    if(t.getTableHeadId().equals(one.getId())){
                        t.setV(total);
                    }
                }
            }
        }
        result.addAll(dayWaterSituationStatisticsTableHxList);
        boolean b = this.saveBatch(result);
        if (b) {
            if(result.get(0).getTime().equals(dayUseWaterPlanChoseTime+":00")){
                dayWaterSituationStatisticsTableHxList.forEach(t->{
                    redisUtil.set("A3:dayUseWaterPlanChoseTime:hx:"+t.getTableHeadId(),t.getV());
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
        boolean remove = this.lambdaUpdate().in(DayWaterSituationStatisticsTableHx::getId, collect).remove();
        if (remove) {
            return RestResponse.ok();
        }else {
            return RestResponse.no("error");
        }
    }

    @Override
    public RestResponse update(List<DayWaterSituationStatisticsTableHx> dayWaterSituationStatisticsTableHxList) {
        List<TotalIdToStation> totalIdToStationList = totalIdToStationService.lambdaQuery().eq(TotalIdToStation::getUseType, 1).eq(TotalIdToStation::getStation, "河西管理站").list();
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
                        for(DayWaterSituationStatisticsTableHx t:dayWaterSituationStatisticsTableHxList){
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
                    for(DayWaterSituationStatisticsTableHx t:dayWaterSituationStatisticsTableHxList){
                        if(t.getTableHeadId().equals(id)){
                            t.setV(value);
                        }
                    }

                }
            }
            for(DayWaterSituationStatisticsTableHx t:dayWaterSituationStatisticsTableHxList){
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
                for(DayWaterSituationStatisticsTableHx t:dayWaterSituationStatisticsTableHxList){
                    if(t.getTableHeadId().equals(one.getId())){
                        t.setV(total);
                    }
                }
            }
        }
        boolean b = this.updateBatchById(dayWaterSituationStatisticsTableHxList);
        if (b) {
            if(dayWaterSituationStatisticsTableHxList.get(0).getTime().equals("今日均")){
                dayWaterSituationStatisticsTableHxList.forEach(t->{
                    String tableParamString = (String)redisUtil.get("trendsTableParam:object:"+t.getTableHeadId());
                    TrendsTableParam tableParam = JSONObject.parseObject(tableParamString, TrendsTableParam.class);
                    if(null != tableParam && !tableParam.getParamName().equals("合计")){
                        redisUtil.set("A3:data:hx:waterFee:today:"+sdf.format(t.getRecordTime())+":"+tableParam.getUnitId(),t.getV());
                    }
                });
                updateYesterdayData(dayWaterSituationStatisticsTableHxList.get(0).getRecordTime(),dayWaterSituationStatisticsTableHxList);
            }
            if(dayWaterSituationStatisticsTableHxList.get(0).getTime().equals(dayUseWaterPlanChoseTime+":00")){
                dayWaterSituationStatisticsTableHxList.forEach(t->{
                    redisUtil.set("A3:dayUseWaterPlanChoseTime:hx:"+t.getTableHeadId(),t.getV());
                });
            }
            return RestResponse.ok();
        }else {
            return RestResponse.no("error");
        }
    }

    @Override
    public RestResponse insertTodayMeanValue() {
        List<DayWaterSituationStatisticsTableHx> todayData = this.lambdaQuery().eq(DayWaterSituationStatisticsTableHx::getTime, "今日均").list();
        if(!todayData.isEmpty()){
            return RestResponse.no("今日均数据已创建");
        }
        List<DayWaterSituationStatisticsTableHx> dayWaterSituationStatisticsTableHxList = new ArrayList<>();
        List<DayWaterSituationStatisticsTableHx> dayWaterSituationStatisticsTableHxes = this.baseMapper.selectList(sdf.format(new Date()));
        if(null!=dayWaterSituationStatisticsTableHxes && dayWaterSituationStatisticsTableHxes.size()>0){
            DayWaterSituationStatisticsTableHx dayWaterSituationStatisticsTableHx = dayWaterSituationStatisticsTableHxes.get(0);
            String endTableList = dayWaterSituationStatisticsTableHx.getEndTableList();
            String[] split = endTableList.split(",");
            for(String t :split){
                DayWaterSituationStatisticsTableHx hx = new DayWaterSituationStatisticsTableHx();
                hx.setId(UUIDUtils.getUUID());
                String tableParamString = (String)redisUtil.get("trendsTableParam:object:"+t);
                TrendsTableParam tableParam = JSONObject.parseObject(tableParamString, TrendsTableParam.class);
                Double flow = (Double) redisUtil.get("irrigatedPlatform:today:"+tableParam.getUnitId());
                hx.setV(flow==null?null:flow);
                hx.setTime("今日均");
                hx.setRecordTime(new Date());
                hx.setTableHeadId(t);
                hx.setFrontTableList(dayWaterSituationStatisticsTableHx.getFrontTableList());
                hx.setEndTableList(dayWaterSituationStatisticsTableHx.getEndTableList());
                dayWaterSituationStatisticsTableHxList.add(hx);
            }
        }
        if(dayWaterSituationStatisticsTableHxList.isEmpty()){
            return RestResponse.no("今日无数据");
        }
        List<TotalIdToStation> totalIdToStationList = totalIdToStationService.lambdaQuery().eq(TotalIdToStation::getUseType, 1).eq(TotalIdToStation::getStation, "河西管理站").list();
        String mk = (String) redisUtil.get("trendsTableParam:list");
        if(StringUtils.isEmpty(mk)){
            updateCache();
            mk = (String) redisUtil.get("trendsTableParam:list");
        }
        List<TrendsTableParam> trendsTableParamListTemp = JSONObject.parseArray(mk, TrendsTableParam.class);
        List<TrendsTableParam> trendsTableParamList = trendsTableParamListTemp.stream().filter(t -> t.getUseType() == 1 && t.getUseStation().equals("河西管理站")).collect(Collectors.toList());
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
                        for(DayWaterSituationStatisticsTableHx t:dayWaterSituationStatisticsTableHxList){
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
                    for(DayWaterSituationStatisticsTableHx t:dayWaterSituationStatisticsTableHxList){
                        if(t.getTableHeadId().equals(id)){
                            t.setV(value);
                        }
                    }

                }
            }
            for(DayWaterSituationStatisticsTableHx t:dayWaterSituationStatisticsTableHxList){
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
                for(DayWaterSituationStatisticsTableHx t:dayWaterSituationStatisticsTableHxList){
                    if(t.getTableHeadId().equals(one.getId())){
                        t.setV(total);
                    }
                }
            }
        }
        boolean b = this.saveBatch(dayWaterSituationStatisticsTableHxList);
        if (b) {
            return RestResponse.ok();
        }else {
            return RestResponse.no("error");
        }
    }

    private void updateYesterdayData(Date now ,List<DayWaterSituationStatisticsTableHx> hxList){
        List<DayWaterSituationStatisticsTableHx> dayWaterSituationStatisticsTableHxList = this.baseMapper.selectInfoAfterDayList(getDate(now,1));
        if(!dayWaterSituationStatisticsTableHxList.isEmpty()){
            dayWaterSituationStatisticsTableHxList.forEach(t->{
                t.setV(hxList.stream().filter(p->p.getTableHeadId().equals(t.getTableHeadId()) && p.getV() !=null).map(DayWaterSituationStatisticsTableHx::getV).reduce(Double::sum).orElse(0.00));
                String tableParamString = (String)redisUtil.get("trendsTableParam:object:"+t.getTableHeadId());
                TrendsTableParam tableParam = JSONObject.parseObject(tableParamString, TrendsTableParam.class);
                if(null != tableParam && !tableParam.getParamName().equals("合计")){
                    redisUtil.set("A3:data:hx:yesterday:"+t.getRecordTime()+":"+tableParam.getUnitId(),t.getV());
                    redisUtil.set("A3:data:hx:yesterday:forPlan:"+tableParam.getParamName(),t.getV());
                }
            });
            this.updateBatchById(dayWaterSituationStatisticsTableHxList);
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

