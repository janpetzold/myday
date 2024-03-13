package com.mydaygpt;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.UUID;
import java.util.TimeZone;

import org.junit.jupiter.api.Test;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Version;

public class EventGeneratorTest {
    @Test
    void testGenerateCalendarEvent() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC")); // Ensure UTC

        Appointment appointment = new Appointment();
        appointment.setStartTime("2025-01-03T12:00:00.000Z");
        appointment.setEndTime("2025-01-03T14:00:00.000Z");
        appointment.setLocation("Mortifami");

        EventGenerator generator = new EventGenerator();
        
        // Execute
        Calendar result = generator.generateCalendarEvent(appointment);

        // Verify the calendar properties
        assertNotNull(result);
        assertEquals("-//irlGPT//irlGPT 1.0//EN", result.getProperty(ProdId.PRODID).getValue());
        assertEquals(Version.VERSION_2_0, result.getProperty(Version.VERSION));
        assertEquals(1, result.getComponents().size());

        // Verify event details
        VEvent event = (VEvent) result.getComponent(VEvent.VEVENT);
        assertNotNull(event);

        // Verify the event UID is a valid UUID
        String eventUid = event.getUid().getValue();
        assertDoesNotThrow(() -> UUID.fromString(eventUid), "The event UID should be a valid UUID.");

        assertEquals(appointment.getLocation(), event.getLocation().getValue(), "Event location should be correct");
        
        String actualStartTime = sdf.format(event.getStartDate().getDate());
        String actualEndTime = sdf.format(event.getEndDate().getDate());

        assertEquals(appointment.getStartTime(), actualStartTime, "The event's start time does not match the expected value.");
        assertEquals(appointment.getEndTime(), actualEndTime, "The event's end time does not match the expected value.");
    }
}
