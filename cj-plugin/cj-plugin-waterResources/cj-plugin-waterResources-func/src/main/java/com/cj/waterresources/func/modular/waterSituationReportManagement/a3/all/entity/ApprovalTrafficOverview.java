package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 审批管理流量概览表(ApprovalTrafficOverview)表实体类
 *
 * @author makejava
 * @since 2024-04-09 16:35:43
 */
@Data
public class ApprovalTrafficOverview extends Model<ApprovalTrafficOverview> {
    //主键ID
    @ApiModelProperty(value = "主键ID")
    private String id;
    //站点名称
    @ApiModelProperty(value = "站点名称")
    private String stationName;
    //站点id
    @ApiModelProperty(value = "站点id")
    private String stationId;
    //站点父节点id
    @ApiModelProperty(value = "站点父节点id")
    private String stationPid;
    //早8流量
    @ApiModelProperty(value = "早8流量")
    private Double eightFlow;
    //加减流量
    @ApiModelProperty(value = "加减流量")
    private Double addSubtractFlow;
    //计划流量
    @ApiModelProperty(value = "计划流量")
    private Double planFlow;
    //模型流量
    @ApiModelProperty(value = "模型流量")
    private Double modelFlow;
    //拟定流量
    @ApiModelProperty(value = "拟定流量")
    private Double draftFlow;
    //审定流量
    @ApiModelProperty(value = "审定流量")
    private Double approvalFlow;
    //概览id
    @ApiModelProperty(value = "概览id")
    private String overviewId;
    //水库
    @ApiModelProperty(value = "水库")
    private String reservoir;



}

