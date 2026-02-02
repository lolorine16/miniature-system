package restaurant.app.util;

import java.util.regex.Pattern;

/**
 * Classe utilitaire pour la validation des données.
 * Contient des méthodes statiques de validation utilisées dans toute l'application.
 * 
 * @author Restaurant App
 * @version 1.0
 */
public class ValidationUtil {
    
    // Patterns de validation
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    
    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("^[0-9+\\-\\s()]{8,20}$");
    
    private static final Pattern NUMERIC_PATTERN = 
        Pattern.compile("^-?\\d+(\\.\\d+)?$");
    
    /**
     * Constructeur privé pour empêcher l'instanciation.
     */
    private ValidationUtil() {
        throw new UnsupportedOperationException("Classe utilitaire - ne peut pas être instanciée");
    }
    
    /**
     * Vérifie qu'une chaîne n'est pas null et non vide.
     * @param value La valeur à vérifier
     * @return true si la valeur est non null et non vide (après trim)
     */
    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }
    
    /**
     * Vérifie qu'une chaîne est null ou vide.
     * @param value La valeur à vérifier
     * @return true si la valeur est null ou vide
     */
    public static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
    
    /**
     * Vérifie qu'un nombre est strictement positif.
     * @param value La valeur à vérifier
     * @return true si la valeur est > 0
     */
    public static boolean isPositive(double value) {
        return value > 0;
    }
    
    /**
     * Vérifie qu'un nombre est positif ou zéro.
     * @param value La valeur à vérifier
     * @return true si la valeur est >= 0
     */
    public static boolean isPositiveOrZero(double value) {
        return value >= 0;
    }
    
    /**
     * Vérifie qu'un entier est strictement positif.
     * @param value La valeur à vérifier
     * @return true si la valeur est > 0
     */
    public static boolean isPositive(int value) {
        return value > 0;
    }
    
    /**
     * Vérifie qu'un entier est positif ou zéro.
     * @param value La valeur à vérifier
     * @return true si la valeur est >= 0
     */
    public static boolean isPositiveOrZero(int value) {
        return value >= 0;
    }
    
    /**
     * Valide un format d'email.
     * @param email L'email à valider
     * @return true si l'email est valide
     */
    public static boolean isValidEmail(String email) {
        if (isEmpty(email)) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }
    
    /**
     * Valide un format de numéro de téléphone.
     * @param phone Le numéro à valider
     * @return true si le numéro est valide
     */
    public static boolean isValidPhone(String phone) {
        if (isEmpty(phone)) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }
    
    /**
     * Vérifie qu'une chaîne représente un nombre.
     * @param value La valeur à vérifier
     * @return true si c'est un nombre valide
     */
    public static boolean isNumeric(String value) {
        if (isEmpty(value)) {
            return false;
        }
        return NUMERIC_PATTERN.matcher(value.trim()).matches();
    }
    
    /**
     * Vérifie qu'une chaîne représente un entier positif.
     * @param value La valeur à vérifier
     * @return true si c'est un entier positif
     */
    public static boolean isPositiveInteger(String value) {
        if (isEmpty(value)) {
            return false;
        }
        try {
            int num = Integer.parseInt(value.trim());
            return num > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Vérifie la longueur minimale d'une chaîne.
     * @param value La valeur à vérifier
     * @param minLength La longueur minimale
     * @return true si la longueur est suffisante
     */
    public static boolean hasMinLength(String value, int minLength) {
        if (value == null) {
            return false;
        }
        return value.trim().length() >= minLength;
    }
    
    /**
     * Vérifie la longueur maximale d'une chaîne.
     * @param value La valeur à vérifier
     * @param maxLength La longueur maximale
     * @return true si la longueur ne dépasse pas le maximum
     */
    public static boolean hasMaxLength(String value, int maxLength) {
        if (value == null) {
            return true;
        }
        return value.trim().length() <= maxLength;
    }
    
    /**
     * Vérifie qu'une valeur est dans une plage.
     * @param value La valeur à vérifier
     * @param min Le minimum (inclus)
     * @param max Le maximum (inclus)
     * @return true si la valeur est dans la plage
     */
    public static boolean isInRange(double value, double min, double max) {
        return value >= min && value <= max;
    }
    
    /**
     * Vérifie qu'un entier est dans une plage.
     * @param value La valeur à vérifier
     * @param min Le minimum (inclus)
     * @param max Le maximum (inclus)
     * @return true si la valeur est dans la plage
     */
    public static boolean isInRange(int value, int min, int max) {
        return value >= min && value <= max;
    }
    
    /**
     * Nettoie une chaîne (trim et gestion des nulls).
     * @param value La valeur à nettoyer
     * @return La valeur nettoyée ou chaîne vide si null
     */
    public static String clean(String value) {
        return value == null ? "" : value.trim();
    }
    
    /**
     * Parse un double depuis une chaîne de manière sécurisée.
     * @param value La valeur à parser
     * @param defaultValue La valeur par défaut si le parsing échoue
     * @return Le double parsé ou la valeur par défaut
     */
    public static double parseDouble(String value, double defaultValue) {
        if (isEmpty(value)) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(value.trim().replace(",", "."));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    /**
     * Parse un entier depuis une chaîne de manière sécurisée.
     * @param value La valeur à parser
     * @param defaultValue La valeur par défaut si le parsing échoue
     * @return L'entier parsé ou la valeur par défaut
     */
    public static int parseInt(String value, int defaultValue) {
        if (isEmpty(value)) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    /**
     * Valide un mot de passe.
     * Le mot de passe doit avoir au moins 4 caractères.
     * @param password Le mot de passe à valider
     * @return true si le mot de passe est valide
     */
    public static boolean isValidPassword(String password) {
        return isNotEmpty(password) && password.length() >= 4;
    }
    
    /**
     * Valide un login utilisateur.
     * Le login doit avoir entre 3 et 50 caractères alphanumériques.
     * @param login Le login à valider
     * @return true si le login est valide
     */
    public static boolean isValidLogin(String login) {
        if (isEmpty(login)) {
            return false;
        }
        String trimmed = login.trim();
        return trimmed.length() >= 3 && trimmed.length() <= 50 
               && trimmed.matches("^[a-zA-Z0-9_]+$");
    }
    
    /**
     * Alias pour isValidLogin - Valide un nom d'utilisateur.
     * @param username Le nom d'utilisateur à valider
     * @return true si le nom d'utilisateur est valide
     */
    public static boolean isValidUsername(String username) {
        return isValidLogin(username);
    }
}
