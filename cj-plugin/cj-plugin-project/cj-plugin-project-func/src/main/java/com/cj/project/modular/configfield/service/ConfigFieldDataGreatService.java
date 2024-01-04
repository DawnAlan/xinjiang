package com.cj.project.modular.configfield.service;

/**
 * 数据字段批量配置生成Service接口
 *
 * @author Lb
 * @date  2023/11/08 17:28
 **/
public interface ConfigFieldDataGreatService {

    //创建默认数据字段配置
    void Create(String projectCode,String instrumentMetaType, String instrumentType);

    //修改数据字段是否显示
    void UpdateFieldDisplay(String projectCode,String instrumentType, String[] Fields,String isDisplay);
}
