package com.cj.fourPredictions.func.modular.flood.weather.controller;

import com.alibaba.fastjson.JSONObject;
import com.cj.business.log.modular.log.annotation.CommonLog;
import com.cj.common.model.RestResponse;
import com.cj.common.util.RestTemplateUtil;
import com.cj.fourPredictions.func.modular.flood.weather.bean.vo.ForecastVO;
import com.cj.fourPredictions.func.modular.flood.weather.service.ForecastService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Api(tags = "天气预报")
@Slf4j
@RestController
@RequestMapping("/weather")
public class ForecastController {
    @Resource
    private ForecastService forecastService;

    @ApiOperation(value = "天气预报获取天气预报二十四小时", notes = "获取天气预报二十四小时")
    @CommonLog(value = "天气预报获取天气预报二十四小时")
    @GetMapping(value = "/getForecast")
    public RestResponse<List<ForecastVO>> getForecast() {
        try {
            List<ForecastVO> forecastVOS = new ArrayList<>();
            if(hasInternetAccess()){
                forecastVOS = forecastService.getForecast();
            }else {
                String url = "http://10.65.8.168:10188/web/weather/getForecast";
                HttpClient client = HttpClients.createDefault();
                HttpGet httpGet = new HttpGet(url);
                HttpResponse res = client.execute(httpGet);
                String jsonObject = EntityUtils.toString(res.getEntity());
                log.info("stringResponseEntity:"+jsonObject);
                forecastVOS = JSONObject.parseArray(jsonObject, ForecastVO.class);
            }
            return RestResponse.ok(forecastVOS);
        } catch (Exception e) {
            e.printStackTrace();
            return RestResponse.no("错误");
        }
    }

    private static boolean hasInternetAccess() {
        try {
            // 创建URL对象
            URL url = new URL("http://www.baidu.com");
            // 打开连接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // 设置请求方式，默认是GET
            connection.setRequestMethod("GET");
            // 设置连接超时和读取超时
            connection.setConnectTimeout(1000);
            connection.setReadTimeout(1000);
            // 发起请求
            connection.connect();
            // 判断请求是否成功（状态码200表示成功）
            return (connection.getResponseCode() == 200);
        } catch (Exception e) {
            // 发生异常，视为无外网
            return false;
        }
    }
}
