package com.kts.yourweather.presenter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateFormatter {
    String dateDay;

    public static String dayOfTheWeek(long date){
        String pattern = "EE";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, new Locale("ru", "RU"));
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+3"));
        String dateOfTheWeek = simpleDateFormat.format(new Date(date * 1000));
        return dateOfTheWeek.toUpperCase();
    }

    public static String fullDate(long date){
        String pattern = "EE, dd MMM yyyy, HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, new Locale("ru", "RU"));
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+3"));
        String dateToday;
        if (date == 0) dateToday = simpleDateFormat.format(new Date());
        else dateToday = simpleDateFormat.format(new Date(date * 1000));
        return dateToday.toUpperCase();
    }
}
