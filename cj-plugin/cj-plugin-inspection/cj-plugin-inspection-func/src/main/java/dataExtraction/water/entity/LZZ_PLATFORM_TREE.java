package dataExtraction.water.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

//楼庄子树结构信息
@Entity
@Table(name = "LZZ_PLATFORM_TREE")
public class LZZ_PLATFORM_TREE {
    private String id;
    private String name;
    private String pId;

    public LZZ_PLATFORM_TREE() {
    }

    public LZZ_PLATFORM_TREE(String id, String name, String pId) {
        this.id = id;
        this.name = name;
        this.pId = pId;
    }

    @Id
    @Column(name = "ID")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    @Column(name = "NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    @Column(name = "P_ID")
    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

}
