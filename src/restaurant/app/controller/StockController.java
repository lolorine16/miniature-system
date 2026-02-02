package restaurant.app.controller;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import restaurant.app.dao.MouvementStockDAO;
import restaurant.app.dao.ProduitDAO;
import restaurant.app.model.entities.MouvementStock;
import restaurant.app.model.entities.Produit;
import restaurant.app.model.enums.TypeMouvement;

/**
 * Contrôleur pour la gestion du stock.
 * Gère les mouvements d'entrée et de sortie.
 * 
 * @author Restaurant App
 * @version 1.0
 */
public class StockController {
    
    private final MouvementStockDAO mouvementDAO;
    private final ProduitDAO produitDAO;
    
    /**
     * Constructeur par défaut.
     */
    public StockController() {
        this.mouvementDAO = new MouvementStockDAO();
        this.produitDAO = new ProduitDAO();
    }
    
    /**
     * Récupère tous les mouvements de stock.
     * @return Liste des mouvements
     * @throws Exception en cas d'erreur
     */
    public List<MouvementStock> getAllMouvements() throws Exception {
        try {
            return mouvementDAO.findAll();
        } catch (SQLException e) {
            throw new Exception("Erreur lors de la récupération des mouvements: " + e.getMessage(), e);
        }
    }
    
    /**
     * Récupère les mouvements d'un produit.
     * @param produitId L'identifiant du produit
     * @return Liste des mouvements
     * @throws Exception en cas d'erreur
     */
    public List<MouvementStock> getMouvementsByProduit(int produitId) throws Exception {
        try {
            return mouvementDAO.findByProduit(produitId);
        } catch (SQLException e) {
            throw new Exception("Erreur lors de la récupération des mouvements: " + e.getMessage(), e);
        }
    }
    
    /**
     * Récupère les mouvements par type.
     * @param type Le type de mouvement
     * @return Liste des mouvements
     * @throws Exception en cas d'erreur
     */
    public List<MouvementStock> getMouvementsByType(TypeMouvement type) throws Exception {
        try {
            return mouvementDAO.findByType(type);
        } catch (SQLException e) {
            throw new Exception("Erreur lors de la récupération des mouvements: " + e.getMessage(), e);
        }
    }
    
    /**
     * Enregistre une entrée de stock.
     * @param produitId L'identifiant du produit
     * @param quantite La quantité entrante
     * @param motif Le motif de l'entrée
     * @return Le mouvement créé
     * @throws Exception en cas d'erreur
     */
    public MouvementStock enregistrerEntree(int produitId, int quantite, String motif) throws Exception {
        return enregistrerMouvement(produitId, quantite, TypeMouvement.ENTREE, motif);
    }
    
    /**
     * Enregistre une sortie de stock.
     * @param produitId L'identifiant du produit
     * @param quantite La quantité sortante
     * @param motif Le motif de la sortie
     * @return Le mouvement créé
     * @throws Exception en cas d'erreur
     */
    public MouvementStock enregistrerSortie(int produitId, int quantite, String motif) throws Exception {
        return enregistrerMouvement(produitId, quantite, TypeMouvement.SORTIE, motif);
    }
    
    /**
     * Enregistre un mouvement de stock et met à jour le stock du produit.
     * @param produitId L'identifiant du produit
     * @param quantite La quantité
     * @param type Le type de mouvement
     * @param motif Le motif
     * @return Le mouvement créé
     * @throws Exception en cas d'erreur
     */
    private MouvementStock enregistrerMouvement(int produitId, int quantite, TypeMouvement type, String motif) throws Exception {
        // Validation
        if (quantite <= 0) {
            throw new Exception("La quantité doit être positive");
        }
        
        try {
            // Récupérer le produit
            Produit produit = produitDAO.findById(produitId);
            if (produit == null) {
                throw new Exception("Produit introuvable");
            }
            
            // Calculer le nouveau stock
            int stockActuel = produit.getStockActuel();
            int nouveauStock;
            
            if (type == TypeMouvement.ENTREE) {
                nouveauStock = stockActuel + quantite;
            } else {
                if (quantite > stockActuel) {
                    throw new Exception("Stock insuffisant. Stock actuel: " + stockActuel);
                }
                nouveauStock = stockActuel - quantite;
            }
            
            // Créer le mouvement
            MouvementStock mouvement = new MouvementStock(produit, type, quantite, motif);
            
            // Sauvegarder le mouvement
            MouvementStock mouvementSauvegarde = mouvementDAO.insert(mouvement);
            
            // Mettre à jour le stock du produit
            produitDAO.updateStock(produitId, nouveauStock);
            
            return mouvementSauvegarde;
            
        } catch (SQLException e) {
            throw new Exception("Erreur lors de l'enregistrement du mouvement: " + e.getMessage(), e);
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
            throw new Exception("Erreur lors de la récupération des produits: " + e.getMessage(), e);
        }
    }
    
    /**
     * Compte le nombre de produits en alerte.
     * @return Le nombre de produits en alerte
     * @throws Exception en cas d'erreur
     */
    public int countProduitsEnAlerte() throws Exception {
        try {
            return produitDAO.countAlertes();
        } catch (SQLException e) {
            throw new Exception("Erreur lors du comptage: " + e.getMessage(), e);
        }
    }
    
    /**
     * Compte le nombre de produits en rupture de stock.
     * @return Le nombre de produits en rupture
     * @throws Exception en cas d'erreur
     */
    public int countProduitsEnRupture() throws Exception {
        try {
            return produitDAO.countOutOfStock();
        } catch (SQLException e) {
            throw new Exception("Erreur lors du comptage: " + e.getMessage(), e);
        }
    }
    
    /**
     * Recherche des mouvements par produit et période.
     * @param type Le type de mouvement (null pour tous)
     * @param produitId L'identifiant du produit (0 pour tous)
     * @param dateDebut Date de début (null pour ignorer)
     * @param dateFin Date de fin (null pour ignorer)
     * @return Liste des mouvements correspondants
     * @throws Exception en cas d'erreur
     */
    public List<MouvementStock> rechercher(TypeMouvement type, int produitId, 
                                           LocalDate dateDebut, LocalDate dateFin) throws Exception {
        try {
            return mouvementDAO.search(type, produitId, dateDebut, dateFin);
        } catch (SQLException e) {
            throw new Exception("Erreur lors de la recherche: " + e.getMessage(), e);
        }
    }
}
