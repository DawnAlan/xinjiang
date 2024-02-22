package com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.bean.req.UseWaterTypeStatisticsReq;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.bean.res.UseWaterTypeStatisticsRes;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.entity.WaterFeeStatisticsDetails;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 水费统计详情(WaterFeeStatisticsDetails)表数据库访问层
 *
 * @author makejava
 * @since 2023-11-29 17:15:43
 */
public interface WaterFeeStatisticsDetailsMapper extends BaseMapper<WaterFeeStatisticsDetails> {

    @Select("select * from WATER_FEE_STATISTICS_DETAILS where STATION = #{station} and YEAR = #{year} AND MONTH = #{month} AND TEN_DAYS = #{tenDays} AND STATISTICS_DATE = #{statisticsDate}")
    List<WaterFeeStatisticsDetails> selectListByName(@Param("station") String station, @Param("year") Integer year, @Param("month") Integer month, @Param("tenDays") String tenDays,@Param("statisticsDate") String statisticsDate);

    List<UseWaterTypeStatisticsRes> statistics(@Param("req") UseWaterTypeStatisticsReq req);

}

