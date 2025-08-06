# LoadBook - Load & Booking Management System

LoadBook is a comprehensive backend system built with Spring Boot and PostgreSQL for managing load and booking operations in logistics. The system provides a robust API-first architecture designed for scalability, performance, and maintainability.

## Overview

This application serves as a complete logistics management platform that handles load creation, booking requests, and automated status workflows. It features advanced filtering capabilities, comprehensive business rule enforcement, and production-ready containerization with PostgreSQL integration.

## Key Features

- **Load Management**: Complete CRUD operations with advanced filtering and pagination support
- **Booking Management**: Automated booking workflows with status transitions and business validation
- **Status Management**: Rule-based status transitions with comprehensive business logic enforcement
- **API Documentation**: Interactive Swagger/OpenAPI 3 documentation with complete endpoint coverage
- **Testing Coverage**: Comprehensive test suite including unit, integration, and controller tests
- **Exception Handling**: Global exception management with structured error responses
- **Database Design**: Normalized PostgreSQL schema with proper relationships and constraints
- **Container Support**: Production-ready Docker configuration with PostgreSQL integration

## Technology Stack

- **Framework**: Spring Boot 3.5.4
- **Database**: PostgreSQL 15 (H2 for testing environments)
- **Runtime**: Java 17
- **Build System**: Gradle 7.x
- **Documentation**: SpringDoc OpenAPI 3
- **Testing**: JUnit 5, Mockito, Spring Boot Test
- **Containerization**: Docker & Docker Compose

## Quick Start

### Prerequisites

- Java 17 or higher
- Docker and Docker Compose
- Git

### Docker Deployment

1. Clone and build the application:
```bash
git clone <repository-url>
cd loadbook
./gradlew build
```

2. Start the services:
```bash
# Development environment
docker-compose up -d

# Production environment
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d
```

3. Access the application:
   - **API Base**: http://localhost:8080
   - **Swagger UI**: http://localhost:8080/swagger-ui.html
   - **API Docs**: http://localhost:8080/api-docs
   - **Health Check**: http://localhost:8080/actuator/health

### Local Development

1. Configure PostgreSQL database:
```sql
CREATE DATABASE loadbook;
CREATE USER loadbook_user WITH PASSWORD 'loadbook_password';
GRANT ALL PRIVILEGES ON DATABASE loadbook TO loadbook_user;
```

2. Configure application properties in `src/main/resources/application-dev.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/loadbook
spring.datasource.username=loadbook_user
spring.datasource.password=loadbook_password
```

3. Run the application:
```bash
./gradlew bootRun --args='--spring.profiles.active=dev'
```

## API Specifications

### Load Entity Structure
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
## Data Models

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
  "noOfTrucks": "integer",
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

## API Reference

### Load Operations

**Create Load**
```http
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

**Query Loads**
```http
GET /api/v1/load?shipperId=SHIPPER_001&truckType=Container&status=POSTED&page=0&size=10
```

**Get Load Details**
```http
GET /api/v1/load/{loadId}
```

**Update Load**
```http
PUT /api/v1/load/{loadId}
Content-Type: application/json
```

**Delete Load**
```http
DELETE /api/v1/load/{loadId}
```

### Booking Operations

**Create Booking Request**
```http
POST /api/v1/booking
Content-Type: application/json

{
  "loadId": "load-uuid-here",
  "transporterId": "TRANSPORTER_001", 
  "proposedRate": 25000.50,
  "comment": "Can deliver within 3 days"
}
```

**Query Bookings**
```http
GET /api/v1/booking?loadId=load-uuid&transporterId=TRANSPORTER_001&status=PENDING&page=0&size=10
```

**Accept Booking**
```http
PATCH /api/v1/booking/{bookingId}/accept
```

**Reject Booking**
```http
PATCH /api/v1/booking/{bookingId}/reject
```

## Development and Testing

### Test Execution
```bash
# Execute all tests
./gradlew test

