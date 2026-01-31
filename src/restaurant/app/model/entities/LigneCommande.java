package app.model.entities;

import java.util.Objects;

public class LigneCommande {
    private final long id;
    private Produit produit;
    private int quantite;
    private double prixUnitaire;

    public LigneCommande(long id, Produit produit, int quantite, double prixUnitaire) {
        this.id = id;
        setProduit(produit);
        setQuantite(quantite);
        setPrixUnitaire(prixUnitaire);
    }

    public long getId() {
        return id;
    }

    public Produit getProduit() {
        return produit;
    }

    public void setProduit(Produit produit) {
        if (produit == null) {
            throw new IllegalArgumentException("Le produit de la ligne ne peut pas etre nul");
        }
        this.produit = produit;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        if (quantite <= 0) {
            throw new IllegalArgumentException("La quantite doit etre supérieure a 0");
        }
        this.quantite = quantite;
    }

    public double getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(double prixUnitaire) {
        if (prixUnitaire <= 0) {
            throw new IllegalArgumentException("Le prix unitaire doit etre strictement positif");
        }
        this.prixUnitaire = prixUnitaire;
    }

    public double getMontantLigne() {
        return calculateMontant();
    }

    public double calculateMontant() {
        return prixUnitaire * quantite;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LigneCommande that = (LigneCommande) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "LigneCommande{" +
                "id=" + id +
                ", produit=" + produit +
                ", quantite=" + quantite +
                ", prixUnitaire=" + prixUnitaire +
                '}';
    }
}
