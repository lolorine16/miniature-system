/*
 * Application de gestion de restaurant
 * Développée avec Java Swing, FlatLaf et Montserrat
 */
package restaurant.app;

import com.formdev.flatlaf.FlatLightLaf;
import restaurant.app.util.DatabaseConnection;
import restaurant.app.util.IconUtil;
import restaurant.app.view.LoginFrame;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Point d'entrée de l'application Restaurant Manager
 * 
 * @author reen-lo
 */
public class Main {
    
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    
    // Couleurs de l'application
    public static final Color PRIMARY_COLOR = new Color(230, 126, 34);     // Orange
    public static final Color PRIMARY_DARK = new Color(211, 84, 0);        // Orange foncé
    public static final Color SECONDARY_COLOR = new Color(52, 73, 94);     // Gris foncé
    public static final Color SUCCESS_COLOR = new Color(39, 174, 96);      // Vert
    public static final Color DANGER_COLOR = new Color(231, 76, 60);       // Rouge
    public static final Color WARNING_COLOR = new Color(241, 196, 15);     // Jaune
    public static final Color BACKGROUND_COLOR = new Color(245, 246, 250); // Gris clair
    public static final Color CARD_COLOR = Color.WHITE;
    public static final Color TEXT_COLOR = new Color(44, 62, 80);          // Texte foncé
    public static final Color TEXT_SECONDARY = new Color(127, 140, 141);   // Texte gris
    
