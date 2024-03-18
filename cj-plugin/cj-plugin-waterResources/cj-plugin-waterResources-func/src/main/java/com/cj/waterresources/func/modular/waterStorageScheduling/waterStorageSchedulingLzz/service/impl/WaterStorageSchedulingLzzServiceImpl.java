package com.cj.waterresources.func.modular.waterStorageScheduling.waterStorageSchedulingLzz.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.auth.core.pojo.SaBaseLoginUser;
import com.cj.auth.core.util.StpLoginUserUtil;
import com.cj.common.model.RestResponse;
import com.cj.common.util.UUIDUtils;
import com.cj.middleDatabase.func.modular.lzz.storageCapacityCurve.entity.StorageCapacityCurve;
import com.cj.middleDatabase.func.modular.lzz.storageCapacityCurve.service.StorageCapacityCurveService;
import com.cj.waterresources.func.modular.surfaceWater.entity.QueryListReq;
import com.cj.waterresources.func.modular.surfaceWater.generator.domain.SurfaceWater;
import com.cj.waterresources.func.modular.surfaceWater.generator.service.SurfaceWaterFlowDetailService;
import com.cj.waterresources.func.modular.surfaceWater.generator.service.SurfaceWaterService;
import com.cj.waterresources.func.modular.surfaceWater.vo.TenDayVo;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.bean.req.TrunkCanalSelectListReq;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.bean.res.SelectYearWaterUsePlanTrunkCanalForSum;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.service.YearWaterUsePlanCropService;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.service.YearWaterUsePlanTrunkCanalService;
import com.cj.waterresources.func.modular.waterStorageScheduling.waterStorageSchedulingLzz.entity.WaterStorageSchedulingLzz;
import com.cj.waterresources.func.modular.waterStorageScheduling.waterStorageSchedulingLzz.mapper.WaterStorageSchedulingLzzMapper;
import com.cj.waterresources.func.modular.waterStorageScheduling.waterStorageSchedulingLzz.service.WaterStorageSchedulingLzzService;
import com.cj.waterresources.func.modular.waterStorageScheduling.waterStorageSchedulingTotalForm.entity.WaterStorageSchedulingTotalForm;
import com.cj.waterresources.func.modular.waterStorageScheduling.waterStorageSchedulingTotalForm.service.WaterStorageSchedulingTotalFormService;
import com.cj.waterresources.func.modular.waterStorageScheduling.waterStorageSchedulingTth.entity.WaterStorageSchedulingTth;
import com.cj.waterresources.func.modular.waterStorageScheduling.waterStorageSchedulingTth.service.WaterStorageSchedulingTthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse add(String formId) {
        SaBaseLoginUser saBaseLoginUser = StpLoginUserUtil.getLoginUser();
        WaterStorageSchedulingTotalForm byId = waterStorageSchedulingTotalFormService.getById(formId);
        if(null==byId){
            return RestResponse.no("方案有误，请重新选择");
        }
        List<TenDayVo> inflowData = getInflowData(byId.getInflowYear());
        if(null==inflowData || inflowData.size()<0){
            return RestResponse.no("方案选择的来水时间无数据");
        }
        Map<Integer, List<TenDayVo>> collect = inflowData.stream().collect(Collectors.groupingBy(TenDayVo::getMonth));
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
        List<StorageCapacityCurve> storageCapacityCurveList = storageCapacityCurveService.lambdaQuery().eq(StorageCapacityCurve::getReservoir, "lzz").list();
        for(Integer month:list){
            List<TenDayVo> tenDayVos = collect.get(month);
            tenDayVos.sort(Comparator.comparing(t -> t.getName()));
            for(TenDayVo vo : tenDayVos){
                WaterStorageSchedulingLzz lzz = new WaterStorageSchedulingLzz();
                lzz.setId(UUIDUtils.getUUID());
                lzz.setDel(0);
                lzz.setCreateBy(saBaseLoginUser.getName());
                lzz.setCreateTime(new Date());
                lzz.setFormId(formId);
                lzz.setMonth(vo.getMonth());
                lzz.setTenDays(vo.getName()==1?"上旬":vo.getName()==2?"中旬":"下旬");
                lzz.setYear(byId.getYear());
                lzz.setReservoirInflow(changeNum(vo.getValue().doubleValue()));
                lzz.setFineTuning(100.00);
                lzz.setFineTuningReservoirInflow(changeNum((lzz.getFineTuning()/100)*lzz.getReservoirInflow()));
                List<TenDayVo> lzzTempData = lzzData.stream().filter(t -> t.getMonth() == vo.getMonth() && t.getName() == vo.getName()).collect(Collectors.toList());
                if(null != lzzTempData && lzzTempData.size()>0){
                    TenDayVo lzzResultData = lzzTempData.get(0);
                    lzz.setWaterPlantDemand(lzzResultData.getValue()==null?0.0:changeNum(lzzResultData.getValue().doubleValue()));
                }
                List<WaterStorageSchedulingTth> tthDataTemp = tth.stream().filter(t -> t.getMonth() == vo.getMonth() && t.getTenDays().equals(lzz.getTenDays())).collect(Collectors.toList());
                if(null != tthDataTemp && tthDataTemp.size()>0){
                    WaterStorageSchedulingTth tthData = tthDataTemp.get(0);
                    lzz.setReservoirWaterDemand(changeNum(tthData.getFineTuningReservoirInflow()));
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
                if(vo.getMonth()==1 && vo.getName()==1){
                    lzz.setWaterStorage(0.0);
                }
                lzz.setSortNum(sortNum++);
                waterStorageSchedulingLzzList.add(lzz);
            }
        }
        for(int j=1;j<waterStorageSchedulingLzzList.size();j++){
            WaterStorageSchedulingLzz lzz = waterStorageSchedulingLzzList.get(j);
            lzz.setWaterStorage(changeNum(
                        (waterStorageSchedulingLzzList.get(j-1).getWaterStorage())+
                        (lzz.getRegulatingWaterStorageCapacity())
                    )
            );
        }
        waterStorageSchedulingLzzList.forEach(t->t.setWaterStorageLevel(getWaterStorageLevel(storageCapacityCurveList,t.getWaterStorage())));
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
        for(WaterStorageSchedulingLzz lzz :waterStorageSchedulingLzzList){
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
        List<StorageCapacityCurve> storageCapacityCurveList = storageCapacityCurveService.lambdaQuery().eq(StorageCapacityCurve::getReservoir, "lzz").list();
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
        waterStorageSchedulingLzzList.forEach(t->t.setWaterStorageLevel(getWaterStorageLevel(storageCapacityCurveList,t.getWaterStorage())));
        boolean b = this.saveOrUpdateBatch(waterStorageSchedulingLzzList);
        if(b){
            return true;
        }else {
            return false;
        }
    }

    private Double getWaterStorageLevel(List<StorageCapacityCurve> storageCapacityCurveList,Double waterStorage){
        List<StorageCapacityCurve> leftCollect = storageCapacityCurveList.stream().filter(t ->t.getStorageCapacity()!=null && t.getStorageCapacity().compareTo(new BigDecimal(waterStorage)) != 1).collect(Collectors.toList());
        List<StorageCapacityCurve> rightCollect = storageCapacityCurveList.stream().filter(t ->t.getStorageCapacity()!=null && t.getStorageCapacity().compareTo(new BigDecimal(waterStorage)) != -1).collect(Collectors.toList());
        leftCollect.sort(Comparator.comparing(t -> t.getStorageCapacity()));
        rightCollect.sort(Comparator.comparing(t -> t.getStorageCapacity()));
        if(null != leftCollect && leftCollect.size()>0 && null != rightCollect && rightCollect.size()>0){
            StorageCapacityCurve leftStorageCapacityCurve = leftCollect.get(leftCollect.size()-1);
            StorageCapacityCurve rightStorageCapacityCurve = rightCollect.get(0);
            Double temp = (waterStorage-leftStorageCapacityCurve.getStorageCapacity().doubleValue())/(rightStorageCapacityCurve.getStorageCapacity().doubleValue()-leftStorageCapacityCurve.getStorageCapacity().doubleValue())/5;
            int i = temp.intValue();
            Double waterStorageLevel = i*0.01+(leftStorageCapacityCurve.getWaterLevel().doubleValue()+leftStorageCapacityCurve.getInterpolation().doubleValue());
            return changeNum(waterStorageLevel);
        }else {
            return null;
        }
    }

    private List<TenDayVo> getInflowData(Integer year) {
        QueryListReq req = new QueryListReq();
        req.setYear(year);
        req.setTableName("日平均流量表");
        req.setManagerName("楼庄子水库");
        req.setPageNo(1);
        req.setPageSize(1);
        IPage<SurfaceWater> surfaceWaterIPage = surfaceWaterService.queryList(req);
        if(surfaceWaterIPage.getTotal()<0){
            return null;
        }
        List<TenDayVo> tenDayVos = surfaceWaterFlowDetailService.ten_day(surfaceWaterIPage.getRecords().get(0).getId());
        return tenDayVos;
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
}

