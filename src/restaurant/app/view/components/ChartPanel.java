package restaurant.app.view.components;

import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import restaurant.app.util.FormatUtil;

/**
 * Composant de graphique personnalisable.
 * Supporte les diagrammes en barres, circulaires et lignes.
 * 
 * @author Restaurant App
 * @version 1.0
 */
public class ChartPanel extends JPanel {
    
    public enum ChartType {
        BAR,        // Diagramme en barres
        PIE,        // Diagramme circulaire
        LINE,       // Diagramme en lignes
        AREA        // Diagramme en aires
    }
    
    private String title;
    private ChartType chartType;
    private List<ChartData> data;
    private boolean showLegend = true;
    private boolean showValues = true;
    private boolean animate = false;
    
    // Couleurs par défaut
    private static final Color[] DEFAULT_COLORS = {
        new Color(59, 130, 246),   // Bleu
        new Color(34, 197, 94),    // Vert
        new Color(245, 158, 11),   // Orange
        new Color(239, 68, 68),    // Rouge
        new Color(139, 92, 246),   // Violet
        new Color(6, 182, 212),    // Cyan
        new Color(236, 72, 153),   // Rose
        new Color(107, 114, 128)   // Gris
    };
    
    /**
     * Constructeur.
     * @param title Le titre du graphique
     * @param chartType Le type de graphique
     */
    public ChartPanel(String title, ChartType chartType) {
        this.title = title;
        this.chartType = chartType;
        this.data = new ArrayList<>();
        
        setOpaque(false);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setPreferredSize(new Dimension(400, 300));
    }
    
    /**
     * Ajoute une donnée au graphique.
     */
    public void addData(String label, double value) {
        addData(label, value, null);
    }
    
    /**
     * Ajoute une donnée avec couleur personnalisée.
     */
    public void addData(String label, double value, Color color) {
        if (color == null) {
            color = DEFAULT_COLORS[data.size() % DEFAULT_COLORS.length];
        }
        data.add(new ChartData(label, value, color));
        repaint();
    }
    
    /**
     * Efface toutes les données.
     */
    public void clearData() {
        data.clear();
        repaint();
    }
    
    /**
     * Définit les données.
     */
    public void setData(List<ChartData> data) {
        this.data = new ArrayList<>(data);
        repaint();
    }
    
    public void setShowLegend(boolean show) {
        this.showLegend = show;
        repaint();
    }
    
    public void setShowValues(boolean show) {
        this.showValues = show;
        repaint();
    }
    
    public void setChartType(ChartType type) {
        this.chartType = type;
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // Fond blanc arrondi
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
        
        // Titre
        g2.setColor(new Color(31, 41, 55));
        g2.setFont(new Font("Montserrat", Font.BOLD, 16));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(title, 25, 35);
        
        // Zone du graphique
        int chartX = 25;
        int chartY = 55;
        int chartWidth = getWidth() - 50;
        int chartHeight = getHeight() - 80;
        
        if (showLegend && chartType == ChartType.PIE) {
            chartWidth = (int) (chartWidth * 0.6);
        }
        
        if (data.isEmpty()) {
            g2.setColor(new Color(156, 163, 175));
            g2.setFont(new Font("Montserrat", Font.PLAIN, 14));
            g2.drawString("Aucune donnée disponible", chartX + 20, chartY + chartHeight / 2);
        } else {
            switch (chartType) {
                case BAR:
                    drawBarChart(g2, chartX, chartY, chartWidth, chartHeight);
                    break;
                case PIE:
                    drawPieChart(g2, chartX, chartY, chartWidth, chartHeight);
                    break;
                case LINE:
                    drawLineChart(g2, chartX, chartY, chartWidth, chartHeight);
                    break;
                case AREA:
                    drawAreaChart(g2, chartX, chartY, chartWidth, chartHeight);
                    break;
            }
        }
        
        g2.dispose();
    }
    
