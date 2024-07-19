package com.cj.waterresources.func.modular.benchmarkTraffic.bean.req;

import com.cj.waterresources.func.core.utils.PageToolUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class BenchmarkTrafficListReq extends PageToolUtil {

    //开始时间
    @ApiModelProperty(value = "开始时间")
    private String startTime;
    //结束时间
    @ApiModelProperty(value = "结束时间")
    private String endTime;
    //单位名称
    @ApiModelProperty(value = "单位名称")
    private String unitName;
    //站审批状态（0-待审核  1-通过  2-拒绝 ）
    @ApiModelProperty(value = "站审批状态（0-待审核  1-通过  2-拒绝 ）")
    private Integer siteApprovalStatus;
    //局审批状态（0-待审核  1-通过  2-拒绝
    @ApiModelProperty(value = "局审批状态（0-待审核  1-通过  2-拒绝）")
    private Integer bureauApprovalStatus;
    //程序执行状态（0-待执行 1-执行成功 2-执行失败）
    @ApiModelProperty(value = "程序执行状态（0-待执行 1-执行成功 2-执行失败）")
    private Integer programExecutionStatus;
    @ApiModelProperty(value = "创建人name")
    private String createByName;
}
