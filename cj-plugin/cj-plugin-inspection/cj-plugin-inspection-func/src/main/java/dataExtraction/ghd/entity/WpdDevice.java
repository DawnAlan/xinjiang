package dataExtraction.ghd.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "wpd_device")

public class WpdDevice implements Serializable {

    private String id;
    private String att1;
    private String att2;
    private String devtype;
    private String devcode;
    private String devname;
    private Integer devstate;
    private String devposition;
    private String video;
    private String unit;
    private String stcd;
    private LocalDateTime endtime;
    private String latitude;
    private String longitude;

    public WpdDevice() {
    }

    public WpdDevice(String id, String att1, String att2, String devtype, String devcode, String devname, Integer devstate, String devposition, String video, String unit, String stcd, LocalDateTime endtime, String latitude, String longitude) {
        this.id = id;
        this.att1 = att1;
        this.att2 = att2;
        this.devtype = devtype;
        this.devcode = devcode;
        this.devname = devname;
        this.devstate = devstate;
        this.devposition = devposition;
        this.video = video;
        this.unit = unit;
        this.stcd = stcd;
        this.endtime = endtime;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    @Id
    @Column(name = "ID")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    @Column(name = "ATT1")
    public String getAtt1() {
        return att1;
    }

    public void setAtt1(String att1) {
        this.att1 = att1;
    }
    @Column(name = "ATT2")
    public String getAtt2() {
        return att2;
    }

    public void setAtt2(String att2) {
        this.att2 = att2;
    }
    @Column(name = "DEVTYPE")
    public String getDevtype() {
        return devtype;
    }

    public void setDevtype(String devtype) {
        this.devtype = devtype;
    }
    @Column(name = "DEVCODE")
    public String getDevcode() {
        return devcode;
    }

    public void setDevcode(String devcode) {
        this.devcode = devcode;
    }
    @Column(name = "DEVNAME")
    public String getDevname() {
        return devname;
    }

    public void setDevname(String devname) {
        this.devname = devname;
    }
    @Column(name = "DEVSTATE")
    public Integer getDevstate() {
        return devstate;
    }

    public void setDevstate(Integer devstate) {
        this.devstate = devstate;
    }
    @Column(name = "DEVPOSITION")
    public String getDevposition() {
        return devposition;
    }

    public void setDevposition(String devposition) {
        this.devposition = devposition;
    }
    @Column(name = "VIDEO")
    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }
    @Column(name = "UNIT")
    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
    @Column(name = "STCD")
    public String getStcd() {
        return stcd;
    }

    public void setStcd(String stcd) {
        this.stcd = stcd;
    }
    @Column(name = "ENDTIME")
    public LocalDateTime getEndtime() {
        return endtime;
    }

    public void setEndtime(LocalDateTime endtime) {
        this.endtime = endtime;
    }

    @Column(name = "LATITUDE")
    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    @Column(name = "LONGITUDE")
    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
