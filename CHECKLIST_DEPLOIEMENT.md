# 🚀 Fichiers et configuration pour le déploiement

## ✅ Fichiers de déploiement créés/modifiés

### À la racine du repo

| Fichier | Rôle | Statut |
|---------|------|--------|
| `Dockerfile` | Build et exécution du conteneur | ✅ Créé |
| `.dockerignore` | Exclusion des fichiers non nécessaires | ✅ Créé |
| `render.yaml` | Configuration Render (optionnel) | ✅ Créé |
| `DEPLOIEMENT_RENDER.md` | Guide complet Render | ✅ Créé |
| `TEST_DOCKER_LOCAL.md` | Guide test Docker local | ✅ Créé |
| `README.md` | Mis à jour avec déploiement | ✅ Modifié |

### Dans `pdfapi/`

| Fichier | Rôle | Changements |
|---------|------|------------|
| `pom.xml` | Build Maven | ✅ JacORB 3.11.0, build-helper-plugin |
| `src/main/java/.../CorbaService.java` | Connexion CORBA | ✅ Env vars ORB_INITIAL_HOST/PORT |
| `src/main/java/.../PdfController.java` | API REST | ✅ PlagePages via fields |

### Architecture du projet pour Render

```
Project_PDFBOX/
├── Dockerfile                 ← Build multi-stage
├── .dockerignore
├── render.yaml               ← Config Render (optionnel)
├── DEPLOIEMENT_RENDER.md     ← Guide step-by-step
├── TEST_DOCKER_LOCAL.md      ← Test Docker en local
├── README.md                 ← Mis à jour
│
├── pdfapi/
│   ├── pom.xml              ← JacORB + build-helper
│   ├── mvnw / .mvn
│   ├── src/main/
│   │   └── java/com/babacar/pdfapi/
│   │       ├── CorbaService.java       ← Env vars
│   │       ├── PdfController.java      ← PlagePages fix
│   │       └── PdfapiApplication.java
│
├── src/
│   ├── Addition.idl
│   └── CalculatriceApp/          ← Sources CORBA générées
│       └── *.java (stubs)
│
└── ... (fichiers CLI, serveur, etc.)
```

## 🔧 Flux de déploiement

### Local → GitHub

```bash
cd c:\RONDOMNUMBER9\TP_Corba

# 1. Tester Docker localement
docker build -t projet-pdfbox .
docker run -p 8080:8080 projet-pdfbox

# 2. Si OK, pousser vers GitHub
git add .
git commit -m "Ready for Render deployment"
git push origin main
```

### GitHub → Render

1. Allez sur https://dashboard.render.com/
2. **+ New** → **Web Service**
3. Connectez `Project_PDFBOX`
4. Render détecte automatiquement le `Dockerfile`
5. Configurer les env vars (voir `DEPLOIEMENT_RENDER.md`)
6. Cliquez **Create** → Déploiement automatique en 5-10 min

### Mise à jour continue

À chaque modification :
```bash
git push origin main
# Render redéploie automatiquement
```

## 📋 Checklist avant déploiement

- [ ] `Dockerfile` est à la racine du repo
- [ ] `pdfapi/pom.xml` contient `org.jacorb:jacorb:3.11.0`
- [ ] `src/CalculatriceApp/*.java` sont présents (stubs CORBA)
- [ ] `CorbaService.java` utilise les env vars `ORB_INITIAL_HOST` et `ORB_INITIAL_PORT`
- [ ] `PdfController.java` crée `PlagePages` sans constructeur
- [ ] Tous les fichiers sont committés dans Git
- [ ] Le repo GitHub est public ou accessible via token d'authentification
- [ ] Render peut accéder au serveur CORBA (via IP publique ou VPN)

## 🐳 Taille de l'image Docker

Estimation :
- Base image Java 17 : ~200 MB
- Maven + dépendances : ~250 MB
- **Total** : ~450-500 MB

## 💰 Coûts Render

| Plan | Prix | Avantages |
|------|------|-----------|
| **Free** | $0 | Test, 15 min inactivité = hibernation |
| **Paid** | $7+/mois | Service toujours actif, performances meilleures |

## 🔗 Liens utiles

- Render Dashboard : https://dashboard.render.com/
- Documentation Render Docker : https://render.com/docs/docker
- Documentation Spring Boot Render : https://render.com/docs/deploy-spring
- Exemple : `https://projet-pdfbox.onrender.com/`

## 📞 Support Render

Si vous avez des problèmes :
1. Consultez les logs Render (Dashboard → Logs)
2. Vérifiez `DEPLOIEMENT_RENDER.md` section "Dépannage"
3. Contact support : https://support.render.com/

## Prochaines étapes

1. **Tester localement** : Suivez `TEST_DOCKER_LOCAL.md`
2. **Pousser vers GitHub** : `git push origin main`
3. **Créer le service Render** : Suivez `DEPLOIEMENT_RENDER.md`
4. **Configurer les env vars** : Pointez vers votre serveur CORBA
5. **Tester l'API** : Utilisez l'URL fournie par Render
