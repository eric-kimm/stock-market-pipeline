package com.example.springboot.DataConsumer;


import org.springframework.data.jpa.repository.JpaRepository;

// Set up JpaRepository to handle JSON messages
public interface StockTradeRepository extends JpaRepository<StockTrade, Long> {

}
