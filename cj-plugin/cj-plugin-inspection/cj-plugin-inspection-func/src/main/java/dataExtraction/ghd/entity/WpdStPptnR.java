package dataExtraction.ghd.entity;

import javax.persistence.*;

/**
 * WpdStPptnR entity. @author Li Qi by MyEclipse Persistence Tools
 */
@Entity
@Table(name = "wpd_st_pptn_r")

public class WpdStPptnR implements java.io.Serializable {

    // Fields

    private WpdStPptnRId id;
    private Double drp;
    private Double intv;
    private Double pdr;
    private Double dyp;
    private String wth;

    // Constructors

    /**
     * default constructor
     */
    public WpdStPptnR() {
    }

    /**
     * minimal constructor
     */
    public WpdStPptnR(WpdStPptnRId id) {
        this.id = id;
    }

    /**
     * full constructor
     */
    public WpdStPptnR(WpdStPptnRId id, Double drp, Double intv, Double pdr, Double dyp, String wth) {
        this.id = id;
        this.drp = drp;
        this.intv = intv;
        this.pdr = pdr;
        this.dyp = dyp;
        this.wth = wth;
    }


    // Property accessors
    @EmbeddedId

    @AttributeOverrides({@AttributeOverride(name = "tm", column = @Column(name = "TM", nullable = false, length = 19)),
            @AttributeOverride(name = "stcd", column = @Column(name = "STCD", nullable = false, length = 12))})

    public WpdStPptnRId getId() {
        return this.id;
    }

    public void setId(WpdStPptnRId id) {
        this.id = id;
    }

    @Column(name = "DRP", precision = 5, scale = 1)

    public Double getDrp() {
        return this.drp;
    }

    public void setDrp(Double drp) {
        this.drp = drp;
    }

    @Column(name = "INTV", precision = 5)

    public Double getIntv() {
        return this.intv;
    }

    public void setIntv(Double intv) {
        this.intv = intv;
    }

    @Column(name = "PDR", precision = 5)

    public Double getPdr() {
        return this.pdr;
    }

    public void setPdr(Double pdr) {
        this.pdr = pdr;
    }

    @Column(name = "DYP", precision = 5, scale = 1)

    public Double getDyp() {
        return this.dyp;
    }

    public void setDyp(Double dyp) {
        this.dyp = dyp;
    }

    @Column(name = "WTH", length = 4)

    public String getWth() {
        return this.wth;
    }

    public void setWth(String wth) {
        this.wth = wth;
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
        buffer.append("drp").append("='").append(getDrp()).append("' ");
        buffer.append("intv").append("='").append(getIntv()).append("' ");
        buffer.append("pdr").append("='").append(getPdr()).append("' ");
        buffer.append("dyp").append("='").append(getDyp()).append("' ");
        buffer.append("wth").append("='").append(getWth()).append("' ");
        buffer.append("]");

        return buffer.toString();
    }

}