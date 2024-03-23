package com.cj.waterresources.func.modular.homePage.inspection;

import cn.hutool.json.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cj.common.util.RedisUtil;
import com.cj.common.util.RestTemplateUtil;
import com.cj.waterresources.func.modular.homePage.inspection.response.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class InspectionInterface {
    private final RedisUtil redisUtil;

    //域名
    private static final String domainPath = "http://10.65.8.168:10188/app";
    //获取token地址
    private static final String getTokenPath = domainPath + "/sys/getToken";
    private static final String tokenRedisKey = "inspectionInterfaceQueryToken";
    //密码
    private static final String password = "izDrAVxr7giz";//@izDrAVxr%#7giO0@z
    //巡查事件列表
    private static final String inspectionListPath = domainPath + "/patrol/bisPatrolKjgs/inspectionList";
    //巡查事件详情
    private static final String inspectionDetailPath = domainPath + "/patrol/bisPatrolKjgs/queryInspectionById";
    //异常情况列表
    private static final String abnormalListPath = domainPath + "/patrol/bisPatrolKjgs/abnormalList";
    //异常情况详情
    private static final String abnormalDetailPath = domainPath + "/patrol/bisPatrolKjgs/queryAbnormalById";

    //private final String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE3MDUxNDc5MTIsInVzZXJuYW1lIjoia2pncyJ9.-BQgXhYiZzE6Uvss-qNMI6xYJEpq4D2WaAU781ZoG9I";

    private String getToken() {
        String redisToken = (String)redisUtil.get(tokenRedisKey);
        if(StringUtils.isEmpty(redisToken)) {
            String data = RestTemplateUtil.getToken(getTokenPath + "?password="+password,null,null);
            String token = JSONObject.parseObject(data).getString("result");
            redisUtil.set(tokenRedisKey, token,30*60);
            redisToken = token;
        }
        return redisToken;
    }

    private HttpHeaders getHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Tenant_id", "2");
        headers.add("X-Access-Token", getToken());
        return headers;
    }

    public List<InspectionListRes> getInspectionList() {
        ResponseEntity<Map> response = RestTemplateUtil.get(inspectionListPath, getHeader(), Map.class);
        return (List<InspectionListRes>) ((Map) response.getBody().get("result")).get("records");
    }

    public InspectionDetailRes getInspectionDetail(String id) {
        ResponseEntity<Map> response = RestTemplateUtil.get(inspectionDetailPath, getHeader(), Map.class, new HashMap<String, String>() {{
            put("id", id);
        }});
        return (InspectionDetailRes) response.getBody().get("result");
    }

    public List<AbnormalListRes> getAbnormalList() {
        ResponseEntity<Map> response = RestTemplateUtil.get(abnormalListPath, getHeader(), Map.class);
        return (List<AbnormalListRes>) ((Map) response.getBody().get("result")).get("records");
    }

    public AbnormalDetailRes getAbnormalDetail(String id) {
        ResponseEntity<Map> response = RestTemplateUtil.get(abnormalDetailPath, getHeader(), Map.class, new HashMap<String, String>() {{
            put("id", id);
        }});
        return (AbnormalDetailRes) response.getBody().get("result");
    }

    public List<AbnormalRes> getAbnormalList1() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String format = sdf.format(new Date());
        String stringResponseEntity = RestTemplateUtil.get(abnormalListPath, getToken());
        JSONArray result = JSONObject.parseObject(stringResponseEntity).getJSONArray("result");
        List<AbnormalRes> abnormalRes = JSONObject.parseArray(result.toString(), AbnormalRes.class);
        if(null != abnormalRes && abnormalRes.size()>0){
            List<AbnormalRes> collect = abnormalRes.stream().filter(t -> t.getCreateTime().split(" ")[0].equals(format)).collect(Collectors.toList());
            return collect;
        }else {
            return null;
        }
    }

    public List<InspectionRes> getInspectionList1() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String format = sdf.format(new Date());
        String stringResponseEntity = RestTemplateUtil.get(inspectionListPath, getToken());
        JSONArray result = JSONObject.parseObject(stringResponseEntity).getJSONObject("result").getJSONArray("records");
        List<InspectionRes> inspectionRes = JSONObject.parseArray(result.toString(), InspectionRes.class);
        if(null != inspectionRes && inspectionRes.size()>0){
            List<InspectionRes> collect = inspectionRes.stream().filter(t -> t.getCreateTime().split(" ")[0].equals(format)).collect(Collectors.toList());
            return collect;
        }else {
            return null;
        }
    }
}
