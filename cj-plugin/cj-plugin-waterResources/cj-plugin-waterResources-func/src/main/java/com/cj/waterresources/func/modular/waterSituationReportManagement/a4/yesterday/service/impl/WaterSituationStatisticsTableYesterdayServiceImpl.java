package com.cj.waterresources.func.modular.waterSituationReportManagement.a4.yesterday.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.common.model.RestResponse;
import com.cj.common.util.RedisUtil;
import com.cj.common.util.UUIDUtils;
import com.cj.waterresources.func.modular.trendsTable.entity.TrendsTableParam;
import com.cj.waterresources.func.modular.trendsTable.service.TrendsTableParamService;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.lzz.entity.DayWaterSituationStatisticsTableLzz;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.lzz.mapper.DayWaterSituationStatisticsTableLzzMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.syyl.entity.DayWaterSituationStatisticsTableSyyl;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.syyl.mapper.DayWaterSituationStatisticsTableSyylMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.zcc.entity.DayWaterSituationStatisticsTableZcc;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.zcc.mapper.DayWaterSituationStatisticsTableZccMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a4.yesterday.mapper.WaterSituationStatisticsTableYesterdayMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a4.yesterday.entity.WaterSituationStatisticsTableYesterday;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a4.yesterday.service.WaterSituationStatisticsTableYesterdayService;
import org.apache.commons.lang3.StringUtils;
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

    @Autowired
    private DayWaterSituationStatisticsTableLzzMapper dayWaterSituationStatisticsTableLzzMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse add(WaterSituationStatisticsTableYesterday waterSituationStatisticsTableYesterday) {
        String mk = (String) redisUtil.get("trendsTableParam:list");
        if(StringUtils.isEmpty(mk)){
            trendsTableParamService.updateCache();
            mk = (String) redisUtil.get("trendsTableParam:list");
        }
        List<TrendsTableParam> trendsTableParamList = JSONObject.parseArray(mk, TrendsTableParam.class);
        waterSituationStatisticsTableYesterday.setId(UUIDUtils.getUUID());
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.add(calendar.DATE, -1);
        String yesterday= sdf.format(calendar.getTime());
        String today= sdf.format(new Date());
        List<DayWaterSituationStatisticsTableZcc> dayWaterSituationStatisticsTableZccs = dayWaterSituationStatisticsTableZccMapper.selectList(yesterday);
        if(null != dayWaterSituationStatisticsTableZccs && dayWaterSituationStatisticsTableZccs.size()>0){
            List<TrendsTableParam> zccList = trendsTableParamList.stream().filter(t->t.getUseType()==1).
                    filter(t -> t.getUseStation().equals("制材厂")).collect(Collectors.toList());
            TrendsTableParam flowTableId = zccList.stream().filter(t->t.getParamName().equals("日均流量")).collect(Collectors.toList()).get(0);
            TrendsTableParam maxFlowTableId = zccList.stream().filter(t->t.getParamName().equals("最大流量")).collect(Collectors.toList()).get(0);
            TrendsTableParam rainFallTableId = zccList.stream().filter(t->t.getParamName().equals("日降雨量")).collect(Collectors.toList()).get(0);
            TrendsTableParam temperatureTableId = zccList.stream().filter(t->t.getParamName().equals("平均气温")).collect(Collectors.toList()).get(0);
          /*  TrendsTableParam flowTableId = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getUseType,1).
                    eq(TrendsTableParam::getUseStation,"制材厂").eq(TrendsTableParam::getParamName,"日均流量").one();
            TrendsTableParam maxFlowTableId = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getUseType,1).
                    eq(TrendsTableParam::getUseStation,"制材厂").eq(TrendsTableParam::getParamName,"最大流量").one();
            TrendsTableParam rainFallTableId = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getUseType,1).
                    eq(TrendsTableParam::getUseStation,"制材厂").eq(TrendsTableParam::getParamName,"日降雨量").one();
            TrendsTableParam temperatureTableId = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getUseType,1).
                    eq(TrendsTableParam::getUseStation,"制材厂").eq(TrendsTableParam::getParamName,"平均气温").one();*/
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
            List<TrendsTableParam> syylList = trendsTableParamList.stream().filter(t->t.getUseType()==1).
                    filter(t -> t.getUseStation().equals("上游雨量")).collect(Collectors.toList());
            TrendsTableParam bylcTableId = syylList.stream().filter(t->t.getParamName().equals("八一林场")).collect(Collectors.toList()).get(0);
            TrendsTableParam hgTableId = syylList.stream().filter(t->t.getParamName().equals("黑沟")).collect(Collectors.toList()).get(0);
            TrendsTableParam ksgTableId = syylList.stream().filter(t->t.getParamName().equals("喀什沟")).collect(Collectors.toList()).get(0);
            TrendsTableParam tjydTableId = syylList.stream().filter(t->t.getParamName().equals("团结一队")).collect(Collectors.toList()).get(0);
            TrendsTableParam tthjkTableId = syylList.stream().filter(t->t.getParamName().equals("头屯河进库")).collect(Collectors.toList()).get(0);
            TrendsTableParam xqzTableId = syylList.stream().filter(t->t.getParamName().equals("小渠子")).collect(Collectors.toList()).get(0);
            /*TrendsTableParam bylcTableId = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getUseType,1).
                    eq(TrendsTableParam::getUseStation,"上游雨量").eq(TrendsTableParam::getParamName,"八一林场").one();
            TrendsTableParam hgTableId = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getUseType,1).
                    eq(TrendsTableParam::getUseStation,"上游雨量").eq(TrendsTableParam::getParamName,"黑沟").one();
            TrendsTableParam ksgTableId = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getUseType,1).
                    eq(TrendsTableParam::getUseStation,"上游雨量").eq(TrendsTableParam::getParamName,"喀什沟").one();
            TrendsTableParam tjydTableId = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getUseType,1).
                    eq(TrendsTableParam::getUseStation,"上游雨量").eq(TrendsTableParam::getParamName,"团结一队").one();
            TrendsTableParam tthjkTableId = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getUseType,1).
                    eq(TrendsTableParam::getUseStation,"上游雨量").eq(TrendsTableParam::getParamName,"头屯河进库").one();
            TrendsTableParam xqzTableId = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getUseType,1).
                    eq(TrendsTableParam::getUseStation,"上游雨量").eq(TrendsTableParam::getParamName,"小渠子").one();*/
            List<Double> bylcValue = dayWaterSituationStatisticsTableSyyls.stream().filter(t -> t.getTableHeadId().equals(bylcTableId.getId())).map(DayWaterSituationStatisticsTableSyyl::getV).collect(Collectors.toList());
            if(null != bylcValue && bylcValue.size() > 0) {
                waterSituationStatisticsTableYesterday.setYlzBylc(bylcValue.get(0));
            }
            List<Double> hgValue = dayWaterSituationStatisticsTableSyyls.stream().filter(t -> t.getTableHeadId().equals(hgTableId.getId())).map(DayWaterSituationStatisticsTableSyyl::getV).collect(Collectors.toList());
            if(null != hgValue && hgValue.size() > 0) {
                waterSituationStatisticsTableYesterday.setYlzHg(hgValue.get(0));
            }
            List<Double> ksgValue = dayWaterSituationStatisticsTableSyyls.stream().filter(t -> t.getTableHeadId().equals(ksgTableId.getId())).map(DayWaterSituationStatisticsTableSyyl::getV).collect(Collectors.toList());
            if(null != ksgValue && ksgValue.size() > 0) {
                waterSituationStatisticsTableYesterday.setYlzKsg(ksgValue.get(0));
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
        List<DayWaterSituationStatisticsTableLzz> dayWaterSituationStatisticsTableLzzYesterdayList = dayWaterSituationStatisticsTableLzzMapper.selectList(yesterday);
        if(null != dayWaterSituationStatisticsTableLzzYesterdayList && dayWaterSituationStatisticsTableLzzYesterdayList.size()>0){
            List<TrendsTableParam> lzzList = trendsTableParamList.stream().filter(t->t.getUseType()==1).
                    filter(t -> t.getUseStation().equals("楼庄子水库")).collect(Collectors.toList());
            TrendsTableParam lzzSwTableId = lzzList.stream().filter(t -> t.getParamName().equals("水位")).collect(Collectors.toList()).get(0);
            TrendsTableParam lzzKrTableId = lzzList.stream().filter(t -> t.getParamName().equals("库容")).collect(Collectors.toList()).get(0);
            List<DayWaterSituationStatisticsTableLzz> collect = dayWaterSituationStatisticsTableLzzYesterdayList.stream().filter(t -> t.getTime().equals("18:00")).collect(Collectors.toList());
            if(null != collect && collect.size()>0){
                List<Double> lzzSwValue = collect.stream().filter(t -> t.getTableHeadId().equals(lzzSwTableId)).map(DayWaterSituationStatisticsTableLzz::getV).collect(Collectors.toList());
                if(null != lzzSwValue && lzzSwValue.size()>0){
                    waterSituationStatisticsTableYesterday.setLzz20Ksw(lzzSwValue.get(0));
                }
                List<Double> lzzKrValue = collect.stream().filter(t -> t.getTableHeadId().equals(lzzKrTableId)).map(DayWaterSituationStatisticsTableLzz::getV).collect(Collectors.toList());
                if(null != lzzKrValue && lzzKrValue.size()>0){
                    waterSituationStatisticsTableYesterday.setLzz20Kr(lzzKrValue.get(0));
                }
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

