package com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.controller;

import com.cj.business.log.modular.log.annotation.CommonLog;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.bean.req.YearCropImportParamReq;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.bean.req.YearCropSelectListReq;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.bean.res.PlanComparedToActualByYearRes;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.bean.vo.NeedWaterVo;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.entity.YearWaterUsePlanCrop;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.service.YearWaterUsePlanCropService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 作物年用水计划(YearWaterUsePlanCrop)表控制层
 *
 * @author makejava
 * @since 2023-12-01 18:26:27
 */
@Api(tags = "年用水计划模块-作物")
@ApiSupport(author = "LEO-LUOXU", order = 1)
@Validated
@RestController
@RequestMapping("yearWaterUsePlanCrop")
public class YearWaterUsePlanCropController {

    @Autowired
    private YearWaterUsePlanCropService yearWaterUsePlanCropService;

    @ApiOperationSupport(order = 1)
    @ApiOperation("年用水计划模块-作物删除")
    @CommonLog(value = "年用水计划模块-作物删除")
    @PostMapping("/delete")
    public RestResponse delete(@RequestBody YearCropImportParamReq req) {
        return yearWaterUsePlanCropService.delete(req);
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("年用水计划模块-作物新增")
    @CommonLog(value = "年用水计划模块-作物新增")
    @PostMapping("/add")
    public RestResponse add(@RequestParam(value = "area",required = true) String area,
                            @RequestParam(value = "unit",required = true) String unit,
                            @RequestParam(value = "unitId",required = true) String unitId,
                            @RequestParam(value = "bindId",required = true) String bindId,
                            @RequestParam(value = "year",required = true) Integer year,
                            @RequestParam(value = "file",required = true) MultipartFile file) {
        YearCropImportParamReq req = new YearCropImportParamReq();
        req.setYear(year);
        req.setArea(area);
        req.setUnit(unit);
        req.setUnitId(unitId);
        req.setBindId(bindId);
        return yearWaterUsePlanCropService.add(req, file);
    }


    @ApiOperationSupport(order = 3)
    @ApiOperation("年用水计划模块-作物查询列表")
    @CommonLog(value = "年用水计划模块-作物查询列表")
    @PostMapping("/select")
    public RestResponse<List<YearWaterUsePlanCrop>> select(@RequestBody YearCropSelectListReq req) {
        return yearWaterUsePlanCropService.selectList(req);
    }


    @ApiOperationSupport(order = 4)
    @ApiOperation("年用水计划模块-计划与实际对比")
    @CommonLog(value = "年用水计划模块-计划与实际对比")
    @PostMapping("/planComparedToActual")
    public RestResponse<List<PlanComparedToActualByYearRes>> planComparedToActual(@RequestParam(value = "planYear",required = true) Integer planYear,
                                                                                  @RequestParam(value = "actualYear",required = true) Integer actualYear,
                                                                                  @RequestParam(value = "month",required = true) Integer month) {
        return yearWaterUsePlanCropService.planComparedToActual(planYear, actualYear, month);
    }

    @ApiOperationSupport(order = 5)
    @ApiOperation("年用水计划模块-头屯河整年需水")
    @CommonLog(value = "年用水计划模块-头屯河整年需水")
    @PostMapping("/needWaterByPlan")
    public RestResponse<NeedWaterVo> needWaterByPlan(@RequestParam(value = "planYear",required = true) Integer planYear) {
        return yearWaterUsePlanCropService.needWaterByPlan(planYear);
    }
    @ApiOperationSupport(order = 6)
    @ApiOperation("年用水计划模块-实际用水量")
    @CommonLog(value = "年用水计划模块-实际用水量")
    @PostMapping("/needWaterByActual")
    public RestResponse<Map<Integer,Double>> needWaterByActual(@RequestParam(value = "planYear",required = true) Integer planYear) {
        return yearWaterUsePlanCropService.needWaterByActual(planYear);
    }
}

