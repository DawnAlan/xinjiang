
package com.cj.dev.api;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件API接口，可参考com.cj.dev.core.util.file包下的工具类扩展更多需要的方法
 *
 * @author xuyuxiang
 * @date 2022/6/22 15:21
 **/
public interface DevFileApi {

    /* =========本地文件========= */

    /**
     * 上传文件返回Url
     *
     * @param file 文件
     * @author xuyuxiang
     * @date 2022/6/22 17:44
     **/
    String storageFileWithReturnUrlLocal(MultipartFile file);

    /**
     * 上传文件返回Id
     *
     * @param file 文件
     * @author xuyuxiang
     * @date 2022/6/22 17:44
     **/
    String storageFileWithReturnIdLocal(MultipartFile file);

    /* =========阿里云文件========= */

    /**
     * 上传文件返回Url
     *
     * @param file 文件
     * @author xuyuxiang
     * @date 2022/6/22 17:44
     **/
    String storageFileWithReturnUrlAliyun(MultipartFile file);

    /**
     * 上传文件返回Id
     *
     * @param file 文件
     * @author xuyuxiang
     * @date 2022/6/22 17:44
     **/
    String storageFileWithReturnIdAliyun(MultipartFile file);

    /* =========腾讯云件========= */

    /**
     * 上传文件返回Url
     *
     * @param file 文件
     * @author xuyuxiang
     * @date 2022/6/22 17:44
     **/
    String storageFileWithReturnUrlTencent(MultipartFile file);

    /**
     * 上传文件返回Id
     *
     * @param file 文件
     * @author xuyuxiang
     * @date 2022/6/22 17:44
     **/
    String storageFileWithReturnIdTencent(MultipartFile file);

    /* =========MINIO件========= */

    /**
     * 上传文件返回Url
     *
     * @param file 文件
     * @author xuyuxiang
     * @date 2022/6/22 17:44
     **/
    String storageFileWithReturnUrlMinio(MultipartFile file);

    /**
     * 上传文件返回Id
     *
     * @param file 文件
     * @author xuyuxiang
     * @date 2022/6/22 17:44
     **/
    String storageFileWithReturnIdMinio(MultipartFile file);
}
