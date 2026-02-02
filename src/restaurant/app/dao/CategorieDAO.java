package restaurant.app.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import restaurant.app.model.entities.Categorie;
import restaurant.app.util.DatabaseConnection;

/**
 * DAO pour la gestion des catégories en base de données.
 * Implémente les opérations CRUD pour l'entité Categorie.
 * 
 * @author Restaurant App
 * @version 1.0
 */
public class CategorieDAO {
    
    private final DatabaseConnection dbConnection;
    
    /**
     * Constructeur par défaut.
     */
    public CategorieDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    /**
     * Récupère toutes les catégories.
     * @return Liste de toutes les catégories
     * @throws SQLException en cas d'erreur SQL
     */
    public List<Categorie> findAll() throws SQLException {
        List<Categorie> categories = new ArrayList<>();
        String sql = "SELECT id, libelle, description, date_creation FROM categories ORDER BY libelle";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                categories.add(mapResultSetToCategorie(rs));
            }
        }
        
        return categories;
    }
    
    /**
     * Recherche une catégorie par son ID.
     * @param id L'identifiant de la catégorie
     * @return La catégorie trouvée ou null
     * @throws SQLException en cas d'erreur SQL
     */
    public Categorie findById(int id) throws SQLException {
        String sql = "SELECT id, libelle, description, date_creation FROM categories WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCategorie(rs);
                }
            }
        }
        
        return null;
    }
    
    /**
     * Recherche une catégorie par son libellé.
     * @param libelle Le libellé à rechercher
     * @return La catégorie trouvée ou null
     * @throws SQLException en cas d'erreur SQL
     */
    public Categorie findByLibelle(String libelle) throws SQLException {
        String sql = "SELECT id, libelle, description, date_creation FROM categories WHERE libelle = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, libelle);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCategorie(rs);
                }
            }
        }
        
        return null;
    }
    
    /**
     * Recherche les catégories contenant le texte spécifié.
     * @param searchText Le texte à rechercher
     * @return Liste des catégories correspondantes
     * @throws SQLException en cas d'erreur SQL
     */
    public List<Categorie> search(String searchText) throws SQLException {
        List<Categorie> categories = new ArrayList<>();
        String sql = "SELECT id, libelle, description, date_creation FROM categories " +
                     "WHERE libelle LIKE ? OR description LIKE ? ORDER BY libelle";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String pattern = "%" + searchText + "%";
            stmt.setString(1, pattern);
            stmt.setString(2, pattern);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    categories.add(mapResultSetToCategorie(rs));
                }
            }
        }
        
        return categories;
    }
    
    /**
     * Insère une nouvelle catégorie.
     * @param categorie La catégorie à insérer
     * @return La catégorie avec son ID généré
     * @throws SQLException en cas d'erreur SQL
     */
    public Categorie insert(Categorie categorie) throws SQLException {
        String sql = "INSERT INTO categories (libelle, description) VALUES (?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, categorie.getLibelle());
            stmt.setString(2, categorie.getDescription());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        categorie.setId(generatedKeys.getInt(1));
                    }
                }
            }
        }
        
        return categorie;
    }
    
    /**
     * Met à jour une catégorie existante.
     * @param categorie La catégorie à mettre à jour
     * @return true si la mise à jour a réussi
     * @throws SQLException en cas d'erreur SQL
     */
    public boolean update(Categorie categorie) throws SQLException {
        String sql = "UPDATE categories SET libelle = ?, description = ? WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, categorie.getLibelle());
            stmt.setString(2, categorie.getDescription());
            stmt.setInt(3, categorie.getId());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Enregistre une catégorie (insert ou update).
     * @param categorie La catégorie à enregistrer
     * @return La catégorie enregistrée
     * @throws SQLException en cas d'erreur SQL
     */
    public Categorie save(Categorie categorie) throws SQLException {
        if (categorie.getId() > 0) {
            update(categorie);
            return categorie;
        } else {
            return insert(categorie);
        }
    }
    
    /**
     * Supprime une catégorie.
     * @param id L'identifiant de la catégorie à supprimer
     * @return true si la suppression a réussi
     * @throws SQLException en cas d'erreur SQL
     */
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM categories WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Vérifie si une catégorie contient des produits.
     * @param categorieId L'identifiant de la catégorie
     * @return true si la catégorie contient des produits
     * @throws SQLException en cas d'erreur SQL
     */
    public boolean hasProducts(int categorieId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM produits WHERE categorie_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, categorieId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Vérifie si un libellé existe déjà.
     * @param libelle Le libellé à vérifier
     * @param excludeId L'ID à exclure (pour l'édition)
     * @return true si le libellé existe
     * @throws SQLException en cas d'erreur SQL
     */
    public boolean existsByLibelle(String libelle, int excludeId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM categories WHERE libelle = ? AND id != ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, libelle);
            stmt.setInt(2, excludeId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Compte le nombre total de catégories.
     * @return Le nombre de catégories
     * @throws SQLException en cas d'erreur SQL
     */
    public int count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM categories";
        
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
     * Mappe un ResultSet vers une entité Categorie.
     * @param rs Le ResultSet à mapper
     * @return L'entité Categorie
     * @throws SQLException en cas d'erreur SQL
     */
    private Categorie mapResultSetToCategorie(ResultSet rs) throws SQLException {
        Categorie categorie = new Categorie();
        categorie.setId(rs.getInt("id"));
        categorie.setLibelle(rs.getString("libelle"));
        categorie.setDescription(rs.getString("description"));
        
        Timestamp ts = rs.getTimestamp("date_creation");
        if (ts != null) {
            categorie.setDateCreation(ts.toLocalDateTime());
        }
        
        return categorie;
    }
}
