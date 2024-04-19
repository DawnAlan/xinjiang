
package com.cj.msg.enums;

import lombok.Getter;

/**
 * 分类枚举
 *
 * @author xuyuxiang
 * @date 2022/6/16 16:14
 **/
@Getter
public enum MessageCategoryEnum {

    /** 指令审批 */
    APPROVAL("指令审批"),

    /** 日用水计划 */
    DAY_WATER_PLAN("日用水计划");

    private final String value;

    MessageCategoryEnum(String value) {
        this.value = value;
    }
}
