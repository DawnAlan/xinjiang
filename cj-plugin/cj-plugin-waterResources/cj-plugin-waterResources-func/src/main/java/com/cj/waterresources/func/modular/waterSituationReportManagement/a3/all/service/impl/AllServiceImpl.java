package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson.JSONObject;
import com.cj.auth.core.pojo.SaBaseLoginUser;
import com.cj.auth.core.util.StpLoginUserUtil;
import com.cj.common.model.RestResponse;
import com.cj.common.util.RedisUtil;
import com.cj.common.util.RestTemplateUtil;
import com.cj.flood.api.PredictionApi;
import com.cj.middleDatabase.func.modular.dto.RealTimeRainfallRes;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.entity.IrrigatedPlatformDataInfo;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.mapper.IrrigatedPlatformDataInfoMapper;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.service.IrrigatedPlatformDataInfoService;
import com.cj.middleDatabase.func.modular.lzz.lzzGaugingStation.entity.LzzGaugingStation;
import com.cj.middleDatabase.func.modular.lzz.lzzGaugingStation.mapper.LzzGaugingStationMapper;
import com.cj.middleDatabase.func.modular.lzz.lzzGaugingStation.service.LzzGaugingStationService;
import com.cj.waterresources.func.modular.overallSituationUnitMgr.entity.OverallSituationUnitMgr;
import com.cj.waterresources.func.modular.overallSituationUnitMgr.service.OverallSituationUnitMgrService;
import com.cj.waterresources.func.modular.trendsTable.entity.TrendsTableParam;
import com.cj.waterresources.func.modular.trendsTable.service.TrendsTableParamService;
import com.cj.waterresources.func.modular.waterSituationDataMaintenance.bean.req.SelectInfoListNewReq;
import com.cj.waterresources.func.modular.waterSituationDataMaintenance.bean.req.SelectInfoListReq;
import com.cj.waterresources.func.modular.waterSituationDataMaintenance.bean.res.HydrographRes;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.bean.req.*;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.bean.res.*;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.service.AllService;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.dkl.mapper.DayWaterSituationStatisticsTableDklMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.hd.entity.DayWaterSituationStatisticsTableHd;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.hd.mapper.DayWaterSituationStatisticsTableHdMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.hd.service.DayWaterSituationStatisticsTableHdService;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.hx.entity.DayWaterSituationStatisticsTableHx;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.hx.mapper.DayWaterSituationStatisticsTableHxMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.hx.service.DayWaterSituationStatisticsTableHxService;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.lzz.bean.res.LzzReportFormsRes;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.lzz.entity.DayWaterSituationStatisticsTableLzz;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.lzz.mapper.DayWaterSituationStatisticsTableLzzMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.lzz.service.DayWaterSituationStatisticsTableLzzService;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.qs.entity.DayWaterSituationStatisticsTableQs;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.qs.entity.DayWaterSituationStatisticsTableQsLh;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.qs.mapper.DayWaterSituationStatisticsTableQsLhMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.qs.mapper.DayWaterSituationStatisticsTableQsMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.qs.service.DayWaterSituationStatisticsTableQsLhService;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.qs.service.DayWaterSituationStatisticsTableQsService;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.syyl.mapper.DayWaterSituationStatisticsTableSyylMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.tjc.mapper.DayWaterSituationStatisticsTableTjcMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.tth.bean.res.TthReportFormsRes;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.tth.entity.DayWaterSituationStatisticsTableTth;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.tth.mapper.DayWaterSituationStatisticsTableTthMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.tth.service.DayWaterSituationStatisticsTableTthService;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.zcc.mapper.DayWaterSituationStatisticsTableZccMapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
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
    private final DayWaterSituationStatisticsTableQsLhMapper dayWaterSituationStatisticsTableQsLhMapper;
    private final RedisUtil redisUtil;
    private final TrendsTableParamService trendsTableParamService;
    private final DayWaterSituationStatisticsTableLzzService dayWaterSituationStatisticsTableLzzService;
    private final DayWaterSituationStatisticsTableTthService dayWaterSituationStatisticsTableTthService;
    private final DayWaterSituationStatisticsTableHdService dayWaterSituationStatisticsTableHdService;
    private final DayWaterSituationStatisticsTableHxService dayWaterSituationStatisticsTableHxService;
    private final DayWaterSituationStatisticsTableQsService dayWaterSituationStatisticsTableQsService;
    private final DayWaterSituationStatisticsTableQsLhService dayWaterSituationStatisticsTableQsLhService;
    private final OverallSituationUnitMgrService overallSituationUnitMgrService;
    private final IrrigatedPlatformDataInfoMapper irrigatedPlatformDataInfoMapper;
    private final LzzGaugingStationMapper lzzGaugingStationMapper;
    private final PredictionApi predictionApi;
    private final LzzGaugingStationService lzzGaugingStationService;
    private final IrrigatedPlatformDataInfoService irrigatedPlatformDataInfoService;


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
            dayWaterSituationStatisticsTableQsLhMapper.deleteByTime(date);
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
        if(trendsTableParam.getUseStation().equals("灯笼渠绿化")){
            List<DayWaterSituationStatisticsTableQsLh> dayWaterSituationStatisticsTableQsLhs = dayWaterSituationStatisticsTableQsLhMapper.selectList2(trendsTableParam.getId(), req.getStartTime(), req.getEndTime());
            if(dayWaterSituationStatisticsTableQsLhs.isEmpty()){
                return RestResponse.no("暂无数据");
            }else {
                dayWaterSituationStatisticsTableQsLhs.forEach(t->{
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
        List<TrendsTableParam> collect = trendsTableParamList.stream().filter(t ->t.getUnitId()!=null && t.getUnitId().equals(req.getId())).collect(Collectors.toList());
        if(collect.isEmpty()){
            return null;
        }
        TrendsTableParam trendsTableParam = collect.get(0);
        if(trendsTableParam.getUseStation().equals("河东管理站")){
            List<DayWaterSituationStatisticsTableHd> dayWaterSituationStatisticsTableHds = dayWaterSituationStatisticsTableHdMapper.selectList2(trendsTableParam.getId(), req.getStartTime(), req.getEndTime());
            if(dayWaterSituationStatisticsTableHds.isEmpty()){
                return null;
            }else {
                dayWaterSituationStatisticsTableHds.stream().filter(t->t.getV()!=null).collect(Collectors.toList()).forEach(t->{
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
                dayWaterSituationStatisticsTableHxes.stream().filter(t->t.getV()!=null).collect(Collectors.toList()).forEach(t->{
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
                dayWaterSituationStatisticsTableQs.stream().filter(t->t.getV()!=null).collect(Collectors.toList()).forEach(t->{
                    HydrographRes res = new HydrographRes();
                    res.setName((String)redisUtil.get("trendsTableParam:name:"+t.getTableHeadId()));
                    res.setFlow(t.getV());
                    res.setTime(sdf.format(t.getRecordTime()));
                    hydrographResList.add(res);
                });
            }
        }
        if(trendsTableParam.getUseStation().equals("灯笼渠绿化")){
            List<DayWaterSituationStatisticsTableQsLh> dayWaterSituationStatisticsTableQsLhs = dayWaterSituationStatisticsTableQsLhMapper.selectList2(trendsTableParam.getId(), req.getStartTime(), req.getEndTime());
            if(dayWaterSituationStatisticsTableQsLhs.isEmpty()){
                return null;
            }else {
                dayWaterSituationStatisticsTableQsLhs.stream().filter(t->t.getV()!=null).collect(Collectors.toList()).forEach(t->{
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
                dayWaterSituationStatisticsTableTths.stream().filter(t->t.getV()!=null).collect(Collectors.toList()).forEach(t->{
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
                dayWaterSituationStatisticsTableLzzes.stream().filter(t->t.getV()!=null).collect(Collectors.toList()).forEach(t->{
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

    @SneakyThrows
    @Override
    public RestResponse selectListForIndustrialWaterFee(SelectListForIndustrialWaterFeeReq req) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
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
                res.setRecordTime(date);
                res.setFlow(formatDoubleThree(collect1.get(date).stream().filter(t->t.getV()!=null).map(DayWaterSituationStatisticsTableLzz::getV).reduce(Double::sum).orElse(0.00)));
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
                res.setRecordTime(tth.getRecordTime());
                res.setFlow(formatDoubleThree(tth.getV()));
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
                res.setRecordTime(tth.getRecordTime());
                res.setFlow(formatDoubleThree(tth.getV()));
                resList.add(res);
            }
        }
        if(resList.isEmpty()){
            return RestResponse.no("暂无流量数据");
        }else {
            List<String> tempList = new ArrayList<>();
            String[] split = req.getStartTime().split("-");
            Integer yearMonth = YearMonth.of(Integer.valueOf(split[0]), Integer.parseInt(split[1])).lengthOfMonth();
            LocalDate startDate = LocalDate.of(Integer.valueOf(split[0]), Integer.valueOf(split[1]), Integer.valueOf(split[2]));
            LocalDate endDate = LocalDate.of(Integer.valueOf(split[0]), Integer.parseInt(split[1]),yearMonth);
            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                String dateTemp = date.toString();
                List<SelectListForIndustrialWaterFeeRes> collect = resList.stream().filter(t -> {
                    try {
                        return sdf.parse(sdf.format(t.getRecordTime())).compareTo(sdf.parse(dateTemp)) == 0;
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toList());
                if(collect.size()==0){
                    tempList.add(dateTemp);
                }
            }
            for(String s:tempList){
                SelectListForIndustrialWaterFeeRes res = new SelectListForIndustrialWaterFeeRes();
                res.setRecordTime(sdf.parse(s));
                res.setFlow(null);
                resList.add(res);
            }
            Collections.sort(resList,new Comparator<SelectListForIndustrialWaterFeeRes>(){
                public int compare(SelectListForIndustrialWaterFeeRes u1,SelectListForIndustrialWaterFeeRes u2){
                    return u1.getRecordTime().compareTo(u2.getRecordTime());
                }
            });
            return RestResponse.ok(resList);
        }
    }

    @Override
    public RestResponse selectReportForms(ReportFormsReq req) {
        if(req.getReservoir().equals("楼庄子水库")){
            return dayWaterSituationStatisticsTableLzzService.selectReportForms(req.getStartTime(),req.getEndTime());
        }else {
            return dayWaterSituationStatisticsTableTthService.selectReportForms(req.getStartTime(),req.getEndTime());
        }
    }

    @SneakyThrows
    @Override
    public RestResponse selectFloodRetentionCapacity(String date) {
        List<FloodRetentionCapacityRes> resList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String endTime = date;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(sdf.parse(endTime));
        calendar.add(Calendar.DAY_OF_MONTH,-1);
        Date startTimeTemp = calendar.getTime();
        String startTime = sdf.format(startTimeTemp);
        List<LzzReportFormsRes> lzzReportFormsResList = dayWaterSituationStatisticsTableLzzService.selectReportForms(startTime, endTime).getData();
        List<TthReportFormsRes> tthReportFormsResList = dayWaterSituationStatisticsTableTthService.selectReportForms(startTime, endTime).getData();
        if(!lzzReportFormsResList.isEmpty()){
            FloodRetentionCapacityRes lzz = new FloodRetentionCapacityRes();
            lzz.setReservoirName("楼庄子水库");
            Double aDouble = formatDouble(lzzReportFormsResList.get(lzzReportFormsResList.size() - 1).getStorageCapacity() - lzzReportFormsResList.get(lzzReportFormsResList.size() - 2).getStorageCapacity());
            lzz.setYesterdayFloodRetentionCapacity(aDouble>0?aDouble:0.00);
            lzz.setYearFloodRetentionCapacity((Double)redisUtil.get("floodRetentionCapacity:lzz"));

            Set<String> allKeysInputFlow = redisUtil.getAllKeys("lzz:input:"+endTime);
            List<Date> dateListInputFlow = new ArrayList<>();
            for(String s:allKeysInputFlow){
                String[] split1 = s.split(" ");
                String[] split2 = split1[0].split(":");
                String dateTemp =split2[split2.length-1]+" "+split1[split1.length-1];
                Date parse = sdf1.parse(dateTemp);
                dateListInputFlow.add(parse);
            }
            List<Date> collectInputFlow = dateListInputFlow.stream().sorted(Comparator.comparing(Date::getDate, Comparator.reverseOrder())).collect(Collectors.toList());
            Double inputFlow = collectInputFlow.size()>0?(Double) redisUtil.get("lzz:input:"+sdf1.format(collectInputFlow.get(0))):null;
            lzz.setInputFlow(inputFlow);
            Set<String> allKeysOut = redisUtil.getAllKeys("lzz:out:"+endTime);
            List<Date> dateListOut = new ArrayList<>();
            for(String s:allKeysOut){
                String[] split1 = s.split(" ");
                String[] split2 = split1[0].split(":");
                String dateTemp =split2[split2.length-1]+" "+split1[split1.length-1];
                Date parse = sdf1.parse(dateTemp);
                dateListOut.add(parse);
            }
            List<Date> collectOut = dateListInputFlow.stream().sorted(Comparator.comparing(Date::getDate, Comparator.reverseOrder())).collect(Collectors.toList());
            Double out = collectOut.size()>0?(Double) redisUtil.get("lzz:out:"+sdf1.format(collectOut.get(0))):null;
            lzz.setOutputFlow(out);
            resList.add(lzz);
        }
        if(!tthReportFormsResList.isEmpty()){
            FloodRetentionCapacityRes tth = new FloodRetentionCapacityRes();
            tth.setReservoirName("头屯河水库");
            Double aDouble = formatDouble(tthReportFormsResList.get(tthReportFormsResList.size() - 1).getStorageCapacity() - tthReportFormsResList.get(tthReportFormsResList.size() - 2).getStorageCapacity());
            tth.setYesterdayFloodRetentionCapacity(aDouble>0?aDouble:0.00);
            tth.setYearFloodRetentionCapacity((Double)redisUtil.get("floodRetentionCapacity:tth"));
            Set<String> allKeysInputFlow = redisUtil.getAllKeys("irrigatedPlatform:sq:tth:input:"+endTime);
            List<Date> dateListInputFlow = new ArrayList<>();
            for(String s:allKeysInputFlow){
                String[] split1 = s.split(" ");
                String[] split2 = split1[0].split(":");
                String dateTemp =split2[split2.length-1]+" "+split1[split1.length-1];
                Date parse = sdf1.parse(dateTemp);
                dateListInputFlow.add(parse);
            }
            List<Date> collectInputFlow = dateListInputFlow.stream().sorted(Comparator.comparing(Date::getDate, Comparator.reverseOrder())).collect(Collectors.toList());
            Double inputFlow = collectInputFlow.size()>0?(Double) redisUtil.get("irrigatedPlatform:sq:tth:input:"+sdf1.format(collectInputFlow.get(0))):null;
            tth.setInputFlow(inputFlow);
            Set<String> allKeysOutputFlow = redisUtil.getAllKeys("A3:tth:out:"+endTime);
            List<Date> dateListOutputFlow = new ArrayList<>();
            for(String s:allKeysOutputFlow){
                String[] split1 = s.split(" ");
                String[] split2 = split1[0].split(":");
                String dateTemp =split2[split2.length-1]+" "+split1[split1.length-1];
                Date parse = sdf1.parse(dateTemp);
                dateListOutputFlow.add(parse);
            }
            List<Date> collectOutputFlow = dateListOutputFlow.stream().sorted(Comparator.comparing(Date::getDate, Comparator.reverseOrder())).collect(Collectors.toList());
            Double outputFlow = collectOutputFlow.size()>0?(Double) redisUtil.get("A3:tth:out:"+sdf1.format(collectOutputFlow.get(0))):null;
            tth.setOutputFlow(outputFlow);
            resList.add(tth);
        }
        if(resList.isEmpty()){
            return RestResponse.no("暂无数据计算");
        }else {
            return RestResponse.ok(resList);
        }
    }

    @Override
    public RestResponse selectFloodRetentionCapacityNew(String date, String ids) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String startTime = "";
        try {
            startTime = getDate(sdf.parse(date), -1);
        } catch (ParseException e) {
            return RestResponse.no("时间格式不正确，请传年月日格式日期参数");
        }
        List<FloodRetentionCapacityRes> resList = new ArrayList<>();
        List<OverallSituationUnitMgr> idsList = new ArrayList<>();
        String overall = (String) redisUtil.get("overallSituationUnitMgr:list");
        if(StringUtils.isEmpty(overall)){
            List<OverallSituationUnitMgr> list = overallSituationUnitMgrService.list();
            redisUtil.set("overallSituationUnitMgr:list", JSONObject.toJSONString(list));
            overall = JSONObject.toJSONString(list);
        }
        List<OverallSituationUnitMgr> list = JSONObject.parseArray(overall, OverallSituationUnitMgr.class);
        String mk = (String) redisUtil.get("trendsTableParam:list");
        if(StringUtils.isEmpty(mk)){
            trendsTableParamService.updateCache();
            mk = (String) redisUtil.get("trendsTableParam:list");
        }
        List<TrendsTableParam> trendsTableParamList = JSONObject.parseArray(mk, TrendsTableParam.class);
        for (String id:ids.split(",")){
            idsList.add(list.stream().filter(t -> t.getId().equals(id)).collect(Collectors.toList()).get(0));
        }
        FloodRetentionCapacityRes lzz = new FloodRetentionCapacityRes();
        List<LzzReportFormsRes> lzzReportFormsResList = dayWaterSituationStatisticsTableLzzService.selectReportForms(startTime, date).getData();
        Double lzzDouble = lzzReportFormsResList.size()<2?null:formatDouble(lzzReportFormsResList.get(lzzReportFormsResList.size() - 1).getStorageCapacity() - lzzReportFormsResList.get(lzzReportFormsResList.size() - 2).getStorageCapacity());
        lzz.setYesterdayFloodRetentionCapacity(lzzDouble==null?null:lzzDouble>0?lzzDouble:0.00);
        lzz.setYearFloodRetentionCapacity(formatDouble((Double)redisUtil.get("floodRetentionCapacity:lzz")));
        lzz.setReservoirName("楼庄子水库");
        FloodRetentionCapacityRes tth = new FloodRetentionCapacityRes();
        List<TthReportFormsRes> tthReportFormsResList = dayWaterSituationStatisticsTableTthService.selectReportForms(startTime, date).getData();
        Double tthDouble = tthReportFormsResList.size()<2?null:formatDouble(tthReportFormsResList.get(tthReportFormsResList.size() - 1).getStorageCapacity() - tthReportFormsResList.get(tthReportFormsResList.size() - 2).getStorageCapacity());
        tth.setYesterdayFloodRetentionCapacity(tthDouble==null?null:tthDouble>0?tthDouble:0.00);
        tth.setYearFloodRetentionCapacity(formatDouble((Double)redisUtil.get("floodRetentionCapacity:tth")));
        tth.setReservoirName("头屯河水库");
        Double lzzOutputFlow = 0.00;
        Double tthOutputFlow = 0.00;
        Double tthInputFlow = 0.00;
        for(OverallSituationUnitMgr mgr:idsList){
            if(getTopUnitNameFromOverallSituationUnitMgr(mgr.getId()).equals("楼庄子水库")){
                lzz.setOverallId(mgr.getId());
                if(StringUtils.isNotEmpty(mgr.getMonitorId())){
                    LzzGaugingStation info = lzzGaugingStationMapper.selectInfoForIndex(mgr.getMonitorId(), date);
                    if(mgr.getUnitName().contains("进库")){
                        lzz.setInputFlow(info==null?null:info.getFlow());
                    }
                    if(mgr.getUnitName().contains("河道")){
                        lzzOutputFlow+=info==null?0.00:info.getFlow()==null?0.00:info.getFlow();
                    }
                    if(mgr.getUnitName().contains("楼庄子水厂管道1")){
                        lzzOutputFlow+=info==null?0.00:info.getFlow()==null?0.00:info.getFlow();
                    }
                    if(mgr.getUnitName().contains("楼庄子水厂管道2")){
                        lzzOutputFlow+=info==null?0.00:info.getFlow()==null?0.00:info.getFlow();
                    }
                }else {
                    TrendsTableParam param = trendsTableParamList.stream().filter(t -> t.getUseStation().equals("楼庄子水库") && t.getUseType() == 1 && StringUtils.isNotEmpty(t.getUnitId())&& t.getUnitId().equals(mgr.getId())).collect(Collectors.toList()).get(0);
                    DayWaterSituationStatisticsTableLzz dayWaterSituationStatisticsTableLzz = dayWaterSituationStatisticsTableLzzMapper.selectListForIndex(date, param.getId());
                    if(param.getParamName().contains("进库")){
                        lzz.setInputFlow(dayWaterSituationStatisticsTableLzz==null?null:dayWaterSituationStatisticsTableLzz.getV());
                    }
                    if(param.getParamName().contains("河道")){
                        lzzOutputFlow+=dayWaterSituationStatisticsTableLzz==null?0.00:dayWaterSituationStatisticsTableLzz.getV()==null?0.00:dayWaterSituationStatisticsTableLzz.getV();
                    }
                    if(param.getParamName().contains("楼庄子水厂管道1")){
                        lzzOutputFlow+=dayWaterSituationStatisticsTableLzz==null?0.00:dayWaterSituationStatisticsTableLzz.getV()==null?0.00:dayWaterSituationStatisticsTableLzz.getV();
                    }
                    if(param.getParamName().contains("楼庄子水厂管道2")){
                        lzzOutputFlow+=dayWaterSituationStatisticsTableLzz==null?0.00:dayWaterSituationStatisticsTableLzz.getV()==null?0.00:dayWaterSituationStatisticsTableLzz.getV();
                    }
                }
            }
            if(getTopUnitNameFromOverallSituationUnitMgr(mgr.getId()).equals("头屯河水库")){
                tth.setOverallId(mgr.getId());
                if(StringUtils.isNotEmpty(mgr.getMonitorId())){
                    IrrigatedPlatformDataInfo info = irrigatedPlatformDataInfoMapper.selectInfoForIndex(mgr.getMonitorId(), date);
                    if(mgr.getUnitName().contains("进库")){
                        tthInputFlow+=info==null?0.00:info.getSqMonitorFlow()==null?0.00:info.getSqMonitorFlow();
                    }
                    if(mgr.getUnitName().contains("龙口流量")){
                        tthInputFlow+=info==null?0.00:info.getSqMonitorFlow()==null?0.00:info.getSqMonitorFlow();
                    }
                    if(mgr.getUnitName().contains("河道")){
                        tthOutputFlow+=info==null?0.00:info.getSqMonitorFlow()==null?0.00:info.getSqMonitorFlow();
                    }
                    if(mgr.getUnitName().contains("红岩流量")){
                        tthOutputFlow+=info==null?0.00:info.getSqMonitorFlow()==null?0.00:info.getSqMonitorFlow();
                    }
                    if(mgr.getUnitName().contains("暗渠流量")){
                        tthOutputFlow+=info==null?0.00:info.getSqMonitorFlow()==null?0.00:info.getSqMonitorFlow();
                    }
                    if(mgr.getUnitName().contains("清水池泵站")){
                        tthOutputFlow+=info==null?0.00:info.getSqMonitorFlow()==null?0.00:info.getSqMonitorFlow();
                    }
                    if(mgr.getUnitName().contains("八钢浮船")){
                        tthOutputFlow+=info==null?0.00:info.getSqMonitorFlow()==null?0.00:info.getSqMonitorFlow();
                    }
                    if(mgr.getUnitName().contains("龙口直供")){
                        tthOutputFlow+=info==null?0.00:info.getSqMonitorFlow()==null?0.00:info.getSqMonitorFlow();
                    }
                }else {
                    TrendsTableParam param = trendsTableParamList.stream().filter(t -> t.getUseStation().equals("头屯河水库") && t.getUseType() == 1 && StringUtils.isNotEmpty(t.getUnitId())&& t.getUnitId().equals(mgr.getId())).collect(Collectors.toList()).get(0);
                    DayWaterSituationStatisticsTableTth dayWaterSituationStatisticsTableTth = dayWaterSituationStatisticsTableTthMapper.selectListForIndex(date, param.getId());
                    if(param.getParamName().contains("进库")){
                        tthInputFlow+=dayWaterSituationStatisticsTableTth==null?0.00:dayWaterSituationStatisticsTableTth.getV()==null?0.00:dayWaterSituationStatisticsTableTth.getV();
                    }
                    if(param.getParamName().contains("龙口流量")){
                        tthInputFlow+=dayWaterSituationStatisticsTableTth==null?0.00:dayWaterSituationStatisticsTableTth.getV()==null?0.00:dayWaterSituationStatisticsTableTth.getV();
                    }
                    if(param.getParamName().contains("河道")){
                        tthOutputFlow+=dayWaterSituationStatisticsTableTth==null?0.00:dayWaterSituationStatisticsTableTth.getV()==null?0.00:dayWaterSituationStatisticsTableTth.getV();
                    }
                    if(param.getParamName().contains("红岩流量")){
                        tthOutputFlow+=dayWaterSituationStatisticsTableTth==null?0.00:dayWaterSituationStatisticsTableTth.getV()==null?0.00:dayWaterSituationStatisticsTableTth.getV();
                    }
                    if(param.getParamName().contains("暗渠流量")){
                        tthOutputFlow+=dayWaterSituationStatisticsTableTth==null?0.00:dayWaterSituationStatisticsTableTth.getV()==null?0.00:dayWaterSituationStatisticsTableTth.getV();
                    }
                    if(param.getParamName().contains("清水池泵站")){
                        tthOutputFlow+=dayWaterSituationStatisticsTableTth==null?0.00:dayWaterSituationStatisticsTableTth.getV()==null?0.00:dayWaterSituationStatisticsTableTth.getV();
                    }
                    if(param.getParamName().contains("八钢浮船")){
                        tthOutputFlow+=dayWaterSituationStatisticsTableTth==null?0.00:dayWaterSituationStatisticsTableTth.getV()==null?0.00:dayWaterSituationStatisticsTableTth.getV();
                    }
                    if(param.getParamName().contains("龙口直供")){
                        tthOutputFlow+=dayWaterSituationStatisticsTableTth==null?0.00:dayWaterSituationStatisticsTableTth.getV()==null?0.00:dayWaterSituationStatisticsTableTth.getV();
                    }
                }
            }
        }
        lzz.setOutputFlow(lzzOutputFlow==0.00?null:lzzOutputFlow);
        tth.setOutputFlow(tthOutputFlow==0.00?null:tthOutputFlow);
        tth.setInputFlow(tthInputFlow==0.00?null:tthInputFlow);
        resList.add(lzz);
        resList.add(tth);
        return RestResponse.ok(resList);
    }

    @Override
    public RestResponse<Map<String,List<TodayWaterSituationRes>>> selectTodayWaterSituation(String date) {
        SaBaseLoginUser saBaseLoginUser = StpLoginUserUtil.getLoginUser();
        List<TodayWaterSituationRes> resList = new ArrayList<>();
        Map<String,List<TodayWaterSituationRes>> resMap = new HashMap<>();
        String mk = (String) redisUtil.get("trendsTableParam:list");
        if(StringUtils.isEmpty(mk)){
            trendsTableParamService.updateCache();
            mk = (String) redisUtil.get("trendsTableParam:list");
        }
        List<TrendsTableParam> trendsTableParamList = JSONObject.parseArray(mk, TrendsTableParam.class);
        if(saBaseLoginUser.getOrgName().equals("河东管理站")){
            String hd = dayWaterSituationStatisticsTableHdMapper.selectEndTableList(date);
            Integer hdSize = hd.split(",").length;
            List<DayWaterSituationStatisticsTableHd> dayWaterSituationStatisticsTableHds = dayWaterSituationStatisticsTableHdMapper.selectListForTodayWaterSituation(date, hdSize);
            if(dayWaterSituationStatisticsTableHds.isEmpty()){
                return RestResponse.no("河东管理站今日暂无数据，请到A3水情日报表填写相关信息后再查询");
            }
            String endTableList = dayWaterSituationStatisticsTableHds.get(0).getEndTableList();
            String[] split = endTableList.split(",");
            for(String s:split){
                TodayWaterSituationRes res = new TodayWaterSituationRes();
                String name = (String) redisUtil.get("trendsTableParam:name:"+s);
                if(!name.equals("合计") && StringUtils.isNotEmpty(name)){
                    res.setName(name);
                    res.setId(s);
                    res.setValue(dayWaterSituationStatisticsTableHds.stream().filter(t->t.getV()!=null && t.getTableHeadId().equals(s)).map(DayWaterSituationStatisticsTableHd::getV).reduce(Double::sum).orElse(0.00));
                    resList.add(res);
                }
            }
            if(resList.isEmpty()){
                return RestResponse.no("今日暂无有效数据");
            }
            resMap.put("河东",resList);
            return RestResponse.ok(resMap);
        }
        if(saBaseLoginUser.getOrgName().equals("河西管理站")){
            String hx = dayWaterSituationStatisticsTableHxMapper.selectEndTableList(date);
            Integer hxSize = hx.split(",").length;
            List<DayWaterSituationStatisticsTableHx> dayWaterSituationStatisticsTableHxs = dayWaterSituationStatisticsTableHxMapper.selectListForTodayWaterSituation(date, hxSize);
            if(dayWaterSituationStatisticsTableHxs.isEmpty()){
                return RestResponse.no("河西管理站今日暂无数据，请到A3水情日报表填写相关信息后再查询");
            }
            String endTableList = dayWaterSituationStatisticsTableHxs.get(0).getEndTableList();
            String[] split = endTableList.split(",");
            for(String s:split){
                TodayWaterSituationRes res = new TodayWaterSituationRes();
                String name = (String) redisUtil.get("trendsTableParam:name:"+s);
                if(!name.equals("合计") && StringUtils.isNotEmpty(name)){
                    res.setName(name);
                    res.setId(s);
                    res.setValue(dayWaterSituationStatisticsTableHxs.stream().filter(t->t.getV()!=null && t.getTableHeadId().equals(s)).map(DayWaterSituationStatisticsTableHx::getV).reduce(Double::sum).orElse(0.00));
                    resList.add(res);
                }
            }
            if(resList.isEmpty()){
                return RestResponse.no("今日暂无有效数据");
            }
            resMap.put("河西",resList);
            return RestResponse.ok(resMap);
        }
        if(saBaseLoginUser.getOrgName().equals("渠首管理站")){
            String qs = dayWaterSituationStatisticsTableQsMapper.selectEndTableList(date);
            Integer qsSize = qs.split(",").length;
            List<DayWaterSituationStatisticsTableQs> dayWaterSituationStatisticsTableQss = dayWaterSituationStatisticsTableQsMapper.selectListForTodayWaterSituation(date, qsSize);
            if(dayWaterSituationStatisticsTableQss.isEmpty()){
                return RestResponse.no("渠首管理站今日暂无数据，请到A3水情日报表填写相关信息后再查询");
            }
            String endTableList = dayWaterSituationStatisticsTableQss.get(0).getEndTableList();
            String[] split = endTableList.split(",");
            for(String s:split){
                TodayWaterSituationRes res = new TodayWaterSituationRes();
                String name = (String) redisUtil.get("trendsTableParam:name:"+s);
                if(!name.equals("合计") && StringUtils.isNotEmpty(name)){
                    res.setName(name);
                    res.setId(s);
                    res.setValue(dayWaterSituationStatisticsTableQss.stream().filter(t->t.getV()!=null && t.getTableHeadId().equals(s)).map(DayWaterSituationStatisticsTableQs::getV).reduce(Double::sum).orElse(0.00));
                    resList.add(res);
                }
            }
            if(resList.isEmpty()){
                return RestResponse.no("今日暂无有效数据");
            }
            resMap.put("渠首",resList);
            return RestResponse.ok(resMap);
        }
        if(saBaseLoginUser.getOrgName().equals("头屯河水库")){
            String tth = dayWaterSituationStatisticsTableTthMapper.selectEndTableList(date);
            Integer tthSize = tth.split(",").length;
            List<DayWaterSituationStatisticsTableTth> dayWaterSituationStatisticsTableTths = dayWaterSituationStatisticsTableTthMapper.selectListForTodayWaterSituation(date, tthSize);
            if(dayWaterSituationStatisticsTableTths.isEmpty()){
                return RestResponse.no("头屯河水库今日暂无数据，请到A3水情日报表填写相关信息后再查询");
            }
            String endTableList = dayWaterSituationStatisticsTableTths.get(0).getEndTableList();
            String[] split = endTableList.split(",");
            for(String s:split){
                TodayWaterSituationRes res = new TodayWaterSituationRes();
                String name = (String) redisUtil.get("trendsTableParam:name:"+s);
                if(!name.equals("合计") && StringUtils.isNotEmpty(name)){
                    res.setName(name);
                    res.setId(s);
                    res.setValue(dayWaterSituationStatisticsTableTths.stream().filter(t->t.getV()!=null && t.getTableHeadId().equals(s)).map(DayWaterSituationStatisticsTableTth::getV).reduce(Double::sum).orElse(0.00));
                    resList.add(res);
                }
            }
            if(resList.isEmpty()){
                return RestResponse.no("今日暂无有效数据");
            }
            resMap.put("头屯河",resList);
            return RestResponse.ok(resMap);
        }
        if(saBaseLoginUser.getOrgName().equals("楼庄子水库")){
            String lzz = dayWaterSituationStatisticsTableLzzMapper.selectEndTableList(date);
            Integer lzzSize = lzz.split(",").length;
            List<DayWaterSituationStatisticsTableLzz> dayWaterSituationStatisticsTableLzzs = dayWaterSituationStatisticsTableLzzMapper.selectListForTodayWaterSituation(date,lzzSize);
            if(dayWaterSituationStatisticsTableLzzs.isEmpty()){
                return RestResponse.no("头屯河水库今日暂无数据，请到A3水情日报表填写相关信息后再查询");
            }
            String endTableList = dayWaterSituationStatisticsTableLzzs.get(0).getEndTableList();
            String[] split = endTableList.split(",");
            for(String s:split){
                TodayWaterSituationRes res = new TodayWaterSituationRes();
                String name = (String) redisUtil.get("trendsTableParam:name:"+s);
                if(!name.equals("合计") && StringUtils.isNotEmpty(name)){
                    res.setName(name);
                    res.setId(s);
                    res.setValue(dayWaterSituationStatisticsTableLzzs.stream().filter(t->t.getV()!=null && t.getTableHeadId().equals(s)).map(DayWaterSituationStatisticsTableLzz::getV).reduce(Double::sum).orElse(0.00));
                    resList.add(res);
                }
            }
            if(resList.isEmpty()){
                return RestResponse.no("今日暂无有效数据");
            }
            resMap.put("楼庄子",resList);
            return RestResponse.ok(resMap);
        }
        Map<String, List<TodayWaterSituationRes>> stringListMap = allData(date);
        if(stringListMap !=null){
            return RestResponse.ok(stringListMap);
        }else {
            return RestResponse.no("今日暂无有效数据");
        }
    }

    @Override
    public RestResponse selectTodayWaterSituationForFlood(String date, String ids) {
        List<TodayWaterSituationForFloodRes> resList = new ArrayList<>();
        List<OverallSituationUnitMgr> idsList = new ArrayList<>();
        String overall = (String) redisUtil.get("overallSituationUnitMgr:list");
        if(StringUtils.isEmpty(overall)){
            List<OverallSituationUnitMgr> list = overallSituationUnitMgrService.list();
            redisUtil.set("overallSituationUnitMgr:list", JSONObject.toJSONString(list));
            overall = JSONObject.toJSONString(list);
        }
        List<OverallSituationUnitMgr> list = JSONObject.parseArray(overall, OverallSituationUnitMgr.class);
        String mk = (String) redisUtil.get("trendsTableParam:list");
        if(StringUtils.isEmpty(mk)){
            trendsTableParamService.updateCache();
            mk = (String) redisUtil.get("trendsTableParam:list");
        }
        List<TrendsTableParam> trendsTableParamList = JSONObject.parseArray(mk, TrendsTableParam.class);
        for (String id:ids.split(",")){
            idsList.add(list.stream().filter(t -> t.getId().equals(id)).collect(Collectors.toList()).get(0));
        }
        for(OverallSituationUnitMgr mgr:idsList){
            TodayWaterSituationForFloodRes res = new TodayWaterSituationForFloodRes();
            res.setOverallId(mgr.getId());
            if(getTopUnitNameFromOverallSituationUnitMgr(mgr.getId()).equals("楼庄子水库")){
                res.setName(mgr.getUnitName());
                if(StringUtils.isNotEmpty(mgr.getMonitorId())){
                    LzzGaugingStation station = lzzGaugingStationMapper.selectInfoForIndex(mgr.getMonitorId(), date);
                    res.setFlow(station==null?null:station.getFlow());
                    if(station==null?false:station.getRelativeWaterLevel()<0){
                        res.setWaterLevel(getWaterLevelByFlow(station.getFlow(),mgr.getId()));
                    }else {
                        res.setWaterLevel(station==null?null:station.getRelativeWaterLevel());
                    }
                }else {
                    List<TrendsTableParam> collect = trendsTableParamList.stream().filter(t -> t.getUseStation().equals("楼庄子水库") && t.getUseType() == 1 && StringUtils.isNotEmpty(t.getUnitId()) && t.getUnitId().equals(mgr.getId())).collect(Collectors.toList());
                    TrendsTableParam param = collect.size()>0?collect.get(0):null;
                    if(null!=param){
                        DayWaterSituationStatisticsTableLzz dayWaterSituationStatisticsTableLzz = dayWaterSituationStatisticsTableLzzMapper.selectListForIndex(date, param.getId());
                        res.setFlow(dayWaterSituationStatisticsTableLzz==null?null:dayWaterSituationStatisticsTableLzz.getV());
                        res.setWaterLevel(res.getFlow()==null?null:getWaterLevelByFlow(res.getFlow(),mgr.getId()));
                    }
                }
            }
            if(getTopUnitNameFromOverallSituationUnitMgr(mgr.getId()).equals("头屯河水库")){
                res.setName(mgr.getUnitName());
                if(StringUtils.isNotEmpty(mgr.getMonitorId())){
                    IrrigatedPlatformDataInfo info = irrigatedPlatformDataInfoMapper.selectInfoForIndex(mgr.getMonitorId(), date);
                    res.setFlow(info==null?null:info.getSqMonitorFlow());
                    if(info==null?false:info.getSqWaterLevel()<0){
                        res.setWaterLevel(getWaterLevelByFlow(res.getFlow(),mgr.getId()));
                    }else {
                        res.setWaterLevel(info==null?null:info.getSqWaterLevel());
                    }
                }else {
                    TrendsTableParam param = trendsTableParamList.stream().filter(t -> t.getUseStation().equals("头屯河水库") && t.getUseType() == 1 && StringUtils.isNotEmpty(t.getUnitId())&& t.getUnitId().equals(mgr.getId())).collect(Collectors.toList()).get(0);
                    DayWaterSituationStatisticsTableTth dayWaterSituationStatisticsTableTth = dayWaterSituationStatisticsTableTthMapper.selectListForIndex(date, param.getId());
                    res.setFlow(dayWaterSituationStatisticsTableTth==null?null:dayWaterSituationStatisticsTableTth.getV());
                    res.setWaterLevel(res.getFlow()==null?null:getWaterLevelByFlow(res.getFlow(),mgr.getId()));
                }
            }
            resList.add(res);
        }
        return RestResponse.ok(resList);
    }

    @Override
    public RestResponse selectTodayRainfall(String date,Integer hour) {
        List<RealTimeRainfallRes> resultList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH");
        String overall = (String) redisUtil.get("overallSituationUnitMgr:list");
        if(StringUtils.isEmpty(overall)){
            List<OverallSituationUnitMgr> list = overallSituationUnitMgrService.list();
            redisUtil.set("overallSituationUnitMgr:list", JSONObject.toJSONString(list));
            overall = JSONObject.toJSONString(list);
        }
        List<OverallSituationUnitMgr> list = JSONObject.parseArray(overall, OverallSituationUnitMgr.class);
        try {
            if (null==hour){
                List<OverallSituationUnitMgr> collect = list.stream().filter(t -> t.getPName().equals("雨量站")).collect(Collectors.toList());
                List<String> tth = collect.stream().filter(t -> t.getDataResource() != null && t.getDataResource() == 1 && StringUtils.isNotEmpty(t.getMonitorId())).map(OverallSituationUnitMgr::getMonitorId).collect(Collectors.toList());
                List<String> lzz = collect.stream().filter(t -> t.getDataResource() != null && t.getDataResource() == 2 && StringUtils.isNotEmpty(t.getMonitorId())).map(OverallSituationUnitMgr::getMonitorId).collect(Collectors.toList());
                String realTimeRainfallByDate = predictionApi.getRealTimeRainfallByDate(date, lzz.size(), tth.size(),lzz,tth);
                if(StringUtils.isNotEmpty(realTimeRainfallByDate)){
                    List<RealTimeRainfallRes> resList = JSONObject.parseArray(realTimeRainfallByDate, RealTimeRainfallRes.class);
                    resList.forEach(t->{
                        t.setOverallId(collect.stream().filter(c->c.getMonitorId().equals(t.getId())).map(OverallSituationUnitMgr::getId).collect(Collectors.toList()).get(0));
                    });
                    Map<String, List<RealTimeRainfallRes>> collect1 = resList.stream().collect(Collectors.groupingBy(RealTimeRainfallRes::getStationName));
                    collect1.forEach((k,v)->{
                        resultList.add(collect1.get(k).get(0));
                    });
                    return RestResponse.ok(resultList);
                }else {
                    return RestResponse.no("暂无数据");
                }
            }else {
                List<OverallSituationUnitMgr> collect = list.stream().filter(t -> t.getPName().equals("雨量站")).collect(Collectors.toList());
                List<String> tth = collect.stream().filter(t -> t.getDataResource() != null && t.getDataResource() == 1).map(OverallSituationUnitMgr::getMonitorId).collect(Collectors.toList());
                List<String> lzz = collect.stream().filter(t -> t.getDataResource() != null && t.getDataResource() == 2).map(OverallSituationUnitMgr::getMonitorId).collect(Collectors.toList());
                Date endTime = null;
                if (StringUtils.isEmpty(date)){
                    endTime = new Date();
                }else {
                    endTime = sdf.parse(date);
                }
                Date startTime = calculateTime(sdf.format(endTime),-hour);
                String realTimeRainfall = predictionApi.getRealTimeRainfall(sdf.format(startTime), sdf.format(endTime),lzz.size(), tth.size(),lzz,tth);
                if(StringUtils.isNotEmpty(realTimeRainfall)){
                    List<RealTimeRainfallRes> resList = JSONObject.parseArray(realTimeRainfall, RealTimeRainfallRes.class);
                    resList.forEach(t->{
                        t.setOverallId(collect.stream().filter(c->c.getMonitorId().equals(t.getId())).map(OverallSituationUnitMgr::getId).collect(Collectors.toList()).get(0));
                    });
                    Map<String, List<RealTimeRainfallRes>> collect1 = resList.stream().collect(Collectors.groupingBy(RealTimeRainfallRes::getStationName));
                    collect1.forEach((k,v)->{
                        resultList.add(collect1.get(k).get(0));
                    });
                    return RestResponse.ok(resultList);
                }else {
                    return RestResponse.no("暂无数据");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            return RestResponse.no("select error");
        }
    }
    private Date calculateTime(String date, int hour){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH");
        try {
            if(StringUtils.isEmpty(date)){
                date = sdf.format(new Date());
            }
            Calendar c1 = Calendar.getInstance();
            try {
                c1.setTime(sdf.parse(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            c1.add(Calendar.HOUR,hour);
            Date back=c1.getTime();
            return sdf.parse(sdf.format(back));
        }catch (Exception e) {
            return new Date();
        }
    }

    @Override
    public RestResponse updateInfoDate() {
        updateInfoDateHd();
        updateInfoDateHx();
        updateInfoDateQs();
        updateInfoDateQsLh();
        updateInfoDateTth();
        updateInfoDateLzz();
        return RestResponse.ok();
    }

    @Override
    public RestResponse selectTodayWaterSituationSelectById(SelectTodayWaterSituationSelectByIdReq req) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        List<SelectTodayWaterSituationSelectByIdRes> selectTodayWaterSituationSelectByIdRes = new ArrayList<>();
        if(StringUtils.isEmpty(req.getId())){
            return null;
        }
        String mk = (String) redisUtil.get("trendsTableParam:list");
        if(StringUtils.isEmpty(mk)){
            trendsTableParamService.updateCache();
            mk = (String) redisUtil.get("trendsTableParam:list");
        }
        List<TrendsTableParam> trendsTableParamList = JSONObject.parseArray(mk, TrendsTableParam.class);
        List<TrendsTableParam> collect = trendsTableParamList.stream().filter(t ->t.getId().equals(req.getId())).collect(Collectors.toList());
        if(collect.isEmpty()){
            return null;
        }
        TrendsTableParam trendsTableParam = collect.get(0);
        if(trendsTableParam.getUseStation().equals("河东管理站")){
            List<DayWaterSituationStatisticsTableHd> dayWaterSituationStatisticsTableHds = dayWaterSituationStatisticsTableHdMapper.selectList2(trendsTableParam.getId(), req.getStartTime(), req.getEndTime());
            if(dayWaterSituationStatisticsTableHds.isEmpty()){
                return null;
            }else {
                dayWaterSituationStatisticsTableHds.stream().filter(t->t.getV()!=null).collect(Collectors.toList()).forEach(t->{
                    SelectTodayWaterSituationSelectByIdRes res = new SelectTodayWaterSituationSelectByIdRes();
                    res.setName((String)redisUtil.get("trendsTableParam:name:"+t.getTableHeadId()));
                    res.setValue(t.getV());
                    res.setTime(sdf.format(t.getRecordTime())+" "+t.getTime());
                    selectTodayWaterSituationSelectByIdRes.add(res);
                });
            }
        }
        if(trendsTableParam.getUseStation().equals("河西管理站")){
            List<DayWaterSituationStatisticsTableHx> dayWaterSituationStatisticsTableHxes = dayWaterSituationStatisticsTableHxMapper.selectList2(trendsTableParam.getId(), req.getStartTime(), req.getEndTime());
            if(dayWaterSituationStatisticsTableHxes.isEmpty()){
                return null;
            }else {
                dayWaterSituationStatisticsTableHxes.stream().filter(t->t.getV()!=null).collect(Collectors.toList()).forEach(t->{
                    SelectTodayWaterSituationSelectByIdRes res = new SelectTodayWaterSituationSelectByIdRes();
                    res.setName((String)redisUtil.get("trendsTableParam:name:"+t.getTableHeadId()));
                    res.setValue(t.getV());
                    res.setTime(sdf.format(t.getRecordTime())+" "+t.getTime());
                    selectTodayWaterSituationSelectByIdRes.add(res);
                });
            }
        }
        if(trendsTableParam.getUseStation().equals("渠首管理站")){
            List<DayWaterSituationStatisticsTableQs> dayWaterSituationStatisticsTableQs = dayWaterSituationStatisticsTableQsMapper.selectList2(trendsTableParam.getId(), req.getStartTime(), req.getEndTime());
            if(dayWaterSituationStatisticsTableQs.isEmpty()){
                return null;
            }else {
                dayWaterSituationStatisticsTableQs.stream().filter(t->t.getV()!=null).collect(Collectors.toList()).forEach(t->{
                    SelectTodayWaterSituationSelectByIdRes res = new SelectTodayWaterSituationSelectByIdRes();
                    res.setName((String)redisUtil.get("trendsTableParam:name:"+t.getTableHeadId()));
                    res.setValue(t.getV());
                    res.setTime(sdf.format(t.getRecordTime())+" "+t.getTime());
                    selectTodayWaterSituationSelectByIdRes.add(res);
                });
            }
        }
        if(trendsTableParam.getUseStation().equals("灯笼渠绿化")){
            List<DayWaterSituationStatisticsTableQsLh> dayWaterSituationStatisticsTableQsLhs = dayWaterSituationStatisticsTableQsLhMapper.selectList2(trendsTableParam.getId(), req.getStartTime(), req.getEndTime());
            if(dayWaterSituationStatisticsTableQsLhs.isEmpty()){
                return null;
            }else {
                dayWaterSituationStatisticsTableQsLhs.stream().filter(t->t.getV()!=null).collect(Collectors.toList()).forEach(t->{
                    SelectTodayWaterSituationSelectByIdRes res = new SelectTodayWaterSituationSelectByIdRes();
                    res.setName((String)redisUtil.get("trendsTableParam:name:"+t.getTableHeadId()));
                    res.setValue(t.getV());
                    res.setTime(sdf.format(t.getRecordTime())+" "+t.getTime());
                    selectTodayWaterSituationSelectByIdRes.add(res);
                });
            }
        }
        if(trendsTableParam.getUseStation().equals("头屯河水库")){
            List<DayWaterSituationStatisticsTableTth> dayWaterSituationStatisticsTableTths = dayWaterSituationStatisticsTableTthMapper.selectList2(trendsTableParam.getId(), req.getStartTime(), req.getEndTime());
            if(dayWaterSituationStatisticsTableTths.isEmpty()){
                return null;
            }else {
                dayWaterSituationStatisticsTableTths.stream().filter(t->t.getV()!=null).collect(Collectors.toList()).forEach(t->{
                    SelectTodayWaterSituationSelectByIdRes res = new SelectTodayWaterSituationSelectByIdRes();
                    res.setName((String)redisUtil.get("trendsTableParam:name:"+t.getTableHeadId()));
                    res.setValue(t.getV());
                    res.setTime(sdf.format(t.getRecordTime())+" "+t.getTime());
                    selectTodayWaterSituationSelectByIdRes.add(res);
                });
            }
        }
        if(trendsTableParam.getUseStation().equals("楼庄子水库")){
            List<DayWaterSituationStatisticsTableLzz> dayWaterSituationStatisticsTableLzzes = dayWaterSituationStatisticsTableLzzMapper.selectList2(trendsTableParam.getId(), req.getStartTime(), req.getEndTime());
            if(dayWaterSituationStatisticsTableLzzes.isEmpty()){
                return null;
            }else {
                dayWaterSituationStatisticsTableLzzes.stream().filter(t->t.getV()!=null).collect(Collectors.toList()).forEach(t->{
                    SelectTodayWaterSituationSelectByIdRes res = new SelectTodayWaterSituationSelectByIdRes();
                    res.setName((String)redisUtil.get("trendsTableParam:name:"+t.getTableHeadId()));
                    res.setValue(t.getV());
                    res.setTime(sdf.format(t.getRecordTime())+" "+t.getTime());
                    selectTodayWaterSituationSelectByIdRes.add(res);
                });
            }
        }
        if(selectTodayWaterSituationSelectByIdRes.isEmpty()){
            return null;
        }
        return RestResponse.ok(selectTodayWaterSituationSelectByIdRes);
    }

    @Override
    public RestResponse selectReservoirHistoryList(SelectReservoirHistoryListReq req) {
        String mk = (String) redisUtil.get("trendsTableParam:list");
        if(StringUtils.isEmpty(mk)){
            trendsTableParamService.updateCache();
            mk = (String) redisUtil.get("trendsTableParam:list");
        }
        List<SelectReservoirHistoryListRes> resList = new ArrayList<>();
        List<TrendsTableParam> trendsTableParamList = JSONObject.parseArray(mk, TrendsTableParam.class);
        if(req.getName().equals("楼庄子水库")){
            List<TrendsTableParam> collect = trendsTableParamList.stream().filter(t -> t.getUseStation().equals("楼庄子水库") && t.getUseType() == 1).collect(Collectors.toList());
            TrendsTableParam param = collect.stream().filter(t -> t.getParamName().equals("库水位")).collect(Collectors.toList()).get(0);
            List<DayWaterSituationStatisticsTableLzz> dayWaterSituationStatisticsTableLzzes = dayWaterSituationStatisticsTableLzzMapper.selectReservoirHistoryList(req.getStartTime(), req.getEndTime(), param.getId());
            if(!dayWaterSituationStatisticsTableLzzes.isEmpty()){
                dayWaterSituationStatisticsTableLzzes.forEach(t->{
                    SelectReservoirHistoryListRes res = new SelectReservoirHistoryListRes();
                    res.setName("楼庄子水库");
                    res.setTime(t.getRecordTime());
                    res.setValue(t.getV());
                    resList.add(res);
                });
            }
            return RestResponse.ok(resList);
        }
        if(req.getName().equals("头屯河水库")){
            List<TrendsTableParam> collect = trendsTableParamList.stream().filter(t -> t.getUseStation().equals("头屯河水库") && t.getUseType() == 1).collect(Collectors.toList());
            TrendsTableParam param = collect.stream().filter(t -> t.getParamName().equals("库水位")).collect(Collectors.toList()).get(0);
            List<DayWaterSituationStatisticsTableTth> dayWaterSituationStatisticsTableTths = dayWaterSituationStatisticsTableTthMapper.selectReservoirHistoryList(req.getStartTime(), req.getEndTime(), param.getId());
            if(!dayWaterSituationStatisticsTableTths.isEmpty()){
                dayWaterSituationStatisticsTableTths.forEach(t->{
                    SelectReservoirHistoryListRes res = new SelectReservoirHistoryListRes();
                    res.setName("头屯河水库");
                    res.setTime(t.getRecordTime());
                    res.setValue(t.getV());
                    resList.add(res);
                });
            }
            return RestResponse.ok(resList);
        }
        return RestResponse.no("暂无数据");
    }

    @SneakyThrows
    @Override
    public RestResponse<List<RealTimeEngineeringSituationDataRes>> getRealTimeWaterLevelData(String date) {
        List<RealTimeEngineeringSituationDataRes> result = new ArrayList<>();
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String lzzId = "f4e914b3e4f34ac18148c93eae02924f";
        String tthId = "f00584c2a99c40278e5513e8df1589a2";

        String mk = (String) redisUtil.get("trendsTableParam:list");
        if(StringUtils.isEmpty(mk)){
            trendsTableParamService.updateCache();
            mk = (String) redisUtil.get("trendsTableParam:list");
        }
        List<TrendsTableParam> trendsTableParamList = JSONObject.parseArray(mk, TrendsTableParam.class);

        RealTimeEngineeringSituationDataRes lzzData = new RealTimeEngineeringSituationDataRes();
        lzzData.setReservoirName("楼庄子水库");
        lzzData.setFloodControlLevel(1394.5);
        Set<String> allKeys = redisUtil.getAllKeys("lzz:waterLevel:"+date);
        if(allKeys.isEmpty()){
            List<TrendsTableParam> collect = trendsTableParamList.stream().filter(t -> t.getUseType() == 1 && t.getUseStation().equals("楼庄子水库")).collect(Collectors.toList());
            TrendsTableParam waterLevelParam = collect.stream().filter(t -> t.getPId().equals("0") && t.getParamName().equals("库水位")).collect(Collectors.toList()).get(0);
            DayWaterSituationStatisticsTableLzz waterLevel = dayWaterSituationStatisticsTableLzzMapper.selectListForIndex(date,waterLevelParam.getId());
            if(waterLevel!=null){
                lzzData.setRealTimeWaterLevel(formatDouble(waterLevel.getV()));
            }
            TrendsTableParam capacityParam = collect.stream().filter(t -> t.getPId().equals("0") && t.getParamName().equals("库容")).collect(Collectors.toList()).get(0);
            DayWaterSituationStatisticsTableLzz capacity = dayWaterSituationStatisticsTableLzzMapper.selectListForIndex(date,capacityParam.getId());
            if(capacity!=null){
                lzzData.setUsedStorageCapacity(formatDouble(capacity.getV()));
            }else {
                if(waterLevel!=null){
                    lzzData.setUsedStorageCapacity(formatDouble(getWaterLevelByLevel(waterLevel.getV(),lzzId)));

                }
            }
            lzzData.setRemainingStorageCapacity(lzzData.getUsedStorageCapacity()==null?null:formatDouble(7374.0 - lzzData.getUsedStorageCapacity()));
        }else {
            List<Date> dateList = new ArrayList<>();
            for(String s:allKeys){
                if(s.contains("日均")){
                    continue;
                }
                String[] split1 = s.split(" ");
                int length = split1[split1.length-1].split(":").length;
                String[] split2 = split1[0].split(":");
                String dateTemp =split2[split2.length-1]+" "+(length==1?split1[split1.length-1]+":00":split1[split1.length-1]);
                Date parse = sdf1.parse(dateTemp);
                dateList.add(parse);
            }
            Collections.sort(dateList, new Comparator<Date>() {
                @Override
                public int compare(Date o1, Date o2) {
                    return o2.compareTo(o1);
                }
            });
            Double v = dateList.size()>0?(Double) redisUtil.get("lzz:waterLevel:"+sdf1.format(dateList.get(0))):null;
            if(v==null || v<0){
                List<TrendsTableParam> collect1 = trendsTableParamList.stream().filter(t -> t.getUseType() == 1 && t.getUseStation().equals("楼庄子水库")).collect(Collectors.toList());
                TrendsTableParam waterLevelParam = collect1.stream().filter(t -> t.getPId().equals("0") && t.getParamName().equals("库水位")).collect(Collectors.toList()).get(0);
                DayWaterSituationStatisticsTableLzz waterLevel = dayWaterSituationStatisticsTableLzzMapper.selectListForIndex(date,waterLevelParam.getId());
                if(waterLevel!=null){
                    lzzData.setRealTimeWaterLevel(formatDouble(waterLevel.getV()));
                }
                TrendsTableParam capacityParam = collect1.stream().filter(t -> t.getPId().equals("0") && t.getParamName().equals("库容")).collect(Collectors.toList()).get(0);
                DayWaterSituationStatisticsTableLzz capacity = dayWaterSituationStatisticsTableLzzMapper.selectListForIndex(date,capacityParam.getId());
                if(capacity!=null){
                    lzzData.setUsedStorageCapacity(formatDouble(capacity.getV()));
                }else {
                    if(waterLevel!=null){
                        lzzData.setUsedStorageCapacity(formatDouble(getWaterLevelByLevel(waterLevel.getV(),lzzId)));
                    }
                }
                lzzData.setRemainingStorageCapacity(lzzData.getUsedStorageCapacity()==null?null:formatDouble(7374.0 - lzzData.getUsedStorageCapacity()));
            }else {
                lzzData.setRealTimeWaterLevel(v==null?null:formatDouble(v));
                lzzData.setUsedStorageCapacity(v==null?null:formatDouble(getWaterLevelByLevel(v,lzzId)));
                lzzData.setRemainingStorageCapacity(v==null?null:formatDouble(7374.0 - lzzData.getUsedStorageCapacity()));
            }
        }
        result.add(lzzData);
        //头屯河水库
        RealTimeEngineeringSituationDataRes tthData = new RealTimeEngineeringSituationDataRes();
        Set<String> allKeysWaterLevel = redisUtil.getAllKeys("irrigatedPlatform:sq:tth:waterLevel:"+date);
        List<Date> dateListWaterLevel = new ArrayList<>();
        for(String s:allKeysWaterLevel){
            String[] split1 = s.split(" ");
            int length = split1[split1.length-1].split(":").length;
            String[] split2 = split1[0].split(":");
            String dateTemp =split2[split2.length-1]+" "+(length==1?split1[split1.length-1]+":00":split1[split1.length-1]);
            Date parse = sdf1.parse(dateTemp);
            dateListWaterLevel.add(parse);
        }
        Collections.sort(dateListWaterLevel, new Comparator<Date>() {
            @Override
            public int compare(Date o1, Date o2) {
                return o2.compareTo(o1);
            }
        });
        Double waterLevel = dateListWaterLevel.size()>0?(Double) redisUtil.get("irrigatedPlatform:sq:tth:waterLevel:"+sdf1.format(dateListWaterLevel.get(0))):null;
        if(null==waterLevel){
            List<TrendsTableParam> collect = trendsTableParamList.stream().filter(t -> t.getUseType() == 1 && t.getUseStation().equals("头屯河水库")).collect(Collectors.toList());
            TrendsTableParam waterLevelParam = collect.stream().filter(t -> t.getPId().equals("0") && t.getParamName().equals("库水位")).collect(Collectors.toList()).get(0);
            waterLevel = dayWaterSituationStatisticsTableTthMapper.selectListForIndex(date,waterLevelParam.getId()).getV();
            tthData.setRealTimeWaterLevel(formatDouble(waterLevel));

        }else {
            tthData.setRealTimeWaterLevel(formatDouble(waterLevel));
        }

        Set<String> allKeysCapacity = redisUtil.getAllKeys("irrigatedPlatform:sq:tth:capacity:"+date);
        List<Date> dateListCapacity = new ArrayList<>();
        for(String s:allKeysCapacity){
            String[] split1 = s.split(" ");
            int length = split1[split1.length-1].split(":").length;
            String[] split2 = split1[0].split(":");
            String dateTemp =split2[split2.length-1]+" "+(length==1?split1[split1.length-1]+":00":split1[split1.length-1]);
            Date parse = sdf1.parse(dateTemp);
            dateListCapacity.add(parse);
        }
        Collections.sort(dateListCapacity, new Comparator<Date>() {
            @Override
            public int compare(Date o1, Date o2) {
                return o2.compareTo(o1);
            }
        });
        Double capacity = dateListCapacity.size()>0?(Double) redisUtil.get("irrigatedPlatform:sq:tth:capacity:"+sdf1.format(dateListCapacity.get(0))):null;
        tthData.setReservoirName("头屯河水库");
        tthData.setFloodControlLevel(988.0);
        if(null ==capacity){
            List<TrendsTableParam> collect = trendsTableParamList.stream().filter(t -> t.getUseType() == 1 && t.getUseStation().equals("头屯河水库")).collect(Collectors.toList());
            TrendsTableParam capacityParam = collect.stream().filter(t -> t.getPId().equals("0") && t.getParamName().equals("水库库容")).collect(Collectors.toList()).get(0);
            capacity = dayWaterSituationStatisticsTableTthMapper.selectListForIndex(date,capacityParam.getId()).getV();
            if(null ==capacity){
                if(tthData.getRealTimeWaterLevel()!=null){
                    tthData.setUsedStorageCapacity(formatDouble(getWaterLevelByLevel(tthData.getRealTimeWaterLevel(),tthId)));
                }
            }else {
                tthData.setUsedStorageCapacity(formatDouble(capacity));
            }

        }else {
            tthData.setUsedStorageCapacity(formatDouble(capacity));
        }
        tthData.setRemainingStorageCapacity(formatDouble(2030.0 - tthData.getUsedStorageCapacity()));
        result.add(tthData);
        return RestResponse.ok(result);
    }

    @Override
    public RestResponse selectCapacityOutPutDetail(String date, String ids) {
        List<SelectCapacityOutPutDetailRes> result = new ArrayList<>();
        List<OverallSituationUnitMgr> idsList = new ArrayList<>();
        String overall = (String) redisUtil.get("overallSituationUnitMgr:list");
        if(StringUtils.isEmpty(overall)){
            List<OverallSituationUnitMgr> list = overallSituationUnitMgrService.list();
            redisUtil.set("overallSituationUnitMgr:list", JSONObject.toJSONString(list));
            overall = JSONObject.toJSONString(list);
        }
        List<OverallSituationUnitMgr> list = JSONObject.parseArray(overall, OverallSituationUnitMgr.class);
        String mk = (String) redisUtil.get("trendsTableParam:list");
        if(StringUtils.isEmpty(mk)){
            trendsTableParamService.updateCache();
            mk = (String) redisUtil.get("trendsTableParam:list");
        }
        List<TrendsTableParam> trendsTableParamList = JSONObject.parseArray(mk, TrendsTableParam.class);
        for (String id:ids.split(",")){
            idsList.add(list.stream().filter(t -> t.getId().equals(id)).collect(Collectors.toList()).get(0));
        }
        for(OverallSituationUnitMgr mgr:idsList){
            SelectCapacityOutPutDetailRes res = new SelectCapacityOutPutDetailRes();
            res.setStationName(mgr.getUnitName());
            if(getTopUnitNameFromOverallSituationUnitMgr(mgr.getId()).equals("楼庄子水库")){
                if(StringUtils.isNotEmpty(mgr.getMonitorId())){
                    LzzGaugingStation info = lzzGaugingStationMapper.selectInfoForIndex(mgr.getMonitorId(), date);
                    res.setFlow(info==null?0.00:info.getFlow()==null?0.00:info.getFlow());
                }else {
                    TrendsTableParam param = trendsTableParamList.stream().filter(t -> t.getUseStation().equals("楼庄子水库") && t.getUseType() == 1 && StringUtils.isNotEmpty(t.getUnitId())&& t.getUnitId().equals(mgr.getId())).collect(Collectors.toList()).get(0);
                    DayWaterSituationStatisticsTableLzz dayWaterSituationStatisticsTableLzz = dayWaterSituationStatisticsTableLzzMapper.selectListForIndex(date, param.getId());
                    res.setFlow(dayWaterSituationStatisticsTableLzz==null?0.00:dayWaterSituationStatisticsTableLzz.getV()==null?0.00:dayWaterSituationStatisticsTableLzz.getV());
                }
            }
            if(getTopUnitNameFromOverallSituationUnitMgr(mgr.getId()).equals("头屯河水库")){
                if(StringUtils.isNotEmpty(mgr.getMonitorId())){
                    IrrigatedPlatformDataInfo info = irrigatedPlatformDataInfoMapper.selectInfoForIndex(mgr.getMonitorId(), date);
                    res.setFlow(info==null?0.00:info.getSqMonitorFlow()==null?0.00:info.getSqMonitorFlow());

                }else {
                    TrendsTableParam param = trendsTableParamList.stream().filter(t -> t.getUseStation().equals("头屯河水库") && t.getUseType() == 1 && StringUtils.isNotEmpty(t.getUnitId())&& t.getUnitId().equals(mgr.getId())).collect(Collectors.toList()).get(0);
                    DayWaterSituationStatisticsTableTth dayWaterSituationStatisticsTableTth = dayWaterSituationStatisticsTableTthMapper.selectListForIndex(date, param.getId());
                    res.setFlow(dayWaterSituationStatisticsTableTth==null?0.00:dayWaterSituationStatisticsTableTth.getV()==null?0.00:dayWaterSituationStatisticsTableTth.getV());
                }
            }
            result.add(res);
        }
        return RestResponse.ok(result);
    }

    private void updateInfoDateHd(){
        List<DayWaterSituationStatisticsTableHd> dayWaterSituationStatisticsTable = dayWaterSituationStatisticsTableHdMapper.selectAllListToday();
        Map<Date, List<DayWaterSituationStatisticsTableHd>> collect = dayWaterSituationStatisticsTable.stream().collect(Collectors.groupingBy(DayWaterSituationStatisticsTableHd::getRecordTime));
        Set<Date> dates = collect.keySet();
        for(Date date: dates){
            List<DayWaterSituationStatisticsTableHd> list = collect.get(date);
            List<DayWaterSituationStatisticsTableHd> dayWaterSituationStatisticsTableList = dayWaterSituationStatisticsTableHdMapper.selectInfoAfterDayList(getDate(date,1));
            if(!dayWaterSituationStatisticsTableList.isEmpty()){
                dayWaterSituationStatisticsTableList.forEach(t->{
                    t.setV(list.stream().filter(p->p.getTableHeadId().equals(t.getTableHeadId()) && p.getV() !=null).map(DayWaterSituationStatisticsTableHd::getV).reduce(Double::sum).orElse(0.00));
                });
                dayWaterSituationStatisticsTableHdService.updateBatchById(dayWaterSituationStatisticsTableList);
            }
        }
    }

    private void updateInfoDateHx(){
        List<DayWaterSituationStatisticsTableHx> dayWaterSituationStatisticsTable = dayWaterSituationStatisticsTableHxMapper.selectAllListToday();
        Map<Date, List<DayWaterSituationStatisticsTableHx>> collect = dayWaterSituationStatisticsTable.stream().collect(Collectors.groupingBy(DayWaterSituationStatisticsTableHx::getRecordTime));
        Set<Date> dates = collect.keySet();
        for(Date date: dates){
            List<DayWaterSituationStatisticsTableHx> list = collect.get(date);
            List<DayWaterSituationStatisticsTableHx> dayWaterSituationStatisticsTableList = dayWaterSituationStatisticsTableHxMapper.selectInfoAfterDayList(getDate(date,1));
            if(!dayWaterSituationStatisticsTableList.isEmpty()){
                dayWaterSituationStatisticsTableList.forEach(t->{
                    t.setV(list.stream().filter(p->p.getTableHeadId().equals(t.getTableHeadId()) && p.getV() !=null).map(DayWaterSituationStatisticsTableHx::getV).reduce(Double::sum).orElse(0.00));
                });
                dayWaterSituationStatisticsTableHxService.updateBatchById(dayWaterSituationStatisticsTableList);
            }
        }
    }

    private void updateInfoDateQs(){
        List<DayWaterSituationStatisticsTableQs> dayWaterSituationStatisticsTable = dayWaterSituationStatisticsTableQsMapper.selectAllListToday();
        Map<Date, List<DayWaterSituationStatisticsTableQs>> collect = dayWaterSituationStatisticsTable.stream().collect(Collectors.groupingBy(DayWaterSituationStatisticsTableQs::getRecordTime));
        Set<Date> dates = collect.keySet();
        for(Date date: dates){
            List<DayWaterSituationStatisticsTableQs> list = collect.get(date);
            List<DayWaterSituationStatisticsTableQs> dayWaterSituationStatisticsTableList = dayWaterSituationStatisticsTableQsMapper.selectInfoAfterDayList(getDate(date,1));
            if(!dayWaterSituationStatisticsTableList.isEmpty()){
                dayWaterSituationStatisticsTableList.forEach(t->{
                    t.setV(list.stream().filter(p->p.getTableHeadId().equals(t.getTableHeadId()) && p.getV() !=null).map(DayWaterSituationStatisticsTableQs::getV).reduce(Double::sum).orElse(0.00));
                });
                dayWaterSituationStatisticsTableQsService.updateBatchById(dayWaterSituationStatisticsTableList);
            }
        }
    }

    private void updateInfoDateQsLh(){
        List<DayWaterSituationStatisticsTableQsLh> dayWaterSituationStatisticsTable = dayWaterSituationStatisticsTableQsLhMapper.selectAllListToday();
        Map<Date, List<DayWaterSituationStatisticsTableQsLh>> collect = dayWaterSituationStatisticsTable.stream().collect(Collectors.groupingBy(DayWaterSituationStatisticsTableQsLh::getRecordTime));
        Set<Date> dates = collect.keySet();
        for(Date date: dates){
            List<DayWaterSituationStatisticsTableQsLh> list = collect.get(date);
            List<DayWaterSituationStatisticsTableQsLh> dayWaterSituationStatisticsTableList = dayWaterSituationStatisticsTableQsLhMapper.selectInfoAfterDayList(getDate(date,1));
            if(!dayWaterSituationStatisticsTableList.isEmpty()){
                dayWaterSituationStatisticsTableList.forEach(t->{
                    t.setV(list.stream().filter(p->p.getTableHeadId().equals(t.getTableHeadId()) && p.getV() !=null).map(DayWaterSituationStatisticsTableQsLh::getV).reduce(Double::sum).orElse(0.00));
                });
                dayWaterSituationStatisticsTableQsLhService.updateBatchById(dayWaterSituationStatisticsTableList);
            }
        }
    }

    private void updateInfoDateTth(){
        List<DayWaterSituationStatisticsTableTth> dayWaterSituationStatisticsTable = dayWaterSituationStatisticsTableTthMapper.selectAllListToday();
        Map<Date, List<DayWaterSituationStatisticsTableTth>> collect = dayWaterSituationStatisticsTable.stream().collect(Collectors.groupingBy(DayWaterSituationStatisticsTableTth::getRecordTime));
        Set<Date> dates = collect.keySet();
        for(Date date: dates){
            List<DayWaterSituationStatisticsTableTth> list = collect.get(date);
            List<DayWaterSituationStatisticsTableTth> dayWaterSituationStatisticsTableList = dayWaterSituationStatisticsTableTthMapper.selectInfoAfterDayList(getDate(date,1));
            if(!dayWaterSituationStatisticsTableList.isEmpty()){
                dayWaterSituationStatisticsTableList.forEach(t->{
                    t.setV(list.stream().filter(p->p.getTableHeadId().equals(t.getTableHeadId()) && p.getV() !=null).map(DayWaterSituationStatisticsTableTth::getV).reduce(Double::sum).orElse(0.00));
                });
                dayWaterSituationStatisticsTableTthService.updateBatchById(dayWaterSituationStatisticsTableList);
            }
        }
    }

    private void updateInfoDateLzz(){
        List<DayWaterSituationStatisticsTableLzz> dayWaterSituationStatisticsTable = dayWaterSituationStatisticsTableLzzMapper.selectAllListToday();
        Map<Date, List<DayWaterSituationStatisticsTableLzz>> collect = dayWaterSituationStatisticsTable.stream().collect(Collectors.groupingBy(DayWaterSituationStatisticsTableLzz::getRecordTime));
        Set<Date> dates = collect.keySet();
        for(Date date: dates){
            List<DayWaterSituationStatisticsTableLzz> list = collect.get(date);
            List<DayWaterSituationStatisticsTableLzz> dayWaterSituationStatisticsTableList = dayWaterSituationStatisticsTableLzzMapper.selectInfoAfterDayList(getDate(date,1));
            if(!dayWaterSituationStatisticsTableList.isEmpty()){
                dayWaterSituationStatisticsTableList.forEach(t->{
                    t.setV(list.stream().filter(p->p.getTableHeadId().equals(t.getTableHeadId()) && p.getV() !=null).map(DayWaterSituationStatisticsTableLzz::getV).reduce(Double::sum).orElse(0.00));
                });
                dayWaterSituationStatisticsTableLzzService.updateBatchById(dayWaterSituationStatisticsTableList);
            }
        }
    }

    private Map<String,List<TodayWaterSituationRes>> allData(String date){
        Map<String,List<TodayWaterSituationRes>> resMap = new LinkedHashMap<>();
        List<TodayWaterSituationRes> hdList = new ArrayList<>();
        List<TodayWaterSituationRes> hxList = new ArrayList<>();
        List<TodayWaterSituationRes> qsList = new ArrayList<>();
        List<TodayWaterSituationRes> tthList = new ArrayList<>();
        List<TodayWaterSituationRes> lzzList = new ArrayList<>();
        String lzz = dayWaterSituationStatisticsTableLzzMapper.selectEndTableList(date);
        Integer lzzSize = lzz.split(",").length;
        List<DayWaterSituationStatisticsTableLzz> dayWaterSituationStatisticsTableLzzs = dayWaterSituationStatisticsTableLzzMapper.selectListForTodayWaterSituation(date,lzzSize);
        if(dayWaterSituationStatisticsTableLzzs.isEmpty()){
            resMap.put("楼庄子",null);
        }else {
            String endTableListLzz = dayWaterSituationStatisticsTableLzzs.get(0).getEndTableList();
            String[] splitLzz = endTableListLzz.split(",");
            for(String s:splitLzz){
                TodayWaterSituationRes res = new TodayWaterSituationRes();
                String name = (String) redisUtil.get("trendsTableParam:name:"+s);
                if(!name.equals("合计") && StringUtils.isNotEmpty(name)){
                    res.setName(name);
                    res.setId(s);
                    res.setValue(dayWaterSituationStatisticsTableLzzs.stream().filter(t->t.getV()!=null && t.getTableHeadId().equals(s)).map(DayWaterSituationStatisticsTableLzz::getV).reduce(Double::sum).orElse(0.00));
                    lzzList.add(res);
                }
            }
            if(lzzList.isEmpty()){
                resMap.put("楼庄子",null);
            }
            resMap.put("楼庄子",lzzList);
        }
        String tth = dayWaterSituationStatisticsTableTthMapper.selectEndTableList(date);
        Integer tthSize = tth.split(",").length;
        List<DayWaterSituationStatisticsTableTth> dayWaterSituationStatisticsTableTths = dayWaterSituationStatisticsTableTthMapper.selectListForTodayWaterSituation(date, tthSize);
        if(dayWaterSituationStatisticsTableTths.isEmpty()){
            resMap.put("头屯河",null);
        }else {
            String endTableListTth = dayWaterSituationStatisticsTableTths.get(0).getEndTableList();
            String[] splitTth = endTableListTth.split(",");
            for(String s:splitTth){
                TodayWaterSituationRes res = new TodayWaterSituationRes();
                String name = (String) redisUtil.get("trendsTableParam:name:"+s);
                if(!name.equals("合计") && StringUtils.isNotEmpty(name)){
                    res.setName(name);
                    res.setId(s);
                    res.setValue(dayWaterSituationStatisticsTableTths.stream().filter(t->t.getV()!=null && t.getTableHeadId().equals(s)).map(DayWaterSituationStatisticsTableTth::getV).reduce(Double::sum).orElse(0.00));
                    tthList.add(res);
                }
            }
            if(tthList.isEmpty()){
                resMap.put("头屯河",null);
            }
            resMap.put("头屯河",tthList);
        }
        String qs = dayWaterSituationStatisticsTableQsMapper.selectEndTableList(date);
        Integer qsSize = qs.split(",").length;
        List<DayWaterSituationStatisticsTableQs> dayWaterSituationStatisticsTableQss = dayWaterSituationStatisticsTableQsMapper.selectListForTodayWaterSituation(date, qsSize);
        if(dayWaterSituationStatisticsTableQss.isEmpty()){
            resMap.put("渠首",null);
        }else {
            String endTableListQs = dayWaterSituationStatisticsTableQss.get(0).getEndTableList();
            String[] splitQs = endTableListQs.split(",");
            for(String s:splitQs){
                TodayWaterSituationRes res = new TodayWaterSituationRes();
                String name = (String) redisUtil.get("trendsTableParam:name:"+s);
                if(!name.equals("合计") && StringUtils.isNotEmpty(name)){
                    res.setName(name);
                    res.setId(s);
                    res.setValue(dayWaterSituationStatisticsTableQss.stream().filter(t->t.getV()!=null && t.getTableHeadId().equals(s)).map(DayWaterSituationStatisticsTableQs::getV).reduce(Double::sum).orElse(0.00));
                    qsList.add(res);
                }
            }
            if(qsList.isEmpty()){
                resMap.put("渠首",null);
            }
            resMap.put("渠首",qsList);
        }
        String hd = dayWaterSituationStatisticsTableHdMapper.selectEndTableList(date);
        Integer hdSize = hd.split(",").length;
        List<DayWaterSituationStatisticsTableHd> dayWaterSituationStatisticsTableHds = dayWaterSituationStatisticsTableHdMapper.selectListForTodayWaterSituation(date, hdSize);
        if(dayWaterSituationStatisticsTableHds.isEmpty()){
            resMap.put("河东",null);
        }else {
            String endTableListHd = dayWaterSituationStatisticsTableHds.get(0).getEndTableList();
            String[] splitHd = endTableListHd.split(",");
            for(String s:splitHd){
                TodayWaterSituationRes res = new TodayWaterSituationRes();
                String name = (String) redisUtil.get("trendsTableParam:name:"+s);
                if(!name.equals("合计") && StringUtils.isNotEmpty(name)){
                    res.setName(name);
                    res.setId(s);
                    res.setValue(dayWaterSituationStatisticsTableHds.stream().filter(t->t.getV()!=null && t.getTableHeadId().equals(s)).map(DayWaterSituationStatisticsTableHd::getV).reduce(Double::sum).orElse(0.00));
                    hdList.add(res);
                }
            }
            if(hdList.isEmpty()){
                resMap.put("河东",null);
            }
            resMap.put("河东",hdList);
        }
        String hx = dayWaterSituationStatisticsTableHxMapper.selectEndTableList(date);
        Integer hxSize = hx.split(",").length;
        List<DayWaterSituationStatisticsTableHx> dayWaterSituationStatisticsTableHxs = dayWaterSituationStatisticsTableHxMapper.selectListForTodayWaterSituation(date, hxSize);
        if(dayWaterSituationStatisticsTableHxs.isEmpty()){
            resMap.put("河西",null);
        }else {
            String endTableListHx = dayWaterSituationStatisticsTableHxs.get(0).getEndTableList();
            String[] splitHx = endTableListHx.split(",");
            for(String s:splitHx){
                TodayWaterSituationRes res = new TodayWaterSituationRes();
                String name = (String) redisUtil.get("trendsTableParam:name:"+s);
                if(!name.equals("合计") && StringUtils.isNotEmpty(name)){
                    res.setName(name);
                    res.setId(s);
                    res.setValue(dayWaterSituationStatisticsTableHxs.stream().filter(t->t.getV()!=null && t.getTableHeadId().equals(s)).map(DayWaterSituationStatisticsTableHx::getV).reduce(Double::sum).orElse(0.00));
                    hxList.add(res);
                }
            }
            if(hxList.isEmpty()){
                resMap.put("河西",null);
            }
            resMap.put("河西",hxList);
        }
        if(resMap.isEmpty()){
            return null;
        }
        return resMap;
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
    private Double formatDouble(Double value) {
        if(value==null){
            return null;
        }
        DecimalFormat df = new DecimalFormat("0.00");
        String format = df.format(value);
        return Double.parseDouble(format);
    }

    private Double formatDoubleThree(Double value) {
        if(value==null){
            return null;
        }
        DecimalFormat df = new DecimalFormat("0.000");
        String format = df.format(value);
        return Double.parseDouble(format);
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

    private String getTopUnitNameFromOverallSituationUnitMgr(String id){
        String overall = (String) redisUtil.get("overallSituationUnitMgr:list");
        if(StringUtils.isEmpty(overall)){
            List<OverallSituationUnitMgr> list = overallSituationUnitMgrService.list();
            redisUtil.set("overallSituationUnitMgr:list", JSONObject.toJSONString(list));
            overall = JSONObject.toJSONString(list);
        }
        List<OverallSituationUnitMgr> list = JSONObject.parseArray(overall, OverallSituationUnitMgr.class);
        List<OverallSituationUnitMgr> collect = list.stream().filter(t -> t.getId().equals(id)).collect(Collectors.toList());
        if(!collect.isEmpty()){
            OverallSituationUnitMgr overallSituationUnitMgr = collect.get(0);
            if(overallSituationUnitMgr.getPId().equals("0")){
                return overallSituationUnitMgr.getUnitName();
            }
            List<OverallSituationUnitMgr> collect1 = list.stream().filter(t -> t.getId().equals(overallSituationUnitMgr.getPId())).collect(Collectors.toList());
            if(!collect1.isEmpty()){
                OverallSituationUnitMgr overallSituationUnitMgr1 = collect1.get(0);
                if(overallSituationUnitMgr1.getPId().equals("0")){
                    return overallSituationUnitMgr1.getUnitName();
                }
                List<OverallSituationUnitMgr> collect2 = list.stream().filter(t -> t.getId().equals(overallSituationUnitMgr1.getPId())).collect(Collectors.toList());
                if(!collect2.isEmpty()){
                    OverallSituationUnitMgr overallSituationUnitMgr2 = collect2.get(0);
                    if(overallSituationUnitMgr2.getPId().equals("0")){
                        return overallSituationUnitMgr2.getUnitName();
                    }
                    List<OverallSituationUnitMgr> collect3 = list.stream().filter(t -> t.getId().equals(overallSituationUnitMgr2.getPId())).collect(Collectors.toList());
                    if(!collect3.isEmpty()){
                        OverallSituationUnitMgr overallSituationUnitMgr3 = collect3.get(0);
                        if(overallSituationUnitMgr3.getPId().equals("0")){
                            return overallSituationUnitMgr3.getUnitName();
                        }
                    }
                }
            }
        }
        return "";
    }


    @SneakyThrows
    private Double getWaterLevelByFlow(Double flow, String id){
        String token = StpUtil.getTokenValue();
        InetAddress localHost = InetAddress.getLocalHost();
        String url = "http://" + localHost.getHostAddress() +":9003/toutunhe/wpdCurved/queryLevelFlow?ndcdId="+id+"&flowRate="+flow;
        String s = RestTemplateUtil.getBySaToken(url,token);
        BigDecimal value = (BigDecimal) JSONObject.parseObject(s).get("data");
        return value.doubleValue();
    }

    @SneakyThrows
    private Double getWaterLevelByLevel(Double level, String id){
        String token = StpUtil.getTokenValue();
        InetAddress localHost = InetAddress.getLocalHost();
        //String hostAddress = "192.168.31.154";
        String hostAddress = localHost.getHostAddress();
        String url = "http://" + hostAddress +":9003/toutunhe/wpdCurved/queryLevelFlow?ndcdId="+id+"&level="+level;
        String s = RestTemplateUtil.getBySaToken(url,token);
        BigDecimal value = (BigDecimal) JSONObject.parseObject(s).get("data");
        return value.doubleValue();
    }
}
