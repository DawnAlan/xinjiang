package com.cj.waterresources.func.modular.waterPrice.canalHeadManagementStation.controller;

import com.cj.common.annotation.CommonLog;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.waterPrice.canalHeadManagementStation.entity.CanalHeadManagementStationDetails;
import com.cj.waterresources.func.modular.waterPrice.canalHeadManagementStation.service.CanalHeadManagementStationDetailsService;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.bean.req.WaterFeeStatisticsDetailsSelectListReq;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.entity.WaterFeeStatisticsDetails;
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
 * 渠首管理站明细表(CanalHeadManagementStationDetails)表控制层
 *
 * @author makejava
 * @since 2023-12-15 18:07:47
 */
@Api(tags = "水费统计详情模块-渠首管理站明细")
@ApiSupport(author = "LEO-LUOXU", order = 1)
@Validated
@RestController
@RequestMapping("canalHeadManagementStationDetails")
public class CanalHeadManagementStationDetailsController {

    @Autowired
    private CanalHeadManagementStationDetailsService canalHeadManagementStationDetailsService;


    @ApiOperationSupport(order = 1)
    @ApiOperation("水费统计详情模块-渠首管理站明细新增")
    @CommonLog(value = "水费统计详情模块-渠首管理站明细新增")
    @PostMapping("/insert")
    public RestResponse insert(@RequestBody CanalHeadManagementStationDetails canalHeadManagementStationDetails) {
        return canalHeadManagementStationDetailsService.add(canalHeadManagementStationDetails);
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("水费统计详情模块-渠首管理站明细更新")
    @CommonLog(value = "水费统计详情模块-渠首管理站明细更新")
    @PostMapping("/update")
    public RestResponse update(@RequestBody CanalHeadManagementStationDetails canalHeadManagementStationDetails) {
        return canalHeadManagementStationDetailsService.update(canalHeadManagementStationDetails);
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation("水费统计详情模块-渠首管理站明细查询列表")
    @CommonLog(value = "水费统计详情模块-渠首管理站明细查询列表")
    @PostMapping("/select")
    public RestResponse select(@RequestBody WaterFeeStatisticsDetailsSelectListReq req) {
        return canalHeadManagementStationDetailsService.selectList(req);
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation("水费统计详情模块-渠首管理站明细清空表")
    @CommonLog(value = "水费统计详情模块-渠首管理站明细清空表")
    @PostMapping("/remove")
    public RestResponse remove(@RequestBody WaterFeeStatisticsDetailsSelectListReq req) {
        return canalHeadManagementStationDetailsService.remove(req);
    }

}

