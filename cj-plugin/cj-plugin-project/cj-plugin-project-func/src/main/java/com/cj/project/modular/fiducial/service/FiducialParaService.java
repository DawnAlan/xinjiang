package com.cj.project.modular.fiducial.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.project.api.fiducial.entity.FiducialPara;
import com.cj.project.api.fiducial.param.*;

import java.util.List;

/**
 * 考证参数Service接口
 *
 * @author Lb
 * @date  2023/09/04 19:57
 **/
public interface FiducialParaService extends IService<FiducialPara> {

    /**
     * 添加考证参数
     *
     * @author Lb
     * @date  2023/09/04 19:57
     */
    void add(FiducialParaAddParam fiducialParaAddParam);

    /**
     * 添加考证参数s
     *
     * @author Lb
     * @date  2023/09/04 19:57
     */
    void adds(List<FiducialParaAddParam> fiducialParaAddParams);


    /**
     * 删除考证参数
     *
     * @author Lb
     * @date  2023/09/04 19:57
     */
    void delete(List<FiducialParaIdParam> fiducialParaIdParamList);

    void deleteByPoint(List<String> fiducialIdParamList);

    /**
     * 获取考证参数集合
     * @param fiducialIdParam
     */
    List<FiducialPara> getList(FiducialIdParam fiducialIdParam);
}
