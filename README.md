# Parking Manager

A server-rendered Spring Boot application that displays facility occupancy and opening hours from the OAuth2-protected Parking Capacity API.

## Requirements

- Java 21
- Gradle (or the included Gradle wrapper)
- OAuth2 client-credentials access to the Parking API

## Configuration

Set the following environment variables before starting the application:

| Variable | Purpose |
| --- | --- |
| `PARKING_API_BASE_URL` | Capacity API base URL; defaults to the preproduction API URL in the OpenAPI specification |
| `PARKING_API_TOKEN_URI` | OAuth2 token endpoint |
| `PARKING_API_CLIENT_ID` | OAuth2 client ID |
| `PARKING_API_CLIENT_SECRET` | OAuth2 client secret |
| `PARKING_API_SCOPES` | Optional comma-separated OAuth2 scopes |
| `PARKING_API_TENANT` | Parking API tenant name |

The UI is available at `http://localhost:9090/parking-manager/`; health and info actuator endpoints are available below `/parking-manager/actuator`.

Parking API HTTP requests and responses are logged at `INFO`, including response bodies. Authorization and cookie headers are excluded from HTTP logs so access tokens are not logged.

## Build and test

```powershell
.\gradlew.bat clean build
.\gradlew.bat test
```

The OpenAPI Generator task reads `apispec/openapi.yaml` and writes generated model classes to `build/generated/openapi`. The supplied specification omits the required `info.version` field, so generator validation is skipped without changing the API contract. Do not edit generated files; make API changes in the specification and regenerate during the build.

## Architecture

- `domain`: immutable facility, occupancy, and opening-hours records
- `application`: overview use case and outbound port
- `adapter.in.web`: MVC controller and server-rendered Thymeleaf views
- `adapter.out.parkingapi`: OAuth-authenticated API client and mapping from generated API models to domain records
- `configuration`: API properties and OAuth client setup
