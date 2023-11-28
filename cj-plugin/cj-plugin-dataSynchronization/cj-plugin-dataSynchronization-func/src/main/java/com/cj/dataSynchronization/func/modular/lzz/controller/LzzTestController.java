package com.cj.dataSynchronization.func.modular.lzz.controller;

import com.cj.common.model.RestResponse;
import com.cj.dataSynchronization.func.modular.lzz.service.LzzTestService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api(tags = "平台测试连接性")
@ApiSupport(author = "LEO-LUOXU", order = 1)
@RestController
@Validated
@RequestMapping("lzzTest")
public class LzzTestController {

    @Autowired
    private LzzTestService lzzTestService;

    @ApiOperationSupport(order = 1)
    @ApiOperation("新增")
    @GetMapping("/selectListTest")
    public String selectListTest() {
        return lzzTestService.selectListTest();
    }
}
