# TP_Corba - PDF CORBA Extension (STATUS)

## ✅ Tâches terminées

- [x] 1. Mettre à jour `src/Addition.idl` : interface `PdfService` avec 8 opérations PDF (fusion, découpage, extraction page, suppression page, ajout mot de passe, conversion en image, extraction texte, création de PDF) + types/résultats.
- [x] 2. Générer les stubs CORBA Java à partir du nouvel IDL (idlj).
- [x] 3. Implémenter `src/CalculatriceServer/PdfServiceImpl.java` en utilisant PDFBox 2.0.29 (chargement, manipulation pages, protection, rendu images, extraction texte, création PDF).
- [x] 4. Modifier `src/CalculatriceServer/StartServer.java` pour enregistrer `PdfService` au NamingService sous un nouveau nom.
- [x] 5. Ajouter 4 clients supplémentaires sous `src/CalculatriceClient/` qui appellent les méthodes PDF avec entrées/sorties en base64.
- [x] 6. Helpers Java pour base64 inclus dans les classes PdfServiceImpl et clients.
- [x] 7. Compiler le projet et vérifier l'exécution : script `compile.bat` créé et testé.
- [x] 8. Correction des erreurs de syntaxe dans StartServer.java (try-catch mal imbriqué).
- [x] 9. Création des scripts de déploiement (run_server.bat, README.md).

## 📋 Structure finale

```
TP_Corba/
├── src/
│   ├── Addition.idl                    # IDL avec Calculatrice + PdfService
│   ├── CalculatriceServer/
│   │   ├── CalculatriceImpl.java       # Service calculatrice (add, sub, mul, div, mod, power)
│   │   ├── PdfServiceImpl.java         # Service PDF (8 opérations)
│   │   └── StartServer.java            # Serveur CORBA
│   ├── CalculatriceClient/
│   │   ├── StartClient.java            # Client interactif calculatrice
│   │   ├── StartPdfClientFusionDecoupage.java
│   │   ├── StartPdfClientExtractionSuppression.java
│   │   ├── StartPdfClientMotDePasseEtCreation.java
│   │   └── StartPdfClientConversionEtTexte.java
│   └── CalculatriceApp/                # Sources IDL (référence)
├── CalculatriceApp/                    # Stubs générés par idlj
├── bin/                                # Classes compilées
├── lib/
│   ├── pdfbox-2.0.29.jar              # PDFBox 2.0.29
│   └── commons-logging-1.2.jar        # Logging
├── compile.bat                         # Script de compilation
├── run_server.bat                      # Script de démarrage
├── README.md                           # Documentation
└── TODO.md                             # Ce fichier
```

## 🚀 Utilisation rapide

1. **Compiler :** `compile.bat`
2. **Démarrer serveur :** `run_server.bat`
3. **Lancer client :** `java -cp bin;lib/pdfbox-2.0.29.jar;lib/commons-logging-1.2.jar StartClient -ORBInitialHost localhost -ORBInitialPort 1050`

## 📝 Notes

- Projet compilé avec succès
- Tous les services CORBA sont opérationnels
- 4 clients PDF + 1 client Calculatrice disponibles
- PDFBox 2.0.29 utilisé (compatible Java 1.8)