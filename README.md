# TP_CORBA - Service de Calculatrice avec Extension PDF

## Vue d'ensemble

Ce projet implémente un service CORBA avec deux interfaces principales :
1. **CalculatriceService** - Opérations mathématiques de base
2. **PdfService** - Manipulation de fichiers PDF via PDFBox

## Structure du projet

```
TP_Corba/
├── src/
│   ├── Addition.idl                    # Spécification IDL principale
│   ├── CalculatriceServer/
│   │   ├── CalculatriceImpl.java       # Implémentation Calculatrice
│   │   ├── PdfServiceImpl.java         # Implémentation PdfService
│   │   └── StartServer.java            # Point d'entrée serveur
│   ├── CalculatriceClient/
│   │   ├── StartClient.java            # Client Calculatrice
│   │   ├── StartPdfClientFusionDecoupage.java
│   │   ├── StartPdfClientExtractionSuppression.java
│   │   ├── StartPdfClientMotDePasseEtCreation.java
│   │   └── StartPdfClientConversionEtTexte.java
│   └── CalculatriceApp/                # Fichiers générés par idlj
├── CalculatriceApp/                    # Copies des fichiers générés
├── bin/                                # Fichiers compilés
├── lib/
│   ├── pdfbox-2.0.29.jar              # Bibliothèque PDFBox
│   ├── fontbox-2.0.29.jar             # Dépendance PDFBox (polices)
│   └── commons-logging-1.2.jar        # Dépendance logging
├── compile.bat                         # Script de compilation
├── run_server.bat                      # Script de démarrage serveur
└── README.md                           # Ce fichier
```

## Fonctionnalités PDF

Le PdfService offre 8 opérations :

1. **fusionPdf** - Fusionne deux PDFs
2. **decoupagePdf** - Découpe un PDF selon une plage de pages
3. **extractionPage** - Extrait une page spécifique
4. **suppressionPage** - Supprime une plage de pages
5. **ajoutMotDePasse** - Protège un PDF avec mot de passe
6. **conversionPdfEnImage** - Convertit PDF en image PNG
7. **extractionTexte** - Extrait le texte d'un PDF
8. **creationPdf** - Crée un PDF depuis du texte

## Prérequis

- Java 1.8 (JDK avec outils CORBA)
- PDFBox 2.0.29 (déjà dans lib/)

## Compilation

```batch
compile.bat
```

## Démarrage

### 1. Lancer le serveur

```batch
run_server.bat
```

Ou manuellement :
```batch
REM Dans un terminal
orbd -ORBInitialPort 1050

REM Dans un autre terminal
java -cp bin;lib/pdfbox-2.0.29.jar;lib/commons-logging-1.2.jar StartServer
```

### 2. Lancer un client

#### Client Calculatrice
```batch
java -cp bin;lib/pdfbox-2.0.29.jar;lib/commons-logging-1.2.jar StartClient -ORBInitialHost localhost -ORBInitialPort 1050
```

#### Clients PDF

**Fusion et découpage :**
```batch
java -cp bin;lib/pdfbox-2.0.29.jar;lib/commons-logging-1.2.jar StartPdfClientFusionDecoupage input1.pdf input2.pdf output.pdf 1 3
```

**Extraction et suppression :**
```batch
java -cp bin;lib/pdfbox-2.0.29.jar;lib/commons-logging-1.2.jar StartPdfClientExtractionSuppression input.pdf extracted.pdf suppressed.pdf 1 2 3
```

**Mot de passe et création :**
```batch
java -cp bin;lib/pdfbox-2.0.29.jar;lib/commons-logging-1.2.jar StartPdfClientMotDePasseEtCreation input.pdf secret123 protected.pdf "Bonjour" created.pdf
```

**Conversion et extraction texte :**
```batch
java -cp bin;lib/pdfbox-2.0.29.jar;lib/commons-logging-1.2.jar StartPdfClientConversionEtTexte input.pdf output.png 150
```

#### Nouveaux clients individuels (ajoutés récemment)

