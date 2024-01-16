package com.cj.data.modular.dataResult.Result;

import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

public class DataPackResult {
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
}
