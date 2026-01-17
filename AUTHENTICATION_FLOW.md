# JWT Authentication System - Complete Implementation Guide

## 🎯 What I Built

I implemented a **complete JWT (JSON Web Token) authentication system** for your Spring Boot API. This allows users to log in with username/password and receive a token to access protected endpoints.

---

## 📋 Components Created

### 1. **AuthController.java** - Login Endpoint
**Location:** `src/main/java/com/assessment/controller/AuthController.java`

**What it does:**
- Provides `POST /api/auth` endpoint for login
- Accepts username/email + password
- Returns JWT token on successful authentication

**Key Code:**
```java
@PostMapping("/auth")
public ResponseEntity<?> login(@RequestBody LoginRequest request) {
    // 1. Authenticate user
    // 2. Generate JWT token
    // 3. Return token to client
}
```

---

### 2. **JwtUtil.java** - Token Generator & Validator
**Location:** `src/main/java/com/assessment/security/JwtUtil.java`

**What it does:**
- **Generates** JWT tokens with username + role embedded
- **Validates** tokens (signature, expiration)
- **Extracts** username from tokens

**Key Methods:**
```java
generateToken(username, role)     // Creates JWT
validateToken(token, username)    // Checks if valid
getUsernameFromToken(token)       // Extracts username
```

**Token Structure:**
```
Header.Payload.Signature
eyJhbGc... (contains username, role, expiration)
```

**Security:**
- Secret key: `your-256-bit-secret-key-change-this-in-production`
- Expiration: 24 hours
- Algorithm: HMAC-SHA256

---

### 3. **JwtAuthenticationFilter.java** - Request Interceptor
**Location:** `src/main/java/com/assessment/security/JwtAuthenticationFilter.java`

**What it does:**
- **Intercepts every HTTP request** before it reaches controllers
- Checks for `Authorization: Bearer <token>` header
- Validates token and sets authentication in Spring Security context

**Flow:**
```
HTTP Request → Filter → Validate JWT → Set Authentication → Controller
```

---

### 4. **SecurityConfig.java** - Updated Security Rules
**Location:** `src/main/java/com/assessment/config/SecurityConfig.java`

**What I changed:**
```java
// PUBLIC endpoints (no authentication required)
POST /api/auth                    ✅ Anyone can login
GET  /api/users/generate          ✅ Anyone can generate users
POST /api/users/batch             ✅ Anyone can upload batch

// PROTECTED endpoints (JWT required)
GET  /api/users/me                🔒 Requires valid JWT
GET  /api/users/{username}        🔒 Requires ADMIN role

// JWT Filter Integration
Added JwtAuthenticationFilter before UsernamePasswordAuthenticationFilter
```

---

### 5. **DTOs (Data Transfer Objects)**

#### **LoginRequest.java**
```java
{
  "username": "john.doe",  // Can be username OR email
  "password": "pass123"
}
```

#### **LoginResponse.java**
```java
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "john.doe",
  "role": "user"
}
```

---

### 6. **pom.xml** - Added JWT Dependencies
```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.3</version>
</dependency>
<!-- + 2 more JWT libraries -->
```

---

## 🔄 Authentication Flow Diagram

```mermaid
sequenceDiagram
    participant Client as User (Browser/Postman)
    participant Auth as AuthController
    participant UserRepo as UserRepository
    participant JWT as JwtUtil
    participant Filter as JwtAuthenticationFilter
    participant Secured as Protected Endpoint

    rect rgb(230, 240, 255)
    Note over Client,JWT: STEP 1: LOGIN & GET TOKEN
    Client->>+Auth: POST /api/auth<br/>{username, password}
    Auth->>+UserRepo: findByUsernameOrEmail()
    UserRepo-->>-Auth: User found
    Auth->>Auth: Check password (BCrypt)
    
    alt Password Valid
        Auth->>+JWT: generateToken(username, role)
        JWT-->>-Auth: JWT Token (24h expiration)
        Auth-->>Client: 200 OK<br/>{token, username, role}
    else Password Invalid
        Auth-->>Client: 401 Unauthorized<br/>{error: "Invalid credentials"}
    end
    end

    rect rgb(240, 255, 240)
    Note over Client,Secured: STEP 2: ACCESS PROTECTED ENDPOINT
    Client->>+Filter: GET /api/users/me<br/>Header: Authorization: Bearer <token>
    Filter->>Filter: Extract token from header
    Filter->>+JWT: validateToken(token)
    JWT->>JWT: Check signature & expiration
    JWT-->>-Filter: ✅ Token valid
    Filter->>+JWT: getUsernameFromToken()
    JWT-->>-Filter: "john.doe"
    Filter->>Filter: Set Spring Security context
    Filter->>+Secured: Forward request with authentication
    Secured-->>-Client: 200 OK<br/>{user profile data}
    end

    rect rgb(255, 240, 240)
    Note over Client,Secured: STEP 3: INVALID TOKEN
    Client->>+Filter: GET /api/users/me<br/>No token or expired token
    Filter->>+JWT: validateToken(token)
    JWT-->>-Filter: ❌ Token invalid/expired
    Filter-->>Client: 401 Unauthorized<br/>{error: "Invalid or expired token"}
    end
```

