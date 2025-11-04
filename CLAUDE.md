# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

simple-commerce is a Spring Boot 3.5.7 e-commerce application using Java 21, Spring Data JPA, and H2 database. The original package name 'project.simple-commerce' was invalid, so the codebase uses 'project.simple_commerce' instead.

## Build and Development Commands

### Build
```bash
./gradlew build
```

### Run Application
```bash
./gradlew bootRun
```

### Run Tests
```bash
./gradlew test
```

### Run Single Test
```bash
./gradlew test --tests "ClassName.methodName"
```

### Clean Build
```bash
./gradlew clean build
```

## Architecture

### Package Structure

The application follows a domain-driven vertical slice architecture where each domain module is self-contained:

```
project.simple_commerce/
├── item/
│   ├── entity/       # JPA entities
│   ├── repository/   # Spring Data JPA repositories
│   ├── service/      # Business logic
│   └── controllers/  # REST controllers
└── member/
    ├── entity/
    └── repository/
```

Each domain module (item, member) contains its own entity, repository, service, and controller layers.

### Technology Stack

- **Framework**: Spring Boot 3.5.7
- **Java Version**: Java 21
- **Database**: H2 (TCP mode at localhost:~/commerce)
- **ORM**: Spring Data JPA with Hibernate
- **Utilities**: Lombok for reducing boilerplate code
- **Validation**: Spring Boot Validation starter

### Database Configuration

The H2 database runs in TCP server mode (not embedded) at `jdbc:h2:tcp://localhost/~/commerce`. The database must be running separately before starting the application.

JPA settings:
- `hibernate.ddl-auto: create` - recreates schema on startup
- `open-in-view: false` - OSIV pattern disabled
- SQL logging enabled with formatting

### Coding Patterns

- **Controllers**: Use `@RestController` with `@RequestMapping` for base paths
- **Services**: Annotated with `@Service` and use constructor injection via `@RequiredArgsConstructor` (Lombok)
- **Repositories**: Extend `JpaRepository<Entity, Long>` - no implementation needed
- **Entities**: Use `@Entity` with `@Id @GeneratedValue` for primary keys

### Adding New Domain Modules

When adding a new domain (e.g., order, cart):
1. Create package under `project.simple_commerce.{domain}`
2. Add `entity/` subpackage with JPA entity classes
3. Add `repository/` subpackage with JpaRepository interfaces
4. Add `service/` subpackage with business logic
5. Add `controllers/` subpackage with REST endpoints

Follow the existing item/member module structure as reference.
