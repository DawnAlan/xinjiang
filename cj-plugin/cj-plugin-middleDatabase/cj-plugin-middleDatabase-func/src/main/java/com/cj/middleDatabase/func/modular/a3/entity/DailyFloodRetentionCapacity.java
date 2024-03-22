package com.cj.middleDatabase.func.modular.a3.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
* 
* @TableName DAILY_FLOOD_RETENTION_CAPACITY
*/
@TableName(value ="DAILY_FLOOD_RETENTION_CAPACITY")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "", description = "")
public class DailyFloodRetentionCapacity implements Serializable {

    @ApiModelProperty(value = "主键")
    private String id;
    @ApiModelProperty(value = "0-楼庄子,1-头屯河,3-楼庄子最后计算时间,4-头屯河最后计算时间")
    private String stationType;
    @ApiModelProperty(value = "日期")
    private String tm;
    @ApiModelProperty(value = "拦蓄洪量")
    private BigDecimal capacity;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
