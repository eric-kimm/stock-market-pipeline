package com.example.springboot.Redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.timeseries.TSElement;

import java.util.List;

@RequestMapping("/timeseries")
@RestController
public class TimeSeriesController {
    private final TimeSeriesRangeViewer viewer;
    private static final Logger logger = LoggerFactory.getLogger(TimeSeriesRangeViewer.class);


    public TimeSeriesController(TimeSeriesRangeViewer viewer) {
        this.viewer = viewer;
    }

    @GetMapping("/{symbol}")
    public List<TSElement> getStockPrices(@PathVariable String symbol) {
        String key =  "stock:" + symbol.toUpperCase() + ":price";
        logger.info("Called getStockPrices for:" + key);
        return viewer.getDataInRange(key);
    }
}
