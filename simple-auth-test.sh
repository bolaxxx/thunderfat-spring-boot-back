#!/bin/bash

echo "==================================================="
echo "ThunderFat Simple Authentication Test"
echo "==================================================="

# Base URL for the API
BASE_URL="http://localhost:8080"
AUTH_URL="$BASE_URL/api/auth/login"

# Test user credentials
EMAIL="admin@thunderfat.com"
PASSWORD="password"

# Step 1: Get the JWT token
echo "Step 1: Authenticating to get token..."
AUTH_RESPONSE=$(curl -s -X POST "$AUTH_URL" \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"$EMAIL\",\"password\":\"$PASSWORD\"}")

echo "Full authentication response:"
echo "$AUTH_RESPONSE"
echo

# Extract the token from the response
TOKEN=$(echo $AUTH_RESPONSE | grep -o '"accessToken":"[^"]*"' | cut -d'"' -f4)

if [ -z "$TOKEN" ]; then
  echo "Authentication failed. Could not extract token."
  exit 1
else
  echo "Authentication successful!"
  echo "Token: ${TOKEN:0:20}..." # Show just the beginning of the token
fi

# Save the token to a file for reference
echo "$TOKEN" > jwt_token.txt
echo "Token saved to jwt_token.txt"

echo "==================================================="
echo "Test complete!"
echo "==================================================="
