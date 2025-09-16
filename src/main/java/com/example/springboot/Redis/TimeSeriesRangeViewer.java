package com.example.springboot.Redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import redis.clients.jedis.UnifiedJedis;
import redis.clients.jedis.timeseries.TSElement;

import java.util.Collections;
import java.util.List;

@Component
public class TimeSeriesRangeViewer {
    private final UnifiedJedis jedis;
    private static final Logger logger = LoggerFactory.getLogger(TimeSeriesRangeViewer.class);

    public TimeSeriesRangeViewer(UnifiedJedis jedis) {
        this.jedis = jedis;
    }

    public List<TSElement> getDataInRange(String key) {
        long now = System.currentTimeMillis();
        long oneHourAgo = now - 3600000L;
        // Check if the key exists before querying
        if (!jedis.exists(key)) {
            logger.warn("Time series key does not exist: {}. Returning empty list.", key);
            return Collections.emptyList();
        }

        logger.info("Fetching time series data for key: {} from {} to {}", key, oneHourAgo, now);
        List<TSElement> results = jedis.tsRange(key, oneHourAgo, now);
        logger.info("Found {} data points for key: {}", results.size(), key);

        return results;
    }
}
