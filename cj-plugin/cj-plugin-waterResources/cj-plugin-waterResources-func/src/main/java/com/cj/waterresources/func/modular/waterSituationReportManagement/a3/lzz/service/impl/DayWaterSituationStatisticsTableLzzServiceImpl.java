package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.lzz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.common.model.RestResponse;
import com.cj.common.util.UUIDUtils;
import com.cj.middleDatabase.func.modular.lzz.lzzGaugingStation.entity.LzzGaugingStation;
import com.cj.middleDatabase.func.modular.lzz.lzzGaugingStation.service.LzzGaugingStationService;
import com.cj.waterresources.func.modular.trendsTable.entity.TrendsTableParam;
import com.cj.waterresources.func.modular.trendsTable.service.TrendsTableParamService;
import com.cj.waterresources.func.modular.waterPrice.totalIdToStation.entity.TotalIdToStation;
import com.cj.waterresources.func.modular.waterPrice.totalIdToStation.service.TotalIdToStationService;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.entity.WaterFeeStatisticsDetails;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.hx.entity.DayWaterSituationStatisticsTableHx;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.lzz.mapper.DayWaterSituationStatisticsTableLzzMapper;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.lzz.entity.DayWaterSituationStatisticsTableLzz;
import com.cj.waterresources.func.modular.waterSituationReportManagement.a3.lzz.service.DayWaterSituationStatisticsTableLzzService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 楼庄子水库日水情统计表(DayWaterSituationStatisticsTableLzz)表服务实现类
 *
 * @author makejava
 * @since 2023-12-23 15:59:34
 */
@Service("dayWaterSituationStatisticsTableLzzService")
public class DayWaterSituationStatisticsTableLzzServiceImpl extends ServiceImpl<DayWaterSituationStatisticsTableLzzMapper, DayWaterSituationStatisticsTableLzz> implements DayWaterSituationStatisticsTableLzzService {

    @Autowired
    private TotalIdToStationService totalIdToStationService;

    @Autowired
    private TrendsTableParamService trendsTableParamService;

