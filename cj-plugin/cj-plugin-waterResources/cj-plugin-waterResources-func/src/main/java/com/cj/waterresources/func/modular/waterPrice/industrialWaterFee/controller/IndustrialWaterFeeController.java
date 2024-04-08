package com.cj.waterresources.func.modular.waterPrice.industrialWaterFee.controller;

import com.cj.business.log.modular.log.annotation.CommonLog;
import com.cj.common.model.RestResponse;
import com.cj.common.util.UUIDUtils;
import com.cj.waterresources.func.modular.waterPrice.industrialWaterFee.bean.req.SelectPaymentReq;
import com.cj.waterresources.func.modular.waterPrice.industrialWaterFee.entity.IndustrialWaterFee;
import com.cj.waterresources.func.modular.waterPrice.industrialWaterFee.entity.WaterManagementUrbanIndustry;
import com.cj.waterresources.func.modular.waterPrice.industrialWaterFee.service.IndustrialWaterFeeService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 工业水费(IndustrialWaterFee)表控制层
 *
 * @author makejava
 * @since 2024-01-31 20:11:18
 */
@Api(tags = "工业水费")
@ApiSupport(author = "LEO-LUOXU", order = 1)
@RestController
@Validated
@RequestMapping("industrialWaterFee")
public class IndustrialWaterFeeController {
    /**
     * 服务对象
     */
    @Resource
    private IndustrialWaterFeeService industrialWaterFeeService;

    @ApiOperationSupport(order = 1)
    @ApiOperation("工业水费新增")
    @CommonLog(value = "工业水费新增")
    @PostMapping("/add")
    public RestResponse add(@RequestBody List<IndustrialWaterFee> industrialWaterFeeList) {
        boolean b = industrialWaterFeeService.saveBatch(industrialWaterFeeList);
        if(b){
            return RestResponse.ok("新增成功");
        }else {
            return RestResponse.no("新增失败");
        }
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("工业水费删除")
    @CommonLog(value = "工业水费删除")
    @GetMapping("/delete")
    public RestResponse delete(@RequestParam("station") String station,
                               @RequestParam("year") Integer year,@RequestParam("month") Integer month) {
        boolean remove = industrialWaterFeeService.lambdaUpdate().eq(IndustrialWaterFee::getStation, station).
                eq(IndustrialWaterFee::getYear, year).eq(IndustrialWaterFee::getMonth, month).remove();
        if(remove){
            return RestResponse.ok("删除成功");
        }else {
            return RestResponse.no("删除失败");
        }
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation("工业水费修改")
    @CommonLog(value = "工业水费修改")
    @PostMapping("/update")
    public RestResponse update(@RequestBody List<IndustrialWaterFee> industrialWaterFeeList) {
        boolean b = industrialWaterFeeService.updateBatchById(industrialWaterFeeList);
        if(b){
            return RestResponse.ok("修改成功");
        }else {
            return RestResponse.no("修改失败");
        }
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation("工业水费查询列表")
    @CommonLog(value = "工业水费查询列表")
    @GetMapping("/select")
    public RestResponse<List<IndustrialWaterFee>> select(@RequestParam("station") String station,
                                                         @RequestParam("year") Integer year,
                                                         @RequestParam("month") Integer month) {
        List<IndustrialWaterFee> list = industrialWaterFeeService.lambdaQuery().eq(IndustrialWaterFee::getStation, station).
                eq(IndustrialWaterFee::getYear, year).eq(IndustrialWaterFee::getMonth, month).list();
        if(null != list && list.size()>0){
            return RestResponse.ok(list);
        }else {
            return RestResponse.no("暂无数据");
        }
    }
    //selectPayment
    @ApiOperationSupport(order = 4)
    @ApiOperation("工业水费缴费查询")
    @CommonLog(value = "工业水费缴费查询")
    @PostMapping ("/selectPayment")
    public RestResponse<WaterManagementUrbanIndustry> selectPayment(@RequestBody SelectPaymentReq input) {
        return RestResponse.ok(industrialWaterFeeService.selectPayment(input));
    }
}

