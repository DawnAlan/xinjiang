package com.cj.waterresources.func.modular.waterSituationReportManagement.a4.yesterday.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.common.model.RestResponse;
import com.cj.common.util.RedisUtil;
import com.cj.common.util.UUIDUtils;
import com.cj.waterresources.func.modular.trendsTable.entity.TrendsTableParam;
import com.cj.waterresources.func.modular.trendsTable.service.TrendsTableParamService;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.dkl.entity.DayWaterSituationStatisticsTableDkl;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.dkl.mapper.DayWaterSituationStatisticsTableDklMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.hd.entity.DayWaterSituationStatisticsTableHd;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.hd.mapper.DayWaterSituationStatisticsTableHdMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.hx.entity.DayWaterSituationStatisticsTableHx;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.hx.mapper.DayWaterSituationStatisticsTableHxMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.lzz.entity.DayWaterSituationStatisticsTableLzz;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.lzz.mapper.DayWaterSituationStatisticsTableLzzMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.qs.entity.DayWaterSituationStatisticsTableQs;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.qs.mapper.DayWaterSituationStatisticsTableQsMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.syyl.entity.DayWaterSituationStatisticsTableSyyl;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.syyl.mapper.DayWaterSituationStatisticsTableSyylMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.tth.entity.DayWaterSituationStatisticsTableTth;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.tth.mapper.DayWaterSituationStatisticsTableTthMapper;
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
    private DayWaterSituationStatisticsTableTthMapper dayWaterSituationStatisticsTableTthMapper;

    @Autowired
    private DayWaterSituationStatisticsTableQsMapper dayWaterSituationStatisticsTableQsMapper;

    @Autowired
    private DayWaterSituationStatisticsTableDklMapper dayWaterSituationStatisticsTableDklMapper;

    @Autowired
    private DayWaterSituationStatisticsTableHdMapper dayWaterSituationStatisticsTableHdMapper;

    @Autowired
    private DayWaterSituationStatisticsTableHxMapper dayWaterSituationStatisticsTableHxMapper;

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
            TrendsTableParam lzzSwTableId = lzzList.stream().filter(t -> t.getParamName().equals("库水位")).collect(Collectors.toList()).get(0);
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
        List<DayWaterSituationStatisticsTableLzz> dayWaterSituationStatisticsTableLzzTodayList = dayWaterSituationStatisticsTableLzzMapper.selectList(today);
        if(null != dayWaterSituationStatisticsTableLzzTodayList && dayWaterSituationStatisticsTableLzzTodayList.size()>0){
            List<TrendsTableParam> lzzList = trendsTableParamList.stream().filter(t->t.getUseType()==1).
                    filter(t -> t.getUseStation().equals("楼庄子水库")).collect(Collectors.toList());
            TrendsTableParam lzzJkll = lzzList.stream().filter(t -> t.getParamName().equals("进库流量")).collect(Collectors.toList()).get(0);
            TrendsTableParam lzzLzzscGd1 = lzzList.stream().filter(t -> t.getParamName().equals("楼庄子水厂管道1")).collect(Collectors.toList()).get(0);
            TrendsTableParam lzzLzzscGd2 = lzzList.stream().filter(t -> t.getParamName().equals("楼庄子水厂管道2")).collect(Collectors.toList()).get(0);
            TrendsTableParam lzzHd = lzzList.stream().filter(t -> t.getParamName().equals("河道")).collect(Collectors.toList()).get(0);
            List<DayWaterSituationStatisticsTableLzz> collect = dayWaterSituationStatisticsTableLzzTodayList.stream().filter(t -> t.getTime().equals("昨日均")).collect(Collectors.toList());
            if(null != collect && collect.size()>0){
                List<Double> lzzJkllValue = collect.stream().filter(t -> t.getTableHeadId().equals(lzzJkll)).map(DayWaterSituationStatisticsTableLzz::getV).collect(Collectors.toList());
                if(null != lzzJkllValue && lzzJkllValue.size()>0){
                    waterSituationStatisticsTableYesterday.setLzzRkrj(lzzJkllValue.get(0));
                }
                List<Double> lzzLzzscGd1Value = collect.stream().filter(t -> t.getTableHeadId().equals(lzzLzzscGd1)).map(DayWaterSituationStatisticsTableLzz::getV).collect(Collectors.toList());
                List<Double> lzzLzzscGd2Value = collect.stream().filter(t -> t.getTableHeadId().equals(lzzLzzscGd2)).map(DayWaterSituationStatisticsTableLzz::getV).collect(Collectors.toList());
                if((null != lzzLzzscGd1Value && lzzLzzscGd1Value.size()>0) || (null != lzzLzzscGd2Value && lzzLzzscGd2Value.size()>0)){
                    waterSituationStatisticsTableYesterday.setLzzCkrjLzzsc((lzzLzzscGd1Value.get(0)==null?0.0:lzzLzzscGd1Value.get(0))+(lzzLzzscGd2Value.get(0)==null?0.0:lzzLzzscGd2Value.get(0)));
                }
                List<Double> lzzHdValue = collect.stream().filter(t -> t.getTableHeadId().equals(lzzHd)).map(DayWaterSituationStatisticsTableLzz::getV).collect(Collectors.toList());
                if(null != lzzHdValue && lzzHdValue.size()>0){
                    waterSituationStatisticsTableYesterday.setLzzCkrjHd(lzzHdValue.get(0));
                }
                waterSituationStatisticsTableYesterday.setLzzCkrjHj((waterSituationStatisticsTableYesterday.getLzzCkrjLzzsc()==null?0.0:waterSituationStatisticsTableYesterday.getLzzCkrjLzzsc())+
                        (waterSituationStatisticsTableYesterday.getLzzCkrjHd()==null?0.0:waterSituationStatisticsTableYesterday.getLzzCkrjHd()));
            }
        }
        List<DayWaterSituationStatisticsTableTth> dayWaterSituationStatisticsTableTthList = dayWaterSituationStatisticsTableTthMapper.selectList(yesterday);
        if(null != dayWaterSituationStatisticsTableTthList && dayWaterSituationStatisticsTableTthList.size()>0){
            List<TrendsTableParam> tthList = trendsTableParamList.stream().filter(t->t.getUseType()==1).
                    filter(t -> t.getUseStation().equals("头屯河水库")).collect(Collectors.toList());
            TrendsTableParam tthSwTableId = tthList.stream().filter(t -> t.getPId().equals("0")).filter(t -> t.getParamName().equals("库水位")).collect(Collectors.toList()).get(0);
            TrendsTableParam tthKrTableId = tthList.stream().filter(t -> t.getPId().equals("0")).filter(t -> t.getParamName().equals("水库库容")).collect(Collectors.toList()).get(0);
            List<DayWaterSituationStatisticsTableTth> data_8 = dayWaterSituationStatisticsTableTthList.stream().filter(t -> t.getTime().equals("8:00")).collect(Collectors.toList());
            List<DayWaterSituationStatisticsTableTth> data_18 = dayWaterSituationStatisticsTableTthList.stream().filter(t -> t.getTime().equals("18:00")).collect(Collectors.toList());
            if(null != data_8 && data_8.size()>0){
                List<Double> data_8_sw = data_8.stream().filter(t -> t.getTableHeadId().equals(tthSwTableId.getId())).map(DayWaterSituationStatisticsTableTth::getV).collect(Collectors.toList());
                if(null != data_8_sw && data_8_sw.size()>0){
                    waterSituationStatisticsTableYesterday.setTth8Ksw(data_8_sw.get(0));
                }
                List<Double> data_8_kr = data_8.stream().filter(t -> t.getTableHeadId().equals(tthKrTableId.getId())).map(DayWaterSituationStatisticsTableTth::getV).collect(Collectors.toList());
                if(null != data_8_kr && data_8_kr.size()>0){
                    waterSituationStatisticsTableYesterday.setTth8Kr(data_8_kr.get(0));
                }
            }
            if(null != data_18 && data_18.size()>0){
                List<Double> data_18_sw = data_18.stream().filter(t -> t.getTableHeadId().equals(tthSwTableId.getId())).map(DayWaterSituationStatisticsTableTth::getV).collect(Collectors.toList());
                if(null != data_18_sw && data_18_sw.size()>0){
                    waterSituationStatisticsTableYesterday.setTth20Ksw(data_18_sw.get(0));
                }
                List<Double> data_18_kr = data_18.stream().filter(t -> t.getTableHeadId().equals(tthKrTableId.getId())).map(DayWaterSituationStatisticsTableTth::getV).collect(Collectors.toList());
                if(null != data_18_kr && data_18_kr.size()>0){
                    waterSituationStatisticsTableYesterday.setTth20Kr(data_18_kr.get(0));
                }
            }

        }
        List<DayWaterSituationStatisticsTableTth> dayWaterSituationStatisticsTableTthTodayList = dayWaterSituationStatisticsTableTthMapper.selectList(today);
        if(null != dayWaterSituationStatisticsTableTthTodayList && dayWaterSituationStatisticsTableTthTodayList.size()>0){
            List<TrendsTableParam> tthList = trendsTableParamList.stream().filter(t->t.getUseType()==1).
                    filter(t -> t.getUseStation().equals("头屯河水库")).collect(Collectors.toList());
            TrendsTableParam tthJkllTableId = tthList.stream().filter(t -> t.getParamName().equals("进库流量")).collect(Collectors.toList()).get(0);
            TrendsTableParam tthLkllTableId = tthList.stream().filter(t -> t.getParamName().equals("龙口流量")).collect(Collectors.toList()).get(0);
            TrendsTableParam tthHdllTableId = tthList.stream().filter(t -> t.getParamName().equals("河道流量")).collect(Collectors.toList()).get(0);
            TrendsTableParam tthBgllTableId = tthList.stream().filter(t -> t.getParamName().equals("八钢流量")).collect(Collectors.toList()).get(0);
            TrendsTableParam tthBgllCountTableId = tthList.stream().filter(t ->t.getPId().equals(tthBgllTableId.getId()) && t.getParamName().equals("合计")).collect(Collectors.toList()).get(0);
            TrendsTableParam tthHygsTableId = tthList.stream().filter(t -> t.getParamName().equals("红岩流量")).collect(Collectors.toList()).get(0);
            List<DayWaterSituationStatisticsTableTth> data_yesterday = dayWaterSituationStatisticsTableTthTodayList.stream().filter(t -> t.getTime().equals("昨日均")).collect(Collectors.toList());
            if(null != data_yesterday && data_yesterday.size()>0){
                List<Double> data_jkll = data_yesterday.stream().filter(t -> t.getTableHeadId().equals(tthJkllTableId.getId())).map(DayWaterSituationStatisticsTableTth::getV).collect(Collectors.toList());
                if(null != data_jkll && data_jkll.size()>0){
                    waterSituationStatisticsTableYesterday.setTthJkrjJk(data_jkll.get(0));
                }
                List<Double> data_lkll = data_yesterday.stream().filter(t -> t.getTableHeadId().equals(tthLkllTableId.getId())).map(DayWaterSituationStatisticsTableTth::getV).collect(Collectors.toList());
                if(null != data_lkll && data_lkll.size()>0){
                    waterSituationStatisticsTableYesterday.setTthJkrjGyys(data_lkll.get(0));
                }
                waterSituationStatisticsTableYesterday.setTthJkrjHj(
                        (waterSituationStatisticsTableYesterday.getTthJkrjGyys()==null?0.0:waterSituationStatisticsTableYesterday.getTthJkrjGyys())+
                                (waterSituationStatisticsTableYesterday.getTthJkrjJk()==null?0.0:waterSituationStatisticsTableYesterday.getTthJkrjJk())
                );
                List<Double> data_hdll = data_yesterday.stream().filter(t -> t.getTableHeadId().equals(tthHdllTableId.getId())).map(DayWaterSituationStatisticsTableTth::getV).collect(Collectors.toList());
                if(null != data_hdll && data_hdll.size()>0){
                    waterSituationStatisticsTableYesterday.setTthCkrjHd(data_hdll.get(0));
                }
                List<Double> data_bgfxll = data_yesterday.stream().filter(t -> t.getTableHeadId().equals(tthBgllCountTableId.getId())).map(DayWaterSituationStatisticsTableTth::getV).collect(Collectors.toList());
                if(null != data_bgfxll && data_bgfxll.size()>0){
                    waterSituationStatisticsTableYesterday.setTthCkrjBg(data_bgfxll.get(0));
                }
                List<Double> data_hygs = data_yesterday.stream().filter(t -> t.getTableHeadId().equals(tthHygsTableId.getId())).map(DayWaterSituationStatisticsTableTth::getV).collect(Collectors.toList());
                if(null != data_hygs && data_hygs.size()>0){
                    waterSituationStatisticsTableYesterday.setTthCkrjHysk(data_hygs.get(0));
                }
                waterSituationStatisticsTableYesterday.setTthCkrjHj(
                        (waterSituationStatisticsTableYesterday.getTthCkrjHd()==null?0.0:waterSituationStatisticsTableYesterday.getTthCkrjHd())+
                        (waterSituationStatisticsTableYesterday.getTthCkrjBg()==null?0.0:waterSituationStatisticsTableYesterday.getTthCkrjBg())+
                        (waterSituationStatisticsTableYesterday.getTthCkrjHysk()==null?0.0:waterSituationStatisticsTableYesterday.getTthCkrjHysk())
                );
            }
        }
        List<DayWaterSituationStatisticsTableQs> dayWaterSituationStatisticsTableQsTodayList = dayWaterSituationStatisticsTableQsMapper.selectList(today);
        List<DayWaterSituationStatisticsTableDkl> dayWaterSituationStatisticsTableDklsTemp = dayWaterSituationStatisticsTableDklMapper.selectList(today);
        List<DayWaterSituationStatisticsTableDkl> dayWaterSituationStatisticsTableDkls = dayWaterSituationStatisticsTableDklsTemp.stream().filter(t->t.getTime().equals("昨日均")).collect(Collectors.toList());
        if(null != dayWaterSituationStatisticsTableQsTodayList && dayWaterSituationStatisticsTableQsTodayList.size()>0){
            List<DayWaterSituationStatisticsTableQs> yesterdayList = dayWaterSituationStatisticsTableQsTodayList.stream().filter(t -> t.getTime().equals("昨日均")).collect(Collectors.toList());
            List<TrendsTableParam> qsList = trendsTableParamList.stream().filter(t->t.getUseType()==1).
                    filter(t -> t.getUseStation().equals("渠首管理站")).collect(Collectors.toList());
            TrendsTableParam dgq = qsList.stream().filter(t -> t.getParamName().equals("东干渠流量")).collect(Collectors.toList()).get(0);
            TrendsTableParam xgq = qsList.stream().filter(t -> t.getParamName().equals("西干渠流量")).collect(Collectors.toList()).get(0);
            TrendsTableParam ld = qsList.stream().filter(t -> t.getParamName().equals("漏斗")).collect(Collectors.toList()).get(0);
            TrendsTableParam by = qsList.stream().filter(t -> t.getParamName().equals("八一")).collect(Collectors.toList()).get(0);
            TrendsTableParam lx = qsList.stream().filter(t -> t.getParamName().equals("立新")).collect(Collectors.toList()).get(0);
            TrendsTableParam lhgcb = qsList.stream().filter(t -> t.getParamName().equals("绿化工程部")).collect(Collectors.toList()).get(0);
            TrendsTableParam lhfwb = qsList.stream().filter(t -> t.getParamName().equals("绿化服务部")).collect(Collectors.toList()).get(0);
            TrendsTableParam yl = qsList.stream().filter(t -> t.getParamName().equals("园林")).collect(Collectors.toList()).get(0);
            TrendsTableParam hd = qsList.stream().filter(t -> t.getParamName().equals("生态")).collect(Collectors.toList()).get(0);
            TrendsTableParam qh = qsList.stream().filter(t -> t.getParamName().equals("全河")).collect(Collectors.toList()).get(0);
            List<TrendsTableParam> dklList = trendsTableParamList.stream().filter(t->t.getUseType()==1).
                    filter(t -> t.getUseStation().equals("对口率")).collect(Collectors.toList());
            TrendsTableParam qsDkl = dklList.stream().filter(t -> t.getParamName().equals("头屯河-渠首")).collect(Collectors.toList()).get(0);
            waterSituationStatisticsTableYesterday.setQsZgqDgq(yesterdayList.stream().filter(t->t.getTableHeadId().equals(dgq.getId()) && t.getV() !=null).map(DayWaterSituationStatisticsTableQs::getV).reduce(Double::sum).orElse(0.00));
            waterSituationStatisticsTableYesterday.setQsZgqXgq(yesterdayList.stream().filter(t->t.getTableHeadId().equals(xgq.getId()) && t.getV() !=null).map(DayWaterSituationStatisticsTableQs::getV).reduce(Double::sum).orElse(0.00));
            waterSituationStatisticsTableYesterday.setQsZgqLd(yesterdayList.stream().filter(t->t.getTableHeadId().equals(ld.getId()) && t.getV() !=null).map(DayWaterSituationStatisticsTableQs::getV).reduce(Double::sum).orElse(0.00));
            waterSituationStatisticsTableYesterday.setQsZgqHj(
                    (waterSituationStatisticsTableYesterday.getQsZgqDgq()==null?0.0:waterSituationStatisticsTableYesterday.getQsZgqDgq())+
                    (waterSituationStatisticsTableYesterday.getQsZgqXgq()==null?0.0:waterSituationStatisticsTableYesterday.getQsZgqXgq())+
                    (waterSituationStatisticsTableYesterday.getQsZgqLd()==null?0.0:waterSituationStatisticsTableYesterday.getQsZgqLd())
            );
            waterSituationStatisticsTableYesterday.setQsDlqNy(
                    yesterdayList.stream().filter(t->t.getTableHeadId().equals(by.getId())).map(DayWaterSituationStatisticsTableQs::getV).reduce(Double::sum).orElse(0.00)+
                    yesterdayList.stream().filter(t->t.getTableHeadId().equals(lx.getId())).map(DayWaterSituationStatisticsTableQs::getV).reduce(Double::sum).orElse(0.00)
            );
            waterSituationStatisticsTableYesterday.setQsDlqLh(
                    yesterdayList.stream().filter(t->t.getTableHeadId().equals(lhgcb.getId())).map(DayWaterSituationStatisticsTableQs::getV).reduce(Double::sum).orElse(0.00)+
                    yesterdayList.stream().filter(t->t.getTableHeadId().equals(lhfwb.getId())).map(DayWaterSituationStatisticsTableQs::getV).reduce(Double::sum).orElse(0.00)+
                    yesterdayList.stream().filter(t->t.getTableHeadId().equals(yl.getId())).map(DayWaterSituationStatisticsTableQs::getV).reduce(Double::sum).orElse(0.00)
            );
            waterSituationStatisticsTableYesterday.setQsDlqHj(
                    (waterSituationStatisticsTableYesterday.getQsDlqNy()==null?0.00:waterSituationStatisticsTableYesterday.getQsDlqNy())+
                    (waterSituationStatisticsTableYesterday.getQsDlqLh()==null?0.00:waterSituationStatisticsTableYesterday.getQsDlqLh())
            );
            waterSituationStatisticsTableYesterday.setQsXhHd(yesterdayList.stream().filter(t->t.getTableHeadId().equals(hd.getId())).map(DayWaterSituationStatisticsTableQs::getV).reduce(Double::sum).orElse(0.00));
            waterSituationStatisticsTableYesterday.setQsQh((yesterdayList.stream().filter(t->t.getTableHeadId().equals(qh.getId())).map(DayWaterSituationStatisticsTableQs::getV).reduce(Double::sum).orElse(0.00)));
            waterSituationStatisticsTableYesterday.setQsDkl(dayWaterSituationStatisticsTableDkls.stream().filter(t->t.getTableHeadId().equals(qsDkl.getId())).map(DayWaterSituationStatisticsTableDkl::getV).reduce(Double::sum).orElse(0.00));
        }
        List<DayWaterSituationStatisticsTableHd> dayWaterSituationStatisticsTableHds = dayWaterSituationStatisticsTableHdMapper.selectList(today);
        if(null != dayWaterSituationStatisticsTableHds && dayWaterSituationStatisticsTableHds.size()>0){
            List<DayWaterSituationStatisticsTableHd> yesterdayList = dayWaterSituationStatisticsTableHds.stream().filter(t -> t.getTime().equals("昨日均")).collect(Collectors.toList());
            List<TrendsTableParam> hdList = trendsTableParamList.stream().filter(t->t.getUseType()==1).
                    filter(t -> t.getUseStation().equals("河东管理站")).collect(Collectors.toList());
            TrendsTableParam hdss = hdList.stream().filter(t -> t.getPId().equals("0")).filter(t -> t.getParamName().equals("合计")).collect(Collectors.toList()).get(0);
            List<TrendsTableParam> dklList = trendsTableParamList.stream().filter(t->t.getUseType()==1).
                    filter(t -> t.getUseStation().equals("对口率")).collect(Collectors.toList());
            TrendsTableParam hdDkl = dklList.stream().filter(t -> t.getParamName().equals("渠首-河东")).collect(Collectors.toList()).get(0);
            waterSituationStatisticsTableYesterday.setHdSs(yesterdayList.stream().filter(t->t.getTableHeadId().equals(hdss.getId())).map(DayWaterSituationStatisticsTableHd::getV).reduce(Double::sum).orElse(0.00));
            waterSituationStatisticsTableYesterday.setHdDkl(dayWaterSituationStatisticsTableDkls.stream().filter(t->t.getTableHeadId().equals(hdDkl.getId())).map(DayWaterSituationStatisticsTableDkl::getV).reduce(Double::sum).orElse(0.00));
        }
        List<DayWaterSituationStatisticsTableHx> dayWaterSituationStatisticsTableHxes = dayWaterSituationStatisticsTableHxMapper.selectList(today);
        if(null != dayWaterSituationStatisticsTableHxes && dayWaterSituationStatisticsTableHxes.size()>0){
            List<DayWaterSituationStatisticsTableHx> yesterdayList = dayWaterSituationStatisticsTableHxes.stream().filter(t -> t.getTime().equals("昨日均")).collect(Collectors.toList());
            List<TrendsTableParam> hxList = trendsTableParamList.stream().filter(t->t.getUseType()==1).
                    filter(t -> t.getUseStation().equals("河西管理站")).collect(Collectors.toList());
            TrendsTableParam hxss = hxList.stream().filter(t -> t.getPId().equals("0")).filter(t -> t.getParamName().equals("合计")).collect(Collectors.toList()).get(0);
            List<TrendsTableParam> dklList = trendsTableParamList.stream().filter(t->t.getUseType()==1).
                    filter(t -> t.getUseStation().equals("对口率")).collect(Collectors.toList());
            TrendsTableParam hxDkl = dklList.stream().filter(t -> t.getParamName().equals("渠首-河西")).collect(Collectors.toList()).get(0);
            waterSituationStatisticsTableYesterday.setHxSs(yesterdayList.stream().filter(t->t.getTableHeadId().equals(hxss.getId())).map(DayWaterSituationStatisticsTableHx::getV).reduce(Double::sum).orElse(0.00));
            waterSituationStatisticsTableYesterday.setHxDkl(dayWaterSituationStatisticsTableDkls.stream().filter(t->t.getTableHeadId().equals(hxDkl.getId())).map(DayWaterSituationStatisticsTableDkl::getV).reduce(Double::sum).orElse(0.00));
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

