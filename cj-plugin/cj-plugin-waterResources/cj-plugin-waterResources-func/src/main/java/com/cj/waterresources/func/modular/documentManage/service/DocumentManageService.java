package com.cj.waterresources.func.modular.documentManage.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cj.auth.core.util.StpLoginUserUtil;
import com.cj.common.exception.CommonException;
import com.cj.common.model.RestResponse;
import com.cj.model.func.core.util.MinioUtils;
import com.cj.waterresources.func.modular.documentManage.req.QueryReq;
import lombok.RequiredArgsConstructor;
import com.cj.waterresources.func.modular.documentManage.domain.DocumentManage;
import com.cj.waterresources.func.modular.documentManage.mapper.DocumentManageMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

/**
 * @author qianyf
 * @description 针对表【DOCUMENT_MANAGE(文档管理表)】的数据库操作Service实现
 * @createDate 2024-05-27 10:50:02
 */
@Service
@RequiredArgsConstructor
public class DocumentManageService extends ServiceImpl<DocumentManageMapper, DocumentManage>
        implements IService<DocumentManage> {
    private final MinioUtils minioUtils;

    @Value("${minio.url}")
    private String url;

    @Value("${minio.bucket}")
    private String bucket;

    private static final String DOCUMENT_PATH = "document/";

    @SneakyThrows
    public RestResponse upload(MultipartFile file, String info) {
        DocumentManage documentManage = JSONObject.parseObject(info, DocumentManage.class);
        Assert.isTrue(StringUtils.hasText(documentManage.getDocumentName()), "文档名称不能为空!");
        if (!this.lambdaQuery().eq(DocumentManage::getDocumentName, documentManage.getDocumentName()).list().isEmpty()) {
            throw new CommonException(documentManage.getDocumentName() + " 文档名称已存在!");
        }
        documentManage.setId(UUID.fastUUID().toString(true));
        documentManage.setUploadBy(StpLoginUserUtil.getLoginUser().getName());
        documentManage.setUploadTime(new Date());
        String fileSuffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
        if (!StringUtils.hasText(documentManage.getDocumentType())) {
            documentManage.setDocumentType(fileSuffix);
        }
        String path = DOCUMENT_PATH
                + DateUtil.format(documentManage.getUploadTime(), "yyyyMMdd/")
                + documentManage.getId() + "." + fileSuffix;
        minioUtils.putObject(bucket, path, file.getInputStream(), file.getContentType());
        documentManage.setDocumentUrl(path);
        boolean success;
        try {
            success = this.save(documentManage);
        } catch (Exception e) {
            minioUtils.deleteObjectInfo(bucket, path);
            throw e;
        }
        if (!success) {
            minioUtils.deleteObjectInfo(bucket, path);
        }
        return RestResponse.ok(success);
    }

    public RestResponse queryList(QueryReq req) {
        IPage<DocumentManage> page = new Page<>(req.getPageNo(), req.getPageSize());
        return RestResponse.ok(this.lambdaQuery()
                .like(DocumentManage::getDocumentName, req.getDocumentName())
                .like(DocumentManage::getUploadBy, req.getUploadBy())
                .ge(req.getStartTime() != null, DocumentManage::getUploadTime, req.getStartTime())
                .le(req.getEndTime() != null, DocumentManage::getUploadTime, req.getEndTime())
                .page(page));
    }

    public RestResponse del(List<String> ids) {
        List<DocumentManage> list = this.lambdaQuery().in(DocumentManage::getId, ids).list();
        list.forEach(n -> minioUtils.deleteObjectInfo(bucket, n.getDocumentUrl()));
        this.removeBatchByIds(ids);
        return RestResponse.ok();
    }

    @SneakyThrows
    public RestResponse view(String id) {
        DocumentManage document = this.getById(id);
        if(null == document){
            return RestResponse.no("暂无文档数据");
        }else {
            String resultUrl = url+"/"+bucket+"/"+document.getDocumentUrl();
            return RestResponse.ok(resultUrl);
        }
    }

    public void download(String id, HttpServletResponse response) {
        minioUtils.download(bucket, this.getById(id).getDocumentUrl(), response);
    }
}




