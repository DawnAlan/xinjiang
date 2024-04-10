package com.cj.waterresources.func.modular.trendsTable.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TrendsTableParam implements Serializable {
    /**
     * 主键ID
     */
    @TableId(value = "ID")
    @ApiModelProperty(value = "ID")
    private String id;

    /**
     * 父ID
     */
    @TableField(value = "P_ID")
    @ApiModelProperty(value = "父ID")
    private String pId;

    /**
     * 参数名
     */
    @TableField(value = "PARAM_NAME")
    @ApiModelProperty(value = "参数名")
    private String paramName;

    /**
     * 参数编码
     */
    @TableField(value = "PARAM_CODE")
    @ApiModelProperty(value = "参数编码")
    private String paramCode;

    /**
     * 是否是父节点(1、是  2、否)
     */
    @TableField(value = "IS_PARENT")
    @ApiModelProperty(value = "是否是父节点(1、是  2、否)")
    private String isParent;

    /**
     * 排序
     */
    @TableField(value = "ORDER_NUM")
    @ApiModelProperty(value = "排序")
    private Integer orderNum;


    @ApiModelProperty(value = "使用类型(1-水情日报 2-水费管理 3-灌溉额度)")
    private Integer useType;

    @ApiModelProperty(value = "分组")
    private String groupingGroup;

    @ApiModelProperty(value = "使用水库或站点")
    private String useStation;

    @ApiModelProperty(value = "统计id")
    private String statisticsId;

    @ApiModelProperty(value = "是否统计")
    private String isStatistics;

    @ApiModelProperty(value = "全局单位id")
    private String unitId;

    @ApiModelProperty(value = "区域（1、昌吉 2、十二师 3、乌鲁木齐）")
    private Integer area;

    @ApiModelProperty(value = "类别（1、流量  2、水位 3、库容  4、浊度）")
    private Integer category;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}