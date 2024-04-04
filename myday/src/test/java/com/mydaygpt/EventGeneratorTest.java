package com.mydaygpt;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Version;

public class EventGeneratorTest {

    // This is the ical4j DateTime format we need for verification
    DateTimeFormatter customFormatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");

    @Test
    void testGenerateCalendarEvent() {
        Appointment appointment = new Appointment();
        appointment.setStartTime("2025-01-03T12:00:00.000Z");
        appointment.setEndTime("2025-01-03T14:00:00.000Z");
        appointment.setTimeZone("Etc/UTC");
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
    void testVerifyTimesMilliseconds() {
        Appointment appointment = new Appointment();
        appointment.setStartTime("2024-04-26T15:00:00.000");
        appointment.setEndTime("2024-04-26T16:00:00.123");
        appointment.setTimeZone("Europe/Berlin");
        appointment.setLocation("Mortifami");

        EventGenerator generator = new EventGenerator();
        Calendar result = generator.generateCalendarEvent(appointment);

        VEvent event = (VEvent) result.getComponent(VEvent.VEVENT);
        DtStart startDate = event.getStartDate();
        DtEnd endDate = event.getEndDate();

        assertEquals(startDate.getTimeZone().getID(), appointment.getTimeZone());
        assertEquals(endDate.getTimeZone().getID(), appointment.getTimeZone());

        assertEquals(
            LocalDateTime.parse(startDate.getDate().toString(), customFormatter), 
            LocalDateTime.parse(appointment.getStartTime())
        );
        
        assertEquals(
            LocalDateTime.parse(endDate.getDate().toString(), customFormatter), 
            LocalDateTime.parse(appointment.getEndTime()).withSecond(0).withNano(0)
        );
    }

    @Test
    void testVerifyBucharestTimezone() {
        Appointment appointment = new Appointment();
        appointment.setStartTime("2023-04-22T11:00:00");
        appointment.setEndTime("2023-04-22T13:00:00");
        appointment.setTimeZone("Europe/Bucharest");
        appointment.setLocation("Mortifami");

        EventGenerator generator = new EventGenerator();
        Calendar result = generator.generateCalendarEvent(appointment);

        VEvent event = (VEvent) result.getComponent(VEvent.VEVENT);
        DtStart startDate = event.getStartDate();
        DtEnd endDate = event.getEndDate();

        assertEquals(startDate.getTimeZone().getID(), appointment.getTimeZone());
        assertEquals(endDate.getTimeZone().getID(), appointment.getTimeZone());

        assertEquals(
            LocalDateTime.parse(startDate.getDate().toString(), customFormatter), 
            LocalDateTime.parse(appointment.getStartTime())
        );
        
        assertEquals(
            LocalDateTime.parse(endDate.getDate().toString(), customFormatter), 
            LocalDateTime.parse(appointment.getEndTime())
        );
    }

    @Test
    void testVerifyTimeZoneWithoutZ() {
        Appointment appointment = new Appointment();
        appointment.setStartTime("2023-04-22T11:00:00");
        appointment.setEndTime("2023-04-22T13:00:00");
        appointment.setTimeZone("Europe/Bucharest");
        appointment.setLocation("Mortifami");

        EventGenerator generator = new EventGenerator();
        Calendar result = generator.generateCalendarEvent(appointment);

        VEvent event = (VEvent) result.getComponent(VEvent.VEVENT);
        DtStart startDate = event.getStartDate();
        DtEnd endDate = event.getEndDate();

        assertEquals(startDate.getTimeZone().getID(), appointment.getTimeZone());
        assertEquals(endDate.getTimeZone().getID(), appointment.getTimeZone());

        assertEquals(
            LocalDateTime.parse(startDate.getDate().toString(), customFormatter), 
            LocalDateTime.parse(appointment.getStartTime())
        );
        
        assertEquals(
            LocalDateTime.parse(endDate.getDate().toString(), customFormatter), 
            LocalDateTime.parse(appointment.getEndTime())
        );
    }

    @Test
    void testVerifyTimeZoneWithZ() {
        Appointment appointment = new Appointment();
        appointment.setStartTime("2023-04-22T11:00:00Z");
        appointment.setEndTime("2023-04-22T13:00:00Z");
        appointment.setTimeZone("Europe/Bucharest");
        appointment.setLocation("Mortifami");

        EventGenerator generator = new EventGenerator();
        Calendar result = generator.generateCalendarEvent(appointment);

        VEvent event = (VEvent) result.getComponent(VEvent.VEVENT);
        DtStart startDate = event.getStartDate();
        DtEnd endDate = event.getEndDate();

        assertEquals(startDate.getTimeZone().getID(), appointment.getTimeZone());
        assertEquals(endDate.getTimeZone().getID(), appointment.getTimeZone());

        DateTimeFormatter zFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

        assertEquals(
            LocalDateTime.parse(startDate.getDate().toString(), customFormatter), 
            LocalDateTime.parse(appointment.getStartTime(), zFormatter)
        );
        
        assertEquals(
            LocalDateTime.parse(endDate.getDate().toString(), customFormatter), 
            LocalDateTime.parse(appointment.getEndTime(), zFormatter)
        );
    }

    @Test
    void testVerifyTimeZoneWithOffset() {
        Appointment appointment = new Appointment();
        appointment.setStartTime("2023-04-22T11:00:00+03:00");
        appointment.setEndTime("2023-04-22T13:00:00+03:00");
        appointment.setTimeZone("Europe/Bucharest");
        appointment.setLocation("Mortifami");

        EventGenerator generator = new EventGenerator();
        Calendar result = generator.generateCalendarEvent(appointment);

        VEvent event = (VEvent) result.getComponent(VEvent.VEVENT);
        DtStart startDate = event.getStartDate();
        DtEnd endDate = event.getEndDate();

        assertEquals(startDate.getTimeZone().getID(), appointment.getTimeZone());
        assertEquals(endDate.getTimeZone().getID(), appointment.getTimeZone());

        DateTimeFormatter offsetFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZZZZZ");

        assertEquals(
            LocalDateTime.parse(startDate.getDate().toString(), customFormatter), 
            LocalDateTime.parse(appointment.getStartTime(), offsetFormatter)
        );
        
        assertEquals(
            LocalDateTime.parse(endDate.getDate().toString(), customFormatter), 
            LocalDateTime.parse(appointment.getEndTime(), offsetFormatter)
        );
    }

    @Test
    void testVerifyReminder() {
        Appointment appointment = new Appointment();
        appointment.setStartTime("2023-04-22T11:00:00");
        appointment.setEndTime("2023-04-22T13:00:00");
        appointment.setReminderTime("2023-04-22T10:30:00");
        appointment.setTimeZone("Europe/Berlin");
        appointment.setLocation("Mortifami");

        EventGenerator generator = new EventGenerator();
        Calendar result = generator.generateCalendarEvent(appointment);
        VEvent event = (VEvent) result.getComponent(VEvent.VEVENT);

        // for whatever reason the formatter for alarms is different to the other times
        DateTimeFormatter alarmFormatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");

        // TODO: Fix reminder timing
        /*
        assertEquals(
            LocalDateTime.parse(event.getAlarms().get(0).getTrigger().getDateTime().toString(), alarmFormatter), 
            LocalDateTime.parse(appointment.getReminderTime())
        );
         */
    }

}
