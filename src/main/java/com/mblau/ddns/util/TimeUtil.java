package com.mblau.ddns.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class TimeUtil {

    private static final String PATTERN_FORMAT = "dd.MM.yyyy hh:mm:ss z";
    private static final DateTimeFormatter defaultFormatter = DateTimeFormatter.ofPattern(PATTERN_FORMAT)
            .withZone(ZoneId.systemDefault());

    public static DateTimeFormatter defaultTimeFormatter() {
        return defaultFormatter;
    }

    public static String prettyPrintCurrentTime() {
        return defaultTimeFormatter().format(Instant.now());
    }
}
