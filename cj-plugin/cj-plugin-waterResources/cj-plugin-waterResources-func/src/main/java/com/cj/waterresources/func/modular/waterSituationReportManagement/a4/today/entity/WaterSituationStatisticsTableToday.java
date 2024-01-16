package com.cj.waterresources.func.modular.waterSituationReportManagement.a4.today.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 今日水情日报表(WaterSituationStatisticsTableToday)表实体类
 *
 * @author makejava
 * @since 2023-12-23 19:11:06
 */
@Data
public class WaterSituationStatisticsTableToday extends Model<WaterSituationStatisticsTableToday> {

    @ApiModelProperty(value = "主键ID")
    private String id;

    //记录时间
    @ApiModelProperty(value = "记录时间")
    private Date recordTime;

    //制材厂-水势
    @ApiModelProperty(value = "制材厂-水势")
    private String zccSs;

    //制材厂-流量
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "制材厂-流量")
    private Double zccLl;

    //楼庄子-库水位
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "楼庄子-库水位")
    private Double lzzKsw;

    //楼庄子-库容
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "楼庄子-库容")
    private Double lzzKr;

    //楼庄子-入库流量
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "楼庄子-入库流量")
    private Double lzzRkll;

    //楼庄子-出库流量-楼庄子水厂
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "楼庄子-出库流量-楼庄子水厂")
    private Double lzzCkllLzzsc;

    //楼庄子-出库流量-河道
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "楼庄子-出库流量-河道")
    private Double lzzCkllHd;

    //楼庄子-出库流量-合计
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "楼庄子-出库流量-合计")
    private Double lzzCkllHj;

    //头屯河-库水位水势
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "头屯河-库水位水势")
    private String tthKswss;

    //头屯河-库水位
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "头屯河-库水位")
    private Double tthKsw;

    //头屯河-库容
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "头屯河-库容")
    private Double tthKr;

    //头屯河-进库-水势
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "头屯河-进库-水势")
    private String tthJkSs;

    //头屯河-进库-入库
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "头屯河-进库-入库")
    private Double tthJkRk;

    //头屯河-进库-工业引
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "头屯河-进库-工业引")
    private Double tthJkGyy;

    //头屯河-进库-合计
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "头屯河-进库-合计")
    private Double tthJkHj;

    //头屯河-出库-八钢供水方式
    @ApiModelProperty(value = "头屯河-出库-八钢供水方式")
    private String tthCkBggsfs;

    //头屯河-出库-河道
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "头屯河-出库-河道")
    private Double tthCkHd;

    //头屯河-出库-八钢
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "头屯河-出库-八钢")
    private Double tthCkBg;

    //头屯河-出库-红岩水库
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "头屯河-出库-红岩水库")
    private Double tthCkHysk;

    //头屯河-出库-合计
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "头屯河-出库-合计")
    private Double tthCkHj;

    //头屯河-浊度-入库
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "头屯河-浊度-入库")
    private Double tthZdRk;

    //头屯河-浊度-暗渠
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "头屯河-浊度-暗渠")
    private Double tthZdAq;

    //头屯河-浊度-出库
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "头屯河-浊度-出库")
    private Double tthZdCk;

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

    //渠首-泄洪
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "首-泄洪")
    private Double qsXh;

    //渠首-全河
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "渠首-全河")
    private Double qsQh;

    //渠首-对口率
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "渠首-对口率")
    private Double qsDkl;

    //河东-头屯河农场-西一支
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "河东-头屯河农场-西一支")
    private Double hdTthnkXyz;

    //河东-头屯河农场-西二支
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "河东-头屯河农场-西二支")
    private Double hdTthncXez;

    //河东-头屯河农场-园林队
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "河东-头屯河农场-园林队")
    private Double hdTthncYld;

    //河东-头屯河农场-合计
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "河东-头屯河农场-合计")
    private Double hdTthncHj;

    //河东-五一农场-中干
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "河东-五一农场-中干")
    private Double hdWyncZg;

    //河东-红岩独立口
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "河东-红岩独立口")
    private Double hdHydlk;

    //河东-三坪农场-西三支
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "河东-三坪农场-西三支")
    private Double hdSpncXsz3;

    //河东-三坪农场-西四支
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "河东-三坪农场-西四支")
    private Double hdSpncXsz4;

    //河东-三坪农场-西五支
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "河东-三坪农场-西五支")
    private Double hdSpncXwz;

    //河东-三坪农场-东干第一
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "河东-三坪农场-东干第一")
    private Double hdSpncDgdy;

    //河东-三坪农场-合计
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "河东-三坪农场-合计")
    private Double hdSpncHj;

    //河东-东三支
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "河东-东三支")
    private Double hdDsz;

    //河东-东一支
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "河东-东一支")
    private Double hdDyz;

    //河东-东二支
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "河东-东二支")
    private Double hdDez;

    //河东-西缘产业合计
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "河东-西缘产业合计")
    private Double hdXycyhj;

    //河东-头区园林
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "河东-头区园林")
    private Double hdTqyl;

    //河东-八钢绿化
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "河东-八钢绿化")
    private Double hdBglh;

    //河东-东干合计
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "河东-东干合计")
    private Double hdDghj;

    //河东-对口率
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "河东-对口率")
    private Double hdDkl;

    //河西-三工镇-三工渠
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "河西-三工镇-三工渠")
    private Double hxSgzSgq;

    //河西-三工镇-二工渠
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "河西-三工镇-二工渠")
    private Double hxSgzEgq;

    //河西-三工镇-旗帜渠
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "河西-三工镇-旗帜渠")
    private Double hxSgzQzq;

    //河西-三工镇-红光渠
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "河西-三工镇-红光渠")
    private Double hxSgzGhq;

    //河西-三工镇-长丰渠
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "河西-三工镇-长丰渠")
    private Double hxSgzCfq;

    //河西-三工镇-合计
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "河西-三工镇-合计")
    private Double hxSgzSs;

    //河西-六工渠-园艺场
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "河西-六工渠-园艺场")
    private Double hxLgqYyc;

    //河西-六工渠-实验场
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "河西-六工渠-实验场")
    private Double hxLgqSyc;

    //河西-六工渠-六工镇
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "河西-六工渠-六工镇")
    private Double hxLgqLgz;

    //河西-六工渠-合计
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "河西-六工渠-合计")
    private Double hxLgqHj;

    //河西-三工南部生态林
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "河西-三工南部生态林")
    private Double hxSgnbstl;

    //河西-泄洪
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "河西-泄洪")
    private Double hxXh;

    //河西-景观带
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "河西-景观带")
    private Double hxJgd;

    //河西-西干合计
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "河西-西干合计")
    private Double hxXghj;

    //河西-对口率
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "河西-对口率")
    private Double hxDkl;
}

