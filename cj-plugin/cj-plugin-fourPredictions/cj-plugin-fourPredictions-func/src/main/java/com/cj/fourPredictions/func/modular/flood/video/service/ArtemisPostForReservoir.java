package com.cj.fourPredictions.func.modular.flood.video.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cj.fourPredictions.func.modular.flood.video.bean.dto.GetRegionsDto;
import com.cj.fourPredictions.func.modular.flood.video.bean.dto.RegionIndexCodeDto;
import com.cj.fourPredictions.func.modular.flood.video.bean.vo.PtzVo;
import com.cj.fourPredictions.func.modular.flood.video.bean.vo.SelZoomVo;
import com.cj.fourPredictions.func.modular.flood.video.common.VideoURL;
import com.hikvision.artemis.sdk.ArtemisHttpUtil;
import com.hikvision.artemis.sdk.config.ArtemisConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 视频接入接口
 */
@Component
public class ArtemisPostForReservoir {

    @Value("${artemisConfig.tth.host}")
    private  String host;

    @Value("${artemisConfig.tth.appKey}")
    private  String appKey;

    @Value("${artemisConfig.tth.appSecret}")
    private  String appSecret;


    public GetRegionsDto get_regions(String pageNo, String pageSize) throws Exception {
        /**
         * https://ip:port/artemis/api/resource/v1/org/orgList
         * 通过查阅AI Cloud开放平台文档或网关门户的文档可以看到获取组织列表的接口定义,该接口为POST请求的Rest接口, 入参为JSON字符串，接口协议为https。
         * ArtemisHttpUtil工具类提供了doPostStringArtemis调用POST请求的方法，入参可传JSON字符串, 请阅读开发指南了解方法入参，没有的参数可传null
         */
        ArtemisConfig config = new ArtemisConfig();
        config.setHost(host); // 代理API网关nginx服务器ip端口
        config.setAppKey(appKey);  // 秘钥appkey
        config.setAppSecret(appSecret);// 秘钥appSecret
        final String getCamsApi = VideoURL.GET_REGIONS;
        Map<String, String> paramMap = new HashMap<String, String>();// post请求Form表单参数
        paramMap.put("pageNo", pageNo);
        paramMap.put("pageSize", pageSize);
        String body = JSON.toJSON(paramMap).toString();
        Map<String, String> path = new HashMap<String, String>(2) {
            {
                put("https://", getCamsApi);
            }
        };
        String s = ArtemisHttpUtil.doPostStringArtemis(config, path, body, null, null, "application/json");
        JSONObject jsonObject = JSONObject.parseObject(s);
        JSONObject data = jsonObject.getJSONObject("data");
        GetRegionsDto GetRegionsDto = JSONObject.parseObject(data.toJSONString(), GetRegionsDto.class);
        return GetRegionsDto;
    }

    public RegionIndexCodeDto region_index_code(String regionIndexCode, String pageNo, String pageSize) throws Exception {
        /**
         * https://ip:port/artemis/api/resource/v1/org/orgList
         * 通过查阅AI Cloud开放平台文档或网关门户的文档可以看到获取组织列表的接口定义,该接口为POST请求的Rest接口, 入参为JSON字符串，接口协议为https。
         * ArtemisHttpUtil工具类提供了doPostStringArtemis调用POST请求的方法，入参可传JSON字符串, 请阅读开发指南了解方法入参，没有的参数可传null
         */
        ArtemisConfig config = new ArtemisConfig();
        config.setHost(host); // 代理API网关nginx服务器ip端口
        config.setAppKey(appKey);  // 秘钥appkey
        config.setAppSecret(appSecret);// 秘钥appSecret
        final String getCamsApi = VideoURL.REGION_INDEX_CODE;
        Map<String, String> paramMap = new HashMap<String, String>();// post请求Form表单参数
        paramMap.put("pageNo", pageNo);
        paramMap.put("pageSize", pageSize);
        paramMap.put("regionIndexCode", regionIndexCode);
        String body = JSON.toJSON(paramMap).toString();
        Map<String, String> path = new HashMap<String, String>(2) {
            {
                put("https://", getCamsApi);
            }
        };
        String s = ArtemisHttpUtil.doPostStringArtemis(config, path, body, null, null, "application/json");
        JSONObject jsonObject = JSONObject.parseObject(s);
        JSONObject data = jsonObject.getJSONObject("data");
        RegionIndexCodeDto regionIndexCodeDto = JSONObject.parseObject(data.toJSONString(), RegionIndexCodeDto.class);
        return regionIndexCodeDto;
    }

