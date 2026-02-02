package restaurant.app.view.panels;

import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import restaurant.app.controller.CommandeController;
import restaurant.app.model.entities.Commande;
import restaurant.app.model.enums.EtatCommande;
import restaurant.app.util.FormatUtil;
import restaurant.app.view.MainFrame;
import restaurant.app.view.components.ModernButton;
import restaurant.app.view.components.ModernTable;

/**
 * Panel de gestion des commandes.
 * 
 * @author Restaurant App
 * @version 1.0
 */
public class CommandePanel extends JPanel implements MainFrame.Refreshable {
    
    private final CommandeController commandeController;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    private JComboBox<String> filterCombo;
    private ModernTable table;
    private DefaultTableModel tableModel;
    
    private static final String[] COLUMNS = {"N°", "Date", "État", "Total", "Actions"};
    
    /**
     * Constructeur.
     */
    public CommandePanel() {
        this.commandeController = new CommandeController();
        
        setLayout(new BorderLayout());
        setBackground(new Color(243, 244, 246));
        setBorder(new EmptyBorder(30, 30, 30, 30));
        
        initComponents();
        loadData();
    }
    
    /**
     * Initialise les composants.
     */
    private void initComponents() {
        // Header
        JPanel headerPanel = createHeader();
        add(headerPanel, BorderLayout.NORTH);
        
        // Contenu
        JPanel contentPanel = new JPanel(new BorderLayout(0, 20));
        contentPanel.setOpaque(false);
        
        // Résumé des états
        JPanel summaryPanel = createSummaryPanel();
        contentPanel.add(summaryPanel, BorderLayout.NORTH);
        
        // Table des commandes
        JPanel tablePanel = createTablePanel();
        contentPanel.add(tablePanel, BorderLayout.CENTER);
        
        add(contentPanel, BorderLayout.CENTER);
    }
    
    /**
     * Crée le header.
     * @return Le panel
     */
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        // Titre
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Commandes");
        titleLabel.setFont(new Font("Montserrat", Font.BOLD, 28));
        titleLabel.setForeground(new Color(31, 41, 55));
        
        JLabel subtitleLabel = new JLabel("Gestion des commandes du jour");
        subtitleLabel.setFont(new Font("Montserrat", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(107, 114, 128));
        
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        titlePanel.add(subtitleLabel);
        
        // Actions
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        actionsPanel.setOpaque(false);
        
        // Filtre par état
        filterCombo = new JComboBox<>(new String[]{
                "Toutes les commandes",
                "En attente",
                "En préparation",
                "Prêtes",
                "Terminées",
                "Annulées"
        });
        filterCombo.setFont(new Font("Montserrat", Font.PLAIN, 13));
        filterCombo.setPreferredSize(new Dimension(180, 45));
        filterCombo.addActionListener(e -> filterCommandes());
        
        ModernButton newButton = new ModernButton("➕ Nouvelle commande", ModernButton.ButtonType.PRIMARY);
        newButton.addActionListener(e -> createNewCommande());
        
        actionsPanel.add(filterCombo);
        actionsPanel.add(newButton);
        
        header.add(titlePanel, BorderLayout.WEST);
        header.add(actionsPanel, BorderLayout.EAST);
        
        return header;
    }
    
