package com.cj.waterresources.func.modular.waterPrice.industrialWaterFee.controller;

import com.cj.waterresources.func.modular.waterPrice.industrialWaterFee.service.WaterManagementUrbanIndustryService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 税费管理-城市工业(WaterManagementUrbanIndustry)表控制层
 *
 * @author makejava
 * @since 2024-04-01 10:52:02
 */
@RestController
@RequestMapping("waterManagementUrbanIndustry")
public class WaterManagementUrbanIndustryController{
    /**
     * 服务对象
     */
    @Resource
    private WaterManagementUrbanIndustryService waterManagementUrbanIndustryService;


}

