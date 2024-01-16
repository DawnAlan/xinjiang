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
package com.cj.project.modular.FiducialEnterA.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * FiducialEnterA编辑参数
 *
 * @author Lb
 * @date  2023/11/23 10:20
 **/
@Getter
@Setter
public class ConfigProjectPointEditParam {

    /** ID */
    @ApiModelProperty(value = "ID", required = true, position = 1)
    @NotBlank(message = "id不能为空")
    private String id;

    /** PROJECTCODE */
    @ApiModelProperty(value = "PROJECTCODE", position = 2)
    private String projectcode;

    /** POINTTYPE */
    @ApiModelProperty(value = "POINTTYPE", position = 3)
    private String pointtype;

    /** POINTID */
    @ApiModelProperty(value = "POINTID", position = 4)
    private String pointid;

    /** POINTNAME */
    @ApiModelProperty(value = "POINTNAME", position = 5)
    private String pointname;

    /** GZFS */
    @ApiModelProperty(value = "GZFS", position = 6)
    private String gzfs;

    /** GZZT */
    @ApiModelProperty(value = "GZZT", position = 7)
    private String gzzt;

    /** ZDJK */
    @ApiModelProperty(value = "ZDJK", position = 8)
    private String zdjk;

    /** STARTTIME */
    @ApiModelProperty(value = "STARTTIME", position = 9)
    private Date starttime;

    /** ZH */
    @ApiModelProperty(value = "ZH", position = 10)
    private String zh;

    /** AZGC */
    @ApiModelProperty(value = "AZGC", position = 11)
    private String azgc;

    /** SCCJ */
    @ApiModelProperty(value = "SCCJ", position = 12)
    private String sccj;

    /** ISRENAME */
    @ApiModelProperty(value = "ISRENAME", position = 13)
    private String isrename;

    /** RENAME */
    @ApiModelProperty(value = "RENAME", position = 14)
    private String rename;

    /** INSTRUMENT_TYPE */
    @ApiModelProperty(value = "INSTRUMENT_TYPE", position = 15)
    private String instrumentType;

    /** INSTRUMENT_NAME */
    @ApiModelProperty(value = "INSTRUMENT_NAME", position = 16)
    private String instrumentName;

    /** INSTRUMENT_METATYPE */
    @ApiModelProperty(value = "INSTRUMENT_METATYPE", position = 17)
    private String instrumentMetatype;

    /** POINTITEMPROJECT */
    @ApiModelProperty(value = "POINTITEMPROJECT", position = 18)
    private String pointitemproject;

}
