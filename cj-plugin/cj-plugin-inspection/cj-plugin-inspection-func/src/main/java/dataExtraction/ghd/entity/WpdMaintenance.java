package dataExtraction.ghd.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "wpd_maintenance")
public class WpdMaintenance implements Serializable {

    private String id;
    private String devId;
    private String user;
    private String type;
    private LocalDateTime time;

    public WpdMaintenance() {

    }
    public WpdMaintenance(String id, String devId, String user, String type, LocalDateTime time) {
        this.id = id;
        this.devId = devId;
        this.user = user;
        this.type = type;
        this.time = time;
    }

    @Id
    @Column(name = "ID")
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    @Column(name = "DEVID")
    public String getDevId() {
        return devId;
    }

    public void setDevId(String devId) {
        this.devId = devId;
    }
    @Column(name = "USER")
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
    @Column(name = "TYPE")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    @Column(name = "TIME")
    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }
}
