package com.cj.waterresources.func.modular.waterStorageScheduling.waterStorageSchedulingLzz.service.impl;

import com.alibaba.fastjson.JSONObject;
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
import com.cj.waterresources.func.modular.surfaceWater.entity.TypicalYearReq;
import com.cj.waterresources.func.modular.surfaceWater.generator.domain.SurfaceWater;
import com.cj.waterresources.func.modular.surfaceWater.generator.service.SurfaceWaterFlowDetailService;
import com.cj.waterresources.func.modular.surfaceWater.generator.service.SurfaceWaterService;
import com.cj.waterresources.func.modular.surfaceWater.vo.TenDayVo;
import com.cj.waterresources.func.modular.surfaceWater.vo.TypicalYearVo;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.bean.req.TrunkCanalSelectListReq;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.bean.res.SelectYearWaterUsePlanTrunkCanalForSum;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.service.YearWaterUsePlanCropService;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.service.YearWaterUsePlanTrunkCanalService;
import com.cj.waterresources.func.modular.waterStorageScheduling.waterStorageSchedulingLzz.bean.res.RealFlowRes;
import com.cj.waterresources.func.modular.waterStorageScheduling.waterStorageSchedulingLzz.entity.WaterStorageSchedulingLzz;
import com.cj.waterresources.func.modular.waterStorageScheduling.waterStorageSchedulingLzz.mapper.WaterStorageSchedulingLzzMapper;
import com.cj.waterresources.func.modular.waterStorageScheduling.waterStorageSchedulingLzz.service.WaterStorageSchedulingLzzService;
import com.cj.waterresources.func.modular.waterStorageScheduling.waterStorageSchedulingTotalForm.entity.WaterStorageSchedulingTotalForm;
import com.cj.waterresources.func.modular.waterStorageScheduling.waterStorageSchedulingTotalForm.service.WaterStorageSchedulingTotalFormService;
import com.cj.waterresources.func.modular.waterStorageScheduling.waterStorageSchedulingTth.entity.WaterStorageSchedulingTth;
import com.cj.waterresources.func.modular.waterStorageScheduling.waterStorageSchedulingTth.service.WaterStorageSchedulingTthService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 楼庄子水库蓄水调度计划表(WaterStorageSchedulingLzz)表服务实现类
 *
 * @author makejava
 * @since 2023-12-12 10:20:22
 */
@Service("waterStorageSchedulingLzzService")
public class WaterStorageSchedulingLzzServiceImpl extends ServiceImpl<WaterStorageSchedulingLzzMapper, WaterStorageSchedulingLzz> implements WaterStorageSchedulingLzzService {


    @Autowired
    private WaterStorageSchedulingTotalFormService waterStorageSchedulingTotalFormService;

    @Autowired
    private SurfaceWaterFlowDetailService surfaceWaterFlowDetailService;

    @Autowired
    private SurfaceWaterService surfaceWaterService;

    @Autowired
    private StorageCapacityCurveService storageCapacityCurveService;

    @Autowired
    private WaterStorageSchedulingTthService waterStorageSchedulingTthService;

    @Autowired
    private YearWaterUsePlanTrunkCanalService yearWaterUsePlanTrunkCanalService;

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
        List<RealFlowRes> incomingWaterData = getIncomingWaterData(byId.getInflowYear());
        Comparator<RealFlowRes> realFlowResComparator = Comparator.comparing(RealFlowRes::getMonth).thenComparing(RealFlowRes::getName);
        //正序
        //resultList.sort(realFlowResComparator);
        //倒叙
        Collections.sort(incomingWaterData, realFlowResComparator.reversed());

