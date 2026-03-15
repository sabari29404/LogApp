# LogApp — Microservices Authentication App

## Tech Stack
- **Backend:** Spring Boot 4, Spring Security, JWT, OAuth2 (Google + GitHub)
- **Frontend:** React 18
- **Database:** PostgreSQL
- **Service Discovery:** Eureka Server
- **API Gateway:** Spring Cloud Gateway
- **Containerization:** Docker + Docker Compose

## Architecture
```
React (port 3000) → API Gateway (port 8000) → Auth Service (port 8080)
                                             → Success Service (port 8001)
                                             → Eureka Server (port 8761)
                                             → PostgreSQL (port 5432)
```

---

## 🐳 Run with Docker (Recommended)

### Prerequisites
- [Docker Desktop](https://www.docker.com/products/docker-desktop/) installed and running

### Steps

**1. Clone the repository**
```bash
git clone https://github.com/sabari29404/LogApp.git
cd LogApp
```

**2. Create your .env file**
```bash
cp .env.example .env
```

**3. Open .env and fill in your credentials**
```
DB_NAME=authdb
DB_USER=postgres
DB_PASSWORD=your_postgres_password
JWT_SECRET=any_random_string_minimum_32_characters
GOOGLE_CLIENT_ID=your_google_client_id
GOOGLE_CLIENT_SECRET=your_google_client_secret
GITHUB_CLIENT_ID=your_github_client_id
GITHUB_CLIENT_SECRET=your_github_client_secret
```

**4. Start everything with one command**
```bash
docker-compose up --build
```

That's it! Docker will automatically:
- Download all dependencies
- Build all services
- Create and seed the PostgreSQL database
- Start all 6 containers

**5. Open the app**

| Service | URL |
|---|---|
| React Frontend | http://localhost:3000 |
| API Gateway | http://localhost:8000 |
| Eureka Dashboard | http://localhost:8761 |

**Login credentials:** `scrummaster` / `1234`

### Stop the app
```bash
docker-compose down
```

### Stop and delete all data
```bash
docker-compose down -v
```

---

## 💻 Run Without Docker (Manual)

### Prerequisites
- Java 25
- Maven
- Node.js & npm
- PostgreSQL

### Steps

**1. Create database**
```sql
CREATE DATABASE authdb;
```

**2. Fill credentials** in:
- `auth-microservice/src/main/resources/application.yml`
- `Success-Service/src/main/resources/application.yaml`

**3. Start services** (5 separate terminals)
```bash
# Terminal 1
cd Eureka-Server && mvn spring-boot:run

# Terminal 2
cd auth-microservice && mvn spring-boot:run

# Terminal 3
cd Success-Service && mvn spring-boot:run

# Terminal 4
cd API-Gateway && mvn spring-boot:run

# Terminal 5
cd logapp-react && npm install && npm start
```

Or on Windows just double-click **`start.bat`**

---

## OAuth2 Setup
Register these redirect URIs in your OAuth apps:
- **Google:** `http://localhost:8080/login/oauth2/code/google`
- **GitHub:** `http://localhost:8080/login/oauth2/code/github`
