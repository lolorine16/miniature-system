package restaurant.app.view.panels;

import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import restaurant.app.controller.ProduitController;
import restaurant.app.controller.StockController;
import restaurant.app.model.entities.MouvementStock;
import restaurant.app.model.entities.Produit;
import restaurant.app.model.enums.TypeMouvement;
import restaurant.app.view.MainFrame;
import restaurant.app.view.components.ModernButton;
import restaurant.app.view.components.ModernTable;
import restaurant.app.view.dialogs.MouvementStockDialog;

/**
 * Panel de gestion du stock.
 * 
 * @author Restaurant App
 * @version 1.0
 */
public class StockPanel extends JPanel implements MainFrame.Refreshable {
    
    private final StockController stockController;
    private final ProduitController produitController;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    private JComboBox<String> filterCombo;
    private ModernTable table;
    private DefaultTableModel tableModel;
    
    private List<Produit> produits;
    
    // Labels pour les alertes
    private JLabel stockFaibleValueLabel;
    private JLabel ruptureValueLabel;
    private JLabel valeurStockValueLabel;
    
    private static final String[] COLUMNS = {"ID", "Date", "Produit", "Type", "Quantite", "Stock avant", "Stock apres", "Motif"};
    
    /**
     * Constructeur.
     */
    public StockPanel() {
        this.stockController = new StockController();
        this.produitController = new ProduitController();
        
        setLayout(new BorderLayout());
        setBackground(new Color(243, 244, 246));
        setBorder(new EmptyBorder(30, 30, 30, 30));
        
        initComponents();
        loadProduits();
        loadData();
    }
    
    /**
     * Initialise les composants.
     */
    private void initComponents() {
        // Header
        JPanel headerPanel = createHeader();
        add(headerPanel, BorderLayout.NORTH);
        
        // Contenu principal
        JPanel contentPanel = new JPanel(new BorderLayout(0, 20));
        contentPanel.setOpaque(false);
        
        // Alertes stock
        JPanel alertsPanel = createAlertsPanel();
        contentPanel.add(alertsPanel, BorderLayout.NORTH);
        
        // Table des mouvements
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
        
        JLabel titleLabel = new JLabel("Gestion du Stock");
        titleLabel.setFont(new Font("Montserrat", Font.BOLD, 28));
        titleLabel.setForeground(new Color(31, 41, 55));
        
        JLabel subtitleLabel = new JLabel("Mouvements d'entrée et de sortie");
        subtitleLabel.setFont(new Font("Montserrat", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(107, 114, 128));
        
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        titlePanel.add(subtitleLabel);
        
        // Actions
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        actionsPanel.setOpaque(false);
        
        // Filtre
        filterCombo = new JComboBox<>(new String[]{"Tous les mouvements", "Entrées", "Sorties"});
        filterCombo.setFont(new Font("Montserrat", Font.PLAIN, 13));
        filterCombo.setPreferredSize(new Dimension(180, 45));
        filterCombo.addActionListener(e -> filterMouvements());
        
        ModernButton entreeButton = new ModernButton("Entree", ModernButton.ButtonType.SUCCESS);
        entreeButton.addActionListener(e -> showMouvementDialog(TypeMouvement.ENTREE));
        
        ModernButton sortieButton = new ModernButton("Sortie", ModernButton.ButtonType.DANGER);
        sortieButton.addActionListener(e -> showMouvementDialog(TypeMouvement.SORTIE));
        
        actionsPanel.add(filterCombo);
        actionsPanel.add(entreeButton);
        actionsPanel.add(sortieButton);
        
        header.add(titlePanel, BorderLayout.WEST);
        header.add(actionsPanel, BorderLayout.EAST);
        
        return header;
    }
    
    /**
     * Cree le panel des alertes.
     * @return Le panel
     */
    private JPanel createAlertsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 20, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(254, 243, 199));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setPreferredSize(new Dimension(0, 100));
        
        // Creer les cartes avec leurs labels
        JPanel card1 = createAlertCardWithLabel("Stock faible", new Color(245, 158, 11));
        stockFaibleValueLabel = (JLabel) card1.getClientProperty("valueLabel");
        
        JPanel card2 = createAlertCardWithLabel("En rupture", new Color(239, 68, 68));
        ruptureValueLabel = (JLabel) card2.getClientProperty("valueLabel");
        
        JPanel card3 = createAlertCardWithLabel("Valeur stock", new Color(34, 197, 94));
        valeurStockValueLabel = (JLabel) card3.getClientProperty("valueLabel");
        
        panel.add(card1);
        panel.add(card2);
        panel.add(card3);
        
