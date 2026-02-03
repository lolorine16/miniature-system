package restaurant.app.view.dialogs;

import java.awt.*;
import java.math.BigDecimal;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import restaurant.app.controller.ProduitController;
import restaurant.app.model.entities.Categorie;
import restaurant.app.model.entities.Produit;
import restaurant.app.view.components.ModernButton;
import restaurant.app.view.components.ModernTextField;

/**
 * Dialogue pour créer/modifier un produit.
 * 
 * @author Restaurant App
 * @version 1.0
 */
public class ProduitDialog extends JDialog {
    
    private final ProduitController produitController;
    private final Produit produit;
    private final List<Categorie> categories;
    
    private ModernTextField libelleField;
    private ModernTextField descriptionField;
    private ModernTextField prixField;
    private ModernTextField stockField;
    private JComboBox<String> categorieCombo;
    private JCheckBox actifCheckBox;
    
    private boolean confirmed = false;
    
    /**
     * Constructeur.
     * @param parent La fenêtre parent
     * @param produit Le produit à modifier (null pour création)
     * @param categories La liste des catégories
     */
    public ProduitDialog(Frame parent, Produit produit, List<Categorie> categories) {
        super(parent, produit == null ? "Nouveau produit" : "Modifier le produit", true);
        this.produitController = new ProduitController();
        this.produit = produit;
        this.categories = categories;
        
        initComponents();
        loadData();
        pack();
        setLocationRelativeTo(parent);
    }
    
    /**
     * Initialise les composants.
     */
    private void initComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(25, 30, 25, 30));
        mainPanel.setBackground(Color.WHITE);
        
        // Titre
        JLabel titleLabel = new JLabel(produit == null ? "Créer un produit" : "Modifier le produit");
        titleLabel.setFont(new Font("Montserrat", Font.BOLD, 20));
        titleLabel.setForeground(new Color(31, 41, 55));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Champ libellé
        mainPanel.add(createFieldPanel("Libellé *", libelleField = new ModernTextField("Nom du produit")));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        
        // Champ description
        mainPanel.add(createFieldPanel("Description", descriptionField = new ModernTextField("Description (optionnel)")));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        
        // Catégorie
        categorieCombo = new JComboBox<>();
        categorieCombo.setFont(new Font("Montserrat", Font.PLAIN, 14));
        if (categories != null) {
            for (Categorie cat : categories) {
                categorieCombo.addItem(cat.getLibelle());
            }
        }
        mainPanel.add(createFieldPanel("Catégorie *", categorieCombo));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        
        // Prix et Stock côte à côte
        JPanel rowPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        rowPanel.setOpaque(false);
        rowPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 75));
        
        prixField = new ModernTextField("0");
        stockField = new ModernTextField("0");
        
        rowPanel.add(createFieldPanel("Prix (FCFA) *", prixField));
        rowPanel.add(createFieldPanel("Stock initial", stockField));
        mainPanel.add(rowPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        
        // Case à cocher actif
        actifCheckBox = new JCheckBox("Produit actif");
        actifCheckBox.setFont(new Font("Montserrat", Font.PLAIN, 14));
        actifCheckBox.setSelected(true);
        actifCheckBox.setOpaque(false);
        actifCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(actifCheckBox);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Boutons
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonsPanel.setOpaque(false);
        buttonsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        ModernButton cancelButton = new ModernButton("Annuler", ModernButton.ButtonType.SECONDARY);
        cancelButton.addActionListener(e -> dispose());
        
        ModernButton saveButton = new ModernButton("Enregistrer", ModernButton.ButtonType.PRIMARY);
        saveButton.addActionListener(e -> save());
        
        buttonsPanel.add(cancelButton);
        buttonsPanel.add(saveButton);
        mainPanel.add(buttonsPanel);
        
        setContentPane(mainPanel);
        setPreferredSize(new Dimension(550, 650));
        setResizable(false);
    }
    
    /**
     * Crée un panel pour un champ avec label.
     */
    private JPanel createFieldPanel(String labelText, JComponent field) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Montserrat", Font.PLAIN, 13));
        label.setForeground(new Color(55, 65, 81));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        if (field instanceof JTextField) {
            field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        }
        
        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(field);
        
        return panel;
    }
    
    /**
     * Charge les données du produit.
     */
    private void loadData() {
        if (produit != null) {
            libelleField.setText(produit.getNom());
            descriptionField.setText(produit.getDescription() != null ? produit.getDescription() : "");
            prixField.setText(String.valueOf(produit.getPrixVente()).toString());
            stockField.setText(String.valueOf(produit.getStockActuel()));
            actifCheckBox.setSelected(produit.isActif());
            
            // Sélectionner la catégorie
            if (categories != null) {
                for (int i = 0; i < categories.size(); i++) {
                    if (categories.get(i).getId() == produit.getCategorie().getId()) {
                        categorieCombo.setSelectedIndex(i);
                        break;
                    }
                }
            }
        }
    }
    
    /**
     * Enregistre le produit.
     */
    private void save() {
        // Validation
        String libelle = libelleField.getText().trim();
        if (libelle.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Le libellé est obligatoire");
            return;
        }
        
        BigDecimal prix;
        try {
            prix = new BigDecimal(prixField.getText().trim().replace(",", "."));
            if (prix.compareTo(BigDecimal.ZERO) <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Le prix doit être un nombre positif");
            return;
        }
        
        int stock;
        try {
            stock = Integer.parseInt(stockField.getText().trim());
            if (stock < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Le stock doit être un nombre entier positif ou nul");
            return;
        }
        
        int selectedIndex = categorieCombo.getSelectedIndex();
        if (selectedIndex < 0 || categories == null || categories.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une catégorie");
            return;
        }
        
        int categorieId = categories.get(selectedIndex).getId();
        
        try {
            Produit p = produit != null ? produit : new Produit();
            p.setNom(libelle);
            p.setDescription(descriptionField.getText().trim().isEmpty() ? null : descriptionField.getText().trim());
            p.setPrixVente(prix.doubleValue());
            p.setStockActuel(stock);
            
            // Créer une catégorie temporaire avec l'ID
            Categorie cat = new Categorie();
            cat.setId(categorieId);
            p.setCategorie(cat);
            p.setActif(actifCheckBox.isSelected());
            
            if (p.getId() > 0) {
                produitController.updateProduit(p);
            } else {
                produitController.createProduit(p);
            }
            
            confirmed = true;
            dispose();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage());
        }
    }
    
    /**
     * Indique si le dialogue a été confirmé.
     * @return true si confirmé
     */
    public boolean isConfirmed() {
        return confirmed;
    }
}
