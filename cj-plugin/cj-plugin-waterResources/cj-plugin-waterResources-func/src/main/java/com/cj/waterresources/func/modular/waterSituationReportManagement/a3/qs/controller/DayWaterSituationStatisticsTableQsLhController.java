package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.qs.controller;

import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.qs.entity.DayWaterSituationStatisticsTableQs;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.qs.entity.DayWaterSituationStatisticsTableQsLh;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.qs.service.DayWaterSituationStatisticsTableQsLhService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * (DayWaterSituationStatisticsTableQsLh)表控制层
 *
 * @author makejava
 * @since 2024-03-21 10:58:50
 */
@Api(tags = "灯笼渠绿化日水情统计表")
@ApiSupport(author = "LEO-LUOXU", order = 1)
@Validated
@RestController
@RequestMapping("dayWaterSituationStatisticsTableQsLh")
public class DayWaterSituationStatisticsTableQsLhController {
    /**
     * 服务对象
     */
    @Resource
    private DayWaterSituationStatisticsTableQsLhService dayWaterSituationStatisticsTableQsLhService;

    @ApiOperationSupport(order = 1)
    @ApiOperation("新增")
    @PostMapping("/add")
    public RestResponse add(@RequestBody List<DayWaterSituationStatisticsTableQsLh> list) {
        return dayWaterSituationStatisticsTableQsLhService.add(list);
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("查询列表")
    @GetMapping("/selectList")
    public RestResponse selectList(@RequestParam(value = "date")String date) {
        return dayWaterSituationStatisticsTableQsLhService.selectList(date);
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation("删除")
    @GetMapping("/delete")
    public RestResponse delete(@RequestParam(value = "ids") String ids) {
        return dayWaterSituationStatisticsTableQsLhService.delete(ids);
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation("修改")
    @PostMapping("/update")
    public RestResponse update(@RequestBody List<DayWaterSituationStatisticsTableQsLh> list) {
        return dayWaterSituationStatisticsTableQsLhService.update(list);
    }
}

