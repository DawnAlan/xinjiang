package com.cj.project.modular.pointstat.result;

import com.cj.common.pojo.bizPojo.TableColumn;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;


/**
 * 考证统计结果
 *
 * @author lb
 * @date 2023/9/18 9:28
 **/
@Getter
@Setter
public class PointStatResult {

    /** 统计汇总 */
    @ApiModelProperty(value = "统计汇总", position = 1)
    private Map<String, Object> resultSUM;

    /** 测点统计表格字段 */
    @ApiModelProperty(value = "测点统计表格字段", position = 2)
    private List<TableColumn> tableColumns;

    /** 测点统计详情 */
    @ApiModelProperty(value = "测点统计详情", position = 3)
    private List<Map<String, Object>> pointStatTable;

    /** 测点考证详情 */
    @ApiModelProperty(value = "测点考证详情", position = 4)
    private List<RemarkPoints> remarkPoints;


}


