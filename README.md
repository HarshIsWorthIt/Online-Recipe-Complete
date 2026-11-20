# Recipely - Java Web Recipe Sharing Platform

Recipely is a role-based recipe-sharing web application built for a Java Web project. It demonstrates Servlets, JDBC, OOP, collections and generics, exception handling, and synchronized background processing.

## Requirements covered

| Rubric area | Implementation |
| --- | --- |
| Core Java and OOP | `BaseEntity` inheritance, `User` polymorphism, `Moderatable` interface, enums, validation, and custom exceptions |
| Collections and generics | Generic `Repository<T>` interface plus `List`, `Map`, and typed service models |
| Multithreading and synchronization | `ScheduledExecutorService` refreshes a synchronized `MetricsCache` |
| Database operation classes | `DatabaseManager`, `DatabaseInitializer`, `UserRepository`, and `RecipeRepository` |
| JDBC | SQLite JDBC driver, prepared statements, schema initialization, and transactions |
| Servlets and web integration | MVC-style Servlet controllers, JSP views, session authentication, and an authorization filter |

## Technology

- Java 21
- Jakarta Servlet 6
- JSP
- SQLite through JDBC
- Maven WAR packaging
- Jetty Maven plugin for local development

## Run locally

1. Install Java 21 and Maven 3.9 or newer.
2. Open a terminal in this folder.
3. Run `mvn clean package` to create `target/recipely.war`.
4. Run `mvn jetty:run` to start the local server.
5. Open `http://localhost:8080`.

On Windows, you can alternatively double-click `run-local.bat` after installing Java and Maven.

The application automatically creates `data/recipely.db` on first run and seeds sample users and recipes.

## Demo accounts

| Role | Email | Password |
| --- | --- | --- |
| Administrator | `admin@recipely.local` | `Admin@123` |
| Contributor | `maya@recipely.local` | `Chef@123` |
| Explorer | `jordan@recipely.local` | `Explore@123` |

## Project structure

```text
src/main/java/com/recipely/
  model/       OOP entities, inheritance, interfaces and enums
  repository/  JDBC data-access classes and generic repository contract
  service/     Validation, business rules and synchronized metrics cache
  servlet/     Web controllers, session handling and filters
src/main/webapp/
  WEB-INF/views/ JSP views
  assets/        CSS
```

## Notes

This project stores only a SHA-256 password digest for demonstration purposes. A production system should use a password-hashing algorithm such as BCrypt or Argon2 and a managed database service.
