package restaurant.app.view.components;

import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Champ de recherche moderne avec icône et placeholder.
 * 
 * @author Restaurant App
 * @version 1.0
 */
public class SearchField extends JPanel {
    
    private JTextField textField;
    private JLabel searchIcon;
    private String placeholder = "Rechercher...";
    private Color borderColorDefault = new Color(209, 213, 219);
    private Color borderColorFocused = new Color(59, 130, 246);
    private boolean focused = false;
    
    /**
     * Constructeur par défaut.
     */
    public SearchField() {
        this("Rechercher...");
    }
    
    /**
     * Constructeur avec placeholder.
     * @param placeholder Le placeholder
     */
    public SearchField(String placeholder) {
        this.placeholder = placeholder;
        initComponents();
        initStyle();
        initListeners();
    }
    
    /**
     * Initialise les composants.
     */
    private void initComponents() {
        setLayout(new BorderLayout(10, 0));
        setOpaque(true);
        setBackground(Color.WHITE);
        
        // Icône de recherche
        searchIcon = new JLabel("🔍");
        searchIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        
        // Champ de texte
        textField = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                
                // Dessiner le placeholder
                if (getText().isEmpty() && !hasFocus()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    g2.setColor(new Color(156, 163, 175));
                    g2.setFont(getFont().deriveFont(Font.ITALIC));
                    
                    Insets insets = getInsets();
                    FontMetrics fm = g2.getFontMetrics();
                    int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                    
                    g2.drawString(SearchField.this.placeholder, insets.left, y);
                    g2.dispose();
                }
            }
        };
        textField.setFont(new Font("Montserrat", Font.PLAIN, 14));
        textField.setBorder(null);
        textField.setOpaque(false);
        textField.setCaretColor(new Color(59, 130, 246));
        
        add(searchIcon, BorderLayout.WEST);
        add(textField, BorderLayout.CENTER);
    }
    
    /**
     * Initialise le style.
     */
    private void initStyle() {
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColorDefault, 1, true),
            new EmptyBorder(10, 15, 10, 15)
        ));
        setPreferredSize(new Dimension(300, 45));
    }
    
    /**
     * Initialise les écouteurs d'événements.
     */
    private void initListeners() {
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                focused = true;
                updateBorder();
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                focused = false;
                updateBorder();
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
            new EmptyBorder(10, 15, 10, 15)
        ));
    }
    
    /**
     * Retourne le texte saisi.
     * @return Le texte
     */
    public String getText() {
        return textField.getText();
    }
    
    /**
     * Définit le texte.
     * @param text Le texte
     */
    public void setText(String text) {
        textField.setText(text);
    }
    
    /**
     * Efface le champ.
     */
    public void clear() {
        textField.setText("");
    }
    
    /**
     * Ajoute un écouteur de document pour réagir aux changements de texte.
     * @param listener L'écouteur
     */
    public void addDocumentListener(javax.swing.event.DocumentListener listener) {
        textField.getDocument().addDocumentListener(listener);
    }
    
    /**
     * Ajoute un ActionListener (déclenché sur Enter).
     * @param listener L'écouteur
     */
    public void addActionListener(java.awt.event.ActionListener listener) {
        textField.addActionListener(listener);
    }
    
    /**
     * Retourne le champ de texte interne.
     * @return Le JTextField
     */
    public JTextField getTextField() {
        return textField;
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        textField.setEnabled(enabled);
    }
    
    @Override
    public void requestFocus() {
        textField.requestFocus();
    }
}
