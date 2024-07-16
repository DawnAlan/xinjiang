package com.cj.dataSynchronization.func.modular.lzz.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Date;

@Mapper
@DS("master")
public interface LzzRainFailMapper {

    @Select("select time from tth.LZZ_RAINFALL_STATION where TREE_ID = '9210201100100' order by time desc limit 1")
    Date selectNewTimeForKqRainFail();
}
