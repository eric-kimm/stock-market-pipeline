package com.example.springboot.DataConsumer;
import jakarta.persistence.*;

@Entity
@Table(name = "stock_trades")
public class StockTrade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private double price;
    private String symbol;
    private long timestamp;
    private int volume;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public int getVolume() { return volume; }
    public void setVolume(int volume) { this.volume = volume; }

}
