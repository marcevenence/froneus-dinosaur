# 🦖 Dinosaur Management System - Froneus Challenge

This project is a robust microservice developed in **Spring Boot 3.5.9** for dinosaur management. It includes automated state change processes (Schedulers), asynchronous messaging integration (RabbitMQ), and persistence in a NoSQL database (MongoDB).

## 🚀 Key Features

- **RESTful API**: Full CRUD for specimen management.
- **Business Validations**: Unique name enforcement, discovery/extinction date logic, and immutable states.
- **Automation (Schedulers)**: Automatic state updates to `ENDANGERED` and `EXTINCT` based on time.
- **Asynchronous Messaging**: Automatic notifications to RabbitMQ on state changes.
- **Documentation**: Swagger UI integrated under the OpenAPI 3 standard.
- **Dockerized**: Full deployment (App + DB + Broker) via Docker Compose.

---

## 🛠️ Tech Stack

- **Language:** Java 21 (Eclipse Temurin)
- **Framework:** Spring Boot 3.5.9
- **Database:** MongoDB 8.2
- **Messaging:** RabbitMQ 4.2.5
- **Mapping:** MapStruct 1.5.5
- **Documentation:** SpringDoc OpenAPI 2.8.5
- **Testing:** JUnit 5, Mockito, AssertJ, Awaitility

---

## 📋 Prerequisites

- Docker and Docker Compose installed.
- Java 21 (only if you wish to run it outside Docker).
- Maven 3.9+ (only for local compilation).

---

## ⚙️ Local Environment Setup

### 1. Environment Variables
Create or edit the `docker/.env` file with the following credentials:

```env
# MongoDB Config
MONGO_PORT=27017
MONGO_ROOT_USER=root
MONGO_ROOT_PASS=example
MONGO_DATABASE=dinosaur_db

# RabbitMQ Config
RABBITMQ_USER=admin
RABBITMQ_PASS=secret
```

### 2. Deployment with Docker Compose

The project offers two Docker configurations depending on your needs:

#### A. Development Environment (Infrastructure Only)
Ideal for running the application from your **IDE** (IntelliJ/Eclipse) and connecting to the local infrastructure:

```bash
# From the docker/ folder
docker compose -f docker-compose-dev.yml up -d
```
This command starts:
- **MongoDB** (Port 27017)
- **Mongo Express UI** (Port 8081)
- **RabbitMQ Broker & UI** (Ports 5672, 15672)

Once the infrastructure is up, you can run the `Application.java` class from your IDE.

#### B. Full Deployment (App + Infrastructure)
Ideal for testing the full deployment including the dockerized application:

```bash
# From the docker/ folder
docker compose up -d
```
This command builds the **App** image and starts all services.

- **App:** [http://localhost:8080](http://localhost:8080)
- **Swagger UI:** [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- **RabbitMQ Management:** [http://localhost:15672](http://localhost:15672)

---

## 💾 Data Initialization and Population

To load initial data and configure the necessary indexes (`unique name` and `status`), run the load script once the containers are up:

```bash
# From the project root
chmod +x docker/mongo/load-data.sh
./docker/mongo/load-data.sh
```

---

## 🧪 Running Tests

The test suite includes unit tests, integration tests, and a full integration workflow (Full Workflow).

```bash
# Run all tests
mvn test

# Run only the full integration workflow
mvn test -Dtest=DinosaurFullIntegrationTest
```

---

## 📡 Main Endpoints

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/dinosaur` | Creates a new dinosaur (Initial status: `ALIVE`). |
| `GET` | `/dinosaur` | Retrieves the full list of dinosaurs. |
| `GET` | `/dinosaur/{id}` | Retrieves details of a dinosaur by ID. |
| `PUT` | `/dinosaur/{id}` | Updates data. (Not allowed if status is `EXTINCT`). |
| `DELETE` | `/dinosaur/{id}` | Removes a record from the database. |

---

## 🔄 Scheduler Logic

The system runs a scheduled task every **10 minutes** (configurable via `scheduler.cron` in `application.yml`):
1. **ALIVE ➡️ ENDANGERED**: Triggered 24 hours before `extinctionDate`.
2. **ANY ➡️ EXTINCT**: Triggered upon reaching `extinctionDate`.
*Each change automatically publishes a JSON message to RabbitMQ.*

---

## 🏗️ Project Structure

```text
src/
 ├── main/
 │    ├── java/com/froneus/dinosaur/
 │    │    ├── config/      # RabbitMQ and Swagger Configuration
 │    │    ├── controller/  # REST Endpoints
 │    │    ├── dto/         # Data Transfer Objects (Requests/Responses)
 │    │    ├── mapper/      # MapStruct Interfaces
 │    │    ├── model/       # MongoDB Entities
 │    │    ├── repository/  # Data Access Layer
 │    │    └── service/     # Business Logic and Schedulers
 │    └── resources/
 │         ├── application.yml      # Generic configuration
 │         ├── application-dev.yml  # Local/Docker configuration
 │         └── application-prod.yml # Production configuration
 └── test/                  # Full Test Suite
```
