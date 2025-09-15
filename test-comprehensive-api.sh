#!/bin/bash

# =====================================================
# COMPREHENSIVE API TEST SCRIPT
# Tests all major endpoints with realistic dummy data
# =====================================================

BASE_URL="http://localhost:8080/api"
TOKEN=""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}======================================================"
echo -e "🚀 THUNDERFAT NUTRITION API COMPREHENSIVE TEST"
echo -e "======================================================${NC}"

# Function to test endpoint
test_endpoint() {
    local method=$1
    local endpoint=$2
    local description=$3
    local data=$4
    local expected_status=$5
    
    echo -e "\n${YELLOW}Testing: $description${NC}"
    echo -e "Endpoint: $method $endpoint"
    
    if [ "$method" = "GET" ]; then
        if [ -n "$TOKEN" ]; then
            response=$(curl -s -w "\n%{http_code}" -H "Authorization: Bearer $TOKEN" "$BASE_URL$endpoint")
        else
            response=$(curl -s -w "\n%{http_code}" "$BASE_URL$endpoint")
        fi
    elif [ "$method" = "POST" ]; then
        if [ -n "$TOKEN" ]; then
            response=$(curl -s -w "\n%{http_code}" -X POST -H "Content-Type: application/json" -H "Authorization: Bearer $TOKEN" -d "$data" "$BASE_URL$endpoint")
        else
            response=$(curl -s -w "\n%{http_code}" -X POST -H "Content-Type: application/json" -d "$data" "$BASE_URL$endpoint")
        fi
    fi
    
    status_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | head -n -1)
    
    if [ "$status_code" = "$expected_status" ]; then
        echo -e "${GREEN}✅ SUCCESS: $status_code${NC}"
    else
        echo -e "${RED}❌ FAILED: Expected $expected_status, got $status_code${NC}"
    fi
    
    # Pretty print JSON if response is JSON
    if echo "$body" | jq . >/dev/null 2>&1; then
        echo -e "Response: $(echo "$body" | jq -C '.')"
    else
        echo -e "Response: $body"
    fi
}

# =====================================================
# 1. AUTHENTICATION TESTS
# =====================================================
echo -e "\n${BLUE}🔐 AUTHENTICATION TESTS${NC}"

# Login as nutricionista
LOGIN_DATA='{
    "email": "dr.martinez@nutrihealth.com",
    "password": "password123"
}'

echo -e "\n${YELLOW}Logging in as Nutricionista...${NC}"
login_response=$(curl -s -w "\n%{http_code}" -X POST -H "Content-Type: application/json" -d "$LOGIN_DATA" "$BASE_URL/auth/login")
login_status=$(echo "$login_response" | tail -n1)
login_body=$(echo "$login_response" | head -n -1)

if [ "$login_status" = "200" ]; then
    TOKEN=$(echo "$login_body" | jq -r '.data.token' 2>/dev/null)
    echo -e "${GREEN}✅ Login successful! Token obtained.${NC}"
else
    echo -e "${RED}❌ Login failed: $login_status${NC}"
    echo "Response: $login_body"
fi

# =====================================================
# 2. USUARIO/USER MANAGEMENT TESTS
# =====================================================
echo -e "\n${BLUE}👥 USER MANAGEMENT TESTS${NC}"

test_endpoint "GET" "/usuarios" "Get all users" "" "200"
test_endpoint "GET" "/usuarios/1" "Get user by ID" "" "200"

# =====================================================
# 3. NUTRICIONISTA TESTS
# =====================================================
echo -e "\n${BLUE}👨‍⚕️ NUTRICIONISTA TESTS${NC}"

test_endpoint "GET" "/nutricionistas" "Get all nutritionists" "" "200"
test_endpoint "GET" "/nutricionistas/1" "Get nutritionist by ID" "" "200"
test_endpoint "GET" "/nutricionistas/1/pacientes" "Get nutritionist's patients" "" "200"

# =====================================================
# 4. PACIENTE TESTS
# =====================================================
echo -e "\n${BLUE}🏥 PATIENT TESTS${NC}"

test_endpoint "GET" "/pacientes" "Get all patients" "" "200"
test_endpoint "GET" "/pacientes/2" "Get patient by ID" "" "200"
test_endpoint "GET" "/pacientes/2/planes" "Get patient's diet plans" "" "200"
test_endpoint "GET" "/pacientes/2/mediciones/generales" "Get patient's general measurements" "" "200"
test_endpoint "GET" "/pacientes/2/mediciones/segmentales" "Get patient's segmental measurements" "" "200"
test_endpoint "GET" "/pacientes/2/antecedentes" "Get patient's medical history" "" "200"

# =====================================================
# 5. ALIMENTOS TESTS
# =====================================================
echo -e "\n${BLUE}🥗 FOOD DATABASE TESTS${NC}"

test_endpoint "GET" "/alimentos" "Get all foods" "" "200"
test_endpoint "GET" "/alimentos/1" "Get food by ID" "" "200"
test_endpoint "GET" "/alimentos/search?nombre=pollo" "Search foods by name" "" "200"

