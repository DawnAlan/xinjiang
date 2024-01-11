package com.cj.fourPredictions.func.modular.flood.forecastEarlyWarning.bean.res;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 来水预报
 * @TableName INCOMING_WATER_FORECAST
 */
@Data
public class IncomingWaterForecast implements Serializable {

    private String id;

    private String programmeName;

    private Integer modelType;

    private Date predictionTime;

    private Integer periodTimeType;

    private Integer periodTimeStep;

    private Integer periodTimeNum;

    private String remark;

    private Date createTime;

    private String createBy;

    private String modelResultAddress;

    private Date endTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    //方案生成状态(1-生成中 2-已生成)
    private Integer status;
}