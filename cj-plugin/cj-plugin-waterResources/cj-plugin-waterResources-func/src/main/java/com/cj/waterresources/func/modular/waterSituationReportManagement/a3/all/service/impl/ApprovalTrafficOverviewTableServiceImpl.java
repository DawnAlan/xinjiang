package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.auth.core.pojo.SaBaseLoginUser;
import com.cj.auth.core.util.StpLoginUserUtil;
import com.cj.common.model.RestResponse;
import com.cj.common.util.ExcelUtils;
import com.cj.common.util.RedisUtil;
import com.cj.common.util.UUIDUtils;
import com.cj.flood.api.PredictionApi;
import com.cj.model.func.core.util.MinioUtils;
import com.cj.model.func.core.util.MultipartFileUtil;
import com.cj.model.func.modular.watertransfer.entity.Excel2;
import com.cj.waterresources.func.core.utils.WebSocketServer;
import com.cj.waterresources.func.modular.trendsTable.entity.TrendsTableParam;
import com.cj.waterresources.func.modular.trendsTable.service.TrendsTableParamService;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.dayWaterUsePlan.entity.DayWaterUsePlan;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.dayWaterUsePlan.service.DayWaterUsePlanService;
import com.cj.waterresources.func.modular.waterResourceAllcation.bean.req.WaterResourceAllocationAddReq;
import com.cj.waterresources.func.modular.waterResourceAllcation.entity.WaterResourceAllocation;
import com.cj.waterresources.func.modular.waterResourceAllcation.entity.WaterSupplyPriority;
import com.cj.waterresources.func.modular.waterResourceAllcation.service.WaterResourceAllocationService;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.bean.req.ApprovalTrafficOverviewTableAddReq;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.bean.res.ApprovalTrafficRes;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.bean.vo.SynchronizationEightDataVo;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.entity.ApprovalTrafficOverview;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.mapper.ApprovalTrafficOverviewTableMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.entity.ApprovalTrafficOverviewTable;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.service.ApprovalTrafficOverviewService;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.service.ApprovalTrafficOverviewTableService;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.hd.entity.DayWaterSituationStatisticsTableHd;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.hd.mapper.DayWaterSituationStatisticsTableHdMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.hd.service.DayWaterSituationStatisticsTableHdService;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.hx.entity.DayWaterSituationStatisticsTableHx;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.hx.mapper.DayWaterSituationStatisticsTableHxMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.hx.service.DayWaterSituationStatisticsTableHxService;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.lzz.entity.DayWaterSituationStatisticsTableLzz;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.lzz.mapper.DayWaterSituationStatisticsTableLzzMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.lzz.service.DayWaterSituationStatisticsTableLzzService;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.qs.entity.DayWaterSituationStatisticsTableQs;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.qs.entity.DayWaterSituationStatisticsTableQsLh;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.qs.mapper.DayWaterSituationStatisticsTableQsLhMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.qs.mapper.DayWaterSituationStatisticsTableQsMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.qs.service.DayWaterSituationStatisticsTableQsLhService;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.qs.service.DayWaterSituationStatisticsTableQsService;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.tth.entity.DayWaterSituationStatisticsTableTth;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.tth.mapper.DayWaterSituationStatisticsTableTthMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.tth.service.DayWaterSituationStatisticsTableTthService;
import lombok.Data;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.poi.excel.sax.ElementName.v;

/**
 * 流量概览表(ApprovalTrafficOverviewTable)表服务实现类
 *
 * @author makejava
 * @since 2024-04-09 16:34:48
 */
@Service("approvalTrafficOverviewTableService")
public class ApprovalTrafficOverviewTableServiceImpl extends ServiceImpl<ApprovalTrafficOverviewTableMapper, ApprovalTrafficOverviewTable> implements ApprovalTrafficOverviewTableService {

    @Autowired
    private ApprovalTrafficOverviewService approvalTrafficOverviewService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private MinioUtils minioUtils;

    @Autowired
    private WaterResourceAllocationService waterResourceAllocationService;

    @Autowired
    private DayWaterSituationStatisticsTableTthMapper tthMapper;

    @Autowired
    private DayWaterSituationStatisticsTableLzzMapper lzzMapper;

    @Autowired
    private DayWaterSituationStatisticsTableQsMapper qsMapper;

    @Autowired
    private DayWaterSituationStatisticsTableQsLhMapper qsLhMapper;

    @Autowired
    private DayWaterSituationStatisticsTableHdMapper hdMapper;

    @Autowired
    private DayWaterSituationStatisticsTableHxMapper hxMapper;

