package com.cj.approval.func.modular.approval.approvalManagement.entity;

import java.util.Date;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 审批管理表(ApprovalManagement)表实体类
 *
 * @author makejava
 * @since 2023-12-19 19:41:02
 */
@Data
public class ApprovalManagement extends Model<ApprovalManagement> {
    //主键ID
    @ApiModelProperty(value = "主键ID")
    private String id;

    //指令类型
    @ApiModelProperty(value = "指令类型")
    private String instructionType;

    //调度单位
    @ApiModelProperty(value = "调度单位")
    private String dispatchingUnit;

    //调度目标
    @ApiModelProperty(value = "调度目标")
    private String dispatchingObjectives;

    //调度参数
    @ApiModelProperty(value = "调度参数")
    private String dispatchingParams;

    //调度时间
    @ApiModelProperty(value = "调度时间")
    private Date dispatchingTime;

    //审批状态(1-待审批 2-已审批 3-已拒绝)
    @ApiModelProperty(value = "审批状态(1-待审批 2-已审批 3-已拒绝)")
    private Integer approvalStatus;

    //下发人
    @ApiModelProperty(value = "下发人")
    private String lssuedBy;

    //审批人
    @ApiModelProperty(value = "审批人")
    private String approvedBy;

    //完成时间
    @ApiModelProperty(value = "完成时间")
    private Date completeTime;

    //指令状态(1-未开始 2-开始 3-进行中 4-已完成)
    @ApiModelProperty(value = "指令状态(1-未开始 2-开始 3-进行中 4-已完成)")
    private Integer instructionStatus;

    //关联指令
    @ApiModelProperty(value = "关联指令")
    private String associativeInstruction;

    //创建时间
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    //创建人
    @ApiModelProperty(value = "创建人")
    private String createBy;

    //更新时间
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    //更新人
    @ApiModelProperty(value = "更新人")
    private String updateBy;

    //逻辑删除(0-正常 1-删除)
    @ApiModelProperty(value = "逻辑删除(0-正常 1-删除)")
    private Integer del;
}

