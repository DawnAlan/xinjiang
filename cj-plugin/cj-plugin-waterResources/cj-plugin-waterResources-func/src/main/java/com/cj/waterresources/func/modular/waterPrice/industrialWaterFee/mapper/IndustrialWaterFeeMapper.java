package com.cj.waterresources.func.modular.waterPrice.industrialWaterFee.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cj.waterresources.func.modular.waterPrice.industrialWaterFee.entity.IndustrialWaterFee;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.bean.req.UseWaterTypeStatisticsReq;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.bean.res.UseWaterTypeStatisticsRes;
import com.cj.waterresources.func.modular.waterSituationDataMaintenance.bean.req.SelectInfoListReq;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 工业水费(IndustrialWaterFee)表数据库访问层
 *
 * @author makejava
 * @since 2024-01-31 20:11:18
 */
public interface IndustrialWaterFeeMapper extends BaseMapper<IndustrialWaterFee> {

    List<UseWaterTypeStatisticsRes> statistics(@Param("req") UseWaterTypeStatisticsReq req);

    List<UseWaterTypeStatisticsRes> selectInfoList(@Param("req") SelectInfoListReq req);

}

