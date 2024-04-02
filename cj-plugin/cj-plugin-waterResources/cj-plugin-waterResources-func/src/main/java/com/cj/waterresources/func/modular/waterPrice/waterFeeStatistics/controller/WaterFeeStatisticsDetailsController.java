package com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cj.business.log.modular.log.annotation.CommonLog;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.waterPrice.paymentWaterFees.bean.req.PaymentWaterFeesAddReq;
import com.cj.waterresources.func.modular.waterPrice.paymentWaterFees.bean.req.PaymentWaterFeesSelectListReq;
import com.cj.waterresources.func.modular.waterPrice.paymentWaterFees.bean.res.PaymentWaterFeesSelectListRes;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.bean.req.UseWaterTypeStatisticsReq;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.bean.req.WaterFeeStatisticsDetailsSelectListReq;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.entity.WaterFeeStatisticsDetails;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.service.WaterFeeStatisticsDetailsService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


/**
 * 水费统计详情(WaterFeeStatisticsDetails)表控制层
 *
 * @author makejava
 * @since 2023-11-29 17:15:42
 */
@Api(tags = "水费统计详情模块")
@ApiSupport(author = "LEO-LUOXU", order = 1)
@Validated
@RestController
@RequestMapping("waterFeeStatisticsDetails")
public class WaterFeeStatisticsDetailsController {

    @Autowired
    private WaterFeeStatisticsDetailsService waterFeeStatisticsDetailsService;

    @ApiOperationSupport(order = 1)
    @ApiOperation("水费统计详情模块新增")
    @CommonLog(value = "水费统计详情模块新增")
    @PostMapping("/insert")
    public RestResponse insert(@RequestBody List<WaterFeeStatisticsDetails> waterFeeStatisticsDetails) {
        return waterFeeStatisticsDetailsService.add(waterFeeStatisticsDetails);
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("水费统计详情模块更新")
    @CommonLog(value = "水费统计详情模块更新")
    @PostMapping("/update")
    public RestResponse update(@RequestBody List<WaterFeeStatisticsDetails> waterFeeStatisticsDetails) {
        return waterFeeStatisticsDetailsService.update(waterFeeStatisticsDetails);
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation("水费统计详情模块查询列表")
    @CommonLog(value = "水费统计详情模块查询列表")
    @PostMapping("/select")
    public RestResponse<Map<String, List<WaterFeeStatisticsDetails>>> select(@RequestBody WaterFeeStatisticsDetailsSelectListReq req) {
        return waterFeeStatisticsDetailsService.selectList(req);
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation("水费统计详情模块清空表格")
    @CommonLog(value = "水费统计详情模块清空表格")
    @PostMapping("/clearTable")
    public RestResponse clearTable(@RequestBody WaterFeeStatisticsDetailsSelectListReq req) {
        return waterFeeStatisticsDetailsService.clearTable(req);
    }

    @ApiOperationSupport(order = 5)
    @ApiOperation("水费统计详情模块新增历史记录")
    @CommonLog(value = "水费统计详情模块新增历史记录")
    @PostMapping("/insertHistory")
    public RestResponse insertHistory(@RequestBody List<List<WaterFeeStatisticsDetails>> waterFeeStatisticsDetailsList) {
        return waterFeeStatisticsDetailsService.addHistory(waterFeeStatisticsDetailsList);
    }

    @ApiOperationSupport(order = 6)
    @ApiOperation("水费统计详情模块用水类型统计")
    @CommonLog(value = "水费统计详情模块用水类型统计")
    @PostMapping("/useWaterTypeStatistics")
    public RestResponse useWaterTypeStatistics(@RequestBody UseWaterTypeStatisticsReq req) {
        return waterFeeStatisticsDetailsService.useWaterTypeStatistics(req);
    }

    @ApiOperationSupport(order = 7)
    @ApiOperation("水费统计详情模块删除用户创建缓存")
    @CommonLog(value = "水费统计详情模块删除用户创建缓存")
    @PostMapping("/deleteRedisData")
    public RestResponse deleteRedisData(@RequestBody WaterFeeStatisticsDetailsSelectListReq req) {
        return waterFeeStatisticsDetailsService.deleteRedisData(req);
    }
}

