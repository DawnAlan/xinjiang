package com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.bean.res.SelectInfoByIrrigationNameListRes;
import com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.entity.IrrigatedPlatformDataInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 灌区平台时刻信息表(IrrigatedPlatformDataInfo)表数据库访问层
 *
 * @author makejava
 * @since 2023-12-06 19:26:06
 */
public interface IrrigatedPlatformDataInfoMapper extends BaseMapper<IrrigatedPlatformDataInfo> {

    @Select("select SQ_MONITOR_FLOW,MONITOR_NAME FROM IRRIGATED_PLATFORM_DATA_INFO WHERE MONITOR_NAME = #{name} order by MONITOR_TIME desc limit 1")
    SelectInfoByIrrigationNameListRes selectInfoByIrrigationNameList(@Param("name") String name);

    @Select("SELECT * FROM IRRIGATED_PLATFORM_DATA_INFO WHERE MONITOR_ID = #{id} AND TO_CHAR(MONITOR_TIME,'YYYY-MM-DD') BETWEEN #{startTime} AND #{endTime}")
    List<IrrigatedPlatformDataInfo> selectInfoByCondition1(@Param("id") String id, @Param("startTime") String startTime,@Param("endTime") String endTime);

    @Select("SELECT * FROM IRRIGATED_PLATFORM_DATA_INFO WHERE MONITOR_ID = #{id} AND TO_CHAR(MONITOR_TIME,'YYYY-MM-DD') = #{time}")
    List<IrrigatedPlatformDataInfo> selectInfoByCondition2(@Param("id") String id, @Param("time")String time);

    @Select("SELECT * FROM IRRIGATED_PLATFORM_DATA_INFO WHERE MONITOR_ID = #{id}")
    List<IrrigatedPlatformDataInfo> selectInfoByCondition3(@Param("id") String id);

    @Select("SELECT * FROM IRRIGATED_PLATFORM_DATA_INFO WHERE MONITOR_NAME = #{name} AND TO_CHAR(MONITOR_TIME,'YYYY-MM-DD') = #{time} order by MONITOR_TIME DESC limit 1")
    IrrigatedPlatformDataInfo selectOneByCondition(@Param("name") String name, @Param("time")String time);
}