---

## 🏗️ System Architecture

```mermaid
flowchart TB
    subgraph Client["Client Layer"]
        Browser["Browser/Postman"]
    end

    subgraph Controller["Controller Layer"]
        AuthC["AuthController<br/>POST /api/auth"]
        UserC["UserController<br/>GET /api/users/me<br/>GET /api/users/{username}"]
    end

    subgraph Security["Security Layer"]
        Filter["JwtAuthenticationFilter<br/>(Intercepts ALL requests)"]
        JwtUtil["JwtUtil<br/>(Token generator/validator)"]
        SecConfig["SecurityConfig<br/>(Defines public/protected routes)"]
    end

    subgraph Service["Service Layer"]
        UserRepo["UserRepository<br/>(Database access)"]
    end

    subgraph Database["Database"]
        H2["H2 Database<br/>(users table)"]
    end

    Browser -->|1. POST login| AuthC
    Browser -->|2. GET with JWT| Filter
    
    Filter -->|Extract & validate token| JwtUtil
    Filter -->|If valid| UserC
    
    AuthC -->|Find user| UserRepo
    AuthC -->|Generate token| JwtUtil
    
    UserC -->|Get user data| UserRepo
    UserRepo -->|Query| H2

    style Filter fill:#ffcccc
    style JwtUtil fill:#ffcccc
    style SecConfig fill:#ffcccc
    style AuthC fill:#ccffcc
    style UserC fill:#ccffcc
```

---

## 🔑 How JWT Works

### Token Structure
```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJqb2huLmRvZSIsInJvbGUiOiJ1c2VyIiwiaWF0IjoxNjQwOTk1MjAwLCJleHAiOjE2NDEwODE2MDB9.signature

├─ HEADER (Base64)
│  {"alg":"HS256","typ":"JWT"}
│
├─ PAYLOAD (Base64)
│  {"sub":"john.doe","role":"user","iat":1640995200,"exp":1641081600}
│  
└─ SIGNATURE (HMAC-SHA256)
   HMACSHA256(header + payload, secret_key)
```

### Why JWT?
- ✅ **Stateless** - No session storage needed
- ✅ **Scalable** - Works across multiple servers
- ✅ **Self-contained** - Token includes user info
- ✅ **Secure** - Signed with secret key

---

## 🧪 How to Test

### 1. **Start the Application**
```bash
mvn spring-boot:run
```

### 2. **Login to Get Token**
```bash
curl -X POST http://localhost:9090/api/auth \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john.doe",
    "password": "pass123"
  }'
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "john.doe",
  "role": "user"
}
```

### 3. **Access Protected Endpoint**
```bash
curl -X GET http://localhost:9090/api/users/me \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

### 4. **Test Invalid Token**
```bash
curl -X GET http://localhost:9090/api/users/me \
  -H "Authorization: Bearer invalid-token"
```

**Response:**
```json
{
  "error": "Unauthorized",
  "message": "Invalid or expired token"
}
```

---

## 📊 Endpoint Security Summary

| Endpoint | Method | Access | Requirements |
|----------|--------|--------|--------------|
| `/api/auth` | POST | 🌐 Public | None |
| `/api/users/generate` | GET | 🌐 Public | None |
| `/api/users/batch` | POST | 🌐 Public | None |
| `/api/users/me` | GET | 🔒 Protected | Valid JWT |
| `/api/users/{username}` | GET | 🔒 Admin Only | Valid JWT + ADMIN role |

---

## 🔐 Security Features Implemented

1. **Password Encryption**: BCrypt hashing (done in UserBatchService)
2. **Token Expiration**: 24-hour validity
3. **Signature Verification**: Prevents token tampering
4. **Role-Based Access**: Admin vs User permissions
5. **Error Handling**: Proper 401/403 responses

---

## 📝 Files Modified/Created Summary

### Created (7 files):
1. ✅ `AuthController.java` - Login endpoint
2. ✅ `JwtUtil.java` - Token operations
3. ✅ `JwtAuthenticationFilter.java` - Request interceptor
4. ✅ `LoginRequest.java` - Login DTO
5. ✅ `LoginResponse.java` - Response DTO

### Modified (2 files):
6. ✅ `SecurityConfig.java` - Added JWT filter + route protection
7. ✅ `pom.xml` - Added JWT dependencies

---

## 🎓 Interview Talking Points

> **"I implemented a JWT-based authentication system for the REST API. When users log in via POST /api/auth, the system validates credentials against the database using BCrypt, then generates a signed JWT token containing the username and role with 24-hour expiration. For protected endpoints, a custom filter intercepts requests, extracts and validates the Bearer token, and sets the Spring Security context. This stateless approach scales well and eliminates server-side session management. The system uses HMAC-SHA256 for token signing and implements proper error handling for expired or invalid tokens."**

---

## 🚀 Next Steps

- ✅ API #1: Generate Users (DONE)
- ✅ API #2: Batch Upload (DONE)
- ✅ API #3: Authentication (DONE - Just now!)
- ⏳ API #4: Get My Profile (TODO - requires JWT)
- ⏳ API #5: Get User by Username (TODO - requires JWT + Admin)

Now you can implement the secured endpoints using the JWT authentication! 🎉
