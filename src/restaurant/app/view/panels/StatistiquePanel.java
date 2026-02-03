package restaurant.app.view.panels;

import restaurant.app.controller.StatistiqueController;
import restaurant.app.model.enums.EtatCommande;
import restaurant.app.util.FormatUtil;
import restaurant.app.view.components.ChartPanel;
import restaurant.app.view.components.ChartPanel.ChartType;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class StatistiquePanel extends JPanel {

    private final StatistiqueController controller;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM");

    private JLabel lblChiffreAffaires;
    private JLabel lblNbCommandes;
    private JLabel lblPanierMoyen;
    private JLabel lblProduitsRupture;

    private ChartPanel chartCA;
    private ChartPanel chartCommandes;
    private ChartPanel chartProduits;
    private ChartPanel chartEtats;

    private JComboBox<String> comboPeriode;
    private JComboBox<String> comboChartType;

    public StatistiquePanel() {
        this.controller = new StatistiqueController();
        initComponents();
        chargerStatistiques();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(248, 249, 250));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        add(creerHeader(), BorderLayout.NORTH);
        add(creerContenuPrincipal(), BorderLayout.CENTER);
    }

    private JPanel creerHeader() {
        JPanel header = new JPanel(new BorderLayout(20, 0));
        header.setOpaque(false);

        JLabel titre = new JLabel("Statistiques");
        titre.setFont(new Font("Montserrat", Font.BOLD, 28));
        titre.setForeground(new Color(33, 37, 41));
        header.add(titre, BorderLayout.WEST);

        JPanel controles = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        controles.setOpaque(false);

        JLabel lblPeriode = new JLabel("Periode:");
        lblPeriode.setFont(new Font("Montserrat", Font.PLAIN, 14));
        controles.add(lblPeriode);

        comboPeriode = new JComboBox<>(new String[]{
            "7 derniers jours",
            "30 derniers jours",
            "3 derniers mois"
        });
        comboPeriode.setFont(new Font("Montserrat", Font.PLAIN, 14));
        comboPeriode.setPreferredSize(new Dimension(150, 35));
        comboPeriode.addActionListener(e -> chargerStatistiques());
        controles.add(comboPeriode);

        JLabel lblType = new JLabel("Graphique:");
        lblType.setFont(new Font("Montserrat", Font.PLAIN, 14));
        controles.add(lblType);

        comboChartType = new JComboBox<>(new String[]{
            "Barres",
            "Lignes",
            "Circulaire"
        });
        comboChartType.setFont(new Font("Montserrat", Font.PLAIN, 14));
        comboChartType.setPreferredSize(new Dimension(120, 35));
        comboChartType.addActionListener(e -> changerTypeGraphique());
        controles.add(comboChartType);

        JButton btnActualiser = new JButton("Actualiser");
        btnActualiser.setFont(new Font("Montserrat", Font.BOLD, 14));
        btnActualiser.setBackground(new Color(0, 123, 255));
        btnActualiser.setForeground(Color.WHITE);
        btnActualiser.setFocusPainted(false);
        btnActualiser.setBorderPainted(false);
        btnActualiser.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnActualiser.setPreferredSize(new Dimension(120, 35));
        btnActualiser.addActionListener(e -> chargerStatistiques());
        controles.add(btnActualiser);

        header.add(controles, BorderLayout.EAST);

        return header;
    }

    private JPanel creerContenuPrincipal() {
        JPanel contenu = new JPanel(new BorderLayout(15, 15));
        contenu.setOpaque(false);

        contenu.add(creerPanneauKPI(), BorderLayout.NORTH);
        contenu.add(creerPanneauGraphiques(), BorderLayout.CENTER);

        return contenu;
    }

    private JPanel creerPanneauKPI() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 15, 0));
        panel.setOpaque(false);

        lblChiffreAffaires = new JLabel("0 FCFA");
        panel.add(creerCarteKPI("Chiffre d'affaires", lblChiffreAffaires, new Color(40, 167, 69)));

        lblNbCommandes = new JLabel("0");
        panel.add(creerCarteKPI("Commandes du jour", lblNbCommandes, new Color(0, 123, 255)));

        lblPanierMoyen = new JLabel("0 FCFA");
        panel.add(creerCarteKPI("CA du mois", lblPanierMoyen, new Color(255, 193, 7)));

        lblProduitsRupture = new JLabel("0");
        panel.add(creerCarteKPI("Produits en rupture", lblProduitsRupture, new Color(220, 53, 69)));

        return panel;
    }

    private JPanel creerCarteKPI(String titre, JLabel lblValeur, Color couleur) {
        JPanel carte = new JPanel(new BorderLayout(10, 5));
        carte.setBackground(Color.WHITE);
        carte.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            new EmptyBorder(20, 20, 20, 20)
        ));

        JPanel indicateur = new JPanel();
        indicateur.setBackground(couleur);
        indicateur.setPreferredSize(new Dimension(5, 0));
        carte.add(indicateur, BorderLayout.WEST);

        JPanel contenu = new JPanel();
        contenu.setLayout(new BoxLayout(contenu, BoxLayout.Y_AXIS));
        contenu.setOpaque(false);

        JLabel lblTitre = new JLabel(titre);
        lblTitre.setFont(new Font("Montserrat", Font.PLAIN, 13));
        lblTitre.setForeground(new Color(108, 117, 125));
        lblTitre.setAlignmentX(Component.LEFT_ALIGNMENT);
        contenu.add(lblTitre);

        contenu.add(Box.createVerticalStrut(8));

        lblValeur.setFont(new Font("Montserrat", Font.BOLD, 24));
        lblValeur.setForeground(new Color(33, 37, 41));
        lblValeur.setAlignmentX(Component.LEFT_ALIGNMENT);
        contenu.add(lblValeur);

        carte.add(contenu, BorderLayout.CENTER);

        return carte;
    }

    private JPanel creerPanneauGraphiques() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 15, 15));
        panel.setOpaque(false);

        chartCA = new ChartPanel("Chiffre d'affaires par jour", ChartType.BAR);
        panel.add(chartCA);

        chartCommandes = new ChartPanel("Commandes par jour", ChartType.LINE);
        panel.add(chartCommandes);

        chartProduits = new ChartPanel("Produits les plus vendus", ChartType.BAR);
        panel.add(chartProduits);

        chartEtats = new ChartPanel("Repartition par etat", ChartType.PIE);
        panel.add(chartEtats);

        return panel;
    }

    private void chargerStatistiques() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            private Map<String, Object> stats;
            private Map<LocalDate, BigDecimal> caParJour;
            private Map<LocalDate, Integer> cmdParJour;
            private Map<String, Integer> produitsVendus;
            private Map<EtatCommande, Integer> repartition;

            @Override
            protected Void doInBackground() throws Exception {
                LocalDate dateFin = LocalDate.now();
                LocalDate dateDebut = getDateDebut();

                stats = controller.getStatistiquesDashboard();
                caParJour = controller.getChiffreAffairesParJour(dateDebut, dateFin);
                cmdParJour = controller.getNombreCommandesParJour(dateDebut, dateFin);
                produitsVendus = controller.getVentesParProduit(8);
                repartition = controller.getRepartitionParEtat();

                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    mettreAJourKPI(stats);
                    mettreAJourGraphiqueCA(caParJour);
                    mettreAJourGraphiqueCommandes(cmdParJour);
                    mettreAJourGraphiqueProduits(produitsVendus);
                    mettreAJourGraphiqueEtats(repartition);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private LocalDate getDateDebut() {
        int index = comboPeriode.getSelectedIndex();
        LocalDate now = LocalDate.now();
        switch (index) {
            case 0: return now.minusDays(7);
            case 1: return now.minusDays(30);
            case 2: return now.minusDays(90);
            default: return now.minusDays(7);
        }
    }

    private void mettreAJourKPI(Map<String, Object> stats) {
        if (stats == null) return;

        Object caJour = stats.get("chiffreAffairesDuJour");
        if (caJour instanceof BigDecimal) {
            lblChiffreAffaires.setText(FormatUtil.formatCurrency((BigDecimal) caJour));
        }

        Object nbCmd = stats.get("commandesDuJour");
        if (nbCmd instanceof Number) {
            lblNbCommandes.setText(String.valueOf(((Number) nbCmd).intValue()));
        }

        Object caMois = stats.get("chiffreAffairesMois");
        if (caMois instanceof BigDecimal) {
            lblPanierMoyen.setText(FormatUtil.formatCurrency((BigDecimal) caMois));
        }

        Object rupture = stats.get("produitsEnRupture");
        if (rupture instanceof Number) {
            lblProduitsRupture.setText(String.valueOf(((Number) rupture).intValue()));
        }
    }

    private void mettreAJourGraphiqueCA(Map<LocalDate, BigDecimal> data) {
        chartCA.clearData();
        if (data == null || data.isEmpty()) return;

        for (Map.Entry<LocalDate, BigDecimal> entry : data.entrySet()) {
            String label = entry.getKey().format(dateFormatter);
            double value = entry.getValue().doubleValue();
            chartCA.addData(label, value);
        }
    }

    private void mettreAJourGraphiqueCommandes(Map<LocalDate, Integer> data) {
        chartCommandes.clearData();
        if (data == null || data.isEmpty()) return;

        for (Map.Entry<LocalDate, Integer> entry : data.entrySet()) {
            String label = entry.getKey().format(dateFormatter);
            chartCommandes.addData(label, entry.getValue());
        }
    }

    private void mettreAJourGraphiqueProduits(Map<String, Integer> data) {
        chartProduits.clearData();
        if (data == null || data.isEmpty()) return;

        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            String nom = entry.getKey();
            if (nom.length() > 12) {
                nom = nom.substring(0, 12) + "...";
            }
            chartProduits.addData(nom, entry.getValue());
        }
    }

    private void mettreAJourGraphiqueEtats(Map<EtatCommande, Integer> data) {
        chartEtats.clearData();
        if (data == null || data.isEmpty()) return;

        for (Map.Entry<EtatCommande, Integer> entry : data.entrySet()) {
            chartEtats.addData(entry.getKey().getLibelle(), entry.getValue());
        }
    }

    private void changerTypeGraphique() {
        ChartType type;
        int index = comboChartType.getSelectedIndex();
        switch (index) {
            case 0: type = ChartType.BAR; break;
            case 1: type = ChartType.LINE; break;
            case 2: type = ChartType.PIE; break;
            default: type = ChartType.BAR;
        }

        chartCA.setChartType(type);
        chartCommandes.setChartType(type);
        chartProduits.setChartType(type);
    }

    public void rafraichir() {
        chargerStatistiques();
    }
}
