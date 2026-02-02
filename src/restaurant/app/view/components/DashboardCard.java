package restaurant.app.view.components;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Carte de tableau de bord moderne pour afficher des statistiques.
 * Design moderne avec icône, valeur et libellé.
 * 
 * @author Restaurant App
 * @version 1.0
 */
public class DashboardCard extends JPanel {
    
    private JLabel iconLabel;
    private JLabel valueLabel;
    private JLabel titleLabel;
    private Color accentColor;
    
    /**
     * Types de cartes prédéfinis.
     */
    public enum CardType {
        PRIMARY(new Color(59, 130, 246), new Color(239, 246, 255)),
        SUCCESS(new Color(34, 197, 94), new Color(240, 253, 244)),
        WARNING(new Color(245, 158, 11), new Color(255, 251, 235)),
        DANGER(new Color(239, 68, 68), new Color(254, 242, 242)),
        INFO(new Color(6, 182, 212), new Color(236, 254, 255)),
        PURPLE(new Color(139, 92, 246), new Color(245, 243, 255));
        
        private final Color accent;
        private final Color background;
        
        CardType(Color accent, Color background) {
            this.accent = accent;
            this.background = background;
        }
    }
    
    /**
     * Constructeur complet.
     * @param title Le titre de la carte
     * @param value La valeur à afficher
     * @param icon L'icône (emoji ou texte)
     * @param type Le type de carte
     */
    public DashboardCard(String title, String value, String icon, CardType type) {
        this.accentColor = type.accent;
        initComponents(title, value, icon, type);
        initStyle(type);
    }
    
    /**
     * Constructeur simplifié.
     * @param title Le titre
     * @param value La valeur
     * @param type Le type
     */
    public DashboardCard(String title, String value, CardType type) {
        this(title, value, getDefaultIcon(type), type);
    }
    
    /**
     * Retourne l'icône par défaut selon le type.
     * @param type Le type de carte
     * @return L'icône
     */
    private static String getDefaultIcon(CardType type) {
        switch (type) {
            case PRIMARY: return "📊";
            case SUCCESS: return "✅";
            case WARNING: return "⚠️";
            case DANGER: return "🔴";
            case INFO: return "ℹ️";
            case PURPLE: return "💜";
            default: return "📌";
        }
    }
    
    /**
     * Initialise les composants.
     */
    private void initComponents(String title, String value, String icon, CardType type) {
        setLayout(new BorderLayout(15, 0));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Panel gauche avec icône
        JPanel iconPanel = new JPanel();
        iconPanel.setOpaque(false);
        iconPanel.setPreferredSize(new Dimension(60, 60));
        iconPanel.setLayout(new GridBagLayout());
        
        iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconPanel.add(iconLabel);
        
        // Cercle de fond pour l'icône
        JPanel iconCircle = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(type.accent.brighter().brighter());
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        iconCircle.setOpaque(false);
        iconCircle.setPreferredSize(new Dimension(55, 55));
        iconCircle.setLayout(new GridBagLayout());
        iconCircle.add(iconLabel);
        
        // Panel central avec valeur et titre
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        
        valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Montserrat", Font.BOLD, 28));
        valueLabel.setForeground(new Color(31, 41, 55));
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Montserrat", Font.PLAIN, 14));
        titleLabel.setForeground(new Color(107, 114, 128));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        contentPanel.add(valueLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        contentPanel.add(titleLabel);
        
        add(iconCircle, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }
    
    /**
     * Initialise le style.
     */
    private void initStyle(CardType type) {
        setBackground(type.background);
        setOpaque(true);
        setPreferredSize(new Dimension(250, 120));
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Fond avec coins arrondis
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
        
        // Bordure gauche colorée
        g2.setColor(accentColor);
        g2.fillRoundRect(0, 0, 5, getHeight(), 5, 5);
        
        // Ombre légère
        g2.setColor(new Color(0, 0, 0, 10));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
        
        g2.dispose();
        
        super.paintComponent(g);
    }
    
    /**
     * Met à jour la valeur affichée.
     * @param value La nouvelle valeur
     */
    public void setValue(String value) {
        valueLabel.setText(value);
    }
    
    /**
     * Met à jour le titre.
     * @param title Le nouveau titre
     */
    public void setTitle(String title) {
        titleLabel.setText(title);
    }
    
    /**
     * Met à jour l'icône.
     * @param icon La nouvelle icône
     */
    public void setIcon(String icon) {
        iconLabel.setText(icon);
    }
    
    /**
     * Crée une carte pour les commandes du jour.
     * @param count Le nombre de commandes
     * @return La carte
     */
    public static DashboardCard createCommandesCard(int count) {
        return new DashboardCard("Commandes du jour", String.valueOf(count), "🛒", CardType.PRIMARY);
    }
    
    /**
     * Crée une carte pour le chiffre d'affaires.
     * @param montant Le montant formaté
     * @return La carte
     */
    public static DashboardCard createCACard(String montant) {
        return new DashboardCard("Chiffre d'affaires", montant, "💰", CardType.SUCCESS);
    }
    
    /**
     * Crée une carte pour les alertes stock.
     * @param count Le nombre d'alertes
     * @return La carte
     */
    public static DashboardCard createStockAlertCard(int count) {
        CardType type = count > 0 ? CardType.WARNING : CardType.SUCCESS;
        String icon = count > 0 ? "⚠️" : "✅";
        return new DashboardCard("Alertes stock", String.valueOf(count), icon, type);
    }
    
    /**
     * Crée une carte pour les produits en rupture.
     * @param count Le nombre de produits
     * @return La carte
     */
    public static DashboardCard createRuptureCard(int count) {
        CardType type = count > 0 ? CardType.DANGER : CardType.SUCCESS;
        String icon = count > 0 ? "🚫" : "✅";
        return new DashboardCard("Produits en rupture", String.valueOf(count), icon, type);
    }
}
