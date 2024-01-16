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
package com.cj.data.api.artdata.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 模板列参数表实体
 *
 * @author dd
 * @date  2024/01/12 17:25
 **/
@Getter
@Setter
@TableName("ARTDATA_COLUMNCONFIG_PARAMETER")
public class ArtdataColumnconfigParameter {

    /** id */
    @TableId
    @ApiModelProperty(value = "id", position = 1)
    private String id;

    /** 配置id */
    @ApiModelProperty(value = "配置id", position = 2)
    private String columnconfigId;

    /** 参数名 */
    @ApiModelProperty(value = "参数名", position = 3)
    private String parameterName;

    /** 参数位置 */
    @ApiModelProperty(value = "参数位置", position = 4)
    private String parameterAt;
}
