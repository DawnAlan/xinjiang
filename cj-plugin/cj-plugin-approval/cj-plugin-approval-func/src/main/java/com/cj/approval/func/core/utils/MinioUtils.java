package com.cj.approval.func.core.utils;


import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Slf4j
public class MinioUtils {

    @Autowired
    private MinioClient minioClient;


    /**
     * 判断桶是否存在
     */
    @SneakyThrows(Exception.class)
    public boolean bucketExists(String bucketName) {
        return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
    }


    /**
     * 创建桶
     *
     * @param bucketName 获取全部的桶  minioClient.listBuckets();
     */
    @SneakyThrows(Exception.class)
    public void createBucket(String bucketName) {
        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }
    }


    /**
     * 根据bucketName获取信息
     *
     * @param bucketName bucket名称
     */
    @SneakyThrows(Exception.class)
    public Optional<Bucket> getBucket(String bucketName) {
        return minioClient.listBuckets().stream().filter(b -> b.name().equals(bucketName)).findFirst();
    }

    /**
     * 根据bucketName删除信息
     *
     * @param bucketName bucket名称
     */
    @SneakyThrows(Exception.class)
    public void removeBucket(String bucketName) {
        minioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
    }

    /**
     * 获取文件流
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @return 二进制流
     */
    @SneakyThrows(Exception.class)
    public InputStream getObject(String bucketName, String objectName) {
        return minioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(objectName).build());
    }


    /**
     * 上传本地文件
     *
     * @param bucketName 存储桶
     * @param objectName 对象名称
     * @param fileName   本地文件路径
     */
    @SneakyThrows(Exception.class)
    public ObjectWriteResponse putObject(String bucketName, String objectName, String fileName) {
        if (!bucketExists(bucketName)) {
            createBucket(bucketName);
        }
        return minioClient.uploadObject(UploadObjectArgs.builder().bucket(bucketName).object(objectName).filename(fileName).build());
    }

    /**
     * 通过流上传文件
     *
     * @param bucketName  存储桶
     * @param objectName  文件对象
     * @param inputStream 文件流
     */
    @SneakyThrows(Exception.class)
    public ObjectWriteResponse putObject(String bucketName, String objectName, InputStream inputStream,String contentType) {
        if (!bucketExists(bucketName)) {
            createBucket(bucketName);
        }
        return minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .stream(inputStream, inputStream.available(), -1)
                .contentType(contentType)
                .build());
    }



    /**
     * 获取文件外链
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @param expires    过期时间 <=7 秒级
     * @return url
     */
    @SneakyThrows(Exception.class)
    public String getUploadObjectUrl(String bucketName, String objectName, Integer expires) {
        return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder().method(Method.PUT).bucket(bucketName).object(objectName).expiry(expires).build());
    }

    /**
     * 下载文件
     * bucketName:桶名
     *
     * @param fileName: 文件名
     */
    @SneakyThrows(Exception.class)
    public void download(String bucketName, String fileName, HttpServletResponse response) {
        // 获取对象的元数据
        StatObjectResponse stat = minioClient.statObject(StatObjectArgs.builder().bucket(bucketName).object(fileName).build());
        response.setContentType(stat.contentType());
        //response.setContentType("video/mp4");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
        InputStream is = minioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(fileName).build());
        IOUtils.copy(is, response.getOutputStream());
        is.close();
    }


    /**
     * 获取文件信息
     * stat:文件元数据
     *
     * @param bucketName: 桶名
     * @param objectName: 文件名
     */
    @SneakyThrows(Exception.class)
    public StatObjectResponse  getObjectInfo(String bucketName, String objectName) {
        // 获取对象的元数据
        StatObjectResponse stat = minioClient.statObject(StatObjectArgs.builder().bucket(bucketName).object(objectName).build());
        return stat;
    }

    /**
     * 删除单个文件信息
     *
     * @param bucketName: 桶名
     * @param fileName: 文件名
     */
    @SneakyThrows(Exception.class)
    public void deleteObjectInfo(String bucketName, String fileName) {
        minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(fileName).build());
    }


    /**
     * 删除批量文件信息
     *
     * @param bucketName: 桶名
     * @param fileNames: 文件名
     */
/*    @SneakyThrows(Exception.class)
    public void deleteBatchObjectInfo(String bucketName, List<String> fileNames) {
        for(String t:fileNames){
            log.info("--------------------删除的文件名："+t);
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(t).build());
        }
    }*/
    @SneakyThrows(Exception.class)
    public Iterable<Result<DeleteError>> deleteBatchObjectInfo(String bucketName, List<String> fileNames) {
        List<DeleteObject> dos = fileNames.stream().map(e -> new DeleteObject(e)).collect(Collectors.toList());
        Iterable<Result<DeleteError>> results = minioClient.removeObjects(RemoveObjectsArgs.builder().bucket(bucketName).objects(dos).build());
        return results;
    }

    /**
     * 创建文件桶（建议租户ID为桶的名称）
     */
    public boolean exitsBucket(String bucket) {
        boolean found = false;
        try {
            found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
        } catch (Exception e) {
            log.error("create bucket is error", e);
        }
        return found;
    }

    /**
     * 删除文件
     *
     * @param bucket     桶名称
     * @param objectName 对象名称
     * @return boolean
     */
    public boolean removeObject(String bucket, String objectName) {
        try {
            boolean exsit = exitsBucket(bucket);
            if (exsit) {
                // 从mybucket中删除myobject。removeobjectargs.builder().bucket(bucketname).object(objectname).build()
                minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucket).object(objectName).build());
                return true;
            }
        } catch (Exception e) {
            log.error("removeObject", e);
        }
        return false;
    }


    /**
     * 批量删除文件
     *
     * @param bucket      桶名称
     * @param objectNames 对象名称
     * @return boolean
     */
    public boolean removeObjects(String bucket, List<String> objectNames) {
        boolean exsit = exitsBucket(bucket);
        if (exsit) {
            try {
                List<DeleteObject> objects = new LinkedList<>();
                for (String str : objectNames) {
                    objects.add(new DeleteObject(str));
                }
                minioClient.removeObjects(RemoveObjectsArgs.builder().bucket(bucket).objects(objects).build());
                return true;
            } catch (Exception e) {
                log.error("removeObject", e);
            }
        }
        return false;
    }
}
