package com.cj.waterresources.func.modular.waterResourceAllcation.controller;

import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.waterResourceAllcation.bean.dto.IncomingWaterForecastDto;
import com.cj.waterresources.func.modular.waterResourceAllcation.bean.req.WaterResourceAllocationAddReq;
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
public class WaterResourceAllocationController{
    /**
     * 服务对象
     */
    @Autowired
    private WaterResourceAllocationService waterResourceAllocationService;

    @ApiOperationSupport(order = 1)
    @ApiOperation("查询")
    @GetMapping("/getListByTime")
    public RestResponse<List<IncomingWaterForecastDto>> delete(@RequestParam("startTime") String startTime, @RequestParam("endTime") String endTime) {
        return waterResourceAllocationService.getIncomingWaterForecastListByTime(startTime, endTime);
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("模型生成")
    @PostMapping("/generativeModel")
    public RestResponse generativeModel(@RequestBody WaterResourceAllocationAddReq req){
        return waterResourceAllocationService.generativeModel(req);
    }

}

