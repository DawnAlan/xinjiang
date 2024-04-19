package com.cj.flood.func.modular.prediction.controller;

import com.cj.common.model.RestResponse;
import com.cj.flood.func.modular.prediction.bean.req.CalibrateReq;
import com.cj.flood.func.modular.prediction.bean.req.ModelParametersReq;
import com.cj.flood.func.modular.prediction.service.ModelParametersService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.ApiOperation;
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

    @ApiOperationSupport(order = 1)
    @ApiOperation("模型率定数据查询")
    @PostMapping("/queryList")
    public RestResponse queryList(@RequestBody ModelParametersReq input) {
        return RestResponse.ok(modelParametersService.queryList(input));
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("模型率定数据删除")
    @PostMapping("/del")
    public RestResponse del(@RequestBody List<String> input) {
        if (modelParametersService.del(input)){
            return RestResponse.ok(true);
        }else {
            return RestResponse.no("删除失败！");
        }
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation("模型率定")
    @PostMapping("/calibrate")
    public RestResponse calibrate(@RequestBody CalibrateReq input) {
        return RestResponse.ok(modelParametersService.calibrate(input));
    }
}

