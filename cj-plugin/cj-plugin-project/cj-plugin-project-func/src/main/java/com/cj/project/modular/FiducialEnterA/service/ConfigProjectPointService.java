package com.cj.project.modular.FiducialEnterA.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.project.modular.FiducialEnterA.entity.ConfigProjectPoint;
import com.cj.project.modular.FiducialEnterA.param.ConfigProjectPointAddParam;
import com.cj.project.modular.FiducialEnterA.param.ConfigProjectPointEditParam;
import com.cj.project.modular.FiducialEnterA.param.ConfigProjectPointIdParam;


import java.util.List;

/**
 * FiducialEnterAService接口
 *
 * @author Lb
 * @date  2023/11/23 10:20
 **/
public interface ConfigProjectPointService extends IService<ConfigProjectPoint> {

    /**
     * 获取List
     *
     * @author Lb
     * @date  2023/11/23 10:20
     */
    List<ConfigProjectPoint> getList(String projectcode,String instrumentName);

    /**
     * 添加
     *
     * @author Lb
     * @date  2023/11/23 10:20
     */
    void add(ConfigProjectPointAddParam configProjectPointAddParam);

    /**
     * 编辑
     *
     * @author Lb
     * @date  2023/11/23 10:20
     */
    void edit(ConfigProjectPointEditParam configProjectPointEditParam);

    /**
     * 删除
     *
     * @author Lb
     * @date  2023/11/23 10:20
     */
    void delete(List<ConfigProjectPointIdParam> configProjectPointIdParamList);

    /**
     * 获取详情
     *
     * @author Lb
     * @date  2023/11/23 10:20
     */
    ConfigProjectPoint detail(ConfigProjectPointIdParam configProjectPointIdParam);

    /**
     * 获取详情
     *
     * @author Lb
     * @date  2023/11/23 10:20
     **/
    ConfigProjectPoint queryEntity(String id);
}