    /**
     * Dessine un diagramme en barres.
     */
    private void drawBarChart(Graphics2D g2, int x, int y, int width, int height) {
        if (data.isEmpty()) return;
        
        double maxValue = data.stream().mapToDouble(d -> d.value).max().orElse(1);
        if (maxValue == 0) maxValue = 1;
        
        int barCount = data.size();
        int spacing = 15;
        int barWidth = Math.max(20, (width - spacing * (barCount + 1)) / barCount);
        
        // Grille horizontale
        g2.setColor(new Color(243, 244, 246));
        g2.setStroke(new BasicStroke(1));
        for (int i = 0; i <= 4; i++) {
            int lineY = y + (int) (height * i / 4.0);
            g2.drawLine(x, lineY, x + width, lineY);
        }
        
        // Barres
        for (int i = 0; i < barCount; i++) {
            ChartData d = data.get(i);
            int barX = x + spacing + i * (barWidth + spacing);
            int barHeight = (int) ((d.value / maxValue) * (height - 30));
            int barY = y + height - barHeight - 25;
            
            // Barre avec dégradé
            GradientPaint gradient = new GradientPaint(
                barX, barY, d.color,
                barX, barY + barHeight, d.color.darker()
            );
            g2.setPaint(gradient);
            g2.fillRoundRect(barX, barY, barWidth, barHeight, 5, 5);
            
            // Valeur au-dessus
            if (showValues) {
                g2.setColor(new Color(31, 41, 55));
                g2.setFont(new Font("Montserrat", Font.BOLD, 10));
                String valueStr = formatValue(d.value);
                FontMetrics fm = g2.getFontMetrics();
                int valueX = barX + (barWidth - fm.stringWidth(valueStr)) / 2;
                g2.drawString(valueStr, valueX, barY - 5);
            }
            
            // Label en bas
            g2.setColor(new Color(107, 114, 128));
            g2.setFont(new Font("Montserrat", Font.PLAIN, 10));
            FontMetrics fm = g2.getFontMetrics();
            String label = truncateLabel(d.label, barWidth + spacing - 5, fm);
            int labelX = barX + (barWidth - fm.stringWidth(label)) / 2;
            g2.drawString(label, labelX, y + height - 5);
        }
    }
    
    /**
     * Dessine un diagramme circulaire.
     */
    private void drawPieChart(Graphics2D g2, int x, int y, int width, int height) {
        if (data.isEmpty()) return;
        
        double total = data.stream().mapToDouble(d -> d.value).sum();
        if (total == 0) return;
        
        int diameter = Math.min(width, height) - 40;
        int centerX = x + diameter / 2 + 20;
        int centerY = y + height / 2;
        
        double startAngle = 90;
        
        for (ChartData d : data) {
            double angle = (d.value / total) * 360;
            
            g2.setColor(d.color);
            g2.fill(new Arc2D.Double(
                centerX - diameter / 2, centerY - diameter / 2,
                diameter, diameter,
                startAngle, -angle,
                Arc2D.PIE
            ));
            
            startAngle -= angle;
        }
        
        // Cercle blanc au centre (effet donut)
        int innerDiameter = diameter / 2;
        g2.setColor(Color.WHITE);
        g2.fillOval(
            centerX - innerDiameter / 2, 
            centerY - innerDiameter / 2,
            innerDiameter, innerDiameter
        );
        
        // Légende
        if (showLegend) {
            int legendX = x + width + 30;
            int legendY = y + 20;
            
            g2.setFont(new Font("Montserrat", Font.PLAIN, 11));
            for (int i = 0; i < data.size(); i++) {
                ChartData d = data.get(i);
                
                g2.setColor(d.color);
                g2.fillRoundRect(legendX, legendY + i * 25, 12, 12, 3, 3);
                
                g2.setColor(new Color(55, 65, 81));
                double percent = (d.value / total) * 100;
                String text = String.format("%s (%.1f%%)", d.label, percent);
                g2.drawString(text, legendX + 20, legendY + i * 25 + 10);
            }
        }
    }
    
    /**
     * Dessine un diagramme en lignes.
     */
    private void drawLineChart(Graphics2D g2, int x, int y, int width, int height) {
        if (data.size() < 2) return;
        
        double maxValue = data.stream().mapToDouble(d -> d.value).max().orElse(1);
        if (maxValue == 0) maxValue = 1;
        
        int pointCount = data.size();
        int spacing = width / (pointCount - 1);
        
        // Grille
        g2.setColor(new Color(243, 244, 246));
        g2.setStroke(new BasicStroke(1));
        for (int i = 0; i <= 4; i++) {
            int lineY = y + (int) (height * i / 4.0);
            g2.drawLine(x, lineY, x + width, lineY);
        }
        
        // Points et lignes
        int[] xPoints = new int[pointCount];
        int[] yPoints = new int[pointCount];
        
        for (int i = 0; i < pointCount; i++) {
            ChartData d = data.get(i);
            xPoints[i] = x + i * spacing;
            yPoints[i] = y + height - 25 - (int) ((d.value / maxValue) * (height - 50));
        }
        
        // Ligne
        g2.setColor(DEFAULT_COLORS[0]);
        g2.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (int i = 0; i < pointCount - 1; i++) {
            g2.drawLine(xPoints[i], yPoints[i], xPoints[i + 1], yPoints[i + 1]);
        }
        
        // Points
        for (int i = 0; i < pointCount; i++) {
            ChartData d = data.get(i);
            
            // Point extérieur
            g2.setColor(DEFAULT_COLORS[0]);
            g2.fillOval(xPoints[i] - 6, yPoints[i] - 6, 12, 12);
            
            // Point intérieur blanc
            g2.setColor(Color.WHITE);
            g2.fillOval(xPoints[i] - 3, yPoints[i] - 3, 6, 6);
            
            // Label en bas
            g2.setColor(new Color(107, 114, 128));
            g2.setFont(new Font("Montserrat", Font.PLAIN, 9));
            FontMetrics fm = g2.getFontMetrics();
            String label = truncateLabel(d.label, spacing - 5, fm);
            int labelX = xPoints[i] - fm.stringWidth(label) / 2;
            g2.drawString(label, labelX, y + height - 5);
            
            // Valeur
            if (showValues) {
                g2.setColor(new Color(31, 41, 55));
                g2.setFont(new Font("Montserrat", Font.BOLD, 9));
                String valueStr = formatValue(d.value);
                fm = g2.getFontMetrics();
                int valueX = xPoints[i] - fm.stringWidth(valueStr) / 2;
                g2.drawString(valueStr, valueX, yPoints[i] - 12);
            }
        }
    }
    
