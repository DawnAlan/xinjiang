package dataExtraction.ghd.entity;

import javax.persistence.*;

@Entity
@Table(name = "wpd_jl_data")

public class WpdJlData implements java.io.Serializable {
    // Fields

    private WpdJlDataId id;
    private Double rz;
    private Double inq;
    private Double w;
    private Double blrz;
    private Double otq;
    private String rwchrcd;
    private String rwptn;
    private Double inqdr;
    private String msqmt;

    private Double sqMonitorFlowRate;
    private Double sqTotalFlow;
    private Double avgWaterDeep;
    private Double avgFlow;
    private Double yesterdayAvgFlow;
    private Double waterDaily;
    private Double yesterdayWaterDaily;
    private Double yearWaterDaily;
    private Double voltage;

    private Double sqCapacity;
    // Constructors

    /** default constructor */
    public WpdJlData() {
    }

    /** minimal constructor */
    public WpdJlData(WpdJlDataId id) {
        this.id = id;
    }

    public WpdJlData(WpdJlDataId id, Double rz, Double inq, Double w, Double blrz, Double otq, String rwchrcd,
                     String rwptn, Double inqdr, String msqmt, Double sqMonitorFlowRate, Double sqTotalFlow,
                     Double avgWaterDeep, Double avgFlow, Double yesterdayAvgFlow, Double waterDaily,
                     Double yesterdayWaterDaily, Double yearWaterDaily, Double voltage,Double sqCapacity) {
        this.id = id;
        this.rz = rz;
        this.inq = inq;
        this.w = w;
        this.blrz = blrz;
        this.otq = otq;
        this.rwchrcd = rwchrcd;
        this.rwptn = rwptn;
        this.inqdr = inqdr;
        this.msqmt = msqmt;
        this.sqMonitorFlowRate = sqMonitorFlowRate;
        this.sqTotalFlow = sqTotalFlow;
        this.avgWaterDeep = avgWaterDeep;
        this.avgFlow = avgFlow;
        this.yesterdayAvgFlow = yesterdayAvgFlow;
        this.waterDaily = waterDaily;
        this.yesterdayWaterDaily = yesterdayWaterDaily;
        this.yearWaterDaily = yearWaterDaily;
        this.voltage = voltage;

        this.sqCapacity = sqCapacity;
    }

    // Property accessors
    @EmbeddedId

    @AttributeOverrides({ @AttributeOverride(name = "tm", column = @Column(name = "TM", nullable = false, length = 19)),
            @AttributeOverride(name = "stcd", column = @Column(name = "STCD", nullable = false, length = 200)) })

    public WpdJlDataId getId() {
        return this.id;
    }

    public void setId(WpdJlDataId id) {
        this.id = id;
    }

    @Column(name = "RZ", precision = 7, scale = 3)

    public Double getRz() {
        return this.rz;
    }

    public void setRz(Double rz) {
        this.rz = rz;
    }

    @Column(name = "INQ", precision = 9, scale = 3)

    public Double getInq() {
        return this.inq;
    }

    public void setInq(Double inq) {
        this.inq = inq;
    }

    @Column(name = "W", precision = 9, scale = 3)

    public Double getW() {
        return this.w;
    }

    public void setW(Double w) {
        this.w = w;
    }

    @Column(name = "BLRZ", precision = 7, scale = 3)

    public Double getBlrz() {
        return this.blrz;
    }

    public void setBlrz(Double blrz) {
        this.blrz = blrz;
    }

    @Column(name = "OTQ", precision = 9, scale = 3)

    public Double getOtq() {
        return this.otq;
    }

    public void setOtq(Double otq) {
        this.otq = otq;
    }

    @Column(name = "RWCHRCD", length = 1)

    public String getRwchrcd() {
        return this.rwchrcd;
    }

    public void setRwchrcd(String rwchrcd) {
        this.rwchrcd = rwchrcd;
    }

    @Column(name = "RWPTN", length = 1)

    public String getRwptn() {
        return this.rwptn;
    }

    public void setRwptn(String rwptn) {
        this.rwptn = rwptn;
    }

    @Column(name = "INQDR", precision = 5)

