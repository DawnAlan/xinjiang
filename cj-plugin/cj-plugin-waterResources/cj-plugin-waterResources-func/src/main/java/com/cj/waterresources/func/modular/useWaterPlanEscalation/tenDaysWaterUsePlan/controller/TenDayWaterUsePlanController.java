package com.cj.waterresources.func.modular.useWaterPlanEscalation.tenDaysWaterUsePlan.controller;




import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.tenDaysWaterUsePlan.bean.req.TenDayWaterUsePlanSelectReq;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.tenDaysWaterUsePlan.entity.TenDayWaterUsePlan;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.tenDaysWaterUsePlan.service.TenDayWaterUsePlanService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 旬用水计划(TenDayWaterUsePlan)表控制层
 *
 * @author makejava
 * @since 2023-12-01 19:41:07
 */
@Api(tags = "旬用水计划模块")
@ApiSupport(author = "LEO-LUOXU", order = 1)
@Validated
@RestController
@RequestMapping("tenDayWaterUsePlan")
public class TenDayWaterUsePlanController {
    /**
     * 服务对象
     */
    @Autowired
    private TenDayWaterUsePlanService tenDayWaterUsePlanService;


    @ApiOperationSupport(order = 1)
    @ApiOperation("删除")
    @GetMapping("/delete")
    public RestResponse delete(@RequestParam("id") String id) {
        return tenDayWaterUsePlanService.delete(id);
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("新增")
    @PostMapping("/add")
    public RestResponse add(@RequestBody TenDayWaterUsePlan tenDayWaterUsePlan) {
        return tenDayWaterUsePlanService.add(tenDayWaterUsePlan);
    }
    @ApiOperationSupport(order = 3)
    @ApiOperation("修改")
    @PostMapping("/update")
    public RestResponse update(@RequestBody TenDayWaterUsePlan tenDayWaterUsePlan) {
        return tenDayWaterUsePlanService.update(tenDayWaterUsePlan);
    }


    @ApiOperationSupport(order = 4)
    @ApiOperation("查询列表")
    @PostMapping("/select")
    public RestResponse<List<TenDayWaterUsePlan>> select(@RequestBody TenDayWaterUsePlanSelectReq req) {
        return tenDayWaterUsePlanService.selectList(req);
    }
}

