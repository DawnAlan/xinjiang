package com.cj.msg.entity;

import lombok.Data;

@Data
public class WarnDto {
    /**
     * name : 3号桥水位站
     * type : WaterStation
     * warnType : level
     * waterLevel : 1.04
     * flow : 345
     * time : 2024-07-22 11:00:00
     * alertLevel : FOUR
     */

    private String name;
    private String type;
    private String warnType;
    private Double waterLevel;
    private Double flow;
    private String time;
    private String alertLevel;
}
