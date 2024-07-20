package com.cj.flood.func.modular.rollUpdate.bean.req;

import com.cj.flood.func.core.common.PageTool;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class ModelRollUpdateSelectListReq extends PageTool {

    //方案名称
    @ApiModelProperty(value = "方案名称")
    private String schemeName;

    //运行状态（0-运行中 1-停止）
    @ApiModelProperty(value = "运行状态（0-运行中 1-停止）")
    private Integer runStatus;

    //创建人
    @ApiModelProperty(value = "创建人")
    private String createBy;

    //开始时间
    @ApiModelProperty(value = "开始时间")
    private String startTime;

    //结束时间
    @ApiModelProperty(value = "结束时间")
    private String endTime;
}
