
package com.cj.waterresources.func.modular.useWaterPlanEscalation.yearWaterUsePlan.bean.enums;

import lombok.Getter;

/**
 * 日志分类枚举
 *
 * @author xuyuxiang
 * @date 2022/6/16 16:14
 **/
@Getter
public enum MonthEnum {
    JANUARY("1", "JANUARY"),
    FEBRUARY("2", "FEBRUARY"),
    MARCH("3", "MARCH"),
    APRIL("4", "APRIL"),
    MAY("5", "MAY"),
    JUNE("6", "JUNE"),
    JULY("7", "JULY"),
    AUGUST("8", "AUGUST"),
    SEPTEMBER("9", "SEPTEMBER"),
    OCTOBER("10", "OCTOBER"),
    NOVEMBER("11", "NOVEMBER"),
    DECEMBER("12", "DECEMBER");

    private final String value;

    private final String name;

    MonthEnum(String value, String name) {
        this.value = value;
        this.name = name;
    }
}
