package com.cj.waterresources.func.modular.waterPrice.waterPriceManagement.entity;

import java.util.Date;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 水价管理(WaterPriceManagement)表实体类
 *
 * @author makejava
 * @since 2023-11-29 10:44:39
 */
@Data
public class WaterPriceManagement extends Model<WaterPriceManagement> {
    //主键ID
    @ApiModelProperty(value = "主键ID")
    private String id;

    //用水类型
    @ApiModelProperty(value = "用水类型")
    private String useWaterType;

    //管理站
    @ApiModelProperty(value = "管理站")
    private String station;

    //取水口/用水户
    @ApiModelProperty(value = "取水口/用水户")
    private String userName;

    //水价
    @ApiModelProperty(value = "水价")
    private Double waterPrice;

    //创建时间
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    //创建人
    @ApiModelProperty(value = "创建人")
    private String createBy;

    //更新时间
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    //更新人
    @ApiModelProperty(value = "更新人")
    private String updateBy;

    //逻辑删除(0-否 1-是)
    @ApiModelProperty(value = "逻辑删除(0-否 1-是)")
    private Integer del;

    @ApiModelProperty(value = "父ID")
    private String pId;

    @ApiModelProperty(value = "定额水量")
    private Double quotaWaterQuantity;

    @ApiModelProperty(value = "定额水价")
    private Double fixedWaterPrice;

    @ApiModelProperty(value = "第一阶段标准")
    private Double firstLevelStandard;

    @ApiModelProperty(value = "第一阶段价格")
    private Double firstTierPrice;

    @ApiModelProperty(value = "第二阶段标准")
    private Double secondLevelStandard;

    @ApiModelProperty(value = "第二阶段价格")
    private Double secondTierPrice;

    @ApiModelProperty(value = "第三阶段标准")
    private Double thirdLevelStandard;

    @ApiModelProperty(value = "第三阶段价格")
    private Double thirdTierPrice;

    @ApiModelProperty(value = "水资源费")
    private Double waterResourcePrice;

}

