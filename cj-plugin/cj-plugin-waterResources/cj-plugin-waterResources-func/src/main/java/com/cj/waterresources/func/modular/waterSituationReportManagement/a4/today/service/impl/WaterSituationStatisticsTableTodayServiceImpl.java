package com.cj.waterresources.func.modular.waterSituationReportManagement.a4.today.service.impl;

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
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.syyl.mapper.DayWaterSituationStatisticsTableSyylMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.tth.entity.DayWaterSituationStatisticsTableTth;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.tth.mapper.DayWaterSituationStatisticsTableTthMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.zcc.entity.DayWaterSituationStatisticsTableZcc;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.zcc.mapper.DayWaterSituationStatisticsTableZccMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a4.today.mapper.WaterSituationStatisticsTableTodayMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a4.today.entity.WaterSituationStatisticsTableToday;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a4.today.service.WaterSituationStatisticsTableTodayService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 今日水情日报表(WaterSituationStatisticsTableToday)表服务实现类
 *
 * @author makejava
 * @since 2023-12-23 19:11:07
 */
@Service("waterSituationStatisticsTableTodayService")
public class WaterSituationStatisticsTableTodayServiceImpl extends ServiceImpl<WaterSituationStatisticsTableTodayMapper, WaterSituationStatisticsTableToday> implements WaterSituationStatisticsTableTodayService {

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    private TrendsTableParamService trendsTableParamService;

