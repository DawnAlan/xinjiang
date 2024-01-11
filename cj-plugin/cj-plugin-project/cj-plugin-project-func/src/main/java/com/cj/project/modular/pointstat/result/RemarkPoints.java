package com.cj.project.modular.pointstat.result;

import com.cj.project.api.fiducial.entity.FiducialBase;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 测点考证详情
 *
 * @author lb
 * @date 2023/9/18 9:28
 **/
@Getter
@Setter
public class RemarkPoints {

    /**
     * 测点考证状态
     */
    @ApiModelProperty(value = "测点考证状态", position = 1)
    public String fiducialRemark;

    /**
     * 考证详情
     */
    @ApiModelProperty(value = "考证详情", position = 2)
    public List<FiducialBase> details;

}

