package com.cj.waterresources.func.modular.waterPrice.sporadicWaterFee.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 零星水费(SporadicWaterFee)表实体类
 *
 * @author makejava
 * @since 2024-02-01 09:01:05
 */
@Data
public class SporadicWaterFee extends Model<SporadicWaterFee> {

    //主键id
    @ApiModelProperty(value = "主键id")
    private String id;

    //单位
    @ApiModelProperty(value = "单位")
    private String unit;

    //单位
    @ApiModelProperty(value = "单位ID")
    private String unitId;

    //单位
    @ApiModelProperty(value = "父节点名称")
    private String pName;

    //单位
    @ApiModelProperty(value = "父节点id")
    private String pId;

    //流量
    @ApiModelProperty(value = "流量")
    private Double flow;

    //水量
    @ApiModelProperty(value = "水量")
    private Double waterAmount;

    //水价
    @ApiModelProperty(value = "水价")
    private Double waterPrice;

    //应缴水费
    @ApiModelProperty(value = "应缴水费")
    private Double payableWaterFee;

    //已交水费
    @ApiModelProperty(value = "已交水费")
    private Double advancePaymentWaterFee;

    //盈余水费
    @ApiModelProperty(value = "盈余水费")
    private Double unpaidWaterFees;

    //应缴水资源费
    @ApiModelProperty(value = "应缴水资源费")
    private Double payableWaterResource;

    //已交水资源费
    @ApiModelProperty(value = "已交水资源费")
    private Double paidWaterResource;

    //盈余水资源费
    @ApiModelProperty(value = "盈余水资源费")
    private Double waterResourceSurplus;

    //类型
    @ApiModelProperty(value = "类型")
    private Integer waterFeeType;

    //年份
    @ApiModelProperty(value = "年份")
    private Integer year;

    //月份
    @ApiModelProperty(value = "月份")
    private Integer month;

    //备注
    @ApiModelProperty(value = "备注")
    private String remark;

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

    //逻辑删除(0-否 1-是)
    @ApiModelProperty(value = "逻辑删除(0-否 1-是)")
    private Integer del;

}

