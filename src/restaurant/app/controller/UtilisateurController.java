package restaurant.app.controller;

import java.sql.SQLException;
import java.util.List;
import restaurant.app.dao.UtilisateurDAO;
import restaurant.app.model.entities.Utilisateur;
import restaurant.app.model.enums.RoleUtilisateur;
import restaurant.app.util.SecurityUtil;
import restaurant.app.util.ValidationUtil;

/**
 * Contrôleur pour la gestion des utilisateurs et l'authentification.
 * 
 * @author Restaurant App
 * @version 1.0
 */
public class UtilisateurController {
    
    private final UtilisateurDAO utilisateurDAO;
    
    // Utilisateur actuellement connecté
    private static Utilisateur utilisateurConnecte;
    
    /**
     * Constructeur par défaut.
     */
    public UtilisateurController() {
        this.utilisateurDAO = new UtilisateurDAO();
    }
    
    /**
     * Authentifie un utilisateur.
     * @param login Le login
     * @param motDePasse Le mot de passe
     * @return L'utilisateur authentifié
     * @throws Exception si l'authentification échoue
     */
    public Utilisateur authentifier(String login, String motDePasse) throws Exception {
        // Validation des entrées
        if (login == null || login.trim().isEmpty()) {
            throw new Exception("Le login est obligatoire");
        }
        
        if (motDePasse == null || motDePasse.isEmpty()) {
            throw new Exception("Le mot de passe est obligatoire");
        }
        
        try {
            // Rechercher l'utilisateur par login
            Utilisateur utilisateur = utilisateurDAO.findByLogin(login.trim());
            
            if (utilisateur == null) {
                throw new Exception("Login ou mot de passe incorrect");
            }
            
            // Vérifier si le compte est actif
            if (!utilisateur.isActif()) {
                throw new Exception("Ce compte a été désactivé");
            }
            
            // Vérifier le mot de passe
            if (!SecurityUtil.verifyPassword(motDePasse, utilisateur.getMotDePasse())) {
                throw new Exception("Login ou mot de passe incorrect");
            }
            
            // Stocker l'utilisateur connecté
            utilisateurConnecte = utilisateur;
            
            return utilisateur;
            
        } catch (SQLException e) {
            throw new Exception("Erreur lors de l'authentification: " + e.getMessage(), e);
        }
    }
    
    /**
     * Déconnecte l'utilisateur actuel.
     */
    public void deconnecter() {
        utilisateurConnecte = null;
    }
    
    /**
     * Retourne l'utilisateur actuellement connecté.
     * @return L'utilisateur connecté ou null
     */
    public static Utilisateur getUtilisateurConnecte() {
        return utilisateurConnecte;
    }
    
    /**
     * Vérifie si un utilisateur est connecté.
     * @return true si connecté
     */
    public static boolean isConnecte() {
        return utilisateurConnecte != null;
    }
    
    /**
     * Vérifie si l'utilisateur connecté est admin.
     * @return true si admin
     */
    public static boolean isAdmin() {
        return utilisateurConnecte != null && 
               utilisateurConnecte.getRole() == RoleUtilisateur.ADMIN;
    }
    
    /**
     * Récupère tous les utilisateurs.
     * @return Liste des utilisateurs
     * @throws Exception en cas d'erreur
     */
    public List<Utilisateur> getAllUtilisateurs() throws Exception {
        try {
            return utilisateurDAO.findAll();
        } catch (SQLException e) {
            throw new Exception("Erreur lors de la récupération des utilisateurs: " + e.getMessage(), e);
        }
    }
    
    /**
     * Récupère les utilisateurs par rôle.
     * @param role Le rôle
     * @return Liste des utilisateurs
     * @throws Exception en cas d'erreur
     */
    public List<Utilisateur> getUtilisateursByRole(RoleUtilisateur role) throws Exception {
        try {
            return utilisateurDAO.findByRole(role);
        } catch (SQLException e) {
            throw new Exception("Erreur lors de la récupération des utilisateurs: " + e.getMessage(), e);
        }
    }
    
    /**
     * Récupère un utilisateur par son ID.
     * @param id L'identifiant
     * @return L'utilisateur ou null
     * @throws Exception en cas d'erreur
     */
    public Utilisateur getUtilisateurById(int id) throws Exception {
        try {
            return utilisateurDAO.findById(id);
        } catch (SQLException e) {
            throw new Exception("Erreur lors de la récupération de l'utilisateur: " + e.getMessage(), e);
        }
    }
    
    /**
     * Recherche des utilisateurs.
     * @param searchText Le texte à rechercher
     * @return Liste des utilisateurs correspondants
     * @throws Exception en cas d'erreur
     */
    public List<Utilisateur> searchUtilisateurs(String searchText) throws Exception {
        try {
            if (searchText == null || searchText.trim().isEmpty()) {
                return utilisateurDAO.findAll();
            }
            return utilisateurDAO.search(searchText);
        } catch (SQLException e) {
            throw new Exception("Erreur lors de la recherche des utilisateurs: " + e.getMessage(), e);
        }
    }
    
    /**
     * Crée un nouvel utilisateur.
     * @param utilisateur L'utilisateur à créer
     * @param motDePasse Le mot de passe en clair
     * @return L'utilisateur créé
     * @throws Exception en cas d'erreur ou de validation
     */
    public Utilisateur creerUtilisateur(Utilisateur utilisateur, String motDePasse) throws Exception {
        // Validation
        validateUtilisateur(utilisateur, motDePasse, true);
        
        try {
            // Vérifier l'unicité du login
            if (utilisateurDAO.existsByLogin(utilisateur.getLogin())) {
                throw new Exception("Ce login est déjà utilisé");
            }
            
            // Hasher le mot de passe
            utilisateur.setMotDePasse(SecurityUtil.hashPassword(motDePasse));
            
            return utilisateurDAO.save(utilisateur);
            
        } catch (SQLException e) {
            throw new Exception("Erreur lors de la création de l'utilisateur: " + e.getMessage(), e);
        }
    }
    
