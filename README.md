# WoToS Vehicle Service

![Build](https://github.com/kevinthelago/wotos-vehicle-service/actions/workflows/maven.yml/badge.svg)
![Coverage](.github/badges/jacoco.svg)

Microservice in the [WoToS](https://github.com/users/kevinthelago/projects/2) system. Mirrors the World of Tanks Tankopedia and, for the Phase-3 Garage, serves per-vehicle **armor profiles** and **3D model (`.glb`) asset metadata** backed by MySQL and S3/MinIO object storage.

## Stack

- Spring Boot 3.2 · Java 17 · Spring Cloud 2023 (Eureka, Config, OpenFeign)
- MySQL 8 (`wotos_vehicles_database`) with Flyway migrations
- AWS SDK v2 against S3 (prod) / MinIO (local) for `.glb` blobs
- springdoc-openapi (Swagger UI)

## Prerequisites

- Java 17 (Temurin recommended) or the included `./mvnw` wrapper
- WoT application ID via env var `WG_APP_ID`
- MySQL 8 and S3/MinIO for the Garage features (armor/model)
- `wotos-eureka-server` (registry) and `wotos-config-server` at `localhost:4040` (config)

## Running Locally

### Command Line

```bash
export DB_URL=jdbc:mysql://localhost:3306/wotos_vehicles_database
export DB_USERNAME=root DB_PASSWORD=...
export AWS_S3_ENDPOINT=http://localhost:9000
export AWS_ACCESS_KEY_ID=minioadmin AWS_SECRET_ACCESS_KEY=minioadmin
export WG_APP_ID=<your-app-id>

./mvnw spring-boot:run
```

Flyway applies the schema on startup and the `wotos-models` bucket is auto-created.

### IntelliJ

1. Open the project root in IntelliJ IDEA.
2. Set the env vars above in the Run Configuration.
3. Run `WotosVehicleServiceApplication`.

### Docker

```bash
docker build -t wotos-vehicle-service .
docker run --rm -p 8080:8080 \
  -e DB_URL=jdbc:mysql://host.docker.internal:3306/wotos_vehicles_database \
  -e DB_USERNAME=root -e DB_PASSWORD=... \
  -e AWS_S3_ENDPOINT=http://host.docker.internal:9000 \
  -e AWS_ACCESS_KEY_ID=minioadmin -e AWS_SECRET_ACCESS_KEY=minioadmin \
  -e WG_APP_ID=... \
  wotos-vehicle-service
```

CI publishes the image to `ghcr.io/kevinthelago/wotos-vehicle-service:dev`.

## Building

```bash
./mvnw clean package        # build JAR, skip tests
./mvnw verify               # build JAR + run all tests + JaCoCo coverage
```

## Testing

```bash
./mvnw verify
```

Integration tests use Testcontainers (MySQL + MinIO) and run in CI; they are skipped automatically where Docker is unavailable. Coverage is reported via JaCoCo (`target/site/jacoco/`) and published as the badge above.

## Tier-1 Ingestion

Populate the 20 Tier-1 tanks (armor + `.glb`) from `src/main/resources/tier1_tanks.json`:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=ingest -Dspring-boot.run.arguments=--tier1
# add --force to re-ingest tanks already present
```

Idempotent (skips already-ingested tanks) and resumable (a per-tank failure does not abort the run).

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/vehicles` | Fetch vehicles from WoT Tankopedia with optional filters |
| `GET` | `/api/vehicles/{id}/armor` | Latest `ArmorProfile` for a vehicle, or `404` |
| `GET` | `/api/vehicles/{id}/model` | `{url, etag, sizeBytes, format}` with a 60s signed URL, or `404` |
| `GET` | `/api/modules` | Fetch vehicle modules from WoT Tankopedia |

All errors use a standard envelope: `{timestamp, status, error, message, path}`.

### Vehicle query parameters

| Parameter | Type | Description |
|-----------|------|-------------|
| `vehicleIds` | `Integer[]` | Filter by specific vehicle IDs |
| `vehicleTiers` | `Integer[]` | Filter by tier (1–10) |
| `types` | `String[]` | Filter by type (e.g. `heavyTank`, `mediumTank`) |
| `nations` | `String[]` | Filter by nation |
| `language` | `String` | Response language (default: `en`) |
| `fields` | `String[]` | Limit returned fields |
| `limit` | `Integer` | Max results |
| `page` | `Integer` | Page number |

## Swagger UI

Once running, API docs are available at:

```
http://localhost:8080/swagger-ui/index.html
```
