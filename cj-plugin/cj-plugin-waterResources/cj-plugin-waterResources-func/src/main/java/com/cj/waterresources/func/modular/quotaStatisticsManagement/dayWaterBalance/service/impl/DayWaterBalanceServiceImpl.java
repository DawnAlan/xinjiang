package com.cj.waterresources.func.modular.quotaStatisticsManagement.dayWaterBalance.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.common.model.RestResponse;
import com.cj.common.util.NumberUtil;
import com.cj.common.util.UUIDUtils;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.dayWaterBalance.bean.req.DayWaterBalanceSelectListReq;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.dayWaterBalance.mapper.DayWaterBalanceMapper;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.dayWaterBalance.entity.DayWaterBalance;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.dayWaterBalance.service.DayWaterBalanceService;
import com.cj.waterresources.func.modular.trendsTable.entity.TrendsTableParam;
import com.cj.waterresources.func.modular.trendsTable.service.TrendsTableParamService;
import com.cj.waterresources.func.modular.waterPrice.waterDistributionRatio.entity.WaterDistributionRatio;
import com.cj.waterresources.func.modular.waterPrice.waterDistributionRatio.service.WaterDistributionRatioService;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.entity.WaterFeeStatisticsDetails;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 日水量平衡表(DayWaterBalance)表服务实现类
 *
 * @author makejava
 * @since 2023-12-22 18:39:38
 */
@Service("dayWaterBalanceService")
public class DayWaterBalanceServiceImpl extends ServiceImpl<DayWaterBalanceMapper, DayWaterBalance> implements DayWaterBalanceService {

    @Autowired
    private TrendsTableParamService trendsTableParamService;

    @Autowired
    private WaterDistributionRatioService waterDistributionRatioService;

    @Override
    public RestResponse<List<DayWaterBalance>> selectList(DayWaterBalanceSelectListReq req) {
        List<DayWaterBalance> list = this.lambdaQuery().eq(StringUtils.isNotEmpty(req.getStation()), DayWaterBalance::getStation, req.getStation()).
                eq(req.getYear() != null, DayWaterBalance::getYear, req.getYear()).
                eq(req.getMonth() != null, DayWaterBalance::getMonth, req.getMonth()).
                eq(req.getDay() != null, DayWaterBalance::getDay, req.getDay()).
                eq(DayWaterBalance::getDel, 0).list();
        if(null!=list && list.size()>0){
            return RestResponse.ok(list);
        }else {
            return RestResponse.no("暂无数据");
        }
    }

    @Override
    public RestResponse addFirst(List<WaterFeeStatisticsDetails> detailsList, Integer day) {
        List<DayWaterBalance> result = new ArrayList<>();
        Double actualWaterReceived = 0.00;
        Double totalRatio = 0.00;
        for(WaterFeeStatisticsDetails details:detailsList){
            TrendsTableParam param = trendsTableParamService.lambdaQuery().
                    eq(TrendsTableParam::getId, details.getTableHeadId()).
                    eq(TrendsTableParam::getPId, "0").
                    eq(TrendsTableParam::getParamName, "合计").one();
            if(null!= param){
                DayWaterBalance balance = new DayWaterBalance();
                balance.setId(UUIDUtils.getUUID());
                balance.setDel(0);
                balance.setCreateTime(new Date());
                balance.setStation(details.getStation());
                balance.setYear(details.getYear());
                balance.setMonth(details.getMonth());
                balance.setDay(day);
                balance.setTableHeadId(details.getTableHeadId());
                ////总实收水量
                actualWaterReceived = details.getV();
                balance.setActualWaterReceived(details.getV());
                WaterDistributionRatio ratio = waterDistributionRatioService.lambdaQuery().eq(WaterDistributionRatio::getStation, details.getStation()).
                        eq(WaterDistributionRatio::getYear, details.getYear()).
                        eq(WaterDistributionRatio::getMonth, details.getMonth()).
                        eq(WaterDistributionRatio::getTenDays, details.getTenDays()).
                        eq(WaterDistributionRatio::getTableHeadId, details.getTableHeadId()).one();
                if(null != ratio){
                    //按比例水量
                    balance.setProportionalWaterQuantity(NumberUtil.holdDecimal((balance.getActualWaterReceived()==null?0.0:balance.getActualWaterReceived()),3));
                    totalRatio = ratio.getV()==null?0.0:ratio.getV();
                }
                //实际水量
                balance.setActualWaterVolume(NumberUtil.holdDecimal(balance.getActualWaterReceived()==null?0.0:balance.getActualWaterReceived(),3));
                result.add(balance);
            }
        }
        for(WaterFeeStatisticsDetails details:detailsList){
            TrendsTableParam param = trendsTableParamService.lambdaQuery().
                    eq(TrendsTableParam::getId, details.getTableHeadId()).
                    eq(TrendsTableParam::getPId, "0").
                    eq(TrendsTableParam::getParamName, "合计").one();
            if(null== param){
                DayWaterBalance balance = new DayWaterBalance();
                balance.setId(UUIDUtils.getUUID());
                balance.setDel(0);
                balance.setCreateTime(new Date());
                balance.setStation(details.getStation());
                balance.setYear(details.getYear());
                balance.setMonth(details.getMonth());
                balance.setDay(day);
                balance.setTableHeadId(details.getTableHeadId());
                //总实收水量
                //balance.setActualWaterReceived(0.0);
                WaterDistributionRatio ratio = waterDistributionRatioService.lambdaQuery().eq(WaterDistributionRatio::getStation, details.getStation()).
                        eq(WaterDistributionRatio::getYear, details.getYear()).
                        eq(WaterDistributionRatio::getMonth, details.getMonth()).
                        eq(WaterDistributionRatio::getTenDays, details.getTenDays()).
                        eq(WaterDistributionRatio::getTableHeadId, details.getTableHeadId()).one();
                if(null != ratio){
                    //按比例水量
                    balance.setProportionalWaterQuantity(totalRatio==0.00?null:NumberUtil.holdDecimal(((ratio.getV()==null?0.0:ratio.getV())/totalRatio)* actualWaterReceived,3));
                }
                //实际水量
                balance.setActualWaterVolume(details.getV()==null?null:NumberUtil.holdDecimal(details.getV(),3));
                result.add(balance);
            }
        }
        boolean b = this.saveBatch(result);
        if(b){
            return RestResponse.ok();
        }else {
            return RestResponse.no("error");
        }
    }

