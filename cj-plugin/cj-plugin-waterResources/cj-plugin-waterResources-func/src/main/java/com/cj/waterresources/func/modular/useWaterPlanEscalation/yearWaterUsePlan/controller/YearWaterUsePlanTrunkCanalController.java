package com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.controller;

import com.cj.common.annotation.CommonLog;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.bean.req.TrunkCanalSelectListReq;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.entity.YearWaterUsePlanTrunkCanal;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.service.YearWaterUsePlanTrunkCanalService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 干渠年用水计划(YearWaterUsePlanTrunkCanal)表控制层
 *
 * @author makejava
 * @since 2023-12-01 18:26:47
 */
@Api(tags = "年用水计划模块-干渠")
@ApiSupport(author = "LEO-LUOXU", order = 1)
@Validated
@RestController
@RequestMapping("yearWaterUsePlanTrunkCanal")
public class YearWaterUsePlanTrunkCanalController {

    @Autowired
    private YearWaterUsePlanTrunkCanalService yearWaterUsePlanTrunkCanalService;

    @ApiOperationSupport(order = 1)
    @ApiOperation("年用水计划模块-干渠新增")
    @CommonLog(value = "年用水计划模块-干渠新增")
    @PostMapping("/add")
    public RestResponse add(@RequestBody YearWaterUsePlanTrunkCanal yearWaterUsePlanTrunkCanal) {
        return yearWaterUsePlanTrunkCanalService.addTrunkCanal(yearWaterUsePlanTrunkCanal);
    }
    @ApiOperationSupport(order = 2)
    @ApiOperation("年用水计划模块-干渠修改")
    @CommonLog(value = "年用水计划模块-干渠修改")
    @PostMapping("/update")
    public RestResponse update(@RequestBody YearWaterUsePlanTrunkCanal yearWaterUsePlanTrunkCanal) {
        return yearWaterUsePlanTrunkCanalService.updateTrunkCanal(yearWaterUsePlanTrunkCanal);
    }


    @ApiOperationSupport(order = 3)
    @ApiOperation("年用水计划模块-干渠查询列表")
    @CommonLog(value = "年用水计划模块-干渠查询列表")
    @PostMapping("/select")
    public RestResponse<List<YearWaterUsePlanTrunkCanal>> select(@RequestBody TrunkCanalSelectListReq req) {
        return yearWaterUsePlanTrunkCanalService.selectList(req);
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation("年用水计划模块-干渠删除")
    @CommonLog(value = "年用水计划模块-干渠删除")
    @GetMapping("delete")
    public RestResponse delete(@RequestParam("id") String id) {
        boolean b = yearWaterUsePlanTrunkCanalService.removeById(id);
        if(b){
            return RestResponse.ok("删除成功");
        }else {
            return RestResponse.no("删除失败");
        }
    }
}

