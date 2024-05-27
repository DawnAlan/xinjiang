package com.cj.waterresources.func.modular.documentManage.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(value = "文档管理列表查询请求实体", description = "文档管理列表查询请求实体")
public class QueryReq {
    @ApiModelProperty(value = "方案名称")
    private String documentName;
    @ApiModelProperty(value = "开始时间")
    private Date startTime;
    @ApiModelProperty(value = "结束时间")
    private Date endTime;
    @ApiModelProperty(value = "上传人")
    private String uploadBy;
    @ApiModelProperty(value = "分页大小")
    private Long pageSize;
    @ApiModelProperty(value = "页码")
    private Long pageNo;
}
