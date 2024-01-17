package com.cj.waterresources.func.modular.waterResourceAllcation.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.waterResourceAllcation.bean.dto.IncomingWaterForecastDto;
import com.cj.waterresources.func.modular.waterResourceAllcation.bean.req.ViewModelReq;
import com.cj.waterresources.func.modular.waterResourceAllcation.bean.req.WaterResourceAllocationAddReq;
import com.cj.waterresources.func.modular.waterResourceAllcation.bean.req.WaterResourceAllocationQueryReq;
import com.cj.waterresources.func.modular.waterResourceAllcation.bean.res.ViewModelRes;
import com.cj.waterresources.func.modular.waterResourceAllcation.bean.res.WaterAllocationComparisonSelectionRes;
import com.cj.waterresources.func.modular.waterResourceAllcation.entity.WaterResourceAllocation;
import com.cj.waterresources.func.modular.waterResourceAllcation.service.WaterResourceAllocationService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 水资源调配模型表(WaterResourceAllocation)表控制层
 *
 * @author makejava
 * @since 2023-11-14 17:34:43
 */
@Api(tags = "水资源调配模块")
@ApiSupport(author = "LEO-LUOXU", order = 1)
@Validated
@RestController
@RequestMapping("waterResourceAllocation")
public class WaterResourceAllocationController {
    /**
     * 服务对象
     */
    @Autowired
    private WaterResourceAllocationService waterResourceAllocationService;

    @ApiOperationSupport(order = 1)
    @ApiOperation("查询")
    @GetMapping("/getListByTime")
    public RestResponse<List<IncomingWaterForecastDto>> getListByTime(@RequestParam("startTime") String startTime,
                                                                      @RequestParam("endTime") String endTime,
                                                                      @RequestParam("bucketType") Integer bucketType) {
        return waterResourceAllocationService.getIncomingWaterForecastListByTime(startTime, endTime, bucketType);
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("模型生成")
    @PostMapping("/generativeModel")
    public RestResponse generativeModel(@RequestBody WaterResourceAllocationAddReq req) {
        return waterResourceAllocationService.generativeModel(req);
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation("模型预览")
    @PostMapping("/viewModel")
    public RestResponse<List<ViewModelRes>> viewModel(@RequestBody ViewModelReq req) {
        return waterResourceAllocationService.viewModel(req);
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation("分页查询调度方案")
    @PostMapping("/getAllocationPage")
    public RestResponse<IPage<WaterResourceAllocation>> getAllocationPage(@RequestBody WaterResourceAllocationQueryReq req) {
        return waterResourceAllocationService.getAllocationPage(req);
    }

    @ApiOperationSupport(order = 5)
    @ApiOperation("修改调度方案")
    @PostMapping("/update")
    public RestResponse update(@RequestBody WaterResourceAllocation waterResourceAllocation) {
        return waterResourceAllocationService.updateAllocation(waterResourceAllocation);
    }

    @ApiOperationSupport(order = 6)
    @ApiOperation("删除调度方案")
    @PostMapping("/delById")
    public RestResponse delById(@RequestBody List<String> ids) {
        return waterResourceAllocationService.delById(ids);
    }

    @ApiOperationSupport(order = 7)
    @ApiOperation("调度方案对比")
    @PostMapping("/compare")
    public RestResponse<WaterAllocationComparisonSelectionRes> compare(@RequestParam String idA, @RequestParam String idB) {
        return waterResourceAllocationService.compare(idA, idB);
    }
}

