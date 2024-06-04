package com.cj.waterresources.func.modular.waterStorageScheduling.waterStorageSchedulingTotalForm.entity;

import java.util.Date;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 供水计划管理总表(WaterStorageSchedulingTotalForm)表实体类
 *
 * @author makejava
 * @since 2024-02-18 09:43:28
 */
@Data
public class WaterStorageSchedulingTotalForm extends Model<WaterStorageSchedulingTotalForm> {
    //主键id
    @ApiModelProperty(value = "主键id")
    private String id;

    //年度
    @ApiModelProperty(value = "年度")
    private Integer year;

    //方案名称
    @ApiModelProperty(value = "方案名称")
    private String planName;

    //来水年份
    @ApiModelProperty(value = "来水年份")
    private Integer inflowYear;

    //tth上一年末水位
    @ApiModelProperty(value = "tth上一年末水位")
    private Double firstData;

    //lzz上一年末水位
    @ApiModelProperty(value = "lzz上一年末水位")
    private Double secondData;

    //创建人
    @ApiModelProperty(value = "创建人")
    private String createBy;

    //创建时间
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
}

