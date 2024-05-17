package com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.job;

import com.alibaba.fastjson.JSONObject;
import com.cj.common.model.RestResponse;
import com.cj.common.util.RedisUtil;
import com.cj.waterresources.func.modular.trendsTable.bean.req.QueryTrendsTableParamReq;
import com.cj.waterresources.func.modular.trendsTable.bean.res.WaterDailyParamSelectRes;
import com.cj.waterresources.func.modular.trendsTable.entity.TrendsTableParam;
import com.cj.waterresources.func.modular.trendsTable.service.TrendsTableParamService;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.bean.res.JobRes;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.entity.WaterFeeStatisticsDetails;
import com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.service.WaterFeeStatisticsDetailsService;
import com.cj.waterresources.func.modular.waterPrice.waterPriceManagement.entity.WaterPriceManagement;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

//@EnableScheduling//开启定时任务
@Component
@Slf4j
public class WaterFeeTask {
    @Autowired
    private WaterFeeStatisticsDetailsService waterFeeStatisticsDetailsService;

    @Autowired
    private RedisUtil redisUtil;


    public JobRes getTableHeadIdNoHaveQs(){
        try {
            JobRes res = new JobRes();
            String mk = (String) redisUtil.get("trendsTableParam:list");
            if(StringUtils.isEmpty(mk)){
                waterFeeStatisticsDetailsService.updateCache();
                mk = (String) redisUtil.get("trendsTableParam:list");
            }
            List<TrendsTableParam> trendsTableParamList = JSONObject.parseArray(mk, TrendsTableParam.class);
            List<String> resList = new ArrayList<>();
            Map<String,List<String>> map = new HashMap<>();
            Map<String, List<TrendsTableParam>> collect = trendsTableParamList.stream().filter(t -> t.getUseType() == 2).collect(Collectors.groupingBy(TrendsTableParam::getUseStation));
            Set<String> strings = collect.keySet();
            for(String s:strings){
                if (s.equals("渠首管理站") || s.equals("零星水费") || s.equals("工业水费")) {
                    continue;
                }
                resList.add(s);
                List<WaterDailyParamSelectRes> resultList = new ArrayList<>();
                List<String> tableIds = new ArrayList<>();
                List<TrendsTableParam> list = trendsTableParamList.stream().filter(t->t.getUseType()==2).filter(t->t.getUseStation().equals(s)).collect(Collectors.toList());
                List<TrendsTableParam> collectTemp = list.stream().filter(t -> t.getPId().equals("0")).collect(Collectors.toList());
                for (TrendsTableParam param:collectTemp){
                    WaterDailyParamSelectRes tempRes = new WaterDailyParamSelectRes();
                    BeanUtils.copyProperties(param,tempRes);
                    resultList.add(tempRes);
                }
                getParamTree(resultList,list);
                for(WaterDailyParamSelectRes res1 : resultList){
                    List<WaterDailyParamSelectRes> children = res1.getChildren();
                    //
                    if(children != null){
                        for(WaterDailyParamSelectRes res2:children){
                            List<WaterDailyParamSelectRes> children1 = res2.getChildren();
                            //
                            if(children1 !=null){
                                for(WaterDailyParamSelectRes res3:children1){
                                   tableIds.add(res3.getId());
                                }
                            }else {
                                tableIds.add(res2.getId());
                            }
                        }
                    }else {
                        tableIds.add(res1.getId());
                    }
                }
                map.put(s,tableIds);
            }
            if(null !=map && map.size()>0){
                res.setMap(map);
                res.setCollect(resList);
            }
            return res;
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getLocalizedMessage());
            return null;
        }
    }

    //@Scheduled(cron="0 30 08 * * ?")//每天8:30
    @XxlJob("createWaterFeeTableNoHaveQs")
    public void createWaterFeeTableNoHaveQs(){
        log.info("--------------------------------执行定时插入水费表操作----------------------------");
        JobRes tableHeadId = getTableHeadIdNoHaveQs();
        if(null != tableHeadId) {
            List<String> collect = tableHeadId.getCollect();
            Map<String, List<String>> map = tableHeadId.getMap();
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime yesterday = now.minusDays(1);
            Integer year = yesterday.getYear();
            Integer month = yesterday.getMonth().getValue();
            Integer day = yesterday.getDayOfMonth();
            String tenDays = determineTenDays(day);
            SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日");
            for (String s : collect) {
                List<String> strings = map.get(s);
                List<WaterFeeStatisticsDetails> result = new ArrayList<>();
                for (String s1 : strings) {
                    if (!s.equals("渠首管理站") && !s.equals("零星水费") && !s.equals("工业水费")) {
                        WaterFeeStatisticsDetails details = new WaterFeeStatisticsDetails();
                        details.setTableHeadId(s1);
                        details.setStation(s);
                        details.setMonth(month);
                        details.setYear(year);
                        details.setStatisticsDate(sdf.format(getDate(new Date(), -1)));
                        details.setTenDays(tenDays);
                        result.add(details);
                    }
                }
                waterFeeStatisticsDetailsService.add(result);
            }
        }
    }


    public String determineTenDays(Integer day){
        if(day<=10){
            return "上旬";
        }
        if(day<=20){
            return "中旬";
        }
        if(day>20){
            return "下旬";
        }
        return "";
    }

    public Date getDate(Date date,Integer i){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE,i);
        return calendar.getTime();
    }

    public void getParamTree(List<WaterDailyParamSelectRes> resultList,List<TrendsTableParam> list){
        if(resultList.size()>0){
            for(WaterDailyParamSelectRes res : resultList){
                List<TrendsTableParam> collect = list.stream().filter(t -> t.getPId().equals(res.getId())).collect(Collectors.toList());
                if(collect.size()>0){
                    List<WaterDailyParamSelectRes> tempList = new ArrayList<>();
                    for (TrendsTableParam param:collect){
                        WaterDailyParamSelectRes tempRes = new WaterDailyParamSelectRes();
                        BeanUtils.copyProperties(param,tempRes);
                        tempList.add(tempRes);
                    }
                    res.setChildren(tempList);
                    getParamTree(tempList,list);
                }
            }
        }
    }
}
