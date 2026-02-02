package restaurant.app.view.panels;

import java.awt.*;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import restaurant.app.controller.CategorieController;
import restaurant.app.controller.ProduitController;
import restaurant.app.model.entities.Categorie;
import restaurant.app.model.entities.Produit;
import restaurant.app.view.MainFrame;
import restaurant.app.view.components.ModernButton;
import restaurant.app.view.components.ModernTable;
import restaurant.app.view.components.SearchField;
import restaurant.app.view.dialogs.ProduitDialog;

/**
 * Panel de gestion des produits.
 * 
 * @author Restaurant App
 * @version 1.0
 */
public class ProduitPanel extends JPanel implements MainFrame.Refreshable {
    
    private final ProduitController produitController;
    private final CategorieController categorieController;
    private final NumberFormat currencyFormat;
    
    private SearchField searchField;
    private JComboBox<String> categorieFilter;
    private ModernTable table;
    private DefaultTableModel tableModel;
    
    private List<Categorie> categories;
    
    private static final String[] COLUMNS = {"ID", "Libellé", "Catégorie", "Prix", "Stock", "Actif"};
    
    /**
     * Constructeur.
     */
    public ProduitPanel() {
        this.produitController = new ProduitController();
        this.categorieController = new CategorieController();
        this.currencyFormat = NumberFormat.getCurrencyInstance(Locale.FRANCE);
        
        setLayout(new BorderLayout());
        setBackground(new Color(243, 244, 246));
        setBorder(new EmptyBorder(30, 30, 30, 30));
        
        initComponents();
        loadCategories();
        loadData();
    }
    
    /**
     * Initialise les composants.
     */
    private void initComponents() {
        // Header
        JPanel headerPanel = createHeader();
        add(headerPanel, BorderLayout.NORTH);
        
        // Table
        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);
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
        
        JLabel titleLabel = new JLabel("Produits");
        titleLabel.setFont(new Font("Montserrat", Font.BOLD, 28));
        titleLabel.setForeground(new Color(31, 41, 55));
        
