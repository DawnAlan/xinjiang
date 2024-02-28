package dataExtraction.ghd.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "wpd_curved_ass")
public class WpdCurvedAss implements Serializable {

    private String id;
    private String name;
    private String ndcdId;
    private String dataId;
    private String enable;
    private LocalDateTime time;

    public WpdCurvedAss(){

    }

    public WpdCurvedAss(String id, String name, String ndcdId, String dataId, String enable, LocalDateTime time) {
        this.id = id;
        this.name = name;
        this.ndcdId = ndcdId;
        this.dataId = dataId;
        this.enable = enable;
        this.time = time;
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
    @Column(name = "ndcd_id")
    public String getNdcdId() {
        return ndcdId;
    }

    public void setNdcdId(String ndcdId) {
        this.ndcdId = ndcdId;
    }
    @Column(name = "data_id")
    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }
    @Column(name = "enable")
    public String getEnable() {
        return enable;
    }

    public void setEnable(String enable) {
        this.enable = enable;
    }
    @Column(name = "time")
    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }
}
