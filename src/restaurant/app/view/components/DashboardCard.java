package restaurant.app.view.components;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Carte de tableau de bord moderne pour afficher des statistiques.
 * Design epure avec valeur et libelle.
 * 
 * @author Restaurant App
 * @version 1.0
 */
public class DashboardCard extends JPanel {
    
    private JLabel valueLabel;
    private JLabel titleLabel;
    private Color accentColor;
    
    /**
     * Types de cartes predefinis.
     */
    public enum CardType {
        PRIMARY(new Color(59, 130, 246)),
        SUCCESS(new Color(34, 197, 94)),
        WARNING(new Color(245, 158, 11)),
        DANGER(new Color(239, 68, 68)),
        INFO(new Color(6, 182, 212)),
        PURPLE(new Color(139, 92, 246));
        
        private final Color accent;
        
        CardType(Color accent) {
            this.accent = accent;
        }
    }
    
    /**
     * Constructeur complet.
     * @param title Le titre de la carte
     * @param value La valeur a afficher
     * @param icon Ignore - conserve pour compatibilite
     * @param type Le type de carte
     */
    public DashboardCard(String title, String value, String icon, CardType type) {
        this.accentColor = type.accent;
        initComponents(title, value, icon, type);
        initStyle(type);
    }
    
    /**
     * Constructeur simplifie.
     * @param title Le titre
     * @param value La valeur
     * @param type Le type
     */
    public DashboardCard(String title, String value, CardType type) {
        this(title, value, "", type);
    }
    
    /**
     * Initialise les composants.
     */
    private void initComponents(String title, String value, String icon, CardType type) {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 25, 20, 25));
        
        // Panel central avec titre et valeur
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        
        // Titre en haut
        titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Montserrat", Font.PLAIN, 13));
        titleLabel.setForeground(new Color(107, 114, 128));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Valeur grande
        valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Montserrat", Font.BOLD, 32));
        valueLabel.setForeground(type.accent);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        contentPanel.add(valueLabel);
        
        add(contentPanel, BorderLayout.CENTER);
    }
    
    /**
     * Initialise le style.
     */
    private void initStyle(CardType type) {
        setBackground(Color.WHITE);
        setOpaque(false);
        setPreferredSize(new Dimension(220, 110));
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Fond blanc avec coins arrondis
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
        
        // Bordure superieure coloree
        g2.setColor(accentColor);
        g2.fillRoundRect(0, 0, getWidth(), 4, 4, 4);
        
        // Ombre legere
        g2.setColor(new Color(0, 0, 0, 15));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
        
        g2.dispose();
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
     * Met a jour l'icone (ignore).
     * @param icon L'icone
     */
    public void setIcon(String icon) {
        // Ignore - pas d'icone
    }
    
    /**
     * Crée une carte pour les commandes du jour.
     * @param count Le nombre de commandes
     * @return La carte
     */
    public static DashboardCard createCommandesCard(int count) {
        return new DashboardCard("Commandes du jour", String.valueOf(count), "", CardType.PRIMARY);
    }
    
    /**
     * Crée une carte pour le chiffre d'affaires.
     * @param montant Le montant formaté
     * @return La carte
     */
    public static DashboardCard createCACard(String montant) {
        return new DashboardCard("Chiffre d'affaires", montant, "", CardType.SUCCESS);
    }
    
    /**
     * Crée une carte pour les alertes stock.
     * @param count Le nombre d'alertes
     * @return La carte
     */
    public static DashboardCard createStockAlertCard(int count) {
        CardType type = count > 0 ? CardType.WARNING : CardType.SUCCESS;
        return new DashboardCard("Alertes stock", String.valueOf(count), "", type);
    }
    
    /**
     * Crée une carte pour les produits en rupture.
     * @param count Le nombre de produits
     * @return La carte
     */
    public static DashboardCard createRuptureCard(int count) {
        CardType type = count > 0 ? CardType.DANGER : CardType.SUCCESS;
        return new DashboardCard("Produits en rupture", String.valueOf(count), "", type);
    }
}
