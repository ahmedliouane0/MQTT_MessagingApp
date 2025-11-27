package com.example.mqtt_chat_demo.config;

import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqttConfig {

    private static final String BROKER_URL = "tcp://broker.hivemq.com:1883";
    private static final String CLIENT_ID = "spring-chat-" + System.currentTimeMillis();
    private static final String TOPIC = "demo/chat";

    @Bean
    public MqttClient mqttClient() throws MqttException {
        MqttClient client = new MqttClient(BROKER_URL, CLIENT_ID);
        
        MqttConnectionOptions options = new MqttConnectionOptions();
        options.setCleanStart(true);
        options.setAutomaticReconnect(true);
        
        // Set callback for incoming messages
        client.setCallback(new MqttCallback() {
            @Override
            public void disconnected(MqttDisconnectResponse disconnectResponse) {
                System.out.println("Disconnected from MQTT broker");
            }

            @Override
            public void mqttErrorOccurred(MqttException exception) {
                System.err.println("MQTT error: " + exception.getMessage());
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                System.out.println("Message received on topic '" + topic + "': " + 
                                   new String(message.getPayload()));
            }

            @Override
            public void deliveryComplete(IMqttToken token) {
                System.out.println("Message delivery complete");
            }

            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                System.out.println("Connected to MQTT broker: " + serverURI);
                try {
                    client.subscribe(TOPIC, 1);
                    System.out.println("Subscribed to topic: " + TOPIC);
                } catch (MqttException e) {
                    System.err.println("Failed to subscribe: " + e.getMessage());
                }
            }

            @Override
            public void authPacketArrived(int reasonCode, MqttProperties properties) {
                // Not needed for this example
            }
        });
        
        // Connect to broker
        client.connect(options);
        
        return client;
    }
}