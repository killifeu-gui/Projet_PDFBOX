# ⚡ Quick Start - Déploiement en 5 min

## 1️⃣ Préparer GitHub

```bash
cd c:\RONDOMNUMBER9\TP_Corba
git add .
git commit -m "Ready for deployment"
git push origin main
```

## 2️⃣ Créer le service Render

1. Allez sur https://dashboard.render.com/
2. **+ New** → **Web Service**
3. Connectez votre repo `Project_PDFBOX`
4. Remplissez :
   - **Name** : `projet-pdfbox`
   - **Runtime** : `Docker`
   - Les autres champs en défaut

## 3️⃣ Ajouter les variables d'environnement

Dans **Environment** du service :

```
ORB_INITIAL_HOST = localhost (ou votre IP CORBA)
ORB_INITIAL_PORT = 1050
```

## 4️⃣ Déployer

Cliquez **Create Web Service** → Attendre 5-10 min → ✅ C'est déployé !

## 5️⃣ Tester

```bash
# Récupérez l'URL depuis le dashboard, par exemple :
# https://projet-pdfbox.onrender.com

curl -X POST https://projet-pdfbox.onrender.com/creation-pdf \
  -H "Content-Type: application/json" \
  -d '{"texte":"Hello Render"}'
```

---

## 📌 Si tu veux tester avant Render

```bash
# Test Docker local
docker build -t projet-pdfbox .
docker run -p 8080:8080 projet-pdfbox

# En parallèle, dans un autre terminal :
curl http://localhost:8080/creation-pdf \
  -H "Content-Type: application/json" \
  -d '{"texte":"Test Docker"}'
```

---

## 📚 Documentation complète

- **[DEPLOIEMENT_RENDER.md](DEPLOIEMENT_RENDER.md)** - Guide détaillé Render
- **[TEST_DOCKER_LOCAL.md](TEST_DOCKER_LOCAL.md)** - Test local avant Render
- **[CHECKLIST_DEPLOIEMENT.md](CHECKLIST_DEPLOIEMENT.md)** - Vérification complète

---

**C'est tout ! 🎉**

À chaque modification locale :
```bash
git push origin main
# → Render redéploie automatiquement
```
