package com.lfx.upms.util;

import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2019-05-07 17:10:25
 */
@UtilityClass
public class DateUtil {

    public static final String SHORT_DATE_LINE = "yyyy-MM-dd";
    public static final String LONG_DATE_LINE = "yyyy-MM-dd HH:mm:ss";

    public static final DateTimeFormatter SHORT_DATE_LINE_FORMATTER = DateTimeFormatter.ofPattern(SHORT_DATE_LINE);
    public static final DateTimeFormatter LONG_DATE_LINE_FORMATTER = DateTimeFormatter.ofPattern(LONG_DATE_LINE);

    public static final ZoneId zoneId = ZoneId.systemDefault();

    /**
     * 获取当天开始时间的时间戳
     *
     * @return
     */
    public static long getStartOfDate() {
        return getStartOfDate(LocalDate.now());
    }

    /**
     * 获取指定时间开始时间的时间戳
     *
     * @return
     */
    public static long getStartOfDate(LocalDate localDate) {
        return localDate.atStartOfDay(zoneId).toInstant().toEpochMilli();
    }

    /**
     * 获取当前时间，格式：yyyy-MM-dd
     *
     * @return
     */
    public static String getShortFormatOfLine() {
        return getShortFormatOfLine(LocalDate.now());
    }

    /**
     * 获取指定时间，格式：yyyy-MM-dd
     *
     * @param localDate
     * @return
     */
    public static String getShortFormatOfLine(LocalDate localDate) {
        return localDate.format(SHORT_DATE_LINE_FORMATTER);
    }

    /**
     * 获取当前时间，格式：yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public static String getLongFormatOfLine() {
        return getLongFormatOfLine(LocalDate.now());
    }

    /**
     * 获取指定时间，格式：yyyy-MM-dd HH:mm:ss
     *
     * @param localDate
     * @return
     */
    public static String getLongFormatOfLine(LocalDate localDate) {
        return LONG_DATE_LINE_FORMATTER.format(LocalDate.now());
    }
}
