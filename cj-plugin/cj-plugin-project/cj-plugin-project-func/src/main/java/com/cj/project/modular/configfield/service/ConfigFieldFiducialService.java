package com.cj.project.modular.configfield.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cj.common.pojo.CommonResult;
import com.cj.project.api.configfield.dto.ConfigFieldFiducialDto;
import com.cj.project.api.configfield.dto.ConfigFieldFiducialPageDto;
import com.cj.project.api.configfield.dto.ConfigFieldFiducialQueryDto;
import com.cj.project.api.configfield.entity.ConfigFieldFiducial;
import com.cj.project.modular.configfield.result.ConfigFieldFiducialResult;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
     * @param configFieldFiducialQueryDto
     * @return
     */
    List<ConfigFieldFiducialResult> getList(ConfigFieldFiducialQueryDto configFieldFiducialQueryDto);

    /**
     * 获取考证字段配置分页
     *
     * @author Lb
     * @date  2023/08/31 19:28
     */
    Page<ConfigFieldFiducial> page(ConfigFieldFiducialPageDto configFieldFiducialPageDto);

    /**
     * 添加考证字段配置
     *
     * @author Lb
     * @date  2023/08/31 19:28
     */
    void add(ConfigFieldFiducial configFieldFiducial);

    /**
     * 编辑考证字段配置
     *
     * @author Lb
     * @date  2023/08/31 19:28
     */
    void edit(ConfigFieldFiducial configFieldFiducial);

    /**
     * 删除考证字段配置
     *
     * @author Lb
     * @date  2023/08/31 19:28
     */
    void delete(List<String> idList);

    /**
     * 获取考证字段配置详情
     *
     * @author Lb
     * @date  2023/08/31 19:28
     */
    ConfigFieldFiducial detail(String id);

    /**
     * 获取考证字段配置详情
     *
     * @author Lb
     * @date  2023/08/31 19:28
     **/
    ConfigFieldFiducial queryEntity(String id);


    void templateExport(ConfigFieldFiducialDto configFieldFiducialDto, HttpServletRequest request , HttpServletResponse response);

    CommonResult dataImport(MultipartFile file);

}
