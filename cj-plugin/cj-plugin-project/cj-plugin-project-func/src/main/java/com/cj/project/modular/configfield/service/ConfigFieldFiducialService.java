package com.cj.project.modular.configfield.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.project.modular.configfield.entity.ConfigFieldFiducial;
import com.cj.project.modular.configfield.param.*;
import com.cj.project.modular.configfield.result.ConfigFieldFiducialResult;

import java.util.List;

/**
 * 考证字段配置Service接口
 *
 * @author Lb
 * @date  2023/08/31 19:28
 **/
public interface ConfigFieldFiducialService extends IService<ConfigFieldFiducial> {

    /**
     * 查询仪器类型考证字段配置
     * @param configFieldFiducialQueryParam
     * @return
     */
    List<ConfigFieldFiducialResult> getList(ConfigFieldFiducialQueryParam configFieldFiducialQueryParam);

    /**
     * 获取考证字段配置分页
     *
     * @author Lb
     * @date  2023/08/31 19:28
     */
    Page<ConfigFieldFiducial> page(ConfigFieldFiducialPageParam configFieldFiducialPageParam);

    /**
     * 添加考证字段配置
     *
     * @author Lb
     * @date  2023/08/31 19:28
     */
    void add(ConfigFieldFiducialAddParam configFieldFiducialAddParam);

    /**
     * 编辑考证字段配置
     *
     * @author Lb
     * @date  2023/08/31 19:28
     */
    void edit(ConfigFieldFiducialEditParam configFieldFiducialEditParam);

    /**
     * 删除考证字段配置
     *
     * @author Lb
     * @date  2023/08/31 19:28
     */
    void delete(List<ConfigFieldFiducialIdParam> configFieldFiducialIdParamList);

    /**
     * 获取考证字段配置详情
     *
     * @author Lb
     * @date  2023/08/31 19:28
     */
    ConfigFieldFiducial detail(ConfigFieldFiducialIdParam configFieldFiducialIdParam);

    /**
     * 获取考证字段配置详情
     *
     * @author Lb
     * @date  2023/08/31 19:28
     **/
    ConfigFieldFiducial queryEntity(String id);
}
