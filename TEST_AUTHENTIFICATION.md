# 🧪 Guide de test de l'authentification JWT

## Préparation

### 1. Compiler l'application

```bash
cd pdfapi
mvn clean package
```

### 2. Démarrer l'API

```bash
# Depuis le dossier pdfapi
mvn spring-boot:run

# Ou directement avec Java
java -jar target/pdfapi-0.0.1-SNAPSHOT.jar
```

L'API démarre sur `http://localhost:8080`

---

## 🧪 Tests complets

### Test 1️⃣ : S'enregistrer (Register)

```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "TestPassword123",
    "email": "test@example.com"
  }'
```

**Réponse attendue :**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0dXNlciIsImlhdCI6MTcxNTQyNjQwMCwiZXhwIjoxNzE1NTEyODAwfQ...",
  "username": "testuser",
  "email": "test@example.com",
  "success": true
}
```

**⚠️ Erreur si le username existe déjà :**
```json
{
  "message": "Username already exists",
  "success": false
}
```

---

### Test 2️⃣ : Se connecter (Login)

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "TestPassword123"
  }'
```

**Réponse attendue :** (même que register)
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "testuser",
  "email": "test@example.com",
  "success": true
}
```

**⚠️ Erreur avec mauvais password :**
```json
{
  "message": "Invalid username or password",
  "success": false
}
```

---

### Test 3️⃣ : Accéder aux endpoints protégés (sans token)

```bash
curl -X POST http://localhost:8080/creation-pdf \
  -H "Content-Type: application/json" \
  -d '{"texte":"Test"}'
```

**Réponse attendue (401 Unauthorized) :**
```json
{
  "error": "Unauthorized: JWT signature does not match locally computed signature"
}
```

---

### Test 4️⃣ : Accéder aux endpoints protégés (avec token)

#### Étape 1 : Copier le token du login

```bash
# Récupérer le token (example)
TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0dXNlciIsImlhdCI6MTcxNTQyNjQwMCwiZXhwIjoxNzE1NTEyODAwfQ..."
```

#### Étape 2 : Créer un PDF authentifié

```bash
TOKEN="YOUR_TOKEN_HERE"

curl -X POST http://localhost:8080/creation-pdf \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"texte":"Mon PDF créé avec authentification"}'
```

**Réponse attendue (200 OK) :**
```
output_file_name.pdf
```

---

## 🧬 Tests avancés avec scripts

### Script Bash complet

```bash
#!/bin/bash

API="http://localhost:8080"

echo "=== Test 1: Enregistrement ==="
RESPONSE=$(curl -s -X POST "$API/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "bob",
    "password": "BobPassword456",
    "email": "bob@example.com"
  }')

echo $RESPONSE
TOKEN=$(echo $RESPONSE | grep -o '"token":"[^"]*' | cut -d'"' -f4)

if [ -z "$TOKEN" ]; then
  echo "❌ Enregistrement échoué"
  exit 1
fi

echo ""
echo "✅ Enregistrement réussi"
echo "Token: $TOKEN"

echo ""
echo "=== Test 2: Création de PDF avec authentification ==="
curl -s -X POST "$API/creation-pdf" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"texte":"Test PDF authentifié"}' | jq .

echo ""
echo "✅ Test complet réussi!"
```

**Exécuter :**
```bash
bash test_auth.sh
```

---

### Script PowerShell (Windows)

```powershell
$API = "http://localhost:8080"

Write-Host "=== Test 1: Enregistrement ===" -ForegroundColor Yellow

$registerBody = @{
    username = "charlie"
    password = "CharliePass789"
    email = "charlie@example.com"
} | ConvertTo-Json

$registerResponse = Invoke-WebRequest -Uri "$API/auth/register" `
    -Method POST `
    -Headers @{"Content-Type" = "application/json"} `
    -Body $registerBody

$authData = $registerResponse.Content | ConvertFrom-Json
$TOKEN = $authData.token

Write-Host "Token reçu: $TOKEN" -ForegroundColor Green

Write-Host ""
Write-Host "=== Test 2: Création de PDF ===" -ForegroundColor Yellow

$pdfBody = @{
    texte = "PDF créé depuis PowerShell"
} | ConvertTo-Json

$pdfResponse = Invoke-WebRequest -Uri "$API/creation-pdf" `
    -Method POST `
    -Headers @{
        "Content-Type" = "application/json"
        "Authorization" = "Bearer $TOKEN"
    } `
    -Body $pdfBody

