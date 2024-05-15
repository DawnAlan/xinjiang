package com.cj.flood.func.modular.prediction.controller;

import com.cj.common.model.RestResponse;
import com.cj.flood.func.modular.prediction.bean.req.CalibrateReq;
import com.cj.flood.func.modular.prediction.bean.req.ModelParameterDetailReq;
import com.cj.flood.func.modular.prediction.bean.req.ModelParametersReq;
import com.cj.flood.func.modular.prediction.service.ModelParametersService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 陕北模型参数(ModelParameters)表控制层
 *
 * @author makejava
 * @since 2024-03-13 12:27:09
 */
@Api(tags = "场次洪水模型率定模块")
@RestController
@RequestMapping("modelParameters")
public class ModelParametersController {

    @Autowired
    private ModelParametersService modelParametersService;

    @ApiOperationSupport(order = 1)
    @ApiOperation("模型率定参数查询")
    @PostMapping("/queryList")
    public RestResponse queryList() {
        return RestResponse.ok(modelParametersService.queryList());
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("默认模型率定参数查询")
    @PostMapping("/queryDefaultList")
    public RestResponse queryDefaultList() {
        return RestResponse.ok(modelParametersService.queryDefaultList());
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation("率定参数明细")
    @PostMapping("/paramDetail")
    public RestResponse paramDetail(@RequestBody ModelParameterDetailReq req) {
        return modelParametersService.paramDetail(req);
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation("设置默认参数")
    @PostMapping("/setDefault")
    public RestResponse setDefault(@RequestBody ModelParametersReq input) {
        if (modelParametersService.setDefault(input)) {
            return RestResponse.ok(true);
        } else {
            return RestResponse.no("设置默认参数失败！");
        }
    }

    @ApiOperationSupport(order = 5)
    @ApiOperation("模型率定数据删除")
    @PostMapping("/del")
    public RestResponse del(@RequestBody List<String> input) {
        if (modelParametersService.del(input)) {
            return RestResponse.ok(true);
        } else {
            return RestResponse.no("删除失败！");
        }
    }

    @ApiOperationSupport(order = 6)
    @ApiOperation("历史来水过程")
    @PostMapping("/ls")
    public RestResponse ls(@RequestBody ModelParametersReq input) {
        if (modelParametersService.ls(input)) {
            return RestResponse.ok(true);
        } else {
            return RestResponse.no("删除失败！");
        }
    }

    @ApiOperationSupport(order = 7)
    @ApiOperation("模型率定")
    @PostMapping("/calibrate")
    public RestResponse calibrate(@RequestBody CalibrateReq input) {
        try{
            return RestResponse.ok(modelParametersService.calibrate(input));
        }
        catch (Exception ex){
            return RestResponse.no(ex.getMessage());
        }
    }
}

