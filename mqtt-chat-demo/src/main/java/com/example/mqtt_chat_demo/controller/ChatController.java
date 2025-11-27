package com.example.mqtt_chat_demo.controller;

import com.example.mqtt_chat_demo.service.MqttMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    @Autowired
    private MqttMessageService mqttService;

    @PostMapping("/send")
    public String sendMessage(@RequestBody String message) {
        mqttService.sendMessage(message);
        return "Message sent: " + message;
    }

    @GetMapping("/health")
    public String health() {
        return "Chat service is running!";
    }
    
    @GetMapping("/status")
    public String status() {
        boolean connected = mqttService.isConnected();
        return connected ? "MQTT Connected ✅" : "MQTT Disconnected ❌";
    }
    
}