package com.cj.project.modular.FiducialEnterA.service;


import com.cj.project.modular.FiducialEnterA.result.PointEnterResult;

import java.util.List;
import java.util.Map;

public interface FiducialEnterAService {
    
    /**
     * 更新项目仪器测点考证
     * 
     * @author : lb
     * @date : 2023/11/20 15:07
    */
    List<Map<String, Object>> EnterPointFiducial(String projectCode, String instrument, String isCover);

}
