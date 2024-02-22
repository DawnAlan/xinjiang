package com.cj.waterresources.func.modular.waterPrice.industrialWaterFee.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.waterresources.func.modular.waterPrice.industrialWaterFee.entity.IndustrialWaterFee;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.bean.req.UseWaterTypeStatisticsReq;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.bean.res.UseWaterTypeStatisticsRes;

import java.util.List;

/**
 * 工业水费(IndustrialWaterFee)表服务接口
 *
 * @author makejava
 * @since 2024-01-31 20:11:19
 */
public interface IndustrialWaterFeeService extends IService<IndustrialWaterFee> {

    List<UseWaterTypeStatisticsRes> statistics(UseWaterTypeStatisticsReq req);

}

