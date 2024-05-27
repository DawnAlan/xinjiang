package com.cj.waterresources.func.modular.documentManage.domain;

import java.io.Serializable;

import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
* 文档管理表
* @TableName DOCUMENT_MANAGE
*/
@TableName(value ="DOCUMENT_MANAGE")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "文档管理表", description = "文档管理表")
public class DocumentManage implements Serializable {

    @ApiModelProperty(value = "主键")
    private String id;
    @ApiModelProperty(value = "文档名称")
    private String documentName;
    @ApiModelProperty(value = "上传者")
    private String uploadBy;
    @ApiModelProperty(value = "上传时间")
    private Date uploadTime;
    @ApiModelProperty(value = "文档类型")
    private String documentType;
    @ApiModelProperty(value = "minio文档存储地址")
    private String documentUrl;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
