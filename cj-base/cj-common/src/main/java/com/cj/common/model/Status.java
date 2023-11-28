package com.cj.common.model;

import lombok.Getter;

public enum Status {

    SUCCESS(200, "成功"),//交易/请求成功

    ERROR(500, "失败"),//交易/请求失败,未知错误

    USERNAME_OR_PASSWORD_INCORRECT(1000, "用户名或密码错误"),
    UNAUTHORIZED(1001, "用户资源未授权无法访问"),
    VERIFICATION_CODE_ERROR(1002, "图片验证码错误"),
    SESSION_ERROR(1003, "请重新登陆"),
    DISABLED_ACCOUNT(1004, "账号已禁用"),
    NOT_ALLOWED_LOGIN_TWO_ACCOUNTS(1005, "不允许同时登陆两个账号"),
    TOKEN_FAIL(1006, "登录超时,请重新登录"),
    LOCK_ACCOUNT(1007, "密码错误次数太多，账号已锁定"),

    PARAM_ERROR(2000, "参数错误"),
    PARAMETER_PARSING_EXCEPTION(2001, "参数解析异常"),
    DATE_ERROR(2002, "数据错误"),
    DATE_NULL(2003, "数据为空"),

    IO_ERROR(3200, "I/O异常"),
    EXPORT_ERROR(3201, "导出失败"),

    DATEBASE_ERROR(3000, "数据库操作异常"),

    RPC_ERROR(4000, "rpc异常"),

    BUSINESS_EXCEPTION(5000, "业务逻辑处理异常"),

    LOCAL_LT_USER(10001,"联调用户信息不全"),
    LOCAL_LT_NAME_PLATE(10008,"重复的机械号牌"),
    LOCAL_LT_NUM_PIN(10009,"重复的PIN"),
    LOCAL_LT_NUM_ENV_CODE(100010,"重复的环保标识码"),
    LOCAL_LT_PHOTO_NAME(10006,"没有上传图片文件名"),
    LOCAL_LT_PHOTO_SIZE(10007,"上传图片过大"),
    LOCAL_LT_USER_NOT(10005,"登录错误， 鉴权失败， 密码不正确"),
    LOCAL_LT_USER_ERROR(10002,"联调用户添加失败"),
    LOCAL_LT_MACHINERY(10003,"联调机械信息不全"),
    LOCAL_LT_MACHINERY_ERROR(10004,"联调机械添加失败"),
    ENCRYPT_OR_DECRYPT_ERROR(5001, "加密/解密失败"),
    WHETHER_TO_COVER(10010,"数据已存在是否覆盖");





    @Getter
    private int code;
    @Getter
    private String descript;

    Status(int code, String descript) {
        this.code = code;
        this.descript = descript;
    }

}