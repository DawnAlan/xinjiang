package com.cj.project.modular.fiducial.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * 测点考证表Id参数
 *
 * @author Lb
 * @date  2023/09/04 12:25
 **/
@Getter
@Setter
@Builder
public class FiducialIdParam {

    /** ID */
    @ApiModelProperty(value = "ID", required = true)
    // @NotBlank(message = "id不能为空")
    private String id;
}
