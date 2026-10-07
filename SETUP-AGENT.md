# SETUP-AGENT.md

## Objective
Implement a simple showcase application named Parking Manager.

### Technology Stack
- Java 21
- Spring Boot 4.1.1
- Spring MVC
- Thymeleaf
- Gradle (Groovy DSL)
- OpenAPI Generator
- OAuth2 Client Credentials
- No database
- Clean Architecture
- Structured logging

### Functional Requirements
- Consume Parking REST API using provided OpenAPI specification.
- Authenticate using OAuth2 Client Credentials.
- Expose application under context path `/parking-manager`.
- Server-side rendered Thymeleaf UI.

### Main Page
Provide:
1. Parking facility selection.
2. Parking facility overview.
3. Occupancy summary:
   - Maximum places
   - Occupied places
   - Free places
4. Parking facility name.
5. Business partner name.
6. Opening hours.

### Architecture
Use Clean Architecture:
- domain
- application
- adapter.in.web
- adapter.out.parkingapi
- configuration

Generated OpenAPI DTOs must remain in outbound adapter.

### Project Structure
src/main/java/com/example/parkingmanager
 ├── domain
 ├── application
 ├── adapter
 │   ├── in/web
 │   └── out/parkingapi
 └── configuration

### OAuth Configuration
Externalize:
- PARKING_API_BASE_URL
- PARKING_API_TOKEN_URI
- PARKING_API_CLIENT_ID
- PARKING_API_CLIENT_SECRET
- PARKING_API_SCOPES

### application.yml
Configure:
- Spring application name
- OAuth2 Client Credentials
- Parking API properties
- Server context path `/parking-manager`
- Actuator health/info
- Structured logging

### UI Requirements
Use Thymeleaf.
Facility selection must use GET request.
Display occupancy cards and opening hours table.

### Logging
Use SLF4J.
Never log secrets or access tokens.

### Testing
Implement:
- Domain tests
- Mapper tests
- Service tests
- MockMvc tests
- API client tests

### Verification
Run:
./gradlew clean build
./gradlew test

Generate complete implementation and README.
