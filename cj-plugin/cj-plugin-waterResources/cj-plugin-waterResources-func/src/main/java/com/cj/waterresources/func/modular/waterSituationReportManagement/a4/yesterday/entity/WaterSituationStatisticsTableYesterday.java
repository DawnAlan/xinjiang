package com.cj.waterresources.func.modular.waterSituationReportManagement.a4.yesterday.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 昨日水情日报表(WaterSituationStatisticsTableYesterday)表实体类
 *
 * @author makejava
 * @since 2023-12-23 19:10:46
 */
@Data
public class WaterSituationStatisticsTableYesterday extends Model<WaterSituationStatisticsTableYesterday> {
    //主键ID
    @ApiModelProperty(value = "主键ID")
    private String id;

    //记录时间
    @ApiModelProperty(value = "记录时间")
    private Date recordTime;

    //制材厂-日均流量
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "制材厂-日均流量")
    private Double zccRjll;

    //制材厂-最大流量
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "制材厂-最大流量")
    private Double zccZdll;

    //制材厂-日降雨量
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "制材厂-日降雨量")
    private Double zccRjyl;

    //制材厂-日均气温
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "制材厂-日均气温")
    private Double zccRjqw;

    //雨量站-八一林场
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "雨量站-八一林场")
    private Double ylzBylc;

    //雨量站-黑沟
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "雨量站-黑沟")
    private Double ylzHg;

    //雨量站-无名沟
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "雨量站-无名沟")
    private Double ylzWmg;

    //雨量站-小渠子
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "雨量站-小渠子")
    private Double ylzXqz;

    //雨量站-团结队
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "雨量站-团结队")
    private Double ylzTjd;

    //雨量站-头屯河进库雨量站
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "雨量站-头屯河进库雨量站")
    private Double ylzTthjkylz;

    //楼庄子-20时-库水位
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "楼庄子-20时-库水位")
    private Double lzz20Ksw;

    //楼庄子-20时-库容
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "楼庄子-20时-库容")
    private Double lzz20Kr;

    //楼庄子-入库日均
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "楼庄子-入库日均")
    private Double lzzRkrj;

    //楼庄子-出库日均-楼庄子水厂
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "楼庄子-出库日均-楼庄子水厂")
    private Double lzzCkrjLzzsc;

    //楼庄子-出库日均-河道
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "楼庄子-出库日均-河道")
    private Double lzzCkrjHd;

    //楼庄子-出库日均-合计
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "楼庄子-出库日均-合计")
    private Double lzzCkrjHj;

    //头屯河-早8时-库水位
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "头屯河-早8时-库水位")
    private Double tth8Ksw;

    //头屯河-早8时-库容
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "头屯河-早8时-库容")
    private Double tth8Kr;

    //头屯河-20时-库水位
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "头屯河-20时-库水位")
    private Double tth20Ksw;

    //头屯河-20时-库容
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "头屯河-20时-库容")
    private Double tth20Kr;

    //头屯河-进库日均-进库
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "头屯河-进库日均-进库")
    private Double tthJkrjJk;

    //头屯河-进库日均-工业引水
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "头屯河-进库日均-工业引水")
    private Double tthJkrjGyys;

    //头屯河-进库日均-合计
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "头屯河-进库日均-合计")
    private Double tthJkrjHj;

    //头屯河-出库日均-河道
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "头屯河-出库日均-河道")
    private Double tthCkrjHd;

    //头屯河-出库日均-八钢
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "头屯河-出库日均-八钢")
    private Double tthCkrjBg;

    //头屯河-出库日均-红岩水库
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "头屯河-出库日均-红岩水库")
    private Double tthCkrjHysk;

    //头屯河-出库日均-合计
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "头屯河-出库日均-合计")
    private Double tthCkrjHj;

    //渠首-总干渠-东干渠
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "渠首-总干渠-东干渠")
    private Double qsZgqDgq;

    //渠首-总干渠-西干渠
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "渠首-总干渠-西干渠")
    private Double qsZgqXgq;

    //渠首-总干渠-漏斗
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "渠首-总干渠-漏斗")
    private Double qsZgqLd;

    //渠首-总干渠-合计
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "渠首-总干渠-合计")
    private Double qsZgqHj;

    //渠首-灯笼渠-农业
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "渠首-灯笼渠-农业")
    private Double qsDlqNy;

    //渠首-灯笼渠-绿化
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "渠首-灯笼渠-绿化")
    private Double qsDlqLh;

    //渠首-灯笼渠-合计
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "渠首-灯笼渠-合计")
    private Double qsDlqHj;

    //渠首-泄洪-河道
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "渠首-泄洪-河道")
    private Double qsXhHd;

    //渠首-全河
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "渠首-全河")
    private Double qsQh;

    //渠首-对口率
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "渠首-对口率")
    private Double qsDkl;

    //河东-实收
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "河东-实收")
    private Double hdSs;

    //河东-对口率
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "河东-对口率")
    private Double hdDkl;

    //河西-实收
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "河西-实收")
    private Double hxSs;

    //河西-对口率
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "河西-对口率")
    private Double hxDkl;
}

