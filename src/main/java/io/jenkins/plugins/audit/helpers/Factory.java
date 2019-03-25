package io.jenkins.plugins.audit.helpers;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Factory {
    /**
     * Returns a date string in the ISO-8601 format
     * @param milliseconds Time in epoch milliseconds to be formatted
     */
    public static String formatDateISO(Long milliseconds) {
        return DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(
                ZonedDateTime.ofInstant(Instant.ofEpochMilli(milliseconds), ZoneId.systemDefault()));
    }
}
