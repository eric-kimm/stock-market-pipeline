package com.example.springboot.WebSocket;

import jakarta.annotation.PostConstruct;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.WebSocketContainer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.net.URI;

// Create WebSocket container and open connection to Finnhub
@Service
public class WebSocketApp {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${finnhub.api.key}")
    private String API_KEY;

    // Inject kafkaTemplate bean
    public WebSocketApp(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostConstruct
    public void init() {
        String uri = "wss://ws.finnhub.io?token=" + API_KEY;

        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        WebSocketEndpoint endpoint = new WebSocketEndpoint(kafkaTemplate);

        try {
            container.connectToServer(endpoint, URI.create(uri));
            System.out.println("Websocket client started!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
