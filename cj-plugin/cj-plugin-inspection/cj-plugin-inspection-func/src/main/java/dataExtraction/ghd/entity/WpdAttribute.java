package dataExtraction.ghd.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "wpd_attribute")
public class WpdAttribute implements Serializable {

    private WpdRunDataId id;
    private Double tmv;


    public WpdAttribute(WpdRunDataId id,Double tmv) {
        this.tmv = tmv;
        this.id = id;
    }

    public WpdAttribute() {

    }

    public WpdAttribute(WpdRunDataId id) {
        this.id = id;
    }

    @EmbeddedId

    @AttributeOverrides({ @AttributeOverride(name = "tm", column = @Column(name = "TM", nullable = false, length = 19)),
            @AttributeOverride(name = "ndcd", column = @Column(name = "NDCD", nullable = false, length = 200)),
            @AttributeOverride(name = "datacd", column = @Column(name = "DATACD", nullable = false, length = 200))})

    @Column(name = "TMV", precision = 7, scale = 3)

    public WpdRunDataId getId() {
        return this.id;
    }

    public void setId(WpdRunDataId id) {
        this.id = id;
    }

    public Double getTmv() {
        return tmv;
    }

    public void setTmv(Double tmv) {
        this.tmv = tmv;
    }
}
