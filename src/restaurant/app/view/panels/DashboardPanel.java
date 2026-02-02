package restaurant.app.view.panels;

import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import restaurant.app.controller.StatistiqueController;
import restaurant.app.view.MainFrame;
import restaurant.app.view.components.DashboardCard;

/**
 * Panel du tableau de bord.
 * Affiche les statistiques principales et les indicateurs clés.
 * 
 * @author Restaurant App
 * @version 1.0
 */
public class DashboardPanel extends JPanel implements MainFrame.Refreshable {
    
    private final StatistiqueController statistiqueController;
    private final NumberFormat currencyFormat;
    
    // Cartes du dashboard
    private DashboardCard commandesCard;
    private DashboardCard caCard;
    private DashboardCard enAttenteCard;
    private DashboardCard ruptureCard;
    
    /**
     * Constructeur.
     */
    public DashboardPanel() {
        this.statistiqueController = new StatistiqueController();
        this.currencyFormat = NumberFormat.getCurrencyInstance(Locale.FRANCE);
        
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
        
        // Contenu principal
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        
        // Section des cartes statistiques
        JPanel cardsSection = createCardsSection();
        contentPanel.add(cardsSection);
        
        contentPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        
        // Section des actions rapides
        JPanel actionsSection = createActionsSection();
        contentPanel.add(actionsSection);
        
        contentPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        
        // Section activité récente
        JPanel activitySection = createActivitySection();
        contentPanel.add(activitySection);
        
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(243, 244, 246));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        add(scrollPane, BorderLayout.CENTER);
    }
    
    /**
     * Crée le header.
     * @return Le panel
     */
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 30, 0));
        
        JLabel titleLabel = new JLabel("Tableau de bord");
        titleLabel.setFont(new Font("Montserrat", Font.BOLD, 28));
        titleLabel.setForeground(new Color(31, 41, 55));
        
        JLabel subtitleLabel = new JLabel("Vue d'ensemble de votre activité");
        subtitleLabel.setFont(new Font("Montserrat", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(107, 114, 128));
        
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        titlePanel.add(subtitleLabel);
        
        // Bouton rafraîchir
        JButton refreshButton = new JButton("🔄 Actualiser");
        refreshButton.setFont(new Font("Montserrat", Font.PLAIN, 13));
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshButton.addActionListener(e -> refresh());
        
        header.add(titlePanel, BorderLayout.WEST);
        header.add(refreshButton, BorderLayout.EAST);
        
        return header;
    }
    
    /**
     * Crée la section des cartes statistiques.
     * @return Le panel
     */
    private JPanel createCardsSection() {
        JPanel section = new JPanel(new GridLayout(1, 4, 20, 0));
        section.setOpaque(false);
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        
        // Cartes
        commandesCard = new DashboardCard("Commandes du jour", "0", "🛒", DashboardCard.CardType.PRIMARY);
        caCard = new DashboardCard("Chiffre d'affaires", "0 €", "💰", DashboardCard.CardType.SUCCESS);
        enAttenteCard = new DashboardCard("En attente", "0", "⏳", DashboardCard.CardType.WARNING);
        ruptureCard = new DashboardCard("Ruptures stock", "0", "📦", DashboardCard.CardType.DANGER);
        
        section.add(commandesCard);
        section.add(caCard);
        section.add(enAttenteCard);
        section.add(ruptureCard);
        
        return section;
    }
    
    /**
     * Crée la section des actions rapides.
     * @return Le panel
     */
    private JPanel createActionsSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Actions rapides");
        titleLabel.setFont(new Font("Montserrat", Font.BOLD, 18));
        titleLabel.setForeground(new Color(31, 41, 55));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        section.add(titleLabel);
        section.add(Box.createRigidArea(new Dimension(0, 15)));
        
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        buttonsPanel.setOpaque(false);
        buttonsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        buttonsPanel.add(createActionButton("➕ Nouvelle commande", new Color(59, 130, 246)));
        buttonsPanel.add(createActionButton("📦 Entrée de stock", new Color(34, 197, 94)));
        buttonsPanel.add(createActionButton("🍔 Nouveau produit", new Color(139, 92, 246)));
        
        section.add(buttonsPanel);
        
        return section;
    }
    
    /**
     * Crée un bouton d'action rapide.
     * @param text Le texte
     * @param color La couleur
     * @return Le bouton
     */
    private JButton createActionButton(String text, Color color) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setFont(new Font("Montserrat", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(200, 45));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
    
    /**
     * Crée la section d'activité récente.
     * @return Le panel
     */
    private JPanel createActivitySection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Activité récente");
        titleLabel.setFont(new Font("Montserrat", Font.BOLD, 18));
        titleLabel.setForeground(new Color(31, 41, 55));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        section.add(titleLabel);
        section.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Panel des activités (placeholder)
        JPanel activityPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
            }
        };
        activityPanel.setOpaque(false);
        activityPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        activityPanel.setLayout(new BoxLayout(activityPanel, BoxLayout.Y_AXIS));
        activityPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        activityPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel emptyLabel = new JLabel("Aucune activité récente");
        emptyLabel.setFont(new Font("Montserrat", Font.PLAIN, 14));
        emptyLabel.setForeground(new Color(156, 163, 175));
        emptyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        activityPanel.add(emptyLabel);
        section.add(activityPanel);
        
        return section;
    }
    
    /**
     * Charge les données.
     */
    private void loadData() {
        SwingWorker<Map<String, Object>, Void> worker = new SwingWorker<>() {
            @Override
            protected Map<String, Object> doInBackground() throws Exception {
                return statistiqueController.getStatistiquesDashboard();
            }
            
            @Override
            protected void done() {
                try {
                    Map<String, Object> stats = get();
                    updateCards(stats);
                } catch (Exception e) {
                    // Afficher des valeurs par défaut en cas d'erreur
                    commandesCard.setValue("--");
                    caCard.setValue("--");
                    enAttenteCard.setValue("--");
                    ruptureCard.setValue("--");
                }
            }
        };
        worker.execute();
    }
    
    /**
     * Met à jour les cartes avec les statistiques.
     * @param stats Les statistiques
     */
    private void updateCards(Map<String, Object> stats) {
        // Commandes du jour
        Integer commandesDuJour = (Integer) stats.get("commandesDuJour");
        commandesCard.setValue(String.valueOf(commandesDuJour != null ? commandesDuJour : 0));
        
        // Chiffre d'affaires
        BigDecimal ca = (BigDecimal) stats.get("chiffreAffairesDuJour");
        caCard.setValue(currencyFormat.format(ca != null ? ca : BigDecimal.ZERO));
        
        // En attente
        Integer enAttente = (Integer) stats.get("commandesEnAttente");
        enAttenteCard.setValue(String.valueOf(enAttente != null ? enAttente : 0));
        
        // Ruptures
        Integer ruptures = (Integer) stats.get("produitsEnRupture");
        ruptureCard.setValue(String.valueOf(ruptures != null ? ruptures : 0));
    }
    
    @Override
    public void refresh() {
        loadData();
    }
}
