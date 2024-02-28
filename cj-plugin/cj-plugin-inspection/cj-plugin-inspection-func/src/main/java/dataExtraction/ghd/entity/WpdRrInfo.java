package dataExtraction.ghd.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "wpd_rr_info")
public class WpdRrInfo implements Serializable {

    private String id;
    private String name;
    private String pId;//节点ID
    private String typeName;//类型名称
    private String typeOf;//是否为末节点
    private String riverId;//河流
    private String typeZ;//类别
    private String latitude;//纬度
    private String longitude;//经度
    private String belongingWatershed;//所属单位

    private String position;//位置
    private String unit;//管理单位
    private String buildName;//建筑物名称
    private Double elevation;//高程
    private Double alertLevelU;//警戒水位上限
    private Double alertLevelB;//警戒水位下限
    private Double riseBfb;//陂涨百分比
    private Double superAlertLevel;//超警戒后报警水位差
    private String jumpAlarm;//跳变报警

    /**
     * 子节点信息
     */
    public List<WpdRrInfo> childList;

    public WpdRrInfo() {

    }
    public WpdRrInfo(String id, String name, String pId, String typeName, String typeOf, String riverId, String typeZ, String latitude, String longitude,
                     String belongingWatershed, String position, String unit, String buildName, Double elevation, Double alertLevelU,
                     Double alertLevelB, Double riseBfb, Double superAlertLevel, String jumpAlarm, List<WpdRrInfo> childList) {
        this.id = id;
        this.name = name;
        this.pId = pId;
        this.typeName = typeName;
        this.typeOf = typeOf;
        this.riverId = riverId;
        this.typeZ = typeZ;
        this.latitude = latitude;
        this.longitude = longitude;
        this.belongingWatershed = belongingWatershed;
        this.position = position;
        this.unit = unit;
        this.buildName = buildName;
        this.elevation = elevation;
        this.alertLevelU = alertLevelU;
        this.alertLevelB = alertLevelB;
        this.riseBfb = riseBfb;
        this.superAlertLevel = superAlertLevel;
        this.jumpAlarm = jumpAlarm;
        this.childList = childList;
    }

    @Id
    @Column(name = "id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    @Column(name = "p_id")
    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    @Column(name = "type_name")
    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    @Column(name = "type_of")
    public String getTypeOf() {
        return typeOf;
    }

    public void setTypeOf(String typeOf) {
        this.typeOf = typeOf;
    }

    @Column(name = "river_id")
    public String getRiverId() {
        return riverId;
    }

    public void setRiverId(String riverId) {
        this.riverId = riverId;
    }

    @Column(name = "type_z")
    public String getTypeZ() {
        return typeZ;
    }

    public void setTypeZ(String typeZ) {
        this.typeZ = typeZ;
    }

    @Column(name = "latitude")
    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    @Column(name = "longitude")
    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    @Column(name = "belonging_watershed")
    public String getBelongingWatershed() {
        return belongingWatershed;
    }

    public void setBelongingWatershed(String belongingWatershed) {
        this.belongingWatershed = belongingWatershed;
    }

    public void setChildren(List<WpdRrInfo> children) {
        if (children.size() == 0) {
            this.childList = new ArrayList<>();
        } else {
            this.childList = children;

        }
    }
    @Column(name = "position")
    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
    @Column(name = "unit")
    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
    @Column(name = "build_name")
    public String getBuildName() {
        return buildName;
    }

    public void setBuildName(String buildName) {
        this.buildName = buildName;
    }
    @Column(name = "elevation")
    public Double getElevation() {
        return elevation;
    }

    public void setElevation(Double elevation) {
        this.elevation = elevation;
    }
    @Column(name = "alert_level_u")
    public Double getAlertLevelU() {
        return alertLevelU;
    }

    public void setAlertLevelU(Double alertLevelU) {
        this.alertLevelU = alertLevelU;
    }
    @Column(name = "alert_level_b")
    public Double getAlertLevelB() {
        return alertLevelB;
    }

    public void setAlertLevelB(Double alertLevelB) {
        this.alertLevelB = alertLevelB;
    }
    @Column(name = "rise_bfb")
    public Double getRiseBfb() {
        return riseBfb;
    }

    public void setRiseBfb(Double riseBfb) {
        this.riseBfb = riseBfb;
    }
    @Column(name = "super_alert_level")
    public Double getSuperAlertLevel() {
        return superAlertLevel;
    }

    public void setSuperAlertLevel(Double superAlertLevel) {
        this.superAlertLevel = superAlertLevel;
    }
    @Column(name = "jump_alarm")
    public String getJumpAlarm() {
        return jumpAlarm;
    }

    public void setJumpAlarm(String jumpAlarm) {
        this.jumpAlarm = jumpAlarm;
    }
}
