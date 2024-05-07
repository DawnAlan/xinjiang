package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.dkl.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.common.model.RestResponse;
import com.cj.common.util.RedisUtil;
import com.cj.common.util.UUIDUtils;
import com.cj.waterresources.func.modular.trendsTable.entity.TrendsTableParam;
import com.cj.waterresources.func.modular.trendsTable.service.TrendsTableParamService;
import com.cj.waterresources.func.modular.waterPrice.totalIdToStation.entity.TotalIdToStation;
import com.cj.waterresources.func.modular.waterPrice.totalIdToStation.service.TotalIdToStationService;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.dkl.mapper.DayWaterSituationStatisticsTableDklMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.dkl.entity.DayWaterSituationStatisticsTableDkl;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.dkl.service.DayWaterSituationStatisticsTableDklService;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.hd.entity.DayWaterSituationStatisticsTableHd;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.hd.service.DayWaterSituationStatisticsTableHdService;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.hx.entity.DayWaterSituationStatisticsTableHx;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.hx.service.DayWaterSituationStatisticsTableHxService;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.lzz.entity.DayWaterSituationStatisticsTableLzz;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.lzz.service.DayWaterSituationStatisticsTableLzzService;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.qs.entity.DayWaterSituationStatisticsTableQs;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.qs.service.DayWaterSituationStatisticsTableQsService;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.tth.entity.DayWaterSituationStatisticsTableTth;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.tth.service.DayWaterSituationStatisticsTableTthService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 对口率日水情统计表(DayWaterSituationStatisticsTableDkl)表服务实现类
 *
 * @author makejava
 * @since 2023-12-23 15:58:24
 */
@Service("dayWaterSituationStatisticsTableDklService")
public class DayWaterSituationStatisticsTableDklServiceImpl extends ServiceImpl<DayWaterSituationStatisticsTableDklMapper, DayWaterSituationStatisticsTableDkl> implements DayWaterSituationStatisticsTableDklService {

    @Autowired
    private TotalIdToStationService totalIdToStationService;

    @Autowired
    private TrendsTableParamService trendsTableParamService;

    @Autowired
    private DayWaterSituationStatisticsTableTthService tthService;

    @Autowired
    private DayWaterSituationStatisticsTableLzzService lzzService;

    @Autowired
    private DayWaterSituationStatisticsTableQsService qsService;

    @Autowired
    private DayWaterSituationStatisticsTableHdService hdService;

    @Autowired
    private DayWaterSituationStatisticsTableHxService hxService;


