package com.example.springboot.DataConsumer;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.kafka.annotation.KafkaListener;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisDataException;
import redis.clients.jedis.timeseries.DuplicatePolicy;
import redis.clients.jedis.timeseries.TSAddParams;


@Component
public class StockTradeConsumerService {
    private final StockTradeRepository repository;
    private final RawTradeRepository rawTradeRepository;
    private final JedisPooled jedisPooled;
    private final ObjectMapper mapper = new ObjectMapper();
    private static final Logger log = LogManager.getLogger(StockTradeConsumerService.class);
    private static final long RETENTION_PERIOD_MS = 86400000L;
    private static final DuplicatePolicy DUPLICATE_POLICY = DuplicatePolicy.LAST;

    public StockTradeConsumerService(StockTradeRepository repository, RawTradeRepository rawTradeRepository, JedisPooled jedisPooled) {
        this.repository = repository;
        this.rawTradeRepository = rawTradeRepository;
        this.jedisPooled = jedisPooled;
    }

    // Save data to RedisTimeSeries

    public void addDataPoint(String key, double value, Map<String, String> labels) {
        try {
            TSAddParams addParams = new TSAddParams()
                    .retention(RETENTION_PERIOD_MS)
                    .duplicatePolicy(DUPLICATE_POLICY)
                    .labels(labels);
            jedisPooled.tsAdd(key, System.currentTimeMillis(), value, addParams);
            log.info("Added data point for key: " + key + " with labels " + labels);
        } catch (JedisConnectionException e) {
            log.error("Failed to connect to Redis while adding data point to RedisTimeSeries:", e);
        } catch (JedisDataException e) {
            log.error("Invalid command or data error from Redis:", e);
        } catch (Exception e) {
            log.error("Error adding data point to RedisTimeSeries:", e);
        }
    }
     // Listens for published messages and stores in Postgres
    @KafkaListener(
            topics = "stock-trades",
            groupId = "myGroup"
    )
    public void consume(String message) {
        try {
            RawTrade raw = new RawTrade();
            raw.setRawMessage(message);
            rawTradeRepository.save(raw);

            JsonNode root = mapper.readTree(message);
            JsonNode dataArray = root.path("data");

            if (dataArray.isArray()) {
                for (JsonNode node : dataArray) {
                    StockTrade trade = new StockTrade();

                    trade.setPrice(node.path("p").asDouble());
                    trade.setSymbol(node.path("s").asText());
                    trade.setTimestamp(node.path("t").asLong());
                    trade.setVolume(node.path("v").asInt());

                    String key = "stock:" + trade.getSymbol() + ":price";

                    Map<String, String> labels = Map.of(
                            "type", "stock",
                            "symbol", trade.getSymbol(),
                            "metric", "price"
                    );
                    addDataPoint(key, trade.getPrice(), labels);
                    repository.save(trade);
                    System.out.println(mapper.writeValueAsString(trade));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
