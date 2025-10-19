# DOGS API
- A RESTful Spring Boot API for managing police dog records.
- This service provides endpoints to register, update, soft-delete, and list all police dogs with filtering and pagination support.

### Overview
This project implements a Dogs Registry API for managing police dogs.
It supports full CRUD operations following RESTful standards, using JSON for input/output, and soft deletion for audit purposes.

### Key Features
1. RESTful API (Create, Read, Update, Delete)
2. Filter dogs by name, breed, or supplier
3. Pagination support
4. Soft delete (records are marked deleted, not removed)
5. Database migration handled via Flyway
6. Entity–DTO mapping using MapStruct
7. Integrated Swagger UI for API documentation

### Tech Stack
| Layer         | Technology                               |
|---------------|------------------------------------------|
| Framework     | Spring Boot 3.5.6                        |
| Java Version	 | 17                                       |
| Database      | H2 in-memory)                            |
| ORM           | Spring Data JPA                          |
| Migrations    | Flyway                                   |
| Mapping       | MapStruct                                |
| Validation    | Jakarta Validation (Hibernate Validator) |
| API Docs      | SpringDoc OpenAPI (Swagger UI)           |
| Build Tool    | Maven                                    |                            

## Getting Started
### Prerequisites
Before running this project, ensure you have:
- Java 17+
- Maven 3.8+

To verify:
```
java -version
mvn -version
```

### Build & Run

Clone the repository and navigate into it:
```
git clone https://github.com/rajinda1980/dogsapi.git
cd dogsapi
```

Build the project:
```
mvn clean install
```

Run the application:
```
mvn spring-boot:run
```

Once started, the API will be available at:
```
http://localhost:8080
```

### Testing

Run all unit and integration test cases:
```
mvn test
```

Or specifically run integration tests:
```
mvn verify
```

All test cases are under:
```
src/test/java/com/polaris/police/dogsapi
```

### Swagger / OpenAPI Docs

The project includes Swagger UI via SpringDoc. <br />
Once running, access the documentation at:
```
http://localhost:8080/swagger-ui/index.html
```
or the raw OpenAPI spec at:
```
http://localhost:8080/v3/api-docs
```

### Accessing H2 Console
To inspect the in-memory database:
1. Open [h2 console](http://localhost:8080/h2-console)
2. Use the following settings:
```
JDBC URL: jdbc:h2:mem:dogsdb
User Name: sa
Password: (leave blank)
```

### API Endpoints
| Method  | Endpoint        | Description                            |
|---------|-----------------|----------------------------------------|
| POST    | /api/dogs       | Create a new dog                       |
| GET     | /api/dogs       | Get list of dogs (filter + pagination) |
| GET     | /api/dogs/{id}  | Get a dog by id                        |
| PUT     | /api/dogs/{id}  | Update dog record                      |
| DELETE  | /api/dogs/{id}  | Soft delete dog record                 |


### Default pagination
```
pageNum = 0
pageSize = 10
```

### Database & Flyway
The app uses Flyway for database versioning and migrations. <br />
Migration files are located in:
```
src/main/resources/db
```
## Notes and Assumptions
1. All DELETE operations are soft deletes (the record remains in DB but is flagged as deleted).
2. The /api/dogs list endpoint does not return deleted entries.
3. The system is initially configured with two suppliers ("Breeder", "Kennels"). Additional suppliers can be added manually via the master database script. There is no API endpoint to manage supplier records.
4. Input payloads are validated for basic rules (e.g., required fields, max length). More specific validations (e.g., badge ID format, date constraints) can be added in the future.
