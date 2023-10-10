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
package com.cj.project.modular.instruments.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 项目仪器表添加参数
 *
 * @author Lb
 * @date  2023/09/02 18:12
 **/
@Getter
@Setter
public class ProjectInstrumentsAddParam {

    /** 项目编号 */
    @ApiModelProperty(value = "项目编号", position = 2)
    private String projectCode;

    /** 监测类型 */
    @ApiModelProperty(value = "监测类型", position = 3)
    private String monitorName;

    /** 项目仪器类型 */
    @ApiModelProperty(value = "项目仪器类型", position = 4)
    private String instrumentType;

    /** 平台仪器类型 */
    @ApiModelProperty(value = "平台仪器类型", position = 5)
    private String instrumentMetaType;

    /** 仪器编码 */
    @ApiModelProperty(value = "仪器编码", position = 6)
    private String instrumentCode;

    /** 设计埋设量 */
    @ApiModelProperty(value = "设计埋设量", position = 7)
    private Integer designCount;

    /** 别名关键词 */
    @ApiModelProperty(value = "别名关键词", position = 8)
    private String nameKeys;

}
