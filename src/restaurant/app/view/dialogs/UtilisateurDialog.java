package restaurant.app.view.dialogs;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import restaurant.app.controller.UtilisateurController;
import restaurant.app.model.entities.Utilisateur;
import restaurant.app.model.enums.RoleUtilisateur;
import restaurant.app.view.components.ModernButton;
import restaurant.app.view.components.ModernPasswordField;
import restaurant.app.view.components.ModernTextField;

/**
 * Dialogue pour créer/modifier un utilisateur.
 * 
 * @author Restaurant App
 * @version 1.0
 */
public class UtilisateurDialog extends JDialog {
    
    private final UtilisateurController utilisateurController;
    private final Utilisateur utilisateur;
    
    private ModernTextField loginField;
    private ModernTextField nomCompletField;
    private ModernTextField emailField;
    private ModernTextField telephoneField;
    private ModernPasswordField passwordField;
    private JComboBox<String> roleCombo;
    private JCheckBox actifCheckBox;
    
    private boolean confirmed = false;
    
    /**
     * Constructeur.
     * @param parent La fenêtre parent
     * @param utilisateur L'utilisateur à modifier (null pour création)
     */
    public UtilisateurDialog(Frame parent, Utilisateur utilisateur) {
        super(parent, utilisateur == null ? "Nouvel utilisateur" : "Modifier l'utilisateur", true);
        this.utilisateurController = new UtilisateurController();
        this.utilisateur = utilisateur;
        
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
        JLabel titleLabel = new JLabel(utilisateur == null ? "Créer un utilisateur" : "Modifier l'utilisateur");
        titleLabel.setFont(new Font("Montserrat", Font.BOLD, 20));
        titleLabel.setForeground(new Color(31, 41, 55));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Login
        mainPanel.add(createFieldPanel("Login *", loginField = new ModernTextField("Identifiant")));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        
        // Nom complet
        mainPanel.add(createFieldPanel("Nom complet *", nomCompletField = new ModernTextField("Prénom Nom")));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        
        // Email
        mainPanel.add(createFieldPanel("Email", emailField = new ModernTextField("email@exemple.com")));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        
        // Téléphone
        mainPanel.add(createFieldPanel("Téléphone", telephoneField = new ModernTextField("+243...")));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        
        // Mot de passe
        String passwordLabel = utilisateur == null ? "Mot de passe *" : "Nouveau mot de passe (laisser vide pour ne pas changer)";
        mainPanel.add(createFieldPanel(passwordLabel, passwordField = new ModernPasswordField("Mot de passe")));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        
        // Rôle
        roleCombo = new JComboBox<>(new String[]{"Administrateur", "Employé"});
        roleCombo.setFont(new Font("Montserrat", Font.PLAIN, 14));
        mainPanel.add(createFieldPanel("Rôle *", roleCombo));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        
        // Actif
        actifCheckBox = new JCheckBox("Compte actif");
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
        setPreferredSize(new Dimension(500, 580));
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
        if (field instanceof JTextField || field instanceof ModernPasswordField) {
            field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        }
        
        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(field);
        
        return panel;
    }
    
    /**
     * Charge les données de l'utilisateur.
     */
    private void loadData() {
        if (utilisateur != null) {
            loginField.setText(utilisateur.getLogin());
            nomCompletField.setText(utilisateur.getNomComplet());
            emailField.setText(utilisateur.getEmail() != null ? utilisateur.getEmail() : "");
            telephoneField.setText(utilisateur.getTelephone() != null ? utilisateur.getTelephone() : "");
            actifCheckBox.setSelected(utilisateur.isActif());
            
            // Sélectionner le rôle
            roleCombo.setSelectedIndex(utilisateur.getRole() == RoleUtilisateur.ADMIN ? 0 : 1);
        }
    }
    
    /**
     * Enregistre l'utilisateur.
     */
    private void save() {
        // Validation
        String login = loginField.getText().trim();
        String nomComplet = nomCompletField.getText().trim();
        String email = emailField.getText().trim();
        String telephone = telephoneField.getText().trim();
        String password = passwordField.getPasswordText();
        
        if (login.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Le login est obligatoire");
            loginField.requestFocus();
            return;
        }
        
        if (nomComplet.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Le nom complet est obligatoire");
            nomCompletField.requestFocus();
            return;
        }
        
        // Mot de passe obligatoire pour un nouvel utilisateur
        if (utilisateur == null && password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Le mot de passe est obligatoire");
            passwordField.requestFocus();
            return;
        }
        
        RoleUtilisateur role = roleCombo.getSelectedIndex() == 0 ? 
                RoleUtilisateur.ADMIN : RoleUtilisateur.EMPLOYE;
        
        try {
            if (utilisateur == null) {
                // Création
                Utilisateur newUser = new Utilisateur();
                newUser.setLogin(login);
                newUser.setNomComplet(nomComplet);
                newUser.setEmail(email.isEmpty() ? null : email);
                newUser.setTelephone(telephone.isEmpty() ? null : telephone);
                newUser.setRole(role);
                newUser.setActif(actifCheckBox.isSelected());
                
                utilisateurController.creerUtilisateur(newUser, password);
            } else {
                // Modification
                utilisateur.setLogin(login);
                utilisateur.setNomComplet(nomComplet);
                utilisateur.setEmail(email.isEmpty() ? null : email);
                utilisateur.setTelephone(telephone.isEmpty() ? null : telephone);
                utilisateur.setRole(role);
                utilisateur.setActif(actifCheckBox.isSelected());
                
                utilisateurController.updateUtilisateur(utilisateur);
                
                // Changer le mot de passe si fourni
                if (!password.isEmpty()) {
                    utilisateurController.changerMotDePasse(utilisateur.getId(), password);
                }
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
     * Indique si le dialogue a été confirmé.
     * @return true si confirmé
     */
    public boolean isConfirmed() {
        return confirmed;
    }
}
