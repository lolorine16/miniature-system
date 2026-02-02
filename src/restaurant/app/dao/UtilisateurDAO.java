package restaurant.app.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import restaurant.app.model.entities.Utilisateur;
import restaurant.app.model.enums.RoleUtilisateur;
import restaurant.app.util.DatabaseConnection;

/**
 * DAO pour la gestion des utilisateurs en base de données.
 * Implémente les opérations CRUD pour l'entité Utilisateur.
 * 
 * @author Restaurant App
 * @version 1.0
 */
public class UtilisateurDAO {
    
    private final DatabaseConnection dbConnection;
    
    /**
     * Constructeur par défaut.
     */
    public UtilisateurDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    /**
     * Récupère tous les utilisateurs.
     * @return Liste de tous les utilisateurs
     * @throws SQLException en cas d'erreur SQL
     */
    public List<Utilisateur> findAll() throws SQLException {
        List<Utilisateur> utilisateurs = new ArrayList<>();
        String sql = "SELECT * FROM utilisateurs ORDER BY nom_complet";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                utilisateurs.add(mapResultSetToUtilisateur(rs));
            }
        }
        
        return utilisateurs;
    }
    
    /**
     * Récupère tous les utilisateurs actifs.
     * @return Liste des utilisateurs actifs
     * @throws SQLException en cas d'erreur SQL
     */
    public List<Utilisateur> findAllActive() throws SQLException {
        List<Utilisateur> utilisateurs = new ArrayList<>();
        String sql = "SELECT * FROM utilisateurs WHERE actif = TRUE ORDER BY nom_complet";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                utilisateurs.add(mapResultSetToUtilisateur(rs));
            }
        }
        
        return utilisateurs;
    }
    
    /**
     * Recherche un utilisateur par son ID.
     * @param id L'identifiant de l'utilisateur
     * @return L'utilisateur trouvé ou null
     * @throws SQLException en cas d'erreur SQL
     */
    public Utilisateur findById(int id) throws SQLException {
        String sql = "SELECT * FROM utilisateurs WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUtilisateur(rs);
                }
            }
        }
        
        return null;
    }
    
    /**
     * Recherche un utilisateur par son login.
     * @param login Le login à rechercher
     * @return L'utilisateur trouvé ou null
     * @throws SQLException en cas d'erreur SQL
     */
    public Utilisateur findByLogin(String login) throws SQLException {
        String sql = "SELECT * FROM utilisateurs WHERE login = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, login);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUtilisateur(rs);
                }
            }
        }
        
        return null;
    }
    
    /**
     * Authentifie un utilisateur.
     * @param login Le login
     * @param motDePasse Le mot de passe (hashé)
     * @return L'utilisateur authentifié ou null
     * @throws SQLException en cas d'erreur SQL
     */
    public Utilisateur authenticate(String login, String motDePasse) throws SQLException {
        String sql = "SELECT * FROM utilisateurs WHERE login = ? AND mot_de_passe = ? AND actif = TRUE";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, login);
            stmt.setString(2, motDePasse);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Utilisateur user = mapResultSetToUtilisateur(rs);
                    // Mettre à jour la dernière connexion
                    updateDerniereConnexion(user.getId());
                    return user;
                }
            }
        }
        
        return null;
    }
    
    /**
     * Met à jour la date de dernière connexion.
     * @param userId L'identifiant de l'utilisateur
     * @throws SQLException en cas d'erreur SQL
     */
    public void updateDerniereConnexion(int userId) throws SQLException {
        String sql = "UPDATE utilisateurs SET derniere_connexion = CURRENT_TIMESTAMP WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        }
    }
    
    /**
     * Recherche les utilisateurs par texte.
     * @param searchText Le texte à rechercher
     * @return Liste des utilisateurs correspondants
     * @throws SQLException en cas d'erreur SQL
     */
    public List<Utilisateur> search(String searchText) throws SQLException {
        List<Utilisateur> utilisateurs = new ArrayList<>();
        String sql = "SELECT * FROM utilisateurs " +
                     "WHERE login LIKE ? OR nom_complet LIKE ? OR email LIKE ? " +
                     "ORDER BY nom_complet";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String pattern = "%" + searchText + "%";
            stmt.setString(1, pattern);
            stmt.setString(2, pattern);
            stmt.setString(3, pattern);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    utilisateurs.add(mapResultSetToUtilisateur(rs));
                }
            }
        }
        
        return utilisateurs;
    }
    
    /**
     * Insère un nouvel utilisateur.
     * @param utilisateur L'utilisateur à insérer
     * @return L'utilisateur avec son ID généré
     * @throws SQLException en cas d'erreur SQL
     */
    public Utilisateur insert(Utilisateur utilisateur) throws SQLException {
        String sql = "INSERT INTO utilisateurs (login, mot_de_passe, nom_complet, role, email, telephone, actif) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, utilisateur.getLogin());
            stmt.setString(2, utilisateur.getMotDePasse());
            stmt.setString(3, utilisateur.getNomComplet());
            stmt.setString(4, utilisateur.getRole().name());
            stmt.setString(5, utilisateur.getEmail());
            stmt.setString(6, utilisateur.getTelephone());
            stmt.setBoolean(7, utilisateur.isActif());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        utilisateur.setId(generatedKeys.getInt(1));
                    }
                }
            }
        }
        
        return utilisateur;
    }
    
    /**
     * Met à jour un utilisateur existant.
     * @param utilisateur L'utilisateur à mettre à jour
     * @return true si la mise à jour a réussi
     * @throws SQLException en cas d'erreur SQL
     */
    public boolean update(Utilisateur utilisateur) throws SQLException {
        String sql = "UPDATE utilisateurs SET login = ?, nom_complet = ?, role = ?, " +
                     "email = ?, telephone = ?, actif = ? WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, utilisateur.getLogin());
            stmt.setString(2, utilisateur.getNomComplet());
            stmt.setString(3, utilisateur.getRole().name());
            stmt.setString(4, utilisateur.getEmail());
            stmt.setString(5, utilisateur.getTelephone());
            stmt.setBoolean(6, utilisateur.isActif());
            stmt.setInt(7, utilisateur.getId());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Met à jour le mot de passe d'un utilisateur.
     * @param userId L'identifiant de l'utilisateur
     * @param newPassword Le nouveau mot de passe (hashé)
     * @return true si la mise à jour a réussi
     * @throws SQLException en cas d'erreur SQL
     */
    public boolean updatePassword(int userId, String newPassword) throws SQLException {
        String sql = "UPDATE utilisateurs SET mot_de_passe = ? WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, newPassword);
            stmt.setInt(2, userId);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Enregistre un utilisateur (insert ou update).
     * @param utilisateur L'utilisateur à enregistrer
     * @return L'utilisateur enregistré
     * @throws SQLException en cas d'erreur SQL
     */
    public Utilisateur save(Utilisateur utilisateur) throws SQLException {
        if (utilisateur.getId() > 0) {
            update(utilisateur);
            return utilisateur;
        } else {
            return insert(utilisateur);
        }
    }
    
    /**
     * Active ou désactive un utilisateur.
     * @param id L'identifiant de l'utilisateur
     * @param actif Le nouvel état
     * @return true si la mise à jour a réussi
     * @throws SQLException en cas d'erreur SQL
     */
    public boolean setActif(int id, boolean actif) throws SQLException {
        String sql = "UPDATE utilisateurs SET actif = ? WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBoolean(1, actif);
            stmt.setInt(2, id);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Supprime un utilisateur (non recommandé, préférer désactiver).
     * @param id L'identifiant de l'utilisateur à supprimer
     * @return true si la suppression a réussi
     * @throws SQLException en cas d'erreur SQL
     */
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM utilisateurs WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Vérifie si un login existe déjà.
     * @param login Le login à vérifier
     * @param excludeId L'ID à exclure (pour l'édition)
     * @return true si le login existe
     * @throws SQLException en cas d'erreur SQL
     */
    public boolean existsByLogin(String login, int excludeId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM utilisateurs WHERE login = ? AND id != ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, login);
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
     * Vérifie si un login existe déjà (sans exclusion).
     * @param login Le login à vérifier
     * @return true si le login existe
     * @throws SQLException en cas d'erreur SQL
     */
    public boolean existsByLogin(String login) throws SQLException {
        return existsByLogin(login, 0);
    }
    
    /**
     * Récupère les utilisateurs par rôle.
     * @param role Le rôle recherché
     * @return Liste des utilisateurs avec ce rôle
     * @throws SQLException en cas d'erreur SQL
     */
    public List<Utilisateur> findByRole(RoleUtilisateur role) throws SQLException {
        List<Utilisateur> utilisateurs = new ArrayList<>();
        String sql = "SELECT * FROM utilisateurs WHERE role = ? ORDER BY nom_complet";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, role.name());
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    utilisateurs.add(mapResultSetToUtilisateur(rs));
                }
            }
        }
        
        return utilisateurs;
    }
    
    /**
     * Compte le nombre total d'utilisateurs.
     * @return Le nombre d'utilisateurs
     * @throws SQLException en cas d'erreur SQL
     */
    public int count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM utilisateurs";
        
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
     * Mappe un ResultSet vers une entité Utilisateur.
     * @param rs Le ResultSet à mapper
     * @return L'entité Utilisateur
     * @throws SQLException en cas d'erreur SQL
     */
    private Utilisateur mapResultSetToUtilisateur(ResultSet rs) throws SQLException {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(rs.getInt("id"));
        utilisateur.setLogin(rs.getString("login"));
        utilisateur.setMotDePasse(rs.getString("mot_de_passe"));
        utilisateur.setNomComplet(rs.getString("nom_complet"));
        
        String roleStr = rs.getString("role");
        if (roleStr != null) {
            utilisateur.setRole(RoleUtilisateur.valueOf(roleStr));
        }
        
        utilisateur.setEmail(rs.getString("email"));
        utilisateur.setTelephone(rs.getString("telephone"));
        utilisateur.setActif(rs.getBoolean("actif"));
        
        Timestamp tsCreation = rs.getTimestamp("date_creation");
        if (tsCreation != null) {
            utilisateur.setDateCreation(tsCreation.toLocalDateTime());
        }
        
        Timestamp tsConnexion = rs.getTimestamp("derniere_connexion");
        if (tsConnexion != null) {
            utilisateur.setDerniereConnexion(tsConnexion.toLocalDateTime());
        }
        
        return utilisateur;
    }
}
