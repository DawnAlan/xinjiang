package com.cj.dataSynchronization.func.modular.tth;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cj.common.util.HttpRequestUtil;
import com.cj.common.util.RedisUtil;
import com.cj.common.util.RestTemplateUtil;
import com.cj.dataSynchronization.func.modular.tth.dtos.*;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

@Slf4j
@Component
public class IrrigatedAreaInvoke {

    @Getter
    private static String ip;
    @Getter
    private static String ip1;
    @Getter
    private static String cmd;

    @Value("${irrigatedArea.ip}")
    public void setIp(String ip) {
        IrrigatedAreaInvoke.ip = ip;
    }

    @Value("${irrigatedArea.ip1}")
    public void setIp1(String ip1) {
        IrrigatedAreaInvoke.ip1 = ip1;
    }

    @Value("${irrigatedArea.cmd}")
    public void setCmd(String cmd) {
        IrrigatedAreaInvoke.cmd = cmd;
    }

    private static RedisUtil redisUtil;

    private static IrrigatedAreaInvoke irrigatedAreaInvoke;

    @Autowired
    private RedisUtil redisUtilTemp;

    @PostConstruct
    public void init() {
        irrigatedAreaInvoke = this;
        irrigatedAreaInvoke.redisUtil = this.redisUtilTemp;
    }

    private final static List<String> COOKIE_PART = Arrays.asList("00MainProject=", "serverAddress=", "webMenuTheme=", "webMenuThemeConfig=");
    private final static String USERNAME = "admin";
    private final static String PASSWORD = "tthlyglj@123";

    public static String getToken(){
        try {
            String url = ip+ IrrigatedAreaURL.GET_TOKEN;
            String post = RestTemplateUtil.post(url, "", "");
            JSONObject jsonObject1 = JSONObject.parseObject(post);
            JSONArray result = jsonObject1.getJSONArray("result");
            String string = result.get(0).toString();
            JSONObject jsonObject2 = JSONObject.parseObject(string);
            String token = jsonObject2.getString("token");
            return token;
        }catch(Exception e){
            e.printStackTrace();
            return "";
        }
    }

