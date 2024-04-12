package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.entity;

import java.util.Date;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 流量概览表(ApprovalTrafficOverviewTable)表实体类
 *
 * @author makejava
 * @since 2024-04-09 16:34:48
 */
@Data
public class ApprovalTrafficOverviewTable extends Model<ApprovalTrafficOverviewTable> {
    //主键id
    @ApiModelProperty(value = "主键id")
    private String id;
    //方案名称
    @ApiModelProperty(value = "方案名称")
    private String name;
    //创建方案时间
    @ApiModelProperty(value = "创建方案时间")
    private Date time;
    //模型id
    @ApiModelProperty(value = "模型id")
    private String modelId;

    @ApiModelProperty(value = "模型名称")
    private String modelName;

}

