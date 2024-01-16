package com.cj.data.modular.dataResult.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 数据成果表查询参数
 *
 * @author Lb
 * @date  2023/10/23 16:51
 **/
@Getter
@Setter
public class DataValuePageParam {

    /** 每页条数 */
    @ApiModelProperty(value = "每页条数")
    private Integer size = 10;

    /** 当前页 */
    @ApiModelProperty(value = "当前页码")
    private Integer current;

    /** 数据记录ID */
    @ApiModelProperty(value = "数据记录ID集合")
    private List<String> dataIdParamList;

}
