package com.cj.waterresources.func.modular.waterStorageScheduling.waterStorageSchedulingTth.controller;

import com.cj.business.log.modular.log.annotation.CommonLog;
import com.cj.common.model.RestResponse;
import com.cj.common.util.UUIDUtils;
import com.cj.waterresources.func.modular.waterStorageScheduling.waterStorageSchedulingTth.service.WaterStorageSchedulingTthService;
import com.cj.waterresources.func.modular.waterStorageScheduling.waterStorageSchedulingTth.entity.WaterStorageSchedulingTth;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 头屯河水库蓄水调度计划表(WaterStorageSchedulingTth)表控制层
 *
 * @author makejava
 * @since 2023-12-12 10:20:44
 */
@Api(tags = "头屯河水库蓄水调度计划表模块")
@ApiSupport(author = "LEO-LUOXU", order = 1)
@Validated
@RestController
@RequestMapping("waterStorageSchedulingTth")
public class WaterStorageSchedulingTthController{

    @Autowired
    private WaterStorageSchedulingTthService waterStorageSchedulingTthService;

    @ApiOperationSupport(order = 1)
    @ApiOperation("头屯河水库蓄水调度计划表模块删除")
    @CommonLog("头屯河水库蓄水调度计划表模块删除")
    @GetMapping("/delete")
    public RestResponse delete(@RequestParam("id") String id) {
        boolean b = waterStorageSchedulingTthService.lambdaUpdate().set(WaterStorageSchedulingTth::getDel,1).eq(WaterStorageSchedulingTth::getId,id).update();
        if(b){
            return RestResponse.ok("删除成功");
        }else {
            return RestResponse.no("删除失败");
        }
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("头屯河水库蓄水调度计划表模块新增")
    @CommonLog("头屯河水库蓄水调度计划表模块新增")
    @PostMapping("/add")
    public RestResponse add(@RequestParam("formId") String formId) {
        return waterStorageSchedulingTthService.add(formId);
    }
    @ApiOperationSupport(order = 3)
    @ApiOperation("头屯河水库蓄水调度计划表模块修改")
    @CommonLog("头屯河水库蓄水调度计划表模块修改")
    @PostMapping("/update")
    public RestResponse update(@RequestBody WaterStorageSchedulingTth waterStorageSchedulingTth) {
        return waterStorageSchedulingTthService.edit(waterStorageSchedulingTth);
    }


    @ApiOperationSupport(order = 4)
    @ApiOperation("头屯河水库蓄水调度计划表模块查询列表")
    @CommonLog("头屯河水库蓄水调度计划表模块查询列表")
    @GetMapping("/select")
    public RestResponse<List<WaterStorageSchedulingTth>> select(@RequestParam(value = "formId") String formId) {
        List<WaterStorageSchedulingTth> list = waterStorageSchedulingTthService.lambdaQuery().eq(WaterStorageSchedulingTth::getFormId, formId).eq(WaterStorageSchedulingTth::getDel, 0).list();
        if(null != list && list.size()>0){
            return RestResponse.ok(list);
        }else {
            return RestResponse.no("暂无数据");
        }
    }

}

