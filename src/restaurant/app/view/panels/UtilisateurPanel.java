package restaurant.app.view.panels;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import restaurant.app.controller.UtilisateurController;
import restaurant.app.model.entities.Utilisateur;
import restaurant.app.model.enums.RoleUtilisateur;
import restaurant.app.view.MainFrame;
import restaurant.app.view.components.ModernButton;
import restaurant.app.view.components.ModernTable;
import restaurant.app.view.components.SearchField;
import restaurant.app.view.dialogs.UtilisateurDialog;

/**
 * Panel de gestion des utilisateurs (admin uniquement).
 * 
 * @author Restaurant App
 * @version 1.0
 */
public class UtilisateurPanel extends JPanel implements MainFrame.Refreshable {
    
    private final UtilisateurController utilisateurController;
    
    private SearchField searchField;
    private JComboBox<String> roleFilter;
    private ModernTable table;
    private DefaultTableModel tableModel;
    
    private static final String[] COLUMNS = {"ID", "Login", "Nom", "Prénom", "Rôle", "Actif"};
    
    /**
     * Constructeur.
     */
    public UtilisateurPanel() {
        this.utilisateurController = new UtilisateurController();
        
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
        
        JLabel titleLabel = new JLabel("Utilisateurs");
        titleLabel.setFont(new Font("Montserrat", Font.BOLD, 28));
        titleLabel.setForeground(new Color(31, 41, 55));
        
        JLabel subtitleLabel = new JLabel("Gestion des comptes utilisateurs");
        subtitleLabel.setFont(new Font("Montserrat", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(107, 114, 128));
        
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        titlePanel.add(subtitleLabel);
        
        // Actions
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        actionsPanel.setOpaque(false);
        
        // Filtre par rôle
        roleFilter = new JComboBox<>(new String[]{"Tous les rôles", "Administrateurs", "Employés"});
        roleFilter.setFont(new Font("Montserrat", Font.PLAIN, 13));
        roleFilter.setPreferredSize(new Dimension(180, 45));
        roleFilter.addActionListener(e -> filterByRole());
        
        searchField = new SearchField("Rechercher un utilisateur...");
        searchField.addActionListener(e -> searchUtilisateurs());
        searchField.getTextField().getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) { searchUtilisateurs(); }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { searchUtilisateurs(); }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) { searchUtilisateurs(); }
        });
        
        ModernButton addButton = new ModernButton("Nouvel utilisateur", ModernButton.ButtonType.PRIMARY);
        addButton.addActionListener(e -> showAddDialog());
        
        actionsPanel.add(roleFilter);
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
        table.setColumnWidth(0, 50);
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
        
        ModernButton editButton = new ModernButton("Modifier", ModernButton.ButtonType.INFO);
        editButton.addActionListener(e -> editSelected());
        
        ModernButton toggleButton = new ModernButton("Activer/Désactiver", ModernButton.ButtonType.WARNING);
        toggleButton.addActionListener(e -> toggleSelected());
        
        ModernButton deleteButton = new ModernButton("Supprimer", ModernButton.ButtonType.DANGER);
        deleteButton.addActionListener(e -> deleteSelected());
        
        bottomPanel.add(editButton);
        bottomPanel.add(toggleButton);
        bottomPanel.add(deleteButton);
        
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Charge les données.
     */
    private void loadData() {
        SwingWorker<List<Utilisateur>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Utilisateur> doInBackground() throws Exception {
                return utilisateurController.getAllUtilisateurs();
            }
            
            @Override
            protected void done() {
                try {
                    List<Utilisateur> utilisateurs = get();
                    updateTable(utilisateurs);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(UtilisateurPanel.this,
                            "Erreur: " + e.getMessage(),
                            "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
    
    /**
     * Filtre par rôle.
     */
    private void filterByRole() {
        int selectedIndex = roleFilter.getSelectedIndex();
        
        SwingWorker<List<Utilisateur>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Utilisateur> doInBackground() throws Exception {
                switch (selectedIndex) {
                    case 1: return utilisateurController.getUtilisateursByRole(RoleUtilisateur.ADMIN);
                    case 2: return utilisateurController.getUtilisateursByRole(RoleUtilisateur.EMPLOYE);
                    default: return utilisateurController.getAllUtilisateurs();
                }
            }
            
            @Override
            protected void done() {
                try {
                    List<Utilisateur> utilisateurs = get();
                    updateTable(utilisateurs);
                } catch (Exception e) {
                    // Ignorer
                }
            }
        };
        worker.execute();
    }
    
    /**
     * Recherche des utilisateurs.
     */
    private void searchUtilisateurs() {
        String searchText = searchField.getText();
        
        SwingWorker<List<Utilisateur>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Utilisateur> doInBackground() throws Exception {
                return utilisateurController.searchUtilisateurs(searchText);
            }
            
            @Override
            protected void done() {
                try {
                    List<Utilisateur> utilisateurs = get();
                    updateTable(utilisateurs);
                } catch (Exception e) {
                    // Ignorer
                }
            }
        };
        worker.execute();
    }
    
    /**
     * Met à jour la table.
     * @param utilisateurs Les utilisateurs
     */
    private void updateTable(List<Utilisateur> utilisateurs) {
        tableModel.setRowCount(0);
        
        for (Utilisateur u : utilisateurs) {
            String roleText = u.getRole().getLibelle();
            
            tableModel.addRow(new Object[]{
                    u.getId(),
                    u.getLogin(),
                    u.getNomComplet(),
                    "",
                    roleText,
                    u.isActif() ? "Oui" : "Non"
            });
        }
    }
    
    /**
     * Affiche le dialogue d'ajout.
     */
    private void showAddDialog() {
        UtilisateurDialog dialog = new UtilisateurDialog((Frame) SwingUtilities.getWindowAncestor(this), null);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            refresh();
        }
    }
    
    /**
     * Affiche le dialogue de modification.
     * @param id L'ID de l'utilisateur
     */
    private void showEditDialog(int id) {
        try {
            Utilisateur utilisateur = utilisateurController.getUtilisateurById(id);
            if (utilisateur != null) {
                UtilisateurDialog dialog = new UtilisateurDialog((Frame) SwingUtilities.getWindowAncestor(this), utilisateur);
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
     * Modifie l'utilisateur sélectionné.
     */
    private void editSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Sélectionnez un utilisateur");
            return;
        }
        
        int id = (int) tableModel.getValueAt(row, 0);
        showEditDialog(id);
    }
    
    /**
     * Active/désactive l'utilisateur sélectionné.
     */
    private void toggleSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Sélectionnez un utilisateur");
            return;
        }
        
        int id = (int) tableModel.getValueAt(row, 0);
        String actifStr = (String) tableModel.getValueAt(row, 5);
        boolean currentlyActive = actifStr.equals("Oui");
        
        try {
            utilisateurController.setActif(id, !currentlyActive);
            refresh();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage());
        }
    }
    
    /**
     * Supprime l'utilisateur sélectionné.
     */
    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Sélectionnez un utilisateur");
            return;
        }
        
        int id = (int) tableModel.getValueAt(row, 0);
        String login = (String) tableModel.getValueAt(row, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
                "Supprimer l'utilisateur \"" + login + "\" ?",
                "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                utilisateurController.deleteUtilisateur(id);
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
