package com.cj.waterresources.func.modular.waterSituationDataMaintenance.controller;

import com.cj.business.log.modular.log.annotation.CommonLog;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.waterSituationDataMaintenance.bean.req.SelectInfoListNewReq;
import com.cj.waterresources.func.modular.waterSituationDataMaintenance.bean.req.SelectInfoListReq;
import com.cj.waterresources.func.modular.waterSituationDataMaintenance.bean.req.UpdateInfoReq;
import com.cj.waterresources.func.modular.waterSituationDataMaintenance.service.WaterSituationService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Api(tags = "水情数据维护模块")
@ApiSupport(author = "LEO-LUOXU", order = 1)
@Validated
@RestController
@RequestMapping("waterSituation")
public class WaterSituationController {

    @Autowired
    private WaterSituationService waterSituationService;

    @ApiOperationSupport(order = 1)
    @ApiOperation("水情数据维护模块查询结构树")
    @CommonLog(value = "水情数据维护模块查询结构树")
    @PostMapping("/selectTree")
    public RestResponse selectTree() {
        return waterSituationService.selectTree();
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("水情数据维护模块查询水情信息")
    @CommonLog(value = "水情数据维护模块查询水情信息")
    @PostMapping("/selectInfoList")
    public RestResponse selectInfoList(@RequestBody SelectInfoListReq req) {
        return waterSituationService.selectInfoList(req);
    }


    @ApiOperationSupport(order = 5)
    @ApiOperation("水情数据维护模块修改水情信息")
    @CommonLog(value = "水情数据维护模块修改水情信息")
    @PostMapping("/update")
    public RestResponse update(@RequestBody UpdateInfoReq req) {
        return waterSituationService.update(req);
    }

    @ApiOperationSupport(order = 6)
    @ApiOperation("水情数据维护模块查询地图上监测点信息")
    @CommonLog(value = "水情数据维护模块查询地图上监测点信息")
    @PostMapping("/selectInfoAllList")
    public RestResponse selectInfoAllList(@RequestBody SelectInfoListReq req) {
        return waterSituationService.selectInfoListAll(req);
    }

    @ApiOperationSupport(order = 7)
    @ApiOperation("水情数据维护模块查询地图上监测点信息(new)")
    @CommonLog(value = "水情数据维护模块查询地图上监测点信息")
    @PostMapping("/selectInfoListAllNew")
    public RestResponse selectInfoListAllNew(@RequestBody SelectInfoListNewReq req) {
        return waterSituationService.selectInfoListAllNew(req);
    }
    @ApiOperationSupport(order = 6)
    @ApiOperation("水情数据维护模块查询水资源首页今日水情")
    @CommonLog(value = "水情数据维护模块查询水资源首页今日水情")
    @GetMapping("/selectTodayWaterSituation")
    public RestResponse selectTodayWaterSituation(@RequestParam("date") String date) {
        return waterSituationService.selectTodayWaterSituation(date);
    }
}
