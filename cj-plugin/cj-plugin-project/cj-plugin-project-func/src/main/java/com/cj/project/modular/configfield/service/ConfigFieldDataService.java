package com.cj.project.modular.configfield.service;

import com.cj.project.api.configfield.dto.ConfigFieldQueryDto;
import com.cj.project.api.configfield.entity.ConfigFieldData;
import com.cj.project.modular.configfield.result.ConfigFieldDataResult;

import java.util.List;

public interface ConfigFieldDataService {

    //获取数据字段配置
    List<ConfigFieldDataResult> getList(ConfigFieldQueryDto configFieldQueryDto);

    /**
     * 添加数据字段配置
     *
     * @author Lb
     * @date  2023/11/08 15:28
     */
    void add(ConfigFieldData configFieldData);

    /**
     * 编辑数据字段配置
     *
     * @author Lb
     * @date  2023/11/08 15:28
     */
    void edit(ConfigFieldData configFieldData);

    /**
     * 删除数据字段配置
     *
     * @author Lb
     * @date  2023/11/08 15:28
     */
    void delete(List<String> idList);

    /**
     * 获取数据字段配置详情
     *
     * @author Lb
     * @date  2023/11/08 15:28
     **/
    ConfigFieldData detail(String id);

    /**
     * 获取数据字段配置详情
     *
     * @author Lb
     * @date  2023/11/08 15:28
     **/
    ConfigFieldData queryEntity(String id);
}
