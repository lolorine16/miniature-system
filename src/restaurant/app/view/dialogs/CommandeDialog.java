package restaurant.app.view.dialogs;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import restaurant.app.controller.CommandeController;
import restaurant.app.controller.ProduitController;
import restaurant.app.model.entities.Commande;
import restaurant.app.model.entities.LigneCommande;
import restaurant.app.model.entities.Produit;
import restaurant.app.util.FormatUtil;
import restaurant.app.view.components.ModernTextField;

/**
 * Dialogue pour creer ou modifier une commande.
 * Permet de selectionner des produits et leurs quantites.
 * 
 * @author Restaurant App
 * @version 1.0
 */
public class CommandeDialog extends JDialog {
    
    private final CommandeController commandeController;
    private final ProduitController produitController;
    
    private Commande commande;
    private List<Produit> produits;
    private List<LigneCommande> lignesCommande;
    
    // Champs client
    private ModernTextField clientNomField;
    private ModernTextField clientTelephoneField;
    private ModernTextField notesField;
    
    private JComboBox<String> produitCombo;
    private JSpinner quantiteSpinner;
    private DefaultTableModel tableModel;
    private JTable lignesTable;
    private JLabel totalLabel;
    
    private boolean confirmed = false;
    
    private static final String[] COLUMNS = {"Produit", "Prix unit.", "Quantite", "Sous-total", ""};
    
    /**
     * Constructeur pour une nouvelle commande.
     * @param parent La fenetre parente
     */
    public CommandeDialog(Frame parent) {
        this(parent, null);
    }
    
    /**
     * Constructeur pour modifier une commande existante.
     * @param parent La fenetre parente
     * @param commande La commande a modifier (null pour nouvelle)
     */
    public CommandeDialog(Frame parent, Commande commande) {
        super(parent, commande == null ? "Nouvelle commande" : "Modifier commande #" + commande.getId(), true);
        
        this.commandeController = new CommandeController();
        this.produitController = new ProduitController();
        this.commande = commande;
        this.lignesCommande = new ArrayList<>();
        
        loadProduits();
        
        if (commande != null && commande.getLignes() != null) {
            lignesCommande.addAll(commande.getLignes());
        }
        
        initComponents();
        updateTotal();
        
        setSize(850, 750);
        setLocationRelativeTo(parent);
        setResizable(false);
    }
    
    /**
     * Charge la liste des produits.
     */
    private void loadProduits() {
        try {
            produits = produitController.getProduitsActifs();
        } catch (Exception e) {
            produits = new ArrayList<>();
        }
    }
    
