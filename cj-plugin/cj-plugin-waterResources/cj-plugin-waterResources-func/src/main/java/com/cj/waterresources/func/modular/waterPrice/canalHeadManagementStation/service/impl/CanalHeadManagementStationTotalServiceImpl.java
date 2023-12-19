package com.cj.waterresources.func.modular.waterPrice.canalHeadManagementStation.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.waterPrice.canalHeadManagementStation.entity.CanalHeadManagementStationDetails;
import com.cj.waterresources.func.modular.waterPrice.canalHeadManagementStation.mapper.CanalHeadManagementStationTotalMapper;
import com.cj.waterresources.func.modular.waterPrice.canalHeadManagementStation.entity.CanalHeadManagementStationTotal;
import com.cj.waterresources.func.modular.waterPrice.canalHeadManagementStation.service.CanalHeadManagementStationTotalService;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.bean.req.WaterFeeStatisticsDetailsSelectListReq;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 渠首管理站总计表(CanalHeadManagementStationTotal)表服务实现类
 *
 * @author makejava
 * @since 2023-12-15 18:08:09
 */
@Service("canalHeadManagementStationTotalService")
public class CanalHeadManagementStationTotalServiceImpl extends ServiceImpl<CanalHeadManagementStationTotalMapper, CanalHeadManagementStationTotal> implements CanalHeadManagementStationTotalService {

    @Override
    public RestResponse selectList(WaterFeeStatisticsDetailsSelectListReq req) {
        List<CanalHeadManagementStationTotal> list = this.lambdaQuery().eq(CanalHeadManagementStationTotal::getYear, req.getYear()).
                eq(CanalHeadManagementStationTotal::getMonth, req.getMonth()).
                eq(CanalHeadManagementStationTotal::getTenDays, req.getTenDays()).list();
        if(null != list && list.size() > 0){
            return RestResponse.ok(list);
        }else {
            return RestResponse.no("暂无数据");
        }
    }
}

