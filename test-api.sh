#!/bin/bash

echo "==========================================="
echo "ThunderFat 2025 Modernization Test Script"
echo "==========================================="

BASE_URL="http://localhost:8080"

echo ""
echo "1. Testing API Information..."
curl -s "$BASE_URL/api/info" | head -c 500
echo ""
echo ""

echo "2. Testing Available Endpoints..."
curl -s "$BASE_URL/api/endpoints" | head -c 500
echo ""
echo ""

echo "3. Testing Application Health..."
curl -s "$BASE_URL/api/test" | head -c 500
echo ""
echo ""

echo "4. Testing Authentication Health..."
curl -s "$BASE_URL/api/health/auth" | head -c 200
echo ""
echo ""

echo "5. Testing OAuth2 Well-Known Configuration..."
curl -s "$BASE_URL/.well-known/openid_configuration" | head -c 200
echo ""
echo ""

echo "6. Testing JWT Keys Endpoint..."
curl -s "$BASE_URL/.well-known/jwks.json" | head -c 200
echo ""
echo ""

echo "7. Testing Actuator Health (if available)..."
curl -s "$BASE_URL/actuator/health" | head -c 200
echo ""
echo ""

echo "==========================================="
echo "Test Complete! Check results above."
echo "==========================================="

echo ""
echo "To test authentication, use:"
echo "curl -X POST $BASE_URL/api/auth/login \\"
echo "  -H 'Content-Type: application/json' \\"
echo "  -d '{\"email\":\"your-email\",\"password\":\"your-password\"}'"
echo ""

echo "OAuth2 Authorization URL:"
echo "$BASE_URL/oauth2/authorize?client_id=angularapp&response_type=code&scope=openid%20profile&redirect_uri=http://localhost:4200/authorized"
