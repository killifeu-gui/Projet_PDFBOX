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

## Mode sans NamingService

Si le NamingService n'est pas disponible, les clients peuvent utiliser le fichier `ior.txt` généré automatiquement.

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