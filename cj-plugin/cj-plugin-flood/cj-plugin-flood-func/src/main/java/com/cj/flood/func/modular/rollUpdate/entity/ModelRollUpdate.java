package com.cj.flood.func.modular.rollUpdate.entity;

import java.util.Date;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 模型滚动更新表(ModelRollUpdate)表实体类
 *
 * @author makejava
 * @since 2024-07-19 14:59:17
 */
@Data
public class ModelRollUpdate extends Model<ModelRollUpdate> {
    //主键ID
    @ApiModelProperty(value = "主键ID")
    private String id;

    //方案名称
    @ApiModelProperty(value = "方案名称")
    private String schemeName;

    //时段数量
    @ApiModelProperty(value = "时段数量")
    private Integer periodTimeCount;

    //刷新频率
    @ApiModelProperty(value = "刷新频率")
    private Integer refreshFrequency;

    //刷新次数
    @ApiModelProperty(value = "刷新次数")
    private Integer refreshCount;

    //调度方案
    @ApiModelProperty(value = "调度方案")
    private String schedulingScheme;

    //楼庄子起调水位
    @ApiModelProperty(value = "楼庄子起调水位")
    private Double lzzStartLevel;

    //头屯河起调水位
    @ApiModelProperty(value = "头屯河起调水位")
    private Double tthStartLevel;

    //当前运行次数
    @ApiModelProperty(value = "当前运行次数")
    private Integer currentRunCount;

    //运行状态（0-运行中 1-停止）
    @ApiModelProperty(value = "运行状态（0-运行中 1-停止）")
    private Integer runStatus;

    //创建人
    @ApiModelProperty(value = "创建人")
    private String createBy;

    //创建时间
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    //备注
    @ApiModelProperty(value = "备注")
    private String remark;

}

