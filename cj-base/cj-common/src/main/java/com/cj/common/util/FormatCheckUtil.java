package com.cj.common.util;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;


/***
 * @Author: yly
 * 格式校验工具类
 * Create time 2024/1/10 19:03
*/
@Slf4j
@Component
public class FormatCheckUtil {


    //Map转换Class
    @SneakyThrows
    public Boolean checkFormat(String fieldValue , String systemType ){

        switch (systemType) {
            case "String":
                return true;
            case "Date":
                String format = "YYYY-MM-DD HH:mm:ss";
                DateFormat dateFormat = new SimpleDateFormat(format);
                try{
                    dateFormat.parse(fieldValue);
                    return true;
                }catch(Exception e){
                    //如果不能转换,肯定是错误格式
                    return false;
                }
            case "Double":
                try {
                    Double.parseDouble(fieldValue); // 或者 double dNum = Double.parseDouble(str);
                    return true;
                } catch (NumberFormatException e) {
                    return false;
                }
            default:
                return false;
        }
    }
}