    /**
     * Met à jour un utilisateur.
     * @param utilisateur L'utilisateur à mettre à jour
     * @return L'utilisateur mis à jour
     * @throws Exception en cas d'erreur ou de validation
     */
    public Utilisateur updateUtilisateur(Utilisateur utilisateur) throws Exception {
        // Validation (sans vérification du mot de passe)
        validateUtilisateur(utilisateur, null, false);
        
        try {
            // Vérifier l'unicité du login
            Utilisateur existant = utilisateurDAO.findByLogin(utilisateur.getLogin());
            if (existant != null && existant.getId() != utilisateur.getId()) {
                throw new Exception("Ce login est déjà utilisé par un autre utilisateur");
            }
            
            return utilisateurDAO.save(utilisateur);
            
        } catch (SQLException e) {
            throw new Exception("Erreur lors de la mise à jour de l'utilisateur: " + e.getMessage(), e);
        }
    }
    
    /**
     * Change le mot de passe d'un utilisateur.
     * @param utilisateurId L'identifiant de l'utilisateur
     * @param nouveauMotDePasse Le nouveau mot de passe
     * @throws Exception en cas d'erreur
     */
    public void changerMotDePasse(int utilisateurId, String nouveauMotDePasse) throws Exception {
        // Validation du nouveau mot de passe
        if (!ValidationUtil.isValidPassword(nouveauMotDePasse)) {
            throw new Exception("Le mot de passe doit contenir au moins 6 caractères");
        }
        
        try {
            Utilisateur utilisateur = utilisateurDAO.findById(utilisateurId);
            if (utilisateur == null) {
                throw new Exception("Utilisateur introuvable");
            }
            
            utilisateur.setMotDePasse(SecurityUtil.hashPassword(nouveauMotDePasse));
            utilisateurDAO.save(utilisateur);
            
        } catch (SQLException e) {
            throw new Exception("Erreur lors du changement de mot de passe: " + e.getMessage(), e);
        }
    }
    
    /**
     * Active ou désactive un utilisateur.
     * @param utilisateurId L'identifiant de l'utilisateur
     * @param actif Le nouvel état
     * @throws Exception en cas d'erreur
     */
    public void setActif(int utilisateurId, boolean actif) throws Exception {
        try {
            Utilisateur utilisateur = utilisateurDAO.findById(utilisateurId);
            if (utilisateur == null) {
                throw new Exception("Utilisateur introuvable");
            }
            
            // Empêcher la désactivation de son propre compte
            if (utilisateurConnecte != null && utilisateurConnecte.getId() == utilisateurId && !actif) {
                throw new Exception("Vous ne pouvez pas désactiver votre propre compte");
            }
            
            utilisateur.setActif(actif);
            utilisateurDAO.save(utilisateur);
            
        } catch (SQLException e) {
            throw new Exception("Erreur lors de la modification du statut: " + e.getMessage(), e);
        }
    }
    
    /**
     * Supprime un utilisateur.
     * @param id L'identifiant de l'utilisateur
     * @throws Exception en cas d'erreur
     */
    public void deleteUtilisateur(int id) throws Exception {
        // Empêcher la suppression de son propre compte
        if (utilisateurConnecte != null && utilisateurConnecte.getId() == id) {
            throw new Exception("Vous ne pouvez pas supprimer votre propre compte");
        }
        
        try {
            if (!utilisateurDAO.delete(id)) {
                throw new Exception("L'utilisateur n'a pas pu être supprimé");
            }
        } catch (SQLException e) {
            throw new Exception("Erreur lors de la suppression de l'utilisateur: " + e.getMessage(), e);
        }
    }
    
    /**
     * Valide les données d'un utilisateur.
     * @param utilisateur L'utilisateur à valider
     * @param motDePasse Le mot de passe (pour création)
     * @param nouveau true si c'est une création
     * @throws Exception si les données sont invalides
     */
    private void validateUtilisateur(Utilisateur utilisateur, String motDePasse, boolean nouveau) throws Exception {
        if (utilisateur.getLogin() == null || utilisateur.getLogin().trim().isEmpty()) {
            throw new Exception("Le login est obligatoire");
        }
        
        if (!ValidationUtil.isValidUsername(utilisateur.getLogin())) {
            throw new Exception("Le login doit contenir entre 3 et 50 caractères alphanumériques");
        }
        
        if (utilisateur.getNomComplet() == null || utilisateur.getNomComplet().trim().isEmpty()) {
            throw new Exception("Le nom complet est obligatoire");
        }
        
        if (utilisateur.getRole() == null) {
            throw new Exception("Le rôle est obligatoire");
        }
        
        // Vérification du mot de passe pour les nouveaux utilisateurs
        if (nouveau) {
            if (motDePasse == null || motDePasse.isEmpty()) {
                throw new Exception("Le mot de passe est obligatoire");
            }
            
            if (!ValidationUtil.isValidPassword(motDePasse)) {
                throw new Exception("Le mot de passe doit contenir au moins 6 caractères");
            }
        }
    }
    
    /**
     * Compte le nombre d'utilisateurs.
     * @return Le nombre d'utilisateurs
     * @throws Exception en cas d'erreur
     */
    public int getCount() throws Exception {
        try {
            return utilisateurDAO.count();
        } catch (SQLException e) {
            throw new Exception("Erreur lors du comptage des utilisateurs: " + e.getMessage(), e);
        }
    }
}
