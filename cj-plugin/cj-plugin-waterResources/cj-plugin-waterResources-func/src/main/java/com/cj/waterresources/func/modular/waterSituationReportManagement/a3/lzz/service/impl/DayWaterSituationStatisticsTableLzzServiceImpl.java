package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.lzz.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.common.model.RestResponse;
import com.cj.common.util.RedisUtil;
import com.cj.common.util.UUIDUtils;
import com.cj.middleDatabase.func.modular.lzz.lzzGaugingStation.entity.LzzGaugingStation;
import com.cj.middleDatabase.func.modular.lzz.lzzGaugingStation.service.LzzGaugingStationService;
import com.cj.waterresources.func.modular.overallSituationUnitMgr.entity.OverallSituationUnitMgr;
import com.cj.waterresources.func.modular.overallSituationUnitMgr.service.OverallSituationUnitMgrService;
import com.cj.waterresources.func.modular.trendsTable.bean.res.WaterDailyParamSelectRes;
import com.cj.waterresources.func.modular.trendsTable.entity.TrendsTableParam;
import com.cj.waterresources.func.modular.trendsTable.service.TrendsTableParamService;
import com.cj.waterresources.func.modular.waterPrice.totalIdToStation.entity.TotalIdToStation;
import com.cj.waterresources.func.modular.waterPrice.totalIdToStation.service.TotalIdToStationService;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.entity.WaterFeeStatisticsDetails;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.hx.entity.DayWaterSituationStatisticsTableHx;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.lzz.mapper.DayWaterSituationStatisticsTableLzzMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.lzz.entity.DayWaterSituationStatisticsTableLzz;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.lzz.service.DayWaterSituationStatisticsTableLzzService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 楼庄子水库日水情统计表(DayWaterSituationStatisticsTableLzz)表服务实现类
 *
 * @author makejava
 * @since 2023-12-23 15:59:34
 */
@Service("dayWaterSituationStatisticsTableLzzService")
public class DayWaterSituationStatisticsTableLzzServiceImpl extends ServiceImpl<DayWaterSituationStatisticsTableLzzMapper, DayWaterSituationStatisticsTableLzz> implements DayWaterSituationStatisticsTableLzzService {

    @Autowired
    private TotalIdToStationService totalIdToStationService;

    @Autowired
    private TrendsTableParamService trendsTableParamService;

