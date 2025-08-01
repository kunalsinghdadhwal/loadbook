#!/bin/bash

# LoadBook Application Startup Script

echo "🚀 Starting LoadBook Application..."

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "❌ Java is not installed. Please install Java 17 or higher."
    exit 1
fi

# Check Java version
JAVA_VERSION=$(java -version 2>&1 | grep -oP 'version ".*?"' | grep -oP '\d+')
if [ "$JAVA_VERSION" -lt 17 ]; then
    echo "❌ Java version must be 17 or higher. Current version: $JAVA_VERSION"
    exit 1
fi

echo "✅ Java version: $JAVA_VERSION"

# Check if PostgreSQL is running
if ! command -v psql &> /dev/null; then
    echo "⚠️  PostgreSQL client not found. Make sure PostgreSQL is installed and running."
fi

# Set environment variables
export SPRING_PROFILES_ACTIVE=dev
export SPRING_DATASOURCE_URL=${SPRING_DATASOURCE_URL:-jdbc:postgresql://localhost:5432/loadbook}
export SPRING_DATASOURCE_USERNAME=${SPRING_DATASOURCE_USERNAME:-loadbook_user}
export SPRING_DATASOURCE_PASSWORD=${SPRING_DATASOURCE_PASSWORD:-loadbook_password}

echo "📊 Database URL: $SPRING_DATASOURCE_URL"

# Build the application if needed
if [ ! -f "build/libs/loadbook-0.0.1-SNAPSHOT.jar" ]; then
    echo "🔨 Building application..."
    ./gradlew build
    
    if [ $? -ne 0 ]; then
        echo "❌ Build failed. Please check the errors above."
        exit 1
    fi
fi

echo "🏃 Starting LoadBook application..."
echo "📖 API Documentation will be available at: http://localhost:8080/swagger-ui.html"
echo "🔍 Health check available at: http://localhost:8080/actuator/health"

# Start the application
java -jar build/libs/loadbook-0.0.1-SNAPSHOT.jar

echo "👋 LoadBook application stopped."
