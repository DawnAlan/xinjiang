package com.cj.waterresources.func.modular.homePage.inspection;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cj.common.util.RestTemplateUtil;
import com.cj.waterresources.func.modular.homePage.inspection.response.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class InspectionInterface {

    @Value("${inspection.path}")
    private String path;


    //获取token地址
    private static final String getTokenPath = "/sys/getToken";
    //密码
    private static final String password = "izDrAVxr7giz";//@izDrAVxr%#7giO0@z
    //巡查事件列表
    private static final String inspectionListPath = "/patrol/bisPatrolKjgs/inspectionList";
    //巡查事件详情
    private static final String inspectionDetailPath = "/patrol/bisPatrolKjgs/queryInspectionById";
    //异常情况列表
    private static final String abnormalListPath = "/patrol/bisPatrolKjgs/abnormalList";
    //异常情况详情
    private static final String abnormalDetailPath ="/patrol/bisPatrolKjgs/queryAbnormalById";

    private String getToken() {
        String data = RestTemplateUtil.getToken(path+getTokenPath + "?password="+password,null,null);
        String token = JSONObject.parseObject(data).getString("result");
        return token;
    }

    private HttpHeaders getHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Tenant_id", "2");
        headers.add("X-Access-Token", getToken());
        return headers;
    }

    public List<InspectionListRes> getInspectionList() {
        ResponseEntity<Map> response = RestTemplateUtil.get(path+inspectionListPath, getHeader(), Map.class);
        return (List<InspectionListRes>) ((Map) response.getBody().get("result")).get("records");
    }

    public InspectionDetailRes getInspectionDetail(String id) {
        ResponseEntity<Map> response = RestTemplateUtil.get(path+inspectionDetailPath, getHeader(), Map.class, new HashMap<String, String>() {{
            put("id", id);
        }});
        return (InspectionDetailRes) response.getBody().get("result");
    }

    public List<AbnormalListRes> getAbnormalList() {
        ResponseEntity<Map> response = RestTemplateUtil.get(path+abnormalListPath, getHeader(), Map.class);
        return (List<AbnormalListRes>) ((Map) response.getBody().get("result")).get("records");
    }

    public AbnormalDetailRes getAbnormalDetail(String id) {
        ResponseEntity<Map> response = RestTemplateUtil.get(path+abnormalDetailPath, getHeader(), Map.class, new HashMap<String, String>() {{
            put("id", id);
        }});
        return (AbnormalDetailRes) response.getBody().get("result");
    }

    public List<AbnormalRes> getAbnormalList1(String time) {
        String stringResponseEntity = RestTemplateUtil.get(path+abnormalListPath, getToken());
        JSONArray result = JSONObject.parseObject(stringResponseEntity).getJSONArray("result");
        List<AbnormalRes> abnormalRes = JSONObject.parseArray(result.toString(), AbnormalRes.class);
        if(null != abnormalRes && abnormalRes.size()>0){
            List<AbnormalRes> collect = abnormalRes.stream().filter(t -> t.getCreateTime().split(" ")[0].equals(time)).collect(Collectors.toList());
            return collect;
        }else {
            return null;
        }
    }

    public List<InspectionRes> getInspectionList1(String time) {
        String stringResponseEntity = RestTemplateUtil.get(path+inspectionListPath, getToken());
        JSONArray result = JSONObject.parseObject(stringResponseEntity).getJSONObject("result").getJSONArray("records");
        List<InspectionRes> inspectionRes = JSONObject.parseArray(result.toString(), InspectionRes.class);
        if(null != inspectionRes && inspectionRes.size()>0){
            List<InspectionRes> collect = inspectionRes.stream().filter(t -> t.getCreateTime().split(" ")[0].equals(time)).collect(Collectors.toList());
            return collect;
        }else {
            return null;
        }
    }
}
