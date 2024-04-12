package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.common.model.RestResponse;
import com.cj.common.util.RedisUtil;
import com.cj.common.util.UUIDUtils;
import com.cj.waterresources.func.modular.trendsTable.entity.TrendsTableParam;
import com.cj.waterresources.func.modular.trendsTable.service.TrendsTableParamService;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.dayWaterUsePlan.entity.DayWaterUsePlan;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.dayWaterUsePlan.service.DayWaterUsePlanService;
import com.cj.waterresources.func.modular.waterResourceAllcation.service.WaterResourceAllocationService;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.bean.req.ApprovalTrafficOverviewTableAddReq;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.bean.res.ApprovalTrafficRes;
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

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");


    @SneakyThrows
    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse add(ApprovalTrafficOverviewTableAddReq req) {
        ApprovalTrafficOverviewTable approvalTrafficOverviewTable = new ApprovalTrafficOverviewTable();
        approvalTrafficOverviewTable.setName(req.getName());
        approvalTrafficOverviewTable.setId(UUIDUtils.getUUID());
        approvalTrafficOverviewTable.setTime(sdf.parse(req.getTime()));
        boolean save = this.save(approvalTrafficOverviewTable);
        if (save) {
            Boolean aBoolean = insertApprovalTrafficOverview(req.getModelId(), approvalTrafficOverviewTable.getId(), approvalTrafficOverviewTable.getTime());
            if (aBoolean) {
                return RestResponse.ok();
            }else {
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
    public RestResponse selectList(Date time, String name) {
        List<ApprovalTrafficOverviewTable> list = this.lambdaQuery().like(StringUtils.isNotEmpty(name), ApprovalTrafficOverviewTable::getName, name).
                eq(time != null, ApprovalTrafficOverviewTable::getTime, time).list();
        if (list != null && list.size() > 0) {
            return RestResponse.ok(list);
        } else {
            return RestResponse.no("查询失败");
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
                    approvalTrafficOverview.setStationPid(tableParam.getPId());
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
                    approvalTrafficOverview.setStationPid(tableParam.getPId());
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
            List<DayWaterSituationStatisticsTableQs> qsList = qsMapper.selectForApproval(format);
            List<DayWaterSituationStatisticsTableQsLh> qsLhList = qsLhMapper.selectForApproval(format);
            List<ApprovalTrafficOverview> approvalTrafficOverviewQsList = new ArrayList<>();
            String json = qs.getV();
            List<ApprovalTrafficRes> approvalTrafficRes = JSONObject.parseArray(json, ApprovalTrafficRes.class);
            for (ApprovalTrafficRes res : approvalTrafficRes) {
                ApprovalTrafficOverview approvalTrafficOverview = new ApprovalTrafficOverview();
                approvalTrafficOverview.setId(UUIDUtils.getUUID());
                approvalTrafficOverview.setStationId(res.getId());
                approvalTrafficOverview.setOverviewId(id);
                approvalTrafficOverview.setStationPid(res.getPid());
                approvalTrafficOverview.setStationName(res.getUnitName());
                Double eightQs = qsList.stream().filter(t -> t.getTableHeadId().equals(res.getUnitId()) && t.getV() != null).map(DayWaterSituationStatisticsTableQs::getV).reduce(Double::sum).orElse(0.00);
                Double eightQsLh = qsLhList.stream().filter(t -> t.getTableHeadId().equals(res.getUnitId()) && t.getV() != null).map(DayWaterSituationStatisticsTableQsLh::getV).reduce(Double::sum).orElse(0.00);
                approvalTrafficOverview.setEightFlow(eightQs+eightQsLh);
                approvalTrafficOverview.setAddSubtractFlow(Double.parseDouble(StringUtils.isEmpty(res.getWaterPlan())?"0.00":res.getWaterPlan()));
                approvalTrafficOverview.setApprovalFlow(Double.parseDouble(StringUtils.isEmpty(res.getFlow())?"0.00":res.getFlow())+Double.parseDouble(StringUtils.isEmpty(res.getWaterPlan())?"0.00":res.getWaterPlan()));
                approvalTrafficOverview.setReservoir("渠首管理站");
                approvalTrafficOverviewQsList.add(approvalTrafficOverview);
                for (ApprovalTrafficRes res1 : res.getChildren()) {
                    ApprovalTrafficOverview approvalTrafficOverview1 = new ApprovalTrafficOverview();
                    approvalTrafficOverview1.setId(UUIDUtils.getUUID());
                    approvalTrafficOverview1.setStationId(res1.getId());
                    approvalTrafficOverview1.setOverviewId(id);
                    approvalTrafficOverview1.setStationPid(res1.getPid());
                    approvalTrafficOverview1.setStationName(res1.getUnitName());
                    Double eightQs1 = qsList.stream().filter(t -> t.getTableHeadId().equals(res1.getUnitId()) && t.getV() != null).map(DayWaterSituationStatisticsTableQs::getV).reduce(Double::sum).orElse(0.00);
                    Double eightQsLh1 = qsLhList.stream().filter(t -> t.getTableHeadId().equals(res1.getUnitId()) && t.getV() != null).map(DayWaterSituationStatisticsTableQsLh::getV).reduce(Double::sum).orElse(0.00);
                    approvalTrafficOverview1.setEightFlow(eightQs1+eightQsLh1);
                    approvalTrafficOverview1.setAddSubtractFlow(Double.parseDouble(StringUtils.isEmpty(res1.getWaterPlan())?"0.00":res1.getWaterPlan()));
                    approvalTrafficOverview1.setApprovalFlow(Double.parseDouble(StringUtils.isEmpty(res1.getFlow())?"0.00":res1.getFlow())+Double.parseDouble(StringUtils.isEmpty(res1.getWaterPlan())?"0.00":res1.getWaterPlan()));
                    approvalTrafficOverview1.setReservoir("渠首管理站");
                    approvalTrafficOverviewQsList.add(approvalTrafficOverview1);
                }
            }
            resultList.addAll(approvalTrafficOverviewQsList);
            resultMap.put("渠首管理站",approvalTrafficOverviewQsList);
        }
        DayWaterUsePlan hd = dayWaterUsePlanService.lambdaQuery().eq(DayWaterUsePlan::getRecordTime, format).eq(DayWaterUsePlan::getArea, "河东管理站").last("limit 1").one();
        if (null != hd) {
            List<DayWaterSituationStatisticsTableHd> hdList = hdMapper.selectForApproval(format);
            List<ApprovalTrafficOverview> approvalTrafficOverviewHdList = new ArrayList<>();
            String json = hd.getV();
            List<ApprovalTrafficRes> approvalTrafficRes = JSONObject.parseArray(json, ApprovalTrafficRes.class);
            for (ApprovalTrafficRes res : approvalTrafficRes) {
                ApprovalTrafficOverview approvalTrafficOverview = new ApprovalTrafficOverview();
                approvalTrafficOverview.setId(UUIDUtils.getUUID());
                approvalTrafficOverview.setStationId(res.getId());
                approvalTrafficOverview.setOverviewId(id);
                approvalTrafficOverview.setStationPid(res.getPid());
                approvalTrafficOverview.setStationName(res.getUnitName());
                Double eightHd = hdList.stream().filter(t -> t.getTableHeadId().equals(res.getUnitId()) && t.getV() != null).map(DayWaterSituationStatisticsTableHd::getV).reduce(Double::sum).orElse(0.00);
                approvalTrafficOverview.setEightFlow(eightHd);
                approvalTrafficOverview.setAddSubtractFlow(Double.parseDouble(StringUtils.isEmpty(res.getWaterPlan())?"0.00":res.getWaterPlan()));
                approvalTrafficOverview.setApprovalFlow(Double.parseDouble(StringUtils.isEmpty(res.getFlow())?"0.00":res.getFlow())+Double.parseDouble(StringUtils.isEmpty(res.getWaterPlan())?"0.00":res.getWaterPlan()));
                approvalTrafficOverview.setReservoir("河东管理站");
                approvalTrafficOverviewHdList.add(approvalTrafficOverview);
                for(ApprovalTrafficRes res1 :res.getChildren()){
                    ApprovalTrafficOverview approvalTrafficOverview1 = new ApprovalTrafficOverview();
                    approvalTrafficOverview1.setId(UUIDUtils.getUUID());
                    approvalTrafficOverview1.setStationId(res1.getId());
                    approvalTrafficOverview1.setOverviewId(id);
                    approvalTrafficOverview1.setStationPid(res1.getPid());
                    approvalTrafficOverview1.setStationName(res1.getUnitName());
                    Double eightHd1 = hdList.stream().filter(t -> t.getTableHeadId().equals(res1.getUnitId()) && t.getV() != null).map(DayWaterSituationStatisticsTableHd::getV).reduce(Double::sum).orElse(0.00);
                    approvalTrafficOverview1.setEightFlow(eightHd1);
                    approvalTrafficOverview1.setAddSubtractFlow(Double.parseDouble(StringUtils.isEmpty(res1.getWaterPlan())?"0.00":res1.getWaterPlan()));
                    approvalTrafficOverview1.setApprovalFlow(Double.parseDouble(StringUtils.isEmpty(res1.getFlow())?"0.00":res1.getFlow())+Double.parseDouble(StringUtils.isEmpty(res1.getWaterPlan())?"0.00":res1.getWaterPlan()));
                    approvalTrafficOverview1.setReservoir("河东管理站");
                    approvalTrafficOverviewHdList.add(approvalTrafficOverview1);
                }
            }
            resultList.addAll(approvalTrafficOverviewHdList);
            resultMap.put("河东管理站",approvalTrafficOverviewHdList);
        }
        DayWaterUsePlan hx = dayWaterUsePlanService.lambdaQuery().eq(DayWaterUsePlan::getRecordTime, format).eq(DayWaterUsePlan::getArea, "河西管理站").last("limit 1").one();
        if (null != hx) {
            List<DayWaterSituationStatisticsTableHx> hxList = hxMapper.selectForApproval(format);
            List<ApprovalTrafficOverview> approvalTrafficOverviewHxList = new ArrayList<>();
            String json = hx.getV();
            List<ApprovalTrafficRes> approvalTrafficRes = JSONObject.parseArray(json, ApprovalTrafficRes.class);
            for (ApprovalTrafficRes res : approvalTrafficRes) {
                ApprovalTrafficOverview approvalTrafficOverview = new ApprovalTrafficOverview();
                approvalTrafficOverview.setId(UUIDUtils.getUUID());
                approvalTrafficOverview.setStationId(res.getId());
                approvalTrafficOverview.setOverviewId(id);
                approvalTrafficOverview.setStationPid(res.getPid());
                approvalTrafficOverview.setStationName(res.getUnitName());
                Double eightHx = hxList.stream().filter(t -> t.getTableHeadId().equals(res.getUnitId()) && t.getV() != null).map(DayWaterSituationStatisticsTableHx::getV).reduce(Double::sum).orElse(0.00);
                approvalTrafficOverview.setEightFlow(eightHx);
                approvalTrafficOverview.setAddSubtractFlow(Double.parseDouble(StringUtils.isEmpty(res.getWaterPlan())?"0.00":res.getWaterPlan()));
                approvalTrafficOverview.setApprovalFlow(Double.parseDouble(StringUtils.isEmpty(res.getFlow())?"0.00":res.getFlow())+Double.parseDouble(StringUtils.isEmpty(res.getWaterPlan())?"0.00":res.getWaterPlan()));
                approvalTrafficOverview.setReservoir("河西管理站");
                approvalTrafficOverviewHxList.add(approvalTrafficOverview);
                for(ApprovalTrafficRes res1 :res.getChildren()){
                    ApprovalTrafficOverview approvalTrafficOverview1 = new ApprovalTrafficOverview();
                    approvalTrafficOverview1.setId(UUIDUtils.getUUID());
                    approvalTrafficOverview1.setStationId(res1.getId());
                    approvalTrafficOverview1.setOverviewId(id);
                    approvalTrafficOverview1.setStationPid(res1.getPid());
                    approvalTrafficOverview1.setStationName(res1.getUnitName());
                    Double eightHd1 = hxList.stream().filter(t -> t.getTableHeadId().equals(res1.getUnitId()) && t.getV() != null).map(DayWaterSituationStatisticsTableHx::getV).reduce(Double::sum).orElse(0.00);
                    approvalTrafficOverview1.setEightFlow(eightHd1);
                    approvalTrafficOverview1.setAddSubtractFlow(Double.parseDouble(StringUtils.isEmpty(res1.getWaterPlan())?"0.00":res1.getWaterPlan()));
                    approvalTrafficOverview1.setApprovalFlow(Double.parseDouble(StringUtils.isEmpty(res1.getFlow())?"0.00":res1.getFlow())+Double.parseDouble(StringUtils.isEmpty(res1.getWaterPlan())?"0.00":res1.getWaterPlan()));
                    approvalTrafficOverview1.setReservoir("河西管理站");
                    approvalTrafficOverviewHxList.add(approvalTrafficOverview1);
                }
            }
            resultList.addAll(approvalTrafficOverviewHxList);
            resultMap.put("河西管理站",approvalTrafficOverviewHxList);
        }
        return approvalTrafficOverviewService.saveBatch(resultList);
    }
}

