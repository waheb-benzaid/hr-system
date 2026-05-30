# HR System — Microservices

A simplified HR system built with Spring Boot microservices architecture, featuring an **Employee Service** and a **Leave Service**, each with its own PostgreSQL database.

---

## Table of Contents

- [Prerequisites & Setup](#prerequisites--setup)
- [Running the Databases (Docker Compose)](#running-the-databases-docker-compose)
- [Running the Services](#running-the-services)
- [Ports & URLs](#ports--urls)
- [API Documentation (Swagger)](#api-documentation-swagger)

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