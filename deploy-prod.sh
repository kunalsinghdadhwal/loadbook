#!/bin/bash

# LoadBook Production Deployment Script

set -e

echo "Starting LoadBook production deployment..."

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "Error: Docker is not running. Please start Docker first."
    exit 1
fi

# Build the application
echo "Building application..."
./gradlew clean build -x test

# Pull latest base images
echo "Pulling latest Docker images..."
docker-compose -f docker-compose.yml -f docker-compose.prod.yml pull postgres

# Build application image
echo "Building application image..."
docker-compose -f docker-compose.yml -f docker-compose.prod.yml build loadbook-app

# Start services with production configuration
echo "Starting services..."
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d

# Wait for services to be healthy
echo "Waiting for services to become healthy..."
sleep 30

# Check service health
echo "Checking service health..."
if docker-compose -f docker-compose.yml -f docker-compose.prod.yml ps | grep -q "unhealthy"; then
    echo "Warning: Some services are not healthy. Check logs:"
    docker-compose -f docker-compose.yml -f docker-compose.prod.yml logs --tail=50
else
    echo "All services are running healthy!"
    echo ""
    echo "Application URLs:"
    echo "  API Base: http://localhost:8080"
    echo "  Health Check: http://localhost:8080/actuator/health"
    echo "  Swagger UI: http://localhost:8080/swagger-ui.html"
    echo ""
    echo "To view logs: docker-compose -f docker-compose.yml -f docker-compose.prod.yml logs -f"
    echo "To stop services: docker-compose -f docker-compose.yml -f docker-compose.prod.yml down"
fi
