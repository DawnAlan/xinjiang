package dataExtraction.water.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

//头屯河树结构信息
@Entity
@Table(name = "IRRIGATED_PLATFORM_TREE")
public class IRRIGATED_PLATFORM_TREE {
    private String id;
    private String name;
    private String parentId;

    public IRRIGATED_PLATFORM_TREE() {
    }

    public IRRIGATED_PLATFORM_TREE(String id, String name, String parentId) {
        this.id = id;
        this.name = name;
        this.parentId = parentId;
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
    @Column(name = "PARENT_ID")
    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
}
