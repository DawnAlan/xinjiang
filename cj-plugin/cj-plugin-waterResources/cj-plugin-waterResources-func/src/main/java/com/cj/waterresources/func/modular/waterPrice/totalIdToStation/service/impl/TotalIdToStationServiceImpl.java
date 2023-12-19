package com.cj.waterresources.func.modular.waterPrice.totalIdToStation.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.waterresources.func.modular.waterPrice.totalIdToStation.mapper.TotalIdToStationMapper;
import com.cj.waterresources.func.modular.waterPrice.totalIdToStation.entity.TotalIdToStation;
import com.cj.waterresources.func.modular.waterPrice.totalIdToStation.service.TotalIdToStationService;
import org.springframework.stereotype.Service;

/**
 * 管理站对应的合计表(TotalIdToStation)表服务实现类
 *
 * @author makejava
 * @since 2023-12-08 18:01:11
 */
@Service("totalIdToStationService")
public class TotalIdToStationServiceImpl extends ServiceImpl<TotalIdToStationMapper, TotalIdToStation> implements TotalIdToStationService {

}

