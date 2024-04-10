package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.common.model.RestResponse;
import com.cj.common.util.RedisUtil;
import com.cj.common.util.UUIDUtils;
import com.cj.waterresources.func.modular.trendsTable.entity.TrendsTableParam;
import com.cj.waterresources.func.modular.trendsTable.service.TrendsTableParamService;
import com.cj.waterresources.func.modular.waterResourceAllcation.service.WaterResourceAllocationService;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.bean.req.ApprovalTrafficOverviewTableAddReq;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.entity.ApprovalTrafficOverview;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.mapper.ApprovalTrafficOverviewTableMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.entity.ApprovalTrafficOverviewTable;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.service.ApprovalTrafficOverviewService;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.service.ApprovalTrafficOverviewTableService;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.hd.service.DayWaterSituationStatisticsTableHdService;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.hx.service.DayWaterSituationStatisticsTableHxService;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.lzz.service.DayWaterSituationStatisticsTableLzzService;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.qs.service.DayWaterSituationStatisticsTableQsService;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.tth.entity.DayWaterSituationStatisticsTableTth;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.tth.service.DayWaterSituationStatisticsTableTthService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private DayWaterSituationStatisticsTableHdService hdService;

    @Autowired
    private DayWaterSituationStatisticsTableHxService hxService;


    @Override
    public RestResponse add(ApprovalTrafficOverviewTableAddReq req) {
        ApprovalTrafficOverviewTable approvalTrafficOverviewTable = new ApprovalTrafficOverviewTable();
        approvalTrafficOverviewTable.setName(req.getName());
        approvalTrafficOverviewTable.setId(UUIDUtils.getUUID());
        approvalTrafficOverviewTable.setTime(req.getTime());
        boolean save = this.save(approvalTrafficOverviewTable);
        if(save){

            return RestResponse.ok();
        }else {
            return RestResponse.no("添加失败");
        }
    }

    @Override
    public RestResponse delete(String id) {
        boolean b = this.removeById(id);
        if(b){
            Long count = approvalTrafficOverviewService.lambdaQuery().eq(ApprovalTrafficOverview::getOverviewId, id).count();
            if(count>0){
                boolean remove = approvalTrafficOverviewService.lambdaUpdate().eq(ApprovalTrafficOverview::getOverviewId, id).remove();
                if(remove){
                    return RestResponse.ok();
                }else {
                    return RestResponse.no("删除流量概览表失败");
                }
            }else {
                return RestResponse.ok();
            }
        }else {
            return RestResponse.no("删除失败");
        }
    }

    @Override
    public RestResponse selectList(Date time, String name) {
        List<ApprovalTrafficOverviewTable> list = this.lambdaQuery().like(StringUtils.isNotEmpty(name), ApprovalTrafficOverviewTable::getName, name).
                eq(time != null, ApprovalTrafficOverviewTable::getTime, time).list();
        if(list!= null && list.size() > 0){
            return RestResponse.ok(list);
        }else {
            return RestResponse.no("查询失败");
        }
    }

    private Boolean insertApprovalTrafficOverview(String modelId,String id){
        String mk = (String) redisUtil.get("trendsTableParam:list");
        if(StringUtils.isEmpty(mk)){
            trendsTableParamService.updateCache();
            mk = (String) redisUtil.get("trendsTableParam:list");
        }
        List<TrendsTableParam> trendsTableParamListTemp = JSONObject.parseArray(mk, TrendsTableParam.class);
        List<TrendsTableParam> trendsTableParamList = trendsTableParamListTemp.stream().filter(t -> t.getUseType() == 1 && !t.getParamName().equals("合计") ).collect(Collectors.toList());
        Map<String, List<TrendsTableParam>> collect = trendsTableParamList.stream().collect(Collectors.groupingBy(TrendsTableParam::getUseStation));
        collect.forEach((k,v)->{
           if(k.equals("头屯河水库")){
               List<String> ids = v.stream().map(TrendsTableParam::getId).collect(Collectors.toList());
               List<DayWaterSituationStatisticsTableTth> list = tthService.lambdaQuery().in(DayWaterSituationStatisticsTableTth::getTableHeadId, ids).list();
               for(DayWaterSituationStatisticsTableTth tth:list){
                   ApprovalTrafficOverview approvalTrafficOverview = new ApprovalTrafficOverview();

               }
           }
           if(k.equals("楼庄子水库")){
               List<String> ids = v.stream().map(TrendsTableParam::getId).collect(Collectors.toList());
           }
           if(k.equals("渠首管理站")){
               List<String> ids = v.stream().map(TrendsTableParam::getId).collect(Collectors.toList());
           }
           if(k.equals("河东管理站")){
               List<String> ids = v.stream().map(TrendsTableParam::getId).collect(Collectors.toList());
           }
           if(k.equals("河西管理站")){
               List<String> ids = v.stream().map(TrendsTableParam::getId).collect(Collectors.toList());
           }
        });
        return null;
    }
}

