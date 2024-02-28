package dataExtraction.utils;

import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;

/**
 * <p>Description:</p>
 *
 * @author chenzhitao
 * @date 2022年12月08日
 */
public class TimeUtil {
    //8小时的秒数
    private static final int OFFSET = 8 * 60 * 60;
    public static final String PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String PATTERN_YMD = "yyyy-MM-dd";
    public static final String PATTERN_YMDH = "yyMMddHH";
    public static final String PATTERN_YYMDH = "yyyyMMddHH";
    //8小时的秒数
    //默认周期抽取时间：1小时
    private static final long DEFAULT_DURATION_TIME = 60;

    public static final Integer TYPE_DAY = 0;

    public static final Integer TYPE_HOUR = 1;

    public static final Integer TYPE_MINUTE = 2;

    /**
     * 获取定时任务周期分钟数
     */
    public static Long getDurationMinute(Long duration, Integer durationTimeType) {
        if (Objects.equals(durationTimeType, TYPE_DAY)) {
            return duration * 24 * 60L;
        }
        if (Objects.equals(durationTimeType, TYPE_HOUR)) {
            return duration * DEFAULT_DURATION_TIME;
        }
        if (Objects.equals(durationTimeType, TYPE_MINUTE)) {
            return duration;
        }
        //如果事件类型不匹配，则按照60min进行抽取
        return DEFAULT_DURATION_TIME;
    }

    /**
     * 时间转yyMMddHH格式
     *
     * @param localDateTime
     * @return
     */
    public static String convertDateToStringYMDH(LocalDateTime localDateTime) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(PATTERN_YMDH);
        return dtf.format(localDateTime);
    }

    /**
     * 时间转yyyyMMddHH格式
     * @param localDateTime
     * @return
     */
    public static String convertDateToStringYYMDH(LocalDateTime localDateTime) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(PATTERN_YYMDH);
        return dtf.format(localDateTime);
    }

    /**
     * 根据yyMMddHH格式字符串转对应的时间
     *
     * @param time
     * @return
     */
    public static LocalDateTime convertStringToDateYMDH(String time) {
        DateTimeFormatter dft = DateTimeFormatter.ofPattern(PATTERN_YMDH);
        return LocalDateTime.parse(time, dft);
    }

    /**
     * 根据yyyyMMddHH格式字符串转对应的时间
     * @param time
     * @return
     */
    public static LocalDateTime convertStringToDateYYMDH(String time) {
        DateTimeFormatter dft = DateTimeFormatter.ofPattern(PATTERN_YYMDH);
        return LocalDateTime.parse(time, dft);
    }

    /**
     * 获取对应时间的毫秒数
     *
     * @param localDateTime
     * @return
     */
    public static long toMilliSecond(LocalDateTime localDateTime) {
        // 比标准实际慢8小时，就是当前的时间了。
        return localDateTime.toInstant(ZoneOffset.ofTotalSeconds(OFFSET)).toEpochMilli();
    }

    /**
     * LocalDateTime转Date
     *
     * @param localDateTime
     * @return
     */
    public static Date asDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 将日期转换为字符串，格式为：yyyy-MM-dd HH:mm:ss
     *
     * @param localDateTime
     * @return
     */
    public static String convertDateToString(LocalDateTime localDateTime) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(PATTERN);
        return dtf.format(localDateTime);
    }

    /**
     * 将日期转换为字符串，格式为：yyyy-MM-dd
     *
     * @param localDateTime
     * @return
     */
    public static String convertDateToStringYMD(LocalDateTime localDateTime) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(PATTERN_YMD);
        return dtf.format(localDateTime);
    }

    /**
     * 将字符串转换为日期，格式为：yyyy-MM-dd HH:mm:ss
     *
     * @param time
     * @return
     */
    public static LocalDateTime convertStringToDate(String time) {
        if (!StringUtils.hasLength(time.trim())) {
            return null;
        }
        DateTimeFormatter dft = DateTimeFormatter.ofPattern(PATTERN);
        return LocalDateTime.parse(time, dft);
    }


    /**
     * 将字符串转换为日期，格式为：yyyy-MM-dd
     *
     * @param time
     * @return
     */
    public static LocalDateTime convertStringToDateYMD(String time) {
        DateTimeFormatter dft = DateTimeFormatter.ofPattern(PATTERN_YMD);
        return LocalDateTime.parse(time, dft);
    }

    /**
     * 返回指定时间的最近8/20点的时间
     *
     * @param dateTime
     * @return
     */
    public static LocalDateTime getLastTime(LocalDateTime dateTime) {
        LocalDateTime fastTime = LocalDateTime.of(dateTime.getYear(), dateTime.getMonth(), dateTime.getDayOfMonth(), 8, 0, 0);
        LocalDateTime lastTime = LocalDateTime.of(dateTime.getYear(), dateTime.getMonth(), dateTime.getDayOfMonth(), 20, 0, 0);
        LocalDateTime yesTime = LocalDateTime.of(dateTime.getYear(), dateTime.getMonth(), dateTime.getDayOfMonth() - 1, 20, 0, 0);
        if (lastTime.isBefore(dateTime)) {
            return lastTime;
        }
        if (fastTime.isBefore(dateTime)) {
            return fastTime;
        }
        if (yesTime.isBefore(dateTime)) {
            return yesTime;
        }
        return null;
    }

}
