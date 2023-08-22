package com.cj.textua.textua.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @创建人 yancheng
 * @创建时间 2023-08-22 10:19
 * @描述
 */
@Getter
@Setter
public class TextuaExtraParam {


    /** 考证字段 */
    @ApiModelProperty(value = "考证字段", position = 3)
    private String fieldname;

    /** 考证数据 */
    @ApiModelProperty(value = "考证数据", position = 4)
    private String fieldvalue;
}
