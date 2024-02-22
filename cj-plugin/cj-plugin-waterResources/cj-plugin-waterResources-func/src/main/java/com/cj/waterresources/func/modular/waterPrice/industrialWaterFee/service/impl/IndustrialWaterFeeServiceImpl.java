package com.cj.waterresources.func.modular.waterPrice.industrialWaterFee.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.waterresources.func.modular.waterPrice.industrialWaterFee.mapper.IndustrialWaterFeeMapper;
import com.cj.waterresources.func.modular.waterPrice.industrialWaterFee.entity.IndustrialWaterFee;
import com.cj.waterresources.func.modular.waterPrice.industrialWaterFee.service.IndustrialWaterFeeService;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.bean.req.UseWaterTypeStatisticsReq;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.bean.res.UseWaterTypeStatisticsRes;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 工业水费(IndustrialWaterFee)表服务实现类
 *
 * @author makejava
 * @since 2024-01-31 20:11:19
 */
@Service("industrialWaterFeeService")
public class IndustrialWaterFeeServiceImpl extends ServiceImpl<IndustrialWaterFeeMapper, IndustrialWaterFee> implements IndustrialWaterFeeService {

    @Override
    public List<UseWaterTypeStatisticsRes> statistics(UseWaterTypeStatisticsReq req) {
        return this.baseMapper.statistics(req);
    }
}

