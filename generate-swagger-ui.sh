#!/bin/bash
SWAGGER_CODEGEN_CLI_VERSION="3.0.25"
SWAGGER_CODEGEN_JAR="swagger-codegen-cli-${SWAGGER_CODEGEN_CLI_VERSION}.jar"

# Download Swagger Codegen CLI
wget "https://repo1.maven.org/maven2/io/swagger/codegen/v3/swagger-codegen-cli/${SWAGGER_CODEGEN_CLI_VERSION}/${SWAGGER_CODEGEN_JAR}" -O ${SWAGGER_CODEGEN_JAR}

# Download the Swagger JSON file
wget "http://localhost:8080/api/v1/v3/api-docs" -O api-docs.json

# Generate Swagger UI static files
java -jar ${SWAGGER_CODEGEN_JAR} generate -i api-docs.json -l html2 -o ./swagger-ui
