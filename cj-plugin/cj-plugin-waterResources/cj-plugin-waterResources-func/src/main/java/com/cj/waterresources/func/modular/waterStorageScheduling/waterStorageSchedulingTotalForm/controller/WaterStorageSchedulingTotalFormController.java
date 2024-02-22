package com.cj.waterresources.func.modular.waterStorageScheduling.waterStorageSchedulingTotalForm.controller;

import com.cj.common.model.RestResponse;
import com.cj.common.util.UUIDUtils;
import com.cj.waterresources.func.modular.waterStorageScheduling.waterStorageSchedulingLzz.entity.WaterStorageSchedulingLzz;
import com.cj.waterresources.func.modular.waterStorageScheduling.waterStorageSchedulingTotalForm.entity.WaterStorageSchedulingTotalForm;
import com.cj.waterresources.func.modular.waterStorageScheduling.waterStorageSchedulingTotalForm.service.WaterStorageSchedulingTotalFormService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 供水计划管理总表(WaterStorageSchedulingTotalForm)表控制层
 *
 * @author makejava
 * @since 2024-02-18 09:43:27
 */
@Api(tags = "供水计划管理总表")
@ApiSupport(author = "LEO-LUOXU", order = 1)
@Validated
@RestController
@RequestMapping("waterStorageSchedulingTotalForm")
public class WaterStorageSchedulingTotalFormController{
    /**
     * 服务对象
     */
    @Resource
    private WaterStorageSchedulingTotalFormService waterStorageSchedulingTotalFormService;

    @ApiOperationSupport(order = 1)
    @ApiOperation("删除")
    @GetMapping("/delete")
    public RestResponse delete(@RequestParam("id") String id) {
        return waterStorageSchedulingTotalFormService.remove(id);
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("新增")
    @PostMapping("/add")
    public RestResponse add(@RequestBody WaterStorageSchedulingTotalForm waterStorageSchedulingTotalForm) {
        return waterStorageSchedulingTotalFormService.add(waterStorageSchedulingTotalForm);
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation("查询列表")
    @GetMapping("/select")
    public RestResponse<List<WaterStorageSchedulingTotalForm>> select() {
        List<WaterStorageSchedulingTotalForm> list = waterStorageSchedulingTotalFormService.list();
        if(null != list && list.size()>0){
            return RestResponse.ok(list);
        }else {
            return RestResponse.no("暂无数据");
        }
    }
}

