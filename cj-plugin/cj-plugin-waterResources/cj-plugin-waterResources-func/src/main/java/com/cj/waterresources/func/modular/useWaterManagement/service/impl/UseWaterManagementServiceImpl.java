package com.cj.waterresources.func.modular.useWaterManagement.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.waterresources.func.modular.useWaterManagement.mapper.UseWaterManagementMapper;
import com.cj.waterresources.func.modular.useWaterManagement.entity.UseWaterManagement;
import com.cj.waterresources.func.modular.useWaterManagement.service.UseWaterManagementService;
import org.springframework.stereotype.Service;

/**
 * 用水单位管理(UseWaterManagement)表服务实现类
 *
 * @author makejava
 * @since 2023-11-28 17:14:42
 */
@Service("useWaterManagementService")
public class UseWaterManagementServiceImpl extends ServiceImpl<UseWaterManagementMapper, UseWaterManagement> implements UseWaterManagementService {

}

