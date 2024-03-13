package com.cj.middleDatabase.func.modular.irrigatedArea.irrigatedPlatformDataInfo.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;
//import javafx.beans.binding.DoubleExpression;
import lombok.Data;

import java.io.Serializable;

/**
 * 灌区平台时刻信息表(IrrigatedPlatformDataInfo)表实体类
 *
 * @author makejava
 * @since 2023-12-07 12:15:03
 */
@Data
public class IrrigatedPlatformDataInfo extends Model<IrrigatedPlatformDataInfo> {
    
    private String id;
    //今日水量
    private Double waterDaily;
    //昨日平均流量
    private Double yesterdayAvgFlow;
    //监测点
    private String monitorName;
    //累计流量
    private Double sqTotalFlow;
    //瞬时流量
    private Double sqMonitorFlow;
    //日平均水深
    private Double avgWaterDeep;
    //日平均水位
    private Double avgWaterLevel;
    //瞬时流速
    private Double sqMonitorFlowRate;
    //开始记录时刻
    private String beginTime;
    //昨日水量
    private Double yesterdayWaterDaily;
    //记录时间
    private String monitorTime;
    //水深
    private Double sqWaterLevel;
    //设备电压
    private Double voltage;
    //日平均流量
    private Double avgFlow;
    //年用水量
    private Double yearWaterDaily;
    //今日降雨量
    private Double qxRainFall;
    //1小时降雨量
    private Double yqRainFallOne;
    //3小时降雨量
    private Double yqRainFallThree;
    //6小时降雨量
    private Double yqRainFallSix;
    //12小时降雨量
    private Double yqRainFallTwelve;
    //24小时降雨量
    private Double yqRainFallTwentyFour;
    //管道压力
    private Double gdPipePressure;
    //管道瞬时流量
    private Double gdMonitorFlow;
    //管道瞬时流速
    private Double gdMonitorFlowRate;
    //是否是空管
    private String gdIsNullPipe;
    //管道累计流量
    private Double gdTotalFlow;

    private String monitorId;

    private Double sqCapacity;
}

