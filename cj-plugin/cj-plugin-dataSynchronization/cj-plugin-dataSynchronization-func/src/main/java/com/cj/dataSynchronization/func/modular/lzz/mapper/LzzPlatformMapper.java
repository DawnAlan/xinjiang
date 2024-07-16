package com.cj.dataSynchronization.func.modular.lzz.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.cj.common.model.RestResponse;
import com.cj.dataSynchronization.func.modular.lzz.bean.ParamDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
@DS("multi-datasource1")
public interface LzzPlatformMapper {

  /*  @Select("select time,v,SENID from wds.HOURDB where SENID = #{senId} and CONVERT(nvarchar(13),[TIME],121) = #{time}")
    ParamDto selectInfoByTime(@Param("senId") String senId, @Param("time") String time);

    @Select("select time,v,SENID from wds.HOURDB where SENID = #{senId} and CONVERT(nvarchar(21),[TIME],121) BETWEEN #{startTime} AND #{endTime}")
    List<ParamDto> selectInfoBetweenTime(@Param("senId") String senId, @Param("startTime") String startTime, @Param("endTime") String endTime);*/

    @Select("select time,FACTV as v,SENID from wds.RTSQ where SENID = #{senId} and CONVERT(nvarchar(13),[TIME],121) = #{time}")
    ParamDto selectInfoByTime(@Param("senId") String senId, @Param("time") String time);

    @Select("select time,FACTV as v,SENID from wds.RTSQ where SENID = #{senId} and CONVERT(nvarchar(21),[TIME],121) BETWEEN #{startTime} AND #{endTime} order by time asc")
    List<ParamDto> selectInfoBetweenTime(@Param("senId") String senId, @Param("startTime") String startTime, @Param("endTime") String endTime);

    @Select("select time,FACTV as v,SENID from wds.RTSQ where SENID = #{senId} and CONVERT(nvarchar(21),[TIME],121) BETWEEN #{startTime} AND #{endTime} order by time desc")
    List<ParamDto> selectInfoBetweenTimeForKq(@Param("senId") String senId, @Param("startTime") String startTime, @Param("endTime") String endTime);

    @Select("select time,FACTV/3600 as v,SENID from wds.RTSQ where SENID = #{senId} and CONVERT(nvarchar(13),[TIME],121) = #{time}")
    ParamDto selectLzzInfoByTime(@Param("senId") String senId, @Param("time") String time);

    @Select("select time,FACTV/3600 as v,SENID from wds.RTSQ where SENID = #{senId} and CONVERT(nvarchar(21),[TIME],121) BETWEEN #{startTime} AND #{endTime}")
    List<ParamDto> selectLzzInfoBetweenTime(@Param("senId") String senId, @Param("startTime") String startTime, @Param("endTime") String endTime);
}
