package com.cj.project.api;

import cn.hutool.json.JSONObject;

import java.util.List;

public interface ProjectInstrumentsApi {


    List<JSONObject> getList(String projectCode, String monitorName, String instrumentType, String instrumentMetaType);
}
