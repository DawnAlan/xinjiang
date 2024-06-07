package com.cj.waterresources.func.modular.quotaStatisticsManagement.irrigationQuotaDetails.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.common.model.RestResponse;
import com.cj.common.util.NumberUtil;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.irrigationQuota.bean.req.IrrigationQuotaContrastReq;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.irrigationQuota.entity.IrrigationQuota;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.irrigationQuota.service.IrrigationQuotaService;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.irrigationQuotaDetails.bean.req.StatisticsByLocationReq;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.irrigationQuota.bean.res.IrrigationQuotaContrastRes;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.irrigationQuotaDetails.bean.res.StatisticsByLocationRes;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.irrigationQuotaDetails.bean.req.StatisticsReq;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.irrigationQuotaDetails.mapper.IrrigationQuotaDetailsMapper;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.irrigationQuotaDetails.entity.IrrigationQuotaDetails;
import com.cj.waterresources.func.modular.quotaStatisticsManagement.irrigationQuotaDetails.service.IrrigationQuotaDetailsService;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.useWaterManagement.entity.UseWaterManagement;
import com.cj.waterresources.func.modular.useWaterPlanEscalation.useWaterManagement.service.UseWaterManagementService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 灌溉明细表(IrrigationQuotaDetails)表服务实现类
 *
 * @author makejava
 * @since 2024-02-02 10:59:15
 */
@Service("irrigationQuotaDetailsService")
public class IrrigationQuotaDetailsServiceImpl extends ServiceImpl<IrrigationQuotaDetailsMapper, IrrigationQuotaDetails> implements IrrigationQuotaDetailsService {

    @Autowired
    private IrrigationQuotaService irrigationQuotaService;

    @Autowired
    private UseWaterManagementService useWaterManagementService;

    @Override
    public RestResponse<Map<String, List<IrrigationQuotaDetails>>> statistics(StatisticsReq req) {
        List<IrrigationQuotaDetails> statistics = this.baseMapper.statistics(req);
        if (null != statistics && statistics.size() > 0) {
            if (StringUtils.isEmpty(req.getUnit())) {
                Map<String, List<IrrigationQuotaDetails>> collect = statistics.stream().collect(Collectors.groupingBy(IrrigationQuotaDetails::getWaterUser));
                return RestResponse.ok(collect);
            } else {
                if (StringUtils.isEmpty(req.getCropType())) {
                    Map<String, List<IrrigationQuotaDetails>> collect = statistics.stream().collect(Collectors.groupingBy(IrrigationQuotaDetails::getCropType));
                    return RestResponse.ok(collect);
                } else {
                    Map<String, List<IrrigationQuotaDetails>> collect = statistics.stream().collect(Collectors.groupingBy(IrrigationQuotaDetails::getIrrigationCrop));
                    return RestResponse.ok(collect);
                }
            }
        } else {
            return RestResponse.no("blank");
        }
    }

    @Override
    public Map<String, List<IrrigationQuotaContrastRes>> contrast(IrrigationQuotaContrastReq req) {
        List<IrrigationQuotaContrastRes> resList = this.baseMapper.contrast(req);

        if (null != resList && resList.size() > 0) {
            if (StringUtils.isEmpty(req.getUnit())) {
                Map<String, List<IrrigationQuotaContrastRes>> collect = resList.stream().collect(Collectors.groupingBy(IrrigationQuotaContrastRes::getWaterUser));
                return collect;
            } else {
                if (StringUtils.isEmpty(req.getCropType())) {
                    Map<String, List<IrrigationQuotaContrastRes>> collect = resList.stream().collect(Collectors.groupingBy(IrrigationQuotaContrastRes::getCropType));
                    return collect;
                } else {
                    Map<String, List<IrrigationQuotaContrastRes>> collect = resList.stream().collect(Collectors.groupingBy(IrrigationQuotaContrastRes::getIrrigationCrop));
                    return collect;
                }
            }
        } else {
            return null;
        }
    }

