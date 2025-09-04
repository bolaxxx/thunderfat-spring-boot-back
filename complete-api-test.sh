#!/bin/bash

echo "====================================================="
echo "ThunderFat API Complete Test Script"
echo "====================================================="

# Base URL for the API
BASE_URL="http://localhost:8080"
AUTH_URL="$BASE_URL/api/auth/login"

# Test user credentials
EMAIL="admin@thunderfat.com"
PASSWORD="password"

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[0;33m'
NC='\033[0m' # No Color

echo -e "\n${YELLOW}Step 1: Testing Authentication${NC}"
echo "POST $AUTH_URL"
AUTH_RESPONSE=$(curl -s -X POST "$AUTH_URL" \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"$EMAIL\",\"password\":\"$PASSWORD\"}")

# Extract the token
TOKEN=$(echo $AUTH_RESPONSE | grep -o '"accessToken":"[^"]*"' | awk -F'"' '{print $4}')
TOKEN_TYPE=$(echo $AUTH_RESPONSE | grep -o '"tokenType":"[^"]*"' | awk -F'"' '{print $4}')

if [ -z "$TOKEN" ]; then
  echo -e "${RED}Authentication failed. Response:${NC}"
  echo "$AUTH_RESPONSE"
  exit 1
else
  echo -e "${GREEN}Authentication successful!${NC}"
  echo "Token Type: $TOKEN_TYPE"
  echo "Token: ${TOKEN:0:20}..."
fi

echo -e "\n${YELLOW}Step 2: Testing Public API Endpoints${NC}"
echo "GET $BASE_URL/api/info"
curl -s "$BASE_URL/api/info" | head -c 500
echo -e "\n"

echo -e "\n${YELLOW}Step 3: Testing Protected API Endpoints${NC}"
echo -e "\n${YELLOW}3.1: GET All Alimentos${NC}"
echo "GET $BASE_URL/alimentos"
RESPONSE=$(curl -s -w "\nHTTP Status: %{http_code}" \
  -H "Authorization: $TOKEN_TYPE $TOKEN" \
  "$BASE_URL/alimentos")
echo "$RESPONSE"

# If we got a 401, try another approach with Bearer prefix
if [[ "$RESPONSE" == *"HTTP Status: 401"* ]]; then
  echo -e "\n${YELLOW}Retrying with explicit Bearer prefix...${NC}"
  RESPONSE=$(curl -s -w "\nHTTP Status: %{http_code}" \
    -H "Authorization: Bearer $TOKEN" \
    "$BASE_URL/alimentos")
  echo "$RESPONSE"
fi

echo -e "\n${YELLOW}3.2: GET Alimento by ID${NC}"
echo "GET $BASE_URL/alimentos/1"
RESPONSE=$(curl -s -w "\nHTTP Status: %{http_code}" \
  -H "Authorization: Bearer $TOKEN" \
  "$BASE_URL/alimentos/1")
echo "$RESPONSE"

echo -e "\n${YELLOW}3.3: Testing with curl verbose output to debug headers${NC}"
echo "GET $BASE_URL/alimentos"
curl -v -X GET "$BASE_URL/alimentos" \
  -H "Authorization: Bearer $TOKEN"
echo -e "\n"

echo -e "\n${YELLOW}Step 4: Testing API with different authorization format${NC}"
# Try without spaces in the token (some servers are picky about whitespace)
echo "GET $BASE_URL/alimentos (compact token)"
curl -v -X GET "$BASE_URL/alimentos" \
  -H "Authorization:Bearer$TOKEN"
echo -e "\n"

echo -e "\n${YELLOW}Step 5: Final direct test of /api/auth/login endpoint${NC}"
curl -v -X POST "$AUTH_URL" \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"$EMAIL\",\"password\":\"$PASSWORD\"}" | head -c 500
echo -e "\n"

echo -e "\n${GREEN}All tests completed!${NC}"
echo "====================================================="
