package com.cj.flood.func.modular.prediction.controller;
import com.cj.common.model.RestResponse;
import com.cj.flood.func.modular.prediction.entity.ModelParameters;
import com.cj.flood.func.modular.prediction.service.ModelParametersService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


/**
 * 陕北模型参数(ModelParameters)表控制层
 *
 * @author makejava
 * @since 2024-03-13 12:27:09
 */
@RestController
@RequestMapping("modelParameters")
public class ModelParametersController{

    @Autowired
    private ModelParametersService modelParametersService;

}

