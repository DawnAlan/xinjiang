package com.cj.waterresources.func.modular.waterSituationDataMaintenance.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.cj.auth.core.pojo.SaBaseLoginUser;
import com.cj.auth.core.util.StpLoginUserUtil;
import com.cj.common.model.RestResponse;
import com.cj.common.util.NumberUtil;
import com.cj.common.util.RedisUtil;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.bean.res.SelectTodayWaterSituationRes;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.entity.IrrigatedPlatformDataInfo;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.service.IrrigatedPlatformDataInfoService;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformTree.entity.IrrigatedPlatformTree;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformTree.service.IrrigatedPlatformTreeService;
import com.cj.middleDatabase.func.modular.lzz.lzzGaugingStation.entity.LzzGaugingStation;
import com.cj.middleDatabase.func.modular.lzz.lzzGaugingStation.service.LzzGaugingStationService;
import com.cj.middleDatabase.func.modular.lzz.lzzPlatformTree.entity.LzzPlatformTree;
import com.cj.middleDatabase.func.modular.lzz.lzzPlatformTree.service.LzzPlatformTreeService;
import com.cj.middleDatabase.func.modular.lzz.lzzRainfallStation.entity.LzzRainfallStation;
import com.cj.middleDatabase.func.modular.lzz.lzzRainfallStation.service.LzzRainfallStationService;
import com.cj.waterresources.func.modular.overallSituationUnitMgr.entity.OverallSituationUnitMgr;
import com.cj.waterresources.func.modular.overallSituationUnitMgr.service.OverallSituationUnitMgrService;
import com.cj.waterresources.func.modular.trendsTable.entity.TrendsTableParam;
import com.cj.waterresources.func.modular.trendsTable.service.TrendsTableParamService;
import com.cj.waterresources.func.modular.waterPrice.industrialWaterFee.service.IndustrialWaterFeeService;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.bean.res.UseWaterTypeStatisticsRes;
import com.cj.waterresources.func.modular.waterPrice.waterPriceManagement.bean.res.WaterPriceSelectListRes;
import com.cj.waterresources.func.modular.waterSituationDataMaintenance.bean.req.SelectInfoListByIdsReq;
import com.cj.waterresources.func.modular.waterSituationDataMaintenance.bean.req.SelectInfoListNewReq;
import com.cj.waterresources.func.modular.waterSituationDataMaintenance.bean.req.SelectInfoListReq;
import com.cj.waterresources.func.modular.waterSituationDataMaintenance.bean.req.UpdateInfoReq;
import com.cj.waterresources.func.modular.waterSituationDataMaintenance.bean.res.FlowRes;
import com.cj.waterresources.func.modular.waterSituationDataMaintenance.bean.res.HydrographRes;
import com.cj.waterresources.func.modular.waterSituationDataMaintenance.bean.res.IrrigatedPlatformTreeRes;
import com.cj.waterresources.func.modular.waterSituationDataMaintenance.bean.res.LzzPlatformTreeRes;
import com.cj.waterresources.func.modular.waterSituationDataMaintenance.service.WaterSituationService;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.bean.res.TodayWaterSituationRes;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.service.AllService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WaterSituationServiceImpl implements WaterSituationService {

    @Autowired
    private IrrigatedPlatformTreeService irrigatedPlatformTreeService;

    @Autowired
    private LzzPlatformTreeService lzzPlatformTreeService;

    @Autowired
    private IrrigatedPlatformDataInfoService irrigatedPlatformDataInfoService;

    @Autowired
    private LzzRainfallStationService lzzRainfallStationService;

    @Autowired
    private LzzGaugingStationService lzzGaugingStationService;

    @Autowired
    private AllService allService;

    @Autowired
    private OverallSituationUnitMgrService overallSituationUnitMgrService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private TrendsTableParamService trendsTableParamService;


    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public RestResponse<Map<String, Object>> selectTree() {
        try {
            Map<String, Object> result = new HashMap<>();
            List<IrrigatedPlatformTreeRes> irrigatedPlatformTreeResList = new ArrayList<>();
            List<IrrigatedPlatformTree> irrigatedPlatformTreeList = irrigatedPlatformTreeService.list();
            irrigatedPlatformTreeList.forEach(t->{
                IrrigatedPlatformTreeRes res = new IrrigatedPlatformTreeRes();
                BeanUtils.copyProperties(t,res);
                irrigatedPlatformTreeResList.add(res);
            });
            List<IrrigatedPlatformTreeRes> irrigatedPlatformParent= irrigatedPlatformTreeResList.stream().filter(t -> t.getParentId().equals("0")).collect(Collectors.toList());
            getIrrigatedTree(irrigatedPlatformParent,irrigatedPlatformTreeResList);
            List<LzzPlatformTreeRes> lzzPlatformTreeResList = new ArrayList<>();
            List<LzzPlatformTree> lzzPlatformTrees = lzzPlatformTreeService.list();
            lzzPlatformTrees.forEach(t->{
                LzzPlatformTreeRes res = new LzzPlatformTreeRes();
                BeanUtils.copyProperties(t,res);
                lzzPlatformTreeResList.add(res);
            });
            List<LzzPlatformTreeRes> lzzPlatformParent= lzzPlatformTreeResList.stream().filter(t -> t.getPId().equals("0")).collect(Collectors.toList());
            getLzzTree(lzzPlatformParent,lzzPlatformTreeResList);
            result.put("irrigated", irrigatedPlatformParent);
            result.put("lzz", lzzPlatformParent);
            return RestResponse.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return RestResponse.no("查询失败");
        }
    }

    @Override
    public RestResponse selectInfoList(SelectInfoListReq req) {
        if(req.getTreeType().equals("irrigated")){
            List<IrrigatedPlatformDataInfo> list = irrigatedPlatformDataInfoService.selectInfoByCondition(req.getTreeId(),req.getTime(),req.getStartTime(),req.getEndTime());
            if(null != list && list.size()>0) {
                return RestResponse.ok(list);
            }else {
                return RestResponse.no("暂无数据");
            }
        }
        if(req.getTreeType().equals("lzz")){
            List<LzzRainfallStation> list = lzzRainfallStationService.selectInfoByCondition(req.getTreeId(),req.getTime(),req.getStartTime(),req.getEndTime());
            if(null != list && list.size()>0){
                return RestResponse.ok(list);
            }
            List<LzzGaugingStation> list1 = lzzGaugingStationService.selectInfoByCondition(req.getTreeId(),req.getTime(),req.getStartTime(),req.getEndTime());
            if(null != list1 && list1.size()>0){
                return RestResponse.ok(list1);
            }
            return RestResponse.no("暂无数据");
        }
        return RestResponse.no("暂无数据");
    }

    @Override
    public RestResponse<List<HydrographRes>> selectInfoList1(SelectInfoListReq req) {
        List<HydrographRes> hydrographResList = new ArrayList<>();
        if(req.getTreeType().equals("irrigated")){
            List<IrrigatedPlatformDataInfo> list = irrigatedPlatformDataInfoService.selectInfoByCondition(req.getTreeId(),req.getTime(),req.getStartTime(),req.getEndTime());
            if(null != list && list.size()>0) {
                list.forEach(t->{
                    HydrographRes res = new HydrographRes();
                    res.setName(t.getMonitorName());
                    res.setTime(sdf.format(t.getMonitorTime()));
                    res.setFlow(t.getSqMonitorFlow());
                    res.setWaterLevel(t.getSqWaterLevel());
                    hydrographResList.add(res);
                });
            }
        }
        if(req.getTreeType().equals("lzz")){
            List<LzzRainfallStation> list = lzzRainfallStationService.selectInfoByCondition(req.getTreeId(),req.getTime(),req.getStartTime(),req.getEndTime());
            if(null != list && list.size()>0){
                list.forEach(t->{
                    HydrographRes res = new HydrographRes();
                    res.setName(t.getStationName());
                    res.setTime(sdf.format(t.getTime()));
                    res.setRainfall(t.getRainfall());
                    res.setTemperature(t.getTemperature());
                    hydrographResList.add(res);
                });
            }
            List<LzzGaugingStation> list1 = lzzGaugingStationService.selectInfoByCondition(req.getTreeId(),req.getTime(),req.getStartTime(),req.getEndTime());
            if(null != list1 && list1.size()>0){
                list1.forEach(t->{
                    HydrographRes res = new HydrographRes();
                    res.setName(t.getStationName());
                    res.setTime(sdf.format(t.getGatherTime()));
                    res.setFlow(t.getFlow());
                    res.setWaterLevel(t.getRelativeWaterLevel());
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
    public RestResponse update(UpdateInfoReq req) {
        if(null != req.getIrrigatedPlatformDataInfo()){
            boolean b = irrigatedPlatformDataInfoService.updateById(req.getIrrigatedPlatformDataInfo());
            if(b){
                return RestResponse.ok("修改成功");
            }else {
                return RestResponse.no("修改失败");
            }
        }
        if(null != req.getLzzRainfallStation()){
            boolean b = lzzRainfallStationService.updateById(req.getLzzRainfallStation());
            if(b){
                return RestResponse.ok("修改成功");
            }else {
                return RestResponse.no("修改失败");
            }
        }
        if(null != req.getLzzGaugingStation()){
            boolean b = lzzGaugingStationService.updateById(req.getLzzGaugingStation());
            if(b){
                return RestResponse.ok("修改成功");
            }else {
                return RestResponse.no("修改失败");
            }
        }
        return RestResponse.no("请上传修改数据");
    }

    @Override
    public RestResponse selectInfoListAll(SelectInfoListReq req) {
        RestResponse restResponse1 = this.selectInfoList1(req);
        if(restResponse1.getCode()==200){
            return restResponse1;
        }
        RestResponse restResponse = allService.selectInfoList(req);
        if(restResponse.getCode()==200){
            return restResponse;
        }
        return RestResponse.no("查无数据");
    }

    @Override
    public RestResponse selectInfoListAllNew(SelectInfoListNewReq req) {
        List<HydrographRes> resList = new ArrayList<>();
        OverallSituationUnitMgr byId = overallSituationUnitMgrService.getById(req.getId());
        if(null == byId){
            return RestResponse.no("所选站点不存在");
        }
        if(StringUtils.isNotEmpty(byId.getMonitorId())){
            List<IrrigatedPlatformDataInfo> listTth = irrigatedPlatformDataInfoService.selectInfoByCondition(byId.getMonitorId(),null,req.getStartTime(),req.getEndTime());
            if(null != listTth && listTth.size()>0) {
                listTth.stream().collect(Collectors.toList()).forEach(t->{
                    HydrographRes res = new HydrographRes();
                    res.setName(t.getMonitorName());
                    res.setTime(sdf.format(t.getMonitorTime()));
                    res.setFlow(t.getSqMonitorFlow()==null?(t.getGdMonitorFlow()==null?null:t.getGdMonitorFlow()):t.getSqMonitorFlow());
                    res.setRainfall(t.getYqRainFallOne()==null?BigDecimal.ZERO:new BigDecimal(t.getYqRainFallOne()));
                    res.setWaterLevel(t.getSqWaterLevel());
                    resList.add(res);
                });
            }
            List<LzzRainfallStation> listLzzRain = lzzRainfallStationService.selectInfoByCondition(byId.getMonitorId(),null,req.getStartTime()+" 00:00",req.getEndTime()+" 23:00");
            if(null != listLzzRain && listLzzRain.size()>0){
                listLzzRain.stream().collect(Collectors.toList()).forEach(t->{
                    HydrographRes res = new HydrographRes();
                    res.setName(t.getStationName());
                    res.setTime(sdf.format(t.getTime()));
                    res.setRainfall(t.getRainfall());
                    res.setTemperature(t.getTemperature());
                    resList.add(res);
                });
            }
            List<LzzGaugingStation> listLzzGaugingStation = lzzGaugingStationService.selectInfoByCondition(byId.getMonitorId(),null,req.getStartTime(),req.getEndTime());
            if(null != listLzzGaugingStation && listLzzGaugingStation.size()>0){
                listLzzGaugingStation.stream().collect(Collectors.toList()).forEach(t->{
                    HydrographRes res = new HydrographRes();
                    res.setName(t.getStationName());
                    res.setTime(sdf.format(t.getGatherTime()));
                    res.setFlow(t.getFlow());
                    res.setWaterLevel(t.getRelativeWaterLevel());
                    resList.add(res);
                });
            }
        }else {
            List<HydrographRes> hydrographResList = allService.selectInfoListAllNew(req);
            if(null!=hydrographResList){
                resList.addAll(hydrographResList);
            }
        }
        if(resList.isEmpty()){
            return RestResponse.no("暂无数据");
        }else {
            return RestResponse.ok(resList);
        }
    }

    @Override
    public RestResponse selectInfoListByIdsNew(SelectInfoListByIdsReq req) {
        List<FlowRes> result = new ArrayList<>();
        List<FlowRes> flowResList = new ArrayList<>();
        List<HydrographRes> resList = new ArrayList<>();
        String[] split = req.getIds().split(",");
        for (String id : split){
            OverallSituationUnitMgr byId = overallSituationUnitMgrService.getById(id);
            if(null == byId){
                continue;
            }
            if(StringUtils.isNotEmpty(byId.getMonitorId())){
                List<IrrigatedPlatformDataInfo> listTth = irrigatedPlatformDataInfoService.selectInfoByCondition(byId.getMonitorId(),null,req.getStartTime(),req.getEndTime());
                if(null != listTth && listTth.size()>0) {
                    listTth.stream().collect(Collectors.toList()).forEach(t->{
                        HydrographRes res = new HydrographRes();
                        res.setName(t.getMonitorName());
                        res.setTime(sdf1.format(t.getMonitorTime()));
                        res.setFlow(t.getSqMonitorFlow()==null?(t.getGdMonitorFlow()==null?null:t.getGdMonitorFlow()):t.getSqMonitorFlow());
                        res.setRainfall(t.getYqRainFallOne()==null?BigDecimal.ZERO:new BigDecimal(t.getYqRainFallOne()));
                        res.setWaterLevel(t.getSqWaterLevel());
                        resList.add(res);
                    });
                }
                List<LzzRainfallStation> listLzzRain = lzzRainfallStationService.selectInfoByCondition(byId.getMonitorId(),null,req.getStartTime(),req.getEndTime());
                if(null != listLzzRain && listLzzRain.size()>0){
                    listLzzRain.stream().collect(Collectors.toList()).forEach(t->{
                        HydrographRes res = new HydrographRes();
                        res.setName(t.getStationName());
                        res.setTime(sdf1.format(t.getTime()));
                        res.setRainfall(t.getRainfall());
                        res.setTemperature(t.getTemperature());
                        resList.add(res);
                    });
                }
                List<LzzGaugingStation> listLzzGaugingStation = lzzGaugingStationService.selectInfoByCondition(byId.getMonitorId(),null,req.getStartTime(),req.getEndTime());
                if(null != listLzzGaugingStation && listLzzGaugingStation.size()>0){
                    listLzzGaugingStation.stream().collect(Collectors.toList()).forEach(t->{
                        HydrographRes res = new HydrographRes();
                        res.setName(t.getStationName());
                        res.setTime(sdf1.format(t.getGatherTime()));
                        res.setFlow(t.getFlow());
                        res.setWaterLevel(t.getRelativeWaterLevel());
                        resList.add(res);
                    });
                }
            }else {
                SelectInfoListNewReq selectInfoListNewReq = new SelectInfoListNewReq();
                selectInfoListNewReq.setStartTime(req.getStartTime());
                selectInfoListNewReq.setEndTime(req.getEndTime());
                selectInfoListNewReq.setId(id);
                List<HydrographRes> hydrographResList = allService.selectInfoListByIdsNew(selectInfoListNewReq);
                if(null!=hydrographResList){
                    resList.addAll(hydrographResList);
                }
            }
        }
        Map<String, List<HydrographRes>> collect = resList.stream().collect(Collectors.groupingBy(HydrographRes::getTime));
        Set<String> strings = collect.keySet();
        for(String date :strings){
            FlowRes flowRes = new FlowRes();
            flowRes.setTime(date);
            Double aDouble = collect.get(date).stream().map(HydrographRes::getFlow).reduce(Double::sum).orElse(0.00);
            flowRes.setFlow(NumberUtil.holdDecimal(aDouble,3));
            result.add(flowRes);

        }
        if(result.isEmpty()){
            return RestResponse.no("暂无数据");
        }else {
            Comparator<FlowRes> comparing = Comparator.comparing(FlowRes::getTime);
            Collections.sort(result, comparing.reversed());
            /*String endTime = result.get(0).getTime();
            String startTime = result.get(result.size()-1).getTime();
            Map<String, List<FlowRes>> collect1 = result.stream().collect(Collectors.groupingBy(FlowRes::getTime));
            String[] split1_1 = startTime.split(" ");
            String[] split2_1 = split1_1[0].split(":");
            String[] split3_1 = split1_1[1].split(":");
            String[] split1_2 = endTime.split(" ");
            String[] split2_2 = split1_2[0].split(":");
            String[] split3_2 = split1_2[1].split(":");
            LocalDateTime start = LocalDateTime.of(Integer.valueOf(split2_1[0]), Integer.valueOf(split2_1[1]), Integer.valueOf(split2_1[2]), Integer.valueOf(split3_1[0]), Integer.valueOf(split3_1[1]));
            LocalDateTime end = LocalDateTime.of(Integer.valueOf(split2_2[0]), Integer.valueOf(split2_2[1]), Integer.valueOf(split2_2[2]), Integer.valueOf(split3_2[0]), Integer.valueOf(split3_2[1]));

            LocalDateTime currentTime = start;
            while (!currentTime.isAfter(end)) {
                FlowRes flowRes = new FlowRes();
                String formattedDateTime = currentTime.format(formatter);
                List<FlowRes> flowResList1 = collect1.get(formattedDateTime);
                if(flowResList1==null && flowResList1.size()==0){
                    flowRes.setFlow(null);
                    flowRes.setTime(formattedDateTime);
                }else {
                    flowRes.setFlow(flowResList1.stream().map(FlowRes::getFlow).reduce(Double::sum).orElse(0.00));
                    flowRes.setTime(formattedDateTime);
                }
                flowResList.add(flowRes);
                currentTime = currentTime.plusMinutes(60);
            }*/
            return RestResponse.ok(result);
        }
    }

    @Override
    public RestResponse selectTodayWaterSituation(String date) {
        SaBaseLoginUser saBaseLoginUser = StpLoginUserUtil.getLoginUser();
        Map<String,List<SelectTodayWaterSituationRes>> resMap = new HashMap<>();
        if(saBaseLoginUser.getOrgName().equals("河东管理站")){
            IrrigatedPlatformTree hdParent = irrigatedPlatformTreeService.lambdaQuery().eq(IrrigatedPlatformTree::getName, "河东站").eq(IrrigatedPlatformTree::getNodetype, "GroupType").one();
            List<IrrigatedPlatformTree> hdList = irrigatedPlatformTreeService.lambdaQuery().eq(IrrigatedPlatformTree::getParentId, hdParent.getId()).list();
            List<String> strings = hdList.stream().map(IrrigatedPlatformTree::getId).collect(Collectors.toList());
            List<SelectTodayWaterSituationRes> selectTodayWaterSituationRes = irrigatedPlatformDataInfoService.selectTodayWaterSituation(strings, date, strings.size());
            if(selectTodayWaterSituationRes.isEmpty()){
                return RestResponse.no("暂无数据");
            }else {
                resMap.put("河东",selectTodayWaterSituationRes);
                return RestResponse.ok(resMap);
            }
        }
        if(saBaseLoginUser.getOrgName().equals("河西管理站")){
            IrrigatedPlatformTree hxParent = irrigatedPlatformTreeService.lambdaQuery().eq(IrrigatedPlatformTree::getName, "河西水利工程站").eq(IrrigatedPlatformTree::getNodetype, "GroupType").one();
            List<IrrigatedPlatformTree> hxList = irrigatedPlatformTreeService.lambdaQuery().eq(IrrigatedPlatformTree::getParentId, hxParent.getId()).list();
            List<String> strings = hxList.stream().map(IrrigatedPlatformTree::getId).collect(Collectors.toList());
            List<SelectTodayWaterSituationRes> selectTodayWaterSituationRes = irrigatedPlatformDataInfoService.selectTodayWaterSituation(strings, date, strings.size());
            if(selectTodayWaterSituationRes.isEmpty()){
                return RestResponse.no("暂无数据");
            }else {
                resMap.put("河西",selectTodayWaterSituationRes);
                return RestResponse.ok(resMap);
            }
        }
        if(saBaseLoginUser.getOrgName().equals("渠首管理站")){
            IrrigatedPlatformTree qsParent = irrigatedPlatformTreeService.lambdaQuery().eq(IrrigatedPlatformTree::getName, "渠首站").eq(IrrigatedPlatformTree::getNodetype, "GroupType").one();
            List<IrrigatedPlatformTree> qsList = irrigatedPlatformTreeService.lambdaQuery().eq(IrrigatedPlatformTree::getParentId, qsParent.getId()).list();
            List<String> strings = qsList.stream().map(IrrigatedPlatformTree::getId).collect(Collectors.toList());
            List<SelectTodayWaterSituationRes> selectTodayWaterSituationRes = irrigatedPlatformDataInfoService.selectTodayWaterSituation(strings, date, strings.size());
            if(selectTodayWaterSituationRes.isEmpty()){
                return RestResponse.no("暂无数据");
            }else {
                resMap.put("渠首",selectTodayWaterSituationRes);
                return RestResponse.ok(resMap);
            }
        }
        if(saBaseLoginUser.getOrgName().equals("头屯河水库")){
            IrrigatedPlatformTree tthParent = irrigatedPlatformTreeService.lambdaQuery().eq(IrrigatedPlatformTree::getName, "水库站").eq(IrrigatedPlatformTree::getNodetype, "GroupType").one();
            List<IrrigatedPlatformTree> tthList = irrigatedPlatformTreeService.lambdaQuery().eq(IrrigatedPlatformTree::getParentId, tthParent.getId()).in(IrrigatedPlatformTree::getName,"入库流量","出库流量").list();
            List<String> strings = tthList.stream().map(IrrigatedPlatformTree::getId).collect(Collectors.toList());
            List<SelectTodayWaterSituationRes> selectTodayWaterSituationRes = irrigatedPlatformDataInfoService.selectTodayWaterSituation(strings, date, strings.size());
            if(selectTodayWaterSituationRes.isEmpty()){
                return RestResponse.no("暂无数据");
            }else {
                resMap.put("头屯河",selectTodayWaterSituationRes);
                return RestResponse.ok(resMap);
            }
        }
        if(saBaseLoginUser.getOrgName().equals("楼庄子水库")){
            List<LzzPlatformTree> lzzPlatformTrees = lzzPlatformTreeService.lambdaQuery().in(LzzPlatformTree::getName, "出库自动水位站", "入库自动水位站", "楼庄子水厂1号管道", "楼庄子水厂2号管道").list();
            List<String> strings = lzzPlatformTrees.stream().map(LzzPlatformTree::getId).collect(Collectors.toList());
            List<SelectTodayWaterSituationRes> selectTodayWaterSituationRes = lzzGaugingStationService.selectTodayWaterSituation(strings, date);
            if(selectTodayWaterSituationRes.isEmpty()){
                return RestResponse.no("暂无数据");
            }else {
                resMap.put("楼庄子",selectTodayWaterSituationRes);
                return RestResponse.ok(resMap);
            }
        }
        Map<String, List<SelectTodayWaterSituationRes>> stringListMap = allData(date);
        if(stringListMap !=null){
            return RestResponse.ok(stringListMap);
        }else {
            return RestResponse.no("今日暂无有效数据");
        }
    }

    private Map<String,List<SelectTodayWaterSituationRes>> allData(String date){
        Map<String,List<SelectTodayWaterSituationRes>> resMap = new LinkedHashMap<>();
        List<LzzPlatformTree> lzzPlatformTrees = lzzPlatformTreeService.lambdaQuery().in(LzzPlatformTree::getName, "出库自动水位站", "入库自动水位站", "楼庄子水厂1号管道", "楼庄子水厂2号管道").list();
        List<String> lzzIds = lzzPlatformTrees.stream().map(LzzPlatformTree::getId).collect(Collectors.toList());
        List<SelectTodayWaterSituationRes> resultLzz = lzzGaugingStationService.selectTodayWaterSituation(lzzIds, date);
        if(resultLzz.isEmpty()){
            resMap.put("楼庄子_1",null);
        }else {
            resMap.put("楼庄子_1",resultLzz);
        }

        IrrigatedPlatformTree tthParent = irrigatedPlatformTreeService.lambdaQuery().eq(IrrigatedPlatformTree::getName, "水库站").eq(IrrigatedPlatformTree::getNodetype, "GroupType").one();
        List<IrrigatedPlatformTree> tthList = irrigatedPlatformTreeService.lambdaQuery().eq(IrrigatedPlatformTree::getParentId, tthParent.getId()).in(IrrigatedPlatformTree::getName,"入库流量","出库流量").list();
        List<String> tthIds = tthList.stream().map(IrrigatedPlatformTree::getId).collect(Collectors.toList());
        List<SelectTodayWaterSituationRes> resultTth = irrigatedPlatformDataInfoService.selectTodayWaterSituation(tthIds, date, tthIds.size());
        if(resultTth.isEmpty()){
            resMap.put("头屯河_2",null);
        }else {
            resMap.put("头屯河_2",resultTth);
        }

        IrrigatedPlatformTree qsParent = irrigatedPlatformTreeService.lambdaQuery().eq(IrrigatedPlatformTree::getName, "渠首站").eq(IrrigatedPlatformTree::getNodetype, "GroupType").one();
        List<IrrigatedPlatformTree> qsList = irrigatedPlatformTreeService.lambdaQuery().eq(IrrigatedPlatformTree::getParentId, qsParent.getId()).list();
        List<String> qsIds = qsList.stream().map(IrrigatedPlatformTree::getId).collect(Collectors.toList());
        List<SelectTodayWaterSituationRes> resultQs = irrigatedPlatformDataInfoService.selectTodayWaterSituation(qsIds, date, qsIds.size());
        if(resultQs.isEmpty()){
            resMap.put("渠首_3",null);
        }else {
            resMap.put("渠首_3",resultQs);
        }
        IrrigatedPlatformTree hdParent = irrigatedPlatformTreeService.lambdaQuery().eq(IrrigatedPlatformTree::getName, "河东站").eq(IrrigatedPlatformTree::getNodetype, "GroupType").one();
        List<IrrigatedPlatformTree> hdList = irrigatedPlatformTreeService.lambdaQuery().eq(IrrigatedPlatformTree::getParentId, hdParent.getId()).list();
        List<String> hdIds = hdList.stream().map(IrrigatedPlatformTree::getId).collect(Collectors.toList());
        List<SelectTodayWaterSituationRes> resultHd = irrigatedPlatformDataInfoService.selectTodayWaterSituation(hdIds, date, hdIds.size());
        if(resultHd.isEmpty()){
            resMap.put("河东_4",null);
        }else {
            resMap.put("河东_4",resultHd);
        }

        IrrigatedPlatformTree hxParent = irrigatedPlatformTreeService.lambdaQuery().eq(IrrigatedPlatformTree::getName, "河西水利工程站").eq(IrrigatedPlatformTree::getNodetype, "GroupType").one();
        List<IrrigatedPlatformTree> hxList = irrigatedPlatformTreeService.lambdaQuery().eq(IrrigatedPlatformTree::getParentId, hxParent.getId()).list();
        List<String> hxIds = hxList.stream().map(IrrigatedPlatformTree::getId).collect(Collectors.toList());
        List<SelectTodayWaterSituationRes> resultHx = irrigatedPlatformDataInfoService.selectTodayWaterSituation(hxIds, date, hxIds.size());
        if(resultHx.isEmpty()){
            resMap.put("河西_5",null);
        }else {
            resMap.put("河西_5",resultHx);
        }
        if(resMap.isEmpty()){
            return null;
        }
        return resMap;
    }

    public void getIrrigatedTree(List<IrrigatedPlatformTreeRes> resultList, List<IrrigatedPlatformTreeRes> list){
        if(resultList.size()>0){
            for(IrrigatedPlatformTreeRes res : resultList){
                List<IrrigatedPlatformTreeRes> collect = list.stream().filter(t -> t.getParentId().equals(res.getId())).collect(Collectors.toList());
                if(collect.size()>0){
                    List<IrrigatedPlatformTreeRes> tempList = new ArrayList<>();
                    for (IrrigatedPlatformTreeRes param:collect){
                        IrrigatedPlatformTreeRes tempRes = new IrrigatedPlatformTreeRes();
                        BeanUtils.copyProperties(param,tempRes);
                        tempList.add(tempRes);
                    }
                    res.setChildren(tempList);
                    getIrrigatedTree(tempList,list);
                }
            }
        }
    }

    public void getLzzTree(List<LzzPlatformTreeRes> resultList, List<LzzPlatformTreeRes> list){
        if(resultList.size()>0){
            for(LzzPlatformTreeRes res : resultList){
                List<LzzPlatformTreeRes> collect = list.stream().filter(t -> t.getPId().equals(res.getId())).collect(Collectors.toList());
                if(collect.size()>0){
                    List<LzzPlatformTreeRes> tempList = new ArrayList<>();
                    for (LzzPlatformTreeRes param:collect){
                        LzzPlatformTreeRes tempRes = new LzzPlatformTreeRes();
                        BeanUtils.copyProperties(param,tempRes);
                        tempList.add(tempRes);
                    }
                    res.setChildren(tempList);
                    getLzzTree(tempList,list);
                }
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
}
