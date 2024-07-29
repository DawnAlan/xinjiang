package com.cj.approval.func.modular.approval.instructionFeedback.entity;

import java.util.Date;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 指令反馈表(InstructionFeedback)表实体类
 *
 * @author makejava
 * @since 2023-12-19 19:41:30
 */
@Data
public class InstructionFeedback extends Model<InstructionFeedback> {
    //主键ID
    @ApiModelProperty(value = "主键ID")
    private String id;

    //指令查看列表ID
    @ApiModelProperty(value = "指令查看列表ID)")
    private String instructionViewId;

    //反馈状态(1-未开始 2-开始 3-进行中 4-已完成)
    @ApiModelProperty(value = "反馈状态(1-未开始 2-开始 3-进行中 4-已完成)")
    private Integer feedbackStatus;

    //反馈人员
    @ApiModelProperty(value = "反馈人员")
    private String feedbackBy;

    //反馈内容
    @ApiModelProperty(value = "反馈内容")
    private String feedbackContext;

    //反馈时间
    @ApiModelProperty(value = "反馈时间")
    private Date feedbackTime;

    //执行人员
    @ApiModelProperty(value = "执行人员")
    private String executive;

    @ApiModelProperty(value = "接收人")
    private String recipient;

    @ApiModelProperty(value = "接收人ID")
    private String recipientId;

    @ApiModelProperty(value = "是否是供水科(0-不是 1-是)")
    private Integer isGsk;
}

