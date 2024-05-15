package com.cj.flood.func.modular.prediction.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cj.business.log.modular.log.annotation.CommonLog;
import com.cj.common.model.RestResponse;
import com.cj.flood.func.modular.prediction.bean.req.IncomingWaterForecastAddReq;
import com.cj.flood.func.modular.prediction.bean.req.IncomingWaterForecastListReq;
import com.cj.flood.func.modular.prediction.bean.req.WaterResourceAllocationTimeReq;
import com.cj.flood.func.modular.prediction.entity.IncomingWaterForecast;
import com.cj.flood.func.modular.prediction.service.IncomingWaterForecastService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "来水预报模块")
@ApiSupport(author = "LEO-LUOXU", order = 1)
@RestController
@Validated
@RequestMapping("incomingWaterForecast")
@Slf4j
public class IncomingWaterForecastController {

    @Autowired
    private IncomingWaterForecastService incomingWaterForecastService;

    @ApiOperationSupport(order = 1)
    @ApiOperation("来水预报模块新增")
    @CommonLog(value = "来水预报模块新增")
    @PostMapping("/add")
    public RestResponse add(@RequestBody IncomingWaterForecastAddReq req) {
        return incomingWaterForecastService.add(req);
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("来水预报模块查询可视化界面")
    @CommonLog(value = "来水预报模块查询可视化界面")
    @GetMapping("/details")
    public RestResponse details(@RequestParam(value = "id") String id) {
        log.error("--------------------------------进入查询模型详情接口id："+id);
        return incomingWaterForecastService.selectDetails(id);
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation("来水预报模块删除")
    @CommonLog(value = "来水预报模块删除")
    @GetMapping("/delete")
    public RestResponse delete(@RequestParam(value = "ids") String ids) {
        return incomingWaterForecastService.delete(ids);
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation("来水预报模块更新")
    @CommonLog(value = "来水预报模块更新")
    @PostMapping("/update")
    public RestResponse update(@RequestBody IncomingWaterForecast incomingWaterForecast) {
        return incomingWaterForecastService.update(incomingWaterForecast);
    }

    @ApiOperationSupport(order = 5)
    @ApiOperation("来水预报模块查询列表")
    @CommonLog(value = "来水预报模块查询列表")
    @PostMapping("/selectList")
    public RestResponse<IPage<IncomingWaterForecast>> selectList(@RequestBody IncomingWaterForecastListReq req) {
        return incomingWaterForecastService.selectList(req);
    }

    @ApiOperationSupport(order = 6)
    @ApiOperation("来水预报模块配水时间查询列表")
    @CommonLog(value = "来水预报模块配水时间查询列表")
    @PostMapping("/selectListByTime")
    public RestResponse<List<IncomingWaterForecast>> selectListByTime(@RequestBody WaterResourceAllocationTimeReq req) {
        return incomingWaterForecastService.selectListByTime(req);
    }
}
