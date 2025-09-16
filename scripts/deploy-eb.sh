#!/bin/bash
# ThunderFat Elastic Beanstalk Deployment Script

# Configuration
APP_NAME="ThunderFat-API"
ENV_NAME="ThunderFatApiProd"
S3_BUCKET="thunderfat-deployments"
REGION="us-west-1"  # Update this to your AWS region

# Colors for output
GREEN="\033[0;32m"
YELLOW="\033[1;33m"
RED="\033[0;31m"
RESET="\033[0m"

# Check if a version was provided
if [ -z "$1" ]; then
  echo -e "${RED}Error: Version label not provided${RESET}"
  echo "Usage: $0 <version-label> [description]"
  exit 1
fi

VERSION_LABEL=$1
DESCRIPTION=${2:-"Deployment of ThunderFat API $VERSION_LABEL"}

# Get current version from pom.xml
POM_VERSION=$(grep -m1 "<version>" pom.xml | sed 's/[[:space:]]*<version>\(.*\)<\/version>.*/\1/')
JAR_FILE="target/thunderfat-spring-boot-backend-${POM_VERSION}.jar"

echo -e "${YELLOW}Deploying ThunderFat API ${VERSION_LABEL}${RESET}"
echo "Using JAR: $JAR_FILE"
echo "Environment: $ENV_NAME"

# Build the application
echo -e "${YELLOW}Building application...${RESET}"
./mvnw clean package -DskipTests || { echo -e "${RED}Build failed${RESET}"; exit 1; }
echo -e "${GREEN}Build successful${RESET}"

# Upload to S3
echo -e "${YELLOW}Uploading to S3...${RESET}"
if aws s3 cp $JAR_FILE s3://$S3_BUCKET/; then
  echo -e "${GREEN}Upload successful${RESET}"
else
  echo -e "${RED}Upload failed${RESET}"
  exit 1
fi

# Create application version
echo -e "${YELLOW}Creating application version...${RESET}"
if aws elasticbeanstalk create-application-version \
  --application-name "$APP_NAME" \
  --version-label "$VERSION_LABEL" \
  --source-bundle S3Bucket="$S3_BUCKET",S3Key=$(basename $JAR_FILE) \
  --description "$DESCRIPTION" \
  --region $REGION; then
  echo -e "${GREEN}Application version created${RESET}"
else
  echo -e "${RED}Failed to create application version${RESET}"
  exit 1
fi

# Update environment
echo -e "${YELLOW}Updating environment...${RESET}"
if aws elasticbeanstalk update-environment \
  --application-name "$APP_NAME" \
  --environment-name "$ENV_NAME" \
  --version-label "$VERSION_LABEL" \
  --region $REGION; then
  echo -e "${GREEN}Environment update initiated${RESET}"
else
  echo -e "${RED}Failed to update environment${RESET}"
  exit 1
fi

echo -e "${GREEN}Deployment initiated successfully!${RESET}"
echo "Monitor status: https://$REGION.console.aws.amazon.com/elasticbeanstalk/home?region=$REGION#/environment/dashboard?applicationName=$APP_NAME&environmentId=$ENV_NAME"
