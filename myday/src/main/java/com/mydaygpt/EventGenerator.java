package com.mydaygpt;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import net.fortuna.ical4j.model.*;
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

        VEvent event = new VEvent(startDateTime, endDateTime, appointment.getLocation());

        // Add UUID
        event.getProperties().add(new RandomUidGenerator().generateUid());

        // Add the location
        event.getProperties().add(new Location(appointment.getLocation()));

        // Create a calendar
        Calendar icsCalendar = new Calendar();
        icsCalendar.getProperties().add(new ProdId("-//irlGPT//irlGPT 1.0//EN"));
        icsCalendar.getProperties().add(Version.VERSION_2_0);
        icsCalendar.getProperties().add(CalScale.GREGORIAN);
        
        // Add the event
        icsCalendar.getComponents().add(event);

        return icsCalendar;
    }
}
