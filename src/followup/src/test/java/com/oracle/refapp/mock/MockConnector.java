/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.mock;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import io.helidon.common.reactive.Multi;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.InjectionPoint;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.SubmissionPublisher;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.spi.Connector;
import org.eclipse.microprofile.reactive.messaging.spi.ConnectorFactory;
import org.eclipse.microprofile.reactive.messaging.spi.IncomingConnectorFactory;
import org.eclipse.microprofile.reactive.messaging.spi.OutgoingConnectorFactory;
import org.eclipse.microprofile.reactive.streams.operators.PublisherBuilder;
import org.eclipse.microprofile.reactive.streams.operators.ReactiveStreams;
import org.eclipse.microprofile.reactive.streams.operators.SubscriberBuilder;
import org.reactivestreams.FlowAdapters;

@ApplicationScoped
@Connector("mock-connector")
public class MockConnector implements IncomingConnectorFactory, OutgoingConnectorFactory {

  @Target({ FIELD })
  @Retention(RUNTIME)
  public @interface Channel {
    String value();
  }

  private final Map<String, SubmissionPublisher<Message<?>>> pubs = new ConcurrentHashMap<>();
  private final Map<String, List<Message<?>>> cons = new ConcurrentHashMap<>();

  @Produces
  @Dependent
  @SuppressWarnings("unchecked,rawtypes")
  public SubmissionPublisher<Message> producesEmitter(InjectionPoint ip) {
    String channelName = ip.getAnnotated().getAnnotation(Channel.class).value();
    return (SubmissionPublisher) pubs.get(channelName);
  }

  @Produces
  @Dependent
  @SuppressWarnings("unchecked,rawtypes")
  public List<Message> producesList(InjectionPoint ip) {
    String channelName = ip.getAnnotated().getAnnotation(Channel.class).value();
    return (List<Message>) (List) getList(channelName);
  }

  @Override
  public PublisherBuilder<? extends Message<?>> getPublisherBuilder(Config config) {
    SubmissionPublisher<Message<?>> pub = new SubmissionPublisher<>();
    pubs.put(config.getValue(ConnectorFactory.CHANNEL_NAME_ATTRIBUTE, String.class), pub);
    return ReactiveStreams.fromPublisher(FlowAdapters.toPublisher(Multi.create(pub).log()));
  }

  @Override
  public SubscriberBuilder<? extends Message<?>, Void> getSubscriberBuilder(Config config) {
    String channelName = config.getValue(ConnectorFactory.CHANNEL_NAME_ATTRIBUTE, String.class);
    return ReactiveStreams
      .<Message<?>>builder()
      .onError(Throwable::printStackTrace)
      .forEach(m -> {
        getList(channelName).add(m);
        m.ack();
      });
  }

  private List<Message<?>> getList(String channelName) {
    return cons.compute(
      channelName,
      (s, messages) -> Optional.ofNullable(messages).orElseGet(CopyOnWriteArrayList::new)
    );
  }
}
