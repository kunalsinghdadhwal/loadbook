# LoadBook - Load & Booking Management System

A comprehensive backend system built with Spring Boot and PostgreSQL for managing Load & Booking operations efficiently. The system is optimized for performance, security, and scalability.

## 🚀 Features

- **Load Management**: Create, update, delete, and retrieve loads with comprehensive filtering and pagination
- **Booking Management**: Handle booking requests with status transitions and business rule validation
- **Status Management**: Automated status transitions based on business logic
- **API Documentation**: Comprehensive Swagger/OpenAPI documentation
- **Robust Testing**: High test coverage with unit, integration, and controller tests
- **Exception Handling**: Global exception handling with structured error responses
- **Database Design**: Normalized schema with foreign key relationships and constraints

## 📋 API Specifications

### Load Entity
```json
{
  "id": "UUID",
  "shipperId": "String",
  "facility": {
    "loadingPoint": "String",
    "unloadingPoint": "String", 
    "loadingDate": "Timestamp",
    "unloadingDate": "Timestamp"
  },
  "productType": "String",
  "truckType": "String",
  "noOfTrucks": "int",
  "weight": "double",
  "comment": "String",
  "datePosted": "Timestamp",
  "status": "POSTED | BOOKED | CANCELLED"
}
```

### Booking Entity
```json
{
  "id": "UUID",
  "loadId": "UUID",
  "transporterId": "String",
  "proposedRate": "double",
  "comment": "String",
  "status": "PENDING | ACCEPTED | REJECTED",
  "requestedAt": "Timestamp"
}
```

## 🔧 Technology Stack

- **Backend**: Spring Boot 3.5.4
- **Database**: PostgreSQL (with H2 for testing)
- **JVM**: Java 17
- **Documentation**: SpringDoc OpenAPI 3
- **Testing**: JUnit 5, Mockito, Spring Boot Test
- **Build Tool**: Gradle

## 🛠️ Setup Instructions

### Prerequisites
- Java 17 or higher
- PostgreSQL 12 or higher
- Gradle 7.x or higher (or use the included wrapper)

### Database Setup
1. Install PostgreSQL and create a database:
```sql
CREATE DATABASE loadbook;
CREATE USER loadbook_user WITH PASSWORD 'loadbook_password';
GRANT ALL PRIVILEGES ON DATABASE loadbook TO loadbook_user;
```

### Application Setup
1. Clone the repository:
```bash
git clone <repository-url>
cd loadbook
```

2. Update database configuration in `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/loadbook
spring.datasource.username=loadbook_user
spring.datasource.password=loadbook_password
```

3. Build the application:
```bash
./gradlew build
```

4. Run the application:
```bash
./gradlew bootRun
```

The application will start on `http://localhost:8080`

## 📖 API Usage

### Access API Documentation
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/api-docs`

### Load Management APIs

#### Create Load
```bash
POST /api/v1/load
Content-Type: application/json

{
  "shipperId": "SHIPPER_001",
  "facility": {
    "loadingPoint": "Mumbai Port",
    "unloadingPoint": "Delhi Warehouse",
    "loadingDate": "2025-08-15T10:00:00",
    "unloadingDate": "2025-08-18T14:00:00"
  },
  "productType": "Electronics",
  "truckType": "Container",
  "noOfTrucks": 2,
  "weight": 15.5,
  "comment": "Handle with care"
}
```

#### Get Loads with Filtering
```bash
GET /api/v1/load?shipperId=SHIPPER_001&truckType=Container&status=POSTED&page=0&size=10
```

#### Get Load by ID
```bash
GET /api/v1/load/{loadId}
```

#### Update Load
```bash
PUT /api/v1/load/{loadId}
Content-Type: application/json

{
  "productType": "Updated Electronics",
  "comment": "Updated comment"
}
```

#### Delete Load
```bash
DELETE /api/v1/load/{loadId}
```

### Booking Management APIs

#### Create Booking
```bash
POST /api/v1/booking
Content-Type: application/json

