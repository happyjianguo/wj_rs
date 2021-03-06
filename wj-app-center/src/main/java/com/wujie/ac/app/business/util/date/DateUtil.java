package com.wujie.ac.app.business.util.date;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.function.IntFunction;

import com.wujie.ac.app.business.enums.ErrorEnum;
import com.wujie.ac.app.framework.exception.CustomException;
import org.springframework.stereotype.Component;


/**
 * 时间工具类
 *
 * @author MengDaNai
 * @version 1.0
 * @ClassName: DateUtil.java
 * @Date 2019年7月19日 下午5:40:37
 */
@Component
public class DateUtil {

    private static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";


    /**
     * @Title localDateTimeToDate
     * @Description localDateTime 转 Date
     * @Author yangf
     * @Date 2019/11/5 10:56
     * @param: localDateTime
     * @Return java.util.Date
     */
    public static Date localDateTimeToDate(LocalDateTime localDateTime) {
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdtStart = localDateTime.atZone(zoneId);
        return Date.from(zdtStart.toInstant());
    }


    /**
     * 获取当前时间
     *
     * @return String
     * @author MengDaNai
     * @Date 2019年7月19日 下午5:40:50
     */
    public static String getDateTime() {
        LocalDateTime datetime = LocalDateTime.now();
        DateTimeFormatter sdf = DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS);
        return datetime.format(sdf);
    }

    /**
     * 获取当前时间
     *
     * @param pattern 时间格式 yyyy-MM-dd HH:mm:ss
     * @return String
     * @author MengDaNai
     * @Date 2019年7月19日 下午5:41:00
     */
    public static String getDateTime(String pattern) {
        LocalDateTime datetime = LocalDateTime.now();
        DateTimeFormatter sdf = DateTimeFormatter.ofPattern(pattern);
        return datetime.format(sdf);
    }

    /**
     * 获取当前时间
     *
     * @return Date
     * @author MengDaNai
     * @Date 2019年7月19日 下午5:41:15
     */
    public static Date getDate() {
        String dateTime = getDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS);
        LocalDateTime startTime = LocalDateTime.parse(dateTime, formatter);
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdtStart = startTime.atZone(zoneId);
        return Date.from(zdtStart.toInstant());
    }

    /**
     * StringToDate
     *
     * @param dateTime
     * @return Date
     * @author MengDaNai
     * @Date 2019年7月19日 下午5:41:22
     */
    public static Date stringToDate(String dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS);
        LocalDateTime startTime = LocalDateTime.parse(dateTime, formatter);
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdtStart = startTime.atZone(zoneId);
        return Date.from(zdtStart.toInstant());
    }

    public static String DateToString(Date date, String pattern) {
        LocalDateTime datetime = DateUtil.dateToLocalDateTime(date);
        DateTimeFormatter sdf = DateTimeFormatter.ofPattern(pattern);
        return datetime.format(sdf);
    }

    /**
     * 获取一个月多少天
     *
     * @param date
     * @return int
     * @author SHISHI
     * @Date 2019年7月19日 下午5:41:32
     */
    public static int getDayByMonth(Date date) {
        return dateToLocalDate(date).lengthOfMonth();
    }

    /**
     * 获取时间是星期几
     *
     * @param date
     * @Author SHISHI
     * @Date 2019/7/18 14:27
     * @Return java.time.DayOfWeek 枚举类型
     * @See java.time.DayOfWeek 定义
     */
    public static DayOfWeek getDayOfWeek(Date date) {
        return dateToLocalDate(date).getDayOfWeek();
    }

    /**
     * 把Date转换为LocalDateTime
     *
     * @param date
     * @Author SHISHI
     * @Date 2019/7/18 11:43
     * @Return java.time.LocalDateTime
     */
    public static LocalDateTime dateToLocalDateTime(Date date) {
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        return LocalDateTime.ofInstant(instant, zone);
    }

    /**
     * 把Date转换为localDate
     *
     * @param date
     * @Author SHISHI
     * @Date 2019/7/18 11:41
     * @Return java.time.LocalDate
     */
    public static LocalDate dateToLocalDate(Date date) {
        return dateToLocalDateTime(date).toLocalDate();
    }

    /**
     * 计算时间差
     *
     * @param oldTime 过去时间
     * @param nowTime 当前时间
     * @param type    要计算的时间差类型
     * @Author SHISHI
     * @Date 2019/7/18 14:14
     * @Return long 技术按结果
     */
    public static long differenceTime(Date oldTime, Date nowTime, TimeTypeEnum type) {
        LocalDateTime oldLocalDateTime = dateToLocalDateTime(oldTime);
        LocalDateTime nowLocalDateTime = dateToLocalDateTime(nowTime);
        switch (type) {
            //年
            case YEAR:
                return differenceYear(oldLocalDateTime, nowLocalDateTime);
            //月
            case MONTH:
                return differenceMonth(oldLocalDateTime, nowLocalDateTime);
            //天
            case DAY:
                return differenceDay(oldLocalDateTime, nowLocalDateTime);
            //小时
            case HOUR:
                return differenceHourly(oldLocalDateTime, nowLocalDateTime);
            //分钟
            case MINUTE:
                return differenceMinute(oldLocalDateTime, nowLocalDateTime);
            //秒
            case SECOND:
                return differenceSecond(oldLocalDateTime, nowLocalDateTime);
            //毫秒
            case MILLI:
                return differenceMilli(oldLocalDateTime, nowLocalDateTime);
            default:
                throw new CustomException(ErrorEnum.GATEWAY_ERROR);
        }
    }

    /**
     * 计算年差
     *
     * @param oldTime
     * @param nowTime
     * @Author SHISHI
     * @Date 2019/7/18 14:58
     * @Return long
     */
    private static long differenceYear(LocalDateTime oldTime, LocalDateTime nowTime) {
        return oldTime.until(nowTime, ChronoUnit.YEARS);
    }

    /**
     * 计算月差
     *
     * @param oldTime
     * @param nowTime
     * @Author SHISHI
     * @Date 2019/7/18 14:58
     * @Return long
     */
    private static long differenceMonth(LocalDateTime oldTime, LocalDateTime nowTime) {
        return oldTime.until(nowTime, ChronoUnit.MONTHS);
    }

    /**
     * 计算天数差
     *
     * @param oldTime 过去时间(小时间)
     * @param nowTime 当前时间(大时间)
     * @Author SHISHI
     * @Date 2019/7/18 13:46
     * @Return long
     */
    private static long differenceDay(LocalDateTime oldTime, LocalDateTime nowTime) {
        return getDuration(oldTime, nowTime).toDays();
    }

    /**
     * 小时差
     *
     * @param oldTime 过去时间(小时间)
     * @param nowTime 当前时间(大时间)
     * @Author SHISHI
     * @Date 2019/7/18 13:54
     * @Return long
     */
    private static long differenceHourly(LocalDateTime oldTime, LocalDateTime nowTime) {
        return getDuration(oldTime, nowTime).toHours();
    }

    /**
     * 分钟差
     *
     * @param oldTime 过去时间(小时间)
     * @param nowTime 当前时间(大时间)
     * @Author SHISHI
     * @Date 2019/7/18 13:54
     * @Return long
     */
    private static long differenceMinute(LocalDateTime oldTime, LocalDateTime nowTime) {
        return getDuration(oldTime, nowTime).toMinutes();
    }

    /**
     * 秒差
     *
     * @param oldTime 过去时间(小时间)
     * @param nowTime 当前时间(大时间)
     * @Author SHISHI
     * @Date 2019/7/18 13:54
     * @Return long
     */
    private static long differenceSecond(LocalDateTime oldTime, LocalDateTime nowTime) {
        return differenceMilli(oldTime, nowTime) / 1000;
    }

    /**
     * 毫秒差
     *
     * @param oldTime 过去时间(小时间)
     * @param nowTime 当前时间(大时间)
     * @Author SHISHI
     * @Date 2019/7/18 13:54
     * @Return long
     */
    private static long differenceMilli(LocalDateTime oldTime, LocalDateTime nowTime) {
        return getDuration(oldTime, nowTime).toMillis();
    }

    /**
     * 获取时间比较工具 Duration
     *
     * @param oldTime 过去时间(小时间)
     * @param nowTime 当前时间(大时间)
     * @Author SHISHI
     * @Date 2019/7/18 13:48
     * @Return java.time.Duration
     */
    private static Duration getDuration(LocalDateTime oldTime, LocalDateTime nowTime) {
        return Duration.between(oldTime, nowTime);
    }

    /**
     * 比较日期大小
     * <p>
     * time1 = "2018-01-01 00:00:00"
     * time2 = "2019-01-01 00:00:00"
     * return true
     * 时间1小于时间2
     * </p>
     *
     * @param time1
     * @param time2
     * @Author SHISHI
     * @Date 2019/7/18 14:22
     * @Return boolean
     */
    public static boolean compareTime(Date time1, Date time2) {
        LocalDateTime localDateTime1 = dateToLocalDateTime(time1);
        LocalDateTime localDateTime2 = dateToLocalDateTime(time2);
        return localDateTime1.isBefore(localDateTime2);
    }

    /**
     * 对时间加减(不支持乘，除，毫秒级)
     *
     * @param time              时间
     * @param num               计算的数量
     * @param timeTypeEnum      计算的时间类型
     * @param calculateTypeEnum 计算的类型
     * @Author SHISHI
     * @Date 2019/7/18 14:55
     * @Return java.util.Date
     */
    public static Date calculatingTime(Date time, int num, TimeTypeEnum timeTypeEnum, CalculateTypeEnum calculateTypeEnum) {
        LocalDateTime localDateTime = dateToLocalDateTime(time);
        switch (timeTypeEnum) {
            case YEAR:
                return calculation(num, calculateTypeEnum, localDateTime::plusYears, localDateTime::minusYears);
            case MONTH:
                return calculation(num, calculateTypeEnum, localDateTime::plusMonths, localDateTime::minusMonths);
            case DAY:
                return calculation(num, calculateTypeEnum, localDateTime::plusDays, localDateTime::minusDays);
            case HOUR:
                return calculation(num, calculateTypeEnum, localDateTime::plusHours, localDateTime::minusHours);
            case MINUTE:
                return calculation(num, calculateTypeEnum, localDateTime::plusMinutes, localDateTime::minusMinutes);
            case SECOND:
                return calculation(num, calculateTypeEnum, localDateTime::plusSeconds, localDateTime::minusSeconds);
            default:
                throw new CustomException(ErrorEnum.GATEWAY_ERROR);
        }
    }

    /**
     * 执行加还是减
     *
     * @param num     计算数量
     * @param addDeal 加方法
     * @param subDeal 减方法
     * @Author SHISHI
     * @Date 2019/7/18 15:15
     * @Return java.time.LocalDateTime
     */
    private static Date calculation(int num, CalculateTypeEnum calculateTypeEnum, IntFunction<LocalDateTime> addDeal, IntFunction<LocalDateTime> subDeal) {
        ZoneId zoneId = ZoneId.systemDefault();
        switch (calculateTypeEnum) {
            case ADD:
                return Date.from(addDeal.apply(num).atZone(zoneId).toInstant());
            case SUBTRACT:
                return Date.from(subDeal.apply(num).atZone(zoneId).toInstant());
            default:
                throw new CustomException(ErrorEnum.GATEWAY_ERROR);
        }
    }

}
