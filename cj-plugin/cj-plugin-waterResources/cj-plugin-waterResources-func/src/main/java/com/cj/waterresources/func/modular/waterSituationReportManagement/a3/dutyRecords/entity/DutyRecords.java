package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.dutyRecords.entity;

import java.util.Date;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 值班记录(DutyRecords)表实体类
 *
 * @author makejava
 * @since 2023-12-25 16:59:50
 */
@Data
public class DutyRecords extends Model<DutyRecords> {
    //主键ID
    @ApiModelProperty(value = "主键ID")
    private String id;

    //记录日期
    @ApiModelProperty(value = "记录日期")
    private Date recordTime;

    //所属站点
    @ApiModelProperty(value = "所属站点")
    private String station;

    //内容
    @ApiModelProperty(value = "内容")
    private String contextInfo;

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

    //值班人员
    @ApiModelProperty(value = "值班人员")
    private String dutyPerson;

    //气温
    @ApiModelProperty(value = "气温")
    private String airTemperature;

    //天气
    @ApiModelProperty(value = "天气")
    private String weather;
}



