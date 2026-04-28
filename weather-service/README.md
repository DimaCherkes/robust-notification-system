# Weather Service

## Role
The **Weather Service** is responsible for providing current and forecast weather data. It acts as an aggregator that fetches data from external providers (OpenWeatherMap) and distributes updates to other services in the system.

## Implemented Functionality
- **Weather Synchronization**:
    - Scheduled tasks to fetch weather data for all monitored cities.
    - Integration with OpenWeatherMap API (One Call 3.0).
- **Change Detection**:
    - Compares new weather data with stored data to identify significant changes.
    - Tracks hourly forecasts for temperature, humidity, wind, and precipitation.
- **Event Dispatching**:
    - Publishes `weather_update` events directly to SQS when significant weather changes are detected.
- **Resilience**:
    - Implements Circuit Breaker pattern (Resilience4j) for external API calls to ensure system stability during provider outages.
- **Tech Stack**: Spring Boot, Resilience4j, AWS SQS, RestClient, PostgreSQL, Flyway.
