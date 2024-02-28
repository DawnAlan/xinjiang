package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.lzz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.bean.req.A3StatisticsReq;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.bean.res.A3StatisticsRes;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.lzz.entity.DayWaterSituationStatisticsTableLzz;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 楼庄子水库日水情统计表(DayWaterSituationStatisticsTableLzz)表数据库访问层
 *
 * @author makejava
 * @since 2023-12-23 15:59:33
 */
public interface DayWaterSituationStatisticsTableLzzMapper extends BaseMapper<DayWaterSituationStatisticsTableLzz> {

    @Select("select * from DAY_WATER_SITUATION_STATISTICS_TABLE_LZZ WHERE TO_CHAR(RECORD_TIME,'YYYY-MM-DD') = #{date}")
    List<DayWaterSituationStatisticsTableLzz> selectList(@Param("date")String date);

    @Delete("DELETE FROM DAY_WATER_SITUATION_STATISTICS_TABLE_LZZ WHERE TO_CHAR(RECORD_TIME,'YYYY-MM-DD') = #{date}")
    Boolean deleteByTime(@Param("date")String date);

    List<A3StatisticsRes> getStatistics(@Param("req") A3StatisticsReq req);

    @Select("select * from DAY_WATER_SITUATION_STATISTICS_TABLE_LZZ WHERE TO_DAYS( NOW( ) ) - TO_DAYS(\"RECORD_TIME\") = 1 and TIME != '昨日均'")
    List<DayWaterSituationStatisticsTableLzz> selectYesterdayList();

}

