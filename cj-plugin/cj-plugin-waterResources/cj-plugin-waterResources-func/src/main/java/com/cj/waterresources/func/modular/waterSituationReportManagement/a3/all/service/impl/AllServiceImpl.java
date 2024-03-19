package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.cj.common.model.RestResponse;
import com.cj.common.util.RedisUtil;
import com.cj.waterresources.func.modular.trendsTable.entity.TrendsTableParam;
import com.cj.waterresources.func.modular.trendsTable.service.TrendsTableParamService;
import com.cj.waterresources.func.modular.waterSituationDataMaintenance.bean.req.SelectInfoListNewReq;
import com.cj.waterresources.func.modular.waterSituationDataMaintenance.bean.req.SelectInfoListReq;
import com.cj.waterresources.func.modular.waterSituationDataMaintenance.bean.res.HydrographRes;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.bean.req.A3StatisticsReq;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.bean.req.SelectListForIndustrialWaterFeeReq;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.bean.res.A3StatisticsRes;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.bean.res.SelectListForIndustrialWaterFeeRes;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.service.AllService;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.dkl.mapper.DayWaterSituationStatisticsTableDklMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.hd.entity.DayWaterSituationStatisticsTableHd;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.hd.mapper.DayWaterSituationStatisticsTableHdMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.hx.entity.DayWaterSituationStatisticsTableHx;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.hx.mapper.DayWaterSituationStatisticsTableHxMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.lzz.entity.DayWaterSituationStatisticsTableLzz;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.lzz.mapper.DayWaterSituationStatisticsTableLzzMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.qs.entity.DayWaterSituationStatisticsTableQs;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.qs.mapper.DayWaterSituationStatisticsTableQsMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.syyl.mapper.DayWaterSituationStatisticsTableSyylMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.tjc.mapper.DayWaterSituationStatisticsTableTjcMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.tth.entity.DayWaterSituationStatisticsTableTth;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.tth.mapper.DayWaterSituationStatisticsTableTthMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.zcc.mapper.DayWaterSituationStatisticsTableZccMapper;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AllServiceImpl implements AllService {

    private final DayWaterSituationStatisticsTableDklMapper dayWaterSituationStatisticsTableDklMapper;
    private final DayWaterSituationStatisticsTableHdMapper dayWaterSituationStatisticsTableHdMapper;
    private final DayWaterSituationStatisticsTableHxMapper dayWaterSituationStatisticsTableHxMapper;
    private final DayWaterSituationStatisticsTableLzzMapper dayWaterSituationStatisticsTableLzzMapper;
    private final DayWaterSituationStatisticsTableQsMapper dayWaterSituationStatisticsTableQsMapper;
    private final DayWaterSituationStatisticsTableSyylMapper dayWaterSituationStatisticsTableSyylMapper;
    private final DayWaterSituationStatisticsTableTjcMapper dayWaterSituationStatisticsTableTjcMapper;
    private final DayWaterSituationStatisticsTableTthMapper dayWaterSituationStatisticsTableTthMapper;
    private final DayWaterSituationStatisticsTableZccMapper dayWaterSituationStatisticsTableZccMapper;
    private final RedisUtil redisUtil;
    private final TrendsTableParamService trendsTableParamService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse deleteAll(String date) {
        try {
            dayWaterSituationStatisticsTableDklMapper.deleteByTime(date);
            dayWaterSituationStatisticsTableHdMapper.deleteByTime(date);
            dayWaterSituationStatisticsTableHxMapper.deleteByTime(date);
            dayWaterSituationStatisticsTableLzzMapper.deleteByTime(date);
            dayWaterSituationStatisticsTableQsMapper.deleteByTime(date);
            dayWaterSituationStatisticsTableSyylMapper.deleteByTime(date);
            dayWaterSituationStatisticsTableTjcMapper.deleteByTime(date);
            dayWaterSituationStatisticsTableTthMapper.deleteByTime(date);
            dayWaterSituationStatisticsTableZccMapper.deleteByTime(date);
            return RestResponse.ok();
        } catch (Exception e){
            return RestResponse.no("error");
        }
    }

    @Override
    public RestResponse statistics(A3StatisticsReq req) {
        String mk = (String) redisUtil.get("trendsTableParam:list");
        if(StringUtils.isEmpty(mk)){
            trendsTableParamService.updateCache();
            mk = (String) redisUtil.get("trendsTableParam:list");
        }
        List<TrendsTableParam> trendsTableParamList = JSONObject.parseArray(mk, TrendsTableParam.class);
        if(req.getStation().equals("楼庄子水库")){
            List<A3StatisticsRes> statistics = dayWaterSituationStatisticsTableLzzMapper.getStatistics(req);
            if(null != statistics && statistics.size()>0){
                Map<String, List<A3StatisticsRes>> collect = statistics.stream().collect(Collectors.groupingBy(A3StatisticsRes::getParamName));
                return RestResponse.ok(change(collect));
            }else {
                return RestResponse.no("blank");
            }
        }
        if(req.getStation().equals("头屯河水库")){
            List<A3StatisticsRes> statistics = dayWaterSituationStatisticsTableTthMapper.getStatistics(req);
            if(null != statistics && statistics.size()>0){
                Map<String, List<A3StatisticsRes>> collect = statistics.stream().collect(Collectors.groupingBy(A3StatisticsRes::getParamName));
                return RestResponse.ok(change(collect));
            }else {
                return RestResponse.no("blank");
            }
        }
        if(req.getStation().equals("渠首管理站")){
            List<A3StatisticsRes> statistics = dayWaterSituationStatisticsTableQsMapper.getStatistics(req);
            if(null != statistics && statistics.size()>0){
                Map<String, List<A3StatisticsRes>> collect = statistics.stream().collect(Collectors.groupingBy(A3StatisticsRes::getParamName));
                return RestResponse.ok(change(collect));
            }else {
                return RestResponse.no("blank");
            }
        }
        if(req.getStation().equals("河东管理站")){
            if(StringUtils.isEmpty(req.getUnit())){
                Map<String,List<String>> selectMap = new HashMap<>();
                for(String id: req.getIds()){
                    List<String> selectId = new ArrayList<>();
                    List<TrendsTableParam> collect = trendsTableParamList.stream().filter(t -> t.getPId().equals(id)).collect(Collectors.toList());
                    if(collect.size()>1){
                        selectId.add(collect.stream().filter(t -> t.getParamName().equals("合计")).map(TrendsTableParam::getId).collect(Collectors.toList()).get(0));
                    }else {
                        if(collect.size()>0){
                            selectId.add(collect.get(0).getId());
                        }
                    }
                    selectMap.put((String) redisUtil.get("trendsTableParam:name:"+id),selectId);
                }
                Map<String, List<A3StatisticsRes>> change = change(selectHdAndChange(selectMap, req));
                if(change.size()>0){
                    return RestResponse.ok(change(change));
                }else {
                    return RestResponse.no("blank");
                }
            }else {
                List<A3StatisticsRes> statistics = dayWaterSituationStatisticsTableHdMapper.getStatistics(req);
                if(null != statistics && statistics.size()>0){
                    Map<String, List<A3StatisticsRes>> collect = statistics.stream().collect(Collectors.groupingBy(A3StatisticsRes::getParamName));
                    return RestResponse.ok(change(collect));
                }else {
                    return RestResponse.no("blank");
                }
            }
        }
        if(req.getStation().equals("河西管理站")){
            if(StringUtils.isEmpty(req.getUnit())){
                Map<String,List<String>> selectMap = new HashMap<>();
                for(String id: req.getIds()){
                    List<String> selectId = new ArrayList<>();
                    List<TrendsTableParam> collect = trendsTableParamList.stream().filter(t -> t.getPId().equals(id)).collect(Collectors.toList());
                    if(collect.size()>1){
                        selectId.add(collect.stream().filter(t -> t.getParamName().equals("合计")).map(TrendsTableParam::getId).collect(Collectors.toList()).get(0));
                    }else {
                        if(collect.size()>0){
                            selectId.add(collect.get(0).getId());
                        }
                    }
                    selectMap.put((String) redisUtil.get("trendsTableParam:name:"+id),selectId);
                }
                Map<String, List<A3StatisticsRes>> change = change(selectHxAndChange(selectMap, req));
                if(change.size()>0){
                    return RestResponse.ok(change(change));
                }else {
                    return RestResponse.no("blank");
                }
            }else {
                List<A3StatisticsRes> statistics = dayWaterSituationStatisticsTableHxMapper.getStatistics(req);
                if(null != statistics && statistics.size()>0){
                    Map<String, List<A3StatisticsRes>> collect = statistics.stream().collect(Collectors.groupingBy(A3StatisticsRes::getParamName));
                    return RestResponse.ok(change(collect));
                }else {
                    return RestResponse.no("blank");
                }
            }
        }
        return null;
    }

    @Override
    public RestResponse selectInfoList(SelectInfoListReq req) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<HydrographRes> hydrographResList = new ArrayList<>();
        if(StringUtils.isEmpty(req.getTreeName())){
            return RestResponse.no("暂无数据");
        }
        String mk = (String) redisUtil.get("trendsTableParam:list");
        if(StringUtils.isEmpty(mk)){
            trendsTableParamService.updateCache();
            mk = (String) redisUtil.get("trendsTableParam:list");
        }
        List<TrendsTableParam> trendsTableParamList = JSONObject.parseArray(mk, TrendsTableParam.class);
        List<TrendsTableParam> collect = trendsTableParamList.stream().filter(t -> t.getParamName().equals(req.getTreeName())).collect(Collectors.toList());
        if(collect.isEmpty()){
            return RestResponse.no("暂无监测点数据");
        }
        TrendsTableParam trendsTableParam = collect.get(0);
        if(trendsTableParam.getUseStation().equals("河东管理站")){
            List<DayWaterSituationStatisticsTableHd> dayWaterSituationStatisticsTableHds = dayWaterSituationStatisticsTableHdMapper.selectList2(trendsTableParam.getId(), req.getStartTime(), req.getEndTime());
            if(dayWaterSituationStatisticsTableHds.isEmpty()){
                return RestResponse.no("暂无数据");
            }else {
                dayWaterSituationStatisticsTableHds.forEach(t->{
                    HydrographRes res = new HydrographRes();
                    res.setName((String)redisUtil.get("trendsTableParam:name:"+t.getTableHeadId()));
                    res.setFlow(t.getV());
                    res.setTime(sdf.format(t.getRecordTime()));
                    hydrographResList.add(res);
                });
            }
        }
        if(trendsTableParam.getUseStation().equals("河西管理站")){
            List<DayWaterSituationStatisticsTableHx> dayWaterSituationStatisticsTableHxes = dayWaterSituationStatisticsTableHxMapper.selectList2(trendsTableParam.getId(), req.getStartTime(), req.getEndTime());
            if(dayWaterSituationStatisticsTableHxes.isEmpty()){
                return RestResponse.no("暂无数据");
            }else {
                dayWaterSituationStatisticsTableHxes.forEach(t->{
                    HydrographRes res = new HydrographRes();
                    res.setName((String)redisUtil.get("trendsTableParam:name:"+t.getTableHeadId()));
                    res.setFlow(t.getV());
                    res.setTime(sdf.format(t.getRecordTime()));
                    hydrographResList.add(res);
                });
            }
        }
        if(trendsTableParam.getUseStation().equals("渠首管理站")){
            List<DayWaterSituationStatisticsTableQs> dayWaterSituationStatisticsTableQs = dayWaterSituationStatisticsTableQsMapper.selectList2(trendsTableParam.getId(), req.getStartTime(), req.getEndTime());
            if(dayWaterSituationStatisticsTableQs.isEmpty()){
                return RestResponse.no("暂无数据");
            }else {
                dayWaterSituationStatisticsTableQs.forEach(t->{
                    HydrographRes res = new HydrographRes();
                    res.setName((String)redisUtil.get("trendsTableParam:name:"+t.getTableHeadId()));
                    res.setFlow(t.getV());
                    res.setTime(sdf.format(t.getRecordTime()));
                    hydrographResList.add(res);
                });
            }
        }
        if(trendsTableParam.getUseStation().equals("头屯河水库")){
            List<DayWaterSituationStatisticsTableTth> dayWaterSituationStatisticsTableTths = dayWaterSituationStatisticsTableTthMapper.selectList2(trendsTableParam.getId(), req.getStartTime(), req.getEndTime());
            if(dayWaterSituationStatisticsTableTths.isEmpty()){
                return RestResponse.no("暂无数据");
            }else {
                dayWaterSituationStatisticsTableTths.forEach(t->{
                    HydrographRes res = new HydrographRes();
                    res.setName((String)redisUtil.get("trendsTableParam:name:"+t.getTableHeadId()));
                    res.setFlow(t.getV());
                    res.setTime(sdf.format(t.getRecordTime()));
                    hydrographResList.add(res);
                });
            }
        }
        if(trendsTableParam.getUseStation().equals("楼庄子水库")){
            List<DayWaterSituationStatisticsTableLzz> dayWaterSituationStatisticsTableLzzes = dayWaterSituationStatisticsTableLzzMapper.selectList2(trendsTableParam.getId(), req.getStartTime(), req.getEndTime());
            if(dayWaterSituationStatisticsTableLzzes.isEmpty()){
                return RestResponse.no("暂无数据");
            }else {
                dayWaterSituationStatisticsTableLzzes.forEach(t->{
                    HydrographRes res = new HydrographRes();
                    res.setName((String)redisUtil.get("trendsTableParam:name:"+t.getTableHeadId()));
                    res.setFlow(t.getV());
                    res.setTime(sdf.format(t.getRecordTime()));
                    hydrographResList.add(res);
                });
            }
        }
        if(hydrographResList.isEmpty()){
            return RestResponse.no("暂无数据");
        }
        return RestResponse.ok(hydrographResList);
    }

    @Override
    public List<HydrographRes> selectInfoListAllNew(SelectInfoListNewReq req) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<HydrographRes> hydrographResList = new ArrayList<>();
        if(StringUtils.isEmpty(req.getId())){
            return null;
        }
        String mk = (String) redisUtil.get("trendsTableParam:list");
        if(StringUtils.isEmpty(mk)){
            trendsTableParamService.updateCache();
            mk = (String) redisUtil.get("trendsTableParam:list");
        }
        List<TrendsTableParam> trendsTableParamList = JSONObject.parseArray(mk, TrendsTableParam.class);
        List<TrendsTableParam> collect = trendsTableParamList.stream().filter(t -> t.getUnitId().equals(req.getId())).collect(Collectors.toList());
        if(collect.isEmpty()){
            return null;
        }
        TrendsTableParam trendsTableParam = collect.get(0);
        if(trendsTableParam.getUseStation().equals("河东管理站")){
            List<DayWaterSituationStatisticsTableHd> dayWaterSituationStatisticsTableHds = dayWaterSituationStatisticsTableHdMapper.selectList2(trendsTableParam.getId(), req.getStartTime(), req.getEndTime());
            if(dayWaterSituationStatisticsTableHds.isEmpty()){
                return null;
            }else {
                dayWaterSituationStatisticsTableHds.forEach(t->{
                    HydrographRes res = new HydrographRes();
                    res.setName((String)redisUtil.get("trendsTableParam:name:"+t.getTableHeadId()));
                    res.setFlow(t.getV());
                    res.setTime(sdf.format(t.getRecordTime()));
                    hydrographResList.add(res);
                });
            }
        }
        if(trendsTableParam.getUseStation().equals("河西管理站")){
            List<DayWaterSituationStatisticsTableHx> dayWaterSituationStatisticsTableHxes = dayWaterSituationStatisticsTableHxMapper.selectList2(trendsTableParam.getId(), req.getStartTime(), req.getEndTime());
            if(dayWaterSituationStatisticsTableHxes.isEmpty()){
                return null;
            }else {
                dayWaterSituationStatisticsTableHxes.forEach(t->{
                    HydrographRes res = new HydrographRes();
                    res.setName((String)redisUtil.get("trendsTableParam:name:"+t.getTableHeadId()));
                    res.setFlow(t.getV());
                    res.setTime(sdf.format(t.getRecordTime()));
                    hydrographResList.add(res);
                });
            }
        }
        if(trendsTableParam.getUseStation().equals("渠首管理站")){
            List<DayWaterSituationStatisticsTableQs> dayWaterSituationStatisticsTableQs = dayWaterSituationStatisticsTableQsMapper.selectList2(trendsTableParam.getId(), req.getStartTime(), req.getEndTime());
            if(dayWaterSituationStatisticsTableQs.isEmpty()){
                return null;
            }else {
                dayWaterSituationStatisticsTableQs.forEach(t->{
                    HydrographRes res = new HydrographRes();
                    res.setName((String)redisUtil.get("trendsTableParam:name:"+t.getTableHeadId()));
                    res.setFlow(t.getV());
                    res.setTime(sdf.format(t.getRecordTime()));
                    hydrographResList.add(res);
                });
            }
        }
        if(trendsTableParam.getUseStation().equals("头屯河水库")){
            List<DayWaterSituationStatisticsTableTth> dayWaterSituationStatisticsTableTths = dayWaterSituationStatisticsTableTthMapper.selectList2(trendsTableParam.getId(), req.getStartTime(), req.getEndTime());
            if(dayWaterSituationStatisticsTableTths.isEmpty()){
                return null;
            }else {
                dayWaterSituationStatisticsTableTths.forEach(t->{
                    HydrographRes res = new HydrographRes();
                    res.setName((String)redisUtil.get("trendsTableParam:name:"+t.getTableHeadId()));
                    res.setFlow(t.getV());
                    res.setTime(sdf.format(t.getRecordTime()));
                    hydrographResList.add(res);
                });
            }
        }
        if(trendsTableParam.getUseStation().equals("楼庄子水库")){
            List<DayWaterSituationStatisticsTableLzz> dayWaterSituationStatisticsTableLzzes = dayWaterSituationStatisticsTableLzzMapper.selectList2(trendsTableParam.getId(), req.getStartTime(), req.getEndTime());
            if(dayWaterSituationStatisticsTableLzzes.isEmpty()){
                return null;
            }else {
                dayWaterSituationStatisticsTableLzzes.forEach(t->{
                    HydrographRes res = new HydrographRes();
                    res.setName((String)redisUtil.get("trendsTableParam:name:"+t.getTableHeadId()));
                    res.setFlow(t.getV());
                    res.setTime(sdf.format(t.getRecordTime()));
                    hydrographResList.add(res);
                });
            }
        }
        if(hydrographResList.isEmpty()){
            return null;
        }
        return hydrographResList;
    }

    @Override
    public RestResponse selectListForIndustrialWaterFee(SelectListForIndustrialWaterFeeReq req) {
        String mk = (String) redisUtil.get("trendsTableParam:list");
        if(StringUtils.isEmpty(mk)){
            trendsTableParamService.updateCache();
            mk = (String) redisUtil.get("trendsTableParam:list");
        }
        List<SelectListForIndustrialWaterFeeRes> resList = new ArrayList<>();
        List<TrendsTableParam> trendsTableParamList = JSONObject.parseArray(mk, TrendsTableParam.class);
        if(req.getName().equals("楼庄子水厂")){
            List<DayWaterSituationStatisticsTableLzz> dayWaterSituationStatisticsTableLzzes = new ArrayList<>();
            List<TrendsTableParam> collect = trendsTableParamList.stream().filter(t -> t.getUseStation().equals("楼庄子水库") && t.getUseType() == 1).collect(Collectors.toList());
            TrendsTableParam dg1 = collect.stream().filter(t -> t.getParamName().equals("楼庄子水厂管道1")).collect(Collectors.toList()).get(0);
            TrendsTableParam dg2 = collect.stream().filter(t -> t.getParamName().equals("楼庄子水厂管道2")).collect(Collectors.toList()).get(0);
            req.setHeadIds(dg1.getId());
            List<DayWaterSituationStatisticsTableLzz> dayWaterSituationStatisticsTableLzzes1 = dayWaterSituationStatisticsTableLzzMapper.selectListForIndustrialWaterFee(req);
            req.setHeadIds(dg2.getId());
            List<DayWaterSituationStatisticsTableLzz> dayWaterSituationStatisticsTableLzzes2 = dayWaterSituationStatisticsTableLzzMapper.selectListForIndustrialWaterFee(req);
            dayWaterSituationStatisticsTableLzzes.addAll(dayWaterSituationStatisticsTableLzzes1);
            dayWaterSituationStatisticsTableLzzes.addAll(dayWaterSituationStatisticsTableLzzes2);
            Map<Date, List<DayWaterSituationStatisticsTableLzz>> collect1 = dayWaterSituationStatisticsTableLzzes.stream().collect(Collectors.groupingBy(DayWaterSituationStatisticsTableLzz::getRecordTime));
            Set<Date> dates = collect1.keySet();
            for(Date date:dates){
                SelectListForIndustrialWaterFeeRes res = new SelectListForIndustrialWaterFeeRes();
                res.setDate(date);
                res.setV(collect1.get(date).stream().map(DayWaterSituationStatisticsTableLzz::getV).reduce(Double::sum).orElse(0.00));
                resList.add(res);
            }
        }
        if(req.getName().equals("八钢")){
            List<TrendsTableParam> collect = trendsTableParamList.stream().filter(t -> t.getUseStation().equals("头屯河水库") && t.getUseType() == 1).collect(Collectors.toList());
            TrendsTableParam bg = collect.stream().filter(t -> t.getParamName().equals("八钢流量")).collect(Collectors.toList()).get(0);
            TrendsTableParam bgCount = collect.stream().filter(t -> t.getParamName().equals("合计") && t.getPId().equals(bg.getId())).collect(Collectors.toList()).get(0);
            req.setHeadId(bgCount.getId());
            List<DayWaterSituationStatisticsTableTth> dayWaterSituationStatisticsTableTths = dayWaterSituationStatisticsTableTthMapper.selectListForIndustrialWaterFee(req);
            for(DayWaterSituationStatisticsTableTth tth:dayWaterSituationStatisticsTableTths){
                SelectListForIndustrialWaterFeeRes res = new SelectListForIndustrialWaterFeeRes();
                res.setDate(tth.getRecordTime());
                res.setV(res.getV());
                resList.add(res);
            }
        }
        if(req.getName().equals("红岩水库")){
            List<TrendsTableParam> collect = trendsTableParamList.stream().filter(t -> t.getUseStation().equals("头屯河水库") && t.getUseType() == 1).collect(Collectors.toList());
            TrendsTableParam hy = collect.stream().filter(t -> t.getParamName().equals("红岩流量")).collect(Collectors.toList()).get(0);
            req.setHeadId(hy.getId());
            List<DayWaterSituationStatisticsTableTth> dayWaterSituationStatisticsTableTths = dayWaterSituationStatisticsTableTthMapper.selectListForIndustrialWaterFee(req);
            for(DayWaterSituationStatisticsTableTth tth:dayWaterSituationStatisticsTableTths){
                SelectListForIndustrialWaterFeeRes res = new SelectListForIndustrialWaterFeeRes();
                res.setDate(tth.getRecordTime());
                res.setV(res.getV());
                resList.add(res);
            }
        }
        if(resList.isEmpty()){
            return RestResponse.no("暂无流量数据");
        }else {
            return RestResponse.ok(resList);
        }
    }

    public Map<String, List<A3StatisticsRes>> change(Map<String, List<A3StatisticsRes>> collect){
        Map<String, List<A3StatisticsRes>> result = new HashMap<>();
        Set<String> strings = collect.keySet();
        for(String s:strings){
            List<A3StatisticsRes> resList = new ArrayList<>();
            Map<Date, List<A3StatisticsRes>> collect1 = collect.get(s).stream().collect(Collectors.groupingBy(A3StatisticsRes::getRecordTime));
            Set<Date> dates = collect1.keySet();
            for(Date date : dates){
                A3StatisticsRes res = new A3StatisticsRes();
                Double aDouble = collect1.get(date).stream().filter(t -> t.getV() != null).map(A3StatisticsRes::getV).reduce(Double::sum).orElse(0.000);
                res.setParamName(s);
                res.setV(aDouble);
                res.setRecordTime(date);
                resList.add(res);
                resList = resList.stream().sorted(Comparator.comparing(A3StatisticsRes::getRecordTime,Comparator.nullsFirst(Comparator.naturalOrder()))).collect(Collectors.toList());
            }
            result.put(s,resList);
        }
        return result;
    }

    private Map<String, List<A3StatisticsRes>> selectHdAndChange(Map<String,List<String>> selectMap,A3StatisticsReq req){
        Map<String, List<A3StatisticsRes>> result = new HashMap<>();
        Set<String> strings = selectMap.keySet();
        for(String station:strings){
            A3StatisticsReq tempReq = new A3StatisticsReq();
            BeanUtils.copyProperties(req,tempReq);
            tempReq.setIds(selectMap.get(selectMap));
            List<A3StatisticsRes> statistics = dayWaterSituationStatisticsTableHdMapper.getStatistics(tempReq);
            result.put(station,statistics);
        }
        return result;
    }

    private Map<String, List<A3StatisticsRes>> selectHxAndChange(Map<String,List<String>> selectMap,A3StatisticsReq req){
        Map<String, List<A3StatisticsRes>> result = new HashMap<>();
        Set<String> strings = selectMap.keySet();
        for(String station:strings){
            A3StatisticsReq tempReq = new A3StatisticsReq();
            BeanUtils.copyProperties(req,tempReq);
            tempReq.setIds(selectMap.get(selectMap));
            List<A3StatisticsRes> statistics = dayWaterSituationStatisticsTableHxMapper.getStatistics(tempReq);
            result.put(station,statistics);
        }
        return result;
    }
}
