# 📋 Project Recap & Prompts Summary

This document summarizes all the requirements and technical prompts used to build the **Dinosaur Management System**.

---

## 🏗️ 1. Project Initialization
- **Prompt**: Generate a Maven project structure with Spring Boot for a Dinosaur microservice.
- **Specifications**:
    - Endpoints: `POST /dinosaur`, `GET /dinosaur`, `GET /dinosaur/{id}`, `PUT /dinosaur/{id}`, `DELETE /dinosaur/{id}`.
    - Fields: `name` (unique), `species`, `discoveryDate`, `extinctionDate`, `status` (ALIVE, ENDANGERED, EXTINCT).
    - Business Rules: `discoveryDate < extinctionDate`, Initial status `ALIVE`, `EXTINCT` dinosaurs cannot be modified.

## ⚙️ 2. Infrastructure & Configuration
- **Prompt**: Convert `application.properties` to `application.yml` with `dev` and `prod` profiles.
- **Prompt**: Configure Docker Compose with MongoDB, Mongo Express, and RabbitMQ.
- **Prompt**: Automate MongoDB initialization (user creation, database setup, and unique indexes) using an `init.js` script inside `docker-entrypoint-initdb.d`.
- **Prompt**: Parameterize database and broker credentials using environment variables (`.env`).

## 🛠️ 3. Core Development
- **Prompt**: Integrate **MapStruct** with **Lombok** to handle DTO mapping (Create/Update requests).
- **Prompt**: Add **Swagger/OpenAPI 3** documentation with detailed DTO descriptions and response examples.
- **Prompt**: Implement a **Global Exception Handler** to return structured JSON error responses (e.g., `{"message": "..."}`).

## 🔄 4. Automation & Messaging
- **Prompt**: Implement **Schedulers** (running every 10 mins):
    - `ALIVE` -> `ENDANGERED` (24h before `extinctionDate`).
    - `ANY` -> `EXTINCT` (on `extinctionDate`).
- **Prompt**: Integrate **RabbitMQ** (AMQP) to notify state changes.
    - Architecture: Broker (Sender) and Client (Receiver) services.
    - Reliability: Implement `@Async` sending with `try-catch` blocks and RabbitMQ retries (backoff) in YAML.

## 🐳 5. Containerization
- **Prompt**: Create a multi-stage **Dockerfile** for the Spring Boot app.
- **Prompt**: Separate Docker Compose files:
    - `docker-compose.yml`: Full stack (App + Infra).
    - `docker-compose-dev.yml`: Infrastructure only (for IDE development).

## 🧪 6. Quality Assurance (Testing)
- **Prompt**: Create a comprehensive test suite:
    - **Unit Tests**: Business logic validation with JUnit 5 and Mockito.
    - **Integration Tests**: Controller validation using `@SpringBootTest` and `@MockitoBean`.
    - **Full Workflow Tests**: End-to-end integration covering Schedulers, MongoDB persistence, and RabbitMQ notifications.
- **Fixes**: Implementation of **Awaitility** to test asynchronous RabbitMQ messages.

## 📖 7. Documentation
- **Prompt**: Generate a professional **README.md** in English with:
    - Local environment setup guide.
    - Docker instructions (dev vs full).
    - Data population scripts.
    - API Reference and Project Structure.

---
*End of Recap.*
