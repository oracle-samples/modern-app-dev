/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.notification;

import static io.micronaut.email.BodyType.HTML;
import static io.micronaut.http.MediaType.APPLICATION_PDF;
import static java.util.Collections.singletonMap;

import io.micronaut.email.Attachment;
import io.micronaut.email.Email;
import io.micronaut.email.EmailSender;
import io.micronaut.email.StringBody;
import io.micronaut.email.template.TemplateBody;
import io.micronaut.views.ModelAndView;
import jakarta.inject.Singleton;
import java.io.InputStream;
import java.util.Map;

@Singleton
public class EmailDeliveryService {

  private final EmailSender<?, ?> emailSender;

  public EmailDeliveryService(EmailSender<?, ?> emailSender) {
    this.emailSender = emailSender;
  }

  public void sendEncounterPdfEmail(
    String patientName,
    String patientEmail,
    String pdfName,
    InputStream pdfFileStream
  ) {
    emailSender.send(
      Email
        .builder()
        .to(patientEmail)
        .subject("Details about your encounter with your doctor")
        .body(new TemplateBody<>(HTML, new ModelAndView<>("encounter", singletonMap("name", patientName))))
        .attachment(Attachment.builder().filename(pdfName).contentType(APPLICATION_PDF).content(pdfFileStream).build())
    );
  }

  public void sendAppointmentBookedEmail(
    String patientEmail,
    String providerEmail,
    String patientName,
    String providerName,
    String startTime,
    String endTime,
    String status
  ) {
    // for patient
    emailSender.send(
      Email
        .builder()
        .to(patientEmail)
        .subject("Appointment Request Notification")
        .body(
          new TemplateBody<>(
            HTML,
            new ModelAndView<>(
              "appointment_patient",
              Map.of(
                "patientName",
                patientName,
                "providerName",
                providerName,
                "date",
                DateUtils.getDate(startTime),
                "slot",
                DateUtils.getTime(startTime) + " - " + DateUtils.getTime(endTime),
                "status",
                status
              )
            )
          )
        )
    );

    // for provider
    emailSender.send(
      Email
        .builder()
        .to(providerEmail)
        .subject("Appointment Request Notification")
        .body(
          new TemplateBody<>(
            HTML,
            new ModelAndView<>(
              "appointment_provider",
              Map.of(
                "patientName",
                patientName,
                "providerName",
                providerName,
                "date",
                DateUtils.getDate(startTime),
                "slot",
                DateUtils.getTime(startTime) + " - " + DateUtils.getTime(endTime),
                "status",
                status
              )
            )
          )
        )
    );
  }

  public void sendFeedbackFollowupEmail(String emailSubject, String emailBody, String patientEmail) {
    emailSender.send(Email.builder().to(patientEmail).subject(emailSubject).body(new StringBody(emailBody, HTML)));
  }
}