    /**
     * Crée le panel de résumé.
     * @return Le panel
     */
    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 15, 0));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(0, 80));
        
        panel.add(createStatusCard("⏳ En attente", "0", new Color(245, 158, 11)));
        panel.add(createStatusCard("👨‍🍳 En préparation", "0", new Color(59, 130, 246)));
        panel.add(createStatusCard("✅ Prêtes", "0", new Color(34, 197, 94)));
        panel.add(createStatusCard("🏁 Terminées", "0", new Color(107, 114, 128)));
        
        return panel;
    }
    
    /**
     * Crée une carte de statut.
     */
    private JPanel createStatusCard(String title, String count, Color color) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(color);
                g2.fillRect(0, 0, 5, getHeight());
                g2.dispose();
            }
        };
        card.setLayout(new BorderLayout());
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Montserrat", Font.PLAIN, 13));
        titleLabel.setForeground(new Color(107, 114, 128));
        
        JLabel countLabel = new JLabel(count);
        countLabel.setFont(new Font("Montserrat", Font.BOLD, 24));
        countLabel.setForeground(color);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(countLabel, BorderLayout.CENTER);
        
        return card;
    }
    
    /**
     * Crée le panel de la table.
     * @return Le panel
     */
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Modèle de table
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Table
        table = new ModernTable(tableModel);
        table.setColumnWidth(0, 80);
        table.setColumnWidth(1, 150);
        table.setColumnWidth(2, 150);
        table.setColumnWidth(3, 120);
        
        // Double-clic pour voir les détails
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row >= 0) {
                        int id = (int) tableModel.getValueAt(row, 0);
                        showCommandeDetails(id);
                    }
                }
            }
        });
        
        JScrollPane scrollPane = table.wrapInScrollPane();
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Boutons d'action
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottomPanel.setOpaque(false);
        
        ModernButton validerButton = new ModernButton("✅ Valider", ModernButton.ButtonType.SUCCESS);
        validerButton.addActionListener(e -> validerCommande());
        
        ModernButton preteButton = new ModernButton("🍽️ Prête", ModernButton.ButtonType.INFO);
        preteButton.addActionListener(e -> marquerPrete());
        
        ModernButton servieButton = new ModernButton("🏁 Servie", ModernButton.ButtonType.SECONDARY);
        servieButton.addActionListener(e -> marquerServie());
        
        ModernButton annulerButton = new ModernButton("❌ Annuler", ModernButton.ButtonType.DANGER);
        annulerButton.addActionListener(e -> annulerCommande());
        
        bottomPanel.add(validerButton);
        bottomPanel.add(preteButton);
        bottomPanel.add(servieButton);
        bottomPanel.add(annulerButton);
        
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Charge les données.
     */
    private void loadData() {
        SwingWorker<List<Commande>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Commande> doInBackground() throws Exception {
                return commandeController.getCommandesDuJour();
            }
            
            @Override
            protected void done() {
                try {
                    List<Commande> commandes = get();
                    updateTable(commandes);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(CommandePanel.this,
                            "Erreur: " + e.getMessage(),
                            "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
    
    /**
     * Filtre les commandes.
     */
    private void filterCommandes() {
        int selectedIndex = filterCombo.getSelectedIndex();
        
        SwingWorker<List<Commande>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Commande> doInBackground() throws Exception {
                switch (selectedIndex) {
                    case 1: return commandeController.getCommandesByEtat(EtatCommande.EN_ATTENTE);
                    case 2: return commandeController.getCommandesByEtat(EtatCommande.EN_PREPARATION);
                    case 3: return commandeController.getCommandesByEtat(EtatCommande.PRETE);
                    case 4: return commandeController.getCommandesByEtat(EtatCommande.LIVREE);
                    case 5: return commandeController.getCommandesByEtat(EtatCommande.ANNULEE);
                    default: return commandeController.getCommandesDuJour();
                }
            }
            
            @Override
            protected void done() {
                try {
                    List<Commande> commandes = get();
                    updateTable(commandes);
                } catch (Exception e) {
                    // Ignorer
                }
            }
        };
        worker.execute();
    }
    
    /**
     * Met à jour la table.
     * @param commandes Les commandes
     */
    private void updateTable(List<Commande> commandes) {
        tableModel.setRowCount(0);
        
        for (Commande c : commandes) {
            String etatIcon = getEtatIcon(c.getEtat());
            
            tableModel.addRow(new Object[]{
                    c.getId(),
                    c.getDateCommande() != null ? c.getDateCommande().format(dateFormatter) : "-",
                    etatIcon + " " + c.getEtat().getLibelle(),
                    FormatUtil.formatCurrency(c.getTotal()),
                    "Actions"
            });
        }
    }
    
    /**
     * Retourne l'icône d'un état.
     */
    private String getEtatIcon(EtatCommande etat) {
        switch (etat) {
            case EN_ATTENTE: return "⏳";
            case EN_PREPARATION: return "👨‍🍳";
            case PRETE: return "✅";
            case LIVREE: return "🏁";
            case ANNULEE: return "❌";
            default: return "❓";
        }
    }
    
    /**
     * Crée une nouvelle commande.
     */
    private void createNewCommande() {
        try {
            Commande commande = commandeController.creerCommande();
            JOptionPane.showMessageDialog(this,
                    "Commande #" + commande.getId() + " créée",
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
            refresh();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Affiche les détails d'une commande.
     */
    private void showCommandeDetails(int id) {
        // À implémenter: afficher un dialogue avec les détails
        JOptionPane.showMessageDialog(this,
                "Détails de la commande #" + id,
                "Détails", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Valide la commande sélectionnée.
     */
    private void validerCommande() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Sélectionnez une commande");
            return;
        }
        
        int id = (int) tableModel.getValueAt(row, 0);
        try {
            commandeController.validerCommande(id);
            refresh();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage());
        }
    }
    
    /**
     * Marque la commande comme prête.
     */
    private void marquerPrete() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Sélectionnez une commande");
            return;
        }
        
        int id = (int) tableModel.getValueAt(row, 0);
        try {
            commandeController.marquerPrete(id);
            refresh();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage());
        }
    }
    
    /**
     * Marque la commande comme servie.
     */
    private void marquerServie() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Sélectionnez une commande");
            return;
        }
        
        int id = (int) tableModel.getValueAt(row, 0);
        try {
            commandeController.marquerLivree(id);
            refresh();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage());
        }
    }
    
    /**
     * Annule la commande sélectionnée.
     */
    private void annulerCommande() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Sélectionnez une commande");
            return;
        }
        
        int id = (int) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Annuler la commande #" + id + " ?",
                "Confirmation", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                commandeController.annulerCommande(id);
                refresh();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage());
            }
        }
    }
    
    @Override
    public void refresh() {
        loadData();
    }
}
