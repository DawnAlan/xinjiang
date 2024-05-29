package com.cj.waterresources.func.modular.quotaStatisticsManagement.tenDaysWaterBalance.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.common.model.RestResponse;
import com.cj.common.util.NumberUtil;
import com.cj.common.util.RedisUtil;
import com.cj.common.util.UUIDUtils;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.tenDaysWaterBalance.bean.req.TenDaysWaterBalanceSelectListReq;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.tenDaysWaterBalance.bean.res.SelectTotalForIndexWarningRes;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.tenDaysWaterBalance.mapper.TenDaysWaterBalanceMapper;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.tenDaysWaterBalance.entity.TenDaysWaterBalance;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.tenDaysWaterBalance.service.TenDaysWaterBalanceService;
import com.cj.waterresources.func.modular.trendsTable.entity.TrendsTableParam;
import com.cj.waterresources.func.modular.trendsTable.service.TrendsTableParamService;

import com.cj.waterresources.func.modular.waterPrice.waterDistributionRatio.entity.WaterDistributionRatio;
import com.cj.waterresources.func.modular.waterPrice.waterDistributionRatio.service.WaterDistributionRatioService;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.bean.res.SelectTotalForIndexRes;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.bean.vo.TrendsTableParamVo;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.entity.WaterFeeStatisticsTotal;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Float.NaN;

/**
 * 旬水量平衡(TenDaysWaterBalance)表服务实现类
 *
 * @author makejava
 * @since 2023-12-22 18:40:02
 */
@Service("tenDaysWaterBalanceService")
public class TenDaysWaterBalanceServiceImpl extends ServiceImpl<TenDaysWaterBalanceMapper, TenDaysWaterBalance> implements TenDaysWaterBalanceService {

    @Autowired
    private TrendsTableParamService trendsTableParamService;

    @Autowired
    private WaterDistributionRatioService waterDistributionRatioService;

    @Autowired
    private RedisUtil redisUtil;

    private  DecimalFormat df = new DecimalFormat("#0.00");


