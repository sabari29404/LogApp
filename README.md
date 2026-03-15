# LogApp — Microservices Authentication App

## Tech Stack
- **Backend:** Spring Boot 4, Spring Security, JWT, OAuth2 (Google + GitHub)
- **Frontend:** React 18
- **Database:** PostgreSQL
- **Service Discovery:** Eureka Server
- **API Gateway:** Spring Cloud Gateway

## Prerequisites
Install these before running:
- Java 25
- Maven
- Node.js & npm
- PostgreSQL

## Step 1 — Clone the Repository
```bash
git clone https://github.com/sabari29404/LogApp.git
cd LogApp
```

## Step 2 — Database Setup
Open pgAdmin or psql and run:
```sql
CREATE DATABASE authdb;

\c authdb

CREATE TABLE company_entity (
    id SERIAL PRIMARY KEY,
    company_name VARCHAR(255) UNIQUE,
    started_at DATE,
    is_registered BOOLEAN
);

INSERT INTO company_entity (company_name, started_at, is_registered) VALUES
('Tech Corp', '2020-01-15', true),
('StartupXYZ', '2022-06-01', false),
('Innovate Ltd', '2019-03-20', true);
```

## Step 3 — Configure Credentials
Fill in the placeholders in these two files:

**`auth-microservice/src/main/resources/application.yml`**
```yaml
datasource:
  password: YOUR_POSTGRES_PASSWORD

security:
  oauth2:
    client:
      registration:
        google:
          client-id: YOUR_GOOGLE_CLIENT_ID
          client-secret: YOUR_GOOGLE_CLIENT_SECRET
        github:
          client-id: YOUR_GITHUB_CLIENT_ID
          client-secret: YOUR_GITHUB_CLIENT_SECRET

app:
  jwt:
    secret: "any_random_string_minimum_32_characters"
```

**`Success-Service/src/main/resources/application.yaml`**
```yaml
datasource:
  password: YOUR_POSTGRES_PASSWORD
```

## Step 4 — Start Backend Services
Open 4 separate terminals:

**Terminal 1**
```bash
cd Eureka-Server
mvn spring-boot:run
```

**Terminal 2**
```bash
cd auth-microservice
mvn spring-boot:run
```

**Terminal 3**
```bash
cd Success-Service
mvn spring-boot:run
```

**Terminal 4**
```bash
cd API-Gateway
mvn spring-boot:run
```

## Step 5 — Start React Frontend
**Terminal 5**
```bash
cd logapp-react
npm install
npm start
```

## Access the App
| Service | URL |
|---|---|
| React Frontend | http://localhost:3000 |
| API Gateway | http://localhost:8000 |
| Eureka Dashboard | http://localhost:8761 |

## Login
- **Username:** scrummaster
- **Password:** 1234

## OAuth2 Redirect URIs
Register these in Google Cloud Console and GitHub:
- Google: `http://localhost:8080/login/oauth2/code/google`
- GitHub: `http://localhost:8080/login/oauth2/code/github`
