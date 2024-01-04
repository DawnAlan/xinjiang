package com.cj.dataSynchronization.func.modular.lzz.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.cj.dataSynchronization.func.modular.lzz.bean.UserIdParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
@DS("multi-datasource2")
public interface PubUserMapper {

    @Select("select count(*) from PUBUSER.WDS_HYDRO_ELEMENTS where DTYPE = 5")
    String selectListTest();

    @Select("SELECT id from PUBUSER.WDS_HYDRO_ELEMENTS where DTYPE = 5 and name = '萨尔达万雨量'")
    String selectRainfallStation();

    @Select("select ID as id,NAME as name,PID as pId from PUBUSER.WDS_HYDRO_MEASANALOG where PID = 92102 and name like CONCAT('%',#{name},'%')")
    List<UserIdParam> selectPidList(@Param("name") String name);

    @Select("select ID as id,NAME as name,PID as pId from PUBUSER.WDS_HYDRO_MEASANALOG where PID = 92102")
    List<UserIdParam> selectPidList();

    @Select("select ID as id,NAME as name,PID as pId from PUBUSER.WDS_HYDRO_ELEMENTS where (PID = #{pId} and NAME like '%水位') OR (PID = #{pId} and NAME like '%流量%') OR (PID = #{pId} and NAME like '%温度%')")
    List<UserIdParam> selectGaugingStationIdList(@Param("pId") String pId);

    @Select("select ID as id,NAME as name,PID as pId from PUBUSER.WDS_HYDRO_ELEMENTS where  (PID = #{pId} and NAME like '%雨量%') OR (PID = #{pId} and NAME like '%温度%')")
    List<UserIdParam> selectRainfallStationIdList(@Param("pId") String pId);

    @Select("select ID as id,NAME as name,PID as pId from PUBUSER.WDS_HYDRO_ELEMENTS where (PID = #{pId} and NAME like '%水位') OR (PID = #{pId} and NAME like '%温度%')")
    List<UserIdParam> selectReservoirLevelIdList(@Param("pId") String pId);

}
