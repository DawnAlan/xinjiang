package com.cj.waterresources.func.modular.quotaStatisticsManagement.dayWaterBalance.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.common.model.RestResponse;
import com.cj.common.util.UUIDUtils;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.dayWaterBalance.bean.req.DayWaterBalanceSelectListReq;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.dayWaterBalance.mapper.DayWaterBalanceMapper;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.dayWaterBalance.entity.DayWaterBalance;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.dayWaterBalance.service.DayWaterBalanceService;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.tenDaysWaterBalance.entity.TenDaysWaterBalance;
import com.cj.waterresources.func.modular.trendsTable.entity.TrendsTableParam;
import com.cj.waterresources.func.modular.trendsTable.service.TrendsTableParamService;
import com.cj.waterresources.func.modular.waterPrice.waterDistributionRatio.entity.WaterDistributionRatio;
import com.cj.waterresources.func.modular.waterPrice.waterDistributionRatio.service.WaterDistributionRatioService;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.entity.WaterFeeStatisticsTotal;
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
    public RestResponse addFirst(List<WaterFeeStatisticsTotal> totalList, Integer day) {
        List<DayWaterBalance> result = new ArrayList<>();
        Double actualWaterReceived = 0.0;
        for(WaterFeeStatisticsTotal total:totalList){
            TrendsTableParam param = trendsTableParamService.lambdaQuery().
                    eq(TrendsTableParam::getId, total.getTableHeadId()).
                    eq(TrendsTableParam::getPId, "0").
                    eq(TrendsTableParam::getParamName, "合计").one();
            if(null!= param){
                DayWaterBalance balance = new DayWaterBalance();
                balance.setId(UUIDUtils.getUUID());
                balance.setDel(0);
                balance.setCreateTime(new Date());
                balance.setStation(total.getStation());
                balance.setYear(total.getYear());
                balance.setMonth(total.getMonth());
                balance.setDay(day);
                balance.setTableHeadId(total.getTableHeadId());
                ////总实收水量
                balance.setActualWaterReceived(total.getAmountTo());
                actualWaterReceived = balance.getActualWaterReceived();
                WaterDistributionRatio ratio = waterDistributionRatioService.lambdaQuery().eq(WaterDistributionRatio::getStation, total.getStation()).
                        eq(WaterDistributionRatio::getYear, total.getYear()).
                        eq(WaterDistributionRatio::getMonth, total.getMonth()).
                        eq(WaterDistributionRatio::getTenDays, total.getTenDays()).
                        eq(WaterDistributionRatio::getTableBeadId, total.getTableHeadId()).one();
                if(null != ratio){
                    //按比例水量
                    balance.setProportionalWaterQuantity(((ratio.getV()==null?0.0:ratio.getV())/100)*(actualWaterReceived==null?0.0:actualWaterReceived));
                }
                //实际水量
                balance.setActualWaterVolume(actualWaterReceived==null?0.0:actualWaterReceived);
                result.add(balance);
            }else {
                DayWaterBalance balance = new DayWaterBalance();
                balance.setId(UUIDUtils.getUUID());
                balance.setDel(0);
                balance.setCreateTime(new Date());
                balance.setStation(total.getStation());
                balance.setYear(total.getYear());
                balance.setMonth(total.getMonth());
                balance.setDay(day);
                balance.setTableHeadId(total.getTableHeadId());
                //总实收水量
                //balance.setActualWaterReceived(0.0);
                WaterDistributionRatio ratio = waterDistributionRatioService.lambdaQuery().eq(WaterDistributionRatio::getStation, total.getStation()).
                        eq(WaterDistributionRatio::getYear, total.getYear()).
                        eq(WaterDistributionRatio::getMonth, total.getMonth()).
                        eq(WaterDistributionRatio::getTenDays, total.getTenDays()).
                        eq(WaterDistributionRatio::getTableBeadId, total.getTableHeadId()).one();
                if(null != ratio){
                    //按比例水量
                    balance.setProportionalWaterQuantity(((ratio.getV()==null?0.0:ratio.getV())/100)* (total.getCurrentWaterVolume()==null?0.0:total.getCurrentWaterVolume()));
                }
                //实际水量
                balance.setActualWaterVolume(total.getAmountTo());
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
    public RestResponse add(List<WaterFeeStatisticsTotal> totalList,Integer day) {
        WaterFeeStatisticsTotal totalTemp = totalList.get(0);
        List<DayWaterBalance> list = this.lambdaQuery().eq(DayWaterBalance::getStation, totalTemp.getStation()).
                eq(DayWaterBalance::getYear, totalTemp.getYear()).
                eq(DayWaterBalance::getMonth, totalTemp.getMonth()).
                eq(DayWaterBalance::getDay, day).list();
        if(null != list && list.size()>0){
            List<DayWaterBalance> result = new ArrayList<>();
            Double actualWaterReceived = 0.0;
            for(DayWaterBalance balance:list){
                for(WaterFeeStatisticsTotal total:totalList){
                    if(balance.getTableHeadId().equals(total.getTableHeadId())){
                        TrendsTableParam param = trendsTableParamService.lambdaQuery().
                                eq(TrendsTableParam::getId, total.getTableHeadId()).
                                eq(TrendsTableParam::getPId, "0").
                                eq(TrendsTableParam::getParamName, "合计").one();
                        if(null!= param){
                            balance.setUpdateTime(new Date());
                            ////总实收水量
                            balance.setActualWaterReceived(total.getAmountTo());
                            actualWaterReceived = balance.getActualWaterReceived();
                            WaterDistributionRatio ratio = waterDistributionRatioService.lambdaQuery().eq(WaterDistributionRatio::getStation, total.getStation()).
                                    eq(WaterDistributionRatio::getYear, total.getYear()).
                                    eq(WaterDistributionRatio::getMonth, total.getMonth()).
                                    eq(WaterDistributionRatio::getTenDays, total.getTenDays()).
                                    eq(WaterDistributionRatio::getTableBeadId, total.getTableHeadId()).one();
                            if(null != ratio){
                                //按比例水量
                                balance.setProportionalWaterQuantity(((ratio.getV()==null?0.0:ratio.getV())/100)*(actualWaterReceived==null?0.0:actualWaterReceived));
                            }
                            //实际水量
                            balance.setActualWaterVolume(actualWaterReceived==null?0.0:actualWaterReceived);
                            result.add(balance);
                        }else {
                            balance.setUpdateTime(new Date());
                            //总实收水量
                            //balance.setActualWaterReceived(0.0);
                            WaterDistributionRatio ratio = waterDistributionRatioService.lambdaQuery().eq(WaterDistributionRatio::getStation, total.getStation()).
                                    eq(WaterDistributionRatio::getYear, total.getYear()).
                                    eq(WaterDistributionRatio::getMonth, total.getMonth()).
                                    eq(WaterDistributionRatio::getTenDays, total.getTenDays()).
                                    eq(WaterDistributionRatio::getTableBeadId, total.getTableHeadId()).one();
                            if(null != ratio){
                                //按比例水量
                                balance.setProportionalWaterQuantity(((ratio.getV()==null?0.0:ratio.getV())/100)* (total.getCurrentWaterVolume()==null?0.0:total.getCurrentWaterVolume()));
                            }
                            //实际水量
                            balance.setActualWaterVolume(total.getAmountTo());
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
           return addFirst(totalList,day);
        }
    }


}

