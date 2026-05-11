# ⚡ QUICK START - Authentification JWT

## 🎯 Objectif

Tous les utilisateurs doivent se **connecter d'abord** avant d'utiliser l'API PDF.

---

## 🚀 Les 5 étapes pour démarrer

### ✅ Étape 1 : Compiler

```bash
cd pdfapi
mvn clean package
```

**Résultat attendu:** Pas d'erreur, fichier `target/pdfapi-0.0.1-SNAPSHOT.jar` créé

### ✅ Étape 2 : Lancer l'API

```bash
mvn spring-boot:run
```

**Résultat attendu:** Application démarre sur `http://localhost:8080`

### ✅ Étape 3 : S'enregistrer (Register)

```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "alice",
    "password": "Password123",
    "email": "alice@example.com"
  }'
```

**Réponse:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "alice",
  "email": "alice@example.com",
  "success": true
}
```

**Copier le token** (valide 24h)

### ✅ Étape 4 : Utiliser le token pour créer un PDF

```bash
TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

curl -X POST http://localhost:8080/creation-pdf \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"texte":"Mon premier PDF créé!"}'
```

**Réponse:**
```
output_file.pdf
```

### ✅ Étape 5 : Autres endpoints (tous avec le même token)

```bash
TOKEN="YOUR_TOKEN_HERE"

# Fusion de 2 PDFs
curl -X POST http://localhost:8080/fusion \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"pdf1":"base64_pdf1","pdf2":"base64_pdf2"}'

# Découpage
curl -X POST http://localhost:8080/decoupage \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"pdf":"base64_pdf","debut":1,"fin":3}'

# ... tous les autres endpoints
```

---

## 📋 Endpoints disponibles

### 🔓 Publics (sans token)
```
POST /auth/register       → Créer compte
POST /auth/login          → Se connecter
GET  /health              → Santé API
```

### 🔒 Protégés (avec token)
```
POST /fusion              → Fusion 2 PDFs
POST /decoupage           → Découpage pages
POST /extraction          → Extraction page
POST /suppression         → Suppression pages
POST /ajout-mot-de-passe  → Protéger PDF
POST /conversion-image    → PDF → PNG
POST /extraction-texte    → Extraire texte
POST /creation-pdf        → Créer PDF
```

---

## ⚠️ Erreurs courants

### Erreur : "Username already exists"
```json
{"message": "Username already exists", "success": false}
```
**Solution:** Utiliser un username différent ou se connecter avec `/auth/login`

### Erreur : "Invalid username or password"
```json
{"message": "Invalid username or password", "success": false}
```
**Solution:** Vérifier les identifiants

### Erreur : 401 Unauthorized
```json
{"error": "Unauthorized: ..."}
```
**Solution:** Ajouter le header `Authorization: Bearer TOKEN`

### Erreur : "Could not compile"
```
[ERROR] COMPILATION ERROR
```
**Solution:** Vérifier que Maven et Java 17+ sont installés

---

## 📖 Documentation complète

- **[AUTHENTIFICATION_JWT.md](AUTHENTIFICATION_JWT.md)** - Guide complet
- **[TEST_AUTHENTIFICATION.md](TEST_AUTHENTIFICATION.md)** - Tests détaillés
- **[AUTHENTIFICATION_CHANGEMENTS.md](AUTHENTIFICATION_CHANGEMENTS.md)** - Modifications effectuées

---

## 🎉 C'est prêt!

**Une fois compilé et testé localement:**
```bash
git add .
git commit -m "Add JWT authentication"
git push origin main
```

→ Render déploiera automatiquement avec l'authentification activée! 🚀

---

**Besoin d'aide?** Lire [AUTHENTIFICATION_JWT.md](AUTHENTIFICATION_JWT.md) pour plus de détails.
