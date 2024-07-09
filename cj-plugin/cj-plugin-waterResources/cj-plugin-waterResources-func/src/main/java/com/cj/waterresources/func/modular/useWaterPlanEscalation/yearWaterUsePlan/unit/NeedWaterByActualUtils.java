package com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.unit;

import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.bean.vo.PlanComparedToActualByYearVo;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.mapper.YearWaterUsePlanCropMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.service.AllService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class NeedWaterByActualUtils {

    public static class SelectNeedWaterByActual implements Callable<Double> {
        private  YearWaterUsePlanCropMapper yearWaterUsePlanCropMapper;
        private AllService allService;

        private Integer num;
        private Integer planYear;

        public SelectNeedWaterByActual(YearWaterUsePlanCropMapper yearWaterUsePlanCropMapper , AllService allService,Integer num,Integer planYear) {
            this.yearWaterUsePlanCropMapper = yearWaterUsePlanCropMapper ;
            this.allService = allService;
            this.num = num;
            this.planYear = planYear;
        }
        // 可自定义返回值
        @Override
        public Double call() {
            Double value = 0.00;
            List<PlanComparedToActualByYearVo> planYearList = yearWaterUsePlanCropMapper.planComparedToActual(planYear, getMonthName(num));
            Map<String, List<PlanComparedToActualByYearVo>> collect = planYearList.stream().collect(Collectors.groupingBy(PlanComparedToActualByYearVo::getArea));
            Set<String> unitNameList = collect.keySet();
            for(String unitName : unitNameList){
                value+= allService.planComparedToActualForYearTotal(planYear, num, collect.get(unitName).stream().map(PlanComparedToActualByYearVo::getBindId).collect(Collectors.toList()),unitName);
            }
            return value;
        }

    }

    private static String getMonthName(Integer value) {
        switch (value){
            case 0:
                return "AMOUNT_COUNT";
            case 1:
                return "JANUARY";
            case 2:
                return "FEBRUARY";
            case 3:
                return "MARCH";
            case 4:
                return "APRIL";
            case 5:
                return "MAY";
            case 6:
                return "JUNE";
            case 7:
                return "JULY";
            case 8:
                return "AUGUST";
            case 9:
                return "SEPTEMBER";
            case 10:
                return "OCTOBER";
            case 11:
                return "NOVEMBER";
            case 12:
                return "DECEMBER";
            default:
                return "AMOUNT_COUNT";
        }
    }
}
