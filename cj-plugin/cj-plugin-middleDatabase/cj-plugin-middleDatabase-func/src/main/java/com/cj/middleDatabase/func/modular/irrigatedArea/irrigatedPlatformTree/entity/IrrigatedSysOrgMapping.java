package com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformTree.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
* 
* @TableName IRRIGATED_SYS_ORG_MAPPING
*/
@TableName(value ="IRRIGATED_SYS_ORG_MAPPING")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "", description = "")
public class IrrigatedSysOrgMapping implements Serializable {

    @ApiModelProperty(value = "主键")
    private String id;
    @ApiModelProperty(value = "灌区平台id")
    private String irrigatedId;
    @ApiModelProperty(value = "组织id")
    private String sysId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
