package com.example.mqtt_chat_demo.config;

import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

@Configuration
public class MqttConfig {

    // Tunisia time zone: UTC+1, no DST since 2009
    private static final ZoneId TUNISIA_ZONE = ZoneId.of("Africa/Tunis");

    private static final String BROKER_URL = "tcp://broker.hivemq.com:1883";

    // Generated once at startup using real Tunisian time
    private static final String CLIENT_ID = "spring-chat-" +
            Instant.now(Clock.system(TUNISIA_ZONE)).toEpochMilli();

    private static final String TOPIC = "demo/chat";

    @Bean
    public MqttClient mqttClient() throws MqttException {
        // Make client ID unique even if app restarts quickly
        String uniqueClientId = CLIENT_ID + "-" + System.nanoTime() % 10000;

        MqttClient client = new MqttClient(BROKER_URL, uniqueClientId);

        MqttConnectionOptions options = new MqttConnectionOptions();
        options.setCleanStart(true);           // Start fresh every time (recommended for public brokers)
        options.setAutomaticReconnect(true);
        options.setConnectionTimeout(10);
        options.setKeepAliveInterval(60);

        client.setCallback(new MqttCallback() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                System.out.println("(Re)connected to broker: " + serverURI);
                try {
                    client.subscribe(TOPIC, 1);
                    System.out.println("Subscribed to topic: " + TOPIC);
                } catch (MqttException e) {
                    System.err.println("Subscribe failed: " + e.getMessage());
                }
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                String payload = new String(message.getPayload());
                System.out.printf("[%s] %s: %s%n",
                        java.time.ZonedDateTime.now(TUNISIA_ZONE).format(
                                java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")),
                        topic, payload);
            }

            @Override
            public void disconnected(MqttDisconnectResponse disconnectResponse) {
                System.out.println("Disconnected: " + disconnectResponse.getReasonString());
            }

            @Override
            public void mqttErrorOccurred(MqttException exception) {
                System.err.println("MQTT Error: " + exception.getMessage());
            }

            @Override
            public void deliveryComplete(IMqttToken token) {
                // System.out.println("Delivery complete"); // optional
            }

            @Override
            public void authPacketArrived(int reasonCode, MqttProperties properties) {
                // Not used with public broker
            }
        });

        System.out.println("Connecting to broker with ClientId: " + uniqueClientId);
        client.connect(options);

        return client;
    }
}