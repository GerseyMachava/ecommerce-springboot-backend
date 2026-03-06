# 🛒 E-commerce Spring Boot Backend

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![MySQL](https://img.shields.io/badge/mysql-%2300f.svg?style=for-the-badge&logo=mysql&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-black?style=for-the-badge&logo=JSON%20web%20tokens)
![Swagger](https://img.shields.io/badge/-Swagger-%23C1272D?style=for-the-badge&logo=swagger&logoColor=white)

A robust, scalable, and secure RESTful API for an E-commerce platform built with **Java 21** and **Spring Boot 3**. This project demonstrates advanced backend patterns, including event-driven architecture, role-based access control (RBAC), and automated documentation.

---

## 🚀 Key Features

*   **🔒 Security & Auth:** Complete Authentication and Authorization system using **JWT (JSON Web Tokens)** and Role-Based Access Control (Admin, User, Customer).
*   **📦 Product Management:** Advanced catalog management with pagination, sorting, dynamic search, and category filtering.
*   **🛒 Shopping Cart:** Real-time cart management with stock validation.
*   **🧾 Order & Checkout:** Order processing with automated status updates.
*   **⚡ Event-Driven Logic:** Decoupled business logic using **Domain Events** (e.g., `PaymentCompletedEvent`) and Listeners.
*   **🖼️ Image Handling:** Integrated system for product image uploads and management.
*   **🛡️ Robust Error Handling:** Centralized exception handling with clear, standardized API responses.
*   **🗺️ Object Mapping:** High-performance DTO mapping using **MapStruct**.

---

## 🛠️ Technical Stack

*   **Backend:** Java 21, Spring Boot 3.2.5
*   **Persistence:** Spring Data JPA, Hibernate 6
*   **Database:** MySQL 8
*   **Security:** Spring Security, Auth0 JWT
*   **Documentation:** Swagger UI (Springdoc OpenAPI)
*   **Validation:** Jakarta Bean Validation
*   **Tools:** Lombok, MapStruct, Maven

---

## 🏗️ Architecture

The project follows the **Layered Architecture** pattern and **SOLID** principles to ensure maintainability and testability:

1.  **Controllers:** REST endpoints handling HTTP requests.
2.  **DTOs (Data Transfer Objects):** Using **Java Records** for immutable data transfer.
3.  **Services:** Business logic layer with interface-based design.
4.  **Repositories:** Data access layer using Spring Data JPA.
5.  **Mappers:** Decoupling entities from DTOs via MapStruct.

---

## 📝 API Documentation

Once the application is running, you can explore the interactive API documentation (Swagger) at:
`http://localhost:8080/swagger-ui.html`

---

## ⚙️ Getting Started

### Prerequisites
*   JDK 21 or higher
*   MySQL 8.0+
*   Maven 3.x

### Setup
1. **Clone the repository:**
   ```bash
   git clone https://github.com/GerseyMachava/ecommerce-springboot-backend.git
   ```
2. **Configure Database:**
   Update `src/main/resources/application.properties` with your MySQL credentials:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/ecommerce
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```
3. **Run the Application:**
   ```bash
   mvn spring-boot:run
   ```

---

## 🛤️ Roadmap & Improvements

- [ ] **Dockerization:** Add Dockerfile and Docker Compose for easy deployment.
- [ ] **Database Migrations:** Integrate Flyway to manage schema changes.
- [ ] **Caching:** Implement Redis for frequently accessed product categories.
- [ ] **Testing:** Expand unit and integration test coverage (JUnit 5 & Mockito).

---

## 👤 Author
**Gersey Machava**
*   GitHub: [@GerseyMachava](https://github.com/GerseyMachava)
*   LinkedIn: [Gersey Machava](https://www.linkedin.com/in/gersey-machava/)
