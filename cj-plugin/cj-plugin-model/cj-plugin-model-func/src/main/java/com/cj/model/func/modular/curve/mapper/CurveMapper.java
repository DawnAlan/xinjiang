package com.cj.model.func.modular.curve.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cj.model.func.modular.FloodPrevent.entity.CurveParam;
import com.cj.model.func.modular.curve.entity.Curve;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author July Lion
* @description 针对表【CURVE】的数据库操作Mapper
* @createDate 2023-11-10 15:00:19
* @Entity com.cj.flood.func.modular.curve.entity.Curve
*/
public interface CurveMapper extends BaseMapper<Curve> {

    @Select("select CURVE_CODE as id ,WATER_LEVEL as level ,VALUE as value  from CURVE")
    List<CurveParam> selectList();
}




