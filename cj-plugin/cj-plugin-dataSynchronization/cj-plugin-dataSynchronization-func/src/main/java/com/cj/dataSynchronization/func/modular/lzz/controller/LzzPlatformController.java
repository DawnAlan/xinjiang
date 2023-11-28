package com.cj.dataSynchronization.func.modular.lzz.controller;

import com.cj.common.model.RestResponse;
import com.cj.dataSynchronization.func.modular.lzz.service.LzzPlatformService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "楼庄子平台")
@ApiSupport(author = "LEO-LUOXU", order = 1)
@RestController
@Validated
@RequestMapping("lzzPlatform")
public class LzzPlatformController {

    @Autowired
    private LzzPlatformService lzzPlatformService;

    @ApiOperationSupport(order = 1)
    @ApiOperation("雨量站录入")
    @GetMapping("/add")
    public RestResponse add() {
        return lzzPlatformService.add();
    }
}