    @Override
    public RestResponse update(List<WaterFeeStatisticsDetails> detailsList, Integer day) {
        WaterFeeStatisticsDetails detailTemp = detailsList.get(0);
        Double actualWaterReceived = 0.00;
        Double totalRatio = 0.00;
        List<DayWaterBalance> list = this.lambdaQuery().eq(DayWaterBalance::getStation, detailTemp.getStation()).
                eq(DayWaterBalance::getYear, detailTemp.getYear()).
                eq(DayWaterBalance::getMonth, detailTemp.getMonth()).
                eq(DayWaterBalance::getDay, day).list();
        if(null != list && list.size()>0){
            List<DayWaterBalance> result = new ArrayList<>();
            for(DayWaterBalance balance:list){
                for(WaterFeeStatisticsDetails details:detailsList){
                    if(balance.getTableHeadId().equals(details.getTableHeadId())){
                        TrendsTableParam param = trendsTableParamService.lambdaQuery().
                                eq(TrendsTableParam::getId, details.getTableHeadId()).
                                eq(TrendsTableParam::getPId, "0").
                                eq(TrendsTableParam::getParamName, "合计").one();
                        if(null!= param){
                            balance.setUpdateTime(new Date());
                            ////总实收水量
                            actualWaterReceived = details.getV();
                            balance.setActualWaterReceived(details.getV());
                            WaterDistributionRatio ratio = waterDistributionRatioService.lambdaQuery().eq(WaterDistributionRatio::getStation, details.getStation()).
                                    eq(WaterDistributionRatio::getYear, details.getYear()).
                                    eq(WaterDistributionRatio::getMonth, details.getMonth()).
                                    eq(WaterDistributionRatio::getTenDays, details.getTenDays()).
                                    eq(WaterDistributionRatio::getTableHeadId, details.getTableHeadId()).one();
                            if(null != ratio){
                                //按比例水量
                                balance.setProportionalWaterQuantity(NumberUtil.holdDecimal((balance.getActualWaterReceived()==null?0.0:balance.getActualWaterReceived()),3));
                                totalRatio = ratio.getV()==null?0.0:ratio.getV();
                            }else {
                                balance.setProportionalWaterQuantity(null);
                            }
                            //实际水量
                            balance.setActualWaterVolume(NumberUtil.holdDecimal(balance.getActualWaterReceived()==null?0.0:balance.getActualWaterReceived(),3));
                            result.add(balance);
                        }
                    }
                }
            }
            for(DayWaterBalance balance:list){
                for(WaterFeeStatisticsDetails details:detailsList){
                    if(balance.getTableHeadId().equals(details.getTableHeadId())){
                        TrendsTableParam param = trendsTableParamService.lambdaQuery().
                                eq(TrendsTableParam::getId, details.getTableHeadId()).
                                eq(TrendsTableParam::getPId, "0").
                                eq(TrendsTableParam::getParamName, "合计").one();
                        if(null== param){
                            balance.setUpdateTime(new Date());
                            WaterDistributionRatio ratio = waterDistributionRatioService.lambdaQuery().eq(WaterDistributionRatio::getStation, details.getStation()).
                                    eq(WaterDistributionRatio::getYear, details.getYear()).
                                    eq(WaterDistributionRatio::getMonth, details.getMonth()).
                                    eq(WaterDistributionRatio::getTenDays, details.getTenDays()).
                                    eq(WaterDistributionRatio::getTableHeadId, details.getTableHeadId()).one();
                            if(null != ratio){
                                //按比例水量
                                balance.setProportionalWaterQuantity(totalRatio==0.00?null:NumberUtil.holdDecimal(((ratio.getV()==null?0.0:ratio.getV())/totalRatio)* actualWaterReceived,3));
                            }else {
                                balance.setProportionalWaterQuantity(null);
                            }
                            //实际水量
                            balance.setActualWaterVolume(details.getV()==null?null:NumberUtil.holdDecimal(details.getV(),3));
                            result.add(balance);
                        }
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
           return RestResponse.no("day balance not found");
        }
    }
}

