package com.cj.project.modular.fiducial.provider;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.cj.project.api.FiducialApi;
import com.cj.project.modular.fiducial.entity.FiducialBase;
import com.cj.project.modular.fiducial.param.FiducialAddParam;
import com.cj.project.modular.fiducial.service.FiducialBaseService;
import com.cj.project.modular.fiducial.service.FiducialService;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FiducialApiProvider implements FiducialApi {

    @Resource
    private FiducialBaseService fiducialBaseService;

    @Resource
    private FiducialService fiducialService;


    @Override
    public List<JSONObject> getBatch(String projectCode, String instrumentStr) {
        return fiducialBaseService.getBatch(projectCode,instrumentStr)
                .stream().map(JSONUtil::parseObj).collect(Collectors.toList());
    }

    @Override
    public void addsByMap(List<Map<String, Object>>  fiducialAddList) {
        fiducialService.addsByMap(fiducialAddList);
    }
}
