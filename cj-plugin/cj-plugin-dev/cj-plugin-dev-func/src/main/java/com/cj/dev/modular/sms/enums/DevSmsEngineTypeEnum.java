
package com.cj.dev.modular.sms.enums;

import lombok.Getter;

/**
 * 短信发送引擎类型枚举
 *
 * @author xuyuxiang
 * @date 2022/6/16 16:14
 **/
@Getter
public enum DevSmsEngineTypeEnum {

    /** 阿里云 */
    ALIYUN("ALIYUN"),

    /** 腾讯云 */
    TENCENT("TENCENT");

    private final String value;

    DevSmsEngineTypeEnum(String value) {
        this.value = value;
    }
}
