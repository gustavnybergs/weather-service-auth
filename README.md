# Weather API – Grupp 3

Ett skolprojekt för att bygga ett weather API med Spring Boot, **JWT-autentisering**, databas och cache-funktionalitet.

## Teknisk Stack

- **Java 17+** med Spring Boot 3.5.5
- **Spring Security** med JWT-autentisering *(NYTT)*
- **PostgreSQL** för datalagring
- **Redis** för cache (5 minuters TTL)
- **Open-Meteo API** för väderdata (gratis tier)
- **Maven** för build management
- **jjwt 0.11.5** för JWT token hantering *(NYTT)*

## Säkerhet

### JWT Authentication *(NYTT)*

**VIKTIGT:** Alla endpoints utom `/api/auth/**` kräver nu JWT-autentisering!

#### 1. Registrera ny användare
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
  "message": "User registered successfully",
  "username": "myuser",
  "email": "user@example.com"
}
```

#### 2. Logga in och få JWT-token
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
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJteXVzZXIiLCJyb2xlcyI6IlJPTEVfVVNFUiIsImlhdCI6MTczMjAxNTIwMCwiZXhwIjoxNzMyMTAxNjAwfQ...",
  "message": "Login successful",
  "username": "myuser",
  "roles": ["ROLE_USER"]
}
```

#### 3. Använd token för alla andra requests *(ÄNDRAT)*
```bash
# Spara token
TOKEN="eyJhbGciOiJIUzI1NiJ9..."

# Alla requests kräver nu Authorization header
curl http://localhost:8080/weather/Stockholm \
  -H "Authorization: Bearer $TOKEN"
```

### API-key (för admin write-operationer)

Write-operationer till admin-endpoints kräver fortfarande både JWT OCH API-key:
```
Authorization: Bearer <token>
X-API-KEY: topsecret123
```

**ApiKeyFilter uppdaterad:** Tillåter nu `/api/auth/**` utan API-key för registrering/login. *(ÄNDRAT)*

### Rate limiting

Basic rate limiting (oförändrat):
- Weather endpoints: 30 requests/minut
- Admin endpoints: 10 requests/minut

## API Endpoints

### Authentication (Public - ingen JWT krävs)
```bash
POST /api/auth/register    # Registrera ny användare
POST /api/auth/login       # Logga in och få JWT-token
```

### User Endpoints *(NU MED JWT - ÄNDRAT)*

**OBS:** Alla dessa kräver nu `Authorization: Bearer <token>` header!

#### Favoriter
```bash
GET /favorites                 # Mina favoritplatser
PUT /favorites/{placeName}     # Markera som favorit
DELETE /favorites/{placeName}  # Ta bort favorit
```

#### Väderdata
```bash
GET /weather/{placeName}                    # Väder för favoritplats (cachad)
GET /weather/weatherAtLocation/{placeName}  # Sök väder för valfri plats
GET /weather/locationByName/{placeName}     # Hämta platsinfo
GET /forecast/{placeName}                   # 7-dagars prognos
```

#### Alerts
```bash
GET /alerts                    # Se alert-definitioner
```

### Admin Endpoints *(NU MED JWT + API-key - ÄNDRAT)*

**OBS:** Kräver både JWT-token OCH X-API-KEY header!

#### Alert Management
```bash
POST /admin/alerts            # Skapa alert
GET /admin/alerts             # Lista alerts
PUT /admin/alerts/{id}        # Uppdatera alert
DELETE /admin/alerts/{id}     # Ta bort alert
```

#### System
```bash
POST /admin/weather/update    # Manuell uppdatering
GET /admin/stats              # Systemstatistik
```

## Exempel

### Komplett workflow (registrera, logga in, använda API)
```bash
# 1. Registrera användare
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "testpass123"
  }'

# 2. Logga in och spara token
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "testpass123"
  }' | jq -r '.token')

echo "Token: $TOKEN"

# 3. Markera Stockholm som favorit (KRÄVER TOKEN NU)
curl -X PUT http://localhost:8080/favorites/Stockholm \
  -H "Authorization: Bearer $TOKEN"

# 4. Hämta väder (KRÄVER TOKEN NU)
curl http://localhost:8080/weather/Stockholm \
  -H "Authorization: Bearer $TOKEN"
```

### Sök efter ny plats *(UPPDATERAT MED JWT)*
```bash
# Sök väder för vilken plats som helst (KRÄVER TOKEN)
curl http://localhost:8080/weather/weatherAtLocation/Malmö \
  -H "Authorization: Bearer $TOKEN"
```