    @Autowired
    private DayWaterUsePlanService dayWaterUsePlanService;

    @Autowired
    private PredictionApi predictionApi;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHHmmss");
    private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    @SneakyThrows
    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse add(ApprovalTrafficOverviewTableAddReq req) {
        List<ApprovalTrafficOverviewTable> list = this.lambdaQuery().eq(ApprovalTrafficOverviewTable::getTime, req.getTime()).eq(ApprovalTrafficOverviewTable::getName, req.getName()).list();
        if(!list.isEmpty()){
            return RestResponse.no("请勿重复创建相同名称方案！");
        }
        SaBaseLoginUser saBaseLoginUser = StpLoginUserUtil.getLoginUser();
        ApprovalTrafficOverviewTable approvalTrafficOverviewTable = new ApprovalTrafficOverviewTable();
        approvalTrafficOverviewTable.setName(req.getName());
        approvalTrafficOverviewTable.setId(UUIDUtils.getUUID());
        approvalTrafficOverviewTable.setTime(sdf.parse(req.getTime()));
        approvalTrafficOverviewTable.setModelId(req.getModelId());
        approvalTrafficOverviewTable.setModelName(req.getModelName());
        boolean save = this.save(approvalTrafficOverviewTable);
        Date date = new Date();
        if (save) {
            Boolean aBoolean = false;
            if (req.getIsAuto()){
                try {
                    String incomingWaterId = predictionApi.autoGenerate(calculateDay(req.getTime(), +1)+" "+sdf2.format(date).split(" ")[1]);
                    if(StringUtils.isNotEmpty(incomingWaterId)){
                        String incomingWaterAddress = predictionApi.selectModelAddressById(incomingWaterId);
                        WebSocketServer.sendInfo("来水预报:1",saBaseLoginUser.getId());
                        String[] split = incomingWaterAddress.split(" ");
                        try {
                            WaterResourceAllocationAddReq resourceAllocationAddReq = new WaterResourceAllocationAddReq();
                            resourceAllocationAddReq.setBucketType(4);
                            resourceAllocationAddReq.setInflowDataAddress(split[0]);
                            resourceAllocationAddReq.setInflowDataId(incomingWaterId);
                            resourceAllocationAddReq.setInflowDataName(split[1]);
                            resourceAllocationAddReq.setInflowDataType(0);
                            resourceAllocationAddReq.setLevelBeginLzz(req.getLevelBeginLzz());
                            resourceAllocationAddReq.setLevelEndLzz(req.getLevelEndLzz());
                            resourceAllocationAddReq.setLevelBeginTth(req.getLevelBeginTth());
                            resourceAllocationAddReq.setLevelEndTth(req.getLevelEndTth());
                            resourceAllocationAddReq.setSchemeName(sdf1.format(sdf.parse(calculateDay(req.getTime(), +1))+" "+sdf2.format(date).split(" ")[1])+"一键[单日配水]");
                            resourceAllocationAddReq.setWaterDistributionType(1);
                            resourceAllocationAddReq.setWaterDistributionStartTime(sdf2.parse(req.getTime()+" 00:00:00"));
                            resourceAllocationAddReq.setWaterDistributionEndTime(sdf2.parse(req.getTime()+" 23:59:59"));
                            resourceAllocationAddReq.setEcologyFlowLzz(Arrays.asList(0.74, 0.74, 0.74, 1.48, 1.48, 1.48, 1.48, 1.48, 1.48, 0.74, 0.74, 0.74));
                            resourceAllocationAddReq.setEcologyFlowTth(Arrays.asList(0.00, 0.00, 0.00, 0.00, 0.74, 0.74, 0.74, 0.74, 0.74, 0.74, 0.74, 0.00));
                            resourceAllocationAddReq.setFloodWaterLevelLzz(Arrays.asList(1394.5, 1394.5, 1394.5, 1394.5, 1394.5, 1394.5, 1394.5, 1394.5, 1394.5, 1394.5, 1394.5, 1394.5));
                            resourceAllocationAddReq.setFloodWaterLevelTth(Arrays.asList(988.0, 988.0, 988.0, 988.0, 988.0, 988.0, 988.0, 988.0, 988.0, 988.0, 988.0, 988.0));
                            resourceAllocationAddReq.setMinWaterLevelLzz(Arrays.asList(1353.3, 1353.3, 1353.3, 1353.3, 1353.3, 1353.3, 1353.3, 1353.3, 1353.3, 1353.3, 1353.3, 1353.3));
                            resourceAllocationAddReq.setMinWaterLevelTth(Arrays.asList(975.0, 975.0, 975.0, 975.0, 975.0, 975.0, 975.0, 975.0, 975.0, 975.0, 975.0, 975.0));
                            resourceAllocationAddReq.setWaterSupplyPriorityList(Arrays.asList(
                                    new WaterSupplyPriority("生态用水",0,1,0),
                                    new WaterSupplyPriority("生活用水",1,2,0),
                                    new WaterSupplyPriority("工业用水",2,3,0),
                                    new WaterSupplyPriority("灌溉用水",3,4,1),
                                    new WaterSupplyPriority("绿化用水",4,5,1)
                            ));
                            String waterResourceAllocationId = waterResourceAllocationService.autoGenerate(resourceAllocationAddReq);
                            if(StringUtils.isNotEmpty(waterResourceAllocationId)){
                                WebSocketServer.sendInfo("水资源调配:1",saBaseLoginUser.getId());
                                aBoolean = insertApprovalTrafficOverview(req.getModelId(), approvalTrafficOverviewTable.getId(), approvalTrafficOverviewTable.getTime());
                            }else {
                                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                                WebSocketServer.sendInfo("水资源调配:2",saBaseLoginUser.getId());
                            }
                        }catch (Exception e){
                            log.error("自动生成水资源调配方案异常:"+e.getMessage());
                            WebSocketServer.sendInfo("水资源调配:2",saBaseLoginUser.getId());
                            e.printStackTrace();
                            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        }
                    }else {
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        WebSocketServer.sendInfo("来水预报:2",saBaseLoginUser.getId());
                        WebSocketServer.sendInfo("水资源调配:2",saBaseLoginUser.getId());
                    }
                }catch (Exception e){
                    log.error("自动生成来水预报方案异常:"+e.getMessage());
                    WebSocketServer.sendInfo("来水预报:2",saBaseLoginUser.getId());
                    e.printStackTrace();
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                }

            }else {
                aBoolean = insertApprovalTrafficOverview(req.getModelId(), approvalTrafficOverviewTable.getId(), approvalTrafficOverviewTable.getTime());
            }
            if (aBoolean) {
                WebSocketServer.sendInfo("流量概览:1",saBaseLoginUser.getId());
                return RestResponse.ok();
            }else {
                WebSocketServer.sendInfo("流量概览:2",saBaseLoginUser.getId());
                return RestResponse.no("添加流量概览表失败");
            }
        } else {
            return RestResponse.no("添加失败");
        }
    }

