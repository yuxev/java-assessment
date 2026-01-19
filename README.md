# User Management API - Spring Boot Assessment

[![Build](https://img.shields.io/badge/build-passing-brightgreen)]() [![Tests](https://img.shields.io/badge/tests-30%2F30-success)]() [![Java](https://img.shields.io/badge/Java-17-orange)]() [![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.1-green)]()

A complete RESTful API for user management with JWT authentication, built with Spring Boot, H2 database, and comprehensive test coverage.

---

## 🎯 Features Implemented

### ✅ All 5 Required API Endpoints

1. **User Generation** - `GET /api/users/generate`
   - Generates realistic fake users using JavaFaker
   - Downloads as JSON file
   - Configurable count (1-500)

2. **Batch Upload** - `POST /api/users/batch`
   - Import users from JSON file
   - Duplicate detection (email/username)
   - Password encryption with BCrypt

3. **Authentication** - `POST /api/auth`
   - Login with username or email
   - Returns JWT token
   - Token expires in 24 hours

4. **My Profile** - `GET /api/users/me`
   - Authenticated user's profile
   - JWT required

5. **Get User** - `GET /api/users/{username}`
   - Admin-only endpoint
   - View any user's profile
   - Role-based access control

---

## 🚀 Quick Start

### Prerequisites
- Java 17 or higher
- Maven 3.6+

### Run the Application

```bash
# Build the project
mvn clean package

# Run the application
mvn spring-boot:run
```

The application will start on **http://localhost:9090**

### Access Swagger UI

Open your browser to:
```
http://localhost:9090/swagger-ui/index.html
```

All endpoints are **fully testable** from the Swagger interface!

---

## 📚 API Documentation

### 1. Generate Users
```http
GET http://localhost:9090/api/users/generate?count=10
```

**Response:** Downloads JSON file with generated users

### 2. Batch Upload Users
```http
POST http://localhost:9090/api/users/batch
Content-Type: multipart/form-data

file: users.json
```

**Response:**
```json
{
  "total": 10,
  "imported": 8,
  "rejected": 2
}
```

### 3. Login
```http
POST http://localhost:9090/api/auth
Content-Type: application/json

{
  "username": "john.doe",
  "password": "password123"
}
```

**Response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### 4. Get My Profile
```http
GET http://localhost:9090/api/users/me
Authorization: Bearer <your-jwt-token>
```

### 5. Get User by Username (Admin Only)
```http
GET http://localhost:9090/api/users/john.doe
Authorization: Bearer <admin-jwt-token>
```

---

## 🧪 Testing

### Run All Tests
```bash
mvn test
```

**Test Coverage:**
- ✅ 30 unit/integration tests
- ✅ 100% endpoint coverage
- ✅ Authentication & authorization tests
- ✅ Error handling tests

**Test Results:**
```
Tests run: 30, Failures: 0, Errors: 0, Skipped: 0
✅ BUILD SUCCESS
```

### Test Files
- `UserControllerUnsecuredTest.java` - Unsecured endpoints (6 tests)
- `AuthControllerTest.java` - Authentication (7 tests)
- `UserControllerSecuredTest.java` - Secured endpoints (10 tests)
- `UserGeneratorTest.java` - Service tests (7 tests)

---

## 🛠️ Technology Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| **Java** | 17 | Programming language |
| **Spring Boot** | 3.2.1 | Application framework |
| **Spring Security** | 6.2.1 | Authentication & authorization |
| **H2 Database** | 2.2.224 | In-memory database |
| **JWT (JJWT)** | 0.11.5 | Token authentication |
| **JavaFaker** | 1.0.2 | Realistic data generation |
| **SpringDoc OpenAPI** | 2.3.0 | Swagger UI documentation |
| **JUnit 5** | 5.10.1 | Testing framework |
| **Maven** | 3.9+ | Build tool |

---

## 📁 Project Structure

```
src/main/java/com/assessment/
├── Application.java                    # Main entry point
├── config/
│   ├── FakerConfig.java               # Faker bean configuration
│   ├── SecurityConfig.java            # Security & JWT setup
│   └── OpenApiConfig.java             # Swagger configuration
├── controller/
│   ├── AuthController.java            # Login endpoint
│   └── UserController.java            # User CRUD endpoints
├── dto/
│   ├── LoginRequest.java              # Login request body
│   ├── AuthResponse.java              # JWT response
│   ├── GeneratedUser.java             # User generation DTO
│   └── BatchImportSummary.java        # Upload summary
├── model/
│   └── User.java                      # JPA entity
├── repository/
│   └── UserRepository.java            # Spring Data JPA
├── service/
│   ├── UserGenerator.java             # Faker-based generator
│   ├── UserGenerationService.java     # Generation logic
│   └── UserBatchService.java          # Batch import logic
├── security/
│   └── JwtAuthenticationFilter.java   # JWT filter
└── util/
    └── JwtUtil.java                   # JWT utilities

src/test/java/com/assessment/
├── controller/
│   ├── AuthControllerTest.java        # Auth endpoint tests
│   ├── UserControllerUnsecuredTest.java
│   └── UserControllerSecuredTest.java
└── service/
    └── UserGeneratorTest.java
```

---

## 🔐 Security Features

- **Password Encryption:** BCrypt with salt
- **JWT Authentication:** Stateless token-based auth
- **Role-Based Access:** Admin vs User roles
- **Token Expiration:** 24-hour validity
- **CORS Configuration:** Enabled for frontend integration
- **CSRF Protection:** Disabled for stateless API

---

## 💾 Database

**H2 In-Memory Database**
- Auto-configured on startup
- No manual setup required
- Data persists during application runtime
- Console available at: `http://localhost:9090/h2-console`

**Connection Details:**
- URL: `jdbc:h2:mem:userdb`
- Username: `sa`
- Password: *(empty)*

---

## 📝 Additional Features

- ✅ **No Manual Configuration:** Zero-config startup
- ✅ **Auto-Generated Swagger:** Interactive API documentation
- ✅ **Comprehensive Error Handling:** Proper HTTP status codes
- ✅ **Input Validation:** Request body validation
- ✅ **Realistic Test Data:** JavaFaker integration
- ✅ **Duplicate Detection:** Email/username uniqueness
- ✅ **Download Trigger:** File download (not browser display)

---

## 📖 Documentation

- **Authentication Flow:** See [AUTHENTICATION_FLOW.md](AUTHENTICATION_FLOW.md)
- **Project Requirements:** See [objective.txt](objective.txt)
- **Test API Client:** Open [test-api.html](test-api.html) in browser

---

## 🎓 Assessment Compliance

### ✅ All Requirements Met

| Requirement | Status |
|-------------|--------|
| Maven project | ✅ |
| Java 8 or higher | ✅ (Java 17) |
| Latest Spring Boot | ✅ (3.2.1) |
| H2 Database | ✅ |
| Port 9090 | ✅ |
| Zero manual config | ✅ |
| Swagger endpoint | ✅ |
| All endpoints testable in Swagger | ✅ |
| Unit tests | ✅ (30 tests) |
| Realistic data (JavaFaker) | ✅ |
| File download (not display) | ✅ |
| Duplicate detection | ✅ |
| Password encryption | ✅ |
| JWT authentication | ✅ |
| Role-based authorization | ✅ |

---

## 📄 License

This is an assessment project for evaluation purposes.