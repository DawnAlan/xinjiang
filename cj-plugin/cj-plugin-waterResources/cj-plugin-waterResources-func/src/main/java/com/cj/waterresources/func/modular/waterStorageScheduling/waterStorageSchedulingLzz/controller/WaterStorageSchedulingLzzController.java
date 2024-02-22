package com.cj.waterresources.func.modular.waterStorageScheduling.waterStorageSchedulingLzz.controller;

import com.cj.common.model.RestResponse;
import com.cj.common.util.UUIDUtils;
import com.cj.waterresources.func.modular.waterStorageScheduling.waterStorageSchedulingLzz.service.WaterStorageSchedulingLzzService;
import com.cj.waterresources.func.modular.waterStorageScheduling.waterStorageSchedulingLzz.entity.WaterStorageSchedulingLzz;
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
 * 楼庄子水库蓄水调度计划表(WaterStorageSchedulingLzz)表控制层
 *
 * @author makejava
 * @since 2023-12-12 10:20:20
 */
@Api(tags = "楼庄子水库蓄水调度计划表模块")
@ApiSupport(author = "LEO-LUOXU", order = 1)
@Validated
@RestController
@RequestMapping("waterStorageSchedulingLzz")
public class WaterStorageSchedulingLzzController {

    @Autowired
    private WaterStorageSchedulingLzzService waterStorageSchedulingLzzService;

    @ApiOperationSupport(order = 1)
    @ApiOperation("删除")
    @GetMapping("/delete")
    public RestResponse delete(@RequestParam("id") String id) {
        boolean b = waterStorageSchedulingLzzService.lambdaUpdate().set(WaterStorageSchedulingLzz::getDel,1).eq(WaterStorageSchedulingLzz::getId,id).update();
        if(b){
            return RestResponse.ok("删除成功");
        }else {
            return RestResponse.no("删除失败");
        }
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("新增")
    @PostMapping("/add")
    public RestResponse add(@RequestParam("formId") String formId) {
        return waterStorageSchedulingLzzService.add(formId);
    }
    @ApiOperationSupport(order = 3)
    @ApiOperation("修改")
    @PostMapping("/update")
    public RestResponse update(@RequestBody List<WaterStorageSchedulingLzz> waterStorageSchedulingLzz) {
        return waterStorageSchedulingLzzService.edit(waterStorageSchedulingLzz);
    }


    @ApiOperationSupport(order = 4)
    @ApiOperation("查询列表")
    @GetMapping("/select")
    public RestResponse<List<WaterStorageSchedulingLzz>> select(@RequestParam(value = "formId") String formId) {
        List<WaterStorageSchedulingLzz> list = waterStorageSchedulingLzzService.lambdaQuery().eq(WaterStorageSchedulingLzz::getFormId, formId).eq(WaterStorageSchedulingLzz::getDel, 0).list();
        if(null != list && list.size()>0){
            return RestResponse.ok(list);
        }else {
            return RestResponse.no("暂无数据");
        }
    }
}

