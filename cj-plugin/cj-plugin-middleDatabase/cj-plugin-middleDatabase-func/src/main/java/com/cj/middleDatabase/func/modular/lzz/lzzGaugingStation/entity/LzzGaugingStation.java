package com.cj.middleDatabase.func.modular.lzz.lzzGaugingStation.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 水位站数据表(LzzGaugingStation)表实体类
 *
 * @author makejava
 * @since 2023-12-05 17:55:45
 */
@Data
public class LzzGaugingStation extends Model<LzzGaugingStation> {
    //主键ID
    private String id;
    //站点名
    private String stationName;
    //相对水位
    private Double relativeWaterLevel;
    //相对流量
    private Double flow;
    //采集时间
    private Date gatherTime;
    //温度
    private Double temperature;
    //实时库容
    private Double storageCapacity;
    //树结构ID
    private String treeId;
    //记录时间1
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    private Date recordTime;

    private Double flowRate;
    private Double totalFlow;
    private Double relativeWaterLevelTwo;
    private Double flowTwo;
    private Double flowRateTwo;
    private Double relativeWaterLevelThree;
    private Double flowThree;
    private Double flowRateThree;
    private Double totalFlowTwo;
    private Double totalFlowThree;
}

