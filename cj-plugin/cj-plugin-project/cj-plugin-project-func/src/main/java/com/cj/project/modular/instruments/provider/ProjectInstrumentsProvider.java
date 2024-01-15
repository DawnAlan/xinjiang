package com.cj.project.modular.instruments.provider;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.cj.project.api.ProjectInstrumentsApi;
import com.cj.project.modular.instruments.service.ProjectInstrumentsService;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

public class ProjectInstrumentsProvider implements ProjectInstrumentsApi {

    @Resource
    private ProjectInstrumentsService projectInstrumentsService;


    @Override
    public List<JSONObject> getList(String projectCode, String monitorName, String instrumentType, String instrumentMetaType) {
        return projectInstrumentsService.getList(projectCode,monitorName, instrumentType, instrumentMetaType)
                .stream().map(JSONUtil::parseObj).collect(Collectors.toList());
    }
}
