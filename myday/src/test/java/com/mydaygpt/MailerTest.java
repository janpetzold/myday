package com.mydaygpt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.text.ParseException;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.mailersend.sdk.emails.Email;
import com.mailersend.sdk.exceptions.MailerSendException;

import net.fortuna.ical4j.model.Calendar;

public class MailerTest {
    @Test
    public void testCompileMail() throws ParseException, IOException, MailerSendException {
        Appointment appointment = new Appointment();
        appointment.setStartTime("2025-01-03T12:00:00.000Z");
        appointment.setEndTime("2025-01-03T14:00:00.000Z");
        appointment.setLocation("Mortifami");

        // Could be checked via https://temp-mail.org/
        appointment.setMail("fokawep747@hdrlog.com");

        EventGenerator eventGenerator = new EventGenerator();
        Calendar calendarItem = eventGenerator.generateCalendarEvent(appointment);

        Mailer mailer = new Mailer();
        Email emailWithoutTitle = mailer.compileEmail(appointment, calendarItem, "en");

        assertNotNull(emailWithoutTitle);

        assertEquals("Myday: New invite", emailWithoutTitle.subject);
        assertEquals("myday", emailWithoutTitle.from.name);
        assertEquals("noreply@mydaygpt.com", emailWithoutTitle.from.email);

        appointment.setSender("Yannick");
        Email emailWithSender = mailer.compileEmail(appointment, calendarItem, "en");
        assertEquals("Yannick", emailWithSender.from.name);
        assertEquals("noreply@mydaygpt.com", emailWithSender.from.email);

        appointment.setTitle("Test drive");
        Email emailWithTitle = mailer.compileEmail(appointment, calendarItem, "en");
        assertEquals("Myday: New invite \"Test drive\"", emailWithTitle.subject);
    }

    @Test
    public void testSendMail() throws ParseException, IOException, MailerSendException {
        Appointment appointment = new Appointment();
        appointment.setStartTime("2025-01-03T12:00:00.000Z");
        appointment.setEndTime("2025-01-03T14:00:00.000Z");
        appointment.setLocation("Mortifami");
        appointment.setSender("Yannick");

        // Could be checked via https://temp-mail.org/
        appointment.setMail("fokawep747@hdrlog.com");

        EventGenerator eventGenerator = new EventGenerator();
        Calendar calendarItem = eventGenerator.generateCalendarEvent(appointment);

        Mailer mailer = new Mailer();
        Email email = mailer.compileEmail(appointment, calendarItem, "en");
        String messageId = mailer.sendEmail(email);

        assertNotNull(email);
        assertNotNull(messageId);
        assertNotEquals("", messageId, "Message ID must be a defined ID");
    }
    
}
