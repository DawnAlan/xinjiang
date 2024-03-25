package com.cj.waterresources.func.modular.surfaceWater.generator.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cj.waterresources.func.modular.surfaceWater.generator.domain.SurfaceWaterFlowDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
* @author Administrator
* @description 针对表【surface_water_flow_detail(地表水水情数据子表)】的数据库操作Mapper
* @createDate 2023-12-25 10:17:44
* @Entity org.jeecg.modules.surfaceWater.generator.domain.SurfaceWaterFlowDetail
*/
@Mapper
public interface SurfaceWaterFlowDetailMapper extends BaseMapper<SurfaceWaterFlowDetail> {

    List<SurfaceWaterFlowDetail> QueryTime(@Param("endDate") Date startDate, @Param("endDate") Date endDate, @Param("siteCode") String siteCode);
}




