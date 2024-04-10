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
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.hd.service.DayWaterSituationStatisticsTableHdService;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.hx.service.DayWaterSituationStatisticsTableHxService;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.lzz.entity.DayWaterSituationStatisticsTableLzz;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.lzz.service.DayWaterSituationStatisticsTableLzzService;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.qs.entity.DayWaterSituationStatisticsTableQs;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.qs.entity.DayWaterSituationStatisticsTableQsLh;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.qs.service.DayWaterSituationStatisticsTableQsLhService;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.qs.service.DayWaterSituationStatisticsTableQsService;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.tth.entity.DayWaterSituationStatisticsTableTth;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.tth.service.DayWaterSituationStatisticsTableTthService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private TrendsTableParamService trendsTableParamService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private WaterResourceAllocationService waterResourceAllocationService;

    @Autowired
    private DayWaterSituationStatisticsTableTthService tthService;

    @Autowired
    private DayWaterSituationStatisticsTableLzzService lzzService;

    @Autowired
    private DayWaterSituationStatisticsTableQsService qsService;

    @Autowired
    private DayWaterSituationStatisticsTableQsLhService qsLhService;

    @Autowired
    private DayWaterSituationStatisticsTableHdService hdService;

    @Autowired
    private DayWaterSituationStatisticsTableHxService hxService;

    @Autowired
    private DayWaterUsePlanService dayWaterUsePlanService;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");


    @Override
    public RestResponse add(ApprovalTrafficOverviewTableAddReq req) {
        ApprovalTrafficOverviewTable approvalTrafficOverviewTable = new ApprovalTrafficOverviewTable();
        approvalTrafficOverviewTable.setName(req.getName());
        approvalTrafficOverviewTable.setId(UUIDUtils.getUUID());
        approvalTrafficOverviewTable.setTime(req.getTime());
        boolean save = this.save(approvalTrafficOverviewTable);
        if (save) {

            return RestResponse.ok();
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

    private Boolean insertApprovalTrafficOverview(String modelId, String id, Date time) {
        Map<String,List<ApprovalTrafficOverview>> resultMap = new HashMap<>();
        String format = sdf.format(time);
        List<DayWaterSituationStatisticsTableTth> tthList = tthService.lambdaQuery().eq(DayWaterSituationStatisticsTableTth::getRecordTime, format).
                eq(DayWaterSituationStatisticsTableTth::getTime, "08:00").list();
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
                    approvalTrafficOverviewTthList.add(approvalTrafficOverview);
                }
            }
            resultMap.put("头屯河水库",approvalTrafficOverviewTthList);
        }
        List<DayWaterSituationStatisticsTableLzz> lzzList = lzzService.lambdaQuery().eq(DayWaterSituationStatisticsTableLzz::getRecordTime, format).
                eq(DayWaterSituationStatisticsTableLzz::getTime, "08:00").list();
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
                    approvalTrafficOverviewLzzList.add(approvalTrafficOverview);
                }
            }
            resultMap.put("头屯河水库",approvalTrafficOverviewLzzList);
        }
        DayWaterUsePlan one = dayWaterUsePlanService.lambdaQuery().eq(DayWaterUsePlan::getRecordTime, format).eq(DayWaterUsePlan::getArea, "渠首管理站").last("limit 1").one();
        if (null != one) {
            List<DayWaterSituationStatisticsTableQs> qsList = qsService.lambdaQuery().eq(DayWaterSituationStatisticsTableQs::getRecordTime, format).
                    eq(DayWaterSituationStatisticsTableQs::getTime, "08:00").list();
            List<DayWaterSituationStatisticsTableQsLh> qsLhList = qsLhService.lambdaQuery().eq(DayWaterSituationStatisticsTableQsLh::getRecordTime, format).
                    eq(DayWaterSituationStatisticsTableQsLh::getTime, "08:00").list();
            List<ApprovalTrafficOverview> approvalTrafficOverviewQsList = new ArrayList<>();
            String json = one.getV();
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
                approvalTrafficOverview.setAddSubtractFlow();
                approvalTrafficOverviewQsList.add(approvalTrafficOverview);
            }
            resultMap.put("渠首管理站",approvalTrafficOverviewQsList);
        }
        return null;
    }
}

