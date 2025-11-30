# E-commerce Spring Boot Backend

A simple RESTful API for an e-commerce application built with Spring Boot.

## Technologies Used

*   **Java 21**: The core programming language.
*   **Spring Boot**: The framework for building the application.
*   **Spring Data JPA**: For data persistence and database interaction.
*   **Spring Security**: For authentication and authorization.
*   **PostgreSQL**: The relational database.
*   **Maven**: For dependency management and building the project.
*   **Lombok**: To reduce boilerplate code.

## Features

_(This section can be filled in as features are added.)_

*   ...
*   ...
*   ...

## Prerequisites

Before you begin, ensure you have met the following requirements:

*   You have installed a recent version of the **Java Development Kit (JDK)** (version 21 or higher).
*   You have a **PostgreSQL** database running.
*   You have **Maven** installed.

## Installation and Setup

1.  **Clone the repository:**

    ```bash
    git clone https://github.com/your-username/ecommerce-springboot-backend.git
    cd ecommerce-springboot-backend
    ```

2.  **Configure the database:**

    Open the `src/main/resources/application.properties` file and update the following properties to match your PostgreSQL database configuration:

    ```properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/your-database-name
    spring.datasource.username=your-username
    spring.datasource.password=your-password
    spring.jpa.hibernate.ddl-auto=update
    ```

3.  **Install dependencies:**

    ```bash
    mvn install
    ```

## How to Run

To run the application, use the following command:

```bash
mvn spring-boot:run
```

The application will start on port 8080 by default.

## API Endpoints

_(This section can be filled in as endpoints are created.)_

*   **...**
    *   `GET /api/...`: ...
    *   `POST /api/...`: ...
*   **...**
    *   `GET /api/...`: ...
    *   `PUT /api/...`: ...
    *   `DELETE /api/...`: ...

## Configuration

All application configuration is located in the `src/main/resources/application.properties` file. You can modify database settings, server port, and other Spring Boot properties in this file.

## Project Structure

The project follows a standard Spring Boot project structure:

```
.
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── ecommerce
│   │   │           └── backend
│   │   │               ├── config
│   │   │               ├── controller
│   │   │               ├── dto
│   │   │               ├── exception
│   │   │               ├── mapper
│   │   │               ├── model
│   │   │               ├── repository
│   │   │               ├── security
│   │   │               └── service
│   │   └── resources
│   │       ├── static
│   │       ├── templates
│   │       └── application.properties
│   └── test
└── pom.xml
```