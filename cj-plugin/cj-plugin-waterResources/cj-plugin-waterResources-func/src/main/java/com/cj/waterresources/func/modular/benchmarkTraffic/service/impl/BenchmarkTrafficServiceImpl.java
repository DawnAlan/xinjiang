package com.cj.waterresources.func.modular.benchmarkTraffic.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.auth.core.pojo.SaBaseLoginUser;
import com.cj.auth.core.util.StpLoginUserUtil;
import com.cj.common.model.RestResponse;
import com.cj.common.util.RedisUtil;
import com.cj.common.util.UUIDUtils;
import com.cj.waterresources.func.modular.benchmarkTraffic.bean.req.ApprovalReq;
import com.cj.waterresources.func.modular.benchmarkTraffic.bean.req.BenchmarkTrafficListReq;
import com.cj.waterresources.func.modular.benchmarkTraffic.mapper.BenchmarkTrafficMapper;
import com.cj.waterresources.func.modular.benchmarkTraffic.entity.BenchmarkTraffic;
import com.cj.waterresources.func.modular.benchmarkTraffic.service.BenchmarkTrafficService;
import com.cj.waterresources.func.modular.trendsTable.entity.TrendsTableParam;
import com.cj.waterresources.func.modular.trendsTable.service.TrendsTableParamService;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.bean.req.WaterFeeStatisticsDetailsSelectListReq;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.entity.WaterFeeStatisticsDetails;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.service.WaterFeeStatisticsDetailsService;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.service.AllService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * 基准流量表(BenchmarkTraffic)表服务实现类
 *
 * @author makejava
 * @since 2024-07-17 11:23:40
 */
@Service("benchmarkTrafficService")
public class BenchmarkTrafficServiceImpl extends ServiceImpl<BenchmarkTrafficMapper, BenchmarkTraffic> implements BenchmarkTrafficService {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private TrendsTableParamService trendsTableParamService;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public RestResponse add(BenchmarkTraffic benchmarkTraffic) {
        SaBaseLoginUser saBaseLoginUser = StpLoginUserUtil.getLoginUser();
        benchmarkTraffic.setApplyTime(new Date());
        benchmarkTraffic.setId(UUIDUtils.getUUID());
        benchmarkTraffic.setSiteApprovalStatus(0);
        benchmarkTraffic.setCreateById(saBaseLoginUser.getId());
        benchmarkTraffic.setCreateByName(saBaseLoginUser.getName());
        boolean save = this.save(benchmarkTraffic);
        if(save){
            return RestResponse.ok();
        }else {

            return RestResponse.no("失败");
        }
    }

    @Override
    public RestResponse deleteById(String id) {
        boolean b = this.removeById(id);
        if(b){
            return RestResponse.ok();
        }else {

            return RestResponse.no("失败");
        }
    }

    @Override
    public RestResponse selectList(BenchmarkTrafficListReq req) {
        IPage<BenchmarkTraffic> page = new Page<>(req.getPageNum(),req.getPageSize());
        IPage<BenchmarkTraffic> page1 = this.lambdaQuery().between(StringUtils.isNotEmpty(req.getStartTime()) && StringUtils.isNotEmpty(req.getEndTime()), BenchmarkTraffic::getApplyTime, req.getStartTime(), req.getEndTime()).
                in(StringUtils.isNotEmpty(req.getUnitName()), BenchmarkTraffic::getUnitName, Arrays.stream(req.getUnitName().split(",")).collect(Collectors.toList())).
                like(StringUtils.isNotEmpty(req.getCreateByName()), BenchmarkTraffic::getCreateByName, req.getCreateByName()).
                eq(req.getSiteApprovalStatus() != null, BenchmarkTraffic::getSiteApprovalStatus, req.getSiteApprovalStatus()).
                eq(req.getBureauApprovalStatus() != null, BenchmarkTraffic::getBureauApprovalStatus, req.getBureauApprovalStatus()).
                eq(req.getProgramExecutionStatus() != null, BenchmarkTraffic::getProgramExecutionStatus, req.getProgramExecutionStatus()).
                orderByDesc(BenchmarkTraffic::getApplyTime).page(page);
        if(page1.getTotal()>0){
            return RestResponse.ok(page1);
        }else {
            return RestResponse.no("暂无数据");
        }
    }

