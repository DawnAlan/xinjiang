package com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.entity;

import java.util.Date;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 水费统计总计(WaterFeeStatisticsTotal)表实体类
 *
 * @author makejava
 * @since 2023-11-29 17:16:56
 */
@Data
public class WaterFeeStatisticsTotal extends Model<WaterFeeStatisticsTotal> {
    //主键ID
    @ApiModelProperty(value = "主键ID")
    private String id;

    //管理站
    @ApiModelProperty(value = "管理站")
    private String station;

    //年度
    @ApiModelProperty(value = "年度")
    private Integer year;

    //月份
    @ApiModelProperty(value = "月份")
    private Integer month;

    //旬
    @ApiModelProperty(value = "旬")
    private String tenDays;

    //表头ID
    @ApiModelProperty(value = "表头ID")
    private String tableHeadId;

    //合计
    @ApiModelProperty(value = "合计")
    private Double amountTo;

    //本旬水量
    @ApiModelProperty(value = "本旬水量")
    private Double currentWaterVolume;

    //上旬水量
    @ApiModelProperty(value = "上旬水量")
    private Double waterVolumeFirstTenDays;

    //累计水量
    @ApiModelProperty(value = "累计水量")
    private Double accumulatedWaterVolume;

    //应交水费
    @ApiModelProperty(value = "应交水费")
    private Double payableWaterFee;

    //预交水费
    @ApiModelProperty(value = "预交水费")
    private Double advancePaymentWaterFee;

    //水费余(欠)
    @ApiModelProperty(value = "水费余(欠)")
    private Double unpaidWaterFees;

    //去年同期水量
    @ApiModelProperty(value = "去年同期水量")
    private Double waterVolumeDuringLastYear;

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

    //已交水资源费
    @ApiModelProperty(value = "已交水资源费")
    private Double paidWaterResource;

    //应交水资源费
    @ApiModelProperty(value = "应交水资源费")
    private Double payableWaterResource;

    //水资源费盈余
    @ApiModelProperty(value = "水资源费盈余")
    private Double waterResourceSurplus;

    //总水费盈余
    @ApiModelProperty(value = "总水费盈余")
    private Double waterResourceTotal;

}

