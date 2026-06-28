# Upstream Service

HTTP ingress service for the Event Routing Engine. Accepts payment-related events via REST and publishes them to a Kafka topic for downstream processing.

## Prerequisites

- Java 17+
- Apache Kafka (default: `localhost:9092`)

## Configuration

Settings are in `src/main/resources/application.properties`:


| Property                         | Default          | Description                |
| -------------------------------- | ---------------- | -------------------------- |
| `server.port`                    | `9090`           | HTTP port                  |
| `spring.kafka.bootstrap-servers` | `localhost:9092` | Kafka broker               |
| `app.kafka.events-topic`         | `events`         | Topic for published events |


Override locally with environment variables or an `application-local.properties` file (gitignored).

## Run locally

```bash
./mvnw spring-boot:run
```

The service starts on `http://localhost:9090`.

## API

### Publish event

```http
POST /events
Content-Type: application/json
```

**Request body**

```json
{
  "eventId": "1001",
  "eventType": "PAYMENT_STATUS_UPDATED",
  "paymentMode": "UPI_QR",
  "paymentStatus": "SUCCESS",
  "merchantId": "1001",
  "customerId": "1001",
  "transactionId": "1001",
  "amount": "250",
  "currency": "INR",
  "metadata": {
    "source": "mobile-app"
  }
}
```

**Response:** `202 Accepted` (empty body)

Published Kafka messages include a server-generated `timestamp` (IST, `yyyy-MM-dd HH:mm:ss`).

### Supported enum values


| Field           | Values                         |
| --------------- | ------------------------------ |
| `eventType`     | `PAYMENT_STATUS_UPDATED`       |
| `paymentMode`   | `UPI_QR`, `ONLINE_CHECKOUT`    |
| `paymentStatus` | `PENDING`, `SUCCESS`, `FAILED` |
| `currency`      | `INR`                          |


## Tests

```bash
./mvnw test
```

Tests use Spring's embedded Kafka broker; no external Kafka instance is required.

## Project structure

```
src/main/java/com/eventrouting/upstream/
├── controller/   # REST endpoints
├── dto/          # Request and publish payloads
├── enums/        # Event, payment, and currency types
└── kafka/        # Kafka producer
```

## Build

```bash
./mvnw clean package
```

