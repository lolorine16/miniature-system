package restaurant.app.util;

import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;

/**
 * Classe utilitaire pour charger les icônes PNG.
 * 
 * @author Restaurant App
 * @version 1.0
 */
public class IconUtil {
    
    private static final String ICONS_PATH = "/pics/";
    
    // Noms des fichiers d'icônes
    public static final String ICON_CHECK = "circle-check.png";
    public static final String ICON_X = "circle-x.png";
    public static final String ICON_ELLIPSIS = "circle-ellipsis.png";
    public static final String ICON_TRASH = "trash.png";
    public static final String ICON_UTENSILS = "utensils.png";
    
    /**
     * Charge une icône depuis le dossier pics.
     * @param iconName Le nom du fichier de l'icône
     * @return L'ImageIcon ou null si non trouvée
     */
    public static ImageIcon loadIcon(String iconName) {
        try {
            URL url = IconUtil.class.getResource(ICONS_PATH + iconName);
            if (url != null) {
                return new ImageIcon(url);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'icône: " + iconName);
        }
        return null;
    }
    
    /**
     * Charge une icône et la redimensionne.
     * @param iconName Le nom du fichier de l'icône
     * @param width La largeur souhaitée
     * @param height La hauteur souhaitée
     * @return L'ImageIcon redimensionnée ou null si non trouvée
     */
    public static ImageIcon loadIcon(String iconName, int width, int height) {
        ImageIcon icon = loadIcon(iconName);
        if (icon != null) {
            Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        }
        return null;
    }
    
    /**
     * Charge l'icône de succès/check.
     * @param size La taille (carré)
     * @return L'ImageIcon
     */
    public static ImageIcon getCheckIcon(int size) {
        return loadIcon(ICON_CHECK, size, size);
    }
    
    /**
     * Charge l'icône d'erreur/X.
     * @param size La taille (carré)
     * @return L'ImageIcon
     */
    public static ImageIcon getXIcon(int size) {
        return loadIcon(ICON_X, size, size);
    }
    
    /**
     * Charge l'icône d'attente/ellipsis.
     * @param size La taille (carré)
     * @return L'ImageIcon
     */
    public static ImageIcon getEllipsisIcon(int size) {
        return loadIcon(ICON_ELLIPSIS, size, size);
    }
    
    /**
     * Charge l'icône de suppression/trash.
     * @param size La taille (carré)
     * @return L'ImageIcon
     */
    public static ImageIcon getTrashIcon(int size) {
        return loadIcon(ICON_TRASH, size, size);
    }
    
    /**
     * Charge l'icône du restaurant/utensils.
     * @param size La taille (carré)
     * @return L'ImageIcon
     */
    public static ImageIcon getUtensilsIcon(int size) {
        return loadIcon(ICON_UTENSILS, size, size);
    }
}
