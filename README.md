# 🍽️ Restaurant Manager

Application de bureau complète pour la gestion d'un restaurant/fast-food, développée en **Java SE** avec **Swing** et **FlatLaf**.

![Java](https://img.shields.io/badge/Java-11+-orange?style=flat-square&logo=java)
![Swing](https://img.shields.io/badge/GUI-Swing-blue?style=flat-square)
![MySQL](https://img.shields.io/badge/Database-MySQL-blue?style=flat-square&logo=mysql)
![FlatLaf](https://img.shields.io/badge/Theme-FlatLaf-green?style=flat-square)

---

## Fonctionnalités

### Authentification
- Connexion sécurisée avec hachage SHA-256
- Gestion des rôles (ADMIN, MANAGER, CAISSIER)
- Session utilisateur avec contrôle d'accès

### Dashboard
- Statistiques en temps réel (CA du jour, commandes, produits)
- Graphiques de ventes (courbe 7 derniers jours)
- Commandes récentes

### Gestion des Catégories
- CRUD complet (Créer, Lire, Modifier, Supprimer)
- Recherche avec filtrage
- Activation/Désactivation

### Gestion des Produits
- CRUD complet avec prix et stock
- Association aux catégories
- Recherche multicritères

### Gestion des Stocks
- Entrées et sorties de stock
- Historique des mouvements
- Alertes de stock bas

### Gestion des Commandes
- Création de commandes avec lignes de détail
- États : EN_ATTENTE, EN_PREPARATION, PRETE, LIVREE, ANNULEE
- Calcul automatique des totaux

### Statistiques
- Chiffre d'affaires par période
- Produits les plus vendus
- Évolution des ventes

### Gestion des Utilisateurs (Admin)
- CRUD des utilisateurs
- Attribution des rôles
- Activation/Désactivation des comptes

---

## Technologies

| Composant | Technologie |
|-----------|-------------|
| Langage | Java SE 11+ |
| Interface | Swing |
| Thème | FlatLaf 3.2.5 |
| Police | Montserrat |
| Base de données | MySQL 8.0 |
| Connecteur | mysql-connector-j 8.0.33 |
| IDE | NetBeans |

---

## Installation

### Prérequis
- Java JDK 11 ou supérieur
- MySQL 8.0 ou supérieur
- NetBeans IDE (recommandé)

### 1. Cloner le projet
```bash
git clone https://github.com/lolorine16/miniature-system.git restaurant-app

cd restaurant-app
```

### 2. Créer la base de données
```bash
# Se connecter à MySQL
mysql -u root -p

# Exécuter le script SQL
source sql/create_database.sql
```

### 3. Télécharger les dépendances

#### FlatLaf
Téléchargez depuis [Maven Central](https://mvnrepository.com/artifact/com.formdev/flatlaf/3.2.5) :
- `flatlaf-3.2.5.jar`

Placez le fichier dans le dossier `lib/` du projet.

#### MySQL Connector
Téléchargez depuis [Maven Central](https://mvnrepository.com/artifact/com.mysql/mysql-connector-j/8.0.33) :
- `mysql-connector-j-8.0.33.jar`

Placez le fichier dans le dossier `lib/` du projet.


### 4. Configurer la connexion base de données
Modifiez le fichier `src/restaurant/app/config.properties` :
```properties
db.url=jdbc:mysql://localhost:3306/restaurant_db
db.user=restaurant_user
db.password=restaurant123
```

### 5. Configurer le projet NetBeans
1. Ouvrir le projet dans NetBeans
2. Clic droit sur le projet → Properties
3. Libraries → Add JAR/Folder
4. Ajouter `lib/flatlaf-3.2.5.jar` et `lib/mysql-connector-j-8.0.33.jar`

### 6. Compiler et exécuter
```bash
# Via NetBeans : F6 ou Run → Run Project

# Via ligne de commande (après build)
java -cp "build/classes:lib/*" restaurant.app.Main
```

---

## Comptes par défaut

| Rôle | Login | Mot de passe |
|------|-------|--------------|
| Admin | admin | admin123 |
| Employe | employe | employe123 |
| Caissier | caissier | caissier123 |

---

## Structure du projet

```
restaurant-app/
├── src/
│   ├── fonts/                          # Polices Montserrat
│   └── restaurant/app/
│       ├── Main.java                   # Point d'entrée
│       ├── config.properties           # Configuration
│       ├── model/
│       │   ├── entities/               # Entités métier
│       │   │   ├── Categorie.java
│       │   │   ├── Produit.java
│       │   │   ├── Commande.java
│       │   │   ├── LigneCommande.java
│       │   │   ├── MouvementStock.java
│       │   │   └── Utilisateur.java
│       │   ├── enums/                  # Énumérations
│       │   │   ├── EtatCommande.java
│       │   │   ├── TypeMouvement.java
│       │   │   └── RoleUtilisateur.java
│       │   └── exceptions/             # Exceptions personnalisées
│       ├── dao/                        # Accès aux données
│       │   ├── CategorieDAO.java
│       │   ├── ProduitDAO.java
│       │   ├── CommandeDAO.java
│       │   ├── LigneCommandeDAO.java
│       │   ├── MouvementStockDAO.java
│       │   └── UtilisateurDAO.java
│       ├── controller/                 # Contrôleurs
│       │   ├── CategorieController.java
│       │   ├── ProduitController.java
│       │   ├── CommandeController.java
│       │   ├── StockController.java
│       │   ├── UtilisateurController.java
│       │   └── StatistiqueController.java
│       ├── view/                       # Vues Swing
│       │   ├── LoginFrame.java
│       │   ├── MainFrame.java
│       │   ├── components/             # Composants réutilisables
│       │   │   ├── ModernButton.java
│       │   │   ├── ModernTextField.java
│       │   │   ├── ModernPasswordField.java
│       │   │   ├── ModernTable.java
│       │   │   ├── DashboardCard.java
│       │   │   └── SearchField.java
│       │   ├── panels/                 # Panneaux de contenu
│       │   │   ├── DashboardPanel.java
│       │   │   ├── CategoriePanel.java
│       │   │   ├── ProduitPanel.java
│       │   │   ├── StockPanel.java
│       │   │   ├── CommandePanel.java
│       │   │   ├── StatistiquePanel.java
│       │   │   └── UtilisateurPanel.java
│       │   └── dialogs/                # Dialogues modaux
│       │       ├── CategorieDialog.java
│       │       ├── ProduitDialog.java
│       │       ├── MouvementStockDialog.java
│       │       └── UtilisateurDialog.java
│       └── util/                       # Utilitaires
│           ├── DatabaseConnection.java
│           ├── ValidationUtil.java
│           ├── SecurityUtil.java
│           └── DateUtil.java
├── sql/
│   └── create_database.sql             # Script de création BDD
├── lib/                                # Bibliothèques JAR
│   ├── flatlaf-3.2.5.jar
│   └── mysql-connector-j-8.0.33.jar
├── nbproject/                          # Configuration NetBeans
└── README.md
```

---

## Design

L'application utilise un design moderne avec :
- **FlatLaf** 
- **Montserrat**
- Palette de couleurs :
  - Primary : `#E67E22` (Orange)
  - Secondary : `#34495E` (Gris foncé)
  - Success : `#27AE60` (Vert)
  - Danger : `#E74C3C` (Rouge)
  - Background : `#F5F6FA` (Gris clair)

---
## Instructions du projet

[Project specification](docs/TP_POO_2026.pdf)


## Auteurs

*GROUPE 13*
Développé par **lolorine16** && **shekina16**
