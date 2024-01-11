package com.cj.project.api.instruments.dto;

import com.cj.common.page.PageEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * 项目仪器表查询参数
 *
 * @author yly
 * @date  2023/09/02 18:12
 **/
@Data
public class ProjectInstrumentsPageDto extends PageEntity {


    /** 项目编号 */
    @ApiModelProperty(value = "项目编号")
    private String projectCode;

    /** 项目仪器类型 */
    @ApiModelProperty(value = "项目仪器类型")
    private String instrumentType;

}
