package com.cj.middleDatabase.func.modular.a3.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.middleDatabase.func.modular.a3.entity.DayWaterSituationStatisticsTableLzz;
import com.cj.middleDatabase.func.modular.a3.mapper.DayWaterSituationStatisticsTableLzzMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author qianyf
* @description 针对表【DAY_WATER_SITUATION_STATISTICS_TABLE_LZZ(楼庄子水库日水情统计表)】的数据库操作Service实现
* @createDate 2024-03-16 17:33:04
*/
@Service("DayWaterSituationStatisticsTableLzzServiceHomePage")
@RequiredArgsConstructor
public class DayWaterSituationStatisticsTableLzzService extends ServiceImpl<DayWaterSituationStatisticsTableLzzMapper, DayWaterSituationStatisticsTableLzz>
    implements IService<DayWaterSituationStatisticsTableLzz>{
    private final DayWaterSituationStatisticsTableLzzMapper dayWaterSituationStatisticsTableLzzMapper;
}