{
  "loadId": "load-uuid-here",
  "transporterId": "TRANSPORTER_001",
  "proposedRate": 25000.50,
  "comment": "Can deliver within 3 days"
}
```

#### Get Bookings with Filtering
```bash
GET /api/v1/booking?loadId=load-uuid&transporterId=TRANSPORTER_001&status=PENDING&page=0&size=10
```

#### Accept/Reject Booking
```bash
PATCH /api/v1/booking/{bookingId}/accept
PATCH /api/v1/booking/{bookingId}/reject
```

## 🧪 Testing

### Run All Tests
```bash
./gradlew test
```

### Run Specific Test Types
```bash
# Unit tests
./gradlew test --tests "com.kunal.loadbook.service.*"

# Integration tests
./gradlew test --tests "com.kunal.loadbook.integration.*"

# Controller tests
./gradlew test --tests "com.kunal.loadbook.controller.*"
```

### Test Coverage
The project maintains high test coverage (>60%) with:
- **Unit Tests**: Service layer business logic testing
- **Integration Tests**: End-to-end API testing
- **Controller Tests**: HTTP endpoint testing
- **Repository Tests**: Database interaction testing

## 📊 Database Schema

### Tables
- **loads**: Main load information with embedded facility details
- **bookings**: Booking requests linked to loads via foreign key

### Key Relationships
- One Load can have many Bookings (1:N)
- Foreign key constraint: `bookings.load_id` → `loads.id`

### Constraints
- Load status transitions: POSTED → BOOKED → CANCELLED
- Booking status transitions: PENDING → ACCEPTED/REJECTED
- Business rule validations through application logic

## 🔒 Business Rules

### Load Rules
- Status defaults to POSTED when created
- Cannot update/delete BOOKED loads
- Cannot transition from CANCELLED to any other status

### Booking Rules
- Cannot create booking for CANCELLED loads
- Only one booking per transporter per load
- When booking is accepted, load status changes to BOOKED
- Accepting a booking rejects all other pending bookings for that load
- Cannot delete ACCEPTED bookings
- If all bookings are deleted/rejected, load status reverts to POSTED

## 🚦 Status Codes

### HTTP Response Codes
- `200 OK`: Successful GET, PUT, PATCH requests
- `201 Created`: Successful POST requests
- `204 No Content`: Successful DELETE requests
- `400 Bad Request`: Validation errors, business rule violations
- `404 Not Found`: Resource not found
- `500 Internal Server Error`: Unexpected server errors

## 🔍 Monitoring & Health

### Health Check
```bash
GET /actuator/health
```

### Application Metrics
```bash
GET /actuator/metrics
```

## 📝 Assumptions

1. **Authentication**: No authentication/authorization implemented (can be added with Spring Security)
2. **Shipper/Transporter Management**: IDs are provided as strings (external system integration assumed)
3. **Rate Management**: Single proposed rate per booking (can be extended for negotiations)
4. **Notification System**: Not implemented (can be added with events/messaging)
5. **File Uploads**: Not supported (can be added for load documents)
6. **Audit Trail**: Basic timestamps provided (can be enhanced with Spring Data JPA Auditing)

## 🚀 Deployment

### Production Considerations
1. **Database**: Use PostgreSQL connection pooling (HikariCP included)
2. **Security**: Add Spring Security for authentication/authorization
3. **Monitoring**: Integrate with Prometheus/Grafana
4. **Logging**: Configure structured logging with Logback
5. **Performance**: Add caching with Redis
6. **Scaling**: Deploy with Docker/Kubernetes

### Docker Support
```dockerfile
FROM openjdk:17-jre-slim
COPY build/libs/loadbook-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## 📞 Support

For questions, issues, or contributions, please contact:
- **Email**: careers@cargopro.ai
- **GitHub**: [Repository Link]

## 📜 License

This project is licensed under the MIT License - see the LICENSE file for details.