**Fusion de PDFs :**
```batch
run_client_fusion.bat pdf1.pdf pdf2.pdf output.pdf
```

**Découpage de PDF :**
```batch
run_client_decoupage.bat pdf.pdf debut fin output.pdf
```

**Extraction de page :**
```batch
run_client_extraction.bat pdf.pdf page output.pdf
```

**Suppression de pages :**
```batch
run_client_suppression.bat pdf.pdf debut fin output.pdf
```

## Tests complets

### Préparation des fichiers de test

```batch
REM Créer un fichier texte
echo "Ceci est un PDF de test avec du texte." > test.txt

REM Créer un PDF vide (nécessite PDFBox)
javac -cp 'lib/pdfbox-2.0.29.jar;lib/commons-logging-1.2.jar' CreatePDF.java
java -cp '.;lib/pdfbox-2.0.29.jar;lib/commons-logging-1.2.jar' CreatePDF

REM Créer un PDF multi-pages
javac -cp 'lib/pdfbox-2.0.29.jar;lib/commons-logging-1.2.jar' AddPage.java
java -cp '.;lib/pdfbox-2.0.29.jar;lib/commons-logging-1.2.jar' AddPage
```

### Démarrage du serveur

```batch
run_server.bat
```

### Tests des opérations PDF

```batch
REM Fusion
run_client_fusion.bat dummy.pdf test2.pdf fusion_result.pdf

REM Découpage (pages 1 à 1)
run_client_decoupage.bat multi_page.pdf 1 1 decoupage_result.pdf

REM Extraction page 2
run_client_extraction.bat multi_page.pdf 2 extraction_result.pdf

REM Suppression page 1
run_client_suppression.bat multi_page.pdf 1 1 suppression_result.pdf

REM Clients combinés existants
java -cp 'bin;lib/pdfbox-2.0.29.jar;lib/commons-logging-1.2.jar' CalculatriceClient.StartPdfClientFusionDecoupage dummy.pdf test2.pdf result.pdf 1 2
java -cp 'bin;lib/pdfbox-2.0.29.jar;lib/commons-logging-1.2.jar' CalculatriceClient.StartPdfClientExtractionSuppression multi_page.pdf extracted.pdf suppressed.pdf 1 2 2
java -cp 'bin;lib/pdfbox-2.0.29.jar;lib/commons-logging-1.2.jar' CalculatriceClient.StartPdfClientMotDePasseEtCreation dummy.pdf password test.txt protected.pdf
java -cp 'bin;lib/pdfbox-2.0.29.jar;lib/commons-logging-1.2.jar' CalculatriceClient.StartPdfClientConversionEtTexte multi_page.pdf image.png 150
```

### Test de la calculatrice

```batch
java -cp 'bin;lib/pdfbox-2.0.29.jar;lib/commons-logging-1.2.jar' CalculatriceClient.StartClient
```

## Mode sans NamingService

Si le NamingService n'est pas disponible, les clients peuvent utiliser le fichier `ior.txt` généré automatiquement.

## Déploiement

### Déploiement avec Docker

Un `Dockerfile` a été ajouté à la racine du projet pour construire et déployer l'API Spring Boot située dans `pdfapi/`.

```bash
# Construire l'image Docker
docker build -t projet-pdfbox .

# Lancer le conteneur
docker run -p 8080:8080 projet-pdfbox
```

Le service sera accessible sur `http://localhost:8080`.

Pour des instructions détaillées sur le test Docker local, voir [TEST_DOCKER_LOCAL.md](TEST_DOCKER_LOCAL.md).

### Déploiement local

1. Compiler : `compile.bat`
2. Démarrer le serveur : `run_server.bat`
3. Lancer les clients selon les besoins

### Déploiement avec Render

**Limitations :** CORBA nécessite un serveur ORB persistant avec port fixe, ce qui n'est pas idéal pour les plateformes cloud comme Render. Cependant, vous pouvez déployer la partie web API.

