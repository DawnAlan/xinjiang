package com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.controller;




import com.cj.business.log.modular.log.annotation.CommonLog;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.bean.req.YearCropSelectListReq;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.entity.YearWaterUsePlanCropOwner;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.service.YearWaterUsePlanCropOwnerService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

/**
 * (YearWaterUsePlanCropOwner)表控制层
 *
 * @author makejava
 * @since 2024-03-22 19:35:39
 */
@Api(tags = "年用水计划模块-作物(供水科)")
@ApiSupport(author = "LEO-LUOXU", order = 1)
@Validated
@RestController
@RequestMapping("yearWaterUsePlanCropOwner")
public class YearWaterUsePlanCropOwnerController{
    /**
     * 服务对象
     */
    @Resource
    private YearWaterUsePlanCropOwnerService yearWaterUsePlanCropOwnerService;

    @ApiOperationSupport(order = 1)
    @ApiOperation("年用水计划模块-作物(供水科)查询列表")
    @CommonLog(value = "年用水计划模块-作物(供水科)查询列表")
    @PostMapping("/select")
    public RestResponse<List<YearWaterUsePlanCropOwner>> select(@RequestBody YearCropSelectListReq req) {
        return yearWaterUsePlanCropOwnerService.selectList(req);
    }
}

