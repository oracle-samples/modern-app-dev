/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.notification;

import com.oracle.refapp.notification.template.EncounterNotificationTemplate;
import com.oracle.refapp.notification.template.PatientAppointmentNotificationTemplate;
import com.oracle.refapp.notification.template.ProviderAppointmentNotificationTemplate;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.util.ByteArrayDataSource;
import java.io.InputStream;
import java.util.Properties;

public class EmailDeliveryService {

  static final String FROM_ADDRESS = System.getenv("FROM_ADDRESS");
  static final String FROM_NAME = "UHO Notification";

  // OCI SMTP username (ocid) generated in console.
  static final String SMTP_USERNAME = System.getenv("SMTP_USERNAME");

  // OCI SMTP password generated in console.
  static final String SMTP_PASSWORD = System.getenv("SMTP_PASSWORD");

  // OCI Email Delivery hostname (eg: "smtp.email.us-ashburn-1.oci.oraclecloud.com").
  static final String HOST = System.getenv("SMTP_HOST");

  static final int PORT = 587;

  static final Properties props = System.getProperties();

  static Session session;

  private static void initEmailSession() {
    props.put("mail.transport.protocol", "smtp");
    props.put("mail.smtp.port", PORT);
    //props.put("mail.smtp.ssl.enable", "true"); //the default value is false if not set
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.auth.login.disable", "true"); //the default authorization order is "LOGIN PLAIN DIGEST-MD5 NTLM". 'LOGIN' must be disabled since Email Delivery authorizes as 'PLAIN'
    props.put("mail.smtp.starttls.enable", "true"); //TLSv1.2 is required
    props.put("mail.smtp.starttls.required", "true"); //Oracle Cloud Infrastructure required

    // Create a Session object to represent a mail session with the specified properties.
    session = Session.getDefaultInstance(props);
  }

  public static void sendEncounterPdfEmail(
    String patientName,
    String patientEmail,
    String pdfName,
    InputStream pdfFileStream
  ) throws Exception {
    // Create a Properties object to contain connection configuration information.
    System.out.println(patientName + " : " + patientEmail);

    initEmailSession();

    // Create a message with the specified information (for patient).
    MimeMessage toPatientMsg = new MimeMessage(session);

    // Create Multipart body (for pdf and text)
    MimeMultipart multipart = new MimeMultipart();
    MimeBodyPart messageBodyPart = new MimeBodyPart();
    EncounterNotificationTemplate encounterNotificationTemplate = new EncounterNotificationTemplate();
    String toPatientBody = encounterNotificationTemplate.getMessage(patientName);
    messageBodyPart.setText(toPatientBody, "utf-8", "html");
    multipart.addBodyPart(messageBodyPart);
    MimeBodyPart attachmentBodyPart = new MimeBodyPart();
    DataSource ds = new ByteArrayDataSource(pdfFileStream, "application/pdf");
    attachmentBodyPart.setDataHandler(new DataHandler(ds));
    attachmentBodyPart.setFileName(pdfName);
    multipart.addBodyPart(attachmentBodyPart);

    toPatientMsg.setFrom(new InternetAddress(FROM_ADDRESS, FROM_NAME));
    toPatientMsg.setRecipient(Message.RecipientType.TO, new InternetAddress(patientEmail));
    toPatientMsg.setSubject("Details about your encounter with your doctor");
    toPatientMsg.setContent(multipart);

    try (Transport transport = session.getTransport()) {
      System.out.println("Sending Email...");

      // Connect to OCI Email Delivery using the SMTP credentials specified.
      transport.connect(HOST, SMTP_USERNAME, SMTP_PASSWORD);

      // Send email.
      transport.sendMessage(toPatientMsg, toPatientMsg.getAllRecipients());
      System.out.println("Email sent successfully");
    } catch (Exception ex) {
      System.out.println("The email was not sent");
      System.out.println("Error message: " + ex.getMessage());
    }
  }

  public static void sendAppointmentBookedEmail(
    String patientEmail,
    String providerEmail,
    String patientName,
    String providerName,
    String startTime,
    String endTime,
    String status
  ) throws Exception {
    initEmailSession();

    PatientAppointmentNotificationTemplate patientAppointmentNotificationTemplate =
      new PatientAppointmentNotificationTemplate();
    String toPatientBody = patientAppointmentNotificationTemplate.getMessage(
      patientName,
      providerName,
      status,
      startTime,
      endTime
    );

    ProviderAppointmentNotificationTemplate providerAppointmentNotificationTemplate =
      new ProviderAppointmentNotificationTemplate();
    String toProviderBody = providerAppointmentNotificationTemplate.getMessage(
      patientName,
      providerName,
      status,
      startTime,
      endTime
    );

    // Create a message with the specified information (for patient).
    MimeMessage toPatientMsg = new MimeMessage(session);
    toPatientMsg.setFrom(new InternetAddress(FROM_ADDRESS, FROM_NAME));
    toPatientMsg.setRecipient(Message.RecipientType.TO, new InternetAddress(patientEmail));
    toPatientMsg.setSubject("Appointment Request Notification");
    toPatientMsg.setContent(toPatientBody, "text/html");

    // Create a message with the specified information (for provider).
    MimeMessage toProviderMsg = new MimeMessage(session);
    toProviderMsg.setFrom(new InternetAddress(FROM_ADDRESS, FROM_NAME));
    toProviderMsg.setRecipient(Message.RecipientType.TO, new InternetAddress(providerEmail));
    toProviderMsg.setSubject("Appointment Request Notification");
    toProviderMsg.setContent(toProviderBody, "text/html");

    try (Transport transport = session.getTransport()) {
      System.out.println("Sending Emails...");

      // Connect to OCI Email Delivery using the SMTP credentials specified.
      transport.connect(HOST, SMTP_USERNAME, SMTP_PASSWORD);

      // Send emails.
      transport.sendMessage(toPatientMsg, toPatientMsg.getAllRecipients());
      transport.sendMessage(toProviderMsg, toProviderMsg.getAllRecipients());
      System.out.println("Email sent successfully");
    } catch (Exception ex) {
      System.out.println("The email was not sent.");
      System.out.println("Error message: " + ex.getMessage());
    }
  }

  public static void sendFeedbackFollowupEmail(String emailSubject, String emailBody, String patientEmail)
    throws Exception {
    initEmailSession();

    // Create a message with the specified information (for patient).
    MimeMessage toPatientMsg = new MimeMessage(session);
    toPatientMsg.setFrom(new InternetAddress(FROM_ADDRESS, FROM_NAME));
    toPatientMsg.setRecipient(Message.RecipientType.TO, new InternetAddress(patientEmail));
    toPatientMsg.setSubject(emailSubject);
    toPatientMsg.setContent(emailBody, "text/html");

    try (Transport transport = session.getTransport()) {
      System.out.println("Sending Email...");

      // Connect to OCI Email Delivery using the SMTP credentials specified.
      transport.connect(HOST, SMTP_USERNAME, SMTP_PASSWORD);

      // Send email.
      transport.sendMessage(toPatientMsg, toPatientMsg.getAllRecipients());
      System.out.println("Email sent successfully");
    } catch (Exception ex) {
      System.out.println("The email was not sent.");
      System.out.println("Error message: " + ex.getMessage());
    }
  }
}