    @Override
    public RestResponse approvalForSite(ApprovalReq req) {
        UpdateWrapper<BenchmarkTraffic> updateWrapper = new UpdateWrapper<>();
        if(req.getStatus()==1){
            updateWrapper.set("SITE_APPROVAL_STATUS",req.getStatus());
            updateWrapper.set("BUREAU_APPROVAL_STATUS",0);
        }else {
            updateWrapper.set("SITE_APPROVAL_STATUS",req.getStatus());
        }
        updateWrapper.eq("ID",req.getId());
        boolean update = this.update(updateWrapper);
        if(update){
            return RestResponse.ok();
        }else {

            return RestResponse.no("失败");
        }
    }

    @Override
    public RestResponse approvalForBureau(ApprovalReq req) {
        UpdateWrapper<BenchmarkTraffic> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("BUREAU_APPROVAL_STATUS",req.getStatus());
        if(req.getStatus()==1){
            updateWrapper.set("PROGRAM_EXECUTION_STATUS",0);
            //启动线程执行程序
            ExecutorService pool = Executors.newSingleThreadExecutor();
            pool.submit(new Runnable() {
                private BenchmarkTrafficService benchmarkTrafficService = SpringUtil.getBean(BenchmarkTrafficService.class);
                private AllService allService = SpringUtil.getBean(AllService.class);
                private WaterFeeStatisticsDetailsService waterFeeStatisticsDetailsService = SpringUtil.getBean(WaterFeeStatisticsDetailsService.class);
                @Override
                public void run() {
                    //TODO 执行程序
                    BenchmarkTraffic byId = benchmarkTrafficService.getById(req.getId());
                    try {
                        Boolean aBoolean = allService.benchmarkTraffic(byId.getUnitName(), byId.getWaterIntakeId(), sdf.format(byId.getAdjustStartTime()), sdf.format(byId.getAdjustEndTime()), byId.getBenchmarkTraffic());
                        if(!aBoolean){
                            benchmarkTrafficService.lambdaUpdate().set(BenchmarkTraffic::getProgramExecutionStatus,2).
                                    set(BenchmarkTraffic::getProgramExecutionRemark,"修改A3表流量失败，请检查输入流量参数是否正确").
                                    eq(BenchmarkTraffic::getId,req.getId()).update();
                        }
                        String tenDays = getTenDays(byId.getAdjustEndTime());
                        String[] split = sdf.format(byId.getAdjustEndTime()).split("-");
                        String tableParamString = (String)redisUtil.get("trendsTableParam:object:"+byId.getWaterIntakeId());
                        TrendsTableParam tableParam = JSONObject.parseObject(tableParamString, TrendsTableParam.class);
                        String mk = (String) redisUtil.get("trendsTableParam:list");
                        if(StringUtils.isEmpty(mk)){
                            trendsTableParamService.updateCache();
                            mk = (String) redisUtil.get("trendsTableParam:list");
                        }
                        List<TrendsTableParam> trendsTableParamListTemp = JSONObject.parseArray(mk, TrendsTableParam.class);
                        List<TrendsTableParam> trendsTableParamList = trendsTableParamListTemp.stream().filter(t -> t.getUseType() == 2).collect(Collectors.toList());
                        String s = trendsTableParamList.stream().filter(t ->StringUtils.isNotEmpty(t.getUnitId()) && t.getUnitId().equals(tableParam.getUnitId())).map(TrendsTableParam::getUseStation).findFirst().get();
                        WaterFeeStatisticsDetailsSelectListReq waterFeeStatisticsDetailsSelectListReq = new WaterFeeStatisticsDetailsSelectListReq();
                        waterFeeStatisticsDetailsSelectListReq.setYear(Integer.parseInt(split[0]));
                        waterFeeStatisticsDetailsSelectListReq.setMonth(Integer.parseInt(split[1]));
                        waterFeeStatisticsDetailsSelectListReq.setTenDays(tenDays);
                        waterFeeStatisticsDetailsSelectListReq.setStation(s);
                        try {
                            List<List<WaterFeeStatisticsDetails>> addHistory = new ArrayList<>();
                            RestResponse<Map<String, List<WaterFeeStatisticsDetails>>> mapRestResponse = waterFeeStatisticsDetailsService.selectList(waterFeeStatisticsDetailsSelectListReq);
                            Map<String, List<WaterFeeStatisticsDetails>> data = mapRestResponse.getData();
                            Set<String> strings = data.keySet();
                            for(String string : strings){
                                List<WaterFeeStatisticsDetails> waterFeeStatisticsDetailsList = data.get(string);
                                waterFeeStatisticsDetailsList.forEach(t->t.setV(null));
                                addHistory.add(waterFeeStatisticsDetailsList);
                            }
                            RestResponse restResponse = waterFeeStatisticsDetailsService.clearTable(waterFeeStatisticsDetailsSelectListReq);
                            if(restResponse.getCode()==200){
                                try {
                                    RestResponse restResponse1 = waterFeeStatisticsDetailsService.addHistory(addHistory);
                                    if(restResponse1.getCode()==200){
                                        benchmarkTrafficService.lambdaUpdate().set(BenchmarkTraffic::getProgramExecutionStatus,1).set(BenchmarkTraffic::getProgramExecutionRemark,"程序执行成功").eq(BenchmarkTraffic::getId,req.getId()).update();
                                    }else {
                                        benchmarkTrafficService.lambdaUpdate().set(BenchmarkTraffic::getProgramExecutionStatus,2).set(BenchmarkTraffic::getProgramExecutionRemark,"水费表历史数据添加失败").eq(BenchmarkTraffic::getId,req.getId()).update();
                                    }
                                }catch (Exception e) {
                                    e.printStackTrace();
                                    benchmarkTrafficService.lambdaUpdate().set(BenchmarkTraffic::getProgramExecutionStatus,2).set(BenchmarkTraffic::getProgramExecutionRemark,"水费表历史数据添加程序异常").eq(BenchmarkTraffic::getId,req.getId()).update();
                                }
                            }else {
                                benchmarkTrafficService.lambdaUpdate().set(BenchmarkTraffic::getProgramExecutionStatus,2).set(BenchmarkTraffic::getProgramExecutionRemark,"水费表清空失败").eq(BenchmarkTraffic::getId,req.getId()).update();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            benchmarkTrafficService.lambdaUpdate().set(BenchmarkTraffic::getProgramExecutionStatus,2).set(BenchmarkTraffic::getProgramExecutionRemark,"水费表清空程序异常").eq(BenchmarkTraffic::getId,req.getId()).update();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        benchmarkTrafficService.lambdaUpdate().set(BenchmarkTraffic::getProgramExecutionStatus,2).set(BenchmarkTraffic::getProgramExecutionRemark,"A3修改程序异常").eq(BenchmarkTraffic::getId,req.getId()).update();
                    }
                }
            });
        }
        updateWrapper.eq("ID",req.getId());
        boolean update = this.update(updateWrapper);
        if(update){
            return RestResponse.ok();
        }else {
            return RestResponse.no("失败");
        }
    }

    private String getTenDays(Date date) {
        int offsetTenDays = (DateUtil.dayOfMonth(date) - 1) / 10;
        if (offsetTenDays == 0) {
            return "上旬";
        }
        if (offsetTenDays == 1) {
            return "中旬";
        }
        return "下旬";
    }

}

