package restaurant.app.controller;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import restaurant.app.dao.CommandeDAO;
import restaurant.app.dao.LigneCommandeDAO;
import restaurant.app.dao.ProduitDAO;
import restaurant.app.model.entities.Commande;
import restaurant.app.model.entities.LigneCommande;
import restaurant.app.model.entities.Produit;
import restaurant.app.model.enums.EtatCommande;

/**
 * Contrôleur pour la gestion des commandes.
 * Gère le cycle de vie complet d'une commande.
 * 
 * @author Restaurant App
 * @version 1.0
 */
public class CommandeController {
    
    private final CommandeDAO commandeDAO;
    private final LigneCommandeDAO ligneCommandeDAO;
    private final ProduitDAO produitDAO;
    
    /**
     * Constructeur par défaut.
     */
    public CommandeController() {
        this.commandeDAO = new CommandeDAO();
        this.ligneCommandeDAO = new LigneCommandeDAO();
        this.produitDAO = new ProduitDAO();
    }
    
    /**
     * Récupère toutes les commandes.
     * @return Liste des commandes
     * @throws Exception en cas d'erreur
     */
    public List<Commande> getAllCommandes() throws Exception {
        try {
            return commandeDAO.findAll();
        } catch (SQLException e) {
            throw new Exception("Erreur lors de la récupération des commandes: " + e.getMessage(), e);
        }
    }
    
    /**
     * Récupère les commandes d'un jour.
     * @param date La date
     * @return Liste des commandes
     * @throws Exception en cas d'erreur
     */
    public List<Commande> getCommandesDuJour(LocalDate date) throws Exception {
        try {
            return commandeDAO.findByDate(date);
        } catch (SQLException e) {
            throw new Exception("Erreur lors de la récupération des commandes: " + e.getMessage(), e);
        }
    }
    
    /**
     * Récupère les commandes du jour courant.
     * @return Liste des commandes
     * @throws Exception en cas d'erreur
     */
    public List<Commande> getCommandesDuJour() throws Exception {
        return getCommandesDuJour(LocalDate.now());
    }
    
    /**
     * Récupère les commandes par état.
     * @param etat L'état recherché
     * @return Liste des commandes
     * @throws Exception en cas d'erreur
     */
    public List<Commande> getCommandesByEtat(EtatCommande etat) throws Exception {
        try {
            return commandeDAO.findByEtat(etat);
        } catch (SQLException e) {
            throw new Exception("Erreur lors de la récupération des commandes: " + e.getMessage(), e);
        }
    }
    
    /**
     * Récupère les commandes en attente.
     * @return Liste des commandes en attente
     * @throws Exception en cas d'erreur
     */
    public List<Commande> getCommandesEnAttente() throws Exception {
        return getCommandesByEtat(EtatCommande.EN_ATTENTE);
    }
    
    /**
     * Récupère les commandes en préparation.
     * @return Liste des commandes en préparation
     * @throws Exception en cas d'erreur
     */
    public List<Commande> getCommandesEnPreparation() throws Exception {
        return getCommandesByEtat(EtatCommande.EN_PREPARATION);
    }
    
    /**
     * Récupère une commande par son ID avec ses lignes.
     * @param id L'identifiant
     * @return La commande ou null
     * @throws Exception en cas d'erreur
     */
    public Commande getCommandeById(int id) throws Exception {
        try {
            Commande commande = commandeDAO.findById(id);
            if (commande != null) {
                List<LigneCommande> lignes = ligneCommandeDAO.findByCommandeId(id);
                commande.getLignes().clear();
                commande.getLignes().addAll(lignes);
            }
            return commande;
        } catch (SQLException e) {
            throw new Exception("Erreur lors de la récupération de la commande: " + e.getMessage(), e);
        }
    }
    
    /**
     * Crée une nouvelle commande.
     * @return La commande créée
     * @throws Exception en cas d'erreur
     */
    public Commande creerCommande() throws Exception {
        try {
            Commande commande = new Commande();
            return commandeDAO.insert(commande);
        } catch (SQLException e) {
            throw new Exception("Erreur lors de la création de la commande: " + e.getMessage(), e);
        }
    }
    
    /**
     * Crée une nouvelle commande avec des informations client.
     * @param clientNom Le nom du client
     * @param clientTelephone Le téléphone du client
     * @param notes Les notes
     * @return La commande créée
     * @throws Exception en cas d'erreur
     */
    public Commande creerCommande(String clientNom, String clientTelephone, String notes) throws Exception {
        try {
            Commande commande = new Commande();
            commande.setClientNom(clientNom);
            commande.setClientTelephone(clientTelephone);
            commande.setNotes(notes);
            return commandeDAO.insert(commande);
        } catch (SQLException e) {
            throw new Exception("Erreur lors de la création de la commande: " + e.getMessage(), e);
        }
    }
    
