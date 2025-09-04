#!/bin/bash

echo "================================================="
echo "ThunderFat JWT Token Debug Test"
echo "================================================="

# Base URL for the API
BASE_URL="http://localhost:8080"
AUTH_URL="$BASE_URL/api/auth/login"

# Test user credentials
EMAIL="admin@thunderfat.com"
PASSWORD="password"

# Step 1: Get the JWT token with full response
echo "Step 1: Authenticating to get token..."
echo "POST $AUTH_URL"
curl -v -X POST "$AUTH_URL" \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"$EMAIL\",\"password\":\"$PASSWORD\"}" > auth_response.json

# Extract key elements
TOKEN=$(grep -o '"accessToken":"[^"]*"' auth_response.json | head -1 | cut -d'"' -f4)
TOKEN_TYPE=$(grep -o '"tokenType":"[^"]*"' auth_response.json | head -1 | cut -d'"' -f4)

if [ -z "$TOKEN" ]; then
  echo "Authentication failed."
  cat auth_response.json
  exit 1
else
  echo "Authentication successful!"
  echo "Token Type: $TOKEN_TYPE"
  echo "Token: ${TOKEN:0:20}..."
  
  # Save token for inspection
  echo "$TOKEN" > token.txt
  
  # Extract JWT parts for debugging
  HEADER=$(echo "$TOKEN" | cut -d'.' -f1)
  PAYLOAD=$(echo "$TOKEN" | cut -d'.' -f2)
  SIGNATURE=$(echo "$TOKEN" | cut -d'.' -f3)
  
  echo -e "\nJWT Header (base64):"
  echo "$HEADER"
  
  # Decode header if possible (may need base64 utility)
  if command -v base64 >/dev/null 2>&1; then
    echo -e "\nDecoded JWT Header:"
    echo "$HEADER" | base64 -d 2>/dev/null || echo "Could not decode (padding issue)"
  fi
fi

echo -e "\n\nStep 2: Try direct endpoint access with properly formatted Authorization header"
echo "GET $BASE_URL/api/info"
curl -v -X GET "$BASE_URL/api/info" \
  -H "Authorization: $TOKEN_TYPE $TOKEN" \
  2>&1 | grep -E '(< HTTP|Bearer)'

echo -e "\n\nStep 3: Check alimento endpoint with properly formatted token"
echo "GET $BASE_URL/alimentos"
curl -v -X GET "$BASE_URL/alimentos" \
  -H "Authorization: $TOKEN_TYPE $TOKEN" \
  2>&1 | grep -E '(< HTTP|Bearer)'

echo -e "\n\nStep 4: Check basic JWT validation"
echo "GET $BASE_URL/api/health/auth"
curl -v -X GET "$BASE_URL/api/health/auth" \
  -H "Authorization: $TOKEN_TYPE $TOKEN" \
  2>&1 | grep -E '(< HTTP|Bearer)'

echo -e "\n================================================="
echo "Test complete"
echo "================================================="
