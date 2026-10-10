# Backend Setup

Requirements: Java 21, Maven, and MySQL.

Create `placement_management_db` in MySQL, then set `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, and `APP_JWT_SECRET` in the shell where Spring Boot will run. The exact Windows PowerShell commands are in the repository root `README_RUN.md`.

Run:

```powershell
mvn spring-boot:run
```

The API runs on port 8080. Do not put actual secrets in `application.properties` or commit them.
