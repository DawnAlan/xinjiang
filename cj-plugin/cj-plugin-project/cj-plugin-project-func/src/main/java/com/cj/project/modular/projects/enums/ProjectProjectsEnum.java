package com.cj.project.modular.projects.enums;

import com.cj.common.exception.CommonException;
import lombok.Getter;

/**
 * 项目枚举
 *
 * @author Lb
 * @date  2023/09/01 12:29
 **/
@Getter
public enum ProjectProjectsEnum {

    /** 流域 */
    BASIN("BASIN"),
    /** 项目 */
    PROJECT("PROJECT");

    private final String value;

    ProjectProjectsEnum(String value) {
        this.value = value;
    }

    public static void validate(String value) {
        boolean flag = BASIN.getValue().equals(value) || PROJECT.getValue().equals(value);
        if(!flag) {
            throw new CommonException("不支持的项目分类：{}", value);
        }
    }
}