    /**
     * Ajoute un produit à une commande.
     * @param commandeId L'identifiant de la commande
     * @param produitId L'identifiant du produit
     * @param quantite La quantité
     * @return La ligne de commande créée
     * @throws Exception en cas d'erreur ou de validation
     */
    public LigneCommande ajouterProduit(int commandeId, int produitId, int quantite) throws Exception {
        if (quantite <= 0) {
            throw new Exception("La quantité doit être positive");
        }
        
        try {
            // Vérifier la commande
            Commande commande = commandeDAO.findById(commandeId);
            if (commande == null) {
                throw new Exception("Commande introuvable");
            }
            
            if (commande.getEtat() != EtatCommande.EN_ATTENTE) {
                throw new Exception("Impossible de modifier une commande qui n'est pas en attente");
            }
            
            // Vérifier le produit et son stock
            Produit produit = produitDAO.findById(produitId);
            if (produit == null) {
                throw new Exception("Produit introuvable");
            }
            
            if (!produit.isActif()) {
                throw new Exception("Ce produit n'est plus disponible");
            }
            
            if (produit.getStockActuel() < quantite) {
                throw new Exception("Stock insuffisant. Stock disponible: " + produit.getStockActuel());
            }
            
            // Créer la ligne de commande
            LigneCommande ligne = new LigneCommande(produit, quantite);
            ligne.setCommande(commande);
            LigneCommande ligneSauvegardee = ligneCommandeDAO.insert(ligne, commandeId);
            
            // Mettre à jour le total de la commande
            updateTotalCommande(commandeId);
            
            return ligneSauvegardee;
            
        } catch (SQLException e) {
            throw new Exception("Erreur lors de l'ajout du produit: " + e.getMessage(), e);
        }
    }
    
    /**
     * Supprime une ligne de commande.
     * @param ligneId L'identifiant de la ligne
     * @throws Exception en cas d'erreur
     */
    public void supprimerLigne(int ligneId) throws Exception {
        try {
            LigneCommande ligne = ligneCommandeDAO.findById(ligneId);
            if (ligne == null) {
                throw new Exception("Ligne de commande introuvable");
            }
            
            Commande commande = ligne.getCommande();
            if (commande != null && commande.getEtat() != EtatCommande.EN_ATTENTE) {
                throw new Exception("Impossible de modifier une commande qui n'est pas en attente");
            }
            
            ligneCommandeDAO.delete(ligneId);
            
            if (commande != null) {
                updateTotalCommande(commande.getId());
            }
            
        } catch (SQLException e) {
            throw new Exception("Erreur lors de la suppression de la ligne: " + e.getMessage(), e);
        }
    }
    
    /**
     * Met à jour le total d'une commande.
     * @param commandeId L'identifiant de la commande
     * @throws SQLException en cas d'erreur
     */
    private void updateTotalCommande(int commandeId) throws SQLException {
        List<LigneCommande> lignes = ligneCommandeDAO.findByCommandeId(commandeId);
        double total = 0.0;
        for (LigneCommande ligne : lignes) {
            total += ligne.getMontantLigne();
        }
        
        Commande commande = commandeDAO.findById(commandeId);
        if (commande != null) {
            commande.setTotal(total);
            commandeDAO.update(commande);
        }
    }
    
    /**
     * Valide une commande (passe en préparation).
     * @param commandeId L'identifiant de la commande
     * @throws Exception en cas d'erreur
     */
    public void validerCommande(int commandeId) throws Exception {
        changerEtat(commandeId, EtatCommande.EN_PREPARATION, EtatCommande.EN_ATTENTE);
        
        // Décrémenter le stock des produits
        try {
            List<LigneCommande> lignes = ligneCommandeDAO.findByCommandeId(commandeId);
            for (LigneCommande ligne : lignes) {
                Produit produit = ligne.getProduit();
                if (produit != null) {
                    int nouveauStock = produit.getStockActuel() - ligne.getQuantite();
                    produitDAO.updateStock(produit.getId(), Math.max(0, nouveauStock));
                }
            }
        } catch (SQLException e) {
            throw new Exception("Erreur lors de la mise à jour du stock: " + e.getMessage(), e);
        }
    }
    
    /**
     * Marque une commande comme prête.
     * @param commandeId L'identifiant de la commande
     * @throws Exception en cas d'erreur
     */
    public void marquerPrete(int commandeId) throws Exception {
        changerEtat(commandeId, EtatCommande.PRETE, EtatCommande.EN_PREPARATION);
    }
    
    /**
     * Marque une commande comme livrée (terminée).
     * @param commandeId L'identifiant de la commande
     * @throws Exception en cas d'erreur
     */
    public void marquerLivree(int commandeId) throws Exception {
        changerEtat(commandeId, EtatCommande.LIVREE, EtatCommande.PRETE);
    }
    
