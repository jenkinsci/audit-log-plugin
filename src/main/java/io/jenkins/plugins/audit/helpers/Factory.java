package io.jenkins.plugins.audit.helpers;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Factory {
    /*
     * Returns a date string in the ISO-8601 format
     */
    public static String dateTimeFormat() {
        ZonedDateTime d = ZonedDateTime.of(LocalDateTime.now(), ZoneId.of("America/Los_Angeles"));
        String s = d.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        return s;
    }

}