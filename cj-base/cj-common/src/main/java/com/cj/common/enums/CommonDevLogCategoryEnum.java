package com.cj.common.enums;

import lombok.Getter;

@Getter
public enum CommonDevLogCategoryEnum {

    /** 操作日志 */
    OPERATE("OPERATE"),

    /** 异常日志 */
    EXCEPTION("EXCEPTION"),

    /** 登录日志 */
    LOGIN("LOGIN"),

    /** 登出日志 */
    LOGOUT("LOGOUT");

    private final String value;

    CommonDevLogCategoryEnum(String value) {
        this.value = value;
    }
}