Write-Host "Réponse: " $pdfResponse.Content -ForegroundColor Green
```

---

## 📊 Tableaux de test

### Scénarios d'enregistrement

| Situation | Payload | Résultat attendu |
|-----------|---------|------------------|
| Nouveau compte | ✅ username, password, email | 200 OK + token |
| Username existant | ❌ Même username | 400 Bad Request |
| Email existant | ❌ Même email | 400 Bad Request |
| Champ manquant | ❌ Sans "email" | 400 Bad Request |
| Valide | ✅ Complet | 200 OK + token |

### Scénarios de login

| Situation | Payload | Résultat attendu |
|-----------|---------|------------------|
| Identifiants corrects | ✅ username + password | 200 OK + token |
| Mauvais password | ❌ Bon username, mauvais password | 400 Bad Request |
| Username inexistant | ❌ Username inconnu | 400 Bad Request |
| Champ manquant | ❌ Sans "password" | 400 Bad Request |

### Scénarios d'accès aux endpoints PDF

| Situation | Header Authorization | Résultat attendu |
|-----------|---------------------|------------------|
| Pas de token | ❌ Absent | 401 Unauthorized |
| Token valide | ✅ Bearer + token | 200 OK + résultat |
| Token expiré | ❌ Bearer + ancien token | 401 Unauthorized |
| Token invalide | ❌ Bearer + texte random | 401 Unauthorized |
| Mauvais format | ❌ "token" au lieu de "Bearer token" | 401 Unauthorized |

---

## 🔧 Dépannage

### Erreur : "Could not set user authentication"

**Cause:** JwtFilter ne peut pas charger l'utilisateur

**Solution:**
```bash
# 1. Vérifier que le token est valide
# 2. Vérifier que l'utilisateur existe en base
# 3. Redémarrer l'API
```

### Erreur : "JWT signature does not match"

**Cause:** La clé secrète utilisée pour valider ne correspond pas

**Solution:**
```properties
# Vérifier app.jwtSecret dans application.properties
# Doit être la même pour générer et valider les tokens
```

### Erreur : "User not found"

**Cause:** L'utilisateur n'existe pas ou a été supprimé

**Solution:**
```bash
# Se réenregistrer avec /auth/register
```

### Erreur : "Username already exists"

**Cause:** Le username est déjà pris

**Solution:**
```bash
# 1. Utiliser un username différent
# 2. Ou se connecter avec le compte existant via /auth/login
# 3. Ou supprimer la base H2 (données en mémoire)
```

---

## 📈 Performance

### Temps de réponse

```bash
# Tester le temps de réponse
time curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"perf_test","password":"Pass123","email":"perf@test.com"}'

# Résultat attendu: < 100ms
```

### Charge

```bash
# Tester avec 100 requêtes parallèles
for i in {1..100}; do
  curl -X POST http://localhost:8080/auth/login \
    -H "Content-Type: application/json" \
    -d "{\"username\":\"testuser\",\"password\":\"TestPassword123\"}" &
done
wait
```

---

## ✅ Checklist de test

- [ ] S'enregistrer fonctionne
- [ ] Login fonctionne
- [ ] Token reçu dans les deux cas
- [ ] Pas de token = 401
- [ ] Avec token = accès autorisé
- [ ] Token expiré = 401
- [ ] Creation PDF marche avec token
- [ ] Fusion PDF marche avec token
- [ ] Tous les 8 endpoints PDF marchent avec token
- [ ] Messages d'erreur cohérents

---

## 🚀 Prêt pour la production ?

```bash
# Checklist avant production
- [ ] Changer app.jwtSecret en production
- [ ] Augmenter app.jwtExpirationMs selon besoin
- [ ] Remplacer H2 par PostgreSQL/MySQL
- [ ] Ajouter rate limiting
- [ ] HTTPS obligatoire
- [ ] Logs correctement configurés
- [ ] Dockerfile mis à jour
- [ ] Variables d'environnement configurées
```

---

**Tous les tests réussis ? Prêt pour déployer sur Render! 🎉**
