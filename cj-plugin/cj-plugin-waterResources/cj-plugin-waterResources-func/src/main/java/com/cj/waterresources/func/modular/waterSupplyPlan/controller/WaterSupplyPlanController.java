package com.cj.waterresources.func.modular.waterSupplyPlan.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.trendsTable.bean.res.WaterDailyParamSelectRes;
import com.cj.waterresources.func.modular.trendsTable.entity.TrendsTableParam;
import com.cj.waterresources.func.modular.waterSupplyPlan.bean.req.WaterSupplyPlanSelectListReq;
import com.cj.waterresources.func.modular.waterSupplyPlan.bean.req.WaterSupplyPlanUpdateReq;
import com.cj.waterresources.func.modular.waterSupplyPlan.bean.res.WaterSupplyPlanSelectListRes;
import com.cj.waterresources.func.modular.waterSupplyPlan.entity.WaterSupplyPlan;
import com.cj.waterresources.func.modular.waterSupplyPlan.service.WaterSupplyPlanService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 供水计划管理(WaterSupplyPlan)表控制层
 *
 * @author makejava
 * @since 2023-11-21 09:51:22
 */
@Api(tags = "供水计划管理")
@ApiSupport(author = "LEO-LUOXU", order = 1)
@Validated
@RestController
@RequestMapping("waterSupplyPlan")
public class WaterSupplyPlanController{

    @Autowired
    private WaterSupplyPlanService waterSupplyPlanService;

    @ApiOperationSupport(order = 1)
    @ApiOperation("新增")
    @PostMapping("/addWaterSupplyPlan")
    public RestResponse addWaterSupplyPlan(@RequestBody WaterSupplyPlan waterSupplyPlan) {
        return waterSupplyPlanService.addWaterSupplyPlan(waterSupplyPlan);
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("删除")
    @GetMapping("/deleteWaterSupplyPlan")
    public RestResponse deleteWaterSupplyPlan(@RequestParam("id") String id) {
        return waterSupplyPlanService.deleteWaterSupplyPlan(id);
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation("修改")
    @PostMapping("/updateWaterSupplyPlan")
    public RestResponse updateWaterSupplyPlan(@RequestBody WaterSupplyPlanUpdateReq req) {
        return waterSupplyPlanService.updateWaterSupplyPlan(req);
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation("查询列表")
    @PostMapping("/getWaterSupplyPlanList")
    public RestResponse<IPage<WaterSupplyPlanSelectListRes>> getWaterSupplyPlanList(@RequestBody WaterSupplyPlanSelectListReq req) {
        return waterSupplyPlanService.getWaterSupplyPlanList(req);
    }

}

