package restaurant.app.view.dialogs;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import restaurant.app.controller.StockController;
import restaurant.app.model.entities.Produit;
import restaurant.app.model.enums.TypeMouvement;
import restaurant.app.view.components.ModernButton;
import restaurant.app.view.components.ModernTextField;

/**
 * Dialogue pour enregistrer un mouvement de stock.
 * 
 * @author Restaurant App
 * @version 1.0
 */
public class MouvementStockDialog extends JDialog {
    
    private final StockController stockController;
    private final TypeMouvement type;
    private final List<Produit> produits;
    
    private JComboBox<String> produitCombo;
    private ModernTextField quantiteField;
    private ModernTextField motifField;
    
    private boolean confirmed = false;
    
    /**
     * Constructeur.
     * @param parent La fenêtre parent
     * @param type Le type de mouvement
     * @param produits La liste des produits
     */
    public MouvementStockDialog(Frame parent, TypeMouvement type, List<Produit> produits) {
        super(parent, type == TypeMouvement.ENTREE ? "Entrée de stock" : "Sortie de stock", true);
        this.stockController = new StockController();
        this.type = type;
        this.produits = produits;
        
        initComponents();
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
        
        // Icône et titre
        String icon = type == TypeMouvement.ENTREE ? "📥" : "📤";
        Color titleColor = type == TypeMouvement.ENTREE ? new Color(34, 197, 94) : new Color(239, 68, 68);
        
        JLabel titleLabel = new JLabel(icon + " " + (type == TypeMouvement.ENTREE ? "Entrée de stock" : "Sortie de stock"));
        titleLabel.setFont(new Font("Montserrat", Font.BOLD, 20));
        titleLabel.setForeground(titleColor);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Sélection du produit
        produitCombo = new JComboBox<>();
        produitCombo.setFont(new Font("Montserrat", Font.PLAIN, 14));
        if (produits != null) {
            for (Produit p : produits) {
                produitCombo.addItem(p.getNom() + " (Stock: " + p.getStockActuel() + ")");
            }
        }
        mainPanel.add(createFieldPanel("Produit *", produitCombo));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Quantité
        quantiteField = new ModernTextField("Quantité");
        mainPanel.add(createFieldPanel("Quantité *", quantiteField));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Motif
        motifField = new ModernTextField("Motif (optionnel)");
        mainPanel.add(createFieldPanel("Motif", motifField));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        
        // Boutons
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonsPanel.setOpaque(false);
        buttonsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        ModernButton cancelButton = new ModernButton("Annuler", ModernButton.ButtonType.SECONDARY);
        cancelButton.addActionListener(e -> dispose());
        
        ModernButton.ButtonType saveType = type == TypeMouvement.ENTREE ? 
                ModernButton.ButtonType.SUCCESS : ModernButton.ButtonType.DANGER;
        ModernButton saveButton = new ModernButton("Enregistrer", saveType);
        saveButton.addActionListener(e -> save());
        
        buttonsPanel.add(cancelButton);
        buttonsPanel.add(saveButton);
        mainPanel.add(buttonsPanel);
        
        setContentPane(mainPanel);
        setPreferredSize(new Dimension(450, 380));
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
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 75));
        
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
     * Enregistre le mouvement.
     */
    private void save() {
        // Validation
        int selectedIndex = produitCombo.getSelectedIndex();
        if (selectedIndex < 0 || produits == null || produits.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un produit");
            return;
        }
        
        int quantite;
        try {
            quantite = Integer.parseInt(quantiteField.getText().trim());
            if (quantite <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "La quantité doit être un nombre entier positif");
            return;
        }
        
        Produit produit = produits.get(selectedIndex);
        String motif = motifField.getText().trim();
        
        try {
            if (type == TypeMouvement.ENTREE) {
                stockController.enregistrerEntree(produit.getId(), quantite, motif.isEmpty() ? null : motif);
            } else {
                stockController.enregistrerSortie(produit.getId(), quantite, motif.isEmpty() ? null : motif);
            }
            
            JOptionPane.showMessageDialog(this,
                    "Mouvement enregistré avec succès",
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
            
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
