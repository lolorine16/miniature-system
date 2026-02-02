package restaurant.app.model.enums;

/**
 * Énumération représentant les différents états possibles d'une commande.
 * 
 * @author Restaurant App
 * @version 1.0
 */
public enum EtatCommande {
    /** Commande en attente de traitement */
    EN_ATTENTE("En attente"),
    
    /** Commande en cours de préparation */
    EN_PREPARATION("En préparation"),
    
    /** Commande prête à être servie ou livrée */
    PRETE("Prête"),
    
    /** Commande livrée au client */
    LIVREE("Livrée"),
    
    /** Commande annulée */
    ANNULEE("Annulée");
    
    private final String libelle;
    
    EtatCommande(String libelle) {
        this.libelle = libelle;
    }
    
    /**
     * Retourne le libellé lisible de l'état.
     * @return Le libellé de l'état
     */
    public String getLibelle() {
        return libelle;
    }
    
    @Override
    public String toString() {
        return libelle;
    }
}
