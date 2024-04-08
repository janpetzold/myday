package com.mydaygpt;

import com.mailersend.sdk.emails.Email;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystemException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.mailersend.sdk.MailerSend;
import com.mailersend.sdk.MailerSendResponse;
import com.mailersend.sdk.exceptions.MailerSendException;

import io.github.cdimascio.dotenv.Dotenv;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;

public class Mailer {
    Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

    private String cleanup(String input) {
        // Replace all special characters
        String cleanString = input.replaceAll("[^a-zA-Z0-9]", "");

        // Shorten to max. 16 characters
        cleanString = cleanString.substring(0, Math.min(16, cleanString.length()));

        return cleanString.toLowerCase();
    }

    // Create temporary file and delete on exit
    private File generateCalendarFile(Calendar icsCalendar, Appointment appointment) throws IOException {
        String fileName = "myday-invite-" + cleanup(appointment.getSender()) + "-" + cleanup(appointment.getLocation()) + "-on-" + getTimeForMailTitle(appointment.getStartTime()) + ".ics";
        File tempFile = new File(fileName);
        tempFile.deleteOnExit();

        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            CalendarOutputter outputter = new CalendarOutputter();
            outputter.output(icsCalendar, fos);
        }

        return tempFile;
    }

    public Email compileEmail(Appointment appointment, Calendar calendarItem, String language) throws FileSystemException {
        Email email = new Email();

        if(appointment.getTitle() == null) {
            email.setSubject("Myday: New invite");
        } else {
            email.setSubject("Myday 📅: New invite \"" + appointment.getTitle() + "\"");
        }

        if(appointment.getSender() == null) {
            appointment.setSender("myday");
        }
        email.setFrom(appointment.getSender(), "noreply@mydaygpt.com");

        email.addRecipient(appointment.getMail(), appointment.getMail());
        email.setPlain("Hello from mydayGPT, there's a new appointment for you, see invite attached.\r\n\r\nLocation: " + appointment.getLocation() + "\r\n\r\nStart time: " + getTimeForMailBody(appointment.getStartTime()) + " (Timezone " + appointment.getTimeZone() + ")\r\n\r\nEnd time: " + getTimeForMailBody(appointment.getEndTime()));

        File icsFile;
        try {
            icsFile = this.generateCalendarFile(calendarItem, appointment);
            email.attachFile(icsFile);
        } catch (IOException e) {
            throw new FileSystemException("calendar.ics");
        }

        return email;
    }

    public String sendEmail(Email email) throws MailerSendException {
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

    private String getTimeForMailTitle(String inputDate) {
        // Parsing the ISO8601 string to a LocalDateTime object
        LocalDateTime dateTime = LocalDateTime.parse(inputDate, DateTimeFormatter.ISO_DATE_TIME);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        return dateTime.format(formatter);
    }

    private String getTimeForMailBody(String inputDate) {
        // Parsing the ISO8601 string to a LocalDateTime object
        LocalDateTime dateTime = LocalDateTime.parse(inputDate, DateTimeFormatter.ISO_DATE_TIME);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy 'at' HH:mm");

        return dateTime.format(formatter);
    }
}
