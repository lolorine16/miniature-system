package restaurant.app.controller;

import java.sql.SQLException;
import java.util.List;
import restaurant.app.dao.CategorieDAO;
import restaurant.app.model.entities.Categorie;

/**
 * Contrôleur pour la gestion des catégories.
 * Intermédiaire entre la vue et le DAO.
 * 
 * @author Restaurant App
 * @version 1.0
 */
public class CategorieController {
    
    private final CategorieDAO categorieDAO;
    
    /**
     * Constructeur par défaut.
     */
    public CategorieController() {
        this.categorieDAO = new CategorieDAO();
    }
    
    /**
     * Récupère toutes les catégories.
     * @return Liste des catégories
     * @throws Exception en cas d'erreur
     */
    public List<Categorie> getAllCategories() throws Exception {
        try {
            return categorieDAO.findAll();
        } catch (SQLException e) {
            throw new Exception("Erreur lors de la récupération des catégories: " + e.getMessage(), e);
        }
    }
    
    /**
     * Récupère une catégorie par son ID.
     * @param id L'identifiant
     * @return La catégorie ou null
     * @throws Exception en cas d'erreur
     */
    public Categorie getCategorieById(int id) throws Exception {
        try {
            return categorieDAO.findById(id);
        } catch (SQLException e) {
            throw new Exception("Erreur lors de la récupération de la catégorie: " + e.getMessage(), e);
        }
    }
    
    /**
     * Recherche des catégories.
     * @param searchText Le texte à rechercher
     * @return Liste des catégories correspondantes
     * @throws Exception en cas d'erreur
     */
    public List<Categorie> searchCategories(String searchText) throws Exception {
        try {
            if (searchText == null || searchText.trim().isEmpty()) {
                return categorieDAO.findAll();
            }
            return categorieDAO.search(searchText);
        } catch (SQLException e) {
            throw new Exception("Erreur lors de la recherche des catégories: " + e.getMessage(), e);
        }
    }
    
    /**
     * Enregistre une catégorie (création ou mise à jour).
     * @param categorie La catégorie à enregistrer
     * @return La catégorie enregistrée
     * @throws Exception en cas d'erreur ou de validation
     */
    public Categorie saveCategorie(Categorie categorie) throws Exception {
        // Validation
        if (categorie.getLibelle() == null || categorie.getLibelle().trim().isEmpty()) {
            throw new Exception("Le libellé de la catégorie est obligatoire");
        }
        
        try {
            // Vérifier l'unicité du libellé
            if (categorieDAO.existsByLibelle(categorie.getLibelle(), categorie.getId())) {
                throw new Exception("Une catégorie avec ce libellé existe déjà");
            }
            
            return categorieDAO.save(categorie);
        } catch (SQLException e) {
            throw new Exception("Erreur lors de l'enregistrement de la catégorie: " + e.getMessage(), e);
        }
    }
    
    /**
     * Supprime une catégorie.
     * @param id L'identifiant de la catégorie
     * @throws Exception en cas d'erreur
     */
    public void deleteCategorie(int id) throws Exception {
        try {
            // Vérifier si la catégorie contient des produits
            if (categorieDAO.hasProducts(id)) {
                throw new Exception("Impossible de supprimer cette catégorie car elle contient des produits");
            }
            
            if (!categorieDAO.delete(id)) {
                throw new Exception("La catégorie n'a pas pu être supprimée");
            }
        } catch (SQLException e) {
            throw new Exception("Erreur lors de la suppression de la catégorie: " + e.getMessage(), e);
        }
    }
    
    /**
     * Compte le nombre de catégories.
     * @return Le nombre de catégories
     * @throws Exception en cas d'erreur
     */
    public int getCount() throws Exception {
        try {
            return categorieDAO.count();
        } catch (SQLException e) {
            throw new Exception("Erreur lors du comptage des catégories: " + e.getMessage(), e);
        }
    }
}
