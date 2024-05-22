package com.cj.waterresources.func.modular.waterStorageScheduling.waterStorageSchedulingLzz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cj.waterresources.func.modular.waterStorageScheduling.waterStorageSchedulingLzz.bean.res.RealFlowRes;
import com.cj.waterresources.func.modular.waterStorageScheduling.waterStorageSchedulingLzz.entity.WaterStorageSchedulingLzz;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 楼庄子水库蓄水调度计划表(WaterStorageSchedulingLzz)表数据库访问层
 *
 * @author makejava
 * @since 2023-12-12 10:20:21
 */
public interface WaterStorageSchedulingLzzMapper extends BaseMapper<WaterStorageSchedulingLzz> {

    List<RealFlowRes> selectRealFlowList(@Param("startTime") String startTime, @Param("endTime") String endTime);
}