    @Autowired
    private DayWaterSituationStatisticsTableZccMapper dayWaterSituationStatisticsTableZccMapper;

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
    public RestResponse add(WaterSituationStatisticsTableToday waterSituationStatisticsTableToday) {
        String today= sdf.format(new Date());
        waterSituationStatisticsTableToday.setId(UUIDUtils.getUUID());
        String mk = (String) redisUtil.get("trendsTableParam:list");
        if(StringUtils.isEmpty(mk)){
            trendsTableParamService.updateCache();
            mk = (String) redisUtil.get("trendsTableParam:list");
        }
        List<TrendsTableParam> trendsTableParamList = JSONObject.parseArray(mk, TrendsTableParam.class);

        List<DayWaterSituationStatisticsTableZcc> dayWaterSituationStatisticsTableZccList = dayWaterSituationStatisticsTableZccMapper.selectList(today);
        if(null != dayWaterSituationStatisticsTableZccList && dayWaterSituationStatisticsTableZccList.size()>0){
            List<TrendsTableParam> zccTableParam = trendsTableParamList.stream().filter(t -> t.getUseType() == 1 && t.getUseStation().equals("制材厂")).collect(Collectors.toList());
            TrendsTableParam ss = zccTableParam.stream().filter(t -> t.getParamName().equals("水势")).collect(Collectors.toList()).get(0);
            TrendsTableParam ll = zccTableParam.stream().filter(t -> t.getParamName().equals("8:00流量")).collect(Collectors.toList()).get(0);
            waterSituationStatisticsTableToday.setZccSs(dayWaterSituationStatisticsTableZccList.stream().filter(t->t.getTableHeadId().equals(ss.getId())).map(DayWaterSituationStatisticsTableZcc::getV).collect(Collectors.toList()).get(0));
            String s = dayWaterSituationStatisticsTableZccList.stream().filter(t -> t.getTableHeadId().equals(ll.getId())).map(DayWaterSituationStatisticsTableZcc::getV).collect(Collectors.toList()).get(0);
            waterSituationStatisticsTableToday.setZccLl(Double.parseDouble(StringUtils.isNotEmpty(s)?s:"0.00"));
        }
        List<DayWaterSituationStatisticsTableLzz> dayWaterSituationStatisticsTableLzzList = dayWaterSituationStatisticsTableLzzMapper.selectList(today);
        if(null != dayWaterSituationStatisticsTableLzzList && dayWaterSituationStatisticsTableLzzList.size()>0){
            List<DayWaterSituationStatisticsTableLzz> data_8 = dayWaterSituationStatisticsTableLzzList.stream().filter(t -> t.getTime().equals("08:00")).collect(Collectors.toList());
            List<TrendsTableParam> lzzTableParam = trendsTableParamList.stream().filter(t -> t.getUseType() == 1 && t.getUseStation().equals("楼庄子水库")).collect(Collectors.toList());
            TrendsTableParam sw = lzzTableParam.stream().filter(t -> t.getParamName().equals("库水位") && t.getPId().equals("0")).collect(Collectors.toList()).get(0);
            TrendsTableParam kr = lzzTableParam.stream().filter(t -> t.getParamName().equals("库容") && t.getPId().equals("0")).collect(Collectors.toList()).get(0);
            TrendsTableParam jkTemp = lzzTableParam.stream().filter(t -> t.getParamName().equals("进库") && t.getPId().equals("0")).collect(Collectors.toList()).get(0);
            TrendsTableParam rkll = lzzTableParam.stream().filter(t -> t.getPId().equals(jkTemp.getId()) && t.getParamName().equals("进库流量")).collect(Collectors.toList()).get(0);
            TrendsTableParam ckTemp = lzzTableParam.stream().filter(t -> t.getParamName().equals("出库") && t.getPId().equals("0")).collect(Collectors.toList()).get(0);
            TrendsTableParam ckllTemp = lzzTableParam.stream().filter(t -> t.getPId().equals(ckTemp.getId()) && t.getParamName().equals("流量")).collect(Collectors.toList()).get(0);
            TrendsTableParam lzzscgd1 = lzzTableParam.stream().filter(t -> t.getPId().equals(ckllTemp.getId()) && t.getParamName().equals("楼庄子水厂管道1")).collect(Collectors.toList()).get(0);
            TrendsTableParam lzzscgd2 = lzzTableParam.stream().filter(t -> t.getPId().equals(ckllTemp.getId()) && t.getParamName().equals("楼庄子水厂管道2")).collect(Collectors.toList()).get(0);
            TrendsTableParam hd = lzzTableParam.stream().filter(t -> t.getPId().equals(ckllTemp.getId()) && t.getParamName().equals("河道")).collect(Collectors.toList()).get(0);
            TrendsTableParam hj = lzzTableParam.stream().filter(t -> t.getPId().equals(ckllTemp.getId()) && t.getParamName().equals("合计")).collect(Collectors.toList()).get(0);
            waterSituationStatisticsTableToday.setLzzKsw(data_8.stream().filter(t->t.getTableHeadId().equals(sw.getId())).map(DayWaterSituationStatisticsTableLzz::getV).collect(Collectors.toList()).get(0));
            waterSituationStatisticsTableToday.setLzzKr(data_8.stream().filter(t->t.getTableHeadId().equals(kr.getId())).map(DayWaterSituationStatisticsTableLzz::getV).collect(Collectors.toList()).get(0));
            waterSituationStatisticsTableToday.setLzzRkll(data_8.stream().filter(t->t.getTableHeadId().equals(rkll.getId())).map(DayWaterSituationStatisticsTableLzz::getV).collect(Collectors.toList()).get(0));
            waterSituationStatisticsTableToday.setLzzCkllHd(data_8.stream().filter(t->t.getTableHeadId().equals(hd.getId())).map(DayWaterSituationStatisticsTableLzz::getV).collect(Collectors.toList()).get(0));
            waterSituationStatisticsTableToday.setLzzCkllLzzsc(
                    data_8.stream().filter(t->t.getTableHeadId().equals(lzzscgd1.getId()) && t.getV()!=null).map(DayWaterSituationStatisticsTableLzz::getV).reduce(Double::sum).orElse(0.00)+
                    data_8.stream().filter(t->t.getTableHeadId().equals(lzzscgd2.getId()) && t.getV()!=null).map(DayWaterSituationStatisticsTableLzz::getV).reduce(Double::sum).orElse(0.00)
            );
            waterSituationStatisticsTableToday.setLzzCkllHj(data_8.stream().filter(t->t.getTableHeadId().equals(hj.getId())).map(DayWaterSituationStatisticsTableLzz::getV).collect(Collectors.toList()).get(0));
        }
        List<DayWaterSituationStatisticsTableTth> dayWaterSituationStatisticsTableTthList = dayWaterSituationStatisticsTableTthMapper.selectList(today);
        if(null != dayWaterSituationStatisticsTableTthList && dayWaterSituationStatisticsTableTthList.size()>0){
            List<DayWaterSituationStatisticsTableTth> data_8 = dayWaterSituationStatisticsTableTthList.stream().filter(t -> t.getTime().equals("08:00")).collect(Collectors.toList());
            List<TrendsTableParam> tthTableParam = trendsTableParamList.stream().filter(t -> t.getUseType() == 1 && t.getUseStation().equals("头屯河水库")).collect(Collectors.toList());
            TrendsTableParam sw = tthTableParam.stream().filter(t -> t.getPId().equals("0") && t.getParamName().equals("库水位")).collect(Collectors.toList()).get(0);
            TrendsTableParam kr = tthTableParam.stream().filter(t -> t.getPId().equals("0") && t.getParamName().equals("水库库容")).collect(Collectors.toList()).get(0);
            TrendsTableParam jkTemp = tthTableParam.stream().filter(t -> t.getPId().equals("0") && t.getParamName().equals("进库流量")).collect(Collectors.toList()).get(0);
            TrendsTableParam jkllTemp = tthTableParam.stream().filter(t -> t.getPId().equals(jkTemp.getId()) && t.getParamName().equals("流量")).collect(Collectors.toList()).get(0);
            TrendsTableParam jkzd = tthTableParam.stream().filter(t -> t.getPId().equals(jkTemp.getId()) && t.getParamName().equals("进库浊度")).collect(Collectors.toList()).get(0);
            TrendsTableParam jkll = tthTableParam.stream().filter(t -> t.getPId().equals(jkllTemp.getId()) && t.getParamName().equals("进库流量")).collect(Collectors.toList()).get(0);
            TrendsTableParam lkll = tthTableParam.stream().filter(t -> t.getPId().equals(jkllTemp.getId()) && t.getParamName().equals("龙口流量")).collect(Collectors.toList()).get(0);
            TrendsTableParam jkhj = tthTableParam.stream().filter(t -> t.getPId().equals(jkllTemp.getId()) && t.getParamName().equals("合计")).collect(Collectors.toList()).get(0);
            TrendsTableParam ckTemp = tthTableParam.stream().filter(t -> t.getPId().equals("0") && t.getParamName().equals("出库流量")).collect(Collectors.toList()).get(0);
            TrendsTableParam hd = tthTableParam.stream().filter(t -> t.getPId().equals(ckTemp.getId()) && t.getParamName().equals("河道流量")).collect(Collectors.toList()).get(0);
            TrendsTableParam hy = tthTableParam.stream().filter(t -> t.getPId().equals(ckTemp.getId()) && t.getParamName().equals("红岩流量")).collect(Collectors.toList()).get(0);
            TrendsTableParam bgllTemp = tthTableParam.stream().filter(t -> t.getPId().equals(ckTemp.getId()) && t.getParamName().equals("八钢流量")).collect(Collectors.toList()).get(0);
            TrendsTableParam bgCount = tthTableParam.stream().filter(t -> t.getPId().equals(bgllTemp.getId()) && t.getParamName().equals("合计")).collect(Collectors.toList()).get(0);

            TrendsTableParam ckzdTemp = tthTableParam.stream().filter(t -> t.getPId().equals("0") && t.getParamName().equals("出库浊度")).collect(Collectors.toList()).get(0);
            TrendsTableParam ckzd = tthTableParam.stream().filter(t -> t.getPId().equals(ckzdTemp.getId()) && t.getParamName().equals("河道浊度")).collect(Collectors.toList()).get(0);
            TrendsTableParam aqzd = tthTableParam.stream().filter(t -> t.getPId().equals(ckzdTemp.getId()) && t.getParamName().equals("暗渠浊度")).collect(Collectors.toList()).get(0);
            waterSituationStatisticsTableToday.setTthKsw(data_8.stream().filter(t->t.getTableHeadId().equals(sw.getId())).map(DayWaterSituationStatisticsTableTth::getV).collect(Collectors.toList()).get(0));
            waterSituationStatisticsTableToday.setTthKr(data_8.stream().filter(t->t.getTableHeadId().equals(kr.getId())).map(DayWaterSituationStatisticsTableTth::getV).collect(Collectors.toList()).get(0));
            waterSituationStatisticsTableToday.setTthJkRk(data_8.stream().filter(t->t.getTableHeadId().equals(jkll.getId())).map(DayWaterSituationStatisticsTableTth::getV).collect(Collectors.toList()).get(0));
            waterSituationStatisticsTableToday.setTthJkGyy(data_8.stream().filter(t->t.getTableHeadId().equals(lkll.getId())).map(DayWaterSituationStatisticsTableTth::getV).collect(Collectors.toList()).get(0));
            waterSituationStatisticsTableToday.setTthJkHj(data_8.stream().filter(t->t.getTableHeadId().equals(jkhj.getId())).map(DayWaterSituationStatisticsTableTth::getV).collect(Collectors.toList()).get(0));
            waterSituationStatisticsTableToday.setTthCkHd(data_8.stream().filter(t->t.getTableHeadId().equals(hd.getId())).map(DayWaterSituationStatisticsTableTth::getV).collect(Collectors.toList()).get(0));
            waterSituationStatisticsTableToday.setTthCkBg(data_8.stream().filter(t->t.getTableHeadId().equals(bgCount.getId())).map(DayWaterSituationStatisticsTableTth::getV).collect(Collectors.toList()).get(0));
            waterSituationStatisticsTableToday.setTthCkHysk(data_8.stream().filter(t->t.getTableHeadId().equals(hy.getId())).map(DayWaterSituationStatisticsTableTth::getV).collect(Collectors.toList()).get(0));
            waterSituationStatisticsTableToday.setTthCkHj(
                    (waterSituationStatisticsTableToday.getTthCkHd()==null?0.00:waterSituationStatisticsTableToday.getTthCkHd())+
                    (waterSituationStatisticsTableToday.getTthCkBg()==null?0.00:waterSituationStatisticsTableToday.getTthCkBg())+
                    (waterSituationStatisticsTableToday.getTthCkHysk()==null?0.00:waterSituationStatisticsTableToday.getTthCkHysk())
            );
            waterSituationStatisticsTableToday.setTthZdRk(data_8.stream().filter(t->t.getTableHeadId().equals(jkzd.getId()) && t.getV()!=null).map(DayWaterSituationStatisticsTableTth::getV).reduce(Double::sum).orElse(0.00));
            waterSituationStatisticsTableToday.setTthZdAq(data_8.stream().filter(t->t.getTableHeadId().equals(aqzd.getId()) && t.getV()!=null).map(DayWaterSituationStatisticsTableTth::getV).reduce(Double::sum).orElse(0.00));
            waterSituationStatisticsTableToday.setTthZdCk(data_8.stream().filter(t->t.getTableHeadId().equals(ckzd.getId()) && t.getV()!=null).map(DayWaterSituationStatisticsTableTth::getV).reduce(Double::sum).orElse(0.00));
        }
        List<DayWaterSituationStatisticsTableQs> dayWaterSituationStatisticsTableQsList = dayWaterSituationStatisticsTableQsMapper.selectList(today);
        if(null != dayWaterSituationStatisticsTableQsList && dayWaterSituationStatisticsTableQsList.size()>0){
            List<DayWaterSituationStatisticsTableQs> data_8 = dayWaterSituationStatisticsTableQsList.stream().filter(t -> t.getTime().equals("08:00")).collect(Collectors.toList());
            List<TrendsTableParam> qsTableParam = trendsTableParamList.stream().filter(t -> t.getUseType() == 1 && t.getUseStation().equals("渠首管理站")).collect(Collectors.toList());
            TrendsTableParam qh = qsTableParam.stream().filter(t -> t.getPId().equals("0") && t.getParamName().equals("全河")).collect(Collectors.toList()).get(0);
            TrendsTableParam daTemp = qsTableParam.stream().filter(t -> t.getPId().equals("0") && t.getParamName().equals("东岸")).collect(Collectors.toList()).get(0);
            TrendsTableParam dgqTemp = qsTableParam.stream().filter(t -> t.getPId().equals(daTemp.getId()) && t.getParamName().equals("东干渠")).collect(Collectors.toList()).get(0);
            TrendsTableParam dgq = qsTableParam.stream().filter(t -> t.getPId().equals(dgqTemp.getId()) && t.getParamName().equals("东干渠流量")).collect(Collectors.toList()).get(0);
            TrendsTableParam dlqTemp = qsTableParam.stream().filter(t -> t.getPId().equals(daTemp.getId()) && t.getParamName().equals("灯笼渠流量")).collect(Collectors.toList()).get(0);
            TrendsTableParam dlq = qsTableParam.stream().filter(t -> t.getPId().equals(dlqTemp.getId()) && t.getParamName().equals("合计")).collect(Collectors.toList()).get(0);
            TrendsTableParam xaTemp = qsTableParam.stream().filter(t -> t.getPId().equals("0") && t.getParamName().equals("西岸")).collect(Collectors.toList()).get(0);
            TrendsTableParam xgq = qsTableParam.stream().filter(t -> t.getPId().equals(xaTemp.getId()) && t.getParamName().equals("西干渠流量")).collect(Collectors.toList()).get(0);
            TrendsTableParam ld = qsTableParam.stream().filter(t -> t.getPId().equals("0") && t.getParamName().equals("漏斗")).collect(Collectors.toList()).get(0);
            TrendsTableParam byNy = qsTableParam.stream().filter(t -> t.getPId().equals(dlqTemp.getId()) && t.getParamName().equals("八一")).collect(Collectors.toList()).get(0);
            TrendsTableParam lxNy = qsTableParam.stream().filter(t -> t.getPId().equals(dlqTemp.getId()) && t.getParamName().equals("立新")).collect(Collectors.toList()).get(0);
            TrendsTableParam dlqLh = qsTableParam.stream().filter(t -> t.getPId().equals(dlqTemp.getId()) && t.getParamName().equals("灯笼渠绿化")).collect(Collectors.toList()).get(0);
            TrendsTableParam st = qsTableParam.stream().filter(t -> t.getPId().equals("0") && t.getParamName().equals("生态")).collect(Collectors.toList()).get(0);
            waterSituationStatisticsTableToday.setQsZgqDgq(data_8.stream().filter(t->t.getTableHeadId().equals(dgq.getId())).map(DayWaterSituationStatisticsTableQs::getV).collect(Collectors.toList()).get(0));
            waterSituationStatisticsTableToday.setQsZgqXgq(data_8.stream().filter(t->t.getTableHeadId().equals(xgq.getId())).map(DayWaterSituationStatisticsTableQs::getV).collect(Collectors.toList()).get(0));
            waterSituationStatisticsTableToday.setQsZgqLd(data_8.stream().filter(t->t.getTableHeadId().equals(ld.getId())).map(DayWaterSituationStatisticsTableQs::getV).collect(Collectors.toList()).get(0));
            waterSituationStatisticsTableToday.setQsZgqHj(
                    (waterSituationStatisticsTableToday.getQsZgqDgq()==null?0.00:waterSituationStatisticsTableToday.getQsZgqDgq())+
                    (waterSituationStatisticsTableToday.getQsZgqXgq()==null?0.00:waterSituationStatisticsTableToday.getQsZgqXgq())+
                    (waterSituationStatisticsTableToday.getQsZgqLd()==null?0.00:waterSituationStatisticsTableToday.getQsZgqLd())
            );
            waterSituationStatisticsTableToday.setQsDlqNy(
                    data_8.stream().filter(t->t.getTableHeadId().equals(byNy.getId()) && t.getV()!=null).map(DayWaterSituationStatisticsTableQs::getV).reduce(Double::sum).orElse(0.00)+
                    data_8.stream().filter(t->t.getTableHeadId().equals(lxNy.getId()) && t.getV()!=null).map(DayWaterSituationStatisticsTableQs::getV).reduce(Double::sum).orElse(0.00)
            );
            waterSituationStatisticsTableToday.setQsDlqLh(
                    data_8.stream().filter(t->t.getTableHeadId().equals(dlqLh.getId()) && t.getV()!=null).map(DayWaterSituationStatisticsTableQs::getV).reduce(Double::sum).orElse(0.00)
            );
            waterSituationStatisticsTableToday.setQsDlqHj(
                    (waterSituationStatisticsTableToday.getQsDlqNy()==null?0.00:waterSituationStatisticsTableToday.getQsDlqNy())+
                    (waterSituationStatisticsTableToday.getQsDlqLh()==null?0.00:waterSituationStatisticsTableToday.getQsDlqLh())
            );
            waterSituationStatisticsTableToday.setQsXh(data_8.stream().filter(t->t.getTableHeadId().equals(st.getId()) && t.getV()!=null).map(DayWaterSituationStatisticsTableQs::getV).reduce(Double::sum).orElse(0.00));
            waterSituationStatisticsTableToday.setQsQh(data_8.stream().filter(t->t.getTableHeadId().equals(qh.getId()) && t.getV()!=null).map(DayWaterSituationStatisticsTableQs::getV).reduce(Double::sum).orElse(0.00));
        }
        List<DayWaterSituationStatisticsTableDkl> dayWaterSituationStatisticsTableDklList = dayWaterSituationStatisticsTableDklMapper.selectList(today);
        if(null != dayWaterSituationStatisticsTableDklList && dayWaterSituationStatisticsTableDklList.size()>0){
            List<DayWaterSituationStatisticsTableDkl> data_8 = dayWaterSituationStatisticsTableDklList.stream().filter(t -> t.getTime().equals("08:00")).collect(Collectors.toList());
            List<TrendsTableParam> dklTableParam = trendsTableParamList.stream().filter(t -> t.getUseType() == 1 && t.getUseStation().equals("对口率")).collect(Collectors.toList());
            TrendsTableParam qs = dklTableParam.stream().filter(t -> t.getPId().equals("0") && t.getParamName().equals("头屯河-渠首")).collect(Collectors.toList()).get(0);
            TrendsTableParam hd = dklTableParam.stream().filter(t -> t.getPId().equals("0") && t.getParamName().equals("渠首-河东")).collect(Collectors.toList()).get(0);
            TrendsTableParam hx = dklTableParam.stream().filter(t -> t.getPId().equals("0") && t.getParamName().equals("渠首-河西")).collect(Collectors.toList()).get(0);
            waterSituationStatisticsTableToday.setQsDkl(data_8.stream().filter(t->t.getTableHeadId().equals(qs.getId())).map(DayWaterSituationStatisticsTableDkl::getV).collect(Collectors.toList()).get(0));
            waterSituationStatisticsTableToday.setHdDkl(data_8.stream().filter(t->t.getTableHeadId().equals(hd.getId())).map(DayWaterSituationStatisticsTableDkl::getV).collect(Collectors.toList()).get(0));
            waterSituationStatisticsTableToday.setHxDkl(data_8.stream().filter(t->t.getTableHeadId().equals(hx.getId())).map(DayWaterSituationStatisticsTableDkl::getV).collect(Collectors.toList()).get(0));
        }
        List<DayWaterSituationStatisticsTableHd> dayWaterSituationStatisticsTableHdList = dayWaterSituationStatisticsTableHdMapper.selectList(today);
        if(null != dayWaterSituationStatisticsTableHdList && dayWaterSituationStatisticsTableHdList.size()>0){
            List<DayWaterSituationStatisticsTableHd> data_8 = dayWaterSituationStatisticsTableHdList.stream().filter(t -> t.getTime().equals("08:00")).collect(Collectors.toList());
            List<TrendsTableParam> hdTableParam = trendsTableParamList.stream().filter(t -> t.getUseType() == 1 && t.getUseStation().equals("河东管理站")).collect(Collectors.toList());
            TrendsTableParam sdncTemp = hdTableParam.stream().filter(t -> t.getPId().equals("0") && t.getParamName().equals("三大农场")).collect(Collectors.toList()).get(0);
            TrendsTableParam tthnc = hdTableParam.stream().filter(t -> t.getPId().equals(sdncTemp.getId()) && t.getParamName().equals("头屯河农场")).collect(Collectors.toList()).get(0);
            TrendsTableParam tthncXyz3D = hdTableParam.stream().filter(t -> t.getPId().equals(tthnc.getId()) && t.getParamName().equals("西一支三队")).collect(Collectors.toList()).get(0);
            TrendsTableParam tthncXyz2D = hdTableParam.stream().filter(t -> t.getPId().equals(tthnc.getId()) && t.getParamName().equals("西一支二队")).collect(Collectors.toList()).get(0);
            TrendsTableParam tthncXrz4D = hdTableParam.stream().filter(t -> t.getPId().equals(tthnc.getId()) && t.getParamName().equals("西二支四队")).collect(Collectors.toList()).get(0);
            TrendsTableParam tthncXrz5D = hdTableParam.stream().filter(t -> t.getPId().equals(tthnc.getId()) && t.getParamName().equals("西二支五队")).collect(Collectors.toList()).get(0);
            TrendsTableParam tthncYld = hdTableParam.stream().filter(t -> t.getPId().equals(tthnc.getId()) && t.getParamName().equals("园林队")).collect(Collectors.toList()).get(0);
            TrendsTableParam tthncHj = hdTableParam.stream().filter(t -> t.getPId().equals(tthnc.getId()) && t.getParamName().equals("合计")).collect(Collectors.toList()).get(0);
            TrendsTableParam wync = hdTableParam.stream().filter(t -> t.getPId().equals(sdncTemp.getId()) && t.getParamName().equals("五一农场")).collect(Collectors.toList()).get(0);
            TrendsTableParam wyncZg = hdTableParam.stream().filter(t -> t.getPId().equals(wync.getId()) && t.getParamName().equals("中干")).collect(Collectors.toList()).get(0);
            TrendsTableParam hyTemp = hdTableParam.stream().filter(t -> t.getPId().equals("0") && t.getParamName().equals("红岩")).collect(Collectors.toList()).get(0);
            TrendsTableParam yhdlk = hdTableParam.stream().filter(t -> t.getPId().equals(hyTemp.getId()) && t.getParamName().equals("红岩水库")).collect(Collectors.toList()).get(0);
            TrendsTableParam spnc = hdTableParam.stream().filter(t -> t.getPId().equals(sdncTemp.getId()) && t.getParamName().equals("三坪农场")).collect(Collectors.toList()).get(0);
            TrendsTableParam spncX3z = hdTableParam.stream().filter(t -> t.getPId().equals(spnc.getId()) && t.getParamName().equals("西三支")).collect(Collectors.toList()).get(0);
            TrendsTableParam spncX4z = hdTableParam.stream().filter(t -> t.getPId().equals(spnc.getId()) && t.getParamName().equals("西四支")).collect(Collectors.toList()).get(0);
            TrendsTableParam spncX5z = hdTableParam.stream().filter(t -> t.getPId().equals(spnc.getId()) && t.getParamName().equals("西五支")).collect(Collectors.toList()).get(0);
            TrendsTableParam spncDgd1z = hdTableParam.stream().filter(t -> t.getPId().equals(spnc.getId()) && t.getParamName().equals("东干第一支")).collect(Collectors.toList()).get(0);
            TrendsTableParam spncHj = hdTableParam.stream().filter(t -> t.getPId().equals(spnc.getId()) && t.getParamName().equals("合计")).collect(Collectors.toList()).get(0);
            TrendsTableParam gx = hdTableParam.stream().filter(t -> t.getPId().equals("0") && t.getParamName().equals("格信公司")).collect(Collectors.toList()).get(0);
            TrendsTableParam gxD3z = hdTableParam.stream().filter(t -> t.getPId().equals(gx.getId()) && t.getParamName().equals("东三支")).collect(Collectors.toList()).get(0);
            TrendsTableParam xy = hdTableParam.stream().filter(t -> t.getPId().equals("0") && t.getParamName().equals("西缘产业")).collect(Collectors.toList()).get(0);
            TrendsTableParam xyD1z = hdTableParam.stream().filter(t -> t.getPId().equals(xy.getId()) && t.getParamName().equals("东一支十一队")).collect(Collectors.toList()).get(0);
            TrendsTableParam xyD2z = hdTableParam.stream().filter(t -> t.getPId().equals(xy.getId()) && t.getParamName().equals("东二支")).collect(Collectors.toList()).get(0);
            TrendsTableParam xyHj = hdTableParam.stream().filter(t -> t.getPId().equals(xy.getId()) && t.getParamName().equals("合计")).collect(Collectors.toList()).get(0);
            TrendsTableParam lh = hdTableParam.stream().filter(t -> t.getPId().equals("0") && t.getParamName().equals("绿化")).collect(Collectors.toList()).get(0);
            TrendsTableParam lhHj = hdTableParam.stream().filter(t -> t.getPId().equals(lh.getId()) && t.getParamName().equals("合计")).collect(Collectors.toList()).get(0);
            TrendsTableParam dgHj = hdTableParam.stream().filter(t -> t.getPId().equals("0") && t.getParamName().equals("合计")).collect(Collectors.toList()).get(0);
            waterSituationStatisticsTableToday.setHdTthnkXyz(
                    data_8.stream().filter(t->t.getTableHeadId().equals(tthncXyz3D.getId()) && t.getV()!=null).map(DayWaterSituationStatisticsTableHd::getV).reduce(Double::sum).orElse(0.00)+
                    data_8.stream().filter(t->t.getTableHeadId().equals(tthncXyz2D.getId()) && t.getV()!=null).map(DayWaterSituationStatisticsTableHd::getV).reduce(Double::sum).orElse(0.00)
            );
            waterSituationStatisticsTableToday.setHdTthncXez(
                    data_8.stream().filter(t->t.getTableHeadId().equals(tthncXrz4D.getId()) && t.getV()!=null).map(DayWaterSituationStatisticsTableHd::getV).reduce(Double::sum).orElse(0.00)+
                    data_8.stream().filter(t->t.getTableHeadId().equals(tthncXrz5D.getId()) && t.getV()!=null).map(DayWaterSituationStatisticsTableHd::getV).reduce(Double::sum).orElse(0.00)
            );
            waterSituationStatisticsTableToday.setHdTthncYld(data_8.stream().filter(t->t.getTableHeadId().equals(tthncYld.getId())).map(DayWaterSituationStatisticsTableHd::getV).collect(Collectors.toList()).get(0));
            waterSituationStatisticsTableToday.setHdTthncHj(data_8.stream().filter(t->t.getTableHeadId().equals(tthncHj.getId())).map(DayWaterSituationStatisticsTableHd::getV).collect(Collectors.toList()).get(0));
            waterSituationStatisticsTableToday.setHdWyncZg(data_8.stream().filter(t->t.getTableHeadId().equals(wyncZg.getId())).map(DayWaterSituationStatisticsTableHd::getV).collect(Collectors.toList()).get(0));
            waterSituationStatisticsTableToday.setHdHydlk(data_8.stream().filter(t->t.getTableHeadId().equals(yhdlk.getId())).map(DayWaterSituationStatisticsTableHd::getV).collect(Collectors.toList()).get(0));
            waterSituationStatisticsTableToday.setHdSpncXsz3(data_8.stream().filter(t->t.getTableHeadId().equals(spncX3z.getId())).map(DayWaterSituationStatisticsTableHd::getV).collect(Collectors.toList()).get(0));
            waterSituationStatisticsTableToday.setHdSpncXsz4(data_8.stream().filter(t->t.getTableHeadId().equals(spncX4z.getId())).map(DayWaterSituationStatisticsTableHd::getV).collect(Collectors.toList()).get(0));
            waterSituationStatisticsTableToday.setHdSpncXwz(data_8.stream().filter(t->t.getTableHeadId().equals(spncX5z.getId())).map(DayWaterSituationStatisticsTableHd::getV).collect(Collectors.toList()).get(0));
            waterSituationStatisticsTableToday.setHdSpncDgdy(data_8.stream().filter(t->t.getTableHeadId().equals(spncDgd1z.getId())).map(DayWaterSituationStatisticsTableHd::getV).collect(Collectors.toList()).get(0));
            waterSituationStatisticsTableToday.setHdSpncHj(data_8.stream().filter(t->t.getTableHeadId().equals(spncHj.getId())).map(DayWaterSituationStatisticsTableHd::getV).collect(Collectors.toList()).get(0));
            waterSituationStatisticsTableToday.setHdDsz(data_8.stream().filter(t->t.getTableHeadId().equals(gxD3z.getId())).map(DayWaterSituationStatisticsTableHd::getV).collect(Collectors.toList()).get(0));
            waterSituationStatisticsTableToday.setHdDyz(data_8.stream().filter(t->t.getTableHeadId().equals(xyD1z.getId())).map(DayWaterSituationStatisticsTableHd::getV).collect(Collectors.toList()).get(0));
            waterSituationStatisticsTableToday.setHdDez(data_8.stream().filter(t->t.getTableHeadId().equals(xyD2z.getId())).map(DayWaterSituationStatisticsTableHd::getV).collect(Collectors.toList()).get(0));
            waterSituationStatisticsTableToday.setHdXycyhj(data_8.stream().filter(t->t.getTableHeadId().equals(xyHj.getId())).map(DayWaterSituationStatisticsTableHd::getV).collect(Collectors.toList()).get(0));
            waterSituationStatisticsTableToday.setHdHdlh(data_8.stream().filter(t->t.getTableHeadId().equals(lhHj.getId())).map(DayWaterSituationStatisticsTableHd::getV).collect(Collectors.toList()).get(0));
            waterSituationStatisticsTableToday.setHdDghj(data_8.stream().filter(t->t.getTableHeadId().equals(dgHj.getId())).map(DayWaterSituationStatisticsTableHd::getV).collect(Collectors.toList()).get(0));

        }
        List<DayWaterSituationStatisticsTableHx> dayWaterSituationStatisticsTableHxList = dayWaterSituationStatisticsTableHxMapper.selectList(today);
        if(null != dayWaterSituationStatisticsTableHxList && dayWaterSituationStatisticsTableHxList.size()>0){
            List<DayWaterSituationStatisticsTableHx> data_8 = dayWaterSituationStatisticsTableHxList.stream().filter(t -> t.getTime().equals("08:00")).collect(Collectors.toList());
            List<TrendsTableParam> hxTableParam = trendsTableParamList.stream().filter(t -> t.getUseType() == 1 && t.getUseStation().equals("河西管理站")).collect(Collectors.toList());
            TrendsTableParam sgzTemp = hxTableParam.stream().filter(t -> t.getPId().equals("0") && t.getParamName().equals("三工镇")).collect(Collectors.toList()).get(0);
            TrendsTableParam sgzSgq = hxTableParam.stream().filter(t -> t.getPId().equals(sgzTemp.getId()) && t.getParamName().equals("三工渠")).collect(Collectors.toList()).get(0);
            TrendsTableParam sgzEgq = hxTableParam.stream().filter(t -> t.getPId().equals(sgzTemp.getId()) && t.getParamName().equals("二工渠")).collect(Collectors.toList()).get(0);
            TrendsTableParam sgzQzqTemp = hxTableParam.stream().filter(t -> t.getPId().equals(sgzTemp.getId()) && t.getParamName().equals("旗帜渠")).collect(Collectors.toList()).get(0);
            TrendsTableParam sgzQzqCount = hxTableParam.stream().filter(t -> t.getPId().equals(sgzQzqTemp.getId()) && t.getParamName().equals("合计")).collect(Collectors.toList()).get(0);
            TrendsTableParam sgzHgq = hxTableParam.stream().filter(t -> t.getPId().equals(sgzTemp.getId()) && t.getParamName().equals("红光渠")).collect(Collectors.toList()).get(0);
            TrendsTableParam sgzCfq = hxTableParam.stream().filter(t -> t.getPId().equals(sgzTemp.getId()) && t.getParamName().equals("长丰渠")).collect(Collectors.toList()).get(0);
            TrendsTableParam sgzHj = hxTableParam.stream().filter(t -> t.getPId().equals(sgzTemp.getId()) && t.getParamName().equals("合计")).collect(Collectors.toList()).get(0);
            TrendsTableParam lgqTemp = hxTableParam.stream().filter(t -> t.getPId().equals("0") && t.getParamName().equals("六工镇")).collect(Collectors.toList()).get(0);
            TrendsTableParam lgqYyc = hxTableParam.stream().filter(t -> t.getPId().equals(lgqTemp.getId()) && t.getParamName().equals("园艺渠")).collect(Collectors.toList()).get(0);
            TrendsTableParam lgqSyc = hxTableParam.stream().filter(t -> t.getPId().equals(lgqTemp.getId()) && t.getParamName().equals("实验场")).collect(Collectors.toList()).get(0);
            TrendsTableParam lgqLgz = hxTableParam.stream().filter(t -> t.getPId().equals(lgqTemp.getId()) && t.getParamName().equals("六工水管站")).collect(Collectors.toList()).get(0);
            TrendsTableParam lgqHj = hxTableParam.stream().filter(t -> t.getPId().equals(lgqTemp.getId()) && t.getParamName().equals("合计")).collect(Collectors.toList()).get(0);
            TrendsTableParam lhTemp = hxTableParam.stream().filter(t -> t.getPId().equals("0") && t.getParamName().equals("绿化")).collect(Collectors.toList()).get(0);
            TrendsTableParam lgSglh = hxTableParam.stream().filter(t -> t.getPId().equals(lhTemp.getId()) && t.getParamName().equals("三工绿化")).collect(Collectors.toList()).get(0);
            TrendsTableParam xh = hxTableParam.stream().filter(t -> t.getPId().equals("0") && t.getParamName().equals("泄洪")).collect(Collectors.toList()).get(0);
            TrendsTableParam jgd = hxTableParam.stream().filter(t -> t.getParamName().equals("净水池")).collect(Collectors.toList()).get(0);
            TrendsTableParam xgHj = hxTableParam.stream().filter(t -> t.getPId().equals("0") && t.getParamName().equals("合计")).collect(Collectors.toList()).get(0);
            waterSituationStatisticsTableToday.setHxSgzSgq(data_8.stream().filter(t->t.getTableHeadId().equals(sgzSgq.getId())).map(DayWaterSituationStatisticsTableHx::getV).collect(Collectors.toList()).get(0));
            waterSituationStatisticsTableToday.setHxSgzEgq(data_8.stream().filter(t->t.getTableHeadId().equals(sgzEgq.getId())).map(DayWaterSituationStatisticsTableHx::getV).collect(Collectors.toList()).get(0));
            waterSituationStatisticsTableToday.setHxSgzQzq(data_8.stream().filter(t->t.getTableHeadId().equals(sgzQzqCount.getId())).map(DayWaterSituationStatisticsTableHx::getV).collect(Collectors.toList()).get(0));
            waterSituationStatisticsTableToday.setHxSgzGhq(data_8.stream().filter(t->t.getTableHeadId().equals(sgzHgq.getId())).map(DayWaterSituationStatisticsTableHx::getV).collect(Collectors.toList()).get(0));
            waterSituationStatisticsTableToday.setHxSgzCfq(data_8.stream().filter(t->t.getTableHeadId().equals(sgzCfq.getId())).map(DayWaterSituationStatisticsTableHx::getV).collect(Collectors.toList()).get(0));
            waterSituationStatisticsTableToday.setHxSgzSs(data_8.stream().filter(t->t.getTableHeadId().equals(sgzHj.getId())).map(DayWaterSituationStatisticsTableHx::getV).collect(Collectors.toList()).get(0));
            waterSituationStatisticsTableToday.setHxLgqYyc(data_8.stream().filter(t->t.getTableHeadId().equals(lgqYyc.getId())).map(DayWaterSituationStatisticsTableHx::getV).collect(Collectors.toList()).get(0));
            waterSituationStatisticsTableToday.setHxLgqSyc(data_8.stream().filter(t->t.getTableHeadId().equals(lgqSyc.getId())).map(DayWaterSituationStatisticsTableHx::getV).collect(Collectors.toList()).get(0));
            waterSituationStatisticsTableToday.setHxLgqLgz(data_8.stream().filter(t->t.getTableHeadId().equals(lgqLgz.getId())).map(DayWaterSituationStatisticsTableHx::getV).collect(Collectors.toList()).get(0));
            waterSituationStatisticsTableToday.setHxLgqHj(data_8.stream().filter(t->t.getTableHeadId().equals(lgqHj.getId())).map(DayWaterSituationStatisticsTableHx::getV).collect(Collectors.toList()).get(0));
            waterSituationStatisticsTableToday.setHxSgnbstl(data_8.stream().filter(t->t.getTableHeadId().equals(lgSglh.getId())).map(DayWaterSituationStatisticsTableHx::getV).collect(Collectors.toList()).get(0));
            waterSituationStatisticsTableToday.setHxXh(data_8.stream().filter(t->t.getTableHeadId().equals(xh.getId())).map(DayWaterSituationStatisticsTableHx::getV).collect(Collectors.toList()).get(0));
            waterSituationStatisticsTableToday.setHxJgd(data_8.stream().filter(t->t.getTableHeadId().equals(jgd.getId())).map(DayWaterSituationStatisticsTableHx::getV).collect(Collectors.toList()).get(0));
            waterSituationStatisticsTableToday.setHxXghj(data_8.stream().filter(t->t.getTableHeadId().equals(xgHj.getId())).map(DayWaterSituationStatisticsTableHx::getV).collect(Collectors.toList()).get(0));
        }
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
/*        waterSituationStatisticsTableToday.setLzzCkllHj(
                (waterSituationStatisticsTableToday.getLzzCkllLzzsc()==null?0.0:waterSituationStatisticsTableToday.getLzzCkllLzzsc())
                +(waterSituationStatisticsTableToday.getLzzCkllHd()==null?0.0:waterSituationStatisticsTableToday.getLzzCkllHd())
        );
        waterSituationStatisticsTableToday.setTthJkHj(
                (waterSituationStatisticsTableToday.getTthJkRk()==null?0.0:waterSituationStatisticsTableToday.getTthJkRk())
                +(waterSituationStatisticsTableToday.getTthJkGyy()==null?0.0:waterSituationStatisticsTableToday.getTthJkGyy())
        );
        waterSituationStatisticsTableToday.setTthCkHj(
                (waterSituationStatisticsTableToday.getTthCkHd()==null?0.00:waterSituationStatisticsTableToday.getTthCkHd())+
                (waterSituationStatisticsTableToday.getTthCkBg()==null?0.00:waterSituationStatisticsTableToday.getTthCkBg())+
                (waterSituationStatisticsTableToday.getTthCkHysk()==null?0.00:waterSituationStatisticsTableToday.getTthCkHysk())
        );
        waterSituationStatisticsTableToday.setQsZgqHj(
                (waterSituationStatisticsTableToday.getQsZgqDgq()==null?0.00:waterSituationStatisticsTableToday.getQsZgqDgq())+
                (waterSituationStatisticsTableToday.getQsZgqXgq()==null?0.00:waterSituationStatisticsTableToday.getQsZgqXgq())+
                (waterSituationStatisticsTableToday.getQsZgqLd()==null?0.00:waterSituationStatisticsTableToday.getQsZgqLd())
        );
        waterSituationStatisticsTableToday.setQsDlqHj(
                (waterSituationStatisticsTableToday.getQsDlqNy()==null?0.00:waterSituationStatisticsTableToday.getQsDlqNy())+
                (waterSituationStatisticsTableToday.getQsDlqLh()==null?0.00:waterSituationStatisticsTableToday.getQsDlqLh())
        );
        //waterSituationStatisticsTableToday.setQsQh();*/
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

