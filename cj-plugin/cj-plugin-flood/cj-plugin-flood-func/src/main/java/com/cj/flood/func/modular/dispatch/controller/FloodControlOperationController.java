package com.cj.flood.func.modular.dispatch.controller;




import com.cj.business.log.modular.log.annotation.CommonLog;
import com.cj.common.model.RestResponse;
import com.cj.flood.func.modular.dispatch.bean.req.FloodControlOperationAddReq;
import com.cj.flood.func.modular.dispatch.bean.req.FloodControlOperationListReq;
import com.cj.flood.func.modular.dispatch.service.FloodControlOperationService;
import com.cj.flood.func.modular.prediction.entity.IncomingWaterForecast;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * 防洪调度表(FloodControlOperation)表控制层
 *
 * @author makejava
 * @since 2023-11-09 15:49:42
 */
@RestController
@RequestMapping("floodControlOperation")
@Api(tags = "防洪调度模块")
@ApiSupport(author = "LEO-LUOXU", order = 2)
@Validated
public class FloodControlOperationController {

    @Autowired
    private FloodControlOperationService floodControlOperationService;

    @ApiOperationSupport(order = 1)
    @ApiOperation("防洪调度模块预报断面")
    @CommonLog(value = "防洪调度模块预报断面")
    @GetMapping("/details")
    public RestResponse details(@RequestParam(value = "id") String id) {
        return floodControlOperationService.selectDetails(id);
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("防洪调度模块新增")
    @CommonLog(value = "防洪调度模块新增")
    @PostMapping("/add")
    public RestResponse add(@RequestBody FloodControlOperationAddReq req) {
        return floodControlOperationService.add(req);
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation("防洪调度模块查询列表")
    @CommonLog(value = "防洪调度模块查询列表")
    @PostMapping("/selectList")
    public RestResponse selectList(@RequestBody FloodControlOperationListReq req) {
        return floodControlOperationService.selectList(req);
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation("防洪调度模块方案对比")
    @CommonLog(value = "防洪调度模块方案对比")
    @GetMapping("/containmentCalculator")
    public RestResponse containmentCalculator(@RequestParam(value = "ids") String ids) {
        return floodControlOperationService.containmentCalculator(ids);
    }

    @ApiOperationSupport(order = 5)
    @ApiOperation("防洪调度模块删除")
    @CommonLog(value = "防洪调度模块删除")
    @GetMapping("/delete")
    public RestResponse delete(@RequestParam(value = "ids") String ids) {
        boolean b = floodControlOperationService.removeByIds(Arrays.stream(ids.split(",")).collect(Collectors.toList()));
        if(b) {
            return RestResponse.ok();
        }else {
            return RestResponse.no("error");
        }
    }

}