    public Double getInqdr() {
        return this.inqdr;
    }

    public void setInqdr(Double inqdr) {
        this.inqdr = inqdr;
    }

    @Column(name = "MSQMT", length = 1)

    public String getMsqmt() {
        return this.msqmt;
    }

    public void setMsqmt(String msqmt) {
        this.msqmt = msqmt;
    }

    /**
     * toString
     *
     * @return String
     */
    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append(getClass().getName()).append("@").append(Integer.toHexString(hashCode())).append(" [");
        buffer.append("id").append("='").append(getId()).append("' ");
        buffer.append("rz").append("='").append(getRz()).append("' ");
        buffer.append("inq").append("='").append(getInq()).append("' ");
        buffer.append("w").append("='").append(getW()).append("' ");
        buffer.append("blrz").append("='").append(getBlrz()).append("' ");
        buffer.append("otq").append("='").append(getOtq()).append("' ");
        buffer.append("rwchrcd").append("='").append(getRwchrcd()).append("' ");
        buffer.append("rwptn").append("='").append(getRwptn()).append("' ");
        buffer.append("inqdr").append("='").append(getInqdr()).append("' ");
        buffer.append("msqmt").append("='").append(getMsqmt()).append("' ");
        buffer.append("]");

        return buffer.toString();
    }
    @Column(name = "SQ_MONITOR_FLOW_RATE", precision = 11, scale = 3)
    public Double getSqMonitorFlowRate() {
        return sqMonitorFlowRate;
    }

    public void setSqMonitorFlowRate(Double sqMonitorFlowRate) {
        this.sqMonitorFlowRate = sqMonitorFlowRate;
    }
    @Column(name = "SQ_TOTAL_FLOW", precision = 11, scale = 3)
    public Double getSqTotalFlow() {
        return sqTotalFlow;
    }

    public void setSqTotalFlow(Double sqTotalFlow) {
        this.sqTotalFlow = sqTotalFlow;
    }
    @Column(name = "AVG_WATER_DEEP", precision = 11, scale = 3)
    public Double getAvgWaterDeep() {
        return avgWaterDeep;
    }

    public void setAvgWaterDeep(Double avgWaterDeep) {
        this.avgWaterDeep = avgWaterDeep;
    }
    @Column(name = "AVG_FLOW", precision = 11, scale = 3)
    public Double getAvgFlow() {
        return avgFlow;
    }

    public void setAvgFlow(Double avgFlow) {
        this.avgFlow = avgFlow;
    }
    @Column(name = "YESTERDAY_AVG_FLOW", precision = 11, scale = 3)
    public Double getYesterdayAvgFlow() {
        return yesterdayAvgFlow;
    }

    public void setYesterdayAvgFlow(Double yesterdayAvgFlow) {
        this.yesterdayAvgFlow = yesterdayAvgFlow;
    }
    @Column(name = "WATER_DAILY", precision = 11, scale = 3)
    public Double getWaterDaily() {
        return waterDaily;
    }

    public void setWaterDaily(Double waterDaily) {
        this.waterDaily = waterDaily;
    }
    @Column(name = "YESTERDAY_WATER_DAILY", precision = 11, scale = 3)
    public Double getYesterdayWaterDaily() {
        return yesterdayWaterDaily;
    }

    public void setYesterdayWaterDaily(Double yesterdayWaterDaily) {
        this.yesterdayWaterDaily = yesterdayWaterDaily;
    }
    @Column(name = "YEAR_WATER_DAILY", precision = 11, scale = 3)
    public Double getYearWaterDaily() {
        return yearWaterDaily;
    }

    public void setYearWaterDaily(Double yearWaterDaily) {
        this.yearWaterDaily = yearWaterDaily;
    }
    @Column(name = "VOLTAGE", precision = 11, scale = 3)
    public Double getVoltage() {
        return voltage;
    }

    public void setVoltage(Double voltage) {
        this.voltage = voltage;
    }

    @Column(name = "SQ_CAPACITY", precision = 9, scale = 3)
    public Double getSqCapacity() {
        return sqCapacity;
    }

    public void setSqCapacity(Double sqCapacity) {
        this.sqCapacity = sqCapacity;
    }
}
