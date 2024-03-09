package com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.service.impl;

import cn.hutool.poi.excel.sax.ElementName;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.common.model.RestResponse;
import com.cj.common.util.RedisUtil;
import com.cj.common.util.UUIDUtils;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.entity.IrrigatedPlatformDataInfo;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.service.IrrigatedPlatformDataInfoService;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.dayWaterBalance.entity.DayWaterBalance;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.dayWaterBalance.service.DayWaterBalanceService;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.tenDaysWaterBalance.entity.TenDaysWaterBalance;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.tenDaysWaterBalance.service.TenDaysWaterBalanceService;
import com.cj.waterresources.func.modular.trendsTable.bean.req.QueryTrendsTableParamReq;
import com.cj.waterresources.func.modular.trendsTable.bean.res.WaterDailyParamSelectRes;
import com.cj.waterresources.func.modular.trendsTable.entity.TrendsTableParam;
import com.cj.waterresources.func.modular.trendsTable.service.TrendsTableParamService;
import com.cj.waterresources.func.modular.waterPrice.industrialWaterFee.service.IndustrialWaterFeeService;
import com.cj.waterresources.func.modular.waterPrice.paymentWaterFees.entity.PaymentWaterFees;
import com.cj.waterresources.func.modular.waterPrice.paymentWaterFees.service.PaymentWaterFeesService;
import com.cj.waterresources.func.modular.waterPrice.totalIdToStation.entity.TotalIdToStation;
import com.cj.waterresources.func.modular.waterPrice.totalIdToStation.service.TotalIdToStationService;
import com.cj.waterresources.func.modular.waterPrice.waterDistributionRatio.entity.WaterDistributionRatio;
import com.cj.waterresources.func.modular.waterPrice.waterDistributionRatio.service.WaterDistributionRatioService;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.bean.req.UseWaterTypeStatisticsReq;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.bean.req.WaterFeeStatisticsDetailsSelectListReq;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.bean.res.JobRes;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.bean.res.UseWaterTypeStatisticsRes;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.entity.WaterFeeStatisticsTotal;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.mapper.WaterFeeStatisticsDetailsMapper;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.entity.WaterFeeStatisticsDetails;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.service.WaterFeeStatisticsDetailsService;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.service.WaterFeeStatisticsTotalService;
import com.cj.waterresources.func.modular.waterPrice.waterPriceManagement.entity.WaterPriceManagement;
import com.cj.waterresources.func.modular.waterPrice.waterPriceManagement.service.WaterPriceManagementService;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.qs.service.DayWaterSituationStatisticsTableQsService;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.poi.excel.sax.ElementName.c;
import static cn.hutool.poi.excel.sax.ElementName.v;

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

    @Autowired
    private IndustrialWaterFeeService industrialWaterFeeService;

    @Autowired
    private RedisUtil redisUtil;


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
        String mk = (String) redisUtil.get("trendsTableParam:list");
        if(StringUtils.isEmpty(mk)){
            updateCache();
            mk = (String) redisUtil.get("trendsTableParam:list");
        }
        List<TrendsTableParam> trendsTableParamListTemp = JSONObject.parseArray(mk, TrendsTableParam.class);
        List<TrendsTableParam> trendsTableParamList = trendsTableParamListTemp.stream().filter(t -> t.getUseType() == 2).collect(Collectors.toList());
        String station = waterFeeStatisticsDetails.get(0).getStation();
        String dateTemp = waterFeeStatisticsDetails.get(0).getYear()+"-"+waterFeeStatisticsDetails.get(0).getStatisticsDate().substring(0,2)+"-"+waterFeeStatisticsDetails.get(0).getStatisticsDate().substring(3,5);
        if(station.equals("渠首管理站")){
            waterFeeStatisticsDetails.forEach(t->{
                Map<String, Double> lanternCanalInfoByDate = getLanternCanalInfoByDate(t.getYear(),t.getMonth(),t.getTenDays(),t.getStatisticsDate());
                String paramName = (String)redisUtil.get("trendsTableParam:name:"+t.getTableHeadId());
                if(paramName.equals("农业")){
                    t.setV(lanternCanalInfoByDate ==null?null:lanternCanalInfoByDate.get("agricultureValue")==null?null:lanternCanalInfoByDate.get("agricultureValue"));
                }
                if(paramName.equals("工业")){
                    t.setV(lanternCanalInfoByDate ==null?null:lanternCanalInfoByDate.get("industryValue")==null?null:lanternCanalInfoByDate.get("industryValue"));
                }
                if(paramName.equals("绿化")){
                    t.setV(lanternCanalInfoByDate ==null?null:lanternCanalInfoByDate.get("greenValue")==null?null:lanternCanalInfoByDate.get("greenValue"));
                }
                t.setId(UUIDUtils.getUUID());
                try {
                    t.setRecordTime(sdf.parse(dateTemp));
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            });
        }else {
            waterFeeStatisticsDetails.forEach(t->{
                if(!sdf.format(new Date()).equals(dateTemp)){
                    String paramName = (String)redisUtil.get("trendsTableParam:name:"+t.getTableHeadId());
                    if(!paramName.equals("合计")){
                        String tableParamString = (String)redisUtil.get("trendsTableParam:object:"+t.getTableHeadId());
                        TrendsTableParam tableParam = JSONObject.parseObject(tableParamString, TrendsTableParam.class);
                        if(t.getStation().contains("河东")){
                            Double v = (Double)redisUtil.get("A3:data:hd:yesterday:"+dateTemp+":"+tableParam.getUnitId());
                            t.setV(v==null?null:v);
                        }
                        if(t.getStation().contains("河西")){
                            Double v = (Double)redisUtil.get("A3:data:hx:yesterday:"+dateTemp+":"+tableParam.getUnitId());
                            t.setV(v==null?null:v);
                        }
                        if(t.getStation().contains("渠首")){
                            Double v = (Double)redisUtil.get("A3:data:qs:yesterday:"+dateTemp+":"+tableParam.getUnitId());
                            t.setV(v==null?null:v);
                        }
                    }
                }
                t.setId(UUIDUtils.getUUID());
                try {
                    t.setRecordTime(sdf.parse(dateTemp));
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        List<TotalIdToStation> list = totalIdToStationService.lambdaQuery().eq(TotalIdToStation::getUseType, 2).eq(TotalIdToStation::getStation, waterFeeStatisticsDetails.get(0).getStation()).list();
        //计算行合计
        if(null != list && list.size()>0){
            Double total = 0.0;
            List<String> collect = list.stream().filter(t->t.getName().equals("合计")).map(TotalIdToStation::getTotalId).collect(Collectors.toList());
            for(String id:collect){
                Double value = 0.0;
                TrendsTableParam tableParam = JSONObject.parseObject((String)redisUtil.get("trendsTableParam:object:"+id),TrendsTableParam.class);
                if(!tableParam.getPId().equals("0")){
                    List<TrendsTableParam> noTotalList = trendsTableParamList.stream().filter(t -> t.getPId().equals(tableParam.getPId())).filter(t -> !t.getParamName().equals("合计")).collect(Collectors.toList());
                    //List<TrendsTableParam> noTotalList = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, tableParam.getPId()).ne(TrendsTableParam::getParamName, "合计").list();
                    for(TrendsTableParam param:noTotalList){
                        for(WaterFeeStatisticsDetails t:waterFeeStatisticsDetails){
                            if(t.getTableHeadId().equals(param.getId())){
                                value+=t.getV()==null?0.0:t.getV();
                            }
                            List<TrendsTableParam> listed = trendsTableParamList.stream().filter(s -> s.getPId().equals(param.getId())).filter(s -> !s.getParamName().equals("合计")).collect(Collectors.toList());
                            //List<TrendsTableParam> listed = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, param.getId()).ne(TrendsTableParam::getParamName, "合计").list();
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
            TrendsTableParam one =null;
            List<TrendsTableParam> collect1 = trendsTableParamList.stream().filter(s -> s.getPId().equals("0")).filter(s -> s.getUseType() == 2).collect(Collectors.toList());
            for(TrendsTableParam param : collect1){
                for(String s:collect){
                    if(param.getId().equals(s)){
                        one = param;
                    }
                }
            }
            //TrendsTableParam one = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, "0").eq(TrendsTableParam::getUseType,2).in(TrendsTableParam::getId, collect).one();
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
                String paramName = (String)redisUtil.get("trendsTableParam:name:"+details.getTableHeadId());
                //String paramName = trendsTableParamService.getById(details.getTableHeadId()).getParamName();
                if(!paramName.equals("合计") && !paramName.equals("来水") && !paramName.equals("引水") && !paramName.equals("泄洪") && (paramName.equals("东干") || paramName.equals("西干") || paramName.equals("漏斗"))){
                    zonggan += details.getV()==null?0.0:details.getV();
                }
            }
            for(WaterFeeStatisticsDetails details:waterFeeStatisticsDetails){
                String paramName = (String)redisUtil.get("trendsTableParam:name:"+details.getTableHeadId());
                //String paramName = trendsTableParamService.getById(details.getTableHeadId()).getParamName();
                if(paramName.equals("总干")){
                    details.setV(zonggan);
                }
            }

            for(WaterFeeStatisticsDetails details:waterFeeStatisticsDetails){
                String paramName = (String)redisUtil.get("trendsTableParam:name:"+details.getTableHeadId());
                //String paramName = trendsTableParamService.getById(details.getTableHeadId()).getParamName();
                if(!paramName.equals("合计") && !paramName.equals("来水") && !paramName.equals("引水") && !paramName.equals("总干") && !paramName.equals("泄洪")){
                    yinshui += details.getV()==null?0.0:details.getV();
                }
            }
            for(WaterFeeStatisticsDetails details:waterFeeStatisticsDetails){
                String paramName = (String)redisUtil.get("trendsTableParam:name:"+details.getTableHeadId());
                //String paramName = trendsTableParamService.getById(details.getTableHeadId()).getParamName();
                if(paramName.equals("引水")){
                    details.setV(yinshui);
                }
            }

            for(WaterFeeStatisticsDetails details:waterFeeStatisticsDetails){
                String paramName = (String)redisUtil.get("trendsTableParam:name:"+details.getTableHeadId());
                //String paramName = trendsTableParamService.getById(details.getTableHeadId()).getParamName();
                if(!paramName.equals("合计") && (paramName.equals("引水") || paramName.equals("泄洪"))){
                    laishui += details.getV()==null?0.0:details.getV();
                }
            }
            for(WaterFeeStatisticsDetails details:waterFeeStatisticsDetails){
                String paramName = (String)redisUtil.get("trendsTableParam:name:"+details.getTableHeadId());
                //String paramName = trendsTableParamService.getById(details.getTableHeadId()).getParamName();
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
                            TrendsTableParam byId1 = JSONObject.parseObject((String)redisUtil.get("trendsTableParam:object:"+total.getTableHeadId()),TrendsTableParam.class);
                            //TrendsTableParam byId1 = trendsTableParamService.getById(total.getTableHeadId());
                            if(!byId1.getPId().equals("0")){
                                TrendsTableParam one = JSONObject.parseObject((String)redisUtil.get("trendsTableParam:object:"+byId1.getPId()),TrendsTableParam.class);
                                //TrendsTableParam one = trendsTableParamService.getById(byId1.getPId());
                                List<PaymentWaterFees> list2 = paymentWaterFeesService.lambdaQuery().eq(PaymentWaterFees::getDel,0).
                                        eq(PaymentWaterFees::getYear, waterFeeStatisticsDetails.get(0).getYear()).
                                        eq(PaymentWaterFees::getWaterUserId, one.getId()).eq(PaymentWaterFees::getType,"水资源费").list();
                                if(null!=list2 && list2.size()>0){
                                    for(PaymentWaterFees fees:list2){
                                        paidWaterResource2+=fees.getPaymentAmount();
                                    }
                                    List<TrendsTableParam> totalTemp = trendsTableParamList.stream().filter(r -> r.getPId().equals(one.getId())).filter(r -> r.getParamName().equals("合计")).collect(Collectors.toList());
                                    TrendsTableParam totalBean = totalTemp.get(0);
                                    //TrendsTableParam totalBean = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, one.getId()).eq(TrendsTableParam::getParamName, "合计").one();
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
                            TrendsTableParam byId = JSONObject.parseObject((String)redisUtil.get("trendsTableParam:object:"+id),TrendsTableParam.class);
                            //TrendsTableParam byId = trendsTableParamService.getById(id);
                            if(!byId.getPId().equals("0")){
                                List<TrendsTableParam> tableParams = trendsTableParamList.stream().filter(r -> r.getPId().equals(byId.getPId())).filter(r -> !r.getParamName().equals("合计")).collect(Collectors.toList());
                                //List<TrendsTableParam> tableParams = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, byId.getPId()).ne(TrendsTableParam::getParamName, "合计").list();
                                for(TrendsTableParam param:tableParams){
                                    List<TrendsTableParam> listed = trendsTableParamList.stream().filter(r -> r.getPId().equals(param.getId())).filter(r -> !r.getParamName().equals("合计")).collect(Collectors.toList());
                                    //List<TrendsTableParam> listed = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, param.getId()).ne(TrendsTableParam::getParamName, "合计").list();
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
                            TrendsTableParam byId =JSONObject.parseObject((String)redisUtil.get("trendsTableParam:object:"+id),TrendsTableParam.class);
                            //TrendsTableParam byId = trendsTableParamService.getById(id);
                            if(byId.getPId().equals("0")){
                                List<String> collect1 = trendsTableParamList.stream().filter(t->t.getPId().equals(byId.getPId())).
                                        filter(t->t.getUseStation().equals(byId.getUseStation())).
                                        filter(t->t.getUseType()==byId.getUseType()).collect(Collectors.toList()).stream().map(TrendsTableParam::getId).collect(Collectors.toList());
                               /* List<String> collect1 = trendsTableParamService.lambdaQuery().
                                        eq(TrendsTableParam::getPId, byId.getPId()).
                                        eq(TrendsTableParam::getUseStation,byId.getUseStation()).
                                        eq(TrendsTableParam::getUseType,byId.getUseType()).
                                        list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());*/
                                List<PaymentWaterFees> paymentWaterFees = paymentWaterFeesService.lambdaQuery().eq(PaymentWaterFees::getDel,0).
                                        eq(PaymentWaterFees::getYear, waterFeeStatisticsDetails.get(0).getYear()).
                                        in(PaymentWaterFees::getWaterUserId, collect1).eq(PaymentWaterFees::getType,"水资源费").list();
                                if(null != paymentWaterFees && paymentWaterFees.size()>0){
                                    for(PaymentWaterFees fees:paymentWaterFees){
                                        paidWaterResourceCount+=fees.getPaymentAmount();
                                    }
                                }
                                if(null != collect1 && collect1.size()>0){
                                    List<String> collect2 = new ArrayList<>();
                                    for(TrendsTableParam param :trendsTableParamList){
                                        for(String s:collect1){
                                            if(param.getPId().equals(s)){
                                                collect2.add(param.getId());
                                            }
                                        }
                                    }
                                    //List<String> collect2 = trendsTableParamService.lambdaQuery().in(TrendsTableParam::getPId, collect1).list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());
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
                                        List<String> collect3 = new ArrayList<>();
                                        for(TrendsTableParam param :trendsTableParamList){
                                            for(String s:collect2){
                                                if(param.getPId().equals(s)){
                                                    collect3.add(param.getId());
                                                }
                                            }
                                        }
                                        //List<String> collect3 = trendsTableParamService.lambdaQuery().in(TrendsTableParam::getPId, collect2).list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());
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
                                List<String> collect1 = trendsTableParamList.stream().filter(t->t.getPId().equals(byId.getPId())).map(TrendsTableParam::getId).collect(Collectors.toList());
                                //List<String> collect1 = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, byId.getPId()).list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());
                                List<String> collect1Son = new ArrayList<>();
                                for(TrendsTableParam param :trendsTableParamList){
                                    for(String s:collect1){
                                        if(param.getPId().equals(s)){
                                            collect1Son.add(param.getId());
                                        }
                                    }
                                }
                                //List<String> collect1Son = trendsTableParamService.lambdaQuery().in(TrendsTableParam::getPId, collect1).list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());
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
                        TrendsTableParam byId1 = JSONObject.parseObject((String)redisUtil.get("trendsTableParam:object:"+total.getTableHeadId()),TrendsTableParam.class);
                        //TrendsTableParam byId1 = trendsTableParamService.getById(total.getTableHeadId());
                        if(!byId1.getPId().equals("0")){
                            TrendsTableParam one = JSONObject.parseObject((String)redisUtil.get("trendsTableParam:object:"+byId1.getPId()),TrendsTableParam.class);
                            //TrendsTableParam one = trendsTableParamService.getById(byId1.getPId());
                            List<PaymentWaterFees> list2 = paymentWaterFeesService.lambdaQuery().eq(PaymentWaterFees::getDel,0).
                                    eq(PaymentWaterFees::getYear, waterFeeStatisticsDetails.get(0).getYear()).
                                    eq(PaymentWaterFees::getWaterUserId, one.getId()).eq(PaymentWaterFees::getType,"水费").list();
                            if(null!=list2 && list2.size()>0){
                                for(PaymentWaterFees fees:list2){
                                    advancePaymentWaterFee2+=fees.getPaymentAmount();
                                }
                                List<TrendsTableParam> totalTemp = trendsTableParamList.stream().filter(t -> t.getPId().equals(one.getId())).filter(t -> t.getParamName().equals("合计")).collect(Collectors.toList());
                                TrendsTableParam totalBean = totalTemp.get(0);
                                //TrendsTableParam totalBean = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, one.getId()).eq(TrendsTableParam::getParamName, "合计").one();
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
                        TrendsTableParam byId = JSONObject.parseObject((String)redisUtil.get("trendsTableParam:object:"+id),TrendsTableParam.class);
                        //TrendsTableParam byId = trendsTableParamService.getById(id);
                        if(!byId.getPId().equals("0")){
                            List<TrendsTableParam> tableParams = trendsTableParamList.stream().filter(t->t.getPId().equals(byId.getPId())).filter(t->!t.getParamName().equals("合计")).collect(Collectors.toList());
                            //List<TrendsTableParam> tableParams = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, byId.getPId()).ne(TrendsTableParam::getParamName, "合计").list();
                            for(TrendsTableParam param:tableParams){
                                List<TrendsTableParam> listed = trendsTableParamList.stream().filter(t->t.getPId().equals(param.getId())).filter(t->!t.getParamName().equals("合计")).collect(Collectors.toList());
                                //List<TrendsTableParam> listed = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, param.getId()).ne(TrendsTableParam::getParamName, "合计").list();
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
                        TrendsTableParam byId = JSONObject.parseObject((String)redisUtil.get("trendsTableParam:object:"+id),TrendsTableParam.class);
                        //TrendsTableParam byId = trendsTableParamService.getById(id);
                        if(byId.getPId().equals("0")){
                            List<String> collect1 = trendsTableParamList.stream().filter(t->t.getPId().equals(byId.getPId())).
                                    filter(t->t.getUseStation().equals(byId.getUseStation())).
                                    filter(t->t.getUseType()==byId.getUseType()).map(TrendsTableParam::getId).collect(Collectors.toList());
                            /*List<String> collect1 = trendsTableParamService.lambdaQuery().
                                    eq(TrendsTableParam::getPId, byId.getPId()).
                                    eq(TrendsTableParam::getUseStation,byId.getUseStation()).
                                    eq(TrendsTableParam::getUseType,byId.getUseType()).
                                    list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());*/
                            List<PaymentWaterFees> paymentWaterFees = paymentWaterFeesService.lambdaQuery().eq(PaymentWaterFees::getDel,0).
                                    eq(PaymentWaterFees::getYear, waterFeeStatisticsDetails.get(0).getYear()).
                                    in(PaymentWaterFees::getWaterUserId, collect1).eq(PaymentWaterFees::getType,"水费").list();
                            if(null != paymentWaterFees && paymentWaterFees.size()>0){
                                for(PaymentWaterFees fees:paymentWaterFees){
                                    advancePaymentWaterFeeCount+=fees.getPaymentAmount();
                                }
                            }
                            if(null != collect1 && collect1.size()>0){
                                List<String> collect2 = new ArrayList<>();
                                for(TrendsTableParam param:trendsTableParamList){
                                    for(String s:collect1){
                                        if(param.getPId().equals(s)){
                                            collect2.add(param.getId());
                                        }
                                    }
                                }
                                //List<String> collect2 = trendsTableParamService.lambdaQuery().in(TrendsTableParam::getPId, collect1).list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());
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
                                    List<String> collect3 = new ArrayList<>();
                                    for(TrendsTableParam param:trendsTableParamList){
                                        for(String s:collect2){
                                            if(param.getPId().equals(s)){
                                                collect3.add(param.getId());
                                            }
                                        }
                                    }
                                    //List<String> collect3 = trendsTableParamService.lambdaQuery().in(TrendsTableParam::getPId, collect2).list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());
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
                            List<String> collect1 = trendsTableParamList.stream().filter(t->t.getPId().equals(byId.getPId())).map(TrendsTableParam::getId).collect(Collectors.toList());
                            //List<String> collect1 = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, byId.getPId()).list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());
                            List<String> collect1Son = new ArrayList<>();
                            for(TrendsTableParam param:trendsTableParamList){
                                for(String s:collect1){
                                    if(param.getPId().equals(s)){
                                        collect1Son.add(param.getId());
                                    }
                                }
                            }
                            //List<String> collect1Son = trendsTableParamService.lambdaQuery().in(TrendsTableParam::getPId, collect1).list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());
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
                        TrendsTableParam byId = JSONObject.parseObject((String)redisUtil.get("trendsTableParam:object:"+id),TrendsTableParam.class);
                        //TrendsTableParam byId = trendsTableParamService.getById(id);
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
                            TrendsTableParam byId1 = JSONObject.parseObject((String)redisUtil.get("trendsTableParam:object:"+total.getTableHeadId()),TrendsTableParam.class);
                            //TrendsTableParam byId1 = trendsTableParamService.getById(total.getTableHeadId());
                            if(!byId1.getPId().equals("0")){
                                TrendsTableParam one = JSONObject.parseObject((String)redisUtil.get("trendsTableParam:object:"+byId1.getPId()),TrendsTableParam.class);
                                //TrendsTableParam one = trendsTableParamService.getById(byId1.getPId());
                                List<PaymentWaterFees> list2 = paymentWaterFeesService.lambdaQuery().eq(PaymentWaterFees::getDel,0).
                                        eq(PaymentWaterFees::getYear, waterFeeStatisticsDetails.get(0).getYear()).
                                        eq(PaymentWaterFees::getWaterUserId, one.getId()).eq(PaymentWaterFees::getType,"水资源费").list();
                                if(null!=list2 && list2.size()>0){
                                    for(PaymentWaterFees fees:list2){
                                        paidWaterResource2+=fees.getPaymentAmount();
                                    }
                                    List<TrendsTableParam> totalTemp = trendsTableParamList.stream().filter(t -> t.getPId().equals(one.getId())).filter(t -> t.getParamName().equals("合计")).collect(Collectors.toList());
                                    TrendsTableParam totalBean = totalTemp.get(0);
                                    //TrendsTableParam totalBean = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, one.getId()).eq(TrendsTableParam::getParamName, "合计").one();
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
                            TrendsTableParam byId = JSONObject.parseObject((String)redisUtil.get("trendsTableParam:object:"+id),TrendsTableParam.class);
                            //TrendsTableParam byId = trendsTableParamService.getById(id);
                            if(!byId.getPId().equals("0")){
                                List<TrendsTableParam> tableParams = trendsTableParamList.stream().filter(t->t.getPId().equals(byId.getPId())).filter(t->!t.getParamName().equals("合计")).collect(Collectors.toList());
                                //List<TrendsTableParam> tableParams = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, byId.getPId()).ne(TrendsTableParam::getParamName, "合计").list();
                                for(TrendsTableParam param:tableParams){
                                    List<TrendsTableParam> listed = trendsTableParamList.stream().filter(t->t.getPId().equals(param.getId())).filter(t->!t.getParamName().equals("合计")).collect(Collectors.toList());
                                    //List<TrendsTableParam> listed = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, param.getId()).ne(TrendsTableParam::getParamName, "合计").list();
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
                            TrendsTableParam byId = JSONObject.parseObject((String)redisUtil.get("trendsTableParam:object:"+id),TrendsTableParam.class);
                            //TrendsTableParam byId = trendsTableParamService.getById(id);
                            if(byId.getPId().equals("0")){
                                List<String> collect1 = trendsTableParamList.stream().filter(t->t.getPId().equals(byId.getPId())).
                                        filter(t->t.getUseStation().equals(byId.getUseStation())).
                                        filter(t->t.getUseType()==byId.getUseType()).map(TrendsTableParam::getId).collect(Collectors.toList());
                                /*List<String> collect1 = trendsTableParamService.lambdaQuery().
                                        eq(TrendsTableParam::getPId, byId.getPId()).
                                        eq(TrendsTableParam::getUseStation,byId.getUseStation()).
                                        eq(TrendsTableParam::getUseType,byId.getUseType()).
                                        list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());*/
                                List<PaymentWaterFees> paymentWaterFees = paymentWaterFeesService.lambdaQuery().eq(PaymentWaterFees::getDel,0).
                                        eq(PaymentWaterFees::getYear, waterFeeStatisticsDetails.get(0).getYear()).
                                        in(PaymentWaterFees::getWaterUserId, collect1).eq(PaymentWaterFees::getType,"水资源费").list();
                                if(null != paymentWaterFees && paymentWaterFees.size()>0){
                                    for(PaymentWaterFees fees:paymentWaterFees){
                                        paidWaterResourceCount+=fees.getPaymentAmount();
                                    }
                                }
                                if(null != collect1 && collect1.size()>0){
                                    List<String> collect2= new ArrayList<>();
                                    for(TrendsTableParam param:trendsTableParamList){
                                        for(String s:collect1){
                                            if(param.getPId().equals(s)){
                                                collect2.add(param.getId());
                                            }
                                        }
                                    }
                                    //List<String> collect2 = trendsTableParamService.lambdaQuery().in(TrendsTableParam::getPId, collect1).list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());
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
                                        List<String> collect3 = new ArrayList<>();
                                        for(TrendsTableParam param:trendsTableParamList){
                                            for(String s:collect2){
                                                if(param.getPId().equals(s)){
                                                    collect3.add(param.getId());
                                                }
                                            }
                                        }
                                        //List<String> collect3 = trendsTableParamService.lambdaQuery().in(TrendsTableParam::getPId, collect2).list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());
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
                                List<String> collect1 = trendsTableParamList.stream().filter(t->t.getPId().equals(byId.getPId())).map(TrendsTableParam::getId).collect(Collectors.toList());
                                //List<String> collect1 = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, byId.getPId()).list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());
                                List<String> collect1Son = new ArrayList<>();
                                for (TrendsTableParam param:trendsTableParamList){
                                    for (String s:collect1){
                                        if(param.getPId().equals(s)){
                                            collect1Son.add(param.getId());
                                        }
                                    }
                                }
                                //List<String> collect1Son = trendsTableParamService.lambdaQuery().in(TrendsTableParam::getPId, collect1).list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());
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
                        TrendsTableParam byId1 = JSONObject.parseObject((String)redisUtil.get("trendsTableParam:object:"+total.getTableHeadId()),TrendsTableParam.class);
                        //TrendsTableParam byId1 = trendsTableParamService.getById(total.getTableHeadId());
                        if(!byId1.getPId().equals("0")){
                            TrendsTableParam one = JSONObject.parseObject((String)redisUtil.get("trendsTableParam:object:"+byId1.getPId()),TrendsTableParam.class);
                            //TrendsTableParam one = trendsTableParamService.getById(byId1.getPId());
                            List<PaymentWaterFees> list2 = paymentWaterFeesService.lambdaQuery().eq(PaymentWaterFees::getDel,0).
                                    eq(PaymentWaterFees::getYear, waterFeeStatisticsDetails.get(0).getYear()).
                                    eq(PaymentWaterFees::getWaterUserId, one.getId()).eq(PaymentWaterFees::getType,"水费").list();
                            if(null!=list2 && list2.size()>0){
                                for(PaymentWaterFees fees:list2){
                                    advancePaymentWaterFee2+=fees.getPaymentAmount();
                                }
                                List<TrendsTableParam> totalTemp = trendsTableParamList.stream().filter(t -> t.getPId().equals(one.getId())).filter(t -> t.getParamName().equals("合计")).collect(Collectors.toList());
                                TrendsTableParam totalBean = totalTemp.get(0);
                                //TrendsTableParam totalBean = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, one.getId()).eq(TrendsTableParam::getParamName, "合计").one();
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
                        TrendsTableParam byId = JSONObject.parseObject((String)redisUtil.get("trendsTableParam:object:"+id),TrendsTableParam.class);
                        //TrendsTableParam byId = trendsTableParamService.getById(id);
                        if(!byId.getPId().equals("0")){
                            List<TrendsTableParam> tableParams = trendsTableParamList.stream().filter(t->t.getPId().equals(byId.getPId())).filter(t->!t.getParamName().equals("合计")).collect(Collectors.toList());
                            //List<TrendsTableParam> tableParams = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, byId.getPId()).ne(TrendsTableParam::getParamName, "合计").list();
                            for(TrendsTableParam param:tableParams){
                                List<TrendsTableParam> listed = trendsTableParamList.stream().filter(t->t.getPId().equals(param.getId())).filter(t->!t.getParamName().equals("合计")).collect(Collectors.toList());
                                //List<TrendsTableParam> listed = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, param.getId()).ne(TrendsTableParam::getParamName, "合计").list();
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
                        TrendsTableParam byId = JSONObject.parseObject((String)redisUtil.get("trendsTableParam:object:"+id),TrendsTableParam.class);
                        //TrendsTableParam byId = trendsTableParamService.getById(id);
                        if(byId.getPId().equals("0")){
                            List<String> collect1 = trendsTableParamList.stream().filter(t->t.getPId().equals(byId.getPId())).
                                    filter(t->t.getUseStation().equals(byId.getUseStation())).
                                    filter(t->t.getUseType()==byId.getUseType()).map(TrendsTableParam::getId).collect(Collectors.toList());
                          /*  List<String> collect1 = trendsTableParamService.lambdaQuery().
                                    eq(TrendsTableParam::getPId, byId.getPId()).
                                    eq(TrendsTableParam::getUseStation,byId.getUseStation()).
                                    eq(TrendsTableParam::getUseType,byId.getUseType()).
                                    list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());*/
                            List<PaymentWaterFees> paymentWaterFees = paymentWaterFeesService.lambdaQuery().eq(PaymentWaterFees::getDel,0).
                                    eq(PaymentWaterFees::getYear, waterFeeStatisticsDetails.get(0).getYear()).
                                    in(PaymentWaterFees::getWaterUserId, collect1).eq(PaymentWaterFees::getType,"水费").list();
                            if(null != paymentWaterFees && paymentWaterFees.size()>0){
                                for(PaymentWaterFees fees:paymentWaterFees){
                                    advancePaymentWaterFeeCount+=fees.getPaymentAmount();
                                }
                            }
                            if(null != collect1 && collect1.size()>0){
                                List<String> collect2= new ArrayList<>();
                                for(TrendsTableParam param:trendsTableParamList){
                                    for(String s:collect1){
                                        if(param.getPId().equals(s)){
                                            collect2.add(param.getId());
                                        }
                                    }
                                }
                                //List<String> collect2 = trendsTableParamService.lambdaQuery().in(TrendsTableParam::getPId, collect1).list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());
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
                                    List<String> collect3 = new ArrayList<>();
                                    for(TrendsTableParam param:trendsTableParamList){
                                        for(String s:collect2){
                                            if(param.getPId().equals(s)){
                                                collect3.add(param.getId());
                                            }
                                        }
                                    }
                                    //List<String> collect3 = trendsTableParamService.lambdaQuery().in(TrendsTableParam::getPId, collect2).list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());
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
                            List<String> collect1 = trendsTableParamList.stream().filter(t->t.getPId().equals(byId.getPId())).map(TrendsTableParam::getId).collect(Collectors.toList());
                            //List<String> collect1 = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, byId.getPId()).list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());
                            List<String> collect1Son = new ArrayList<>();
                            for (TrendsTableParam param:trendsTableParamList){
                                for (String s:collect1){
                                    if(param.getPId().equals(s)){
                                        collect1Son.add(param.getId());
                                    }
                                }
                            }
                            //List<String> collect1Son = trendsTableParamService.lambdaQuery().in(TrendsTableParam::getPId, collect1).list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());
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
                        TrendsTableParam byId = JSONObject.parseObject((String)redisUtil.get("trendsTableParam:object:"+id),TrendsTableParam.class);
                        //TrendsTableParam byId = trendsTableParamService.getById(id);
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
        String mk = (String) redisUtil.get("trendsTableParam:list");
        if(StringUtils.isEmpty(mk)){
            updateCache();
            mk = (String) redisUtil.get("trendsTableParam:list");
        }
        List<TrendsTableParam> trendsTableParamListTemp = JSONObject.parseArray(mk, TrendsTableParam.class);
        List<TrendsTableParam> trendsTableParamList = trendsTableParamListTemp.stream().filter(t -> t.getUseType() == 2).collect(Collectors.toList());
        try {
            String station = waterFeeStatisticsDetails.get(0).getStation();
            List<TotalIdToStation> list = totalIdToStationService.lambdaQuery().eq(TotalIdToStation::getUseType, 2).eq(TotalIdToStation::getStation, waterFeeStatisticsDetails.get(0).getStation()).list();
            if(null != list && list.size()>0){
                Double total = 0.0;
                List<String> collect = list.stream().filter(t->t.getName().equals("合计")).map(TotalIdToStation::getTotalId).collect(Collectors.toList());
                for(String id:collect){
                    Double value = 0.0;
                    TrendsTableParam tableParam = JSONObject.parseObject((String)redisUtil.get("trendsTableParam:object:"+id),TrendsTableParam.class);
                    if(!tableParam.getPId().equals("0")){
                        List<TrendsTableParam> noTotalList = trendsTableParamList.stream().filter(t->t.getPId().equals(tableParam.getPId())).filter(t->!t.getParamName().equals("合计")).collect(Collectors.toList());
                        //List<TrendsTableParam> noTotalList = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, tableParam.getPId()).ne(TrendsTableParam::getParamName, "合计").list();
                        for(TrendsTableParam param:noTotalList){
                            for(WaterFeeStatisticsDetails t:waterFeeStatisticsDetails){
                                if(t.getTableHeadId().equals(param.getId())){
                                    value+=t.getV()==null?0.0:t.getV();
                                }
                                List<TrendsTableParam> listed = trendsTableParamList.stream().filter(r->r.getPId().equals(param.getId())).filter(r->!r.getParamName().equals("合计")).collect(Collectors.toList());
                                //List<TrendsTableParam> listed = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, param.getId()).ne(TrendsTableParam::getParamName, "合计").list();
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
                TrendsTableParam one =null;
                List<TrendsTableParam> collect1 = trendsTableParamList.stream().filter(s -> s.getPId().equals("0")).filter(s -> s.getUseType() == 2).collect(Collectors.toList());
                for(TrendsTableParam param : collect1){
                    for(String s:collect){
                        if(param.getId().equals(s)){
                            one = param;
                        }
                    }
                }
                //TrendsTableParam one = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, "0").eq(TrendsTableParam::getUseType,2).in(TrendsTableParam::getId, collect).one();
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
                    String paramName = (String)redisUtil.get("trendsTableParam:name:"+details.getTableHeadId());
                    //String paramName = trendsTableParamService.getById(details.getTableHeadId()).getParamName();
                    if(!paramName.equals("合计") && !paramName.equals("来水") && !paramName.equals("引水") && !paramName.equals("泄洪") && (paramName.equals("东干") || paramName.equals("西干") || paramName.equals("漏斗"))){
                        zonggan += details.getV()==null?0.0:details.getV();
                    }
                }
                for(WaterFeeStatisticsDetails details:waterFeeStatisticsDetails){
                    String paramName = (String)redisUtil.get("trendsTableParam:name:"+details.getTableHeadId());
                    //String paramName = trendsTableParamService.getById(details.getTableHeadId()).getParamName();
                    if(paramName.equals("总干")){
                        details.setV(zonggan);
                    }
                }

                for(WaterFeeStatisticsDetails details:waterFeeStatisticsDetails){
                    String paramName = (String)redisUtil.get("trendsTableParam:name:"+details.getTableHeadId());
                    //String paramName = trendsTableParamService.getById(details.getTableHeadId()).getParamName();
                    if(!paramName.equals("合计") && !paramName.equals("来水") && !paramName.equals("引水") && !paramName.equals("总干") && !paramName.equals("泄洪")){
                        yinshui += details.getV()==null?0.0:details.getV();
                    }
                }
                for(WaterFeeStatisticsDetails details:waterFeeStatisticsDetails){
                    String paramName = (String)redisUtil.get("trendsTableParam:name:"+details.getTableHeadId());
                    //String paramName = trendsTableParamService.getById(details.getTableHeadId()).getParamName();
                    if(paramName.equals("引水")){
                        details.setV(yinshui);
                    }
                }

                for(WaterFeeStatisticsDetails details:waterFeeStatisticsDetails){
                    String paramName = (String)redisUtil.get("trendsTableParam:name:"+details.getTableHeadId());
                    //String paramName = trendsTableParamService.getById(details.getTableHeadId()).getParamName();
                    if(!paramName.equals("合计") && (paramName.equals("引水") || paramName.equals("泄洪"))){
                        laishui += details.getV()==null?0.0:details.getV();
                    }
                }
                for(WaterFeeStatisticsDetails details:waterFeeStatisticsDetails){
                    String paramName = (String)redisUtil.get("trendsTableParam:name:"+details.getTableHeadId());
                    //String paramName = trendsTableParamService.getById(details.getTableHeadId()).getParamName();
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
        String mk = (String) redisUtil.get("trendsTableParam:list");
        if(StringUtils.isEmpty(mk)){
            updateCache();
            mk = (String) redisUtil.get("trendsTableParam:list");
        }
        List<TrendsTableParam> trendsTableParamListTemp = JSONObject.parseArray(mk, TrendsTableParam.class);
        List<TrendsTableParam> trendsTableParamList = trendsTableParamListTemp.stream().filter(t -> t.getUseType() == 2).collect(Collectors.toList());
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
                        TrendsTableParam byId1 = JSONObject.parseObject((String)redisUtil.get("trendsTableParam:object:"+total.getTableHeadId()),TrendsTableParam.class);
                        //TrendsTableParam byId1 = trendsTableParamService.getById(total.getTableHeadId());
                        if(!byId1.getPId().equals("0")){
                            TrendsTableParam one = JSONObject.parseObject((String)redisUtil.get("trendsTableParam:object:"+byId1.getPId()),TrendsTableParam.class);
                            //TrendsTableParam one = trendsTableParamService.getById(byId1.getPId());
                            List<PaymentWaterFees> list2 = paymentWaterFeesService.lambdaQuery().eq(PaymentWaterFees::getDel,0).
                                    eq(PaymentWaterFees::getYear, waterFeeStatisticsDetails.get(0).getYear()).
                                    eq(PaymentWaterFees::getWaterUserId, one.getId()).eq(PaymentWaterFees::getType,"水资源费").list();
                            if(null!=list2 && list2.size()>0){
                                for(PaymentWaterFees fees:list2){
                                    paidWaterResource2+=fees.getPaymentAmount();
                                }
                                List<TrendsTableParam> totalTemp = trendsTableParamList.stream().filter(r -> r.getPId().equals(one.getId())).filter(r -> r.getParamName().equals("合计")).collect(Collectors.toList());
                                TrendsTableParam totalBean = totalTemp.get(0);
                                //TrendsTableParam totalBean = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, one.getId()).eq(TrendsTableParam::getParamName, "合计").one();
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
                        TrendsTableParam byId = JSONObject.parseObject((String)redisUtil.get("trendsTableParam:object:"+id),TrendsTableParam.class);
                        //TrendsTableParam byId = trendsTableParamService.getById(id);
                        if(!byId.getPId().equals("0")){
                            List<TrendsTableParam> tableParams = trendsTableParamList.stream().filter(t->t.getPId().equals(byId.getPId())).filter(t->!t.getParamName().equals("合计")).collect(Collectors.toList());
                            //List<TrendsTableParam> tableParams = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, byId.getPId()).ne(TrendsTableParam::getParamName, "合计").list();
                            for(TrendsTableParam param:tableParams){
                                List<TrendsTableParam> listed = trendsTableParamList.stream().filter(t->t.getPId().equals(param.getId())).filter(t->!t.getParamName().equals("合计")).collect(Collectors.toList());
                                //List<TrendsTableParam> listed = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, param.getId()).ne(TrendsTableParam::getParamName, "合计").list();
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
                        TrendsTableParam byId = JSONObject.parseObject((String)redisUtil.get("trendsTableParam:object:"+id),TrendsTableParam.class);
                        //TrendsTableParam byId = trendsTableParamService.getById(id);
                        if(byId.getPId().equals("0")){
                            List<String> collect1 = trendsTableParamList.stream().filter(t->t.getPId().equals(byId.getPId())).
                                    filter(t->t.getUseStation().equals(byId.getUseStation())).
                                    filter(t->t.getUseType()==byId.getUseType()).collect(Collectors.toList()).stream().map(TrendsTableParam::getId).collect(Collectors.toList());
                           /* List<String> collect1 = trendsTableParamService.lambdaQuery().
                                    eq(TrendsTableParam::getPId, byId.getPId()).
                                    eq(TrendsTableParam::getUseStation,byId.getUseStation()).
                                    eq(TrendsTableParam::getUseType,byId.getUseType()).
                                    list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());*/
                            List<PaymentWaterFees> paymentWaterFees = paymentWaterFeesService.lambdaQuery().eq(PaymentWaterFees::getDel,0).
                                    eq(PaymentWaterFees::getYear, waterFeeStatisticsDetails.get(0).getYear()).
                                    in(PaymentWaterFees::getWaterUserId, collect1).eq(PaymentWaterFees::getType,"水资源费").list();
                            if(null != paymentWaterFees && paymentWaterFees.size()>0){
                                for(PaymentWaterFees fees:paymentWaterFees){
                                    paidWaterResourceCount+=fees.getPaymentAmount();
                                }
                            }
                            if(null != collect1 && collect1.size()>0){
                                List<String> collect2 = new ArrayList<>();
                                for(TrendsTableParam param :trendsTableParamList){
                                    for(String s:collect1){
                                        if(param.getPId().equals(s)){
                                            collect2.add(param.getId());
                                        }
                                    }
                                }
                                //List<String> collect2 = trendsTableParamService.lambdaQuery().in(TrendsTableParam::getPId, collect1).list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());
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
                                    List<String> collect3 = new ArrayList<>();
                                    for(TrendsTableParam param :trendsTableParamList){
                                        for(String s:collect2){
                                            if(param.getPId().equals(s)){
                                                collect3.add(param.getId());
                                            }
                                        }
                                    }
                                    //List<String> collect3 = trendsTableParamService.lambdaQuery().in(TrendsTableParam::getPId, collect2).list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());
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
                            List<String> collect1 = trendsTableParamList.stream().filter(t->t.getPId().equals(byId.getPId())).map(TrendsTableParam::getId).collect(Collectors.toList());
                            //List<String> collect1 = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, byId.getPId()).list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());
                            List<String> collect1Son = new ArrayList<>();
                            for(TrendsTableParam param :trendsTableParamList){
                                for(String s:collect1){
                                    if(param.getPId().equals(s)){
                                        collect1Son.add(param.getId());
                                    }
                                }
                            }
                            //List<String> collect1Son = trendsTableParamService.lambdaQuery().in(TrendsTableParam::getPId, collect1).list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());
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
                    TrendsTableParam byId1 = JSONObject.parseObject((String)redisUtil.get("trendsTableParam:object:"+total.getTableHeadId()),TrendsTableParam.class);
                    //TrendsTableParam byId1 = trendsTableParamService.getById(total.getTableHeadId());
                    if(!byId1.getPId().equals("0")){
                        TrendsTableParam one = JSONObject.parseObject((String)redisUtil.get("trendsTableParam:object:"+byId1.getPId()),TrendsTableParam.class);
                        //TrendsTableParam one = trendsTableParamService.getById(byId1.getPId());
                        List<PaymentWaterFees> list2 = paymentWaterFeesService.lambdaQuery().eq(PaymentWaterFees::getDel,0).
                                eq(PaymentWaterFees::getYear, waterFeeStatisticsDetails.get(0).getYear()).
                                eq(PaymentWaterFees::getWaterUserId, one.getId()).eq(PaymentWaterFees::getType,"水费").list();
                        if(null!=list2 && list2.size()>0){
                            for(PaymentWaterFees fees:list2){
                                advancePaymentWaterFee2+=fees.getPaymentAmount();
                            }
                            List<TrendsTableParam> totalTemp = trendsTableParamList.stream().filter(t -> t.getPId().equals(one.getId())).filter(t -> t.getParamName().equals("合计")).collect(Collectors.toList());
                            TrendsTableParam totalBean = totalTemp.get(0);
                            //TrendsTableParam totalBean = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, one.getId()).eq(TrendsTableParam::getParamName, "合计").one();
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
                    TrendsTableParam byId = JSONObject.parseObject((String)redisUtil.get("trendsTableParam:object:"+id),TrendsTableParam.class);
                    //TrendsTableParam byId = trendsTableParamService.getById(id);
                    if(!byId.getPId().equals("0")){
                        List<TrendsTableParam> tableParams = trendsTableParamList.stream().filter(t->t.getPId().equals(byId.getPId())).filter(t->!t.getParamName().equals("合计")).collect(Collectors.toList());
                        //List<TrendsTableParam> tableParams = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, byId.getPId()).ne(TrendsTableParam::getParamName, "合计").list();
                        for(TrendsTableParam param:tableParams){
                            List<TrendsTableParam> listed = trendsTableParamList.stream().filter(t->t.getPId().equals(param.getId())).filter(t->!t.getParamName().equals("合计")).collect(Collectors.toList());
                            //List<TrendsTableParam> listed = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, param.getId()).ne(TrendsTableParam::getParamName, "合计").list();
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
                    TrendsTableParam byId = JSONObject.parseObject((String)redisUtil.get("trendsTableParam:object:"+id),TrendsTableParam.class);
                    //TrendsTableParam byId = trendsTableParamService.getById(id);
                    if(byId.getPId().equals("0")){
                        List<String> collect1 = trendsTableParamList.stream().filter(t->t.getPId().equals(byId.getPId())).
                                filter(t->t.getUseStation().equals(byId.getUseStation())).
                                filter(t->t.getUseType()==byId.getUseType()).map(TrendsTableParam::getId).collect(Collectors.toList());
                        /*List<String> collect1 = trendsTableParamService.lambdaQuery().
                                eq(TrendsTableParam::getPId, byId.getPId()).
                                eq(TrendsTableParam::getUseStation,byId.getUseStation()).
                                eq(TrendsTableParam::getUseType,byId.getUseType()).
                                list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());*/
                        List<PaymentWaterFees> paymentWaterFees = paymentWaterFeesService.lambdaQuery().eq(PaymentWaterFees::getDel,0).
                                eq(PaymentWaterFees::getYear, waterFeeStatisticsDetails.get(0).getYear()).
                                in(PaymentWaterFees::getWaterUserId, collect1).eq(PaymentWaterFees::getType,"水费").list();
                        if(null != paymentWaterFees && paymentWaterFees.size()>0){
                            for(PaymentWaterFees fees:paymentWaterFees){
                                advancePaymentWaterFeeCount+=fees.getPaymentAmount();
                            }
                        }
                        if(null != collect1 && collect1.size()>0){
                            List<String> collect2 = new ArrayList<>();
                            for(TrendsTableParam param:trendsTableParamList){
                                for(String s:collect1){
                                    if(param.getPId().equals(s)){
                                        collect2.add(param.getId());
                                    }
                                }
                            }
                            //List<String> collect2 = trendsTableParamService.lambdaQuery().in(TrendsTableParam::getPId, collect1).list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());
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
                                List<String> collect3 = new ArrayList<>();
                                for(TrendsTableParam param:trendsTableParamList){
                                    for(String s:collect2){
                                        if(param.getPId().equals(s)){
                                            collect3.add(param.getId());
                                        }
                                    }
                                }
                                //List<String> collect3 = trendsTableParamService.lambdaQuery().in(TrendsTableParam::getPId, collect2).list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());
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
                        List<String> collect1 = trendsTableParamList.stream().filter(t->t.getPId().equals(byId.getPId())).map(TrendsTableParam::getId).collect(Collectors.toList());
                        //List<String> collect1 = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, byId.getPId()).list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());
                        List<String> collect1Son = new ArrayList<>();
                        for(TrendsTableParam param:trendsTableParamList){
                            for(String s:collect1){
                                if(param.getPId().equals(s)){
                                    collect1Son.add(param.getId());
                                }
                            }
                        }
                        //List<String> collect1Son = trendsTableParamService.lambdaQuery().in(TrendsTableParam::getPId, collect1).list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());
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
                    TrendsTableParam byId = JSONObject.parseObject((String)redisUtil.get("trendsTableParam:object:"+id),TrendsTableParam.class);
                    //TrendsTableParam byId = trendsTableParamService.getById(id);
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
            for(int i=11;i<=20;i++){
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

    @Override
    public void updateCache(){
        List<TrendsTableParam> listed = trendsTableParamService.list();
        redisUtil.set("trendsTableParam:list", JSONObject.toJSONString(listed));
        for (TrendsTableParam param:listed){
            redisUtil.set("trendsTableParam:name:"+param.getId(), param.getParamName());
            redisUtil.set("trendsTableParam:object:"+param.getId(), JSONObject.toJSONString(param));
        }
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public RestResponse addHistory(List<List<WaterFeeStatisticsDetails>> waterFeeStatisticsDetailsList) {
        WaterFeeStatisticsDetails details1 = waterFeeStatisticsDetailsList.get(0).get(0);
        WaterFeeStatisticsDetails details2 = waterFeeStatisticsDetailsList.get(waterFeeStatisticsDetailsList.size()-1).get(0);
        String flag = (String) redisUtil.get("waterFee:"+details1.getStation()+details1.getYear()+details1.getMonth()+details1.getTenDays());
        if(StringUtils.isEmpty(flag)){
            redisUtil.set("waterFee:"+details1.getStation()+details1.getYear()+details1.getMonth()+details1.getTenDays(),"1");
        }else {
            return RestResponse.no("已有用户在创建该旬表格，请勿重复创建");
        }
        String mk = (String) redisUtil.get("trendsTableParam:list");
        if(StringUtils.isEmpty(mk)){
            updateCache();
            mk = (String) redisUtil.get("trendsTableParam:list");
        }
        List<TrendsTableParam> trendsTableParamListTemp = JSONObject.parseArray(mk, TrendsTableParam.class);
        List<TrendsTableParam> trendsTableParamList = trendsTableParamListTemp.stream().filter(t -> t.getUseType() == 2).collect(Collectors.toList());
        String start = details1.getYear()+"-"+details1.getStatisticsDate().substring(0,2)+"-"+details1.getStatisticsDate().substring(3,5);
        String end = details2.getYear()+"-"+details2.getStatisticsDate().substring(0,2)+"-"+details2.getStatisticsDate().substring(3,5);
        List<TrendsTableParam> collect5 = trendsTableParamList.stream().filter(t -> t.getUseType() == 2).filter(t -> t.getUseStation().equals(details1.getStation())).collect(Collectors.toList());
        List<String> tableHeadName = getTableHeadName(collect5);
        long ss = System.currentTimeMillis();
        List<IrrigatedPlatformDataInfo> list3 = irrigatedPlatformDataInfoService.lambdaQuery().in(IrrigatedPlatformDataInfo::getMonitorName,tableHeadName).between(IrrigatedPlatformDataInfo::getMonitorTime,start+" 00:00:00",end+" 23:59:59").list();
        long ee = System.currentTimeMillis();
        log.warn("方法耗时：" + (ee - ss) + "ms");
        for(List<WaterFeeStatisticsDetails> waterFeeStatisticsDetails:waterFeeStatisticsDetailsList){
            String station = waterFeeStatisticsDetails.get(0).getStation();
            String dateTemp = waterFeeStatisticsDetails.get(0).getYear()+"-"+waterFeeStatisticsDetails.get(0).getStatisticsDate().substring(0,2)+"-"+waterFeeStatisticsDetails.get(0).getStatisticsDate().substring(3,5);
            if(station.equals("渠首管理站")){
                waterFeeStatisticsDetails.forEach(t->{
                    Map<String, Double> lanternCanalInfoByDate = getLanternCanalInfoByDate(t.getYear(),t.getMonth(),t.getTenDays(),t.getStatisticsDate());
                    String paramName = (String)redisUtil.get("trendsTableParam:name:"+t.getTableHeadId());
                    if(paramName.equals("农业")){
                        t.setV(lanternCanalInfoByDate ==null?null:lanternCanalInfoByDate.get("agricultureValue")==null?null:lanternCanalInfoByDate.get("agricultureValue"));
                    }
                    if(paramName.equals("工业")){
                        t.setV(lanternCanalInfoByDate ==null?null:lanternCanalInfoByDate.get("industryValue")==null?null:lanternCanalInfoByDate.get("industryValue"));
                    }
                    if(paramName.equals("绿化")){
                        t.setV(lanternCanalInfoByDate ==null?null:lanternCanalInfoByDate.get("greenValue")==null?null:lanternCanalInfoByDate.get("greenValue"));
                    }
                    t.setId(UUIDUtils.getUUID());
                });
            }else {
                waterFeeStatisticsDetails.forEach(t->{
                    if(!sdf.format(new Date()).equals(dateTemp)){
                        String paramName = (String)redisUtil.get("trendsTableParam:name:"+t.getTableHeadId());
                        if(!paramName.equals("合计")){
                            String tableParamString = (String)redisUtil.get("trendsTableParam:object:"+t.getTableHeadId());
                            TrendsTableParam tableParam = JSONObject.parseObject(tableParamString, TrendsTableParam.class);
                            if(t.getStation().contains("河东")){
                                Double v = (Double)redisUtil.get("A3:data:hd:yesterday:"+dateTemp+":"+tableParam.getUnitId());
                                t.setV(v==null?null:v);
                            }
                            if(t.getStation().contains("河西")){
                                Double v = (Double)redisUtil.get("A3:data:hx:yesterday:"+dateTemp+":"+tableParam.getUnitId());
                                t.setV(v==null?null:v);
                            }
                            if(t.getStation().contains("渠首")){
                                Double v = (Double)redisUtil.get("A3:data:qs:yesterday:"+dateTemp+":"+tableParam.getUnitId());
                                t.setV(v==null?null:v);
                            }
                        }
                    }
                    try {
                        t.setRecordTime(sdf.parse(dateTemp));
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
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
                    TrendsTableParam tableParam = JSONObject.parseObject((String)redisUtil.get("trendsTableParam:object:"+id),TrendsTableParam.class);
                    if(!tableParam.getPId().equals("0")){
                        List<TrendsTableParam> noTotalList = trendsTableParamList.stream().filter(t -> t.getPId().equals(tableParam.getPId())).filter(t -> !t.getParamName().equals("合计")).collect(Collectors.toList());
                        //List<TrendsTableParam> noTotalList = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, tableParam.getPId()).ne(TrendsTableParam::getParamName, "合计").list();
                        for(TrendsTableParam param:noTotalList){
                            for(WaterFeeStatisticsDetails t:waterFeeStatisticsDetails){
                                if(t.getTableHeadId().equals(param.getId())){
                                    value+=t.getV()==null?0.0:t.getV();
                                }
                                List<TrendsTableParam> listed = trendsTableParamList.stream().filter(s -> s.getPId().equals(param.getId())).filter(s -> !s.getParamName().equals("合计")).collect(Collectors.toList());
                                //List<TrendsTableParam> listed = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, param.getId()).ne(TrendsTableParam::getParamName, "合计").list();
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
                TrendsTableParam one =null;
                List<TrendsTableParam> collect1 = trendsTableParamList.stream().filter(s -> s.getPId().equals("0")).filter(s -> s.getUseType() == 2).collect(Collectors.toList());
                for(TrendsTableParam param : collect1){
                    for(String s:collect){
                        if(param.getId().equals(s)){
                            one = param;
                        }
                    }
                }
                //TrendsTableParam one = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, "0").eq(TrendsTableParam::getUseType,2).in(TrendsTableParam::getId, collect).one();
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
                    String paramName = (String)redisUtil.get("trendsTableParam:name:"+details.getTableHeadId());
                    //String paramName = trendsTableParamService.getById(details.getTableHeadId()).getParamName();
                    if(!paramName.equals("合计") && !paramName.equals("来水") && !paramName.equals("引水") && !paramName.equals("泄洪") && (paramName.equals("东干") || paramName.equals("西干") || paramName.equals("漏斗"))){
                        zonggan += details.getV()==null?0.0:details.getV();
                    }
                }
                for(WaterFeeStatisticsDetails details:waterFeeStatisticsDetails){
                    String paramName = (String)redisUtil.get("trendsTableParam:name:"+details.getTableHeadId());
                    //String paramName = trendsTableParamService.getById(details.getTableHeadId()).getParamName();
                    if(paramName.equals("总干")){
                        details.setV(zonggan);
                    }
                }

                for(WaterFeeStatisticsDetails details:waterFeeStatisticsDetails){
                    String paramName = (String)redisUtil.get("trendsTableParam:name:"+details.getTableHeadId());
                    //String paramName = trendsTableParamService.getById(details.getTableHeadId()).getParamName();
                    if(!paramName.equals("合计") && !paramName.equals("来水") && !paramName.equals("引水") && !paramName.equals("总干") && !paramName.equals("泄洪")){
                        yinshui += details.getV()==null?0.0:details.getV();
                    }
                }
                for(WaterFeeStatisticsDetails details:waterFeeStatisticsDetails){
                    String paramName = (String)redisUtil.get("trendsTableParam:name:"+details.getTableHeadId());
                    //String paramName = trendsTableParamService.getById(details.getTableHeadId()).getParamName();
                    if(paramName.equals("引水")){
                        details.setV(yinshui);
                    }
                }

                for(WaterFeeStatisticsDetails details:waterFeeStatisticsDetails){
                    String paramName = (String)redisUtil.get("trendsTableParam:name:"+details.getTableHeadId());
                    //String paramName = trendsTableParamService.getById(details.getTableHeadId()).getParamName();
                    if(!paramName.equals("合计") && (paramName.equals("引水") || paramName.equals("泄洪"))){
                        laishui += details.getV()==null?0.0:details.getV();
                    }
                }
                for(WaterFeeStatisticsDetails details:waterFeeStatisticsDetails){
                    String paramName = (String)redisUtil.get("trendsTableParam:name:"+details.getTableHeadId());
                    //String paramName = trendsTableParamService.getById(details.getTableHeadId()).getParamName();
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
                                TrendsTableParam byId1 = JSONObject.parseObject((String)redisUtil.get("trendsTableParam:object:"+total.getTableHeadId()),TrendsTableParam.class);
                                //TrendsTableParam byId1 = trendsTableParamService.getById(total.getTableHeadId());
                                if(!byId1.getPId().equals("0")){
                                    TrendsTableParam one = JSONObject.parseObject((String)redisUtil.get("trendsTableParam:object:"+byId1.getPId()),TrendsTableParam.class);
                                    //TrendsTableParam one = trendsTableParamService.getById(byId1.getPId());
                                    List<PaymentWaterFees> list2 = paymentWaterFeesService.lambdaQuery().eq(PaymentWaterFees::getDel,0).
                                            eq(PaymentWaterFees::getYear, waterFeeStatisticsDetails.get(0).getYear()).
                                            eq(PaymentWaterFees::getWaterUserId, one.getId()).eq(PaymentWaterFees::getType,"水资源费").list();
                                    if(null!=list2 && list2.size()>0){
                                        for(PaymentWaterFees fees:list2){
                                            paidWaterResource2+=fees.getPaymentAmount();
                                        }
                                        List<TrendsTableParam> totalTemp = trendsTableParamList.stream().filter(r -> r.getPId().equals(one.getId())).filter(r -> r.getParamName().equals("合计")).collect(Collectors.toList());
                                        TrendsTableParam totalBean = totalTemp.get(0);
                                        //TrendsTableParam totalBean = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, one.getId()).eq(TrendsTableParam::getParamName, "合计").one();
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
                                TrendsTableParam byId = JSONObject.parseObject((String)redisUtil.get("trendsTableParam:object:"+id),TrendsTableParam.class);
                                //TrendsTableParam byId = trendsTableParamService.getById(id);
                                if(!byId.getPId().equals("0")){
                                    List<TrendsTableParam> tableParams = trendsTableParamList.stream().filter(r -> r.getPId().equals(byId.getPId())).filter(r -> !r.getParamName().equals("合计")).collect(Collectors.toList());
                                    //List<TrendsTableParam> tableParams = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, byId.getPId()).ne(TrendsTableParam::getParamName, "合计").list();
                                    for(TrendsTableParam param:tableParams){
                                        List<TrendsTableParam> listed = trendsTableParamList.stream().filter(r -> r.getPId().equals(param.getId())).filter(r -> !r.getParamName().equals("合计")).collect(Collectors.toList());
                                        //List<TrendsTableParam> listed = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, param.getId()).ne(TrendsTableParam::getParamName, "合计").list();
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
                                TrendsTableParam byId =JSONObject.parseObject((String)redisUtil.get("trendsTableParam:object:"+id),TrendsTableParam.class);
                                //TrendsTableParam byId = trendsTableParamService.getById(id);
                                if(byId.getPId().equals("0")){
                                    List<String> collect1 = trendsTableParamList.stream().filter(t->t.getPId().equals(byId.getPId())).
                                            filter(t->t.getUseStation().equals(byId.getUseStation())).
                                            filter(t->t.getUseType()==byId.getUseType()).collect(Collectors.toList()).stream().map(TrendsTableParam::getId).collect(Collectors.toList());
                               /* List<String> collect1 = trendsTableParamService.lambdaQuery().
                                        eq(TrendsTableParam::getPId, byId.getPId()).
                                        eq(TrendsTableParam::getUseStation,byId.getUseStation()).
                                        eq(TrendsTableParam::getUseType,byId.getUseType()).
                                        list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());*/
                                    List<PaymentWaterFees> paymentWaterFees = paymentWaterFeesService.lambdaQuery().eq(PaymentWaterFees::getDel,0).
                                            eq(PaymentWaterFees::getYear, waterFeeStatisticsDetails.get(0).getYear()).
                                            in(PaymentWaterFees::getWaterUserId, collect1).eq(PaymentWaterFees::getType,"水资源费").list();
                                    if(null != paymentWaterFees && paymentWaterFees.size()>0){
                                        for(PaymentWaterFees fees:paymentWaterFees){
                                            paidWaterResourceCount+=fees.getPaymentAmount();
                                        }
                                    }
                                    if(null != collect1 && collect1.size()>0){
                                        List<String> collect2 = new ArrayList<>();
                                        for(TrendsTableParam param :trendsTableParamList){
                                            for(String s:collect1){
                                                if(param.getPId().equals(s)){
                                                    collect2.add(param.getId());
                                                }
                                            }
                                        }
                                        //List<String> collect2 = trendsTableParamService.lambdaQuery().in(TrendsTableParam::getPId, collect1).list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());
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
                                            List<String> collect3 = new ArrayList<>();
                                            for(TrendsTableParam param :trendsTableParamList){
                                                for(String s:collect2){
                                                    if(param.getPId().equals(s)){
                                                        collect3.add(param.getId());
                                                    }
                                                }
                                            }
                                            //List<String> collect3 = trendsTableParamService.lambdaQuery().in(TrendsTableParam::getPId, collect2).list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());
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
                                    List<String> collect1 = trendsTableParamList.stream().filter(t->t.getPId().equals(byId.getPId())).map(TrendsTableParam::getId).collect(Collectors.toList());
                                    //List<String> collect1 = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, byId.getPId()).list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());
                                    List<String> collect1Son = new ArrayList<>();
                                    for(TrendsTableParam param :trendsTableParamList){
                                        for(String s:collect1){
                                            if(param.getPId().equals(s)){
                                                collect1Son.add(param.getId());
                                            }
                                        }
                                    }
                                    //List<String> collect1Son = trendsTableParamService.lambdaQuery().in(TrendsTableParam::getPId, collect1).list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());
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
                            TrendsTableParam byId1 = JSONObject.parseObject((String)redisUtil.get("trendsTableParam:object:"+total.getTableHeadId()),TrendsTableParam.class);
                            //TrendsTableParam byId1 = trendsTableParamService.getById(total.getTableHeadId());
                            if(!byId1.getPId().equals("0")){
                                TrendsTableParam one = JSONObject.parseObject((String)redisUtil.get("trendsTableParam:object:"+byId1.getPId()),TrendsTableParam.class);
                                //TrendsTableParam one = trendsTableParamService.getById(byId1.getPId());
                                List<PaymentWaterFees> list2 = paymentWaterFeesService.lambdaQuery().eq(PaymentWaterFees::getDel,0).
                                        eq(PaymentWaterFees::getYear, waterFeeStatisticsDetails.get(0).getYear()).
                                        eq(PaymentWaterFees::getWaterUserId, one.getId()).eq(PaymentWaterFees::getType,"水费").list();
                                if(null!=list2 && list2.size()>0){
                                    for(PaymentWaterFees fees:list2){
                                        advancePaymentWaterFee2+=fees.getPaymentAmount();
                                    }
                                    List<TrendsTableParam> totalTemp = trendsTableParamList.stream().filter(t -> t.getPId().equals(one.getId())).filter(t -> t.getParamName().equals("合计")).collect(Collectors.toList());
                                    TrendsTableParam totalBean = totalTemp.get(0);
                                    //TrendsTableParam totalBean = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, one.getId()).eq(TrendsTableParam::getParamName, "合计").one();
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
                            TrendsTableParam byId = JSONObject.parseObject((String)redisUtil.get("trendsTableParam:object:"+id),TrendsTableParam.class);
                            //TrendsTableParam byId = trendsTableParamService.getById(id);
                            if(!byId.getPId().equals("0")){
                                List<TrendsTableParam> tableParams = trendsTableParamList.stream().filter(t->t.getPId().equals(byId.getPId())).filter(t->!t.getParamName().equals("合计")).collect(Collectors.toList());
                                //List<TrendsTableParam> tableParams = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, byId.getPId()).ne(TrendsTableParam::getParamName, "合计").list();
                                for(TrendsTableParam param:tableParams){
                                    List<TrendsTableParam> listed = trendsTableParamList.stream().filter(t->t.getPId().equals(param.getId())).filter(t->!t.getParamName().equals("合计")).collect(Collectors.toList());
                                    //List<TrendsTableParam> listed = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, param.getId()).ne(TrendsTableParam::getParamName, "合计").list();
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
                            TrendsTableParam byId = JSONObject.parseObject((String)redisUtil.get("trendsTableParam:object:"+id),TrendsTableParam.class);
                            //TrendsTableParam byId = trendsTableParamService.getById(id);
                            if(byId.getPId().equals("0")){
                                List<String> collect1 = trendsTableParamList.stream().filter(t->t.getPId().equals(byId.getPId())).
                                        filter(t->t.getUseStation().equals(byId.getUseStation())).
                                        filter(t->t.getUseType()==byId.getUseType()).map(TrendsTableParam::getId).collect(Collectors.toList());
                            /*List<String> collect1 = trendsTableParamService.lambdaQuery().
                                    eq(TrendsTableParam::getPId, byId.getPId()).
                                    eq(TrendsTableParam::getUseStation,byId.getUseStation()).
                                    eq(TrendsTableParam::getUseType,byId.getUseType()).
                                    list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());*/
                                List<PaymentWaterFees> paymentWaterFees = paymentWaterFeesService.lambdaQuery().eq(PaymentWaterFees::getDel,0).
                                        eq(PaymentWaterFees::getYear, waterFeeStatisticsDetails.get(0).getYear()).
                                        in(PaymentWaterFees::getWaterUserId, collect1).eq(PaymentWaterFees::getType,"水费").list();
                                if(null != paymentWaterFees && paymentWaterFees.size()>0){
                                    for(PaymentWaterFees fees:paymentWaterFees){
                                        advancePaymentWaterFeeCount+=fees.getPaymentAmount();
                                    }
                                }
                                if(null != collect1 && collect1.size()>0){
                                    List<String> collect2 = new ArrayList<>();
                                    for(TrendsTableParam param:trendsTableParamList){
                                        for(String s:collect1){
                                            if(param.getPId().equals(s)){
                                                collect2.add(param.getId());
                                            }
                                        }
                                    }
                                    //List<String> collect2 = trendsTableParamService.lambdaQuery().in(TrendsTableParam::getPId, collect1).list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());
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
                                        List<String> collect3 = new ArrayList<>();
                                        for(TrendsTableParam param:trendsTableParamList){
                                            for(String s:collect2){
                                                if(param.getPId().equals(s)){
                                                    collect3.add(param.getId());
                                                }
                                            }
                                        }
                                        //List<String> collect3 = trendsTableParamService.lambdaQuery().in(TrendsTableParam::getPId, collect2).list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());
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
                                List<String> collect1 = trendsTableParamList.stream().filter(t->t.getPId().equals(byId.getPId())).map(TrendsTableParam::getId).collect(Collectors.toList());
                                //List<String> collect1 = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, byId.getPId()).list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());
                                List<String> collect1Son = new ArrayList<>();
                                for(TrendsTableParam param:trendsTableParamList){
                                    for(String s:collect1){
                                        if(param.getPId().equals(s)){
                                            collect1Son.add(param.getId());
                                        }
                                    }
                                }
                                //List<String> collect1Son = trendsTableParamService.lambdaQuery().in(TrendsTableParam::getPId, collect1).list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());
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
                            TrendsTableParam byId = JSONObject.parseObject((String)redisUtil.get("trendsTableParam:object:"+id),TrendsTableParam.class);
                            //TrendsTableParam byId = trendsTableParamService.getById(id);
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
                                continue;
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
                                TrendsTableParam byId1 = JSONObject.parseObject((String)redisUtil.get("trendsTableParam:object:"+total.getTableHeadId()),TrendsTableParam.class);
                                //TrendsTableParam byId1 = trendsTableParamService.getById(total.getTableHeadId());
                                if(!byId1.getPId().equals("0")){
                                    TrendsTableParam one = JSONObject.parseObject((String)redisUtil.get("trendsTableParam:object:"+byId1.getPId()),TrendsTableParam.class);
                                    //TrendsTableParam one = trendsTableParamService.getById(byId1.getPId());
                                    List<PaymentWaterFees> list2 = paymentWaterFeesService.lambdaQuery().eq(PaymentWaterFees::getDel,0).
                                            eq(PaymentWaterFees::getYear, waterFeeStatisticsDetails.get(0).getYear()).
                                            eq(PaymentWaterFees::getWaterUserId, one.getId()).eq(PaymentWaterFees::getType,"水资源费").list();
                                    if(null!=list2 && list2.size()>0){
                                        for(PaymentWaterFees fees:list2){
                                            paidWaterResource2+=fees.getPaymentAmount();
                                        }
                                        List<TrendsTableParam> totalTemp = trendsTableParamList.stream().filter(t -> t.getPId().equals(one.getId())).filter(t -> t.getParamName().equals("合计")).collect(Collectors.toList());
                                        TrendsTableParam totalBean = totalTemp.get(0);
                                        //TrendsTableParam totalBean = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, one.getId()).eq(TrendsTableParam::getParamName, "合计").one();
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
                                TrendsTableParam byId = JSONObject.parseObject((String)redisUtil.get("trendsTableParam:object:"+id),TrendsTableParam.class);
                                //TrendsTableParam byId = trendsTableParamService.getById(id);
                                if(!byId.getPId().equals("0")){
                                    List<TrendsTableParam> tableParams = trendsTableParamList.stream().filter(t->t.getPId().equals(byId.getPId())).filter(t->!t.getParamName().equals("合计")).collect(Collectors.toList());
                                    //List<TrendsTableParam> tableParams = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, byId.getPId()).ne(TrendsTableParam::getParamName, "合计").list();
                                    for(TrendsTableParam param:tableParams){
                                        List<TrendsTableParam> listed = trendsTableParamList.stream().filter(t->t.getPId().equals(param.getId())).filter(t->!t.getParamName().equals("合计")).collect(Collectors.toList());
                                        //List<TrendsTableParam> listed = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, param.getId()).ne(TrendsTableParam::getParamName, "合计").list();
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
                                TrendsTableParam byId = JSONObject.parseObject((String)redisUtil.get("trendsTableParam:object:"+id),TrendsTableParam.class);
                                //TrendsTableParam byId = trendsTableParamService.getById(id);
                                if(byId.getPId().equals("0")){
                                    List<String> collect1 = trendsTableParamList.stream().filter(t->t.getPId().equals(byId.getPId())).
                                            filter(t->t.getUseStation().equals(byId.getUseStation())).
                                            filter(t->t.getUseType()==byId.getUseType()).map(TrendsTableParam::getId).collect(Collectors.toList());
                                /*List<String> collect1 = trendsTableParamService.lambdaQuery().
                                        eq(TrendsTableParam::getPId, byId.getPId()).
                                        eq(TrendsTableParam::getUseStation,byId.getUseStation()).
                                        eq(TrendsTableParam::getUseType,byId.getUseType()).
                                        list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());*/
                                    List<PaymentWaterFees> paymentWaterFees = paymentWaterFeesService.lambdaQuery().eq(PaymentWaterFees::getDel,0).
                                            eq(PaymentWaterFees::getYear, waterFeeStatisticsDetails.get(0).getYear()).
                                            in(PaymentWaterFees::getWaterUserId, collect1).eq(PaymentWaterFees::getType,"水资源费").list();
                                    if(null != paymentWaterFees && paymentWaterFees.size()>0){
                                        for(PaymentWaterFees fees:paymentWaterFees){
                                            paidWaterResourceCount+=fees.getPaymentAmount();
                                        }
                                    }
                                    if(null != collect1 && collect1.size()>0){
                                        List<String> collect2= new ArrayList<>();
                                        for(TrendsTableParam param:trendsTableParamList){
                                            for(String s:collect1){
                                                if(param.getPId().equals(s)){
                                                    collect2.add(param.getId());
                                                }
                                            }
                                        }
                                        //List<String> collect2 = trendsTableParamService.lambdaQuery().in(TrendsTableParam::getPId, collect1).list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());
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
                                            List<String> collect3 = new ArrayList<>();
                                            for(TrendsTableParam param:trendsTableParamList){
                                                for(String s:collect2){
                                                    if(param.getPId().equals(s)){
                                                        collect3.add(param.getId());
                                                    }
                                                }
                                            }
                                            //List<String> collect3 = trendsTableParamService.lambdaQuery().in(TrendsTableParam::getPId, collect2).list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());
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
                                    List<String> collect1 = trendsTableParamList.stream().filter(t->t.getPId().equals(byId.getPId())).map(TrendsTableParam::getId).collect(Collectors.toList());
                                    //List<String> collect1 = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, byId.getPId()).list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());
                                    List<String> collect1Son = new ArrayList<>();
                                    for (TrendsTableParam param:trendsTableParamList){
                                        for (String s:collect1){
                                            if(param.getPId().equals(s)){
                                                collect1Son.add(param.getId());
                                            }
                                        }
                                    }
                                    //List<String> collect1Son = trendsTableParamService.lambdaQuery().in(TrendsTableParam::getPId, collect1).list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());
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
                            TrendsTableParam byId1 = JSONObject.parseObject((String)redisUtil.get("trendsTableParam:object:"+total.getTableHeadId()),TrendsTableParam.class);
                            //TrendsTableParam byId1 = trendsTableParamService.getById(total.getTableHeadId());
                            if(!byId1.getPId().equals("0")){
                                TrendsTableParam one = JSONObject.parseObject((String)redisUtil.get("trendsTableParam:object:"+byId1.getPId()),TrendsTableParam.class);
                                //TrendsTableParam one = trendsTableParamService.getById(byId1.getPId());
                                List<PaymentWaterFees> list2 = paymentWaterFeesService.lambdaQuery().eq(PaymentWaterFees::getDel,0).
                                        eq(PaymentWaterFees::getYear, waterFeeStatisticsDetails.get(0).getYear()).
                                        eq(PaymentWaterFees::getWaterUserId, one.getId()).eq(PaymentWaterFees::getType,"水费").list();
                                if(null!=list2 && list2.size()>0){
                                    for(PaymentWaterFees fees:list2){
                                        advancePaymentWaterFee2+=fees.getPaymentAmount();
                                    }
                                    List<TrendsTableParam> totalTemp = trendsTableParamList.stream().filter(t -> t.getPId().equals(one.getId())).filter(t -> t.getParamName().equals("合计")).collect(Collectors.toList());
                                    TrendsTableParam totalBean = totalTemp.get(0);
                                    //TrendsTableParam totalBean = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, one.getId()).eq(TrendsTableParam::getParamName, "合计").one();
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
                            TrendsTableParam byId = JSONObject.parseObject((String)redisUtil.get("trendsTableParam:object:"+id),TrendsTableParam.class);
                            //TrendsTableParam byId = trendsTableParamService.getById(id);
                            if(!byId.getPId().equals("0")){
                                List<TrendsTableParam> tableParams = trendsTableParamList.stream().filter(t->t.getPId().equals(byId.getPId())).filter(t->!t.getParamName().equals("合计")).collect(Collectors.toList());
                                //List<TrendsTableParam> tableParams = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, byId.getPId()).ne(TrendsTableParam::getParamName, "合计").list();
                                for(TrendsTableParam param:tableParams){
                                    List<TrendsTableParam> listed = trendsTableParamList.stream().filter(t->t.getPId().equals(param.getId())).filter(t->!t.getParamName().equals("合计")).collect(Collectors.toList());
                                    //List<TrendsTableParam> listed = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, param.getId()).ne(TrendsTableParam::getParamName, "合计").list();
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
                            TrendsTableParam byId = JSONObject.parseObject((String)redisUtil.get("trendsTableParam:object:"+id),TrendsTableParam.class);
                            //TrendsTableParam byId = trendsTableParamService.getById(id);
                            if(byId.getPId().equals("0")){
                                List<String> collect1 = trendsTableParamList.stream().filter(t->t.getPId().equals(byId.getPId())).
                                        filter(t->t.getUseStation().equals(byId.getUseStation())).
                                        filter(t->t.getUseType()==byId.getUseType()).map(TrendsTableParam::getId).collect(Collectors.toList());
                          /*  List<String> collect1 = trendsTableParamService.lambdaQuery().
                                    eq(TrendsTableParam::getPId, byId.getPId()).
                                    eq(TrendsTableParam::getUseStation,byId.getUseStation()).
                                    eq(TrendsTableParam::getUseType,byId.getUseType()).
                                    list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());*/
                                List<PaymentWaterFees> paymentWaterFees = paymentWaterFeesService.lambdaQuery().eq(PaymentWaterFees::getDel,0).
                                        eq(PaymentWaterFees::getYear, waterFeeStatisticsDetails.get(0).getYear()).
                                        in(PaymentWaterFees::getWaterUserId, collect1).eq(PaymentWaterFees::getType,"水费").list();
                                if(null != paymentWaterFees && paymentWaterFees.size()>0){
                                    for(PaymentWaterFees fees:paymentWaterFees){
                                        advancePaymentWaterFeeCount+=fees.getPaymentAmount();
                                    }
                                }
                                if(null != collect1 && collect1.size()>0){
                                    List<String> collect2= new ArrayList<>();
                                    for(TrendsTableParam param:trendsTableParamList){
                                        for(String s:collect1){
                                            if(param.getPId().equals(s)){
                                                collect2.add(param.getId());
                                            }
                                        }
                                    }
                                    //List<String> collect2 = trendsTableParamService.lambdaQuery().in(TrendsTableParam::getPId, collect1).list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());
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
                                        List<String> collect3 = new ArrayList<>();
                                        for(TrendsTableParam param:trendsTableParamList){
                                            for(String s:collect2){
                                                if(param.getPId().equals(s)){
                                                    collect3.add(param.getId());
                                                }
                                            }
                                        }
                                        //List<String> collect3 = trendsTableParamService.lambdaQuery().in(TrendsTableParam::getPId, collect2).list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());
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
                                List<String> collect1 = trendsTableParamList.stream().filter(t->t.getPId().equals(byId.getPId())).map(TrendsTableParam::getId).collect(Collectors.toList());
                                //List<String> collect1 = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, byId.getPId()).list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());
                                List<String> collect1Son = new ArrayList<>();
                                for (TrendsTableParam param:trendsTableParamList){
                                    for (String s:collect1){
                                        if(param.getPId().equals(s)){
                                            collect1Son.add(param.getId());
                                        }
                                    }
                                }
                                //List<String> collect1Son = trendsTableParamService.lambdaQuery().in(TrendsTableParam::getPId, collect1).list().stream().map(TrendsTableParam::getId).collect(Collectors.toList());
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
                            TrendsTableParam byId = JSONObject.parseObject((String)redisUtil.get("trendsTableParam:object:"+id),TrendsTableParam.class);
                            //TrendsTableParam byId = trendsTableParamService.getById(id);
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
                                continue;
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
        redisUtil.del("waterFee:"+details1.getStation()+details1.getYear()+details1.getMonth()+details1.getTenDays());
        return RestResponse.ok("添加成功");
    }

    @Override
    public RestResponse useWaterTypeStatistics(UseWaterTypeStatisticsReq req) {
        String mk = (String) redisUtil.get("trendsTableParam:list");
        if(StringUtils.isEmpty(mk)){
            updateCache();
            mk = (String) redisUtil.get("trendsTableParam:list");
        }
        List<TrendsTableParam> trendsTableParamListTemp = JSONObject.parseArray(mk, TrendsTableParam.class);
        List<TrendsTableParam> trendsTableParamList = trendsTableParamListTemp.stream().filter(t -> t.getUseType() == 2).collect(Collectors.toList());
        if(req.getStation().equals("楼庄子水库")){
            req.setStationList(Arrays.asList("楼庄子水厂"));
            List<UseWaterTypeStatisticsRes> statistics = industrialWaterFeeService.statistics(req);
            if(null != statistics){
                Map<String, List<UseWaterTypeStatisticsRes>> result = new HashMap<>();
                if(StringUtils.isNotEmpty(req.getUseType())){
                    result = statistics.stream().collect(Collectors.groupingBy(UseWaterTypeStatisticsRes::getParamName));
                    return RestResponse.ok(result);
                }else {
                    statistics.forEach(t->t.setParamName("城镇生活"));
                    result.put("城镇生活",statistics);
                    return RestResponse.ok(result);
                }
            }else {
                return RestResponse.no("blank");
            }
        }
        if(req.getStation().equals("头屯河水库")){
            req.setStationList(Arrays.asList("红岩水库","八钢"));
            List<UseWaterTypeStatisticsRes> statistics = industrialWaterFeeService.statistics(req);
            if(null != statistics){
                Map<String, List<UseWaterTypeStatisticsRes>> result = new HashMap<>();
                if(StringUtils.isNotEmpty(req.getUseType())){
                    result = statistics.stream().collect(Collectors.groupingBy(UseWaterTypeStatisticsRes::getParamName));
                    List<UseWaterTypeStatisticsRes> bgTemp = result.get("八钢");
                    if(req.getUseType().equals("工业")){
                        List<UseWaterTypeStatisticsRes> bg = new ArrayList<>();
                        bgTemp.forEach(t->{
                            UseWaterTypeStatisticsRes res = new UseWaterTypeStatisticsRes();
                            BeanUtils.copyProperties(t,res);
                            res.setV((t.getV()==null?0.00:t.getV())*0.7);
                            bg.add(res);
                        });
                        List<UseWaterTypeStatisticsRes> collect = bg.stream().sorted(Comparator.comparing(UseWaterTypeStatisticsRes::getRecordTime, Comparator.nullsFirst(Comparator.naturalOrder()))).collect(Collectors.toList());
                        result.put("八钢",collect);
                        List<UseWaterTypeStatisticsRes> nyTemp = result.get("红岩水库");
                        if(null != nyTemp && nyTemp.size()>0){
                            List<UseWaterTypeStatisticsRes> ny = nyTemp.stream().sorted(Comparator.comparing(UseWaterTypeStatisticsRes::getRecordTime, Comparator.nullsFirst(Comparator.naturalOrder()))).collect(Collectors.toList());
                            result.put("红岩水库",ny);
                        }else {
                            result.put("红岩水库",null);
                        }
                    }
                    if(req.getUseType().equals("农业")){
                        result.remove("红岩水库");
                        List<UseWaterTypeStatisticsRes> bg = new ArrayList<>();
                        bgTemp.forEach(t->{
                            UseWaterTypeStatisticsRes res = new UseWaterTypeStatisticsRes();
                            BeanUtils.copyProperties(t,res);
                            res.setV((t.getV()==null?0.00:t.getV())*0.3);
                            bg.add(res);
                        });
                        List<UseWaterTypeStatisticsRes> collect = bg.stream().sorted(Comparator.comparing(UseWaterTypeStatisticsRes::getRecordTime, Comparator.nullsFirst(Comparator.naturalOrder()))).collect(Collectors.toList());
                        result.put("八钢",collect);
                    }
                    return RestResponse.ok(result);
                }else {
                    Map<Date, List<UseWaterTypeStatisticsRes>> collect = statistics.stream().collect(Collectors.groupingBy(UseWaterTypeStatisticsRes::getRecordTime));
                    Set<Date> dates = collect.keySet();
                    List<UseWaterTypeStatisticsRes> gyList = new ArrayList<>();
                    List<UseWaterTypeStatisticsRes> nyList = new ArrayList<>();
                    for(Date date : dates){
                        UseWaterTypeStatisticsRes res = new UseWaterTypeStatisticsRes();
                        Double aDouble = 0.0;
                        for(UseWaterTypeStatisticsRes temp : collect.get(date)){
                            if(temp.getParamName().equals("八钢")){
                                aDouble += (temp.getV()==null?0.00:temp.getV())*0.7;
                            }else {
                                aDouble += temp.getV()==null?0.00:temp.getV();
                            }
                        }
                        res.setV(aDouble);
                        res.setRecordTime(date);
                        res.setParamName("工业");
                        gyList.add(res);
                        List<UseWaterTypeStatisticsRes> collect1 = gyList.stream().sorted(Comparator.comparing(UseWaterTypeStatisticsRes::getRecordTime, Comparator.nullsFirst(Comparator.naturalOrder()))).collect(Collectors.toList());
                        result.put("工业",collect1);
                    }
                    for(Date date : dates){
                        UseWaterTypeStatisticsRes res = new UseWaterTypeStatisticsRes();
                        Double aDouble = 0.0;
                        for(UseWaterTypeStatisticsRes temp : collect.get(date)){
                            if(temp.getParamName().equals("八钢")) {
                                aDouble += (temp.getV()==null?0.00:temp.getV()) * 0.3;
                            }
                        }
                        res.setV(aDouble);
                        res.setRecordTime(date);
                        res.setParamName("农业");
                        nyList.add(res);
                        List<UseWaterTypeStatisticsRes> collect1 = nyList.stream().sorted(Comparator.comparing(UseWaterTypeStatisticsRes::getRecordTime, Comparator.nullsFirst(Comparator.naturalOrder()))).collect(Collectors.toList());
                        result.put("农业",collect1);
                    }
                    return RestResponse.ok(result);
                }
            }else {
                return RestResponse.no("blank");
            }
        }
        if(req.getStation().equals("渠首管理站")){
            Map<String, List<UseWaterTypeStatisticsRes>> collect = new HashMap<>();
            List<TrendsTableParam> dlqParamList = trendsTableParamList.stream().filter(t -> t.getUseStation().equals("渠首管理站") && t.getUseType() == 2).collect(Collectors.toList());
            if(StringUtils.isEmpty(req.getUseType())){
                TrendsTableParam dlqParam = dlqParamList.stream().filter(t -> t.getParamName().equals("灯笼渠")).collect(Collectors.toList()).get(0);
                List<String> ids1 = trendsTableParamList.stream().filter(t -> t.getPId().equals(dlqParam.getId()) && !t.getParamName().equals("合计")).map(TrendsTableParam::getId).collect(Collectors.toList());
                req.setStationList(ids1);
                List<UseWaterTypeStatisticsRes> statistics1 = this.baseMapper.statistics(req);
                if(null != statistics1 && statistics1.size()>0){
                    collect = statistics1.stream().collect(Collectors.groupingBy(UseWaterTypeStatisticsRes::getParamName));
                }
                TrendsTableParam ldParam = dlqParamList.stream().filter(t -> t.getParamName().equals("漏斗")).collect(Collectors.toList()).get(0);
                TrendsTableParam xhParam = dlqParamList.stream().filter(t -> t.getParamName().equals("泄洪")).collect(Collectors.toList()).get(0);
                List<String> ids2 = new ArrayList<>();
                ids2.add(ldParam.getId());
                ids2.add(xhParam.getId());
                req.setStationList(ids2);
                List<UseWaterTypeStatisticsRes> statistics2 = this.baseMapper.statistics(req);
                if(null != statistics2 && statistics2.size()>0){
                    statistics2.forEach(t->t.setParamName("生态用水"));
                    collect.put("生态用水",statistics2);
                }
                if(collect.size()<0){
                    return RestResponse.no("blank");
                }else{
                    return RestResponse.ok(collect);
                }
            }else {
                if(req.getUseType().equals("生态用水")){
                    TrendsTableParam ldParam = dlqParamList.stream().filter(t -> t.getParamName().equals("漏斗")).collect(Collectors.toList()).get(0);
                    TrendsTableParam xhParam = dlqParamList.stream().filter(t -> t.getParamName().equals("泄洪")).collect(Collectors.toList()).get(0);
                    List<String> ids2 = new ArrayList<>();
                    ids2.add(ldParam.getId());
                    ids2.add(xhParam.getId());
                    req.setStationList(ids2);
                    List<UseWaterTypeStatisticsRes> statistics2 = this.baseMapper.statistics(req);
                    if(null != statistics2 && statistics2.size()>0){
                        collect = statistics2.stream().collect(Collectors.groupingBy(UseWaterTypeStatisticsRes::getParamName));
                    }
                }else {
                    TrendsTableParam dlqParam = dlqParamList.stream().filter(t -> t.getParamName().equals("灯笼渠")).collect(Collectors.toList()).get(0);
                    List<String> ids1 = trendsTableParamList.stream().filter(t -> t.getPId().equals(dlqParam.getId()) && t.getParamName().equals(req.getUseType())).map(TrendsTableParam::getId).collect(Collectors.toList());
                    req.setStationList(ids1);
                    List<UseWaterTypeStatisticsRes> statistics1 = this.baseMapper.statistics(req);
                    if(null != statistics1 && statistics1.size()>0){
                        collect = statistics1.stream().collect(Collectors.groupingBy(UseWaterTypeStatisticsRes::getParamName));
                    }
                }
                if(collect.size()<0){
                    return RestResponse.no("blank");
                }else{
                    return RestResponse.ok(collect);
                }
            }
        }
        if(req.getStation().equals("河东管理站")){
            Map<String, List<UseWaterTypeStatisticsRes>> collect = new HashMap<>();
            List<TrendsTableParam> hdNy = trendsTableParamList.stream().filter(t -> t.getUseType() == 2 && t.getUseStation().equals("河东管理站农业")).collect(Collectors.toList());
            List<TrendsTableParam> hdLh = trendsTableParamList.stream().filter(t -> t.getUseType() == 2 && t.getUseStation().equals("河东管理站绿化")).collect(Collectors.toList());
            if(StringUtils.isEmpty(req.getUseType())){
                TrendsTableParam totalNyParam = hdNy.stream().filter(t -> t.getPId().equals("0") && t.getParamName().equals("合计")).collect(Collectors.toList()).get(0);
                req.setStationList(Arrays.asList(totalNyParam.getId()));
                List<UseWaterTypeStatisticsRes> statistics1 = this.baseMapper.statistics(req);
                if(null != statistics1 && statistics1.size()>0){
                    statistics1.forEach(t->t.setParamName("农业"));
                    collect.put("农业",statistics1);
                }
                TrendsTableParam totalLhParam = hdLh.stream().filter(t -> t.getPId().equals("0") && t.getParamName().equals("合计")).collect(Collectors.toList()).get(0);
                req.setStationList(Arrays.asList(totalLhParam.getId()));
                List<UseWaterTypeStatisticsRes> statistics2 = this.baseMapper.statistics(req);
                if(null != statistics2 && statistics2.size()>0){
                    statistics2.forEach(t->t.setParamName("绿化"));
                    collect.put("绿化",statistics2);
                }
                if(collect.size()>0){
                    return RestResponse.ok(collect);
                }else {
                    return RestResponse.no("blank");
                }
            }else {
                if(req.getUseType().equals("农业")){
                    req.setStationList(getTableIds(hdNy));
                    List<UseWaterTypeStatisticsRes> statistics = this.baseMapper.statistics(req);
                    if(null != statistics && statistics.size()>0){
                        Map<String, List<UseWaterTypeStatisticsRes>> collect1 = statistics.stream().collect(Collectors.groupingBy(UseWaterTypeStatisticsRes::getParamName));
                        return RestResponse.ok(collect1);
                    }else {
                        return RestResponse.no("blank");
                    }
                }
                if(req.getUseType().equals("绿化")){
                    req.setStationList(getTableIds(hdLh));
                    List<UseWaterTypeStatisticsRes> statistics = this.baseMapper.statistics(req);
                    if(null != statistics && statistics.size()>0){
                        Map<String, List<UseWaterTypeStatisticsRes>> collect1 = statistics.stream().collect(Collectors.groupingBy(UseWaterTypeStatisticsRes::getParamName));
                        return RestResponse.ok(collect1);
                    }else {
                        return RestResponse.no("blank");
                    }
                }
                return RestResponse.no("请传入正确的用水类型");
            }
        }
        if(req.getStation().equals("河西管理站")){
            Map<String, List<UseWaterTypeStatisticsRes>> collect = new HashMap<>();
            List<TrendsTableParam> hx = trendsTableParamList.stream().filter(t -> t.getUseType() == 2 && t.getUseStation().equals("河西管理站")).collect(Collectors.toList());
            if(StringUtils.isEmpty(req.getUseType())){
                List<String> ids1 = new ArrayList<>();
                List<String> nyAll = hx.stream().filter(t -> t.getPId().equals("0") && !t.getParamName().equals("合计") && !t.getParamName().equals("城绿化")).map(TrendsTableParam::getId).collect(Collectors.toList());
                for(String pid:nyAll){
                    List<String> collect1 = hx.stream().filter(t -> t.getPId().equals(pid) && !t.getParamName().equals("合计")).map(TrendsTableParam::getId).collect(Collectors.toList());
                    for(String pid1:collect1){
                        List<String> collect2 = hx.stream().filter(t -> t.getPId().equals(pid1) && !t.getParamName().equals("合计")).map(TrendsTableParam::getId).collect(Collectors.toList());
                        if(null != collect2 && collect2.size()>0){
                            ids1.addAll(collect2);
                        }else {
                            ids1.add(pid1);
                        }
                    }
                }
                req.setStationList(ids1);
                List<UseWaterTypeStatisticsRes> statistics = this.baseMapper.statistics(req);
                if(null != statistics && statistics.size()>0){
                    List<UseWaterTypeStatisticsRes> resList = new ArrayList<>();
                    Map<Date, List<UseWaterTypeStatisticsRes>> collect1 = statistics.stream().collect(Collectors.groupingBy(UseWaterTypeStatisticsRes::getRecordTime));
                    Set<Date> dates = collect1.keySet();
                    for(Date date:dates){
                        UseWaterTypeStatisticsRes res = new UseWaterTypeStatisticsRes();
                        Double aDouble = collect1.get(date).stream().filter(t -> t.getV() != null).map(UseWaterTypeStatisticsRes::getV).reduce(Double::sum).orElse(0.00);
                        res.setParamName("农业");
                        res.setV(aDouble);
                        res.setRecordTime(date);
                        resList.add(res);
                    }
                    collect.put("农业",resList);
                }
                TrendsTableParam lhTemp = hx.stream().filter(t -> t.getPId().equals("0") && t.getParamName().equals("城绿化")).collect(Collectors.toList()).get(0);
                TrendsTableParam lh = hx.stream().filter(t -> t.getPId().equals(lhTemp.getId()) && t.getParamName().equals("合计")).collect(Collectors.toList()).get(0);
                req.setStationList(Arrays.asList(lh.getId()));
                List<UseWaterTypeStatisticsRes> statistics1 = this.baseMapper.statistics(req);
                if(null != statistics1 && statistics1.size()>0){
                    statistics1.forEach(t->t.setParamName("绿化"));
                    collect.put("绿化",statistics1);
                }
                if(collect.size()>0){
                    return RestResponse.ok(collect);
                }else {
                    return RestResponse.no("blank");
                }
            }else {
                if(req.getUseType().equals("农业")){
                    List<String> ids = new ArrayList<>();
                    List<String> nyAll = hx.stream().filter(t -> t.getPId().equals("0") && !t.getParamName().equals("合计") && !t.getParamName().equals("城绿化")).map(TrendsTableParam::getId).collect(Collectors.toList());
                    for(String pid:nyAll){
                        List<String> collect1 = hx.stream().filter(t -> t.getPId().equals(pid) && !t.getParamName().equals("合计")).map(TrendsTableParam::getId).collect(Collectors.toList());
                        for(String pid1:collect1){
                            List<String> collect2 = hx.stream().filter(t -> t.getPId().equals(pid1) && !t.getParamName().equals("合计")).map(TrendsTableParam::getId).collect(Collectors.toList());
                            if(null != collect2 && collect2.size()>0){
                                ids.addAll(collect2);
                            }else {
                                ids.add(pid1);
                            }
                        }
                    }
                    req.setStationList(ids);
                    List<UseWaterTypeStatisticsRes> statistics = this.baseMapper.statistics(req);
                    if(null != statistics && statistics.size()>0){
                        Map<String, List<UseWaterTypeStatisticsRes>> collect1 = statistics.stream().collect(Collectors.groupingBy(UseWaterTypeStatisticsRes::getParamName));
                        return RestResponse.ok(collect1);
                    }else {
                        return RestResponse.no("blank");
                    }
                }
                if(req.getUseType().equals("绿化")){
                    TrendsTableParam lhTemp = hx.stream().filter(t -> t.getPId().equals("0") && t.getParamName().equals("城绿化")).collect(Collectors.toList()).get(0);
                    List<String> strings = hx.stream().filter(t -> t.getPId().equals(lhTemp.getId()) && !t.getParamName().equals("合计")).map(TrendsTableParam::getId).collect(Collectors.toList());
                    req.setStationList(strings);
                    List<UseWaterTypeStatisticsRes> statistics = this.baseMapper.statistics(req);
                    if(null != statistics && statistics.size()>0){
                        Map<String, List<UseWaterTypeStatisticsRes>> collect1 = statistics.stream().collect(Collectors.groupingBy(UseWaterTypeStatisticsRes::getParamName));
                        return RestResponse.ok(collect1);
                    }else {
                        return RestResponse.no("blank");
                    }
                }
                return RestResponse.no("请传入正确的用水类型");
            }
        }
        return RestResponse.no("请传入正确的管理站");
    }

    @Override
    public RestResponse deleteRedisData(WaterFeeStatisticsDetailsSelectListReq req) {
        redisUtil.del("waterFee:"+req.getStation()+req.getYear()+req.getMonth()+req.getTenDays());
        return RestResponse.ok();
    }

    public List<String> getTableHeadName(List<TrendsTableParam> trendsTableParamList){
        try {
            List<String> tableIds = new ArrayList<>();
            QueryTrendsTableParamReq req = new QueryTrendsTableParamReq();
            req.setUseType(2);
            req.setUseStation(trendsTableParamList.get(0).getUseStation());
            RestResponse<List<WaterDailyParamSelectRes>> select = trendsTableParamService.select(req);
            List<WaterDailyParamSelectRes> data = select.getData();
            for(WaterDailyParamSelectRes res1 : data){
                List<WaterDailyParamSelectRes> children = res1.getChildren();
                if(children != null){
                    for(WaterDailyParamSelectRes res2:children){
                        List<WaterDailyParamSelectRes> children1 = res2.getChildren();
                        if(children1 !=null){
                            for(WaterDailyParamSelectRes res3:children1){
                                if(!res3.getParamName().equals("合计")) {
                                    tableIds.add(res3.getParamName());
                                }
                            }
                        }else {
                            if(!res2.getParamName().equals("合计")) {
                                tableIds.add(res2.getParamName());
                            }
                        }
                    }
                }else {
                    if(!res1.getParamName().equals("合计")){
                        tableIds.add(res1.getParamName());
                    }
                }
            }
            return tableIds;
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getLocalizedMessage());
            return null;
        }
    }

    public List<String> getTableIds(List<TrendsTableParam> trendsTableParamList){
        List<String> ids = new ArrayList<>();
        List<String> pIdCollect = trendsTableParamList.stream().filter(t -> !t.getParamName().equals("合计") && t.getPId().equals("0")).map(TrendsTableParam::getId).collect(Collectors.toList());
        for(String pid:pIdCollect){
            List<String> pIdCollect1 = trendsTableParamList.stream().filter(t -> !t.getParamName().equals("合计") && t.getPId().equals(pid)).map(TrendsTableParam::getId).collect(Collectors.toList());
            if(null != pIdCollect1 && pIdCollect1.size()>0){
                for(String pid1:pIdCollect1) {
                    List<String> pIdCollect2 = trendsTableParamList.stream().filter(t -> !t.getParamName().equals("合计") && t.getPId().equals(pid1)).map(TrendsTableParam::getId).collect(Collectors.toList());
                    if(null != pIdCollect2 && pIdCollect2.size()>0){
                        ids.addAll(pIdCollect2);
                    }else {
                        ids.add(pid1);
                    }
                }
            }else {
                ids.add(pid);
            }
        }
        return ids;
    }
}

