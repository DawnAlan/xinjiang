package com.cj.flood.func.modular.prediction.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cj.common.model.RestResponse;
import com.cj.flood.func.modular.prediction.bean.req.IncomingWaterForecastAddReq;
import com.cj.flood.func.modular.prediction.bean.req.IncomingWaterForecastListReq;
import com.cj.flood.func.modular.prediction.entity.IncomingWaterForecast;
import com.cj.flood.func.modular.prediction.service.IncomingWaterForecastService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api(tags = "来水预报模块")
@ApiSupport(author = "LEO-LUOXU", order = 1)
@RestController
@Validated
@RequestMapping("incomingWaterForecast")
public class IncomingWaterForecastController {

    @Autowired
    private IncomingWaterForecastService incomingWaterForecastService;

    @ApiOperationSupport(order = 1)
    @ApiOperation("新增")
    @PostMapping("/add")
    public RestResponse add(@RequestBody IncomingWaterForecastAddReq req) {
        return incomingWaterForecastService.add(req);
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("查询可视化界面")
    @GetMapping("/details")
    public RestResponse details(@RequestParam(value = "id") String id) {
        return incomingWaterForecastService.selectDetails(id);
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation("删除")
    @GetMapping("/delete")
    public RestResponse delete(@RequestParam(value = "id") String id) {
        return incomingWaterForecastService.delete(id);
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation("更新")
    @PostMapping("/update")
    public RestResponse update(@RequestBody IncomingWaterForecast incomingWaterForecast) {
        return incomingWaterForecastService.update(incomingWaterForecast);
    }

    @ApiOperationSupport(order = 5)
    @ApiOperation("查询列表")
    @PostMapping("/selectList")
    public RestResponse<IPage<IncomingWaterForecast>> selectList(@RequestBody IncomingWaterForecastListReq req) {
        return incomingWaterForecastService.selectList(req);
    }
}
