package com.cj.waterresources.func.modular.useWaterPlanEscalation.useWaterManagement.entity;

import java.util.Date;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 用水单位管理(UseWaterManagement)表实体类
 *
 * @author makejava
 * @since 2023-11-28 17:14:41
 */
@Data
public class UseWaterManagement extends Model<UseWaterManagement> {

    @ApiModelProperty(value = "id")
    private String id;

    //用水计划(1-年 2-旬 3-日)
    @ApiModelProperty(value = "用水计划")
    private String useWaterPlan;

    //使用区域
    @ApiModelProperty(value = "区域")
    private String area;

    //父节点
    @ApiModelProperty(value = "父节点")
    private String pId;

    //单位名称
    @ApiModelProperty(value = "单位名称")
    private String unitName;

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
}

