package com.mydaygpt;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Version;

public class EventGeneratorTest {
    @Test
    void testGenerateCalendarEvent() {
        Appointment appointment = new Appointment();
        appointment.setStartTime("2025-01-03T12:00:00.000Z");
        appointment.setEndTime("2025-01-03T14:00:00.000Z");
        appointment.setLocation("Mortifami");

        EventGenerator generator = new EventGenerator();
        
        // Execute
        Calendar result = generator.generateCalendarEvent(appointment);

        // Verify the calendar properties
        assertNotNull(result);
        assertEquals("-//mydayGPT//mydayGPT 1.0//EN", result.getProperty(ProdId.PRODID).getValue());
        assertEquals(Version.VERSION_2_0, result.getProperty(Version.VERSION));
        assertEquals(1, result.getComponents().size());

        // Verify event details
        VEvent event = (VEvent) result.getComponent(VEvent.VEVENT);
        assertNotNull(event);

        // Verify the event UID is a valid UUID
        String eventUid = event.getUid().getValue();
        assertDoesNotThrow(() -> UUID.fromString(eventUid), "The event UID should be a valid UUID.");

        assertEquals(appointment.getLocation(), event.getLocation().getValue(), "Event location should be correct");
    }

    @Test
    void testVerifyTimes() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[.SSS]'Z'");

        TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
        TimeZone timezone = registry.getTimeZone("Etc/UTC");
        VTimeZone tz = timezone.getVTimeZone();

        Appointment appointment = new Appointment();
        appointment.setStartTime("2024-03-26T15:00:00Z");
        appointment.setEndTime("2024-03-26T15:00:00.123Z");
        appointment.setLocation("Mortifami");

        EventGenerator generator = new EventGenerator();
        
        // Execute
        Calendar result = generator.generateCalendarEvent(appointment);

        VEvent event = (VEvent) result.getComponent(VEvent.VEVENT);
        DtStart startDate = event.getStartDate();
        DtEnd endDate = event.getEndDate();

        ZonedDateTime initialStartDate = ZonedDateTime.parse(appointment.getStartTime(), formatter.withZone(ZoneId.of("UTC")));
        DateTime actualStartDateTime = new DateTime(Date.from(initialStartDate.toInstant()), new TimeZone(tz));

        ZonedDateTime initialEndDate = ZonedDateTime.parse(appointment.getEndTime(), formatter.withZone(ZoneId.of("UTC")));
        DateTime actualEndDateTime = new DateTime(Date.from(initialEndDate.toInstant()), new TimeZone(tz));

        assertEquals(startDate.getDate(), actualStartDateTime, "The event's start time does not match the expected value.");
        assertEquals(endDate.getDate(), actualStartDateTime, "The event's end time does not match the expected value.");
    }

}
