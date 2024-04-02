package com.cj.waterresources.func.modular.waterResourceAllcation.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import com.cj.waterresources.func.modular.waterResourceAllcation.entity.WaterResourceAllocationControlObject;
import com.cj.waterresources.func.modular.waterResourceAllcation.mapper.WaterResourceAllocationControlObjectMapper;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author qianyf
* @description 针对表【WATER_RESOURCE_ALLOCATION_CONTROL_OBJECT】的数据库操作Service实现
* @createDate 2024-04-01 17:57:58
*/
@Service
@RequiredArgsConstructor
public class WaterResourceAllocationControlObjectService extends ServiceImpl<WaterResourceAllocationControlObjectMapper, WaterResourceAllocationControlObject>
    implements IService<WaterResourceAllocationControlObject>{

}




