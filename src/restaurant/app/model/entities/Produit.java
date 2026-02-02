package restaurant.app.model.entities;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entité représentant un produit du restaurant.
 * Contient les informations sur le produit, son prix, son stock et son seuil d'alerte.
 * 
 * @author Restaurant App
 * @version 1.0
 */
public class Produit {
    
    private int id;
    private String nom;
    private Categorie categorie;
    private double prixVente;
    private int stockActuel;
    private int seuilAlerte;
    private String description;
    private String imagePath;
    private boolean actif;
    private LocalDateTime dateCreation;

    /**
     * Constructeur par défaut.
     */
    public Produit() {
        this.actif = true;
        this.stockActuel = 0;
        this.seuilAlerte = 10;
        this.dateCreation = LocalDateTime.now();
    }

    /**
     * Constructeur avec paramètres essentiels.
     * @param nom Le nom du produit
     * @param categorie La catégorie du produit
     * @param prixVente Le prix de vente
     */
    public Produit(String nom, Categorie categorie, double prixVente) {
        this();
        setNom(nom);
        setCategorie(categorie);
        setPrixVente(prixVente);
    }

    /**
     * Constructeur complet.
     * @param id L'identifiant unique
     * @param nom Le nom du produit
     * @param categorie La catégorie
     * @param prixVente Le prix de vente
     * @param stockActuel Le stock actuel
     * @param seuilAlerte Le seuil d'alerte
     * @param description La description
     * @param imagePath Le chemin de l'image
     * @param actif Si le produit est actif
     */
    public Produit(int id, String nom, Categorie categorie, double prixVente, 
                   int stockActuel, int seuilAlerte, String description, 
                   String imagePath, boolean actif) {
        this.id = id;
        setNom(nom);
        setCategorie(categorie);
        setPrixVente(prixVente);
        setStockActuel(stockActuel);
        setSeuilAlerte(seuilAlerte);
        this.description = description;
        this.imagePath = imagePath;
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

    public String getNom() {
        return nom;
    }

    /**
     * Définit le nom du produit.
     * @param nom Le nom (non vide)
     * @throws IllegalArgumentException si le nom est null ou vide
     */
    public void setNom(String nom) {
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du produit ne peut pas être vide");
        }
        this.nom = nom.trim();
    }

    public Categorie getCategorie() {
        return categorie;
    }

    /**
     * Définit la catégorie du produit.
     * @param categorie La catégorie (non nulle)
     * @throws IllegalArgumentException si la catégorie est nulle
     */
    public void setCategorie(Categorie categorie) {
        if (categorie == null) {
            throw new IllegalArgumentException("La catégorie du produit ne peut pas être nulle");
        }
        this.categorie = categorie;
    }

    public double getPrixVente() {
        return prixVente;
    }

    /**
     * Définit le prix de vente.
     * @param prixVente Le prix (strictement positif)
     * @throws IllegalArgumentException si le prix n'est pas positif
     */
    public void setPrixVente(double prixVente) {
        if (prixVente <= 0) {
            throw new IllegalArgumentException("Le prix de vente doit être strictement positif");
        }
        this.prixVente = prixVente;
    }

    public int getStockActuel() {
        return stockActuel;
    }

    /**
     * Définit le stock actuel.
     * @param stockActuel Le stock (>= 0)
     * @throws IllegalArgumentException si le stock est négatif
     */
    public void setStockActuel(int stockActuel) {
        if (stockActuel < 0) {
            throw new IllegalArgumentException("Le stock actuel ne peut pas être négatif");
        }
        this.stockActuel = stockActuel;
    }

    public int getSeuilAlerte() {
        return seuilAlerte;
    }

    /**
     * Définit le seuil d'alerte.
     * @param seuilAlerte Le seuil (>= 0)
     * @throws IllegalArgumentException si le seuil est négatif
     */
    public void setSeuilAlerte(int seuilAlerte) {
        if (seuilAlerte < 0) {
            throw new IllegalArgumentException("Le seuil d'alerte ne peut pas être négatif");
        }
        this.seuilAlerte = seuilAlerte;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
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

    // Méthodes métier

    /**
     * Augmente le stock du produit.
     * @param quantite La quantité à ajouter (> 0)
     * @throws IllegalArgumentException si la quantité n'est pas positive
     */
    public void increaseStock(int quantite) {
        if (quantite <= 0) {
            throw new IllegalArgumentException("La quantité d'augmentation doit être positive");
        }
        this.stockActuel += quantite;
    }

    /**
     * Diminue le stock du produit.
     * @param quantite La quantité à retirer (> 0)
     * @throws IllegalArgumentException si la quantité n'est pas positive
     * @throws IllegalStateException si le stock devient négatif
     */
    public void decreaseStock(int quantite) {
        if (quantite <= 0) {
            throw new IllegalArgumentException("La quantité de diminution doit être positive");
        }
        if (stockActuel - quantite < 0) {
            throw new IllegalStateException("Stock insuffisant pour cette opération");
        }
        this.stockActuel -= quantite;
    }

    /**
     * Vérifie si le stock est sous le seuil d'alerte.
     * @return true si le stock est inférieur ou égal au seuil d'alerte
     */
    public boolean isBelowAlertThreshold() {
        return stockActuel <= seuilAlerte;
    }

    /**
     * Calcule l'écart entre le stock actuel et le seuil d'alerte.
     * @return L'écart (négatif si sous le seuil)
     */
    public int getEcartSeuil() {
        return stockActuel - seuilAlerte;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Produit produit = (Produit) o;
        return id == produit.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return nom;
    }
}
