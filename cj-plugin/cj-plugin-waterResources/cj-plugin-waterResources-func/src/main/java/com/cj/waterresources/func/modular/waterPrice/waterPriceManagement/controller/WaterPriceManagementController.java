package com.cj.waterresources.func.modular.waterPrice.waterPriceManagement.controller;


import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.waterPrice.waterPriceManagement.bean.req.WaterPriceSelectListReq;
import com.cj.waterresources.func.modular.waterPrice.waterPriceManagement.bean.req.WaterPriceUpdateReq;
import com.cj.waterresources.func.modular.waterPrice.waterPriceManagement.bean.res.WaterPriceSelectListRes;
import com.cj.waterresources.func.modular.waterPrice.waterPriceManagement.service.WaterPriceManagementService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 水价管理(WaterPriceManagement)表控制层
 *
 * @author makejava
 * @since 2023-11-29 10:44:38
 */
@Api(tags = "水价管理模块")
@ApiSupport(author = "LEO-LUOXU", order = 1)
@Validated
@RestController
@RequestMapping("waterPriceManagement")
public class WaterPriceManagementController {

    @Autowired
    private WaterPriceManagementService waterPriceManagementService;

    @ApiOperationSupport(order = 1)
    @ApiOperation("删除")
    @GetMapping("/delete")
    public RestResponse delete(@RequestParam("id") String id) {
        return waterPriceManagementService.deleteWaterPrice(id);
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("修改")
    @PostMapping("/update")
    public RestResponse update(@RequestBody WaterPriceUpdateReq req) {
        return waterPriceManagementService.updateWaterPrice(req);
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation("查询列表")
    @PostMapping("/select")
    public RestResponse<List<WaterPriceSelectListRes>> select(@RequestBody WaterPriceSelectListReq req) {
        return waterPriceManagementService.waterPriceSelectList(req);
    }

}

