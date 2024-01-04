package com.cj.waterresources.func.modular.waterPrice.paymentWaterFees.entity;

import java.util.Date;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 水费缴纳(PaymentWaterFees)表实体类
 *
 * @author makejava
 * @since 2023-11-29 11:28:30
 */
@Data
public class PaymentWaterFees extends Model<PaymentWaterFees> {
    //主键ID
    @ApiModelProperty(value = "主键ID")
    private String id;

    //管理站
    @ApiModelProperty(value = "管理站")
    private String station;

    //用水户名称
    @ApiModelProperty(value = "用水户名称")
    private String waterUserName;

    //用水户ID
    @ApiModelProperty(value = "用水户ID")
    private String waterUserId;

    //缴费金额
    @ApiModelProperty(value = "缴费金额")
    private Double paymentAmount;

    //缴费时间
    @ApiModelProperty(value = "缴费时间")
    private Date paymentTime;

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

    //水费类型
    @ApiModelProperty(value = "水费类型")
    private String type;

    //缴费年度
    @ApiModelProperty(value = "缴费年度")
    private Integer year;

}

