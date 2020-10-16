package io.jenkins.plugins.audit.helpers;

import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Restricted(NoExternalUse.class)
public class DateTimeHelper {
    /**
     * Returns a date string in the ISO-8601 format
     * @param milliseconds Time in epoch milliseconds to be formatted
     */
    public static String formatDateISO(long milliseconds) {
        return DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(
                ZonedDateTime.ofInstant(Instant.ofEpochMilli(milliseconds), ZoneId.systemDefault()));
    }

    public static String currentDateTimeISO() {
        return formatDateISO(System.currentTimeMillis());
    }
}
