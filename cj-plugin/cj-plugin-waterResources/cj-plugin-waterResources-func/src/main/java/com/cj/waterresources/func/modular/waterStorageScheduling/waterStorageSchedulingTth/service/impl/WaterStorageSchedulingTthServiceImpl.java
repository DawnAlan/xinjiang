package com.cj.waterresources.func.modular.waterStorageScheduling.waterStorageSchedulingTth.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.auth.core.pojo.SaBaseLoginUser;
import com.cj.auth.core.util.StpLoginUserUtil;
import com.cj.common.model.RestResponse;
import com.cj.common.util.NumberUtil;
import com.cj.common.util.RedisUtil;
import com.cj.common.util.UUIDUtils;
import com.cj.middleDatabase.func.modular.lzz.storageCapacityCurve.entity.StorageCapacityCurve;
import com.cj.middleDatabase.func.modular.lzz.storageCapacityCurve.service.StorageCapacityCurveService;
import com.cj.waterresources.func.core.utils.GetSzyDataUtils;
import com.cj.waterresources.func.modular.overallSituationUnitMgr.entity.OverallSituationUnitMgr;
import com.cj.waterresources.func.modular.overallSituationUnitMgr.service.OverallSituationUnitMgrService;
import com.cj.waterresources.func.modular.surfaceWater.entity.QueryListReq;
import com.cj.waterresources.func.modular.surfaceWater.generator.domain.SurfaceWater;
import com.cj.waterresources.func.modular.surfaceWater.generator.service.SurfaceWaterFlowDetailService;
import com.cj.waterresources.func.modular.surfaceWater.generator.service.SurfaceWaterService;
import com.cj.waterresources.func.modular.surfaceWater.vo.TenDayVo;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.bean.req.TrunkCanalSelectListReq;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.bean.res.SelectYearWaterUsePlanCropForSum;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.bean.res.SelectYearWaterUsePlanTrunkCanalForSum;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.entity.YearWaterUsePlanCrop;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.service.YearWaterUsePlanCropService;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.service.YearWaterUsePlanTrunkCanalService;
import com.cj.waterresources.func.modular.waterStorageScheduling.waterStorageSchedulingTotalForm.entity.WaterStorageSchedulingTotalForm;
import com.cj.waterresources.func.modular.waterStorageScheduling.waterStorageSchedulingTotalForm.service.WaterStorageSchedulingTotalFormService;
import com.cj.waterresources.func.modular.waterStorageScheduling.waterStorageSchedulingTth.entity.WaterStorageSchedulingTth;
import com.cj.waterresources.func.modular.waterStorageScheduling.waterStorageSchedulingTth.mapper.WaterStorageSchedulingTthMapper;
import com.cj.waterresources.func.modular.waterStorageScheduling.waterStorageSchedulingTth.service.WaterStorageSchedulingTthService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 头屯河水库蓄水调度计划表(WaterStorageSchedulingTth)表服务实现类
 *
 * @author makejava
 * @since 2023-12-12 10:20:46
 */
@Service("waterStorageSchedulingTthService")
public class WaterStorageSchedulingTthServiceImpl extends ServiceImpl<WaterStorageSchedulingTthMapper, WaterStorageSchedulingTth> implements WaterStorageSchedulingTthService {

    @Autowired
    private YearWaterUsePlanTrunkCanalService yearWaterUsePlanTrunkCanalService;

    @Autowired
    private YearWaterUsePlanCropService yearWaterUsePlanCropService;

    @Autowired
    private WaterStorageSchedulingTotalFormService waterStorageSchedulingTotalFormService;

