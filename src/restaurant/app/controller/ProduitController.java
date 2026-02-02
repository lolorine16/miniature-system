package restaurant.app.controller;

import java.sql.SQLException;
import java.util.List;
import restaurant.app.dao.ProduitDAO;
import restaurant.app.model.entities.Produit;

/**
 * Contrôleur pour la gestion des produits.
 * 
 * @author Restaurant App
 * @version 1.0
 */
public class ProduitController {
    
    private final ProduitDAO produitDAO;
    
    /**
     * Constructeur par défaut.
     */
    public ProduitController() {
        this.produitDAO = new ProduitDAO();
    }
    
    /**
     * Récupère tous les produits.
     * @return Liste des produits
     * @throws Exception en cas d'erreur
     */
    public List<Produit> getAllProduits() throws Exception {
        try {
            return produitDAO.findAll();
        } catch (SQLException e) {
            throw new Exception("Erreur lors de la récupération des produits: " + e.getMessage(), e);
        }
    }
    
    /**
     * Récupère les produits actifs uniquement.
     * @return Liste des produits actifs
     * @throws Exception en cas d'erreur
     */
    public List<Produit> getProduitsActifs() throws Exception {
        try {
            return produitDAO.findActifs();
        } catch (SQLException e) {
            throw new Exception("Erreur lors de la récupération des produits: " + e.getMessage(), e);
        }
    }
    
    /**
     * Récupère les produits d'une catégorie.
     * @param categorieId L'identifiant de la catégorie
     * @return Liste des produits
     * @throws Exception en cas d'erreur
     */
    public List<Produit> getProduitsByCategorie(int categorieId) throws Exception {
        try {
            return produitDAO.findByCategorie(categorieId);
        } catch (SQLException e) {
            throw new Exception("Erreur lors de la récupération des produits: " + e.getMessage(), e);
        }
    }
    
    /**
     * Récupère les produits en alerte de stock.
     * @return Liste des produits en alerte
     * @throws Exception en cas d'erreur
     */
    public List<Produit> getProduitsEnAlerte() throws Exception {
        try {
            return produitDAO.findEnAlerte();
        } catch (SQLException e) {
            throw new Exception("Erreur lors de la récupération des produits en alerte: " + e.getMessage(), e);
        }
    }
    
    /**
     * Récupère un produit par son ID.
     * @param id L'identifiant
     * @return Le produit ou null
     * @throws Exception en cas d'erreur
     */
    public Produit getProduitById(int id) throws Exception {
        try {
            return produitDAO.findById(id);
        } catch (SQLException e) {
            throw new Exception("Erreur lors de la récupération du produit: " + e.getMessage(), e);
        }
    }
    
    /**
     * Recherche des produits avec critères.
     * @param searchText Le texte à rechercher (dans le nom)
     * @param categorieId L'identifiant de la catégorie (0 pour tous)
     * @param actifsUniquement True pour les actifs uniquement
     * @param enAlerteUniquement True pour les produits en alerte uniquement
     * @return Liste des produits correspondants
     * @throws Exception en cas d'erreur
     */
    public List<Produit> searchProduits(String searchText, int categorieId, 
                                        boolean actifsUniquement, boolean enAlerteUniquement) throws Exception {
        try {
            return produitDAO.search(searchText, categorieId, actifsUniquement, enAlerteUniquement);
        } catch (SQLException e) {
            throw new Exception("Erreur lors de la recherche des produits: " + e.getMessage(), e);
        }
    }
    
    /**
     * Recherche simple des produits par nom.
     * @param searchText Le texte à rechercher
     * @return Liste des produits correspondants
     * @throws Exception en cas d'erreur
     */
    public List<Produit> searchProduits(String searchText) throws Exception {
        return searchProduits(searchText, 0, false, false);
    }
    
    /**
     * Crée un nouveau produit.
     * @param produit Le produit à créer
     * @return Le produit créé avec son ID
     * @throws Exception en cas d'erreur ou de validation
     */
    public Produit createProduit(Produit produit) throws Exception {
        validateProduit(produit);
        
        try {
            return produitDAO.insert(produit);
        } catch (SQLException e) {
            throw new Exception("Erreur lors de la création du produit: " + e.getMessage(), e);
        }
    }
    
