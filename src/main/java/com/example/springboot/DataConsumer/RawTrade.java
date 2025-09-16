package com.example.springboot.DataConsumer;

import jakarta.persistence.*;

@Entity
@Table(name = "raw_trades")
public class RawTrade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "raw_message", columnDefinition = "TEXT", nullable = false)
    private String rawMessage;

    private java.sql.Timestamp receivedAt = new java.sql.Timestamp(System.currentTimeMillis());

    public String getRawMessage() { return rawMessage; }
    public void setRawMessage(String rawMessage) { this.rawMessage = rawMessage; }
}
