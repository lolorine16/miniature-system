package restaurant.app.model.entities;

import java.time.LocalDateTime;
import java.util.Objects;
import restaurant.app.model.enums.RoleUtilisateur;

/**
 * Entité représentant un utilisateur du système.
 * Peut être un administrateur ou un employé.
 * 
 * @author Restaurant App
 * @version 1.0
 */
public class Utilisateur {
    
    private int id;
    private String login;
    private String motDePasse;
    private String nomComplet;
    private RoleUtilisateur role;
    private String email;
    private String telephone;
    private boolean actif;
    private LocalDateTime dateCreation;
    private LocalDateTime derniereConnexion;

    /**
     * Constructeur par défaut.
     */
    public Utilisateur() {
        this.role = RoleUtilisateur.EMPLOYE;
        this.actif = true;
        this.dateCreation = LocalDateTime.now();
    }

    /**
     * Constructeur avec paramètres essentiels.
     * @param login Le login
     * @param motDePasse Le mot de passe
     * @param nomComplet Le nom complet
     */
    public Utilisateur(String login, String motDePasse, String nomComplet) {
        this();
        setLogin(login);
        setMotDePasse(motDePasse);
        setNomComplet(nomComplet);
    }

    /**
     * Constructeur complet.
     * @param id L'identifiant unique
     * @param login Le login
     * @param motDePasse Le mot de passe
     * @param nomComplet Le nom complet
     * @param role Le rôle
     * @param email L'email
     * @param telephone Le téléphone
     * @param actif Si l'utilisateur est actif
     */
    public Utilisateur(int id, String login, String motDePasse, String nomComplet,
                       RoleUtilisateur role, String email, String telephone, boolean actif) {
        this.id = id;
        setLogin(login);
        setMotDePasse(motDePasse);
        setNomComplet(nomComplet);
        this.role = role != null ? role : RoleUtilisateur.EMPLOYE;
        this.email = email;
        this.telephone = telephone;
        this.actif = actif;
        this.dateCreation = LocalDateTime.now();
    }

    // Getters et Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    /**
     * Définit le login de l'utilisateur.
     * @param login Le login (non vide)
     * @throws IllegalArgumentException si le login est null ou vide
     */
    public void setLogin(String login) {
        if (login == null || login.trim().isEmpty()) {
            throw new IllegalArgumentException("Le login ne peut pas être vide");
        }
        this.login = login.trim();
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    /**
     * Définit le mot de passe de l'utilisateur.
     * @param motDePasse Le mot de passe (non vide)
     * @throws IllegalArgumentException si le mot de passe est null ou vide
     */
    public void setMotDePasse(String motDePasse) {
        if (motDePasse == null || motDePasse.isEmpty()) {
            throw new IllegalArgumentException("Le mot de passe ne peut pas être vide");
        }
        this.motDePasse = motDePasse;
    }

    public String getNomComplet() {
        return nomComplet;
    }

    /**
     * Définit le nom complet de l'utilisateur.
     * @param nomComplet Le nom complet (non vide)
     * @throws IllegalArgumentException si le nom est null ou vide
     */
    public void setNomComplet(String nomComplet) {
        if (nomComplet == null || nomComplet.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom complet ne peut pas être vide");
        }
        this.nomComplet = nomComplet.trim();
    }

    public RoleUtilisateur getRole() {
        return role;
    }

    public void setRole(RoleUtilisateur role) {
        this.role = role != null ? role : RoleUtilisateur.EMPLOYE;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public LocalDateTime getDerniereConnexion() {
        return derniereConnexion;
    }

    public void setDerniereConnexion(LocalDateTime derniereConnexion) {
        this.derniereConnexion = derniereConnexion;
    }

    // Méthodes métier

    /**
     * Vérifie si l'utilisateur est administrateur.
     * @return true si l'utilisateur est administrateur
     */
    public boolean isAdmin() {
        return role == RoleUtilisateur.ADMIN;
    }

    /**
     * Met à jour la date de dernière connexion à maintenant.
     */
    public void updateDerniereConnexion() {
        this.derniereConnexion = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Utilisateur that = (Utilisateur) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return nomComplet != null ? nomComplet : login;
    }
}
