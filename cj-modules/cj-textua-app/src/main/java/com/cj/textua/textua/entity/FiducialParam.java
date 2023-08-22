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
package com.cj.textua.textua.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 考证参数信息表(跟字段配置表关联)实体
 *
 * @author yancheng
 * @date  2023/08/21 20:50
 **/
@Getter
@Setter
@TableName("fiducial_param")
public class FiducialParam {

    /** 主键UID */
    @TableId
    @ApiModelProperty(value = "主键UID", position = 1)
    private String id;

    /** 测点UID */
    @ApiModelProperty(value = "测点UID", position = 2)
    private String pointid;

    /** 考证字段 */
    @ApiModelProperty(value = "考证字段", position = 3)
    private String fieldname;

    /** 考证数据 */
    @ApiModelProperty(value = "考证数据", position = 4)
    private String fieldvalue;
}
