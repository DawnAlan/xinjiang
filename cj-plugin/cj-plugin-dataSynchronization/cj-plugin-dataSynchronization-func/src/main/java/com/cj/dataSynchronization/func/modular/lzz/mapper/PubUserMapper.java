package com.cj.dataSynchronization.func.modular.lzz.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
@DS("multi-datasource2")
public interface PubUserMapper {

    @Select("select count(*) from PUBUSER.WDS_HYDRO_ELEMENTS where DTYPE = 5")
    String selectListTest();

    @Select("SELECT id from PUBUSER.WDS_HYDRO_ELEMENTS where DTYPE = 5 and name = '萨尔达万雨量'")
    String selectRainfallStation();
}
