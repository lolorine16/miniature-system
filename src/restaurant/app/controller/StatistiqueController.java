package restaurant.app.controller;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import restaurant.app.dao.CommandeDAO;
import restaurant.app.dao.ProduitDAO;
import restaurant.app.model.entities.Produit;
import restaurant.app.model.enums.EtatCommande;

/**
 * Contrôleur pour les statistiques et rapports.
 * 
 * @author Restaurant App
 * @version 1.0
 */
public class StatistiqueController {
    
    private final CommandeDAO commandeDAO;
    private final ProduitDAO produitDAO;
    
    /**
     * Constructeur par défaut.
     */
    public StatistiqueController() {
        this.commandeDAO = new CommandeDAO();
        this.produitDAO = new ProduitDAO();
    }
    
    // ==================== Statistiques du jour ====================
    
    /**
     * Récupère le nombre de commandes du jour.
     * @return Le nombre de commandes
     * @throws Exception en cas d'erreur
     */
    public int getNombreCommandesDuJour() throws Exception {
        try {
            return commandeDAO.countByDate(LocalDate.now());
        } catch (SQLException e) {
            throw new Exception("Erreur: " + e.getMessage(), e);
        }
    }
    
    /**
     * Récupère le chiffre d'affaires du jour.
     * @return Le CA du jour
     * @throws Exception en cas d'erreur
     */
    public BigDecimal getChiffreAffairesDuJour() throws Exception {
        try {
            BigDecimal total = commandeDAO.getTotalByDate(LocalDate.now());
            return total != null ? total : BigDecimal.ZERO;
        } catch (SQLException e) {
            throw new Exception("Erreur: " + e.getMessage(), e);
        }
    }
    
    /**
     * Récupère le nombre de commandes en attente.
     * @return Le nombre de commandes en attente
     * @throws Exception en cas d'erreur
     */
    public int getNombreCommandesEnAttente() throws Exception {
        try {
            return commandeDAO.countByEtat(EtatCommande.EN_ATTENTE);
        } catch (SQLException e) {
            throw new Exception("Erreur: " + e.getMessage(), e);
        }
    }
    
    // ==================== Statistiques sur une période ====================
    
    /**
     * Récupère le chiffre d'affaires sur une période.
     * @param dateDebut Date de début
     * @param dateFin Date de fin
     * @return Le CA de la période
     * @throws Exception en cas d'erreur
     */
    public BigDecimal getChiffreAffairesPeriode(LocalDate dateDebut, LocalDate dateFin) throws Exception {
        try {
            BigDecimal total = commandeDAO.getTotalByPeriod(dateDebut, dateFin);
            return total != null ? total : BigDecimal.ZERO;
        } catch (SQLException e) {
            throw new Exception("Erreur: " + e.getMessage(), e);
        }
    }
    
    /**
     * Récupère le nombre de commandes sur une période.
     * @param dateDebut Date de début
     * @param dateFin Date de fin
     * @return Le nombre de commandes
     * @throws Exception en cas d'erreur
     */
    public int getNombreCommandesPeriode(LocalDate dateDebut, LocalDate dateFin) throws Exception {
        try {
            return commandeDAO.countByPeriod(dateDebut, dateFin);
        } catch (SQLException e) {
            throw new Exception("Erreur: " + e.getMessage(), e);
        }
    }
    
    /**
     * Calcule le panier moyen sur une période.
     * @param dateDebut Date de début
     * @param dateFin Date de fin
     * @return Le panier moyen
     * @throws Exception en cas d'erreur
     */
    public BigDecimal getPanierMoyenPeriode(LocalDate dateDebut, LocalDate dateFin) throws Exception {
        try {
            BigDecimal total = commandeDAO.getTotalByPeriod(dateDebut, dateFin);
            int count = commandeDAO.countByPeriod(dateDebut, dateFin);
            
            if (count == 0 || total == null) {
                return BigDecimal.ZERO;
            }
            
            return total.divide(BigDecimal.valueOf(count), 2, java.math.RoundingMode.HALF_UP);
        } catch (SQLException e) {
            throw new Exception("Erreur: " + e.getMessage(), e);
        }
    }
    
    // ==================== Statistiques par jour ====================
    
    /**
     * Récupère le CA par jour sur une période.
     * @param dateDebut Date de début
     * @param dateFin Date de fin
     * @return Map date -> CA
     * @throws Exception en cas d'erreur
     */
    public Map<LocalDate, BigDecimal> getChiffreAffairesParJour(LocalDate dateDebut, LocalDate dateFin) throws Exception {
        try {
            Map<LocalDate, BigDecimal> result = new LinkedHashMap<>();
            
            LocalDate date = dateDebut;
            while (!date.isAfter(dateFin)) {
                BigDecimal total = commandeDAO.getTotalByDate(date);
                result.put(date, total != null ? total : BigDecimal.ZERO);
                date = date.plusDays(1);
            }
            
            return result;
        } catch (SQLException e) {
            throw new Exception("Erreur: " + e.getMessage(), e);
        }
    }
    
    /**
     * Récupère le nombre de commandes par jour sur une période.
     * @param dateDebut Date de début
     * @param dateFin Date de fin
     * @return Map date -> nombre
     * @throws Exception en cas d'erreur
     */
    public Map<LocalDate, Integer> getNombreCommandesParJour(LocalDate dateDebut, LocalDate dateFin) throws Exception {
        try {
            Map<LocalDate, Integer> result = new LinkedHashMap<>();
            
            LocalDate date = dateDebut;
            while (!date.isAfter(dateFin)) {
                int count = commandeDAO.countByDate(date);
                result.put(date, count);
                date = date.plusDays(1);
            }
            
            return result;
        } catch (SQLException e) {
            throw new Exception("Erreur: " + e.getMessage(), e);
        }
    }
    
