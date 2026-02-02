package restaurant.app.view.components;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Champ de mot de passe moderne avec placeholder et bouton afficher/masquer.
 * Utilise la police Montserrat.
 * 
 * @author Restaurant App
 * @version 1.0
 */
public class ModernPasswordField extends JPanel {
    
    private JPasswordField passwordField;
    private JToggleButton toggleButton;
    private String placeholder;
    private Color borderColorDefault = new Color(209, 213, 219);
    private Color borderColorFocused = new Color(59, 130, 246);
    private boolean focused = false;
    
    /**
     * Constructeur par défaut.
     */
    public ModernPasswordField() {
        this("");
    }
    
    /**
     * Constructeur avec placeholder.
     * @param placeholder Le placeholder
     */
    public ModernPasswordField(String placeholder) {
        this.placeholder = placeholder;
        initComponents();
        initStyle();
        initListeners();
    }
    
    /**
     * Initialise les composants.
     */
    private void initComponents() {
        setLayout(new BorderLayout());
        setOpaque(true);
        setBackground(Color.WHITE);
        
        // Champ de mot de passe
        passwordField = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                
                // Dessiner le placeholder
                if (getPassword().length == 0 && !hasFocus() && 
                    ModernPasswordField.this.placeholder != null && 
                    !ModernPasswordField.this.placeholder.isEmpty()) {
                    
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    g2.setColor(new Color(156, 163, 175));
                    g2.setFont(getFont().deriveFont(Font.ITALIC));
                    
                    Insets insets = getInsets();
                    FontMetrics fm = g2.getFontMetrics();
                    int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                    
                    g2.drawString(ModernPasswordField.this.placeholder, insets.left, y);
                    g2.dispose();
                }
            }
        };
        passwordField.setFont(new Font("Montserrat", Font.PLAIN, 14));
        passwordField.setBorder(new EmptyBorder(0, 0, 0, 0));
        passwordField.setOpaque(false);
        passwordField.setCaretColor(new Color(59, 130, 246));
        
        // Bouton toggle
        toggleButton = new JToggleButton("👁");
        toggleButton.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        toggleButton.setBorderPainted(false);
        toggleButton.setContentAreaFilled(false);
        toggleButton.setFocusPainted(false);
        toggleButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        toggleButton.setPreferredSize(new Dimension(40, 40));
        
        // Panel wrapper pour le champ
        JPanel fieldPanel = new JPanel(new BorderLayout());
        fieldPanel.setOpaque(false);
        fieldPanel.setBorder(new EmptyBorder(0, 15, 0, 5));
        fieldPanel.add(passwordField, BorderLayout.CENTER);
        fieldPanel.add(toggleButton, BorderLayout.EAST);
        
        add(fieldPanel, BorderLayout.CENTER);
    }
    
    /**
     * Initialise le style.
     */
    private void initStyle() {
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColorDefault, 1, true),
            new EmptyBorder(5, 0, 5, 0)
        ));
        setPreferredSize(new Dimension(250, 45));
    }
    
    /**
     * Initialise les écouteurs d'événements.
     */
    private void initListeners() {
        passwordField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                focused = true;
                updateBorder();
            }
            
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                focused = false;
                updateBorder();
            }
        });
        
        toggleButton.addActionListener(e -> {
            if (toggleButton.isSelected()) {
                passwordField.setEchoChar((char) 0);
                toggleButton.setText("🙈");
            } else {
                passwordField.setEchoChar('•');
                toggleButton.setText("👁");
            }
        });
    }
    
    /**
     * Met à jour la bordure selon l'état de focus.
     */
    private void updateBorder() {
        Color borderColor = focused ? borderColorFocused : borderColorDefault;
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor, focused ? 2 : 1, true),
            new EmptyBorder(5, 0, 5, 0)
        ));
    }
    
    /**
     * Retourne le mot de passe.
     * @return Le mot de passe
     */
    public char[] getPassword() {
        return passwordField.getPassword();
    }
    
    /**
     * Retourne le mot de passe en String.
     * @return Le mot de passe
     */
    public String getPasswordText() {
        return new String(passwordField.getPassword());
    }
    
    /**
     * Définit le placeholder.
     * @param placeholder Le placeholder
     */
    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        repaint();
    }
    
    /**
     * Efface le champ.
     */
    public void clear() {
        passwordField.setText("");
    }
    
    /**
     * Indique si le champ est en erreur.
     * @param error true pour afficher un état d'erreur
     */
    public void setError(boolean error) {
        if (error) {
            borderColorDefault = new Color(239, 68, 68);
            borderColorFocused = new Color(220, 38, 38);
        } else {
            borderColorDefault = new Color(209, 213, 219);
            borderColorFocused = new Color(59, 130, 246);
        }
        updateBorder();
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        passwordField.setEnabled(enabled);
        toggleButton.setEnabled(enabled);
    }
    
    /**
     * Définit le focus sur le champ.
     */
    @Override
    public void requestFocus() {
        passwordField.requestFocus();
    }
}
