package com.cj.common.util;


import java.util.UUID;

/**
 *
 * @作者: chencong
 * @项目: mail--cc.ccoder.mail.utils
 * @时间: 2017年6月7日下午7:05:14
 * @TODO： 生成随机字符串的工具类 uuid
 */
public class UUIDUtils {

    public static String getUUID(){
        return UUID.randomUUID().toString().replace("-", "");
    }
}

