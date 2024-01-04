package com.cj.project.modular.fiducial.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 考证参数表实体
 *
 * @author Lb
 * @date  2023/09/04 19:57
 **/
@Getter
@Setter
@TableName("fiducial_para")
public class FiducialPara {

    /** ID */
    @TableId
    @ApiModelProperty(value = "ID", position = 1)
    private String id;

    /** 测点编号 */
    @ApiModelProperty(value = "测点编号", position = 2)
    private String pointId;

    /** 考证字段 */
    @ApiModelProperty(value = "考证字段", position = 3)
    private String fieldKey;

    /** 考证值 */
    @ApiModelProperty(value = "考证值", position = 4)
    private String fieldValue;

}
