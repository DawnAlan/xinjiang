package com.cj.waterresources.func.modular.waterSituationReportManagement.a4.yesterday.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.common.model.RestResponse;
import com.cj.common.util.UUIDUtils;
import com.cj.waterresources.func.modular.trendsTable.entity.TrendsTableParam;
import com.cj.waterresources.func.modular.trendsTable.service.TrendsTableParamService;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.syyl.entity.DayWaterSituationStatisticsTableSyyl;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.syyl.mapper.DayWaterSituationStatisticsTableSyylMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.zcc.entity.DayWaterSituationStatisticsTableZcc;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.zcc.mapper.DayWaterSituationStatisticsTableZccMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a4.yesterday.mapper.WaterSituationStatisticsTableYesterdayMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a4.yesterday.entity.WaterSituationStatisticsTableYesterday;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a4.yesterday.service.WaterSituationStatisticsTableYesterdayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 昨日水情日报表(WaterSituationStatisticsTableYesterday)表服务实现类
 *
 * @author makejava
 * @since 2023-12-23 19:10:47
 */
@Service("waterSituationStatisticsTableYesterdayService")
public class WaterSituationStatisticsTableYesterdayServiceImpl extends ServiceImpl<WaterSituationStatisticsTableYesterdayMapper, WaterSituationStatisticsTableYesterday> implements WaterSituationStatisticsTableYesterdayService {

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    private TrendsTableParamService trendsTableParamService;

    @Autowired
    private DayWaterSituationStatisticsTableZccMapper dayWaterSituationStatisticsTableZccMapper;

