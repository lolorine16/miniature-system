package restaurant.app.view;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import restaurant.app.controller.UtilisateurController;
import restaurant.app.model.entities.Utilisateur;
import restaurant.app.model.enums.RoleUtilisateur;
import restaurant.app.util.IconUtil;
import restaurant.app.view.panels.*;

/**
 * Fenêtre principale de l'application.
 * Contient la sidebar et le contenu principal.
 * 
 * @author Restaurant App
 * @version 1.0
 */
public class MainFrame extends JFrame {
    
    // Utilisateur connecté
    private final Utilisateur utilisateur;
    
    // Composants principaux
    private JPanel sidebarPanel;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    
    // Items du menu
    private Map<String, JPanel> menuItems;
    private JPanel selectedMenuItem;
    
    // Couleurs
    private static final Color SIDEBAR_BG = new Color(31, 41, 55);
    private static final Color SIDEBAR_HOVER = new Color(55, 65, 81);
    private static final Color SIDEBAR_SELECTED = new Color(59, 130, 246);
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Color TEXT_MUTED = new Color(156, 163, 175);
    
    // Noms des panels
    private static final String DASHBOARD = "dashboard";
    private static final String CATEGORIES = "categories";
    private static final String PRODUITS = "produits";
    private static final String STOCK = "stock";
    private static final String COMMANDES = "commandes";
    private static final String STATISTIQUES = "statistiques";
    private static final String UTILISATEURS = "utilisateurs";
    
    /**
     * Constructeur.
     * @param utilisateur L'utilisateur connecté
     */
    public MainFrame(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
        this.menuItems = new HashMap<>();
        initFrame();
        initComponents();
        selectMenuItem(DASHBOARD);
        centerOnScreen();
    }
    
