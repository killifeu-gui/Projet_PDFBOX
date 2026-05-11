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

**Étapes :**
1. Déployer sur Render comme web service en utilisant le `Dockerfile` racine
2. Configurer les variables d'environnement :
   - `ORB_INITIAL_HOST` : IP du serveur CORBA
   - `ORB_INITIAL_PORT` : 1050

**Exemple d'utilisation API :**
```bash
curl -X POST http://your-render-app.com/fusion \
  -H "Content-Type: application/json" \
  -d '{"pdf1":"base64...", "pdf2":"base64..."}'
```

#### Option 2 : Serveur CORBA complet

- Déployer comme "Background Worker" sur Render
- Nécessite un port statique et connexion persistante
- Moins adapté aux environnements cloud éphémères

## Fonctionnalité Web

**Oui, l'application est fonctionnelle sous format web via l'API REST.**

- **Endpoint** : `pdfapi/` (Spring Boot)
- **Port par défaut** : 8080
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