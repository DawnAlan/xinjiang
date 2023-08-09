
package com.cj.gen.modular.config.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * 代码生成详细配置查询参数
 *
 * @author yubaoshan
 * @date 2022/10/25 22:33
 */
@Getter
@Setter
public class GenConfigListParam {

    /** 排序字段 */
    @ApiModelProperty(value = "排序字段，字段驼峰名称，如：userName")
    private String sortField;

    /** 排序方式 */
    @ApiModelProperty(value = "排序方式，升序：ASCEND；降序：DESCEND")
    private String sortOrder;

    /** 基础ID */
    @ApiModelProperty(value = "基础ID")
    @NotBlank(message = "basicId不能为空")
    private String basicId;
}
