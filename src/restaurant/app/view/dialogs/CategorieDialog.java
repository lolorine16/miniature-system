package restaurant.app.view.dialogs;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import restaurant.app.controller.CategorieController;
import restaurant.app.model.entities.Categorie;
import restaurant.app.view.components.ModernButton;
import restaurant.app.view.components.ModernTextField;

/**
 * Dialogue pour créer/modifier une catégorie.
 * 
 * @author Restaurant App
 * @version 1.0
 */
public class CategorieDialog extends JDialog {
    
    private final CategorieController categorieController;
    private final Categorie categorie;
    
    private ModernTextField libelleField;
    private ModernTextField descriptionField;
    
    private boolean confirmed = false;
    
    /**
     * Constructeur.
     * @param parent La fenêtre parent
     * @param categorie La catégorie à modifier (null pour création)
     */
    public CategorieDialog(Frame parent, Categorie categorie) {
        super(parent, categorie == null ? "Nouvelle catégorie" : "Modifier la catégorie", true);
        this.categorieController = new CategorieController();
        this.categorie = categorie;
        
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
        JLabel titleLabel = new JLabel(categorie == null ? "Créer une catégorie" : "Modifier la catégorie");
        titleLabel.setFont(new Font("Montserrat", Font.BOLD, 20));
        titleLabel.setForeground(new Color(31, 41, 55));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Champ libellé
        mainPanel.add(createFieldPanel("Libellé *", libelleField = new ModernTextField("Nom de la catégorie")));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Champ description
        mainPanel.add(createFieldPanel("Description", descriptionField = new ModernTextField("Description (optionnel)")));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        
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
        setPreferredSize(new Dimension(450, 300));
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
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        
        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(field);
        
        return panel;
    }
    
    /**
     * Charge les données de la catégorie.
     */
    private void loadData() {
        if (categorie != null) {
            libelleField.setText(categorie.getLibelle());
            descriptionField.setText(categorie.getDescription() != null ? categorie.getDescription() : "");
        }
    }
    
    /**
     * Enregistre la catégorie.
     */
    private void save() {
        String libelle = libelleField.getText().trim();
        String description = descriptionField.getText().trim();
        
        // Validation
        if (libelle.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Le libellé est obligatoire",
                    "Erreur de validation", JOptionPane.ERROR_MESSAGE);
            libelleField.requestFocus();
            return;
        }
        
        try {
            Categorie cat = categorie != null ? categorie : new Categorie();
            cat.setLibelle(libelle);
            cat.setDescription(description.isEmpty() ? null : description);
            
            categorieController.saveCategorie(cat);
            
            confirmed = true;
            dispose();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
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
