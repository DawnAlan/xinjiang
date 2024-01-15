package com.cj.data.modular.dataResult.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 数据成果表实体
 *
 * @author Lb
 * @date  2023/10/23 16:51
 **/
@Getter
@Setter
@TableName("data_value")
public class DataValue {

    /** ID */
    @TableId
    @ApiModelProperty(value = "ID", position = 1)
    private String id;

    /** 数据记录ID */
    @ApiModelProperty(value = "数据记录ID", position = 2)
    private String dataId;

    /** 字段 */
    @ApiModelProperty(value = "字段", position = 3)
    private String fieldKey;

    /** 字段值 */
    @ApiModelProperty(value = "字段值", position = 4)
    private Float fieldValue;

    /** 粗差标识 */
    @ApiModelProperty(value = "粗差标识", position = 5)
    private String errorMark;

    /** 粗差备注 */
    @ApiModelProperty(value = "粗差备注", position = 6)
    private String errorNote;

    /** 预警标识 */
    @ApiModelProperty(value = "预警标识", position = 7)
    private String warnMark;

    /** 预警备注 */
    @ApiModelProperty(value = "预警备注", position = 8)
    private String warnNote;
}
