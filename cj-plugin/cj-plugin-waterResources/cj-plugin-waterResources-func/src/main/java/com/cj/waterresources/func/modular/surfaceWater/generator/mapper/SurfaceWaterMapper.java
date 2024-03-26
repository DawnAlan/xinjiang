package com.cj.waterresources.func.modular.surfaceWater.generator.mapper;

import com.cj.waterresources.func.modular.surfaceWater.generator.domain.SurfaceWater;
import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;
import java.util.Map;

/**
* @author Administrator
* @description 针对表【surface_water(地表水情数据)】的数据库操作Mapper
* @createDate 2023-12-25 10:17:44
* @Entity org.jeecg.modules.surfaceWater.generator.domain.SurfaceWater
*/
@Mapper
public interface SurfaceWaterMapper extends BaseMapper<SurfaceWater> {


    List<Map<String, Object>> annualList(List<Integer> yearlist);
}