    @Autowired
    private LzzGaugingStationService lzzGaugingStationService;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    @Override
    public RestResponse<Map<String,List<DayWaterSituationStatisticsTableLzz>>> selectList(String date) {
        try {
            List<DayWaterSituationStatisticsTableLzz> list = this.baseMapper.selectList(date);
            if(null != list && list.size()>0) {
                Map<String, List<DayWaterSituationStatisticsTableLzz>> collect = list.stream().collect(Collectors.groupingBy(DayWaterSituationStatisticsTableLzz::getTime));
                return RestResponse.ok(collect);
            }else {
                return RestResponse.no("fail");
            }
        }catch (Exception e){
            e.printStackTrace();
            return RestResponse.no("error");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse add(List<DayWaterSituationStatisticsTableLzz> dayWaterSituationStatisticsTableLzzList) {
        for(DayWaterSituationStatisticsTableLzz t:dayWaterSituationStatisticsTableLzzList){
            t.setId(UUIDUtils.getUUID());
            String paramName = trendsTableParamService.getById(t.getTableHeadId()).getParamName();
            if(paramName.equals("水位")){
                LzzGaugingStation waterLevel = lzzGaugingStationService.selectInfoByNameAndTime(sdf.format(t.getRecordTime()) + " " + t.getTime(),"楼庄子库水位站");
                t.setV(waterLevel==null?null:waterLevel.getRelativeWaterLevel()==null?null:waterLevel.getRelativeWaterLevel());
            }
            if(paramName.equals("库容")){
                LzzGaugingStation waterLevel = lzzGaugingStationService.selectInfoByNameAndTime(sdf.format(t.getRecordTime()) + " " + t.getTime(),"楼庄子库水位站");
                t.setV(waterLevel==null?null:waterLevel.getStorageCapacity()==null?null:waterLevel.getStorageCapacity());
            }
        }
        List<DayWaterSituationStatisticsTableLzz> result = new ArrayList<>();
        List<DayWaterSituationStatisticsTableLzz> list = this.baseMapper.selectList(sdf.format(dayWaterSituationStatisticsTableLzzList.get(0).getRecordTime()));
        List<DayWaterSituationStatisticsTableLzz> tempList = list.stream().filter(t -> t.getTime().equals("昨日均")).collect(Collectors.toList());
        if(null != tempList && tempList.size()==0){
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(new Date());
            calendar.add(calendar.DATE, -1);
            String yesterday= sdf.format(calendar.getTime());
            List<DayWaterSituationStatisticsTableLzz> yesterdayList = this.baseMapper.selectList(yesterday);
            Map<String, List<DayWaterSituationStatisticsTableLzz>> collect = yesterdayList.stream().collect(Collectors.groupingBy(DayWaterSituationStatisticsTableLzz::getTableHeadId));
            Set<String> strings = collect.keySet();
            for(String s:strings){
                DayWaterSituationStatisticsTableLzz yesterdayBean = new DayWaterSituationStatisticsTableLzz();
                yesterdayBean.setTime("昨日均");
                yesterdayBean.setId(UUIDUtils.getUUID());
                yesterdayBean.setTableHeadId(s);
                yesterdayBean.setRecordTime(new Date());
                yesterdayBean.setV(collect.get(s).stream().filter(t->t.getV()!=null).map(DayWaterSituationStatisticsTableLzz::getV).reduce(Double::sum).orElse(0.00));
                result.add(yesterdayBean);
            }
        }
        List<TotalIdToStation> totalIdToStationList = totalIdToStationService.lambdaQuery().eq(TotalIdToStation::getUseType, 1).eq(TotalIdToStation::getStation, "楼庄子水库").list();
        //计算行合计
        if(null != totalIdToStationList && totalIdToStationList.size()>0){
            Double total = 0.0;
            List<String> totalCollect = totalIdToStationList.stream().filter(t->t.getName().equals("合计")).map(TotalIdToStation::getTotalId).collect(Collectors.toList());
            for(String id:totalCollect){
                Double value = 0.0;
                TrendsTableParam tableParam = trendsTableParamService.getById(id);
                if(!tableParam.getPId().equals("0")){
                    List<TrendsTableParam> noTotalList = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, tableParam.getPId()).ne(TrendsTableParam::getParamName, "合计").list();
                    for(TrendsTableParam param:noTotalList){
                        for(DayWaterSituationStatisticsTableLzz t:dayWaterSituationStatisticsTableLzzList){
                            if(t.getTableHeadId().equals(param.getId())){
                                value+=t.getV()==null?0.0:t.getV();
                            }
                            List<TrendsTableParam> listed = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, param.getId()).ne(TrendsTableParam::getParamName, "合计").list();
                            if(null != listed && listed.size()>0){
                                for (TrendsTableParam param1:listed){
                                    if(t.getTableHeadId().equals(param1.getId())){
                                        value+=t.getV()==null?0.0:t.getV();
                                    }
                                }
                            }
                        }
                    }
                    for(DayWaterSituationStatisticsTableLzz t:dayWaterSituationStatisticsTableLzzList){
                        if(t.getTableHeadId().equals(id)){
                            t.setV(value);
                        }
                    }
                }
            }
            for(DayWaterSituationStatisticsTableLzz t:dayWaterSituationStatisticsTableLzzList){
                if(!totalIdToStationList.stream().map(TotalIdToStation::getTotalId).collect(Collectors.toList()).contains(t.getTableHeadId())){
                    total+=t.getV()==null?0.0:t.getV();
                }
            }
            TrendsTableParam one = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, "0").eq(TrendsTableParam::getUseType,1).in(TrendsTableParam::getId, totalCollect).one();
            if(null != one){
                for(DayWaterSituationStatisticsTableLzz t:dayWaterSituationStatisticsTableLzzList){
                    if(t.getTableHeadId().equals(one.getId())){
                        t.setV(total);
                    }
                }
            }
        }
        result.addAll(dayWaterSituationStatisticsTableLzzList);
        boolean b = this.saveBatch(result);
        if (b) {
            return RestResponse.ok();
        }else {
            return RestResponse.no("error");
        }
    }

    @Override
    public RestResponse delete(String ids) {
        List<String> collect = Arrays.stream(ids.split(",")).collect(Collectors.toList());
        boolean remove = this.lambdaUpdate().in(DayWaterSituationStatisticsTableLzz::getId, collect).remove();
        if (remove) {
            return RestResponse.ok();
        }else {
            return RestResponse.no("error");
        }
    }

    @Override
    public RestResponse update(List<DayWaterSituationStatisticsTableLzz> dayWaterSituationStatisticsTableLzzList) {
        List<TotalIdToStation> totalIdToStationList = totalIdToStationService.lambdaQuery().eq(TotalIdToStation::getUseType, 1).eq(TotalIdToStation::getStation, "楼庄子水库").list();
        //计算行合计
        if(null != totalIdToStationList && totalIdToStationList.size()>0){
            Double total = 0.0;
            List<String> totalCollect = totalIdToStationList.stream().filter(t->t.getName().equals("合计")).map(TotalIdToStation::getTotalId).collect(Collectors.toList());
            for(String id:totalCollect){
                Double value = 0.0;
                TrendsTableParam tableParam = trendsTableParamService.getById(id);
                if(!tableParam.getPId().equals("0")){
                    List<TrendsTableParam> noTotalList = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, tableParam.getPId()).ne(TrendsTableParam::getParamName, "合计").list();
                    for(TrendsTableParam param:noTotalList){
                        for(DayWaterSituationStatisticsTableLzz t:dayWaterSituationStatisticsTableLzzList){
                            if(t.getTableHeadId().equals(param.getId())){
                                value+=t.getV()==null?0.0:t.getV();
                            }
                            List<TrendsTableParam> listed = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, param.getId()).ne(TrendsTableParam::getParamName, "合计").list();
                            if(null != listed && listed.size()>0){
                                for (TrendsTableParam param1:listed){
                                    if(t.getTableHeadId().equals(param1.getId())){
                                        value+=t.getV()==null?0.0:t.getV();
                                    }
                                }
                            }
                        }
                    }
                    for(DayWaterSituationStatisticsTableLzz t:dayWaterSituationStatisticsTableLzzList){
                        if(t.getTableHeadId().equals(id)){
                            t.setV(value);
                        }
                    }

                }
            }
            for(DayWaterSituationStatisticsTableLzz t:dayWaterSituationStatisticsTableLzzList){
                if(!totalIdToStationList.stream().map(TotalIdToStation::getTotalId).collect(Collectors.toList()).contains(t.getTableHeadId())){
                    total+=t.getV()==null?0.0:t.getV();
                }
            }
            TrendsTableParam one = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getPId, "0").eq(TrendsTableParam::getUseType,1).in(TrendsTableParam::getId, totalCollect).one();
            if(null != one){
                for(DayWaterSituationStatisticsTableLzz t:dayWaterSituationStatisticsTableLzzList){
                    if(t.getTableHeadId().equals(one.getId())){
                        t.setV(total);
                    }
                }
            }
        }
        boolean b = this.updateBatchById(dayWaterSituationStatisticsTableLzzList);
        if (b) {
            return RestResponse.ok();
        }else {
            return RestResponse.no("error");
        }
    }
}