    /**
     * Initialise les composants.
     */
    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);
        
        // Titre
        JLabel titleLabel = new JLabel(commande == null ? "Nouvelle commande" : "Commande #" + commande.getId());
        titleLabel.setFont(new Font("Montserrat", Font.BOLD, 20));
        titleLabel.setForeground(new Color(31, 41, 55));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Panel central avec scroll
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        
        // Panel informations client
        JPanel clientPanel = createClientPanel();
        centerPanel.add(clientPanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Panel d'ajout de produit
        JPanel addPanel = createAddProductPanel();
        centerPanel.add(addPanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Table des lignes
        JPanel tablePanel = createTablePanel();
        centerPanel.add(tablePanel);
        
        // Total
        JPanel totalPanel = createTotalPanel();
        centerPanel.add(totalPanel);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Boutons
        JPanel buttonsPanel = createButtonsPanel();
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
    }
    
    /**
     * Cree le panel des informations client.
     */
    private JPanel createClientPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 10, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createTitledBorder("Informations client"));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        
        // Nom client
        JPanel nomPanel = new JPanel(new BorderLayout(5, 0));
        nomPanel.setOpaque(false);
        JLabel nomLabel = new JLabel("Nom client:");
        nomLabel.setFont(new Font("Montserrat", Font.PLAIN, 12));
        clientNomField = new ModernTextField("Nom du client");
        nomPanel.add(nomLabel, BorderLayout.NORTH);
        nomPanel.add(clientNomField, BorderLayout.CENTER);
        
        // Telephone
        JPanel telPanel = new JPanel(new BorderLayout(5, 0));
        telPanel.setOpaque(false);
        JLabel telLabel = new JLabel("Telephone:");
        telLabel.setFont(new Font("Montserrat", Font.PLAIN, 12));
        clientTelephoneField = new ModernTextField("Numero de telephone");
        telPanel.add(telLabel, BorderLayout.NORTH);
        telPanel.add(clientTelephoneField, BorderLayout.CENTER);
        
        // Notes
        JPanel notesPanel = new JPanel(new BorderLayout(5, 0));
        notesPanel.setOpaque(false);
        JLabel notesLabel = new JLabel("Notes:");
        notesLabel.setFont(new Font("Montserrat", Font.PLAIN, 12));
        notesField = new ModernTextField("Notes (ex: sans oignon)");
        notesPanel.add(notesLabel, BorderLayout.NORTH);
        notesPanel.add(notesField, BorderLayout.CENTER);
        
        panel.add(nomPanel);
        panel.add(telPanel);
        panel.add(notesPanel);
        
        return panel;
    }
    
    /**
     * Cree le panel d'ajout de produit.
     */
    private JPanel createAddProductPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createTitledBorder("Ajouter un produit"));
        
        // Combo des produits
        produitCombo = new JComboBox<>();
        produitCombo.setFont(new Font("Montserrat", Font.PLAIN, 13));
        produitCombo.setPreferredSize(new Dimension(300, 35));
        
        for (Produit p : produits) {
            produitCombo.addItem(p.getNom() + " - " + FormatUtil.formatCurrency(p.getPrixVente()));
        }
        
        // Spinner quantite
        JLabel qtyLabel = new JLabel("Quantite:");
        qtyLabel.setFont(new Font("Montserrat", Font.PLAIN, 13));
        
        quantiteSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        quantiteSpinner.setFont(new Font("Montserrat", Font.PLAIN, 13));
        quantiteSpinner.setPreferredSize(new Dimension(70, 35));
        
        // Bouton ajouter
        JButton addButton = new JButton("Ajouter");
        addButton.setFont(new Font("Montserrat", Font.BOLD, 13));
        addButton.setBackground(new Color(34, 197, 94));
        addButton.setForeground(Color.WHITE);
        addButton.setPreferredSize(new Dimension(100, 35));
        addButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addButton.addActionListener(e -> addProduct());
        
        panel.add(produitCombo);
        panel.add(qtyLabel);
        panel.add(quantiteSpinner);
        panel.add(addButton);
        
        return panel;
    }
    
    /**
     * Cree le panel de la table.
     */
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createTitledBorder("Produits de la commande"));
        
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        lignesTable = new JTable(tableModel);
        lignesTable.setFont(new Font("Montserrat", Font.PLAIN, 13));
        lignesTable.setRowHeight(35);
        lignesTable.getTableHeader().setFont(new Font("Montserrat", Font.BOLD, 12));
        
        // Colonne de suppression
        lignesTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        
        // Bouton supprimer sur clic
        lignesTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int column = lignesTable.columnAtPoint(e.getPoint());
                int row = lignesTable.rowAtPoint(e.getPoint());
                if (column == 4 && row >= 0) {
                    removeLine(row);
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(lignesTable);
        scrollPane.setPreferredSize(new Dimension(0, 200));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        updateTableFromLignes();
        
        return panel;
    }
    
    /**
     * Cree le panel du total.
     */
    private JPanel createTotalPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setOpaque(false);
        
        JLabel label = new JLabel("Total: ");
        label.setFont(new Font("Montserrat", Font.BOLD, 18));
        
        totalLabel = new JLabel("0 FCFA");
        totalLabel.setFont(new Font("Montserrat", Font.BOLD, 24));
        totalLabel.setForeground(new Color(34, 197, 94));
        
        panel.add(label);
        panel.add(totalLabel);
        
        return panel;
    }
    
    /**
     * Cree le panel des boutons.
     */
    private JPanel createButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panel.setOpaque(false);
        
        JButton cancelButton = new JButton("Annuler");
        cancelButton.setFont(new Font("Montserrat", Font.PLAIN, 13));
        cancelButton.setPreferredSize(new Dimension(100, 40));
        cancelButton.addActionListener(e -> dispose());
        
        JButton confirmButton = new JButton("Confirmer");
        confirmButton.setFont(new Font("Montserrat", Font.BOLD, 13));
        confirmButton.setBackground(new Color(59, 130, 246));
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setPreferredSize(new Dimension(120, 40));
        confirmButton.addActionListener(e -> confirm());
        
        panel.add(cancelButton);
        panel.add(confirmButton);
        
        return panel;
    }
    
    /**
     * Ajoute un produit a la commande.
     */
    private void addProduct() {
        int selectedIndex = produitCombo.getSelectedIndex();
        if (selectedIndex < 0 || selectedIndex >= produits.size()) {
            return;
        }
        
        Produit produit = produits.get(selectedIndex);
        int quantite = (Integer) quantiteSpinner.getValue();
        
        // Verifier si le produit est deja dans la liste
        for (LigneCommande ligne : lignesCommande) {
            if (ligne.getProduit().getId() == produit.getId()) {
                // Augmenter la quantite
                ligne.setQuantite(ligne.getQuantite() + quantite);
                updateTableFromLignes();
                updateTotal();
                return;
            }
        }
        
        // Ajouter nouvelle ligne
        LigneCommande ligne = new LigneCommande(produit, quantite);
        lignesCommande.add(ligne);
        
        updateTableFromLignes();
        updateTotal();
        
        // Reset
        quantiteSpinner.setValue(1);
    }
    
    /**
     * Supprime une ligne.
     */
    private void removeLine(int row) {
        if (row >= 0 && row < lignesCommande.size()) {
            lignesCommande.remove(row);
            updateTableFromLignes();
            updateTotal();
        }
    }
    
    /**
     * Met a jour la table depuis les lignes.
     */
    private void updateTableFromLignes() {
        tableModel.setRowCount(0);
        
        for (LigneCommande ligne : lignesCommande) {
            double sousTotal = ligne.getPrixUnitaire() * ligne.getQuantite();
            tableModel.addRow(new Object[]{
                    ligne.getProduit().getNom(),
                    FormatUtil.formatCurrency(ligne.getPrixUnitaire()),
                    ligne.getQuantite(),
                    FormatUtil.formatCurrency(sousTotal),
                    "Supprimer"
            });
        }
    }
    
    /**
     * Met a jour le total.
     */
    private void updateTotal() {
        double total = 0;
        for (LigneCommande ligne : lignesCommande) {
            total += ligne.getPrixUnitaire() * ligne.getQuantite();
        }
        totalLabel.setText(FormatUtil.formatCurrency(total));
    }
    
    /**
     * Confirme la commande.
     */
    private void confirm() {
        if (lignesCommande.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez ajouter au moins un produit",
                    "Erreur", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            // Recuperer les informations client
            String clientNom = clientNomField.getText().trim();
            String clientTelephone = clientTelephoneField.getText().trim();
            String notes = notesField.getText().trim();
            
            // Vider si c'est le placeholder
            if (clientNom.equals("Nom du client")) clientNom = "";
            if (clientTelephone.equals("Numero de telephone")) clientTelephone = "";
            if (notes.equals("Notes (ex: sans oignon)")) notes = "";
            
            if (commande == null) {
                // Nouvelle commande avec infos client
                commande = commandeController.creerCommande(
                        clientNom.isEmpty() ? null : clientNom,
                        clientTelephone.isEmpty() ? null : clientTelephone,
                        notes.isEmpty() ? null : notes
                );
            }
            
            // Ajouter les lignes
            for (LigneCommande ligne : lignesCommande) {
                commandeController.ajouterProduit(
                        commande.getId(),
                        ligne.getProduit().getId(),
                        ligne.getQuantite()
                );
            }
            
            confirmed = true;
            dispose();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Retourne si le dialogue a ete confirme.
     * @return true si confirme
     */
    public boolean isConfirmed() {
        return confirmed;
    }
    
    /**
     * Retourne la commande creee/modifiee.
     * @return La commande
     */
    public Commande getCommande() {
        return commande;
    }
}