    // Polices Montserrat
    public static Font FONT_REGULAR;
    public static Font FONT_BOLD;
    public static Font FONT_MEDIUM;
    public static Font FONT_SEMIBOLD;
    public static Font FONT_LIGHT;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Configuration du Look and Feel
        try {
            // Charger les polices Montserrat
            loadMontserratFonts();
            
            // Configurer FlatLaf
            FlatLightLaf.setup();
            
            // Configurer les propriétés UI globales
            configureUIDefaults();
            
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la configuration du Look and Feel", ex);
            // Fallback sur le Look and Feel système
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Impossible de charger le Look and Feel système", e);
            }
        }
        
        // Lancer l'application sur l'EDT
        SwingUtilities.invokeLater(() -> {
            showSplashScreen();
        });
    }
    
    /**
     * Charge les polices Montserrat depuis les ressources
     */
    private static void loadMontserratFonts() {
        try {
            // Charger les différentes variantes de Montserrat
            FONT_REGULAR = loadFont("/fonts/Montserrat-Regular.ttf");
            FONT_BOLD = loadFont("/fonts/Montserrat-Bold.ttf");
            FONT_MEDIUM = loadFont("/fonts/Montserrat-Medium.ttf");
            FONT_SEMIBOLD = loadFont("/fonts/Montserrat-SemiBold.ttf");
            FONT_LIGHT = loadFont("/fonts/Montserrat-Light.ttf");
            
            // Si les polices ne sont pas trouvées, utiliser les polices système
            if (FONT_REGULAR == null) {
                LOGGER.warning("Police Montserrat non trouvée, utilisation de la police par défaut");
                FONT_REGULAR = new Font("SansSerif", Font.PLAIN, 14);
                FONT_BOLD = new Font("SansSerif", Font.BOLD, 14);
                FONT_MEDIUM = new Font("SansSerif", Font.PLAIN, 14);
                FONT_SEMIBOLD = new Font("SansSerif", Font.BOLD, 14);
                FONT_LIGHT = new Font("SansSerif", Font.PLAIN, 14);
            }
            
            // Enregistrer les polices dans le GraphicsEnvironment
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            if (FONT_REGULAR != null) {
                ge.registerFont(FONT_REGULAR);
                ge.registerFont(FONT_BOLD);
                ge.registerFont(FONT_MEDIUM);
                ge.registerFont(FONT_SEMIBOLD);
                ge.registerFont(FONT_LIGHT);
            }
            
            LOGGER.info("Polices Montserrat chargées avec succès");
            
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Erreur lors du chargement des polices", e);
            // Utiliser les polices par défaut
            FONT_REGULAR = new Font("SansSerif", Font.PLAIN, 14);
            FONT_BOLD = new Font("SansSerif", Font.BOLD, 14);
            FONT_MEDIUM = new Font("SansSerif", Font.PLAIN, 14);
            FONT_SEMIBOLD = new Font("SansSerif", Font.BOLD, 14);
            FONT_LIGHT = new Font("SansSerif", Font.PLAIN, 14);
        }
    }
    
    /**
     * Charge une police depuis les ressources
     */
    private static Font loadFont(String path) {
        try {
            InputStream is = Main.class.getResourceAsStream(path);
            if (is != null) {
                Font font = Font.createFont(Font.TRUETYPE_FONT, is);
                is.close();
                return font.deriveFont(14f);
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Impossible de charger la police: " + path, e);
        }
        return null;
    }
    
    /**
     * Configure les propriétés UI par défaut
     */
    private static void configureUIDefaults() {
        UIManager.put("defaultFont", FONT_REGULAR != null ? FONT_REGULAR : new Font("SansSerif", Font.PLAIN, 14));
        
        // Couleurs FlatLaf
        UIManager.put("@accentColor", PRIMARY_COLOR);
        UIManager.put("Component.focusColor", PRIMARY_COLOR);
        UIManager.put("Component.focusedBorderColor", PRIMARY_COLOR);
        
        // Boutons
        UIManager.put("Button.arc", 10);
        UIManager.put("Component.arc", 10);
        UIManager.put("ProgressBar.arc", 10);
        UIManager.put("TextComponent.arc", 10);
        
        // Fond
        UIManager.put("Panel.background", BACKGROUND_COLOR);
        UIManager.put("RootPane.background", BACKGROUND_COLOR);
        
        // Table
        UIManager.put("Table.showHorizontalLines", true);
        UIManager.put("Table.showVerticalLines", false);
        UIManager.put("Table.intercellSpacing", new Dimension(0, 1));
        UIManager.put("Table.cellNoFocusBorder", BorderFactory.createEmptyBorder(8, 10, 8, 10));
        UIManager.put("Table.focusCellHighlightBorder", BorderFactory.createEmptyBorder(8, 10, 8, 10));
        UIManager.put("TableHeader.font", FONT_SEMIBOLD != null ? FONT_SEMIBOLD.deriveFont(13f) : new Font("SansSerif", Font.BOLD, 13));
        
        // ScrollBar
        UIManager.put("ScrollBar.width", 10);
        UIManager.put("ScrollBar.thumbArc", 10);
        UIManager.put("ScrollBar.thumbInsets", new Insets(2, 2, 2, 2));
        
        // TabbedPane
        UIManager.put("TabbedPane.selectedBackground", Color.WHITE);
        UIManager.put("TabbedPane.focusColor", PRIMARY_COLOR);
        
        // TextField
        UIManager.put("TextField.placeholderForeground", TEXT_SECONDARY);
        
        // ComboBox
        UIManager.put("ComboBox.padding", new Insets(8, 10, 8, 10));
        
        // Spinner
        UIManager.put("Spinner.padding", new Insets(8, 10, 8, 10));
    }
    
    /**
     * Affiche l'écran de démarrage
     */
    private static void showSplashScreen() {
        // Créer la fenêtre splash
        JWindow splash = new JWindow();
        splash.setSize(500, 350);
        splash.setLocationRelativeTo(null);
        
        // Panel principal du splash
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Dégradé de fond
                GradientPaint gradient = new GradientPaint(
                    0, 0, PRIMARY_COLOR,
                    getWidth(), getHeight(), PRIMARY_DARK
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        
        // Icône restaurant (image PNG)
        JLabel iconLabel = new JLabel(IconUtil.getUtensilsIcon(72), SwingConstants.CENTER);
        iconLabel.setForeground(Color.WHITE);
        
        // Titre
        JLabel titleLabel = new JLabel("Restaurant Manager", SwingConstants.CENTER);
        titleLabel.setFont(FONT_BOLD != null ? FONT_BOLD.deriveFont(32f) : new Font("SansSerif", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        
        // Sous-titre
        JLabel subtitleLabel = new JLabel("Système de gestion de restaurant", SwingConstants.CENTER);
        subtitleLabel.setFont(FONT_REGULAR != null ? FONT_REGULAR.deriveFont(14f) : new Font("SansSerif", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(255, 255, 255, 200));
        
        // Barre de progression
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(false);
        progressBar.setForeground(Color.WHITE);
        progressBar.setBackground(new Color(255, 255, 255, 50));
        progressBar.setBorderPainted(false);
        progressBar.setPreferredSize(new Dimension(300, 6));
        
        // Message de chargement
        JLabel loadingLabel = new JLabel("Chargement...", SwingConstants.CENTER);
        loadingLabel.setFont(FONT_LIGHT != null ? FONT_LIGHT.deriveFont(12f) : new Font("SansSerif", Font.PLAIN, 12));
        loadingLabel.setForeground(new Color(255, 255, 255, 180));
        
        // Organisation
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        progressBar.setAlignmentX(Component.CENTER_ALIGNMENT);
        loadingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        centerPanel.add(iconLabel);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(titleLabel);
        centerPanel.add(Box.createVerticalStrut(5));
        centerPanel.add(subtitleLabel);
        centerPanel.add(Box.createVerticalStrut(40));
        centerPanel.add(progressBar);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(loadingLabel);
        
        panel.add(centerPanel, BorderLayout.CENTER);
        
        // Version
        JLabel versionLabel = new JLabel("Version 1.0.0", SwingConstants.CENTER);
        versionLabel.setFont(FONT_LIGHT != null ? FONT_LIGHT.deriveFont(10f) : new Font("SansSerif", Font.PLAIN, 10));
        versionLabel.setForeground(new Color(255, 255, 255, 150));
        panel.add(versionLabel, BorderLayout.SOUTH);
        
        splash.setContentPane(panel);
        splash.setBackground(new Color(0, 0, 0, 0));
        
        // Effet de transparence sur la fenêtre
        splash.setShape(new java.awt.geom.RoundRectangle2D.Double(0, 0, 500, 350, 20, 20));
        
        splash.setVisible(true);
        
        // Animation de chargement avec vérification de la base de données
        Timer timer = new Timer(30, null);
        final int[] progress = {0};
        final String[] messages = {
            "Initialisation...",
            "Chargement des polices...",
            "Connexion à la base de données...",
            "Vérification des données...",
            "Préparation de l'interface...",
            "Démarrage..."
        };
        
        timer.addActionListener(e -> {
            progress[0] += 2;
            progressBar.setValue(progress[0]);
            
            // Mettre à jour le message
            int messageIndex = progress[0] / 20;
            if (messageIndex < messages.length) {
                loadingLabel.setText(messages[messageIndex]);
            }
            
            // Vérifier la connexion à la base de données au bon moment
            if (progress[0] == 40) {
                new Thread(() -> {
                    try {
                        Connection conn = DatabaseConnection.getInstance().getConnection();
                        if (conn != null) {
                            LOGGER.info("Connexion à la base de données réussie");
                        }
                    } catch (Exception ex) {
                        LOGGER.log(Level.WARNING, "Erreur de connexion à la base de données", ex);
                    }
                }).start();
            }
            
            if (progress[0] >= 100) {
                timer.stop();
                splash.dispose();
                
                // Afficher la fenêtre de connexion
                SwingUtilities.invokeLater(() -> {
                    LoginFrame loginFrame = new LoginFrame();
                    loginFrame.setVisible(true);
                });
            }
        });
        
        timer.start();
    }
    
    /**
     * Méthode utilitaire pour obtenir la police Montserrat avec une taille spécifique
     */
    public static Font getMontserrat(int style, float size) {
        Font baseFont;
        switch (style) {
            case Font.BOLD:
                baseFont = FONT_BOLD;
                break;
            case Font.ITALIC:
                baseFont = FONT_MEDIUM;
                break;
            default:
                baseFont = FONT_REGULAR;
        }
        
        if (baseFont == null) {
            return new Font("SansSerif", style, (int) size);
        }
        
        return baseFont.deriveFont(size);
    }
    
    /**
     * Obtenir la police Regular
     */
    public static Font getMontserratRegular(float size) {
        return FONT_REGULAR != null ? FONT_REGULAR.deriveFont(size) : new Font("SansSerif", Font.PLAIN, (int) size);
    }
    
    /**
     * Obtenir la police Bold
     */
    public static Font getMontserratBold(float size) {
        return FONT_BOLD != null ? FONT_BOLD.deriveFont(size) : new Font("SansSerif", Font.BOLD, (int) size);
    }
    
    /**
     * Obtenir la police Medium
     */
    public static Font getMontserratMedium(float size) {
        return FONT_MEDIUM != null ? FONT_MEDIUM.deriveFont(size) : new Font("SansSerif", Font.PLAIN, (int) size);
    }
    
    /**
     * Obtenir la police SemiBold
     */
    public static Font getMontserratSemiBold(float size) {
        return FONT_SEMIBOLD != null ? FONT_SEMIBOLD.deriveFont(size) : new Font("SansSerif", Font.BOLD, (int) size);
    }
    
    /**
     * Obtenir la police Light
     */
    public static Font getMontserratLight(float size) {
        return FONT_LIGHT != null ? FONT_LIGHT.deriveFont(size) : new Font("SansSerif", Font.PLAIN, (int) size);
    }
}
