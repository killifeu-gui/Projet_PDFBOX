# 🔐 Authentification JWT - Guide Complet

## 📋 Vue d'ensemble

Tous les utilisateurs doivent **s'identifier** avant d'accéder aux endpoints PDF. Le système utilise **JWT (JSON Web Tokens)** pour sécuriser l'accès.

### Architecture

```
Client
  ↓
Demande → /auth/register ou /auth/login → Reçoit TOKEN JWT
  ↓
Ajoute le token au header Authorization
  ↓
Demande → /fusion (+ header Authorization: Bearer TOKEN) → Accès accordé
```

---

## 🚀 Utilisation

### 1️⃣ S'enregistrer (Créer un compte)

**Endpoint:** `POST /auth/register`

**Payload (JSON):**
```json
{
  "username": "john_doe",
  "password": "SecurePassword123",
  "email": "john@example.com"
}
```

**Réponse réussie (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "john_doe",
  "email": "john@example.com",
  "success": true
}
```

**Réponse échouée (400 Bad Request):**
```json
{
  "message": "Username already exists",
  "success": false
}
```

---

### 2️⃣ Se connecter (Login)

**Endpoint:** `POST /auth/login`

**Payload (JSON):**
```json
{
  "username": "john_doe",
  "password": "SecurePassword123"
}
```

**Réponse réussie (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "john_doe",
  "email": "john@example.com",
  "success": true
}
```

**Réponse échouée (400 Bad Request):**
```json
{
  "message": "Invalid username or password",
  "success": false
}
```

---

### 3️⃣ Utiliser le Token pour accéder aux PDFs

**Tous les endpoints PDF** (_/fusion_, _/decoupage_, _/extraction_, etc.) nécessitent le token.

**Header requis:**
```
Authorization: Bearer <TOKEN_JWT>
```

**Exemple complet (cURL):**
```bash
curl -X POST http://localhost:8080/creation-pdf \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -d '{"texte":"Mon PDF créé"}'
```

---

## 📚 Endpoints Disponibles

### ✅ Publics (Sans token)
```
POST /auth/register          → Créer un compte
POST /auth/login             → Se connecter
GET  /health                 → État de l'API
```

### 🔒 Protégés (Avec token JWT)
```
POST /fusion                 → Fusionner 2 PDFs
POST /decoupage              → Découper pages
POST /extraction             → Extraire une page
POST /suppression            → Supprimer pages
POST /ajout-mot-de-passe     → Protéger PDF
POST /conversion-image       → PDF → PNG
POST /extraction-texte       → Extraire texte
POST /creation-pdf           → Créer PDF
```

---

## 🔑 Format JWT

Un token JWT contient 3 parties séparées par des points (`.`) :

```
header.payload.signature
```

**Exemple décodé:**
```
Header:
{
  "alg": "HS256",
  "typ": "JWT"
}

Payload:
{
  "sub": "john_doe",
  "iat": 1715426400,
  "exp": 1715512800
}

Signature: (calculée avec la clé secrète)
```

### ⏰ Durée de validité

- **Par défaut**: 24 heures (86400000 ms)
- **Configurable** dans `application.properties` via `app.jwtExpirationMs`

---

## 💻 Exemples complets

### Workflow complet avec cURL

```bash
# 1️⃣ S'enregistrer
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "alice",
    "password": "Password123",
    "email": "alice@example.com"
  }'

# Réponse (copier le token)
# {
#   "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
#   "username": "alice",
#   "email": "alice@example.com",
#   "success": true
# }

# 2️⃣ Utiliser le token pour créer un PDF
TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

curl -X POST http://localhost:8080/creation-pdf \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"texte":"Bonjour depuis l'API"}'

# 3️⃣ Sans le token → Erreur 401
curl -X POST http://localhost:8080/creation-pdf \
  -H "Content-Type: application/json" \
  -d '{"texte":"Sans token"}'

# Réponse:
# {"error": "Unauthorized: ..."}
```

### Workflow avec Node.js/JavaScript

```javascript
// 1️⃣ S'enregistrer
const registerResponse = await fetch('http://localhost:8080/auth/register', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    username: 'bob',
    password: 'SecurePass456',
    email: 'bob@example.com'
  })
});

const authData = await registerResponse.json();
const token = authData.token;

// 2️⃣ Utiliser le token
const pdfResponse = await fetch('http://localhost:8080/creation-pdf', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`
  },
  body: JSON.stringify({ texte: 'Mon contenu' })
});

const result = await pdfResponse.json();
console.log(result);
```

### Workflow avec Python

```python
import requests
import json

