package com.cj.project.modular.pointstat.controller;


import com.cj.common.annotation.CommonLog;
import com.cj.common.pojo.CommonResult;
import com.cj.project.modular.pointstat.result.PointStatResult;
import com.cj.project.modular.pointstat.service.PointStatService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 测点统计控制器
 *
 * @author Lb
 * @date  2023/09/20 19:28
 */
@Api(tags = "测点统计控制器")
@ApiSupport(author = "lb", order = 1)
@RestController
@Validated
public class PointStatController {

    @Resource
    private PointStatService pointStatService;

    /**
     * 测点统计_通过仪器类型
     *
     * @author Lb
     * @date 2023/09/20 19:28
     */
    @ApiOperationSupport(order = 1)
    @ApiOperation("测点统计_通过仪器类型")
    @CommonLog("测点统计_通过仪器类型")
    @GetMapping("/project/pointstat/statbyfiducial")
    public CommonResult<PointStatResult> getList(String projectCode, String instrumentStr) {
        return CommonResult.data(pointStatService.GetInstrumentStat(projectCode, instrumentStr));
    }
}
