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
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.hx.entity.DayWaterSituationStatisticsTableHx;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.lzz.entity.DayWaterSituationStatisticsTableLzz;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional(rollbackFor = Exception.class)
    public RestResponse add(List<DayWaterSituationStatisticsTableHd> dayWaterSituationStatisticsTableHdList) {
        List<DayWaterSituationStatisticsTableHd> todayData = this.baseMapper.selectListHave(dayWaterSituationStatisticsTableHdList.get(0).getTime(),sdf.format(dayWaterSituationStatisticsTableHdList.get(0).getRecordTime()));
        if(!todayData.isEmpty()){
            return RestResponse.no(dayWaterSituationStatisticsTableHdList.get(0).getTime()+"数据已创建");
        }
        dayWaterSituationStatisticsTableHdList.forEach(t->{
            t.setId(UUIDUtils.getUUID());
            String tableParamString = (String)redisUtil.get("trendsTableParam:object:"+t.getTableHeadId());
            TrendsTableParam tableParam = JSONObject.parseObject(tableParamString, TrendsTableParam.class);
            Double flow = (Double) redisUtil.get("irrigatedPlatform:sq:date:id:"+sdf.format(t.getRecordTime())+" "+t.getTime()+":"+tableParam.getUnitId());
            t.setV(flow==null?null:flow);
        });
        List<DayWaterSituationStatisticsTableHd> result = new ArrayList<>();
        List<DayWaterSituationStatisticsTableHd> list = this.baseMapper.selectList(sdf.format(dayWaterSituationStatisticsTableHdList.get(0).getRecordTime()));
        List<DayWaterSituationStatisticsTableHd> tempList = list.stream().filter(t -> t.getTime().equals("昨日均")).collect(Collectors.toList());
        if(null != tempList && tempList.size()==0){
            List<DayWaterSituationStatisticsTableHd> dayWaterSituationStatisticsTableHds = this.baseMapper.selectInfoList(getDate(dayWaterSituationStatisticsTableHdList.get(0).getRecordTime(),-1));
            DayWaterSituationStatisticsTableHd hd = dayWaterSituationStatisticsTableHdList.get(0);
            String[] split = hd.getEndTableList().split(",");
            for(String s:split){
                DayWaterSituationStatisticsTableHd yesterdayBean = new DayWaterSituationStatisticsTableHd();
                yesterdayBean.setTime("昨日均");
                yesterdayBean.setId(UUIDUtils.getUUID());
                yesterdayBean.setTableHeadId(s);
                yesterdayBean.setRecordTime(hd.getRecordTime());
                yesterdayBean.setV(dayWaterSituationStatisticsTableHds.stream().filter(t->t.getTableHeadId().equals(s) && t.getV()!=null).map(DayWaterSituationStatisticsTableHd::getV).reduce(Double::sum).orElse(0.00));
                yesterdayBean.setEndTableList(hd.getEndTableList());
                yesterdayBean.setFrontTableList(hd.getFrontTableList());
                result.add(yesterdayBean);
                String tableParamString = (String)redisUtil.get("trendsTableParam:object:"+yesterdayBean.getTableHeadId());
                TrendsTableParam tableParam = JSONObject.parseObject(tableParamString, TrendsTableParam.class);
                if(null != tableParam && !tableParam.getParamName().equals("合计")){
                    redisUtil.set("A3:data:hd:yesterday:"+getDate(hd.getRecordTime(),-1)+":"+tableParam.getUnitId(),yesterdayBean.getV());
                    redisUtil.set("A3:data:hd:yesterday:forPlan:"+tableParam.getParamName(),yesterdayBean.getV());
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
        List<TotalIdToStation> totalIdToStationList = totalIdToStationService.lambdaQuery().eq(TotalIdToStation::getUseType, 1).eq(TotalIdToStation::getStation, "河东管理站").list();
        //计算行合计
        if(null != totalIdToStationList && totalIdToStationList.size()>0){
            Double total = 0.0;
            List<String> totalCollect = totalIdToStationList.stream().map(TotalIdToStation::getTotalId).collect(Collectors.toList());
            for(String id:totalCollect){
                Double value = 0.0;
                String tableParamString = (String)redisUtil.get("trendsTableParam:object:"+id);
                TrendsTableParam tableParam = JSONObject.parseObject(tableParamString, TrendsTableParam.class);
                if(!tableParam.getPId().equals("0")){
                    List<TrendsTableParam> noTotalList = trendsTableParamList.stream().filter(t->t.getPId().equals(tableParam.getPId()) && !t.getParamName().equals("合计")).collect(Collectors.toList());
                    for(TrendsTableParam param:noTotalList){
                        for(DayWaterSituationStatisticsTableHd t:dayWaterSituationStatisticsTableHdList){
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
                    for(DayWaterSituationStatisticsTableHd t:dayWaterSituationStatisticsTableHdList){
                        if(t.getTableHeadId().equals(id)){
                            t.setV(value);
                        }
                    }
                }
            }
            for(DayWaterSituationStatisticsTableHd t:dayWaterSituationStatisticsTableHdList){
                if(!totalCollect.contains(t.getTableHeadId())){
                    String name = (String)redisUtil.get("trendsTableParam:name:"+t.getTableHeadId());
                    if(!name.equals("红岩水库")){
                        total+=t.getV()==null?0.0:t.getV();
                    }
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
            List<String> totalCollect = totalIdToStationList.stream().map(TotalIdToStation::getTotalId).collect(Collectors.toList());
            for(String id:totalCollect){
                Double value = 0.0;
                String tableParamString = (String)redisUtil.get("trendsTableParam:object:"+id);
                TrendsTableParam tableParam = JSONObject.parseObject(tableParamString, TrendsTableParam.class);
                if(!tableParam.getPId().equals("0")){
                    List<TrendsTableParam> noTotalList = trendsTableParamList.stream().filter(t->t.getPId().equals(tableParam.getPId()) && !t.getParamName().equals("合计")).collect(Collectors.toList());
                    for(TrendsTableParam param:noTotalList){
                        for(DayWaterSituationStatisticsTableHd t:dayWaterSituationStatisticsTableHdList){
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
                    for(DayWaterSituationStatisticsTableHd t:dayWaterSituationStatisticsTableHdList){
                        if(t.getTableHeadId().equals(id)){
                            t.setV(value);
                        }
                    }
                }
            }
            for(DayWaterSituationStatisticsTableHd t:dayWaterSituationStatisticsTableHdList){
                if(!totalCollect.contains(t.getTableHeadId())){
                    String name = (String)redisUtil.get("trendsTableParam:name:"+t.getTableHeadId());
                    if(!name.equals("红岩水库")){
                        total+=t.getV()==null?0.0:t.getV();
                    }
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
                for(DayWaterSituationStatisticsTableHd t:dayWaterSituationStatisticsTableHdList){
                    if(t.getTableHeadId().equals(one.getId())){
                        t.setV(total);
                    }
                }
            }
        }
        boolean b = this.updateBatchById(dayWaterSituationStatisticsTableHdList);
        if (b) {
            if(dayWaterSituationStatisticsTableHdList.get(0).getTime().equals("今日均")){
                dayWaterSituationStatisticsTableHdList.forEach(t->{
                    String tableParamString = (String)redisUtil.get("trendsTableParam:object:"+t.getTableHeadId());
                    TrendsTableParam tableParam = JSONObject.parseObject(tableParamString, TrendsTableParam.class);
                    if(null != tableParam && !tableParam.getParamName().equals("合计")){
                        redisUtil.set("A3:data:hd:waterFee:today:"+sdf.format(t.getRecordTime())+":"+tableParam.getUnitId(),t.getV());
                    }
                });
                updateYesterdayData(dayWaterSituationStatisticsTableHdList.get(0).getRecordTime(),dayWaterSituationStatisticsTableHdList);
            }
            return RestResponse.ok();
        }else {
            return RestResponse.no("error");
        }
    }

    @Override
    public RestResponse insertTodayMeanValue() {
        List<DayWaterSituationStatisticsTableHd> todayData = this.lambdaQuery().eq(DayWaterSituationStatisticsTableHd::getTime, "今日均").list();
        if(!todayData.isEmpty()){
            return RestResponse.no("今日均数据已创建");
        }
        List<DayWaterSituationStatisticsTableHd> dayWaterSituationStatisticsTableHdList = new ArrayList<>();
        List<DayWaterSituationStatisticsTableHd> dayWaterSituationStatisticsTableHds = this.baseMapper.selectList(sdf.format(new Date()));
        if(null!=dayWaterSituationStatisticsTableHds && dayWaterSituationStatisticsTableHds.size()>0){
            DayWaterSituationStatisticsTableHd dayWaterSituationStatisticsTableHd = dayWaterSituationStatisticsTableHds.get(0);
            String endTableList = dayWaterSituationStatisticsTableHd.getEndTableList();
            String[] split = endTableList.split(",");
            for(String t :split){
                DayWaterSituationStatisticsTableHd hd = new DayWaterSituationStatisticsTableHd();
                hd.setId(UUIDUtils.getUUID());
                String tableParamString = (String)redisUtil.get("trendsTableParam:object:"+t);
                TrendsTableParam tableParam = JSONObject.parseObject(tableParamString, TrendsTableParam.class);
                Double flow = (Double) redisUtil.get("irrigatedPlatform:today:"+tableParam.getUnitId());
                hd.setV(flow==null?null:flow);
                hd.setTime("今日均");
                hd.setRecordTime(new Date());
                hd.setTableHeadId(t);
                hd.setFrontTableList(dayWaterSituationStatisticsTableHd.getFrontTableList());
                hd.setEndTableList(dayWaterSituationStatisticsTableHd.getEndTableList());
                dayWaterSituationStatisticsTableHdList.add(hd);
            }
        }
        if(dayWaterSituationStatisticsTableHdList.isEmpty()){
            return RestResponse.no("今日无数据");
        }
        List<TotalIdToStation> totalIdToStationList = totalIdToStationService.lambdaQuery().eq(TotalIdToStation::getUseType, 1).eq(TotalIdToStation::getStation, "河东管理站").list();
        String mk = (String) redisUtil.get("trendsTableParam:list");
        if(StringUtils.isEmpty(mk)){
            updateCache();
            mk = (String) redisUtil.get("trendsTableParam:list");
        }
        List<TrendsTableParam> trendsTableParamListTemp = JSONObject.parseArray(mk, TrendsTableParam.class);
        List<TrendsTableParam> trendsTableParamList = trendsTableParamListTemp.stream().filter(t -> t.getUseType() == 1 && t.getUseStation().equals("河东管理站")).collect(Collectors.toList());
        //计算行合计
        if(null != totalIdToStationList && totalIdToStationList.size()>0){
            Double total = 0.0;
            List<String> totalCollect = totalIdToStationList.stream().map(TotalIdToStation::getTotalId).collect(Collectors.toList());
            for(String id:totalCollect){
                Double value = 0.0;
                String tableParamString = (String)redisUtil.get("trendsTableParam:object:"+id);
                TrendsTableParam tableParam = JSONObject.parseObject(tableParamString, TrendsTableParam.class);
                if(!tableParam.getPId().equals("0")){
                    List<TrendsTableParam> noTotalList = trendsTableParamList.stream().filter(t->t.getPId().equals(tableParam.getPId()) && !t.getParamName().equals("合计")).collect(Collectors.toList());
                    for(TrendsTableParam param:noTotalList){
                        for(DayWaterSituationStatisticsTableHd t:dayWaterSituationStatisticsTableHdList){
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
                    for(DayWaterSituationStatisticsTableHd t:dayWaterSituationStatisticsTableHdList){
                        if(t.getTableHeadId().equals(id)){
                            t.setV(value);
                        }
                    }
                }
            }
            for(DayWaterSituationStatisticsTableHd t:dayWaterSituationStatisticsTableHdList){
                if(!totalCollect.contains(t.getTableHeadId())){
                    String name = (String)redisUtil.get("trendsTableParam:name:"+t.getTableHeadId());
                    if(!name.equals("红岩水库")){
                        total+=t.getV()==null?0.0:t.getV();
                    }
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
                for(DayWaterSituationStatisticsTableHd t:dayWaterSituationStatisticsTableHdList){
                    if(t.getTableHeadId().equals(one.getId())){
                        t.setV(total);
                    }
                }
            }
        }
        boolean b = this.saveBatch(dayWaterSituationStatisticsTableHdList);
        if (b) {
            return RestResponse.ok();
        }else {
            return RestResponse.no("error");
        }
    }

    private void updateYesterdayData(Date now,List<DayWaterSituationStatisticsTableHd> hdList){
        List<DayWaterSituationStatisticsTableHd> dayWaterSituationStatisticsTableHdList = this.baseMapper.selectInfoAfterDayList(getDate(now,1));
        if(!dayWaterSituationStatisticsTableHdList.isEmpty()){
            dayWaterSituationStatisticsTableHdList.forEach(t->{
                t.setV(hdList.stream().filter(p->p.getTableHeadId().equals(t.getTableHeadId()) && p.getV() !=null).map(DayWaterSituationStatisticsTableHd::getV).reduce(Double::sum).orElse(0.00));
                String tableParamString = (String)redisUtil.get("trendsTableParam:object:"+t.getTableHeadId());
                TrendsTableParam tableParam = JSONObject.parseObject(tableParamString, TrendsTableParam.class);
                if(null != tableParam && !tableParam.getParamName().equals("合计")){
                    redisUtil.set("A3:data:hd:yesterday:"+t.getRecordTime()+":"+tableParam.getUnitId(),t.getV());
                    redisUtil.set("A3:data:hd:yesterday:forPlan:"+tableParam.getParamName(),t.getV());
                }
            });
            this.updateBatchById(dayWaterSituationStatisticsTableHdList);
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

