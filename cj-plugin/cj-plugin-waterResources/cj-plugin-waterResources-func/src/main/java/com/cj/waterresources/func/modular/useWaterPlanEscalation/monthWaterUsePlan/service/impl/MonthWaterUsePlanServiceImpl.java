package com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.common.model.RestResponse;
import com.cj.common.util.NumberUtil;
import com.cj.common.util.UUIDUtils;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.bean.req.MonthWaterUsePlanSelectListReq;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.bean.res.PlanComparedToActualByMonthRes;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.bean.vo.PlanComparedToActualByMonthVo;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.mapper.MonthWaterUsePlanMapper;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.entity.MonthWaterUsePlan;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.service.MonthWaterUsePlanService;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.service.AllService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 月用水计划(MonthWaterUsePlan)表服务实现类
 *
 * @author makejava
 * @since 2023-12-07 16:48:27
 */
@Service("monthWaterUsePlanService")
public class MonthWaterUsePlanServiceImpl extends ServiceImpl<MonthWaterUsePlanMapper, MonthWaterUsePlan> implements MonthWaterUsePlanService {

    @Autowired
    private AllService allService;

    @Override
    public RestResponse add(MonthWaterUsePlan monthWaterUsePlan) {
        List<MonthWaterUsePlan> list = this.lambdaQuery().eq(MonthWaterUsePlan::getYear, monthWaterUsePlan.getYear()).
                eq(MonthWaterUsePlan::getMonth, monthWaterUsePlan.getMonth()).
                eq(MonthWaterUsePlan::getArea, monthWaterUsePlan.getArea()).
                eq(MonthWaterUsePlan::getDel,0).
                eq(MonthWaterUsePlan::getUnit, monthWaterUsePlan.getUnit()).list();
        if(null != list && list.size()>0){
            return RestResponse.no("请勿重复添加单位");
        }
        monthWaterUsePlan.setId(UUIDUtils.getUUID());
        monthWaterUsePlan.setCreateTime(new Date());
        monthWaterUsePlan.setDel(0);
        boolean save = this.save(monthWaterUsePlan);
        if(save){
            return RestResponse.ok("新增成功");
        }else {
            return RestResponse.no("新增失败");
        }
    }

    @Override
    public RestResponse delete(String id) {
        boolean update = this.lambdaUpdate().set(MonthWaterUsePlan::getDel, 1).eq(MonthWaterUsePlan::getId, id).update();
        if(update){
            return RestResponse.ok("删除成功");
        }else {
            return RestResponse.no("删除失败");
        }
    }

    @Override
    public RestResponse update(MonthWaterUsePlan monthWaterUsePlan) {
        MonthWaterUsePlan byId = this.getById(monthWaterUsePlan.getId());
        if(byId.getUnit().equals(monthWaterUsePlan.getUnit())){
            monthWaterUsePlan.setUpdateTime(new Date());
            boolean b = this.updateById(monthWaterUsePlan);
            if(b){
                return RestResponse.ok("修改成功");
            }else {
                return RestResponse.no("修改失败");
            }
        }else {
            List<MonthWaterUsePlan> list = this.lambdaQuery().eq(MonthWaterUsePlan::getYear, monthWaterUsePlan.getYear()).
                    eq(MonthWaterUsePlan::getMonth, monthWaterUsePlan.getMonth()).
                    eq(MonthWaterUsePlan::getArea, monthWaterUsePlan.getArea()).eq(MonthWaterUsePlan::getUnit, monthWaterUsePlan.getUnit()).list();
            if(null != list && list.size()>0){
                return RestResponse.no("请勿重复添加单位");
            }
            monthWaterUsePlan.setUpdateTime(new Date());
            boolean b = this.updateById(monthWaterUsePlan);
            if(b){
                return RestResponse.ok("修改成功");
            }else {
                return RestResponse.no("修改失败");
            }
        }
    }

    @Override
    public RestResponse<List<MonthWaterUsePlan>> selectList(MonthWaterUsePlanSelectListReq req) {
        List<MonthWaterUsePlan> list = this.lambdaQuery().
                eq(StringUtils.isNotEmpty(req.getArea()), MonthWaterUsePlan::getArea, req.getArea()).
                eq(req.getYear() != null, MonthWaterUsePlan::getYear, req.getYear()).
                eq(req.getMonth() != null,MonthWaterUsePlan::getMonth,req.getMonth()).
                eq(MonthWaterUsePlan::getDel, 0).list();
        if(list.size()>0){
            return RestResponse.ok(list);
        }else {
            return RestResponse.no("暂无数据");
        }
    }
    @Override
    public RestResponse<List<PlanComparedToActualByMonthRes>> planComparedToActual(String plan, String actual, String tenDays) {
        List<PlanComparedToActualByMonthRes> resultList = new ArrayList<>();
        String[] split = plan.split("-");
        Integer planYear = Integer.parseInt(split[0]);
        Integer planMonth = Integer.parseInt(split[1]);
        String[] split1 = actual.split("-");
        Integer actualYear = Integer.parseInt(split1[0]);
        Integer actualMonth = Integer.parseInt(split1[1]);
        List<PlanComparedToActualByMonthVo> planComparedToActualByMonthVos = this.baseMapper.planComparedToActual(planYear, planMonth, getTenDaysName(tenDays));
        Map<String, List<PlanComparedToActualByMonthVo>> collect = planComparedToActualByMonthVos.stream().collect(Collectors.groupingBy(PlanComparedToActualByMonthVo::getArea));
        Set<String> strings = collect.keySet();
        for(String unitName:strings){
            PlanComparedToActualByMonthRes res = new PlanComparedToActualByMonthRes();
            res.setUnitName(unitName);
            res.setPlanValue(collect.get(unitName).stream().filter(t->t.getV() !=null).map(PlanComparedToActualByMonthVo::getV).reduce(Double::sum).orElse(0.00));
            Map<String, Double> stringDoubleMap = allService.planComparedToActualForMonth(planYear, planMonth, tenDays, collect.get(unitName).stream().map(PlanComparedToActualByMonthVo::getBindId).collect(Collectors.toList()), unitName);
            res.setActualValue(NumberUtil.holdDecimal(stringDoubleMap.get(unitName)*8.64,2));
            res.setPlanSubtractActualValue(NumberUtil.holdDecimal(res.getActualValue()-res.getPlanValue(),2));
            Map<String, Double> stringDoubleMap1 = allService.planComparedToActualForMonth(actualYear, actualMonth, tenDays,collect.get(unitName).stream().map(PlanComparedToActualByMonthVo::getBindId).collect(Collectors.toList()), unitName);
            res.setActualValueForContrastYear(NumberUtil.holdDecimal(stringDoubleMap1.get(unitName)*8.64,2));
            res.setPlanSubtractActualValueForContrastYear(NumberUtil.holdDecimal(res.getActualValue()-res.getActualValueForContrastYear(),2));
            res.setContrast(NumberUtil.holdDecimal((res.getActualValueForContrastYear()==null ||res.getActualValueForContrastYear()==0.00)?0.00: (res.getPlanSubtractActualValueForContrastYear()/res.getActualValueForContrastYear())*100,2));
            resultList.add(res);
        }
        return RestResponse.ok(resultList);
    }



    private String getTenDaysName(String value) {
        switch (value){
            case "全月":
                return "TOTAL";
            case "上旬":
                return "EARLY_OCTOBER";
            case "中旬":
                return "MID_DAY";
            case "下旬":
                return "LATER_OCTOBER";
            default:
                return "TOTAL";
        }
    }
}

