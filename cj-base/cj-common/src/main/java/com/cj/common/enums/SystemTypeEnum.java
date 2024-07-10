package com.cj.common.enums;

import lombok.Getter;

/**
 * 系统格式类型枚举
 *
 * @author yly
 * @date 2024/1/12 10:40
 **/
@Getter
public enum SystemTypeEnum {

    /**
     * String
     */
    STRING("String"),

    /**
     * Date
     */
    DATETIME("DateTime"),


    /**
     * Double
     */
    DOUBLE("Double");

    private final String value;

    SystemTypeEnum(String value) {
        this.value = value;
    }

}
