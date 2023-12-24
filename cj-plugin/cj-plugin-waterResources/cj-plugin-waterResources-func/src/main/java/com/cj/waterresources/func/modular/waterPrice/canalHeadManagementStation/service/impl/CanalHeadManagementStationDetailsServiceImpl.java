package com.cj.waterresources.func.modular.waterPrice.canalHeadManagementStation.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.common.model.RestResponse;
import com.cj.common.util.UUIDUtils;
import com.cj.waterresources.func.modular.trendsTable.entity.TrendsTableParam;
import com.cj.waterresources.func.modular.trendsTable.service.TrendsTableParamService;
import com.cj.waterresources.func.modular.waterPrice.canalHeadManagementStation.entity.CanalHeadManagementStationTotal;
import com.cj.waterresources.func.modular.waterPrice.canalHeadManagementStation.mapper.CanalHeadManagementStationDetailsMapper;
import com.cj.waterresources.func.modular.waterPrice.canalHeadManagementStation.entity.CanalHeadManagementStationDetails;
import com.cj.waterresources.func.modular.waterPrice.canalHeadManagementStation.service.CanalHeadManagementStationDetailsService;
import com.cj.waterresources.func.modular.waterPrice.canalHeadManagementStation.service.CanalHeadManagementStationTotalService;
import com.cj.waterresources.func.modular.waterPrice.waterDistributionRatio.entity.WaterDistributionRatio;
import com.cj.waterresources.func.modular.waterPrice.waterDistributionRatio.service.WaterDistributionRatioService;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.bean.req.WaterFeeStatisticsDetailsSelectListReq;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.entity.WaterFeeStatisticsDetails;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.service.WaterFeeStatisticsDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 渠首管理站明细表(CanalHeadManagementStationDetails)表服务实现类
 *
 * @author makejava
 * @since 2023-12-15 18:07:49
 */
@Service("canalHeadManagementStationDetailsService")
public class CanalHeadManagementStationDetailsServiceImpl extends ServiceImpl<CanalHeadManagementStationDetailsMapper, CanalHeadManagementStationDetails> implements CanalHeadManagementStationDetailsService {

    @Autowired
    private WaterFeeStatisticsDetailsService waterFeeStatisticsDetailsService;
    @Autowired
    private TrendsTableParamService trendsTableParamService;
    @Autowired
    private CanalHeadManagementStationTotalService canalHeadManagementStationTotalService;

    @Autowired
    private WaterDistributionRatioService waterDistributionRatioService;


    @Override
    public  Map<String,Double> getLanternCanalInfoByDate(String date) {
        Map<String,Double> resultMap = new HashMap<>();
        List<WaterFeeStatisticsDetails> lanternCanalInfoList = waterFeeStatisticsDetailsService.lambdaQuery().eq(WaterFeeStatisticsDetails::getStation, "渠首灯笼渠").eq(WaterFeeStatisticsDetails::getStatisticsDate, date).list();
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
            resultMap.put("totalValue", 0.0);
            resultMap.put("agricultureValue", 0.0);
            resultMap.put("greenValue", 0.0);
            resultMap.put("industryValue", 0.0);
        }
        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse addDetails(CanalHeadManagementStationDetails canalHeadManagementStationDetails) {
        canalHeadManagementStationDetails.setId(UUIDUtils.getUUID());
        String data = canalHeadManagementStationDetails.getData();
        Map<String, Double> lanternCanalInfoByDate = getLanternCanalInfoByDate(data);
        if(null != lanternCanalInfoByDate && lanternCanalInfoByDate.size()>0){
            canalHeadManagementStationDetails.setLanternCanalTotal(lanternCanalInfoByDate.get("totalValue"));
            canalHeadManagementStationDetails.setLanternCanalAgriculture(lanternCanalInfoByDate.get("agricultureValue"));
            canalHeadManagementStationDetails.setLanternCanalGreen(lanternCanalInfoByDate.get("greenValue"));
            canalHeadManagementStationDetails.setLanternCanalIndustry(lanternCanalInfoByDate.get("industryValue"));
        }
        canalHeadManagementStationDetails.setEastBankTotal(
                (canalHeadManagementStationDetails.getLanternCanalTotal()==null?0.0:canalHeadManagementStationDetails.getLanternCanalTotal())+
                (canalHeadManagementStationDetails.getDongGan()==null?0.0:canalHeadManagementStationDetails.getDongGan())
        );
        canalHeadManagementStationDetails.setDiversion(
                (canalHeadManagementStationDetails.getEastBankTotal()==null?0.0:canalHeadManagementStationDetails.getEastBankTotal())+
                (canalHeadManagementStationDetails.getXiGan()==null?0.0:canalHeadManagementStationDetails.getXiGan())+
                (canalHeadManagementStationDetails.getFunnel()==null?0.0:canalHeadManagementStationDetails.getFunnel())
        );
        canalHeadManagementStationDetails.setIncomingWater(
                (canalHeadManagementStationDetails.getDiversion()==null?0.0:canalHeadManagementStationDetails.getDiversion())+
                (canalHeadManagementStationDetails.getFloodDischarge()==null?0.0:canalHeadManagementStationDetails.getFloodDischarge())
        );
        canalHeadManagementStationDetails.setTotalDry(
                (canalHeadManagementStationDetails.getDongGan()==null?0.0:canalHeadManagementStationDetails.getDongGan())+
                (canalHeadManagementStationDetails.getXiGan()==null?0.0:canalHeadManagementStationDetails.getXiGan())+
                (canalHeadManagementStationDetails.getFunnel()==null?0.0:canalHeadManagementStationDetails.getFunnel())
        );
        boolean save = this.save(canalHeadManagementStationDetails);
        if(save){
            return RestResponse.ok("添加明细数据成功");
        }else {
            return RestResponse.no("添加明细数据失败");
        }
    }

