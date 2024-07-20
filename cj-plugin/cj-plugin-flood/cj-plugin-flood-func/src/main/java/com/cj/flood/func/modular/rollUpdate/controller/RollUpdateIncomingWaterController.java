package com.cj.flood.func.modular.rollUpdate.controller;

import com.cj.flood.func.modular.rollUpdate.service.RollUpdateIncomingWaterService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;


/**
 * 滚动更新来水预报模型结果表(RollUpdateIncomingWater)表控制层
 *
 * @author makejava
 * @since 2024-07-19 14:59:57
 */
@RestController
@RequestMapping("rollUpdateIncomingWater")
public class RollUpdateIncomingWaterController {
    /**
     * 服务对象
     */
    @Resource
    private RollUpdateIncomingWaterService rollUpdateIncomingWaterService;

}