    @Autowired
    private RedisUtil redisUtil;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public RestResponse<Map<String, List<DayWaterSituationStatisticsTableDkl>>> selectList(String date) {
        try {
            List<DayWaterSituationStatisticsTableDkl> list = this.baseMapper.selectList(date);
            if(null != list && list.size()>0) {
                Map<String, List<DayWaterSituationStatisticsTableDkl>> collect = list.stream().collect(Collectors.groupingBy(DayWaterSituationStatisticsTableDkl::getTime));
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
    public RestResponse add(List<DayWaterSituationStatisticsTableDkl> dayWaterSituationStatisticsTableDklList) {
        dayWaterSituationStatisticsTableDklList.forEach(t-> {
            try {
                t.setRecordTime(sdf.parse(sdf.format(t.getRecordTime())));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });
        List<DayWaterSituationStatisticsTableDkl> todayData = this.baseMapper.selectListHave(dayWaterSituationStatisticsTableDklList.get(0).getTime(),sdf.format(dayWaterSituationStatisticsTableDklList.get(0).getRecordTime()));
        if(!todayData.isEmpty()){
            return RestResponse.no(dayWaterSituationStatisticsTableDklList.get(0).getTime()+"数据已创建");
        }
        String mk = (String) redisUtil.get("trendsTableParam:list");
        if(StringUtils.isEmpty(mk)){
            trendsTableParamService.updateCache();
            mk = (String) redisUtil.get("trendsTableParam:list");
        }
        List<TrendsTableParam> trendsTableParamListTemp = JSONObject.parseArray(mk, TrendsTableParam.class);
        List<TrendsTableParam> trendsTableParamList = trendsTableParamListTemp.stream().filter(t -> t.getUseType() == 1).collect(Collectors.toList());
        dayWaterSituationStatisticsTableDklList.forEach(t->t.setId(UUIDUtils.getUUID()));
        List<DayWaterSituationStatisticsTableDkl> result = new ArrayList<>();
        List<DayWaterSituationStatisticsTableDkl> list = this.baseMapper.selectList(sdf.format(dayWaterSituationStatisticsTableDklList.get(0).getRecordTime()));
        List<DayWaterSituationStatisticsTableDkl> tempList = list.stream().filter(t -> t.getTime().equals("昨日均")).collect(Collectors.toList());
        if(null != tempList && tempList.size()==0){
            List<DayWaterSituationStatisticsTableDkl> yesterdayList = this.baseMapper.selectInfoList(getDate(dayWaterSituationStatisticsTableDklList.get(0).getRecordTime(),-1));
            DayWaterSituationStatisticsTableDkl dkl = dayWaterSituationStatisticsTableDklList.get(0);
            String[] split = dkl.getEndTableList().split(",");
            for(String s:split){
                DayWaterSituationStatisticsTableDkl yesterdayBean = new DayWaterSituationStatisticsTableDkl();
                yesterdayBean.setTime("昨日均");
                yesterdayBean.setId(UUIDUtils.getUUID());
                yesterdayBean.setTableHeadId(s);
                yesterdayBean.setRecordTime(dayWaterSituationStatisticsTableDklList.get(0).getRecordTime());
                Double v = yesterdayList.stream().filter(t -> t.getTableHeadId().equals(s)).filter(t->t.getV()!=null).map(t -> t.getV()).reduce(Double::sum).orElse(0.00);
                yesterdayBean.setV(v==null?0.0:v);
                yesterdayBean.setEndTableList(dkl.getEndTableList());
                yesterdayBean.setFrontTableList(dkl.getFrontTableList());
                result.add(yesterdayBean);
            }
        }
        List<TotalIdToStation> totalIdToStationList = totalIdToStationService.lambdaQuery().eq(TotalIdToStation::getUseType, 1).eq(TotalIdToStation::getStation, "对口率").list();
        //计算行合计
        if(null != totalIdToStationList && totalIdToStationList.size()>0){
            Double total = 0.0;
            List<String> totalCollect = totalIdToStationList.stream().filter(t->t.getName().equals("合计")).map(TotalIdToStation::getTotalId).collect(Collectors.toList());
            for(String id:totalCollect){
                Double value = 0.0;
                TrendsTableParam tableParam = JSONObject.parseObject((String)redisUtil.get("trendsTableParam:object:"+id),TrendsTableParam.class);
                if(!tableParam.getPId().equals("0")){
                    List<TrendsTableParam> noTotalList = trendsTableParamList.stream().filter(t->t.getPId().equals(tableParam.getPId())).filter(t->!t.getParamName().equals("合计")).collect(Collectors.toList());
                    for(TrendsTableParam param:noTotalList){
                        for(DayWaterSituationStatisticsTableDkl t:dayWaterSituationStatisticsTableDklList){
                            if(t.getTableHeadId().equals(param.getId())){
                                value+=t.getV()==null?0.0:t.getV();
                            }
                            List<TrendsTableParam> listed = trendsTableParamList.stream().filter(p->p.getPId().equals(param.getId())).filter(p->!p.getParamName().equals("合计")).collect(Collectors.toList());
                            if(null != listed && listed.size()>0){
                                for (TrendsTableParam param1:listed){
                                    if(t.getTableHeadId().equals(param1.getId())){
                                        value+=t.getV()==null?0.0:t.getV();
                                    }
                                }
                            }
                        }
                    }
                    for(DayWaterSituationStatisticsTableDkl t:dayWaterSituationStatisticsTableDklList){
                        if(t.getTableHeadId().equals(id)){
                            t.setV(value);
                        }
                    }
                }
            }
            for(DayWaterSituationStatisticsTableDkl t:dayWaterSituationStatisticsTableDklList){
                if(!totalIdToStationList.stream().map(TotalIdToStation::getTotalId).collect(Collectors.toList()).contains(t.getTableHeadId())){
                    total+=t.getV()==null?0.0:t.getV();
                }
            }
            TrendsTableParam one =null;
            List<TrendsTableParam> collect1 = trendsTableParamList.stream().filter(t -> t.getPId().equals("0")).filter(t -> t.getUseType() == 1).collect(Collectors.toList());
            for(TrendsTableParam param : collect1){
                for(String s:totalCollect){
                    if(param.getId().equals(s)){
                        one = param;
                    }
                }
            }
            if(null != one){
                for(DayWaterSituationStatisticsTableDkl t:dayWaterSituationStatisticsTableDklList){
                    if(t.getTableHeadId().equals(one.getId())){
                        t.setV(total);
                    }
                }
            }
        }
        //计算对口率
        for(DayWaterSituationStatisticsTableDkl dkl:dayWaterSituationStatisticsTableDklList){
            TrendsTableParam tableParam = JSONObject.parseObject((String)redisUtil.get("trendsTableParam:object:"+dkl.getTableHeadId()),TrendsTableParam.class);
            if(tableParam.getParamName().equals("楼庄子-头屯河")){
                try {
                    List<TrendsTableParam> lzzParamList = trendsTableParamList.stream().filter(t->t.getUseStation().equals("楼庄子水库")).filter(t->t.getUseType()==1).collect(Collectors.toList());
                    TrendsTableParam lzzCkParam = lzzParamList.stream().filter(t -> t.getPId().equals("0")).filter(t -> t.getParamName().equals("出库")).collect(Collectors.toList()).get(0);
                    TrendsTableParam lzzLlParam = lzzParamList.stream().filter(t -> t.getPId().equals(lzzCkParam.getId())).filter(t -> t.getParamName().equals("流量")).collect(Collectors.toList()).get(0);
                    TrendsTableParam lzzParam = lzzParamList.stream().filter(t -> t.getParamName().equals("河道") && t.getPId().equals(lzzLlParam.getId())).collect(Collectors.toList()).get(0);
                    Double lzz = lzzService.selectList(sdf.format(dkl.getRecordTime())).getData().get("08:00").
                            stream().filter(t -> t.getTableHeadId().equals(lzzParam.getId())).filter(t->t.getV()!=null).map(DayWaterSituationStatisticsTableLzz::getV).reduce(Double::sum).orElse(0.00);
                    List<TrendsTableParam> tthParamList = trendsTableParamList.stream().filter(t->t.getUseStation().equals("头屯河水库")).filter(t->t.getUseType()==1).collect(Collectors.toList());
                    TrendsTableParam tthJkParam = tthParamList.stream().filter(t -> t.getPId().equals("0")).filter(t -> t.getParamName().equals("进库流量")).collect(Collectors.toList()).get(0);
                    TrendsTableParam tthJkLlParam = tthParamList.stream().filter(t -> t.getPId().equals(tthJkParam.getId())).filter(t -> t.getParamName().equals("流量")).collect(Collectors.toList()).get(0);
                    TrendsTableParam tthParam = trendsTableParamList.stream().filter(t -> t.getParamName().equals("合计")).filter(t->t.getPId().equals(tthJkLlParam.getId())).collect(Collectors.toList()).get(0);
                    Double tth = tthService.selectList(sdf.format(dkl.getRecordTime())).getData().get("08:00").
                            stream().filter(t -> t.getTableHeadId().equals(tthParam.getId())).filter(t->t.getV()!=null).map(DayWaterSituationStatisticsTableTth::getV).reduce(Double::sum).orElse(0.00);
                    dkl.setV((lzz==null || lzz==0)?0.00:(tth/lzz)*100);
                }catch (Exception e){
                    e.printStackTrace();
                    return RestResponse.no("头屯河水库对口率生成参数缺失，请检查参数后再生成！");
                }
            }
            if(tableParam.getParamName().equals("头屯河-渠首")){
                try {
                    List<TrendsTableParam> tthParamList = trendsTableParamList.stream().filter(t->t.getUseStation().equals("头屯河水库")).filter(t->t.getUseType()==1).collect(Collectors.toList());
                    TrendsTableParam tthJkParam = tthParamList.stream().filter(t -> t.getPId().equals("0")).filter(t -> t.getParamName().equals("出库流量")).collect(Collectors.toList()).get(0);
                    TrendsTableParam hd = tthParamList.stream().filter(t -> t.getPId().equals(tthJkParam.getId())).filter(t -> t.getParamName().equals("河道流量")).collect(Collectors.toList()).get(0);
                    Double tth = tthService.selectList(sdf.format(dkl.getRecordTime())).getData().get("08:00").
                            stream().filter(t -> t.getTableHeadId().equals(hd.getId())).filter(t->t.getV()!=null).map(DayWaterSituationStatisticsTableTth::getV).reduce(Double::sum).orElse(0.00);
                    List<DayWaterSituationStatisticsTableQs> dayWaterSituationStatisticsTableQs = qsService.selectList(sdf.format(dkl.getRecordTime())).getData().get("08:00");
                    List<TrendsTableParam> qsParamList = trendsTableParamList.stream().filter(t->t.getUseStation().equals("渠首管理站")).filter(t->t.getUseType()==1).collect(Collectors.toList());
                    TrendsTableParam qhParam = qsParamList.stream().filter(t -> t.getParamName().equals("全河")).filter(t -> t.getPId().equals("0")).collect(Collectors.toList()).get(0);
                    Double qh = dayWaterSituationStatisticsTableQs.stream().filter(t -> t.getTableHeadId().equals(qhParam.getId())).filter(t->t.getV()!=null).map(DayWaterSituationStatisticsTableQs::getV).reduce(Double::sum).orElse(0.00);
                    dkl.setV((tth==null || tth==0.00)?0.00:(qh/tth)*100);
                }catch (Exception e){
                    e.printStackTrace();
                    return RestResponse.no("渠首管理站对口率生成参数缺失，请检查参数后再生成！");
                }
            }
            if(tableParam.getParamName().equals("渠首-河东")){
                try {
                    TrendsTableParam dgqParamList = trendsTableParamList.stream().filter(t->t.getUseStation().equals("渠首管理站")).filter(t->t.getUseType()==1).collect(Collectors.toList())
                            .stream().filter(t -> t.getParamName().equals("东岸")).filter(t -> t.getPId().equals("0")).collect(Collectors.toList()).get(0);
                    TrendsTableParam dgqParamTemp = trendsTableParamList.stream().filter(t->t.getParamName().equals("东干渠")).filter(t->t.getPId().equals(dgqParamList.getId())).collect(Collectors.toList()).get(0);
                    TrendsTableParam dgqParam = trendsTableParamList.stream().filter(t->t.getParamName().equals("东干渠流量")).filter(t->t.getPId().equals(dgqParamTemp.getId())).collect(Collectors.toList()).get(0);
                    Double dgq = qsService.selectList(sdf.format(dkl.getRecordTime())).getData().get("08:00").stream().filter(t -> t.getTableHeadId().equals(dgqParam.getId())).filter(t->t.getV()!=null).
                            map(DayWaterSituationStatisticsTableQs::getV).reduce(Double::sum).orElse(0.00);
                    TrendsTableParam hdParam = trendsTableParamList.stream().filter(t->t.getUseStation().equals("河东管理站")).filter(t->t.getUseType()==1).collect(Collectors.toList())
                            .stream().filter(t->t.getParamName().equals("合计")).filter(t->t.getPId().equals("0")).collect(Collectors.toList()).get(0);
                    Double hd = hdService.selectList(sdf.format(dkl.getRecordTime())).getData().get("08:00").stream().filter(t -> t.getTableHeadId().equals(hdParam.getId())).filter(t->t.getV()!=null).
                            map(DayWaterSituationStatisticsTableHd::getV).reduce(Double::sum).orElse(0.00);
                    dkl.setV((dgq==null || dgq ==0.00)?0.00:(hd/dgq)*100);
                }catch (Exception e){
                    e.printStackTrace();
                    return RestResponse.no("河东管理站对口率生成参数缺失，请检查参数后再生成！");
                }
            }
            if(tableParam.getParamName().equals("渠首-河西")){
                try {
                    TrendsTableParam xgqParamList = trendsTableParamList.stream().filter(t->t.getUseStation().equals("渠首管理站")).filter(t->t.getUseType()==1).collect(Collectors.toList())
                            .stream().filter(t -> t.getParamName().equals("西岸")).filter(t -> t.getPId().equals("0")).collect(Collectors.toList()).get(0);
                    TrendsTableParam xgqParam = trendsTableParamList.stream().filter(t->t.getParamName().equals("西干渠流量")).filter(t->t.getPId().equals(xgqParamList.getId())).collect(Collectors.toList()).get(0);
                    Double xgq = qsService.selectList(sdf.format(dkl.getRecordTime())).getData().get("08:00").stream().filter(t -> t.getTableHeadId().equals(xgqParam.getId())).filter(t->t.getV()!=null).
                            map(DayWaterSituationStatisticsTableQs::getV).reduce(Double::sum).orElse(0.00);
                    TrendsTableParam hxParam = trendsTableParamList.stream().filter(t->t.getUseStation().equals("河西管理站")).filter(t->t.getUseType()==1).collect(Collectors.toList())
                            .stream().filter(t->t.getParamName().equals("合计")).filter(t->t.getPId().equals("0")).collect(Collectors.toList()).get(0);
                    Double hx = hxService.selectList(sdf.format(dkl.getRecordTime())).getData().get("08:00").stream().filter(t -> t.getTableHeadId().equals(hxParam.getId())).filter(t->t.getV()!=null).
                            map(DayWaterSituationStatisticsTableHx::getV).reduce(Double::sum).orElse(0.00);
                    dkl.setV((xgq==null || xgq ==0.00)?0.00:(hx/xgq)*100);
                }catch (Exception e){
                    e.printStackTrace();
                    return RestResponse.no("河西管理站对口率生成参数缺失，请检查参数后再生成！");
                }
            }
        }
        result.addAll(dayWaterSituationStatisticsTableDklList);
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
        boolean remove = this.lambdaUpdate().in(DayWaterSituationStatisticsTableDkl::getId, collect).remove();
        if (remove) {
            return RestResponse.ok();
        }else {
            return RestResponse.no("error");
        }
    }

    @Override
    public RestResponse update(List<DayWaterSituationStatisticsTableDkl> dayWaterSituationStatisticsTableDklList) {
        String mk = (String) redisUtil.get("trendsTableParam:list");
        if(StringUtils.isEmpty(mk)){
            trendsTableParamService.updateCache();
            mk = (String) redisUtil.get("trendsTableParam:list");
        }
        List<TrendsTableParam> trendsTableParamListTemp = JSONObject.parseArray(mk, TrendsTableParam.class);
        List<TrendsTableParam> trendsTableParamList = trendsTableParamListTemp.stream().filter(t -> t.getUseType() == 1).collect(Collectors.toList());
        List<TotalIdToStation> totalIdToStationList = totalIdToStationService.lambdaQuery().eq(TotalIdToStation::getUseType, 1).eq(TotalIdToStation::getStation, "对口率").list();
        //计算行合计
        if(null != totalIdToStationList && totalIdToStationList.size()>0){
            Double total = 0.0;
            List<String> totalCollect = totalIdToStationList.stream().filter(t->t.getName().equals("合计")).map(TotalIdToStation::getTotalId).collect(Collectors.toList());
            for(String id:totalCollect){
                Double value = 0.0;
                TrendsTableParam tableParam = JSONObject.parseObject((String)redisUtil.get("trendsTableParam:object:"+id),TrendsTableParam.class);
                if(!tableParam.getPId().equals("0")){
                    List<TrendsTableParam> noTotalList = trendsTableParamList.stream().filter(t->t.getPId().equals(tableParam.getPId())).filter(t->!t.getParamName().equals("合计")).collect(Collectors.toList());
                    for(TrendsTableParam param:noTotalList){
                        for(DayWaterSituationStatisticsTableDkl t:dayWaterSituationStatisticsTableDklList){
                            if(t.getTableHeadId().equals(param.getId())){
                                value+=t.getV()==null?0.0:t.getV();
                            }
                            List<TrendsTableParam> listed = trendsTableParamList.stream().filter(p->p.getPId().equals(param.getId())).filter(p->!p.getParamName().equals("合计")).collect(Collectors.toList());
                            if(null != listed && listed.size()>0){
                                for (TrendsTableParam param1:listed){
                                    if(t.getTableHeadId().equals(param1.getId())){
                                        value+=t.getV()==null?0.0:t.getV();
                                    }
                                }
                            }
                        }
                    }
                    for(DayWaterSituationStatisticsTableDkl t:dayWaterSituationStatisticsTableDklList){
                        if(t.getTableHeadId().equals(id)){
                            t.setV(value);
                        }
                    }
                }
            }
            for(DayWaterSituationStatisticsTableDkl t:dayWaterSituationStatisticsTableDklList){
                if(!totalIdToStationList.stream().map(TotalIdToStation::getTotalId).collect(Collectors.toList()).contains(t.getTableHeadId())){
                    total+=t.getV()==null?0.0:t.getV();
                }
            }
            TrendsTableParam one =null;
            List<TrendsTableParam> collect1 = trendsTableParamList.stream().filter(t -> t.getPId().equals("0")).filter(t -> t.getUseType() == 1).collect(Collectors.toList());
            for(TrendsTableParam param : collect1){
                for(String s:totalCollect){
                    if(param.getId().equals(s)){
                        one = param;
                    }
                }
            }
            if(null != one){
                for(DayWaterSituationStatisticsTableDkl t:dayWaterSituationStatisticsTableDklList){
                    if(t.getTableHeadId().equals(one.getId())){
                        t.setV(total);
                    }
                }
            }
        }
        boolean b = this.updateBatchById(dayWaterSituationStatisticsTableDklList);
        if (b) {
            if (dayWaterSituationStatisticsTableDklList.get(0).getTime().equals("今日均")){
                updateYesterdayData(dayWaterSituationStatisticsTableDklList.get(0).getRecordTime(),dayWaterSituationStatisticsTableDklList);
            }
            return RestResponse.ok();
        }else {
            return RestResponse.no("error");
        }
    }

    @Override
    public RestResponse insertTodayMeanValue(Date date,String time) {
        List<DayWaterSituationStatisticsTableDkl> dayWaterSituationStatisticsTableDkls = this.baseMapper.selectList(sdf.format(date));
        List<DayWaterSituationStatisticsTableDkl> todayData = dayWaterSituationStatisticsTableDkls.stream().filter(t -> t.getTime().equals(time)).collect(Collectors.toList());
        if(!todayData.isEmpty()){
            log.warn("******************************************************对口率"+time+"数据已创建");
            return RestResponse.no(time+"数据已创建");
        }
        List<DayWaterSituationStatisticsTableDkl> dayWaterSituationStatisticsTableDklList = new ArrayList<>();
        if(null!=dayWaterSituationStatisticsTableDkls && dayWaterSituationStatisticsTableDkls.size()>0){
            DayWaterSituationStatisticsTableDkl dayWaterSituationStatisticsTableDkl = dayWaterSituationStatisticsTableDkls.get(0);
            String endTableList = dayWaterSituationStatisticsTableDkl.getEndTableList();
            String[] split = endTableList.split(",");
            for(String t :split){
                DayWaterSituationStatisticsTableDkl dkl = new DayWaterSituationStatisticsTableDkl();
                dkl.setId(UUIDUtils.getUUID());
                dkl.setV(null);
                dkl.setTime(time);
                try {
                    dkl.setRecordTime(sdf.parse(sdf.format(date)));
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                dkl.setTableHeadId(t);
                dkl.setFrontTableList(dayWaterSituationStatisticsTableDkl.getFrontTableList());
                dkl.setEndTableList(dayWaterSituationStatisticsTableDkl.getEndTableList());
                dayWaterSituationStatisticsTableDklList.add(dkl);
            }
        }
        if(dayWaterSituationStatisticsTableDklList.isEmpty()){
            log.warn("****************************************************************"+time+"无数据");
            return RestResponse.no(time+"无数据");
        }
        String mk = (String) redisUtil.get("trendsTableParam:list");
        if(StringUtils.isEmpty(mk)){
            trendsTableParamService.updateCache();
            mk = (String) redisUtil.get("trendsTableParam:list");
        }
        List<TrendsTableParam> trendsTableParamListTemp = JSONObject.parseArray(mk, TrendsTableParam.class);
        List<TrendsTableParam> trendsTableParamList = trendsTableParamListTemp.stream().filter(t -> t.getUseType() == 1).collect(Collectors.toList());
        //计算对口率
        for(DayWaterSituationStatisticsTableDkl dkl:dayWaterSituationStatisticsTableDklList){
            TrendsTableParam tableParam = JSONObject.parseObject((String)redisUtil.get("trendsTableParam:object:"+dkl.getTableHeadId()),TrendsTableParam.class);
            if(tableParam.getParamName().equals("楼庄子-头屯河")){
                try {
                    List<TrendsTableParam> lzzParamList = trendsTableParamList.stream().filter(t->t.getUseStation().equals("楼庄子水库")).filter(t->t.getUseType()==1).collect(Collectors.toList());
                    TrendsTableParam lzzCkParam = lzzParamList.stream().filter(t -> t.getPId().equals("0") &&t.getParamName().equals("出库")).collect(Collectors.toList()).get(0);
                    TrendsTableParam lzzLlParam = lzzParamList.stream().filter(t -> t.getPId().equals(lzzCkParam.getId())).filter(t -> t.getParamName().equals("流量")).collect(Collectors.toList()).get(0);
                    TrendsTableParam lzzParam = lzzParamList.stream().filter(t -> t.getParamName().equals("河道") && t.getPId().equals(lzzLlParam.getId())).collect(Collectors.toList()).get(0);
                    List<DayWaterSituationStatisticsTableLzz> lzzToday = lzzService.selectList(sdf.format(dkl.getRecordTime())).getData().get(time);
                    Double lzz = (lzzToday.size()<0 || lzzToday.size()==0)?0.00:lzzToday.stream().filter(t -> t.getTableHeadId().equals(lzzParam.getId())).filter(t->t.getV()!=null).
                            map(DayWaterSituationStatisticsTableLzz::getV).reduce(Double::sum).orElse(0.00);
                    List<TrendsTableParam> tthParamList = trendsTableParamList.stream().filter(t->t.getUseStation().equals("头屯河水库")).filter(t->t.getUseType()==1).collect(Collectors.toList());
                    TrendsTableParam tthJkParam = tthParamList.stream().filter(t -> t.getPId().equals("0")).filter(t -> t.getParamName().equals("进库流量")).collect(Collectors.toList()).get(0);
                    TrendsTableParam tthJkLlParam = tthParamList.stream().filter(t -> t.getPId().equals(tthJkParam.getId())).filter(t -> t.getParamName().equals("流量")).collect(Collectors.toList()).get(0);
                    TrendsTableParam tthParam = trendsTableParamList.stream().filter(t -> t.getParamName().equals("合计")).filter(t->t.getPId().equals(tthJkLlParam.getId())).collect(Collectors.toList()).get(0);
                    List<DayWaterSituationStatisticsTableTth> tthToday = tthService.selectList(sdf.format(dkl.getRecordTime())).getData().get(time);
                    Double tth = (tthToday.size()<0 || tthToday.size()==0)?0.00:tthToday.stream().filter(t -> t.getTableHeadId().equals(tthParam.getId())).filter(t->t.getV()!=null).
                            map(DayWaterSituationStatisticsTableTth::getV).reduce(Double::sum).orElse(0.00);
                    dkl.setV((lzz==null || lzz==0)?0.00:(tth/lzz)*100);
                }catch (Exception e){
                    e.printStackTrace();
                    log.error("头屯河水库对口率生成参数缺失，请检查参数后再生成！");
                }
            }
            if(tableParam.getParamName().equals("头屯河-渠首")){
                try {
                    List<TrendsTableParam> tthParamList = trendsTableParamList.stream().filter(t->t.getUseStation().equals("头屯河水库")).filter(t->t.getUseType()==1).collect(Collectors.toList());
                    TrendsTableParam tthJkParam = tthParamList.stream().filter(t -> t.getPId().equals("0")).filter(t -> t.getParamName().equals("出库流量")).collect(Collectors.toList()).get(0);
                    TrendsTableParam hd = tthParamList.stream().filter(t -> t.getPId().equals(tthJkParam.getId())).filter(t -> t.getParamName().equals("河道流量")).collect(Collectors.toList()).get(0);
                    List<DayWaterSituationStatisticsTableTth> tthToday = tthService.selectList(sdf.format(dkl.getRecordTime())).getData().get(time);
                    Double tth = (tthToday.size()<0 || tthToday.size()==0)?0.00:tthToday.stream().filter(t -> t.getTableHeadId().equals(hd.getId())).filter(t->t.getV()!=null).
                            map(DayWaterSituationStatisticsTableTth::getV).reduce(Double::sum).orElse(0.00);
                    List<DayWaterSituationStatisticsTableQs> qsToday = qsService.selectList(sdf.format(dkl.getRecordTime())).getData().get(time);
                    List<TrendsTableParam> qsParamList = trendsTableParamList.stream().filter(t->t.getUseStation().equals("渠首管理站")).filter(t->t.getUseType()==1).collect(Collectors.toList());
                    TrendsTableParam qhParam = qsParamList.stream().filter(t -> t.getParamName().equals("全河")).filter(t -> t.getPId().equals("0")).collect(Collectors.toList()).get(0);
                    Double qh = (qsToday.size()<0 || qsToday.size()==0)?0.00:qsToday.stream().filter(t -> t.getTableHeadId().equals(qhParam.getId())).filter(t->t.getV()!=null).
                            map(DayWaterSituationStatisticsTableQs::getV).reduce(Double::sum).orElse(0.00);
                    dkl.setV((qh==null || qh==0.00)?0.00:(qh/tth)*100);
                }catch (Exception e){
                    e.printStackTrace();
                    log.error("渠首管理站对口率生成参数缺失，请检查参数后再生成！");
                }
            }
            if(tableParam.getParamName().equals("渠首-河东")){
                try {
                    TrendsTableParam dgqParamList = trendsTableParamList.stream().filter(t->t.getUseStation().equals("渠首管理站")).filter(t->t.getUseType()==1).collect(Collectors.toList())
                            .stream().filter(t -> t.getParamName().equals("东岸")).filter(t -> t.getPId().equals("0")).collect(Collectors.toList()).get(0);
                    TrendsTableParam dgqParamTemp = trendsTableParamList.stream().filter(t->t.getParamName().equals("东干渠")).filter(t->t.getPId().equals(dgqParamList.getId())).collect(Collectors.toList()).get(0);
                    TrendsTableParam dgqParam = trendsTableParamList.stream().filter(t->t.getParamName().equals("东干渠流量")).filter(t->t.getPId().equals(dgqParamTemp.getId())).collect(Collectors.toList()).get(0);
                    List<DayWaterSituationStatisticsTableQs> dgqToday = qsService.selectList(sdf.format(dkl.getRecordTime())).getData().get(time);
                    Double dgq =(dgqToday.size()<0 || dgqToday.size()==0)?0.00:dgqToday.stream().filter(t -> t.getTableHeadId().equals(dgqParam.getId())).filter(t->t.getV()!=null).
                            map(DayWaterSituationStatisticsTableQs::getV).reduce(Double::sum).orElse(0.00);
                    TrendsTableParam hdParam = trendsTableParamList.stream().filter(t->t.getUseStation().equals("河东管理站")).filter(t->t.getUseType()==1).collect(Collectors.toList())
                            .stream().filter(t->t.getParamName().equals("合计")).filter(t->t.getPId().equals("0")).collect(Collectors.toList()).get(0);
                    List<DayWaterSituationStatisticsTableHd> hdToday = hdService.selectList(sdf.format(dkl.getRecordTime())).getData().get(time);
                    Double hd =(hdToday.size()<0 || hdToday.size()==0)?0.00:hdToday.stream().filter(t -> t.getTableHeadId().equals(hdParam.getId())).filter(t->t.getV()!=null).
                            map(DayWaterSituationStatisticsTableHd::getV).reduce(Double::sum).orElse(0.00);
                    dkl.setV((dgq==null || dgq ==0.00)?0.00:(hd/dgq)*100);
                }catch (Exception e){
                    e.printStackTrace();
                    log.error("河东管理站对口率生成参数缺失，请检查参数后再生成！");
                }
            }
            if(tableParam.getParamName().equals("渠首-河西")){
                try {
                    TrendsTableParam xgqParamList = trendsTableParamList.stream().filter(t->t.getUseStation().equals("渠首管理站")).filter(t->t.getUseType()==1).collect(Collectors.toList())
                            .stream().filter(t -> t.getParamName().equals("西岸")).filter(t -> t.getPId().equals("0")).collect(Collectors.toList()).get(0);
                    TrendsTableParam xgqParam = trendsTableParamList.stream().filter(t->t.getParamName().equals("西干渠流量")).filter(t->t.getPId().equals(xgqParamList.getId())).collect(Collectors.toList()).get(0);
                    List<DayWaterSituationStatisticsTableQs> xgqToday = qsService.selectList(sdf.format(dkl.getRecordTime())).getData().get(time);
                    Double xgq =(xgqToday.size()<0 || xgqToday.size()==0)?0.00:xgqToday.stream().filter(t -> t.getTableHeadId().equals(xgqParam.getId())).filter(t->t.getV()!=null).
                            map(DayWaterSituationStatisticsTableQs::getV).reduce(Double::sum).orElse(0.00);
                    TrendsTableParam hxParam = trendsTableParamList.stream().filter(t->t.getUseStation().equals("河西管理站")).filter(t->t.getUseType()==1).collect(Collectors.toList())
                            .stream().filter(t->t.getParamName().equals("合计")).filter(t->t.getPId().equals("0")).collect(Collectors.toList()).get(0);
                    List<DayWaterSituationStatisticsTableHx> hxToday = hxService.selectList(sdf.format(dkl.getRecordTime())).getData().get(time);
                    Double hx =(hxToday.size()<0 || hxToday.size()==0)?0.00:hxToday.stream().filter(t -> t.getTableHeadId().equals(hxParam.getId())).filter(t->t.getV()!=null).
                            map(DayWaterSituationStatisticsTableHx::getV).reduce(Double::sum).orElse(0.00);
                    dkl.setV((xgq==null || xgq ==0.00)?0.00:(hx/xgq)*100);
                }catch (Exception e){
                    e.printStackTrace();
                    log.error("河西管理站对口率生成参数缺失，请检查参数后再生成！");
                }
            }
        }
        boolean b = this.saveBatch(dayWaterSituationStatisticsTableDklList);
        if (b) {
            return RestResponse.ok();
        }else {
            return RestResponse.no("error");
        }
    }

    private void updateYesterdayData(Date now,List<DayWaterSituationStatisticsTableDkl> dklList){
        List<DayWaterSituationStatisticsTableDkl> dayWaterSituationStatisticsTableDklList = this.baseMapper.selectInfoAfterDayList(getDate(now,1));
        if(!dayWaterSituationStatisticsTableDklList.isEmpty()){
            dayWaterSituationStatisticsTableDklList.forEach(t->{
                t.setV(dklList.stream().filter(p->p.getTableHeadId().equals(t.getTableHeadId()) && p.getV() !=null).map(DayWaterSituationStatisticsTableDkl::getV).reduce(Double::sum).orElse(0.00));
            });
            this.updateBatchById(dayWaterSituationStatisticsTableDklList);
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

