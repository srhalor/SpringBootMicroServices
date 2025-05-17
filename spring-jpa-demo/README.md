# Spring JPA Demo

A sample Spring Boot application demonstrating CRUD operations using Spring Data JPA, Lombok, and RESTful APIs for managing students and their addresses.

## Features
- Manage students and their addresses
- RESTful endpoints for CRUD operations
- Pagination and sorting support
- Uses Lombok for boilerplate code reduction
- JPA/Hibernate for ORM
- H2 in-memory database for development

## Project Structure
- `controller/` – REST controllers (API endpoints)
- `service/` – Business logic and service layer
- `repository/` – Spring Data JPA repositories
- `entity/` – JPA entity classes (Student, Address, Base)
- `dto/` – Data Transfer Objects for API communication
- `builder/` – Utility classes for converting between entities and DTOs

## Endpoints
- `POST /student` – Create a new student
- `GET /student` – List students (with pagination and sorting)
- `GET /student/{id}` – Get student by ID
- `PUT /student/{id}` – Update student by ID
- `DELETE /student/{id}` – Delete student by ID

## Getting Started
1. **Clone the repository**
2. **Configure the database** in `src/main/resources/application.yaml` if needed (defaults to H2 in-memory)
3. **Build and run the application**:
   ```sh
   ./mvnw spring-boot:run
   ```
4. **Access the API** at `http://localhost:8080`

## Technologies Used
- Java 21
- Spring Boot 3.4.5
- Spring Data JPA
- Lombok
- Hibernate
- H2 Database (for development)

## Author
- Shailesh Halor ([GitHub](https://github.com/srhalr))

## License
This project is for demonstration purposes.
