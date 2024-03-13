package com.mydaygpt;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.text.ParseException;

import org.junit.jupiter.api.Test;

import com.mailersend.sdk.exceptions.MailerSendException;

import net.fortuna.ical4j.model.Calendar;

public class MailerTest {
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
        String messageId = mailer.sendEmail(appointment, calendarItem, "en");

        assertNotNull(messageId);
        assertNotEquals("", messageId, "Message ID must be a defined ID");
    }
    
}
