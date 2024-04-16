package com.cj.flood.func.modular.prediction.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 来水预报
 * @TableName INCOMING_WATER_FORECAST
 */
@TableName(value ="INCOMING_WATER_FORECAST")
@Data
public class IncomingWaterForecast implements Serializable {
    /**
     * 主键id
     */
    @TableId(value = "ID")
    private String id;

    /**
     * 方案名称
     */
    @TableField(value = "PROGRAMME_NAME")
    private String programmeName;

    /**
     * 模型类型(1-中长期 2-短期 3-场次)
     */
    @TableField(value = "MODEL_TYPE")
    private Integer modelType;

    /**
     * 预报时间
     */
    @TableField(value = "PREDICTION_TIME")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date predictionTime;

    /**
     * 时段类型(1-月 2-旬 3-日 4-小时)
     */
    @TableField(value = "PERIOD_TIME_TYPE")
    private Integer periodTimeType;

    /**
     * 时段步长
     */
    @TableField(value = "PERIOD_TIME_STEP")
    private Integer periodTimeStep;

    /**
     * 时间数量
     */
    @TableField(value = "PERIOD_TIME_NUM")
    private Integer periodTimeNum;

    /**
     * 备注
     */
    @TableField(value = "REMARK")
    private String remark;

    /**
     * 创建时间
     */
    @TableField(value = "CREATE_TIME")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 创建人
     */
    @TableField(value = "CREATE_BY")
    private String createBy;

    @TableField(value = "MODEL_RESULT_ADDRESS")
    private String modelResultAddress;

    /**
     * 结束时间
     */
    @TableField(value = "END_TIME")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    //方案生成状态(1-生成中 2-已生成)
    private Integer status;
}