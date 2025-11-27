package com.example.mqtt_chat_demo.service;

import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MqttMessageService {

    private static final String TOPIC = "demo/chat";

    @Autowired
    private MqttClient mqttClient;

    public void sendMessage(String messageText) {
        try {
            MqttMessage message = new MqttMessage(messageText.getBytes());
            message.setQos(1);
            message.setRetained(false);
            
            mqttClient.publish(TOPIC, message);
            System.out.println("Published message: " + messageText);
            
        } catch (MqttException e) {
            System.err.println("Error publishing message: " + e.getMessage());
            throw new RuntimeException("Failed to send MQTT message", e);
        }
    }

    public boolean isConnected() {
        return mqttClient.isConnected();
    }
}