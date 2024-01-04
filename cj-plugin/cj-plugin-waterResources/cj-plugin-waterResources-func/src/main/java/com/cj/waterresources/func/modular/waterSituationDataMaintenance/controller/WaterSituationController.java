package com.cj.waterresources.func.modular.waterSituationDataMaintenance.controller;

import com.cj.common.model.RestResponse;
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
    @ApiOperation("查询结构树")
    @PostMapping("/selectTree")
    public RestResponse selectTree() {
        return waterSituationService.selectTree();
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("查询水情信息")
    @PostMapping("/selectInfoList")
    public RestResponse selectInfoList(@RequestBody SelectInfoListReq req) {
        return waterSituationService.selectInfoList(req);
    }


    @ApiOperationSupport(order = 5)
    @ApiOperation("修改水情信息")
    @PostMapping("/update")
    public RestResponse update(@RequestBody UpdateInfoReq req) {
        return waterSituationService.update(req);
    }
}
