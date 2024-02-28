package dataExtraction.water.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "IRRIGATED_PLATFORM_DATA_INFO")
public class IRRIGATED_PLATFORM_DATA_INFO {
    private String monitorId;//监测点ID
    private String monitorTime;//时间
    private Double yqRainFallOne;//降雨量

    private Double sqWaterLevel;
    private Double sqMonitorFlowRate;
    private Double sqTotalFlow;
    private Double avgWaterDeep;
    private Double avgFlow;
    private Double yesterdayAvgFlow;
    private Double waterDaily;
    private Double yesterdayWaterDaily;
    private Double yearWaterDaily;
    private Double voltage;
    private Double sqcapacity;


    public IRRIGATED_PLATFORM_DATA_INFO() {
    }

    public IRRIGATED_PLATFORM_DATA_INFO(String monitorId, String monitorTime, Double yqRainFallOne,
                                        Double sqWaterLevel, Double sqMonitorFlowRate, Double sqTotalFlow, Double avgWaterDeep, Double avgFlow,
                                        Double yesterdayAvgFlow, Double waterDaily, Double yesterdayWaterDaily, Double yearWaterDaily, Double voltage, Double sqcapacity) {
        this.monitorId = monitorId;
        this.monitorTime = monitorTime;
        this.yqRainFallOne = yqRainFallOne;
        this.sqWaterLevel = sqWaterLevel;
        this.sqMonitorFlowRate = sqMonitorFlowRate;
        this.sqTotalFlow = sqTotalFlow;
        this.avgWaterDeep = avgWaterDeep;
        this.avgFlow = avgFlow;
        this.yesterdayAvgFlow = yesterdayAvgFlow;
        this.waterDaily = waterDaily;
        this.yesterdayWaterDaily = yesterdayWaterDaily;
        this.yearWaterDaily = yearWaterDaily;
        this.voltage = voltage;
        this.sqcapacity = sqcapacity;
    }

    @Id
    @Column(name = "MONITOR_ID")
    public String getMonitorId() {
        return monitorId;
    }

    public void setMonitorId(String monitorId) {
        this.monitorId = monitorId;
    }
    @Column(name = "MONITOR_TIME")
    public String getMonitorTime() {
        return monitorTime;
    }

    public void setMonitorTime(String monitorTime) {
        this.monitorTime = monitorTime;
    }
    @Column(name = "YQ_RAIN_FALL_ONE")
    public Double getYqRainFallOne() {
        return yqRainFallOne;
    }

    public void setYqRainFallOne(Double yqRainFallOne) {
        this.yqRainFallOne = yqRainFallOne;
    }

    ///
    @Column(name = "SQ_MONITOR_FLOW_RATE", precision = 9, scale = 3)
    public Double getSqMonitorFlowRate() {
        return sqMonitorFlowRate;
    }

    public void setSqMonitorFlowRate(Double sqMonitorFlowRate) {
        this.sqMonitorFlowRate = sqMonitorFlowRate;
    }
    @Column(name = "SQ_TOTAL_FLOW", precision = 9, scale = 3)
    public Double getSqTotalFlow() {
        return sqTotalFlow;
    }

    public void setSqTotalFlow(Double sqTotalFlow) {
        this.sqTotalFlow = sqTotalFlow;
    }
    @Column(name = "AVG_WATER_DEEP", precision = 9, scale = 3)
    public Double getAvgWaterDeep() {
        return avgWaterDeep;
    }

    public void setAvgWaterDeep(Double avgWaterDeep) {
        this.avgWaterDeep = avgWaterDeep;
    }
    @Column(name = "AVG_FLOW", precision = 9, scale = 3)
    public Double getAvgFlow() {
        return avgFlow;
    }

    public void setAvgFlow(Double avgFlow) {
        this.avgFlow = avgFlow;
    }
    @Column(name = "YESTERDAY_AVG_FLOW", precision = 9, scale = 3)
    public Double getYesterdayAvgFlow() {
        return yesterdayAvgFlow;
    }

    public void setYesterdayAvgFlow(Double yesterdayAvgFlow) {
        this.yesterdayAvgFlow = yesterdayAvgFlow;
    }
    @Column(name = "WATER_DAILY", precision = 9, scale = 3)
    public Double getWaterDaily() {
        return waterDaily;
    }

    public void setWaterDaily(Double waterDaily) {
        this.waterDaily = waterDaily;
    }
    @Column(name = "YESTERDAY_WATER_DAILY", precision = 9, scale = 3)
    public Double getYesterdayWaterDaily() {
        return yesterdayWaterDaily;
    }

    public void setYesterdayWaterDaily(Double yesterdayWaterDaily) {
        this.yesterdayWaterDaily = yesterdayWaterDaily;
    }
    @Column(name = "YEAR_WATER_DAILY", precision = 9, scale = 3)
    public Double getYearWaterDaily() {
        return yearWaterDaily;
    }

    public void setYearWaterDaily(Double yearWaterDaily) {
        this.yearWaterDaily = yearWaterDaily;
    }
    @Column(name = "VOLTAGE", precision = 9, scale = 3)
    public Double getVoltage() {
        return voltage;
    }

    public void setVoltage(Double voltage) {
        this.voltage = voltage;
    }

    @Column(name = "SQ_WATER_LEVEL", precision = 9, scale = 3)
    public Double getSqWaterLevel() {
        return sqWaterLevel;
    }
    public void setSqWaterLevel(Double sqWaterLevel) {
        this.sqWaterLevel = sqWaterLevel;
    }

    @Column(name = "SQ_CAPACITY", precision = 9, scale = 3)
    public Double getSqcapacity() {
        return sqcapacity;
    }

    public void setSqcapacity(Double sqcapacity) {
        this.sqcapacity = sqcapacity;
    }
}
