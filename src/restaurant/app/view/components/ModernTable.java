package restaurant.app.view.components;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;

/**
 * Table moderne avec style personnalisé et fonctionnalités étendues.
 * Utilise la police Montserrat et un design moderne.
 * 
 * @author Restaurant App
 * @version 1.0
 */
public class ModernTable extends JTable {
    
    private Color headerBackground = new Color(249, 250, 251);
    private Color headerForeground = new Color(55, 65, 81);
    private Color rowBackground = Color.WHITE;
    private Color alternateRowBackground = new Color(249, 250, 251);
    private Color selectionBackground = new Color(219, 234, 254);
    private Color selectionForeground = new Color(30, 64, 175);
    private Color gridColor = new Color(229, 231, 235);
    
    /**
     * Constructeur par défaut.
     */
    public ModernTable() {
        super();
        initStyle();
    }
    
    /**
     * Constructeur avec modèle.
     * @param model Le modèle de table
     */
    public ModernTable(TableModel model) {
        super(model);
        initStyle();
    }
    
    /**
     * Constructeur avec données et colonnes.
     * @param data Les données
     * @param columnNames Les noms des colonnes
     */
    public ModernTable(Object[][] data, Object[] columnNames) {
        super(data, columnNames);
        initStyle();
    }
    
    /**
     * Initialise le style de la table.
     */
    private void initStyle() {
        // Police
        setFont(new Font("Montserrat", Font.PLAIN, 13));
        
        // Couleurs
        setBackground(rowBackground);
        setForeground(new Color(31, 41, 55));
        setSelectionBackground(selectionBackground);
        setSelectionForeground(selectionForeground);
        setGridColor(gridColor);
        
        // Dimensions
        setRowHeight(45);
        setShowVerticalLines(false);
        setIntercellSpacing(new Dimension(0, 1));
        
        // Focus
        setFocusable(true);
        
        // En-tête
        JTableHeader header = getTableHeader();
        header.setFont(new Font("Montserrat", Font.BOLD, 13));
        header.setBackground(headerBackground);
        header.setForeground(headerForeground);
        header.setPreferredSize(new Dimension(header.getWidth(), 50));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, gridColor));
        header.setReorderingAllowed(false);
        
        // Renderer personnalisé pour l'en-tête
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                label.setFont(new Font("Montserrat", Font.BOLD, 13));
                label.setBackground(headerBackground);
                label.setForeground(headerForeground);
                label.setBorder(new EmptyBorder(10, 15, 10, 15));
                label.setHorizontalAlignment(SwingConstants.LEFT);
                return label;
            }
        });
        
        // Renderer personnalisé pour les cellules
        setDefaultRenderer(Object.class, new ModernCellRenderer());
    }
    
    /**
     * Renderer de cellule moderne avec alternance de couleurs.
     */
    private class ModernCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
            
            label.setFont(new Font("Montserrat", Font.PLAIN, 13));
            label.setBorder(new EmptyBorder(10, 15, 10, 15));
            
            if (isSelected) {
                label.setBackground(selectionBackground);
                label.setForeground(selectionForeground);
            } else {
                // Alternance des couleurs
                if (row % 2 == 0) {
                    label.setBackground(rowBackground);
                } else {
                    label.setBackground(alternateRowBackground);
                }
                label.setForeground(new Color(31, 41, 55));
            }
            
            return label;
        }
    }
    
    /**
     * Configure une colonne avec une largeur fixe.
     * @param columnIndex L'index de la colonne
     * @param width La largeur
     */
    public void setColumnWidth(int columnIndex, int width) {
        if (columnIndex < getColumnCount()) {
            TableColumn column = getColumnModel().getColumn(columnIndex);
            column.setPreferredWidth(width);
            column.setMinWidth(width);
            column.setMaxWidth(width);
        }
    }
    
    /**
     * Configure une colonne comme colonne d'actions (centré).
     * @param columnIndex L'index de la colonne
     */
    public void setActionColumn(int columnIndex) {
        if (columnIndex < getColumnCount()) {
            TableColumn column = getColumnModel().getColumn(columnIndex);
            column.setCellRenderer(new CenterRenderer());
        }
    }
    
    /**
     * Renderer centré pour les colonnes d'actions.
     */
    private class CenterRenderer extends DefaultTableCellRenderer {
        public CenterRenderer() {
            setHorizontalAlignment(SwingConstants.CENTER);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            Component c = super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
            
            if (isSelected) {
                c.setBackground(selectionBackground);
                c.setForeground(selectionForeground);
            } else {
                if (row % 2 == 0) {
                    c.setBackground(rowBackground);
                } else {
                    c.setBackground(alternateRowBackground);
                }
                c.setForeground(new Color(31, 41, 55));
            }
            
            return c;
        }
    }
    
    /**
     * Active ou désactive les lignes rayées.
     * @param striped true pour activer
     */
    public void setStriped(boolean striped) {
        if (striped) {
            alternateRowBackground = new Color(249, 250, 251);
        } else {
            alternateRowBackground = rowBackground;
        }
        repaint();
    }
    
    /**
     * Enveloppe la table dans un JScrollPane stylisé.
     * @return Le JScrollPane
     */
    public JScrollPane wrapInScrollPane() {
        JScrollPane scrollPane = new JScrollPane(this);
        scrollPane.setBorder(BorderFactory.createLineBorder(gridColor, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        return scrollPane;
    }
}
