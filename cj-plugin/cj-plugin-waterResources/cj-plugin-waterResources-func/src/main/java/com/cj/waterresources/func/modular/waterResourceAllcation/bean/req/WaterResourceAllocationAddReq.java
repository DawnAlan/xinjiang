package com.cj.waterresources.func.modular.waterResourceAllcation.bean.req;

import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class WaterResourceAllocationAddReq implements Serializable {

    //方案名称
    @ApiModelProperty(value = "方案名称")
    private String schemeName;

    //时段类型(1-年逐月 2-月逐旬 3-旬逐日)
    @ApiModelProperty(value = "时段类型(1-年逐月 2-月逐旬 3-旬逐日) 4-日前")
    private Integer bucketType;

    //配水开始时间
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "配水开始时间")
    private Date waterDistributionStartTime;

    //配水结束时间
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "配水结束时间")
    private Date waterDistributionEndTime;

    //配水类型(1-供水比例最大 2-供水缺额最小 3-单库调度)
    @ApiModelProperty(value = "配水类型(1-供水比例最大 2-供水缺额最小 3-单库调度)")
    private Integer waterDistributionType;

    //来水数据地址
    @ApiModelProperty(value = "来水数据地址")
    private String inflowDataAddress;

    //来水数据名称
    @ApiModelProperty(value = "来水数据名称")
    private String inflowDataName;

    //需水数据地址
    @ApiModelProperty(value = "需水数据地址")
    private String needWaterDataAddress;

    //需水数据名称
    @ApiModelProperty(value = "需水数据名称")
    private String needWaterName;

    //备注
    @ApiModelProperty(value = "备注")
    private String remark;

    //楼庄子汛限水位
    @ApiModelProperty(value = "楼庄子汛限水位")
    private Double  floodWaterLevelLzz;

    //头屯河汛限水位
    @ApiModelProperty(value = "头屯河汛限水位")
    private Double  floodWaterLevelTth;

    //楼庄子起调水位
    @ApiModelProperty(value = "楼庄子起调水位")
    private Double levelBeginLzz;

    //头屯河起调水位
    @ApiModelProperty(value = "头屯河起调水位")
    private Double levelBeginTth;

    //楼庄子期末水位
    @ApiModelProperty(value = "楼庄子期末水位")
    private Double levelEndLzz;

    //头屯河期末水位
    @ApiModelProperty(value = "头屯河期末水位")
    private Double levelEndTth;
}
