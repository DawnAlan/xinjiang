package com.cj.data.modular.dataResult.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.cj.common.pojo.CommonEntity;
import com.fhs.core.trans.vo.TransPojo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 数据基础表实体
 *
 * @author Lb
 * @date  2023/10/13 08:59
 **/
@Getter
@Setter
@TableName("data_record")
public class DataRecord extends CommonEntity {

    /** ID */
    @TableId
    @ApiModelProperty(value = "ID", position = 1)
    private String id;

    /** 测点ID */
    @ApiModelProperty(value = "测点ID", position = 2)
    private String pointId;

    /** 观测时间 */
    @ApiModelProperty(value = "观测时间", position = 3)
    private Date observationDate;

    /** 观测方式 */
    @ApiModelProperty(value = "观测方式", position = 4)
    private String recordMethod;

    /** 审核标识 */
    @ApiModelProperty(value = "审核标识", position = 5)
    private String reviewFlag;

    /** 审核信息 */
    @ApiModelProperty(value = "审核信息", position = 6)
    private String reviewInfo;

    /** 审核时间 */
    @ApiModelProperty(value = "审核时间", position = 7)
    private Date reviewTime;

    /** 审核人 */
    @ApiModelProperty(value = "审核人", position = 8)
    private String reviewUser;

    /** 备注 */
    @ApiModelProperty(value = "备注", position = 9)
    private String deleteInfo;

    /** 扩展字段 */
    @ApiModelProperty(value = "扩展字段", position = 10)
    private String extJson;

}