#### Option 1 : API Web uniquement (recommandé pour cloud)

La partie `pdfapi/` fournit une API REST Spring Boot qui encapsule les appels CORBA.

**Ce qui a été corrigé :**
- `pdfapi/pom.xml` utilise maintenant `org.jacorb:jacorb:3.11.0` comme ORB
- `pdfapi` compile les sources CORBA générées depuis `../src/CalculatriceApp`

**Configuration Render - Étapes manuelles :**

1. **Connectez votre dépôt GitHub**
   - Allez sur https://dashboard.render.com/
   - Cliquez sur **+ New** → **Web Service**
   - Connectez votre repo GitHub `killifeu-gui/Project_PDFBOX`

2. **Configurez le build Docker**
   - **Build Command** (laisser vide, Render détectera le `Dockerfile`)
   - **Start Command** (laisser vide, le `ENTRYPOINT` du Dockerfile s'exécutera)
   - **Root Directory** : `/` (racine du repo)

3. **Variables d'environnement (Settings → Environment)**
   - `ORB_INITIAL_HOST` = `votre-ip-corba.com` (ou localhost si CORBA est sur Render aussi)
   - `ORB_INITIAL_PORT` = `1050`
   - `PORT` = `8080` (déjà défini par Spring Boot)

4. **Déploiement**
   - Plan : Free (suffisant pour tests)
   - Région : Oregon (ou la plus proche)
   - Auto-deploy : Oui (déploie à chaque push sur `main`)

5. **Déploiement automatique GitHub**
   ```bash
   git add .
   git commit -m "Setup Render deployment"
   git push origin main
   ```
   → Render détectera le push et déploiera automatiquement

**Configuration alternative avec `render.yaml` (optionnel)**

Un fichier `render.yaml` a été ajouté à la racine. Vous pouvez le modifier pour ajuster la configuration.

**Exemple d'utilisation API :**
```bash
curl -X POST https://your-render-app.onrender.com/fusion \
  -H "Content-Type: application/json" \
  -d '{"pdf1":"base64_encoded_pdf1", "pdf2":"base64_encoded_pdf2"}'
```

#### Option 2 : Serveur CORBA complet sur Render (expérimental)

- Render supporte les **Background Workers**, mais pas les services ORB persistants
- Nécessite un port statique et connexion persistante
- Non recommandé pour production

#### Dépannage Render

Si le déploiement échoue :

1. **Vérifiez les logs Render**
   - Dashboard → Votre service → Logs
   - Cherchez les erreurs Maven ou Docker

2. **Si `ORB_INITIAL_HOST` est inaccessible**
   - Render ne peut pas atteindre votre serveur CORBA local
   - Solution : Hébergez aussi le serveur CORBA sur Render ou exposez le vôtre avec un tunnel

3. **Erreur de Java version**
   - Le Dockerfile utilise Java 17 pour runtime et Maven 3.9.9
   - Si vous avez besoin de Java 1.8, modifiez le `Dockerfile`

---

## 📚 Guides de déploiement complets

- **[AUTHENTIFICATION_JWT.md](AUTHENTIFICATION_JWT.md)** - Guide complet de l'authentification JWT (nouveau)
- **[DEPLOIEMENT_RENDER.md](DEPLOIEMENT_RENDER.md)** - Guide complet Render (étapes manuelles)
- **[TEST_DOCKER_LOCAL.md](TEST_DOCKER_LOCAL.md)** - Test Docker localement avant déploiement
- **[CHECKLIST_DEPLOIEMENT.md](CHECKLIST_DEPLOIEMENT.md)** - Checklist et architecture finale

Recommandé : Commencez par [AUTHENTIFICATION_JWT.md](AUTHENTIFICATION_JWT.md) pour comprendre l'auth, puis [TEST_DOCKER_LOCAL.md](TEST_DOCKER_LOCAL.md), puis [DEPLOIEMENT_RENDER.md](DEPLOIEMENT_RENDER.md).

---

## Fonctionnalité Web

**Oui, l'application est fonctionnelle sous format web via l'API REST.**

- **Endpoint** : `pdfapi/` (Spring Boot)
- **Port par défaut** : 8080

### 🔐 Authentification JWT (NOUVEAU)

**Tous les utilisateurs doivent s'identifier avant d'accéder aux endpoints PDF.**

#### Enregistrement et connexion

```bash
# 1. S'enregistrer
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "SecurePassword123",
    "email": "john@example.com"
  }'

# Réponse : reçoit un TOKEN JWT

# 2. Se connecter (si compte existant)
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "SecurePassword123"
  }'

# Réponse : reçoit un TOKEN JWT
```

#### Utiliser le token pour accéder aux PDFs

```bash
# Chaque requête doit inclure le header Authorization avec le token

curl -X POST http://localhost:8080/creation-pdf \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{"texte":"Mon PDF créé"}'

# Tous les endpoints PDF nécessitent ce header :
# /fusion, /decoupage, /extraction, /suppression, 
# /ajout-mot-de-passe, /conversion-image, /extraction-texte
```

#### Endpoints d'authentification

```
✅ Publics (sans token) :
   POST /auth/register    → Créer un compte
   POST /auth/login       → Se connecter
   GET  /health           → Santé de l'API

🔒 Protégés (avec token JWT) :
   POST /fusion
   POST /decoupage
   POST /extraction
   POST /suppression
   POST /ajout-mot-de-passe
   POST /conversion-image
   POST /extraction-texte
   POST /creation-pdf
```

**📖 Documentation complète:** Voir [AUTHENTIFICATION_JWT.md](AUTHENTIFICATION_JWT.md)

---
- **Routes disponibles** :
  - `POST /fusion` - Fusion de PDFs
  - `POST /decoupage` - Découpage de PDF
  - `POST /extraction` - Extraction de page
  - `POST /suppression` - Suppression de pages
  - `POST /ajout-mot-de-passe` - Ajout mot de passe
  - `POST /conversion-image` - Conversion en image
  - `POST /extraction-texte` - Extraction de texte
  - `POST /creation-pdf` - Création de PDF

**État actuel :** L'API compile avec des warnings mais fonctionne. Les dépendances CORBA peuvent nécessiter des ajustements pour le déploiement cloud.

## Architecture

```
┌─────────────────┐     CORBA/IIOP     ┌─────────────────┐     ┌─────────────────┐
│   Clients CLI   │◄──────────────────►│   Serveur CORBA │◄───►│   API Web       │
│                 │                    │                 │     │   (Spring)      │
│ - Calculatrice  │                    │ - Calculatrice  │     │                 │
│ - PDF (8 ops)   │                    │ - PdfService    │     │ - Endpoints REST │
└─────────────────┘                    └─────────────────┘     └─────────────────┘
                                                │
                                                ▼
                                       ┌─────────────────┐
                                       │    PDFBox       │
                                       │   2.0.29        │
                                       └─────────────────┘
```

## Notes

- Les PDFs sont transmis en base64 via CORBA
- Le serveur enregistre les IOR dans `ior.txt` pour fallback
- PDFBox 2.0.29 est utilisé (compatible Java 1.8)
- L'API web permet l'intégration avec des applications modernes

## Architecture

```
┌─────────────────┐     CORBA/IIOP     ┌─────────────────┐
│   Clients       │◄──────────────────►│    Serveur      │
│                 │                    │                 │
│ - StartClient   │                    │ - Calculatrice  │
│ - PdfClients    │                    │ - PdfService    │
└─────────────────┘                    └─────────────────┘
                                                │
                                                ▼
                                       ┌─────────────────┐
                                       │    PDFBox       │
                                       │   2.0.29        │
                                       └─────────────────┘
```

## Notes

- Les PDFs sont transmis en base64 via CORBA
- Le serveur enregistre les IOR dans `ior.txt` pour fallback
- PDFBox 2.0.29 est utilisé (compatible Java 1.8)