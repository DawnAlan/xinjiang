package com.cj.dataSynchronization.func.modular.lzz.controller;

import com.cj.dataSynchronization.func.modular.lzz.service.PubUserService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "水情专业模型表")
@ApiSupport(author = "LEO-LUOXU", order = 1)
@RestController
@Validated
@RequestMapping("pubUser")
public class PubUserController {

    @Autowired
    private PubUserService pubUserService;

    @ApiOperationSupport(order = 1)
    @ApiOperation("测试")
    @GetMapping("/selectListTest")
    public String selectListTest() {
        return pubUserService.selectListTest();
    }
}
