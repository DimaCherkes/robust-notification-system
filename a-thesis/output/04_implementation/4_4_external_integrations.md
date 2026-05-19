# 4.4 External API Integration and Email Services

A notification system is only as good as its ability to interact with the outside world. This system integrates with two critical external services: **OpenWeather API** and **AWS SES**.

### 4.4.1 Weather Data Acquisition (OpenWeather)
The Weather Service acts as a bridge to the OpenWeather API.
*   **Scheduling:** Uses Spring `@Scheduled` to poll the API at configurable intervals.
*   **Error Handling:** Implements exponential backoff when the API is rate-limited or unavailable.
*   **Normalization:** Converts the complex JSON response from OpenWeather into a simplified internal `WeatherReport` format.

### 4.4.2 Email Delivery (AWS SES)
**Amazon Simple Email Service (SES)** is used for high-scale, reliable email delivery.
*   **Sandbox Mode:** During development and for this thesis, the system operates in SES Sandbox mode, which requires verifying the identities of both the sender and the recipient.
*   **Integration:** The Notification Service uses the AWS SDK for Java to communicate with SES.
*   **Verification:** SES provides built-in feedback on bounces and complaints, allowing for future enhancements in delivery tracking.

[DIAGRAM: External service integration flow (Weather Service and SES)]

By using managed services for these external interactions, the Robust Notification System avoids the complexity of building its own weather-gathering or email-sending infrastructure, allowing it to focus on the core logic of decision-making and alerting.
