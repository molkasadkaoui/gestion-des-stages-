# 🎓 Gestion des Stages - Application Web

Plateforme de gestion des stages pour La Poste Tunisienne, permettant la candidature, l'affectation d'encadrants, et le suivi des rapports.

## 📋 Prérequis

Avant de démarrer, assure-toi d'avoir installé :
- **Java 17+** (pour le backend Spring Boot)
- **Node.js 18+** (pour le frontend Angular)
- **npm** (gestionnaire de paquets Node)
- **MySQL 8+** ou une base de données compatible

## 🚀 Guide de Démarrage Rapide

### 1️⃣ Démarrer le Backend (Spring Boot)

```bash
cd backend
./mvnw spring-boot:run
```

Le backend démarre sur `http://localhost:8081`

### 2️⃣ Démarrer le Frontend (Angular)

Dans un **nouveau terminal** :

```bash
cd frontend
npm install
npm run dev
```

Le frontend démarre sur `http://localhost:4200`

## 🔐 Identifiants de Test

Utilise ces comptes pour tester l'application :

### Stagiaire
- Email: `stagiaire@test.com`
- Mot de passe: `Password123`

### Encadrant
- Email: `encadrant@test.com`
- Mot de passe: `Password123`

### Administrateur
- Email: `admin@test.com`
- Mot de passe: `Password123`

## 🎯 Fonctionnalités Principales

### Pour les Stagiaires
- ✅ Consulter la liste des stages disponibles
- ✅ Postuler à un stage
- ✅ Voir sa candidature et son affectation
- ✅ Déposer un rapport de stage

### Pour les Encadrants
- ✅ Consulter les stages des stagiaires affectés
- ✅ Voir la liste des rapports à traiter
- ✅ Valider ou rejeter les rapports
- ✅ Recevoir des notifications

### Pour l'Administrateur
- ✅ Gérer les utilisateurs (stagiaires, encadrants, admins)
- ✅ Gérer les stages
- ✅ Consulter le tableau de bord avec les statistiques
- ✅ Gérer les candidatures et affectations

## 📧 Configuration Email

L'application envoie des emails via Gmail. Pour que cela fonctionne :

1. Configure les variables d'environnement ou modifie `application.properties` :
```properties
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
```

2. Utilise un [App Password Gmail](https://support.google.com/accounts/answer/185833) pour plus de sécurité

## 🗄️ Base de Données

La base de données se configure automatiquement au démarrage grâce à **Flyway**.

Les migrations SQL se trouvent dans : `backend/src/main/resources/db/migration/`

## 📁 Structure du Projet

```
gestion-stages/
├── backend/                 # API Spring Boot
│   ├── src/main/java/
│   ├── pom.xml
│   └── mvnw
├── frontend/                # Application Angular
│   ├── src/
│   ├── package.json
│   └── angular.json
└── README.md
```

## 🛠️ Troubleshooting

### Le backend ne démarre pas
```bash
# Nettoie et recompile
cd backend
./mvnw clean package -DskipTests
./mvnw spring-boot:run
```

### Le frontend affiche des erreurs
```bash
# Nettoie le cache et redémarre
cd frontend
rm -rf node_modules dist
npm install
npm run dev
# Puis recharge le navigateur avec Ctrl+Shift+R
```

### Les emails ne s'envoient pas
- Vérifie que Gmail SMTP est activé
- Utilise un App Password au lieu du mot de passe Gmail
- Regarde les logs du backend pour les erreurs

### La base de données ne se crée pas
- Vérifie que MySQL est en cours d'exécution
- Vérifie les identifiants dans `application.properties`
- Consulte les logs Spring pour les erreurs Flyway

## 🔄 Workflow de Déploiement

1. **Développement local** → Tester en local avec les deux serveurs
2. **Build production** → `npm run build` (frontend) et `./mvnw package` (backend)
3. **Déploiement** → Utiliser le JAR généré et le dist/ du frontend

## 📞 Support

Pour toute question ou problème, contacte l'équipe de développement.

---

**Fait avec ❤️ pour La Poste Tunisienne**
