package com.cj.flood.func.modular.prediction.controller;

import com.cj.common.model.RestResponse;
import com.cj.flood.func.modular.prediction.bean.req.ModelParametersReq;
import com.cj.flood.func.modular.prediction.entity.ModelParameters;
import com.cj.flood.func.modular.prediction.service.ModelParametersService;
/*import com.cj.model.func.modular.FloodPredict.Calibration.entity.CalibrationParam;*/
import com.cj.model.func.modular.FloodPredict.entity.calibrationParam;
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

