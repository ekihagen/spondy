#!/bin/bash

echo "🔧 Setting up Java environment..."
export JAVA_HOME=$(/opt/homebrew/bin/brew --prefix openjdk@17)
export PATH="$JAVA_HOME/bin:$PATH"

echo "☕ Java version:"
java -version

echo "🚀 Starting backend with dev profile..."
cd backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev 