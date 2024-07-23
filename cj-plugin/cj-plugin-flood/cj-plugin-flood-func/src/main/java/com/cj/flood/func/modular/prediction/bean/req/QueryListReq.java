package com.cj.flood.func.modular.prediction.bean.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class QueryListReq {
//    @ApiModelProperty(value = "页码")
//    private Integer pageNo;
//
//    @ApiModelProperty(value = "分页大小")
//    private Integer pageSize;

    @ApiModelProperty(value = "创建时间排序,1-升序,0-降序")
    private Integer dateAsc;

    @ApiModelProperty(value = "断面名称")
    private String siteName;

    @ApiModelProperty(value = "率定名称,模糊匹配")
    private String modelName;

    @ApiModelProperty(value = "开始时间")
    private Date startTime;

    @ApiModelProperty(value = "结束时间")
    private Date endTime;
}
