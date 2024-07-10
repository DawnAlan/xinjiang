package com.cj.common.enums;

import lombok.Getter;

@Getter
public enum CommonDevLogExeStatusEnum {

    /** 成功 */
    SUCCESS("SUCCESS"),

    /** 失败 */
    FAIL("FAIL");

    private final String value;

    CommonDevLogExeStatusEnum(String value) {
        this.value = value;
    }
}
