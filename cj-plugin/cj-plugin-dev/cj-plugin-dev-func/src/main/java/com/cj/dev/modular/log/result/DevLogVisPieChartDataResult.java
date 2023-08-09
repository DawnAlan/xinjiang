
package com.cj.dev.modular.log.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 访问日志饼状图数据结果
 *
 * @author xuyuxiang
 * @date 2022/9/4 21:14
 */
@Getter
@Setter
public class DevLogVisPieChartDataResult {

    /** 类型 */
    @ApiModelProperty(value = "类型", position = 1)
    private String type;

    /** 数量 */
    @ApiModelProperty(value = "数量", position = 2)
    private Long value;
}