        return panel;
    }
    
    /**
     * Cree une carte d'alerte avec label stocke.
     */
    private JPanel createAlertCardWithLabel(String title, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Montserrat", Font.BOLD, 14));
        titleLabel.setForeground(color.darker());
        
        JLabel valueLabel = new JLabel("0");
        valueLabel.setFont(new Font("Montserrat", Font.BOLD, 20));
        valueLabel.setForeground(new Color(31, 41, 55));
        
        card.add(titleLabel);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(valueLabel);
        
        // Stocker le label pour mise a jour ulterieure
        card.putClientProperty("valueLabel", valueLabel);
        
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
        
        // Titre
        JLabel titleLabel = new JLabel("Historique des mouvements");
        titleLabel.setFont(new Font("Montserrat", Font.BOLD, 16));
        titleLabel.setForeground(new Color(31, 41, 55));
        titleLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Modèle de table
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Table
        table = new ModernTable(tableModel);
        table.setColumnWidth(0, 50);
        table.setColumnWidth(1, 140);
        table.setColumnWidth(3, 80);
        table.setColumnWidth(4, 80);
        table.setColumnWidth(5, 100);
        table.setColumnWidth(6, 100);
        
        JScrollPane scrollPane = table.wrapInScrollPane();
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Charge les produits.
     */
    private void loadProduits() {
        try {
            produits = produitController.getAllProduits();
            updateAlerts();
        } catch (Exception e) {
            // Ignorer
        }
    }
    
    /**
     * Met a jour les alertes de stock.
     */
    private void updateAlerts() {
        if (produits == null) return;
        
        int stockFaible = 0;
        int rupture = 0;
        double valeurTotale = 0;
        
        for (Produit p : produits) {
            if (p.getStockActuel() <= 0) {
                rupture++;
            } else if (p.getStockActuel() <= p.getSeuilAlerte()) {
                stockFaible++;
            }
            valeurTotale += p.getStockActuel() * p.getPrixVente();
        }
        
        if (stockFaibleValueLabel != null) {
            stockFaibleValueLabel.setText(String.valueOf(stockFaible));
        }
        if (ruptureValueLabel != null) {
            ruptureValueLabel.setText(String.valueOf(rupture));
        }
        if (valeurStockValueLabel != null) {
            valeurStockValueLabel.setText(restaurant.app.util.FormatUtil.formatCurrency(valeurTotale));
        }
    }
    
    /**
     * Charge les données.
     */
    private void loadData() {
        SwingWorker<List<MouvementStock>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<MouvementStock> doInBackground() throws Exception {
                return stockController.getAllMouvements();
            }
            
            @Override
            protected void done() {
                try {
                    List<MouvementStock> mouvements = get();
                    updateTable(mouvements);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(StockPanel.this,
                            "Erreur lors du chargement: " + e.getMessage(),
                            "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
    
    /**
     * Filtre les mouvements.
     */
    private void filterMouvements() {
        int selectedIndex = filterCombo.getSelectedIndex();
        
        SwingWorker<List<MouvementStock>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<MouvementStock> doInBackground() throws Exception {
                switch (selectedIndex) {
                    case 1: return stockController.getMouvementsByType(TypeMouvement.ENTREE);
                    case 2: return stockController.getMouvementsByType(TypeMouvement.SORTIE);
                    default: return stockController.getAllMouvements();
                }
            }
            
            @Override
            protected void done() {
                try {
                    List<MouvementStock> mouvements = get();
                    updateTable(mouvements);
                } catch (Exception e) {
                    // Ignorer
                }
            }
        };
        worker.execute();
    }
    
    /**
     * Met à jour la table.
     * @param mouvements Les mouvements
     */
    private void updateTable(List<MouvementStock> mouvements) {
        tableModel.setRowCount(0);
        
        for (MouvementStock m : mouvements) {
            String produitNom = "-";
            if (m.getProduit() != null) {
                produitNom = m.getProduit().getNom();
            } else if (produits != null) {
                // Fallback si produit non chargé
                for (Produit p : produits) {
                    if (p.getId() == m.getId()) {
                        produitNom = p.getNom();
                        break;
                    }
                }
            }
            
            String typeText = m.getType().getLibelle();
            
            tableModel.addRow(new Object[]{
                    m.getId(),
                    m.getDateMouvement() != null ? m.getDateMouvement().format(dateFormatter) : "-",
                    produitNom,
                    typeText,
                    m.getQuantite(),
                    "-",
                    "-",
                    m.getMotif() != null ? m.getMotif() : "-"
            });
        }
    }
    
    /**
     * Affiche le dialogue de mouvement.
     * @param type Le type de mouvement
     */
    private void showMouvementDialog(TypeMouvement type) {
        MouvementStockDialog dialog = new MouvementStockDialog(
                (Frame) SwingUtilities.getWindowAncestor(this), type, produits);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            refresh();
        }
    }
    
    @Override
    public void refresh() {
        loadProduits();
        loadData();
    }
}
