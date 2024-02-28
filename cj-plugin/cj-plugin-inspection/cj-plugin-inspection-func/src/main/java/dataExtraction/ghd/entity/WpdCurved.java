package dataExtraction.ghd.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "wpd_curved")
@IdClass(WpdCurved.class)
public class WpdCurved implements Serializable {

    private String id;
    private Double v0;
    private Double v1;

    public WpdCurved(){

    }

    public WpdCurved(String id, Double v0, Double v1) {
        this.id = id;
        this.v0 = v0;
        this.v1 = v1;
    }
    @Id
    @Column(name = "id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Column(name = "v0")
    public Double getV0() {
        return v0;
    }

    public void setV0(Double v0) {
        this.v0 = v0;
    }
    @Column(name = "v1")
    public Double getV1() {
        return v1;
    }

    public void setV1(Double v1) {
        this.v1 = v1;
    }
}
