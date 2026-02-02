package restaurant.app.model.enums;

/**
 * Énumération représentant les rôles des utilisateurs du système.
 * 
 * @author Restaurant App
 * @version 1.0
 */
public enum RoleUtilisateur {
    /** Administrateur avec tous les droits */
    ADMIN("Administrateur"),
    
    /** Employé avec droits limités */
    EMPLOYE("Employé");
    
    private final String libelle;
    
    RoleUtilisateur(String libelle) {
        this.libelle = libelle;
    }
    
    /**
     * Retourne le libellé lisible du rôle.
     * @return Le libellé du rôle
     */
    public String getLibelle() {
        return libelle;
    }
    
    @Override
    public String toString() {
        return libelle;
    }
}
