package com.cj.waterresources.func.modular.waterSituationDataMaintenance.bean.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
public class SelectInfoListReq {

    @ApiModelProperty(value = "树节点id")
    private String treeId;

    @ApiModelProperty(value = "名称")
    private String treeName;

    @ApiModelProperty(value = "树类型(头屯河灌区-irrigated  楼庄子水库-lzz)")
    private String treeType;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "查询当前时间")
    private String time;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "查询时间区间-开始时间")
    private String startTime;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "查询时间区间-结束时间")
    private String endTime;
}
