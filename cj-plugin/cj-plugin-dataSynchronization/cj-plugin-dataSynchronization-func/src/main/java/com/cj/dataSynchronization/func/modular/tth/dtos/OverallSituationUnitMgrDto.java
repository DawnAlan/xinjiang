package com.cj.dataSynchronization.func.modular.tth.dtos;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 全局单位管理(OverallSituationUnitMgr)表实体类
 *
 * @author makejava
 * @since 2024-02-21 11:23:14
 */
@Data
public class OverallSituationUnitMgrDto extends Model<OverallSituationUnitMgrDto> {
    //主键ID
    @ApiModelProperty(value = "主键ID")
    private String id;

    //单位名称
    @ApiModelProperty(value = "单位名称")
    private String unitName;

    //单位全称
    @ApiModelProperty(value = "单位全称")
    private String unitFullName;

    //父节点id
    @ApiModelProperty(value = "父节点id")
    private String pId;

    //父节点名称
    @ApiModelProperty(value = "父节点名称")
    private String pName;

    //数据来源（1-灌区e平台 2-楼庄子平台 3-其他）
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "数据来源（1-灌区e平台 2-楼庄子平台 3-其他）")
    private Integer dataResource;

    //监测点名称
    @ApiModelProperty(value = "监测点名称")
    private String monitorName;

    //监测点id
    @ApiModelProperty(value = "监测点id")
    private String monitorId;

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

    //排序字段
    @ApiModelProperty(value = "排序字段")
    private Integer sortNum;

}

