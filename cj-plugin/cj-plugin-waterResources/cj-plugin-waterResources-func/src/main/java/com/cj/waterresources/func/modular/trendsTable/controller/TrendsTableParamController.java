package com.cj.waterresources.func.modular.trendsTable.controller;


import com.cj.common.annotation.CommonLog;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.trendsTable.bean.req.QueryTrendsTableParamReq;
import com.cj.waterresources.func.modular.trendsTable.bean.req.TrendsTableParamAddReq;
import com.cj.waterresources.func.modular.trendsTable.bean.req.TrendsTableParamUpdateReq;
import com.cj.waterresources.func.modular.trendsTable.bean.res.WaterDailyParamSelectRes;
import com.cj.waterresources.func.modular.trendsTable.entity.TrendsTableParam;
import com.cj.waterresources.func.modular.trendsTable.service.TrendsTableParamService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 水情日报(WaterDaily)表控制层
 *
 * @author makejava
 * @since 2023-10-27 10:06:40
 */
@Api(tags = "动态表头模块")
@ApiSupport(author = "LEO-LUOXU", order = 1)
@RestController
@Validated
@RequestMapping("trendsParam")
public class TrendsTableParamController {
    /**
     * 服务对象
     */
    @Resource
    private TrendsTableParamService trendsTableParamService;


    @ApiOperationSupport(order = 1)
    @ApiOperation("动态表头模块新增")
    @CommonLog(value = "动态表头模块新增")
    @PostMapping("/add")
    public RestResponse add(@RequestBody TrendsTableParamAddReq req) {
        return trendsTableParamService.add(req);
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("删除")
    @GetMapping("/delete")
    public RestResponse delete(@RequestParam("id") String id) {
        return trendsTableParamService.delete(id);
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation("修改")
    @PostMapping("/update")
    public RestResponse update(@RequestBody TrendsTableParamUpdateReq param) {
        return trendsTableParamService.update(param);
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation("查询列表")
    @PostMapping("/select")
    public RestResponse<List<WaterDailyParamSelectRes>> select(@RequestBody QueryTrendsTableParamReq req) {
        return trendsTableParamService.select(req);
    }


}

