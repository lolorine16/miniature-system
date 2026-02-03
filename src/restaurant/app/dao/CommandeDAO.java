package restaurant.app.dao;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import restaurant.app.model.entities.*;
import restaurant.app.model.enums.EtatCommande;
import restaurant.app.util.DatabaseConnection;

/**
 * DAO pour la gestion des commandes en base de données.
 * Implémente les opérations CRUD pour l'entité Commande.
 * 
 * @author Restaurant App
 * @version 1.0
 */
public class CommandeDAO {
    
    private final DatabaseConnection dbConnection;
    private final LigneCommandeDAO ligneCommandeDAO;
    private final UtilisateurDAO utilisateurDAO;
    
    /**
     * Constructeur par défaut.
     */
    public CommandeDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
        this.ligneCommandeDAO = new LigneCommandeDAO();
        this.utilisateurDAO = new UtilisateurDAO();
    }
    
    /**
     * Récupère les dernières commandes.
     * @param limite Le nombre maximum de commandes à retourner
     * @return Liste des dernières commandes
     * @throws SQLException en cas d'erreur SQL
     */
    public List<Commande> findRecent(int limite) throws SQLException {
        List<Commande> commandes = new ArrayList<>();
        String sql = "SELECT c.*, u.login, u.nom_complet " +
                     "FROM commandes c " +
                     "LEFT JOIN utilisateurs u ON c.utilisateur_id = u.id " +
                     "ORDER BY c.date_commande DESC LIMIT ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, limite);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    commandes.add(mapResultSetToCommande(rs));
                }
            }
        }
        
        return commandes;
    }

    /**
     * Récupère toutes les commandes.
     * @return Liste de toutes les commandes
     * @throws SQLException en cas d'erreur SQL
     */
    public List<Commande> findAll() throws SQLException {
        List<Commande> commandes = new ArrayList<>();
        String sql = "SELECT c.*, u.login, u.nom_complet " +
                     "FROM commandes c " +
                     "LEFT JOIN utilisateurs u ON c.utilisateur_id = u.id " +
                     "ORDER BY c.date_commande DESC";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                commandes.add(mapResultSetToCommande(rs));
            }
        }
        
        return commandes;
    }
    
    /**
     * Recherche une commande par son ID.
     * @param id L'identifiant de la commande
     * @return La commande trouvée ou null
     * @throws SQLException en cas d'erreur SQL
     */
    public Commande findById(int id) throws SQLException {
        String sql = "SELECT c.*, u.login, u.nom_complet " +
                     "FROM commandes c " +
                     "LEFT JOIN utilisateurs u ON c.utilisateur_id = u.id " +
                     "WHERE c.id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Commande commande = mapResultSetToCommande(rs);
                    // Charger les lignes de commande (utiliser addLigneInternal pour éviter les vérifications d'état)
                    List<LigneCommande> lignes = ligneCommandeDAO.findByCommandeId(id);
                    for (LigneCommande ligne : lignes) {
                        commande.addLigneInternal(ligne);
                    }
                    return commande;
                }
            }
        }
        
        return null;
    }
    
    /**
     * Recherche les commandes avec un état donné.
     * @param etat L'état recherché
     * @return Liste des commandes avec cet état
     * @throws SQLException en cas d'erreur SQL
     */
    public List<Commande> findByEtat(EtatCommande etat) throws SQLException {
        List<Commande> commandes = new ArrayList<>();
        String sql = "SELECT c.*, u.login, u.nom_complet " +
                     "FROM commandes c " +
                     "LEFT JOIN utilisateurs u ON c.utilisateur_id = u.id " +
                     "WHERE c.etat = ? " +
                     "ORDER BY c.date_commande DESC";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, etat.name());
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    commandes.add(mapResultSetToCommande(rs));
                }
            }
        }
        
        return commandes;
    }
    
    /**
     * Recherche les commandes d'une date.
     * @param date La date recherchée
     * @return Liste des commandes de cette date
     * @throws SQLException en cas d'erreur SQL
     */
    public List<Commande> findByDate(LocalDate date) throws SQLException {
        List<Commande> commandes = new ArrayList<>();
        String sql = "SELECT c.*, u.login, u.nom_complet " +
                     "FROM commandes c " +
                     "LEFT JOIN utilisateurs u ON c.utilisateur_id = u.id " +
                     "WHERE DATE(c.date_commande) = ? " +
                     "ORDER BY c.date_commande DESC";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(date));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    commandes.add(mapResultSetToCommande(rs));
                }
            }
        }
        
        return commandes;
    }
    
    /**
     * Recherche les commandes entre deux dates.
     * @param debut Date de début
     * @param fin Date de fin
     * @return Liste des commandes dans la période
     * @throws SQLException en cas d'erreur SQL
     */
    public List<Commande> findByDateRange(LocalDateTime debut, LocalDateTime fin) throws SQLException {
        List<Commande> commandes = new ArrayList<>();
        String sql = "SELECT c.*, u.login, u.nom_complet " +
                     "FROM commandes c " +
                     "LEFT JOIN utilisateurs u ON c.utilisateur_id = u.id " +
                     "WHERE c.date_commande BETWEEN ? AND ? " +
                     "ORDER BY c.date_commande DESC";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setTimestamp(1, Timestamp.valueOf(debut));
            stmt.setTimestamp(2, Timestamp.valueOf(fin));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    commandes.add(mapResultSetToCommande(rs));
                }
            }
        }
        
        return commandes;
    }
    
    /**
     * Recherche multicritère.
     * @param etat L'état (null pour tous)
     * @param debut Date de début (null pour ignorer)
     * @param fin Date de fin (null pour ignorer)
     * @return Liste des commandes correspondantes
     * @throws SQLException en cas d'erreur SQL
     */
    public List<Commande> search(EtatCommande etat, LocalDate debut, LocalDate fin) throws SQLException {
        List<Commande> commandes = new ArrayList<>();
        
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT c.*, u.login, u.nom_complet ");
        sql.append("FROM commandes c ");
        sql.append("LEFT JOIN utilisateurs u ON c.utilisateur_id = u.id ");
        sql.append("WHERE 1=1 ");
        
        if (etat != null) {
            sql.append("AND c.etat = ? ");
        }
        if (debut != null) {
            sql.append("AND DATE(c.date_commande) >= ? ");
        }
        if (fin != null) {
            sql.append("AND DATE(c.date_commande) <= ? ");
        }
        sql.append("ORDER BY c.date_commande DESC");
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            int paramIndex = 1;
            if (etat != null) {
                stmt.setString(paramIndex++, etat.name());
            }
            if (debut != null) {
                stmt.setDate(paramIndex++, Date.valueOf(debut));
            }
            if (fin != null) {
                stmt.setDate(paramIndex++, Date.valueOf(fin));
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    commandes.add(mapResultSetToCommande(rs));
                }
            }
        }
        
        return commandes;
    }
    
    /**
     * Insère une nouvelle commande.
     * @param commande La commande à insérer
     * @return La commande avec son ID généré
     * @throws SQLException en cas d'erreur SQL
     */
    public Commande insert(Commande commande) throws SQLException {
        String sql = "INSERT INTO commandes (date_commande, etat, total, utilisateur_id, " +
                     "client_nom, client_telephone, notes) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setTimestamp(1, Timestamp.valueOf(commande.getDateCommande()));
            stmt.setString(2, commande.getEtat().name());
            stmt.setDouble(3, commande.getTotal());
            
            if (commande.getUtilisateur() != null) {
                stmt.setInt(4, commande.getUtilisateur().getId());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }
            
            stmt.setString(5, commande.getClientNom());
            stmt.setString(6, commande.getClientTelephone());
            stmt.setString(7, commande.getNotes());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        commande.setId(generatedKeys.getInt(1));
                    }
                }
            }
        }
        
        return commande;
    }
    
    /**
     * Met à jour une commande existante.
     * @param commande La commande à mettre à jour
     * @return true si la mise à jour a réussi
     * @throws SQLException en cas d'erreur SQL
     */
    public boolean update(Commande commande) throws SQLException {
        String sql = "UPDATE commandes SET etat = ?, total = ?, client_nom = ?, " +
                     "client_telephone = ?, notes = ? WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, commande.getEtat().name());
            stmt.setDouble(2, commande.getTotal());
            stmt.setString(3, commande.getClientNom());
            stmt.setString(4, commande.getClientTelephone());
            stmt.setString(5, commande.getNotes());
            stmt.setInt(6, commande.getId());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Met à jour l'état d'une commande.
     * @param id L'identifiant de la commande
     * @param etat Le nouvel état
     * @return true si la mise à jour a réussi
     * @throws SQLException en cas d'erreur SQL
     */
    public boolean updateEtat(int id, EtatCommande etat) throws SQLException {
        String sql = "UPDATE commandes SET etat = ? WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, etat.name());
            stmt.setInt(2, id);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Met à jour le total d'une commande.
     * @param id L'identifiant de la commande
     * @param total Le nouveau total
     * @return true si la mise à jour a réussi
     * @throws SQLException en cas d'erreur SQL
     */
    public boolean updateTotal(int id, double total) throws SQLException {
        String sql = "UPDATE commandes SET total = ? WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDouble(1, total);
            stmt.setInt(2, id);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Supprime une commande.
     * @param id L'identifiant de la commande à supprimer
     * @return true si la suppression a réussi
     * @throws SQLException en cas d'erreur SQL
     */
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM commandes WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Compte le nombre de commandes par état.
     * @param etat L'état recherché
     * @return Le nombre de commandes
     * @throws SQLException en cas d'erreur SQL
     */
    public int countByEtat(EtatCommande etat) throws SQLException {
        String sql = "SELECT COUNT(*) FROM commandes WHERE etat = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, etat.name());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        
        return 0;
    }
    
    /**
     * Calcule le chiffre d'affaires du jour.
     * @return Le CA du jour
     * @throws SQLException en cas d'erreur SQL
     */
    public double getChiffreAffairesDuJour() throws SQLException {
        String sql = "SELECT COALESCE(SUM(total), 0) FROM commandes " +
                     "WHERE DATE(date_commande) = CURDATE() AND etat = 'VALIDEE'";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getDouble(1);
            }
        }
        
        return 0.0;
    }
    
    /**
     * Calcule le chiffre d'affaires du mois.
     * @return Le CA du mois
     * @throws SQLException en cas d'erreur SQL
     */
    public double getChiffreAffairesDuMois() throws SQLException {
        String sql = "SELECT COALESCE(SUM(total), 0) FROM commandes " +
                     "WHERE YEAR(date_commande) = YEAR(CURDATE()) " +
                     "AND MONTH(date_commande) = MONTH(CURDATE()) AND etat = 'VALIDEE'";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getDouble(1);
            }
        }
        
        return 0.0;
    }
    
    /**
     * Calcule le chiffre d'affaires de l'année.
     * @return Le CA de l'année
     * @throws SQLException en cas d'erreur SQL
     */
    public double getChiffreAffairesAnnee() throws SQLException {
        String sql = "SELECT COALESCE(SUM(total), 0) FROM commandes " +
                     "WHERE YEAR(date_commande) = YEAR(CURDATE()) AND etat != 'ANNULEE'";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getDouble(1);
            }
        }
        
        return 0.0;
    }
    
    /**
     * Compte les commandes pour une date donnée.
     * @param date La date à vérifier
     * @return Le nombre de commandes
     * @throws SQLException en cas d'erreur SQL
     */
    public int countByDate(LocalDate date) throws SQLException {
        String sql = "SELECT COUNT(*) FROM commandes WHERE DATE(date_commande) = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(date));
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        
        return 0;
    }
    
    /**
     * Calcule le total des commandes pour une date donnée.
     * @param date La date à vérifier
     * @return Le total en BigDecimal
     * @throws SQLException en cas d'erreur SQL
     */
    public BigDecimal getTotalByDate(LocalDate date) throws SQLException {
        String sql = "SELECT COALESCE(SUM(total), 0) FROM commandes " +
                     "WHERE DATE(date_commande) = ? AND etat != 'ANNULEE'";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(date));
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal(1);
                }
            }
        }
        
        return BigDecimal.ZERO;
    }
    
    /**
     * Calcule le total des commandes pour une période donnée.
     * @param dateDebut Date de début
     * @param dateFin Date de fin
     * @return Le total en BigDecimal
     * @throws SQLException en cas d'erreur SQL
     */
    public BigDecimal getTotalByPeriod(LocalDate dateDebut, LocalDate dateFin) throws SQLException {
        String sql = "SELECT COALESCE(SUM(total), 0) FROM commandes " +
                     "WHERE DATE(date_commande) BETWEEN ? AND ? AND etat != 'ANNULEE'";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(dateDebut));
            stmt.setDate(2, Date.valueOf(dateFin));
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal(1);
                }
            }
        }
        
        return BigDecimal.ZERO;
    }
    
    /**
     * Compte les commandes pour une période donnée.
     * @param dateDebut Date de début
     * @param dateFin Date de fin
     * @return Le nombre de commandes
     * @throws SQLException en cas d'erreur SQL
     */
    public int countByPeriod(LocalDate dateDebut, LocalDate dateFin) throws SQLException {
        String sql = "SELECT COUNT(*) FROM commandes WHERE DATE(date_commande) BETWEEN ? AND ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(dateDebut));
            stmt.setDate(2, Date.valueOf(dateFin));
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        
        return 0;
    }
    
    /**
     * Mappe un ResultSet vers une entité Commande.
     * @param rs Le ResultSet à mapper
     * @return L'entité Commande
     * @throws SQLException en cas d'erreur SQL
     */
    private Commande mapResultSetToCommande(ResultSet rs) throws SQLException {
        Commande commande = new Commande();
        commande.setId(rs.getInt("id"));
        commande.setTotal(rs.getDouble("total"));
        commande.setClientNom(rs.getString("client_nom"));
        commande.setClientTelephone(rs.getString("client_telephone"));
        commande.setNotes(rs.getString("notes"));
        
        Timestamp ts = rs.getTimestamp("date_commande");
        if (ts != null) {
            commande.setDateCommande(ts.toLocalDateTime());
        }
        
        String etatStr = rs.getString("etat");
        if (etatStr != null) {
            commande.setEtat(EtatCommande.valueOf(etatStr));
        }
        
        // Mapper l'utilisateur si présent
        int utilisateurId = rs.getInt("utilisateur_id");
        if (!rs.wasNull()) {
            Utilisateur utilisateur = new Utilisateur();
            utilisateur.setId(utilisateurId);
            utilisateur.setLogin(rs.getString("login"));
            utilisateur.setNomComplet(rs.getString("nom_complet"));
            commande.setUtilisateur(utilisateur);
        }
        
        return commande;
    }
}
