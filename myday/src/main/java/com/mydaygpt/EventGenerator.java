package com.mydaygpt;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Optional;

import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.component.VAlarm;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.*;
import net.fortuna.ical4j.util.RandomUidGenerator;

public class EventGenerator {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    public final Calendar generateCalendarEvent(Appointment appointment) throws ParseException{
        sdf.setTimeZone(TimeZone.getTimeZone("UTC")); // Ensure UTC

        DateTime startDateTime;
        DateTime endDateTime;
        
        startDateTime = new DateTime(sdf.parse(appointment.getStartTime()));
        startDateTime.setUtc(true);

        endDateTime = new DateTime(sdf.parse(appointment.getEndTime()));
        endDateTime.setUtc(true);

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
            DateTime reminderDateTime = new DateTime(sdf.parse(appointment.getReminderTime()));
            reminderDateTime.setUtc(true);

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
