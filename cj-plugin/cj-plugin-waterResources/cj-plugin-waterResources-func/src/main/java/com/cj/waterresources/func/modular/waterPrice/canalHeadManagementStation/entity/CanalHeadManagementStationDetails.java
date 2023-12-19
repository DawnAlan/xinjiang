package com.cj.waterresources.func.modular.waterPrice.canalHeadManagementStation.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serializable;

/**
 * 渠首管理站明细表(CanalHeadManagementStationDetails)表实体类
 *
 * @author makejava
 * @since 2023-12-15 18:07:49
 */
@Data
public class CanalHeadManagementStationDetails extends Model<CanalHeadManagementStationDetails> {
    //主键ID
    private String id;
    //日期
    private String data;
    //来水
    private Double incomingWater;
    //引水
    private Double diversion;
    //总干
    private Double totalDry;
    //西干
    private Double xiGan;
    //东岸合计
    private Double eastBankTotal;
    //东干
    private Double dongGan;
    //灯笼渠合计
    private Double lanternCanalTotal;
    //灯笼渠农业
    private Double lanternCanalAgriculture;
    //灯笼渠绿化
    private Double lanternCanalGreen;
    //灯笼渠工业
    private Double lanternCanalIndustry;
    //漏斗
    private Double funnel;
    //泄洪
    private Double floodDischarge;
    //年度
    private Integer year;
    //月份
    private Integer month;
    //旬
    private String tenDays;
}

