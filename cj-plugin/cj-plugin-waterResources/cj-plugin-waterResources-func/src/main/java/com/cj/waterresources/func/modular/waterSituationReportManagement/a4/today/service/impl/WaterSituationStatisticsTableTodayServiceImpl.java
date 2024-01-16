package com.cj.waterresources.func.modular.waterSituationReportManagement.a4.today.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.common.model.RestResponse;
import com.cj.common.util.UUIDUtils;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a4.today.mapper.WaterSituationStatisticsTableTodayMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a4.today.entity.WaterSituationStatisticsTableToday;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a4.today.service.WaterSituationStatisticsTableTodayService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 今日水情日报表(WaterSituationStatisticsTableToday)表服务实现类
 *
 * @author makejava
 * @since 2023-12-23 19:11:07
 */
@Service("waterSituationStatisticsTableTodayService")
public class WaterSituationStatisticsTableTodayServiceImpl extends ServiceImpl<WaterSituationStatisticsTableTodayMapper, WaterSituationStatisticsTableToday> implements WaterSituationStatisticsTableTodayService {

    @Override
    public RestResponse add(WaterSituationStatisticsTableToday waterSituationStatisticsTableToday) {
        waterSituationStatisticsTableToday.setId(UUIDUtils.getUUID());
        boolean save = this.save(waterSituationStatisticsTableToday);
        if(save){
            return RestResponse.ok();
        }else {
            return RestResponse.no("fail");
        }
    }

    @Override
    public RestResponse delete(String id) {
        boolean b = this.removeById(id);
        if(b){
            return RestResponse.ok();
        }else {
            return RestResponse.no("fail");
        }
    }

    @Override
    public RestResponse update(WaterSituationStatisticsTableToday waterSituationStatisticsTableToday) {
        boolean b = this.updateById(waterSituationStatisticsTableToday);
        if(b){
            return RestResponse.ok();
        }else {
            return RestResponse.no("fail");
        }
    }

    @Override
    public RestResponse<List<WaterSituationStatisticsTableToday>> select(String date) {
        List<WaterSituationStatisticsTableToday> waterSituationStatisticsTableTodays = this.baseMapper.selectList(date);
        if(null != waterSituationStatisticsTableTodays && waterSituationStatisticsTableTodays.size()>0){
            return RestResponse.ok(waterSituationStatisticsTableTodays);
        }else {
            return RestResponse.no("fail");
        }
    }
}

