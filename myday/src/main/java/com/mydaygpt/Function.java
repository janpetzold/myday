package com.mydaygpt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mailersend.sdk.exceptions.MailerSendException;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import io.github.cdimascio.dotenv.Dotenv;
import net.fortuna.ical4j.model.Calendar;

import java.nio.file.FileSystemException;
import java.text.ParseException;
import java.util.Optional;

/**
 * Azure Functions with HTTP Trigger.
 */
public class Function {
    Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

    @FunctionName("appointments")
    public HttpResponseMessage run(
            @HttpTrigger(
                name = "req",
                methods = {HttpMethod.POST},
                authLevel = AuthorizationLevel.ANONYMOUS)
                HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        String validApiKey = dotenv.get("API_KEY");
        if (validApiKey == null) {
            validApiKey = System.getenv("API_KEY");
        }
            
        // Validate API key
        final String apiKey = request.getHeaders().get("x-api-key");
        if (apiKey == null || !apiKey.equals(validApiKey)) {
            return request.createResponseBuilder(HttpStatus.UNAUTHORIZED).body("Invalid or missing API key.").build();
        }

        // Parse request
        String jsonBody = request.getBody().orElse(null);
        if (jsonBody == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Please pass appointment details in the request body").build();
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            Appointment appointment = mapper.readValue(jsonBody, Appointment.class);

            // TODO: add actual language detection
            String language = "en";

            // Log the input values
            context.getLogger().info("Received appointment details: ");
            context.getLogger().info("Language: " + appointment.startTime);
            context.getLogger().info("Start Time: " + appointment.startTime);
            context.getLogger().info("End Time: " + appointment.endTime);
            context.getLogger().info("Title: " + appointment.title);
            context.getLogger().info("Location: " + appointment.location);
            // context.getLogger().info("Sender: " + appointment.sender);
            // context.getLogger().info("Mail: " + appointment.mail);

            context.getLogger().info("Creating event");
            EventGenerator eventGenerator = new EventGenerator();
            Calendar calendarItem = eventGenerator.generateCalendarEvent(appointment);

            context.getLogger().info("Sending mail with attachment");
            Mailer mailer = new Mailer();
            mailer.sendEmail(appointment, calendarItem, language);
            
            return request.createResponseBuilder(HttpStatus.OK).body("Appointment created successfully.").build();

        } catch (ParseException e) {
            String err = "Error parsing start and/or end time";
            context.getLogger().warning(err);
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body(err).build();
        } catch (MailerSendException e) {
            String err = "Mail could not be sent";
            context.getLogger().warning(err);
            return request.createResponseBuilder(HttpStatus.SERVICE_UNAVAILABLE).body(err).build();
        } catch (FileSystemException e) {
            String err = "Could not generate ics file as calendar to mail";
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body(err).build();
        } catch (JsonProcessingException e) {
            String err = "Could not process JSON input payload";
            context.getLogger().warning(err);
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body(err).build();
        }
    }
}