    @Autowired
    private DayWaterSituationStatisticsTableSyylMapper dayWaterSituationStatisticsTableSyylMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse add(WaterSituationStatisticsTableYesterday waterSituationStatisticsTableYesterday) {
        waterSituationStatisticsTableYesterday.setId(UUIDUtils.getUUID());
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.add(calendar.DATE, -1);
        String yesterday= sdf.format(calendar.getTime());
        List<DayWaterSituationStatisticsTableZcc> dayWaterSituationStatisticsTableZccs = dayWaterSituationStatisticsTableZccMapper.selectList(yesterday);
        if(null != dayWaterSituationStatisticsTableZccs && dayWaterSituationStatisticsTableZccs.size()>0){
            TrendsTableParam flowTableId = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getUseType,1).
                    eq(TrendsTableParam::getUseStation,"制材厂").eq(TrendsTableParam::getParamName,"日均流量").one();
            TrendsTableParam maxFlowTableId = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getUseType,1).
                    eq(TrendsTableParam::getUseStation,"制材厂").eq(TrendsTableParam::getParamName,"最大流量").one();
            TrendsTableParam rainFallTableId = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getUseType,1).
                    eq(TrendsTableParam::getUseStation,"制材厂").eq(TrendsTableParam::getParamName,"日降雨量").one();
            TrendsTableParam temperatureTableId = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getUseType,1).
                    eq(TrendsTableParam::getUseStation,"制材厂").eq(TrendsTableParam::getParamName,"平均气温").one();
            List<String> flowValue = dayWaterSituationStatisticsTableZccs.stream().filter(t -> t.getTableHeadId().equals(flowTableId.getId())).map(DayWaterSituationStatisticsTableZcc::getV).collect(Collectors.toList());
            if(null != flowValue && flowValue.size() > 0) {
                waterSituationStatisticsTableYesterday.setZccRjll(flowValue.get(0));
            }
            List<String> maxFlowValue = dayWaterSituationStatisticsTableZccs.stream().filter(t -> t.getTableHeadId().equals(maxFlowTableId.getId())).map(DayWaterSituationStatisticsTableZcc::getV).collect(Collectors.toList());
            if(null != maxFlowValue && maxFlowValue.size() > 0) {
                waterSituationStatisticsTableYesterday.setZccZdll(maxFlowValue.get(0));
            }
            List<String> rainFallValue = dayWaterSituationStatisticsTableZccs.stream().filter(t -> t.getTableHeadId().equals(rainFallTableId.getId())).map(DayWaterSituationStatisticsTableZcc::getV).collect(Collectors.toList());
            if(null != rainFallValue && rainFallValue.size() > 0) {
                waterSituationStatisticsTableYesterday.setZccRjyl(rainFallValue.get(0));
            }
            List<String> temperatureValue = dayWaterSituationStatisticsTableZccs.stream().filter(t -> t.getTableHeadId().equals(temperatureTableId.getId())).map(DayWaterSituationStatisticsTableZcc::getV).collect(Collectors.toList());
            if(null != temperatureValue && temperatureValue.size() > 0) {
                waterSituationStatisticsTableYesterday.setZccRjqw(temperatureValue.get(0));
            }
        }
        List<DayWaterSituationStatisticsTableSyyl> dayWaterSituationStatisticsTableSyyls = dayWaterSituationStatisticsTableSyylMapper.selectList(yesterday);
        if(null != dayWaterSituationStatisticsTableSyyls && dayWaterSituationStatisticsTableSyyls.size()>0){
            TrendsTableParam bylcTableId = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getUseType,1).
                    eq(TrendsTableParam::getUseStation,"上游雨量").eq(TrendsTableParam::getParamName,"八一林场").one();
            TrendsTableParam hgTableId = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getUseType,1).
                    eq(TrendsTableParam::getUseStation,"上游雨量").eq(TrendsTableParam::getParamName,"黑沟").one();
            TrendsTableParam wmgTableId = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getUseType,1).
                    eq(TrendsTableParam::getUseStation,"上游雨量").eq(TrendsTableParam::getParamName,"无名沟").one();
            TrendsTableParam tjydTableId = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getUseType,1).
                    eq(TrendsTableParam::getUseStation,"上游雨量").eq(TrendsTableParam::getParamName,"团结一队").one();
            TrendsTableParam tthjkTableId = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getUseType,1).
                    eq(TrendsTableParam::getUseStation,"上游雨量").eq(TrendsTableParam::getParamName,"头屯河进库").one();
            TrendsTableParam xqzTableId = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getUseType,1).
                    eq(TrendsTableParam::getUseStation,"上游雨量").eq(TrendsTableParam::getParamName,"小渠子").one();
            List<Double> bylcValue = dayWaterSituationStatisticsTableSyyls.stream().filter(t -> t.getTableHeadId().equals(bylcTableId.getId())).map(DayWaterSituationStatisticsTableSyyl::getV).collect(Collectors.toList());
            if(null != bylcValue && bylcValue.size() > 0) {
                waterSituationStatisticsTableYesterday.setYlzBylc(bylcValue.get(0));
            }
            List<Double> hgValue = dayWaterSituationStatisticsTableSyyls.stream().filter(t -> t.getTableHeadId().equals(hgTableId.getId())).map(DayWaterSituationStatisticsTableSyyl::getV).collect(Collectors.toList());
            if(null != hgValue && hgValue.size() > 0) {
                waterSituationStatisticsTableYesterday.setYlzHg(hgValue.get(0));
            }
            List<Double> wmgValue = dayWaterSituationStatisticsTableSyyls.stream().filter(t -> t.getTableHeadId().equals(wmgTableId.getId())).map(DayWaterSituationStatisticsTableSyyl::getV).collect(Collectors.toList());
            if(null != wmgValue && wmgValue.size() > 0) {
                waterSituationStatisticsTableYesterday.setYlzWmg(wmgValue.get(0));
            }
            List<Double> tjydValue = dayWaterSituationStatisticsTableSyyls.stream().filter(t -> t.getTableHeadId().equals(tjydTableId.getId())).map(DayWaterSituationStatisticsTableSyyl::getV).collect(Collectors.toList());
            if(null != tjydValue && tjydValue.size() > 0) {
                waterSituationStatisticsTableYesterday.setYlzXqz(tjydValue.get(0));
            }
            List<Double> tthjkValue = dayWaterSituationStatisticsTableSyyls.stream().filter(t -> t.getTableHeadId().equals(tthjkTableId.getId())).map(DayWaterSituationStatisticsTableSyyl::getV).collect(Collectors.toList());
            if(null != tthjkValue && tthjkValue.size() > 0) {
                waterSituationStatisticsTableYesterday.setYlzTjd(tthjkValue.get(0));
            }
            List<Double> xqzValue = dayWaterSituationStatisticsTableSyyls.stream().filter(t -> t.getTableHeadId().equals(xqzTableId.getId())).map(DayWaterSituationStatisticsTableSyyl::getV).collect(Collectors.toList());
            if(null != xqzValue && xqzValue.size() > 0) {
                waterSituationStatisticsTableYesterday.setYlzTthjkylz(xqzValue.get(0));
            }
        }
        boolean save = this.save(waterSituationStatisticsTableYesterday);
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
    public RestResponse update(WaterSituationStatisticsTableYesterday waterSituationStatisticsTableYesterday) {
        boolean b = this.updateById(waterSituationStatisticsTableYesterday);
        if(b){
            return RestResponse.ok();
        }else {
            return RestResponse.no("fail");
        }
    }

    @Override
    public RestResponse<List<WaterSituationStatisticsTableYesterday>> select(String date) {
        List<WaterSituationStatisticsTableYesterday> waterSituationStatisticsTableYesterdays = this.baseMapper.selectList(date);
        if(null != waterSituationStatisticsTableYesterdays && waterSituationStatisticsTableYesterdays.size()>0){
            return RestResponse.ok(waterSituationStatisticsTableYesterdays);
        }else {
            return RestResponse.no("fail");
        }
    }
}

