# Info
When asking an IntelliJ AI Agent to initialize a project, use a single detailed SETUP-AGENT.md 
for project creation and an AGENTS.md for long-term development governance.

A good rule is:

SETUP-AGENT.md = one-time scaffold/bootstrap instructions
AGENTS.md = permanent architecture and coding rules

This separation works very well for architect-driven projects because it keeps the bootstrap concerns
 separate from DDD and architectural governance.

# AGENTS.md (Global project instructions)
This is the primary file that tells the coding agent how the project should be built, structured, and maintained.

## Architecture Vision
 * DDD
 * Clean Architecture
 * Event Driven Architecture
 * CQRS where justified
 
## Technology Choices
  * Java 21
  * Spring Boot
  * Kafka
  * PostgreSQL
  * Testcontainers
  * OpenTelemetry

##   Development Rules
  * Hexagonal architecture
  * Domain events
  * No anemic domain model
  * Explicit bounded contexts
  
## Non-functional Requirements
 * Metrics
 * Tracing
 * Structured logging
 * Security by default

# SETUP-AGENT.md (Project bootstrap instructions)

This file provides concrete instructions for creating the initial project skeleton.

## What to create
## What dependencies to add
## What Docker infrastructure to prepare
## What sample code to generate
# What must be verified