    @Override
    public RestResponse add(CanalHeadManagementStationDetails canalHeadManagementStationDetails) {
        RestResponse restResponse = addDetails(canalHeadManagementStationDetails);
        if(restResponse.getCode()==200){
            List<CanalHeadManagementStationTotal> canalHeadManagementStationTotalList = new ArrayList<>();
            List<CanalHeadManagementStationDetails> list = this.lambdaQuery().eq(CanalHeadManagementStationDetails::getYear, canalHeadManagementStationDetails.getYear()).
                    eq(CanalHeadManagementStationDetails::getMonth, canalHeadManagementStationDetails.getMonth()).
                    eq(CanalHeadManagementStationDetails::getTenDays, canalHeadManagementStationDetails.getTenDays()).list();
            if(null != list && list.size() > 0){
                Map<String, Object> jisuan = jisuan(canalHeadManagementStationDetails.getYear(), canalHeadManagementStationDetails.getMonth(), canalHeadManagementStationDetails.getTenDays());
                CanalHeadManagementStationTotal firstTenDays = canalHeadManagementStationTotalService.lambdaQuery().
                        eq(CanalHeadManagementStationTotal::getYear, jisuan.get("year")).
                        eq(CanalHeadManagementStationTotal::getMonth, jisuan.get("month")).
                        eq(CanalHeadManagementStationTotal::getTenDays, jisuan.get("tenDays")).
                        eq(CanalHeadManagementStationTotal::getCode, 1).one();
                CanalHeadManagementStationTotal lastYear = canalHeadManagementStationTotalService.lambdaQuery().
                        eq(CanalHeadManagementStationTotal::getYear, canalHeadManagementStationDetails.getYear()-1).
                        eq(CanalHeadManagementStationTotal::getMonth, canalHeadManagementStationDetails.getMonth()).
                        eq(CanalHeadManagementStationTotal::getTenDays, canalHeadManagementStationDetails.getTenDays()).
                        eq(CanalHeadManagementStationTotal::getCode, 1).one();
                CanalHeadManagementStationTotal total1 = new CanalHeadManagementStationTotal();
                total1.setId(UUIDUtils.getUUID());
                total1.setDel(0);
                total1.setCreateTime(new Date());
                total1.setYear(canalHeadManagementStationDetails.getYear());
                total1.setMonth(canalHeadManagementStationDetails.getMonth());
                total1.setTenDays(canalHeadManagementStationDetails.getTenDays());
                total1.setAmountTo(list.stream().filter(t->t.getIncomingWater()!=null).map(CanalHeadManagementStationDetails::getIncomingWater).reduce(Double::sum).orElse(0.00));
                total1.setCurrentWaterVolume((total1.getAmountTo()==null?0.0:total1.getAmountTo())*60*60*24);
                total1.setWaterVolumeFirstTenDays(firstTenDays==null?0.0:firstTenDays.getWaterVolumeFirstTenDays()==null?0.0:firstTenDays.getWaterVolumeFirstTenDays());
                total1.setAccumulatedWaterVolume(
                        (total1.getCurrentWaterVolume()==null?0.0:total1.getCurrentWaterVolume())+
                        (total1.getWaterVolumeFirstTenDays()==null?0.0:total1.getWaterVolumeFirstTenDays())
                );
                total1.setWaterVolumeDuringLastYear(lastYear==null?0.0:lastYear.getWaterVolumeDuringLastYear()==null?0.0:lastYear.getWaterVolumeDuringLastYear());
                total1.setCode("incomingWater");
                canalHeadManagementStationTotalList.add(total1);
                CanalHeadManagementStationTotal total2 = new CanalHeadManagementStationTotal();
                total2.setId(UUIDUtils.getUUID());
                total2.setDel(0);
                total2.setCreateTime(new Date());
                total2.setYear(canalHeadManagementStationDetails.getYear());
                total2.setMonth(canalHeadManagementStationDetails.getMonth());
                total2.setTenDays(canalHeadManagementStationDetails.getTenDays());
                total2.setAmountTo(list.stream().filter(t->t.getDiversion()!=null).map(CanalHeadManagementStationDetails::getDiversion).reduce(Double::sum).orElse(0.00));
                total2.setCurrentWaterVolume((total2.getAmountTo()==null?0.0:total2.getAmountTo())*60*60*24);
                total2.setWaterVolumeFirstTenDays(firstTenDays==null?0.0:firstTenDays.getWaterVolumeFirstTenDays()==null?0.0:firstTenDays.getWaterVolumeFirstTenDays());
                total2.setAccumulatedWaterVolume(
                        (total2.getCurrentWaterVolume()==null?0.0:total2.getCurrentWaterVolume())+
                                (total2.getWaterVolumeFirstTenDays()==null?0.0:total2.getWaterVolumeFirstTenDays())
                );
                total2.setWaterVolumeDuringLastYear(lastYear==null?0.0:lastYear.getWaterVolumeDuringLastYear()==null?0.0:lastYear.getWaterVolumeDuringLastYear());
                total2.setCode("diversion");
                canalHeadManagementStationTotalList.add(total2);
                CanalHeadManagementStationTotal total3 = new CanalHeadManagementStationTotal();
                total3.setId(UUIDUtils.getUUID());
                total3.setDel(0);
                total3.setCreateTime(new Date());
                total3.setYear(canalHeadManagementStationDetails.getYear());
                total3.setMonth(canalHeadManagementStationDetails.getMonth());
                total3.setTenDays(canalHeadManagementStationDetails.getTenDays());
                total3.setAmountTo(list.stream().filter(t->t.getTotalDry()!=null).map(CanalHeadManagementStationDetails::getTotalDry).reduce(Double::sum).orElse(0.00));
                total3.setCurrentWaterVolume((total3.getAmountTo()==null?0.0:total3.getAmountTo())*60*60*24);
                total3.setWaterVolumeFirstTenDays(firstTenDays==null?0.0:firstTenDays.getWaterVolumeFirstTenDays()==null?0.0:firstTenDays.getWaterVolumeFirstTenDays());
                total3.setAccumulatedWaterVolume(
                        (total3.getCurrentWaterVolume()==null?0.0:total3.getCurrentWaterVolume())+
                                (total3.getWaterVolumeFirstTenDays()==null?0.0:total3.getWaterVolumeFirstTenDays())
                );
                total3.setWaterVolumeDuringLastYear(lastYear==null?0.0:lastYear.getWaterVolumeDuringLastYear()==null?0.0:lastYear.getWaterVolumeDuringLastYear());
                total3.setCode("totalDry");
                canalHeadManagementStationTotalList.add(total3);
                CanalHeadManagementStationTotal total4 = new CanalHeadManagementStationTotal();
                total4.setId(UUIDUtils.getUUID());
                total4.setDel(0);
                total4.setCreateTime(new Date());
                total4.setYear(canalHeadManagementStationDetails.getYear());
                total4.setMonth(canalHeadManagementStationDetails.getMonth());
                total4.setTenDays(canalHeadManagementStationDetails.getTenDays());
                total4.setAmountTo(list.stream().filter(t->t.getXiGan()!=null).map(CanalHeadManagementStationDetails::getXiGan).reduce(Double::sum).orElse(0.00));
                total4.setCurrentWaterVolume((total4.getAmountTo()==null?0.0:total4.getAmountTo())*60*60*24);
                total4.setWaterVolumeFirstTenDays(firstTenDays==null?0.0:firstTenDays.getWaterVolumeFirstTenDays()==null?0.0:firstTenDays.getWaterVolumeFirstTenDays());
                total4.setAccumulatedWaterVolume(
                        (total4.getCurrentWaterVolume()==null?0.0:total4.getCurrentWaterVolume())+
                                (total4.getWaterVolumeFirstTenDays()==null?0.0:total4.getWaterVolumeFirstTenDays())
                );
                total4.setWaterVolumeDuringLastYear(lastYear==null?0.0:lastYear.getWaterVolumeDuringLastYear()==null?0.0:lastYear.getWaterVolumeDuringLastYear());
                total4.setCode("xiGan");
                canalHeadManagementStationTotalList.add(total4);
                CanalHeadManagementStationTotal total5 = new CanalHeadManagementStationTotal();
                total5.setId(UUIDUtils.getUUID());
                total5.setDel(0);
                total5.setCreateTime(new Date());
                total5.setYear(canalHeadManagementStationDetails.getYear());
                total5.setMonth(canalHeadManagementStationDetails.getMonth());
                total5.setTenDays(canalHeadManagementStationDetails.getTenDays());
                total5.setAmountTo(list.stream().filter(t->t.getEastBankTotal()!=null).map(CanalHeadManagementStationDetails::getEastBankTotal).reduce(Double::sum).orElse(0.00));
                total5.setCurrentWaterVolume((total5.getAmountTo()==null?0.0:total5.getAmountTo())*60*60*24);
                total5.setWaterVolumeFirstTenDays(firstTenDays==null?0.0:firstTenDays.getWaterVolumeFirstTenDays()==null?0.0:firstTenDays.getWaterVolumeFirstTenDays());
                total5.setAccumulatedWaterVolume(
                        (total5.getCurrentWaterVolume()==null?0.0:total5.getCurrentWaterVolume())+
                                (total5.getWaterVolumeFirstTenDays()==null?0.0:total5.getWaterVolumeFirstTenDays())
                );
                total5.setWaterVolumeDuringLastYear(lastYear==null?0.0:lastYear.getWaterVolumeDuringLastYear()==null?0.0:lastYear.getWaterVolumeDuringLastYear());
                total5.setCode("eastBankTotal");
                canalHeadManagementStationTotalList.add(total5);
                CanalHeadManagementStationTotal total6 = new CanalHeadManagementStationTotal();
                total6.setId(UUIDUtils.getUUID());
                total6.setDel(0);
                total6.setCreateTime(new Date());
                total6.setYear(canalHeadManagementStationDetails.getYear());
                total6.setMonth(canalHeadManagementStationDetails.getMonth());
                total6.setTenDays(canalHeadManagementStationDetails.getTenDays());
                total6.setAmountTo(list.stream().filter(t->t.getDongGan()!=null).map(CanalHeadManagementStationDetails::getDongGan).reduce(Double::sum).orElse(0.00));
                total6.setCurrentWaterVolume((total6.getAmountTo()==null?0.0:total6.getAmountTo())*60*60*24);
                total6.setWaterVolumeFirstTenDays(firstTenDays==null?0.0:firstTenDays.getWaterVolumeFirstTenDays()==null?0.0:firstTenDays.getWaterVolumeFirstTenDays());
                total6.setAccumulatedWaterVolume(
                        (total6.getCurrentWaterVolume()==null?0.0:total6.getCurrentWaterVolume())+
                                (total6.getWaterVolumeFirstTenDays()==null?0.0:total6.getWaterVolumeFirstTenDays())
                );
                total6.setWaterVolumeDuringLastYear(lastYear==null?0.0:lastYear.getWaterVolumeDuringLastYear()==null?0.0:lastYear.getWaterVolumeDuringLastYear());
                total6.setCode("dongGan");
                canalHeadManagementStationTotalList.add(total6);
                CanalHeadManagementStationTotal total7 = new CanalHeadManagementStationTotal();
                total7.setId(UUIDUtils.getUUID());
                total7.setDel(0);
                total7.setCreateTime(new Date());
                total7.setYear(canalHeadManagementStationDetails.getYear());
                total7.setMonth(canalHeadManagementStationDetails.getMonth());
                total7.setTenDays(canalHeadManagementStationDetails.getTenDays());
                total7.setAmountTo(list.stream().filter(t->t.getLanternCanalTotal()!=null).map(CanalHeadManagementStationDetails::getLanternCanalTotal).reduce(Double::sum).orElse(0.00));
                total7.setCurrentWaterVolume((total7.getAmountTo()==null?0.0:total7.getAmountTo())*60*60*24);
                total6.setWaterVolumeFirstTenDays(firstTenDays==null?0.0:firstTenDays.getWaterVolumeFirstTenDays()==null?0.0:firstTenDays.getWaterVolumeFirstTenDays());
                total6.setAccumulatedWaterVolume(
                        (total7.getCurrentWaterVolume()==null?0.0:total7.getCurrentWaterVolume())+
                                (total7.getWaterVolumeFirstTenDays()==null?0.0:total7.getWaterVolumeFirstTenDays())
                );
                total7.setWaterVolumeDuringLastYear(lastYear==null?0.0:lastYear.getWaterVolumeDuringLastYear()==null?0.0:lastYear.getWaterVolumeDuringLastYear());
                total7.setCode("lanternCanalTotal");
                canalHeadManagementStationTotalList.add(total7);
                CanalHeadManagementStationTotal total8 = new CanalHeadManagementStationTotal();
                total8.setId(UUIDUtils.getUUID());
                total8.setDel(0);
                total8.setCreateTime(new Date());
                total8.setYear(canalHeadManagementStationDetails.getYear());
                total8.setMonth(canalHeadManagementStationDetails.getMonth());
                total8.setTenDays(canalHeadManagementStationDetails.getTenDays());
                total8.setAmountTo(list.stream().filter(t->t.getLanternCanalAgriculture()!=null).map(CanalHeadManagementStationDetails::getLanternCanalAgriculture).reduce(Double::sum).orElse(0.00));
                total8.setCurrentWaterVolume((total8.getAmountTo()==null?0.0:total8.getAmountTo())*60*60*24);
                total8.setWaterVolumeFirstTenDays(firstTenDays==null?0.0:firstTenDays.getWaterVolumeFirstTenDays()==null?0.0:firstTenDays.getWaterVolumeFirstTenDays());
                total8.setAccumulatedWaterVolume(
                        (total8.getCurrentWaterVolume()==null?0.0:total8.getCurrentWaterVolume())+
                                (total8.getWaterVolumeFirstTenDays()==null?0.0:total8.getWaterVolumeFirstTenDays())
                );
                total8.setWaterVolumeDuringLastYear(lastYear==null?0.0:lastYear.getWaterVolumeDuringLastYear()==null?0.0:lastYear.getWaterVolumeDuringLastYear());
                total8.setCode("lanternCanalAgriculture");
                canalHeadManagementStationTotalList.add(total8);
                CanalHeadManagementStationTotal total9 = new CanalHeadManagementStationTotal();
                total9.setId(UUIDUtils.getUUID());
                total9.setDel(0);
                total9.setCreateTime(new Date());
                total9.setYear(canalHeadManagementStationDetails.getYear());
                total9.setMonth(canalHeadManagementStationDetails.getMonth());
                total9.setTenDays(canalHeadManagementStationDetails.getTenDays());
                total9.setAmountTo(list.stream().filter(t->t.getLanternCanalGreen()!=null).map(CanalHeadManagementStationDetails::getLanternCanalGreen).reduce(Double::sum).orElse(0.00));
                total9.setCurrentWaterVolume((total9.getAmountTo()==null?0.0:total9.getAmountTo())*60*60*24);
                total9.setWaterVolumeFirstTenDays(firstTenDays==null?0.0:firstTenDays.getWaterVolumeFirstTenDays()==null?0.0:firstTenDays.getWaterVolumeFirstTenDays());
                total9.setAccumulatedWaterVolume(
                        (total9.getCurrentWaterVolume()==null?0.0:total9.getCurrentWaterVolume())+
                                (total9.getWaterVolumeFirstTenDays()==null?0.0:total9.getWaterVolumeFirstTenDays())
                );
                total9.setWaterVolumeDuringLastYear(lastYear==null?0.0:lastYear.getWaterVolumeDuringLastYear()==null?0.0:lastYear.getWaterVolumeDuringLastYear());
                total9.setCode("lanternCanalGreen");
                canalHeadManagementStationTotalList.add(total9);
                CanalHeadManagementStationTotal total10 = new CanalHeadManagementStationTotal();
                total10.setId(UUIDUtils.getUUID());
                total10.setDel(0);
                total10.setCreateTime(new Date());
                total10.setYear(canalHeadManagementStationDetails.getYear());
                total10.setMonth(canalHeadManagementStationDetails.getMonth());
                total10.setTenDays(canalHeadManagementStationDetails.getTenDays());
                total10.setAmountTo(list.stream().filter(t->t.getLanternCanalIndustry()!=null).map(CanalHeadManagementStationDetails::getLanternCanalIndustry).reduce(Double::sum).orElse(0.00));
                total10.setCurrentWaterVolume((total10.getAmountTo()==null?0.0:total10.getAmountTo())*60*60*24);
                total10.setWaterVolumeFirstTenDays(firstTenDays==null?0.0:firstTenDays.getWaterVolumeFirstTenDays()==null?0.0:firstTenDays.getWaterVolumeFirstTenDays());
                total10.setAccumulatedWaterVolume(
                        (total10.getCurrentWaterVolume()==null?0.0:total10.getCurrentWaterVolume())+
                                (total10.getWaterVolumeFirstTenDays()==null?0.0:total10.getWaterVolumeFirstTenDays())
                );
                total10.setWaterVolumeDuringLastYear(lastYear==null?0.0:lastYear.getWaterVolumeDuringLastYear()==null?0.0:lastYear.getWaterVolumeDuringLastYear());
                total10.setCode("lanternCanalIndustry");
                canalHeadManagementStationTotalList.add(total10);
                CanalHeadManagementStationTotal total11 = new CanalHeadManagementStationTotal();
                total11.setId(UUIDUtils.getUUID());
                total11.setDel(0);
                total11.setCreateTime(new Date());
                total11.setYear(canalHeadManagementStationDetails.getYear());
                total11.setMonth(canalHeadManagementStationDetails.getMonth());
                total11.setTenDays(canalHeadManagementStationDetails.getTenDays());
                total11.setAmountTo(list.stream().filter(t->t.getFunnel()!=null).map(CanalHeadManagementStationDetails::getFunnel).reduce(Double::sum).orElse(0.00));
                total11.setCurrentWaterVolume((total11.getAmountTo()==null?0.0:total11.getAmountTo())*60*60*24);
                total11.setWaterVolumeFirstTenDays(firstTenDays==null?0.0:firstTenDays.getWaterVolumeFirstTenDays()==null?0.0:firstTenDays.getWaterVolumeFirstTenDays());
                total11.setAccumulatedWaterVolume(
                        (total11.getCurrentWaterVolume()==null?0.0:total11.getCurrentWaterVolume())+
                                (total11.getWaterVolumeFirstTenDays()==null?0.0:total11.getWaterVolumeFirstTenDays())
                );
                total11.setWaterVolumeDuringLastYear(lastYear==null?0.0:lastYear.getWaterVolumeDuringLastYear()==null?0.0:lastYear.getWaterVolumeDuringLastYear());
                total11.setCode("funnel");
                canalHeadManagementStationTotalList.add(total11);
                CanalHeadManagementStationTotal total12 = new CanalHeadManagementStationTotal();
                total12.setId(UUIDUtils.getUUID());
                total12.setDel(0);
                total12.setCreateTime(new Date());
                total12.setYear(canalHeadManagementStationDetails.getYear());
                total12.setMonth(canalHeadManagementStationDetails.getMonth());
                total12.setTenDays(canalHeadManagementStationDetails.getTenDays());
                total12.setAmountTo(list.stream().filter(t->t.getFloodDischarge()!=null).map(CanalHeadManagementStationDetails::getFloodDischarge).reduce(Double::sum).orElse(0.00));
                total12.setCurrentWaterVolume((total12.getAmountTo()==null?0.0:total12.getAmountTo())*60*60*24);
                total12.setWaterVolumeFirstTenDays(firstTenDays==null?0.0:firstTenDays.getWaterVolumeFirstTenDays()==null?0.0:firstTenDays.getWaterVolumeFirstTenDays());
                total12.setAccumulatedWaterVolume(
                        (total12.getCurrentWaterVolume()==null?0.0:total12.getCurrentWaterVolume())+
                                (total12.getWaterVolumeFirstTenDays()==null?0.0:total12.getWaterVolumeFirstTenDays())
                );
                total12.setWaterVolumeDuringLastYear(lastYear==null?0.0:lastYear.getWaterVolumeDuringLastYear()==null?0.0:lastYear.getWaterVolumeDuringLastYear());
                total12.setCode("floodDischarge");
                canalHeadManagementStationTotalList.add(total12);
                boolean b = canalHeadManagementStationTotalService.saveBatch(canalHeadManagementStationTotalList);
                if(b){
                    return RestResponse.ok("添加成功");
                }else {
                    return RestResponse.no("添加失败");
                }
            }else {
                return RestResponse.no("查询不到明细数据");
            }
        }else {
            return RestResponse.no("添加明细数据失败");
        }
    }

