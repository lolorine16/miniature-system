package restaurant.app.view.dialogs;

import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import restaurant.app.controller.CommandeController;
import restaurant.app.model.entities.Commande;
import restaurant.app.model.entities.LigneCommande;
import restaurant.app.util.FormatUtil;

/**
 * Dialogue pour afficher les details d'une commande.
 * 
 * @author Restaurant App
 * @version 1.0
 */
public class CommandeDetailsDialog extends JDialog {
    
    private final CommandeController commandeController;
    private final int commandeId;
    private Commande commande;
    
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    /**
     * Constructeur.
     * @param parent La fenetre parente
     * @param commandeId L'ID de la commande
     */
    public CommandeDetailsDialog(Frame parent, int commandeId) {
        super(parent, "Details de la commande #" + commandeId, true);
        
        this.commandeController = new CommandeController();
        this.commandeId = commandeId;
        
        loadCommande();
        initComponents();
        
        setSize(600, 500);
        setLocationRelativeTo(parent);
        setResizable(false);
    }
    
    /**
     * Charge la commande.
     */
    private void loadCommande() {
        try {
            commande = commandeController.getCommandeById(commandeId);
        } catch (Exception e) {
            commande = null;
        }
    }
    
    /**
     * Initialise les composants.
     */
    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 15));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);
        
        if (commande == null) {
            mainPanel.add(new JLabel("Commande introuvable"), BorderLayout.CENTER);
            setContentPane(mainPanel);
            return;
        }
        
        // Header avec infos commande
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Table des lignes
        JPanel tablePanel = createTablePanel();
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        
        // Footer avec total
        JPanel footerPanel = createFooterPanel();
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
    }
    
    /**
     * Cree le panel d'en-tete.
     */
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 20, 10));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createTitledBorder("Informations"));
        
        // Date
        JLabel dateLabel = new JLabel("Date:");
        dateLabel.setFont(new Font("Montserrat", Font.BOLD, 13));
        JLabel dateValue = new JLabel(commande.getDateCommande() != null ? 
                commande.getDateCommande().format(DATE_FORMAT) : "-");
        dateValue.setFont(new Font("Montserrat", Font.PLAIN, 13));
        
        // Etat
        JLabel etatLabel = new JLabel("Etat:");
        etatLabel.setFont(new Font("Montserrat", Font.BOLD, 13));
        JLabel etatValue = new JLabel(commande.getEtat().getLibelle());
        etatValue.setFont(new Font("Montserrat", Font.BOLD, 13));
        etatValue.setForeground(getEtatColor(commande.getEtat()));
        
        panel.add(dateLabel);
        panel.add(dateValue);
        panel.add(etatLabel);
        panel.add(etatValue);
        
        return panel;
    }
    
    /**
     * Retourne la couleur selon l'etat.
     */
    private Color getEtatColor(restaurant.app.model.enums.EtatCommande etat) {
        switch (etat) {
            case EN_ATTENTE: return new Color(245, 158, 11);
            case EN_PREPARATION: return new Color(59, 130, 246);
            case PRETE: return new Color(34, 197, 94);
            case LIVREE: return new Color(107, 114, 128);
            case ANNULEE: return new Color(239, 68, 68);
            default: return Color.BLACK;
        }
    }
    
    /**
     * Cree le panel de la table.
     */
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createTitledBorder("Produits commandes"));
        
        String[] columns = {"Produit", "Prix unitaire", "Quantite", "Sous-total"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Ajouter les lignes
        List<LigneCommande> lignes = commande.getLignes();
        if (lignes != null) {
            for (LigneCommande ligne : lignes) {
                String produitNom = ligne.getProduit() != null ? ligne.getProduit().getNom() : "Produit inconnu";
                double sousTotal = ligne.getPrixUnitaire() * ligne.getQuantite();
                
                model.addRow(new Object[]{
                        produitNom,
                        FormatUtil.formatCurrency(ligne.getPrixUnitaire()),
                        ligne.getQuantite(),
                        FormatUtil.formatCurrency(sousTotal)
                });
            }
        }
        
        JTable table = new JTable(model);
        table.setFont(new Font("Montserrat", Font.PLAIN, 13));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Montserrat", Font.BOLD, 12));
        
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Cree le panel de pied.
     */
    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        // Total
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalPanel.setOpaque(false);
        
        JLabel totalLabel = new JLabel("Total: ");
        totalLabel.setFont(new Font("Montserrat", Font.BOLD, 18));
        
        JLabel totalValue = new JLabel(FormatUtil.formatCurrency(commande.getTotal()));
        totalValue.setFont(new Font("Montserrat", Font.BOLD, 24));
        totalValue.setForeground(new Color(34, 197, 94));
        
        totalPanel.add(totalLabel);
        totalPanel.add(totalValue);
        
        // Bouton fermer
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        
        JButton closeButton = new JButton("Fermer");
        closeButton.setFont(new Font("Montserrat", Font.PLAIN, 13));
        closeButton.setPreferredSize(new Dimension(100, 35));
        closeButton.addActionListener(e -> dispose());
        
        buttonPanel.add(closeButton);
        
        panel.add(totalPanel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
}
