package com.cj.waterresources.func.modular.trendsTable.bean.req;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class TrendsTableParamAddReq implements Serializable {

    @ApiModelProperty(value = "iD")
    private String id;

    /**
     * 父ID
     */
    @ApiModelProperty(value = "父ID")
    private String pId;

    /**
     * 参数名
     */
    @ApiModelProperty(value = "参数名")
    private String paramName;

    /**
     * 参数编码
     */
    @ApiModelProperty(value = "参数编码")
    private String paramCode;

    /**
     * 是否是父节点(1、是  2、否)
     */
    @ApiModelProperty(value = "是否是父节点(1、是  2、否)")
    private String isParent;

    /**
     * 排序
     */
    @ApiModelProperty(value = "排序")
    private Integer orderNum;


    @ApiModelProperty(value = "使用类型(1-水情日报 2-水费管理 3-灌溉额度)")
    private Integer useType;

    @ApiModelProperty(value = "分组")
    private String groupingGroup;

    @ApiModelProperty(value = "使用水库或站点")
    private String useStation;

    @ApiModelProperty(value = "用水类型")
    private String useWaterType;

    @ApiModelProperty(value = "全局单位id")
    private String unitId;

    @ApiModelProperty(value = "区域（1、昌吉 2、十二师 3、乌鲁木齐）")
    private Integer area;

    @ApiModelProperty(value = "类别（1、流量  2、水位 3、库容  4、浊度）")
    private Integer category;
}
