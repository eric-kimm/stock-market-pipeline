Stock Market Data Pipeline on the Cloud

Pipeline Work Flow
1. WebSocket connection between Finnhub API and Spring Boot data ingestor to obtain stock data
2. Stock data is published to kafka
3. Kafka distributes stock data to PostgreSQL for history and Redis for insight analytics and fast visualizations.
