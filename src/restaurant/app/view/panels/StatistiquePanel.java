package restaurant.app.view.panels;

import java.awt.*;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import restaurant.app.controller.StatistiqueController;
import restaurant.app.util.FormatUtil;
import restaurant.app.view.MainFrame;

/**
 * Panel des statistiques.
 * 
 * @author Restaurant App
 * @version 1.0
 */
public class StatistiquePanel extends JPanel implements MainFrame.Refreshable {
    
    private final StatistiqueController statistiqueController;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    private JComboBox<String> periodCombo;
    private JPanel statsPanel;
    
    /**
     * Constructeur.
     */
    public StatistiquePanel() {
        this.statistiqueController = new StatistiqueController();
        
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
        
        // Contenu
        JScrollPane scrollPane = new JScrollPane(createContentPanel());
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(243, 244, 246));
        
        add(scrollPane, BorderLayout.CENTER);
    }
    
    /**
     * Crée le header.
     * @return Le panel
     */
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        // Titre
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Statistiques");
        titleLabel.setFont(new Font("Montserrat", Font.BOLD, 28));
        titleLabel.setForeground(new Color(31, 41, 55));
        
        JLabel subtitleLabel = new JLabel("Analyse de votre activité");
        subtitleLabel.setFont(new Font("Montserrat", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(107, 114, 128));
        
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        titlePanel.add(subtitleLabel);
        
        // Sélecteur de période
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        actionsPanel.setOpaque(false);
        
        periodCombo = new JComboBox<>(new String[]{
                "Aujourd'hui",
                "Cette semaine",
                "Ce mois",
                "Cette année"
        });
        periodCombo.setFont(new Font("Montserrat", Font.PLAIN, 13));
        periodCombo.setPreferredSize(new Dimension(180, 45));
        periodCombo.addActionListener(e -> loadData());
        
        JButton exportButton = new JButton("Exporter");
        exportButton.setFont(new Font("Montserrat", Font.PLAIN, 13));
        
        actionsPanel.add(periodCombo);
        actionsPanel.add(exportButton);
        
        header.add(titlePanel, BorderLayout.WEST);
        header.add(actionsPanel, BorderLayout.EAST);
        
        return header;
    }
    
    /**
     * Crée le panel de contenu.
     * @return Le panel
     */
    private JPanel createContentPanel() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        
        // KPIs
        statsPanel = new JPanel(new GridLayout(2, 3, 20, 20));
        statsPanel.setOpaque(false);
        statsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 280));
        statsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        content.add(statsPanel);
        content.add(Box.createRigidArea(new Dimension(0, 30)));
        
        // Graphique placeholder
        JPanel chartPanel = createChartPlaceholder();
        chartPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(chartPanel);
        
        return content;
    }
    
    /**
     * Crée une carte KPI.
     */
    private JPanel createKPICard(String title, String value, String subtitle, Color color) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(25, 25, 25, 25));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Montserrat", Font.PLAIN, 13));
        titleLabel.setForeground(new Color(107, 114, 128));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Montserrat", Font.BOLD, 32));
        valueLabel.setForeground(color);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("Montserrat", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(156, 163, 175));
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        card.add(titleLabel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(valueLabel);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(subtitleLabel);
        
        return card;
    }
    
    /**
     * Crée un placeholder pour le graphique.
     */
    private JPanel createChartPlaceholder() {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(25, 25, 25, 25));
        panel.setPreferredSize(new Dimension(0, 300));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        
        JLabel titleLabel = new JLabel("Évolution du chiffre d'affaires");
        titleLabel.setFont(new Font("Montserrat", Font.BOLD, 16));
        titleLabel.setForeground(new Color(31, 41, 55));
        
        // Placeholder graphique
        JPanel chartArea = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Grille
                g2.setColor(new Color(229, 231, 235));
                for (int i = 0; i < 5; i++) {
                    int y = getHeight() * i / 4;
                    g2.drawLine(0, y, getWidth(), y);
                }
                
                // Barres de démonstration
                int barWidth = 40;
                int spacing = 30;
                int[] values = {60, 80, 45, 90, 70, 85, 95};
                Color barColor = new Color(59, 130, 246);
                
                int startX = (getWidth() - (values.length * (barWidth + spacing))) / 2;
                for (int i = 0; i < values.length; i++) {
                    int x = startX + i * (barWidth + spacing);
                    int barHeight = (int) (getHeight() * 0.8 * values[i] / 100);
                    int y = getHeight() - barHeight - 30;
                    
                    g2.setColor(barColor);
                    g2.fillRoundRect(x, y, barWidth, barHeight, 5, 5);
                }
                
                g2.dispose();
            }
        };
        chartArea.setOpaque(false);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(chartArea, BorderLayout.CENTER);
        
        return panel;
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
                    updateStats(stats);
                } catch (Exception e) {
                    // Afficher des valeurs par défaut
                }
            }
        };
        worker.execute();
    }
    
    /**
     * Met à jour les statistiques.
     */
    private void updateStats(Map<String, Object> stats) {
        statsPanel.removeAll();
        
        // CA du jour
        BigDecimal caDuJour = (BigDecimal) stats.get("chiffreAffairesDuJour");
        statsPanel.add(createKPICard("Chiffre d'affaires du jour",
                FormatUtil.formatCurrency(caDuJour != null ? caDuJour : BigDecimal.ZERO),
                "Ventes du jour", new Color(34, 197, 94)));
        
        // CA du mois
        BigDecimal caMois = (BigDecimal) stats.get("chiffreAffairesMois");
        statsPanel.add(createKPICard("Chiffre d'affaires du mois",
                FormatUtil.formatCurrency(caMois != null ? caMois : BigDecimal.ZERO),
                "Depuis le 1er du mois", new Color(59, 130, 246)));
        
        // Commandes du jour
        Integer commandesDuJour = (Integer) stats.get("commandesDuJour");
        statsPanel.add(createKPICard("Commandes du jour",
                String.valueOf(commandesDuJour != null ? commandesDuJour : 0),
                "Nombre de commandes", new Color(139, 92, 246)));
        
        // Commandes en attente
        Integer enAttente = (Integer) stats.get("commandesEnAttente");
        statsPanel.add(createKPICard("En attente",
                String.valueOf(enAttente != null ? enAttente : 0),
                "À traiter", new Color(245, 158, 11)));
        
        // Total produits
        Integer totalProduits = (Integer) stats.get("totalProduits");
        statsPanel.add(createKPICard("Produits au catalogue",
                String.valueOf(totalProduits != null ? totalProduits : 0),
                "Produits actifs", new Color(6, 182, 212)));
        
        // Ruptures
        Integer ruptures = (Integer) stats.get("produitsEnRupture");
        statsPanel.add(createKPICard("En rupture",
                String.valueOf(ruptures != null ? ruptures : 0),
                "Produits à réapprovisionner", new Color(239, 68, 68)));
        
        statsPanel.revalidate();
        statsPanel.repaint();
    }
    
    @Override
    public void refresh() {
        loadData();
    }
}