    /**
     * Annule une commande.
     * @param commandeId L'identifiant de la commande
     * @throws Exception en cas d'erreur
     */
    public void annulerCommande(int commandeId) throws Exception {
        try {
            Commande commande = commandeDAO.findById(commandeId);
            if (commande == null) {
                throw new Exception("Commande introuvable");
            }
            
            if (commande.getEtat() == EtatCommande.LIVREE) {
                throw new Exception("Impossible d'annuler une commande livrée");
            }
            
            // Si la commande était en préparation ou prête, restaurer le stock
            if (commande.getEtat() == EtatCommande.EN_PREPARATION || 
                commande.getEtat() == EtatCommande.PRETE) {
                
                List<LigneCommande> lignes = ligneCommandeDAO.findByCommandeId(commandeId);
                for (LigneCommande ligne : lignes) {
                    Produit produit = ligne.getProduit();
                    if (produit != null) {
                        int nouveauStock = produit.getStockActuel() + ligne.getQuantite();
                        produitDAO.updateStock(produit.getId(), nouveauStock);
                    }
                }
            }
            
            commande.setEtat(EtatCommande.ANNULEE);
            commandeDAO.update(commande);
            
        } catch (SQLException e) {
            throw new Exception("Erreur lors de l'annulation de la commande: " + e.getMessage(), e);
        }
    }
    
    /**
     * Change l'état d'une commande.
     * @param commandeId L'identifiant de la commande
     * @param nouvelEtat Le nouvel état
     * @param etatAttendu L'état attendu actuel
     * @throws Exception en cas d'erreur ou si l'état actuel ne correspond pas
     */
    private void changerEtat(int commandeId, EtatCommande nouvelEtat, EtatCommande etatAttendu) throws Exception {
        try {
            Commande commande = commandeDAO.findById(commandeId);
            if (commande == null) {
                throw new Exception("Commande introuvable");
            }
            
            if (commande.getEtat() != etatAttendu) {
                throw new Exception("La commande doit être " + etatAttendu.getLibelle() + 
                        " pour passer à " + nouvelEtat.getLibelle());
            }
            
            commande.setEtat(nouvelEtat);
            commandeDAO.update(commande);
            
        } catch (SQLException e) {
            throw new Exception("Erreur lors du changement d'état: " + e.getMessage(), e);
        }
    }
    
    /**
     * Supprime une commande (seulement si en attente).
     * @param commandeId L'identifiant de la commande
     * @throws Exception en cas d'erreur
     */
    public void supprimerCommande(int commandeId) throws Exception {
        try {
            Commande commande = commandeDAO.findById(commandeId);
            if (commande == null) {
                throw new Exception("Commande introuvable");
            }
            
            if (commande.getEtat() != EtatCommande.EN_ATTENTE) {
                throw new Exception("Seules les commandes en attente peuvent être supprimées");
            }
            
            // Supprimer les lignes
            ligneCommandeDAO.deleteByCommandeId(commandeId);
            
            // Supprimer la commande
            commandeDAO.delete(commandeId);
            
        } catch (SQLException e) {
            throw new Exception("Erreur lors de la suppression de la commande: " + e.getMessage(), e);
        }
    }
    
    /**
     * Compte les commandes du jour.
     * @return Le nombre de commandes
     * @throws Exception en cas d'erreur
     */
    public int getCountDuJour() throws Exception {
        try {
            return commandeDAO.countByDate(LocalDate.now());
        } catch (SQLException e) {
            throw new Exception("Erreur lors du comptage des commandes: " + e.getMessage(), e);
        }
    }
    
    /**
     * Calcule le chiffre d'affaires du jour.
     * @return Le chiffre d'affaires
     * @throws Exception en cas d'erreur
     */
    public BigDecimal getChiffreAffairesDuJour() throws Exception {
        try {
            return commandeDAO.getTotalByDate(LocalDate.now());
        } catch (SQLException e) {
            throw new Exception("Erreur lors du calcul du CA: " + e.getMessage(), e);
        }
    }
    
    /**
     * Recherche des commandes par critères.
     * @param etat L'état (null pour tous)
     * @param debut Date de début (null pour ignorer)
     * @param fin Date de fin (null pour ignorer)
     * @return Liste des commandes
     * @throws Exception en cas d'erreur
     */
    public List<Commande> rechercher(EtatCommande etat, LocalDate debut, LocalDate fin) throws Exception {
        try {
            return commandeDAO.search(etat, debut, fin);
        } catch (SQLException e) {
            throw new Exception("Erreur lors de la recherche: " + e.getMessage(), e);
        }
    }
}
