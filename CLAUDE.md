# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Build
./mvnw clean package

# Run tests
./mvnw test

# Run single test class
./mvnw test -Dtest=CustomerArchiveServiceImplTest

# Start service
./mvnw spring-boot:run
```

The service runs on port **8085**.

## Architecture

Standard Spring Boot layered architecture. All source lives under the `sync` package:

| Layer | Package | Role |
|---|---|---|
| Controller | `sync.controller` | REST API — interface + `impl/` implementation |
| Service | `sync.service` | Business logic — interface + `impl/` implementation |
| Repository | `sync.repositories` | Spring Data JPA interface |
| Entities | `sync.entities` | JPA domain model |
| DTOs | `sync.dtos` | Request/response shapes |
| Mappers | `sync.mappers` | MapStruct DTO↔entity conversion |
| Config | `sync.config` | Security and JWT wiring |
| Enums | `sync.enums` | `Gender`, `AccountStatus`, `OperationType` |

**Domain model**: `CustomerArchive` (root aggregate) → `AccountArchive` (1-to-many) → `OperationArchive` (1-to-many). All PKs are UUIDs. Cascade is ALL with orphan removal.

**Single exposed endpoint**: `POST /api/v1/archives/customers` — archives a complete customer snapshot (customer + accounts + operations in one payload). Returns 201 on success.

## Security

The service is an **OAuth2 Resource Server** validating RSA-signed JWTs. Only the public key (`src/main/resources/certs/public.pem`) lives here; the private key is held by the issuing service.

- JWT scopes are mapped to Spring Security authorities with a `SCOPE_` prefix.
- `POST /api/v1/archives/customers` requires the `SCOPE_archive:write` authority.
- `/actuator/**` is public. All other paths are denied.
- Sessions are stateless; CSRF is disabled.

Key classes: `SecurityConfig`, `JwtDecoderConfig` (builds `NimbusJwtDecoder`), `RsaKeyProperties` (binds `rsa.public-key` from `application.yml`).

## Database

PostgreSQL on `localhost:5432/financial-archive-db`. DDL strategy is `none` — the schema must exist before the service starts; Hibernate will not create or alter tables.

## Key Dependencies

- **MapStruct 1.5.5** — DTO mapping; processors run via `maven-compiler-plugin`. Add new mappings in `sync/mappers/`.
- **Lombok** — annotation processing; must be declared before MapStruct in the compiler plugin config.
- **spring-boot-starter-oauth2-resource-server** — pulls in Nimbus JOSE for JWT decoding.
- **JaCoCo** — coverage reports generated during `verify` phase.