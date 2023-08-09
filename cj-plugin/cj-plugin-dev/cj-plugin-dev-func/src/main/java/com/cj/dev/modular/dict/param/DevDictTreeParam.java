
package com.cj.dev.modular.dict.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 字典树参数
 *
 * @author xuyuxiang
 * @date 2022/7/30 21:49
 */
@Getter
@Setter
public class DevDictTreeParam {

    /** 字典分类 */
    @ApiModelProperty(value = "字典分类")
    private String category;
}
