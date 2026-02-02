package restaurant.app.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Classe utilitaire pour la sécurité et le hashage des mots de passe.
 * Utilise SHA-256 pour le hashage des mots de passe.
 * 
 * @author Restaurant App
 * @version 1.0
 */
public class SecurityUtil {
    
    private static final String HASH_ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16;
    
    /**
     * Constructeur privé pour empêcher l'instanciation.
     */
    private SecurityUtil() {
        throw new UnsupportedOperationException("Classe utilitaire - ne peut pas être instanciée");
    }
    
    /**
     * Hash un mot de passe avec SHA-256.
     * @param password Le mot de passe en clair
     * @return Le hash du mot de passe en hexadécimal
     */
    public static String hashPassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Le mot de passe ne peut pas être vide");
        }
        
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] hashBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Algorithme de hashage non disponible: " + HASH_ALGORITHM, e);
        }
    }
    
    /**
     * Hash un mot de passe avec un sel.
     * @param password Le mot de passe en clair
     * @param salt Le sel
     * @return Le hash du mot de passe
     */
    public static String hashPasswordWithSalt(String password, String salt) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Le mot de passe ne peut pas être vide");
        }
        
        String saltedPassword = salt + password;
        return hashPassword(saltedPassword);
    }
    
    /**
     * Génère un sel aléatoire.
     * @return Le sel encodé en Base64
     */
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
    
    /**
     * Vérifie si un mot de passe correspond à un hash.
     * @param password Le mot de passe en clair
     * @param hashedPassword Le hash à comparer
     * @return true si le mot de passe correspond
     */
    public static boolean verifyPassword(String password, String hashedPassword) {
        if (password == null || hashedPassword == null) {
            return false;
        }
        
        String computedHash = hashPassword(password);
        return computedHash.equals(hashedPassword);
    }
    
    /**
     * Vérifie si un mot de passe correspond à un hash avec sel.
     * @param password Le mot de passe en clair
     * @param hashedPassword Le hash à comparer
     * @param salt Le sel utilisé
     * @return true si le mot de passe correspond
     */
    public static boolean verifyPasswordWithSalt(String password, String hashedPassword, String salt) {
        if (password == null || hashedPassword == null || salt == null) {
            return false;
        }
        
        String computedHash = hashPasswordWithSalt(password, salt);
        return computedHash.equals(hashedPassword);
    }
    
    /**
     * Convertit un tableau de bytes en chaîne hexadécimale.
     * @param bytes Le tableau de bytes
     * @return La représentation hexadécimale
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
    
    /**
     * Vérifie la force d'un mot de passe.
     * @param password Le mot de passe à évaluer
     * @return Le niveau de force (0-4: Très faible à Très fort)
     */
    public static int getPasswordStrength(String password) {
        if (password == null || password.isEmpty()) {
            return 0;
        }
        
        int strength = 0;
        
        // Longueur minimale
        if (password.length() >= 6) strength++;
        if (password.length() >= 8) strength++;
        
        // Contient des chiffres
        if (password.matches(".*\\d.*")) strength++;
        
        // Contient des majuscules et minuscules
        if (password.matches(".*[a-z].*") && password.matches(".*[A-Z].*")) strength++;
        
        // Contient des caractères spéciaux
        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) strength++;
        
        return Math.min(strength, 4);
    }
    
    /**
     * Retourne une description de la force du mot de passe.
     * @param strength Le niveau de force (0-4)
     * @return La description textuelle
     */
    public static String getPasswordStrengthLabel(int strength) {
        switch (strength) {
            case 0: return "Très faible";
            case 1: return "Faible";
            case 2: return "Moyen";
            case 3: return "Fort";
            case 4: return "Très fort";
            default: return "Inconnu";
        }
    }
    
    /**
     * Valide les critères minimaux d'un mot de passe.
     * @param password Le mot de passe à valider
     * @return true si le mot de passe est valide
     */
    public static boolean isValidPassword(String password) {
        if (password == null) {
            return false;
        }
        // Minimum 4 caractères pour cette application simple
        return password.length() >= 4;
    }
    
    /**
     * Génère un mot de passe aléatoire.
     * @param length La longueur souhaitée
     * @return Le mot de passe généré
     */
    public static String generateRandomPassword(int length) {
        if (length < 4) {
            length = 8;
        }
        
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();
        
        for (int i = 0; i < length; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return password.toString();
    }
}
