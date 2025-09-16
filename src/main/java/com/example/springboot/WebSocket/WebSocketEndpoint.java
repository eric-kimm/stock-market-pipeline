package com.example.springboot.WebSocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.core.KafkaTemplate;

import java.io.IOException;

//Client endpoint that defines how Spring Boot app(client) communicates with Finnhub API (server) through WebSocket
@ClientEndpoint
public class WebSocketEndpoint {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private Session session;
    private static final Logger log = LogManager.getLogger(WebSocketEndpoint.class);

    // Inject kafkaTemplate bean
    public WebSocketEndpoint(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    // Handles WebSocket connection
    @OnOpen
    public void onOpen(Session session) throws IOException {

        // Get session and WebSocket connection
        log.info("Connected to WebSocket server: " + session.getId());
        this.session = session;

        String [] symbols =
                {
                "AAPL", "NVDA", "MSFT", "GOOG", "AMZN", "META", "TSLA", "NFLX", "ORCL", "TSMC",
                "GME", "AMC", "LCID", "RIVN", "MSTR", "JPM", "XOM"
        };

        try {
            for (String symbol : symbols) {
                String subscribe = "{\"type\":\"subscribe\", \"symbol\":\"" + symbol + "\"}";

                // Subscribe to Finnhub API for each symbol
                session.getBasicRemote().sendText(subscribe);
                Thread.sleep(50);
            }
        } catch (Exception e) {
            log.error("Error while sending data to WebSocket server: " + e.getMessage());
        }
    }

    // Handles new messages received from Finnhub
    @OnMessage
    public void onMessage(Session session, String message) throws IOException {
        try {
            JsonNode jsonNode = new ObjectMapper().readTree(message);
            String type = jsonNode.get("type").asText();                // Read message as jsonNode to check "type"
            if (!type.equals("ping")) {
                log.info("Message received from Finnhub: " + message);
                kafkaTemplate.send("stock-trades", message);
                kafkaTemplate.send("testing",  message);
            } else {
                System.out.println("Ping ignored");
                log.info("Ping ignored LOG");
            }
        } catch (Exception e) {
            log.error("Error in receiving message from Finnhub: " +  e.getMessage());
        }
        kafkaTemplate.send("testing",  message);
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        // WebSocket connection closes
        System.out.println("Connection closed: " +  session.getId());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        // Do error handling here
        System.err.println("WebSocker error: " + session.getId() + ": " + throwable.getMessage());
    }
}
