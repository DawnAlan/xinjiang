package com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.common.model.RestResponse;
import com.cj.common.util.UUIDUtils;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.entity.IrrigatedPlatformDataInfo;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.service.IrrigatedPlatformDataInfoService;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.dayWaterBalance.entity.DayWaterBalance;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.dayWaterBalance.service.DayWaterBalanceService;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.tenDaysWaterBalance.entity.TenDaysWaterBalance;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.tenDaysWaterBalance.service.TenDaysWaterBalanceService;
import com.cj.waterresources.func.modular.trendsTable.entity.TrendsTableParam;
import com.cj.waterresources.func.modular.trendsTable.service.TrendsTableParamService;
import com.cj.waterresources.func.modular.waterPrice.paymentWaterFees.entity.PaymentWaterFees;
import com.cj.waterresources.func.modular.waterPrice.paymentWaterFees.service.PaymentWaterFeesService;
import com.cj.waterresources.func.modular.waterPrice.totalIdToStation.entity.TotalIdToStation;
import com.cj.waterresources.func.modular.waterPrice.totalIdToStation.service.TotalIdToStationService;
import com.cj.waterresources.func.modular.waterPrice.waterDistributionRatio.entity.WaterDistributionRatio;
import com.cj.waterresources.func.modular.waterPrice.waterDistributionRatio.service.WaterDistributionRatioService;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.bean.req.WaterFeeStatisticsDetailsSelectListReq;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.entity.WaterFeeStatisticsTotal;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.mapper.WaterFeeStatisticsDetailsMapper;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.entity.WaterFeeStatisticsDetails;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.service.WaterFeeStatisticsDetailsService;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.service.WaterFeeStatisticsTotalService;
import com.cj.waterresources.func.modular.waterPrice.waterPriceManagement.entity.WaterPriceManagement;
import com.cj.waterresources.func.modular.waterPrice.waterPriceManagement.service.WaterPriceManagementService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 水费统计详情(WaterFeeStatisticsDetails)表服务实现类
 *
 * @author makejava
 * @since 2023-11-29 17:15:45
 */
@Service("waterFeeStatisticsDetailsService")
public class WaterFeeStatisticsDetailsServiceImpl extends ServiceImpl<WaterFeeStatisticsDetailsMapper, WaterFeeStatisticsDetails> implements WaterFeeStatisticsDetailsService {

    @Autowired
    private TotalIdToStationService totalIdToStationService;

    @Autowired
    private TrendsTableParamService trendsTableParamService;

    @Autowired
    private PaymentWaterFeesService paymentWaterFeesService;

    @Autowired
    private WaterPriceManagementService waterPriceManagementService;

    @Autowired
    private WaterFeeStatisticsTotalService waterFeeStatisticsTotalService;

    @Value("${waterResourcePrice}")
    private String waterResourcePrice;

    @Autowired
    private WaterDistributionRatioService waterDistributionRatioService;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    private IrrigatedPlatformDataInfoService irrigatedPlatformDataInfoService;

    @Autowired
    private DayWaterBalanceService dayWaterBalanceService;

    @Autowired
    private TenDaysWaterBalanceService tenDaysWaterBalanceService;


    public  Map<String,Double> getLanternCanalInfoByDate(Integer year,Integer month,String tenDays,String statisticsDate) {
        Map<String,Double> resultMap = new HashMap<>();
        List<WaterFeeStatisticsDetails> lanternCanalInfoList = this.baseMapper.selectListByName("渠首灯笼渠", year, month, tenDays,statisticsDate);
        List<TrendsTableParam> lanternCanalTrendsTableList = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getUseType, 2).eq(TrendsTableParam::getUseStation, "渠首灯笼渠").list();
        String totalTableId = lanternCanalTrendsTableList.stream().filter(t -> t.getParamName().equals("合计") && t.getPId().equals("0")).map(TrendsTableParam::getId).collect(Collectors.toList()).get(0);

        String agricultureTableId = lanternCanalTrendsTableList.stream().filter(t -> t.getParamName().equals("农业供水") && t.getPId().equals("0")).map(TrendsTableParam::getId).collect(Collectors.toList()).get(0);
        String agricultureTotalTableId = lanternCanalTrendsTableList.stream().filter(t -> t.getPId().equals(agricultureTableId) && t.getParamName().equals("合计")).map(TrendsTableParam::getId).collect(Collectors.toList()).get(0);

        String greenTableId = lanternCanalTrendsTableList.stream().filter(t -> t.getParamName().equals("绿化供水") && t.getPId().equals("0")).map(TrendsTableParam::getId).collect(Collectors.toList()).get(0);
        String greenTotalTableId = lanternCanalTrendsTableList.stream().filter(t -> t.getPId().equals(greenTableId) && t.getParamName().equals("合计")).map(TrendsTableParam::getId).collect(Collectors.toList()).get(0);

        String industryTableId = lanternCanalTrendsTableList.stream().filter(t -> t.getParamName().equals("工业供水") && t.getPId().equals("0")).map(TrendsTableParam::getId).collect(Collectors.toList()).get(0);
        String industryTotalTableId = lanternCanalTrendsTableList.stream().filter(t -> t.getPId().equals(industryTableId) && t.getParamName().equals("合计")).map(TrendsTableParam::getId).collect(Collectors.toList()).get(0);

