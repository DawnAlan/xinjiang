
package com.cj.biz.modular.org.enums;

import lombok.Getter;
import com.cj.common.exception.CommonException;

/**
 * 机构分类枚举
 *
 * @author xuyuxiang
 * @date 2022/4/21 19:56
 **/
@Getter
public enum BizOrgCategoryEnum {

    /** 公司 */
    COMPANY("COMPANY"),

    /** 部门 */
    DEPT("DEPT");

    private final String value;

    BizOrgCategoryEnum(String value) {
        this.value = value;
    }

    public static void validate(String value) {
        boolean flag = COMPANY.getValue().equals(value) || DEPT.getValue().equals(value);
        if(!flag) {
            throw new CommonException("不支持的机构分类：{}", value);
        }
    }
}
