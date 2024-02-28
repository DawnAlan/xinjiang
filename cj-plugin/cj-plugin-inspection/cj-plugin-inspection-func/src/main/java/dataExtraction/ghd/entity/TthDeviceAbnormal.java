package dataExtraction.ghd.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "tth_device_abnormal")
public class TthDeviceAbnormal implements java.io.Serializable{

    private String id;
    private String name;
    private String deviceId;
    private LocalDateTime time;


    public TthDeviceAbnormal(String id, String name, String deviceId, LocalDateTime time) {
        this.id = id;
        this.name = name;
        this.deviceId = deviceId;
        this.time = time;
    }

    public TthDeviceAbnormal() {

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
    @Column(name = "devic_id")
    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
    @Column(name = "time")
    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }
}
