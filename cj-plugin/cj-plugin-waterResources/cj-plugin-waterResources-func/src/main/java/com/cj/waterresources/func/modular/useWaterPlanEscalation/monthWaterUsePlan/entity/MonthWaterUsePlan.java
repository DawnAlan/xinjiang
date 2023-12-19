package com.cj.waterresources.func.modular.useWaterPlanEscalation.monthWaterUsePlan.entity;

import java.util.Date;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 月用水计划(MonthWaterUsePlan)表实体类
 *
 * @author makejava
 * @since 2023-12-07 16:48:27
 */
@Data
public class MonthWaterUsePlan extends Model<MonthWaterUsePlan> {

    @ApiModelProperty(value = "主键ID")
    private String id;

    //单位
    @ApiModelProperty(value = "单位")
    private String unit;

    //上旬
    @ApiModelProperty(value = "上旬")
    private Double earlyOctober;

    //中旬
    @ApiModelProperty(value = "中旬")
    private Double midDay;

    //下旬
    @ApiModelProperty(value = "下旬")
    private Double laterOctober;

    //合计
    @ApiModelProperty(value = "合计")
    private Double total;

    //年度
    @ApiModelProperty(value = "年度")
    private Integer year;

    //月度
    @ApiModelProperty(value = "月度")
    private Integer month;

    //区域
    @ApiModelProperty(value = "区域")
    private String area;

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

    //单位ID
    @ApiModelProperty(value = "单位ID")
    private String unitId;
}

