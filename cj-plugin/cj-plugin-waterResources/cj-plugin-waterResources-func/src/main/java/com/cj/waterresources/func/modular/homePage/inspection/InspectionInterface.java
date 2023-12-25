package com.cj.waterresources.func.modular.homePage.inspection;

import com.cj.common.util.RedisUtil;
import com.cj.common.util.RestTemplateUtil;
import com.cj.waterresources.func.modular.homePage.inspection.response.AbnormalDetailRes;
import com.cj.waterresources.func.modular.homePage.inspection.response.AbnormalListRes;
import com.cj.waterresources.func.modular.homePage.inspection.response.InspectionDetailRes;
import com.cj.waterresources.func.modular.homePage.inspection.response.InspectionListRes;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class InspectionInterface {
    private final RedisUtil redisUtil;

    //域名
    private static final String domainPath = "";
    //获取token地址
    private static final String getTokenPath = domainPath + "/sys/getToken";
    private static final String tokenRedisKey = "inspectionInterfaceQueryToken";
    //密码
    private static final String password = "@izDrAVxr%#7giO0@z";
    //巡查事件列表
    private static final String inspectionListPath = domainPath + "/patrol/bisPatrolKjgs/inspectionList";
    //巡查事件详情
    private static final String inspectionDetailPath = domainPath + "/patrol/bisPatrolKjgs/queryInspectionById";
    //异常情况列表
    private static final String abnormalListPath = domainPath + "/patrol/bisPatrolKjgs/abnormalList";
    //异常情况详情
    private static final String abnormalDetailPath = domainPath + "/patrol/bisPatrolKjgs/queryAbnormalById";

    private String getToken() {
        Object o = redisUtil.get(tokenRedisKey);
        if (null != o && o.toString() != "") {
            return o.toString();
        }
        ResponseEntity<Map> response = RestTemplateUtil.get(getTokenPath, Map.class, new HashMap<String, String>() {{
            put("password", password);
        }});
        String token = response.getBody().get("result").toString();
        redisUtil.set(tokenRedisKey, token, 60 * 29);
        return token;
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
}
