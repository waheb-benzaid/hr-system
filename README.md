# HR System — Microservices

A simplified HR system built with Spring Boot microservices architecture, featuring an **Employee Service** and a **Leave Service**, each with its own PostgreSQL database.

---

## Table of Contents

- [Prerequisites & Setup](#prerequisites--setup)
- [Running the Databases (Docker Compose)](#running-the-databases-docker-compose)
- [Running the Services](#running-the-services)
- [Ports & URLs](#ports--urls)
- [API Documentation (Swagger)](#api-documentation-swagger)
- [Technical Choices & Design Decisions](#technical-choices--design-decisions)
- [Monolith to Microservices — Migration Strategy](#monolith-to-microservices--migration-strategy)

---

## Prerequisites & Setup

### 1. Java Development Kit (JDK 25)

This project requires **JDK 25**. Check if it is already installed:

```bash
java -version
javac -version
```

If not installed, download and install from the official source:

- **Fedora / RHEL / CentOS:**
  ```bash
  sudo dnf install java-25-openjdk-devel
  ```
- **Ubuntu / Debian:**
  ```bash
  sudo apt install openjdk-25-jdk
  ```
- **macOS (Homebrew):**
  ```bash
  brew install openjdk@25
  ```
- **Windows / Manual install:** Download from [https://jdk.java.net/25](https://jdk.java.net/25) and follow the installer.

After installation, verify again:
```bash
java -version   # should show: openjdk version "25.x.x"
javac -version  # should show: javac 25.x.x
```

> **Note:** Maven is not required globally. Both services include an embedded Maven Wrapper (`./mvnw`) that downloads and manages Maven automatically on first run.

---

### 2. Docker & Docker Compose

Docker is used to run the PostgreSQL databases.

- **Fedora / RHEL:**
  ```bash
  sudo dnf install docker docker-compose-plugin
  sudo systemctl start docker
  sudo systemctl enable docker
  ```
- **Ubuntu / Debian:**
  ```bash
  sudo apt install docker.io docker-compose-plugin
  sudo systemctl start docker
  ```
- **macOS / Windows:** Install [Docker Desktop](https://www.docker.com/products/docker-desktop/)

Verify installation:
```bash
docker --version          # Docker version 24.x or higher
docker compose version    # Docker Compose version v2.x or higher
```

---

### 3. IDE (Recommended)

Any IDE that supports Java and Spring Boot works. **IntelliJ IDEA** is recommended:

- Download: [https://www.jetbrains.com/idea/download](https://www.jetbrains.com/idea/download)
- The **Community Edition** (free) is sufficient.
- After opening the project, IntelliJ will automatically detect the Maven projects and download all dependencies.

**VS Code** is also supported — install the [Extension Pack for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack).

---

### 4. Dependencies (Auto-downloaded by Maven)

All dependencies are declared in each service's `pom.xml` and are downloaded automatically when you build or run a service. No manual installation is needed. Key dependencies include:

| Dependency | Purpose |
|---|---|
| `spring-boot-starter-webmvc` | REST API layer |
| `spring-boot-starter-data-jpa` | Database access via JPA/Hibernate |
| `spring-boot-starter-validation` | Bean Validation (`@NotBlank`, `@Email`, etc.) |
| `postgresql` | PostgreSQL JDBC driver |
| `lombok` | Boilerplate reduction (`@Getter`, `@Builder`, etc.) |
| `springdoc-openapi-starter-webmvc-ui` | Swagger / OpenAPI documentation |

---

## Running the Databases (Docker Compose)

The `docker-compose.yml` at the root of the project defines two isolated PostgreSQL databases — one per service.

### Start all databases

From the project root:

```bash
sudo docker compose up -d
```

### Start only a specific database

```bash
sudo docker compose up -d employee-db   # Employee Service database only
sudo docker compose up -d leave-db      # Leave Service database only
```

### Stop all databases

```bash
sudo docker compose down
```

### Stop and remove all data (full reset)

```bash
sudo docker compose down -v
```

### Check database status

```bash
sudo docker compose ps
sudo docker compose logs employee-db
sudo docker compose logs leave-db
```

> **What this creates:**
> - `employee-db` → PostgreSQL on port **5432**, database `employee_db`
> - `leave-db` → PostgreSQL on port **5433**, database `leave_db`
> - Data is persisted in named Docker volumes (`employee-db-data`, `leave-db-data`) and survives container restarts.

---

## Running the Services

Each service is a standalone Spring Boot application. You can run them from the terminal using the included Maven Wrapper, or directly from your IDE.

> **Important:** The databases must be running (via Docker Compose) before starting the services.

### Employee Service (Port 8081)

```bash
cd employee-service
./mvnw spring-boot:run
```

Wait for the following log line before making requests:
```
Started EmployeeApplication in X.XXX seconds
```

### Leave Service (Port 8082)

Open a **new terminal**, then:

```bash
cd leave-service
./mvnw spring-boot:run
```

Wait for:
```
Started LeaveApplication in X.XXX seconds
```

> **Service dependency:** The Leave Service communicates with the Employee Service to validate employees when creating leave requests. The Employee Service should be running first.

### Running from IntelliJ IDEA

1. Open the project root folder in IntelliJ IDEA.
2. Wait for Maven to index the projects (bottom progress bar).
3. Navigate to `EmployeeApplication.java` → right-click → **Run**.
4. Navigate to `LeaveApplication.java` → right-click → **Run**.

---

## Ports & URLs

| Service | Application Port | Database Port | Database Name |
|---|---|---|---|
| Employee Service | **8081** | 5432 | `employee_db` |
| Leave Service | **8082** | 5433 | `leave_db` |
| pgAdmin (optional) | **5050** | — | — |

### Service endpoints

| URL | Description |
|---|---|
| `http://localhost:8081/api/v1/employees` | Employee Service REST API |
| `http://localhost:8082/api/v1/leaves` | Leave Service REST API |

### pgAdmin (Database UI)

If you have pgAdmin running via Docker Compose:

- URL: [http://localhost:5050](http://localhost:5050)
- Email: `admin@admin.com`
- Password: `admin`

To connect to a database in pgAdmin, add a new server with:
- **Employee DB:** Host `localhost`, Port `5432`, Database `employee_db`, User `employee_user`, Password `employee_pass`
- **Leave DB:** Host `localhost`, Port `5433`, Database `leave_db`, User `leave_user`, Password `leave_pass`

---

## API Documentation (Swagger)

Both services expose interactive API documentation powered by **SpringDoc OpenAPI / Swagger UI**.

### Unified Documentation Hub (Recommended)

A single page that lets you switch between both services using two buttons:

| URL | Requirement |
|---|---|
| **`http://localhost:8081/swagger-hub.html`** | Employee Service must be running |
| `http://localhost:8082/swagger-hub.html` | Leave Service must be running |

The hub shows a **live status indicator** (green/red dot) next to each service button. If a service is offline, a clear message is displayed with a retry button.

### Individual Service Swagger UIs

Each service also has its own standalone Swagger UI:

| Service | Swagger UI URL | OpenAPI JSON |
|---|---|---|
| Employee Service | `http://localhost:8081/swagger-ui.html` | `http://localhost:8081/v3/api-docs` |
| Leave Service | `http://localhost:8082/swagger-ui.html` | `http://localhost:8082/v3/api-docs` |

### Using Swagger UI

1. Open `http://localhost:8081/swagger-hub.html` in your browser.
2. Click **👤 Employee Service** or **📅 Leave Service** to view the endpoints.
3. Click on any endpoint to expand it.
4. Click **Try it out** to test the endpoint directly from the browser.
5. Fill in the request body or parameters and click **Execute**.

---

## Technical Choices & Design Decisions

### 1. Repository Pattern

The codebase follows the **Repository pattern**, which separates the application into four distinct layers:

| Layer | Role |
|---|---|
| **Controller** | Receives HTTP requests, validates input, delegates to the service |
| **Service** | Contains all business logic and orchestration |
| **Repository** | Handles all database access via Spring Data JPA |
| **Model / DTO** | Entities represent the database schema; DTOs decouple the API contract from internal data |

**Why this pattern?**

- **Separation of concerns:** Each layer has a single, well-defined responsibility. A change to the database schema does not affect the API contract, and vice versa.
- **Maintainability:** Business logic lives exclusively in the service layer, making it easy to locate, test, and modify without touching HTTP or database code.
- **Testability:** Each layer can be tested in isolation — the service can be unit-tested with a mocked repository, and the controller can be tested with a mocked service.
- **Scalability:** Adding new features (e.g., caching, auditing) means touching only the relevant layer rather than spreading logic across the codebase.

---

### 2. Synchronous Communication Between Services

The Leave Service communicates with the Employee Service using **synchronous REST calls** via Spring's `RestClient`.

**Why synchronous?**

- **Simplicity:** The use case is a straightforward request/response — before creating a leave request, we need to know immediately whether the employee exists. Synchronous communication maps naturally to this.
- **Immediate feedback:** The client gets a direct, meaningful error response if the employee does not exist or if the Employee Service is down — no delayed processing or polling required.


---

### 3. Soft Delete (Deactivation) Instead of Hard Delete

Deleting an employee record permanently (`DELETE FROM employees`) was deliberately avoided. Instead, employees are **deactivated** by setting an `active = false` flag and recording a `deactivatedAt` timestamp.

**Why soft delete?**

- **Reference integrity across services:** Leave requests store the `employeeId` as a UUID reference. If the employee record were deleted, all historical leave requests would point to a non-existent entity — breaking auditability and reporting.
---

### 4. Consistency Between Services

In a microservice architecture, each service owns its own database. There is no shared database and no foreign keys across service boundaries. Consistency is ensured through the following mechanisms:

**At creation time (strong consistency):**
Before a leave request is created, the Leave Service calls `GET /api/v1/employees/{id}` on the Employee Service synchronously. The leave request is only persisted if the employee is confirmed to exist and the call succeeds. This prevents orphaned leave requests from being created in the first place.

**At rest (eventual reference):**
Once a leave request is created, the `employeeId` is stored locally in the Leave Service's database. The Leave Service does not re-validate the employee on every subsequent read — the assumption is that the data was valid at creation time. This is a deliberate trade-off: re-validating on every read would create unnecessary coupling and latency.

**Soft delete as a consistency safeguard:**
Because employees are never hard-deleted, an `employeeId` stored in a leave request will always point to a record that still exists in the Employee Service database — it may be inactive, but it is never gone. This preserves referential integrity without cross-service foreign keys.

**Duplicate prevention:**
A unique constraint at the database level (`employee_id, start_date, end_date`) and a pre-insert check at the service layer together ensure no duplicate leave requests can be created — even under concurrent submissions.

---

### 5. Failure Scenario Handling

The system uses a **fail-fast** strategy: when a dependency is unavailable or data is invalid, requests are rejected immediately with a clear, structured error response.

#### Invalid Input Data — `400 Bad Request`

Validation happens at two levels:

- **Jakarta Bean Validation** on DTOs catches missing fields, blank strings, invalid email formats, and past dates before the request even reaches the service layer.
- **Service-layer checks** enforce business rules: email uniqueness, date range logic (`endDate >= startDate`), valid status transitions, and duplicate leave request detection.

All validation errors are caught by a `GlobalExceptionHandler` and returned in a consistent JSON format:
```json
{
  "status": 400,
  "message": "Validation failed",
  "timestamp": "2026-05-30T01:31:34",
  "errors": {
    "email": "Email must be a valid email address",
    "firstName": "First name is required"
  }
}
```

#### Resource Not Found — `404 Not Found`

A `ResourceNotFoundException` is thrown whenever a requested entity (employee or leave request) does not exist. The `GlobalExceptionHandler` maps this to a clean 404 response with a descriptive message.

#### Employee Service Unavailable — `503 Service Unavailable`

The `RestClient` in the Leave Service is configured with a **3-second timeout** on both connection and read. If the Employee Service is unreachable (network error, timeout, or crash), a `ServiceUnavailableException` is thrown and the request is rejected with a `503` response:
```json
{
  "status": 503,
  "message": "Employee Service is currently unavailable. Please try again later.",
  "timestamp": "2026-05-30T01:31:34"
}
```
No leave request is persisted in this case. The client is expected to retry once the dependency recovers.

#### Employee Not Found (404 from Employee Service) — `400 Bad Request`

If the Employee Service responds with `404` (employee does not exist), this is treated as invalid input from the caller's perspective — they submitted an `employeeId` that does not exist. This is returned as a `400 Bad Request`, not a `503`, since the problem is with the request data, not with service availability.

#### Invalid Status Transition — `400 Bad Request`

The Leave Service enforces a strict state machine for leave status:
- `PENDING → APPROVED` ✅
- `PENDING → REJECTED` ✅
- `APPROVED → anything` ❌
- `REJECTED → anything` ❌

Attempting an invalid transition returns a `400` with a message explaining why the change was rejected.

---

## Monolith to Microservices — Migration Strategy

This section answers the question: if we had a monolithic system containing both Employee and Leave features in production, how would we migrate to the microservice architecture we built above — without breaking anything for existing clients?

The key challenge is: **how do we do this gradually and safely, without a "big bang" switch that risks downtime?**

### The Approach — Strangler Fig Pattern

The strategy I would follow is called the **Strangler Fig Pattern**. The idea is simple: instead of rewriting everything at once and switching over in one shot, we build the new microservices alongside the monolith, gradually redirect traffic to them, and eventually shut down the monolith when it is no longer needed.

Think of it like building a new bridge next to the old one, moving traffic lane by lane, and then demolishing the old bridge only when all traffic has been moved.

---

### Phase 1 — Analyze and Define Service Boundaries

**What we do:** Before writing any code, we study the monolith to understand how Employee and Leave features are connected.

**Concretely:**
- Identify which database tables belong to Employee and which belong to Leave
- Map out all the places in the code where Employee logic calls Leave logic (and vice versa)
- Identify shared tables, shared models, or direct SQL joins between the two features
- Document all the API endpoints that external clients are currently using

**Why this matters:** If we start splitting without understanding the dependencies, we will break things. For example, if the Leave module directly queries the `employees` table via a SQL JOIN, that JOIN will break once the tables are in separate databases.

**Output:** A clear map of what belongs to each service, what they share, and what the current API contract looks like.

---

### Phase 2 — Build the New Microservices in Parallel

**What we do:** We develop the Employee Service and Leave Service as new, independent applications — exactly like what we built in this project. But we do **not** redirect any traffic to them yet.

**Concretely:**
- Each service gets its own codebase, its own database, its own API
- The Leave Service communicates with the Employee Service via REST (not via shared database)
- We replicate the same business logic that exists in the monolith
- We write tests to verify that the new services produce the same results as the monolith for the same inputs

**Key rule:** The monolith stays running and serving all real traffic during this phase. The new services exist but are not exposed to clients yet.

---

### Phase 3 — Data Migration

**What we do:** We migrate the data from the single monolith database into two separate databases (one per service).

**Concretely:**
- Set up the two new PostgreSQL databases (`employee_db`, `leave_db`)
- Write migration scripts that copy Employee data to `employee_db` and Leave data to `leave_db`
- Replace the direct `employeeId` foreign key in the Leave table with a UUID reference (since cross-database foreign keys do not exist)
- Run the migration on a copy of the production database first and validate the results before touching real data

**Risk:** Data can change in the monolith while we are migrating. To handle this, we use a **dual-write** approach in the next phase — any write that happens in the monolith is also replicated to the new databases, so they stay in sync.

---

### Phase 4 — API Gateway + Dual Write

**What we do:** We place an **API Gateway** (e.g., Spring Cloud Gateway, Nginx, or Kong) in front of both the monolith and the new services. This gateway becomes the single entry point for all clients.

```
                          ┌─────────────┐
     Clients ────────────>│ API Gateway │
                          └──────┬──────┘
                                 │
                    ┌────────────┼────────────┐
                    ▼            ▼             ▼
              ┌──────────┐ ┌──────────┐ ┌──────────┐
              │ Monolith │ │ Employee │ │  Leave   │
              │ (old)    │ │ Service  │ │ Service  │
              └──────────┘ └──────────┘ └──────────┘
```

**Why an API Gateway?**
- Clients do not need to know that the backend is changing. They keep calling the same URLs.
- The gateway can route `/api/v1/employees` to the monolith or to the new Employee Service — we control which one, and we can switch gradually.
- If something goes wrong with a new service, the gateway can instantly route traffic back to the monolith (rollback).

**Dual write:** During this phase, the monolith continues to handle all traffic, but every write (create, update, delete) is also sent to the new microservice databases. This keeps the new databases in sync with the monolith. We can then compare the data in both systems to verify correctness.

---

### Phase 5 — Gradual Traffic Shifting (Canary Release)

**What we do:** We start redirecting a small percentage of real traffic from the monolith to the new microservices, and increase it gradually.

**Concretely:**
- Start with **read-only traffic**: route `GET /api/v1/employees` to the new Employee Service for 10% of requests, while 90% still go to the monolith
- Compare the responses — they should be identical
- If they match, increase to 25%, then 50%, then 100% of read traffic
- Once reads are fully migrated, do the same for **write traffic** (POST, PUT, PATCH, DELETE)
- Monitor error rates, latency, and data consistency at every step

**Rollback plan:** At any point, if error rates increase or data does not match, the gateway routes 100% of traffic back to the monolith. The monolith is still running and its data is still up to date (because of dual-write).

---

### Phase 6 — Decommission the Monolith

**What we do:** Once all traffic has been running on the new microservices for a sufficient period (e.g., 2–4 weeks) with no issues, we shut down the monolith.

**Concretely:**
- Stop the dual-write mechanism
- Remove the monolith from the API Gateway routing
- Keep the monolith codebase and database archived (not deleted) for a safety period
- Clean up old infrastructure

---

### How This Ensures Safety

- **No big bang:** We never switch everything at once. Traffic shifts gradually.
- **Rollback at every phase:** The monolith remains running and serving traffic until the very end. We can always go back.
- **Client transparency:** The API Gateway keeps the same URLs. Clients do not change anything.
- **Data consistency:** Dual-write keeps both systems in sync. We validate data correctness before shifting traffic.
- **Monitoring:** We compare responses between the monolith and the new services to catch discrepancies before they affect users.