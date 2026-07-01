# ShopKart Smart E-Commerce Order Management Platform

A microservices-based e-commerce backend built with Spring Boot, Apache Kafka, Docker, and PostgreSQL. Designed to demonstrate real-world distributed systems architecture including event-driven communication, service-to-service HTTP calls, and JWT authentication.

---

## Architecture Overview

```
shopkart/
├── clothwear/           → Auth Service        (port 8080)
├── product-service/     → Product Catalog     (port 8081)
├── cart-service/        → Cart Management     (port 8082)
├── order-service/       → Order Processing    (port 8083) [coming soon]
├── payment-service/     → Payment Simulation  (port 8084) [coming soon]
├── inventory-service/   → Stock Management    (port 8085) [coming soon]
├── notification-service/→ Alerts & Emails     (port 8086) [coming soon]
├── analytics-service/   → AI Dashboard        (port 8087) [coming soon]
└── docker-compose.yml   → PostgreSQL + Kafka + Zookeeper
```

---

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 21, Spring Boot 4.x |
| Database | PostgreSQL 15 (schema-per-service) |
| Messaging | Apache Kafka + Zookeeper |
| Auth | Spring Security + JWT (jjwt 0.12.3) |
| Password | BCrypt hashing |
| HTTP Client | Spring RestClient (service-to-service) |
| Build | Maven 3.9 |
| Infrastructure | Docker + Docker Compose |
| Frontend | React + Vite (coming soon) |
| Cloud | AWS EC2, RDS, S3 (coming soon) |

---

## Services Built

### Auth Service — `clothwear` (port 8080)
Handles user registration and login using JWT tokens.

**Endpoints:**
```
POST /api/auth/register   → register a new user
POST /api/auth/login      → login and receive JWT token
GET  /api/auth/health     → health check
```

**Example register request:**
```json
{
  "name": "Chaitanya",
  "email": "chaitanya@test.com",
  "password": "password123"
}
```

---

### Product Service (port 8081)
Serves a catalog of 240 real fashion products (jeans, shirts, lehenga, dresses, tops) with USD pricing. Products are auto-loaded from JSON files on startup using a `CommandLineRunner`.

**Endpoints:**
```
GET  /api/products                        → all 240 products
GET  /api/products/{id}                   → single product
GET  /api/products/category/{category}    → filter by category
GET  /api/products/search?keyword=jeans   → search by keyword
PUT  /api/products/{id}/price             → update price (admin)
GET  /api/products/health                 → health check
```

**Key design decisions:**
- Prices stored as `BigDecimal` (never `double`) for accuracy
- Each product loaded from 6 JSON files (men_jeans, men_shirt, LenghaCholi, women_dress, women_jeans, women_top)
- INR prices converted to USD at ₹83.5 per dollar
- Uses its own `product_schema` in PostgreSQL — isolated from other services

---

### Cart Service (port 8082)
Manages shopping cart items. Demonstrates **service-to-service HTTP communication** — Cart Service calls Product Service over HTTP using Spring `RestClient` to fetch live prices instead of storing them locally.

**Endpoints:**
```
POST   /api/cart/add                          → add item to cart
GET    /api/cart/{userEmail}                  → get full cart with subtotal
DELETE /api/cart/{userEmail}/item/{productId} → remove one item
DELETE /api/cart/{userEmail}/clear            → clear entire cart
GET    /api/cart/health                       → health check
```

**Example add-to-cart request:**
```json
{
  "userEmail": "chaitanya@test.com",
  "productId": 1,
  "quantity": 2
}
```

**Example cart response:**
```json
{
  "items": [
    {
      "productId": 1,
      "title": "Men Regular Mid Rise Black Jeans",
      "brand": "LAHEJA",
      "price": 7.17,
      "quantity": 2,
      "lineTotal": 14.34,
      "currency": "USD"
    }
  ],
  "subtotal": 14.34,
  "totalItems": 2,
  "currency": "USD"
}
```

---

## Microservice Communication Patterns

This project uses three communication patterns:

**1. Synchronous HTTP (RestClient)**
Cart Service → Product Service: when adding to cart, Cart Service calls Product Service's REST API to validate the product exists and fetch its current price.

**2. Asynchronous Events (Kafka) — coming with Order Service**
Order Service will publish to `order.placed` Kafka topic. Payment, Inventory, and Notification services will independently consume and react — without Order Service waiting for any of them.

**3. Schema isolation (shared Postgres, separate schemas)**
All services share one PostgreSQL container but each owns its own schema: `public` (auth), `product_schema`, `cart_schema`. This isolates data ownership while keeping local setup simple.

---

## Kafka Topics (planned)

| Topic | Producer | Consumers |
|---|---|---|
| `order.placed` | Order Service | Payment, Inventory, Notification |
| `payment.success` | Payment Service | Notification, Order |
| `payment.failed` | Payment Service | Notification, Order |
| `inventory.updated` | Inventory Service | Analytics |

---

## Getting Started

### Prerequisites
- Java 21+
- Maven 3.9+
- Docker Desktop

### 1. Start infrastructure
```bash
git clone https://github.com/Chaitanya1819/shopkart.git
cd shopkart
docker-compose up -d
```

This starts PostgreSQL (port 5432), Kafka (port 9092), and Zookeeper (port 2181).

### 2. Create database schemas
```bash
docker exec -it shopkart-postgres psql -U shopkart -d shopkart
```
```sql
CREATE SCHEMA IF NOT EXISTS product_schema;
CREATE SCHEMA IF NOT EXISTS cart_schema;
\q
```

### 3. Run Auth Service
```bash
cd clothwear
mvn spring-boot:run
```

### 4. Run Product Service
```bash
cd product-service
mvn spring-boot:run
```
Automatically loads 240 products on first startup.

### 5. Run Cart Service
```bash
cd cart-service
mvn spring-boot:run
```

---

## Project Status

| Service | Status | Port |
|---|---|---|
| Auth Service | Complete | 8080 |
| Product Service | Complete | 8081 |
| Cart Service | Complete | 8082 |
| Order Service | In Progress | 8083 |
| Payment Service | Planned | 8084 |
| Inventory Service | Planned | 8085 |
| Notification Service | Planned | 8086 |
| AI Analytics Service | Planned | 8087 |
| React Frontend | Planned | 5173 |
| AWS Deployment | Planned | — |

---

## Author

**Chaitanya Bejjanki**
GitHub: [@Chaitanya1819](https://github.com/Chaitanya1819)
