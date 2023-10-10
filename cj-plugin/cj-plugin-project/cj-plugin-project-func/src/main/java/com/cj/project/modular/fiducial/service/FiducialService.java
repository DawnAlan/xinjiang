package com.cj.project.modular.fiducial.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.project.modular.fiducial.entity.FiducialBase;
import com.cj.project.modular.fiducial.entity.FiducialPara;
import com.cj.project.modular.fiducial.param.*;
import com.cj.project.modular.fiducial.result.FiducialResult;

import java.util.List;
import java.util.Map;

/**
 * 测点考证Service接口
 *
 * @author Lb
 * @date  2023/09/04 12:25
 **/
public interface FiducialService {

    /**
     * 获取测点考证分页
     *
     * @author Lb
     * @date  2023/09/04 12:25
     */
    Page<FiducialResult> page(Page<FiducialBase> fiducialBasePage);

    /**
     * 添加测点考证
     *
     * @author Lb
     * @date  2023/09/04 12:25
     */
    void add(String projectCode, String instrumentID, Map<String,Object> fieldMaps);

    void adds(List<FiducialAddParam> fiducialAddParamList);

    /**
     * 编辑测点考证
     *
     * @author Lb
     * @date  2023/09/04 12:25
     */
    void edit(FiducialBaseEditParam fiducialBaseEditParam);

    /**
     * 删除测点考证
     *
     * @author Lb
     * @date  2023/09/04 12:25
     */
    void delete(List<FiducialIdParam> fiducialIdParamList);

    /**
     * 合成考证结果
     * @return
     */
    FiducialResult ToFiducial(FiducialBase fiducialBase, List<FiducialPara> fiducialParas);
}
