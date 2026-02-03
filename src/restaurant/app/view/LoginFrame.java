package restaurant.app.view;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import restaurant.app.controller.UtilisateurController;
import restaurant.app.model.entities.Utilisateur;
import restaurant.app.util.IconUtil;
import restaurant.app.view.components.ModernButton;
import restaurant.app.view.components.ModernPasswordField;
import restaurant.app.view.components.ModernTextField;

/**
 * Écran de connexion de l'application.
 * Design moderne avec Montserrat.
 * 
 * @author Restaurant App
 * @version 1.0
 */
public class LoginFrame extends JFrame {
    
    // Composants
    private ModernTextField loginField;
    private ModernPasswordField passwordField;
    private ModernButton loginButton;
    private JLabel errorLabel;
    private JLabel loadingLabel;
    
    // Contrôleur
    private final UtilisateurController utilisateurController;
    
    /**
     * Constructeur.
     */
    public LoginFrame() {
        this.utilisateurController = new UtilisateurController();
        initFrame();
        initComponents();
        centerOnScreen();
    }
    
    /**
     * Initialise le frame.
     */
    private void initFrame() {
        setTitle("Restaurant App - Connexion");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(450, 550);
    }
    
    /**
     * Initialise les composants.
     */
    private void initComponents() {
        // Panel principal avec dégradé
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Dégradé de fond
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(59, 130, 246),
                    0, getHeight(), new Color(37, 99, 235)
                );
                g2.setPaint(gradient);
                g2.fillRect(0, 0, getWidth(), getHeight());
                
                g2.dispose();
            }
        };
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        // Panel de la carte de connexion
        JPanel cardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Ombre
                g2.setColor(new Color(0, 0, 0, 30));
                g2.fillRoundRect(5, 5, getWidth() - 5, getHeight() - 5, 20, 20);
                
                // Fond blanc
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 5, getHeight() - 5, 20, 20);
                
                g2.dispose();
            }
        };
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setOpaque(false);
        cardPanel.setBorder(new EmptyBorder(40, 40, 40, 40));
        cardPanel.setPreferredSize(new Dimension(380, 450));
        
        // Logo / Icône
        JLabel iconLabel = new JLabel();
        ImageIcon utensilsIcon = IconUtil.getUtensilsIcon(64);
        if (utensilsIcon != null) {
            iconLabel.setIcon(utensilsIcon);
        }
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Titre
        JLabel titleLabel = new JLabel("Restaurant App");
        titleLabel.setFont(new Font("Montserrat", Font.BOLD, 24));
        titleLabel.setForeground(new Color(31, 41, 55));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Sous-titre
        JLabel subtitleLabel = new JLabel("Connectez-vous à votre compte");
        subtitleLabel.setFont(new Font("Montserrat", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(107, 114, 128));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Champ login
        JPanel loginPanel = createFieldPanel("Identifiant");
        loginField = new ModernTextField("Entrez votre identifiant");
        loginField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        loginPanel.add(loginField);
        
        // Champ mot de passe
        JPanel passwordPanel = createFieldPanel("Mot de passe");
        passwordField = new ModernPasswordField("Entrez votre mot de passe");
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        passwordPanel.add(passwordField);
        
        // Message d'erreur
        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("Montserrat", Font.PLAIN, 12));
        errorLabel.setForeground(new Color(239, 68, 68));
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Label de chargement
        loadingLabel = new JLabel("Connexion en cours...");
        loadingLabel.setFont(new Font("Montserrat", Font.ITALIC, 12));
        loadingLabel.setForeground(new Color(107, 114, 128));
        loadingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        loadingLabel.setVisible(false);
        
        // Bouton connexion
        loginButton = new ModernButton("Se connecter", ModernButton.ButtonType.PRIMARY);
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.addActionListener(e -> performLogin());
        
        // Action sur Enter dans les champs
        loginField.addActionListener(e -> passwordField.requestFocus());
        // Note: Pour passwordField, on doit utiliser une approche différente
        
        // Assemblage
        cardPanel.add(iconLabel);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        cardPanel.add(titleLabel);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        cardPanel.add(subtitleLabel);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        cardPanel.add(loginPanel);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        cardPanel.add(passwordPanel);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        cardPanel.add(errorLabel);
        cardPanel.add(loadingLabel);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        cardPanel.add(loginButton);
        
        mainPanel.add(cardPanel);
        setContentPane(mainPanel);
        
        // Focus sur le champ login au démarrage
        SwingUtilities.invokeLater(() -> loginField.requestFocus());
    }
    
    /**
     * Crée un panel pour un champ avec son label.
     * @param labelText Le texte du label
     * @return Le panel
     */
    private JPanel createFieldPanel(String labelText) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 75));
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Montserrat", Font.PLAIN, 13));
        label.setForeground(new Color(55, 65, 81));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        
        return panel;
    }
    
    /**
     * Effectue la connexion.
     */
    private void performLogin() {
        String login = loginField.getText().trim();
        String password = passwordField.getPasswordText();
        
        // Validation basique
        if (login.isEmpty()) {
            showError("Veuillez entrer votre identifiant");
            loginField.setError(true);
            loginField.requestFocus();
            return;
        }
        
        if (password.isEmpty()) {
            showError("Veuillez entrer votre mot de passe");
            passwordField.setError(true);
            passwordField.requestFocus();
            return;
        }
        
        // Reset des erreurs
        loginField.setError(false);
        passwordField.setError(false);
        
        // Afficher le chargement
        setLoading(true);
        
        // Exécuter l'authentification dans un thread séparé
        SwingWorker<Utilisateur, Void> worker = new SwingWorker<>() {
            @Override
            protected Utilisateur doInBackground() throws Exception {
                return utilisateurController.authentifier(login, password);
            }
            
            @Override
            protected void done() {
                setLoading(false);
                try {
                    Utilisateur utilisateur = get();
                    onLoginSuccess(utilisateur);
                } catch (Exception e) {
                    String message = e.getCause() != null ? 
                            e.getCause().getMessage() : e.getMessage();
                    showError(message);
                }
            }
        };
        worker.execute();
    }
    
    /**
     * Appelé en cas de succès de connexion.
     * @param utilisateur L'utilisateur connecté
     */
    private void onLoginSuccess(Utilisateur utilisateur) {
        // Fermer la fenêtre de login
        dispose();
        
        // Ouvrir la fenêtre principale
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame(utilisateur);
            mainFrame.setVisible(true);
        });
    }
    
    /**
     * Affiche un message d'erreur.
     * @param message Le message
     */
    private void showError(String message) {
        errorLabel.setText(message);
    }
    
    /**
     * Efface le message d'erreur.
     */
    private void clearError() {
        errorLabel.setText(" ");
    }
    
    /**
     * Active/désactive le mode chargement.
     * @param loading true pour activer
     */
    private void setLoading(boolean loading) {
        loginButton.setEnabled(!loading);
        loginField.setEnabled(!loading);
        passwordField.setEnabled(!loading);
        loadingLabel.setVisible(loading);
        if (loading) {
            clearError();
        }
    }
    
    /**
     * Centre la fenêtre sur l'écran.
     */
    private void centerOnScreen() {
        setLocationRelativeTo(null);
    }
}
