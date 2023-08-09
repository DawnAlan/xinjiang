
package com.cj.gen.modular.basic.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * 数据源库字段列表参数
 *
 * @author xuyuxiang
 * @date 2022/7/29 9:59
 */
@Getter
@Setter
public class GenBasicTableColumnParam {

    /** 表名称 */
    @ApiModelProperty(value = "表名称", required = true)
    @NotBlank(message = "表名称不能为空")
    private String tableName;
}
