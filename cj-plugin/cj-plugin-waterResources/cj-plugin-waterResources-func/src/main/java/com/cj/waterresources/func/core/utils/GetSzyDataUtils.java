package com.cj.waterresources.func.core.utils;

import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson.JSONObject;
import com.cj.common.util.RestTemplateUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Slf4j
public class GetSzyDataUtils {

    private static String szyAddress;

    @Value("${szyAddress}")
    public void setKey(String szyAddress) {    //注意这里的set方法不能是静态的
        GetSzyDataUtils.szyAddress = szyAddress;
    }

    @SneakyThrows
    public static Double getWaterLevelByFlow(Double flow, String id){
        String token = StpUtil.getTokenValue();
        String url = "http://" + szyAddress +":9003/toutunhe/wpdCurved/queryLevelFlow?ndcdId="+id+"&flowRate="+flow;
        String s = RestTemplateUtil.getBySaToken(url,token);
        BigDecimal value = (BigDecimal) JSONObject.parseObject(s).get("data");
        return value==null?0.00:value.doubleValue();
    }

    @SneakyThrows
    public static Double getWaterLevelByLevel(Double level, String id){
        String token = StpUtil.getTokenValue();
        String url = "http://" + szyAddress +":9003/toutunhe/wpdCurved/queryLevelFlow?ndcdId="+id+"&level="+level;
        String s = RestTemplateUtil.getBySaToken(url,token);
        BigDecimal value = (BigDecimal) JSONObject.parseObject(s).get("data");
        return value==null?0.00:value.doubleValue();
    }
}
