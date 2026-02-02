package restaurant.app.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

/**
 * Classe utilitaire pour la manipulation des dates.
 * Fournit des méthodes de formatage et de calcul sur les dates.
 * 
 * @author Restaurant App
 * @version 1.0
 */
public class DateUtil {
    
    // Formats de date standards
    public static final String FORMAT_DATE = "dd/MM/yyyy";
    public static final String FORMAT_DATETIME = "dd/MM/yyyy HH:mm";
    public static final String FORMAT_DATETIME_FULL = "dd/MM/yyyy HH:mm:ss";
    public static final String FORMAT_TIME = "HH:mm";
    public static final String FORMAT_DATE_SQL = "yyyy-MM-dd";
    public static final String FORMAT_DATETIME_SQL = "yyyy-MM-dd HH:mm:ss";
    
    // Formatters pré-configurés
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(FORMAT_DATE);
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern(FORMAT_DATETIME);
    private static final DateTimeFormatter DATETIME_FULL_FORMATTER = DateTimeFormatter.ofPattern(FORMAT_DATETIME_FULL);
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(FORMAT_TIME);
    
    /**
     * Constructeur privé pour empêcher l'instanciation.
     */
    private DateUtil() {
        throw new UnsupportedOperationException("Classe utilitaire - ne peut pas être instanciée");
    }
    
    /**
     * Formate une date en chaîne (dd/MM/yyyy).
     * @param date La date à formater
     * @return La chaîne formatée ou chaîne vide si null
     */
    public static String formatDate(LocalDate date) {
        if (date == null) {
            return "";
        }
        return date.format(DATE_FORMATTER);
    }
    
    /**
     * Formate une date-heure en chaîne (dd/MM/yyyy HH:mm).
     * @param dateTime La date-heure à formater
     * @return La chaîne formatée ou chaîne vide si null
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(DATETIME_FORMATTER);
    }
    
    /**
     * Formate une date-heure complète (dd/MM/yyyy HH:mm:ss).
     * @param dateTime La date-heure à formater
     * @return La chaîne formatée ou chaîne vide si null
     */
    public static String formatDateTimeFull(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(DATETIME_FULL_FORMATTER);
    }
    
    /**
     * Formate l'heure seule (HH:mm).
     * @param dateTime La date-heure
     * @return L'heure formatée
     */
    public static String formatTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(TIME_FORMATTER);
    }
    
    /**
     * Parse une chaîne en date.
     * @param dateString La chaîne à parser (dd/MM/yyyy)
     * @return La date ou null si parsing impossible
     */
    public static LocalDate parseDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(dateString.trim(), DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
    
    /**
     * Parse une chaîne en date-heure.
     * @param dateTimeString La chaîne à parser (dd/MM/yyyy HH:mm)
     * @return La date-heure ou null si parsing impossible
     */
    public static LocalDateTime parseDateTime(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateTimeString.trim(), DATETIME_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
    
    /**
     * Retourne le début de la journée (00:00:00).
     * @param date La date
     * @return Le début de la journée
     */
    public static LocalDateTime startOfDay(LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        return date.atStartOfDay();
    }
    
    /**
     * Retourne la fin de la journée (23:59:59).
     * @param date La date
     * @return La fin de la journée
     */
    public static LocalDateTime endOfDay(LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        return date.atTime(23, 59, 59);
    }
    
    /**
     * Retourne le début du mois.
     * @param date La date
     * @return Le premier jour du mois à 00:00
     */
    public static LocalDateTime startOfMonth(LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        return date.withDayOfMonth(1).atStartOfDay();
    }
    
    /**
     * Retourne la fin du mois.
     * @param date La date
     * @return Le dernier jour du mois à 23:59:59
     */
    public static LocalDateTime endOfMonth(LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        return date.withDayOfMonth(date.lengthOfMonth()).atTime(23, 59, 59);
    }
    
    /**
     * Retourne le début de l'année.
     * @param date La date
     * @return Le 1er janvier à 00:00
     */
    public static LocalDateTime startOfYear(LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        return date.withDayOfYear(1).atStartOfDay();
    }
    
    /**
     * Retourne la fin de l'année.
     * @param date La date
     * @return Le 31 décembre à 23:59:59
     */
    public static LocalDateTime endOfYear(LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        return date.withDayOfYear(date.lengthOfYear()).atTime(23, 59, 59);
    }
    
    /**
     * Calcule le nombre de jours entre deux dates.
     * @param start La date de début
     * @param end La date de fin
     * @return Le nombre de jours
     */
    public static long daysBetween(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(start, end);
    }
    
    /**
     * Vérifie si une date est aujourd'hui.
     * @param date La date à vérifier
     * @return true si c'est aujourd'hui
     */
    public static boolean isToday(LocalDate date) {
        return date != null && date.equals(LocalDate.now());
    }
    
    /**
     * Vérifie si une date-heure est aujourd'hui.
     * @param dateTime La date-heure à vérifier
     * @return true si c'est aujourd'hui
     */
    public static boolean isToday(LocalDateTime dateTime) {
        return dateTime != null && dateTime.toLocalDate().equals(LocalDate.now());
    }
    
    /**
     * Retourne une représentation relative de la date ("Aujourd'hui", "Hier", etc.).
     * @param date La date
     * @return La représentation relative
     */
    public static String getRelativeDate(LocalDate date) {
        if (date == null) {
            return "";
        }
        
        LocalDate today = LocalDate.now();
        long days = daysBetween(date, today);
        
        if (days == 0) {
            return "Aujourd'hui";
        } else if (days == 1) {
            return "Hier";
        } else if (days == -1) {
            return "Demain";
        } else if (days > 0 && days < 7) {
            return "Il y a " + days + " jours";
        } else {
            return formatDate(date);
        }
    }
    
    /**
     * Retourne la date actuelle.
     * @return La date du jour
     */
    public static LocalDate today() {
        return LocalDate.now();
    }
    
    /**
     * Retourne la date-heure actuelle.
     * @return La date-heure actuelle
     */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }
}
