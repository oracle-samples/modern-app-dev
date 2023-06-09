/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.oracle.refapp.clients.encounter.model.Encounter;
import com.oracle.refapp.clients.patient.model.Patient;
import com.oracle.refapp.clients.provider.model.Provider;
import com.oracle.refapp.exceptions.EncounterContextException;
import com.oracle.refapp.exceptions.NoSuchEncounterFoundException;
import com.oracle.refapp.exceptions.NoSuchPatientFoundException;
import com.oracle.refapp.exceptions.NoSuchProviderFoundException;
import com.oracle.refapp.exceptions.ProcessingException;
import com.oracle.refapp.model.EncounterCloudEvent;
import com.oracle.refapp.model.FollowupMessage;
import io.helidon.messaging.connectors.kafka.KafkaMessage;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.faulttolerance.Asynchronous;
import org.eclipse.microprofile.reactive.messaging.Acknowledgment;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.eclipse.microprofile.reactive.streams.operators.ProcessorBuilder;
import org.eclipse.microprofile.reactive.streams.operators.ReactiveStreams;

@Slf4j
@ApplicationScoped
public class FollowupService {

  private static final Mustache MUSTACHE = new DefaultMustacheFactory().compile("email-body.mustache");
  private static final ObjectMapper JSON_MAPPER = new ObjectMapper()
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

  private final IdcsService idcsService;
  private final EncounterService encounterService;
  private final PatientService patientService;
  private final ProviderService providerService;
  private final TelemetryService telemetryService;

  @Inject
  public FollowupService(
    IdcsService idcsService,
    EncounterService encounterService,
    PatientService patientService,
    ProviderService providerService,
    TelemetryService telemetryService
  ) {
    this.idcsService = idcsService;
    this.encounterService = encounterService;
    this.patientService = patientService;
    this.providerService = providerService;
    this.telemetryService = telemetryService;
  }

  /**
   * This method returns a processor which consumes the message generated post Encounter to create feedback request.
   */
  @Incoming("from-stream")
  @Outgoing("to-stream")
  @Acknowledgment(Acknowledgment.Strategy.MANUAL)
  public ProcessorBuilder<Message<String>, KafkaMessage<String, String>> processMessageFromEncounterStream() {
    return ReactiveStreams
      .<Message<String>>builder()
      .map(this::deserializeCloudEvent)
      // Let FaultTolerance to handle blocking part asynchronously
      .flatMap(msg -> ReactiveStreams.fromCompletionStageNullable(this.createFollowupMessage(msg)))
      .map(this::serializeFollowup)
      .onError(this::errorHandler);
  }

  @Asynchronous
  public CompletionStage<Message<FollowupMessage>> createFollowupMessage(
    Message<EncounterCloudEvent> cloudEventMessage
  ) throws NoSuchEncounterFoundException, NoSuchPatientFoundException, NoSuchProviderFoundException {
    String encounterId = cloudEventMessage.getPayload().getData().getEncounterId();
    if (log.isInfoEnabled()) log.info("Got encounterId {} from message", encounterId);

    String accessToken = idcsService.getAuthToken();
    Encounter encounter = encounterService.getEncounterDetails(encounterId, accessToken);

    if (!encounter.getFollowUpRequested()) {
      if (log.isInfoEnabled()) log.info("Followup is not requested for encounter {}", encounterId);
      telemetryService.postFollowupProcessedMetric(1.0, encounterId);
      return cloudEventMessage.ack().thenApply(unused -> null);
    }

    Patient patient = patientService.getPatientDetails(encounter.getPatientId(), accessToken);
    Provider provider = providerService.getProviderDetails(encounter.getProviderId(), accessToken);

    FollowupMessage followupMessage = createFollowupMessage(encounter, provider, patient);

    if (log.isInfoEnabled()) log.info(
      "Sending FollowupMessage {} as requested for encounter {}",
      followupMessage,
      encounterId
    );
    return CompletableFuture.completedFuture(Message.of(followupMessage, cloudEventMessage::ack));
  }

  private Message<EncounterCloudEvent> deserializeCloudEvent(Message<String> incomingMessage) {
    try {
      if (log.isDebugEnabled()) log.debug("Retrieved payload from message {}", incomingMessage.getPayload());
      EncounterCloudEvent event = JSON_MAPPER.readValue(incomingMessage.getPayload(), EncounterCloudEvent.class);
      return Message.of(event, incomingMessage::ack);
    } catch (JsonProcessingException e) {
      telemetryService.postFollowupProcessedMetric(0.0, null);
      throw new ProcessingException("Encounter Message parsing failed", e);
    }
  }

  private KafkaMessage<String, String> serializeFollowup(Message<FollowupMessage> incomingMessage) {
    try {
      FollowupMessage followupMessage = incomingMessage.getPayload();
      String followupAsString = JSON_MAPPER.writeValueAsString(followupMessage);

      if (log.isDebugEnabled()) log.debug("Sending followup message {}", followupAsString);
      telemetryService.postFollowupProcessedMetric(1.0, followupMessage.getEncounterId());

      return KafkaMessage.of(
        followupMessage.getEncounterId(),
        followupAsString,
        () -> {
          if (log.isDebugEnabled()) log.debug("Followup message was acknowledged by followup stream.");
          return incomingMessage.ack();
        }
      );
    } catch (JsonProcessingException e) {
      throw new ProcessingException("Followup Message processing failed", e);
    }
  }

  private void errorHandler(Throwable t) {
    if (t instanceof EncounterContextException) {
      String encounterId = ((EncounterContextException) t).getEncounterId();
      log.error("Error when processing encounter {}", encounterId, t);
      telemetryService.postFollowupProcessedMetric(0.0, encounterId);
    }
    log.error("Error when processing encounter message", t);
  }

  private FollowupMessage createFollowupMessage(Encounter encounter, Provider provider, Patient patient) {
    String providerName = provider.getFirstName() + " " + provider.getLastName();

    FollowupMessage message = new FollowupMessage();
    message.setEncounterId(encounter.getEncounterId());
    message.setPatientName(patient.getName());
    message.setPatientEmail(patient.getEmail());
    message.setProviderName(providerName);
    message.setEmailSubject("Followup request with " + providerName);
    message.setEmailBody(renderEmailBody(provider, patient));
    return message;
  }

  private String renderEmailBody(Provider provider, Patient patient) {
    try {
      StringWriter writer = new StringWriter();
      MUSTACHE.execute(writer, Map.of("provider", provider, "patient", patient)).flush();
      return writer.toString();
    } catch (IOException e) {
      throw new ProcessingException("Error when rendering email body.", e);
    }
  }
}
