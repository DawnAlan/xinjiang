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
package com.cj.project.modular.treemodel.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 测点树编辑参数
 *
 * @author Lb
 * @date  2023/09/14 16:41
 **/
@Getter
@Setter
public class TreeModelEditParam {

    /** 节点ID */
    @ApiModelProperty(value = "节点ID", required = true, position = 1)
    @NotBlank(message = "id不能为空")
    private String id;

    /** 项目Code */
    @ApiModelProperty(value = "项目Code", position = 2)
    private String projectCode;

    /** 节点名称 */
    @ApiModelProperty(value = "节点名称", position = 3)
    private String parentId;

    /** 节点父ID */
    @ApiModelProperty(value = "节点父ID", position = 4)
    private String nodeName;

    /** 节点备注 */
    @ApiModelProperty(value = "节点备注", position = 5)
    private String nodeDescr;

    /** 是否为最末级节点 */
    @ApiModelProperty(value = "是否为最末级节点", position = 6)
    private Integer isEnd;

    /** 节点描述 */
    @ApiModelProperty(value = "节点描述", position = 7)
    private String nodeInfo;

    /** 树目录类型 */
    @ApiModelProperty(value = "树目录类型", position = 8)
    private String category;

    /** 节点排序 */
    @ApiModelProperty(value = "节点排序", position = 9)
    private Integer sortCode;

}
