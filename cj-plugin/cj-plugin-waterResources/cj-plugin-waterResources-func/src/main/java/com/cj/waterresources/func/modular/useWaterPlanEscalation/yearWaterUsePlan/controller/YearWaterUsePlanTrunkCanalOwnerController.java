package com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.controller;


import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.bean.req.TrunkCanalSelectListReq;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.entity.YearWaterUsePlanTrunkCanal;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.entity.YearWaterUsePlanTrunkCanalOwner;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.service.YearWaterUsePlanTrunkCanalOwnerService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

/**
 * (YearWaterUsePlanTrunkCanalOwner)表控制层
 *
 * @author makejava
 * @since 2024-03-22 19:40:52
 */
@Api(tags = "年用水计划模块-干渠(供水科)")
@ApiSupport(author = "LEO-LUOXU", order = 1)
@Validated
@RestController
@RequestMapping("yearWaterUsePlanTrunkCanalOwner")
public class YearWaterUsePlanTrunkCanalOwnerController {
    /**
     * 服务对象
     */
    @Resource
    private YearWaterUsePlanTrunkCanalOwnerService yearWaterUsePlanTrunkCanalOwnerService;


    @ApiOperationSupport(order = 1)
    @ApiOperation("查询列表")
    @PostMapping("/select")
    public RestResponse<List<YearWaterUsePlanTrunkCanalOwner>> select(@RequestBody TrunkCanalSelectListReq req) {
        return yearWaterUsePlanTrunkCanalOwnerService.selectList(req);
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("新增")
    @PostMapping("/add")
    public RestResponse add(@RequestBody YearWaterUsePlanTrunkCanalOwner yearWaterUsePlanTrunkCanalOwner) {
        return yearWaterUsePlanTrunkCanalOwnerService.addTrunkCanal(yearWaterUsePlanTrunkCanalOwner);
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation("删除")
    @GetMapping("delete")
    public RestResponse delete(@RequestParam("id") String id) {
        boolean b = yearWaterUsePlanTrunkCanalOwnerService.removeById(id);
        if(b){
            return RestResponse.ok("删除成功");
        }else {
            return RestResponse.no("删除失败");
        }
    }
}