BASE_URL = 'http://localhost:8080'

# 1️⃣ S'enregistrer
register_response = requests.post(
    f'{BASE_URL}/auth/register',
    json={
        'username': 'charlie',
        'password': 'MyPassword789',
        'email': 'charlie@example.com'
    }
)

auth_data = register_response.json()
token = auth_data['token']

# 2️⃣ Utiliser le token
headers = {
    'Authorization': f'Bearer {token}',
    'Content-Type': 'application/json'
}

pdf_response = requests.post(
    f'{BASE_URL}/creation-pdf',
    headers=headers,
    json={'texte': 'Contenu du PDF'}
)

print(pdf_response.json())
```

---

## 🛡️ Sécurité

### Configuration JWT

Les paramètres JWT sont définis dans `application.properties`:

```properties
# Clé secrète pour signer les tokens (minimum 256 bits recommandé)
app.jwtSecret=SecretKeyForJWTTokenPdfServiceSecretKeyForJWTTokenPdfService123456

# Durée de vie du token en millisecondes (86400000 = 24h)
app.jwtExpirationMs=86400000
```

### 🔒 Bonnes pratiques

1. **Changer la clé secrète** en production:
   ```properties
   app.jwtSecret=VotreCleSuperSecureIlFautQueSesoitLongEtComplexe123456789
   ```

2. **Augmenter la clé** pour plus de sécurité (min 256 bits / 32 caractères)

3. **Protéger votre clé** - Ne pas la commiter en clair dans Git

4. **HTTPS uniquement** en production pour transmettre les tokens

5. **Durée d'expiration** - Adapter selon votre besoin:
   - Court terme (1h): `3600000`
   - Moyen terme (7j): `604800000`
   - Long terme (30j): `2592000000`

---

## ⚠️ Messages d'erreur

### 401 Unauthorized
```json
{"error": "Unauthorized: JWT signature does not match locally computed signature"}
```
**Cause:** Token invalide ou expiré
**Solution:** Se reconnecter avec `/auth/login`

### 400 Bad Request
```json
{"message": "Username already exists", "success": false}
```
**Cause:** L'username est déjà pris
**Solution:** Choisir un autre username

### 400 Bad Request
```json
{"message": "Invalid username or password", "success": false}
```
**Cause:** Identifiants incorrects
**Solution:** Vérifier username et password

### 500 Internal Server Error
```json
{"message": "Could not set user authentication in security context: ..."}
```
**Cause:** Problème serveur
**Solution:** Vérifier les logs

---

## 🔄 Flux d'authentification complet

```
┌─────────────────┐
│   Utilisateur   │
└────────┬────────┘
         │
         ↓
    ┌────────────────┐
    │ /auth/register │
    │  ou /auth/login│
    └────────┬───────┘
             │
             ↓
    ┌─────────────────────┐
    │ JWT Token généré    │
    │ (24h de validité)   │
    └────────┬────────────┘
             │
             ↓
    ┌────────────────────────────┐
    │ Stocké en local (client)   │
    │ ou session (backend)       │
    └────────┬─────────────────┘
             │
             ↓
    ┌──────────────────────────┐
    │ Chaque requête envoie:   │
    │ Authorization: Bearer X  │
    └────────┬─────────────────┘
             │
             ↓
    ┌────────────────────────────┐
    │ JwtFilter valide le token  │
    │ et authentifie l'utilisateur│
    └────────┬─────────────────┘
             │
             ↓
    ┌────────────────────────────┐
    │ Accès accordé à l'endpoint │
    │ /fusion, /creation-pdf, etc│
    └────────────────────────────┘
```

---

## 📋 Résumé des commandes

```bash
# Enregistrement
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"user","password":"pass","email":"user@example.com"}'

# Login
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user","password":"pass"}'

# Utiliser un endpoint protégé
curl -X POST http://localhost:8080/creation-pdf \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TOKEN_ICI" \
  -d '{"texte":"Mon texte"}'

# Tester l'accès santé
curl http://localhost:8080/health
```

---

## 🚀 Déploiement

### Sur Render

1. Les variables JWT sont auto-configurées dans `application.properties`
2. La base H2 est en mémoire (données perdues au redémarrage)
3. Pour persistance: utiliser une base de données PostgreSQL/MySQL

### Variables d'environnement personnalisées

```bash
# Ajouter dans Render dashboard → Environment Variables
app.jwtSecret=VotreCleSuperSecureIci
app.jwtExpirationMs=3600000
```

---

**🎉 L'authentification est prête !** Tous les utilisateurs doivent maintenant s'identifier avant d'utiliser l'API PDF.
