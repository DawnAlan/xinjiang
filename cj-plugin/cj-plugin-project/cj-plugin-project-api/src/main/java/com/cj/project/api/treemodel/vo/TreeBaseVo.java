package com.cj.project.api.treemodel.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class TreeBaseVo implements Serializable {

    /**
     * 分项
     */
    private String subProject;

    /**
     * 分部
     */
    private String itemProject;

    /**
     * 类别
     */
    private String instrumentType;

    /**
     * 测点编号
     */
    private String pointName;
}
