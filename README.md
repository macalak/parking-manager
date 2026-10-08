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
| `PARKING_CONTRACT_API_BASE_URL` | Customers Contracts Consumers API base URL; defaults to the URL in `apispec/cc-openapi.yaml` |
| `PARKING_CONTRACT_API_TENANT` | Customers Contracts Consumers API tenant; defaults to `PARKING_API_TENANT` |

The facility overview is available at `http://localhost:9090/parking-manager/`; customer details can be looked up by business ID at `/parking-manager/customers?businessId=...`. Health and info actuator endpoints are available below `/parking-manager/actuator`.

The Capacity API adapter logs raw HTTP requests and responses at `INFO`, excluding authorization and cookie headers. The Customers Contracts Consumers client logs structured operation status without logging request bodies or access tokens.

## Build and test

```powershell
.\gradlew.bat clean build
.\gradlew.bat test
```

The OpenAPI Generator tasks read `apispec/openapi.yaml` and `apispec/cc-openapi.yaml`, writing generated sources to `build/generated/openapi` and `build/generated/contract-openapi`. The supplied capacity specification omits the required `info.version` field, so generator validation is skipped without changing the API contract. Do not edit generated files; make API changes in the specifications and regenerate during the build.

## Architecture

- `domain`: immutable facility, occupancy, and opening-hours records
- `application`: overview use case and outbound port
- `adapter.in.web`: MVC controller and server-rendered Thymeleaf views
- `adapter.out.parkingapi`: OAuth-authenticated API client and mapping from generated API models to domain records
- `adapter.out.parkingcontract`: OAuth-authenticated client exposing all generated Customers Contracts Consumers API groups
- `configuration`: API properties and OAuth client setup