        if(null != lanternCanalInfoList && lanternCanalInfoList.size()>0){
            WaterFeeStatisticsDetails total = lanternCanalInfoList.stream().filter(t -> t.getTableHeadId().equals(totalTableId)&&t.getV()!=null).collect(Collectors.toList()).get(0);
            WaterFeeStatisticsDetails agriculture = lanternCanalInfoList.stream().filter(t -> t.getTableHeadId().equals(agricultureTotalTableId)&&t.getV()!=null).collect(Collectors.toList()).get(0);
            WaterFeeStatisticsDetails green = lanternCanalInfoList.stream().filter(t -> t.getTableHeadId().equals(greenTotalTableId)&&t.getV()!=null).collect(Collectors.toList()).get(0);
            WaterFeeStatisticsDetails industry = lanternCanalInfoList.stream().filter(t -> t.getTableHeadId().equals(industryTotalTableId)&&t.getV()!=null).collect(Collectors.toList()).get(0);
            resultMap.put("totalValue", total.getV());
            resultMap.put("agricultureValue", agriculture.getV());
            resultMap.put("greenValue", green.getV());
            resultMap.put("industryValue", industry.getV());
        }else {
            resultMap.put("totalValue", null);
            resultMap.put("agricultureValue", null);
            resultMap.put("greenValue", null);
            resultMap.put("industryValue", null);
        }
        return resultMap;
    }


    @Override
    @Transactional(rollbackFor=Exception.class)
    public RestResponse add(List<WaterFeeStatisticsDetails> waterFeeStatisticsDetails) {
        String station = waterFeeStatisticsDetails.get(0).getStation();
        String dateTemp = waterFeeStatisticsDetails.get(0).getYear()+"-"+waterFeeStatisticsDetails.get(0).getMonth()+"-"+waterFeeStatisticsDetails.get(0).getStatisticsDate().substring(3,5);
        if(station.equals("渠首管理站")){
            waterFeeStatisticsDetails.forEach(t->{
                Map<String, Double> lanternCanalInfoByDate = getLanternCanalInfoByDate(t.getYear(),t.getMonth(),t.getTenDays(),t.getStatisticsDate());
                TrendsTableParam byId = trendsTableParamService.getById(t.getTableHeadId());
                if(byId.getParamName().equals("农业")){
                    t.setV(lanternCanalInfoByDate ==null?null:lanternCanalInfoByDate.get("agricultureValue")==null?null:lanternCanalInfoByDate.get("agricultureValue"));
                }
                if(byId.getParamName().equals("工业")){
                    t.setV(lanternCanalInfoByDate ==null?null:lanternCanalInfoByDate.get("industryValue")==null?null:lanternCanalInfoByDate.get("industryValue"));
                }
                if(byId.getParamName().equals("绿化")){
                    t.setV(lanternCanalInfoByDate ==null?null:lanternCanalInfoByDate.get("greenValue")==null?null:lanternCanalInfoByDate.get("greenValue"));
                }
                t.setId(UUIDUtils.getUUID());
            });
        }else {
            waterFeeStatisticsDetails.forEach(t->{
                if(!sdf.format(new Date()).equals(dateTemp)){
                    String dateTemp1 = waterFeeStatisticsDetails.get(0).getYear()+"-"+waterFeeStatisticsDetails.get(0).getMonth()+"-"+(Integer.valueOf(waterFeeStatisticsDetails.get(0).getStatisticsDate().substring(3,5))+1);
                    String paramName = trendsTableParamService.getById(t.getTableHeadId()).getParamName();
                    if(!paramName.equals("合计")){
                        IrrigatedPlatformDataInfo irrigatedPlatformDataInfo = irrigatedPlatformDataInfoService.selectOneByCondition(paramName, dateTemp1);
                        t.setV(irrigatedPlatformDataInfo==null?null:irrigatedPlatformDataInfo.getYesterdayAvgFlow()==null?null:irrigatedPlatformDataInfo.getYesterdayAvgFlow());
                    }
                }
                t.setId(UUIDUtils.getUUID());
            });
        }

        List<TotalIdToStation> list = totalIdToStationService.lambdaQuery().eq(TotalIdToStation::getUseType, 2).eq(TotalIdToStation::getStation, waterFeeStatisticsDetails.get(0).getStation()).list();
        //计算行合计
        if(null != list && list.size()>0){
            Double total = 0.0;
            List<String> collect = list.stream().filter(t->t.getName().equals("合计")).map(TotalIdToStation::getTotalId).collect(Collectors.toList());
            for(String id:collect){
                Double value = 0.0;
                TrendsTableParam tableParam = trendsTableParamService.getById(id);
                if(!tableParam.getPId().equals("0")){
                    List<TrendsTableParam> noTotalList = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, tableParam.getPId()).ne(TrendsTableParam::getParamName, "合计").list();
                    for(TrendsTableParam param:noTotalList){
                        for(WaterFeeStatisticsDetails t:waterFeeStatisticsDetails){
                            if(t.getTableHeadId().equals(param.getId())){
                                value+=t.getV()==null?0.0:t.getV();
                            }
                            List<TrendsTableParam> listed = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, param.getId()).ne(TrendsTableParam::getParamName, "合计").list();
                            if(null != listed && listed.size()>0){
                                for (TrendsTableParam param1:listed){
                                    if(t.getTableHeadId().equals(param1.getId())){
                                        value+=t.getV()==null?0.0:t.getV();
                                    }
                                }
                            }
                        }
                    }
                    for(WaterFeeStatisticsDetails t:waterFeeStatisticsDetails){
                        if(t.getTableHeadId().equals(id)){
                            t.setV(value);
                        }
                    }

                }
            }
            for(WaterFeeStatisticsDetails t:waterFeeStatisticsDetails){
                if(!list.stream().map(TotalIdToStation::getTotalId).collect(Collectors.toList()).contains(t.getTableHeadId())){
                    total+=t.getV()==null?0.0:t.getV();
                }
            }
            TrendsTableParam one = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, "0").eq(TrendsTableParam::getUseType,2).in(TrendsTableParam::getId, collect).one();
            if(null != one){
                for(WaterFeeStatisticsDetails t:waterFeeStatisticsDetails){
                    if(t.getTableHeadId().equals(one.getId())){
                        t.setV(total);
                    }
                }
            }
        }
        if(station.equals("渠首管理站")){
            Double laishui = 0.0;
            Double yinshui = 0.0;
            Double zonggan = 0.0;

            for(WaterFeeStatisticsDetails details:waterFeeStatisticsDetails){
                String paramName = trendsTableParamService.getById(details.getTableHeadId()).getParamName();
                if(!paramName.equals("合计") && !paramName.equals("来水") && !paramName.equals("引水") && !paramName.equals("泄洪") && (paramName.equals("东干") || paramName.equals("西干") || paramName.equals("漏斗"))){
                    zonggan += details.getV()==null?0.0:details.getV();
                }
            }
            for(WaterFeeStatisticsDetails details:waterFeeStatisticsDetails){
                String paramName = trendsTableParamService.getById(details.getTableHeadId()).getParamName();
                if(paramName.equals("总干")){
                    details.setV(zonggan);
                }
            }

            for(WaterFeeStatisticsDetails details:waterFeeStatisticsDetails){
                String paramName = trendsTableParamService.getById(details.getTableHeadId()).getParamName();
                if(!paramName.equals("合计") && !paramName.equals("来水") && !paramName.equals("引水") && !paramName.equals("总干") && !paramName.equals("泄洪")){
                    yinshui += details.getV()==null?0.0:details.getV();
                }
            }
            for(WaterFeeStatisticsDetails details:waterFeeStatisticsDetails){
                String paramName = trendsTableParamService.getById(details.getTableHeadId()).getParamName();
                if(paramName.equals("引水")){
                    details.setV(yinshui);
                }
            }

            for(WaterFeeStatisticsDetails details:waterFeeStatisticsDetails){
                String paramName = trendsTableParamService.getById(details.getTableHeadId()).getParamName();
                if(!paramName.equals("合计") && (paramName.equals("引水") || paramName.equals("泄洪"))){
                    laishui += details.getV()==null?0.0:details.getV();
                }
            }
            for(WaterFeeStatisticsDetails details:waterFeeStatisticsDetails){
                String paramName = trendsTableParamService.getById(details.getTableHeadId()).getParamName();
                if(paramName.equals("来水")){
                    details.setV(laishui);
                }
            }
        }
        boolean b = this.saveBatch(waterFeeStatisticsDetails);
        //计算列合计
        if (b) {
            Double payableWaterFeeCount = 0.0;
            Double unpaidWaterFeesCount = 0.0;
            Double payableWaterResourceCount = 0.0;
            Double waterResourceSurplusCount = 0.0;
            WaterFeeStatisticsDetails tempObj =  waterFeeStatisticsDetails.get(0);
            List<WaterFeeStatisticsTotal> tempList = waterFeeStatisticsTotalService.lambdaQuery().eq(WaterFeeStatisticsTotal::getStation, tempObj.getStation()).
                    eq(WaterFeeStatisticsTotal::getYear, tempObj.getYear()).
                    eq(WaterFeeStatisticsTotal::getMonth, tempObj.getMonth()).
                    eq(WaterFeeStatisticsTotal::getTenDays, tempObj.getTenDays()).list();
            //添加第二行之后的
            if(null != tempList && tempList.size()>0){
                List<WaterFeeStatisticsTotal> waterFeeStatisticsTotalList = new ArrayList<>();
                WaterFeeStatisticsDetails temp = waterFeeStatisticsDetails.get(0);
                for(WaterFeeStatisticsTotal tempTotal : tempList){
                    WaterFeeStatisticsTotal waterFeeStatisticsTotal = new WaterFeeStatisticsTotal();
                    BeanUtils.copyProperties(tempTotal,waterFeeStatisticsTotal);
                    Double amountTo = waterFeeStatisticsDetails.stream().filter(t -> t.getTableHeadId().equals(tempTotal.getTableHeadId()) && t.getV()!=null).map(WaterFeeStatisticsDetails::getV).reduce(Double::sum).orElse(0.00);
                    //本旬水量
                    waterFeeStatisticsTotal.setAmountTo(amountTo+waterFeeStatisticsTotal.getAmountTo());
                    waterFeeStatisticsTotal.setCurrentWaterVolume(waterFeeStatisticsTotal.getAmountTo()*60*60*24);
                    Map<String, Object> jisuan = jisuan(temp.getYear(), temp.getMonth(), temp.getTenDays());
                    if((Integer)jisuan.get("year")== waterFeeStatisticsDetails.get(0).getYear()){
                        WaterFeeStatisticsTotal one = waterFeeStatisticsTotalService.lambdaQuery().eq(WaterFeeStatisticsTotal::getYear, jisuan.get("year")).
                                eq(WaterFeeStatisticsTotal::getMonth, jisuan.get("month")).
                                eq(WaterFeeStatisticsTotal::getTenDays, jisuan.get("tenDays")).
                                eq(WaterFeeStatisticsTotal::getTableHeadId, tempTotal.getTableHeadId()).
                                eq(WaterFeeStatisticsTotal::getStation, temp.getStation()).
                                one();
                        //上旬水量
                        waterFeeStatisticsTotal.setWaterVolumeFirstTenDays(one==null?0.0:one.getAccumulatedWaterVolume());
                    }else {
                        waterFeeStatisticsTotal.setWaterVolumeFirstTenDays(0.0);
                    }
                    //累计水量
                    waterFeeStatisticsTotal.setAccumulatedWaterVolume(waterFeeStatisticsTotal.getCurrentWaterVolume()+waterFeeStatisticsTotal.getWaterVolumeFirstTenDays());
                    WaterPriceManagement byId = waterPriceManagementService.getById(tempTotal.getTableHeadId());
                    Double payableWaterFee = byId==null?0:byId.getWaterPrice()==null?0:byId.getWaterPrice()*waterFeeStatisticsTotal.getAccumulatedWaterVolume();
                    //应交水费
                    waterFeeStatisticsTotal.setPayableWaterFee(payableWaterFee);
                    if(tempObj.getStation().equals("河东管理站绿化") || tempObj.getStation().equals("渠首砂厂")){
                        waterFeeStatisticsTotal.setPayableWaterResource(Double.valueOf(waterResourcePrice)*waterFeeStatisticsTotal.getAccumulatedWaterVolume());
                    }
                    WaterFeeStatisticsTotal lastYear = waterFeeStatisticsTotalService.lambdaQuery().eq(WaterFeeStatisticsTotal::getYear, temp.getYear()-1).
                            eq(WaterFeeStatisticsTotal::getMonth,  temp.getMonth()).
                            eq(WaterFeeStatisticsTotal::getTenDays, temp.getTenDays()).
                            eq(WaterFeeStatisticsTotal::getTableHeadId, tempTotal.getTableHeadId()).
                            eq(WaterFeeStatisticsTotal::getStation, temp.getStation()).
                            one();
                    waterFeeStatisticsTotal.setWaterVolumeDuringLastYear(lastYear==null?0:lastYear.getAccumulatedWaterVolume());
                    waterFeeStatisticsTotalList.add(waterFeeStatisticsTotal);
                }


                if(tempObj.getStation().equals("河东管理站绿化") || tempObj.getStation().equals("渠首砂厂")){
                    //预交水资源费(单位)
                    Map<String,Double> tempMap = new HashMap<>();
                    for(WaterFeeStatisticsTotal total :waterFeeStatisticsTotalList){
                        Double paidWaterResource1 = 0.0;
                        Double paidWaterResource2 = 0.0;
                        List<PaymentWaterFees> list1 = paymentWaterFeesService.lambdaQuery().eq(PaymentWaterFees::getDel,0).
                                eq(PaymentWaterFees::getYear, waterFeeStatisticsDetails.get(0).getYear()).
                                eq(PaymentWaterFees::getWaterUserId, total.getTableHeadId()).eq(PaymentWaterFees::getType,"水资源费").list();
                        if(null != list1 && list1.size()>0){
                            for(PaymentWaterFees fees:list1){
                                paidWaterResource1+=fees.getPaymentAmount();
                            }
                            tempMap.put(total.getTableHeadId()+total.getStation()+total.getYear()+total.getMonth()+total.getTenDays(),paidWaterResource1);
                        }else {
                            TrendsTableParam byId1 = trendsTableParamService.getById(total.getTableHeadId());
                            if(!byId1.getPId().equals("0")){
                                TrendsTableParam one = trendsTableParamService.getById(byId1.getPId());
                                List<PaymentWaterFees> list2 = paymentWaterFeesService.lambdaQuery().eq(PaymentWaterFees::getDel,0).
                                        eq(PaymentWaterFees::getYear, waterFeeStatisticsDetails.get(0).getYear()).
                                        eq(PaymentWaterFees::getWaterUserId, one.getId()).eq(PaymentWaterFees::getType,"水资源费").list();
                                if(null!=list2 && list2.size()>0){
                                    for(PaymentWaterFees fees:list2){
                                        paidWaterResource2+=fees.getPaymentAmount();
                                    }
                                    TrendsTableParam totalBean = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, one.getId()).eq(TrendsTableParam::getParamName, "合计").one();
                                    if(totalBean!=null){
                                        tempMap.put(totalBean.getId()+total.getStation()+total.getYear()+total.getMonth()+total.getTenDays(),paidWaterResource2);
                                    }
                                }else {
                                    tempMap.put(total.getTableHeadId()+total.getStation()+total.getYear()+total.getMonth()+total.getTenDays(),0.0);
                                }
                            }else {
                                tempMap.put(total.getTableHeadId()+total.getStation()+total.getYear()+total.getMonth()+total.getTenDays(),0.0);
                            }
                        }
                    }
                    for(WaterFeeStatisticsTotal total :waterFeeStatisticsTotalList){
                        //预交水资源费
                        total.setPaidWaterResource(tempMap.get(total.getTableHeadId()+total.getStation()+total.getYear()+total.getMonth()+total.getTenDays())==null?0.0:tempMap.get(total.getTableHeadId()+total.getStation()+total.getYear()+total.getMonth()+total.getTenDays()));
                        //水资源费余(欠)
                        total.setWaterResourceSurplus(total.getPaidWaterResource()-total.getPayableWaterResource());
                    }
                    //预交水资源费(合计)
                    List<TotalIdToStation> listTotalIdToStation = totalIdToStationService.lambdaQuery().eq(TotalIdToStation::getUseType, 2).eq(TotalIdToStation::getStation, waterFeeStatisticsDetails.get(0).getStation()).list();
                    if(null != listTotalIdToStation && listTotalIdToStation.size()>0){
                        List<String> totalColl = listTotalIdToStation.stream().filter(t->t.getName().equals("合计")).map(TotalIdToStation::getTotalId).collect(Collectors.toList());
                        for(String id:totalColl){
                            TrendsTableParam byId = trendsTableParamService.getById(id);
                            if(!byId.getPId().equals("0")){
                                List<TrendsTableParam> tableParams = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, byId.getPId()).ne(TrendsTableParam::getParamName, "合计").list();
                                for(TrendsTableParam param:tableParams){
                                    List<TrendsTableParam> listed = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, param.getId()).ne(TrendsTableParam::getParamName, "合计").list();
                                    if(listed.size()>0){
                                        for(TrendsTableParam param1:listed){
                                            for(WaterFeeStatisticsTotal total:waterFeeStatisticsTotalList){
                                                if(param1.getId().equals(total.getTableHeadId())){
                                                    payableWaterResourceCount+=total.getPayableWaterResource();
                                                    waterResourceSurplusCount+=total.getWaterResourceSurplus();
                                                }
                                            }
                                        }
                                    }else {
                                        for(WaterFeeStatisticsTotal total:waterFeeStatisticsTotalList){
                                            if(param.getId().equals(total.getTableHeadId())){
                                                payableWaterResourceCount+=total.getPayableWaterResource();
                                                waterResourceSurplusCount+=total.getWaterResourceSurplus();
                                            }
                                        }
                                    }
                                }
                                for(WaterFeeStatisticsTotal total:waterFeeStatisticsTotalList){
                                    if(total.getTableHeadId().equals(id)){
                                        total.setPayableWaterResource(payableWaterResourceCount);
                                        total.setWaterResourceSurplus(waterResourceSurplusCount);
                                        payableWaterResourceCount = 0.0;
                                        waterResourceSurplusCount = 0.0;
                                    }
                                }
                            }
                        }
                    }
                    if(null != listTotalIdToStation && listTotalIdToStation.size()>0){
                        Double paidWaterResourceCount = 0.0;
                        List<String> totalColl = listTotalIdToStation.stream().filter(t->t.getName().equals("合计")).map(TotalIdToStation::getTotalId).collect(Collectors.toList());
                        for(String id:totalColl){
                            TrendsTableParam byId = trendsTableParamService.getById(id);
                            if(byId.getPId().equals("0")){
                                List<String> collect1 = trendsTableParamService.lambdaQuery().
                                        eq(TrendsTableParam::getPId, byId.getPId()).
                                        eq(TrendsTableParam::getUseStation,byId.getUseStation()).
                                        eq(TrendsTableParam::getUseType,byId.getUseType()).
                                        list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());
                                List<PaymentWaterFees> paymentWaterFees = paymentWaterFeesService.lambdaQuery().eq(PaymentWaterFees::getDel,0).
                                        eq(PaymentWaterFees::getYear, waterFeeStatisticsDetails.get(0).getYear()).
                                        in(PaymentWaterFees::getWaterUserId, collect1).eq(PaymentWaterFees::getType,"水资源费").list();
                                if(null != paymentWaterFees && paymentWaterFees.size()>0){
                                    for(PaymentWaterFees fees:paymentWaterFees){
                                        paidWaterResourceCount+=fees.getPaymentAmount();
                                    }
                                }
                                if(null != collect1 && collect1.size()>0){
                                    List<String> collect2 = trendsTableParamService.lambdaQuery().in(TrendsTableParam::getPId, collect1).list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());
                                    if(null != collect2 && collect2.size()>0){
                                        List<PaymentWaterFees> paymentWaterFees2 = paymentWaterFeesService.lambdaQuery().eq(PaymentWaterFees::getDel,0).
                                                eq(PaymentWaterFees::getYear, waterFeeStatisticsDetails.get(0).getYear()).
                                                in(PaymentWaterFees::getWaterUserId, collect2).eq(PaymentWaterFees::getType,"水资源费").list();
                                        if(null != paymentWaterFees2 && paymentWaterFees2.size()>0){
                                            for(PaymentWaterFees fees:paymentWaterFees2){
                                                paidWaterResourceCount+=fees.getPaymentAmount();
                                            }
                                        }
                                    }
                                    if(null != collect2 && collect2.size()>0){
                                        List<String> collect3 = trendsTableParamService.lambdaQuery().in(TrendsTableParam::getPId, collect2).list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());
                                        if(null != collect3 && collect3.size()>0){
                                            List<PaymentWaterFees> paymentWaterFees3= paymentWaterFeesService.lambdaQuery().eq(PaymentWaterFees::getDel,0).
                                                    eq(PaymentWaterFees::getYear, waterFeeStatisticsDetails.get(0).getYear()).
                                                    in(PaymentWaterFees::getWaterUserId, collect3).eq(PaymentWaterFees::getType,"水资源费").list();
                                            if(null != paymentWaterFees3 && paymentWaterFees3.size()>0){
                                                for(PaymentWaterFees fees:paymentWaterFees3){
                                                    paidWaterResourceCount+=fees.getPaymentAmount();
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            if(!byId.getPId().equals("0")){
                                List<String> collect1 = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, byId.getPId()).list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());
                                List<String> collect1Son = trendsTableParamService.lambdaQuery().in(TrendsTableParam::getPId, collect1).list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());
                                List<PaymentWaterFees> paymentWaterFees = paymentWaterFeesService.lambdaQuery().eq(PaymentWaterFees::getDel,0).
                                        eq(PaymentWaterFees::getYear, waterFeeStatisticsDetails.get(0).getYear()).
                                        in(PaymentWaterFees::getWaterUserId, collect1).eq(PaymentWaterFees::getType,"水资源费").list();
                                if(null!= collect1Son && collect1Son.size()>0){
                                    List<PaymentWaterFees> paymentWaterFeesSon = paymentWaterFeesService.lambdaQuery().eq(PaymentWaterFees::getDel,0).
                                            eq(PaymentWaterFees::getYear, waterFeeStatisticsDetails.get(0).getYear()).
                                            in(PaymentWaterFees::getWaterUserId, collect1Son).eq(PaymentWaterFees::getType,"水资源费").list();
                                    paymentWaterFees.addAll(paymentWaterFeesSon);
                                }
                                if(null != paymentWaterFees && paymentWaterFees.size()>0){
                                    for(PaymentWaterFees fees:paymentWaterFees){
                                        paidWaterResourceCount+=fees.getPaymentAmount();
                                    }
                                }else {
                                    List<PaymentWaterFees> paymentWaterFees2 = paymentWaterFeesService.lambdaQuery().eq(PaymentWaterFees::getDel,0).
                                            eq(PaymentWaterFees::getYear, waterFeeStatisticsDetails.get(0).getYear()).
                                            in(PaymentWaterFees::getWaterUserId, byId.getPId()).eq(PaymentWaterFees::getType,"水资源费").list();
                                    if(null != paymentWaterFees2 && paymentWaterFees2.size()>0){
                                        for(PaymentWaterFees fees:paymentWaterFees2){
                                            paidWaterResourceCount+=fees.getPaymentAmount();
                                        }
                                    }
                                }
                            }
                            for(WaterFeeStatisticsTotal total:waterFeeStatisticsTotalList){
                                if(id.equals(total.getTableHeadId())){
                                    total.setPaidWaterResource(paidWaterResourceCount);
                                    paidWaterResourceCount = 0.0;
                                }
                            }
                        }
                    }
                }

                //预交水费(单位)
                Map<String,Double> tempMap = new HashMap<>();
                for(WaterFeeStatisticsTotal total :waterFeeStatisticsTotalList){
                    Double advancePaymentWaterFee1 = 0.0;
                    Double advancePaymentWaterFee2 = 0.0;
                    List<PaymentWaterFees> list1 = paymentWaterFeesService.lambdaQuery().eq(PaymentWaterFees::getDel,0).
                            eq(PaymentWaterFees::getYear, waterFeeStatisticsDetails.get(0).getYear()).
                            eq(PaymentWaterFees::getWaterUserId, total.getTableHeadId()).eq(PaymentWaterFees::getType,"水费").list();
                    if(null != list1 && list1.size()>0){
                        for(PaymentWaterFees fees:list1){
                            advancePaymentWaterFee1+=fees.getPaymentAmount();
                        }
                        tempMap.put(total.getTableHeadId()+total.getStation()+total.getYear()+total.getMonth()+total.getTenDays(),advancePaymentWaterFee1);
                    }else {
                        TrendsTableParam byId1 = trendsTableParamService.getById(total.getTableHeadId());
                        if(!byId1.getPId().equals("0")){
                            TrendsTableParam one = trendsTableParamService.getById(byId1.getPId());
                            List<PaymentWaterFees> list2 = paymentWaterFeesService.lambdaQuery().eq(PaymentWaterFees::getDel,0).
                                    eq(PaymentWaterFees::getYear, waterFeeStatisticsDetails.get(0).getYear()).
                                    eq(PaymentWaterFees::getWaterUserId, one.getId()).eq(PaymentWaterFees::getType,"水费").list();
                            if(null!=list2 && list2.size()>0){
                                for(PaymentWaterFees fees:list2){
                                    advancePaymentWaterFee2+=fees.getPaymentAmount();
                                }
                                TrendsTableParam totalBean = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, one.getId()).eq(TrendsTableParam::getParamName, "合计").one();
                                if(totalBean!=null){
                                    tempMap.put(totalBean.getId()+total.getStation()+total.getYear()+total.getMonth()+total.getTenDays(),advancePaymentWaterFee2);
                                }
                            }else {
                                tempMap.put(total.getTableHeadId()+total.getStation()+total.getYear()+total.getMonth()+total.getTenDays(),0.0);
                            }
                        }else {
                            tempMap.put(total.getTableHeadId()+total.getStation()+total.getYear()+total.getMonth()+total.getTenDays(),0.0);
                        }
                    }
                }
                for(WaterFeeStatisticsTotal total :waterFeeStatisticsTotalList){
                    //预交水费
                    total.setAdvancePaymentWaterFee(tempMap.get(total.getTableHeadId()+total.getStation()+total.getYear()+total.getMonth()+total.getTenDays())==null?0.0:tempMap.get(total.getTableHeadId()+total.getStation()+total.getYear()+total.getMonth()+total.getTenDays()));
                    //水费余(欠)
                    total.setUnpaidWaterFees(total.getAdvancePaymentWaterFee()-total.getPayableWaterFee());
                }
                //预交水费(合计)
                List<TotalIdToStation> listTotalIdToStation = totalIdToStationService.lambdaQuery().eq(TotalIdToStation::getUseType, 2).eq(TotalIdToStation::getStation, waterFeeStatisticsDetails.get(0).getStation()).list();
                if(null != listTotalIdToStation && listTotalIdToStation.size()>0){
                    List<String> totalColl = listTotalIdToStation.stream().filter(t->t.getName().equals("合计")).map(TotalIdToStation::getTotalId).collect(Collectors.toList());
                    for(String id:totalColl){
                        TrendsTableParam byId = trendsTableParamService.getById(id);
                        if(!byId.getPId().equals("0")){
                            List<TrendsTableParam> tableParams = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, byId.getPId()).ne(TrendsTableParam::getParamName, "合计").list();
                            for(TrendsTableParam param:tableParams){
                                List<TrendsTableParam> listed = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, param.getId()).ne(TrendsTableParam::getParamName, "合计").list();
                                if(listed.size()>0){
                                    for(TrendsTableParam param1:listed){
                                        for(WaterFeeStatisticsTotal total:waterFeeStatisticsTotalList){
                                            if(param1.getId().equals(total.getTableHeadId())){
                                                payableWaterFeeCount+=total.getPayableWaterFee();
                                                unpaidWaterFeesCount+=total.getUnpaidWaterFees();
                                            }
                                        }
                                    }
                                }else {
                                    for(WaterFeeStatisticsTotal total:waterFeeStatisticsTotalList){
                                        if(param.getId().equals(total.getTableHeadId())){
                                            payableWaterFeeCount+=total.getPayableWaterFee();
                                            unpaidWaterFeesCount+=total.getUnpaidWaterFees();
                                        }
                                    }
                                }
                            }
                            for(WaterFeeStatisticsTotal total:waterFeeStatisticsTotalList){
                                if(total.getTableHeadId().equals(id)){
                                    total.setPayableWaterFee(payableWaterFeeCount);
                                    total.setUnpaidWaterFees(unpaidWaterFeesCount);
                                    payableWaterFeeCount = 0.0;
                                    unpaidWaterFeesCount = 0.0;
                                }
                            }
                        }
                    }
                }
                if(null != listTotalIdToStation && listTotalIdToStation.size()>0){
                    Double advancePaymentWaterFeeCount = 0.0;
                    List<String> totalColl = listTotalIdToStation.stream().filter(t->t.getName().equals("合计")).map(TotalIdToStation::getTotalId).collect(Collectors.toList());
                    for(String id:totalColl){
                        TrendsTableParam byId = trendsTableParamService.getById(id);
                        if(byId.getPId().equals("0")){
                            List<String> collect1 = trendsTableParamService.lambdaQuery().
                                    eq(TrendsTableParam::getPId, byId.getPId()).
                                    eq(TrendsTableParam::getUseStation,byId.getUseStation()).
                                    eq(TrendsTableParam::getUseType,byId.getUseType()).
                                    list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());
                            List<PaymentWaterFees> paymentWaterFees = paymentWaterFeesService.lambdaQuery().eq(PaymentWaterFees::getDel,0).
                                    eq(PaymentWaterFees::getYear, waterFeeStatisticsDetails.get(0).getYear()).
                                    in(PaymentWaterFees::getWaterUserId, collect1).eq(PaymentWaterFees::getType,"水费").list();
                            if(null != paymentWaterFees && paymentWaterFees.size()>0){
                                for(PaymentWaterFees fees:paymentWaterFees){
                                    advancePaymentWaterFeeCount+=fees.getPaymentAmount();
                                }
                            }
                            if(null != collect1 && collect1.size()>0){
                                List<String> collect2 = trendsTableParamService.lambdaQuery().in(TrendsTableParam::getPId, collect1).list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());
                                if(null != collect2 && collect2.size()>0){
                                    List<PaymentWaterFees> paymentWaterFees2 = paymentWaterFeesService.lambdaQuery().eq(PaymentWaterFees::getDel,0).
                                            eq(PaymentWaterFees::getYear, waterFeeStatisticsDetails.get(0).getYear()).
                                            in(PaymentWaterFees::getWaterUserId, collect2).eq(PaymentWaterFees::getType,"水费").list();
                                    if(null != paymentWaterFees2 && paymentWaterFees2.size()>0){
                                        for(PaymentWaterFees fees:paymentWaterFees2){
                                            advancePaymentWaterFeeCount+=fees.getPaymentAmount();
                                        }
                                    }
                                }
                                if(null != collect2 && collect2.size()>0){
                                    List<String> collect3 = trendsTableParamService.lambdaQuery().in(TrendsTableParam::getPId, collect2).list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());
                                    if(null != collect3 && collect3.size()>0){
                                        List<PaymentWaterFees> paymentWaterFees3= paymentWaterFeesService.lambdaQuery().eq(PaymentWaterFees::getDel,0).
                                                eq(PaymentWaterFees::getYear, waterFeeStatisticsDetails.get(0).getYear()).
                                                in(PaymentWaterFees::getWaterUserId, collect3).eq(PaymentWaterFees::getType,"水费").list();
                                        if(null != paymentWaterFees3 && paymentWaterFees3.size()>0){
                                            for(PaymentWaterFees fees:paymentWaterFees3){
                                                advancePaymentWaterFeeCount+=fees.getPaymentAmount();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if(!byId.getPId().equals("0")){
                            List<String> collect1 = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, byId.getPId()).list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());
                            List<String> collect1Son = trendsTableParamService.lambdaQuery().in(TrendsTableParam::getPId, collect1).list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());
                            List<PaymentWaterFees> paymentWaterFees = paymentWaterFeesService.lambdaQuery().eq(PaymentWaterFees::getDel,0).
                                    eq(PaymentWaterFees::getYear, waterFeeStatisticsDetails.get(0).getYear()).
                                    in(PaymentWaterFees::getWaterUserId, collect1).eq(PaymentWaterFees::getType,"水费").list();
                            if(null!= collect1Son && collect1Son.size()>0){
                                List<PaymentWaterFees> paymentWaterFeesSon = paymentWaterFeesService.lambdaQuery().eq(PaymentWaterFees::getDel,0).
                                        eq(PaymentWaterFees::getYear, waterFeeStatisticsDetails.get(0).getYear()).
                                        in(PaymentWaterFees::getWaterUserId, collect1Son).eq(PaymentWaterFees::getType,"水费").list();
                                paymentWaterFees.addAll(paymentWaterFeesSon);
                            }
                            if(null != paymentWaterFees && paymentWaterFees.size()>0){
                                for(PaymentWaterFees fees:paymentWaterFees){
                                    advancePaymentWaterFeeCount+=fees.getPaymentAmount();
                                }
                            }else {
                                List<PaymentWaterFees> paymentWaterFees2 = paymentWaterFeesService.lambdaQuery().eq(PaymentWaterFees::getDel,0).
                                        eq(PaymentWaterFees::getYear, waterFeeStatisticsDetails.get(0).getYear()).
                                        in(PaymentWaterFees::getWaterUserId, byId.getPId()).eq(PaymentWaterFees::getType,"水费").list();
                                if(null != paymentWaterFees2 && paymentWaterFees2.size()>0){
                                    for(PaymentWaterFees fees:paymentWaterFees2){
                                        advancePaymentWaterFeeCount+=fees.getPaymentAmount();
                                    }
                                }
                            }
                        }
                        for(WaterFeeStatisticsTotal total:waterFeeStatisticsTotalList){
                            if(id.equals(total.getTableHeadId())){
                                total.setAdvancePaymentWaterFee(advancePaymentWaterFeeCount);
                                advancePaymentWaterFeeCount = 0.0;
                            }
                        }
                    }
                }
                for(WaterFeeStatisticsTotal total:waterFeeStatisticsTotalList){
                    if(total.getWaterResourceSurplus()!=null){
                        total.setWaterResourceTotal(total.getUnpaidWaterFees()+total.getWaterResourceSurplus());
                    }
                }
                //总合计
                if(null != listTotalIdToStation && listTotalIdToStation.size()>0){
                    Double payableWaterFeeAllCount = 0.0;
                    Double unpaidWaterFeesAllCount = 0.0;
                    List<String> totalColl = listTotalIdToStation.stream().filter(t->t.getName().equals("合计")).map(TotalIdToStation::getTotalId).collect(Collectors.toList());
                    for(String id:totalColl){
                        TrendsTableParam byId = trendsTableParamService.getById(id);
                        if(byId.getPId().equals("0")){
                            for(WaterFeeStatisticsTotal total:waterFeeStatisticsTotalList){
                                if(!totalColl.contains(total.getTableHeadId())){
                                    payableWaterFeeAllCount += total.getPayableWaterFee();
                                    unpaidWaterFeesAllCount += total.getUnpaidWaterFees();
                                }
                            }
                            for(WaterFeeStatisticsTotal total:waterFeeStatisticsTotalList){
                                if(id.equals(total.getTableHeadId())){
                                    total.setPayableWaterFee(payableWaterFeeAllCount);
                                    total.setUnpaidWaterFees(unpaidWaterFeesAllCount);
                                }
                            }
                        }
                    }
                }
                boolean b1 = waterFeeStatisticsTotalService.updateBatchById(waterFeeStatisticsTotalList);
                if (b1) {
                    RestResponse add = tenDaysWaterBalanceService.add(waterFeeStatisticsTotalList);
                    if(add.getCode()==200){
                        Integer day = Integer.valueOf(waterFeeStatisticsDetails.get(0).getStatisticsDate().substring(3, 5));
                        log.error("----------------------------------day--------------------------------"+day);
                        RestResponse add1 = dayWaterBalanceService.addFirst(waterFeeStatisticsDetails, day);
                        if(add1.getCode()==200){
                            return RestResponse.ok("添加成功");
                        }else {
                            return RestResponse.no("添加失败");
                        }
                    }else {
                        return RestResponse.no("添加失败");
                    }
                }else {
                    return RestResponse.no("添加失败");
                }
            }else {
                //添加第一行
                Map<String, List<WaterFeeStatisticsDetails>> collect = waterFeeStatisticsDetails.stream().collect(Collectors.groupingBy(WaterFeeStatisticsDetails::getTableHeadId));
                Set<String> strings = collect.keySet();
                List<WaterFeeStatisticsTotal> waterFeeStatisticsTotalList = new ArrayList<>();
                for(String string : strings){
                    WaterFeeStatisticsTotal waterFeeStatisticsTotal = new WaterFeeStatisticsTotal();
                    WaterFeeStatisticsDetails temp = waterFeeStatisticsDetails.get(0);
                    Double amountTo = collect.get(string).stream().filter(t->t.getV()!=null).map(WaterFeeStatisticsDetails::getV).reduce(Double::sum).orElse(0.00);
                    waterFeeStatisticsTotal.setStation(temp.getStation());
                    waterFeeStatisticsTotal.setYear(temp.getYear());
                    waterFeeStatisticsTotal.setMonth(temp.getMonth());
                    waterFeeStatisticsTotal.setTenDays(temp.getTenDays());
                    waterFeeStatisticsTotal.setAmountTo(amountTo);
                    waterFeeStatisticsTotal.setTableHeadId(string);
                    waterFeeStatisticsTotal.setId(UUIDUtils.getUUID());
                    waterFeeStatisticsTotal.setCurrentWaterVolume(amountTo*60*60*24);
                    Map<String, Object> jisuan = jisuan(temp.getYear(), temp.getMonth(), temp.getTenDays());
                    if((Integer)jisuan.get("year")== waterFeeStatisticsDetails.get(0).getYear()){
                        WaterFeeStatisticsTotal one = waterFeeStatisticsTotalService.lambdaQuery().eq(WaterFeeStatisticsTotal::getYear, jisuan.get("year")).
                                eq(WaterFeeStatisticsTotal::getMonth, jisuan.get("month")).
                                eq(WaterFeeStatisticsTotal::getTenDays, jisuan.get("tenDays")).
                                eq(WaterFeeStatisticsTotal::getTableHeadId, string).
                                eq(WaterFeeStatisticsTotal::getStation, temp.getStation()).
                                one();
                        //上旬水量
                        waterFeeStatisticsTotal.setWaterVolumeFirstTenDays(one==null?0.0:one.getAccumulatedWaterVolume());
                    }else {
                        //上旬水量
                        waterFeeStatisticsTotal.setWaterVolumeFirstTenDays(0.0);
                    }
                    //累计水量
                    waterFeeStatisticsTotal.setAccumulatedWaterVolume(waterFeeStatisticsTotal.getCurrentWaterVolume()+waterFeeStatisticsTotal.getWaterVolumeFirstTenDays());
                    WaterPriceManagement byId = waterPriceManagementService.getById(string);
                    Double payableWaterFee = byId==null?0:byId.getWaterPrice()==null?0:byId.getWaterPrice()*waterFeeStatisticsTotal.getAccumulatedWaterVolume();
                    //应交水费
                    waterFeeStatisticsTotal.setPayableWaterFee(payableWaterFee);
                    if(tempObj.getStation().equals("河东管理站绿化") || tempObj.getStation().equals("渠首砂厂")){
                        waterFeeStatisticsTotal.setPayableWaterResource(Double.valueOf(waterResourcePrice)*waterFeeStatisticsTotal.getAccumulatedWaterVolume());
                    }
                    WaterFeeStatisticsTotal lastYear = waterFeeStatisticsTotalService.lambdaQuery().eq(WaterFeeStatisticsTotal::getYear, temp.getYear()-1).
                            eq(WaterFeeStatisticsTotal::getMonth,  temp.getMonth()).
                            eq(WaterFeeStatisticsTotal::getTenDays, temp.getTenDays()).
                            eq(WaterFeeStatisticsTotal::getTableHeadId, string).
                            eq(WaterFeeStatisticsTotal::getStation, temp.getStation()).
                            one();
                    waterFeeStatisticsTotal.setWaterVolumeDuringLastYear(lastYear==null?0:lastYear.getAccumulatedWaterVolume());
                    waterFeeStatisticsTotalList.add(waterFeeStatisticsTotal);
                }
                if(tempObj.getStation().equals("河东管理站绿化") || tempObj.getStation().equals("渠首砂厂")){
                    //预交水资源费(单位)
                    Map<String,Double> tempMap = new HashMap<>();
                    for(WaterFeeStatisticsTotal total :waterFeeStatisticsTotalList){
                        Double paidWaterResource1 = 0.0;
                        Double paidWaterResource2 = 0.0;
                        List<PaymentWaterFees> list1 = paymentWaterFeesService.lambdaQuery().eq(PaymentWaterFees::getDel,0).
                                eq(PaymentWaterFees::getYear, waterFeeStatisticsDetails.get(0).getYear()).
                                eq(PaymentWaterFees::getWaterUserId, total.getTableHeadId()).eq(PaymentWaterFees::getType,"水资源费").list();
                        if(null != list1 && list1.size()>0){
                            for(PaymentWaterFees fees:list1){
                                paidWaterResource1+=fees.getPaymentAmount();
                            }
                            tempMap.put(total.getTableHeadId()+total.getStation()+total.getYear()+total.getMonth()+total.getTenDays(),paidWaterResource1);
                        }else {
                            TrendsTableParam byId1 = trendsTableParamService.getById(total.getTableHeadId());
                            if(!byId1.getPId().equals("0")){
                                TrendsTableParam one = trendsTableParamService.getById(byId1.getPId());
                                List<PaymentWaterFees> list2 = paymentWaterFeesService.lambdaQuery().eq(PaymentWaterFees::getDel,0).
                                        eq(PaymentWaterFees::getYear, waterFeeStatisticsDetails.get(0).getYear()).
                                        eq(PaymentWaterFees::getWaterUserId, one.getId()).eq(PaymentWaterFees::getType,"水资源费").list();
                                if(null!=list2 && list2.size()>0){
                                    for(PaymentWaterFees fees:list2){
                                        paidWaterResource2+=fees.getPaymentAmount();
                                    }
                                    TrendsTableParam totalBean = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, one.getId()).eq(TrendsTableParam::getParamName, "合计").one();
                                    if(totalBean!=null){
                                        tempMap.put(totalBean.getId()+total.getStation()+total.getYear()+total.getMonth()+total.getTenDays(),paidWaterResource2);
                                    }
                                }else {
                                    tempMap.put(total.getTableHeadId()+total.getStation()+total.getYear()+total.getMonth()+total.getTenDays(),0.0);
                                }
                            }else {
                                tempMap.put(total.getTableHeadId()+total.getStation()+total.getYear()+total.getMonth()+total.getTenDays(),0.0);
                            }
                        }
                    }
                    for(WaterFeeStatisticsTotal total :waterFeeStatisticsTotalList){
                        //预交水资源费
                        total.setPaidWaterResource(tempMap.get(total.getTableHeadId()+total.getStation()+total.getYear()+total.getMonth()+total.getTenDays())==null?0.0:tempMap.get(total.getTableHeadId()+total.getStation()+total.getYear()+total.getMonth()+total.getTenDays()));
                        //水资源费余(欠)
                        total.setWaterResourceSurplus(total.getPaidWaterResource()-total.getPayableWaterResource());
                    }
                    //预交水资源费(合计)
                    List<TotalIdToStation> listTotalIdToStation = totalIdToStationService.lambdaQuery().eq(TotalIdToStation::getUseType, 2).eq(TotalIdToStation::getStation, waterFeeStatisticsDetails.get(0).getStation()).list();
                    if(null != listTotalIdToStation && listTotalIdToStation.size()>0){
                        List<String> totalColl = listTotalIdToStation.stream().filter(t->t.getName().equals("合计")).map(TotalIdToStation::getTotalId).collect(Collectors.toList());
                        for(String id:totalColl){
                            TrendsTableParam byId = trendsTableParamService.getById(id);
                            if(!byId.getPId().equals("0")){
                                List<TrendsTableParam> tableParams = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, byId.getPId()).ne(TrendsTableParam::getParamName, "合计").list();
                                for(TrendsTableParam param:tableParams){
                                    List<TrendsTableParam> listed = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, param.getId()).ne(TrendsTableParam::getParamName, "合计").list();
                                    if(listed.size()>0){
                                        for(TrendsTableParam param1:listed){
                                            for(WaterFeeStatisticsTotal total:waterFeeStatisticsTotalList){
                                                if(param1.getId().equals(total.getTableHeadId())){
                                                    payableWaterResourceCount+=total.getPayableWaterResource();
                                                    waterResourceSurplusCount+=total.getWaterResourceSurplus();
                                                }
                                            }
                                        }
                                    }else {
                                        for(WaterFeeStatisticsTotal total:waterFeeStatisticsTotalList){
                                            if(param.getId().equals(total.getTableHeadId())){
                                                payableWaterResourceCount+=total.getPayableWaterResource();
                                                waterResourceSurplusCount+=total.getWaterResourceSurplus();
                                            }
                                        }
                                    }
                                }
                                for(WaterFeeStatisticsTotal total:waterFeeStatisticsTotalList){
                                    if(total.getTableHeadId().equals(id)){
                                        total.setPayableWaterResource(payableWaterResourceCount);
                                        total.setWaterResourceSurplus(waterResourceSurplusCount);
                                        payableWaterResourceCount = 0.0;
                                        waterResourceSurplusCount = 0.0;
                                    }
                                }
                            }
                        }
                    }
                    if(null != listTotalIdToStation && listTotalIdToStation.size()>0){
                        Double paidWaterResourceCount = 0.0;
                        List<String> totalColl = listTotalIdToStation.stream().filter(t->t.getName().equals("合计")).map(TotalIdToStation::getTotalId).collect(Collectors.toList());
                        for(String id:totalColl){
                            TrendsTableParam byId = trendsTableParamService.getById(id);
                            if(byId.getPId().equals("0")){
                                List<String> collect1 = trendsTableParamService.lambdaQuery().
                                        eq(TrendsTableParam::getPId, byId.getPId()).
                                        eq(TrendsTableParam::getUseStation,byId.getUseStation()).
                                        eq(TrendsTableParam::getUseType,byId.getUseType()).
                                        list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());
                                List<PaymentWaterFees> paymentWaterFees = paymentWaterFeesService.lambdaQuery().eq(PaymentWaterFees::getDel,0).
                                        eq(PaymentWaterFees::getYear, waterFeeStatisticsDetails.get(0).getYear()).
                                        in(PaymentWaterFees::getWaterUserId, collect1).eq(PaymentWaterFees::getType,"水资源费").list();
                                if(null != paymentWaterFees && paymentWaterFees.size()>0){
                                    for(PaymentWaterFees fees:paymentWaterFees){
                                        paidWaterResourceCount+=fees.getPaymentAmount();
                                    }
                                }
                                if(null != collect1 && collect1.size()>0){
                                    List<String> collect2 = trendsTableParamService.lambdaQuery().in(TrendsTableParam::getPId, collect1).list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());
                                    if(null != collect2 && collect2.size()>0){
                                        List<PaymentWaterFees> paymentWaterFees2 = paymentWaterFeesService.lambdaQuery().eq(PaymentWaterFees::getDel,0).
                                                eq(PaymentWaterFees::getYear, waterFeeStatisticsDetails.get(0).getYear()).
                                                in(PaymentWaterFees::getWaterUserId, collect2).eq(PaymentWaterFees::getType,"水资源费").list();
                                        if(null != paymentWaterFees2 && paymentWaterFees2.size()>0){
                                            for(PaymentWaterFees fees:paymentWaterFees2){
                                                paidWaterResourceCount+=fees.getPaymentAmount();
                                            }
                                        }
                                    }
                                    if(null != collect2 && collect2.size()>0){
                                        List<String> collect3 = trendsTableParamService.lambdaQuery().in(TrendsTableParam::getPId, collect2).list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());
                                        if(null != collect3 && collect3.size()>0){
                                            List<PaymentWaterFees> paymentWaterFees3= paymentWaterFeesService.lambdaQuery().eq(PaymentWaterFees::getDel,0).
                                                    eq(PaymentWaterFees::getYear, waterFeeStatisticsDetails.get(0).getYear()).
                                                    in(PaymentWaterFees::getWaterUserId, collect3).eq(PaymentWaterFees::getType,"水资源费").list();
                                            if(null != paymentWaterFees3 && paymentWaterFees3.size()>0){
                                                for(PaymentWaterFees fees:paymentWaterFees3){
                                                    paidWaterResourceCount+=fees.getPaymentAmount();
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            if(!byId.getPId().equals("0")){
                                List<String> collect1 = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, byId.getPId()).list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());
                                List<String> collect1Son = trendsTableParamService.lambdaQuery().in(TrendsTableParam::getPId, collect1).list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());
                                List<PaymentWaterFees> paymentWaterFees = paymentWaterFeesService.lambdaQuery().eq(PaymentWaterFees::getDel,0).
                                        eq(PaymentWaterFees::getYear, waterFeeStatisticsDetails.get(0).getYear()).
                                        in(PaymentWaterFees::getWaterUserId, collect1).eq(PaymentWaterFees::getType,"水资源费").list();
                                if(null!= collect1Son && collect1Son.size()>0){
                                    List<PaymentWaterFees> paymentWaterFeesSon = paymentWaterFeesService.lambdaQuery().eq(PaymentWaterFees::getDel,0).
                                            eq(PaymentWaterFees::getYear, waterFeeStatisticsDetails.get(0).getYear()).
                                            in(PaymentWaterFees::getWaterUserId, collect1Son).eq(PaymentWaterFees::getType,"水资源费").list();
                                    paymentWaterFees.addAll(paymentWaterFeesSon);
                                }
                                if(null != paymentWaterFees && paymentWaterFees.size()>0){
                                    for(PaymentWaterFees fees:paymentWaterFees){
                                        paidWaterResourceCount+=fees.getPaymentAmount();
                                    }
                                }else {
                                    List<PaymentWaterFees> paymentWaterFees2 = paymentWaterFeesService.lambdaQuery().eq(PaymentWaterFees::getDel,0).
                                            eq(PaymentWaterFees::getYear, waterFeeStatisticsDetails.get(0).getYear()).
                                            in(PaymentWaterFees::getWaterUserId, byId.getPId()).eq(PaymentWaterFees::getType,"水资源费").list();
                                    if(null != paymentWaterFees2 && paymentWaterFees2.size()>0){
                                        for(PaymentWaterFees fees:paymentWaterFees2){
                                            paidWaterResourceCount+=fees.getPaymentAmount();
                                        }
                                    }
                                }
                            }
                            for(WaterFeeStatisticsTotal total:waterFeeStatisticsTotalList){
                                if(id.equals(total.getTableHeadId())){
                                    total.setPaidWaterResource(paidWaterResourceCount);
                                    paidWaterResourceCount = 0.0;
                                }
                            }
                        }
                    }
                }

                //预交水费(单位)
                Map<String,Double> tempMap = new HashMap<>();
                for(WaterFeeStatisticsTotal total :waterFeeStatisticsTotalList){
                    Double advancePaymentWaterFee1 = 0.0;
                    Double advancePaymentWaterFee2 = 0.0;
                    List<PaymentWaterFees> list1 = paymentWaterFeesService.lambdaQuery().eq(PaymentWaterFees::getDel,0).
                            eq(PaymentWaterFees::getYear, waterFeeStatisticsDetails.get(0).getYear()).
                            eq(PaymentWaterFees::getWaterUserId, total.getTableHeadId()).eq(PaymentWaterFees::getType,"水费").list();
                    if(null != list1 && list1.size()>0){
                        for(PaymentWaterFees fees:list1){
                            advancePaymentWaterFee1+=fees.getPaymentAmount();
                        }
                        tempMap.put(total.getTableHeadId()+total.getStation()+total.getYear()+total.getMonth()+total.getTenDays(),advancePaymentWaterFee1);
                    }else {
                        TrendsTableParam byId1 = trendsTableParamService.getById(total.getTableHeadId());
                        if(!byId1.getPId().equals("0")){
                            TrendsTableParam one = trendsTableParamService.getById(byId1.getPId());
                            List<PaymentWaterFees> list2 = paymentWaterFeesService.lambdaQuery().eq(PaymentWaterFees::getDel,0).
                                    eq(PaymentWaterFees::getYear, waterFeeStatisticsDetails.get(0).getYear()).
                                    eq(PaymentWaterFees::getWaterUserId, one.getId()).eq(PaymentWaterFees::getType,"水费").list();
                            if(null!=list2 && list2.size()>0){
                                for(PaymentWaterFees fees:list2){
                                    advancePaymentWaterFee2+=fees.getPaymentAmount();
                                }
                                TrendsTableParam totalBean = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, one.getId()).eq(TrendsTableParam::getParamName, "合计").one();
                                if(totalBean!=null){
                                    tempMap.put(totalBean.getId()+total.getStation()+total.getYear()+total.getMonth()+total.getTenDays(),advancePaymentWaterFee2);
                                }
                            }else {
                                tempMap.put(total.getTableHeadId()+total.getStation()+total.getYear()+total.getMonth()+total.getTenDays(),0.0);
                            }
                        }else {
                            tempMap.put(total.getTableHeadId()+total.getStation()+total.getYear()+total.getMonth()+total.getTenDays(),0.0);
                        }
                    }
                }
                for(WaterFeeStatisticsTotal total :waterFeeStatisticsTotalList){
                    //预交水费
                    total.setAdvancePaymentWaterFee(tempMap.get(total.getTableHeadId()+total.getStation()+total.getYear()+total.getMonth()+total.getTenDays())==null?0.0:tempMap.get(total.getTableHeadId()+total.getStation()+total.getYear()+total.getMonth()+total.getTenDays()));
                    //水费余(欠)
                    total.setUnpaidWaterFees(total.getAdvancePaymentWaterFee()-total.getPayableWaterFee());
                }
                //预交水费(合计)
                List<TotalIdToStation> listTotalIdToStation = totalIdToStationService.lambdaQuery().eq(TotalIdToStation::getUseType, 2).eq(TotalIdToStation::getStation, waterFeeStatisticsDetails.get(0).getStation()).list();
                if(null != listTotalIdToStation && listTotalIdToStation.size()>0){
                    List<String> totalColl = listTotalIdToStation.stream().filter(t->t.getName().equals("合计")).map(TotalIdToStation::getTotalId).collect(Collectors.toList());
                    for(String id:totalColl){
                        TrendsTableParam byId = trendsTableParamService.getById(id);
                        if(!byId.getPId().equals("0")){
                            List<TrendsTableParam> tableParams = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, byId.getPId()).ne(TrendsTableParam::getParamName, "合计").list();
                            for(TrendsTableParam param:tableParams){
                                List<TrendsTableParam> listed = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, param.getId()).ne(TrendsTableParam::getParamName, "合计").list();
                                if(listed.size()>0){
                                    for(TrendsTableParam param1:listed){
                                        for(WaterFeeStatisticsTotal total:waterFeeStatisticsTotalList){
                                            if(param1.getId().equals(total.getTableHeadId())){
                                                payableWaterFeeCount+=total.getPayableWaterFee();
                                                unpaidWaterFeesCount+=total.getUnpaidWaterFees();
                                            }
                                        }
                                    }
                                }else {
                                    for(WaterFeeStatisticsTotal total:waterFeeStatisticsTotalList){
                                        if(param.getId().equals(total.getTableHeadId())){
                                            payableWaterFeeCount+=total.getPayableWaterFee();
                                            unpaidWaterFeesCount+=total.getUnpaidWaterFees();
                                        }
                                    }
                                }
                            }
                            for(WaterFeeStatisticsTotal total:waterFeeStatisticsTotalList){
                                if(total.getTableHeadId().equals(id)){
                                    total.setPayableWaterFee(payableWaterFeeCount);
                                    total.setUnpaidWaterFees(unpaidWaterFeesCount);
                                    payableWaterFeeCount = 0.0;
                                    unpaidWaterFeesCount = 0.0;
                                }
                            }
                        }
                    }
                }
                if(null != listTotalIdToStation && listTotalIdToStation.size()>0){
                    Double advancePaymentWaterFeeCount = 0.0;
                    List<String> totalColl = listTotalIdToStation.stream().filter(t->t.getName().equals("合计")).map(TotalIdToStation::getTotalId).collect(Collectors.toList());
                    for(String id:totalColl){
                        TrendsTableParam byId = trendsTableParamService.getById(id);
                        if(byId.getPId().equals("0")){
                            List<String> collect1 = trendsTableParamService.lambdaQuery().
                                    eq(TrendsTableParam::getPId, byId.getPId()).
                                    eq(TrendsTableParam::getUseStation,byId.getUseStation()).
                                    eq(TrendsTableParam::getUseType,byId.getUseType()).
                                    list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());
                            List<PaymentWaterFees> paymentWaterFees = paymentWaterFeesService.lambdaQuery().eq(PaymentWaterFees::getDel,0).
                                    eq(PaymentWaterFees::getYear, waterFeeStatisticsDetails.get(0).getYear()).
                                    in(PaymentWaterFees::getWaterUserId, collect1).eq(PaymentWaterFees::getType,"水费").list();
                            if(null != paymentWaterFees && paymentWaterFees.size()>0){
                                for(PaymentWaterFees fees:paymentWaterFees){
                                    advancePaymentWaterFeeCount+=fees.getPaymentAmount();
                                }
                            }
                            if(null != collect1 && collect1.size()>0){
                                List<String> collect2 = trendsTableParamService.lambdaQuery().in(TrendsTableParam::getPId, collect1).list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());
                                if(null != collect2 && collect2.size()>0){
                                    List<PaymentWaterFees> paymentWaterFees2 = paymentWaterFeesService.lambdaQuery().eq(PaymentWaterFees::getDel,0).
                                            eq(PaymentWaterFees::getYear, waterFeeStatisticsDetails.get(0).getYear()).
                                            in(PaymentWaterFees::getWaterUserId, collect2).eq(PaymentWaterFees::getType,"水费").list();
                                    if(null != paymentWaterFees2 && paymentWaterFees2.size()>0){
                                        for(PaymentWaterFees fees:paymentWaterFees2){
                                            advancePaymentWaterFeeCount+=fees.getPaymentAmount();
                                        }
                                    }
                                }
                                if(null != collect2 && collect2.size()>0){
                                    List<String> collect3 = trendsTableParamService.lambdaQuery().in(TrendsTableParam::getPId, collect2).list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());
                                    if(null != collect3 && collect3.size()>0){
                                        List<PaymentWaterFees> paymentWaterFees3= paymentWaterFeesService.lambdaQuery().eq(PaymentWaterFees::getDel,0).
                                                eq(PaymentWaterFees::getYear, waterFeeStatisticsDetails.get(0).getYear()).
                                                in(PaymentWaterFees::getWaterUserId, collect3).eq(PaymentWaterFees::getType,"水费").list();
                                        if(null != paymentWaterFees3 && paymentWaterFees3.size()>0){
                                            for(PaymentWaterFees fees:paymentWaterFees3){
                                                advancePaymentWaterFeeCount+=fees.getPaymentAmount();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if(!byId.getPId().equals("0")){
                            List<String> collect1 = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, byId.getPId()).list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());
                            List<String> collect1Son = trendsTableParamService.lambdaQuery().in(TrendsTableParam::getPId, collect1).list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());
                            List<PaymentWaterFees> paymentWaterFees = paymentWaterFeesService.lambdaQuery().eq(PaymentWaterFees::getDel,0).
                                    eq(PaymentWaterFees::getYear, waterFeeStatisticsDetails.get(0).getYear()).
                                    in(PaymentWaterFees::getWaterUserId, collect1).eq(PaymentWaterFees::getType,"水费").list();
                            if(null!= collect1Son && collect1Son.size()>0){
                                List<PaymentWaterFees> paymentWaterFeesSon = paymentWaterFeesService.lambdaQuery().eq(PaymentWaterFees::getDel,0).
                                        eq(PaymentWaterFees::getYear, waterFeeStatisticsDetails.get(0).getYear()).
                                        in(PaymentWaterFees::getWaterUserId, collect1Son).eq(PaymentWaterFees::getType,"水费").list();
                                paymentWaterFees.addAll(paymentWaterFeesSon);
                            }
                            if(null != paymentWaterFees && paymentWaterFees.size()>0){
                                for(PaymentWaterFees fees:paymentWaterFees){
                                    advancePaymentWaterFeeCount+=fees.getPaymentAmount();
                                }
                            }else {
                                List<PaymentWaterFees> paymentWaterFees2 = paymentWaterFeesService.lambdaQuery().eq(PaymentWaterFees::getDel,0).
                                        eq(PaymentWaterFees::getYear, waterFeeStatisticsDetails.get(0).getYear()).
                                        in(PaymentWaterFees::getWaterUserId, byId.getPId()).eq(PaymentWaterFees::getType,"水费").list();
                                if(null != paymentWaterFees2 && paymentWaterFees2.size()>0){
                                    for(PaymentWaterFees fees:paymentWaterFees2){
                                        advancePaymentWaterFeeCount+=fees.getPaymentAmount();
                                    }
                                }
                            }
                        }
                        for(WaterFeeStatisticsTotal total:waterFeeStatisticsTotalList){
                            if(id.equals(total.getTableHeadId())){
                                total.setAdvancePaymentWaterFee(advancePaymentWaterFeeCount);
                                advancePaymentWaterFeeCount = 0.0;
                            }
                        }
                    }
                }
                for(WaterFeeStatisticsTotal total:waterFeeStatisticsTotalList){
                    if(total.getWaterResourceSurplus()!=null){
                        total.setWaterResourceTotal(total.getUnpaidWaterFees()+total.getWaterResourceSurplus());
                    }
                }
                //总合计
                if(null != listTotalIdToStation && listTotalIdToStation.size()>0){
                    Double payableWaterFeeAllCount = 0.0;
                    Double unpaidWaterFeesAllCount = 0.0;
                    List<String> totalColl = listTotalIdToStation.stream().filter(t->t.getName().equals("合计")).map(TotalIdToStation::getTotalId).collect(Collectors.toList());
                    for(String id:totalColl){
                        TrendsTableParam byId = trendsTableParamService.getById(id);
                        if(byId.getPId().equals("0")){
                            for(WaterFeeStatisticsTotal total:waterFeeStatisticsTotalList){
                                if(!totalColl.contains(total.getTableHeadId())){
                                    payableWaterFeeAllCount += total.getPayableWaterFee();
                                    unpaidWaterFeesAllCount += total.getUnpaidWaterFees();
                                }
                            }
                            for(WaterFeeStatisticsTotal total:waterFeeStatisticsTotalList){
                                if(id.equals(total.getTableHeadId())){
                                    total.setPayableWaterFee(payableWaterFeeAllCount);
                                    total.setUnpaidWaterFees(unpaidWaterFeesAllCount);
                                }
                            }
                        }
                    }
                }
                boolean b1 = waterFeeStatisticsTotalService.saveBatch(waterFeeStatisticsTotalList);
                if (b1) {
                    RestResponse add = tenDaysWaterBalanceService.add(waterFeeStatisticsTotalList);
                    if(add.getCode()==200){
                        Integer day = Integer.valueOf(waterFeeStatisticsDetails.get(0).getStatisticsDate().substring(3, 5));
                        log.error("----------------------------------day--------------------------------"+day);
                        RestResponse add1 = dayWaterBalanceService.addFirst(waterFeeStatisticsDetails, day);
                        if(add1.getCode()==200){
                            return RestResponse.ok("添加成功");
                        }else {
                            return RestResponse.no("添加失败");
                        }
                    }else {
                        return RestResponse.no("添加失败");
                    }
                }else {
                    return RestResponse.no("添加失败");
                }
            }
        }else{
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return RestResponse.no("添加失败");
        }
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public RestResponse updateInfo(List<WaterFeeStatisticsDetails> waterFeeStatisticsDetails) {
        try {
            String station = waterFeeStatisticsDetails.get(0).getStation();
            List<TotalIdToStation> list = totalIdToStationService.lambdaQuery().eq(TotalIdToStation::getUseType, 2).eq(TotalIdToStation::getStation, waterFeeStatisticsDetails.get(0).getStation()).list();
            if(null != list && list.size()>0){
                Double total = 0.0;
                List<String> collect = list.stream().filter(t->t.getName().equals("合计")).map(TotalIdToStation::getTotalId).collect(Collectors.toList());
                for(String id:collect){
                    Double value = 0.0;
                    TrendsTableParam tableParam = trendsTableParamService.getById(id);
                    if(!tableParam.getPId().equals("0")){
                        List<TrendsTableParam> noTotalList = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, tableParam.getPId()).ne(TrendsTableParam::getParamName, "合计").list();
                        for(TrendsTableParam param:noTotalList){
                            for(WaterFeeStatisticsDetails t:waterFeeStatisticsDetails){
                                if(t.getTableHeadId().equals(param.getId())){
                                    value+=t.getV()==null?0.0:t.getV();
                                }
                                List<TrendsTableParam> listed = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, param.getId()).ne(TrendsTableParam::getParamName, "合计").list();
                                if(null != listed && listed.size()>0){
                                    for (TrendsTableParam param1:listed){
                                        if(t.getTableHeadId().equals(param1.getId())){
                                            value+=t.getV()==null?0.0:t.getV();
                                        }
                                    }
                                }
                            }
                        }
                        for(WaterFeeStatisticsDetails t:waterFeeStatisticsDetails){
                            if(t.getTableHeadId().equals(id)){
                                t.setV(value);
                            }
                        }

                    }
                }
                for(WaterFeeStatisticsDetails t:waterFeeStatisticsDetails){
                    if(!list.stream().map(TotalIdToStation::getTotalId).collect(Collectors.toList()).contains(t.getTableHeadId())){
                        total+=t.getV()==null?0.0:t.getV();
                        System.out.println(t.getTableHeadId());
                    }
                }
                TrendsTableParam one = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, "0").eq(TrendsTableParam::getUseType,2).in(TrendsTableParam::getId, collect).one();
                if(null != one){
                    for(WaterFeeStatisticsDetails t:waterFeeStatisticsDetails){
                        if(t.getTableHeadId().equals(one.getId())){
                            t.setV(total);
                        }
                    }
                }
            }
            if(station.equals("渠首管理站")){
                Double laishui = 0.0;
                Double yinshui = 0.0;
                Double zonggan = 0.0;

                for(WaterFeeStatisticsDetails details:waterFeeStatisticsDetails){
                    String paramName = trendsTableParamService.getById(details.getTableHeadId()).getParamName();
                    if(!paramName.equals("合计") && !paramName.equals("来水") && !paramName.equals("引水") && !paramName.equals("泄洪") && (paramName.equals("东干") || paramName.equals("西干") || paramName.equals("漏斗"))){
                        zonggan += details.getV()==null?0.0:details.getV();
                    }
                }
                for(WaterFeeStatisticsDetails details:waterFeeStatisticsDetails){
                    String paramName = trendsTableParamService.getById(details.getTableHeadId()).getParamName();
                    if(paramName.equals("总干")){
                        details.setV(zonggan);
                    }
                }

                for(WaterFeeStatisticsDetails details:waterFeeStatisticsDetails){
                    String paramName = trendsTableParamService.getById(details.getTableHeadId()).getParamName();
                    if(!paramName.equals("合计") && !paramName.equals("来水") && !paramName.equals("引水") && !paramName.equals("总干") && !paramName.equals("泄洪")){
                        yinshui += details.getV()==null?0.0:details.getV();
                    }
                }
                for(WaterFeeStatisticsDetails details:waterFeeStatisticsDetails){
                    String paramName = trendsTableParamService.getById(details.getTableHeadId()).getParamName();
                    if(paramName.equals("引水")){
                        details.setV(yinshui);
                    }
                }

                for(WaterFeeStatisticsDetails details:waterFeeStatisticsDetails){
                    String paramName = trendsTableParamService.getById(details.getTableHeadId()).getParamName();
                    if(!paramName.equals("合计") && (paramName.equals("引水") || paramName.equals("泄洪"))){
                        laishui += details.getV()==null?0.0:details.getV();
                    }
                }
                for(WaterFeeStatisticsDetails details:waterFeeStatisticsDetails){
                    String paramName = trendsTableParamService.getById(details.getTableHeadId()).getParamName();
                    if(paramName.equals("来水")){
                        details.setV(laishui);
                    }
                }
            }
            boolean b = this.updateBatchById(waterFeeStatisticsDetails);
            if (b) {
                return RestResponse.ok("更新成功");
            }else{
                return RestResponse.no("更新失败");
            }
        }catch (Exception e) {
            e.printStackTrace();
            return RestResponse.no("更新错误");
        }
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public RestResponse update(List<WaterFeeStatisticsDetails> waterFeeStatisticsDetails) {
        RestResponse restResponse = this.updateInfo(waterFeeStatisticsDetails);
        if(restResponse.getCode()==200){
            Double payableWaterFeeCount = 0.0;
            Double unpaidWaterFeesCount = 0.0;
            Double payableWaterResourceCount = 0.0;
            Double waterResourceSurplusCount = 0.0;
            WaterFeeStatisticsDetails tempObj = waterFeeStatisticsDetails.get(0);
            List<WaterFeeStatisticsDetails> totaDetailslList = this.lambdaQuery().eq(WaterFeeStatisticsDetails::getStation, tempObj.getStation()).
                    eq(WaterFeeStatisticsDetails::getYear, tempObj.getYear()).
                    eq(WaterFeeStatisticsDetails::getMonth, tempObj.getMonth()).
                    eq(WaterFeeStatisticsDetails::getTenDays, tempObj.getTenDays()).list();
            List<WaterFeeStatisticsTotal> totalCountList = waterFeeStatisticsTotalService.lambdaQuery().eq(WaterFeeStatisticsTotal::getStation, tempObj.getStation()).
                    eq(WaterFeeStatisticsTotal::getYear, tempObj.getYear()).
                    eq(WaterFeeStatisticsTotal::getMonth, tempObj.getMonth()).
                    eq(WaterFeeStatisticsTotal::getTenDays, tempObj.getTenDays()).list();
            List<WaterFeeStatisticsTotal> waterFeeStatisticsTotalList = new ArrayList<>();
            //详细列表数据统计
            for(WaterFeeStatisticsTotal total : totalCountList){
                WaterFeeStatisticsTotal waterFeeStatisticsTotal = new WaterFeeStatisticsTotal();
                Double aDouble = totaDetailslList.stream().filter(t -> t.getTableHeadId().equals(total.getTableHeadId()) && t.getV()!=null).map(WaterFeeStatisticsDetails::getV).reduce(Double::sum).orElse(0.00);
                waterFeeStatisticsTotal.setUpdateTime(new Date());
                BeanUtils.copyProperties(total,waterFeeStatisticsTotal);
                //
                waterFeeStatisticsTotal.setAmountTo(aDouble);
                //本旬水量
                waterFeeStatisticsTotal.setCurrentWaterVolume((waterFeeStatisticsTotal.getAmountTo()==null?0.0:waterFeeStatisticsTotal.getAmountTo())*60*60*24);
                Map<String, Object> jisuan = jisuan(tempObj.getYear(), tempObj.getMonth(), tempObj.getTenDays());
                if((Integer)jisuan.get("year")== waterFeeStatisticsDetails.get(0).getYear()){
                    WaterFeeStatisticsTotal one = waterFeeStatisticsTotalService.lambdaQuery().eq(WaterFeeStatisticsTotal::getYear, jisuan.get("year")).
                            eq(WaterFeeStatisticsTotal::getMonth, jisuan.get("month")).
                            eq(WaterFeeStatisticsTotal::getTenDays, jisuan.get("tenDays")).
                            eq(WaterFeeStatisticsTotal::getTableHeadId, total.getTableHeadId()).
                            eq(WaterFeeStatisticsTotal::getStation, tempObj.getStation()).
                            one();
                    //上旬水量
                    waterFeeStatisticsTotal.setWaterVolumeFirstTenDays(one==null?0.0:one.getAccumulatedWaterVolume());
                }else {
                    //上旬水量
                    waterFeeStatisticsTotal.setWaterVolumeFirstTenDays(0.0);
                }
                //累计水量
                waterFeeStatisticsTotal.setAccumulatedWaterVolume(waterFeeStatisticsTotal.getCurrentWaterVolume()+waterFeeStatisticsTotal.getWaterVolumeFirstTenDays());
                WaterPriceManagement byId = waterPriceManagementService.getById(total.getTableHeadId());
                Double payableWaterFee = byId==null?0:byId.getWaterPrice()==null?0:byId.getWaterPrice()*waterFeeStatisticsTotal.getAccumulatedWaterVolume();
                //应交水费
                waterFeeStatisticsTotal.setPayableWaterFee(payableWaterFee);
                if(tempObj.getStation().equals("河东管理站绿化") || tempObj.getStation().equals("渠首砂厂")){
                    waterFeeStatisticsTotal.setPayableWaterResource(Double.valueOf(waterResourcePrice)*waterFeeStatisticsTotal.getAccumulatedWaterVolume());
                }
                WaterFeeStatisticsTotal lastYear = waterFeeStatisticsTotalService.lambdaQuery().eq(WaterFeeStatisticsTotal::getYear, tempObj.getYear()-1).
                        eq(WaterFeeStatisticsTotal::getMonth,  tempObj.getMonth()).
                        eq(WaterFeeStatisticsTotal::getTenDays, tempObj.getTenDays()).
                        eq(WaterFeeStatisticsTotal::getTableHeadId, total.getTableHeadId()).
                        eq(WaterFeeStatisticsTotal::getStation, tempObj.getStation()).
                        one();
                waterFeeStatisticsTotal.setWaterVolumeDuringLastYear(lastYear==null?0:lastYear.getAccumulatedWaterVolume());
                waterFeeStatisticsTotalList.add(waterFeeStatisticsTotal);
            }
            if(tempObj.getStation().equals("河东管理站绿化") || tempObj.getStation().equals("渠首砂厂")){
                //预交水资源费(单位)
                Map<String,Double> tempMap = new HashMap<>();
                for(WaterFeeStatisticsTotal total :waterFeeStatisticsTotalList){
                    Double paidWaterResource1 = 0.0;
                    Double paidWaterResource2 = 0.0;
                    List<PaymentWaterFees> list1 = paymentWaterFeesService.lambdaQuery().eq(PaymentWaterFees::getDel,0).
                            eq(PaymentWaterFees::getYear, waterFeeStatisticsDetails.get(0).getYear()).
                            eq(PaymentWaterFees::getWaterUserId, total.getTableHeadId()).eq(PaymentWaterFees::getType,"水资源费").list();
                    if(null != list1 && list1.size()>0){
                        for(PaymentWaterFees fees:list1){
                            paidWaterResource1+=fees.getPaymentAmount();
                        }
                        tempMap.put(total.getTableHeadId()+total.getStation()+total.getYear()+total.getMonth()+total.getTenDays(),paidWaterResource1);
                    }else {
                        TrendsTableParam byId1 = trendsTableParamService.getById(total.getTableHeadId());
                        if(!byId1.getPId().equals("0")){
                            TrendsTableParam one = trendsTableParamService.getById(byId1.getPId());
                            List<PaymentWaterFees> list2 = paymentWaterFeesService.lambdaQuery().eq(PaymentWaterFees::getDel,0).
                                    eq(PaymentWaterFees::getYear, waterFeeStatisticsDetails.get(0).getYear()).
                                    eq(PaymentWaterFees::getWaterUserId, one.getId()).eq(PaymentWaterFees::getType,"水资源费").list();
                            if(null!=list2 && list2.size()>0){
                                for(PaymentWaterFees fees:list2){
                                    paidWaterResource2+=fees.getPaymentAmount();
                                }
                                TrendsTableParam totalBean = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, one.getId()).eq(TrendsTableParam::getParamName, "合计").one();
                                if(totalBean!=null){
                                    tempMap.put(totalBean.getId()+total.getStation()+total.getYear()+total.getMonth()+total.getTenDays(),paidWaterResource2);
                                }
                            }else {
                                tempMap.put(total.getTableHeadId()+total.getStation()+total.getYear()+total.getMonth()+total.getTenDays(),0.0);
                            }
                        }else {
                            tempMap.put(total.getTableHeadId()+total.getStation()+total.getYear()+total.getMonth()+total.getTenDays(),0.0);
                        }
                    }
                }
                for(WaterFeeStatisticsTotal total :waterFeeStatisticsTotalList){
                    //预交水资源费
                    total.setPaidWaterResource(tempMap.get(total.getTableHeadId()+total.getStation()+total.getYear()+total.getMonth()+total.getTenDays())==null?0.0:tempMap.get(total.getTableHeadId()+total.getStation()+total.getYear()+total.getMonth()+total.getTenDays()));
                    //水资源费余(欠)
                    total.setWaterResourceSurplus(total.getPaidWaterResource()-total.getPayableWaterResource());
                }
                //预交水资源费(合计)
                List<TotalIdToStation> listTotalIdToStation = totalIdToStationService.lambdaQuery().eq(TotalIdToStation::getUseType, 2).eq(TotalIdToStation::getStation, waterFeeStatisticsDetails.get(0).getStation()).list();
                if(null != listTotalIdToStation && listTotalIdToStation.size()>0){
                    List<String> totalColl = listTotalIdToStation.stream().filter(t->t.getName().equals("合计")).map(TotalIdToStation::getTotalId).collect(Collectors.toList());
                    for(String id:totalColl){
                        TrendsTableParam byId = trendsTableParamService.getById(id);
                        if(!byId.getPId().equals("0")){
                            List<TrendsTableParam> tableParams = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, byId.getPId()).ne(TrendsTableParam::getParamName, "合计").list();
                            for(TrendsTableParam param:tableParams){
                                List<TrendsTableParam> listed = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, param.getId()).ne(TrendsTableParam::getParamName, "合计").list();
                                if(listed.size()>0){
                                    for(TrendsTableParam param1:listed){
                                        for(WaterFeeStatisticsTotal total:waterFeeStatisticsTotalList){
                                            if(param1.getId().equals(total.getTableHeadId())){
                                                payableWaterResourceCount+=total.getPayableWaterResource();
                                                waterResourceSurplusCount+=total.getWaterResourceSurplus();
                                            }
                                        }
                                    }
                                }else {
                                    for(WaterFeeStatisticsTotal total:waterFeeStatisticsTotalList){
                                        if(param.getId().equals(total.getTableHeadId())){
                                            payableWaterResourceCount+=total.getPayableWaterResource();
                                            waterResourceSurplusCount+=total.getWaterResourceSurplus();
                                        }
                                    }
                                }
                            }
                            for(WaterFeeStatisticsTotal total:waterFeeStatisticsTotalList){
                                if(total.getTableHeadId().equals(id)){
                                    total.setPayableWaterResource(payableWaterResourceCount);
                                    total.setWaterResourceSurplus(waterResourceSurplusCount);
                                    payableWaterResourceCount = 0.0;
                                    waterResourceSurplusCount = 0.0;
                                }
                            }
                        }
                    }
                }
                if(null != listTotalIdToStation && listTotalIdToStation.size()>0){
                    Double paidWaterResourceCount = 0.0;
                    List<String> totalColl = listTotalIdToStation.stream().filter(t->t.getName().equals("合计")).map(TotalIdToStation::getTotalId).collect(Collectors.toList());
                    for(String id:totalColl){
                        TrendsTableParam byId = trendsTableParamService.getById(id);
                        if(byId.getPId().equals("0")){
                            List<String> collect1 = trendsTableParamService.lambdaQuery().
                                    eq(TrendsTableParam::getPId, byId.getPId()).
                                    eq(TrendsTableParam::getUseStation,byId.getUseStation()).
                                    eq(TrendsTableParam::getUseType,byId.getUseType()).
                                    list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());
                            List<PaymentWaterFees> paymentWaterFees = paymentWaterFeesService.lambdaQuery().eq(PaymentWaterFees::getDel,0).
                                    eq(PaymentWaterFees::getYear, waterFeeStatisticsDetails.get(0).getYear()).
                                    in(PaymentWaterFees::getWaterUserId, collect1).eq(PaymentWaterFees::getType,"水资源费").list();
                            if(null != paymentWaterFees && paymentWaterFees.size()>0){
                                for(PaymentWaterFees fees:paymentWaterFees){
                                    paidWaterResourceCount+=fees.getPaymentAmount();
                                }
                            }
                            if(null != collect1 && collect1.size()>0){
                                List<String> collect2 = trendsTableParamService.lambdaQuery().in(TrendsTableParam::getPId, collect1).list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());
                                if(null != collect2 && collect2.size()>0){
                                    List<PaymentWaterFees> paymentWaterFees2 = paymentWaterFeesService.lambdaQuery().eq(PaymentWaterFees::getDel,0).
                                            eq(PaymentWaterFees::getYear, waterFeeStatisticsDetails.get(0).getYear()).
                                            in(PaymentWaterFees::getWaterUserId, collect2).eq(PaymentWaterFees::getType,"水资源费").list();
                                    if(null != paymentWaterFees2 && paymentWaterFees2.size()>0){
                                        for(PaymentWaterFees fees:paymentWaterFees2){
                                            paidWaterResourceCount+=fees.getPaymentAmount();
                                        }
                                    }
                                }
                                if(null != collect2 && collect2.size()>0){
                                    List<String> collect3 = trendsTableParamService.lambdaQuery().in(TrendsTableParam::getPId, collect2).list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());
                                    if(null != collect3 && collect3.size()>0){
                                        List<PaymentWaterFees> paymentWaterFees3= paymentWaterFeesService.lambdaQuery().eq(PaymentWaterFees::getDel,0).
                                                eq(PaymentWaterFees::getYear, waterFeeStatisticsDetails.get(0).getYear()).
                                                in(PaymentWaterFees::getWaterUserId, collect3).eq(PaymentWaterFees::getType,"水资源费").list();
                                        if(null != paymentWaterFees3 && paymentWaterFees3.size()>0){
                                            for(PaymentWaterFees fees:paymentWaterFees3){
                                                paidWaterResourceCount+=fees.getPaymentAmount();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if(!byId.getPId().equals("0")){
                            List<String> collect1 = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, byId.getPId()).list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());
                            List<String> collect1Son = trendsTableParamService.lambdaQuery().in(TrendsTableParam::getPId, collect1).list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());
                            List<PaymentWaterFees> paymentWaterFees = paymentWaterFeesService.lambdaQuery().eq(PaymentWaterFees::getDel,0).
                                    eq(PaymentWaterFees::getYear, waterFeeStatisticsDetails.get(0).getYear()).
                                    in(PaymentWaterFees::getWaterUserId, collect1).eq(PaymentWaterFees::getType,"水资源费").list();
                            if(null!= collect1Son && collect1Son.size()>0){
                                List<PaymentWaterFees> paymentWaterFeesSon = paymentWaterFeesService.lambdaQuery().eq(PaymentWaterFees::getDel,0).
                                        eq(PaymentWaterFees::getYear, waterFeeStatisticsDetails.get(0).getYear()).
                                        in(PaymentWaterFees::getWaterUserId, collect1Son).eq(PaymentWaterFees::getType,"水资源费").list();
                                paymentWaterFees.addAll(paymentWaterFeesSon);
                            }
                            if(null != paymentWaterFees && paymentWaterFees.size()>0){
                                for(PaymentWaterFees fees:paymentWaterFees){
                                    paidWaterResourceCount+=fees.getPaymentAmount();
                                }
                            }else {
                                List<PaymentWaterFees> paymentWaterFees2 = paymentWaterFeesService.lambdaQuery().eq(PaymentWaterFees::getDel,0).
                                        eq(PaymentWaterFees::getYear, waterFeeStatisticsDetails.get(0).getYear()).
                                        in(PaymentWaterFees::getWaterUserId, byId.getPId()).eq(PaymentWaterFees::getType,"水资源费").list();
                                if(null != paymentWaterFees2 && paymentWaterFees2.size()>0){
                                    for(PaymentWaterFees fees:paymentWaterFees2){
                                        paidWaterResourceCount+=fees.getPaymentAmount();
                                    }
                                }
                            }
                        }
                        for(WaterFeeStatisticsTotal total:waterFeeStatisticsTotalList){
                            if(id.equals(total.getTableHeadId())){
                                total.setPaidWaterResource(paidWaterResourceCount);
                                paidWaterResourceCount = 0.0;
                            }
                        }
                    }
                }
            }

            //预交水费(单位)
            Map<String,Double> tempMap = new HashMap<>();
            for(WaterFeeStatisticsTotal total :waterFeeStatisticsTotalList){
                Double advancePaymentWaterFee1 = 0.0;
                Double advancePaymentWaterFee2 = 0.0;
                List<PaymentWaterFees> list1 = paymentWaterFeesService.lambdaQuery().eq(PaymentWaterFees::getDel,0).
                        eq(PaymentWaterFees::getYear, waterFeeStatisticsDetails.get(0).getYear()).
                        eq(PaymentWaterFees::getWaterUserId, total.getTableHeadId()).eq(PaymentWaterFees::getType,"水费").list();
                if(null != list1 && list1.size()>0){
                    for(PaymentWaterFees fees:list1){
                        advancePaymentWaterFee1+=fees.getPaymentAmount();
                    }
                    tempMap.put(total.getTableHeadId()+total.getStation()+total.getYear()+total.getMonth()+total.getTenDays(),advancePaymentWaterFee1);
                }else {
                    TrendsTableParam byId1 = trendsTableParamService.getById(total.getTableHeadId());
                    if(!byId1.getPId().equals("0")){
                        TrendsTableParam one = trendsTableParamService.getById(byId1.getPId());
                        List<PaymentWaterFees> list2 = paymentWaterFeesService.lambdaQuery().eq(PaymentWaterFees::getDel,0).
                                eq(PaymentWaterFees::getYear, waterFeeStatisticsDetails.get(0).getYear()).
                                eq(PaymentWaterFees::getWaterUserId, one.getId()).eq(PaymentWaterFees::getType,"水费").list();
                        if(null!=list2 && list2.size()>0){
                            for(PaymentWaterFees fees:list2){
                                advancePaymentWaterFee2+=fees.getPaymentAmount();
                            }
                            TrendsTableParam totalBean = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, one.getId()).eq(TrendsTableParam::getParamName, "合计").one();
                            if(totalBean!=null){
                                tempMap.put(totalBean.getId()+total.getStation()+total.getYear()+total.getMonth()+total.getTenDays(),advancePaymentWaterFee2);
                            }
                        }else {
                            tempMap.put(total.getTableHeadId()+total.getStation()+total.getYear()+total.getMonth()+total.getTenDays(),0.0);
                        }
                    }else {
                        tempMap.put(total.getTableHeadId()+total.getStation()+total.getYear()+total.getMonth()+total.getTenDays(),0.0);
                    }
                }
            }
            for(WaterFeeStatisticsTotal total :waterFeeStatisticsTotalList){
                //预交水费
                total.setAdvancePaymentWaterFee(tempMap.get(total.getTableHeadId()+total.getStation()+total.getYear()+total.getMonth()+total.getTenDays())==null?0.0:tempMap.get(total.getTableHeadId()+total.getStation()+total.getYear()+total.getMonth()+total.getTenDays()));
                //水费余(欠)
                total.setUnpaidWaterFees(total.getAdvancePaymentWaterFee()-total.getPayableWaterFee());
            }
            //预交水费(合计)
            List<TotalIdToStation> listTotalIdToStation = totalIdToStationService.lambdaQuery().eq(TotalIdToStation::getUseType, 2).eq(TotalIdToStation::getStation, waterFeeStatisticsDetails.get(0).getStation()).list();
            if(null != listTotalIdToStation && listTotalIdToStation.size()>0){
                List<String> totalColl = listTotalIdToStation.stream().filter(t->t.getName().equals("合计")).map(TotalIdToStation::getTotalId).collect(Collectors.toList());
                for(String id:totalColl){
                    TrendsTableParam byId = trendsTableParamService.getById(id);
                    if(!byId.getPId().equals("0")){
                        List<TrendsTableParam> tableParams = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, byId.getPId()).ne(TrendsTableParam::getParamName, "合计").list();
                        for(TrendsTableParam param:tableParams){
                            List<TrendsTableParam> listed = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, param.getId()).ne(TrendsTableParam::getParamName, "合计").list();
                            if(listed.size()>0){
                                for(TrendsTableParam param1:listed){
                                    for(WaterFeeStatisticsTotal total:waterFeeStatisticsTotalList){
                                        if(param1.getId().equals(total.getTableHeadId())){
                                            payableWaterFeeCount+=total.getPayableWaterFee();
                                            unpaidWaterFeesCount+=total.getUnpaidWaterFees();
                                        }
                                    }
                                }
                            }else {
                                for(WaterFeeStatisticsTotal total:waterFeeStatisticsTotalList){
                                    if(param.getId().equals(total.getTableHeadId())){
                                        payableWaterFeeCount+=total.getPayableWaterFee();
                                        unpaidWaterFeesCount+=total.getUnpaidWaterFees();
                                    }
                                }
                            }
                        }
                        for(WaterFeeStatisticsTotal total:waterFeeStatisticsTotalList){
                            if(total.getTableHeadId().equals(id)){
                                total.setPayableWaterFee(payableWaterFeeCount);
                                total.setUnpaidWaterFees(unpaidWaterFeesCount);
                                payableWaterFeeCount = 0.0;
                                unpaidWaterFeesCount = 0.0;
                            }
                        }
                    }
                }
            }
            if(null != listTotalIdToStation && listTotalIdToStation.size()>0){
                Double advancePaymentWaterFeeCount = 0.0;
                List<String> totalColl = listTotalIdToStation.stream().filter(t->t.getName().equals("合计")).map(TotalIdToStation::getTotalId).collect(Collectors.toList());
                for(String id:totalColl){
                    TrendsTableParam byId = trendsTableParamService.getById(id);
                    if(byId.getPId().equals("0")){
                        List<String> collect1 = trendsTableParamService.lambdaQuery().
                                eq(TrendsTableParam::getPId, byId.getPId()).
                                eq(TrendsTableParam::getUseStation,byId.getUseStation()).
                                eq(TrendsTableParam::getUseType,byId.getUseType()).
                                list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());
                        List<PaymentWaterFees> paymentWaterFees = paymentWaterFeesService.lambdaQuery().eq(PaymentWaterFees::getDel,0).
                                eq(PaymentWaterFees::getYear, waterFeeStatisticsDetails.get(0).getYear()).
                                in(PaymentWaterFees::getWaterUserId, collect1).eq(PaymentWaterFees::getType,"水费").list();
                        if(null != paymentWaterFees && paymentWaterFees.size()>0){
                            for(PaymentWaterFees fees:paymentWaterFees){
                                advancePaymentWaterFeeCount+=fees.getPaymentAmount();
                            }
                        }
                        if(null != collect1 && collect1.size()>0){
                            List<String> collect2 = trendsTableParamService.lambdaQuery().in(TrendsTableParam::getPId, collect1).list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());
                            if(null != collect2 && collect2.size()>0){
                                List<PaymentWaterFees> paymentWaterFees2 = paymentWaterFeesService.lambdaQuery().eq(PaymentWaterFees::getDel,0).
                                        eq(PaymentWaterFees::getYear, waterFeeStatisticsDetails.get(0).getYear()).
                                        in(PaymentWaterFees::getWaterUserId, collect2).eq(PaymentWaterFees::getType,"水费").list();
                                if(null != paymentWaterFees2 && paymentWaterFees2.size()>0){
                                    for(PaymentWaterFees fees:paymentWaterFees2){
                                        advancePaymentWaterFeeCount+=fees.getPaymentAmount();
                                    }
                                }
                            }
                            if(null != collect2 && collect2.size()>0){
                                List<String> collect3 = trendsTableParamService.lambdaQuery().in(TrendsTableParam::getPId, collect2).list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());
                                if(null != collect3 && collect3.size()>0){
                                    List<PaymentWaterFees> paymentWaterFees3= paymentWaterFeesService.lambdaQuery().eq(PaymentWaterFees::getDel,0).
                                            eq(PaymentWaterFees::getYear, waterFeeStatisticsDetails.get(0).getYear()).
                                            in(PaymentWaterFees::getWaterUserId, collect3).eq(PaymentWaterFees::getType,"水费").list();
                                    if(null != paymentWaterFees3 && paymentWaterFees3.size()>0){
                                        for(PaymentWaterFees fees:paymentWaterFees3){
                                            advancePaymentWaterFeeCount+=fees.getPaymentAmount();
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if(!byId.getPId().equals("0")){
                        List<String> collect1 = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, byId.getPId()).list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());
                        List<String> collect1Son = trendsTableParamService.lambdaQuery().in(TrendsTableParam::getPId, collect1).list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());
                        List<PaymentWaterFees> paymentWaterFees = paymentWaterFeesService.lambdaQuery().eq(PaymentWaterFees::getDel,0).
                                eq(PaymentWaterFees::getYear, waterFeeStatisticsDetails.get(0).getYear()).
                                in(PaymentWaterFees::getWaterUserId, collect1).eq(PaymentWaterFees::getType,"水费").list();
                        if(null!= collect1Son && collect1Son.size()>0){
                            List<PaymentWaterFees> paymentWaterFeesSon = paymentWaterFeesService.lambdaQuery().eq(PaymentWaterFees::getDel,0).
                                    eq(PaymentWaterFees::getYear, waterFeeStatisticsDetails.get(0).getYear()).
                                    in(PaymentWaterFees::getWaterUserId, collect1Son).eq(PaymentWaterFees::getType,"水费").list();
                            paymentWaterFees.addAll(paymentWaterFeesSon);
                        }
                        if(null != paymentWaterFees && paymentWaterFees.size()>0){
                            for(PaymentWaterFees fees:paymentWaterFees){
                                advancePaymentWaterFeeCount+=fees.getPaymentAmount();
                            }
                        }else {
                            List<PaymentWaterFees> paymentWaterFees2 = paymentWaterFeesService.lambdaQuery().eq(PaymentWaterFees::getDel,0).
                                    eq(PaymentWaterFees::getYear, waterFeeStatisticsDetails.get(0).getYear()).
                                    in(PaymentWaterFees::getWaterUserId, byId.getPId()).eq(PaymentWaterFees::getType,"水费").list();
                            if(null != paymentWaterFees2 && paymentWaterFees2.size()>0){
                                for(PaymentWaterFees fees:paymentWaterFees2){
                                    advancePaymentWaterFeeCount+=fees.getPaymentAmount();
                                }
                            }
                        }
                    }
                    for(WaterFeeStatisticsTotal total:waterFeeStatisticsTotalList){
                        if(id.equals(total.getTableHeadId())){
                            total.setAdvancePaymentWaterFee(advancePaymentWaterFeeCount);
                            advancePaymentWaterFeeCount = 0.0;
                        }
                    }
                }
            }
            for(WaterFeeStatisticsTotal total:waterFeeStatisticsTotalList){
                if(total.getWaterResourceSurplus()!=null){
                    total.setWaterResourceTotal(total.getUnpaidWaterFees()+total.getWaterResourceSurplus());
                }
            }
            //总合计
            if(null != listTotalIdToStation && listTotalIdToStation.size()>0){
                Double payableWaterFeeAllCount = 0.0;
                Double unpaidWaterFeesAllCount = 0.0;
                List<String> totalColl = listTotalIdToStation.stream().filter(t->t.getName().equals("合计")).map(TotalIdToStation::getTotalId).collect(Collectors.toList());
                for(String id:totalColl){
                    TrendsTableParam byId = trendsTableParamService.getById(id);
                    if(byId.getPId().equals("0")){
                        for(WaterFeeStatisticsTotal total:waterFeeStatisticsTotalList){
                            if(!totalColl.contains(total.getTableHeadId())){
                                payableWaterFeeAllCount += total.getPayableWaterFee();
                                unpaidWaterFeesAllCount += total.getUnpaidWaterFees();
                            }
                        }
                        for(WaterFeeStatisticsTotal total:waterFeeStatisticsTotalList){
                            if(id.equals(total.getTableHeadId())){
                                total.setPayableWaterFee(payableWaterFeeAllCount);
                                total.setUnpaidWaterFees(unpaidWaterFeesAllCount);
                            }
                        }
                    }
                }
            }
            boolean b1 = waterFeeStatisticsTotalService.updateBatchById(waterFeeStatisticsTotalList);
            if (b1) {
                RestResponse add = tenDaysWaterBalanceService.add(waterFeeStatisticsTotalList);
                if(add.getCode()==200){
                    Integer day = Integer.valueOf(waterFeeStatisticsDetails.get(0).getStatisticsDate().substring(3, 5));
                    log.error("----------------------------------day--------------------------------"+day);
                    RestResponse add1 = dayWaterBalanceService.update(waterFeeStatisticsDetails, day);
                    if(add1.getCode()==200){
                        return RestResponse.ok("添加成功");
                    }else {
                        return RestResponse.no("添加失败");
                    }
                }else {
                    return RestResponse.no("添加失败");
                }
            }else {
                return RestResponse.no("更新失败");
            }
        }else {
            return RestResponse.no("更新失败");
        }
    }

    @Override
    public RestResponse<Map<String, List<WaterFeeStatisticsDetails>>> selectList(WaterFeeStatisticsDetailsSelectListReq req) {
        try {
            List<WaterFeeStatisticsDetails> list = this.lambdaQuery().eq(WaterFeeStatisticsDetails::getStation, req.getStation()).
                    eq(WaterFeeStatisticsDetails::getYear, req.getYear()).
                    eq(WaterFeeStatisticsDetails::getMonth, req.getMonth()).
                    eq(WaterFeeStatisticsDetails::getTenDays, req.getTenDays()).list();
            if(null != list && list.size()>0){
                Map<String, List<WaterFeeStatisticsDetails>> collect = list.stream().collect(Collectors.groupingBy(WaterFeeStatisticsDetails::getStatisticsDate));
                collect.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEachOrdered(x->collect.put(x.getKey(),x.getValue()));
                return RestResponse.ok(collect);
            }else {
                return RestResponse.no("暂无数据");
            }
        }catch (Exception e) {
            e.printStackTrace();
            return RestResponse.no("查询失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse clearTable(WaterFeeStatisticsDetailsSelectListReq req) {
        boolean remove = this.lambdaUpdate().eq(WaterFeeStatisticsDetails::getStation, req.getStation()).
                eq(WaterFeeStatisticsDetails::getYear, req.getYear()).
                eq(WaterFeeStatisticsDetails::getMonth, req.getMonth()).
                eq(WaterFeeStatisticsDetails::getTenDays, req.getTenDays()).remove();
        boolean remove1 = waterFeeStatisticsTotalService.lambdaUpdate().eq(WaterFeeStatisticsTotal::getStation, req.getStation()).
                eq(WaterFeeStatisticsTotal::getYear, req.getYear()).
                eq(WaterFeeStatisticsTotal::getMonth, req.getMonth()).
                eq(WaterFeeStatisticsTotal::getTenDays, req.getTenDays()).remove();
        boolean remove2 = waterDistributionRatioService.lambdaUpdate().eq(WaterDistributionRatio::getStation, req.getStation()).
                eq(WaterDistributionRatio::getYear, req.getYear()).
                eq(WaterDistributionRatio::getMonth, req.getMonth()).
                eq(WaterDistributionRatio::getTenDays, req.getTenDays()).remove();
        boolean remove3 = tenDaysWaterBalanceService.lambdaUpdate().eq(TenDaysWaterBalance::getStation, req.getStation()).
                eq(TenDaysWaterBalance::getYear, req.getYear()).
                eq(TenDaysWaterBalance::getMonth, req.getMonth()).
                eq(TenDaysWaterBalance::getTenDays, req.getTenDays()).remove();
        boolean remove4 = dayWaterBalanceService.lambdaUpdate().eq(DayWaterBalance::getStation, req.getStation()).
                eq(DayWaterBalance::getYear, req.getYear()).
                eq(DayWaterBalance::getMonth, req.getMonth()).
                in(DayWaterBalance::getDay, getDays(req.getTenDays())).remove();
        if(remove1 && remove2 && remove && remove3 && remove4){
            return RestResponse.ok("清空表成功");
        }else {
            return RestResponse.no("清空表失败");
        }
    }

    public static Map<String, Object> jisuan(Integer year,Integer month,String tenDays){
        Map<String, Object> result = new HashMap();
        if(tenDays.equals("上旬")){
            if(month==1){
                year = year-1;
                month = 12;
                tenDays = "下旬";
                result.put("year",year);
                result.put("month",month);
                result.put("tenDays",tenDays);
                return result;
            }else {
                month = month-1;
                if(tenDays.equals("上旬")){
                    tenDays ="下旬";
                    result.put("year",year);
                    result.put("month",month);
                    result.put("tenDays",tenDays);
                    return result;
                }
                if(tenDays.equals("中旬")){
                    tenDays ="上旬";
                    result.put("year",year);
                    result.put("month",month);
                    result.put("tenDays",tenDays);
                    return result;
                }
                if(tenDays.equals("下旬")){
                    tenDays = "中旬";
                    result.put("year",year);
                    result.put("month",month);
                    result.put("tenDays",tenDays);
                    return result;
                }
            }
        }

        if(tenDays.equals("中旬")){
            tenDays ="上旬";
            result.put("year",year);
            result.put("month",month);
            result.put("tenDays",tenDays);
            return result;
        }
        if(tenDays.equals("下旬")){
            tenDays = "中旬";
            result.put("year",year);
            result.put("month",month);
            result.put("tenDays",tenDays);
            return result;
        }
        return result;
    }

    public List<Integer> getDays(String tenDays){
        List<Integer> result = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();
        int daysInMonth = currentDate.lengthOfMonth();
        if(tenDays.equals("上旬")){
            for(int i=1;i<=10;i++){
                result.add(i);
            }
        }
        if(tenDays.equals("中旬")){
            for(int i=11;i<=10;i++){
                result.add(i);
            }
        }
        if(tenDays.equals("下旬")){
            for(int i=21;i<=daysInMonth;i++){
                result.add(i);
            }
        }
        return result;
    }
}