    /**
     * Initialise le frame.
     */
    private void initFrame() {
        setTitle("Restaurant App - " + utilisateur.getNomComplet());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1200, 800));
        setSize(1400, 900);
    }
    
    /**
     * Initialise les composants.
     */
    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(243, 244, 246));
        
        // Sidebar
        sidebarPanel = createSidebar();
        mainPanel.add(sidebarPanel, BorderLayout.WEST);
        
        // Contenu principal
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(new Color(243, 244, 246));
        
        // Ajouter les panels
        contentPanel.add(new DashboardPanel(), DASHBOARD);
        contentPanel.add(new CategoriePanel(), CATEGORIES);
        contentPanel.add(new ProduitPanel(), PRODUITS);
        contentPanel.add(new StockPanel(), STOCK);
        contentPanel.add(new CommandePanel(), COMMANDES);
        contentPanel.add(new StatistiquePanel(), STATISTIQUES);
        
        // Panel utilisateurs (admin uniquement)
        if (utilisateur.getRole() == RoleUtilisateur.ADMIN) {
            contentPanel.add(new UtilisateurPanel(), UTILISATEURS);
        }
        
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        setContentPane(mainPanel);
    }
    
    /**
     * Crée la sidebar.
     * @return Le panel sidebar
     */
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(260, 0));
        sidebar.setBorder(new EmptyBorder(0, 0, 0, 0));
        
        // En-tête avec logo
        JPanel headerPanel = createSidebarHeader();
        sidebar.add(headerPanel);
        
        // Séparateur
        sidebar.add(createSeparator());
        
        // Menu principal
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(SIDEBAR_BG);
        menuPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        // Items du menu (sans emojis)
        menuPanel.add(createMenuItem(DASHBOARD, "", "Tableau de bord"));
        menuPanel.add(createMenuItem(COMMANDES, "", "Commandes"));
        
        // Séparateur gestion
        menuPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        menuPanel.add(createMenuSection("GESTION"));
        
        menuPanel.add(createMenuItem(CATEGORIES, "", "Catégories"));
        menuPanel.add(createMenuItem(PRODUITS, "", "Produits"));
        menuPanel.add(createMenuItem(STOCK, "", "Stock"));
        
        // Séparateur rapports (admin)
        if (utilisateur.getRole() == RoleUtilisateur.ADMIN) {
            menuPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            menuPanel.add(createMenuSection("ADMINISTRATION"));
            
            menuPanel.add(createMenuItem(STATISTIQUES, "", "Statistiques"));
            menuPanel.add(createMenuItem(UTILISATEURS, "", "Utilisateurs"));
        }
        
        sidebar.add(menuPanel);
        
        // Espace flexible
        sidebar.add(Box.createVerticalGlue());
        
        // Footer avec info utilisateur
        JPanel footerPanel = createSidebarFooter();
        sidebar.add(footerPanel);
        
        return sidebar;
    }
    
    /**
     * Crée l'en-tête de la sidebar.
     * @return Le panel
     */
    private JPanel createSidebarHeader() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(SIDEBAR_BG);
        header.setBorder(new EmptyBorder(25, 20, 25, 20));
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        logoPanel.setBackground(SIDEBAR_BG);
        
        ImageIcon utensilsIcon = IconUtil.getUtensilsIcon(24);
        if (utensilsIcon != null) {
            JLabel iconLabel = new JLabel(utensilsIcon);
            logoPanel.add(iconLabel);
        }
        
        JLabel logoLabel = new JLabel("Restaurant App");
        logoLabel.setFont(new Font("Montserrat", Font.BOLD, 18));
        logoLabel.setForeground(TEXT_COLOR);
        logoPanel.add(logoLabel);
        
        logoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.add(logoPanel);
        
        return header;
    }
    
    /**
     * Crée un séparateur.
     * @return Le composant
     */
    private Component createSeparator() {
        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(55, 65, 81));
        separator.setBackground(new Color(55, 65, 81));
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return separator;
    }
    
    /**
     * Crée un titre de section du menu.
     * @param title Le titre
     * @return Le composant
     */
    private Component createMenuSection(String title) {
        JLabel label = new JLabel(title);
        label.setFont(new Font("Montserrat", Font.BOLD, 11));
        label.setForeground(TEXT_MUTED);
        label.setBorder(new EmptyBorder(10, 20, 5, 20));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        return label;
    }
    
    /**
     * Crée un item de menu.
     * @param name Le nom (identifiant)
     * @param icon L'icône
     * @param text Le texte
     * @return Le panel
     */
    private JPanel createMenuItem(String name, String icon, String text) {
        JPanel item = new JPanel();
        item.setLayout(new BoxLayout(item, BoxLayout.X_AXIS));
        item.setBackground(SIDEBAR_BG);
        item.setBorder(new EmptyBorder(12, 20, 12, 20));
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        item.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        
        JLabel textLabel = new JLabel(text);
        textLabel.setFont(new Font("Montserrat", Font.PLAIN, 14));
        textLabel.setForeground(TEXT_COLOR);
        
        item.add(iconLabel);
        item.add(Box.createRigidArea(new Dimension(15, 0)));
        item.add(textLabel);
        item.add(Box.createHorizontalGlue());
        
        // Stocker les labels pour la mise à jour du style
        item.putClientProperty("textLabel", textLabel);
        item.putClientProperty("name", name);
        
        // Événements
        item.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (item != selectedMenuItem) {
                    item.setBackground(SIDEBAR_HOVER);
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (item != selectedMenuItem) {
                    item.setBackground(SIDEBAR_BG);
                }
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                selectMenuItem(name);
            }
        });
        
        menuItems.put(name, item);
        return item;
    }
    
    /**
     * Affiche un panel specifique (methode publique).
     * @param name Le nom du panel
     */
    public void showPanel(String name) {
        selectMenuItem(name);
    }
    
    /**
     * Selectionne un item du menu.
     * @param name Le nom de l'item
     */
    private void selectMenuItem(String name) {
        // Désélectionner l'ancien
        if (selectedMenuItem != null) {
            selectedMenuItem.setBackground(SIDEBAR_BG);
            JLabel textLabel = (JLabel) selectedMenuItem.getClientProperty("textLabel");
            if (textLabel != null) {
                textLabel.setFont(new Font("Montserrat", Font.PLAIN, 14));
            }
        }
        
        // Sélectionner le nouveau
        JPanel item = menuItems.get(name);
        if (item != null) {
            item.setBackground(SIDEBAR_SELECTED);
            JLabel textLabel = (JLabel) item.getClientProperty("textLabel");
            if (textLabel != null) {
                textLabel.setFont(new Font("Montserrat", Font.BOLD, 14));
            }
            selectedMenuItem = item;
        }
        
        // Afficher le panel correspondant
        cardLayout.show(contentPanel, name);
        
        // Rafraîchir le panel si nécessaire
        refreshPanel(name);
    }
    
    /**
     * Rafraîchit un panel.
     * @param name Le nom du panel
     */
    private void refreshPanel(String name) {
        // Les panels peuvent implémenter une interface Refreshable
        Component[] components = contentPanel.getComponents();
        for (Component comp : components) {
            if (comp.isVisible() && comp instanceof Refreshable) {
                ((Refreshable) comp).refresh();
            }
        }
    }
    
    /**
     * Crée le footer de la sidebar.
     * @return Le panel
     */
    private JPanel createSidebarFooter() {
        JPanel footer = new JPanel();
        footer.setLayout(new BorderLayout(10, 0));
        footer.setBackground(new Color(17, 24, 39));
        footer.setBorder(new EmptyBorder(15, 20, 15, 20));
        footer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        
        // Avatar
        JPanel avatar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(SIDEBAR_SELECTED);
                g2.fillOval(0, 0, 40, 40);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Montserrat", Font.BOLD, 16));
                FontMetrics fm = g2.getFontMetrics();
                String initials = getInitials(utilisateur.getNomComplet());
                int x = (40 - fm.stringWidth(initials)) / 2;
                int y = (40 + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(initials, x, y);
                g2.dispose();
            }
            
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(40, 40);
            }
        };
        avatar.setOpaque(false);
        
        // Info utilisateur
        JPanel userInfo = new JPanel();
        userInfo.setLayout(new BoxLayout(userInfo, BoxLayout.Y_AXIS));
        userInfo.setOpaque(false);
        
        JLabel nameLabel = new JLabel(utilisateur.getNomComplet());
        nameLabel.setFont(new Font("Montserrat", Font.BOLD, 13));
        nameLabel.setForeground(TEXT_COLOR);
        
        JLabel roleLabel = new JLabel(utilisateur.getRole().getLibelle());
        roleLabel.setFont(new Font("Montserrat", Font.PLAIN, 11));
        roleLabel.setForeground(TEXT_MUTED);
        
        userInfo.add(nameLabel);
        userInfo.add(roleLabel);
        
        // Bouton déconnexion (texte simple)
        JLabel logoutLabel = new JLabel("Quitter");
        logoutLabel.setFont(new Font("Montserrat", Font.PLAIN, 12));
        logoutLabel.setForeground(new Color(239, 68, 68));
        logoutLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutLabel.setToolTipText("Déconnexion");
        logoutLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                logout();
            }
        });
        
        footer.add(avatar, BorderLayout.WEST);
        footer.add(userInfo, BorderLayout.CENTER);
        footer.add(logoutLabel, BorderLayout.EAST);
        
        return footer;
    }
    
    /**
     * Extrait les initiales d'un nom.
     * @param name Le nom complet
     * @return Les initiales
     */
    private String getInitials(String name) {
        if (name == null || name.isEmpty()) return "?";
        String[] parts = name.split(" ");
        if (parts.length >= 2) {
            return (parts[0].charAt(0) + "" + parts[1].charAt(0)).toUpperCase();
        }
        return name.substring(0, Math.min(2, name.length())).toUpperCase();
    }
    
    /**
     * Déconnexion.
     */
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Voulez-vous vraiment vous déconnecter ?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            new UtilisateurController().deconnecter();
            dispose();
            
            SwingUtilities.invokeLater(() -> {
                new LoginFrame().setVisible(true);
            });
        }
    }
    
    /**
     * Centre la fenêtre sur l'écran.
     */
    private void centerOnScreen() {
        setLocationRelativeTo(null);
    }
    
    /**
     * Interface pour les panels rafraîchissables.
     */
    public interface Refreshable {
        void refresh();
    }
}
