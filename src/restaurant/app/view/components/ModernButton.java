package restaurant.app.view.components;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

/**
 * Bouton moderne personnalisé avec effets de survol.
 * Utilise la police Montserrat et les couleurs du thème.
 * 
 * @author Restaurant App
 * @version 1.0
 */
public class ModernButton extends JButton {
    
    // Couleurs du thème
    private Color backgroundColor;
    private Color hoverColor;
    private Color pressedColor;
    private Color textColor;
    
    // États
    private boolean hovered = false;
    private boolean pressed = false;
    
    /**
     * Types de boutons prédéfinis.
     */
    public enum ButtonType {
        PRIMARY,
        SECONDARY,
        SUCCESS,
        DANGER,
        WARNING,
        INFO
    }
    
    /**
     * Constructeur avec texte et type.
     * @param text Le texte du bouton
     * @param type Le type de bouton
     */
    public ModernButton(String text, ButtonType type) {
        super(text);
        initColors(type);
        initStyle();
        initListeners();
    }
    
    /**
     * Constructeur avec texte (type PRIMARY par défaut).
     * @param text Le texte du bouton
     */
    public ModernButton(String text) {
        this(text, ButtonType.PRIMARY);
    }
    
    /**
     * Constructeur avec texte et icône.
     * @param text Le texte du bouton
     * @param icon L'icône
     * @param type Le type de bouton
     */
    public ModernButton(String text, Icon icon, ButtonType type) {
        super(text, icon);
        initColors(type);
        initStyle();
        initListeners();
    }
    
    /**
     * Initialise les couleurs selon le type.
     * @param type Le type de bouton
     */
    private void initColors(ButtonType type) {
        switch (type) {
            case PRIMARY:
                backgroundColor = new Color(59, 130, 246);  // Bleu
                hoverColor = new Color(37, 99, 235);
                pressedColor = new Color(29, 78, 216);
                textColor = Color.WHITE;
                break;
            case SECONDARY:
                backgroundColor = new Color(107, 114, 128);  // Gris
                hoverColor = new Color(75, 85, 99);
                pressedColor = new Color(55, 65, 81);
                textColor = Color.WHITE;
                break;
            case SUCCESS:
                backgroundColor = new Color(34, 197, 94);   // Vert
                hoverColor = new Color(22, 163, 74);
                pressedColor = new Color(21, 128, 61);
                textColor = Color.WHITE;
                break;
            case DANGER:
                backgroundColor = new Color(239, 68, 68);   // Rouge
                hoverColor = new Color(220, 38, 38);
                pressedColor = new Color(185, 28, 28);
                textColor = Color.WHITE;
                break;
            case WARNING:
                backgroundColor = new Color(245, 158, 11);  // Orange
                hoverColor = new Color(217, 119, 6);
                pressedColor = new Color(180, 83, 9);
                textColor = Color.WHITE;
                break;
            case INFO:
                backgroundColor = new Color(6, 182, 212);   // Cyan
                hoverColor = new Color(8, 145, 178);
                pressedColor = new Color(14, 116, 144);
                textColor = Color.WHITE;
                break;
        }
    }
    
    /**
     * Initialise le style du bouton.
     */
    private void initStyle() {
        setFont(new Font("Montserrat", Font.BOLD, 13));
        setForeground(textColor);
        setBackground(backgroundColor);
        setBorderPainted(false);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(120, 40));
    }
    
    /**
     * Initialise les écouteurs d'événements.
     */
    private void initListeners() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (isEnabled()) {
                    hovered = true;
                    repaint();
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                hovered = false;
                pressed = false;
                repaint();
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                if (isEnabled()) {
                    pressed = true;
                    repaint();
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                pressed = false;
                repaint();
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Déterminer la couleur de fond
        Color bgColor;
        if (!isEnabled()) {
            bgColor = new Color(200, 200, 200);
        } else if (pressed) {
            bgColor = pressedColor;
        } else if (hovered) {
            bgColor = hoverColor;
        } else {
            bgColor = backgroundColor;
        }
        
        // Dessiner le fond arrondi
        g2.setColor(bgColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        
        // Effet d'ombre légère
        if (!pressed && isEnabled()) {
            g2.setColor(new Color(0, 0, 0, 30));
            g2.fillRoundRect(2, 2, getWidth() - 2, getHeight() - 2, 10, 10);
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 10, 10);
        }
        
        g2.dispose();
        
        super.paintComponent(g);
    }
    
    /**
     * Définit les couleurs personnalisées.
     * @param background Couleur de fond
     * @param hover Couleur au survol
     * @param pressed Couleur quand pressé
     * @param text Couleur du texte
     */
    public void setCustomColors(Color background, Color hover, Color pressed, Color text) {
        this.backgroundColor = background;
        this.hoverColor = hover;
        this.pressedColor = pressed;
        this.textColor = text;
        setForeground(textColor);
        repaint();
    }
    
    /**
     * Crée un bouton avec icône uniquement.
     * @param icon L'icône
     * @param type Le type
     * @return Le bouton
     */
    public static ModernButton createIconButton(Icon icon, ButtonType type) {
        ModernButton button = new ModernButton("", icon, type);
        button.setPreferredSize(new Dimension(40, 40));
        return button;
    }
}
