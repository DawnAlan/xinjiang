package com.cj.waterresources.func.core.utils;

import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson.JSONObject;
import com.cj.common.util.RestTemplateUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.InetAddress;

@Component
@Slf4j
public class GetSzyDataUtils {

    @SneakyThrows
    public static Double getWaterLevelByFlow(Double flow, String id){
        String token = StpUtil.getTokenValue();
        InetAddress localHost = InetAddress.getLocalHost();
        String url = "http://" + localHost.getHostAddress() +":9003/toutunhe/wpdCurved/queryLevelFlow?ndcdId="+id+"&flowRate="+flow;
        String s = RestTemplateUtil.getBySaToken(url,token);
        BigDecimal value = (BigDecimal) JSONObject.parseObject(s).get("data");
        return value==null?0.00:value.doubleValue();
    }

    @SneakyThrows
    public static Double getWaterLevelByLevel(Double level, String id){
        String token = StpUtil.getTokenValue();
        InetAddress localHost = InetAddress.getLocalHost();
        //String hostAddress = "192.168.31.154";
        String hostAddress = localHost.getHostAddress();
        String url = "http://" + hostAddress +":9003/toutunhe/wpdCurved/queryLevelFlow?ndcdId="+id+"&level="+level;
        String s = RestTemplateUtil.getBySaToken(url,token);
        BigDecimal value = (BigDecimal) JSONObject.parseObject(s).get("data");
        return value==null?0.00:value.doubleValue();
    }
}