    @Override
    public RestResponse delete(String id) {
        boolean b = this.removeById(id);
        if(b){
            return RestResponse.ok("删除成功");
        }else {
            return RestResponse.no("删除失败");
        }
    }

    @Override
    public RestResponse updateDetails(CanalHeadManagementStationDetails canalHeadManagementStationDetails) {
        canalHeadManagementStationDetails.setEastBankTotal(
                (canalHeadManagementStationDetails.getLanternCanalTotal()==null?0.0:canalHeadManagementStationDetails.getLanternCanalTotal())+
                        (canalHeadManagementStationDetails.getDongGan()==null?0.0:canalHeadManagementStationDetails.getDongGan())
        );
        canalHeadManagementStationDetails.setDiversion(
                (canalHeadManagementStationDetails.getEastBankTotal()==null?0.0:canalHeadManagementStationDetails.getEastBankTotal())+
                        (canalHeadManagementStationDetails.getXiGan()==null?0.0:canalHeadManagementStationDetails.getXiGan())+
                        (canalHeadManagementStationDetails.getFunnel()==null?0.0:canalHeadManagementStationDetails.getFunnel())
        );
        canalHeadManagementStationDetails.setIncomingWater(
                (canalHeadManagementStationDetails.getDiversion()==null?0.0:canalHeadManagementStationDetails.getDiversion())+
                        (canalHeadManagementStationDetails.getFloodDischarge()==null?0.0:canalHeadManagementStationDetails.getFloodDischarge())
        );
        canalHeadManagementStationDetails.setTotalDry(
                (canalHeadManagementStationDetails.getDongGan()==null?0.0:canalHeadManagementStationDetails.getDongGan())+
                        (canalHeadManagementStationDetails.getXiGan()==null?0.0:canalHeadManagementStationDetails.getXiGan())+
                        (canalHeadManagementStationDetails.getFunnel()==null?0.0:canalHeadManagementStationDetails.getFunnel())
        );
        boolean b = this.updateById(canalHeadManagementStationDetails);
        if(b){
            return RestResponse.ok("更新成功");
        }else {
            return RestResponse.no("更新失败");
        }
    }

