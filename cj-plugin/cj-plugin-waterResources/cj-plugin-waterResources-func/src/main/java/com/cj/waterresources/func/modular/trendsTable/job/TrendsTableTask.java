package com.cj.waterresources.func.modular.trendsTable.job;

import com.alibaba.fastjson.JSONObject;
import com.cj.common.util.RedisUtil;
import com.cj.waterresources.func.modular.trendsTable.entity.TrendsTableParam;
import com.cj.waterresources.func.modular.trendsTable.service.TrendsTableParamService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@EnableScheduling//开启定时任务
@Component
@Slf4j
public class TrendsTableTask {

    @Autowired
    private TrendsTableParamService trendsTableParamService;

    @Autowired
    private RedisUtil redisUtil;

    @Scheduled(cron="0 0 0/1 * * ?")//每小时执行一次，以空格分隔
    public void saveCache(){
        String listTemp = (String)redisUtil.get("trendsTableParam:list");
        if(StringUtils.isEmpty(listTemp)){
            List<TrendsTableParam> listed = trendsTableParamService.list();
            redisUtil.set("trendsTableParam:list", JSONObject.toJSONString(listed));
            for (TrendsTableParam param:listed){
                redisUtil.set("trendsTableParam:name:"+param.getId(), param.getParamName());
                redisUtil.set("trendsTableParam:object:"+param.getId(), JSONObject.toJSONString(param));
            }
        }
    }

}
