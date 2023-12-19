package com.cj.waterresources.func.modular.waterPrice.canalHeadManagementStation.controller;

import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.waterPrice.canalHeadManagementStation.service.CanalHeadManagementStationTotalService;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.bean.req.WaterFeeStatisticsDetailsSelectListReq;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 渠首管理站总计表(CanalHeadManagementStationTotal)表控制层
 *
 * @author makejava
 * @since 2023-12-15 18:08:09
 */
@Api(tags = "水费统计详情模块-渠首管理站总计")
@ApiSupport(author = "LEO-LUOXU", order = 1)
@Validated
@RestController
@RequestMapping("canalHeadManagementStationTotal")
public class CanalHeadManagementStationTotalController{

    @Autowired
    private CanalHeadManagementStationTotalService canalHeadManagementStationTotalService;

    @ApiOperationSupport(order = 1)
    @ApiOperation("查询列表")
    @PostMapping("/select")
    public RestResponse select(@RequestBody WaterFeeStatisticsDetailsSelectListReq req) {
        return canalHeadManagementStationTotalService.selectList(req);
    }
}

