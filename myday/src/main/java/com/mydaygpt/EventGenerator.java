package com.mydaygpt;

import java.time.ZonedDateTime;

import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.component.VAlarm;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.*;
import net.fortuna.ical4j.util.RandomUidGenerator;

public class EventGenerator {
    public final Calendar generateCalendarEvent(Appointment appointment) {
        DateTime startDateTime;
        DateTime endDateTime;
        DateTime reminderDateTime;

        TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
        TimeZone timezone = registry.getTimeZone(appointment.getTimeZone());
        VTimeZone tz = timezone.getVTimeZone();

        System.out.println("EventGenerator set to timezone " + tz.getTimeZoneId().getValue());

        ZonedDateTime startDate = TimeParser.getEventDate(appointment.getStartTime(), appointment.getTimeZone());
        startDateTime = new DateTime(Date.from(startDate.toInstant()), new TimeZone(tz));

        ZonedDateTime endDate = TimeParser.getEventDate(appointment.getEndTime(), appointment.getTimeZone());
        endDateTime = new DateTime(Date.from(endDate.toInstant()), new TimeZone(tz));

        // Location is mandatory, therefore take it as default for title
        String title = appointment.getLocation();
        if(appointment.getTitle() != null && appointment.getTitle().length() > 0) {
            title = appointment.getTitle();
        }
        
        VEvent event = new VEvent(startDateTime, endDateTime, title);

        // Add UUID
        event.getProperties().add(new RandomUidGenerator().generateUid());

        // Add the location
        event.getProperties().add(new Location(appointment.getLocation()));

        // Create a calendar
        Calendar icsCalendar = new Calendar();
        icsCalendar.getProperties().add(new ProdId("-//mydayGPT//mydayGPT 1.0//EN"));
        icsCalendar.getProperties().add(Version.VERSION_2_0);
        icsCalendar.getProperties().add(CalScale.GREGORIAN);

        // Add reminder if present
        if(appointment.getReminderTime() != null && appointment.getReminderTime().length() > 0) {
            ZonedDateTime reminderDate = TimeParser.getEventDate(appointment.getReminderTime(), appointment.getTimeZone());
            reminderDateTime = new DateTime(Date.from(reminderDate.toInstant()), new TimeZone(tz));

            VAlarm reminder = new VAlarm(reminderDateTime);
            reminder.getProperties().add(Action.DISPLAY);

            if(appointment.getTitle() == null) {
                reminder.getProperties().add(new Description("myday Reminder"));
            } else {
                reminder.getProperties().add(new Description("myday Reminder: " + appointment.getTitle()));
            }

            event.getAlarms().add(reminder);
        }
        
        // Add the event
        icsCalendar.getComponents().add(event);

        return icsCalendar;
    }
}
