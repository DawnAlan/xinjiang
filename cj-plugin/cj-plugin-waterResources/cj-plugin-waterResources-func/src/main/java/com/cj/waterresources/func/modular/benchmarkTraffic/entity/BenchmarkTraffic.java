package com.cj.waterresources.func.modular.benchmarkTraffic.entity;

import java.util.Date;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 基准流量表(BenchmarkTraffic)表实体类
 *
 * @author makejava
 * @since 2024-07-17 11:23:39
 */
@Data
public class BenchmarkTraffic extends Model<BenchmarkTraffic> {
    //主键ID
    @ApiModelProperty(value = "主键ID")
    private String id;

    //申请日期
    @ApiModelProperty(value = "申请日期")
    private Date applyTime;

    //单位id
    @ApiModelProperty(value = "单位id")
    private String unitId;

    //单位名称
    @ApiModelProperty(value = "单位名称")
    private String unitName;

    //取水口id
    @ApiModelProperty(value = "取水口id")
    private String waterIntakeId;

    //取水口名称
    @ApiModelProperty(value = "取水口名称")
    private String waterIntakeName;

    //基准流量
    @ApiModelProperty(value = "基准流量")
    private Double benchmarkTraffic;

    //调整开始时间
    @ApiModelProperty(value = "调整开始时间")
    private Date adjustStartTime;

    //调整结束时间
    @ApiModelProperty(value = "调整结束时间")
    private Date adjustEndTime;

    //站审批人id
    @ApiModelProperty(value = "站审批人id")
    private String siteApproverId;

    //站审批人name
    @ApiModelProperty(value = "站审批人name")
    private String siteApproverName;

    //站审批状态（0-待审核  1-通过  2-拒绝 ）
    @ApiModelProperty(value = "站审批状态（0-待审核  1-通过  2-拒绝 ）")
    private Integer siteApprovalStatus;

    //局审批人id
    @ApiModelProperty(value = "局审批人id")
    private String bureauApproverId;

    //局审批人name
    @ApiModelProperty(value = "局审批人name")
    private String bureauApproverName;

    //局审批状态（0-待审核  1-通过  2-拒绝）
    @ApiModelProperty(value = "局审批状态（0-待审核  1-通过  2-拒绝）")
    private Integer bureauApprovalStatus;

    //程序执行状态（0-待执行 1-执行成功 2-执行失败）
    @ApiModelProperty(value = "程序执行状态（0-待执行 1-执行成功 2-执行失败）")
    private Integer programExecutionStatus;

    //程序执行备注
    @ApiModelProperty(value = "程序执行备注")
    private String programExecutionRemark;

    //创建人id
    @ApiModelProperty(value = "创建人id")
    private String createById;

    //创建人name
    @ApiModelProperty(value = "创建人name")
    private String createByName;
}