    @Override
    public RestResponse update(CanalHeadManagementStationDetails canalHeadManagementStationDetails) {
        RestResponse restResponse = updateDetails(canalHeadManagementStationDetails);
        if(restResponse.getCode()==200){
            List<CanalHeadManagementStationDetails> list = this.lambdaQuery().eq(CanalHeadManagementStationDetails::getYear, canalHeadManagementStationDetails.getYear()).
                    eq(CanalHeadManagementStationDetails::getMonth, canalHeadManagementStationDetails.getMonth()).
                    eq(CanalHeadManagementStationDetails::getTenDays, canalHeadManagementStationDetails.getTenDays()).list();
            boolean remove = canalHeadManagementStationTotalService.lambdaUpdate().eq(CanalHeadManagementStationTotal::getYear, canalHeadManagementStationDetails.getYear()).
                    eq(CanalHeadManagementStationTotal::getMonth, canalHeadManagementStationDetails.getMonth()).
                    eq(CanalHeadManagementStationTotal::getTenDays, canalHeadManagementStationDetails.getTenDays()).remove();
            if(remove){
                List<CanalHeadManagementStationTotal> canalHeadManagementStationTotalList = new ArrayList<>();
                if(null != list && list.size() > 0){
                    Map<String, Object> jisuan = jisuan(canalHeadManagementStationDetails.getYear(), canalHeadManagementStationDetails.getMonth(), canalHeadManagementStationDetails.getTenDays());
                    CanalHeadManagementStationTotal firstTenDays = canalHeadManagementStationTotalService.lambdaQuery().
                            eq(CanalHeadManagementStationTotal::getYear, jisuan.get("year")).
                            eq(CanalHeadManagementStationTotal::getMonth, jisuan.get("month")).
                            eq(CanalHeadManagementStationTotal::getTenDays, jisuan.get("tenDays")).
                            eq(CanalHeadManagementStationTotal::getCode, 1).one();
                    CanalHeadManagementStationTotal lastYear = canalHeadManagementStationTotalService.lambdaQuery().
                            eq(CanalHeadManagementStationTotal::getYear, canalHeadManagementStationDetails.getYear()-1).
                            eq(CanalHeadManagementStationTotal::getMonth, canalHeadManagementStationDetails.getMonth()).
                            eq(CanalHeadManagementStationTotal::getTenDays, canalHeadManagementStationDetails.getTenDays()).
                            eq(CanalHeadManagementStationTotal::getCode, 1).one();
                    CanalHeadManagementStationTotal total1 = new CanalHeadManagementStationTotal();
                    total1.setId(UUIDUtils.getUUID());
                    total1.setDel(0);
                    total1.setCreateTime(new Date());
                    total1.setYear(canalHeadManagementStationDetails.getYear());
                    total1.setMonth(canalHeadManagementStationDetails.getMonth());
                    total1.setTenDays(canalHeadManagementStationDetails.getTenDays());
                    total1.setAmountTo(list.stream().filter(t->t.getIncomingWater()!=null).map(CanalHeadManagementStationDetails::getIncomingWater).reduce(Double::sum).orElse(0.00));
                    total1.setCurrentWaterVolume((total1.getAmountTo()==null?0.0:total1.getAmountTo())*60*60*24);
                    total1.setWaterVolumeFirstTenDays(firstTenDays==null?0.0:firstTenDays.getWaterVolumeFirstTenDays()==null?0.0:firstTenDays.getWaterVolumeFirstTenDays());
                    total1.setAccumulatedWaterVolume(
                            (total1.getCurrentWaterVolume()==null?0.0:total1.getCurrentWaterVolume())+
                                    (total1.getWaterVolumeFirstTenDays()==null?0.0:total1.getWaterVolumeFirstTenDays())
                    );
                    total1.setWaterVolumeDuringLastYear(lastYear==null?0.0:lastYear.getWaterVolumeDuringLastYear()==null?0.0:lastYear.getWaterVolumeDuringLastYear());
                    total1.setCode("incomingWater");
                    canalHeadManagementStationTotalList.add(total1);
                    CanalHeadManagementStationTotal total2 = new CanalHeadManagementStationTotal();
                    total2.setId(UUIDUtils.getUUID());
                    total2.setDel(0);
                    total2.setCreateTime(new Date());
                    total2.setYear(canalHeadManagementStationDetails.getYear());
                    total2.setMonth(canalHeadManagementStationDetails.getMonth());
                    total2.setTenDays(canalHeadManagementStationDetails.getTenDays());
                    total2.setAmountTo(list.stream().filter(t->t.getDiversion()!=null).map(CanalHeadManagementStationDetails::getDiversion).reduce(Double::sum).orElse(0.00));
                    total2.setCurrentWaterVolume((total2.getAmountTo()==null?0.0:total2.getAmountTo())*60*60*24);
                    total2.setWaterVolumeFirstTenDays(firstTenDays==null?0.0:firstTenDays.getWaterVolumeFirstTenDays()==null?0.0:firstTenDays.getWaterVolumeFirstTenDays());
                    total2.setAccumulatedWaterVolume(
                            (total2.getCurrentWaterVolume()==null?0.0:total2.getCurrentWaterVolume())+
                                    (total2.getWaterVolumeFirstTenDays()==null?0.0:total2.getWaterVolumeFirstTenDays())
                    );
                    total2.setWaterVolumeDuringLastYear(lastYear==null?0.0:lastYear.getWaterVolumeDuringLastYear()==null?0.0:lastYear.getWaterVolumeDuringLastYear());
                    total2.setCode("diversion");
                    canalHeadManagementStationTotalList.add(total2);
                    CanalHeadManagementStationTotal total3 = new CanalHeadManagementStationTotal();
                    total3.setId(UUIDUtils.getUUID());
                    total3.setDel(0);
                    total3.setCreateTime(new Date());
                    total3.setYear(canalHeadManagementStationDetails.getYear());
                    total3.setMonth(canalHeadManagementStationDetails.getMonth());
                    total3.setTenDays(canalHeadManagementStationDetails.getTenDays());
                    total3.setAmountTo(list.stream().filter(t->t.getTotalDry()!=null).map(CanalHeadManagementStationDetails::getTotalDry).reduce(Double::sum).orElse(0.00));
                    total3.setCurrentWaterVolume((total3.getAmountTo()==null?0.0:total3.getAmountTo())*60*60*24);
                    total3.setWaterVolumeFirstTenDays(firstTenDays==null?0.0:firstTenDays.getWaterVolumeFirstTenDays()==null?0.0:firstTenDays.getWaterVolumeFirstTenDays());
                    total3.setAccumulatedWaterVolume(
                            (total3.getCurrentWaterVolume()==null?0.0:total3.getCurrentWaterVolume())+
                                    (total3.getWaterVolumeFirstTenDays()==null?0.0:total3.getWaterVolumeFirstTenDays())
                    );
                    total3.setWaterVolumeDuringLastYear(lastYear==null?0.0:lastYear.getWaterVolumeDuringLastYear()==null?0.0:lastYear.getWaterVolumeDuringLastYear());
                    total3.setCode("totalDry");
                    canalHeadManagementStationTotalList.add(total3);
                    CanalHeadManagementStationTotal total4 = new CanalHeadManagementStationTotal();
                    total4.setId(UUIDUtils.getUUID());
                    total4.setDel(0);
                    total4.setCreateTime(new Date());
                    total4.setYear(canalHeadManagementStationDetails.getYear());
                    total4.setMonth(canalHeadManagementStationDetails.getMonth());
                    total4.setTenDays(canalHeadManagementStationDetails.getTenDays());
                    total4.setAmountTo(list.stream().filter(t->t.getXiGan()!=null).map(CanalHeadManagementStationDetails::getXiGan).reduce(Double::sum).orElse(0.00));
                    total4.setCurrentWaterVolume((total4.getAmountTo()==null?0.0:total4.getAmountTo())*60*60*24);
                    total4.setWaterVolumeFirstTenDays(firstTenDays==null?0.0:firstTenDays.getWaterVolumeFirstTenDays()==null?0.0:firstTenDays.getWaterVolumeFirstTenDays());
                    total4.setAccumulatedWaterVolume(
                            (total4.getCurrentWaterVolume()==null?0.0:total4.getCurrentWaterVolume())+
                                    (total4.getWaterVolumeFirstTenDays()==null?0.0:total4.getWaterVolumeFirstTenDays())
                    );
                    total4.setWaterVolumeDuringLastYear(lastYear==null?0.0:lastYear.getWaterVolumeDuringLastYear()==null?0.0:lastYear.getWaterVolumeDuringLastYear());
                    total4.setCode("xiGan");
                    canalHeadManagementStationTotalList.add(total4);
                    CanalHeadManagementStationTotal total5 = new CanalHeadManagementStationTotal();
                    total5.setId(UUIDUtils.getUUID());
                    total5.setDel(0);
                    total5.setCreateTime(new Date());
                    total5.setYear(canalHeadManagementStationDetails.getYear());
                    total5.setMonth(canalHeadManagementStationDetails.getMonth());
                    total5.setTenDays(canalHeadManagementStationDetails.getTenDays());
                    total5.setAmountTo(list.stream().filter(t->t.getEastBankTotal()!=null).map(CanalHeadManagementStationDetails::getEastBankTotal).reduce(Double::sum).orElse(0.00));
                    total5.setCurrentWaterVolume((total5.getAmountTo()==null?0.0:total5.getAmountTo())*60*60*24);
                    total5.setWaterVolumeFirstTenDays(firstTenDays==null?0.0:firstTenDays.getWaterVolumeFirstTenDays()==null?0.0:firstTenDays.getWaterVolumeFirstTenDays());
                    total5.setAccumulatedWaterVolume(
                            (total5.getCurrentWaterVolume()==null?0.0:total5.getCurrentWaterVolume())+
                                    (total5.getWaterVolumeFirstTenDays()==null?0.0:total5.getWaterVolumeFirstTenDays())
                    );
                    total5.setWaterVolumeDuringLastYear(lastYear==null?0.0:lastYear.getWaterVolumeDuringLastYear()==null?0.0:lastYear.getWaterVolumeDuringLastYear());
                    total5.setCode("eastBankTotal");
                    canalHeadManagementStationTotalList.add(total5);
                    CanalHeadManagementStationTotal total6 = new CanalHeadManagementStationTotal();
                    total6.setId(UUIDUtils.getUUID());
                    total6.setDel(0);
                    total6.setCreateTime(new Date());
                    total6.setYear(canalHeadManagementStationDetails.getYear());
                    total6.setMonth(canalHeadManagementStationDetails.getMonth());
                    total6.setTenDays(canalHeadManagementStationDetails.getTenDays());
                    total6.setAmountTo(list.stream().filter(t->t.getDongGan()!=null).map(CanalHeadManagementStationDetails::getDongGan).reduce(Double::sum).orElse(0.00));
                    total6.setCurrentWaterVolume((total6.getAmountTo()==null?0.0:total6.getAmountTo())*60*60*24);
                    total6.setWaterVolumeFirstTenDays(firstTenDays==null?0.0:firstTenDays.getWaterVolumeFirstTenDays()==null?0.0:firstTenDays.getWaterVolumeFirstTenDays());
                    total6.setAccumulatedWaterVolume(
                            (total6.getCurrentWaterVolume()==null?0.0:total6.getCurrentWaterVolume())+
                                    (total6.getWaterVolumeFirstTenDays()==null?0.0:total6.getWaterVolumeFirstTenDays())
                    );
                    total6.setWaterVolumeDuringLastYear(lastYear==null?0.0:lastYear.getWaterVolumeDuringLastYear()==null?0.0:lastYear.getWaterVolumeDuringLastYear());
                    total6.setCode("dongGan");
                    canalHeadManagementStationTotalList.add(total6);
                    CanalHeadManagementStationTotal total7 = new CanalHeadManagementStationTotal();
                    total7.setId(UUIDUtils.getUUID());
                    total7.setDel(0);
                    total7.setCreateTime(new Date());
                    total7.setYear(canalHeadManagementStationDetails.getYear());
                    total7.setMonth(canalHeadManagementStationDetails.getMonth());
                    total7.setTenDays(canalHeadManagementStationDetails.getTenDays());
                    total7.setAmountTo(list.stream().filter(t->t.getLanternCanalTotal()!=null).map(CanalHeadManagementStationDetails::getLanternCanalTotal).reduce(Double::sum).orElse(0.00));
                    total7.setCurrentWaterVolume((total7.getAmountTo()==null?0.0:total7.getAmountTo())*60*60*24);
                    total6.setWaterVolumeFirstTenDays(firstTenDays==null?0.0:firstTenDays.getWaterVolumeFirstTenDays()==null?0.0:firstTenDays.getWaterVolumeFirstTenDays());
                    total6.setAccumulatedWaterVolume(
                            (total7.getCurrentWaterVolume()==null?0.0:total7.getCurrentWaterVolume())+
                                    (total7.getWaterVolumeFirstTenDays()==null?0.0:total7.getWaterVolumeFirstTenDays())
                    );
                    total7.setWaterVolumeDuringLastYear(lastYear==null?0.0:lastYear.getWaterVolumeDuringLastYear()==null?0.0:lastYear.getWaterVolumeDuringLastYear());
                    total7.setCode("lanternCanalTotal");
                    canalHeadManagementStationTotalList.add(total7);
                    CanalHeadManagementStationTotal total8 = new CanalHeadManagementStationTotal();
                    total8.setId(UUIDUtils.getUUID());
                    total8.setDel(0);
                    total8.setCreateTime(new Date());
                    total8.setYear(canalHeadManagementStationDetails.getYear());
                    total8.setMonth(canalHeadManagementStationDetails.getMonth());
                    total8.setTenDays(canalHeadManagementStationDetails.getTenDays());
                    total8.setAmountTo(list.stream().filter(t->t.getLanternCanalAgriculture()!=null).map(CanalHeadManagementStationDetails::getLanternCanalAgriculture).reduce(Double::sum).orElse(0.00));
                    total8.setCurrentWaterVolume((total8.getAmountTo()==null?0.0:total8.getAmountTo())*60*60*24);
                    total8.setWaterVolumeFirstTenDays(firstTenDays==null?0.0:firstTenDays.getWaterVolumeFirstTenDays()==null?0.0:firstTenDays.getWaterVolumeFirstTenDays());
                    total8.setAccumulatedWaterVolume(
                            (total8.getCurrentWaterVolume()==null?0.0:total8.getCurrentWaterVolume())+
                                    (total8.getWaterVolumeFirstTenDays()==null?0.0:total8.getWaterVolumeFirstTenDays())
                    );
                    total8.setWaterVolumeDuringLastYear(lastYear==null?0.0:lastYear.getWaterVolumeDuringLastYear()==null?0.0:lastYear.getWaterVolumeDuringLastYear());
                    total8.setCode("lanternCanalAgriculture");
                    canalHeadManagementStationTotalList.add(total8);
                    CanalHeadManagementStationTotal total9 = new CanalHeadManagementStationTotal();
                    total9.setId(UUIDUtils.getUUID());
                    total9.setDel(0);
                    total9.setCreateTime(new Date());
                    total9.setYear(canalHeadManagementStationDetails.getYear());
                    total9.setMonth(canalHeadManagementStationDetails.getMonth());
                    total9.setTenDays(canalHeadManagementStationDetails.getTenDays());
                    total9.setAmountTo(list.stream().filter(t->t.getLanternCanalGreen()!=null).map(CanalHeadManagementStationDetails::getLanternCanalGreen).reduce(Double::sum).orElse(0.00));
                    total9.setCurrentWaterVolume((total9.getAmountTo()==null?0.0:total9.getAmountTo())*60*60*24);
                    total9.setWaterVolumeFirstTenDays(firstTenDays==null?0.0:firstTenDays.getWaterVolumeFirstTenDays()==null?0.0:firstTenDays.getWaterVolumeFirstTenDays());
                    total9.setAccumulatedWaterVolume(
                            (total9.getCurrentWaterVolume()==null?0.0:total9.getCurrentWaterVolume())+
                                    (total9.getWaterVolumeFirstTenDays()==null?0.0:total9.getWaterVolumeFirstTenDays())
                    );
                    total9.setWaterVolumeDuringLastYear(lastYear==null?0.0:lastYear.getWaterVolumeDuringLastYear()==null?0.0:lastYear.getWaterVolumeDuringLastYear());
                    total9.setCode("lanternCanalGreen");
                    canalHeadManagementStationTotalList.add(total9);
                    CanalHeadManagementStationTotal total10 = new CanalHeadManagementStationTotal();
                    total10.setId(UUIDUtils.getUUID());
                    total10.setDel(0);
                    total10.setCreateTime(new Date());
                    total10.setYear(canalHeadManagementStationDetails.getYear());
                    total10.setMonth(canalHeadManagementStationDetails.getMonth());
                    total10.setTenDays(canalHeadManagementStationDetails.getTenDays());
                    total10.setAmountTo(list.stream().filter(t->t.getLanternCanalIndustry()!=null).map(CanalHeadManagementStationDetails::getLanternCanalIndustry).reduce(Double::sum).orElse(0.00));
                    total10.setCurrentWaterVolume((total10.getAmountTo()==null?0.0:total10.getAmountTo())*60*60*24);
                    total10.setWaterVolumeFirstTenDays(firstTenDays==null?0.0:firstTenDays.getWaterVolumeFirstTenDays()==null?0.0:firstTenDays.getWaterVolumeFirstTenDays());
                    total10.setAccumulatedWaterVolume(
                            (total10.getCurrentWaterVolume()==null?0.0:total10.getCurrentWaterVolume())+
                                    (total10.getWaterVolumeFirstTenDays()==null?0.0:total10.getWaterVolumeFirstTenDays())
                    );
                    total10.setWaterVolumeDuringLastYear(lastYear==null?0.0:lastYear.getWaterVolumeDuringLastYear()==null?0.0:lastYear.getWaterVolumeDuringLastYear());
                    total10.setCode("lanternCanalIndustry");
                    canalHeadManagementStationTotalList.add(total10);
                    CanalHeadManagementStationTotal total11 = new CanalHeadManagementStationTotal();
                    total11.setId(UUIDUtils.getUUID());
                    total11.setDel(0);
                    total11.setCreateTime(new Date());
                    total11.setYear(canalHeadManagementStationDetails.getYear());
                    total11.setMonth(canalHeadManagementStationDetails.getMonth());
                    total11.setTenDays(canalHeadManagementStationDetails.getTenDays());
                    total11.setAmountTo(list.stream().filter(t->t.getFunnel()!=null).map(CanalHeadManagementStationDetails::getFunnel).reduce(Double::sum).orElse(0.00));
                    total11.setCurrentWaterVolume((total11.getAmountTo()==null?0.0:total11.getAmountTo())*60*60*24);
                    total11.setWaterVolumeFirstTenDays(firstTenDays==null?0.0:firstTenDays.getWaterVolumeFirstTenDays()==null?0.0:firstTenDays.getWaterVolumeFirstTenDays());
                    total11.setAccumulatedWaterVolume(
                            (total11.getCurrentWaterVolume()==null?0.0:total11.getCurrentWaterVolume())+
                                    (total11.getWaterVolumeFirstTenDays()==null?0.0:total11.getWaterVolumeFirstTenDays())
                    );
                    total11.setWaterVolumeDuringLastYear(lastYear==null?0.0:lastYear.getWaterVolumeDuringLastYear()==null?0.0:lastYear.getWaterVolumeDuringLastYear());
                    total11.setCode("funnel");
                    canalHeadManagementStationTotalList.add(total11);
                    CanalHeadManagementStationTotal total12 = new CanalHeadManagementStationTotal();
                    total12.setId(UUIDUtils.getUUID());
                    total12.setDel(0);
                    total12.setCreateTime(new Date());
                    total12.setYear(canalHeadManagementStationDetails.getYear());
                    total12.setMonth(canalHeadManagementStationDetails.getMonth());
                    total12.setTenDays(canalHeadManagementStationDetails.getTenDays());
                    total12.setAmountTo(list.stream().filter(t->t.getFloodDischarge()!=null).map(CanalHeadManagementStationDetails::getFloodDischarge).reduce(Double::sum).orElse(0.00));
                    total12.setCurrentWaterVolume((total12.getAmountTo()==null?0.0:total12.getAmountTo())*60*60*24);
                    total12.setWaterVolumeFirstTenDays(firstTenDays==null?0.0:firstTenDays.getWaterVolumeFirstTenDays()==null?0.0:firstTenDays.getWaterVolumeFirstTenDays());
                    total12.setAccumulatedWaterVolume(
                            (total12.getCurrentWaterVolume()==null?0.0:total12.getCurrentWaterVolume())+
                                    (total12.getWaterVolumeFirstTenDays()==null?0.0:total12.getWaterVolumeFirstTenDays())
                    );
                    total12.setWaterVolumeDuringLastYear(lastYear==null?0.0:lastYear.getWaterVolumeDuringLastYear()==null?0.0:lastYear.getWaterVolumeDuringLastYear());
                    total12.setCode("floodDischarge");
                    canalHeadManagementStationTotalList.add(total12);
                    boolean b = canalHeadManagementStationTotalService.saveBatch(canalHeadManagementStationTotalList);
                    if(b){
                        return RestResponse.ok("添加成功");
                    }else {
                        return RestResponse.no("添加失败");
                    }
                }else {
                    return RestResponse.no("查询不到明细数据");
                }
            }else {
                return RestResponse.no("更新失败");
            }

        }else {
            return RestResponse.no("更新失败");
        }
    }

