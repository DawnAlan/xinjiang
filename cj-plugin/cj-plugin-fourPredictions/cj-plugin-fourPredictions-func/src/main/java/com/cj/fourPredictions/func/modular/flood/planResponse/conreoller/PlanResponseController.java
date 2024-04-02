package com.cj.fourPredictions.func.modular.flood.planResponse.conreoller;

import com.cj.business.log.modular.log.annotation.CommonLog;
import com.cj.common.model.RestResponse;
import com.cj.fourPredictions.func.modular.flood.forecastEarlyWarning.bean.res.IncomingWaterForecast;
import com.cj.fourPredictions.func.modular.flood.planResponse.bean.res.FloodControlOperation;
import com.cj.fourPredictions.func.modular.flood.planResponse.service.PlanResponseService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "防洪兴利-预案响应")
@ApiSupport(author = "LEO-LUOXU", order = 1)
@Validated
@RestController
@RequestMapping("planResponse")
public class PlanResponseController {

    @Autowired
    private PlanResponseService planResponseService;

    @ApiOperationSupport(order = 1)
    @ApiOperation("防洪兴利-预案响应根据来水预报id查询洪水调度方案列表")
    @CommonLog(value = "防洪兴利-预案响应根据来水预报id查询洪水调度方案列表")
    @GetMapping("/getFloodControlOperationListById")
    public RestResponse<List<FloodControlOperation>> getFloodControlOperationListById(@RequestParam(value = "id")String id) {
        return planResponseService.getFloodControlOperationListById(id);
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("防洪兴利-预案响应根据洪水调度方案id查询洪水调度方案部分信息")
    @CommonLog(value = "防洪兴利-预案响应根据洪水调度方案id查询洪水调度方案部分信息")
    @GetMapping("/getFloodControlOperationFrontViewById")
    public RestResponse getFloodControlOperationFrontViewById(@RequestParam(value = "id")String id) {
        return planResponseService.getFloodControlOperationFrontViewById(id);
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("防洪兴利-预案响应根据洪水调度方案id查询洪水调度方案详情")
    @CommonLog(value = "防洪兴利-预案响应根据洪水调度方案id查询洪水调度方案详情")
    @GetMapping("/getFloodControlOperationDetails")
    public RestResponse getFloodControlOperationDetails(@RequestParam(value = "id")String id) {
        return planResponseService.getFloodControlOperationDetails(id);
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation("防洪兴利-预案响应获取场次来水预报模型列表")
    @CommonLog(value = "防洪兴利-预案响应获取场次来水预报模型列表")
    @GetMapping("/getProgrammeListForFloodControlOperation")
    public RestResponse<List<IncomingWaterForecast>> getProgrammeListForFloodControlOperation() {
        return planResponseService.getProgrammeListForFloodControlOperation();
    }
}