    /**
     * Dessine un diagramme en aires.
     */
    private void drawAreaChart(Graphics2D g2, int x, int y, int width, int height) {
        if (data.size() < 2) return;
        
        double maxValue = data.stream().mapToDouble(d -> d.value).max().orElse(1);
        if (maxValue == 0) maxValue = 1;
        
        int pointCount = data.size();
        int spacing = width / (pointCount - 1);
        int baseY = y + height - 25;
        
        // Grille
        g2.setColor(new Color(243, 244, 246));
        g2.setStroke(new BasicStroke(1));
        for (int i = 0; i <= 4; i++) {
            int lineY = y + (int) (height * i / 4.0);
            g2.drawLine(x, lineY, x + width, lineY);
        }
        
        // Créer le polygone pour l'aire
        int[] xPoints = new int[pointCount + 2];
        int[] yPoints = new int[pointCount + 2];
        
        for (int i = 0; i < pointCount; i++) {
            ChartData d = data.get(i);
            xPoints[i] = x + i * spacing;
            yPoints[i] = y + height - 25 - (int) ((d.value / maxValue) * (height - 50));
        }
        
        // Fermer le polygone
        xPoints[pointCount] = x + (pointCount - 1) * spacing;
        yPoints[pointCount] = baseY;
        xPoints[pointCount + 1] = x;
        yPoints[pointCount + 1] = baseY;
        
        // Remplir l'aire avec dégradé
        Color areaColor = new Color(59, 130, 246, 100);
        g2.setColor(areaColor);
        g2.fillPolygon(xPoints, yPoints, pointCount + 2);
        
        // Ligne du dessus
        g2.setColor(DEFAULT_COLORS[0]);
        g2.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (int i = 0; i < pointCount - 1; i++) {
            g2.drawLine(xPoints[i], yPoints[i], xPoints[i + 1], yPoints[i + 1]);
        }
        
        // Labels
        g2.setFont(new Font("Montserrat", Font.PLAIN, 9));
        for (int i = 0; i < pointCount; i++) {
            ChartData d = data.get(i);
            g2.setColor(new Color(107, 114, 128));
            FontMetrics fm = g2.getFontMetrics();
            String label = truncateLabel(d.label, spacing - 5, fm);
            int labelX = xPoints[i] - fm.stringWidth(label) / 2;
            g2.drawString(label, labelX, y + height - 5);
        }
    }
    
    private String formatValue(double value) {
        if (value >= 1000000) {
            return String.format("%.1fM", value / 1000000);
        } else if (value >= 1000) {
            return String.format("%.1fK", value / 1000);
        } else if (value == (int) value) {
            return String.valueOf((int) value);
        } else {
            return String.format("%.1f", value);
        }
    }
    
    private String truncateLabel(String label, int maxWidth, FontMetrics fm) {
        if (fm.stringWidth(label) <= maxWidth) {
            return label;
        }
        String truncated = label;
        while (truncated.length() > 1 && fm.stringWidth(truncated + "...") > maxWidth) {
            truncated = truncated.substring(0, truncated.length() - 1);
        }
        return truncated + "...";
    }
    
    /**
     * Classe représentant une donnée du graphique.
     */
    public static class ChartData {
        public final String label;
        public final double value;
        public final Color color;
        
        public ChartData(String label, double value, Color color) {
            this.label = label;
            this.value = value;
            this.color = color;
        }
    }
}