        JLabel subtitleLabel = new JLabel("Gérez votre catalogue de produits");
        subtitleLabel.setFont(new Font("Montserrat", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(107, 114, 128));
        
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        titlePanel.add(subtitleLabel);
        
        // Actions
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        actionsPanel.setOpaque(false);
        
        // Filtre catégorie
        categorieFilter = new JComboBox<>();
        categorieFilter.setFont(new Font("Montserrat", Font.PLAIN, 13));
        categorieFilter.setPreferredSize(new Dimension(180, 45));
        categorieFilter.addItem("Toutes les catégories");
        categorieFilter.addActionListener(e -> filterByCategorie());
        
        searchField = new SearchField("Rechercher un produit...");
        searchField.addActionListener(e -> searchProduits());
        searchField.getTextField().getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) { searchProduits(); }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { searchProduits(); }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) { searchProduits(); }
        });
        
        ModernButton addButton = new ModernButton("➕ Nouveau produit", ModernButton.ButtonType.PRIMARY);
        addButton.addActionListener(e -> showAddDialog());
        
        actionsPanel.add(categorieFilter);
        actionsPanel.add(searchField);
        actionsPanel.add(addButton);
        
        header.add(titlePanel, BorderLayout.WEST);
        header.add(actionsPanel, BorderLayout.EAST);
        
        return header;
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
        table.setColumnWidth(0, 60);
        table.setColumnWidth(3, 100);
        table.setColumnWidth(4, 80);
        table.setColumnWidth(5, 80);
        
        // Double-clic pour éditer
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row >= 0) {
                        int id = (int) tableModel.getValueAt(row, 0);
                        showEditDialog(id);
                    }
                }
            }
        });
        
        JScrollPane scrollPane = table.wrapInScrollPane();
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Panel de boutons en bas
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottomPanel.setOpaque(false);
        
        ModernButton editButton = new ModernButton("✏️ Modifier", ModernButton.ButtonType.INFO);
        editButton.addActionListener(e -> editSelected());
        
        ModernButton deleteButton = new ModernButton("🗑️ Supprimer", ModernButton.ButtonType.DANGER);
        deleteButton.addActionListener(e -> deleteSelected());
        
        bottomPanel.add(editButton);
        bottomPanel.add(deleteButton);
        
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Charge les catégories.
     */
    private void loadCategories() {
        try {
            categories = categorieController.getAllCategories();
            for (Categorie cat : categories) {
                categorieFilter.addItem(cat.getLibelle());
            }
        } catch (Exception e) {
            // Ignorer
        }
    }
    
    /**
     * Charge les données.
     */
    private void loadData() {
        SwingWorker<List<Produit>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Produit> doInBackground() throws Exception {
                return produitController.getAllProduits();
            }
            
            @Override
            protected void done() {
                try {
                    List<Produit> produits = get();
                    updateTable(produits);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(ProduitPanel.this,
                            "Erreur lors du chargement: " + e.getMessage(),
                            "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
    
    /**
     * Filtre par catégorie.
     */
    private void filterByCategorie() {
        int selectedIndex = categorieFilter.getSelectedIndex();
        if (selectedIndex == 0) {
            loadData();
        } else if (categories != null && selectedIndex > 0 && selectedIndex <= categories.size()) {
            Categorie cat = categories.get(selectedIndex - 1);
            
            SwingWorker<List<Produit>, Void> worker = new SwingWorker<>() {
                @Override
                protected List<Produit> doInBackground() throws Exception {
                    return produitController.getProduitsByCategorie(cat.getId());
                }
                
                @Override
                protected void done() {
                    try {
                        List<Produit> produits = get();
                        updateTable(produits);
                    } catch (Exception e) {
                        // Ignorer
                    }
                }
            };
            worker.execute();
        }
    }
    
    /**
     * Recherche des produits.
     */
    private void searchProduits() {
        String searchText = searchField.getText();
        
        SwingWorker<List<Produit>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Produit> doInBackground() throws Exception {
                return produitController.searchProduits(searchText);
            }
            
            @Override
            protected void done() {
                try {
                    List<Produit> produits = get();
                    updateTable(produits);
                } catch (Exception e) {
                    // Ignorer
                }
            }
        };
        worker.execute();
    }
    
    /**
     * Met à jour la table.
     * @param produits Les produits
     */
    private void updateTable(List<Produit> produits) {
        tableModel.setRowCount(0);
        
        for (Produit p : produits) {
            // Trouver le nom de la catégorie
            String categorieName = "-";
            if (categories != null) {
                for (Categorie cat : categories) {
                    if (cat.getId() == p.getCategorie().getId()) {
                        categorieName = cat.getLibelle();
                        break;
                    }
                }
            }
            
            // Colorer le stock si faible
            String stockDisplay = String.valueOf(p.getStockActuel());
            if (p.getStockActuel() <= 0) {
                stockDisplay = "❌ " + p.getStockActuel();
            } else if (p.getStockActuel() <= 10) {
                stockDisplay = "⚠️ " + p.getStockActuel();
            }
            
            tableModel.addRow(new Object[]{
                    p.getId(),
                    p.getNom(),
                    categorieName,
                    currencyFormat.format(p.getPrixVente()),
                    stockDisplay,
                    p.isActif() ? "✅" : "❌"
            });
        }
    }
    
    /**
     * Affiche le dialogue d'ajout.
     */
    private void showAddDialog() {
        ProduitDialog dialog = new ProduitDialog((Frame) SwingUtilities.getWindowAncestor(this), null, categories);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            refresh();
        }
    }
    
    /**
     * Affiche le dialogue de modification.
     * @param id L'ID du produit
     */
    private void showEditDialog(int id) {
        try {
            Produit produit = produitController.getProduitById(id);
            if (produit != null) {
                ProduitDialog dialog = new ProduitDialog((Frame) SwingUtilities.getWindowAncestor(this), produit, categories);
                dialog.setVisible(true);
                
                if (dialog.isConfirmed()) {
                    refresh();
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Modifie le produit sélectionné.
     */
    private void editSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner un produit",
                    "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int id = (int) tableModel.getValueAt(row, 0);
        showEditDialog(id);
    }
    
    /**
     * Supprime le produit sélectionné.
     */
    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner un produit",
                    "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int id = (int) tableModel.getValueAt(row, 0);
        String libelle = (String) tableModel.getValueAt(row, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
                "Voulez-vous vraiment supprimer le produit \"" + libelle + "\" ?",
                "Confirmation de suppression",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                produitController.deleteProduit(id);
                refresh();
                JOptionPane.showMessageDialog(this,
                        "Produit supprimé avec succès",
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Erreur: " + e.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    @Override
    public void refresh() {
        loadData();
    }
}
