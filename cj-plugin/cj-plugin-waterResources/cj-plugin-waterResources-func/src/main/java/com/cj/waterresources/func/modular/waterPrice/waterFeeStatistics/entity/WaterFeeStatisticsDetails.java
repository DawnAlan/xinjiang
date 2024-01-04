package com.cj.waterresources.func.modular.waterPrice.waterFeeStatistics.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 水费统计详情(WaterFeeStatisticsDetails)表实体类
 *
 * @author makejava
 * @since 2023-11-29 17:15:43
 */
@Data
public class WaterFeeStatisticsDetails extends Model<WaterFeeStatisticsDetails> {
    //主键ID
    @ApiModelProperty(value = "主键ID")
    private String id;

    //日期
    @ApiModelProperty(value = "日期")
    private String statisticsDate;

    //表头ID
    @ApiModelProperty(value = "表头ID")
    private String tableHeadId;

    //值
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "值")
    private Double v;

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

}

