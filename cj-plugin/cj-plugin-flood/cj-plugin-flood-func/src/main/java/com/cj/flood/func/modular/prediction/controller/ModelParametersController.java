package com.cj.flood.func.modular.prediction.controller;

import com.cj.flood.func.modular.prediction.service.ModelParametersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


/**
 * 陕北模型参数(ModelParameters)表控制层
 *
 * @author makejava
 * @since 2024-03-13 12:27:09
 */
@RestController
@RequestMapping("modelParameters")
public class ModelParametersController {

    @Autowired
    private ModelParametersService modelParametersService;

/*    @ApiOperationSupport(order = 1)
    @ApiOperation("模型率定数据查询")
    @PostMapping("/queryList")
    public RestResponse queryList(@RequestBody ModelParametersReq input) {
        return RestResponse.ok(modelParametersService.queryList(input));
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("模型率定")
    @PostMapping("/calibrate")
    public RestResponse calibrate(@RequestBody CalibrationParam input) {
        return RestResponse.ok(modelParametersService.calibrate(input));
    }*/
}

