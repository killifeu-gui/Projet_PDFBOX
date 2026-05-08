# TP_Corba - PDF CORBA Extension (TODO)

- [ ] 1. Mettre à jour `src/Addition.idl` : ajouter une interface `PdfService` avec 8 opérations PDF (fusion, découpage, extraction page, suppression page, ajout mot de passe, conversion en image, extraction texte, création de PDF) + types/résultats.
- [ ] 2. Générer / mettre à jour les stubs CORBA Java à partir du nouvel IDL (idlj).
- [ ] 3. Implémenter `src/CalculatriceServer/PdfServiceImpl.java` en utilisant PDFBox 1.8 (chargement, manipulation pages, protection, rendu images, extraction texte, création PDF).
- [ ] 4. Modifier `src/CalculatriceServer/StartServer.java` pour enregistrer `PdfService` au NamingService sous un nouveau nom.
- [ ] 5. Ajouter 4 clients supplémentaires sous `src/CalculatriceClient/` qui appellent les méthodes PDF avec entrées/sorties en base64.
- [ ] 6. Ajouter helpers Java pour base64 (encode/decode) et pour sérialiser paramètres (ranges pages, format image, etc.) si nécessaire.
- [ ] 7. Compiler le projet et vérifier l’exécution : lancer serveur puis lancer chaque client.