    /**
     * Met à jour un produit existant.
     * @param produit Le produit à mettre à jour
     * @return Le produit mis à jour
     * @throws Exception en cas d'erreur ou de validation
     */
    public Produit updateProduit(Produit produit) throws Exception {
        if (produit.getId() <= 0) {
            throw new Exception("L'identifiant du produit est invalide");
        }
        
        validateProduit(produit);
        
        try {
            if (!produitDAO.update(produit)) {
                throw new Exception("Le produit n'a pas pu être mis à jour");
            }
            return produit;
        } catch (SQLException e) {
            throw new Exception("Erreur lors de la mise à jour du produit: " + e.getMessage(), e);
        }
    }
    
    /**
     * Valide les données d'un produit.
     * @param produit Le produit à valider
     * @throws Exception si les données sont invalides
     */
    private void validateProduit(Produit produit) throws Exception {
        if (produit.getNom() == null || produit.getNom().trim().isEmpty()) {
            throw new Exception("Le nom du produit est obligatoire");
        }
        
        if (produit.getPrixVente() <= 0) {
            throw new Exception("Le prix du produit doit être positif");
        }
        
        if (produit.getStockActuel() < 0) {
            throw new Exception("Le stock ne peut pas être négatif");
        }
        
        if (produit.getCategorie() == null || produit.getCategorie().getId() <= 0) {
            throw new Exception("La catégorie est obligatoire");
        }
    }
    
    /**
     * Met à jour le stock d'un produit.
     * @param produitId L'identifiant du produit
     * @param nouveauStock Le nouveau stock
     * @throws Exception en cas d'erreur
     */
    public void updateStock(int produitId, int nouveauStock) throws Exception {
        if (nouveauStock < 0) {
            throw new Exception("Le stock ne peut pas être négatif");
        }
        
        try {
            if (!produitDAO.updateStock(produitId, nouveauStock)) {
                throw new Exception("Le stock n'a pas pu être mis à jour");
            }
        } catch (SQLException e) {
            throw new Exception("Erreur lors de la mise à jour du stock: " + e.getMessage(), e);
        }
    }
    
    /**
     * Supprime un produit.
     * @param id L'identifiant du produit
     * @throws Exception en cas d'erreur
     */
    public void deleteProduit(int id) throws Exception {
        try {
            if (!produitDAO.delete(id)) {
                throw new Exception("Le produit n'a pas pu être supprimé");
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("foreign key") || e.getMessage().contains("FOREIGN KEY")) {
                throw new Exception("Impossible de supprimer ce produit car il est utilisé dans des commandes");
            }
            throw new Exception("Erreur lors de la suppression du produit: " + e.getMessage(), e);
        }
    }
    
    /**
     * Active ou désactive un produit.
     * @param id L'identifiant du produit
     * @param actif True pour activer, false pour désactiver
     * @throws Exception en cas d'erreur
     */
    public void toggleActif(int id, boolean actif) throws Exception {
        try {
            Produit produit = produitDAO.findById(id);
            if (produit == null) {
                throw new Exception("Produit introuvable");
            }
            
            produit.setActif(actif);
            produitDAO.update(produit);
        } catch (SQLException e) {
            throw new Exception("Erreur lors de la modification du produit: " + e.getMessage(), e);
        }
    }
    
    /**
     * Compte le nombre de produits actifs.
     * @return Le nombre de produits actifs
     * @throws Exception en cas d'erreur
     */
    public int countActifs() throws Exception {
        try {
            return produitDAO.countActifs();
        } catch (SQLException e) {
            throw new Exception("Erreur lors du comptage des produits: " + e.getMessage(), e);
        }
    }
    
    /**
     * Compte le nombre de produits en alerte de stock.
     * @return Le nombre de produits en alerte
     * @throws Exception en cas d'erreur
     */
    public int countEnAlerte() throws Exception {
        try {
            return produitDAO.countAlertes();
        } catch (SQLException e) {
            throw new Exception("Erreur lors du comptage des produits en alerte: " + e.getMessage(), e);
        }
    }
    
    /**
     * Récupère les produits les plus vendus.
     * @param limite Le nombre maximum de produits
     * @return Liste des produits
     * @throws Exception en cas d'erreur
     */
    public List<Produit> getBestSellers(int limite) throws Exception {
        try {
            return produitDAO.findBestSellers(limite);
        } catch (SQLException e) {
            throw new Exception("Erreur lors de la récupération des best-sellers: " + e.getMessage(), e);
        }
    }
}
