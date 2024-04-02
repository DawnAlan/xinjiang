package com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.controller;


import com.cj.business.log.modular.log.annotation.CommonLog;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.bean.req.WaterFeeStatisticsDetailsSelectListReq;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.entity.WaterFeeStatisticsDetails;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.entity.WaterFeeStatisticsTotal;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.service.WaterFeeStatisticsTotalService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 水费统计总计(WaterFeeStatisticsTotal)表控制层
 *
 * @author makejava
 * @since 2023-11-29 17:16:56
 */
@Api(tags = "水费统计总计模块")
@ApiSupport(author = "LEO-LUOXU", order = 1)
@Validated
@RestController
@RequestMapping("waterFeeStatisticsTotal")
public class WaterFeeStatisticsTotalController{

    @Autowired
    private WaterFeeStatisticsTotalService waterFeeStatisticsTotalService;

    @ApiOperationSupport(order = 1)
    @ApiOperation("水费统计总计模块查询列表")
    @CommonLog(value = "水费统计总计模块查询列表")
    @PostMapping("/select")
    public RestResponse<List<WaterFeeStatisticsTotal>> select(@RequestBody WaterFeeStatisticsDetailsSelectListReq req) {
        return waterFeeStatisticsTotalService.selectInfoList(req);
    }

    @ApiOperationSupport(order = 1)
    @ApiOperation("水费统计总计模块修改备注")
    @CommonLog(value = "水费统计总计模块修改备注")
    @PostMapping("/updateRemark")
    public RestResponse updateRemark(@RequestBody WaterFeeStatisticsTotal total) {
        boolean update = waterFeeStatisticsTotalService.lambdaUpdate().set(WaterFeeStatisticsTotal::getRemark, total.getRemark()).eq(WaterFeeStatisticsTotal::getId, total.getId()).update();
        if(update){
            return RestResponse.ok();
        }else {
            return RestResponse.no("Error");
        }
    }
}

