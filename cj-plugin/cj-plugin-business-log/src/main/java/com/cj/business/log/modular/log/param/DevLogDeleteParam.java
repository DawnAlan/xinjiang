
package com.cj.business.log.modular.log.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * 日志清空参数
 *
 * @author xuyuxiang
 * @date 2022/9/6 13:16
 */
@Getter
@Setter
public class DevLogDeleteParam {

    /** 日志分类 */
    @ApiModelProperty(value = "日志分类", required = true)
    @NotBlank(message = "category不能为空")
    private String category;
}
