package com.cj.project.api;

import cn.hutool.json.JSONObject;

import java.util.List;
import java.util.Map;

public interface FiducialApi {

    List<JSONObject> getBatch(String projectCode, String instrumentStr);


    void addsByMap(List<Map<String, Object>>  fiducialAddList);
}
