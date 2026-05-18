# Guide de déploiement sur Render

## Prérequis
- Un repo GitHub connecté à Render
- Votre repo `Project_PDFBOX` pushé sur GitHub avec les derniers fichiers

## Étapes de déploiement

### 1. Préparation GitHub

Assurez-vous que tous les fichiers sont à jour dans GitHub :

```bash
cd c:\RONDOMNUMBER9\TP_Corba
git add .
git commit -m "Setup Render deployment with Docker"
git push origin main
```

Vérifiez que les fichiers suivants sont présents dans le repo :
- ✅ `Dockerfile` (racine)
- ✅ `.dockerignore` (racine)
- ✅ `pdfapi/pom.xml` (avec GlassFish CORBA)
- ✅ `pdfapi/mvnw` (wrapper Maven)
- ✅ `pdfapi/.mvn/wrapper/maven-wrapper.properties`
- ✅ `src/CalculatriceApp/*.java` (stubs CORBA)

### 2. Créer un service Web sur Render

1. Allez sur https://dashboard.render.com/
2. Connectez-vous avec votre compte GitHub
3. Cliquez sur **+ New** en haut à droite
4. Sélectionnez **Web Service**

### 3. Connecter le repo GitHub

1. Sous "GitHub repository", cliquez sur **Search for a repository**
2. Tapez `killifeu-gui/Project_PDFBOX` (ou le nom exact)
3. Sélectionnez votre repo
4. Cliquez sur **Connect**

### 4. Configuration de build

Remplissez les champs comme suit :

| Champ | Valeur |
|-------|--------|
| Name | `projet-pdfbox` |
| Runtime | `Docker` |
| Root Directory | `/` (vide ou /) |
| Build Command | (Laisser vide, Render utilisera le Dockerfile) |
| Start Command | (Laisser vide, le Dockerfile a un ENTRYPOINT) |

### 5. Configuration d'environnement

1. Cliquez sur l'onglet **Environment**
2. Ajoutez les variables :

| Key | Value | Scope |
|-----|-------|-------|
| `ORB_INITIAL_HOST` | `<CORBA_HOST>` — hôte du serveur CORBA accessible depuis Render | Runtime |
| `ORB_INITIAL_PORT` | `1050` | Runtime |

> Important : n’utilisez `localhost` que pour les tests en local. Sur Render, le service CORBA doit être accessible depuis le cloud.

### 6. Plan et région

1. **Instance Type** : Free (gratuit, suffisant pour tests)
2. **Region** : `Oregon` (ou la région la plus proche)
3. **Auto-Deploy** : Oui (déploie automatiquement à chaque push `main`)

### 7. Déploiement

1. Cliquez sur **Create Web Service** en bas
2. Render va :
   - Cloner votre repo GitHub
   - Construire l'image Docker
   - Déployer le conteneur
   - Attribuer une URL du type `https://projet-pdfbox.onrender.com`

⏳ **Temps de déploiement** : 5-10 minutes pour la première fois

### 8. Vérifier le déploiement

1. Sur le dashboard Render, sélectionnez votre service
2. Onglet **Logs** : cherchez les messages de démarrage de Spring Boot
3. Cherchez : `Tomcat started on port(s): 8080`
4. L'URL du service s'affiche en haut du dashboard

### 9. Tester l'API

Une fois déployé, testez une requête :

```bash
curl -X POST https://projet-pdfbox.onrender.com/creation-pdf \
  -H "Content-Type: application/json" \
  -d '{"texte":"Bonjour depuis Render!"}'
```

## Mise à jour du déploiement

À chaque modification locale :

```bash
git add .
git commit -m "Description du changement"
git push origin main
```

Render redéploiera automatiquement en 2-5 minutes.

## Dépannage

### Erreur : "failed to read dockerfile"
- ✅ Vérifiez que `Dockerfile` existe à la racine
- ✅ Vérifiez le chemin des fichiers copyés dans le Dockerfile

### Erreur : "Cannot resolve org.glassfish.corba"
- Maven n'a pas accès aux dépendances
- Vérifiez votre connexion Internet dans le build Docker
- Vérifiez que le repo central Maven est accessible

### Erreur : "ORB_INITIAL_HOST unreachable"
- Le service CORBA n'est pas accessible depuis Render
- Solution 1 : Hébergez aussi le serveur CORBA sur Render
- Solution 2 : Exposez votre serveur CORBA local avec ngrok
- Solution 3 : Utilisez une VPN privée Render

### Port 8080 déjà utilisé
- Render gère automatiquement les ports
- Ne configurez pas `PORT` = 8080 dans les variables si Render l'assigne
- Supprimez `PORT` de la configuration Render si vous utilisez Docker, car Render injecte déjà la bonne valeur au runtime

## Endpoints disponibles après déploiement

POST `https://votre-domaine.onrender.com/` :

- `/fusion` - Fusion de PDFs
- `/decoupage` - Découpage
- `/extraction` - Extraction de page
- `/suppression` - Suppression de pages
- `/ajout-mot-de-passe` - Ajout mot de passe
- `/conversion-image` - Conversion PNG
- `/extraction-texte` - Extraction texte
- `/creation-pdf` - Création PDF

Tous les endpoints acceptent du JSON Base64 pour les PDFs.

## Coûts sur Render

- **Free** : 
  - $0/mois
  - Hibernation après 15 min d'inactivité
  - 100 GB de bande passante/mois

- **Paid** :
  - $7+/mois
  - Service toujours actif
  - Recommandé pour production
