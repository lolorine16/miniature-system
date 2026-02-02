package restaurant.app.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import restaurant.app.model.entities.*;
import restaurant.app.util.DatabaseConnection;

/**
 * DAO pour la gestion des lignes de commande en base de données.
 * Implémente les opérations CRUD pour l'entité LigneCommande.
 * 
 * @author Restaurant App
 * @version 1.0
 */
public class LigneCommandeDAO {
    
    private final DatabaseConnection dbConnection;
    
    /**
     * Constructeur par défaut.
     */
    public LigneCommandeDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    /**
     * Récupère toutes les lignes d'une commande.
     * @param commandeId L'identifiant de la commande
     * @return Liste des lignes de la commande
     * @throws SQLException en cas d'erreur SQL
     */
    public List<LigneCommande> findByCommandeId(int commandeId) throws SQLException {
        List<LigneCommande> lignes = new ArrayList<>();
        String sql = "SELECT lc.*, p.nom as produit_nom, p.prix_vente as produit_prix, " +
                     "p.categorie_id, c.libelle as categorie_libelle " +
                     "FROM lignes_commande lc " +
                     "JOIN produits p ON lc.produit_id = p.id " +
                     "JOIN categories c ON p.categorie_id = c.id " +
                     "WHERE lc.commande_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, commandeId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lignes.add(mapResultSetToLigneCommande(rs));
                }
            }
        }
        
        return lignes;
    }
    
    /**
     * Recherche une ligne par son ID.
     * @param id L'identifiant de la ligne
     * @return La ligne trouvée ou null
     * @throws SQLException en cas d'erreur SQL
     */
    public LigneCommande findById(int id) throws SQLException {
        String sql = "SELECT lc.*, p.nom as produit_nom, p.prix_vente as produit_prix, " +
                     "p.categorie_id, c.libelle as categorie_libelle " +
                     "FROM lignes_commande lc " +
                     "JOIN produits p ON lc.produit_id = p.id " +
                     "JOIN categories c ON p.categorie_id = c.id " +
                     "WHERE lc.id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToLigneCommande(rs);
                }
            }
        }
        
        return null;
    }
    
    /**
     * Insère une nouvelle ligne de commande.
     * @param ligne La ligne à insérer
     * @param commandeId L'identifiant de la commande
     * @return La ligne avec son ID généré
     * @throws SQLException en cas d'erreur SQL
     */
    public LigneCommande insert(LigneCommande ligne, int commandeId) throws SQLException {
        String sql = "INSERT INTO lignes_commande (commande_id, produit_id, quantite, " +
                     "prix_unitaire, montant_ligne) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, commandeId);
            stmt.setInt(2, ligne.getProduit().getId());
            stmt.setInt(3, ligne.getQuantite());
            stmt.setDouble(4, ligne.getPrixUnitaire());
            stmt.setDouble(5, ligne.getMontantLigne());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        ligne.setId(generatedKeys.getInt(1));
                    }
                }
            }
        }
        
        return ligne;
    }
    
    /**
     * Met à jour une ligne de commande.
     * @param ligne La ligne à mettre à jour
     * @return true si la mise à jour a réussi
     * @throws SQLException en cas d'erreur SQL
     */
    public boolean update(LigneCommande ligne) throws SQLException {
        String sql = "UPDATE lignes_commande SET quantite = ?, prix_unitaire = ?, " +
                     "montant_ligne = ? WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, ligne.getQuantite());
            stmt.setDouble(2, ligne.getPrixUnitaire());
            stmt.setDouble(3, ligne.getMontantLigne());
            stmt.setInt(4, ligne.getId());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Supprime une ligne de commande.
     * @param id L'identifiant de la ligne à supprimer
     * @return true si la suppression a réussi
     * @throws SQLException en cas d'erreur SQL
     */
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM lignes_commande WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Supprime toutes les lignes d'une commande.
     * @param commandeId L'identifiant de la commande
     * @return Le nombre de lignes supprimées
     * @throws SQLException en cas d'erreur SQL
     */
    public int deleteByCommandeId(int commandeId) throws SQLException {
        String sql = "DELETE FROM lignes_commande WHERE commande_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, commandeId);
            return stmt.executeUpdate();
        }
    }
    
    /**
     * Récupère les produits les plus vendus.
     * @param limit Le nombre de produits à retourner
     * @return Liste des paires (produit, quantité totale)
     * @throws SQLException en cas d'erreur SQL
     */
    public List<Object[]> getTopProduits(int limit) throws SQLException {
        List<Object[]> results = new ArrayList<>();
        String sql = "SELECT p.id, p.nom, SUM(lc.quantite) as total_vendu, " +
                     "SUM(lc.montant_ligne) as ca_genere " +
                     "FROM lignes_commande lc " +
                     "JOIN produits p ON lc.produit_id = p.id " +
                     "JOIN commandes c ON lc.commande_id = c.id " +
                     "WHERE c.etat = 'VALIDEE' " +
                     "GROUP BY p.id, p.nom " +
                     "ORDER BY total_vendu DESC " +
                     "LIMIT ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, limit);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Object[] row = new Object[4];
                    row[0] = rs.getInt("id");
                    row[1] = rs.getString("nom");
                    row[2] = rs.getInt("total_vendu");
                    row[3] = rs.getDouble("ca_genere");
                    results.add(row);
                }
            }
        }
        
        return results;
    }
    
    /**
     * Mappe un ResultSet vers une entité LigneCommande.
     * @param rs Le ResultSet à mapper
     * @return L'entité LigneCommande
     * @throws SQLException en cas d'erreur SQL
     */
    private LigneCommande mapResultSetToLigneCommande(ResultSet rs) throws SQLException {
        LigneCommande ligne = new LigneCommande();
        ligne.setId(rs.getInt("id"));
        ligne.setQuantite(rs.getInt("quantite"));
        ligne.setPrixUnitaire(rs.getDouble("prix_unitaire"));
        ligne.setMontantLigne(rs.getDouble("montant_ligne"));
        
        // Mapper le produit
        Produit produit = new Produit();
        produit.setId(rs.getInt("produit_id"));
        produit.setNom(rs.getString("produit_nom"));
        produit.setPrixVente(rs.getDouble("produit_prix"));
        
        // Mapper la catégorie du produit
        Categorie categorie = new Categorie();
        categorie.setId(rs.getInt("categorie_id"));
        categorie.setLibelle(rs.getString("categorie_libelle"));
        produit.setCategorie(categorie);
        
        ligne.setProduit(produit);
        
        return ligne;
    }
}
