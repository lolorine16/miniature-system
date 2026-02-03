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
import restaurant.app.view.dialogs.CommandeDetailsDialog;
import restaurant.app.view.dialogs.CommandeDialog;

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
    
    // Labels des compteurs
    private JLabel enAttenteCountLabel;
    private JLabel enPreparationCountLabel;
    private JLabel pretesCountLabel;
    private JLabel termineesCountLabel;
    
    private static final String[] COLUMNS = {"N", "Date", "Etat", "Total"};
    
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
        
        ModernButton newButton = new ModernButton("Nouvelle commande", ModernButton.ButtonType.PRIMARY);
        newButton.addActionListener(e -> createNewCommande());
        
        actionsPanel.add(filterCombo);
        actionsPanel.add(newButton);
        
        header.add(titlePanel, BorderLayout.WEST);
        header.add(actionsPanel, BorderLayout.EAST);
        
        return header;
    }
    
    /**
     * Cree le panel de resume.
     * @return Le panel
     */
    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 15, 0));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(0, 80));
        
        JPanel card1 = createStatusCardWithLabel("En attente", new Color(245, 158, 11));
        enAttenteCountLabel = (JLabel) card1.getClientProperty("countLabel");
        
        JPanel card2 = createStatusCardWithLabel("En preparation", new Color(59, 130, 246));
        enPreparationCountLabel = (JLabel) card2.getClientProperty("countLabel");
        
        JPanel card3 = createStatusCardWithLabel("Pretes", new Color(34, 197, 94));
        pretesCountLabel = (JLabel) card3.getClientProperty("countLabel");
        
        JPanel card4 = createStatusCardWithLabel("Terminees", new Color(107, 114, 128));
        termineesCountLabel = (JLabel) card4.getClientProperty("countLabel");
        
        panel.add(card1);
        panel.add(card2);
        panel.add(card3);
        panel.add(card4);
        
        return panel;
    }
    
    /**
     * Cree une carte de statut avec label stocke.
     */
    private JPanel createStatusCardWithLabel(String title, Color color) {
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
        
        JLabel countLabel = new JLabel("0");
        countLabel.setFont(new Font("Montserrat", Font.BOLD, 24));
        countLabel.setForeground(color);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(countLabel, BorderLayout.CENTER);
        
        // Stocker le label
        card.putClientProperty("countLabel", countLabel);
        
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
        
        // Mode auto-resize pour remplir toute la largeur
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setFillsViewportHeight(true);
        
        // Hauteur de ligne plus grande
        table.setRowHeight(40);
        
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
        scrollPane.setPreferredSize(new Dimension(700, 400));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Boutons d'action
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottomPanel.setOpaque(false);
        
        ModernButton validerButton = new ModernButton("Valider", ModernButton.ButtonType.SUCCESS);
        validerButton.addActionListener(e -> validerCommande());
        
        ModernButton preteButton = new ModernButton("Prête", ModernButton.ButtonType.INFO);
        preteButton.addActionListener(e -> marquerPrete());
        
        ModernButton servieButton = new ModernButton("Servie", ModernButton.ButtonType.SECONDARY);
        servieButton.addActionListener(e -> marquerServie());
        
        ModernButton annulerButton = new ModernButton("Annuler", ModernButton.ButtonType.DANGER);
        annulerButton.addActionListener(e -> annulerCommande());
        
        bottomPanel.add(validerButton);
        bottomPanel.add(preteButton);
        bottomPanel.add(servieButton);
        bottomPanel.add(annulerButton);
        
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Charge les donnees.
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
                    updateCounters(commandes);
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
     * Met a jour la table.
     * @param commandes Les commandes
     */
    private void updateTable(List<Commande> commandes) {
        tableModel.setRowCount(0);
        
        for (Commande c : commandes) {
            tableModel.addRow(new Object[]{
                    c.getId(),
                    c.getDateCommande() != null ? c.getDateCommande().format(dateFormatter) : "-",
                    c.getEtat().getLibelle(),
                    FormatUtil.formatCurrency(c.getTotal())
            });
        }
    }
    
    /**
     * Met a jour les compteurs.
     * @param commandes Les commandes
     */
    private void updateCounters(List<Commande> commandes) {
        int enAttente = 0;
        int enPreparation = 0;
        int pretes = 0;
        int terminees = 0;
        
        for (Commande c : commandes) {
            switch (c.getEtat()) {
                case EN_ATTENTE:
                    enAttente++;
                    break;
                case EN_PREPARATION:
                    enPreparation++;
                    break;
                case PRETE:
                    pretes++;
                    break;
                case LIVREE:
                    terminees++;
                    break;
                default:
                    break;
            }
        }
        
        if (enAttenteCountLabel != null) {
            enAttenteCountLabel.setText(String.valueOf(enAttente));
        }
        if (enPreparationCountLabel != null) {
            enPreparationCountLabel.setText(String.valueOf(enPreparation));
        }
        if (pretesCountLabel != null) {
            pretesCountLabel.setText(String.valueOf(pretes));
        }
        if (termineesCountLabel != null) {
            termineesCountLabel.setText(String.valueOf(terminees));
        }
    }
    
    /**
     * Cree une nouvelle commande.
     */
    private void createNewCommande() {
        CommandeDialog dialog = new CommandeDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                null
        );
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            JOptionPane.showMessageDialog(this,
                    "Commande #" + dialog.getCommande().getId() + " creee avec succes!",
                    "Succes", JOptionPane.INFORMATION_MESSAGE);
            refresh();
        }
    }
    
    /**
     * Affiche les details d'une commande.
     */
    private void showCommandeDetails(int id) {
        CommandeDetailsDialog dialog = new CommandeDetailsDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                id
        );
        dialog.setVisible(true);
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
