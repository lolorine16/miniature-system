package restaurant.app.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import restaurant.app.model.entities.Categorie;
import restaurant.app.model.entities.Produit;
import restaurant.app.util.DatabaseConnection;

/**
 * DAO pour la gestion des produits en base de données.
 * Implémente les opérations CRUD pour l'entité Produit.
 * 
 * @author Restaurant App
 * @version 1.0
 */
public class ProduitDAO {
    
    private final DatabaseConnection dbConnection;
    private final CategorieDAO categorieDAO;
    
    /**
     * Constructeur par défaut.
     */
    public ProduitDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
        this.categorieDAO = new CategorieDAO();
    }
    
    /**
     * Récupère tous les produits.
     * @return Liste de tous les produits
     * @throws SQLException en cas d'erreur SQL
     */
    public List<Produit> findAll() throws SQLException {
        List<Produit> produits = new ArrayList<>();
        String sql = "SELECT p.*, c.libelle as categorie_libelle, c.description as categorie_description " +
                     "FROM produits p " +
                     "JOIN categories c ON p.categorie_id = c.id " +
                     "ORDER BY p.nom";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                produits.add(mapResultSetToProduit(rs));
            }
        }
        
        return produits;
    }
    
    /**
     * Récupère tous les produits actifs.
     * @return Liste des produits actifs
     * @throws SQLException en cas d'erreur SQL
     */
    public List<Produit> findAllActive() throws SQLException {
        List<Produit> produits = new ArrayList<>();
        String sql = "SELECT p.*, c.libelle as categorie_libelle, c.description as categorie_description " +
                     "FROM produits p " +
                     "JOIN categories c ON p.categorie_id = c.id " +
                     "WHERE p.actif = TRUE " +
                     "ORDER BY p.nom";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                produits.add(mapResultSetToProduit(rs));
            }
        }
        
        return produits;
    }
    
    /**
     * Recherche un produit par son ID.
     * @param id L'identifiant du produit
     * @return Le produit trouvé ou null
     * @throws SQLException en cas d'erreur SQL
     */
    public Produit findById(int id) throws SQLException {
        String sql = "SELECT p.*, c.libelle as categorie_libelle, c.description as categorie_description " +
                     "FROM produits p " +
                     "JOIN categories c ON p.categorie_id = c.id " +
                     "WHERE p.id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToProduit(rs);
                }
            }
        }
        
        return null;
    }
    
    /**
     * Recherche les produits d'une catégorie.
     * @param categorieId L'identifiant de la catégorie
     * @return Liste des produits de la catégorie
     * @throws SQLException en cas d'erreur SQL
     */
    public List<Produit> findByCategorie(int categorieId) throws SQLException {
        List<Produit> produits = new ArrayList<>();
        String sql = "SELECT p.*, c.libelle as categorie_libelle, c.description as categorie_description " +
                     "FROM produits p " +
                     "JOIN categories c ON p.categorie_id = c.id " +
                     "WHERE p.categorie_id = ? " +
                     "ORDER BY p.nom";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, categorieId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    produits.add(mapResultSetToProduit(rs));
                }
            }
        }
        
        return produits;
    }
    
    /**
     * Recherche les produits par nom.
     * @param nom Le nom à rechercher
     * @return Liste des produits correspondants
     * @throws SQLException en cas d'erreur SQL
     */
    public List<Produit> findByNom(String nom) throws SQLException {
        List<Produit> produits = new ArrayList<>();
        String sql = "SELECT p.*, c.libelle as categorie_libelle, c.description as categorie_description " +
                     "FROM produits p " +
                     "JOIN categories c ON p.categorie_id = c.id " +
                     "WHERE p.nom LIKE ? " +
                     "ORDER BY p.nom";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + nom + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    produits.add(mapResultSetToProduit(rs));
                }
            }
        }
        
        return produits;
    }
    
    /**
     * Recherche les produits en alerte de stock.
     * @return Liste des produits sous le seuil d'alerte
     * @throws SQLException en cas d'erreur SQL
     */
    public List<Produit> findAlertesStock() throws SQLException {
        List<Produit> produits = new ArrayList<>();
        String sql = "SELECT p.*, c.libelle as categorie_libelle, c.description as categorie_description " +
                     "FROM produits p " +
                     "JOIN categories c ON p.categorie_id = c.id " +
                     "WHERE p.stock_actuel <= p.seuil_alerte AND p.actif = TRUE " +
                     "ORDER BY (p.stock_actuel - p.seuil_alerte) ASC";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                produits.add(mapResultSetToProduit(rs));
            }
        }
        
        return produits;
    }
    
    /**
     * Recherche multicritère.
     * @param searchText Texte à rechercher
     * @param categorieId ID de la catégorie (0 pour toutes)
     * @param actifOnly Uniquement les produits actifs
     * @param alerteOnly Uniquement les produits en alerte
     * @return Liste des produits correspondants
     * @throws SQLException en cas d'erreur SQL
     */
    public List<Produit> search(String searchText, int categorieId, boolean actifOnly, boolean alerteOnly) throws SQLException {
        List<Produit> produits = new ArrayList<>();
        
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT p.*, c.libelle as categorie_libelle, c.description as categorie_description ");
        sql.append("FROM produits p ");
        sql.append("JOIN categories c ON p.categorie_id = c.id ");
        sql.append("WHERE 1=1 ");
        
        if (searchText != null && !searchText.trim().isEmpty()) {
            sql.append("AND (p.nom LIKE ? OR p.description LIKE ?) ");
        }
        if (categorieId > 0) {
            sql.append("AND p.categorie_id = ? ");
        }
        if (actifOnly) {
            sql.append("AND p.actif = TRUE ");
        }
        if (alerteOnly) {
            sql.append("AND p.stock_actuel <= p.seuil_alerte ");
        }
        sql.append("ORDER BY p.nom");
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            int paramIndex = 1;
            if (searchText != null && !searchText.trim().isEmpty()) {
                String pattern = "%" + searchText + "%";
                stmt.setString(paramIndex++, pattern);
                stmt.setString(paramIndex++, pattern);
            }
            if (categorieId > 0) {
                stmt.setInt(paramIndex++, categorieId);
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    produits.add(mapResultSetToProduit(rs));
                }
            }
        }
        
        return produits;
    }
    
    /**
     * Insère un nouveau produit.
     * @param produit Le produit à insérer
     * @return Le produit avec son ID généré
     * @throws SQLException en cas d'erreur SQL
     */
    public Produit insert(Produit produit) throws SQLException {
        String sql = "INSERT INTO produits (nom, categorie_id, prix_vente, stock_actuel, seuil_alerte, " +
                     "description, image_path, actif) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, produit.getNom());
            stmt.setInt(2, produit.getCategorie().getId());
            stmt.setDouble(3, produit.getPrixVente());
            stmt.setInt(4, produit.getStockActuel());
            stmt.setInt(5, produit.getSeuilAlerte());
            stmt.setString(6, produit.getDescription());
            stmt.setString(7, produit.getImagePath());
            stmt.setBoolean(8, produit.isActif());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        produit.setId(generatedKeys.getInt(1));
                    }
                }
            }
        }
        
        return produit;
    }
    
    /**
     * Met à jour un produit existant.
     * @param produit Le produit à mettre à jour
     * @return true si la mise à jour a réussi
     * @throws SQLException en cas d'erreur SQL
     */
    public boolean update(Produit produit) throws SQLException {
        String sql = "UPDATE produits SET nom = ?, categorie_id = ?, prix_vente = ?, " +
                     "stock_actuel = ?, seuil_alerte = ?, description = ?, image_path = ?, actif = ? " +
                     "WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, produit.getNom());
            stmt.setInt(2, produit.getCategorie().getId());
            stmt.setDouble(3, produit.getPrixVente());
            stmt.setInt(4, produit.getStockActuel());
            stmt.setInt(5, produit.getSeuilAlerte());
            stmt.setString(6, produit.getDescription());
            stmt.setString(7, produit.getImagePath());
            stmt.setBoolean(8, produit.isActif());
            stmt.setInt(9, produit.getId());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Enregistre un produit (insert ou update).
     * @param produit Le produit à enregistrer
     * @return Le produit enregistré
     * @throws SQLException en cas d'erreur SQL
     */
    public Produit save(Produit produit) throws SQLException {
        if (produit.getId() > 0) {
            update(produit);
            return produit;
        } else {
            return insert(produit);
        }
    }
    
    /**
     * Met à jour uniquement le stock d'un produit.
     * @param id L'identifiant du produit
     * @param newStock Le nouveau stock
     * @return true si la mise à jour a réussi
     * @throws SQLException en cas d'erreur SQL
     */
    public boolean updateStock(int id, int newStock) throws SQLException {
        String sql = "UPDATE produits SET stock_actuel = ? WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, newStock);
            stmt.setInt(2, id);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Active ou désactive un produit.
     * @param id L'identifiant du produit
     * @param actif Le nouvel état
     * @return true si la mise à jour a réussi
     * @throws SQLException en cas d'erreur SQL
     */
    public boolean setActif(int id, boolean actif) throws SQLException {
        String sql = "UPDATE produits SET actif = ? WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBoolean(1, actif);
            stmt.setInt(2, id);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Supprime un produit.
     * @param id L'identifiant du produit à supprimer
     * @return true si la suppression a réussi
     * @throws SQLException en cas d'erreur SQL
     */
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM produits WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Vérifie si un produit a des commandes associées.
     * @param produitId L'identifiant du produit
     * @return true si le produit a des commandes
     * @throws SQLException en cas d'erreur SQL
     */
    public boolean hasCommandes(int produitId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM lignes_commande WHERE produit_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, produitId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Compte le nombre total de produits.
     * @return Le nombre de produits
     * @throws SQLException en cas d'erreur SQL
     */
    public int count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM produits";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        
        return 0;
    }
    
    /**
     * Compte le nombre de produits en alerte.
     * @return Le nombre de produits en alerte
     * @throws SQLException en cas d'erreur SQL
     */
    public int countAlertes() throws SQLException {
        String sql = "SELECT COUNT(*) FROM produits WHERE stock_actuel <= seuil_alerte AND actif = TRUE";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        
        return 0;
    }
    
    /**
     * Récupère les produits les plus vendus.
     * @param limite Nombre maximum de produits à retourner
     * @return Liste des produits les plus vendus
     * @throws SQLException en cas d'erreur SQL
     */
    public List<Produit> findBestSellers(int limite) throws SQLException {
        List<Produit> produits = new ArrayList<>();
        String sql = "SELECT p.*, c.libelle as categorie_libelle, c.description as categorie_description, " +
                     "COALESCE(SUM(lc.quantite), 0) as total_vendu " +
                     "FROM produits p " +
                     "JOIN categories c ON p.categorie_id = c.id " +
                     "LEFT JOIN lignes_commande lc ON p.id = lc.produit_id " +
                     "LEFT JOIN commandes cmd ON lc.commande_id = cmd.id AND cmd.etat != 'ANNULEE' " +
                     "WHERE p.actif = TRUE " +
                     "GROUP BY p.id " +
                     "ORDER BY total_vendu DESC " +
                     "LIMIT ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, limite);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    produits.add(mapResultSetToProduit(rs));
                }
            }
        }
        
        return produits;
    }
    
    /**
     * Compte le nombre de produits en rupture de stock.
     * @return Le nombre de produits en rupture
     * @throws SQLException en cas d'erreur SQL
     */
    public int countOutOfStock() throws SQLException {
        String sql = "SELECT COUNT(*) FROM produits WHERE stock_actuel = 0 AND actif = TRUE";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        
        return 0;
    }
    
    /**
     * Compte le nombre de produits avec stock bas.
     * @param seuil Le seuil de stock bas
     * @return Le nombre de produits avec stock bas
     * @throws SQLException en cas d'erreur SQL
     */
    public int countLowStock(int seuil) throws SQLException {
        String sql = "SELECT COUNT(*) FROM produits WHERE stock_actuel <= ? AND stock_actuel > 0 AND actif = TRUE";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, seuil);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        
        return 0;
    }
    
    /**
     * Récupère tous les produits actifs (alias de findAllActive).
     * @return Liste des produits actifs
     * @throws SQLException en cas d'erreur SQL
     */
    public List<Produit> findActifs() throws SQLException {
        return findAllActive();
    }
    
    /**
     * Récupère les produits en alerte de stock.
     * @return Liste des produits en alerte
     * @throws SQLException en cas d'erreur SQL
     */
    public List<Produit> findEnAlerte() throws SQLException {
        List<Produit> produits = new ArrayList<>();
        String sql = "SELECT p.*, c.libelle as categorie_libelle, c.description as categorie_description " +
                     "FROM produits p " +
                     "JOIN categories c ON p.categorie_id = c.id " +
                     "WHERE p.stock_actuel <= p.seuil_alerte AND p.actif = TRUE " +
                     "ORDER BY p.stock_actuel ASC";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                produits.add(mapResultSetToProduit(rs));
            }
        }
        
        return produits;
    }
    
    /**
     * Compte le nombre de produits actifs.
     * @return Le nombre de produits actifs
     * @throws SQLException en cas d'erreur SQL
     */
    public int countActifs() throws SQLException {
        String sql = "SELECT COUNT(*) FROM produits WHERE actif = TRUE";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        
        return 0;
    }
    
    /**
     * Mappe un ResultSet vers une entité Produit.
     * @param rs Le ResultSet à mapper
     * @return L'entité Produit
     * @throws SQLException en cas d'erreur SQL
     */
    private Produit mapResultSetToProduit(ResultSet rs) throws SQLException {
        Produit produit = new Produit();
        produit.setId(rs.getInt("id"));
        produit.setNom(rs.getString("nom"));
        produit.setPrixVente(rs.getDouble("prix_vente"));
        produit.setStockActuel(rs.getInt("stock_actuel"));
        produit.setSeuilAlerte(rs.getInt("seuil_alerte"));
        produit.setDescription(rs.getString("description"));
        produit.setImagePath(rs.getString("image_path"));
        produit.setActif(rs.getBoolean("actif"));
        
        // Mapper la catégorie
        Categorie categorie = new Categorie();
        categorie.setId(rs.getInt("categorie_id"));
        categorie.setLibelle(rs.getString("categorie_libelle"));
        categorie.setDescription(rs.getString("categorie_description"));
        produit.setCategorie(categorie);
        
        Timestamp ts = rs.getTimestamp("date_creation");
        if (ts != null) {
            produit.setDateCreation(ts.toLocalDateTime());
        }
        
        return produit;
    }
}
