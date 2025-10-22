This file contains short, actionable guidance for AI coding agents working in the dentalsugeryapi Spring Boot project.

Keep it concise: read the referenced files before making changes.

Project snapshot
- Java 25 / Spring Boot 3.5.6 Maven project (see `pom.xml`).
- Main app: `src/main/java/cs489/dentalsugeryapi/dentalsugeryapi/DentalsugeryapiApplication.java`.

Big-picture architecture
- Thin REST controllers under `controller/` delegate business logic to service interfaces in `service/` and persistence to Spring Data repositories in `repository/`.
- Domain models live in `model/` and the project uses DTO records in `dto/` for request/response mapping (see `PatientRequestDTO`, `PatientResponseDTO`, `AppointmentRequestDTO`, `AppointmentResponseDTO`).
- Services follow the interface/impl pattern (e.g., `PatientService` / `PatientServiceImpl`). Controllers perform explicit mapping between DTOs and entities inside controller methods (no mapper framework used).

Key conventions and patterns (follow these exactly)
- URL base: controllers use the prefix `/dentalsugery/api/*` (see `PatientController`, `AppointmentController`). Preserve this pattern for new controllers/endpoints.
- DTOs are Java records (immutable-ish). Controllers map DTO -> entity manually and return DTOs. Keep controllers responsible for mapping unless adding a central mapper.
- `findOrCreateX` pattern exists for Address/Patient to avoid duplicates (see `PatientServiceImpl.findOrCreatePatient` and `AddressService.findOrCreateAddress`). Use these helpers when creating related entities in services or controllers.
- Error handling: controllers catch domain exceptions (e.g., `PatientNotFoundException`, `OutstandingBillException`, `AppointmentLimitExceededException`) and translate them to appropriate HTTP statuses and `ErrorResponseDTO`. Reuse this pattern.
- Database: configured via `application.properties` to use MySQL and Hibernate `ddl-auto=update`. Tests expect a Spring context load (`DentalsugeryapiApplicationTests`).

Build / test / run
- Build: use the included wrapper: `./mvnw clean package` (Windows Powershell: `.\mvnw.cmd clean package`).
- Run: `./mvnw spring-boot:run` (Windows: `.\mvnw.cmd spring-boot:run`).
- Tests: `./mvnw test` (Windows: `.\mvnw.cmd test`).
- Database: default configuration points to `jdbc:mysql://localhost:3306/apsd489` with credentials in `application.properties`. If MySQL is not available, run with a test profile or mock repositories.

Files to read first (fast path)
- `pom.xml` — dependencies & Java/Spring versions.
- `src/main/resources/application.properties` — datasource + JPA behavior.
- `PatientController`, `AppointmentController` — show request mapping, DTO usage, and exception handling style.
- `service/*Impl` — shows transactional/create-or-find logic (especially `PatientServiceImpl`).

Small implementation rules for PRs
- Keep DTO/entity mapping code near controllers unless the change touches many controllers — only then introduce a mapper class and wire it via constructor injection.
- Preserve use of `findOrCreate...` service methods when creating nested resources to avoid duplicate entities.
- When adding endpoints, follow existing URL and response shapes (use `DeleteResponseDTO`, `ErrorResponseDTO`, etc.).
- Add unit tests for service-layer logic and a SpringBootTest only if you need the full context. Keep tests small and focused.

Examples from codebase
- Create appointment flow: `AppointmentController.createAppointment` builds Patient, Dentist, SurgeryLocation and uses `addressService.findOrCreateAddress(...)` and `patientService.addNewPatient(...)` to persist; exceptions map to 409/400 responses.
- Patient update: `PatientServiceImpl.updatePatient` preserves existing address and uses `addressService.findOrCreateAddress` to update address relationships.

If you are unsure
- Read the controller and its paired service impl before changing behavior.
- Run `.\mvnw.cmd -DskipTests=true package` to validate compilation quickly.
- Ask: "Should mapping move to a mapper class or stay in controller?" before large refactors.

Feedback
- If this guidance is missing anything or a convention changes, update this file and mention the reason in the change commit message.
