package com.cj.waterresources.func.modular.waterPrice.paymentWaterFees.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cj.business.log.modular.log.annotation.CommonLog;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.waterPrice.paymentWaterFees.bean.req.PaymentWaterFeesAddReq;
import com.cj.waterresources.func.modular.waterPrice.paymentWaterFees.bean.req.PaymentWaterFeesSelectListReq;
import com.cj.waterresources.func.modular.waterPrice.paymentWaterFees.bean.res.PaymentWaterFeesSelectListRes;
import com.cj.waterresources.func.modular.waterPrice.paymentWaterFees.service.PaymentWaterFeesService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
/**
 * 水费缴纳(PaymentWaterFees)表控制层
 *
 * @author makejava
 * @since 2023-11-29 11:28:29
 */
@Api(tags = "水费缴纳管理模块")
@ApiSupport(author = "LEO-LUOXU", order = 1)
@Validated
@RestController
@RequestMapping("paymentWaterFees")
public class
PaymentWaterFeesController{

    @Autowired
    private PaymentWaterFeesService paymentWaterFeesService;

    @ApiOperationSupport(order = 1)
    @ApiOperation("水费缴纳管理模块删除")
    @CommonLog(value = "水费缴纳管理模块删除")
    @GetMapping("/delete")
    public RestResponse delete(@RequestParam("id") String id) {
        return paymentWaterFeesService.delete(id);
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("水费缴纳管理模块新增")
    @CommonLog(value = "水费缴纳管理模块新增")
    @PostMapping("/insert")
    public RestResponse insert(@RequestBody PaymentWaterFeesAddReq req) {
        return paymentWaterFeesService.paymentWaterFeesAdd(req);
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation("水费缴纳管理模块查询列表")
    @CommonLog(value = "水费缴纳管理模块查询列表")
    @PostMapping("/select")
    public RestResponse<IPage<PaymentWaterFeesSelectListRes>> select(@RequestBody PaymentWaterFeesSelectListReq req) {
        return paymentWaterFeesService.paymentWaterFeesSelectList(req);
    }

}

