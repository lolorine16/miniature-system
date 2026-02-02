package restaurant.app.model.enums;

/**
 * Énumération représentant les types de mouvements de stock.
 * 
 * @author Restaurant App
 * @version 1.0
 */
public enum TypeMouvement {
    /** Entrée de stock (approvisionnement) */
    ENTREE("Entrée"),
    
    /** Sortie de stock (vente, perte, etc.) */
    SORTIE("Sortie");
    
    private final String libelle;
    
    TypeMouvement(String libelle) {
        this.libelle = libelle;
    }
    
    /**
     * Retourne le libellé lisible du type de mouvement.
     * @return Le libellé du type
     */
    public String getLibelle() {
        return libelle;
    }
    
    @Override
    public String toString() {
        return libelle;
    }
}
