package com.atlas32.e2e;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.atlas32.Application;
import com.atlas32.domain.atlas.command.Command;
import com.atlas32.domain.atlas.command.CommandType;
import com.atlas32.domain.atlas.device.Device;
import com.atlas32.infrastructure.mqtt.consumer.LocationUpdateConsumer;
import com.atlas32.infrastructure.mqtt.producer.CommandPublisher;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
public class MqttEndToEndTest {

  private static MqttClient testClient;

  @Autowired
  CommandPublisher commandPublisher;

  @BeforeAll
  static void setupClient() throws Exception {
    String testClientId = "testClient-" + System.currentTimeMillis();

    String brokerUrl =
        "tcp://" + "localhost" + ":" + "1883";
    testClient = new MqttClient(brokerUrl, testClientId);

    MqttConnectOptions options = new MqttConnectOptions();
    options.setCleanSession(true);
    testClient.connect(options);
  }

  @AfterAll
  static void teardownClient() throws Exception {
    if (testClient != null && testClient.isConnected()) {
      testClient.disconnect();
      testClient.close();
    }
  }

  @Test
  @DisplayName("Given a command, "
      + "when the producer sends it, "
      + "then the consumer should receive it")
  void testOutboundPublishing() throws Exception {
    String deviceId = "123";
    String topicToSubscribe = "device/" + deviceId + "/command";

    CountDownLatch latch = new CountDownLatch(1);
    final StringBuilder receivedPayload = new StringBuilder();

    // subsribe to the topic
    testClient.subscribe(topicToSubscribe, (topic, msg) -> {
      receivedPayload.append(new String(msg.getPayload()));
      latch.countDown();
    });

    Command testCommand = new Command(
        new Device(deviceId),
        CommandType.GET_COORDINATES,
        "payloadTest"
    );

    // publish the command
    commandPublisher.sendCommand(testCommand);

    boolean messageReceived = latch.await(5, TimeUnit.SECONDS);

    Assertions.assertTrue(messageReceived, "The message did not arrive within the expected time frame");
    Assertions.assertTrue(receivedPayload.toString().contains("GET_COORDINATES"),
        "The payload should contain the command type");
    Assertions.assertTrue(receivedPayload.toString().contains("payloadTest"),
        "The payload should contain the command payload");
  }

  @Test
  @DisplayName("Given a location update message, "
      + "when the consumer receives it, "
      + "then it should log the message")
  void testInboundLog() throws Exception {
    Logger logger = (Logger) LoggerFactory.getLogger(LocationUpdateConsumer.class);
    ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
    listAppender.start();
    logger.addAppender(listAppender);

    String deviceId = "123";
    String topicToSubscribe = "device/" + deviceId + "/response";

    testClient.publish(topicToSubscribe, new MqttMessage());

    Thread.sleep(500);

    boolean found = listAppender.list.stream()
        .anyMatch(event -> event.getFormattedMessage().contains("Location update message received:"));

    Assertions.assertTrue(found);
  }
}