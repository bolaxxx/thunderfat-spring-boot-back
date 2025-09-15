# 📊 Comprehensive Test Data Documentation

## Overview
This document describes the comprehensive test data generated for the ThunderFat Nutrition Management System. The data covers all major entities and provides realistic scenarios for thorough API testing.

## 🎯 Data Coverage Summary

### 👥 **User Management (5 Users)**
- **2 Nutricionistas**: Dr. Carlos Martínez, Dra. Elena López
- **3 Pacientes**: Ana García, Luis Rodríguez, María Fernández  
- **3 Roles**: NUTRICIONISTA, PACIENTE, ADMIN
- **Authentication**: BCrypt password hashing, realistic email addresses

### 🏥 **Healthcare Professionals**
```
Dr. Carlos Martínez Ruiz (ID: 1)
├── Email: dr.martinez@nutrihealth.com
├── Colegiado: MAD-2019-1234
├── Location: Madrid, Gran Vía 28
└── Patients: Ana García, Luis Rodríguez

Dra. Elena López Sánchez (ID: 4)  
├── Email: dra.lopez@nutrihealth.com
├── Colegiado: BCN-2020-5678
├── Location: Barcelona, Avda. Diagonal 435
└── Patients: María Fernández
```

### 🏥 **Patient Profiles**
```
Ana García Pérez (ID: 2)
├── Age: 40 years (Born: 1985-03-15)
├── Gender: Female, Height: 165cm
├── Location: Madrid, Calle Mayor 45
├── Nutritionist: Dr. Martínez
├── Medical: Hypertension (controlled)
└── Diet Plan: Low sodium (1400-1600 kcal)

Luis Rodríguez Gómez (ID: 3)
├── Age: 35 years (Born: 1990-07-22)  
├── Gender: Male, Height: 178cm
├── Location: Sevilla, Plaza España 12
├── Nutritionist: Dr. Martínez
├── Medical: Type 2 Diabetes (recent diagnosis)
└── Diet Plan: Diabetic controlled (1800-2200 kcal)

María Fernández Torres (ID: 5)
├── Age: 33 years (Born: 1992-11-08)
├── Gender: Female, Height: 162cm  
├── Location: Barcelona, Rambla Catalunya 89
├── Nutritionist: Dra. López
├── Medical: Lactose intolerance
└── Diet Plan: Vegetarian (1300-1500 kcal)
```

## 🥗 **Nutrition Database**

### **Complete Food Database (10 Foods)**
Each food item includes 25+ nutritional parameters:

1. **Pollo pechuga sin piel** - High protein, low fat
2. **Arroz blanco cocido** - Carbohydrate base
3. **Brócoli crudo** - High fiber, vitamins, antioxidants  
4. **Salmón crudo** - Omega-3, high protein
5. **Aguacate crudo** - Healthy fats, fiber
6. **Quinoa cocida** - Complete protein, minerals
7. **Espinacas crudas** - Iron, folate, vitamins
8. **Aceite oliva virgen extra** - Healthy fats, vitamin E
9. **Tomate crudo** - Lycopene, vitamin C
10. **Huevos crudos** - Complete protein, B vitamins

### **Recipe Database (5 Recipes)**
Realistic Mediterranean-style recipes with calculated nutritionals:

1. **Ensalada Mediterránea** (245 kcal) - Mixed greens with olive oil
2. **Pollo a la Plancha con Brócoli** (199 kcal) - Lean protein + vegetables  
3. **Salmón con Quinoa** (351 kcal) - Omega-3 rich complete meal
4. **Tortilla de Huevo con Espinacas** (178 kcal) - Protein-rich breakfast
5. **Arroz Integral con Verduras** (165 kcal) - Fiber-rich carbohydrate

## 📋 **Diet Plan Management**

### **Active Diet Plans (3 Plans)**
```
Plan 1 - Ana García (Low Sodium)
├── Duration: Sept 1-Oct 1, 2025
├── Calories: 1400-1600 kcal/day  
├── Meals: 5 per day
├── Macros: 60% carbs, 20% protein, 20% fat
└── Filter: Gluten-free

Plan 2 - Luis Rodríguez (Diabetic)
├── Duration: Sept 10-Oct 10, 2025
├── Calories: 1800-2200 kcal/day
├── Meals: 5 per day  
├── Macros: 60% carbs, 30% protein, 15% fat
└── Filter: None (standard)

Plan 3 - María Fernández (Vegetarian)
├── Duration: Sept 15-Oct 15, 2025
├── Calories: 1300-1500 kcal/day
├── Meals: 4 per day
├── Macros: 60% carbs, 30% protein, 15% fat  
└── Filter: Vegetarian
```

## 📊 **Health Monitoring Data**

### **Body Composition Tracking**
- **General Measurements**: Weight, body fat, muscle mass, BMR
- **Segmental Analysis**: Arms, legs, torso composition
- **Specific Metrics**: BMI, metabolic age, bone density

