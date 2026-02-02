package restaurant.app.model.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import restaurant.app.model.enums.EtatCommande;

/**
 * Entité représentant une commande client.
 * Contient les lignes de commande et gère le cycle de vie de la commande.
 * 
 * @author Restaurant App
 * @version 1.0
 */
public class Commande {
    
    private int id;
    private LocalDateTime dateCommande;
    private EtatCommande etat;
    private double total;
    private Utilisateur utilisateur;
    private String clientNom;
    private String clientTelephone;
    private String notes;
    private final List<LigneCommande> lignes;

    /**
     * Constructeur par défaut.
     */
    public Commande() {
        this.dateCommande = LocalDateTime.now();
        this.etat = EtatCommande.EN_ATTENTE;
        this.total = 0.0;
        this.lignes = new ArrayList<>();
    }

    /**
     * Constructeur avec utilisateur.
     * @param utilisateur L'utilisateur qui crée la commande
     */
    public Commande(Utilisateur utilisateur) {
        this();
        this.utilisateur = utilisateur;
    }

    /**
     * Constructeur complet.
     * @param id L'identifiant unique
     * @param dateCommande La date de la commande
     * @param etat L'état de la commande
     * @param total Le total de la commande
     * @param utilisateur L'utilisateur
     * @param clientNom Le nom du client
     * @param clientTelephone Le téléphone du client
     * @param notes Les notes
     */
    public Commande(int id, LocalDateTime dateCommande, EtatCommande etat, double total,
                    Utilisateur utilisateur, String clientNom, String clientTelephone, String notes) {
        this.id = id;
        this.dateCommande = dateCommande != null ? dateCommande : LocalDateTime.now();
        this.etat = etat != null ? etat : EtatCommande.EN_ATTENTE;
        this.total = total;
        this.utilisateur = utilisateur;
        this.clientNom = clientNom;
        this.clientTelephone = clientTelephone;
        this.notes = notes;
        this.lignes = new ArrayList<>();
    }

    // Getters et Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getDateCommande() {
        return dateCommande;
    }

    public void setDateCommande(LocalDateTime dateCommande) {
        this.dateCommande = dateCommande;
    }

    public EtatCommande getEtat() {
        return etat;
    }

    /**
     * Définit l'état de la commande.
     * @param etat L'état (non nul)
     * @throws IllegalArgumentException si l'état est nul
     */
    public void setEtat(EtatCommande etat) {
        if (etat == null) {
            throw new IllegalArgumentException("L'état de la commande ne peut pas être nul");
        }
        this.etat = etat;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public String getClientNom() {
        return clientNom;
    }

    public void setClientNom(String clientNom) {
        this.clientNom = clientNom;
    }

    public String getClientTelephone() {
        return clientTelephone;
    }

    public void setClientTelephone(String clientTelephone) {
        this.clientTelephone = clientTelephone;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * Retourne une copie non modifiable des lignes de commande.
     * @return Liste des lignes de commande
     */
    public List<LigneCommande> getLignes() {
        return Collections.unmodifiableList(lignes);
    }

    // Méthodes métier

    /**
     * Ajoute une ligne à la commande.
     * @param ligne La ligne à ajouter
     * @throws IllegalArgumentException si la ligne est nulle
     * @throws IllegalStateException si la commande n'est pas en cours
     */
    public void addLigne(LigneCommande ligne) {
        if (ligne == null) {
            throw new IllegalArgumentException("La ligne ne peut pas être nulle");
        }
        if (etat != EtatCommande.EN_ATTENTE) {
            throw new IllegalStateException("Impossible de modifier une commande qui n'est pas en cours");
        }
        ligne.setCommande(this);
        lignes.add(ligne);
        recalculateTotal();
    }

    /**
     * Retire une ligne de la commande.
     * @param ligne La ligne à retirer
     * @throws IllegalArgumentException si la ligne est nulle
     * @throws IllegalStateException si la commande n'est pas en cours
     */
    public void removeLigne(LigneCommande ligne) {
        if (ligne == null) {
            throw new IllegalArgumentException("La ligne ne peut pas être nulle");
        }
        if (etat != EtatCommande.EN_ATTENTE) {
            throw new IllegalStateException("Impossible de modifier une commande qui n'est pas en cours");
        }
        lignes.remove(ligne);
        recalculateTotal();
    }

    /**
     * Vide toutes les lignes de la commande.
     * @throws IllegalStateException si la commande n'est pas en cours
     */
    public void clearLignes() {
        if (etat != EtatCommande.EN_ATTENTE) {
            throw new IllegalStateException("Impossible de modifier une commande qui n'est pas en cours");
        }
        lignes.clear();
        this.total = 0.0;
    }

    /**
     * Recalcule le total de la commande.
     */
    public void recalculateTotal() {
        this.total = lignes.stream()
                .mapToDouble(LigneCommande::getMontantLigne)
                .sum();
    }

    /**
     * Calcule et retourne le total (sans modifier l'attribut).
     * @return Le total calculé
     */
    public double calculateTotal() {
        return lignes.stream()
                .mapToDouble(LigneCommande::getMontantLigne)
                .sum();
    }

    /**
     * Valide la commande.
     * @throws IllegalStateException si la commande est vide ou n'est pas en cours
     */
    public void validateCommande() {
        if (lignes.isEmpty()) {
            throw new IllegalStateException("Une commande valide doit contenir au moins une ligne");
        }
        if (etat != EtatCommande.EN_ATTENTE) {
            throw new IllegalStateException("Seule une commande en cours peut être validée");
        }
        this.etat = EtatCommande.LIVREE;
        recalculateTotal();
    }

    /**
     * Annule la commande.
     * @throws IllegalStateException si la commande est déjà annulée
     */
    public void annulerCommande() {
        if (etat == EtatCommande.ANNULEE) {
            throw new IllegalStateException("La commande est déjà annulée");
        }
        this.etat = EtatCommande.ANNULEE;
    }

    /**
     * Vérifie si la commande est modifiable.
     * @return true si la commande est en cours
     */
    public boolean isModifiable() {
        return etat == EtatCommande.EN_ATTENTE;
    }

    /**
     * Retourne le nombre de lignes.
     * @return Le nombre de lignes
     */
    public int getNombreLignes() {
        return lignes.size();
    }

    /**
     * Retourne le nombre total d'articles.
     * @return La somme des quantités
     */
    public int getNombreArticles() {
        return lignes.stream()
                .mapToInt(LigneCommande::getQuantite)
                .sum();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Commande commande = (Commande) o;
        return id == commande.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Commande #" + id;
    }
}
