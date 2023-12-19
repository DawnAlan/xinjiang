package com.cj.waterresources.func.modular.useWaterPlanEscalation.dayWaterUsePlan.entity;

import java.util.Date;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 日用水计划(DayWaterUsePlan)表实体类
 *
 * @author makejava
 * @since 2023-12-07 17:27:08
 */
@Data
public class DayWaterUsePlan extends Model<DayWaterUsePlan> {
    //主键ID
    @ApiModelProperty(value = "主键ID")
    private String id;

    //记录日期
    @ApiModelProperty(value = "记录日期")
    private Date recordTime;

    //区域
    @ApiModelProperty(value = "区域")
    private String area;

    //记录值
    @ApiModelProperty(value = "记录值")
    private String v;

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