    @Override
    public RestResponse selectList(WaterFeeStatisticsDetailsSelectListReq req) {
        List<CanalHeadManagementStationDetails> list = this.lambdaQuery().eq(CanalHeadManagementStationDetails::getYear, req.getYear()).
                eq(CanalHeadManagementStationDetails::getMonth, req.getMonth()).
                eq(CanalHeadManagementStationDetails::getTenDays, req.getTenDays()).list();
        if(null != list && list.size() > 0){
            return RestResponse.ok(list);
        }else {
            return RestResponse.no("暂无数据");
        }
    }

    @Override
    public RestResponse remove(WaterFeeStatisticsDetailsSelectListReq req) {
        boolean remove = this.lambdaUpdate().eq(CanalHeadManagementStationDetails::getYear, req.getYear()).
                eq(CanalHeadManagementStationDetails::getMonth, req.getMonth()).
                eq(CanalHeadManagementStationDetails::getTenDays, req.getTenDays()).remove();
        boolean remove1 = canalHeadManagementStationTotalService.lambdaUpdate().eq(CanalHeadManagementStationTotal::getYear, req.getYear()).
                eq(CanalHeadManagementStationTotal::getMonth, req.getMonth()).
                eq(CanalHeadManagementStationTotal::getTenDays, req.getTenDays()).remove();
        boolean remove2 = waterDistributionRatioService.lambdaUpdate().eq(WaterDistributionRatio::getStation, req.getStation()).
                eq(WaterDistributionRatio::getYear, req.getYear()).
                eq(WaterDistributionRatio::getMonth, req.getMonth()).
                eq(WaterDistributionRatio::getTenDays, req.getTenDays()).remove();
        if(remove1&&remove&&remove2){
            return RestResponse.ok();
        }else {
            return RestResponse.no("error");
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
}

