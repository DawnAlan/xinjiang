package dataExtraction.ghd.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author cxl
 * @date 2023/5/24 17:24
 */
@Embeddable
public class WpdRunDataId implements Serializable {

    private static final long serialVersionUID = 2778201417830185714L;
    // 节点代码
    private String ndcd;
    // 元数据代码
    private String datacd;
    // 时间
    private LocalDateTime tm;

    public WpdRunDataId() {
    }

    public WpdRunDataId(String ndcd, String datacd, LocalDateTime tm) {
        this.ndcd = ndcd;
        this.datacd = datacd;
        this.tm = tm;
    }

    @Column(name = "NDCD", nullable = false, length = 64)
    public String getNdcd() {
        return ndcd;
    }

    public void setNdcd(String ndcd) {
        this.ndcd = ndcd;
    }

    @Column(name = "DATACD", nullable = false, length = 64)
    public String getDatacd() {
        return datacd;
    }

    public void setDatacd(String datacd) {
        this.datacd = datacd;
    }

    @Column(name = "TM", nullable = false, length = 19)
    public LocalDateTime getTm() {
        return tm;
    }


    public void setTm(LocalDateTime tm) {
        this.tm = tm;
    }
}
