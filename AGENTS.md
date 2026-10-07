# AGENTS.md

## Purpose
Parking Manager is a lightweight Spring Boot showcase application consuming an OAuth-protected Parking API.

## Required Technologies
- Java 21
- Spring Boot 4.1.1
- Spring MVC
- Thymeleaf
- Gradle Groovy DSL
- Spring Security OAuth2 Client
- OpenAPI Generator

## Architecture Rules
Dependency direction:

Web -> Application -> Domain
REST Adapter -> Application Port -> Domain

### Domain
Must not depend on:
- Spring
- HTTP
- Thymeleaf
- Generated OpenAPI classes

### Application
Contains:
- Use cases
- Service layer
- Ports

### Adapters
Inbound:
- MVC Controllers

Outbound:
- Parking API integration
- OAuth integration
- DTO mapping

## Coding Standards
- Constructor injection only
- No Lombok
- Use immutable records where appropriate
- Avoid static mutable state
- Prefer composition over inheritance
- No field injection

## OpenAPI Rules
- OpenAPI specification is source of truth
- Do not modify generated files
- Keep generated code under build directory
- Map generated DTOs to domain objects

## Security
- Use OAuth2 Client Credentials
- Never log secrets
- Never log access tokens
- Never disable TLS validation

## Logging
Use structured logging.

Include:
- facility id
- operation name
- status

## Thymeleaf Rules
- Server-side rendering only
- No SPA framework
- Escape all external data
- Avoid th:utext

## Testing
Every change requires tests.

Required:
- Unit tests
- MVC tests
- API integration tests with stubs

## Definition of Done
- Build passes
- Tests pass
- No secrets committed
- Documentation updated
- Architecture rules preserved