    public static List<QueryMonitorBasicDto> getQueryMonitorBasic(){
        try {
            String url = ip+ IrrigatedAreaURL.QUERY_MONITOR_BASIC;
            JSONObject jsonObject = new JSONObject();
            JSONObject jsonObjectParam = new JSONObject();
            jsonObjectParam.put("REGION_ID","2-310-017");
            jsonObjectParam.put("MONITOR_WAY","03");
            jsonObject.put("token","holdetime");
            jsonObject.put("EntityData", Arrays.asList(jsonObjectParam));
            String token = getToken();
            String post = RestTemplateUtil.post(url, jsonObject.toJSONString(), token);
            JSONObject jsonObject1 = JSONObject.parseObject(post);
            String result = jsonObject1.getJSONArray("result").toJSONString();
            List<QueryMonitorBasicDto> queryMonitorBasicDtos = JSONObject.parseArray(result, QueryMonitorBasicDto.class);
            if(null != queryMonitorBasicDtos && queryMonitorBasicDtos.size()>0){
                return queryMonitorBasicDtos;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static List<QueryRealTimeDataDto> getQueryRealTimeData(String MONITOR_ID){
        try {
            String url = ip+ IrrigatedAreaURL.QUERY_REAL_TIME_DATA;
            JSONObject jsonObject = new JSONObject();
            JSONObject jsonObjectParam = new JSONObject();
            jsonObjectParam.put("REGION_ID","2-310-017");
            jsonObjectParam.put("MONITOR_WAY","02");
            jsonObjectParam.put("MONITOR_ID","'"+MONITOR_ID+"'");
            jsonObject.put("token","holdetime");
            jsonObject.put("EntityData", Arrays.asList(jsonObjectParam));
            String token = getToken();
            //log.info("入参:"+jsonObject.toJSONString());
            String post = RestTemplateUtil.post(url, jsonObject.toJSONString(), token);
            JSONObject jsonObject1 = JSONObject.parseObject(post);
            String result = jsonObject1.getJSONArray("result").toJSONString();
            List<QueryRealTimeDataDto> queryRealTimeDataDtoList = JSONObject.parseArray(result, QueryRealTimeDataDto.class);
            if(null != queryRealTimeDataDtoList && queryRealTimeDataDtoList.size()>0){
                return queryRealTimeDataDtoList;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static List<HistoryDataVo> getHistoryDataForWater(String MONITOR_ID,String startTime,String endTime){
        try {
            String url = ip+ IrrigatedAreaURL.GET_HISTORY_DATA;
            JSONObject jsonObject = new JSONObject();
            JSONObject jsonObjectParam = new JSONObject();
            List<JSONObject> terms = new ArrayList<>();
            JSONObject q1 = new JSONObject();
            q1.put("Value",startTime);//开始时间
            q1.put("Symbol",">=");
            q1.put("Column","T.MONITOR_TIME");
            terms.add(q1);
            JSONObject q2 = new JSONObject();
            q2.put("Value",endTime);//开始时间
            q2.put("Symbol","<=");
            q2.put("Column","T.MONITOR_TIME");
            terms.add(q2);
            JSONObject q3 = new JSONObject();
            q3.put("Value","2-310-017");//开始时间
            q3.put("Symbol","=");
            q3.put("Column","T.REGION_ID");
            terms.add(q3);
            JSONObject q4 = new JSONObject();
            q4.put("Value",MONITOR_ID);//开始时间
            q4.put("Symbol","=");
            q4.put("Column","T.MONITOR_ID");
            terms.add(q4);
            JSONObject q5 = new JSONObject();
            q5.put("Value","0");//开始时间
            q5.put("Symbol","=");
            q5.put("Column","T.IS_SURPASS");
            terms.add(q5);
            jsonObjectParam.put("TermsQ",terms);
            jsonObjectParam.put("Table","IA_TD_WATER_DATA");
            jsonObject.put("token","holdetime");
            jsonObject.put("EntityData", Arrays.asList(jsonObjectParam));
            String token = getToken();
            String post = RestTemplateUtil.post(url, jsonObject.toJSONString(), token);
            JSONObject jsonObject1 = JSONObject.parseObject(post);
            String result = jsonObject1.getJSONArray("result").toJSONString();
            List<HistoryDataVo> queryRealTimeDataDtoList = JSONObject.parseArray(result, HistoryDataVo.class);
            if(null != queryRealTimeDataDtoList && queryRealTimeDataDtoList.size()>0){
                return queryRealTimeDataDtoList;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static List<HistoryDataVo> getHistoryDataForRain(String MONITOR_ID,String startTime,String endTime){
        try {
            String url = ip+ IrrigatedAreaURL.GET_HISTORY_DATA;
            JSONObject jsonObject = new JSONObject();
            JSONObject jsonObjectParam = new JSONObject();
            List<JSONObject> terms = new ArrayList<>();
            JSONObject q1 = new JSONObject();
            q1.put("Value",startTime);//开始时间
            q1.put("Symbol",">=");
            q1.put("Column","T.MONITOR_TIME");
            terms.add(q1);
            JSONObject q2 = new JSONObject();
            q2.put("Value",endTime);//开始时间
            q2.put("Symbol","<=");
            q2.put("Column","T.MONITOR_TIME");
            terms.add(q2);
            JSONObject q3 = new JSONObject();
            q3.put("Value","2-310-017");//开始时间
            q3.put("Symbol","=");
            q3.put("Column","T.REGION_ID");
            terms.add(q3);
            JSONObject q4 = new JSONObject();
            q4.put("Value",MONITOR_ID);//开始时间
            q4.put("Symbol","=");
            q4.put("Column","T.MONITOR_ID");
            terms.add(q4);
            jsonObjectParam.put("TermsQ",terms);
            jsonObjectParam.put("Table","IA_TD_RAIN_DATA");
            jsonObject.put("token","holdetime");
            jsonObject.put("EntityData", Arrays.asList(jsonObjectParam));
            String token = getToken();
            String post = RestTemplateUtil.post(url, jsonObject.toJSONString(), token);
            JSONObject jsonObject1 = JSONObject.parseObject(post);
            String result = jsonObject1.getJSONArray("result").toJSONString();
            List<HistoryDataVo> queryRealTimeDataDtoList = JSONObject.parseArray(result, HistoryDataVo.class);
            if(null != queryRealTimeDataDtoList && queryRealTimeDataDtoList.size()>0){
                return queryRealTimeDataDtoList;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static List<HistoryDataVo> getHistoryDataForPipeLine(String MONITOR_ID,String startTime,String endTime){
        try {
            String url = ip+ IrrigatedAreaURL.GET_HISTORY_DATA;
            JSONObject jsonObject = new JSONObject();
            JSONObject jsonObjectParam = new JSONObject();
            List<JSONObject> terms = new ArrayList<>();
            JSONObject q1 = new JSONObject();
            q1.put("Value",startTime);//开始时间
            q1.put("Symbol",">=");
            q1.put("Column","T.MONITOR_TIME");
            terms.add(q1);
            JSONObject q2 = new JSONObject();
            q2.put("Value",endTime);//开始时间
            q2.put("Symbol","<=");
            q2.put("Column","T.MONITOR_TIME");
            terms.add(q2);
            JSONObject q3 = new JSONObject();
            q3.put("Value","2-310-017");//开始时间
            q3.put("Symbol","=");
            q3.put("Column","T.REGION_ID");
            terms.add(q3);
            JSONObject q4 = new JSONObject();
            q4.put("Value",MONITOR_ID);//开始时间
            q4.put("Symbol","=");
            q4.put("Column","T.MONITOR_ID");
            terms.add(q4);
            jsonObjectParam.put("TermsQ",terms);
            jsonObjectParam.put("Table","IA_TD_PIPELINE_DATA");
            jsonObject.put("token","holdetime");
            jsonObject.put("EntityData", Arrays.asList(jsonObjectParam));
            String token = getToken();
            String post = RestTemplateUtil.post(url, jsonObject.toJSONString(), token);
            JSONObject jsonObject1 = JSONObject.parseObject(post);
            String result = jsonObject1.getJSONArray("result").toJSONString();
            List<HistoryDataVo> queryRealTimeDataDtoList = JSONObject.parseArray(result, HistoryDataVo.class);
            if(null != queryRealTimeDataDtoList && queryRealTimeDataDtoList.size()>0){
                return queryRealTimeDataDtoList;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static void login() {
        String loadPublicKey = ip1+IrrigatedAreaURL.LOAD_PUBLIC_KEY;
        String loginVerify = ip1+IrrigatedAreaURL.LOGIN_VERIFY;
        log.info("=============================================login start===============================================");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        log.info("============================================loadPublicKey:"+loadPublicKey);
        ResponseEntity<Map> mapResponseEntity = RestTemplateUtil.post(loadPublicKey, new HttpEntity<>(null, headers), Map.class);
        StringBuilder sb = new StringBuilder();
        List<String> cookies = mapResponseEntity.getHeaders().get("set-cookie");
        for (int i = 0; i < COOKIE_PART.size(); i++) {
            for (String cookie : cookies) {
                if (cookie.contains(COOKIE_PART.get(i))) {
                    sb.append(cookie.split(";")[0]);
                    break;
                }
            }
            sb.append("; ");
        }
        String loginCookie = sb.substring(0, sb.length() - 2);
        String publicKey = mapResponseEntity.getBody().get("result").toString();
        String[] encryptStr = execShell(USERNAME, PASSWORD, publicKey).split(",");
        log.info("============================================loginVerify:"+loginVerify);
        ResponseEntity<Map> mapResponseEntityLogin = RestTemplateUtil.post(loginVerify, setParams(encryptStr, loginCookie), Map.class);
        log.info(mapResponseEntityLogin.getBody().toString());
        redisUtil.set("tth:cookie", loginCookie);
    }

    private static HttpEntity<MultiValueMap<String, Object>> setParams(String[] encryptStr, String loginCookie) {
        MultiValueMap<String, Object> postParameters = new LinkedMultiValueMap<>();
        postParameters.add("name", encryptStr[0]);
        postParameters.add("kv1", encryptStr[1]);
        postParameters.add("publicKey", null);
        postParameters.add("frameType", "01");
        postParameters.add("title", "智慧灌区e平台");
        HttpHeaders loginHeaders = new HttpHeaders();
        loginHeaders.add("Accept", "application/json, text/javascript, */*; q=0.01");
        loginHeaders.add("Accept-Encoding", "gzip, deflate");
        loginHeaders.add("Accept-Language", "zh-CN,zh;q=0.9");
        loginHeaders.add("Content-Length", "558");
        loginHeaders.add("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        loginHeaders.add("Cookie", loginCookie);
        return new HttpEntity<>(postParameters, loginHeaders);
    }
    @SneakyThrows
    private static String execShell(String... args) {
        String line, result = null;
        String command = "node "+cmd+" "+ String.join(" ", args);
        Process p = Runtime.getRuntime().exec(command);
        BufferedReader stdout = new BufferedReader(new InputStreamReader(p.getInputStream()));
        while ((line = stdout.readLine()) != null) {
            result = line;
        }
        stdout.close();
        log.info("------------------------------------------------------------login:result: " + result);
        return result;
    }

    public static List<AllTreeDto> getAllTree(){
        try {
            String cookie = "";
            String post = "";
            JSONArray jsonObject1 = null;
            cookie = (String)redisUtil.get("tth:cookie");
            if(StringUtils.isEmpty(cookie)){
                login();
                cookie = (String)redisUtil.get("tth:cookie");
            }
            String url = ip1+IrrigatedAreaURL.GET_ALL_TREE;
            MultiValueMap<String, Object> postParameters = new LinkedMultiValueMap<>();
            post = RestTemplateUtil.post2(url, postParameters, cookie);
            try {
                jsonObject1 = JSONObject.parseArray(post);
            }catch (Exception e){
                log.error("------------------------------------------查询灌区平台树列表异常信息cookie失效了:"+e.getMessage());
                login();
                cookie = (String)redisUtil.get("tth:cookie");
                post = RestTemplateUtil.post2(url, postParameters, cookie);
                jsonObject1 = JSONObject.parseArray(post);
            }
            List<AllTreeDto> allTreeDtoList = JSONObject.parseArray(jsonObject1.toJSONString(), AllTreeDto.class);
            return allTreeDtoList;
        }catch (Exception e){
            e.printStackTrace();
            log.error("查询灌区平台树列表异常信息："+e.getMessage());
        }
        return null;
    }

    public static List<AllHistoryDataDto> getAllHistoryData(String id, String date,String dataInfo,String MONITOR_TYPE){
        try {
            String cookie = "";
            String post = "";
            JSONObject jsonObject1 = new JSONObject();
            Map<String, String> head = new HashMap<>();
            cookie = (String)redisUtil.get("tth:cookie");
            Map<String, String> request = new HashMap<>();
            if(StringUtils.isEmpty(cookie)){
                login();
                cookie = (String)redisUtil.get("tth:cookie");
            }
            String url = ip1+IrrigatedAreaURL.GET_ALL_HISTORY_DATA;
            head.put("Cookie",cookie);
            request.put("TABLE","IA_TD_WATER_DATA");
            request.put("ID",id);
            request.put("BEGIN_TIME_MARK","02");
            request.put("MONITOR_TYPE",MONITOR_TYPE);
            request.put("MONITOR_DATE",date);
            request.put("BEGIN_TIME",dataInfo);
            request.put("REGION_ID","2-310-017");
            request.put("pageSize","10000");
            request.put("pageNum","1");
            post = HttpRequestUtil.post(request, url, head, "application/x-www-form-urlencoded");
            log.info("调用灌区平台查询数据列表的url:"+url);
            log.info("调用灌区平台查询数据列表的data:"+request);
            //log.info("灌区平台的数据返回数据："+post);
            Integer total = (Integer) JSONObject.parseObject(post).get("total");
            if(total == 0){
                System.out.println(post);
            }
            try {
                jsonObject1 = JSONObject.parseObject(post);
            }catch (Exception e){
                log.error("------------------------------------------查询列表异常信息cookie失效了:"+e.getMessage());
                login();
                cookie = (String)redisUtil.get("tth:cookie");
                head.put("Cookie",cookie);
                post = HttpRequestUtil.post(request, url, head, "application/x-www-form-urlencoded");
                jsonObject1 = JSONObject.parseObject(post);
            }
            JSONArray result = jsonObject1.getJSONArray("result");
            List<AllHistoryDataDto> allHistoryDataDtos = JSONObject.parseArray(result.toString(), AllHistoryDataDto.class);
            if(null != allHistoryDataDtos && allHistoryDataDtos.size() > 0 ){
                return allHistoryDataDtos;
            }
        }catch (Exception e){
            e.printStackTrace();
            log.error("查询数据列表异常信息:"+e.getMessage());
        }
        return null;
    }

}
