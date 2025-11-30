# Weather Service - Full Stack Application

En komplett fullstack-applikation för väderdata med JWT-autentisering, microservice-arkitektur och email-verifiering. Byggd som slutprojekt i Java Enterprise Edition-kursen.

## Översikt

**Backend:** Spring Boot REST API med JWT-säkerhet, PostgreSQL-databas, Redis-cache och RabbitMQ message queue  
**Frontend:** React SPA med TypeScript, Tailwind CSS och JWT-autentisering  
**Microservices:** Email-service för användarverifiering via RabbitMQ  
**Deployment:** Docker Compose med multi-container setup

## Teknisk Stack

### Backend
- **Java 17** med Spring Boot 3.5.5
- **Spring Security** med JWT-autentisering (jjwt 0.11.5)
- **PostgreSQL 16** för datalagring
- **Redis** för cache (5 minuters TTL)
- **RabbitMQ** för asynkron meddelandehantering
- **Open-Meteo API** för väderdata

### Frontend
- **React 18** med TypeScript
- **React Router** för navigation
- **Tailwind CSS** för styling
- **JWT** localStorage-baserad autentisering

### Microservices
- **Email-Service** (Spring Boot) för verifikationsmail via Gmail SMTP
- **RabbitMQ** message broker för service-kommunikation

### DevOps
- **Docker Compose** för containerisering
- **Maven** för backend build
- **npm** för frontend build

## Säkerhetsimplementation

### Tre Säkerhetsnivåer

1. **Public** - Ingen autentisering krävs
   - `POST /api/auth/register`
   - `POST /api/auth/login`
   - `GET /api/auth/verify`

2. **Authenticated** - JWT-token krävs
   - `GET /favorites`
   - `PUT /favorites/{placeName}`
   - `GET /weather/**`
   - `GET /forecast/**`

3. **Admin** - JWT-token + ADMIN-roll krävs
   - `POST /admin/**`
   - `PUT /admin/**`
   - `DELETE /admin/**`

### JWT Token Flow
```
1. User registers → Backend creates user (enabled=true för demo)
2. Email-service skickar verifikationsmail (optional verification)
3. User logs in → Backend generates JWT token
4. Frontend sparar token i localStorage
5. Alla API-requests inkluderar: Authorization: Bearer <token>
6. Backend validerar token + extraherar användarinfo
```

## Docker Deployment

### Starta hela systemet
```bash
# Klona projektet
git clone <repository-url>
cd weather-service-auth

# Skapa .env-fil med Gmail credentials
cat > .env << 'ENVFILE'
OPENWEATHER_API_KEY=your_key_here
EMAIL_USERNAME=your-email@gmail.com
EMAIL_PASSWORD=your-app-password
ENVFILE

# Starta alla services
docker-compose up --build -d

# Kontrollera status
docker-compose ps
```

### Services & Portar

| Service | Port | Beskrivning |
|---------|------|-------------|
| Backend | 8080 | Spring Boot REST API |
| Frontend | 3000 | React development server |
| PostgreSQL | 5432 | Databas |
| Redis | 6379 | Cache |
| RabbitMQ | 5672 | Message broker |
| RabbitMQ Management | 15672 | Web UI (admin/admin123) |
| Email-Service | 8081 | Email microservice |

## API Endpoints

### Authentication (Public)

#### Registrera användare
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "myuser",
    "email": "user@example.com",
    "password": "securepass123"
  }'
```

**Response:**
```json
{
  "message": "User registered successfully. Please check your email to verify your account.",
  "username": "myuser",
  "email": "user@example.com"
}
```

#### Logga in
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "myuser",
    "password": "securepass123"
  }'
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "user": {
    "id": 1,
    "username": "myuser",
    "email": "user@example.com",
    "roles": ["ROLE_USER"]
  },
  "message": "Login successful"
}
```

#### Email-verifiering
```bash
# Klicka på länk i email eller:
curl http://localhost:8080/api/auth/verify?token=<verification-token>
```

