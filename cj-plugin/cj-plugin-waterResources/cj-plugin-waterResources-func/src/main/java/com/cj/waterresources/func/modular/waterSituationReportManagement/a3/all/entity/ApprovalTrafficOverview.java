package com.cj.waterresources.func.modular.waterSituationReportManagement.a3.all.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;
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
    private String id;
    //站点
    private String stationName;
    //父节点id
    private String pid;
    //早8流量
    private Double eightFlow;
    //加减流量
    private Double addSubtractFlow;
    //计划流量
    private Double planLfow;
    //模型流量
    private Double modelFlow;
    //拟定流量
    private Double draftFlow;
    //审定流量
    private Double approvalFlow;
    //概览id
    private String overviewId;



}

