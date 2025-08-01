# API Usage Guide

This document provides examples of how to use the Load & Booking Management System APIs.

## Base URL
```
http://localhost:8080
```

## API Documentation
- Swagger UI: http://localhost:8080/swagger-ui.html
- API Docs: http://localhost:8080/api-docs

## Authentication
Currently, the system does not implement authentication. All endpoints are publicly accessible.

## Load Management APIs

### 1. Create a Load
```bash
curl -X POST http://localhost:8080/api/loads \
  -H "Content-Type: application/json" \
  -d '{
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
  }'
```

### 2. Get All Loads (with pagination and filtering)
```bash
# Get all loads
curl "http://localhost:8080/api/loads"

# Get loads with filters
curl "http://localhost:8080/api/loads?shipperId=SHIPPER_001&status=POSTED&page=0&size=10"

# Get loads by truck type
curl "http://localhost:8080/api/loads?truckType=Container"
```

### 3. Get Load by ID
```bash
curl "http://localhost:8080/api/loads/{loadId}"
```

### 4. Update a Load
```bash
curl -X PUT http://localhost:8080/api/loads/{loadId} \
  -H "Content-Type: application/json" \
  -d '{
    "productType": "Updated Electronics",
    "weight": 16.0,
    "comment": "Updated comment"
  }'
```

### 5. Delete a Load
```bash
curl -X DELETE "http://localhost:8080/api/loads/{loadId}"
```

## Booking Management APIs

### 1. Create a Booking
```bash
curl -X POST http://localhost:8080/api/bookings \
  -H "Content-Type: application/json" \
  -d '{
    "loadId": "load-uuid-here",
    "transporterId": "TRANSPORTER_001",
    "proposedRate": 25000.50,
    "comment": "Can deliver within 3 days"
  }'
```

### 2. Get All Bookings (with filtering)
```bash
# Get all bookings
curl "http://localhost:8080/api/bookings"

# Get bookings by load ID
curl "http://localhost:8080/api/bookings?loadId=load-uuid-here"

# Get bookings by transporter
curl "http://localhost:8080/api/bookings?transporterId=TRANSPORTER_001"

# Get bookings by status
curl "http://localhost:8080/api/bookings?status=PENDING"
```

### 3. Get Booking by ID
```bash
curl "http://localhost:8080/api/bookings/{bookingId}"
```

### 4. Update a Booking
```bash
curl -X PUT http://localhost:8080/api/bookings/{bookingId} \
  -H "Content-Type: application/json" \
  -d '{
    "proposedRate": 24000.0,
    "comment": "Updated rate"
  }'
```

### 5. Accept a Booking
```bash
curl -X PUT "http://localhost:8080/api/bookings/{bookingId}/accept"
```

### 6. Reject a Booking
```bash
curl -X PUT "http://localhost:8080/api/bookings/{bookingId}/reject"
```

### 7. Delete a Booking
```bash
curl -X DELETE "http://localhost:8080/api/bookings/{bookingId}"
```

## Health Check
```bash
curl "http://localhost:8080/actuator/health"
```

## Error Responses

All error responses follow a consistent format:

```json
{
  "message": "Error description",
  "status": 400,
  "error": "Bad Request",
  "path": "/api/loads",
  "timestamp": "2025-08-01T10:30:00"
}
```

## Status Codes

- `200 OK` - Successful GET/PUT requests
- `201 Created` - Successful POST requests
- `204 No Content` - Successful DELETE requests
- `400 Bad Request` - Validation errors or business logic violations
- `404 Not Found` - Resource not found
- `500 Internal Server Error` - Unexpected server errors

## Business Rules

### Load Status Transitions
- New loads are created with status `POSTED`
- When a booking is accepted, load status changes to `BOOKED`
- When a load is deleted or all bookings are rejected, status changes to `CANCELLED`

### Booking Status Transitions
- New bookings are created with status `PENDING`
- Bookings can be updated to `ACCEPTED` or `REJECTED`
- Only one booking per load can be `ACCEPTED`

### Validation Rules
- Cannot create bookings for `CANCELLED` loads
- Cannot create duplicate bookings (same load + transporter)
- Cannot delete `ACCEPTED` bookings
- All numeric values must be positive
- All required fields must be provided
