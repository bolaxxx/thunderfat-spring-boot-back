#!/bin/bash

# Script to generate development certificates for ThunderFat Spanish compliance
# This creates DUMMY certificates for development only - NOT for production use

echo "Generating development certificates for ThunderFat Spanish market compliance..."
echo "WARNING: These are DEVELOPMENT certificates only - NOT valid for production!"

# Create certificates directory if it doesn't exist
mkdir -p ./certificates

# Generate development keystore (PKCS12 format)
keytool -genkeypair \
  -alias thunderfat-sign \
  -keyalg RSA \
  -keysize 2048 \
  -validity 365 \
  -keystore ./certificates/thunderfat.p12 \
  -storetype PKCS12 \
  -storepass changeit \
  -keypass changeit \
  -dname "CN=ThunderFat Desarrollo, OU=IT Department, O=ThunderFat Nutricion SL, L=Madrid, ST=Madrid, C=ES" \
  -ext SAN=dns:localhost,dns:thunderfat.local

# Generate truststore
keytool -genkeypair \
  -alias ca-cert \
  -keyalg RSA \
  -keysize 2048 \
  -validity 365 \
  -keystore ./certificates/truststore.jks \
  -storetype JKS \
  -storepass changeit \
  -keypass changeit \
  -dname "CN=ThunderFat CA Desarrollo, OU=CA, O=ThunderFat Nutricion SL, L=Madrid, ST=Madrid, C=ES"

echo ""
echo "Development certificates generated successfully!"
echo ""
echo "Files created:"
echo "  - ./certificates/thunderfat.p12 (Main keystore)"
echo "  - ./certificates/truststore.jks (Truststore)"
echo ""
echo "Certificate details:"
echo "  - Keystore password: changeit"
echo "  - Certificate alias: thunderfat-sign"
echo "  - Validity: 365 days"
echo "  - Algorithm: RSA 2048-bit"
echo ""
echo "⚠️  IMPORTANT: Replace with real Spanish digital certificates before production!"
echo "⚠️  For production, use certificates from:"
echo "    - FNMT (Fábrica Nacional de Moneda y Timbre)"
echo "    - ACCV (Agencia de Tecnología y Certificación Electrónica)"
echo "    - Other recognized Spanish certification authorities"
