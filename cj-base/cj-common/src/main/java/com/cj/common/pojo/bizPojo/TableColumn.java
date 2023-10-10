package com.cj.common.pojo.bizPojo;


import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 *     通用表格列字段对象：列名称字段、列名字中文
 * </p>
 *
 * @author LB
 * @date 2023/9/18 12:02
 */
@Getter
@Setter
public class TableColumn {

    /**
     * 列字段
     */
    @ApiModelProperty(value = "列字段", position = 1)
    public String columnName;

    /**
     * 列字段释义
     */
    @ApiModelProperty(value = "列字段释义", position = 2)
    public String columnComment;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注", position = 3)
    public String extJson;

    public TableColumn(String columnName, String columnComment, String extJson) {
        this.columnName = columnName;
        this.columnComment = columnComment;
        this.extJson = extJson;
    }
}
