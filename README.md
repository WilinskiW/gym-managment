# 🏋️ Gym Management System

A Spring Boot application for managing gym facilities, membership plans, and member registrations. This project demonstrates high-quality software engineering practices, inspired by domain-driven logic, comprehensive data validation, and testing.

## Technologies
*   **Java 21**
*   **Spring Boot 3.x** (Web, Data JPA, Validation)
*   **H2 Database** (In-memory storage for development)
*   **Flyway** (Database schema migrations and versioning)
*   **Lombok** (Boilerplate code reduction)
*   **JUnit 5 & AssertJ** (Integration testing with `RestTestClient`)

## Key Features
*   **Gym Management:** Create and manage gym locations with strict validation (e.g., unique names, regional phone formats).
*   **Membership Plans:** Define plans with specific durations, pricing, and member capacity limits.
*   **Advanced Registration Logic:** 
    *   **Email Uniqueness:** Prevents duplicate active memberships within the same gym, while allowing a single user (email) to hold plans in different gyms.
    *   **Capacity Control:** Real-time validation of available spots before member registration.
    *   **Lifecycle Management:** Support for manual cancellation and subsequent re-registration (creating a new membership history record).
*   **Revenue Reporting:** Automated financial reports grouped by gym and currency, including only active memberships.

## Database & Sample Data
The project uses **Flyway** for database lifecycle management.
*   The schema is automatically generated upon startup.
*   **Sample Data:** Flyway migrations automatically seed the database with example gyms, plans, and members, allowing for immediate API testing.

## Exploration Tools
Once the application is running, the following tools are available:

*   **Swagger UI:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) – Explore and test all REST endpoints via a visual interface.
*   **H2 Console:** [http://localhost:8080/db-console](http://localhost:8080/db-console) – Direct access to the in-memory database.
    *   **JDBC URL:** `jdbc:h2:mem:gym-management` (check `application.properties` if different)
    *   **User:** `sa`
    *   **Password:** *(leave blank)*

## Testing
The project includes a comprehensive suite of unit and integration tests to ensure reliability and business logic correctness.

### Unit Tests
*   **Focus**: Testing core business logic in the Service layer and Domain entities.
*   **Isolation**: Unit tests use a self-implemented, in-memory mock/fake database (or simple repositories) to speed up execution and isolate tests from specific infrastructure or persistence layers.

### Integration Tests
*   **Focus**: Testing the entire HTTP-to-Database flow, including REST controllers and Global Exception Handling.
*   **Environment**: Integration tests run on a dedicated `integration` Spring profile.
*   **Consistency**: Uses **Flyway** with the `flyway.clean()` and `flyway.migrate()` strategy before each test method to ensure a clean, consistent database state and absolute test isolation.

To run the tests, use:
```
./mvnw test
```

## Project Structure
Main source packages are inspired by Domain-Driven Design principles:
- `/domain` : Core business logic, entities, repositories, and service layer.
- `/infrastructure` : REST controllers, global exception handling, and infrastructure configuration.

## How to Run?
1. Clone the repository.
2. Ensure JDK 21 is installed.
3. Run the application:
```
./mvnw spring-boot:run
```