    @Autowired
    private OverallSituationUnitMgrService overallSituationUnitMgrService;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse add(String formId) {
        SaBaseLoginUser saBaseLoginUser = StpLoginUserUtil.getLoginUser();
        WaterStorageSchedulingTotalForm byId = waterStorageSchedulingTotalFormService.getById(formId);
        if(null==byId){
            return RestResponse.no("方案有误，请重新选择");
        }
        List<TenDayVo> inflowData = JSONArray.parseArray(defaultIncomingWaterData,TenDayVo.class);
        Map<Integer, List<TenDayVo>> collect = inflowData.stream().collect(Collectors.groupingBy(TenDayVo::getMonth));
        Set<Integer> integers = collect.keySet();
        //排序
        List<Integer> list = new ArrayList<>(integers);
        Collections.sort(list);
        List<WaterStorageSchedulingTth> waterStorageSchedulingTthList = new LinkedList<>();
        SelectYearWaterUsePlanCropForSum ny = yearWaterUsePlanCropService.selectListForSum(byId.getYear(), "农业");
        List<TenDayVo> nyData = getData4_11(ny);
        TrunkCanalSelectListReq req = new TrunkCanalSelectListReq();
        req.setYear(byId.getYear());
        req.setUseWaterPlan("年用水计划");
        req.setArea("绿化");
        SelectYearWaterUsePlanTrunkCanalForSum lhTemp = yearWaterUsePlanTrunkCanalService.selectListForSum(req);
        List<TenDayVo> lhData = getData1_12(lhTemp);
        req.setArea("工业");
        SelectYearWaterUsePlanTrunkCanalForSum gyTemp = yearWaterUsePlanTrunkCanalService.selectListForSum(req);
        List<TenDayVo> gyData = getData1_12(gyTemp);
        req.setArea("红岩");
        SelectYearWaterUsePlanTrunkCanalForSum hyTemp = yearWaterUsePlanTrunkCanalService.selectListForSum(req);
        List<TenDayVo> hyData = getData1_12(hyTemp);
        Integer sortNum = 1;
        for(Integer month:list){
            List<TenDayVo> tenDayVos = collect.get(month);
            tenDayVos.sort(Comparator.comparing(t -> t.getName()));
            for(TenDayVo vo : tenDayVos){
                WaterStorageSchedulingTth tth = new WaterStorageSchedulingTth();
                tth.setId(UUIDUtils.getUUID());
                tth.setDel(0);
                tth.setCreateBy(saBaseLoginUser.getName());
                tth.setCreateTime(new Date());
                tth.setFormId(formId);
                tth.setMonth(vo.getMonth());
                tth.setTenDays(vo.getName()==1?"上旬":vo.getName()==2?"中旬":"下旬");
                tth.setYear(byId.getYear());
                tth.setWaterStorageLevel(changeNum(vo.getValue().doubleValue()));
                tth.setFineTuning(100.00);
                //tth.setFineTuningReservoirInflow(changeNum((tth.getFineTuning()/100)*tth.getReservoirInflow()));
                List<TenDayVo> ggTemp = nyData.stream().filter(t -> t.getMonth() == month && t.getName()==vo.getName()).collect(Collectors.toList());
                if(null != ggTemp && ggTemp.size()>0){
                    TenDayVo gg = ggTemp.get(0);
                    tth.setIrrigationWaterDemand(gg==null?null:gg.getValue()==null?null:changeNum(gg.getValue().doubleValue()));
                }
                List<TenDayVo> lhTempData = lhData.stream().filter(t -> t.getMonth() == month && t.getName()==vo.getName()).collect(Collectors.toList());
                if(null != lhTempData && lhTempData.size()>0){
                    TenDayVo lh = lhTempData.get(0);
                    tth.setGreenWaterDemand(lh==null?null:lh.getValue()==null?null:changeNum(lh.getValue().doubleValue()));
                }
                List<TenDayVo> gyTempData = gyData.stream().filter(t -> t.getMonth() == month && t.getName()==vo.getName()).collect(Collectors.toList());
                if(null != gyTempData && gyTempData.size()>0){
                    TenDayVo gy = gyTempData.get(0);
                    tth.setIndustryWaterDemand(gy==null?null:gy.getValue()==null?null:changeNum(gy.getValue().doubleValue()));
                }
                List<TenDayVo> hyTempData = hyData.stream().filter(t -> t.getMonth() == month && t.getName()==vo.getName()).collect(Collectors.toList());
                if(null != hyTempData && hyTempData.size()>0){
                    TenDayVo hy = hyTempData.get(0);
                    tth.setHongyanWaterDemand(hy==null?null:hy.getValue()==null?null:changeNum(hy.getValue().doubleValue()));
                }
                tth.setWaterSupplyVolumeTotal(changeNum(
                            (tth.getIrrigationWaterDemand()==null?0.0:tth.getIrrigationWaterDemand())+
                            (tth.getGreenWaterDemand()==null?0.0:tth.getGreenWaterDemand())+
                            (tth.getIndustryWaterDemand()==null?0.0:tth.getIndustryWaterDemand())+
                            (tth.getHongyanWaterDemand()==null?0.0:tth.getHongyanWaterDemand())+
                            (tth.getWaterLoss()==null?0.0:tth.getWaterLoss())
                        )
                );
                tth.setRegulatingWaterStorageCapacity(changeNum(
                            (tth.getFineTuningReservoirInflow()==null?0.0:tth.getFineTuningReservoirInflow())-
                            (tth.getWaterSupplyVolumeTotal()==null?0.0:tth.getWaterSupplyVolumeTotal())
                        )
                );
                if(vo.getMonth()==1 && vo.getName()==1){
                    List<OverallSituationUnitMgr> unitList = getUnitList();
                    String id = unitList.stream().filter(t -> t.getPId().equals("0") && t.getUnitName().equals("头屯河水库")).map(OverallSituationUnitMgr::getId).findFirst().get();
                    Double waterLevelByLevel1 = GetSzyDataUtils.getWaterLevelByLevel(byId.getFirstData(), id);
                    Double waterLevelByLevel = GetSzyDataUtils.getWaterLevelByLevel(inflowData.get(0).getValue().doubleValue(), id);
                    tth.setRegulatingWaterStorageCapacity(waterLevelByLevel-waterLevelByLevel1);
                }
                tth.setSortNum(sortNum++);
                waterStorageSchedulingTthList.add(tth);
            }
        }
        List<OverallSituationUnitMgr> unitList = getUnitList();
        String id = unitList.stream().filter(t -> t.getPId().equals("0") && t.getUnitName().equals("头屯河水库")).map(OverallSituationUnitMgr::getId).findFirst().get();
        waterStorageSchedulingTthList.forEach(t->{
            t.setWaterStorage(GetSzyDataUtils.getWaterLevelByLevel(t.getWaterStorageLevel(),id));
        });
        for(int j=1;j<waterStorageSchedulingTthList.size();j++){
            WaterStorageSchedulingTth tth = waterStorageSchedulingTthList.get(j);
            tth.setRegulatingWaterStorageCapacity(changeNum(tth.getWaterStorage()-waterStorageSchedulingTthList.get(j-1).getWaterStorage()));
        }
        waterStorageSchedulingTthList.forEach(t->{
            t.setReservoirInflow(t.getWaterSupplyVolumeTotal()+t.getRegulatingWaterStorageCapacity());
        });
        boolean b = this.saveBatch(waterStorageSchedulingTthList);
        if(b){
            return RestResponse.ok();
        }else {
            return RestResponse.no("error");
        }
    }

