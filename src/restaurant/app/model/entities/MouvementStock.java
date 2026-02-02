package restaurant.app.model.entities;

import restaurant.app.model.enums.TypeMouvement;

import restaurant.app.model.enums.TypeMouvement;
/**
 * Entité représentant un mouvement de stock (entrée ou sortie).
 * Permet de tracer l'historique des modifications de stock.
 * 
 * @author Restaurant App
 * @version 1.0
 */
public class MouvementStock {
    
    private int id;
    private Produit produit;
    private TypeMouvement type;
    private int quantite;
    private LocalDateTime dateMouvement;
    private String motif;
    private Utilisateur utilisateur;

    /**
     * Constructeur par défaut.
     */
    public MouvementStock() {
        this.dateMouvement = LocalDateTime.now();
    }

    /**
     * Constructeur avec paramètres essentiels.
     * @param produit Le produit concerné
     * @param type Le type de mouvement
     * @param quantite La quantité
     * @param motif Le motif du mouvement
     */
    public MouvementStock(Produit produit, TypeMouvement type, int quantite, String motif) {
        this();
        setProduit(produit);
        setType(type);
        setQuantite(quantite);
        setMotif(motif);
    }

    /**
     * Constructeur complet.
     * @param id L'identifiant unique
     * @param produit Le produit concerné
     * @param type Le type de mouvement
     * @param quantite La quantité
     * @param dateMouvement La date du mouvement
     * @param motif Le motif
     * @param utilisateur L'utilisateur ayant effectué le mouvement
     */
    public MouvementStock(int id, Produit produit, TypeMouvement type, int quantite,
                          LocalDateTime dateMouvement, String motif, Utilisateur utilisateur) {
        this.id = id;
        setProduit(produit);
        setType(type);
        setQuantite(quantite);
        this.dateMouvement = dateMouvement != null ? dateMouvement : LocalDateTime.now();
        setMotif(motif);
        this.utilisateur = utilisateur;
    }

    // Getters et Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Produit getProduit() {
        return produit;
    }

    /**
     * Définit le produit concerné par le mouvement.
     * @param produit Le produit (non nul)
     * @throws IllegalArgumentException si le produit est nul
     */
    public void setProduit(Produit produit) {
        if (produit == null) {
            throw new IllegalArgumentException("Le produit du mouvement ne peut pas être nul");
        }
        this.produit = produit;
    }

    public TypeMouvement getType() {
        return type;
    }

    /**
     * Définit le type de mouvement.
     * @param type Le type (non nul)
     * @throws IllegalArgumentException si le type est nul
     */
    public void setType(TypeMouvement type) {
        if (type == null) {
            throw new IllegalArgumentException("Le type de mouvement ne peut pas être nul");
        }
        this.type = type;
    }

    public int getQuantite() {
        return quantite;
    }

    /**
     * Définit la quantité du mouvement.
     * @param quantite La quantité (> 0)
     * @throws IllegalArgumentException si la quantité n'est pas positive
     */
    public void setQuantite(int quantite) {
        if (quantite <= 0) {
            throw new IllegalArgumentException("La quantité doit être strictement positive");
        }
        this.quantite = quantite;
    }

    public LocalDateTime getDateMouvement() {
        return dateMouvement;
    }

    public void setDateMouvement(LocalDateTime dateMouvement) {
        this.dateMouvement = dateMouvement;
    }

    public String getMotif() {
        return motif;
    }

    /**
     * Définit le motif du mouvement.
     * @param motif Le motif (non vide)
     * @throws IllegalArgumentException si le motif est null ou vide
     */
    public void setMotif(String motif) {
        if (motif == null || motif.trim().isEmpty()) {
            throw new IllegalArgumentException("Le motif du mouvement ne peut pas être vide");
        }
        this.motif = motif.trim();
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    // Méthodes métier

    /**
     * Vérifie si c'est une entrée de stock.
     * @return true si c'est une entrée
     */
    public boolean isEntree() {
        return type == TypeMouvement.ENTREE;
    }

    /**
     * Vérifie si c'est une sortie de stock.
     * @return true si c'est une sortie
     */
    public boolean isSortie() {
        return type == TypeMouvement.SORTIE;
    }

    /**
     * Applique le mouvement sur le produit (modifie le stock).
     * @throws IllegalStateException si le mouvement ne peut pas être appliqué
     */
    public void appliquer() {
        if (produit == null) {
            throw new IllegalStateException("Impossible d'appliquer un mouvement sans produit");
        }
        if (type == TypeMouvement.ENTREE) {
            produit.increaseStock(quantite);
        } else {
            produit.decreaseStock(quantite);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MouvementStock that = (MouvementStock) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return type + " de " + quantite + " " + (produit != null ? produit.getNom() : "?");
    }
}