    // ==================== Statistiques des produits ====================
    
    /**
     * Récupère les produits les plus vendus.
     * @param limite Le nombre maximum de résultats
     * @return Liste des produits les plus vendus
     * @throws Exception en cas d'erreur
     */
    public List<Produit> getProduitsLesPlusVendus(int limite) throws Exception {
        try {
            return produitDAO.findBestSellers(limite);
        } catch (SQLException e) {
            throw new Exception("Erreur: " + e.getMessage(), e);
        }
    }
    
    /**
     * Récupère les produits les plus vendus avec leurs quantités.
     * @param limite Le nombre maximum de résultats
     * @return Map nom produit -> quantité vendue
     * @throws Exception en cas d'erreur
     */
    public Map<String, Integer> getVentesParProduit(int limite) throws Exception {
        try {
            return produitDAO.findBestSellersWithQuantity(limite);
        } catch (SQLException e) {
            throw new Exception("Erreur: " + e.getMessage(), e);
        }
    }
    
    /**
     * Récupère le nombre de produits en rupture de stock.
     * @return Le nombre de produits
     * @throws Exception en cas d'erreur
     */
    public int getNombreProduitsEnRupture() throws Exception {
        try {
            return produitDAO.countOutOfStock();
        } catch (SQLException e) {
            throw new Exception("Erreur: " + e.getMessage(), e);
        }
    }
    
    /**
     * Récupère le nombre de produits avec stock faible.
     * @param seuil Le seuil d'alerte
     * @return Le nombre de produits
     * @throws Exception en cas d'erreur
     */
    public int getNombreProduitsStockFaible(int seuil) throws Exception {
        try {
            return produitDAO.countLowStock(seuil);
        } catch (SQLException e) {
            throw new Exception("Erreur: " + e.getMessage(), e);
        }
    }
    
    // ==================== Statistiques par état de commande ====================
    
    /**
     * Récupère la répartition des commandes par état.
     * @return Map état -> nombre
     * @throws Exception en cas d'erreur
     */
    public Map<EtatCommande, Integer> getRepartitionParEtat() throws Exception {
        try {
            Map<EtatCommande, Integer> result = new LinkedHashMap<>();
            
            for (EtatCommande etat : EtatCommande.values()) {
                result.put(etat, commandeDAO.countByEtat(etat));
            }
            
            return result;
        } catch (SQLException e) {
            throw new Exception("Erreur: " + e.getMessage(), e);
        }
    }
    
    // ==================== Statistiques globales ====================
    
    /**
     * Récupère les statistiques globales du tableau de bord.
     * @return Map contenant les statistiques
     * @throws Exception en cas d'erreur
     */
    public Map<String, Object> getStatistiquesDashboard() throws Exception {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // Commandes du jour
            stats.put("commandesDuJour", commandeDAO.countByDate(LocalDate.now()));
            
            // CA du jour
            BigDecimal caDuJour = commandeDAO.getTotalByDate(LocalDate.now());
            stats.put("chiffreAffairesDuJour", caDuJour != null ? caDuJour : BigDecimal.ZERO);
            
            // Commandes en attente
            stats.put("commandesEnAttente", commandeDAO.countByEtat(EtatCommande.EN_ATTENTE));
            
            // Commandes en préparation
            stats.put("commandesEnPreparation", commandeDAO.countByEtat(EtatCommande.EN_PREPARATION));
            
            // Produits en rupture
            stats.put("produitsEnRupture", produitDAO.countOutOfStock());
            
            // Nombre total de produits
            stats.put("totalProduits", produitDAO.count());
            
            // CA du mois
            LocalDate debutMois = LocalDate.now().withDayOfMonth(1);
            BigDecimal caMois = commandeDAO.getTotalByPeriod(debutMois, LocalDate.now());
            stats.put("chiffreAffairesMois", caMois != null ? caMois : BigDecimal.ZERO);
            
            return stats;
            
        } catch (SQLException e) {
            throw new Exception("Erreur lors de la récupération des statistiques: " + e.getMessage(), e);
        }
    }
    
    // ==================== Rapports ====================
    
    /**
     * Génère un rapport de ventes pour une période.
     * @param dateDebut Date de début
     * @param dateFin Date de fin
     * @return Map contenant les données du rapport
     * @throws Exception en cas d'erreur
     */
    public Map<String, Object> genererRapportVentes(LocalDate dateDebut, LocalDate dateFin) throws Exception {
        Map<String, Object> rapport = new LinkedHashMap<>();
        
        rapport.put("periode", dateDebut + " - " + dateFin);
        rapport.put("dateGeneration", LocalDate.now());
        rapport.put("chiffreAffairesTotal", getChiffreAffairesPeriode(dateDebut, dateFin));
        rapport.put("nombreCommandes", getNombreCommandesPeriode(dateDebut, dateFin));
        rapport.put("panierMoyen", getPanierMoyenPeriode(dateDebut, dateFin));
        rapport.put("caParJour", getChiffreAffairesParJour(dateDebut, dateFin));
        rapport.put("produitsLesPlusVendus", getProduitsLesPlusVendus(10));
        
        return rapport;
    }
}
