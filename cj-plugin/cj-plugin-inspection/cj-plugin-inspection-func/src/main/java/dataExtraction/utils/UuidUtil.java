package dataExtraction.utils;

import java.util.UUID;

/**
 * <p>Description:</p>
 *
 * @author chenzhitao
 * @date 2022年11月30日
 * uuid工具类
 */
public class UuidUtil {
    public static String getUUID() {
        return UUID.randomUUID().toString();
    }

}
