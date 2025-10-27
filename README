# Kafka Producer Demo - Group 2

This README explains the codebase, file structure, how Kafka and ZooKeeper are implemented, how the frontend interacts with the backend API, and provides clear start / troubleshoot instructions.

## Project structure (important files)

Top-level layout (project root: contains `docker-compose.yml`, `pom.xml`):

```
kafka-producer-group2-demo/
├── docker-compose.yml            # starts Zookeeper + Kafka
├── mvnw, mvnw.cmd                # maven wrapper
├── pom.xml                       # dependencies and build
├── src/
│   ├── main/
│   │   ├── java/com/group2/kafka_producer_group2_demo/
│   │   │   ├── KafkaEventProducer.java   # sends events to Kafka
│   │   │   ├── KafkaEventConsumer.java   # listens to Kafka topic and stores recent events
│   │   │   ├── KafkaProducerGroup2DemoApplication.java  # Spring Boot main
│   │   │   ├── controller/                # REST controllers
│   │   │   │   ├── OrderController.java   # POST /api/orders -> send to Kafka
│   │   │   │   └── EventController.java   # GET /api/events -> returns recent events
│   │   │   └── dto/                       # DTOs for incoming orders
│   │   │       └── OrderDto.java
│   │   └── resources/
│   │       └── static/                    # static frontend served by Spring Boot
│   │           ├── index.html
│   │           ├── app.js
│   │           └── app.css
│   └── test/
├── README.md                        # this file
```
> Note: In this project both ZooKeeper and Kafka are started inside Docker containers using `docker-compose.yml` (the demo uses the Confluent images). See the T1 docker-compose steps below to start them.

## How Kafka is implemented

- The project uses Spring for Apache Kafka (Spring Kafka) with `KafkaTemplate` for producing and `@KafkaListener` for consuming.
- Producer: `KafkaEventProducer` builds a JSON-compatible Map and calls `kafkaTemplate.send(topic, event)` to send to topic `OrderPlaced`.
- Consumer: `KafkaEventConsumer` listens to `OrderPlaced` using `@KafkaListener` and stores recent events in an in-memory deque (used by `EventController` for the UI).
- Serialization: the application uses Spring's `JsonSerializer`/`JsonDeserializer` (configured in `application.properties`) so Map/POJO objects are converted to/from JSON automatically.

## What is ZooKeeper and why is it used here

- ZooKeeper is a coordination service used by older Kafka deployments to manage broker metadata, leader election, and cluster state.
- The demo uses `confluentinc/cp-zookeeper` paired with the Confluent Kafka image. In production newer Kafka versions can run without ZooKeeper (KRaft mode), but this stack uses ZooKeeper for simplicity and compatibility with the Confluent images provided.


## How the frontend calls the API

- The frontend is a simple static site served by Spring Boot from `src/main/resources/static/index.html`.
- When a user submits the order form the JS in `app.js` sends:
  - POST `/api/orders` with JSON payload matching `OrderDto`:
    ```json
    {"orderId":"ORD-1001","customerName":"Alice","items":["Pizza"],"address":"123"}
    ```
  - The server `OrderController` receives that payload, converts it to a Map, and calls `KafkaEventProducer.sendOrderPlacedEvent(map)`.
  - The consumer picks up the message from Kafka and stores it; the UI polls GET `/api/events` every 5 seconds to show recent events.

## Where the frontend is viewed (port)

- The Spring Boot app runs an embedded Tomcat on port **8080** by default. The static UI will be available at:
  - http://localhost:8080/index.html

## Start / Troubleshooting flow (T1/T2)

Run these in two separate terminals.

T1 — Docker (Kafka + ZooKeeper)

MAIN:
```powershell
Set-Location -LiteralPath 'D:\D SCHOOL\SYSTEMS\kafka-producer-group2-demo\kafka-producer-group2-demo'
docker-compose up -d
docker ps
```

TROUBLESHOOT (if something fails):
```powershell
docker-compose down
docker network prune   # confirm when prompted
docker-compose up -d
docker ps
```

Notes:
- Use `docker logs <container>` to inspect container logs, or `docker-compose logs`.
- Confirm the Kafka container name exactly — typically `kafka-producer-group2-demo-kafka-1` in this project.

T2 — Start Spring Boot backend (serves frontend + API)

MAIN:
```powershell
Set-Location -LiteralPath 'D:\D SCHOOL\SYSTEMS\kafka-producer-group2-demo\kafka-producer-group2-demo'
.\mvnw.cmd spring-boot:run
```

Run detached (optional) and follow logs:
```powershell
Start-Process -FilePath '.\mvnw.cmd' -ArgumentList 'spring-boot:run' -WorkingDirectory (Get-Location).Path -RedirectStandardOutput '.\spring.log' -RedirectStandardError '.\spring.err' -PassThru
Get-Content .\spring.log -Tail 200 -Wait
```

Quick verification (after both T1 and T2 are running):

1. Open frontend: http://localhost:8080/index.html
2. Submit the form.
3. Or use PowerShell to test via API:
```powershell
# send example order
Invoke-RestMethod -Uri 'http://localhost:8080/api/orders' -Method Post -ContentType 'application/json' -Body '{"orderId":"ORD-1002","customerName":"Bob Lee","items":["Pizza","Soda"],"address":"45 River Rd"}'
# view events
Invoke-RestMethod -Uri 'http://localhost:8080/api/events' -Method Get | ConvertTo-Json -Depth 5
```

## Common troubleshooting tips

- If Spring Boot fails to start: check `spring.log` (if started detached) or the console running `mvnw`. Look for Tomcat bind errors or Kafka connection errors.
- If UI fails to POST: open browser DevTools → Network and see the HTTP status and response body.
- If Kafka errors: check `docker logs kafka-producer-group2-demo-kafka-1` and the Spring Boot logs for connectivity errors.

## Summary

- Frontend: served by Spring Boot at port **8080**.
- Backend API: POST `/api/orders` and GET `/api/events`.
- Message broker: Kafka (topic `OrderPlaced`) with ZooKeeper for cluster coordination.

If you want, I can also add examples of `curl`/PowerShell commands and commit the changes; tell me if you want me to commit them to git.