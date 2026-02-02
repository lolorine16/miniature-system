package restaurant.app.model.entities;

import java.util.Objects;

/**
 * Entité représentant une ligne de commande.
 * Contient un produit avec sa quantité et son prix au moment de l'achat.
 * 
 * @author Restaurant App
 * @version 1.0
 */
public class LigneCommande {
    
    private int id;
    private Commande commande;
    private Produit produit;
    private int quantite;
    private double prixUnitaire;
    private double montantLigne;

    /**
     * Constructeur par défaut.
     */
    public LigneCommande() {
    }

    /**
     * Constructeur avec produit et quantité.
     * Le prix unitaire est automatiquement récupéré du produit.
     * @param produit Le produit
     * @param quantite La quantité
     */
    public LigneCommande(Produit produit, int quantite) {
        setProduit(produit);
        setQuantite(quantite);
        this.prixUnitaire = produit.getPrixVente();
        calculateMontant();
    }

    /**
     * Constructeur complet.
     * @param id L'identifiant unique
     * @param commande La commande parente
     * @param produit Le produit
     * @param quantite La quantité
     * @param prixUnitaire Le prix unitaire
     */
    public LigneCommande(int id, Commande commande, Produit produit, int quantite, double prixUnitaire) {
        this.id = id;
        this.commande = commande;
        setProduit(produit);
        setQuantite(quantite);
        setPrixUnitaire(prixUnitaire);
        calculateMontant();
    }

    // Getters et Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Commande getCommande() {
        return commande;
    }

    public void setCommande(Commande commande) {
        this.commande = commande;
    }

    public Produit getProduit() {
        return produit;
    }

    /**
     * Définit le produit de la ligne.
     * @param produit Le produit (non nul)
     * @throws IllegalArgumentException si le produit est nul
     */
    public void setProduit(Produit produit) {
        if (produit == null) {
            throw new IllegalArgumentException("Le produit de la ligne ne peut pas être nul");
        }
        this.produit = produit;
    }

    public int getQuantite() {
        return quantite;
    }

    /**
     * Définit la quantité.
     * @param quantite La quantité (> 0)
     * @throws IllegalArgumentException si la quantité n'est pas positive
     */
    public void setQuantite(int quantite) {
        if (quantite <= 0) {
            throw new IllegalArgumentException("La quantité doit être supérieure à 0");
        }
        this.quantite = quantite;
        calculateMontant();
    }

    public double getPrixUnitaire() {
        return prixUnitaire;
    }

    /**
     * Définit le prix unitaire.
     * @param prixUnitaire Le prix (> 0)
     * @throws IllegalArgumentException si le prix n'est pas positif
     */
    public void setPrixUnitaire(double prixUnitaire) {
        if (prixUnitaire <= 0) {
            throw new IllegalArgumentException("Le prix unitaire doit être strictement positif");
        }
        this.prixUnitaire = prixUnitaire;
        calculateMontant();
    }

    public double getMontantLigne() {
        return montantLigne;
    }

    public void setMontantLigne(double montantLigne) {
        this.montantLigne = montantLigne;
    }

    // Méthodes métier

    /**
     * Calcule et met à jour le montant de la ligne.
     * @return Le montant calculé (prix x quantité)
     */
    public double calculateMontant() {
        this.montantLigne = prixUnitaire * quantite;
        return this.montantLigne;
    }

    /**
     * Augmente la quantité de 1.
     */
    public void incrementQuantite() {
        setQuantite(this.quantite + 1);
    }

    /**
     * Diminue la quantité de 1 (minimum 1).
     */
    public void decrementQuantite() {
        if (this.quantite > 1) {
            setQuantite(this.quantite - 1);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LigneCommande that = (LigneCommande) o;
        // Si les deux ont un ID, comparer par ID
        if (id != 0 && that.id != 0) {
            return id == that.id;
        }
        // Sinon comparer par produit
        return Objects.equals(produit, that.produit);
    }

    @Override
    public int hashCode() {
        return id != 0 ? Objects.hash(id) : Objects.hash(produit);
    }

    @Override
    public String toString() {
        return produit != null ? produit.getNom() + " x" + quantite : "LigneCommande #" + id;
    }
}
