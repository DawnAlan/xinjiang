package com.cj.waterresources.func.modular.waterSituationDataMaintenance.bean.res;

import lombok.Data;

import java.util.List;

@Data
public class LzzPlatformTreeRes {
    //主键ID
    private String id;
    //名称
    private String name;
    //父ID
    private String pId;

    private List<LzzPlatformTreeRes> children;
}