    @Override
    public RestResponse<Map<String, List<StatisticsByLocationRes>>> statisticsByLocation(StatisticsByLocationReq req) {
        Map<String, List<StatisticsByLocationRes>> result = new HashMap<>();
        Map<String, String> dateMap = getDateMap(req);
        if(null == dateMap){
            return RestResponse.no("参数异常");
        }
        String startTime = req.getStartTime();
        String endTime = req.getEndTime();
        List<UseWaterManagement> useWaterManagementList = useWaterManagementService.lambdaQuery().eq(UseWaterManagement::getUseWaterPlan, "年用水计划").list();
        Map<Integer, List<UseWaterManagement>> collect = useWaterManagementList.stream().filter(t -> t.getLocation() != null).collect(Collectors.groupingBy(UseWaterManagement::getLocation));
        Set<Integer> integers = collect.keySet();
        for(Integer i:integers){
            List<StatisticsByLocationRes> tempList = new ArrayList<>();
            List<String> strings = collect.get(i).stream().map(UseWaterManagement::getUnitName).collect(Collectors.toList());
            List<IrrigationQuota> list = irrigationQuotaService.lambdaQuery().in(IrrigationQuota::getWaterUser, strings).eq(IrrigationQuota::getYear,req.getYear()).list();
            Map<String, List<IrrigationQuota>> collect1 = list.stream().collect(Collectors.groupingBy(IrrigationQuota::getIrrigationCrop));
            Set<String> strings1 = collect1.keySet();
            for(String crop:strings1){
                StatisticsByLocationRes res = new StatisticsByLocationRes();
                Double aDouble = collect1.get(crop).stream().filter(t -> t.getTotalPlannedIrrigationArea() != null).map(IrrigationQuota::getTotalPlannedIrrigationArea).reduce(Double::sum).orElse(0.00);
                res.setArea(NumberUtil.holdDecimal(aDouble,2));
                res.setCrop(crop);
                List<IrrigationQuotaDetails> list1 = this.lambdaQuery().in(IrrigationQuotaDetails::getWaterUser, strings).apply("CREATE_TIME BETWEEN '" + startTime + "' and '" + endTime + "'").list();
                Double irrigationArea = list1.stream().filter(t -> t.getIrrigationCrop().equals(crop) && t.getIrrigationArea() != null).map(IrrigationQuotaDetails::getIrrigationArea).reduce(Double::sum).orElse(0.00);
                Double irrigationWaterVolume = list1.stream().filter(t -> t.getIrrigationCrop().equals(crop) && t.getIrrigationWaterVolume() != null).map(IrrigationQuotaDetails::getIrrigationWaterVolume).reduce(Double::sum).orElse(0.00);
                res.setIrrigation(NumberUtil.holdDecimal(irrigationArea,2));
                res.setWaterConsumption(NumberUtil.holdDecimal(irrigationWaterVolume,2));
                tempList.add(res);
            }
            //地区(1-十二师 2-乌鲁木齐经开区 3-昌吉)
            result.put(i==1?"十二师":i==2?"乌鲁木齐经开区":"昌吉",tempList);
        }
        return RestResponse.ok(result);
    }

    private Map<String,String> getDateMap(StatisticsByLocationReq req){
        Map<String,String> result = new HashMap<>();
        if(req.getMonth()==null){
            int year = LocalDateTime.now().getYear();
            result.put("startTime",year+"-01-01");
            result.put("endTime",year+"-12-31");
            return result;
        }
        if(req.getMonth() !=null && StringUtils.isEmpty(req.getTenDays())){
            result.put("startTime",req.getYear()+"-"+(req.getMonth().toString().length()==1?"0"+req.getMonth():req.getMonth())+"-01");
            result.put("endTime",getEndOfMonth(req.getYear(),req.getMonth()));
            return result;
        }
        if(req.getMonth()!=null && !StringUtils.isEmpty(req.getTenDays())){
            result.put("startTime",getStartOfMonthByTenDays(req.getYear(),req.getMonth(),req.getTenDays()));
            result.put("endTime",getEndOfMonthByTenDays(req.getYear(),req.getMonth(),req.getTenDays()));
            return result;
        }
        return null;
    }

    private String getEndOfMonth(Integer year, Integer month){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        // 创建一个Calendar实例
        Calendar calendar = Calendar.getInstance();
        // 设置Calendar的年份、月份和日期为该月的最大日期
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month-1); // 月份是从0开始的
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        // 获取月底的时间戳
        Date time = calendar.getTime();
        return sdf.format(time);
    }

    private String getEndOfMonthByTenDays(Integer year, Integer month,String tenDays){
        if(tenDays.equals("上旬")){
            String monthStr = month.toString().length()==1?"0"+month:month+"";
            return year+"-"+monthStr+"-10";
        }
        if(tenDays.equals("中旬")){
            String monthStr = month.toString().length()==1?"0"+month:month+"";
            return year+"-"+monthStr+"-20";
        }
        if(tenDays.equals("下旬")){
            return getEndOfMonth(year,month);
        }
        return null;
    }

    private String getStartOfMonthByTenDays(Integer year, Integer month,String tenDays){
        if(tenDays.equals("上旬")){
            String monthStr = month.toString().length()==1?"0"+month:month+"";
            return year+"-"+monthStr+"-01";
        }
        if(tenDays.equals("中旬")){
            String monthStr = month.toString().length()==1?"0"+month:month+"";
            return year+"-"+monthStr+"-11";
        }
        if(tenDays.equals("下旬")){
            String monthStr = month.toString().length()==1?"0"+month:month+"";
            return year+"-"+monthStr+"-21";
        }
        return null;
    }
}