    @Override
    public RestResponse delete(String id) {
        boolean b = this.removeById(id);
        if (b) {
            Long count = approvalTrafficOverviewService.lambdaQuery().eq(ApprovalTrafficOverview::getOverviewId, id).count();
            if (count > 0) {
                boolean remove = approvalTrafficOverviewService.lambdaUpdate().eq(ApprovalTrafficOverview::getOverviewId, id).remove();
                if (remove) {
                    return RestResponse.ok();
                } else {
                    return RestResponse.no("删除流量概览表失败");
                }
            } else {
                return RestResponse.ok();
            }
        } else {
            return RestResponse.no("删除失败");
        }
    }

    @Override
    public RestResponse selectList(String time, String name) {
        List<ApprovalTrafficOverviewTable> list = this.lambdaQuery().like(StringUtils.isNotEmpty(name), ApprovalTrafficOverviewTable::getName, name).
                eq(StringUtils.isNotEmpty(time), ApprovalTrafficOverviewTable::getTime,time).list();
        if (list != null && list.size() > 0) {
            return RestResponse.ok(list);
        } else {
            return RestResponse.no("查询失败");
        }
    }

    @Override
    public RestResponse synchronizationEightData(String id) {
        List<ApprovalTrafficOverview> list = approvalTrafficOverviewService.lambdaQuery().eq(ApprovalTrafficOverview::getOverviewId, id).list();
        if(list.isEmpty()){
            return RestResponse.no("当前方案无数数据");
        }
        ApprovalTrafficOverviewTable byId = this.getById(id);
        String format = sdf.format(byId.getTime());
        List<SynchronizationEightDataVo> voList  = new ArrayList<>();
        List<DayWaterSituationStatisticsTableTth> tthList = tthMapper.selectForApproval(format);
        if (!tthList.isEmpty()) {
            String endTableList = tthList.get(0).getEndTableList();
            String[] split = endTableList.split(",");
            for (String s : split) {
                SynchronizationEightDataVo vo = new SynchronizationEightDataVo();
                String tableParamString = (String) redisUtil.get("trendsTableParam:object:" + s);
                TrendsTableParam tableParam = JSONObject.parseObject(tableParamString, TrendsTableParam.class);
                if (tableParam.getParamName().equals("进库流量") || tableParam.getParamName().equals("河道流量")) {
                    Double eight = tthList.stream().filter(t -> t.getTableHeadId().equals(s) && t.getV() != null).map(DayWaterSituationStatisticsTableTth::getV).reduce(Double::sum).orElse(0.00);
                    vo.setId(s);
                    vo.setV(eight);
                }
                if(vo.getId()!=null){
                    voList.add(vo);
                }
            }
        }

        List<DayWaterSituationStatisticsTableLzz> lzzList = lzzMapper.selectForApproval(format);;
        if (!lzzList.isEmpty()) {
            String endTableList = lzzList.get(0).getEndTableList();
            String[] split = endTableList.split(",");
            for (String s : split) {
                SynchronizationEightDataVo vo = new SynchronizationEightDataVo();
                String tableParamString = (String) redisUtil.get("trendsTableParam:object:" + s);
                TrendsTableParam tableParam = JSONObject.parseObject(tableParamString, TrendsTableParam.class);
                if (tableParam.getParamName().equals("进库流量") || tableParam.getParamName().equals("河道")) {
                    Double eight = lzzList.stream().filter(t -> t.getTableHeadId().equals(s) && t.getV() != null).map(DayWaterSituationStatisticsTableLzz::getV).reduce(Double::sum).orElse(0.00);
                    vo.setId(s);
                    vo.setV(eight);
                }
                if(vo.getId()!=null){
                    voList.add(vo);
                }
            }
        }
        DayWaterUsePlan qs = dayWaterUsePlanService.lambdaQuery().eq(DayWaterUsePlan::getRecordTime, format).eq(DayWaterUsePlan::getArea, "渠首管理站").last("limit 1").one();
        if (null != qs) {

            List<DayWaterSituationStatisticsTableQs> qsList = qsMapper.selectForApproval(format);
            List<DayWaterSituationStatisticsTableQsLh> qsLhList = qsLhMapper.selectForApproval(format);
            String json = qs.getV();
            List<ApprovalTrafficRes> approvalTrafficRes = JSONObject.parseArray(json, ApprovalTrafficRes.class);
            for (ApprovalTrafficRes res : approvalTrafficRes) {
                SynchronizationEightDataVo vo = new SynchronizationEightDataVo();
                Double eightQs = qsList.stream().filter(t -> t.getTableHeadId().equals(res.getUnitId()) && t.getV() != null).map(DayWaterSituationStatisticsTableQs::getV).reduce(Double::sum).orElse(0.00);
                Double eightQsLh = qsLhList.stream().filter(t -> t.getTableHeadId().equals(res.getUnitId()) && t.getV() != null).map(DayWaterSituationStatisticsTableQsLh::getV).reduce(Double::sum).orElse(0.00);
                vo.setId(res.getUnitId());
                vo.setV(formatDouble( eightQs+eightQsLh));
                for (ApprovalTrafficRes res1 : res.getChildren()) {
                    SynchronizationEightDataVo vo1 = new SynchronizationEightDataVo();
                    Double eightQs1 = qsList.stream().filter(t -> t.getTableHeadId().equals(res1.getUnitId()) && t.getV() != null).map(DayWaterSituationStatisticsTableQs::getV).reduce(Double::sum).orElse(0.00);
                    Double eightQsLh1 = qsLhList.stream().filter(t -> t.getTableHeadId().equals(res1.getUnitId()) && t.getV() != null).map(DayWaterSituationStatisticsTableQsLh::getV).reduce(Double::sum).orElse(0.00);
                    vo1.setId(res1.getUnitId());
                    vo1.setV(formatDouble( eightQs1+eightQsLh1));
                    if(vo1.getId()!=null){
                        voList.add(vo1);
                    }
                }
                if(vo.getId()!=null){
                    voList.add(vo);
                }
            }
        }
        DayWaterUsePlan hd = dayWaterUsePlanService.lambdaQuery().eq(DayWaterUsePlan::getRecordTime, format).eq(DayWaterUsePlan::getArea, "河东管理站").last("limit 1").one();
        if (null != hd) {
            List<DayWaterSituationStatisticsTableHd> hdList = hdMapper.selectForApproval(format);
            String json = hd.getV();
            List<ApprovalTrafficRes> approvalTrafficRes = JSONObject.parseArray(json, ApprovalTrafficRes.class);
            for (ApprovalTrafficRes res : approvalTrafficRes) {
                SynchronizationEightDataVo vo = new SynchronizationEightDataVo();
                Double eightHd = hdList.stream().filter(t -> t.getTableHeadId().equals(res.getUnitId()) && t.getV() != null).map(DayWaterSituationStatisticsTableHd::getV).reduce(Double::sum).orElse(0.00);
                vo.setId(res.getUnitId());
                vo.setV(formatDouble( eightHd));
                for(ApprovalTrafficRes res1 :res.getChildren()){
                    SynchronizationEightDataVo vo1 = new SynchronizationEightDataVo();
                    Double eightHd1 = hdList.stream().filter(t -> t.getTableHeadId().equals(res1.getUnitId()) && t.getV() != null).map(DayWaterSituationStatisticsTableHd::getV).reduce(Double::sum).orElse(0.00);
                    vo1.setId(res1.getUnitId());
                    vo1.setV(formatDouble( eightHd1));
                    if(vo1.getId()!=null){
                        voList.add(vo1);
                    }
                }
                if(vo.getId()!=null){
                    voList.add(vo);
                }
            }
        }
        DayWaterUsePlan hx = dayWaterUsePlanService.lambdaQuery().eq(DayWaterUsePlan::getRecordTime, format).eq(DayWaterUsePlan::getArea, "河西管理站").last("limit 1").one();
        if (null != hx) {
            List<DayWaterSituationStatisticsTableHx> hxList = hxMapper.selectForApproval(format);
            String json = hx.getV();
            List<ApprovalTrafficRes> approvalTrafficRes = JSONObject.parseArray(json, ApprovalTrafficRes.class);
            for (ApprovalTrafficRes res : approvalTrafficRes) {
                SynchronizationEightDataVo vo = new SynchronizationEightDataVo();
                Double eightHx = hxList.stream().filter(t -> t.getTableHeadId().equals(res.getUnitId()) && t.getV() != null).map(DayWaterSituationStatisticsTableHx::getV).reduce(Double::sum).orElse(0.00);
                vo.setId(res.getUnitId());
                vo.setV(formatDouble( eightHx));
                for(ApprovalTrafficRes res1 :res.getChildren()){
                    SynchronizationEightDataVo vo1 = new SynchronizationEightDataVo();
                    Double eightHx1 = hxList.stream().filter(t -> t.getTableHeadId().equals(res1.getUnitId()) && t.getV() != null).map(DayWaterSituationStatisticsTableHx::getV).reduce(Double::sum).orElse(0.00);
                    vo1.setId(res1.getUnitId());
                    vo1.setV(formatDouble( eightHx1));
                    if(vo1.getId()!=null){
                        voList.add(vo1);
                    }
                }
                if(vo.getId()!=null){
                    voList.add(vo);
                }

            }
        }
        list.forEach(t->{
            t.setEightFlow(voList.stream().filter(v -> v.getId().equals(t.getStationId()) && v.getV() != null).map(SynchronizationEightDataVo::getV).reduce(Double::sum).orElse(0.00));
        });
        boolean b = approvalTrafficOverviewService.updateBatchById(list);
        if(b){
            return RestResponse.ok();
        }else {
            return RestResponse.no("同步失败");
        }
    }

