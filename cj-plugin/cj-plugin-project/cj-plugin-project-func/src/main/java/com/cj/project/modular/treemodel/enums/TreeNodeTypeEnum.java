package com.cj.project.modular.treemodel.enums;

public enum TreeNodeTypeEnum {

    TYPE_CUSTOMIZE(1, "自定义分类"),
    TYPE_DETECT(2, "检测类型"),
    TYPE_INSTRUMENT(3, "仪器类型"),
    TYPE_NODE(4, "测点编号");

    TreeNodeTypeEnum(Integer type, String name) {
        this.type = type;
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    private final Integer type;
    private final String name;

    TreeNodeTypeEnum(int i, String string) {
        this.type = i;
        this.name = string;
    }

    public static TreeNodeTypeEnum getTypeName(int key) {

        TreeNodeTypeEnum result = null;

        for (TreeNodeTypeEnum s : values()) {
            if (s.getType() == key) {
                result = s;
                break;
            }
        }

        return result;
    }

    public static TreeNodeTypeEnum getTypeValue(String msg) {

        TreeNodeTypeEnum result = null;

        for (TreeNodeTypeEnum s : values()) {
            if (s.getName().equals(msg)) {
                result = s;
                break;
            }
        }
        return result;
    }

}
