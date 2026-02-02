-- ============================================
-- SCRIPT DE CRÉATION DE LA BASE DE DONNÉES
-- Application de Gestion de Restaurant
-- ============================================

-- Création de la base de données
DROP DATABASE IF EXISTS restaurant_db;
CREATE DATABASE restaurant_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE restaurant_db;

-- ============================================
-- TABLE DES UTILISATEURS
-- ============================================
CREATE TABLE utilisateurs (
    id INT PRIMARY KEY AUTO_INCREMENT,
    login VARCHAR(50) UNIQUE NOT NULL,
    mot_de_passe VARCHAR(255) NOT NULL,
    nom_complet VARCHAR(150) NOT NULL,
    role ENUM('ADMIN', 'EMPLOYE') DEFAULT 'EMPLOYE',
    email VARCHAR(150),
    telephone VARCHAR(20),
    actif BOOLEAN DEFAULT TRUE,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    derniere_connexion TIMESTAMP NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- TABLE DES CATÉGORIES
-- ============================================
CREATE TABLE categories (
    id INT PRIMARY KEY AUTO_INCREMENT,
    libelle VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- TABLE DES PRODUITS
-- ============================================
CREATE TABLE produits (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(150) NOT NULL,
    categorie_id INT NOT NULL,
    prix_vente DECIMAL(10,2) NOT NULL,
    stock_actuel INT NOT NULL DEFAULT 0,
    seuil_alerte INT NOT NULL DEFAULT 10,
    description TEXT,
    image_path VARCHAR(255),
    actif BOOLEAN DEFAULT TRUE,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_prix_vente CHECK (prix_vente > 0),
    CONSTRAINT chk_stock_actuel CHECK (stock_actuel >= 0),
    CONSTRAINT fk_produit_categorie FOREIGN KEY (categorie_id) 
        REFERENCES categories(id) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- TABLE DES MOUVEMENTS DE STOCK
-- ============================================
CREATE TABLE mouvements_stock (
    id INT PRIMARY KEY AUTO_INCREMENT,
    produit_id INT NOT NULL,
    type ENUM('ENTREE', 'SORTIE') NOT NULL,
    quantite INT NOT NULL,
    date_mouvement TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    motif VARCHAR(200) NOT NULL,
    utilisateur_id INT,
    CONSTRAINT chk_quantite_mouvement CHECK (quantite > 0),
    CONSTRAINT fk_mouvement_produit FOREIGN KEY (produit_id) 
        REFERENCES produits(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_mouvement_utilisateur FOREIGN KEY (utilisateur_id) 
        REFERENCES utilisateurs(id) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- TABLE DES COMMANDES
-- ============================================
CREATE TABLE commandes (
    id INT PRIMARY KEY AUTO_INCREMENT,
    date_commande TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    etat ENUM('EN_COURS', 'VALIDEE', 'ANNULEE') DEFAULT 'EN_COURS',
    total DECIMAL(10,2) DEFAULT 0,
    utilisateur_id INT,
    client_nom VARCHAR(150),
    client_telephone VARCHAR(20),
    notes TEXT,
    CONSTRAINT fk_commande_utilisateur FOREIGN KEY (utilisateur_id) 
        REFERENCES utilisateurs(id) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- TABLE DES LIGNES DE COMMANDE
-- ============================================
CREATE TABLE lignes_commande (
    id INT PRIMARY KEY AUTO_INCREMENT,
    commande_id INT NOT NULL,
    produit_id INT NOT NULL,
    quantite INT NOT NULL,
    prix_unitaire DECIMAL(10,2) NOT NULL,
    montant_ligne DECIMAL(10,2) NOT NULL,
    CONSTRAINT chk_quantite_ligne CHECK (quantite > 0),
    CONSTRAINT fk_ligne_commande FOREIGN KEY (commande_id) 
        REFERENCES commandes(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_ligne_produit FOREIGN KEY (produit_id) 
        REFERENCES produits(id) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- INDEX POUR OPTIMISER LES PERFORMANCES
-- ============================================
CREATE INDEX idx_produit_categorie ON produits(categorie_id);
CREATE INDEX idx_produit_nom ON produits(nom);
CREATE INDEX idx_produit_actif ON produits(actif);
CREATE INDEX idx_mouvement_produit ON mouvements_stock(produit_id);
CREATE INDEX idx_mouvement_date ON mouvements_stock(date_mouvement);
CREATE INDEX idx_mouvement_type ON mouvements_stock(type);
CREATE INDEX idx_commande_etat ON commandes(etat);
CREATE INDEX idx_commande_date ON commandes(date_commande);
CREATE INDEX idx_commande_utilisateur ON commandes(utilisateur_id);
CREATE INDEX idx_ligne_commande ON lignes_commande(commande_id);
CREATE INDEX idx_ligne_produit ON lignes_commande(produit_id);
CREATE INDEX idx_utilisateur_login ON utilisateurs(login);
CREATE INDEX idx_utilisateur_actif ON utilisateurs(actif);

-- ============================================
-- TRIGGERS POUR MISE À JOUR AUTOMATIQUE
-- ============================================

-- Trigger pour mettre à jour le total de la commande après insertion d'une ligne
DELIMITER //
CREATE TRIGGER after_ligne_insert
AFTER INSERT ON lignes_commande
FOR EACH ROW
BEGIN
    UPDATE commandes 
    SET total = (
        SELECT COALESCE(SUM(montant_ligne), 0) 
        FROM lignes_commande 
        WHERE commande_id = NEW.commande_id
    )
    WHERE id = NEW.commande_id;
END//

-- Trigger pour mettre à jour le total après suppression d'une ligne
CREATE TRIGGER after_ligne_delete
AFTER DELETE ON lignes_commande
FOR EACH ROW
BEGIN
    UPDATE commandes 
    SET total = (
        SELECT COALESCE(SUM(montant_ligne), 0) 
        FROM lignes_commande 
        WHERE commande_id = OLD.commande_id
    )
    WHERE id = OLD.commande_id;
END//

-- Trigger pour mettre à jour le total après modification d'une ligne
CREATE TRIGGER after_ligne_update
AFTER UPDATE ON lignes_commande
FOR EACH ROW
BEGIN
    UPDATE commandes 
    SET total = (
        SELECT COALESCE(SUM(montant_ligne), 0) 
        FROM lignes_commande 
        WHERE commande_id = NEW.commande_id
    )
    WHERE id = NEW.commande_id;
END//

DELIMITER ;

-- ============================================
-- DONNÉES DE DÉMARRAGE
-- ============================================

-- Utilisateurs par défaut (mot de passe hashé SHA-256)
-- admin123 = 240be518fabd2724ddb6f04eeb9d5b0f5b7c5e9e3c6d7d8e9f0a1b2c3d4e5f6a (exemple)
INSERT INTO utilisateurs (login, mot_de_passe, nom_complet, role, email) VALUES
('admin', 'admin123', 'Administrateur Système', 'ADMIN', 'admin@restaurant.com'),
('employe', 'employe123', 'Employé Test', 'EMPLOYE', 'employe@restaurant.com'),
('caissier', 'caissier123', 'Jean Dupont', 'EMPLOYE', 'jean.dupont@restaurant.com');

-- Catégories
INSERT INTO categories (libelle, description) VALUES
('Boissons', 'Boissons froides et chaudes'),
('Plats Principaux', 'Plats principaux et spécialités'),
('Desserts', 'Desserts et sucreries'),
('Entrées', 'Entrées et apéritifs'),
('Snacks', 'Snacks et encas rapides');

-- Produits
INSERT INTO produits (nom, categorie_id, prix_vente, stock_actuel, seuil_alerte, description) VALUES
-- Boissons
('Coca-Cola 33cl', 1, 2.50, 100, 20, 'Canette de Coca-Cola classique'),
('Fanta Orange 33cl', 1, 2.50, 80, 20, 'Canette de Fanta Orange'),
('Sprite 33cl', 1, 2.50, 75, 20, 'Canette de Sprite'),
('Eau Minérale 50cl', 1, 1.50, 150, 30, 'Bouteille d''eau minérale'),
('Jus d''Orange 25cl', 1, 3.00, 50, 15, 'Jus d''orange frais'),
('Café Expresso', 1, 1.80, 200, 50, 'Café expresso italien'),
('Thé Menthe', 1, 2.00, 100, 25, 'Thé à la menthe marocain'),

-- Plats Principaux
('Burger Classic', 2, 8.50, 30, 10, 'Burger boeuf avec salade, tomate, oignon'),
('Burger Cheese', 2, 9.50, 25, 10, 'Burger boeuf avec cheddar fondu'),
('Burger Double', 2, 12.00, 20, 8, 'Double steak de boeuf avec fromage'),
('Pizza Margherita', 2, 10.00, 15, 5, 'Pizza tomate, mozzarella, basilic'),
('Pizza Pepperoni', 2, 12.00, 15, 5, 'Pizza avec pepperoni épicé'),
('Tacos Poulet', 2, 7.50, 40, 10, 'Tacos garni de poulet grillé'),
('Wrap Végétarien', 2, 6.50, 25, 8, 'Wrap aux légumes frais'),

-- Desserts
('Tiramisu', 3, 5.50, 20, 5, 'Tiramisu traditionnel italien'),
('Fondant Chocolat', 3, 6.00, 15, 5, 'Fondant au chocolat noir'),
('Crème Brûlée', 3, 5.00, 18, 5, 'Crème brûlée à la vanille'),
('Salade de Fruits', 3, 4.50, 25, 8, 'Salade de fruits frais de saison'),
('Glace 2 Boules', 3, 3.50, 50, 15, 'Glace artisanale au choix'),

-- Entrées
('Salade César', 4, 7.00, 20, 5, 'Salade César avec poulet grillé'),
('Soupe du Jour', 4, 4.50, 30, 10, 'Soupe fraîche du jour'),
('Bruschetta', 4, 5.50, 25, 8, 'Bruschetta tomate basilic'),

-- Snacks
('Frites Portion', 5, 3.50, 60, 15, 'Portion de frites croustillantes'),
('Nuggets x6', 5, 5.00, 40, 10, 'Box de 6 nuggets de poulet'),
('Onion Rings', 5, 4.00, 35, 10, 'Rondelles d''oignon panées');

-- Quelques mouvements de stock initiaux
INSERT INTO mouvements_stock (produit_id, type, quantite, motif, utilisateur_id) VALUES
(1, 'ENTREE', 100, 'Stock initial', 1),
(2, 'ENTREE', 80, 'Stock initial', 1),
(8, 'ENTREE', 30, 'Stock initial', 1),
(9, 'ENTREE', 25, 'Stock initial', 1);

-- Quelques commandes de test
INSERT INTO commandes (etat, utilisateur_id, client_nom, client_telephone, total) VALUES
('VALIDEE', 2, 'Martin Legrand', '0612345678', 23.50),
('VALIDEE', 2, 'Sophie Durant', '0698765432', 18.00),
('EN_COURS', 3, NULL, NULL, 0);

-- Lignes de commande pour les commandes de test
INSERT INTO lignes_commande (commande_id, produit_id, quantite, prix_unitaire, montant_ligne) VALUES
(1, 8, 2, 8.50, 17.00),
(1, 1, 2, 2.50, 5.00),
(1, 24, 1, 3.50, 3.50),
(2, 11, 1, 10.00, 10.00),
(2, 4, 2, 1.50, 3.00),
(2, 18, 1, 5.00, 5.00);

-- ============================================
-- FIN DU SCRIPT
-- ============================================
