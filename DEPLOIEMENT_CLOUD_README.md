# Projet PDF-Box API - Guide de Déploiement

## 🎯 Objectif

API REST Spring Boot + CORBA pour gestion et transformation de fichiers PDF (fusion, découpage, extraction, suppression, ajout mot de passe, conversion texte/image).

## 🚀 Architecture

- **API** : Spring Boot 2.7.18 (Java 8)
- **Dépendances clés** : 
  - PDFBox 2.0.29 (manipulation PDF)
  - GlassFish CORBA 5.0.2 (intégration service CORBA)
  - Spring Security + JWT (authentification)
- **Déploiement** : Docker sur Render

## 📦 Structure

```
.
├── pdfapi/                    # API Spring Boot
│   ├── src/main/java/
│   │   └── com/babacar/pdfapi/
│   │       ├── PdfController.java       # Endpoints PDF
│   │       ├── AuthController.java      # Auth & JWT
│   │       ├── CorbaService.java        # Client CORBA
│   │       ├── security/                # JWT & Security
│   │       ├── model/, service/, etc.
│   │   └── pom.xml
├── src/CalculatriceApp/       # Stubs CORBA générés (.idl)
├── Dockerfile                 # Multi-stage build
├── .dockerignore             # Exclusions build Docker
├── .gitignore               # Exclusions Git
└── render.yaml              # Config Render (optional)
```

## 🔧 Prérequis locaux (test)

- Java 8+
- Maven 3.8+
- Serveur CORBA accessible (ORB_INITIAL_HOST + ORB_INITIAL_PORT)

## 📡 Variables d'environnement

| Variable | Valeur | Scope | Défaut |
|----------|--------|-------|--------|
| `ORB_INITIAL_HOST` | Host du serveur CORBA | Runtime | `localhost` |
| `ORB_INITIAL_PORT` | Port CORBA | Runtime | `1050` |
| `app.jwtSecret` | Clé secrète JWT | Runtime | Random |
| `app.jwtExpirationMs` | Expiration JWT (ms) | Runtime | `86400000` (24h) |

## 🐳 Build & Déploiement Docker

### Build local
```bash
docker build -t projet-pdfbox .
docker run -e ORB_INITIAL_HOST=<your-corba-host> -e ORB_INITIAL_PORT=1050 -p 8080:8080 projet-pdfbox
```

### Déploiement sur Render

1. Poussez le repo sur GitHub
2. Allez sur https://dashboard.render.com/
3. **+ New → Web Service**
4. Connectez votre repo `killifeu-gui/Projet_PDFBOX`
5. Config :
   - **Name** : `projet-pdfbox`
   - **Environment** : `Docker`
   - **Region** : Oregon
6. **Environment** → Ajouter :
   - `ORB_INITIAL_HOST` = `<votre-ip-corba>` (⚠️ `localhost` ne fonctionne PAS sur Render)
   - `ORB_INITIAL_PORT` = `1050`
7. **Create Web Service** → attendre build (~5-10 min)

## ✅ Endpoints disponibles

```bash
POST /creation-pdf         # Créer un PDF à partir de texte
POST /fusion              # Fusionner plusieurs PDFs
POST /decoupage           # Découper un PDF
POST /extraction          # Extraire des pages
POST /suppression         # Supprimer des pages
POST /ajout-mot-de-passe  # Protéger PDF
POST /conversion-image    # PDF → PNG
POST /extraction-texte    # Extraire texte du PDF
POST /register            # Créer compte
POST /login               # Auth JWT
```

## 🔐 Authentification

1. **Créer un compte** :
```bash
curl -X POST http://localhost:8080/register \
  -H "Content-Type: application/json" \
  -d '{"username":"user1","password":"pass123"}'
```

2. **Login & obtenir token JWT** :
```bash
curl -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user1","password":"pass123"}'
```

3. **Utiliser le token pour les endpoints PDF** :
```bash
curl -X POST http://localhost:8080/creation-pdf \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"texte":"Bonjour !"}'
```

## 📊 Vérifier le déploiement Render

1. Dashboard Render → sélectionner service
2. **Logs** → chercher :
   - `Tomcat started on port(s): 8080`
   - `CORBA PdfService successfully initialized`
3. **URL** affichée en haut → tester l'endpoint health (ou login pour vérifier)

## ❌ Dépannage

### `ORB_INITIAL_HOST unreachable`
- Le serveur CORBA n'est pas accessible depuis Render
- ✅ Solution 1 : Hébergez le serveur CORBA sur le cloud (même Render ou autre)
- ✅ Solution 2 : Utilisez un tunnel (ngrok) pour exposer votre serveur local
- ✅ Solution 3 : VPN privée Render

### `Cannot resolve org.glassfish.corba`
- Vérifiez la connexion Internet du build Docker
- Vérifiez que Maven central est accessible
- Testez localement : `mvn dependency:resolve` dans `pdfapi/`

### Build timeout
- Render Free: 12 min max (Maven + PDFBox peut être long)
- Solution : Passer au plan **Standard** ($7+/mois)

## 🧹 Fichiers supprimés (non nécessaires pour Render)

- `bin/` (compilés, recréés par Docker)
- `CalculatriceClient/`, `CalculatriceServer/` (clients CORBA locaux, pas besoin dans API cloud)
- `*.bat` (scripts Windows, inutiles en Docker/Linux)
- `ior*.txt`, `orb.db/` (fichiers de runtime locaux)

## 📚 Ressources

- [Spring Boot Docker](https://spring.io/guides/topicals/spring-boot-docker/)
- [Render Docs](https://render.com/docs)
- [PDFBox](https://pdfbox.apache.org/)
- [GlassFish CORBA](https://docs.oracle.com/cd/E19683-01/806-4915/6jg56m6oo/index.html)

---

**Status** : ✅ Prêt pour déploiement  
**Dernière mise à jour** : 2026-05-18