### **Medical History Integration**
- **Clinical Records**: Hypertension, diabetes, intolerances
- **Treatment History**: Current medications, supplements
- **Progress Tracking**: Multiple measurement points over time

## 📅 **Appointment System**

### **Scheduled Appointments (5 Appointments)**
```
Sept 20, 09:00-10:00 → Ana García & Dr. Martínez  
Sept 21, 15:30-16:30 → María Fernández & Dra. López
Sept 22, 11:00-12:00 → Luis Rodríguez & Dr. Martínez
Sept 25, 10:00-11:00 → Ana García & Dr. Martínez (Follow-up)
Sept 27, 16:00-17:00 → Luis Rodríguez & Dr. Martínez (Follow-up)
```

## 💬 **Real-time Communication**

### **Chat System (3 Active Chats)**
```
Chat 1: Dr. Martínez ↔ Ana García
├── Topic: Diet adaptation and energy levels
├── Messages: 3 exchanges
└── Status: Active

Chat 2: Dr. Martínez ↔ Luis Rodríguez  
├── Topic: Glucose level monitoring
├── Messages: 2 exchanges
└── Status: Active

Chat 3: Dra. López ↔ María Fernández
├── Topic: Vegetarian recipe preferences
├── Messages: 2 exchanges  
└── Status: Active
```

## 🍎 **Dietary Management**

### **Dietary Filters (5 Filters)**
1. **Sin Gluten** - Celiac-safe foods
2. **Vegetariano** - No meat or fish  
3. **Vegano** - No animal products
4. **Bajo en Sodio** - Hypertension management
5. **Diabético** - Carbohydrate controlled

## 🔐 **Security & Authentication**

### **User Credentials**
```
All users password: "password123"
Emails:
├── dr.martinez@nutrihealth.com (Nutricionista)
├── dra.lopez@nutrihealth.com (Nutricionista)  
├── ana.garcia@email.com (Paciente)
├── luis.rodriguez@email.com (Paciente)
└── maria.fernandez@email.com (Paciente)
```

### **Role-Based Access**
- **NUTRICIONISTA**: Full access to assigned patients
- **PACIENTE**: Personal data and communication access
- **ADMIN**: System-wide access (reserved)

## 🧪 **Testing Scenarios**

### **Covered Test Cases**
✅ **Authentication**: Login/logout, token management  
✅ **User Management**: CRUD operations, role assignment
✅ **Patient Care**: Medical history, measurements, progress
✅ **Nutrition Planning**: Food database, recipe creation, diet plans
✅ **Appointment Management**: Scheduling, conflicts, follow-ups  
✅ **Communication**: Real-time chat, message history
✅ **Data Relationships**: Patient-nutritionist assignments
✅ **Medical Compliance**: Treatment tracking, history management
✅ **Dietary Restrictions**: Filter application, allergy management

### **API Endpoint Coverage**
- **User Management**: `/api/usuarios/*`, `/api/nutricionistas/*`, `/api/pacientes/*`
- **Nutrition**: `/api/alimentos/*`, `/api/platos/*`, `/api/planes/*`
- **Healthcare**: `/api/citas/*`, `/api/mediciones/*`, `/api/antecedentes/*`
- **Communication**: `/api/chats/*`, `/api/mensajes/*`
- **Filters**: `/api/filtros/*`
- **Health**: `/api/actuator/*`

## 🚀 **Usage Instructions**

### **Quick Start**
1. **Start Application**: `./mvnw spring-boot:run`
2. **Run Tests**: `./test-comprehensive-api.sh`
3. **Login**: Use any provided email with password "password123"
4. **Explore**: All endpoints populated with realistic data

### **Development Workflow**
1. **Database Reset**: Hibernate recreates schema on startup
2. **Data Population**: `data-h2.sql` loads automatically
3. **API Testing**: Comprehensive test script validates all endpoints
4. **Frontend Development**: Rich data set for UI development

## 📈 **Benefits for Development**

### **Realistic Testing Environment**
- ✅ **Complete Patient Journeys**: From registration to treatment
- ✅ **Multi-user Scenarios**: Nutritionist-patient interactions
- ✅ **Time-based Data**: Appointments, measurements, progress tracking
- ✅ **Complex Relationships**: Cross-entity dependencies maintained
- ✅ **Healthcare Compliance**: Medical terminology and workflows

### **Performance Testing**  
- ✅ **Relationship Loading**: Lazy/eager loading optimization
- ✅ **Query Performance**: Complex joins and filtering
- ✅ **Caching Validation**: Multi-level cache effectiveness
- ✅ **Security Testing**: Role-based access control validation

This comprehensive test data provides a solid foundation for developing, testing, and demonstrating the full capabilities of the ThunderFat Nutrition Management System.
