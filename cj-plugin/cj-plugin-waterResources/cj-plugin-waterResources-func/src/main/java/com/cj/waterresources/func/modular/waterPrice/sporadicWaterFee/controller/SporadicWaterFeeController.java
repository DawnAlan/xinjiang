package com.cj.waterresources.func.modular.waterPrice.sporadicWaterFee.controller;

import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.waterPrice.sporadicWaterFee.bean.req.SporadicWaterFeeSelectListReq;
import com.cj.waterresources.func.modular.waterPrice.sporadicWaterFee.entity.SporadicWaterFee;
import com.cj.waterresources.func.modular.waterPrice.sporadicWaterFee.service.SporadicWaterFeeService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

/**
 * 零星水费(SporadicWaterFee)表控制层
 *
 * @author makejava
 * @since 2024-02-01 08:58:07
 */
@Api(tags = "零星水费")
@ApiSupport(author = "LEO-LUOXU", order = 1)
@RestController
@Validated
@RequestMapping("sporadicWaterFee")
public class SporadicWaterFeeController {
    /**
     * 服务对象
     */
    @Resource
    private SporadicWaterFeeService sporadicWaterFeeService;

    @ApiOperationSupport(order = 1)
    @ApiOperation("新增")
    @PostMapping("/add")
    public RestResponse add(@RequestBody SporadicWaterFee sporadicWaterFee) {
        return sporadicWaterFeeService.add(sporadicWaterFee);
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("删除")
    @GetMapping("/delete")
    public RestResponse delete(@RequestParam("ids") String ids) {
        return sporadicWaterFeeService.delete(ids);
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation("修改")
    @PostMapping("/update")
    public RestResponse update(@RequestBody SporadicWaterFee sporadicWaterFee) {
        return sporadicWaterFeeService.update(sporadicWaterFee);
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation("查询列表")
    @PostMapping("/select")
    public RestResponse<List<SporadicWaterFee>> select(@RequestBody SporadicWaterFeeSelectListReq req) {
        return sporadicWaterFeeService.selectList(req);
    }
}

