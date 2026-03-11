package tear.conception.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtil {
    
    private static final long MINUTE = 60 * 1000;
    private static final long HOUR = 60 * MINUTE;
    private static final long DAY = 24 * HOUR;
    
    public static final int TIME_PERIOD_MORNING = 0;
    public static final int TIME_PERIOD_NOON = 1;
    public static final int TIME_PERIOD_AFTERNOON = 2;
    public static final int TIME_PERIOD_EVENING = 3;
    public static final int TIME_PERIOD_NIGHT = 4;

    public static String formatTime(long timestamp) {
        long now = System.currentTimeMillis();
        long diff = now - timestamp;

        if (diff < MINUTE) {
            return "刚刚";
        } else if (diff < HOUR) {
            return (diff / MINUTE) + "分钟前";
        } else if (diff < DAY) {
            return (diff / HOUR) + "小时前";
        } else if (diff < 7 * DAY) {
                return (diff / DAY) + "天前";
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                return sdf.format(new Date(timestamp));
            }
    }

    public static String formatDate(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    public static String formatDateTime(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    public static boolean isSameDay(long timestamp1, long timestamp2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTimeInMillis(timestamp1);
        cal2.setTimeInMillis(timestamp2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
            && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
            && cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }

    public static boolean isToday(long timestamp) {
        return isSameDay(timestamp, System.currentTimeMillis());
    }

    public static long getTodayStart() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }
    
    public static int getCurrentTimePeriod() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        
        if (hour >= 5 && hour < 12) {
            return TIME_PERIOD_MORNING;
        } else if (hour >= 12 && hour < 14) {
            return TIME_PERIOD_NOON;
        } else if (hour >= 14 && hour < 18) {
            return TIME_PERIOD_AFTERNOON;
        } else if (hour >= 18 && hour < 22) {
            return TIME_PERIOD_EVENING;
        } else {
            return TIME_PERIOD_NIGHT;
        }
    }
    
    public static String getBeijingTimeString() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        return sdf.format(calendar.getTime());
    }
}
