package restaurant.app.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Classe utilitaire pour le formatage des valeurs monétaires en Franc CFA.
 * 
 * @author Restaurant App
 * @version 1.0
 */
public class FormatUtil {
    
    private static final DecimalFormat CURRENCY_FORMAT;
    
    static {
        // Configuration du format FCFA
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.FRANCE);
        symbols.setGroupingSeparator(' ');
        CURRENCY_FORMAT = new DecimalFormat("#,##0 FCFA", symbols);
    }
    
    /**
     * Formate un montant en Franc CFA.
     * @param amount Le montant à formater
     * @return Le montant formaté (ex: "1 500 FCFA")
     */
    public static String formatCurrency(double amount) {
        return CURRENCY_FORMAT.format(amount);
    }
    
    /**
     * Formate un montant BigDecimal en Franc CFA.
     * @param amount Le montant à formater
     * @return Le montant formaté (ex: "1 500 FCFA")
     */
    public static String formatCurrency(BigDecimal amount) {
        if (amount == null) {
            return formatCurrency(0);
        }
        return CURRENCY_FORMAT.format(amount);
    }
    
    /**
     * Formate un montant en Franc CFA (version courte pour grands montants).
     * @param amount Le montant à formater
     * @return Le montant formaté (ex: "1.5K FCFA" pour 1500)
     */
    public static String formatCurrencyShort(double amount) {
        if (amount >= 1_000_000) {
            return String.format("%.1fM FCFA", amount / 1_000_000);
        } else if (amount >= 1_000) {
            return String.format("%.1fK FCFA", amount / 1_000);
        }
        return formatCurrency(amount);
    }
    
    /**
     * Formate un montant BigDecimal en Franc CFA (version courte).
     * @param amount Le montant à formater
     * @return Le montant formaté
     */
    public static String formatCurrencyShort(BigDecimal amount) {
        if (amount == null) {
            return formatCurrencyShort(0);
        }
        return formatCurrencyShort(amount.doubleValue());
    }
}
