package com.cj.flood.func.modular.prediction.controller;

import com.cj.flood.func.modular.prediction.service.ModelParametersDetailService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * (ModelParametersDetail)表控制层
 *
 * @author makejava
 * @since 2024-04-19 14:26:50
 */
@RestController
@RequestMapping("modelParametersDetail")
public class ModelParametersDetailController {
    /**
     * 服务对象
     */
    @Resource
    private ModelParametersDetailService modelParametersDetailService;

}

