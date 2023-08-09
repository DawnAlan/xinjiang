
package com.cj.dev.modular.file.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * 文件Id参数
 *
 * @author xuyuxiang
 * @date 2022/7/31 10:24
 */
@Getter
@Setter
public class DevFileIdParam {

    /** id */
    @ApiModelProperty(value = "id", required = true)
    @NotBlank(message = "id不能为空")
    private String id;
}