    @SneakyThrows
    private Boolean insertApprovalTrafficOverview(String modelId, String id, Date time) {
        List<ApprovalTrafficOverview> resultList = new ArrayList<>();
        Map<String,List<ApprovalTrafficOverview>> resultMap = new HashMap<>();
        String format = sdf.format(time);
        List<DayWaterSituationStatisticsTableTth> tthList = tthMapper.selectForApproval(format);
        if (!tthList.isEmpty()) {
            List<ApprovalTrafficOverview> approvalTrafficOverviewTthList = new ArrayList<>();
            String endTableList = tthList.get(0).getEndTableList();
            String[] split = endTableList.split(",");
            for (String s : split) {
                String tableParamString = (String) redisUtil.get("trendsTableParam:object:" + s);
                TrendsTableParam tableParam = JSONObject.parseObject(tableParamString, TrendsTableParam.class);
                if (tableParam.getParamName().equals("进库流量") || tableParam.getParamName().equals("河道流量")) {
                    ApprovalTrafficOverview approvalTrafficOverview = new ApprovalTrafficOverview();
                    approvalTrafficOverview.setId(UUIDUtils.getUUID());
                    approvalTrafficOverview.setStationId(s);
                    approvalTrafficOverview.setOverviewId(id);
                    approvalTrafficOverview.setStationPid("0");
                    approvalTrafficOverview.setStationName(tableParam.getParamName());
                    Double eight = tthList.stream().filter(t -> t.getTableHeadId().equals(s) && t.getV() != null).map(DayWaterSituationStatisticsTableTth::getV).reduce(Double::sum).orElse(0.00);
                    approvalTrafficOverview.setEightFlow(eight);
                    approvalTrafficOverview.setReservoir("头屯河水库");
                    approvalTrafficOverviewTthList.add(approvalTrafficOverview);
                }
            }
            resultList.addAll(approvalTrafficOverviewTthList);
            resultMap.put("头屯河水库",approvalTrafficOverviewTthList);
        }
        List<DayWaterSituationStatisticsTableLzz> lzzList = lzzMapper.selectForApproval(format);;
        if (!lzzList.isEmpty()) {
            List<ApprovalTrafficOverview> approvalTrafficOverviewLzzList = new ArrayList<>();
            String endTableList = lzzList.get(0).getEndTableList();
            String[] split = endTableList.split(",");
            for (String s : split) {
                String tableParamString = (String) redisUtil.get("trendsTableParam:object:" + s);
                TrendsTableParam tableParam = JSONObject.parseObject(tableParamString, TrendsTableParam.class);
                if (tableParam.getParamName().equals("进库流量") || tableParam.getParamName().equals("河道")) {
                    ApprovalTrafficOverview approvalTrafficOverview = new ApprovalTrafficOverview();
                    approvalTrafficOverview.setId(UUIDUtils.getUUID());
                    approvalTrafficOverview.setStationId(s);
                    approvalTrafficOverview.setOverviewId(id);
                    approvalTrafficOverview.setStationPid("0");
                    approvalTrafficOverview.setStationName(tableParam.getParamName());
                    Double eight = lzzList.stream().filter(t -> t.getTableHeadId().equals(s) && t.getV() != null).map(DayWaterSituationStatisticsTableLzz::getV).reduce(Double::sum).orElse(0.00);
                    approvalTrafficOverview.setEightFlow(eight);
                    approvalTrafficOverview.setReservoir("楼庄子水库");
                    approvalTrafficOverviewLzzList.add(approvalTrafficOverview);
                }
            }
            resultList.addAll(approvalTrafficOverviewLzzList);
            resultMap.put("楼庄子水库",approvalTrafficOverviewLzzList);
        }
        DayWaterUsePlan qs = dayWaterUsePlanService.lambdaQuery().eq(DayWaterUsePlan::getRecordTime, format).eq(DayWaterUsePlan::getArea, "渠首管理站").last("limit 1").one();
        if (null != qs) {
            List<ApprovalTrafficOverview> approvalTrafficOverviewQsList = new ArrayList<>();
            String json = qs.getV();
            List<ApprovalTrafficRes> approvalTrafficRes = JSONObject.parseArray(json, ApprovalTrafficRes.class);
            for (ApprovalTrafficRes res : approvalTrafficRes) {
                ApprovalTrafficOverview approvalTrafficOverview = new ApprovalTrafficOverview();
                approvalTrafficOverview.setId(UUIDUtils.getUUID());
                approvalTrafficOverview.setStationId(res.getUnitId());
                approvalTrafficOverview.setOverviewId(id);
                approvalTrafficOverview.setStationPid(res.getPid());
                approvalTrafficOverview.setStationName(res.getUnitName());
                approvalTrafficOverview.setEightFlow(Double.parseDouble(StringUtils.isEmpty(res.getFlow())?"0.00":res.getFlow()));
                approvalTrafficOverview.setAddSubtractFlow(Double.parseDouble(StringUtils.isEmpty(res.getWaterPlan())?"0.00":res.getWaterPlan()));
                approvalTrafficOverview.setPlanFlow(Double.parseDouble(StringUtils.isEmpty(res.getFlow())?"0.00":res.getFlow())+Double.parseDouble(StringUtils.isEmpty(res.getWaterPlan())?"0.00":res.getWaterPlan()));
                approvalTrafficOverview.setReservoir("渠首管理站");
                approvalTrafficOverviewQsList.add(approvalTrafficOverview);
                for (ApprovalTrafficRes res1 : res.getChildren()) {
                    ApprovalTrafficOverview approvalTrafficOverview1 = new ApprovalTrafficOverview();
                    approvalTrafficOverview1.setId(UUIDUtils.getUUID());
                    approvalTrafficOverview1.setStationId(res1.getUnitId());
                    approvalTrafficOverview1.setOverviewId(id);
                    approvalTrafficOverview1.setStationPid(res1.getPid());
                    approvalTrafficOverview1.setStationName(res1.getUnitName());
                    approvalTrafficOverview1.setEightFlow(Double.parseDouble(StringUtils.isEmpty(res1.getFlow())?"0.00":res1.getFlow()));
                    approvalTrafficOverview1.setAddSubtractFlow(Double.parseDouble(StringUtils.isEmpty(res1.getWaterPlan())?"0.00":res1.getWaterPlan()));
                    approvalTrafficOverview1.setPlanFlow(Double.parseDouble(StringUtils.isEmpty(res1.getFlow())?"0.00":res1.getFlow())+Double.parseDouble(StringUtils.isEmpty(res1.getWaterPlan())?"0.00":res1.getWaterPlan()));
                    approvalTrafficOverview1.setReservoir("渠首管理站");
                    approvalTrafficOverviewQsList.add(approvalTrafficOverview1);
                }
            }
            resultList.addAll(approvalTrafficOverviewQsList);
            resultMap.put("渠首管理站",approvalTrafficOverviewQsList);
        }
        DayWaterUsePlan hd = dayWaterUsePlanService.lambdaQuery().eq(DayWaterUsePlan::getRecordTime, format).eq(DayWaterUsePlan::getArea, "河东管理站").last("limit 1").one();
        if (null != hd) {
            List<ApprovalTrafficOverview> approvalTrafficOverviewHdList = new ArrayList<>();
            String json = hd.getV();
            List<ApprovalTrafficRes> approvalTrafficRes = JSONObject.parseArray(json, ApprovalTrafficRes.class);
            for (ApprovalTrafficRes res : approvalTrafficRes) {
                ApprovalTrafficOverview approvalTrafficOverview = new ApprovalTrafficOverview();
                approvalTrafficOverview.setId(UUIDUtils.getUUID());
                approvalTrafficOverview.setStationId(res.getUnitId());
                approvalTrafficOverview.setOverviewId(id);
                approvalTrafficOverview.setStationPid(res.getPid());
                approvalTrafficOverview.setStationName(res.getUnitName());
                approvalTrafficOverview.setEightFlow(Double.parseDouble(StringUtils.isEmpty(res.getFlow())?"0.00":res.getFlow()));
                approvalTrafficOverview.setAddSubtractFlow(Double.parseDouble(StringUtils.isEmpty(res.getWaterPlan())?"0.00":res.getWaterPlan()));
                approvalTrafficOverview.setPlanFlow(Double.parseDouble(StringUtils.isEmpty(res.getFlow())?"0.00":res.getFlow())+Double.parseDouble(StringUtils.isEmpty(res.getWaterPlan())?"0.00":res.getWaterPlan()));
                approvalTrafficOverview.setReservoir("河东管理站");
                approvalTrafficOverviewHdList.add(approvalTrafficOverview);
                for(ApprovalTrafficRes res1 :res.getChildren()){
                    ApprovalTrafficOverview approvalTrafficOverview1 = new ApprovalTrafficOverview();
                    approvalTrafficOverview1.setId(UUIDUtils.getUUID());
                    approvalTrafficOverview1.setStationId(res1.getUnitId());
                    approvalTrafficOverview1.setOverviewId(id);
                    approvalTrafficOverview1.setStationPid(res1.getPid());
                    approvalTrafficOverview1.setStationName(res1.getUnitName());
                    approvalTrafficOverview1.setEightFlow(Double.parseDouble(StringUtils.isEmpty(res1.getFlow())?"0.00":res1.getFlow()));
                    approvalTrafficOverview1.setAddSubtractFlow(Double.parseDouble(StringUtils.isEmpty(res1.getWaterPlan())?"0.00":res1.getWaterPlan()));
                    approvalTrafficOverview1.setPlanFlow(Double.parseDouble(StringUtils.isEmpty(res1.getFlow())?"0.00":res1.getFlow())+Double.parseDouble(StringUtils.isEmpty(res1.getWaterPlan())?"0.00":res1.getWaterPlan()));
                    approvalTrafficOverview1.setReservoir("河东管理站");
                    approvalTrafficOverviewHdList.add(approvalTrafficOverview1);
                }
            }
            resultList.addAll(approvalTrafficOverviewHdList);
            resultMap.put("河东管理站",approvalTrafficOverviewHdList);
        }
        DayWaterUsePlan hx = dayWaterUsePlanService.lambdaQuery().eq(DayWaterUsePlan::getRecordTime, format).eq(DayWaterUsePlan::getArea, "河西管理站").last("limit 1").one();
        if (null != hx) {
            List<ApprovalTrafficOverview> approvalTrafficOverviewHxList = new ArrayList<>();
            String json = hx.getV();
            List<ApprovalTrafficRes> approvalTrafficRes = JSONObject.parseArray(json, ApprovalTrafficRes.class);
            for (ApprovalTrafficRes res : approvalTrafficRes) {
                ApprovalTrafficOverview approvalTrafficOverview = new ApprovalTrafficOverview();
                approvalTrafficOverview.setId(UUIDUtils.getUUID());
                approvalTrafficOverview.setStationId(res.getUnitId());
                approvalTrafficOverview.setOverviewId(id);
                approvalTrafficOverview.setStationPid(res.getPid());
                approvalTrafficOverview.setStationName(res.getUnitName());
                approvalTrafficOverview.setEightFlow(Double.parseDouble(StringUtils.isEmpty(res.getFlow())?"0.00":res.getFlow()));
                approvalTrafficOverview.setAddSubtractFlow(Double.parseDouble(StringUtils.isEmpty(res.getWaterPlan())?"0.00":res.getWaterPlan()));
                approvalTrafficOverview.setPlanFlow(Double.parseDouble(StringUtils.isEmpty(res.getFlow())?"0.00":res.getFlow())+Double.parseDouble(StringUtils.isEmpty(res.getWaterPlan())?"0.00":res.getWaterPlan()));
                approvalTrafficOverview.setReservoir("河西管理站");
                approvalTrafficOverviewHxList.add(approvalTrafficOverview);
                for(ApprovalTrafficRes res1 :res.getChildren()){
                    ApprovalTrafficOverview approvalTrafficOverview1 = new ApprovalTrafficOverview();
                    approvalTrafficOverview1.setId(UUIDUtils.getUUID());
                    approvalTrafficOverview1.setStationId(res1.getUnitId());
                    approvalTrafficOverview1.setOverviewId(id);
                    approvalTrafficOverview1.setStationPid(res1.getPid());
                    approvalTrafficOverview1.setStationName(res1.getUnitName());
                    approvalTrafficOverview1.setEightFlow(Double.parseDouble(StringUtils.isEmpty(res1.getFlow())?"0.00":res1.getFlow()));
                    approvalTrafficOverview1.setAddSubtractFlow(Double.parseDouble(StringUtils.isEmpty(res1.getWaterPlan())?"0.00":res1.getWaterPlan()));
                    approvalTrafficOverview1.setPlanFlow(Double.parseDouble(StringUtils.isEmpty(res1.getFlow())?"0.00":res1.getFlow())+Double.parseDouble(StringUtils.isEmpty(res1.getWaterPlan())?"0.00":res1.getWaterPlan()));
                    approvalTrafficOverview1.setReservoir("河西管理站");
                    approvalTrafficOverviewHxList.add(approvalTrafficOverview1);
                }
            }
            resultList.addAll(approvalTrafficOverviewHxList);
            resultMap.put("河西管理站",approvalTrafficOverviewHxList);
        }
        if(StringUtils.isNotEmpty(modelId)){
            WaterResourceAllocation byId = waterResourceAllocationService.getById(modelId);
            List<Excel2> excelList = getListFromMinio(byId.getAllocationDataCustomAddress(), Excel2.class);
            resultList.forEach(t->{
                t.setModelFlow(excelList.stream().filter(x->x.getUnitId() != null &&x.getUnitId().equals(t.getStationId())).map(Excel2::getFlow).reduce(Double::sum).orElse(0.00));
            });
        }
        return approvalTrafficOverviewService.saveBatch(resultList);
    }

    private Double formatDouble(Double value) {
        DecimalFormat df = new DecimalFormat("0.00");
        String format = df.format(value);
        return Double.parseDouble(format);
    }

    @SneakyThrows
    private String calculateDay(String date, int day){
        Date time = sdf.parse(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        calendar.add(Calendar.DAY_OF_MONTH,day);
        Date result = calendar.getTime();
        return sdf.format(result);
    }

    @SneakyThrows
    private List getListFromMinio(String minioPath, Class clazz) {
        String[] split = minioPath.split("/");
        String[] split1 = split[split.length - 1].split("\\.");
        InputStream is = minioUtils.getObject("tth", minioPath);
        MultipartFile multipartFile = MultipartFileUtil.inputStreamToMultipartFile(is, split1[0]);
        return ExcelUtils.importExcel(multipartFile, clazz);
    }
}

