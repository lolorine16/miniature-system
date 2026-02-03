package restaurant.app.view.panels;

import java.awt.*;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import restaurant.app.controller.StatistiqueController;
import restaurant.app.dao.CommandeDAO;
import restaurant.app.dao.ProduitDAO;
import restaurant.app.model.entities.Commande;
import restaurant.app.model.entities.Produit;
import restaurant.app.util.FormatUtil;
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
    private final CommandeDAO commandeDAO;
    private final ProduitDAO produitDAO;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    // Cartes du dashboard
    private DashboardCard commandesCard;
    private DashboardCard caCard;
    private DashboardCard enAttenteCard;
    private DashboardCard ruptureCard;
    
    // Panel activité récente
    private JPanel activityContentPanel;
    // Panel ruptures de stock
    private JPanel rupturesContentPanel;
    
    /**
     * Constructeur.
     */
    public DashboardPanel() {
        this.statistiqueController = new StatistiqueController();
        this.commandeDAO = new CommandeDAO();
        this.produitDAO = new ProduitDAO();
        
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
        
        // Section avec deux colonnes: ruptures à gauche, activité récente à droite
        JPanel bottomSection = new JPanel(new GridLayout(1, 2, 20, 0));
        bottomSection.setOpaque(false);
        bottomSection.setAlignmentX(Component.LEFT_ALIGNMENT);
        bottomSection.setMaximumSize(new Dimension(Integer.MAX_VALUE, 350));
        
        // Ruptures de stock (gauche)
        JPanel rupturesSection = createRupturesSection();
        bottomSection.add(rupturesSection);
        
        // Activité récente (droite)
        JPanel activitySection = createActivitySection();
        bottomSection.add(activitySection);
        
        contentPanel.add(bottomSection);
        
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
        JButton refreshButton = new JButton("Actualiser");
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
        
        // Cartes (sans icônes emoji)
        commandesCard = new DashboardCard("Commandes du jour", "0", "", DashboardCard.CardType.PRIMARY);
        caCard = new DashboardCard("Chiffre d'affaires", "0 FCFA", "", DashboardCard.CardType.SUCCESS);
        enAttenteCard = new DashboardCard("En attente", "0", "", DashboardCard.CardType.WARNING);
        ruptureCard = new DashboardCard("Ruptures stock", "0", "", DashboardCard.CardType.DANGER);
        
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
        
        JButton cmdButton = createActionButton("Nouvelle commande", new Color(59, 130, 246));
        cmdButton.addActionListener(e -> navigateTo("commandes"));
        
        JButton stockButton = createActionButton("Entree de stock", new Color(34, 197, 94));
        stockButton.addActionListener(e -> navigateTo("stock"));
        
        JButton produitButton = createActionButton("Nouveau produit", new Color(139, 92, 246));
        produitButton.addActionListener(e -> navigateTo("produits"));
        
        buttonsPanel.add(cmdButton);
        buttonsPanel.add(stockButton);
        buttonsPanel.add(produitButton);
        
        section.add(buttonsPanel);
        
        return section;
    }
    
    /**
     * Navigue vers un panel specifique.
     * @param panelName Le nom du panel
     */
    private void navigateTo(String panelName) {
        // Remonter la hierarchie pour trouver MainFrame
        Component comp = this;
        while (comp != null) {
            if (comp instanceof MainFrame) {
                ((MainFrame) comp).showPanel(panelName);
                return;
            }
            comp = comp.getParent();
        }
        // Alternative: utiliser SwingUtilities
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window instanceof MainFrame) {
            ((MainFrame) window).showPanel(panelName);
        }
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
     * Cree la section des ruptures de stock.
     * @return Le panel
     */
    private JPanel createRupturesSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Ruptures de stock");
        titleLabel.setFont(new Font("Montserrat", Font.BOLD, 18));
        titleLabel.setForeground(new Color(31, 41, 55));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        section.add(titleLabel);
        section.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Panel des ruptures
        JPanel rupturesPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
            }
        };
        rupturesPanel.setOpaque(false);
        rupturesPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        rupturesPanel.setLayout(new BoxLayout(rupturesPanel, BoxLayout.Y_AXIS));
        rupturesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        rupturesContentPanel = rupturesPanel;
        
        section.add(rupturesPanel);
        
        return section;
    }
    
    /**
     * Charge les produits en rupture de stock.
     */
    private void loadRupturesStock() {
        if (rupturesContentPanel == null) return;
        
        SwingWorker<List<Produit>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Produit> doInBackground() throws Exception {
                return produitDAO.findOutOfStock();
            }
            
            @Override
            protected void done() {
                try {
                    List<Produit> produits = get();
                    rupturesContentPanel.removeAll();
                    
                    if (produits.isEmpty()) {
                        JLabel emptyLabel = new JLabel("Aucune rupture de stock");
                        emptyLabel.setFont(new Font("Montserrat", Font.PLAIN, 14));
                        emptyLabel.setForeground(new Color(34, 197, 94));
                        emptyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                        rupturesContentPanel.add(emptyLabel);
                    } else {
                        for (Produit p : produits) {
                            rupturesContentPanel.add(createRuptureItem(p));
                            rupturesContentPanel.add(Box.createRigidArea(new Dimension(0, 8)));
                        }
                    }
                    
                    rupturesContentPanel.revalidate();
                    rupturesContentPanel.repaint();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
    
    /**
     * Cree un item de rupture pour un produit.
     */
    private JPanel createRuptureItem(Produit produit) {
        JPanel item = new JPanel(new BorderLayout(10, 0));
        item.setOpaque(false);
        item.setAlignmentX(Component.LEFT_ALIGNMENT);
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        
        // Indicateur rouge
        JPanel indicator = new JPanel();
        indicator.setBackground(new Color(239, 68, 68));
        indicator.setPreferredSize(new Dimension(4, 0));
        item.add(indicator, BorderLayout.WEST);
        
        // Nom du produit
        JLabel nomLabel = new JLabel(produit.getNom());
        nomLabel.setFont(new Font("Montserrat", Font.BOLD, 13));
        nomLabel.setForeground(new Color(31, 41, 55));
        item.add(nomLabel, BorderLayout.CENTER);
        
        // Catégorie
        String categorie = produit.getCategorie() != null ? produit.getCategorie().getLibelle() : "";
        JLabel catLabel = new JLabel(categorie);
        catLabel.setFont(new Font("Montserrat", Font.PLAIN, 12));
        catLabel.setForeground(new Color(107, 114, 128));
        item.add(catLabel, BorderLayout.EAST);
        
        return item;
    }

    /**
     * Crée la section d'activité récente.
     * @return Le panel
     */
    private JPanel createActivitySection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Activite recente");
        titleLabel.setFont(new Font("Montserrat", Font.BOLD, 18));
        titleLabel.setForeground(new Color(31, 41, 55));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        section.add(titleLabel);
        section.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Panel des activités
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
        activityPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        activityPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Stocker le panel pour mise à jour
        activityContentPanel = activityPanel;
        
        section.add(activityPanel);
        
        return section;
    }
    
    /**
     * Charge les commandes récentes dans le panel d'activité.
     */
    private void loadRecentActivity() {
        if (activityContentPanel == null) return;
        
        SwingWorker<List<Commande>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Commande> doInBackground() throws Exception {
                return commandeDAO.findRecent(5);
            }
            
            @Override
            protected void done() {
                try {
                    List<Commande> commandes = get();
                    activityContentPanel.removeAll();
                    
                    if (commandes.isEmpty()) {
                        JLabel emptyLabel = new JLabel("Aucune activite recente");
                        emptyLabel.setFont(new Font("Montserrat", Font.PLAIN, 14));
                        emptyLabel.setForeground(new Color(156, 163, 175));
                        emptyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                        activityContentPanel.add(emptyLabel);
                    } else {
                        for (Commande cmd : commandes) {
                            activityContentPanel.add(createActivityItem(cmd));
                            activityContentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                        }
                    }
                    
                    activityContentPanel.revalidate();
                    activityContentPanel.repaint();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
    
    /**
     * Crée un item d'activité pour une commande.
     */
    private JPanel createActivityItem(Commande cmd) {
        JPanel item = new JPanel(new BorderLayout(10, 0));
        item.setOpaque(false);
        item.setAlignmentX(Component.LEFT_ALIGNMENT);
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        
        // Indicateur de couleur selon l'état
        Color stateColor = getStateColor(cmd.getEtat());
        JPanel indicator = new JPanel();
        indicator.setBackground(stateColor);
        indicator.setPreferredSize(new Dimension(4, 0));
        item.add(indicator, BorderLayout.WEST);
        
        // Infos commande
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setOpaque(false);
        
        String cmdText = "Commande #" + cmd.getId();
        if (cmd.getClientNom() != null && !cmd.getClientNom().isEmpty()) {
            cmdText += " - " + cmd.getClientNom();
        }
        JLabel cmdLabel = new JLabel(cmdText);
        cmdLabel.setFont(new Font("Montserrat", Font.BOLD, 13));
        cmdLabel.setForeground(new Color(31, 41, 55));
        
        JLabel detailLabel = new JLabel(cmd.getEtat().getLibelle() + " | " + 
            FormatUtil.formatCurrency(cmd.getTotal()));
        detailLabel.setFont(new Font("Montserrat", Font.PLAIN, 12));
        detailLabel.setForeground(new Color(107, 114, 128));
        
        infoPanel.add(cmdLabel, BorderLayout.NORTH);
        infoPanel.add(detailLabel, BorderLayout.SOUTH);
        item.add(infoPanel, BorderLayout.CENTER);
        
        // Date
        JLabel dateLabel = new JLabel(cmd.getDateCommande().format(dateFormatter));
        dateLabel.setFont(new Font("Montserrat", Font.PLAIN, 12));
        dateLabel.setForeground(new Color(156, 163, 175));
        item.add(dateLabel, BorderLayout.EAST);
        
        return item;
    }
    
    /**
     * Retourne la couleur selon l'état de la commande.
     */
    private Color getStateColor(restaurant.app.model.enums.EtatCommande etat) {
        switch (etat) {
            case EN_ATTENTE: return new Color(255, 193, 7);
            case EN_PREPARATION: return new Color(59, 130, 246);
            case PRETE: return new Color(34, 197, 94);
            case LIVREE: return new Color(16, 185, 129);
            case ANNULEE: return new Color(239, 68, 68);
            default: return new Color(156, 163, 175);
        }
    }
    
    /**
     * Charge les données.
     */
    private void loadData() {
        // Charger les statistiques
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
        
        // Charger les ruptures de stock
        loadRupturesStock();
        
        // Charger l'activité récente
        loadRecentActivity();
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
        caCard.setValue(FormatUtil.formatCurrency(ca != null ? ca : BigDecimal.ZERO));
        
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
