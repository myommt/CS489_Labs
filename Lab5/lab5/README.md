ADS (Advantis Dental Surgeries) - Minimal Spring Boot skeleton

This repository was updated to include a minimal Spring Boot web application
for managing dental surgeries (Surgery entity) with a REST API.

How to run (PowerShell):

1. Build and run tests:

```powershell
cd "c:\Users\zaimy\OneDrive\Documents\Git\MIUCompro\CS489\Labs\Lab5\lab5";
mvn -B test
```

2. Run the application:

```powershell
mvn spring-boot:run
```

3. Example API endpoints (when app is running on http://localhost:8080):

- GET  /api/surgeries         -> list all surgeries
- GET  /api/surgeries/{id}    -> get a surgery
- POST /api/surgeries         -> create (JSON body)
- PUT  /api/surgeries/{id}    -> update
- DELETE /api/surgeries/{id}  -> delete

Notes:
- Uses in-memory H2 database (development/demo).
- This is a starting skeleton; next steps: authentication, UI, more entities (dentists, appointments, patients), validation, DTOs, and error handling.

Using MySQL
------------
- A sample MySQL profile file is available at `src/main/resources/application-mysql.properties`.
- To run the app using MySQL (after you create the database and update credentials):

```powershell
# create schema in your MySQL server first, e.g. in MySQL Shell or client:
# CREATE DATABASE adsdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

cd "c:\Users\zaimy\OneDrive\Documents\Git\MIUCompro\CS489\Labs\Lab5\lab5";
mvn spring-boot:run -Dspring-boot.run.profiles=mysql
```

Security note: Do not commit production passwords. Use environment variables or a secret store in real deployments.

Package structure and layering
-----------------------------
I organized the code into packages to reflect a layered architecture. Keep adding new features into these packages:

- `controller` — REST controllers (HTTP endpoints)
- `service` — Business logic and transactional operations (appointment booking rules live here)
- `repository` — Spring Data JPA repositories (DB access)
- `model` — JPA entities
- `config` — (optional) DataSource, security, or other framework configuration

This separation makes it straightforward to:
- Add authorization (Spring Security) by adding a `security` or `config` package and wiring filters
- Replace H2 with MySQL by switching profiles (no code change required)
- Add DTOs and mappers between controller <-> service using MapStruct or manual mapping

