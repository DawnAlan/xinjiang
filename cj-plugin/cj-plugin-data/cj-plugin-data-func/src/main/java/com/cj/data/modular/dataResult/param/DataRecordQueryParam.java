package com.cj.data.modular.dataResult.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataRecordQueryParam {

    /** 排序字段 */
    @ApiModelProperty(value = "排序字段，字段驼峰名称，如：observationDate", position = 1)
    private String sortField;

    /** 排序方式 */
    @ApiModelProperty(value = "排序方式，升序：ASCEND；降序：DESCEND", position = 1)
    private String sortOrder;

    /** 测点ID */
    @ApiModelProperty(value = "POINT_ID")
    private String pointId;

    /** 观测时间开始 */
    @ApiModelProperty(value = "观测时间开始")
    private String startObservationDate;

    /** 观测时间结束 */
    @ApiModelProperty(value = "观测时间结束")
    private String endObservationDate;

    /** 观测方式 */
    @ApiModelProperty(value = "观测方式")
    private String recordMethod;
}
