package com.cj.project.modular.FiducialEnterA.controller;


import cn.hutool.core.util.ObjectUtil;
import com.cj.common.annotation.CommonLog;
import com.cj.common.pojo.CommonResult;
import com.cj.project.modular.FiducialEnterA.param.ConfigProjectPointAddParam;
import com.cj.project.modular.FiducialEnterA.service.FiducialEnterAService;
import com.fhs.common.utils.StringUtil;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Api(tags = "集成考证控制器")
@ApiSupport(author = "", order = 1)
@RestController
@Validated
public class FiducialEnterAController {

    @Resource
    private FiducialEnterAService fiducialEnterAService;

    @ApiOperationSupport(order = 2)
    @ApiOperation("集成考证")
    @CommonLog("集成考证")
    @GetMapping("/FiducialEnterA/enter")
    public CommonResult<List<Map<String, Object>>> EnterPoints(String projectCode, String instrument, String isCover) {
        if(StringUtil.isEmpty(isCover))
            isCover = "0";
        List<Map<String, Object>> result = fiducialEnterAService.EnterPointFiducial(projectCode, instrument, isCover);
        return CommonResult.data(result);
    }
}
