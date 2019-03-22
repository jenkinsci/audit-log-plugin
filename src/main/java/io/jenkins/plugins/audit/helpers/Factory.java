package io.jenkins.plugins.audit.helpers;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Factory {
    /**
     * Returns a date string in the ISO-8601 format
     * @param date Date object to be formatted.
     */
    public static String formatDateISO(Date date) {
        SimpleDateFormat sdf;
        TimeZone timeZone = Calendar.getInstance().getTimeZone();
        sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        sdf.setTimeZone(timeZone);
        return sdf.format(date);
    }
}
