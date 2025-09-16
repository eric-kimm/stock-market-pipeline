package com.example.springboot.DataConsumer;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RawTradeRepository extends JpaRepository<RawTrade,Long> {
}