    @Autowired
    private RedisUtil redisUtil;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    @Override
    public RestResponse<Map<String,List<DayWaterSituationStatisticsTableLzz>>> selectList(String date) {
        try {
            List<DayWaterSituationStatisticsTableLzz> list = this.baseMapper.selectList(date);
            if(null != list && list.size()>0) {
                Map<String, List<DayWaterSituationStatisticsTableLzz>> collect = list.stream().collect(Collectors.groupingBy(DayWaterSituationStatisticsTableLzz::getTime));
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
    public RestResponse add(List<DayWaterSituationStatisticsTableLzz> dayWaterSituationStatisticsTableLzzList) {
        for(DayWaterSituationStatisticsTableLzz t:dayWaterSituationStatisticsTableLzzList){
            t.setId(UUIDUtils.getUUID());
            String paramName = trendsTableParamService.getById(t.getTableHeadId()).getParamName();
            if(paramName.equals("库水位")){
                Double v = (Double)redisUtil.get("lzz:time:waterLevel:"+sdf.format(t.getRecordTime())+" "+t.getTime());
                t.setV(v==null?null:v);
            }
            if(paramName.equals("库容")){
                Double v = (Double)redisUtil.get("lzz:time:capacity:"+sdf.format(t.getRecordTime())+" "+t.getTime());
                t.setV(v==null?null:v);
            }
            if(paramName.equals("楼庄子水厂管道1")){
                Double v = (Double)redisUtil.get("lzz:waterworks:1:"+sdf.format(t.getRecordTime())+" "+t.getTime());
                t.setV(v==null?null:v);
            }
            if(paramName.equals("楼庄子水厂管道2")){
                Double v = (Double)redisUtil.get("lzz:waterworks:2:"+sdf.format(t.getRecordTime())+" "+t.getTime());
                t.setV(v==null?null:v);
            }
            if(paramName.equals("进库流量")){
                Double v = (Double)redisUtil.get("lzz:input:"+sdf.format(t.getRecordTime())+" "+t.getTime());
                t.setV(v==null?null:v);
            }
            if(paramName.equals("河道")){
                Double v = (Double)redisUtil.get("lzz:out:"+sdf.format(t.getRecordTime())+" "+t.getTime());
                t.setV(v==null?null:v);
            }
        }
        List<DayWaterSituationStatisticsTableLzz> result = new ArrayList<>();
        List<DayWaterSituationStatisticsTableLzz> list = this.baseMapper.selectList(sdf.format(dayWaterSituationStatisticsTableLzzList.get(0).getRecordTime()));
        List<DayWaterSituationStatisticsTableLzz> tempList = list.stream().filter(t -> t.getTime().equals("昨日均")).collect(Collectors.toList());
        if(null != tempList && tempList.size()==0){
            List<DayWaterSituationStatisticsTableLzz> dayWaterSituationStatisticsTableLzzes = this.baseMapper.selectInfoList(getDate(dayWaterSituationStatisticsTableLzzList.get(0).getRecordTime(),-1));
            DayWaterSituationStatisticsTableLzz dayWaterSituationStatisticsTableLzz = dayWaterSituationStatisticsTableLzzList.get(0);
            String endTableList = dayWaterSituationStatisticsTableLzz.getEndTableList();
            String[] split = endTableList.split(",");
            for(String s:split){
                DayWaterSituationStatisticsTableLzz yesterdayBean = new DayWaterSituationStatisticsTableLzz();
                yesterdayBean.setTime("昨日均");
                yesterdayBean.setId(UUIDUtils.getUUID());
                yesterdayBean.setTableHeadId(s);
                yesterdayBean.setRecordTime(dayWaterSituationStatisticsTableLzzList.get(0).getRecordTime());
                yesterdayBean.setV(dayWaterSituationStatisticsTableLzzes.stream().filter(t->t.getTableHeadId().equals(s) && t.getV()!=null).map(DayWaterSituationStatisticsTableLzz::getV).reduce(Double::sum).orElse(0.00));
                yesterdayBean.setEndTableList(dayWaterSituationStatisticsTableLzz.getEndTableList());
                yesterdayBean.setFrontTableList(dayWaterSituationStatisticsTableLzz.getFrontTableList());
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
        List<TotalIdToStation> totalIdToStationList = totalIdToStationService.lambdaQuery().eq(TotalIdToStation::getUseType, 1).eq(TotalIdToStation::getStation, "楼庄子水库").list();
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
                    //List<TrendsTableParam> noTotalList = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, tableParam.getPId()).ne(TrendsTableParam::getParamName, "合计").list();
                    for(TrendsTableParam param:noTotalList){
                        for(DayWaterSituationStatisticsTableLzz t:dayWaterSituationStatisticsTableLzzList){
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
                    for(DayWaterSituationStatisticsTableLzz t:dayWaterSituationStatisticsTableLzzList){
                        if(t.getTableHeadId().equals(id)){
                            t.setV(value);
                        }
                    }
                }
            }
            for(DayWaterSituationStatisticsTableLzz t:dayWaterSituationStatisticsTableLzzList){
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
                for(DayWaterSituationStatisticsTableLzz t:dayWaterSituationStatisticsTableLzzList){
                    if(t.getTableHeadId().equals(one.getId())){
                        t.setV(total);
                    }
                }
            }
        }
        result.addAll(dayWaterSituationStatisticsTableLzzList);
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
        boolean remove = this.lambdaUpdate().in(DayWaterSituationStatisticsTableLzz::getId, collect).remove();
        if (remove) {
            return RestResponse.ok();
        }else {
            return RestResponse.no("error");
        }
    }

    @Override
    public RestResponse update(List<DayWaterSituationStatisticsTableLzz> dayWaterSituationStatisticsTableLzzList) {
        dayWaterSituationStatisticsTableLzzList.forEach(t->{
            String paramName = trendsTableParamService.getById(t.getTableHeadId()).getParamName();
            if(paramName.equals("库水位")){
                redisUtil.set("lzz:time:waterLevel:true:"+sdf.format(t.getRecordTime())+" "+t.getTime(),t.getV(),24*3600);
            }
        });
        List<TotalIdToStation> totalIdToStationList = totalIdToStationService.lambdaQuery().eq(TotalIdToStation::getUseType, 1).eq(TotalIdToStation::getStation, "楼庄子水库").list();
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
                        for(DayWaterSituationStatisticsTableLzz t:dayWaterSituationStatisticsTableLzzList){
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
                    for(DayWaterSituationStatisticsTableLzz t:dayWaterSituationStatisticsTableLzzList){
                        if(t.getTableHeadId().equals(id)){
                            t.setV(value);
                        }
                    }

                }
            }
            for(DayWaterSituationStatisticsTableLzz t:dayWaterSituationStatisticsTableLzzList){
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
                for(DayWaterSituationStatisticsTableLzz t:dayWaterSituationStatisticsTableLzzList){
                    if(t.getTableHeadId().equals(one.getId())){
                        t.setV(total);
                    }
                }
            }
        }
        boolean b = this.updateBatchById(dayWaterSituationStatisticsTableLzzList);
        if (b) {
            updateYesterdayData(dayWaterSituationStatisticsTableLzzList.get(0).getRecordTime());
            return RestResponse.ok();
        }else {
            return RestResponse.no("error");
        }
    }

    @Override
    public RestResponse insertTodayMeanValue() {
        List<DayWaterSituationStatisticsTableLzz> dayWaterSituationStatisticsTableLzzList = new ArrayList<>();
        List<DayWaterSituationStatisticsTableLzz> dayWaterSituationStatisticsTableLzzes = this.baseMapper.selectList(sdf.format(new Date()));
        if(null!=dayWaterSituationStatisticsTableLzzes && dayWaterSituationStatisticsTableLzzes.size()>0){
            DayWaterSituationStatisticsTableLzz dayWaterSituationStatisticsTableLzz = dayWaterSituationStatisticsTableLzzes.get(0);
            String endTableList = dayWaterSituationStatisticsTableLzz.getEndTableList();
            String[] split = endTableList.split(",");
            for(String t :split){
                DayWaterSituationStatisticsTableLzz lzz = new DayWaterSituationStatisticsTableLzz();
                lzz.setId(UUIDUtils.getUUID());
                lzz.setV(null);
                lzz.setTime("今日均");
                lzz.setRecordTime(new Date());
                lzz.setTableHeadId(t);
                lzz.setFrontTableList(dayWaterSituationStatisticsTableLzz.getFrontTableList());
                lzz.setEndTableList(dayWaterSituationStatisticsTableLzz.getEndTableList());
                dayWaterSituationStatisticsTableLzzList.add(lzz);
            }
        }
        if(dayWaterSituationStatisticsTableLzzList.isEmpty()){
            return RestResponse.no("今日无数据");
        }
        List<TotalIdToStation> totalIdToStationList = totalIdToStationService.lambdaQuery().eq(TotalIdToStation::getUseType, 1).eq(TotalIdToStation::getStation, "楼庄子水库").list();
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
                        for(DayWaterSituationStatisticsTableLzz t:dayWaterSituationStatisticsTableLzzList){
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
                    for(DayWaterSituationStatisticsTableLzz t:dayWaterSituationStatisticsTableLzzList){
                        if(t.getTableHeadId().equals(id)){
                            t.setV(value);
                        }
                    }

                }
            }
            for(DayWaterSituationStatisticsTableLzz t:dayWaterSituationStatisticsTableLzzList){
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
                for(DayWaterSituationStatisticsTableLzz t:dayWaterSituationStatisticsTableLzzList){
                    if(t.getTableHeadId().equals(one.getId())){
                        t.setV(total);
                    }
                }
            }
        }
        boolean b = this.saveBatch(dayWaterSituationStatisticsTableLzzList);
        if (b) {
            return RestResponse.ok();
        }else {
            return RestResponse.no("error");
        }
    }

    private void updateYesterdayData(Date now){
        List<DayWaterSituationStatisticsTableLzz> dayWaterSituationStatisticsTablelzzList = this.baseMapper.selectInfoList(sdf.format(now));
        List<DayWaterSituationStatisticsTableLzz> dayWaterSituationStatisticsTablelzzs = this.baseMapper.selectList(getDate(now, 1));
        if(!dayWaterSituationStatisticsTablelzzs.isEmpty()){
            List<DayWaterSituationStatisticsTableLzz> hdList = dayWaterSituationStatisticsTablelzzs.stream().filter(t -> t.getTime().equals("昨日均")).collect(Collectors.toList());
            hdList.forEach(t->{
                t.setV(dayWaterSituationStatisticsTablelzzList.stream().filter(p->p.getTableHeadId().equals(t.getTableHeadId()) && p.getV() !=null).map(DayWaterSituationStatisticsTableLzz::getV).reduce(Double::sum).orElse(0.00));
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

