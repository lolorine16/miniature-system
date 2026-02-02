package restaurant.app.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Gestionnaire de connexion à la base de données MySQL.
 * Implémente le pattern Singleton pour une gestion centralisée des connexions.
 * 
 * @author Restaurant App
 * @version 1.0
 */
public class DatabaseConnection {
    
    private static DatabaseConnection instance;
    private Connection connection;
    
    private String url;
    private String user;
    private String password;
    private String driver;
    
    /**
     * Constructeur privé - charge la configuration depuis le fichier properties.
     */
    private DatabaseConnection() {
        loadConfiguration();
    }
    
    /**
     * Charge la configuration de la base de données depuis le fichier config.properties.
     */
    private void loadConfiguration() {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("restaurant/app/config.properties")) {
            if (input != null) {
                props.load(input);
                this.url = props.getProperty("db.url", "jdbc:mysql://localhost:3306/restaurant_db");
                this.user = props.getProperty("db.user", "root");
                this.password = props.getProperty("db.password", "");
                this.driver = props.getProperty("db.driver", "com.mysql.cj.jdbc.Driver");
            } else {
                // Valeurs par défaut si le fichier n'existe pas
                this.url = "jdbc:mysql://localhost:3306/restaurant_db";
                this.user = "root";
                this.password = "";
                this.driver = "com.mysql.cj.jdbc.Driver";
            }
        } catch (IOException e) {
            // Utiliser les valeurs par défaut
            this.url = "jdbc:mysql://localhost:3306/restaurant_db";
            this.user = "root";
            this.password = "";
            this.driver = "com.mysql.cj.jdbc.Driver";
        }
    }
    
    /**
     * Retourne l'instance unique du gestionnaire de connexion.
     * @return L'instance DatabaseConnection
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
    
    /**
     * Établit et retourne une connexion à la base de données.
     * @return La connexion active
     * @throws SQLException en cas d'erreur de connexion
     */
    public Connection getConnection() throws SQLException {
        try {
            // Charger le driver JDBC
            Class.forName(driver);
            
            // Vérifier si la connexion est valide
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(url, user, password);
            }
            
            return connection;
            
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver MySQL non trouvé: " + e.getMessage(), e);
        }
    }
    
    /**
     * Ferme la connexion active.
     */
    public void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println("Erreur lors de la fermeture de la connexion: " + e.getMessage());
            }
        }
    }
    
    /**
     * Teste la connexion à la base de données.
     * @return true si la connexion est réussie
     */
    public boolean testConnection() {
        try {
            Connection conn = getConnection();
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Erreur de connexion: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Configure manuellement les paramètres de connexion.
     * @param url L'URL de la base de données
     * @param user Le nom d'utilisateur
     * @param password Le mot de passe
     */
    public void configure(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
        // Fermer la connexion existante pour forcer une reconnexion avec les nouveaux paramètres
        closeConnection();
    }
    
    // Getters pour les paramètres de configuration
    
    public String getUrl() {
        return url;
    }
    
    public String getUser() {
        return user;
    }
}
