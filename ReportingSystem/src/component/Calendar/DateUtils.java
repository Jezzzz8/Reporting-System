package component.Calendar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
    
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat displayFormat = new SimpleDateFormat("EEEE, MMMM dd, yyyy");
    private static final SimpleDateFormat monthYearFormat = new SimpleDateFormat("MMMM yyyy");
    
    public static Date parseDate(String dateStr) throws ParseException {
        return dateFormat.parse(dateStr);
    }
    
    public static String formatDate(Date date) {
        return dateFormat.format(date);
    }
    
    public static String formatDisplayDate(Date date) {
        return displayFormat.format(date);
    }
    
    public static String formatMonthYear(Date date) {
        return monthYearFormat.format(date);
    }
    
    public static Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }
    
    public static Date getNextWeekday(Date startDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        
        do {
            cal.add(Calendar.DATE, 1);
        } while (isWeekend(cal.getTime()));
        
        return cal.getTime();
    }
    
    public static boolean isWeekend(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        return dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY;
    }
    
    public static boolean isPastDate(Date date) {
        Date today = new Date();
        return date.before(today);
    }
    
    public static int getDayOfWeek(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_WEEK);
    }
}