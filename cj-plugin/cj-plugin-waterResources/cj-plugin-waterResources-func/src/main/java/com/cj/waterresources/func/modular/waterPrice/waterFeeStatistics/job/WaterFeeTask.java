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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@EnableScheduling//开启定时任务
@Component
@Slf4j
public class WaterFeeTask {
    @Autowired
    private TrendsTableParamService trendsTableParamService;
    @Autowired
    private WaterFeeStatisticsDetailsService waterFeeStatisticsDetailsService;

    @Autowired
    private RedisUtil redisUtil;


    public JobRes getTableHeadId(){
        try {
            JobRes res = new JobRes();
            log.info("--------------------------------执行定时插入水费表操作----------------------------");
            String mk = (String) redisUtil.get("trendsTableParam:list");
            if(StringUtils.isEmpty(mk)){
                waterFeeStatisticsDetailsService.updateCache();
                mk = (String) redisUtil.get("trendsTableParam:list");
            }
            List<TrendsTableParam> trendsTableParamList = JSONObject.parseArray(mk, TrendsTableParam.class);
            Map<String,List<String>> map = new HashMap<>();
            List<String> collect = trendsTableParamList.stream().filter(t -> t.getUseType() == 2).map(TrendsTableParam::getUseStation).collect(Collectors.toList());
            res.setCollect(collect);
            //List<String> collect = trendsTableParamService.lambdaQuery().eq(TrendsTableParam::getUseType, 2).list().stream().map(TrendsTableParam::getUseStation).collect(Collectors.toList());
            for(String s:collect){
                List<String> tableIds = new ArrayList<>();
                QueryTrendsTableParamReq req = new QueryTrendsTableParamReq();
                req.setUseType(2);
                req.setUseStation(s);
                RestResponse<List<WaterDailyParamSelectRes>> select = trendsTableParamService.select(req);
                List<WaterDailyParamSelectRes> data = select.getData();
                for(WaterDailyParamSelectRes res1 : data){
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
            res.setMap(map);
            return res;
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getLocalizedMessage());
            return null;
        }
    }
    @Scheduled(cron="0 30 8 * * ?")//每天8:30
    public void createWaterFeeTableNoHaveQs(){
        JobRes tableHeadId = getTableHeadId();
        if(null != tableHeadId) {
            List<String> collect = tableHeadId.getCollect();
            Map<String, List<String>> map = tableHeadId.getMap();
            LocalDateTime now = LocalDateTime.now();
            Integer year = now.getYear();
            Integer month = now.getMonth().getValue();
            Integer day = now.getDayOfMonth();
            String tenDays = determineTenDays(day);
            SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日");
            for (String s : collect) {
                List<String> strings = map.get(s);
                List<WaterFeeStatisticsDetails> result = new ArrayList<>();
                for (String s1 : strings) {
                    if (!s.equals("渠首管理站")) {
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

    @Scheduled(cron="0 35 8 * * ?")//每天8:35
    public void createWaterFeeTableHaveQs(){
        JobRes tableHeadId = getTableHeadId();
        if(null != tableHeadId){
            List<String> collect = tableHeadId.getCollect();
            Map<String, List<String>> map = tableHeadId.getMap();
            LocalDateTime now = LocalDateTime.now();
            Integer year = now.getYear();
            Integer month = now.getMonth().getValue();
            Integer day = now.getDayOfMonth();
            String tenDays = determineTenDays(day);
            SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日");
            for(String s:collect){
                List<String> strings = map.get(s);
                List<WaterFeeStatisticsDetails> result = new ArrayList<>();
                for(String s1:strings){
                    if(s.equals("渠首管理站")){
                        WaterFeeStatisticsDetails details = new WaterFeeStatisticsDetails();
                        details.setTableHeadId(s1);
                        details.setStation(s);
                        details.setMonth(month);
                        details.setYear(year);
                        details.setStatisticsDate(sdf.format(getDate(new Date(),-1)));
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
}
