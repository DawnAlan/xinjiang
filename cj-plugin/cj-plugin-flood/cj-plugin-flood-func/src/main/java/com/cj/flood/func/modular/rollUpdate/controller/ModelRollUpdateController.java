package com.cj.flood.func.modular.rollUpdate.controller;


import com.cj.business.log.modular.log.annotation.CommonLog;
import com.cj.common.model.RestResponse;
import com.cj.flood.func.modular.prediction.bean.req.IncomingWaterForecastAddReq;
import com.cj.flood.func.modular.rollUpdate.bean.req.ModelRollUpdateSelectListReq;
import com.cj.flood.func.modular.rollUpdate.entity.ModelRollUpdate;
import com.cj.flood.func.modular.rollUpdate.service.ModelRollUpdateService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 模型滚动更新表(ModelRollUpdate)表控制层
 *
 * @author makejava
 * @since 2024-07-19 14:59:16
 */
@Api(tags = "模型滚动更新")
@ApiSupport(author = "LEO-LUOXU", order = 1)
@Validated
@RestController
@RequestMapping("modelRollUpdate")
public class ModelRollUpdateController {
    /**
     * 服务对象
     */
    @Resource
    private ModelRollUpdateService modelRollUpdateService;

    @ApiOperationSupport(order = 1)
    @ApiOperation("模型滚动更新模块新增")
    @CommonLog(value = "模型滚动更新模块新增")
    @PostMapping("/add")
    public RestResponse add(@RequestBody ModelRollUpdate modelRollUpdate) {
        return modelRollUpdateService.add(modelRollUpdate);
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("模型滚动更新模块修改")
    @CommonLog(value = "模型滚动更新模块修改")
    @PostMapping("/update")
    public RestResponse update(@RequestBody ModelRollUpdate modelRollUpdate) {
        return modelRollUpdateService.update(modelRollUpdate);
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation("模型滚动更新模块查询列表")
    @CommonLog(value = "模型滚动更新模块查询列表")
    @PostMapping("/selectList")
    public RestResponse selectList(@RequestBody ModelRollUpdateSelectListReq req) {
        return modelRollUpdateService.selectList(req);
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation("模型滚动更新模块删除")
    @CommonLog(value = "模型滚动更新模块删除")
    @GetMapping("/delete")
    public RestResponse delete(@RequestParam(value = "ids") String ids) {
        return modelRollUpdateService.delete(ids);
    }

    @ApiOperationSupport(order = 5)
    @ApiOperation("模型滚动更新模块结束滚动")
    @CommonLog(value = "模型滚动更新模块结束滚动")
    @GetMapping("/stop")
    public RestResponse stop(@RequestParam(value = "ids") String ids) {
        return modelRollUpdateService.stop(ids);
    }

    @ApiOperationSupport(order = 6)
    @ApiOperation("模型滚动更新模块开启滚动")
    @CommonLog(value = "模型滚动更新模块开启滚动")
    @GetMapping("/start")
    public RestResponse start(@RequestParam(value = "ids") String ids) {
        return modelRollUpdateService.start(ids);
    }

    @ApiOperationSupport(order = 7)
    @ApiOperation("模型滚动更新模块查询模型结果列表")
    @CommonLog(value = "模型滚动更新模块查询模型结果列表")
    @GetMapping("/selectModelResultList")
    public RestResponse selectModelResultList(@RequestParam(value = "id") String id) {
        return modelRollUpdateService.selectModelResultList(id);
    }

    @ApiOperationSupport(order = 8)
    @ApiOperation("模型滚动更新模块查询模型详情")
    @CommonLog(value = "模型滚动更新模块查询模型详情")
    @GetMapping("/selectDetailsById")
    public RestResponse selectDetailsById(@RequestParam(value = "id") String id) {
        return modelRollUpdateService.selectDetailsById(id);
    }

    @ApiOperationSupport(order = 8)
    @ApiOperation("模型滚动更新模块查询模型详情")
    @CommonLog(value = "模型滚动更新模块查询模型详情")
    @GetMapping("/selectDetailsByInComingWaterId")
    public RestResponse selectDetailsByInComingWaterId(@RequestParam(value = "inComingWaterId") String inComingWaterId) {
        return modelRollUpdateService.selectDetailsByInComingWaterId(inComingWaterId);
    }
}

