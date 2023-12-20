package com.cj.approval.func.modular.approval.instructionViewing.entity;

import java.util.Date;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 指令查看表(InstructionViewing)表实体类
 *
 * @author makejava
 * @since 2023-12-19 19:41:48
 */
@Data
public class InstructionViewing extends Model<InstructionViewing> {
    //主键ID
    @ApiModelProperty(value = "主键ID")
    private String id;

    //指令ID
    @ApiModelProperty(value = "指令ID")
    private String instructionId;

    //单位
    @ApiModelProperty(value = "单位")
    private String unit;

    //查阅状态(1-已阅读 2-未阅读)
    @ApiModelProperty(value = "查阅状态(1-已阅读 2-未阅读)")
    private Integer viewStatus;

    //指令状态(1-未开始 2-开始 3-进行中 4-已完成)
    @ApiModelProperty(value = "指令状态(1-未开始 2-开始 3-进行中 4-已完成)")
    private Integer instructionStatus;

    //阅读时间
    @ApiModelProperty(value = "阅读时间")
    private Date readTime;

    //完成时间
    @ApiModelProperty(value = "完成时间")
    private Date completeTime;

}

