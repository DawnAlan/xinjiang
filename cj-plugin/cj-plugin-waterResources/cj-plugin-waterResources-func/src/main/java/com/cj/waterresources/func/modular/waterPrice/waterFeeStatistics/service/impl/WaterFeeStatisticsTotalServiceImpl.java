package com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.common.model.RestResponse;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.bean.req.WaterFeeStatisticsDetailsSelectListReq;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.entity.WaterFeeStatisticsDetails;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.mapper.WaterFeeStatisticsTotalMapper;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.entity.WaterFeeStatisticsTotal;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.service.WaterFeeStatisticsTotalService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 水费统计总计(WaterFeeStatisticsTotal)表服务实现类
 *
 * @author makejava
 * @since 2023-11-29 17:16:56
 */
@Service("waterFeeStatisticsTotalService")
public class WaterFeeStatisticsTotalServiceImpl extends ServiceImpl<WaterFeeStatisticsTotalMapper, WaterFeeStatisticsTotal> implements WaterFeeStatisticsTotalService {

    @Override
    public RestResponse<List<WaterFeeStatisticsTotal>> selectInfoList(WaterFeeStatisticsDetailsSelectListReq req) {
        try {
            List<WaterFeeStatisticsTotal> list = this.lambdaQuery().eq(WaterFeeStatisticsTotal::getStation, req.getStation()).
                    eq(WaterFeeStatisticsTotal::getYear, req.getYear()).
                    eq(WaterFeeStatisticsTotal::getMonth, req.getMonth()).
                    eq(WaterFeeStatisticsTotal::getTenDays, req.getTenDays()).list();
            if(null != list && list.size()>0){
                return RestResponse.ok(list);
            }else {
                return RestResponse.no("暂无数据");
            }
        }catch (Exception e) {
            e.printStackTrace();
            return RestResponse.no("查询失败");
        }
    }
}