    public  String get_preview_url(String cameraIndexCode) throws Exception {
        /**
         * https://ip:port/artemis/api/resource/v1/org/orgList
         * 通过查阅AI Cloud开放平台文档或网关门户的文档可以看到获取组织列表的接口定义,该接口为POST请求的Rest接口, 入参为JSON字符串，接口协议为https。
         * ArtemisHttpUtil工具类提供了doPostStringArtemis调用POST请求的方法，入参可传JSON字符串, 请阅读开发指南了解方法入参，没有的参数可传null
         */
        ArtemisConfig config = new ArtemisConfig();
        config.setHost(host); // 代理API网关nginx服务器ip端口
        config.setAppKey(appKey);  // 秘钥appkey
        config.setAppSecret(appSecret);// 秘钥appSecret
        final String getCamsApi = VideoURL.GET_PREVIEW_URL;
        Map<String, String> paramMap = new HashMap<String, String>();// post请求Form表单参数
        paramMap.put("cameraIndexCode", cameraIndexCode);
        paramMap.put("streamType", "0");
        paramMap.put("protocol", "ws");
        paramMap.put("transmode", "0");
        String body = JSON.toJSON(paramMap).toString();
        Map<String, String> path = new HashMap<String, String>(2) {
            {
                put("https://", getCamsApi);
            }
        };
        return ArtemisHttpUtil.doPostStringArtemis(config,path, body, null, null, "application/json");
    }

    public  String get_cameras(String pageNo,String pageSize) throws Exception {
        /**
         * https://ip:port/artemis/api/resource/v1/org/orgList
         * 通过查阅AI Cloud开放平台文档或网关门户的文档可以看到获取组织列表的接口定义,该接口为POST请求的Rest接口, 入参为JSON字符串，接口协议为https。
         * ArtemisHttpUtil工具类提供了doPostStringArtemis调用POST请求的方法，入参可传JSON字符串, 请阅读开发指南了解方法入参，没有的参数可传null
         */
        ArtemisConfig config = new ArtemisConfig();
        config.setHost(host); // 代理API网关nginx服务器ip端口
        config.setAppKey(appKey);  // 秘钥appkey
        config.setAppSecret(appSecret);// 秘钥appSecret
        final String getCamsApi = VideoURL.GET_PREVIEW_URL;
        Map<String, String> paramMap = new HashMap<String, String>();// post请求Form表单参数
        paramMap.put("pageNo", pageNo);
        paramMap.put("pageSize", pageSize);
        paramMap.put("streamType", "0");
        paramMap.put("protocol", "ws");
        paramMap.put("transmode", "0");
        String body = JSON.toJSON(paramMap).toString();
        Map<String, String> path = new HashMap<String, String>(2) {
            {
                put("https://", getCamsApi);
            }
        };
        return ArtemisHttpUtil.doPostStringArtemis(config,path, body, null, null, "application/json");
    }

    public String ptz(PtzVo vo) throws Exception {
        ArtemisConfig config = new ArtemisConfig();
        config.setHost(host); // 代理API网关nginx服务器ip端口
        config.setAppKey(appKey);  // 秘钥appkey
        config.setAppSecret(appSecret);// 秘钥appSecret
        final String getCamsApi = VideoURL.PTZ;
        Map<String, Object> paramMap = new HashMap<String, Object>();// post请求Form表单参数
        paramMap.put("cameraIndexCode", vo.getCameraIndexCode());
        paramMap.put("action", vo.getAction());
        paramMap.put("command", vo.getCommand());
        paramMap.put("speed", vo.getSpeed());
        paramMap.put("presetIndex", vo.getPresetIndex());
        String body = JSON.toJSON(paramMap).toString();
        Map<String, String> path = new HashMap<String, String>(2) {
            {
                put("https://", getCamsApi);
            }
        };
        return ArtemisHttpUtil.doPostStringArtemis(config,path, body, null, null, "application/json");
    }

    public String selZoom(SelZoomVo vo) throws Exception {
        ArtemisConfig config = new ArtemisConfig();
        config.setHost(host); // 代理API网关nginx服务器ip端口
        config.setAppKey(appKey);  // 秘钥appkey
        config.setAppSecret(appSecret);// 秘钥appSecret
        final String getCamsApi = VideoURL.SEL_ZOOM;
        Map<String, Object> paramMap = new HashMap<String, Object>();// post请求Form表单参数
        paramMap.put("cameraIndexCode", vo.getCameraIndexCode());
        paramMap.put("startX", vo.getStartX());
        paramMap.put("startY", vo.getStartY());
        paramMap.put("endX", vo.getEndX());
        paramMap.put("endY", vo.getEndY());
        String body = JSON.toJSON(paramMap).toString();
        Map<String, String> path = new HashMap<String, String>(2) {
            {
                put("https://", getCamsApi);
            }
        };
        return ArtemisHttpUtil.doPostStringArtemis(config,path, body, null, null, "application/json");
    }


}
