package com.mydaygpt;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

public class TimeParser {
    public static ZonedDateTime getEventDate(String inputDate, String timeZone) {
        // Define a custom formatter that can handle these aspects:
        //
        // - milliseconds are optional
        // - trailing Z may or may not be present
        // - timezone offsets like "+03:00" may or may not be present
        //
        // Since we have the timezone in a dedicated (other) field we can treat the other timestamps
        // as local times.
        //
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .append(DateTimeFormatter.ISO_LOCAL_DATE)
                .appendLiteral('T')
                .appendPattern("HH:mm:ss")
                .optionalStart()
                .appendPattern(".SSS")
                .optionalEnd()
                .optionalStart()
                .appendOffset("+HH:mm", "+00:00") // Offset handling, but it's essentially ignored for LocalDateTime parsing
                .optionalEnd()
                .optionalStart()
                .appendLiteral('Z') // 'Z' for UTC, treated as +00:00 offset but ignored
                .optionalEnd()
                .toFormatter();

        LocalDateTime localDateTime = LocalDateTime.parse(inputDate, formatter);
        ZoneId zoneId = ZoneId.of(timeZone);
        ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);

        // Force seconds / milliseconds / nanoseconds to 0 since we can't work with them anyway
        zonedDateTime = zonedDateTime.withSecond(0).withNano(0);

        System.out.println("Parsed without timezone, converted to " + timeZone + ": " + zonedDateTime);

        return zonedDateTime;
    }
}
