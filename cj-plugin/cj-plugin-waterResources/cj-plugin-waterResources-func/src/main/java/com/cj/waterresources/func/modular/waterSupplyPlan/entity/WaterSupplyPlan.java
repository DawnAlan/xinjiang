package com.cj.waterresources.func.modular.waterSupplyPlan.entity;

import java.util.Date;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 供水计划管理(WaterSupplyPlan)表实体类
 *
 * @author makejava
 * @since 2023-11-21 09:51:23
 */
@Data
public class WaterSupplyPlan extends Model<WaterSupplyPlan> {
    //主键ID
    @ApiModelProperty(value = "主键ID")
    private String id;

    //供水计划类型(1-蓄水调度计划表)
    @ApiModelProperty(value = "供水计划类型(1-蓄水调度计划表)")
    private Integer waterSupplyPlanType;

    //水库(1-头屯河水库 2-楼庄子水库)
    @ApiModelProperty(value = "水库(1-头屯河水库 2-楼庄子水库)")
    private Integer reservoir;

    //年度
    @ApiModelProperty(value = "年度")
    private Integer year;

    //表格行数据
    @ApiModelProperty(value = "表格行数据")
    private String tableValue;

    //表格头数据
    @ApiModelProperty(value = "表格头数据")
    private String tableHead;

    //创建人
    @ApiModelProperty(value = "创建人")
    private String createBy;

    //创建时间
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    //更新人
    @ApiModelProperty(value = "更新人")
    private String updateBy;

    //更新时间
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    //逻辑删除(0-否 1-是)
    @ApiModelProperty(value = "逻辑删除(0-否 1-是)")
    private Integer del;

}