### User Endpoints (JWT Required)

#### Favoriter
```bash
# Hämta mina favoriter
curl http://localhost:8080/favorites \
  -H "Authorization: Bearer $TOKEN"

# Lägg till favorit
curl -X PUT http://localhost:8080/favorites/Stockholm \
  -H "Authorization: Bearer $TOKEN"

# Ta bort favorit
curl -X DELETE http://localhost:8080/favorites/Stockholm \
  -H "Authorization: Bearer $TOKEN"
```

#### Väderdata
```bash
# Väder för favoritplats (cachad 5 min)
curl http://localhost:8080/weather/Stockholm \
  -H "Authorization: Bearer $TOKEN"

# Sök valfri plats
curl http://localhost:8080/weather/weatherAtLocation/London \
  -H "Authorization: Bearer $TOKEN"

# 7-dagars prognos
curl http://localhost:8080/forecast/Stockholm \
  -H "Authorization: Bearer $TOKEN"
```

### Admin Endpoints (JWT + ADMIN Role)
```bash
# Lista alla alerts
curl http://localhost:8080/admin/alerts \
  -H "Authorization: Bearer $ADMIN_TOKEN"

# Skapa alert
curl -X POST http://localhost:8080/admin/alerts \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "placeName": "Stockholm",
    "alertType": "TEMPERATURE",
    "threshold": 30.0,
    "operator": "GREATER_THAN"
  }'
```

## Frontend Features

### Sidor

- **`/`** - Hemsida med väder-sökning
- **`/login`** - Login-formulär
- **`/register`** - Registreringsformulär
- **`/favorites`** - Favoritplatser
- **`/forecast`** - 7-dagars väderprognos

### Komponenter

- **Navbar** - Visar Login/Register eller Username/Logout baserat på auth-status
- **LoginPage** - JWT-autentisering med localStorage
- **RegisterPage** - Skapar användare + skickar verifikationsmail
- **Logout** - Rensar localStorage och navigerar till login

### JWT Hantering
```typescript
// src/utils/api.ts
const getHeaders = () => {
  const token = localStorage.getItem('jwt_token');
  return {
    'Content-Type': 'application/json',
    ...(token && { Authorization: `Bearer ${token}` }),
  };
};
```

## Email Verification System

### Flow

1. **Registrering** → Backend skapar user + verification token (UUID)
2. **RabbitMQ** → Backend publicerar meddelande till `verification_email` queue
3. **Email-Service** → Konsumerar meddelande från queue
4. **Gmail SMTP** → Email-service skickar mail med verifieringslänk
5. **Användare** → Klickar på länk i email
6. **Backend** → Validerar token, sätter `enabled=true`

### RabbitMQ Message Format
```json
{
  "email": "user@example.com",
  "username": "myuser",
  "verificationUrl": "http://localhost:8080/api/auth/verify?token=<uuid>"
}
```

### Gmail Configuration

Email-service kräver Gmail App Password (inte vanligt lösenord):

1. Gå till https://myaccount.google.com/apppasswords
2. Skapa app password för "WeatherApp"
3. Lägg till i `.env`:
```
EMAIL_USERNAME=your-email@gmail.com
EMAIL_PASSWORD=xxxx xxxx xxxx xxxx
```

## Databasschema

### Users Table
```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN DEFAULT false,
    verification_token VARCHAR(255),
    verification_token_expiry TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### User_Roles Table
```sql
CREATE TABLE user_roles (
    user_id BIGINT REFERENCES users(id),
    role VARCHAR(20) NOT NULL,
    PRIMARY KEY (user_id, role)
);
```

### Places Table
```sql
CREATE TABLE places (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    latitude DECIMAL(9,6) NOT NULL,
    longitude DECIMAL(9,6) NOT NULL
);
```

### User_Favorites Table
```sql
CREATE TABLE user_favorites (
    user_id BIGINT REFERENCES users(id),
    place_id BIGINT REFERENCES places(id),
    PRIMARY KEY (user_id, place_id)
);
```

## Lokal Development

### Backend
```bash
cd weather-service-auth
./mvnw spring-boot:run
```

### Frontend
```bash
cd weather-frontend
npm install
npm start
```

### Email-Service
```bash
cd email-service
./mvnw spring-boot:run
```

## Testing

### Komplett Test Scenario
```bash
# 1. Registrera användare
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","email":"test@example.com","password":"test123"}'