# =====================================================
# 6. PLATOS TESTS
# =====================================================
echo -e "\n${BLUE}🍽️ RECIPE TESTS${NC}"

test_endpoint "GET" "/platos" "Get all recipes" "" "200"
test_endpoint "GET" "/platos/1" "Get recipe by ID" "" "200"
test_endpoint "GET" "/platos/predeterminados" "Get predetermined recipes" "" "200"

# =====================================================
# 7. PLAN DIETA TESTS
# =====================================================
echo -e "\n${BLUE}📋 DIET PLAN TESTS${NC}"

test_endpoint "GET" "/planes" "Get all diet plans" "" "200"
test_endpoint "GET" "/planes/1" "Get diet plan by ID" "" "200"
test_endpoint "GET" "/planes/1/dias" "Get diet plan days" "" "200"

# =====================================================
# 8. CITAS TESTS
# =====================================================
echo -e "\n${BLUE}📅 APPOINTMENT TESTS${NC}"

test_endpoint "GET" "/citas" "Get all appointments" "" "200"
test_endpoint "GET" "/citas/1" "Get appointment by ID" "" "200"
test_endpoint "GET" "/citas/nutricionista/1" "Get appointments by nutritionist" "" "200"
test_endpoint "GET" "/citas/paciente/2" "Get appointments by patient" "" "200"

# =====================================================
# 9. CHAT TESTS
# =====================================================
echo -e "\n${BLUE}💬 CHAT SYSTEM TESTS${NC}"

test_endpoint "GET" "/chats" "Get all chats" "" "200"
test_endpoint "GET" "/chats/1" "Get chat by ID" "" "200"
test_endpoint "GET" "/chats/1/mensajes" "Get chat messages" "" "200"

# =====================================================
# 10. FILTROS ALIMENTARIOS TESTS
# =====================================================
echo -e "\n${BLUE}🍎 DIETARY FILTER TESTS${NC}"

test_endpoint "GET" "/filtros" "Get all dietary filters" "" "200"
test_endpoint "GET" "/filtros/1" "Get filter by ID" "" "200"

# =====================================================
# 11. POST TESTS (Creating new data)
# =====================================================
echo -e "\n${BLUE}📝 CREATE OPERATION TESTS${NC}"

# Create new patient
NEW_PATIENT='{
    "nombre": "Carlos",
    "apellidos": "Nuevo Paciente",
    "email": "carlos.nuevo@email.com",
    "fechanacimiento": "1988-05-20",
    "sexo": "MASCULINO",
    "altura": 180.0,
    "telefono": "645789123",
    "direccion": "Calle Nueva 123",
    "localidad": "Madrid",
    "provincia": "Madrid",
    "codigopostal": "28001",
    "dni": "12345678Z"
}'

test_endpoint "POST" "/pacientes" "Create new patient" "$NEW_PATIENT" "201"

# Create new appointment
NEW_APPOINTMENT='{
    "fechaini": "2025-10-01T10:00:00",
    "fechafin": "2025-10-01T11:00:00",
    "pacienteId": 2,
    "nutricionistaId": 1
}'

test_endpoint "POST" "/citas" "Create new appointment" "$NEW_APPOINTMENT" "201"

# Create new chat message
NEW_MESSAGE='{
    "contenido": "¿Cómo va el seguimiento de la dieta?",
    "enviadoPor": "NUTRICIONISTA",
    "chatId": 1
}'

test_endpoint "POST" "/chats/1/mensajes" "Send new message" "$NEW_MESSAGE" "201"

# =====================================================
# 12. HEALTH CHECK TESTS
# =====================================================
echo -e "\n${BLUE}🏥 HEALTH & MONITORING TESTS${NC}"

test_endpoint "GET" "/actuator/health" "Application health check" "" "200"
test_endpoint "GET" "/actuator/info" "Application info" "" "200"

# =====================================================
# SUMMARY
# =====================================================
echo -e "\n${BLUE}======================================================"
echo -e "📊 TEST SUMMARY COMPLETED"
echo -e "======================================================${NC}"
echo -e "${GREEN}✅ All major API endpoints tested with comprehensive dummy data${NC}"
echo -e "${YELLOW}📋 Test Data Coverage:${NC}"
echo -e "   • 2 Nutricionistas with full profiles"
echo -e "   • 3 Pacientes with medical history"
echo -e "   • 10 Alimentos with complete nutritional data"
echo -e "   • 5 Platos with recipes and nutritional calculations"
echo -e "   • 3 Planes de dieta with different parameters"
echo -e "   • 5 Citas scheduled across different dates"
echo -e "   • Complete measurement tracking (general, segmental, specific)"
echo -e "   • Medical history and treatment records"
echo -e "   • Real-time chat system with messages"
echo -e "   • Dietary filters and restrictions"
echo -e "${GREEN}🎯 Ready for comprehensive API testing and development!${NC}"