        Map<Integer, List<RealFlowRes>> collect = incomingWaterData.stream().collect(Collectors.groupingBy(RealFlowRes::getMonth));
        Set<Integer> integers = collect.keySet();
        //排序
        List<Integer> list = new ArrayList<>(integers);
        Collections.sort(list);
        TrunkCanalSelectListReq req = new TrunkCanalSelectListReq();
        req.setYear(byId.getYear());
        req.setUseWaterPlan("年用水计划");
        req.setArea("楼庄子水厂");
        SelectYearWaterUsePlanTrunkCanalForSum lzzTemp = yearWaterUsePlanTrunkCanalService.selectListForSum(req);
        List<TenDayVo> lzzData = getData1_12(lzzTemp);
        List<WaterStorageSchedulingTth> tth = waterStorageSchedulingTthService.lambdaQuery().eq(WaterStorageSchedulingTth::getFormId, formId).list();
        if(tth.isEmpty()){
            return RestResponse.no("请先创建头屯河水库");
        }
        List<WaterStorageSchedulingLzz> waterStorageSchedulingLzzList = new LinkedList<>();
        Integer sortNum = 1;
        for(Integer month:list){
            List<RealFlowRes> tenDayVos = collect.get(month);
            tenDayVos.sort(Comparator.comparing(t -> t.getName()));
            for(RealFlowRes vo : tenDayVos){
                WaterStorageSchedulingLzz lzz = new WaterStorageSchedulingLzz();
                lzz.setId(UUIDUtils.getUUID());
                lzz.setDel(0);
                lzz.setCreateBy(saBaseLoginUser.getName());
                lzz.setCreateTime(new Date());
                lzz.setFormId(formId);
                lzz.setMonth(vo.getMonth());
                lzz.setTenDays(vo.getName()==1?"上旬":vo.getName()==2?"中旬":"下旬");
                lzz.setYear(byId.getYear());
                lzz.setReservoirInflow(vo.getFlow()==null?0.0:changeNum(vo.getFlow().doubleValue()));
                lzz.setFineTuning(100.00);
                lzz.setFineTuningReservoirInflow(lzz.getReservoirInflow()==null?0.0:changeNum((lzz.getFineTuning()/100)*lzz.getReservoirInflow()));
                List<TenDayVo> lzzTempData = lzzData.stream().filter(t -> t.getMonth() == vo.getMonth() && t.getName() == vo.getName()).collect(Collectors.toList());
                if(null != lzzTempData && lzzTempData.size()>0){
                    TenDayVo lzzResultData = lzzTempData.get(0);
                    lzz.setWaterPlantDemand(lzzResultData.getValue()==null?0.0:changeNum(lzzResultData.getValue().doubleValue()));
                }
                List<WaterStorageSchedulingTth> tthDataTemp = tth.stream().filter(t -> t.getMonth() == vo.getMonth() && t.getTenDays().equals(lzz.getTenDays())).collect(Collectors.toList());
                if(null != tthDataTemp && tthDataTemp.size()>0){
                    WaterStorageSchedulingTth tthData = tthDataTemp.get(0);
                    lzz.setReservoirWaterDemand(changeNum(tthData.getReservoirInflow()));
                }
                lzz.setWaterSupplyVolumeTotal(changeNum(
                            (lzz.getWaterPlantDemand()==null?0.0:lzz.getWaterPlantDemand())+
                            (lzz.getReservoirWaterDemand()==null?0.0:lzz.getReservoirWaterDemand())+
                             (lzz.getWaterLoss()==null?0.0:lzz.getWaterLoss())
                        )
                );
                lzz.setRegulatingWaterStorageCapacity(changeNum(
                            (lzz.getFineTuningReservoirInflow()==null?0.0:lzz.getFineTuningReservoirInflow())-
                            (lzz.getWaterSupplyVolumeTotal()==null?0.0:lzz.getWaterSupplyVolumeTotal())
                        )
                );
                lzz.setSortNum(sortNum++);
                waterStorageSchedulingLzzList.add(lzz);
            }
        }
        List<OverallSituationUnitMgr> unitList = getUnitList();
        String id = unitList.stream().filter(t -> t.getPId().equals("0") && t.getUnitName().equals("楼庄子水库")).map(OverallSituationUnitMgr::getId).findFirst().get();
        Double waterLevelByLevel1 = GetSzyDataUtils.getWaterLevelByLevel(byId.getSecondData(), id);
        waterStorageSchedulingLzzList.get(0).setWaterStorage(waterLevelByLevel1+waterStorageSchedulingLzzList.get(0).getRegulatingWaterStorageCapacity());
        for(int j=1;j<waterStorageSchedulingLzzList.size();j++){
            WaterStorageSchedulingLzz lzz = waterStorageSchedulingLzzList.get(j);
            lzz.setWaterStorage(changeNum(
                        (waterStorageSchedulingLzzList.get(j-1).getWaterStorage())+
                        (lzz.getRegulatingWaterStorageCapacity())
                    )
            );
        }
        waterStorageSchedulingLzzList.forEach(t->t.setWaterStorageLevel(GetSzyDataUtils.getWaterLevelByFlow(t.getWaterStorage(),id)));
        boolean b = this.saveBatch(waterStorageSchedulingLzzList);
        if(b){
            return RestResponse.ok();
        }else {
            return RestResponse.no("error");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse edit(List<WaterStorageSchedulingLzz> waterStorageSchedulingLzzList) {
        for(WaterStorageSchedulingLzz lzz:waterStorageSchedulingLzzList){
            lzz.setFineTuningReservoirInflow((lzz.getFineTuning()/100)*lzz.getReservoirInflow());
            lzz.setWaterSupplyVolumeTotal(
                    changeNum(
                        (lzz.getWaterPlantDemand()==null?0.0:lzz.getWaterPlantDemand())+
                        (lzz.getReservoirWaterDemand()==null?0.0:lzz.getReservoirWaterDemand())+
                        (lzz.getWaterLoss()==null?0.0:lzz.getWaterLoss())
                    )
            );
            lzz.setRegulatingWaterStorageCapacity(changeNum
                    (
                        (lzz.getFineTuningReservoirInflow()==null?0.0:lzz.getFineTuningReservoirInflow())-
                        (lzz.getWaterSupplyVolumeTotal()==null?0.0:lzz.getWaterSupplyVolumeTotal())
                    )
            );
            if(lzz.getSortNum()==1){
                List<OverallSituationUnitMgr> unitList = getUnitList();
                String id = unitList.stream().filter(t -> t.getPId().equals("0") && t.getUnitName().equals("楼庄子水库")).map(OverallSituationUnitMgr::getId).findFirst().get();
                WaterStorageSchedulingTotalForm byId = waterStorageSchedulingTotalFormService.getById(lzz.getFormId());
                Double waterLevelByLevel = GetSzyDataUtils.getWaterLevelByLevel(byId.getSecondData(), id);
                lzz.setWaterStorage(lzz.getRegulatingWaterStorageCapacity()+waterLevelByLevel);
                lzz.setWaterStorageLevel(GetSzyDataUtils.getWaterLevelByFlow(lzz.getWaterStorage(), id));
            }
        }

        boolean b = this.updateBatchById(waterStorageSchedulingLzzList);
        if(b){
            if(updateAll(waterStorageSchedulingLzzList.get(0).getFormId())){
                return RestResponse.ok();
            }else {
                return RestResponse.no("false2");
            }
        }else {
            return RestResponse.no("false1");
        }
    }

    private Boolean updateAll(String formId){
        List<WaterStorageSchedulingLzz> waterStorageSchedulingLzzList = this.lambdaQuery().eq(WaterStorageSchedulingLzz::getFormId,formId).list();
        waterStorageSchedulingLzzList.sort(Comparator.comparing(t -> t.getSortNum()));
        for(int j=1;j<waterStorageSchedulingLzzList.size();j++){
            WaterStorageSchedulingLzz lzz = waterStorageSchedulingLzzList.get(j);
            lzz.setWaterStorage(changeNum
                    (
                        (waterStorageSchedulingLzzList.get(j-1).getWaterStorage())+
                        (lzz.getRegulatingWaterStorageCapacity())
                    )
            );
        }
        List<OverallSituationUnitMgr> unitList = getUnitList();
        String id = unitList.stream().filter(t -> t.getPId().equals("0") && t.getUnitName().equals("楼庄子水库")).map(OverallSituationUnitMgr::getId).findFirst().get();
        waterStorageSchedulingLzzList.forEach(t->t.setWaterStorageLevel(GetSzyDataUtils.getWaterLevelByFlow(t.getWaterStorage(),id)));
        boolean b = this.saveOrUpdateBatch(waterStorageSchedulingLzzList);
        if(b){
            return true;
        }else {
            return false;
        }
    }

    private List<TenDayVo> getData1_12(SelectYearWaterUsePlanTrunkCanalForSum sum){
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

    private List<RealFlowRes> getIncomingWaterData(Integer year){
        LocalDateTime now = LocalDateTime.now();
        int month = now.getMonth().getValue();
        int day = now.getDayOfMonth();
        String startTime = now.getYear()+ "-01-01";
        List<Map<String, Object>> maps = surfaceWaterService.annualList();
        TypicalYearVo typicalYearVo = surfaceWaterService.typicalYear(new TypicalYearReq());
        if(month>0 || month<6){
            String endTime = now.getYear() + "-" +month +"-"+day;
            List<RealFlowRes> resultList = this.baseMapper.selectRealFlowList(startTime, endTime);
            Comparator<RealFlowRes> realFlowResComparator = Comparator.comparing(RealFlowRes::getMonth).thenComparing(RealFlowRes::getName);
            //正序
            //resultList.sort(realFlowResComparator);
            //倒叙
            Collections.sort(resultList, realFlowResComparator.reversed());
            RealFlowRes realFlowRes = resultList.get(0);
            if(realFlowRes.getMonth()>0 && realFlowRes.getMonth()<3){
                int indexStart = (realFlowRes.getMonth()-1)*3+(realFlowRes.getName()-1);
                int indexEnd = 2*3+3-1;
                for(int i=indexStart;i<=indexEnd;i++){
                    for(int j=realFlowRes.getName()+1;j<=3;j++){
                        RealFlowRes resTemp = new RealFlowRes();
                        Map<String, Object> stringObjectMap = maps.get(i);
                        resTemp.setMonth(i/3==0?1:2);
                        resTemp.setName(j);
                        resTemp.setFlow(new BigDecimal(stringObjectMap.get(year.toString()).toString()).multiply(new BigDecimal("8.64")).setScale(3, RoundingMode.HALF_UP));
                        resultList.add(resTemp);
                    }
                }
                //3月份
                BigDecimal prediction3 = typicalYearVo.getPrediction3();
                for(int i=1;i<=3;i++){
                    RealFlowRes resTemp = new RealFlowRes();
                    resTemp.setMonth(3);
                    resTemp.setName(i);
                    resTemp.setFlow(new BigDecimal(NumberUtil.holdDecimal(prediction3.doubleValue()/3,3)).multiply(new BigDecimal("8.64")).setScale(3, RoundingMode.HALF_UP));
                    resultList.add(resTemp);
                }
                //4月份
                BigDecimal prediction4 = typicalYearVo.getPrediction4();
                for(int i=1;i<=3;i++){
                    RealFlowRes resTemp = new RealFlowRes();
                    resTemp.setMonth(4);
                    resTemp.setName(i);
                    resTemp.setFlow(new BigDecimal(NumberUtil.holdDecimal(prediction4.doubleValue()/3,3)).multiply(new BigDecimal("8.64")).setScale(3, RoundingMode.HALF_UP));
                    resultList.add(resTemp);
                }
                //5月份
                BigDecimal prediction5 = typicalYearVo.getPrediction5();
                for(int i=1;i<=3;i++){
                    RealFlowRes resTemp = new RealFlowRes();
                    resTemp.setMonth(5);
                    resTemp.setName(i);
                    resTemp.setFlow(new BigDecimal(NumberUtil.holdDecimal(prediction5.doubleValue()/3,3)).multiply(new BigDecimal("8.64")).setScale(3, RoundingMode.HALF_UP));
                    resultList.add(resTemp);
                }
                for(int i=6;i<=12;i++){
                    for(int j=1;j<=3;j++){
                        RealFlowRes resTemp = new RealFlowRes();
                        resTemp.setMonth(i);
                        resTemp.setName(j);
                        resTemp.setFlow(new BigDecimal(maps.get((i-1)*3+(j-1)).get(year.toString()).toString()).multiply(new BigDecimal("8.64")).setScale(3, RoundingMode.HALF_UP));
                        resultList.add(resTemp);
                    }
                }
                return resultList;
            }
            if(realFlowRes.getMonth()==3){
                BigDecimal prediction3 = typicalYearVo.getPrediction3();
                if(realFlowRes.getName()==1){
                    RealFlowRes resTemp = new RealFlowRes();
                    resTemp.setMonth(3);
                    resTemp.setName(2);
                    resTemp.setFlow(new BigDecimal(NumberUtil.holdDecimal(prediction3.doubleValue()/3,3)).multiply(new BigDecimal("8.64")).setScale(3, RoundingMode.HALF_UP));
                    resultList.add(resTemp);
                }
                if(realFlowRes.getName()==2){
                    RealFlowRes resTemp = new RealFlowRes();
                    resTemp.setMonth(3);
                    resTemp.setName(3);
                    resTemp.setFlow(new BigDecimal(NumberUtil.holdDecimal(prediction3.doubleValue()/3,3)).multiply(new BigDecimal("8.64")).setScale(3, RoundingMode.HALF_UP));
                    resultList.add(resTemp);
                }
                //4月份
                BigDecimal prediction4 = typicalYearVo.getPrediction4();
                for(int i=1;i<=3;i++){
                    RealFlowRes resTemp = new RealFlowRes();
                    resTemp.setMonth(4);
                    resTemp.setName(i);
                    resTemp.setFlow(new BigDecimal(NumberUtil.holdDecimal(prediction4.doubleValue()/3,3)).multiply(new BigDecimal("8.64")).setScale(3, RoundingMode.HALF_UP));
                    resultList.add(resTemp);
                }
                //5月份
                BigDecimal prediction5 = typicalYearVo.getPrediction5();
                for(int i=1;i<=3;i++){
                    RealFlowRes resTemp = new RealFlowRes();
                    resTemp.setMonth(5);
                    resTemp.setName(i);
                    resTemp.setFlow(new BigDecimal(NumberUtil.holdDecimal(prediction5.doubleValue()/3,3)).multiply(new BigDecimal("8.64")).setScale(3, RoundingMode.HALF_UP));
                    resultList.add(resTemp);
                }
                for(int i=6;i<=12;i++){
                    for(int j=1;j<=3;j++){
                        RealFlowRes resTemp = new RealFlowRes();
                        resTemp.setMonth(i);
                        resTemp.setName(j);
                        resTemp.setFlow(new BigDecimal(maps.get((i-1)*3+(j-1)).get(year.toString()).toString()).multiply(new BigDecimal("8.64")).setScale(3, RoundingMode.HALF_UP));
                        resultList.add(resTemp);
                    }
                }
                return resultList;
            }
            if(realFlowRes.getMonth()==4){
                //4月份
                BigDecimal prediction4 = typicalYearVo.getPrediction4();
                if(realFlowRes.getName()==1){
                    RealFlowRes resTemp = new RealFlowRes();
                    resTemp.setMonth(4);
                    resTemp.setName(2);
                    resTemp.setFlow(new BigDecimal(NumberUtil.holdDecimal(prediction4.doubleValue()/3,3)).multiply(new BigDecimal("8.64")).setScale(3, RoundingMode.HALF_UP));
                    resultList.add(resTemp);
                }
                if(realFlowRes.getName()==2){
                    RealFlowRes resTemp = new RealFlowRes();
                    resTemp.setMonth(4);
                    resTemp.setName(3);
                    resTemp.setFlow(new BigDecimal(NumberUtil.holdDecimal(prediction4.doubleValue()/3,3)).multiply(new BigDecimal("8.64")).setScale(3, RoundingMode.HALF_UP));
                    resultList.add(resTemp);
                }
                //5月份
                BigDecimal prediction5 = typicalYearVo.getPrediction5();
                for(int i=1;i<=3;i++){
                    RealFlowRes resTemp = new RealFlowRes();
                    resTemp.setMonth(5);
                    resTemp.setName(i);
                    resTemp.setFlow(new BigDecimal(NumberUtil.holdDecimal(prediction5.doubleValue()/3,3)).multiply(new BigDecimal("8.64")).setScale(3, RoundingMode.HALF_UP));
                    resultList.add(resTemp);
                }
                for(int i=6;i<=12;i++){
                    for(int j=1;j<=3;j++){
                        RealFlowRes resTemp = new RealFlowRes();
                        resTemp.setMonth(i);
                        resTemp.setName(j);
                        resTemp.setFlow(new BigDecimal(maps.get((i-1)*3+(j-1)).get(year.toString()).toString()).multiply(new BigDecimal("8.64")).setScale(3, RoundingMode.HALF_UP));
                        resultList.add(resTemp);
                    }
                }
                return resultList;
            }
            if(realFlowRes.getMonth()==5){
                //5月份
                BigDecimal prediction5 = typicalYearVo.getPrediction5();
                if(realFlowRes.getName()==1){
                    RealFlowRes resTemp = new RealFlowRes();
                    resTemp.setMonth(5);
                    resTemp.setName(2);
                    resTemp.setFlow(new BigDecimal(NumberUtil.holdDecimal(prediction5.doubleValue()/3,3)).multiply(new BigDecimal("8.64")).setScale(3, RoundingMode.HALF_UP));
                    resultList.add(resTemp);
                }
                if(realFlowRes.getName()==2){
                    RealFlowRes resTemp = new RealFlowRes();
                    resTemp.setMonth(5);
                    resTemp.setName(3);
                    resTemp.setFlow(new BigDecimal(NumberUtil.holdDecimal(prediction5.doubleValue()/3,3)).multiply(new BigDecimal("8.64")).setScale(3, RoundingMode.HALF_UP));
                    resultList.add(resTemp);
                }
                for(int i=6;i<=12;i++){
                    for(int j=1;j<=3;j++){
                        RealFlowRes resTemp = new RealFlowRes();
                        resTemp.setMonth(i);
                        resTemp.setName(j);
                        resTemp.setFlow(new BigDecimal(maps.get((i-1)*3+(j-1)).get(year.toString()).toString()).multiply(new BigDecimal("8.64")).setScale(3, RoundingMode.HALF_UP));
                        resultList.add(resTemp);
                    }
                }
                return resultList;
            }

        }else{
            String endTime = now.getYear() + "-" +month +"-"+day;
            List<RealFlowRes> resultList = this.baseMapper.selectRealFlowList(startTime, endTime);
            Comparator<RealFlowRes> realFlowResComparator = Comparator.comparing(RealFlowRes::getMonth).thenComparing(RealFlowRes::getName);
            //正序
            //resultList.sort(realFlowResComparator);
            //倒叙
            Collections.sort(resultList, realFlowResComparator.reversed());
            RealFlowRes realFlowRes = resultList.get(0);
            for(int i=realFlowRes.getMonth();i<=12;i++){
                for(int j=realFlowRes.getName()+1;j<=3;j++){
                    RealFlowRes resTemp = new RealFlowRes();
                    resTemp.setMonth(i);
                    resTemp.setName(j);
                    resTemp.setFlow(new BigDecimal(maps.get((i-1)*3+(j-1)).get(year.toString()).toString()).multiply(new BigDecimal("8.64")).setScale(3, RoundingMode.HALF_UP));
                    resultList.add(resTemp);
                }
            }
            return resultList;
        }
        return null;
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
}

