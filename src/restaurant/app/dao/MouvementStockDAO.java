package restaurant.app.dao;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import restaurant.app.model.entities.*;
import restaurant.app.model.enums.TypeMouvement;
import restaurant.app.util.DatabaseConnection;

/**
 * DAO pour la gestion des mouvements de stock en base de données.
 * Implémente les opérations CRUD pour l'entité MouvementStock.
 * 
 * @author Restaurant App
 * @version 1.0
 */
public class MouvementStockDAO {
    
    private final DatabaseConnection dbConnection;
    
    /**
     * Constructeur par défaut.
     */
    public MouvementStockDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    /**
     * Récupère tous les mouvements de stock.
     * @return Liste de tous les mouvements
     * @throws SQLException en cas d'erreur SQL
     */
    public List<MouvementStock> findAll() throws SQLException {
        List<MouvementStock> mouvements = new ArrayList<>();
        String sql = "SELECT ms.*, p.nom as produit_nom, p.stock_actuel, " +
                     "u.login, u.nom_complet " +
                     "FROM mouvements_stock ms " +
                     "JOIN produits p ON ms.produit_id = p.id " +
                     "LEFT JOIN utilisateurs u ON ms.utilisateur_id = u.id " +
                     "ORDER BY ms.date_mouvement DESC";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                mouvements.add(mapResultSetToMouvementStock(rs));
            }
        }
        
        return mouvements;
    }
    
    /**
     * Recherche un mouvement par son ID.
     * @param id L'identifiant du mouvement
     * @return Le mouvement trouvé ou null
     * @throws SQLException en cas d'erreur SQL
     */
    public MouvementStock findById(int id) throws SQLException {
        String sql = "SELECT ms.*, p.nom as produit_nom, p.stock_actuel, " +
                     "u.login, u.nom_complet " +
                     "FROM mouvements_stock ms " +
                     "JOIN produits p ON ms.produit_id = p.id " +
                     "LEFT JOIN utilisateurs u ON ms.utilisateur_id = u.id " +
                     "WHERE ms.id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMouvementStock(rs);
                }
            }
        }
        
        return null;
    }
    
    /**
     * Recherche les mouvements d'un produit.
     * @param produitId L'identifiant du produit
     * @return Liste des mouvements du produit
     * @throws SQLException en cas d'erreur SQL
     */
    public List<MouvementStock> findByProduit(int produitId) throws SQLException {
        List<MouvementStock> mouvements = new ArrayList<>();
        String sql = "SELECT ms.*, p.nom as produit_nom, p.stock_actuel, " +
                     "u.login, u.nom_complet " +
                     "FROM mouvements_stock ms " +
                     "JOIN produits p ON ms.produit_id = p.id " +
                     "LEFT JOIN utilisateurs u ON ms.utilisateur_id = u.id " +
                     "WHERE ms.produit_id = ? " +
                     "ORDER BY ms.date_mouvement DESC";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, produitId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    mouvements.add(mapResultSetToMouvementStock(rs));
                }
            }
        }
        
        return mouvements;
    }
    
    /**
     * Recherche les mouvements par type.
     * @param type Le type de mouvement
     * @return Liste des mouvements de ce type
     * @throws SQLException en cas d'erreur SQL
     */
    public List<MouvementStock> findByType(TypeMouvement type) throws SQLException {
        List<MouvementStock> mouvements = new ArrayList<>();
        String sql = "SELECT ms.*, p.nom as produit_nom, p.stock_actuel, " +
                     "u.login, u.nom_complet " +
                     "FROM mouvements_stock ms " +
                     "JOIN produits p ON ms.produit_id = p.id " +
                     "LEFT JOIN utilisateurs u ON ms.utilisateur_id = u.id " +
                     "WHERE ms.type = ? " +
                     "ORDER BY ms.date_mouvement DESC";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, type.name());
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    mouvements.add(mapResultSetToMouvementStock(rs));
                }
            }
        }
        
        return mouvements;
    }
    
    /**
     * Recherche les mouvements entre deux dates.
     * @param debut Date de début
     * @param fin Date de fin
     * @return Liste des mouvements dans la période
     * @throws SQLException en cas d'erreur SQL
     */
    public List<MouvementStock> findByDateRange(LocalDateTime debut, LocalDateTime fin) throws SQLException {
        List<MouvementStock> mouvements = new ArrayList<>();
        String sql = "SELECT ms.*, p.nom as produit_nom, p.stock_actuel, " +
                     "u.login, u.nom_complet " +
                     "FROM mouvements_stock ms " +
                     "JOIN produits p ON ms.produit_id = p.id " +
                     "LEFT JOIN utilisateurs u ON ms.utilisateur_id = u.id " +
                     "WHERE ms.date_mouvement BETWEEN ? AND ? " +
                     "ORDER BY ms.date_mouvement DESC";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setTimestamp(1, Timestamp.valueOf(debut));
            stmt.setTimestamp(2, Timestamp.valueOf(fin));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    mouvements.add(mapResultSetToMouvementStock(rs));
                }
            }
        }
        
        return mouvements;
    }
    
    /**
     * Recherche multicritère.
     * @param type Le type (null pour tous)
     * @param produitId L'ID du produit (0 pour tous)
     * @param debut Date de début (null pour ignorer)
     * @param fin Date de fin (null pour ignorer)
     * @return Liste des mouvements correspondants
     * @throws SQLException en cas d'erreur SQL
     */
    public List<MouvementStock> search(TypeMouvement type, int produitId, 
                                        LocalDate debut, LocalDate fin) throws SQLException {
        List<MouvementStock> mouvements = new ArrayList<>();
        
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ms.*, p.nom as produit_nom, p.stock_actuel, ");
        sql.append("u.login, u.nom_complet ");
        sql.append("FROM mouvements_stock ms ");
        sql.append("JOIN produits p ON ms.produit_id = p.id ");
        sql.append("LEFT JOIN utilisateurs u ON ms.utilisateur_id = u.id ");
        sql.append("WHERE 1=1 ");
        
        if (type != null) {
            sql.append("AND ms.type = ? ");
        }
        if (produitId > 0) {
            sql.append("AND ms.produit_id = ? ");
        }
        if (debut != null) {
            sql.append("AND DATE(ms.date_mouvement) >= ? ");
        }
        if (fin != null) {
            sql.append("AND DATE(ms.date_mouvement) <= ? ");
        }
        sql.append("ORDER BY ms.date_mouvement DESC");
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            int paramIndex = 1;
            if (type != null) {
                stmt.setString(paramIndex++, type.name());
            }
            if (produitId > 0) {
                stmt.setInt(paramIndex++, produitId);
            }
            if (debut != null) {
                stmt.setDate(paramIndex++, Date.valueOf(debut));
            }
            if (fin != null) {
                stmt.setDate(paramIndex++, Date.valueOf(fin));
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    mouvements.add(mapResultSetToMouvementStock(rs));
                }
            }
        }
        
        return mouvements;
    }
    
    /**
     * Insère un nouveau mouvement de stock.
     * @param mouvement Le mouvement à insérer
     * @return Le mouvement avec son ID généré
     * @throws SQLException en cas d'erreur SQL
     */
    public MouvementStock insert(MouvementStock mouvement) throws SQLException {
        String sql = "INSERT INTO mouvements_stock (produit_id, type, quantite, " +
                     "date_mouvement, motif, utilisateur_id) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, mouvement.getProduit().getId());
            stmt.setString(2, mouvement.getType().name());
            stmt.setInt(3, mouvement.getQuantite());
            stmt.setTimestamp(4, Timestamp.valueOf(mouvement.getDateMouvement()));
            stmt.setString(5, mouvement.getMotif());
            
            if (mouvement.getUtilisateur() != null) {
                stmt.setInt(6, mouvement.getUtilisateur().getId());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        mouvement.setId(generatedKeys.getInt(1));
                    }
                }
            }
        }
        
        return mouvement;
    }
    
    /**
     * Supprime un mouvement de stock.
     * @param id L'identifiant du mouvement à supprimer
     * @return true si la suppression a réussi
     * @throws SQLException en cas d'erreur SQL
     */
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM mouvements_stock WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Compte le nombre total de mouvements.
     * @return Le nombre de mouvements
     * @throws SQLException en cas d'erreur SQL
     */
    public int count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM mouvements_stock";
        
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
     * Mappe un ResultSet vers une entité MouvementStock.
     * @param rs Le ResultSet à mapper
     * @return L'entité MouvementStock
     * @throws SQLException en cas d'erreur SQL
     */
    private MouvementStock mapResultSetToMouvementStock(ResultSet rs) throws SQLException {
        MouvementStock mouvement = new MouvementStock();
        mouvement.setId(rs.getInt("id"));
        mouvement.setQuantite(rs.getInt("quantite"));
        mouvement.setMotif(rs.getString("motif"));
        
        String typeStr = rs.getString("type");
        if (typeStr != null) {
            mouvement.setType(TypeMouvement.valueOf(typeStr));
        }
        
        Timestamp ts = rs.getTimestamp("date_mouvement");
        if (ts != null) {
            mouvement.setDateMouvement(ts.toLocalDateTime());
        }
        
        // Mapper le produit
        Produit produit = new Produit();
        produit.setId(rs.getInt("produit_id"));
        produit.setNom(rs.getString("produit_nom"));
        produit.setStockActuel(rs.getInt("stock_actuel"));
        mouvement.setProduit(produit);
        
        // Mapper l'utilisateur si présent
        int utilisateurId = rs.getInt("utilisateur_id");
        if (!rs.wasNull()) {
            Utilisateur utilisateur = new Utilisateur();
            utilisateur.setId(utilisateurId);
            utilisateur.setLogin(rs.getString("login"));
            utilisateur.setNomComplet(rs.getString("nom_complet"));
            mouvement.setUtilisateur(utilisateur);
        }
        
        return mouvement;
    }
}