# 2. Kolla email och klicka på verifieringslänk (optional)

# 3. Logga in och spara token
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"test123"}' \
  | jq -r '.token')

# 4. Lägg till favorit
curl -X PUT http://localhost:8080/favorites/Stockholm \
  -H "Authorization: Bearer $TOKEN"

# 5. Hämta väderdata
curl http://localhost:8080/weather/Stockholm \
  -H "Authorization: Bearer $TOKEN"

# 6. Se favoriter
curl http://localhost:8080/favorites \
  -H "Authorization: Bearer $TOKEN"
```

## Monitoring & Logs
```bash
# Visa alla container logs
docker-compose logs

# Följ specifik service
docker-compose logs -f backend
docker-compose logs -f email-service

# RabbitMQ Management UI
http://localhost:15672
# Login: admin / admin123

# Kolla databas
docker exec -it weather-postgres psql -U admin -d weatherdb
```

## Troubleshooting

### Email skickas inte

1. Kolla email-service logs:
```bash
docker-compose logs email-service
```

2. Verifiera Gmail App Password i `.env`
3. Kontrollera RabbitMQ connection:
```bash
docker-compose logs rabbitmq
```

### JWT Authentication fails

1. Kolla att token finns i localStorage (DevTools → Application)
2. Verifiera `Authorization: Bearer <token>` header
3. Kontrollera token expiry (default 24h)

### Database connection issues
```bash
# Restart PostgreSQL
docker-compose restart postgres

# Kolla logs
docker-compose logs postgres
```

## Projektstruktur
```
weather-service-auth/
├── src/main/java/com/grupp3/weather/
│   ├── config/          # Security, Redis, RabbitMQ config
│   ├── controller/      # REST endpoints
│   ├── dto/             # Data Transfer Objects
│   ├── entity/          # JPA entities
│   ├── repository/      # Database repositories
│   ├── security/        # JWT filter, UserDetailsService
│   └── service/         # Business logic
├── docker-compose.yml   # Multi-container setup
└── .env                 # Environment variables

weather-frontend/
├── src/
│   ├── components/      # React components
│   ├── pages/           # Route pages
│   ├── types/           # TypeScript types
│   └── utils/           # API helpers
└── package.json

email-service/
├── src/main/java/com/grupp3/email/
│   ├── EmailService.java    # Gmail SMTP
│   └── EmailConsumer.java   # RabbitMQ listener
└── pom.xml
```

## Kurskrav Uppfyllda

- **Github + Commits** - Strukturerade commits med feature branches
- **JWT Authentication** - Spring Security med JWT tokens
- **Docker Deployment** - Multi-container setup med PostgreSQL, Redis, RabbitMQ
- **Frontend Register/Login/Logout** - React med TypeScript
- **App-logik** - Väderdata med favoriter och cache
- **Permissions + Roles** - USER/ADMIN med tre säkerhetsnivåer
- **Säkerhet** - CSRF disabled för JWT, BCrypt password hashing, DTO-pattern
- **Felhantering** - GlobalExceptionHandler med logging
- **Microservice** - Email-service via RabbitMQ message queue
- **Email Verification** - Asynkron mail-skickning med Gmail SMTP

## Utvecklare

**Gustav Nyberg**  
Java-utvecklingsstudent, Stockholms Tekniska Institut  
JAVA24 Program, Examen VT 2026

## Licens

Skolprojekt - Stockholms Tekniska Institut
