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
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.dkl.entity.DayWaterSituationStatisticsTableDkl;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.lzz.entity.DayWaterSituationStatisticsTableLzz;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.qs.bean.req.selectListFlowReq;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.qs.bean.res.selectListFlowRes;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.qs.bean.vo.QsFlowListTotalVo;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.qs.entity.DayWaterSituationStatisticsTableQsLh;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.qs.mapper.DayWaterSituationStatisticsTableQsMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.qs.entity.DayWaterSituationStatisticsTableQs;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.qs.service.DayWaterSituationStatisticsTableQsLhService;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.qs.service.DayWaterSituationStatisticsTableQsService;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.tth.entity.DayWaterSituationStatisticsTableTth;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private DayWaterSituationStatisticsTableQsLhService dayWaterSituationStatisticsTableQsLhService;

    @Value("${dayUseWaterPlanChoseTime}")
    private String dayUseWaterPlanChoseTime;

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
                return RestResponse.no("暂无数据");
            }
        }catch (Exception e){
            e.printStackTrace();
            return RestResponse.no("error");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse add(List<DayWaterSituationStatisticsTableQs> dayWaterSituationStatisticsTableQsList) {
        List<DayWaterSituationStatisticsTableQs> todayData = this.baseMapper.selectListHave(dayWaterSituationStatisticsTableQsList.get(0).getTime(),sdf.format(dayWaterSituationStatisticsTableQsList.get(0).getRecordTime()));
        if(!todayData.isEmpty()){
            return RestResponse.no(dayWaterSituationStatisticsTableQsList.get(0).getTime()+"数据已创建");
        }
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
        List<TrendsTableParam> trendsTableParamList = trendsTableParamListTemp.stream().filter(t -> t.getUseType() == 1 && t.getUseStation().equals("渠首管理站")).collect(Collectors.toList());
        List<TotalIdToStation> totalIdToStationList = totalIdToStationService.lambdaQuery().eq(TotalIdToStation::getUseType, 1).eq(TotalIdToStation::getStation, "渠首管理站").list();
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
                        for(DayWaterSituationStatisticsTableQs t:dayWaterSituationStatisticsTableQsList){
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
            if(null != one){
                for(DayWaterSituationStatisticsTableQs t:dayWaterSituationStatisticsTableQsList){
                    if(t.getTableHeadId().equals(one.getId())){
                        t.setV(total);
                    }
                }
            }
        }
        //计算总干流量代码
        TrendsTableParam zg= trendsTableParamList.stream().filter(t -> t.getParamName().equals("总干") && t.getPId().equals("0")).collect(Collectors.toList()).get(0);
        TrendsTableParam zgll = trendsTableParamList.stream().filter(t -> t.getPId().equals(zg.getId()) && t.getParamName().equals("总干流量")).collect(Collectors.toList()).get(0);
        TrendsTableParam xa= trendsTableParamList.stream().filter(t -> t.getParamName().equals("西岸") && t.getPId().equals("0")).collect(Collectors.toList()).get(0);
        TrendsTableParam zgqll = trendsTableParamList.stream().filter(t -> t.getPId().equals(xa.getId()) && t.getParamName().equals("西干渠流量")).collect(Collectors.toList()).get(0);
        TrendsTableParam da= trendsTableParamList.stream().filter(t -> t.getParamName().equals("东岸") && t.getPId().equals("0")).collect(Collectors.toList()).get(0);
        TrendsTableParam dgq= trendsTableParamList.stream().filter(t -> t.getParamName().equals("东干渠") && t.getPId().equals(da.getId())).collect(Collectors.toList()).get(0);
        TrendsTableParam dgqll= trendsTableParamList.stream().filter(t -> t.getParamName().equals("东干渠流量") && t.getPId().equals(dgq.getId())).collect(Collectors.toList()).get(0);
        TrendsTableParam ld= trendsTableParamList.stream().filter(t -> t.getParamName().equals("漏斗") && t.getPId().equals("0")).collect(Collectors.toList()).get(0);
        TrendsTableParam sc= trendsTableParamList.stream().filter(t -> t.getParamName().equals("砂厂") && t.getPId().equals("0")).collect(Collectors.toList()).get(0);
        TrendsTableParam jzd= trendsTableParamList.stream().filter(t -> t.getParamName().equals("集中点") && t.getPId().equals(sc.getId())).collect(Collectors.toList()).get(0);
        dayWaterSituationStatisticsTableQsList.forEach(t->{
            if(t.getTableHeadId().equals(zgll.getId())){
                t.setV(
                        dayWaterSituationStatisticsTableQsList.stream().filter(a->a.getTableHeadId().equals(zgqll.getId()) && a.getV()!=null).map(DayWaterSituationStatisticsTableQs::getV).reduce(Double::sum).orElse(0.00)+
                        dayWaterSituationStatisticsTableQsList.stream().filter(a->a.getTableHeadId().equals(dgqll.getId()) && a.getV()!=null).map(DayWaterSituationStatisticsTableQs::getV).reduce(Double::sum).orElse(0.00)+
                        dayWaterSituationStatisticsTableQsList.stream().filter(a->a.getTableHeadId().equals(ld.getId()) && a.getV()!=null).map(DayWaterSituationStatisticsTableQs::getV).reduce(Double::sum).orElse(0.00)+
                        dayWaterSituationStatisticsTableQsList.stream().filter(a->a.getTableHeadId().equals(jzd.getId()) && a.getV()!=null).map(DayWaterSituationStatisticsTableQs::getV).reduce(Double::sum).orElse(0.00)
                );
            }
        });
        //计算全河逻辑代码
        TrendsTableParam qh= trendsTableParamList.stream().filter(t -> t.getParamName().equals("全河") && t.getPId().equals("0")).collect(Collectors.toList()).get(0);
        TrendsTableParam dlq= trendsTableParamList.stream().filter(t -> t.getParamName().equals("灯笼渠流量") && t.getPId().equals(da.getId())).collect(Collectors.toList()).get(0);
        TrendsTableParam dlqCount= trendsTableParamList.stream().filter(t -> t.getParamName().equals("合计") && t.getPId().equals(dlq.getId())).collect(Collectors.toList()).get(0);
        TrendsTableParam st= trendsTableParamList.stream().filter(t -> t.getParamName().equals("生态") && t.getPId().equals("0")).collect(Collectors.toList()).get(0);
        TrendsTableParam hybx= trendsTableParamList.stream().filter(t -> t.getParamName().equals("鸿远博兴") && t.getPId().equals(sc.getId())).collect(Collectors.toList()).get(0);
        TrendsTableParam xh= trendsTableParamList.stream().filter(t -> t.getParamName().equals("兴虎") && t.getPId().equals(sc.getId())).collect(Collectors.toList()).get(0);
        dayWaterSituationStatisticsTableQsList.forEach(t->{
            if(t.getTableHeadId().equals(qh.getId())){
                t.setV(
                        dayWaterSituationStatisticsTableQsList.stream().filter(a->a.getTableHeadId().equals(zgll.getId()) && a.getV()!=null).map(DayWaterSituationStatisticsTableQs::getV).reduce(Double::sum).orElse(0.00)+
                        dayWaterSituationStatisticsTableQsList.stream().filter(a->a.getTableHeadId().equals(dlqCount.getId()) && a.getV()!=null).map(DayWaterSituationStatisticsTableQs::getV).reduce(Double::sum).orElse(0.00)+
                        dayWaterSituationStatisticsTableQsList.stream().filter(a->a.getTableHeadId().equals(st.getId()) && a.getV()!=null).map(DayWaterSituationStatisticsTableQs::getV).reduce(Double::sum).orElse(0.00)+
                        dayWaterSituationStatisticsTableQsList.stream().filter(a->a.getTableHeadId().equals(hybx.getId()) && a.getV()!=null).map(DayWaterSituationStatisticsTableQs::getV).reduce(Double::sum).orElse(0.00)+
                        dayWaterSituationStatisticsTableQsList.stream().filter(a->a.getTableHeadId().equals(xh.getId()) && a.getV()!=null).map(DayWaterSituationStatisticsTableQs::getV).reduce(Double::sum).orElse(0.00)
                );
            }
        });
        result.addAll(dayWaterSituationStatisticsTableQsList);
        boolean b = this.saveBatch(result);
        if (b) {
            if(result.get(0).getTime().equals(dayUseWaterPlanChoseTime+":00")){
                dayWaterSituationStatisticsTableQsList.forEach(t->{
                    redisUtil.set("A3:dayUseWaterPlanChoseTime:qs:"+t.getTableHeadId(),t.getV());
                });
            }
            return RestResponse.ok();
        }else {
            return RestResponse.no("error");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
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
    @Transactional(rollbackFor = Exception.class)
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
                if(!tableParam.getPId().equals("0")){
                    List<TrendsTableParam> noTotalList = trendsTableParamList.stream().filter(t->t.getPId().equals(tableParam.getPId()) && !t.getParamName().equals("合计")).collect(Collectors.toList());
                    for(TrendsTableParam param:noTotalList){
                        for(DayWaterSituationStatisticsTableQs t:dayWaterSituationStatisticsTableQsList){
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
            if(null != one){
                for(DayWaterSituationStatisticsTableQs t:dayWaterSituationStatisticsTableQsList){
                    if(t.getTableHeadId().equals(one.getId())){
                        t.setV(total);
                    }
                }
            }
        }
        //计算总干流量代码
        TrendsTableParam zg= trendsTableParamList.stream().filter(t -> t.getParamName().equals("总干") && t.getPId().equals("0")).collect(Collectors.toList()).get(0);
        TrendsTableParam zgll = trendsTableParamList.stream().filter(t -> t.getPId().equals(zg.getId()) && t.getParamName().equals("总干流量")).collect(Collectors.toList()).get(0);
        TrendsTableParam xa= trendsTableParamList.stream().filter(t -> t.getParamName().equals("西岸") && t.getPId().equals("0")).collect(Collectors.toList()).get(0);
        TrendsTableParam zgqll = trendsTableParamList.stream().filter(t -> t.getPId().equals(xa.getId()) && t.getParamName().equals("西干渠流量")).collect(Collectors.toList()).get(0);
        TrendsTableParam da= trendsTableParamList.stream().filter(t -> t.getParamName().equals("东岸") && t.getPId().equals("0")).collect(Collectors.toList()).get(0);
        TrendsTableParam dgq= trendsTableParamList.stream().filter(t -> t.getParamName().equals("东干渠") && t.getPId().equals(da.getId())).collect(Collectors.toList()).get(0);
        TrendsTableParam dgqll= trendsTableParamList.stream().filter(t -> t.getParamName().equals("东干渠流量") && t.getPId().equals(dgq.getId())).collect(Collectors.toList()).get(0);
        TrendsTableParam ld= trendsTableParamList.stream().filter(t -> t.getParamName().equals("漏斗") && t.getPId().equals("0")).collect(Collectors.toList()).get(0);
        TrendsTableParam sc= trendsTableParamList.stream().filter(t -> t.getParamName().equals("砂厂") && t.getPId().equals("0")).collect(Collectors.toList()).get(0);
        TrendsTableParam jzd= trendsTableParamList.stream().filter(t -> t.getParamName().equals("集中点") && t.getPId().equals(sc.getId())).collect(Collectors.toList()).get(0);
        dayWaterSituationStatisticsTableQsList.forEach(t->{
            if(t.getTableHeadId().equals(zgll.getId())){
                t.setV(
                        dayWaterSituationStatisticsTableQsList.stream().filter(a->a.getTableHeadId().equals(zgqll.getId()) && a.getV()!=null).map(DayWaterSituationStatisticsTableQs::getV).reduce(Double::sum).orElse(0.00)+
                        dayWaterSituationStatisticsTableQsList.stream().filter(a->a.getTableHeadId().equals(dgqll.getId()) && a.getV()!=null).map(DayWaterSituationStatisticsTableQs::getV).reduce(Double::sum).orElse(0.00)+
                        dayWaterSituationStatisticsTableQsList.stream().filter(a->a.getTableHeadId().equals(ld.getId()) && a.getV()!=null).map(DayWaterSituationStatisticsTableQs::getV).reduce(Double::sum).orElse(0.00)+
                        dayWaterSituationStatisticsTableQsList.stream().filter(a->a.getTableHeadId().equals(jzd.getId()) && a.getV()!=null).map(DayWaterSituationStatisticsTableQs::getV).reduce(Double::sum).orElse(0.00)
                );
            }
        });
        //计算全河逻辑代码
        TrendsTableParam qh= trendsTableParamList.stream().filter(t -> t.getParamName().equals("全河") && t.getPId().equals("0")).collect(Collectors.toList()).get(0);
        TrendsTableParam dlq= trendsTableParamList.stream().filter(t -> t.getParamName().equals("灯笼渠流量") && t.getPId().equals(da.getId())).collect(Collectors.toList()).get(0);
        TrendsTableParam dlqCount= trendsTableParamList.stream().filter(t -> t.getParamName().equals("合计") && t.getPId().equals(dlq.getId())).collect(Collectors.toList()).get(0);
        TrendsTableParam st= trendsTableParamList.stream().filter(t -> t.getParamName().equals("生态") && t.getPId().equals("0")).collect(Collectors.toList()).get(0);
        TrendsTableParam hybx= trendsTableParamList.stream().filter(t -> t.getParamName().equals("鸿远博兴") && t.getPId().equals(sc.getId())).collect(Collectors.toList()).get(0);
        TrendsTableParam xh= trendsTableParamList.stream().filter(t -> t.getParamName().equals("兴虎") && t.getPId().equals(sc.getId())).collect(Collectors.toList()).get(0);
        dayWaterSituationStatisticsTableQsList.forEach(t->{
            if(t.getTableHeadId().equals(qh.getId())){
                t.setV(
                        dayWaterSituationStatisticsTableQsList.stream().filter(a->a.getTableHeadId().equals(zgll.getId()) && a.getV()!=null).map(DayWaterSituationStatisticsTableQs::getV).reduce(Double::sum).orElse(0.00)+
                        dayWaterSituationStatisticsTableQsList.stream().filter(a->a.getTableHeadId().equals(dlqCount.getId()) && a.getV()!=null).map(DayWaterSituationStatisticsTableQs::getV).reduce(Double::sum).orElse(0.00)+
                        dayWaterSituationStatisticsTableQsList.stream().filter(a->a.getTableHeadId().equals(st.getId()) && a.getV()!=null).map(DayWaterSituationStatisticsTableQs::getV).reduce(Double::sum).orElse(0.00)+
                        dayWaterSituationStatisticsTableQsList.stream().filter(a->a.getTableHeadId().equals(hybx.getId()) && a.getV()!=null).map(DayWaterSituationStatisticsTableQs::getV).reduce(Double::sum).orElse(0.00)+
                        dayWaterSituationStatisticsTableQsList.stream().filter(a->a.getTableHeadId().equals(xh.getId()) && a.getV()!=null).map(DayWaterSituationStatisticsTableQs::getV).reduce(Double::sum).orElse(0.00)
                );
            }
        });
        boolean b = this.updateBatchById(dayWaterSituationStatisticsTableQsList);
        if (b) {
            if(dayWaterSituationStatisticsTableQsList.get(0).getTime().equals("今日均")){
                dayWaterSituationStatisticsTableQsList.forEach(t->{
                    String tableParamString = (String)redisUtil.get("trendsTableParam:object:"+t.getTableHeadId());
                    TrendsTableParam tableParam = JSONObject.parseObject(tableParamString, TrendsTableParam.class);
                    if(null != tableParam && !tableParam.getParamName().equals("合计")){
                        redisUtil.set("A3:data:qs:waterFee:today:"+sdf.format(t.getRecordTime())+":"+tableParam.getUnitId(),t.getV());
                    }
                });
                updateYesterdayData(dayWaterSituationStatisticsTableQsList.get(0).getRecordTime(),dayWaterSituationStatisticsTableQsList);
            }
            if(dayWaterSituationStatisticsTableQsList.get(0).getTime().equals(dayUseWaterPlanChoseTime+":00")){
                dayWaterSituationStatisticsTableQsList.forEach(t->{
                    redisUtil.set("A3:dayUseWaterPlanChoseTime:qs:"+t.getTableHeadId(),t.getV());
                });
            }
            return RestResponse.ok();
        }else {
            return RestResponse.no("error");
        }
    }

    @Override
    public RestResponse insertTodayMeanValue() {
        List<DayWaterSituationStatisticsTableQs> dayWaterSituationStatisticsTableQss = this.baseMapper.selectList(sdf.format(new Date()));
        List<DayWaterSituationStatisticsTableQs> todayData = dayWaterSituationStatisticsTableQss.stream().filter(t -> t.getTime().equals("今日均")).collect(Collectors.toList());
        if(!todayData.isEmpty()){
            return RestResponse.no("今日均数据已创建");
        }
        List<DayWaterSituationStatisticsTableQs> dayWaterSituationStatisticsTableQsList = new ArrayList<>();
        if(null!=dayWaterSituationStatisticsTableQss && dayWaterSituationStatisticsTableQss.size()>0){
            DayWaterSituationStatisticsTableQs dayWaterSituationStatisticsTableQs = dayWaterSituationStatisticsTableQss.get(0);
            String endTableList = dayWaterSituationStatisticsTableQs.getEndTableList();
            String[] split = endTableList.split(",");
            for(String t :split){
                DayWaterSituationStatisticsTableQs qs = new DayWaterSituationStatisticsTableQs();
                qs.setId(UUIDUtils.getUUID());
                String tableParamString = (String)redisUtil.get("trendsTableParam:object:"+t);
                TrendsTableParam tableParam = JSONObject.parseObject(tableParamString, TrendsTableParam.class);
                Double flow = (Double) redisUtil.get("irrigatedPlatform:today:"+tableParam.getUnitId());
                qs.setV(flow==null?null:flow);
                qs.setTime("今日均");
                qs.setRecordTime(new Date());
                qs.setTableHeadId(t);
                qs.setFrontTableList(dayWaterSituationStatisticsTableQs.getFrontTableList());
                qs.setEndTableList(dayWaterSituationStatisticsTableQs.getEndTableList());
                dayWaterSituationStatisticsTableQsList.add(qs);
            }
        }
        if(dayWaterSituationStatisticsTableQsList.isEmpty()){
            return RestResponse.no("今日无数据");
        }
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
                if(!tableParam.getPId().equals("0")){
                    List<TrendsTableParam> noTotalList = trendsTableParamList.stream().filter(t->t.getPId().equals(tableParam.getPId()) && !t.getParamName().equals("合计")).collect(Collectors.toList());
                    for(TrendsTableParam param:noTotalList){
                        for(DayWaterSituationStatisticsTableQs t:dayWaterSituationStatisticsTableQsList){
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
            if(null != one){
                for(DayWaterSituationStatisticsTableQs t:dayWaterSituationStatisticsTableQsList){
                    if(t.getTableHeadId().equals(one.getId())){
                        t.setV(total);
                    }
                }
            }
        }
        //计算总干流量代码
        TrendsTableParam zg= trendsTableParamList.stream().filter(t -> t.getParamName().equals("总干") && t.getPId().equals("0")).collect(Collectors.toList()).get(0);
        TrendsTableParam zgll = trendsTableParamList.stream().filter(t -> t.getPId().equals(zg.getId()) && t.getParamName().equals("总干流量")).collect(Collectors.toList()).get(0);
        TrendsTableParam xa= trendsTableParamList.stream().filter(t -> t.getParamName().equals("西岸") && t.getPId().equals("0")).collect(Collectors.toList()).get(0);
        TrendsTableParam zgqll = trendsTableParamList.stream().filter(t -> t.getPId().equals(xa.getId()) && t.getParamName().equals("西干渠流量")).collect(Collectors.toList()).get(0);
        TrendsTableParam da= trendsTableParamList.stream().filter(t -> t.getParamName().equals("东岸") && t.getPId().equals("0")).collect(Collectors.toList()).get(0);
        TrendsTableParam dgq= trendsTableParamList.stream().filter(t -> t.getParamName().equals("东干渠") && t.getPId().equals(da.getId())).collect(Collectors.toList()).get(0);
        TrendsTableParam dgqll= trendsTableParamList.stream().filter(t -> t.getParamName().equals("东干渠流量") && t.getPId().equals(dgq.getId())).collect(Collectors.toList()).get(0);
        TrendsTableParam ld= trendsTableParamList.stream().filter(t -> t.getParamName().equals("漏斗") && t.getPId().equals("0")).collect(Collectors.toList()).get(0);
        TrendsTableParam sc= trendsTableParamList.stream().filter(t -> t.getParamName().equals("砂厂") && t.getPId().equals("0")).collect(Collectors.toList()).get(0);
        TrendsTableParam jzd= trendsTableParamList.stream().filter(t -> t.getParamName().equals("集中点") && t.getPId().equals(sc.getId())).collect(Collectors.toList()).get(0);
        dayWaterSituationStatisticsTableQsList.forEach(t->{
            if(t.getTableHeadId().equals(zgll.getId())){
                t.setV(
                        dayWaterSituationStatisticsTableQsList.stream().filter(a->a.getTableHeadId().equals(zgqll.getId()) && a.getV()!=null).map(DayWaterSituationStatisticsTableQs::getV).reduce(Double::sum).orElse(0.00)+
                                dayWaterSituationStatisticsTableQsList.stream().filter(a->a.getTableHeadId().equals(dgqll.getId()) && a.getV()!=null).map(DayWaterSituationStatisticsTableQs::getV).reduce(Double::sum).orElse(0.00)+
                                dayWaterSituationStatisticsTableQsList.stream().filter(a->a.getTableHeadId().equals(ld.getId()) && a.getV()!=null).map(DayWaterSituationStatisticsTableQs::getV).reduce(Double::sum).orElse(0.00)+
                                dayWaterSituationStatisticsTableQsList.stream().filter(a->a.getTableHeadId().equals(jzd.getId()) && a.getV()!=null).map(DayWaterSituationStatisticsTableQs::getV).reduce(Double::sum).orElse(0.00)
                );
            }
        });
        //计算全河逻辑代码
        TrendsTableParam qh= trendsTableParamList.stream().filter(t -> t.getParamName().equals("全河") && t.getPId().equals("0")).collect(Collectors.toList()).get(0);
        TrendsTableParam dlq= trendsTableParamList.stream().filter(t -> t.getParamName().equals("灯笼渠流量") && t.getPId().equals(da.getId())).collect(Collectors.toList()).get(0);
        TrendsTableParam dlqCount= trendsTableParamList.stream().filter(t -> t.getParamName().equals("合计") && t.getPId().equals(dlq.getId())).collect(Collectors.toList()).get(0);
        TrendsTableParam st= trendsTableParamList.stream().filter(t -> t.getParamName().equals("生态") && t.getPId().equals("0")).collect(Collectors.toList()).get(0);
        TrendsTableParam hybx= trendsTableParamList.stream().filter(t -> t.getParamName().equals("鸿远博兴") && t.getPId().equals(sc.getId())).collect(Collectors.toList()).get(0);
        TrendsTableParam xh= trendsTableParamList.stream().filter(t -> t.getParamName().equals("兴虎") && t.getPId().equals(sc.getId())).collect(Collectors.toList()).get(0);
        dayWaterSituationStatisticsTableQsList.forEach(t->{
            if(t.getTableHeadId().equals(qh.getId())){
                t.setV(
                        dayWaterSituationStatisticsTableQsList.stream().filter(a->a.getTableHeadId().equals(zgll.getId()) && a.getV()!=null).map(DayWaterSituationStatisticsTableQs::getV).reduce(Double::sum).orElse(0.00)+
                                dayWaterSituationStatisticsTableQsList.stream().filter(a->a.getTableHeadId().equals(dlqCount.getId()) && a.getV()!=null).map(DayWaterSituationStatisticsTableQs::getV).reduce(Double::sum).orElse(0.00)+
                                dayWaterSituationStatisticsTableQsList.stream().filter(a->a.getTableHeadId().equals(st.getId()) && a.getV()!=null).map(DayWaterSituationStatisticsTableQs::getV).reduce(Double::sum).orElse(0.00)+
                                dayWaterSituationStatisticsTableQsList.stream().filter(a->a.getTableHeadId().equals(hybx.getId()) && a.getV()!=null).map(DayWaterSituationStatisticsTableQs::getV).reduce(Double::sum).orElse(0.00)+
                                dayWaterSituationStatisticsTableQsList.stream().filter(a->a.getTableHeadId().equals(xh.getId()) && a.getV()!=null).map(DayWaterSituationStatisticsTableQs::getV).reduce(Double::sum).orElse(0.00)
                );
            }
        });
        boolean b = this.saveBatch(dayWaterSituationStatisticsTableQsList);
        if (b) {
            return RestResponse.ok();
        }else {
            return RestResponse.no("error");
        }
    }

    @Override
    public RestResponse selectListFlow(selectListFlowReq req) {
        selectListFlowRes res = new selectListFlowRes();
        String thisYearStartTime = req.getEndTime().split("-")[0]+"-01-01";
        String[] startTimeTemp = req.getStartTime().split("-");
        String[] endTimeTemp = req.getEndTime().split("-");
        String lastYearStartTime = String.valueOf(Integer.parseInt(startTimeTemp[0])-1)+"-"+startTimeTemp[1]+"-"+startTimeTemp[2];
        String lastYearEndTime = String.valueOf(Integer.parseInt(endTimeTemp[0])-1)+"-"+endTimeTemp[1]+"-"+endTimeTemp[2];
        String mk = (String) redisUtil.get("trendsTableParam:list");
        if(StringUtils.isEmpty(mk)){
            updateCache();
            mk = (String) redisUtil.get("trendsTableParam:list");
        }
        List<TrendsTableParam> trendsTableParamListTemp = JSONObject.parseArray(mk, TrendsTableParam.class);
        List<TrendsTableParam> trendsTableParamList = trendsTableParamListTemp.stream().filter(t -> t.getUseType() == 1 && t.getUseStation().equals("渠首管理站")).collect(Collectors.toList());
        List<QsFlowListTotalVo>  qsFlowListTotalVoList = new ArrayList<>();
        List<String> waterLevelIdList = trendsTableParamList.stream().filter(t -> t.getParamName().contains("水位")).map(TrendsTableParam::getId).collect(Collectors.toList());

        List<DayWaterSituationStatisticsTableQs> thisTenDaysList = this.baseMapper.selectListByTime(waterLevelIdList,req.getStartTime(),req.getEndTime());
        Map<Date, List<DayWaterSituationStatisticsTableQs>> topForTimeMap = thisTenDaysList.stream().collect(Collectors.groupingBy(DayWaterSituationStatisticsTableQs::getRecordTime));
        Map<String, List<DayWaterSituationStatisticsTableQs>> topForTableIdMap = thisTenDaysList.stream().collect(Collectors.groupingBy(DayWaterSituationStatisticsTableQs::getTableHeadId));

        List<DayWaterSituationStatisticsTableQs> thisYearList = this.baseMapper.selectListByTime(waterLevelIdList,thisYearStartTime,req.getEndTime());
        Map<String, List<DayWaterSituationStatisticsTableQs>> thisYearMap = thisYearList.stream().collect(Collectors.groupingBy(DayWaterSituationStatisticsTableQs::getTableHeadId));

        List<DayWaterSituationStatisticsTableQs> lastTenDaysList = this.baseMapper.selectListByTime(waterLevelIdList,lastYearStartTime,lastYearEndTime);
        Map<String, List<DayWaterSituationStatisticsTableQs>> lastTenDaysMap = lastTenDaysList.stream().collect(Collectors.groupingBy(DayWaterSituationStatisticsTableQs::getTableHeadId));
        Set<String> ids = topForTableIdMap.keySet();
        for(String s:ids){
            QsFlowListTotalVo vo = new QsFlowListTotalVo();
            vo.setTableHeaderId(s);
            Double thisTenDaysflow = topForTableIdMap.size()==0?0.00:topForTableIdMap.get(s).stream().filter(t -> t.getV() != null).map(DayWaterSituationStatisticsTableQs::getV).reduce(Double::sum).orElse(0.00);
            vo.setCurrentWaterFlow(thisTenDaysflow);
            vo.setCurrentWaterVolume(thisTenDaysflow*86400);
            Double thisYearTotalFlow = thisYearMap.size()==0?0.00:thisYearMap.get(s).stream().filter(t -> t.getV() != null).map(DayWaterSituationStatisticsTableQs::getV).reduce(Double::sum).orElse(0.00);
            vo.setAccumulatedWaterVolume(thisYearTotalFlow*86400);
            Double thisYearTenDaysflow = lastTenDaysMap.size()==0?0.00:lastTenDaysMap.get(s).stream().filter(t -> t.getV() != null).map(DayWaterSituationStatisticsTableQs::getV).reduce(Double::sum).orElse(0.00);
            vo.setWaterVolumeDuringLastYear(thisYearTenDaysflow*86400);
            qsFlowListTotalVoList.add(vo);
        }
        res.setFlowDetail(topForTimeMap);
        res.setFlowTotal(qsFlowListTotalVoList);
        return RestResponse.ok(res);
    }

    private void updateYesterdayData(Date now,List<DayWaterSituationStatisticsTableQs> qsList){
        List<DayWaterSituationStatisticsTableQs> dayWaterSituationStatisticsTableQsList = this.baseMapper.selectInfoAfterDayList(getDate(now,1));
        if(!dayWaterSituationStatisticsTableQsList.isEmpty()){
            dayWaterSituationStatisticsTableQsList.forEach(t->{
                t.setV(qsList.stream().filter(p->p.getTableHeadId().equals(t.getTableHeadId()) && p.getV() !=null).map(DayWaterSituationStatisticsTableQs::getV).reduce(Double::sum).orElse(0.00));
                String tableParamString = (String)redisUtil.get("trendsTableParam:object:"+t.getTableHeadId());
                TrendsTableParam tableParam = JSONObject.parseObject(tableParamString, TrendsTableParam.class);
                if(null != tableParam && !tableParam.getParamName().equals("合计")){
                    redisUtil.set("A3:data:qs:yesterday:forPlan:"+tableParam.getParamName(),t.getV());
                }
            });
            this.updateBatchById(dayWaterSituationStatisticsTableQsList);
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

