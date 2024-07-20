package com.cj.flood.func.modular.rollUpdate.controller;



import com.cj.flood.func.modular.rollUpdate.service.RollUpdateFloodControlService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * (RollUpdateFloodControl)表控制层
 *
 * @author makejava
 * @since 2024-07-19 14:59:38
 */
@RestController
@RequestMapping("rollUpdateFloodControl")
public class RollUpdateFloodControlController{
    /**
     * 服务对象
     */
    @Resource
    private RollUpdateFloodControlService rollUpdateFloodControlService;

}