    @Override
    public RestResponse<List<TenDaysWaterBalance>> selectList(TenDaysWaterBalanceSelectListReq req) {
        List<TenDaysWaterBalance> list = this.lambdaQuery().eq(StringUtils.isNotEmpty(req.getStation()), TenDaysWaterBalance::getStation, req.getStation()).
                eq(req.getYear() != null, TenDaysWaterBalance::getYear, req.getYear()).
                eq(req.getMonth() != null, TenDaysWaterBalance::getMonth, req.getMonth()).
                eq(StringUtils.isNotEmpty(req.getTenDays()), TenDaysWaterBalance::getTenDays, req.getTenDays()).
                eq(TenDaysWaterBalance::getDel, 0).list();
        if(null!=list && list.size()>0){
            return RestResponse.ok(list);
        }else {
            return RestResponse.no("暂无数据");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse addFirst(List<WaterFeeStatisticsTotal> totalList) {
        String mk = (String) redisUtil.get("trendsTableParam:list");
        if(StringUtils.isEmpty(mk)){
            trendsTableParamService.updateCache();
            mk = (String) redisUtil.get("trendsTableParam:list");
        }
        List<TrendsTableParam> trendsTableParamListTemp = JSONObject.parseArray(mk, TrendsTableParam.class);
        List<TrendsTableParam> trendsTableParamList = trendsTableParamListTemp.stream().filter(t -> t.getUseType() == 2 && t.getUseStation().equals(totalList.get(0).getStation())).collect(Collectors.toList());
        List<TenDaysWaterBalance> result = new ArrayList<>();
        Double actualWaterReceived = 0.0;
        Double allRatio = 0.00;
        List<String> tableHeadIdList = totalList.stream().map(WaterFeeStatisticsTotal::getTableHeadId).collect(Collectors.toList());
        TrendsTableParam trendsTableParamForTotal = null;
        for(TrendsTableParam param:trendsTableParamList){
            if(tableHeadIdList.contains(param.getId()) && param.getParamName().equals("合计") && param.getPId().equals("0")){
                trendsTableParamForTotal = param;
            }
        }
        if(trendsTableParamForTotal !=null){
            String id = trendsTableParamForTotal.getId();
            List<WaterFeeStatisticsTotal> resultTemp = totalList.stream().filter(t -> t.getTableHeadId().equals(id)).collect(Collectors.toList());
            actualWaterReceived = resultTemp.get(0).getCurrentWaterVolume();
        }

        for(WaterFeeStatisticsTotal total:totalList){
            TrendsTableParam param = trendsTableParamService.lambdaQuery().
                    eq(TrendsTableParam::getId, total.getTableHeadId()).
                    eq(TrendsTableParam::getPId, "0").
                    eq(TrendsTableParam::getParamName, "合计").one();
            if(null!= param){
                WaterDistributionRatio ratio = waterDistributionRatioService.lambdaQuery().eq(WaterDistributionRatio::getStation, total.getStation()).
                        eq(WaterDistributionRatio::getYear, total.getYear()).
                        eq(WaterDistributionRatio::getMonth, total.getMonth()).
                        eq(WaterDistributionRatio::getTenDays, total.getTenDays()).
                        eq(WaterDistributionRatio::getTableHeadId, total.getTableHeadId()).one();
                if(null != ratio){
                    allRatio = ratio.getV();
                }
            }
        }
        for(WaterFeeStatisticsTotal total:totalList){
            TenDaysWaterBalance balance = new TenDaysWaterBalance();
            TrendsTableParam param = trendsTableParamService.lambdaQuery().
                    eq(TrendsTableParam::getId, total.getTableHeadId()).
                    eq(TrendsTableParam::getPId, "0").
                    eq(TrendsTableParam::getParamName, "合计").one();
            if(null!= param){
                //总实收水量
                balance.setActualWaterReceived(actualWaterReceived);
            }
            balance.setId(UUIDUtils.getUUID());
            balance.setDel(0);
            balance.setCreateTime(new Date());
            balance.setStation(total.getStation());
            balance.setYear(total.getYear());
            balance.setMonth(total.getMonth());
            balance.setTenDays(total.getTenDays());
            balance.setTableHeadId(total.getTableHeadId());
            WaterDistributionRatio ratio = waterDistributionRatioService.lambdaQuery().eq(WaterDistributionRatio::getStation, total.getStation()).
                    eq(WaterDistributionRatio::getYear, total.getYear()).
                    eq(WaterDistributionRatio::getMonth, total.getMonth()).
                    eq(WaterDistributionRatio::getTenDays, total.getTenDays()).
                    eq(WaterDistributionRatio::getTableHeadId, total.getTableHeadId()).one();
            if(null != ratio){
                //按比例水量
                balance.setProportionalWaterQuantity(allRatio==0.00?null: NumberUtil.holdDecimal(((ratio.getV()==null?0.0:ratio.getV())/allRatio)* actualWaterReceived,3));
            }
            //实际水量
            balance.setActualWaterVolume(total.getCurrentWaterVolume());
            //实际比例
            Double actualProportion = actualWaterReceived==0.0?0.0:balance.getActualWaterVolume()/actualWaterReceived;
            balance.setActualProportion(Double.isNaN(actualProportion)?null:Double.parseDouble(df.format(actualProportion*100)));
            //盈亏水量
            balance.setProfitAndLossWaterVolume(balance.getActualWaterVolume()-(balance.getProportionalWaterQuantity()==null?0.0:balance.getProportionalWaterQuantity()));
            //盈亏比例
            balance.setProfitAndLossRatio((balance.getActualProportion()==null?0.0:balance.getActualProportion())-(ratio==null?0.0:ratio.getV()==null?0.0:ratio.getV()));
            result.add(balance);
        }

        boolean b = this.saveBatch(result);
        if(b){
            return RestResponse.ok();
        }else {
            return RestResponse.no("error");
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse add(List<WaterFeeStatisticsTotal> totalList) {
        List<TenDaysWaterBalance> list = this.lambdaQuery().eq(TenDaysWaterBalance::getStation, totalList.get(0).getStation()).
                eq(TenDaysWaterBalance::getYear, totalList.get(0).getYear()).
                eq(TenDaysWaterBalance::getMonth, totalList.get(0).getMonth()).
                eq(TenDaysWaterBalance::getTenDays, totalList.get(0).getTenDays()).
                eq(TenDaysWaterBalance::getDel, 0).list();
        if(null!=list && list.size()>0){
            String mk = (String) redisUtil.get("trendsTableParam:list");
            if(StringUtils.isEmpty(mk)){
                trendsTableParamService.updateCache();
                mk = (String) redisUtil.get("trendsTableParam:list");
            }
            List<TrendsTableParam> trendsTableParamListTemp = JSONObject.parseArray(mk, TrendsTableParam.class);
            List<TrendsTableParam> trendsTableParamList = trendsTableParamListTemp.stream().filter(t -> t.getUseType() == 2 && t.getUseStation().equals(totalList.get(0).getStation())).collect(Collectors.toList());
            List<TenDaysWaterBalance> result = new ArrayList<>();
            Double actualWaterReceived = 0.0;
            List<String> tableHeadIdList = totalList.stream().map(WaterFeeStatisticsTotal::getTableHeadId).collect(Collectors.toList());
            TrendsTableParam trendsTableParamForTotal = null;
            for(TrendsTableParam param:trendsTableParamList){
                if(tableHeadIdList.contains(param.getId()) && param.getParamName().equals("合计") && param.getPId().equals("0")){
                    trendsTableParamForTotal = param;
                }
            }
            //TrendsTableParam trendsTableParamForTotal = trendsTableParamService.lambdaQuery().in(TrendsTableParam::getId, tableHeadIdList).eq(TrendsTableParam::getPId, "0").eq(TrendsTableParam::getParamName, "合计").one();
            if(trendsTableParamForTotal !=null){
                String id = trendsTableParamForTotal.getId();
                List<WaterFeeStatisticsTotal> resultTemp = totalList.stream().filter(t -> t.getTableHeadId().equals(id)).collect(Collectors.toList());
                actualWaterReceived = resultTemp.get(0).getCurrentWaterVolume();
            }
            Double allRatio = 0.00;
            for(WaterFeeStatisticsTotal total:totalList){
                TrendsTableParam param = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getId, total.getTableHeadId()).eq(TrendsTableParam::getPId, "0").eq(TrendsTableParam::getParamName, "合计").one();
                if(null!= param){
                    WaterDistributionRatio ratio = waterDistributionRatioService.lambdaQuery().eq(WaterDistributionRatio::getStation, total.getStation()).
                            eq(WaterDistributionRatio::getYear, total.getYear()).
                            eq(WaterDistributionRatio::getMonth, total.getMonth()).
                            eq(WaterDistributionRatio::getTenDays, total.getTenDays()).
                            eq(WaterDistributionRatio::getTableHeadId, total.getTableHeadId()).one();
                    if(null != ratio) {
                        //总实收水量
                        allRatio = ratio.getV();
                    }
                }
            }
            for(TenDaysWaterBalance balance:list){

                for(WaterFeeStatisticsTotal total:totalList){
                    if(balance.getTableHeadId().equals(total.getTableHeadId())){
                        TrendsTableParam param = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getId, total.getTableHeadId()).eq(TrendsTableParam::getPId, "0").eq(TrendsTableParam::getParamName, "合计").one();
                        if(null!= param){
                            //总实收水量
                            balance.setActualWaterReceived(total.getCurrentWaterVolume());
                        }
                        balance.setUpdateTime(new Date());
                        WaterDistributionRatio ratio = waterDistributionRatioService.lambdaQuery().eq(WaterDistributionRatio::getStation, total.getStation()).
                                eq(WaterDistributionRatio::getYear, total.getYear()).
                                eq(WaterDistributionRatio::getMonth, total.getMonth()).
                                eq(WaterDistributionRatio::getTenDays, total.getTenDays()).
                                eq(WaterDistributionRatio::getTableHeadId, total.getTableHeadId()).one();
                        if(null != ratio){
                            //按比例水量
                            balance.setProportionalWaterQuantity(allRatio==0.00?null: NumberUtil.holdDecimal(((ratio.getV()==null?0.0:ratio.getV())/allRatio)* actualWaterReceived,3));
                        }
                        //实际水量
                        balance.setActualWaterVolume(total.getCurrentWaterVolume()==null?0.0:total.getCurrentWaterVolume());
                        //实际比例
                        Double actualProportion = actualWaterReceived==0.0?0.0:balance.getActualWaterVolume()/actualWaterReceived;
                        balance.setActualProportion(Double.isNaN(actualProportion)?null:Double.parseDouble(df.format(actualProportion*100)));
                        //盈亏水量
                        balance.setProfitAndLossWaterVolume(balance.getActualWaterVolume()-(balance.getProportionalWaterQuantity()==null?0.0:balance.getProportionalWaterQuantity()));
                        //盈亏比例
                        balance.setProfitAndLossRatio((balance.getActualProportion()==null?0.0:balance.getActualProportion())-(ratio==null?0.0:ratio.getV()==null?0.0:ratio.getV()));
                        result.add(balance);
                    }
                }
            }
            boolean b = this.updateBatchById(result);
            if(b){
                return RestResponse.ok();
            }else {
                return RestResponse.no("error");
            }

        }else {
            return addFirst(totalList);
        }
    }

    @Override
    public RestResponse selectTotalForIndexWarning(String stationName,String time) {
        List<SelectTotalForIndexWarningRes> resList = new ArrayList<>();
        String mk = (String) redisUtil.get("trendsTableParam:list");
        if(StringUtils.isEmpty(mk)){
            trendsTableParamService.updateCache();
            mk = (String) redisUtil.get("trendsTableParam:list");
        }
        List<TrendsTableParam> trendsTableParamListTemp = JSONObject.parseArray(mk, TrendsTableParam.class);
        List<TrendsTableParam> trendsTableParamList = trendsTableParamListTemp.stream().filter(t -> t.getUseType() == 2 && t.getUseStation().equals(stationName)&& t.getPId().equals("0") && !t.getParamName().equals("合计")).collect(Collectors.toList());
        List<TrendsTableParam> trendsTableParamAllList = trendsTableParamListTemp.stream().filter(t -> t.getUseType() == 2 && t.getUseStation().equals(stationName)).collect(Collectors.toList());
        String[] split = time.split("-");
        Integer year = Integer.valueOf(split[0]);
        Integer month =Integer.valueOf(split[1]);
        Integer day = Integer.valueOf(split[2]);
        String tenDays = determineTenDays(day);
        List<String> paramIds = trendsTableParamList.stream().map(TrendsTableParam::getId).collect(Collectors.toList());
        List<TrendsTableParamVo> resultParamVoList = new ArrayList<>();
        for(String s:paramIds){
            TrendsTableParamVo vo = new TrendsTableParamVo();
            List<TrendsTableParam> collect = trendsTableParamAllList.stream().filter(t -> t.getPId().equals(s)).collect(Collectors.toList());
            if(collect.size()==0){
                String name = (String)redisUtil.get("trendsTableParam:name:"+s);
                vo.setName(name);
                vo.setId(s);
            }
            if (collect.size()==1){
                TrendsTableParam param = collect.get(0);
                String name = (String)redisUtil.get("trendsTableParam:name:"+param.getPId());
                vo.setName(name);
                vo.setId(param.getId());
            }
            if(collect.size()>1) {
                TrendsTableParam param = collect.stream().filter(t -> t.getParamName().equals("合计")).collect(Collectors.toList()).get(0);
                String name = (String)redisUtil.get("trendsTableParam:name:"+param.getPId());
                vo.setName(name);
                vo.setId(param.getId());
            }
            resultParamVoList.add(vo);
        }
        List<String> strings = resultParamVoList.stream().map(TrendsTableParamVo::getId).collect(Collectors.toList());
        List<TenDaysWaterBalance> waterFeeStatisticsTotalList = this.lambdaQuery().in(TenDaysWaterBalance::getTableHeadId, strings).eq(TenDaysWaterBalance::getYear, year).eq(TenDaysWaterBalance::getMonth, month).
                eq(TenDaysWaterBalance::getTenDays, tenDays).list();
        for(TrendsTableParamVo vo:resultParamVoList){
            SelectTotalForIndexWarningRes res = new SelectTotalForIndexWarningRes();
            res.setName(vo.getName().replace("管理站",""));
            Double proportionalWaterQuantity = waterFeeStatisticsTotalList.stream().filter(t -> t.getTableHeadId().equals(vo.getId())).map(TenDaysWaterBalance::getProportionalWaterQuantity).reduce(Double::sum).orElse(0.00);
            Double actualWaterVolume = waterFeeStatisticsTotalList.stream().filter(t -> t.getTableHeadId().equals(vo.getId())).map(TenDaysWaterBalance::getActualWaterVolume).reduce(Double::sum).orElse(0.00);
            res.setActualWaterVolume(formatDoubleForThreeDecimal(actualWaterVolume));
            res.setProportionalWaterQuantity(formatDoubleForThreeDecimal(proportionalWaterQuantity));
            resList.add(res);
        }
        return RestResponse.ok(resList);
    }

    public String determineTenDays(Integer day){
        if(day<=10){
            return "上旬";
        }
        if(day<=20){
            return "中旬";
        }
        if(day>20){
            return "下旬";
        }
        return "";
    }

    private Double formatDoubleForThreeDecimal(Double value){
        String format = df.format(value);
        double v = Double.parseDouble(format);
        return v;
    }
}

