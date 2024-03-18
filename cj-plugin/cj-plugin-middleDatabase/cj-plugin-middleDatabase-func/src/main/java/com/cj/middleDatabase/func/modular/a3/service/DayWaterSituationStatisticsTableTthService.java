package com.cj.middleDatabase.func.modular.a3.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.middleDatabase.func.modular.a3.entity.DayWaterSituationStatisticsTableTth;
import com.cj.middleDatabase.func.modular.a3.mapper.DayWaterSituationStatisticsTableTthMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author qianyf
* @description 针对表【DAY_WATER_SITUATION_STATISTICS_TABLE_TTH(头屯河水库日水情统计表)】的数据库操作Service实现
* @createDate 2024-03-16 17:33:04
*/
@Service("DayWaterSituationStatisticsTableTthServiceHomePage")
@RequiredArgsConstructor
public class DayWaterSituationStatisticsTableTthService extends ServiceImpl<DayWaterSituationStatisticsTableTthMapper, DayWaterSituationStatisticsTableTth>
    implements IService<DayWaterSituationStatisticsTableTth>{
    private final DayWaterSituationStatisticsTableTthMapper dayWaterSituationStatisticsTableTthMapper;
}




