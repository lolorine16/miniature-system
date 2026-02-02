package restaurant.app.view.panels;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import restaurant.app.controller.CategorieController;
import restaurant.app.model.entities.Categorie;
import restaurant.app.view.MainFrame;
import restaurant.app.view.components.ModernButton;
import restaurant.app.view.components.ModernTable;
import restaurant.app.view.components.SearchField;
import restaurant.app.view.dialogs.CategorieDialog;

/**
 * Panel de gestion des catégories.
 * 
 * @author Restaurant App
 * @version 1.0
 */
public class CategoriePanel extends JPanel implements MainFrame.Refreshable {
    
    private final CategorieController categorieController;
    
    private SearchField searchField;
    private ModernTable table;
    private DefaultTableModel tableModel;
    
    private static final String[] COLUMNS = {"ID", "Libellé", "Description", "Actif", "Actions"};
    
    /**
     * Constructeur.
     */
    public CategoriePanel() {
        this.categorieController = new CategorieController();
        
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
        
        JLabel titleLabel = new JLabel("Catégories");
        titleLabel.setFont(new Font("Montserrat", Font.BOLD, 28));
        titleLabel.setForeground(new Color(31, 41, 55));
        
        JLabel subtitleLabel = new JLabel("Gérez les catégories de produits");
        subtitleLabel.setFont(new Font("Montserrat", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(107, 114, 128));
        
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        titlePanel.add(subtitleLabel);
        
        // Actions
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        actionsPanel.setOpaque(false);
        
        searchField = new SearchField("Rechercher une catégorie...");
        searchField.addActionListener(e -> searchCategories());
        searchField.getTextField().getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) { searchCategories(); }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { searchCategories(); }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) { searchCategories(); }
        });
        
        ModernButton addButton = new ModernButton("➕ Nouvelle catégorie", ModernButton.ButtonType.PRIMARY);
        addButton.addActionListener(e -> showAddDialog());
        
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
                return column == 4; // Seule la colonne Actions est "éditable" (pour les boutons)
            }
        };
        
        // Table
        table = new ModernTable(tableModel);
        table.setColumnWidth(0, 60);
        table.setColumnWidth(3, 80);
        table.setColumnWidth(4, 150);
        
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
     * Charge les données.
     */
    private void loadData() {
        SwingWorker<List<Categorie>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Categorie> doInBackground() throws Exception {
                return categorieController.getAllCategories();
            }
            
            @Override
            protected void done() {
                try {
                    List<Categorie> categories = get();
                    updateTable(categories);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(CategoriePanel.this,
                            "Erreur lors du chargement: " + e.getMessage(),
                            "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
    
    /**
     * Recherche des catégories.
     */
    private void searchCategories() {
        String searchText = searchField.getText();
        
        SwingWorker<List<Categorie>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Categorie> doInBackground() throws Exception {
                return categorieController.searchCategories(searchText);
            }
            
            @Override
            protected void done() {
                try {
                    List<Categorie> categories = get();
                    updateTable(categories);
                } catch (Exception e) {
                    // Ignorer les erreurs de recherche
                }
            }
        };
        worker.execute();
    }
    
    /**
     * Met à jour la table.
     * @param categories Les catégories
     */
    private void updateTable(List<Categorie> categories) {
        tableModel.setRowCount(0);
        
        for (Categorie cat : categories) {
            tableModel.addRow(new Object[]{
                    cat.getId(),
                    cat.getLibelle(),
                    cat.getDescription() != null ? cat.getDescription() : "-",
                    "✅ Actif",
                    "Actions"
            });
        }
    }
    
    /**
     * Affiche le dialogue d'ajout.
     */
    private void showAddDialog() {
        CategorieDialog dialog = new CategorieDialog((Frame) SwingUtilities.getWindowAncestor(this), null);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            refresh();
        }
    }
    
    /**
     * Affiche le dialogue de modification.
     * @param id L'ID de la catégorie
     */
    private void showEditDialog(int id) {
        try {
            Categorie categorie = categorieController.getCategorieById(id);
            if (categorie != null) {
                CategorieDialog dialog = new CategorieDialog((Frame) SwingUtilities.getWindowAncestor(this), categorie);
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
     * Modifie la catégorie sélectionnée.
     */
    private void editSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner une catégorie",
                    "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int id = (int) tableModel.getValueAt(row, 0);
        showEditDialog(id);
    }
    
    /**
     * Supprime la catégorie sélectionnée.
     */
    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner une catégorie",
                    "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int id = (int) tableModel.getValueAt(row, 0);
        String libelle = (String) tableModel.getValueAt(row, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
                "Voulez-vous vraiment supprimer la catégorie \"" + libelle + "\" ?",
                "Confirmation de suppression",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                categorieController.deleteCategorie(id);
                refresh();
                JOptionPane.showMessageDialog(this,
                        "Catégorie supprimée avec succès",
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
