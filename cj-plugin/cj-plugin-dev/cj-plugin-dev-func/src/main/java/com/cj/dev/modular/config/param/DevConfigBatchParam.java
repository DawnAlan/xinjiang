
package com.cj.dev.modular.config.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * 配置批量更新参数
 *
 * @author xuyuxiang
 * @date 2022/7/7 17:01
 **/
@Getter
@Setter
public class DevConfigBatchParam {

    /** 配置键 */
    @ApiModelProperty(value = "配置键", required = true, position = 1)
    @NotBlank(message = "configKey不能为空")
    private String configKey;

    /** 配置值 */
    @ApiModelProperty(value = "配置值", required = true, position = 2)
    @NotBlank(message = "configValue不能为空")
    private String configValue;

}
