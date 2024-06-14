
package com.cj.dev.modular.dict.enums;

import lombok.Getter;
import com.cj.common.exception.CommonException;

/**
 * 字典分类枚举
 *
 * @author xuyuxiang
 * @date 2022/7/6 22:21
 */
@Getter
public enum DevDictCategoryEnum {

    /**
     * 框架
     */
    FRM("FRM"),

    /**
     * 业务
     */
    BIZ("BIZ"),

    /**
     * 考证字典
     */
    FIDUCIAL("FIDUCIAL"),

    /**
     * MONITOR监测系统字典
     */
    MONITOR("MONITOR"),

    /**
     * 字典分类
     */
    DICT_CATEGORY("DICT_CATEGORY");

    private final String value;

    DevDictCategoryEnum(String value) {
        this.value = value;
    }

    public static void validate(String value) {
        boolean flag = FRM.getValue().equals(value) || BIZ.getValue().equals(value)||
                FIDUCIAL.getValue().equals(value)|| MONITOR.getValue().equals(value)||
                DICT_CATEGORY.getValue().equals(value);
        if(!flag) {
            throw new CommonException("不支持的字典分类：{}", value);
        }
    }
}
