#!/bin/bash
curl -X POST -H "Content-Type: application/json" -d '{"email":"admin@thunderfat.com","password":"password"}' http://localhost:8080/api/auth/login -v
