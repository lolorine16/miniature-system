package app.model.entities;

import app.model.enums.EtatCommande;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Commande {
    private final long id;
    private final LocalDateTime dateCommande;
    private EtatCommande etat;
    private final List<LigneCommande> lignes;

    public Commande(long id, LocalDateTime dateCommande) {
        this.id = id;
        if (dateCommande == null) {
            throw new IllegalArgumentException("La date de la commande ne peut pas etre nulle");
        }
        this.dateCommande = dateCommande;
        this.etat = EtatCommande.EN_COURS;
        this.lignes = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public LocalDateTime getDateCommande() {
        return dateCommande;
    }

    public EtatCommande getEtat() {
        return etat;
    }

    public void setEtat(EtatCommande etat) {
        if (etat == null) {
            throw new IllegalArgumentException("L'etat de la commande ne peut pas etre nul");
        }
        this.etat = etat;
    }

    public List<LigneCommande> getLignes() {
        return Collections.unmodifiableList(lignes);
    }

    public void addLigne(LigneCommande ligne) {
        if (ligne == null) {
            throw new IllegalArgumentException("La ligne ne peut pas etre nulle");
        }
        if (etat != EtatCommande.EN_COURS) {
            throw new IllegalStateException("On ne peut pas modifier une commande qui n'est pas en cours");
        }
        lignes.add(ligne);
    }

    public void removeLigne(LigneCommande ligne) {
        if (ligne == null) {
            throw new IllegalArgumentException("La ligne ne peut pas etre nulle");
        }
        if (etat != EtatCommande.EN_COURS) {
            throw new IllegalStateException("On ne peut pas modifier une commande qui n'est pas en cours");
        }
        lignes.remove(ligne);
    }

    public double calculateTotal() {
        return lignes.stream().mapToDouble(LigneCommande::getMontantLigne).sum();
    }

    public void validateCommande() {
        if (lignes.isEmpty()) {
            throw new IllegalStateException("Une commande valide doit contenir au moins une ligne");
        }
        this.etat = EtatCommande.VALIDEE;
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
        return "Commande{" +
                "id=" + id +
                ", dateCommande=" + dateCommande +
                ", etat=" + etat +
                ", lignes=" + lignes +
                '}';
    }
}
