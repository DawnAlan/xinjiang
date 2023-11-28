package com.cj.dataSynchronization.func.modular.lzz.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.cj.dataSynchronization.func.modular.lzz.bean.ParamDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
@DS("multi-datasource1")
public interface LzzTestMapper {

    @Select("select count(*) from wds.DAYDB where SENID = '3110100184' AND V>0")
    String selectListTest();

    @Select("select TOP 1 time,v from wds.DAYDB where SENID = #{senId} order by time desc")
    ParamDto selectInfo(@Param("senId") String senId);
}
