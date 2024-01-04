package com.cj.project.modular.configfield.service;

import com.cj.project.modular.configfield.entity.ConfigFieldData;
import com.cj.project.modular.configfield.param.*;
import com.cj.project.modular.configfield.result.ConfigFieldDataResult;

import java.util.List;

public interface ConfigFieldDataService {

    //获取数据字段配置
    List<ConfigFieldDataResult> getList(ConfigFieldQueryParam configFieldQueryParam);

    /**
     * 添加数据字段配置
     *
     * @author Lb
     * @date  2023/11/08 15:28
     */
    void add(ConfigFieldDataAddParam configFieldDataAddParam);

    /**
     * 编辑数据字段配置
     *
     * @author Lb
     * @date  2023/11/08 15:28
     */
    void edit(ConfigFieldDataEditParam configFieldDataEditParam);

    /**
     * 删除数据字段配置
     *
     * @author Lb
     * @date  2023/11/08 15:28
     */
    void delete(List<ConfigFieldIdParam> configFieldIdParamList);

    /**
     * 获取数据字段配置详情
     *
     * @author Lb
     * @date  2023/11/08 15:28
     **/
    ConfigFieldData detail(ConfigFieldIdParam configFieldIdParam);

    /**
     * 获取数据字段配置详情
     *
     * @author Lb
     * @date  2023/11/08 15:28
     **/
    ConfigFieldData queryEntity(String id);
}
