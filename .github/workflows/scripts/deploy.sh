#!/bin/bash

set -e  # stop if error

VERSION=$1

echo "Starting deployment for version: $VERSION"

# Build Docker Image
echo "Building Docker image..."
docker build -t $ACR_LOGIN_SERVER/springboot-app:$VERSION .

# Tag latest
docker tag $ACR_LOGIN_SERVER/springboot-app:$VERSION $ACR_LOGIN_SERVER/springboot-app:latest

# Push Image
echo "Pushing Docker image..."
docker push $ACR_LOGIN_SERVER/springboot-app:$VERSION
docker push $ACR_LOGIN_SERVER/springboot-app:latest

# Deploy to Azure
echo "Deploying to Azure..."
az webapp config container set \
  --name $APP_NAME \
  --resource-group $RESOURCE_GROUP \
  --docker-custom-image-name $ACR_LOGIN_SERVER/springboot-app:$VERSION \
  --docker-registry-server-url https://$ACR_LOGIN_SERVER

echo "Deployment completed!"