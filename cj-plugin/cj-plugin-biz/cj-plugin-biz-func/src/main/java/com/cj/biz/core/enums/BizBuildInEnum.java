
package com.cj.biz.core.enums;

import lombok.Getter;

/**
 * 系统内置的不可删除的标识枚举
 *
 * @author xuyuxiang
 * @date 2022/4/21 19:56
 **/
@Getter
public enum BizBuildInEnum {

    /**
     * 超管用户账号
     */
    BUILD_IN_USER_ACCOUNT("superAdmin", "超管");

    private final String value;

    private final String name;

    BizBuildInEnum(String value, String name) {
        this.value = value;
        this.name = name;
    }
}
