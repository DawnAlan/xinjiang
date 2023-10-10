package com.cj.project.modular.fiducial.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.project.modular.fiducial.entity.FiducialBase;
import com.cj.project.modular.fiducial.param.*;

import java.util.List;

/**
 * 测点考证_基础数据表Service接口
 *
 * @author Lb
 * @date  2023/09/04 12:25
 **/
public interface FiducialBaseService extends IService<FiducialBase> {

    /**
     * 获取测点考证_基础数据表分页
     *
     * @author Lb
     * @date  2023/09/04 12:25
     */
    Page<FiducialBase> page(FiducialPageParam fiducialBaseParam);

    /**
     * 添加测点考证_基础数据表
     *
     * @author Lb
     * @date  2023/09/04 12:25
     */
    void add(FiducialBase fiducialBase);

    /**
     * 编辑测点考证_基础数据表
     *
     * @author Lb
     * @date  2023/09/04 12:25
     */
    void edit(FiducialBaseEditParam fiducialBaseEditParam);

    /**
     * 删除考证_基础数据表记录
     *
     * @author Lb
     * @date  2023/09/04 19:57
     */
    void delete(List<FiducialIdParam> fiducialIdParamList);

    /**
     * 获取测点考证_基础数据表详情
     *
     * @author Lb
     * @date  2023/09/04 12:25
     */
    FiducialBase detail(FiducialIdParam fiducialIdParam);

    /**
     * 获取测点考证_基础数据表详情
     *
     * @author Lb
     * @date  2023/09/04 12:25
     **/
    FiducialBase queryEntity(String id);

    /**
     * 批量添加考证
     * Fiducial Service related
     */
    void adds(FiducialBaseAddParam fiducialBaseAddParam);

    /**
     * 查询考证
     * @param fiducialQueryParam 查询参数
     * @return
     */
    FiducialBase getOne(FiducialQueryParam fiducialQueryParam);

    /**
     * 批量查询考证
     * @param fiducialQueryParam 查询参数
     * @return
     */
    List<FiducialBase> getBatch(FiducialQueryParam fiducialQueryParam);

    List<FiducialBase> getBatch(String projectCode, String instrumentStr);
}
