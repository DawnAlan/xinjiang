/*
 * Copyright [2022] [https://www.xiaonuo.vip]
 *
 * Snowy采用APACHE LICENSE 2.0开源协议，您在使用过程中，需要注意以下几点：
 *
 * 1.请不要删除和修改根目录下的LICENSE文件。
 * 2.请不要删除和修改Snowy源码头部的版权声明。
 * 3.本项目代码可免费商业使用，商业使用请保留源码和相关描述文件的项目出处，作者声明等。
 * 4.分发源码时候，请注明软件出处 https://www.xiaonuo.vip
 * 5.不可二次分发开源参与同类竞品，如有想法可联系团队xiaonuobase@qq.com商议合作。
 * 6.若您的项目无法满足以上几点，需要更多功能代码，获取Snowy商业授权许可，请在官网购买授权，地址为 https://www.xiaonuo.vip
 */
package com.cj.project.modular.fiducial.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.Map;

/**
 * 测点考证表添加参数
 *
 * @author Lb
 * @date  2023/09/04 12:25
 **/
@Getter
@Setter
public class FiducialAddParam {

    /** 项目编号 */
    @ApiModelProperty(value = "项目编号", required = true, position = 1)
    @NotBlank(message = "projectCode不能为空")
    private String projectCode;

    /** 项目仪器类型 */
    @ApiModelProperty(value = "项目仪器类型", required = true, position = 2)
    @NotBlank(message = "instrumentType不能为空")
    private String instrumentType;

    /** 测点编号 */
    @ApiModelProperty(value = "测点编号", required = true, position = 3)
    @NotBlank(message = "pointName不能为空")
    private String pointName;

    /** 测点详情 */
    @ApiModelProperty(value = "测点详情", required = true, position = 4)
    private Map<String, Object> detail;

}
