# Guide de test Docker local

## Prérequis
- Docker Desktop installé et en cours d'exécution
- Git bash ou terminal standard (pas PowerShell si possible)

## Build local du conteneur Docker

### 1. Construire l'image

```bash
cd c:\RONDOMNUMBER9\TP_Corba
docker build -t projet-pdfbox:latest .
```

**Sortie attendue** :
```
[+] Building ... 
...
 => [stage-1 3/3] COPY --from=build /app/pdfapi/target/*.jar /app/app.jar
 => exporting to image
 => => naming to docker.io/library/projet-pdfbox:latest
```

### 2. Exécuter le conteneur

```bash
docker run -p 8080:8080 \
  -e ORB_INITIAL_HOST=localhost \
  -e ORB_INITIAL_PORT=1050 \
  projet-pdfbox:latest
```

**Sortie attendue** :
```
. ____ _ __ _ _
 /\\ / ___'_ __ _ _(_)_ __ __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                   (v3.2.5)

2026-05-11 ... INFO ... Tomcat started on port(s): 8080
```

### 3. Tester l'API

En parallèle, ouvrez un autre terminal :

```bash
# Test simple : création de PDF
curl -X POST http://localhost:8080/creation-pdf \
  -H "Content-Type: application/json" \
  -d '{"texte":"Test depuis Docker"}'
```

### 4. Arrêter le conteneur

```bash
Ctrl+C
```

Ou depuis un autre terminal :
```bash
docker ps  # Trouvez le CONTAINER ID
docker stop <CONTAINER_ID>
```

## Résoudre les problèmes Docker

### Erreur : "Cannot find Dockerfile"
```bash
docker build -f ./Dockerfile -t projet-pdfbox .
```

### Erreur : "Cannot connect to Docker daemon"
- Assurez-vous que **Docker Desktop** est lancé
- Redémarrez Docker Desktop

### Build très lent (première fois)
- Maven télécharge toutes les dépendances (~200 MB)
- C'est normal, cela peut prendre 5-10 minutes

### Erreur : "Maven failed to build"
```bash
# Reconstruisez en forçant un nettoyage
docker build --no-cache -t projet-pdfbox .
```

## Vérifier l'image créée

```bash
docker images | grep projet-pdfbox
```

Sortie :
```
projet-pdfbox              latest       abc123def456   2 minutes ago   500MB
```

## Nettoyer les images/conteneurs

```bash
# Supprimer l'image
docker rmi projet-pdfbox

# Supprimer tous les conteneurs arrêtés
docker container prune

# Supprimer tout (attention!)
docker system prune -a
```

## Variables d'environnement pour le test

Si votre serveur CORBA n'est pas sur localhost:1050, modifiez :

```bash
docker run -p 8080:8080 \
  -e ORB_INITIAL_HOST=192.168.1.100 \
  -e ORB_INITIAL_PORT=1050 \
  projet-pdfbox:latest
```

## Accès au conteneur en cours d'exécution

```bash
docker exec -it <CONTAINER_ID> /bin/sh
```

Cela vous donne un shell dans le conteneur pour déboguer.

## Une fois que c'est OK localement

```bash
git add .
git commit -m "Docker ready for Render"
git push origin main
```

Render détectera le push et déploiera automatiquement !
