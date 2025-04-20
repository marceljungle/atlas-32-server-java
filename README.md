# atlas-32-server

A Spring Boot 3 application that:

- Exposes a web dashboard (Thymeleaf + Bootstrap)
- Publishes / consumes MQTT messages to communicate with ESP32 devices
- Stores users, devices, and encrypted Google Maps API keys in PostgreSQL

---

## Table of contents

- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Local development](#local-development)
- [Running the full stack with Docker Compose](#running-the-full-stack-with-docker-compose)
- [Building a multi-arch container image](#building-a-multi-arch-container-image)
- [Tests](#tests)
- [Environment variables](#environment-variables)
- [Project layout](#project-layout)

---

## Architecture

```
┌──────────────┐      MQTT pub/sub       ┌───────────────┐
│ Spring Boot  │  ─────────────────────▶ │   Mosquitto   │
│  dashboard   │ ◀────────────────────── │    broker     │
│              │                         └───────────────┘
│              │
│              │             JDBC
│              │ ───────────────────────▶ PostgreSQL
└──────────────┘
```

- **atlas-32-boot** – Spring Boot application.
- **atlas-32-domain** – Pure domain model (entities, value objects).
- **atlas-32-usecases** – Application/service layer.
- **atlas-32-infrastructure** – Adapters: MQTT, JPA repositories, Thymeleaf views.

---

## Prerequisites

| Tool                        | Version                |
|-----------------------------|------------------------|
| **Java JDK**                | 17 or 21 (we use 21)   |
| **Maven**                   | 3.9+ (wrapper included)|
| **Docker / Docker Desktop** | v24+ (for containers & Buildx) |


## Local development

```bash
# Build everything & run Spring Boot locally
./mvnw clean spring-boot:run

# App listens on http://localhost:4046 (change in application.yml)
```

---

## Running the full stack with Docker Compose

```bash
docker compose up        # or "docker compose up -d"
```

`docker-compose.yml` starts three services:

| Service       | Purpose         | Ports                           |
|---------------|-----------------|---------------------------------|
| atlas-server  | Spring Boot     | 4046 (external) → 8080 (inside) |
| postgres      | Database        | 5432                            |
| mosquitto     | MQTT broker     | 1883                            |

The server waits for PostgreSQL to become healthy before booting.

---

## Building a multi-arch container image

```bash
# (only once) Create & use a buildx builder
docker buildx create --name multiarch --use

# Build for both amd64 and arm64 and push
docker buildx build \
  --platform linux/amd64,linux/arm64 \
  -t yourname/atlas-32-server:1.0.0 \
  --push .
```

Alternatively, let Spring Boot + Buildpacks do the work:

```bash
# Build & push single-arch image (host arch)
cd atlas-32-boot
mvn spring-boot:build-image \
    -DskipTests \
    -Dspring-boot.build-image.imageName=yourname/atlas-32-server:1.0.0
```

(Buildpacks currently produce multi-arch when run through Buildx too.)

---

## Environment variables

| Variable                   | Default                                   | Description |
|----------------------------|-------------------------------------------|-------------|
| `SPRING_DATASOURCE_URL`    | `jdbc:postgresql://localhost:5432/atlas32` | JDBC URL    |
| `SPRING_DATASOURCE_USERNAME` | `admin`                                 | DB user     |
| `SPRING_DATASOURCE_PASSWORD` | `password`                              | DB password |
| `MQTT_BROKER_URL`          | `tcp://localhost:1883`                    | Broker URL  |

All variables are configured in `docker-compose.yml`, so you rarely need to set them manually.