### Skapa alert (admin) *(UPPDATERAT MED JWT)*
```bash
curl -X POST http://localhost:8080/admin/alerts \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -H "X-API-KEY: <your-api-key>" \
  -d '{
    "name": "Kyla",
    "alertType": "temperature",
    "operator": "<",
    "thresholdValue": 0.0,
    "severity": "medium",
    "message": "Kallt väder"
  }'
```

## Funktioner

**JWT-autentisering:** Säker användarhantering med BCrypt-hashade lösenord och JWT-tokens (24h expiration)

**Rollbaserad åtkomst:** Stöd för ROLE_USER och ROLE_ADMIN (redo för framtida admin-funktioner)

**Automatisk uppdatering:** Hämtar väder för favoritplatser var 30:e minut

**Cache:** Redis cache för att minska API-anrop till Open-Meteo

**Alert system:** Admin kan definiera väderalerts som triggas automatiskt

**Sökning:** Använder Open-Meteo geocoding för att hitta platser

## Installation

### Förutsättningar
- Java 17+
- PostgreSQL
- Redis

### Database setup
```sql
CREATE DATABASE weather_db;
CREATE USER weather_user WITH PASSWORD 'weather_pass';
GRANT ALL PRIVILEGES ON DATABASE weather_db TO weather_user;
```

### Application Properties *(UPPDATERAT)*

Skapa `src/main/resources/application.properties`:
```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/weather_db
spring.datasource.username=weather_user
spring.datasource.password=weather_pass

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

# Redis
spring.data.redis.host=localhost
spring.data.redis.port=6379

# API Key (för admin endpoints)
app.api-key=topsecret123

# JWT (24 timmar) - NYTT
jwt.expiration=86400000
```

**OBS:** `users` och `user_roles` tabeller skapas automatiskt av Hibernate.

### Köra applikationen
```bash
# Starta PostgreSQL och Redis först
./mvnw spring-boot:run

# Går på http://localhost:8080
```

## Frontend

Projektet inkluderar också en React TypeScript frontend i `weather-frontend/` mappen:
```bash
cd weather-frontend
npm install
npm start
# Går på http://localhost:3000
```

**OBS:** Frontend behöver uppdateras för att hantera JWT-autentisering (framtida uppgift).

### Frontend funktioner
- Sök väder för valfri plats
- Lägg till/ta bort favoriter
- Visa 7-dagars prognoser för favoritplatser
- Responsiv design med Tailwind CSS

## Data Models

### User
```json
{
  "id": 1,
  "username": "testuser",
  "email": "test@example.com",
  "roles": ["ROLE_USER"],
  "enabled": true,
  "createdAt": "2025-11-19T12:00:00"
}
```

### JWT Login Response
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlciIsInJvbGVzIjoiUk9MRV9VU0VSIiwiaWF0IjoxNzMyMDE1MjAwLCJleHAiOjE3MzIxMDE2MDB9...",
  "message": "Login successful",
  "username": "testuser",
  "roles": ["ROLE_USER"]
}
```

### Weather Response (oförändrad)
```json
{
  "place": {
    "name": "Stockholm",
    "lat": 59.3293,
    "lon": 18.0686
  },
  "source": "open-meteo",
  "cached": false,
  "data": {
    "time": "2025-09-12T19:15",
    "temperature_2m": 15.4,
    "cloud_cover": 45,
    "wind_speed_10m": 19.1
  }
}
```

### Alert Definition (oförändrad)
```json
{
  "id": 1,
  "name": "Kyla",
  "alertType": "temperature",
  "operator": "<",
  "thresholdValue": 0.0,
  "severity": "medium",
  "message": "Kallt väder",
  "active": true
}
```

## Säkerhetsimplementation

### JWT Components
- **JwtUtil**: Genererar och validerar JWT-tokens med HS256 algorithm
- **JwtAuthenticationFilter**: Interceptar alla requests och validerar Bearer tokens
- **CustomUserDetailsService**: Laddar användare från database för Spring Security
- **SecurityConfig**: Konfigurerar security filter chain med stateless sessions

### Password Security
- **BCrypt hashing**: Alla lösenord hashas med BCrypt (cost factor 10)
- **UserService**: Hanterar användarregistrering med automatisk password hashing

### Multi-layer Security (oförändrad struktur, JWT tillagd)
1. **JwtAuthenticationFilter**: Validerar JWT på alla requests (NYTT)
2. **ApiKeyFilter**: Skyddar admin write-operationer (UPPDATERAT för /api/auth/**)
3. **DDoSProtectionFilter**: Rate limiting och bot-detection (oförändrad)

## Testing

Alla 44 tester kör och passerar:
```bash
./mvnw test
```
