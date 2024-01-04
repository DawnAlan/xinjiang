package com.cj.project.modular.configfield.service;


import com.cj.project.modular.configfield.param.ConfigFieldFiducialAddParam;

import java.util.Map;

/**
 * 考证字段批量配置生成Service接口
 *
 * @author Lb
 * @date  2023/08/31 19:28
 **/
public interface ConfigFieldFiducialGreatService {

    //创建默认考证字段配置
    void Create(String projectCode,String instrumentMetaType, String instrumentType);

    //修改考证字段是否显示
    void UpdateFieldDisplay(String projectCode,String instrumentType, String[] Fields,String isDisplay);

    /**
     * 批量添加某字段配置
     * 需字段已经加到FiducialBase.class中
     * @author : lb
     * @date : 2023/12/02 12:32
    */
    void BatchAddField(String projectCode, String instrumentType, String[] Fields);
}
