package com.cj.waterresources.func.modular.quotaStatisticsManagement.tenDaysWaterBalance.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.common.model.RestResponse;
import com.cj.common.util.UUIDUtils;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.tenDaysWaterBalance.bean.req.TenDaysWaterBalanceSelectListReq;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.tenDaysWaterBalance.mapper.TenDaysWaterBalanceMapper;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.tenDaysWaterBalance.entity.TenDaysWaterBalance;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.tenDaysWaterBalance.service.TenDaysWaterBalanceService;
import com.cj.waterresources.func.modular.trendsTable.entity.TrendsTableParam;
import com.cj.waterresources.func.modular.trendsTable.service.TrendsTableParamService;
import com.cj.waterresources.func.modular.waterPrice.canalHeadManagementStation.entity.CanalHeadManagementStationTotal;
import com.cj.waterresources.func.modular.waterPrice.waterDistributionRatio.entity.WaterDistributionRatio;
import com.cj.waterresources.func.modular.waterPrice.waterDistributionRatio.service.WaterDistributionRatioService;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.entity.WaterFeeStatisticsTotal;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
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
        List<TenDaysWaterBalance> result = new ArrayList<>();
        Double actualWaterReceived = 0.0;
        if(totalList.get(0).getStation().equals("渠首管理站")){
            List<String> tableHeadIdList = totalList.stream().map(WaterFeeStatisticsTotal::getTableHeadId).collect(Collectors.toList());
            TrendsTableParam trendsTableParamForTotal = trendsTableParamService.lambdaQuery().in(TrendsTableParam::getId, tableHeadIdList).eq(TrendsTableParam::getParamName, "引水").one();
            List<WaterFeeStatisticsTotal> resultTemp = totalList.stream().filter(t -> t.getTableHeadId().equals(trendsTableParamForTotal.getId())).collect(Collectors.toList());
            actualWaterReceived = resultTemp.get(0).getCurrentWaterVolume();

            for(WaterFeeStatisticsTotal total:totalList){
                TenDaysWaterBalance balance = new TenDaysWaterBalance();
                String paramName = trendsTableParamService.getById(total.getTableHeadId()).getParamName();
                if(paramName.equals("引水")){
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
                        eq(WaterDistributionRatio::getTableBeadId, total.getTableHeadId()).one();
                if(null != ratio){
                    //按比例水量
                    balance.setProportionalWaterQuantity(((ratio.getV()==null?0.0:ratio.getV())/100)* (total.getCurrentWaterVolume()==null?0.0:total.getCurrentWaterVolume()));
                }
                //实际水量
                balance.setActualWaterVolume(total.getCurrentWaterVolume());
                //实际比例
                Double actualProportion = balance.getActualWaterVolume()/actualWaterReceived;
                balance.setActualProportion(Double.isNaN(actualProportion)?null:Double.parseDouble(df.format(actualProportion*100)));
                //盈亏水量
                balance.setProfitAndLossWaterVolume(balance.getActualWaterVolume()-(balance.getProportionalWaterQuantity()==null?0.0:balance.getProportionalWaterQuantity()));
                //盈亏比例
                balance.setProfitAndLossRatio((balance.getActualProportion()==null?0.0:balance.getActualProportion())-(ratio.getV()==null?0.0:ratio.getV()));
                result.add(balance);
            }
        }else {
            List<String> tableHeadIdList = totalList.stream().map(WaterFeeStatisticsTotal::getTableHeadId).collect(Collectors.toList());
            TrendsTableParam trendsTableParamForTotal = trendsTableParamService.lambdaQuery().in(TrendsTableParam::getId, tableHeadIdList).eq(TrendsTableParam::getPId, "0").eq(TrendsTableParam::getParamName, "合计").one();
            List<WaterFeeStatisticsTotal> resultTemp = totalList.stream().filter(t -> t.getTableHeadId().equals(trendsTableParamForTotal.getId())).collect(Collectors.toList());
            actualWaterReceived = resultTemp.get(0).getCurrentWaterVolume();

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
                        eq(WaterDistributionRatio::getTableBeadId, total.getTableHeadId()).one();
                if(null != ratio){
                    //按比例水量
                    balance.setProportionalWaterQuantity(((ratio.getV()==null?0.0:ratio.getV())/100)* (total.getCurrentWaterVolume()==null?0.0:total.getCurrentWaterVolume()));
                }
                //实际水量
                balance.setActualWaterVolume(total.getCurrentWaterVolume());
                //实际比例
                Double actualProportion = balance.getActualWaterVolume()/actualWaterReceived;
                balance.setActualProportion(Double.isNaN(actualProportion)?null:Double.parseDouble(df.format(actualProportion*100)));
                //盈亏水量
                balance.setProfitAndLossWaterVolume(balance.getActualWaterVolume()-(balance.getProportionalWaterQuantity()==null?0.0:balance.getProportionalWaterQuantity()));
                //盈亏比例
                balance.setProfitAndLossRatio((balance.getActualProportion()==null?0.0:balance.getActualProportion())-(ratio.getV()==null?0.0:ratio.getV()));
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
    @Transactional(rollbackFor = Exception.class)
    public RestResponse add(List<WaterFeeStatisticsTotal> totalList) {
        List<TenDaysWaterBalance> list = this.lambdaQuery().eq(TenDaysWaterBalance::getStation, totalList.get(0).getStation()).
                eq(TenDaysWaterBalance::getYear, totalList.get(0).getYear()).
                eq(TenDaysWaterBalance::getMonth, totalList.get(0).getMonth()).
                eq(TenDaysWaterBalance::getTenDays, totalList.get(0).getTenDays()).
                eq(TenDaysWaterBalance::getDel, 0).list();
        if(null!=list && list.size()>0){
            List<TenDaysWaterBalance> result = new ArrayList<>();
            Double actualWaterReceived = 0.0;
            if(totalList.get(0).getStation().equals("渠首管理站")){

                List<String> tableHeadIdList = totalList.stream().map(WaterFeeStatisticsTotal::getTableHeadId).collect(Collectors.toList());
                TrendsTableParam trendsTableParamForTotal = trendsTableParamService.lambdaQuery().in(TrendsTableParam::getId, tableHeadIdList).eq(TrendsTableParam::getParamName, "引水").one();
                List<WaterFeeStatisticsTotal> resultTemp = totalList.stream().filter(t -> t.getTableHeadId().equals(trendsTableParamForTotal.getId())).collect(Collectors.toList());
                actualWaterReceived = resultTemp.get(0).getCurrentWaterVolume();

                for(TenDaysWaterBalance balance:list){
                    for(WaterFeeStatisticsTotal total:totalList){
                        if(balance.getTableHeadId().equals(total.getTableHeadId())){
                            String paramName = trendsTableParamService.getById(total.getTableHeadId()).getParamName();
                            if(paramName.equals("引水")){
                                //总实收水量
                                balance.setActualWaterReceived(actualWaterReceived);
                            }
                            balance.setUpdateTime(new Date());
                            //总实收水量
                            //balance.setActualWaterReceived(total.getCurrentWaterVolume());
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
                            balance.setActualWaterVolume(total.getCurrentWaterVolume()==null?0.0:total.getCurrentWaterVolume());
                            //实际比例
                            Double actualProportion = balance.getActualWaterVolume()/actualWaterReceived;
                            balance.setActualProportion(Double.isNaN(actualProportion)?null:Double.parseDouble(df.format(actualProportion*100)));
                            //盈亏水量
                            balance.setProfitAndLossWaterVolume(balance.getActualWaterVolume()-(balance.getProportionalWaterQuantity()==null?0.0:balance.getProportionalWaterQuantity()));
                            //盈亏比例
                            balance.setProfitAndLossRatio((balance.getActualProportion()==null?0.0:balance.getActualProportion())-(ratio.getV()==null?0.0:ratio.getV()));
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

                List<String> tableHeadIdList = totalList.stream().map(WaterFeeStatisticsTotal::getTableHeadId).collect(Collectors.toList());
                TrendsTableParam trendsTableParamForTotal = trendsTableParamService.lambdaQuery().in(TrendsTableParam::getId, tableHeadIdList).eq(TrendsTableParam::getPId, "0").eq(TrendsTableParam::getParamName, "合计").one();
                List<WaterFeeStatisticsTotal> resultTemp = totalList.stream().filter(t -> t.getTableHeadId().equals(trendsTableParamForTotal.getId())).collect(Collectors.toList());
                actualWaterReceived = resultTemp.get(0).getCurrentWaterVolume();

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
                                    eq(WaterDistributionRatio::getTableBeadId, total.getTableHeadId()).one();
                            if(null != ratio){
                                //按比例水量
                                balance.setProportionalWaterQuantity(((ratio.getV()==null?0.0:ratio.getV())/100)* (total.getCurrentWaterVolume()==null?0.0:total.getCurrentWaterVolume()));
                            }
                            //实际水量
                            balance.setActualWaterVolume(total.getCurrentWaterVolume()==null?0.0:total.getCurrentWaterVolume());
                            //实际比例
                            Double actualProportion = balance.getActualWaterVolume()/actualWaterReceived;
                            balance.setActualProportion(Double.isNaN(actualProportion)?null:Double.parseDouble(df.format(actualProportion*100)));
                            //盈亏水量
                            balance.setProfitAndLossWaterVolume(balance.getActualWaterVolume()-(balance.getProportionalWaterQuantity()==null?0.0:balance.getProportionalWaterQuantity()));
                            //盈亏比例
                            balance.setProfitAndLossRatio((balance.getActualProportion()==null?0.0:balance.getActualProportion())-(ratio.getV()==null?0.0:ratio.getV()));
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
            }

        }else {
            return addFirst(totalList);
        }
    }
}

