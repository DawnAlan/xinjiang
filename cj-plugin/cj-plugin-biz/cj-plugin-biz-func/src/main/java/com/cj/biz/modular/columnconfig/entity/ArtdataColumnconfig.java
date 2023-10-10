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
package com.cj.biz.modular.columnconfig.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 格式配置表实体
 *
 * @author dengdi
 * @date  2023/08/22 10:10
 **/
@Getter
@Setter
@TableName("artdata_columnconfig")
public class ArtdataColumnconfig {

    /** 配置id */
    @TableId
    @ApiModelProperty(value = "配置id", position = 1)
    private String uid;

    /** 测点id */
    @ApiModelProperty(value = "测点id", position = 2)
    private String pointld;

    /** 测点编号 */
    @ApiModelProperty(value = "测点编号", position = 3)
    private String pointname;

    /** 仪器类型 */
    @ApiModelProperty(value = "仪器类型", position = 4)
    private String instrumentname;

    /** 所在文件地址 */
    @ApiModelProperty(value = "所在文件地址", position = 5)
    private String filepath;

    /** 所在sheet */
    @ApiModelProperty(value = "所在sheet", position = 6)
    private String sheetat;

    /** 读取方向 */
    @ApiModelProperty(value = "读取方向", position = 7)
    private String readdirection;

    /** 测点编号读取位置 */
    @ApiModelProperty(value = "测点编号读取位置", position = 8)
    private String pointat;

    /** 基准行列 */
    @ApiModelProperty(value = "基准行列", position = 9)
    private Integer baselineat;

    /** 观测日期位置 */
    @ApiModelProperty(value = "观测日期位置", position = 10)
    private Integer observationdateat;

    /** 备注位置 */
    @ApiModelProperty(value = "备注位置", position = 11)
    private Integer remarkat;

    /** 创建时间 */
    @ApiModelProperty(value = "创建时间", position = 12)
    private Date createdate;

    /** 创建人 */
    @ApiModelProperty(value = "创建人", position = 13)
    private String createusername;

    /** 修改时间 */
    @ApiModelProperty(value = "修改时间", position = 14)
    private Date modifydate;

    /** 修改人 */
    @ApiModelProperty(value = "修改人", position = 15)
    private String modifyusername;
}