    private List<OverallSituationUnitMgr> getUnitList(){
        String overall = (String) redisUtil.get("overallSituationUnitMgr:list");
        if(StringUtils.isEmpty(overall)){
            List<OverallSituationUnitMgr> overallSituationUnitMgrList = overallSituationUnitMgrService.list();
            redisUtil.set("overallSituationUnitMgr:list", com.alibaba.fastjson.JSONObject.toJSONString(overallSituationUnitMgrList));
            overall = JSONObject.toJSONString(overallSituationUnitMgrList);
        }
        List<OverallSituationUnitMgr> list = JSONObject.parseArray(overall, OverallSituationUnitMgr.class);
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse edit(WaterStorageSchedulingTth tth) {
        List<OverallSituationUnitMgr> unitList = getUnitList();
        String id = unitList.stream().filter(t -> t.getPId().equals("0") && t.getUnitName().equals("头屯河水库")).map(OverallSituationUnitMgr::getId).findFirst().get();
        tth.setWaterStorage(GetSzyDataUtils.getWaterLevelByLevel(tth.getWaterStorageLevel(),id));
        if(tth.getSortNum()==1){
            WaterStorageSchedulingTotalForm byId = waterStorageSchedulingTotalFormService.getById(tth.getFormId());
            Double waterLevelByLevel = GetSzyDataUtils.getWaterLevelByLevel(byId.getFirstData(), id);
            tth.setRegulatingWaterStorageCapacity(NumberUtil.holdDecimal(tth.getWaterStorage()-waterLevelByLevel,2));
        }else {
            WaterStorageSchedulingTth one = this.lambdaQuery().eq(WaterStorageSchedulingTth::getFormId, tth.getFormId()).eq(WaterStorageSchedulingTth::getSortNum, tth.getSortNum() - 1).one();
            tth.setRegulatingWaterStorageCapacity(NumberUtil.holdDecimal(one.getWaterStorage()-tth.getWaterStorage(),2));
        }
        tth.setWaterSupplyVolumeTotal(changeNum(
                    (tth.getIrrigationWaterDemand()==null?0.0:tth.getIrrigationWaterDemand())+
                    (tth.getGreenWaterDemand()==null?0.0:tth.getGreenWaterDemand())+
                    (tth.getIndustryWaterDemand()==null?0.0:tth.getIndustryWaterDemand())+
                    (tth.getHongyanWaterDemand()==null?0.0:tth.getHongyanWaterDemand())+
                    (tth.getEcologicalBaseFlow()==null?0.0:tth.getEcologicalBaseFlow())+
                    (tth.getWaterLoss()==null?0.0:tth.getWaterLoss())
                )
        );
        tth.setReservoirInflow(changeNum(
                    (tth.getRegulatingWaterStorageCapacity()==null?0.0:tth.getRegulatingWaterStorageCapacity())+
                    (tth.getWaterSupplyVolumeTotal()==null?0.0:tth.getWaterSupplyVolumeTotal())
                )
        );
        boolean b = this.updateById(tth);
        if(b){
            if(updateAll(tth.getFormId())){
                return RestResponse.ok();
            }else {
                return RestResponse.no("false2");
            }
        }else {
            return RestResponse.no("false1");
        }
    }

    private Boolean updateAll(String formId){
        List<WaterStorageSchedulingTth> waterStorageSchedulingTthList = this.lambdaQuery().eq(WaterStorageSchedulingTth::getFormId,formId).list();
        waterStorageSchedulingTthList.sort(Comparator.comparing(t -> t.getSortNum()));
        for(int j=1;j<waterStorageSchedulingTthList.size();j++){
            WaterStorageSchedulingTth tth = waterStorageSchedulingTthList.get(j);
            tth.setRegulatingWaterStorageCapacity(NumberUtil.holdDecimal((tth.getWaterStorage()-waterStorageSchedulingTthList.get(j-1).getWaterStorage()),2));
        }
        waterStorageSchedulingTthList.forEach(t-> {
            t.setReservoirInflow(t.getRegulatingWaterStorageCapacity()+t.getWaterSupplyVolumeTotal());
        });
        boolean b = this.saveOrUpdateBatch(waterStorageSchedulingTthList);
        if(b){
            return true;
        }else {
            return false;
        }
    }

    private List<TenDayVo> getData4_11(SelectYearWaterUsePlanCropForSum gg){
        if(null==gg){
            return new ArrayList<TenDayVo>();
        }
        List<TenDayVo> voList = new ArrayList<>();
        TenDayVo vo4_1 = new TenDayVo();
        vo4_1.setMonth(4);
        vo4_1.setName(1);
        vo4_1.setValue(gg.getAprilEarlyOctober()==null?null:new BigDecimal(gg.getAprilEarlyOctober()).setScale(2, RoundingMode.HALF_UP));
        voList.add(vo4_1);
        TenDayVo vo4_2 = new TenDayVo();
        vo4_2.setMonth(4);
        vo4_2.setName(2);
        vo4_2.setValue(gg.getAprilMidDay()==null?null:new BigDecimal(gg.getAprilMidDay()).setScale(2, RoundingMode.HALF_UP));
        voList.add(vo4_2);
        TenDayVo vo4_3 = new TenDayVo();
        vo4_3.setMonth(4);
        vo4_3.setName(3);
        vo4_3.setValue(gg.getAprilLaterOctober()==null?null:new BigDecimal(gg.getAprilLaterOctober()).setScale(2, RoundingMode.HALF_UP));
        voList.add(vo4_3);

        TenDayVo vo5_1 = new TenDayVo();
        vo5_1.setMonth(5);
        vo5_1.setName(1);
        vo5_1.setValue(gg.getMayEarlyOctober()==null?null:new BigDecimal(gg.getMayEarlyOctober()).setScale(2, RoundingMode.HALF_UP));
        voList.add(vo5_1);
        TenDayVo vo5_2 = new TenDayVo();
        vo5_2.setMonth(5);
        vo5_2.setName(2);
        vo5_2.setValue(gg.getMayMidDay()==null?null:new BigDecimal(gg.getMayMidDay()).setScale(2, RoundingMode.HALF_UP));
        voList.add(vo5_2);
        TenDayVo vo5_3 = new TenDayVo();
        vo5_3.setMonth(5);
        vo5_3.setName(3);
        vo5_3.setValue(gg.getMayLaterOctober()==null?null:new BigDecimal(gg.getMayLaterOctober()).setScale(2, RoundingMode.HALF_UP));
        voList.add(vo5_3);

        TenDayVo vo6_1 = new TenDayVo();
        vo6_1.setMonth(6);
        vo6_1.setName(1);
        vo6_1.setValue(gg.getJuneEarlyOctober()==null?null:new BigDecimal(gg.getJuneEarlyOctober()).setScale(2, RoundingMode.HALF_UP));
        voList.add(vo6_1);
        TenDayVo vo6_2 = new TenDayVo();
        vo6_2.setMonth(6);
        vo6_2.setName(2);
        vo6_2.setValue(gg.getJuneMidDay()==null?null:new BigDecimal(gg.getJuneMidDay()).setScale(2, RoundingMode.HALF_UP));
        voList.add(vo6_2);
        TenDayVo vo6_3 = new TenDayVo();
        vo6_3.setMonth(6);
        vo6_3.setName(3);
        vo6_3.setValue(gg.getJuneLaterOctober()==null?null:new BigDecimal(gg.getJuneLaterOctober()).setScale(2, RoundingMode.HALF_UP));
        voList.add(vo6_3);

        TenDayVo vo7_1 = new TenDayVo();
        vo7_1.setMonth(7);
        vo7_1.setName(1);
        vo7_1.setValue(gg.getJulyEarlyOctober()==null?null:new BigDecimal(gg.getJulyEarlyOctober()).setScale(2, RoundingMode.HALF_UP));
        voList.add(vo7_1);
        TenDayVo vo7_2 = new TenDayVo();
        vo7_2.setMonth(7);
        vo7_2.setName(2);
        vo7_2.setValue(gg.getJulyMidDay()==null?null:new BigDecimal(gg.getJulyMidDay()).setScale(2, RoundingMode.HALF_UP));
        voList.add(vo7_2);
        TenDayVo vo7_3 = new TenDayVo();
        vo7_3.setMonth(7);
        vo7_3.setName(3);
        vo7_3.setValue(gg.getJulyLaterOctober()==null?null:new BigDecimal(gg.getJulyLaterOctober()).setScale(2, RoundingMode.HALF_UP));
        voList.add(vo7_3);

        TenDayVo vo8_1 = new TenDayVo();
        vo8_1.setMonth(8);
        vo8_1.setName(1);
        vo8_1.setValue(gg.getAugustEarlyOctober()==null?null:new BigDecimal(gg.getAugustEarlyOctober()).setScale(2, RoundingMode.HALF_UP));
        voList.add(vo8_1);
        TenDayVo vo8_2 = new TenDayVo();
        vo8_2.setMonth(8);
        vo8_2.setName(2);
        vo8_2.setValue(gg.getAugustMidDay()==null?null:new BigDecimal(gg.getAugustMidDay()).setScale(2, RoundingMode.HALF_UP));
        voList.add(vo8_2);
        TenDayVo vo8_3 = new TenDayVo();
        vo8_3.setMonth(8);
        vo8_3.setName(3);
        vo8_3.setValue(gg.getAugustLaterOctober()==null?null:new BigDecimal(gg.getAugustLaterOctober()).setScale(2, RoundingMode.HALF_UP));
        voList.add(vo8_3);

        TenDayVo vo9_1 = new TenDayVo();
        vo9_1.setMonth(9);
        vo9_1.setName(1);
        vo9_1.setValue(gg.getSeptemberEarlyOctober()==null?null:new BigDecimal(gg.getSeptemberEarlyOctober()).setScale(2, RoundingMode.HALF_UP));
        voList.add(vo9_1);
        TenDayVo vo9_2 = new TenDayVo();
        vo9_2.setMonth(9);
        vo9_2.setName(2);
        vo9_2.setValue(gg.getSeptemberMidDay()==null?null:new BigDecimal(gg.getSeptemberMidDay()).setScale(2, RoundingMode.HALF_UP));
        voList.add(vo9_2);
        TenDayVo vo9_3 = new TenDayVo();
        vo9_3.setMonth(9);
        vo9_3.setName(3);
        vo9_3.setValue(gg.getSeptemberLaterOctober()==null?null:new BigDecimal(gg.getSeptemberLaterOctober()).setScale(2, RoundingMode.HALF_UP));
        voList.add(vo9_3);

        TenDayVo vo10_1 = new TenDayVo();
        vo10_1.setMonth(10);
        vo10_1.setName(1);
        vo10_1.setValue(gg.getOctoberEarlyOctober()==null?null:new BigDecimal(gg.getOctoberEarlyOctober()).setScale(2, RoundingMode.HALF_UP));
        voList.add(vo10_1);
        TenDayVo vo10_2 = new TenDayVo();
        vo10_2.setMonth(10);
        vo10_2.setName(2);
        vo10_2.setValue(gg.getOctoberMidDay()==null?null:new BigDecimal(gg.getOctoberMidDay()).setScale(2, RoundingMode.HALF_UP));
        voList.add(vo10_2);
        TenDayVo vo10_3 = new TenDayVo();
        vo10_3.setMonth(10);
        vo10_3.setName(3);
        vo10_3.setValue(gg.getOctoberLaterOctober()==null?null:new BigDecimal(gg.getOctoberLaterOctober()).setScale(2, RoundingMode.HALF_UP));
        voList.add(vo10_3);

        TenDayVo vo11_1 = new TenDayVo();
        vo11_1.setMonth(11);
        vo11_1.setName(1);
        vo11_1.setValue(gg.getNovemberEarlyOctober()==null?null:new BigDecimal(gg.getNovemberEarlyOctober()).setScale(2, RoundingMode.HALF_UP));
        voList.add(vo11_1);
        TenDayVo vo11_2 = new TenDayVo();
        vo11_2.setMonth(11);
        vo11_2.setName(2);
        vo11_2.setValue(gg.getNovemberMidDay()==null?null:new BigDecimal(gg.getNovemberMidDay()).setScale(2, RoundingMode.HALF_UP));
        voList.add(vo11_2);
        TenDayVo vo11_3 = new TenDayVo();
        vo11_3.setMonth(11);
        vo11_3.setName(3);
        vo11_3.setValue(gg.getNovemberLaterOctober()==null?null:new BigDecimal(gg.getNovemberLaterOctober()).setScale(2, RoundingMode.HALF_UP));
        voList.add(vo11_3);
        return voList;
    }

    private List<TenDayVo> getData1_12(SelectYearWaterUsePlanTrunkCanalForSum sum){
        if(null==sum){
            return new ArrayList<>();
        }
        List<TenDayVo> voList = new ArrayList<>();
        TenDayVo vo1_1 = new TenDayVo();
        vo1_1.setMonth(1);
        vo1_1.setName(1);
        vo1_1.setValue(sum.getJanuary()==null?null:new BigDecimal(sum.getJanuary()/3).setScale(2, RoundingMode.HALF_UP));
        voList.add(vo1_1);
        TenDayVo vo1_2 = new TenDayVo();
        vo1_2.setMonth(1);
        vo1_2.setName(2);
        vo1_2.setValue(sum.getJanuary()==null?null:new BigDecimal(sum.getJanuary()/3).setScale(2, RoundingMode.HALF_UP));
        voList.add(vo1_2);
        TenDayVo vo1_3 = new TenDayVo();
        vo1_3.setMonth(1);
        vo1_3.setName(3);
        vo1_3.setValue(sum.getJanuary()==null?null:new BigDecimal(sum.getJanuary()/3).setScale(2, RoundingMode.HALF_UP));
        voList.add(vo1_3);

        TenDayVo vo2_1 = new TenDayVo();
        vo2_1.setMonth(2);
        vo2_1.setName(1);
        vo2_1.setValue(sum.getFebruary()==null?null:new BigDecimal(sum.getFebruary()/3).setScale(2, RoundingMode.HALF_UP));
        voList.add(vo2_1);
        TenDayVo vo2_2 = new TenDayVo();
        vo2_2.setMonth(2);
        vo2_2.setName(2);
        vo2_2.setValue(sum.getFebruary()==null?null:new BigDecimal(sum.getFebruary()/3).setScale(2, RoundingMode.HALF_UP));
        voList.add(vo2_2);
        TenDayVo vo2_3 = new TenDayVo();
        vo2_3.setMonth(2);
        vo2_3.setName(3);
        vo2_3.setValue(sum.getFebruary()==null?null:new BigDecimal(sum.getFebruary()/3).setScale(2, RoundingMode.HALF_UP));
        voList.add(vo2_3);

        TenDayVo vo3_1 = new TenDayVo();
        vo3_1.setMonth(3);
        vo3_1.setName(1);
        vo3_1.setValue(sum.getMarch()==null?null:new BigDecimal(sum.getMarch()/3).setScale(2, RoundingMode.HALF_UP));
        voList.add(vo3_1);
        TenDayVo vo3_2 = new TenDayVo();
        vo3_2.setMonth(3);
        vo3_2.setName(2);
        vo3_2.setValue(sum.getMarch()==null?null:new BigDecimal(sum.getMarch()/3).setScale(2, RoundingMode.HALF_UP));
        voList.add(vo3_2);
        TenDayVo vo3_3 = new TenDayVo();
        vo3_3.setMonth(3);
        vo3_3.setName(3);
        vo3_3.setValue(sum.getMarch()==null?null:new BigDecimal(sum.getMarch()/3).setScale(2, RoundingMode.HALF_UP));
        voList.add(vo3_3);


        TenDayVo vo4_1 = new TenDayVo();
        vo4_1.setMonth(4);
        vo4_1.setName(1);
        vo4_1.setValue(sum.getApril()==null?null:new BigDecimal(sum.getApril()/3).setScale(2, RoundingMode.HALF_UP));
        voList.add(vo4_1);
        TenDayVo vo4_2 = new TenDayVo();
        vo4_2.setMonth(4);
        vo4_2.setName(2);
        vo4_2.setValue(sum.getApril()==null?null:new BigDecimal(sum.getApril()/3).setScale(2, RoundingMode.HALF_UP));
        voList.add(vo4_2);
        TenDayVo vo4_3 = new TenDayVo();
        vo4_3.setMonth(4);
        vo4_3.setName(3);
        vo4_3.setValue(sum.getApril()==null?null:new BigDecimal(sum.getApril()/3).setScale(2, RoundingMode.HALF_UP));
        voList.add(vo4_3);

        TenDayVo vo5_1 = new TenDayVo();
        vo5_1.setMonth(5);
        vo5_1.setName(1);
        vo5_1.setValue(sum.getMay()==null?null:new BigDecimal(sum.getMay()/3).setScale(2, RoundingMode.HALF_UP));
        voList.add(vo5_1);
        TenDayVo vo5_2 = new TenDayVo();
        vo5_2.setMonth(5);
        vo5_2.setName(2);
        vo5_2.setValue(sum.getMay()==null?null:new BigDecimal(sum.getMay()/3).setScale(2, RoundingMode.HALF_UP));
        voList.add(vo5_2);
        TenDayVo vo5_3 = new TenDayVo();
        vo5_3.setMonth(5);
        vo5_3.setName(3);
        vo5_3.setValue(sum.getMay()==null?null:new BigDecimal(sum.getMay()/3).setScale(2, RoundingMode.HALF_UP));
        voList.add(vo5_3);

        TenDayVo vo6_1 = new TenDayVo();
        vo6_1.setMonth(6);
        vo6_1.setName(1);
        vo6_1.setValue(sum.getJune()==null?null:new BigDecimal(sum.getJune()/3).setScale(2, RoundingMode.HALF_UP));
        voList.add(vo6_1);
        TenDayVo vo6_2 = new TenDayVo();
        vo6_2.setMonth(6);
        vo6_2.setName(2);
        vo6_2.setValue(sum.getJune()==null?null:new BigDecimal(sum.getJune()/3).setScale(2, RoundingMode.HALF_UP));
        voList.add(vo6_2);
        TenDayVo vo6_3 = new TenDayVo();
        vo6_3.setMonth(6);
        vo6_3.setName(3);
        vo6_3.setValue(sum.getJune()==null?null:new BigDecimal(sum.getJune()/3).setScale(2, RoundingMode.HALF_UP));
        voList.add(vo6_3);

        TenDayVo vo7_1 = new TenDayVo();
        vo7_1.setMonth(7);
        vo7_1.setName(1);
        vo7_1.setValue(sum.getJuly()==null?null:new BigDecimal(sum.getJuly()/3).setScale(2, RoundingMode.HALF_UP));
        voList.add(vo7_1);
        TenDayVo vo7_2 = new TenDayVo();
        vo7_2.setMonth(7);
        vo7_2.setName(2);
        vo7_2.setValue(sum.getJuly()==null?null:new BigDecimal(sum.getJuly()/3).setScale(2, RoundingMode.HALF_UP));
        voList.add(vo7_2);
        TenDayVo vo7_3 = new TenDayVo();
        vo7_3.setMonth(7);
        vo7_3.setName(3);
        vo7_3.setValue(sum.getJuly()==null?null:new BigDecimal(sum.getJuly()/3).setScale(2, RoundingMode.HALF_UP));
        voList.add(vo7_3);

        TenDayVo vo8_1 = new TenDayVo();
        vo8_1.setMonth(8);
        vo8_1.setName(1);
        vo8_1.setValue(sum.getAugust()==null?null:new BigDecimal(sum.getAugust()/3).setScale(2, RoundingMode.HALF_UP));
        voList.add(vo8_1);
        TenDayVo vo8_2 = new TenDayVo();
        vo8_2.setMonth(8);
        vo8_2.setName(2);
        vo8_2.setValue(sum.getAugust()==null?null:new BigDecimal(sum.getAugust()/3).setScale(2, RoundingMode.HALF_UP));
        voList.add(vo8_2);
        TenDayVo vo8_3 = new TenDayVo();
        vo8_3.setMonth(8);
        vo8_3.setName(3);
        vo8_3.setValue(sum.getAugust()==null?null:new BigDecimal(sum.getAugust()/3).setScale(2, RoundingMode.HALF_UP));
        voList.add(vo8_3);

        TenDayVo vo9_1 = new TenDayVo();
        vo9_1.setMonth(9);
        vo9_1.setName(1);
        vo9_1.setValue(sum.getSeptember()==null?null:new BigDecimal(sum.getSeptember()/3).setScale(2, RoundingMode.HALF_UP));
        voList.add(vo9_1);
        TenDayVo vo9_2 = new TenDayVo();
        vo9_2.setMonth(9);
        vo9_2.setName(2);
        vo9_2.setValue(sum.getSeptember()==null?null:new BigDecimal(sum.getSeptember()/3).setScale(2, RoundingMode.HALF_UP));
        voList.add(vo9_2);
        TenDayVo vo9_3 = new TenDayVo();
        vo9_3.setMonth(9);
        vo9_3.setName(3);
        vo9_3.setValue(sum.getSeptember()==null?null:new BigDecimal(sum.getSeptember()/3).setScale(2, RoundingMode.HALF_UP));
        voList.add(vo9_3);

        TenDayVo vo10_1 = new TenDayVo();
        vo10_1.setMonth(10);
        vo10_1.setName(1);
        vo10_1.setValue(sum.getOctober()==null?null:new BigDecimal(sum.getOctober()/3).setScale(2, RoundingMode.HALF_UP));
        voList.add(vo10_1);
        TenDayVo vo10_2 = new TenDayVo();
        vo10_2.setMonth(10);
        vo10_2.setName(2);
        vo10_2.setValue(sum.getOctober()==null?null:new BigDecimal(sum.getOctober()/3).setScale(2, RoundingMode.HALF_UP));
        voList.add(vo10_2);
        TenDayVo vo10_3 = new TenDayVo();
        vo10_3.setMonth(10);
        vo10_3.setName(3);
        vo10_3.setValue(sum.getOctober()==null?null:new BigDecimal(sum.getOctober()/3).setScale(2, RoundingMode.HALF_UP));
        voList.add(vo10_3);

        TenDayVo vo11_1 = new TenDayVo();
        vo11_1.setMonth(11);
        vo11_1.setName(1);
        vo11_1.setValue(sum.getNovember()==null?null:new BigDecimal(sum.getNovember()/3).setScale(2, RoundingMode.HALF_UP));
        voList.add(vo11_1);
        TenDayVo vo11_2 = new TenDayVo();
        vo11_2.setMonth(11);
        vo11_2.setName(2);
        vo11_2.setValue(sum.getNovember()==null?null:new BigDecimal(sum.getNovember()/3).setScale(2, RoundingMode.HALF_UP));
        voList.add(vo11_2);
        TenDayVo vo11_3 = new TenDayVo();
        vo11_3.setMonth(11);
        vo11_3.setName(3);
        vo11_3.setValue(sum.getNovember()==null?null:new BigDecimal(sum.getNovember()/3).setScale(2, RoundingMode.HALF_UP));
        voList.add(vo11_3);

        TenDayVo vo12_1 = new TenDayVo();
        vo12_1.setMonth(12);
        vo12_1.setName(1);
        vo12_1.setValue(sum.getDecember()==null?null:new BigDecimal(sum.getDecember()/3).setScale(2, RoundingMode.HALF_UP));
        voList.add(vo12_1);
        TenDayVo vo12_2 = new TenDayVo();
        vo12_2.setMonth(12);
        vo12_2.setName(2);
        vo12_2.setValue(sum.getDecember()==null?null:new BigDecimal(sum.getDecember()/3).setScale(2, RoundingMode.HALF_UP));
        voList.add(vo12_2);
        TenDayVo vo12_3 = new TenDayVo();
        vo12_3.setMonth(12);
        vo12_3.setName(3);
        vo12_3.setValue(sum.getDecember()==null?null:new BigDecimal(sum.getDecember()/3).setScale(2, RoundingMode.HALF_UP));
        voList.add(vo12_3);
        return voList;
    }

    private Double changeNum(Double num){
        DecimalFormat format2 = new DecimalFormat("#.00");
        String format = format2.format(num);
        return Double.parseDouble(format);
    }

    private String defaultIncomingWaterData = "[\n" +
            "    {\n" +
            "        \"month\":1,\n" +
            "        \"name\":1,\n" +
            "        \"value\":987.04\n" +
            "    },\n" +
            "    {\n" +
            "        \"month\":1,\n" +
            "        \"name\":2,\n" +
            "        \"value\":987.34\n" +
            "    },\n" +
            "    {\n" +
            "        \"month\":1,\n" +
            "        \"name\":3,\n" +
            "        \"value\":987.15\n" +
            "    },\n" +
            "    {\n" +
            "        \"month\":2,\n" +
            "        \"name\":1,\n" +
            "        \"value\":987.11\n" +
            "    },\n" +
            "    {\n" +
            "        \"month\":2,\n" +
            "        \"name\":2,\n" +
            "        \"value\":986.99\n" +
            "    },\n" +
            "    {\n" +
            "        \"month\":2,\n" +
            "        \"name\":3,\n" +
            "        \"value\":986.84\n" +
            "    },\n" +
            "    {\n" +
            "        \"month\":3,\n" +
            "        \"name\":1,\n" +
            "        \"value\":986.16\n" +
            "    },\n" +
            "    {\n" +
            "        \"month\":3,\n" +
            "        \"name\":2,\n" +
            "        \"value\":985.56\n" +
            "    },\n" +
            "    {\n" +
            "        \"month\":3,\n" +
            "        \"name\":3,\n" +
            "        \"value\":985.30\n" +
            "    },\n" +
            "    {\n" +
            "        \"month\":4,\n" +
            "        \"name\":1,\n" +
            "        \"value\":984.89\n" +
            "    },\n" +
            "    {\n" +
            "        \"month\":4,\n" +
            "        \"name\":2,\n" +
            "        \"value\":984.61\n" +
            "    },\n" +
            "    {\n" +
            "        \"month\":4,\n" +
            "        \"name\":3,\n" +
            "        \"value\":982.95\n" +
            "    },\n" +
            "    {\n" +
            "        \"month\":5,\n" +
            "        \"name\":1,\n" +
            "        \"value\":978.38\n" +
            "    },\n" +
            "    {\n" +
            "        \"month\":5,\n" +
            "        \"name\":2,\n" +
            "        \"value\":978.90\n" +
            "    },\n" +
            "    {\n" +
            "        \"month\":5,\n" +
            "        \"name\":3,\n" +
            "        \"value\":976.84\n" +
            "    },\n" +
            "    {\n" +
            "        \"month\":6,\n" +
            "        \"name\":1,\n" +
            "        \"value\":977.78\n" +
            "    },\n" +
            "    {\n" +
            "        \"month\":6,\n" +
            "        \"name\":2,\n" +
            "        \"value\":982.41\n" +
            "    },\n" +
            "    {\n" +
            "        \"month\":6,\n" +
            "        \"name\":3,\n" +
            "        \"value\":981.60\n" +
            "    },\n" +
            "    {\n" +
            "        \"month\":7,\n" +
            "        \"name\":1,\n" +
            "        \"value\":978.21\n" +
            "    },\n" +
            "    {\n" +
            "        \"month\":7,\n" +
            "        \"name\":2,\n" +
            "        \"value\":977.33\n" +
            "    },\n" +
            "    {\n" +
            "        \"month\":7,\n" +
            "        \"name\":3,\n" +
            "        \"value\":975.60\n" +
            "    },\n" +
            "    {\n" +
            "        \"month\":8,\n" +
            "        \"name\":1,\n" +
            "        \"value\":975.41\n" +
            "    },\n" +
            "    {\n" +
            "        \"month\":8,\n" +
            "        \"name\":2,\n" +
            "        \"value\":978.16\n" +
            "    },\n" +
            "    {\n" +
            "        \"month\":8,\n" +
            "        \"name\":3,\n" +
            "        \"value\":978.50\n" +
            "    },\n" +
            "    {\n" +
            "        \"month\":9,\n" +
            "        \"name\":1,\n" +
            "        \"value\":979.49\n" +
            "    },\n" +
            "    {\n" +
            "        \"month\":9,\n" +
            "        \"name\":2,\n" +
            "        \"value\":982.05\n" +
            "    },\n" +
            "    {\n" +
            "        \"month\":9,\n" +
            "        \"name\":3,\n" +
            "        \"value\":982.76\n" +
            "    },\n" +
            "    {\n" +
            "        \"month\":10,\n" +
            "        \"name\":1,\n" +
            "        \"value\":982.60\n" +
            "    },\n" +
            "    {\n" +
            "        \"month\":10,\n" +
            "        \"name\":2,\n" +
            "        \"value\":978.49\n" +
            "    },\n" +
            "    {\n" +
            "        \"month\":10,\n" +
            "        \"name\":3,\n" +
            "        \"value\":976.69\n" +
            "    },\n" +
            "    {\n" +
            "        \"month\":11,\n" +
            "        \"name\":1,\n" +
            "        \"value\":977.01\n" +
            "    },\n" +
            "    {\n" +
            "        \"month\":11,\n" +
            "        \"name\":2,\n" +
            "        \"value\":978.92\n" +
            "    },\n" +
            "    {\n" +
            "        \"month\":11,\n" +
            "        \"name\":3,\n" +
            "        \"value\":979.85\n" +
            "    },\n" +
            "    {\n" +
            "        \"month\":12,\n" +
            "        \"name\":1,\n" +
            "        \"value\":980.45\n" +
            "    },\n" +
            "    {\n" +
            "        \"month\":12,\n" +
            "        \"name\":2,\n" +
            "        \"value\":980.39\n" +
            "    },\n" +
            "    {\n" +
            "        \"month\":12,\n" +
            "        \"name\":3,\n" +
            "        \"value\":980.52\n" +
            "    }\n" +
            "]\n";
}

