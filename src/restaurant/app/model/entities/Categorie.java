package restaurant.app.model.entities;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entité représentant une catégorie de produits.
 * Une catégorie permet de regrouper les produits par type (Boissons, Plats, etc.)
 * 
 * @author Restaurant App
 * @version 1.0
 */
public class Categorie {
    
    private int id;
    private String libelle;
    private String description;
    private LocalDateTime dateCreation;

    /**
     * Constructeur par défaut.
     */
    public Categorie() {
        this.dateCreation = LocalDateTime.now();
    }

    /**
     * Constructeur avec libellé uniquement.
     * @param libelle Le libellé de la catégorie
     */
    public Categorie(String libelle) {
        this();
        setLibelle(libelle);
    }

    /**
     * Constructeur complet.
     * @param id L'identifiant unique
     * @param libelle Le libellé de la catégorie
     * @param description La description de la catégorie
     */
    public Categorie(int id, String libelle, String description) {
        this.id = id;
        setLibelle(libelle);
        this.description = description;
        this.dateCreation = LocalDateTime.now();
    }

    /**
     * Constructeur complet avec date.
     * @param id L'identifiant unique
     * @param libelle Le libellé de la catégorie
     * @param description La description de la catégorie
     * @param dateCreation La date de création
     */
    public Categorie(int id, String libelle, String description, LocalDateTime dateCreation) {
        this.id = id;
        setLibelle(libelle);
        this.description = description;
        this.dateCreation = dateCreation;
    }

    // Getters et Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLibelle() {
        return libelle;
    }

    /**
     * Définit le libellé de la catégorie.
     * @param libelle Le libellé (non vide)
     * @throws IllegalArgumentException si le libellé est null ou vide
     */
    public void setLibelle(String libelle) {
        if (libelle == null || libelle.trim().isEmpty()) {
            throw new IllegalArgumentException("Le libellé de la catégorie ne peut pas être vide");
        }
        this.libelle = libelle.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Categorie categorie = (Categorie) o;
        return id == categorie.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return libelle;
    }
}
