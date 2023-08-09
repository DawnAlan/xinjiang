
package com.cj.gen.modular.basic.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 数据库表字段结果
 *
 * @author xuyuxiang
 * @date 2022/7/19 19:06
 **/
@Getter
@Setter
public class GenBasicTableColumnResult {

    /** 字段名称 */
    @ApiModelProperty(value = "字段名称", position = 1)
    private String columnName;

    /** 字段类型 */
    @ApiModelProperty(value = "字段类型", position = 2)
    private String typeName;

    /** 字段注释 */
    @ApiModelProperty(value = "字段注释", position = 3)
    private String columnRemark;
}
