package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.zcc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.common.model.RestResponse;
import com.cj.common.util.UUIDUtils;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.tth.entity.DayWaterSituationStatisticsTableTth;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.zcc.mapper.DayWaterSituationStatisticsTableZccMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.zcc.entity.DayWaterSituationStatisticsTableZcc;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.zcc.service.DayWaterSituationStatisticsTableZccService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 制材厂日水情统计表(DayWaterSituationStatisticsTableZcc)表服务实现类
 *
 * @author makejava
 * @since 2023-12-23 16:01:32
 */
@Service("dayWaterSituationStatisticsTableZccService")
public class DayWaterSituationStatisticsTableZccServiceImpl extends ServiceImpl<DayWaterSituationStatisticsTableZccMapper, DayWaterSituationStatisticsTableZcc> implements DayWaterSituationStatisticsTableZccService {

    @Override
    public RestResponse<List<DayWaterSituationStatisticsTableZcc>> selectList(String date) {
        List<DayWaterSituationStatisticsTableZcc> dayWaterSituationStatisticsTableZccs = this.baseMapper.selectList(date);
        if(null != dayWaterSituationStatisticsTableZccs && dayWaterSituationStatisticsTableZccs.size()>0){
            return RestResponse.ok(dayWaterSituationStatisticsTableZccs);
        }else {
            return RestResponse.no("fail");
        }
    }

    @Override
    public RestResponse add(List<DayWaterSituationStatisticsTableZcc> dayWaterSituationStatisticsTableZccList) {
        dayWaterSituationStatisticsTableZccList.forEach(t->t.setId(UUIDUtils.getUUID()));
        boolean b = this.saveBatch(dayWaterSituationStatisticsTableZccList);
        if (b) {
            return RestResponse.ok();
        }else {
            return RestResponse.no("error");
        }
    }

    @Override
    public RestResponse delete(String ids) {
        List<String> collect = Arrays.stream(ids.split(",")).collect(Collectors.toList());
        boolean remove = this.lambdaUpdate().in(DayWaterSituationStatisticsTableZcc::getId, collect).remove();
        if (remove) {
            return RestResponse.ok();
        }else {
            return RestResponse.no("error");
        }
    }

    @Override
    public RestResponse update(List<DayWaterSituationStatisticsTableZcc> dayWaterSituationStatisticsTableZccList) {
        boolean b = this.updateBatchById(dayWaterSituationStatisticsTableZccList);
        if (b) {
            return RestResponse.ok();
        }else {
            return RestResponse.no("error");
        }
    }

}

