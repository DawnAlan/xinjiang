package dataExtraction.water.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

//楼庄子水库-水位站数据
@Entity
@Table(name = "LZZ_GAUGING_STATION")
public class LZZ_GAUGING_STATION {
    private String id;
    private String relativeWaterLevel;//水位
    private String flow;//流量
    private LocalDateTime gatherTime;
    private String treeId;

    public LZZ_GAUGING_STATION(String id, String relativeWaterLevel, String flow, LocalDateTime gatherTime, String treeId) {
        this.id = id;
        this.relativeWaterLevel = relativeWaterLevel;
        this.flow = flow;
        this.gatherTime = gatherTime;
        this.treeId = treeId;
    }

    public LZZ_GAUGING_STATION() {

    }

    @Id
    @Column(name = "ID")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    @Column(name = "RELATIVE_WATER_LEVEL")
    public String getRelativeWaterLevel() {
        return relativeWaterLevel;
    }

    public void setRelativeWaterLevel(String relativeWaterLevel) {
        this.relativeWaterLevel = relativeWaterLevel;
    }
    @Column(name = "FLOW")
    public String getFlow() {
        return flow;
    }

    public void setFlow(String flow) {
        this.flow = flow;
    }
    @Column(name = "GATHER_TIME")
    public LocalDateTime getGatherTime() {
        return gatherTime;
    }

    public void setGatherTime(LocalDateTime gatherTime) {
        this.gatherTime = gatherTime;
    }
    @Column(name = "TREE_ID")
    public String getTreeId() {
        return treeId;
    }

    public void setTreeId(String treeId) {
        this.treeId = treeId;
    }

}