# Run specific test categories
./gradlew test --tests "com.kunal.loadbook.service.*"          # Unit tests
./gradlew test --tests "com.kunal.loadbook.integration.*"     # Integration tests
./gradlew test --tests "com.kunal.loadbook.controller.*"      # Controller tests
```

### Test Coverage
The application maintains comprehensive test coverage across multiple layers:
- **Unit Tests**: Service layer business logic validation
- **Integration Tests**: End-to-end API workflow testing  
- **Controller Tests**: HTTP endpoint behavior verification
- **Repository Tests**: Database interaction validation

## Database Architecture

### Schema Design
- **loads**: Primary table storing load information with embedded facility details
- **bookings**: Booking requests table with foreign key relationship to loads

### Entity Relationships
- **One-to-Many**: Load → Bookings (1:N relationship)
- **Foreign Key**: `bookings.load_id` references `loads.id` with cascade rules

### Data Integrity
- Database-level constraints and validations
- Application-layer business rule enforcement
- Automated status transition management with audit trails

## Business Logic

### Load Lifecycle Management
- **Initial State**: New loads default to POSTED status
- **State Constraints**: BOOKED loads cannot be modified or deleted
- **Valid Transitions**: POSTED → BOOKED → CANCELLED
- **Terminal States**: CANCELLED loads cannot transition to other statuses

### Booking Workflow Rules
- **Availability Check**: Cannot create bookings for CANCELLED loads
- **Uniqueness Constraint**: One booking per transporter per load
- **Status Cascade**: Accepting a booking changes load status to BOOKED
- **Automatic Rejection**: Accepting one booking rejects all other pending bookings
- **Protection**: ACCEPTED bookings cannot be deleted
- **State Reversion**: If all bookings are removed/rejected, load reverts to POSTED

## HTTP API Standards

### Response Codes
- **200 OK**: Successful GET, PUT, PATCH operations
- **201 Created**: Successful POST operations with resource creation
- **204 No Content**: Successful DELETE operations
- **400 Bad Request**: Input validation errors or business rule violations
- **404 Not Found**: Requested resource does not exist
- **500 Internal Server Error**: Unexpected system errors

### Error Response Format
```json
{
  "timestamp": "2025-08-06T12:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed for field 'weight'",
  "path": "/api/v1/load"
}
```

## Deployment and Operations

### Container Orchestration
```bash
# Development deployment
docker-compose up -d

# Production deployment with optimized settings
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d

# Service management
docker-compose logs -f loadbook-app    # View application logs
docker-compose logs -f postgres        # View database logs
docker-compose down                     # Stop all services
```

### Environment Management
The application supports multiple deployment profiles:
- **Development**: `application-dev.properties` - H2 in-memory database
- **Testing**: `application-test.properties` - Isolated test configurations
- **Docker**: `application-docker.properties` - Containerized PostgreSQL setup
- **Production**: Environment variables via docker-compose.prod.yml

### Monitoring and Observability
- **Health Check**: `/actuator/health` - Service health status
- **Metrics**: `/actuator/metrics` - Application performance metrics  
- **Info**: `/actuator/info` - Build and version information
- **Docker Health**: Built-in container health checks for application and database

## Performance and Scalability

### Application Optimization
- **Stateless Design**: Enables horizontal scaling across multiple instances
- **Connection Pooling**: HikariCP with optimized pool settings
- **Query Optimization**: JPA criteria queries with proper indexing strategy
- **Pagination**: Built-in support for large dataset handling

### Database Optimization
- **Connection Management**: Configured connection pooling parameters
- **Query Performance**: Proper indexing on frequently queried columns
- **Transaction Management**: Optimized transaction boundaries
- **Resource Limits**: Memory and CPU constraints for production deployment

## Security Considerations

### Data Protection
- **Input Validation**: Comprehensive validation using Bean Validation annotations
- **SQL Injection Prevention**: Parameterized queries through JPA/Hibernate
- **Error Handling**: Structured error responses without sensitive information exposure

### Infrastructure Security
- **Network Isolation**: Docker network segregation for service communication
- **Environment Variables**: Externalized configuration for sensitive data
- **Health Check Security**: Non-sensitive endpoint exposure for monitoring

## System Requirements

### Development Environment
- **Java Runtime**: OpenJDK 17 or higher
- **Build Tool**: Gradle 7.x (wrapper included)
- **Database**: PostgreSQL 12+ (H2 for testing)
- **Container Runtime**: Docker 20.10+ and Docker Compose 2.0+

### Production Environment
- **CPU**: Minimum 2 cores (4 cores recommended)
- **Memory**: Minimum 2GB RAM (4GB recommended)
- **Storage**: SSD with 20GB+ available space
- **Network**: Reliable internet connectivity for image pulls
- **Database**: Dedicated PostgreSQL instance with connection pooling

## Support Information

For technical support, bug reports, or system questions:
- **Contact**: kunalsinghdadhwal@gmail.com
- **Repository**: https://github.com/kunalsinghdadhwal/loadbook
- **Documentation**: Interactive API documentation available at `/swagger-ui.html`

## License

This project is licensed under the MIT License. See the LICENSE file for complete terms and conditions.
