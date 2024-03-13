package com.mydaygpt;

import com.mailersend.sdk.emails.Email;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystemException;

import com.mailersend.sdk.MailerSend;
import com.mailersend.sdk.MailerSendResponse;
import com.mailersend.sdk.exceptions.MailerSendException;

import io.github.cdimascio.dotenv.Dotenv;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;

public class Mailer {
    Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

    // Create temporary file and delete on exit
    private File generateCalendarFile(Calendar icsCalendar) throws IOException {
        File tempFile = File.createTempFile("calendar", ".ics");

        tempFile.deleteOnExit();

        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            CalendarOutputter outputter = new CalendarOutputter();
            outputter.output(icsCalendar, fos);
        }

        return tempFile;
    }

    public String sendEmail(Appointment appointment, Calendar calendarItem) throws MailerSendException, FileSystemException {
        Email email = new Email();

        if(appointment.getSender() == null || appointment.getSender().length() == 0) {
            appointment.setSender("myday");
        }

        email.setFrom(appointment.getSender(), "noreply@mydaygpt.com");
        email.addRecipient(appointment.getMail(), appointment.getMail());
        
        email.setSubject("New appointment via myday");

        email.setPlain("Hello from mydayGPT, there's a new appointment for you, see invite attached.\r\n\r\nLocation: " + appointment.getLocation() + "\r\n\r\nStart time: " + appointment.getStartTime() + "\r\n\r\nEnd time: " + appointment.getEndTime());

        File icsFile;
        try {
            icsFile = this.generateCalendarFile(calendarItem);
            email.attachFile(icsFile);
        } catch (IOException e) {
            throw new FileSystemException("calendar.ics");
        }

        String token = dotenv.get("MAILERSEND_TOKEN");
        if (token == null) {
            token = System.getenv("MAILERSEND_TOKEN");
        }

        MailerSend ms = new MailerSend();
        ms.setToken(token);

        MailerSendResponse response = ms.emails().send(email);

        // TODO: Add logging
        System.out.println("Message was sent, status code is " + response.responseStatusCode + ", message ID " + response.messageId);
        return response.messageId;
    }
}
