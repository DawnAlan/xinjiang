
package com.cj.dev.modular.file.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;
import com.cj.dev.modular.file.entity.DevFile;
import com.cj.dev.modular.file.param.DevFileIdParam;
import com.cj.dev.modular.file.param.DevFileListParam;
import com.cj.dev.modular.file.param.DevFilePageParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 文件Service接口
 *
 * @author xuyuxiang
 * @date 2022/2/23 18:27
 **/
public interface DevFileService extends IService<DevFile> {

    /**
     * MultipartFile文件上传，返回文件id
     *
     * @author xuyuxiang
     * @date 2022/4/22 15:53
     **/
    String uploadReturnId(String engine, MultipartFile file);

    /**
     * MultipartFile文件上传，返回文件Url
     *
     * @author xuyuxiang
     * @date 2022/4/22 15:53
     **/
    String uploadReturnUrl(String engine, MultipartFile file);

    /**
     * 文件分页列表接口
     *
     * @author xuyuxiang
     * @date 2022/6/21 15:44
     **/
    Page<DevFile> page(DevFilePageParam devFilePageParam);

    /**
     * 文件列表接口
     *
     * @author xuyuxiang
     * @date 2022/6/21 15:44
     **/
    List<DevFile> list(DevFileListParam devFileListParam);

    /**
     * 下载文件
     *
     * @author xuyuxiang
     * @date 2022/6/21 15:44
     **/
    void download(DevFileIdParam devFileIdParam, HttpServletResponse response) throws IOException;

    /**
     * 删除文件
     *
     * @author xuyuxiang
     * @date 2022/8/4 10:36
     **/
    void delete(List<DevFileIdParam> devFileIdParamList);

    /**
     * 获取文件详情
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:18
     */
    DevFile detail(DevFileIdParam devFileIdParam);

    /**
     * 获取文件详情
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:18
     */
    DevFile queryEntity(String id);
}
