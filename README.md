# WoToS Vehicle Service

![Build](https://github.com/kevinthelago/wotos-vehicle-service/actions/workflows/maven.yml/badge.svg)

Microservice in the [WoToS](https://github.com/users/kevinthelago/projects/2) system. Fetches vehicle and module data from the World of Tanks Tankopedia API and exposes it to the edge service. Has no persistent database — all data is retrieved live from the WoT API on request.

## Prerequisites

- Java 8 (Temurin recommended)
- Maven or the included `./mvnw` wrapper
- WoT application ID set as environment variable: `wg-app-id`
- `wotos-eureka-server` running (service registry)
- `wotos-config-server` running at `localhost:4040` (serves application config)

## Running Locally

### Command Line

```bash
./mvnw spring-boot:run
```

### IntelliJ

1. Open the project root in IntelliJ IDEA.
2. Set the environment variable `wg-app-id=<your-app-id>` in the Run Configuration.
3. Run `WotosVehicleServiceApplication`.

## Building

```bash
./mvnw clean package        # build JAR, skip tests
./mvnw clean install        # build JAR + run all tests
```

## Testing

```bash
./mvnw test                          # run all tests
./mvnw test -Dtest=VehicleServiceTest  # run a single test class
```

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/vehicles` | Fetch vehicles from WoT Tankopedia with optional filters |
| `GET` | `/api/modules` | Fetch vehicle modules from WoT Tankopedia |

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

Once the service is running, API documentation is available at:

```
http://localhost:8080/swagger-ui/index.html
```
