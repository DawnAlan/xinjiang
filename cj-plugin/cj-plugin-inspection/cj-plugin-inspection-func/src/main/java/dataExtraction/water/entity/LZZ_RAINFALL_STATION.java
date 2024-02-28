package dataExtraction.water.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

//楼庄子雨量站降雨量
@Entity
@Table(name = "LZZ_RAINFALL_STATION")
public class LZZ_RAINFALL_STATION {

    private String id;
    private LocalDateTime time;
    private Double rainfall;
    private String treeId;

    public LZZ_RAINFALL_STATION() {
    }

    public LZZ_RAINFALL_STATION(String id, LocalDateTime time, Double rainfall,String treeId) {
        this.id = id;
        this.time = time;
        this.rainfall = rainfall;
        this.treeId = treeId;
    }

    @Id
    @Column(name = "ID")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    @Column(name = "TIME")
    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }
    @Column(name = "RAINFALL")
    public Double getRainfall() {
        return rainfall;
    }

    public void setRainfall(Double rainfall) {
        this.rainfall = rainfall;
    }
    @Column(name = "TREE_ID")
    public String getTreeId() {
        return treeId;
    }

    public void setTreeId(String treeId) {
        this.treeId = treeId;
    }
}
