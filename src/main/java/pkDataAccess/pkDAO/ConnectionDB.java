package pkDataAccess.pkDAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ConnectionDB {
    private final String url;
    private final String driver;
    private final String user;
    private final String password;
    private Connection connection;

    public ConnectionDB(String url, String user, String password) {
        this(url, null, user, password);
    }

    public ConnectionDB(String url, String driver, String user, String password) {
        this.url = url;
        this.driver = driver;
        this.user = user;
        this.password = password;
    }

    /**
     * Creates a connection using Neon/PostgreSQL environment variables.
     * DATABASE_URL is preferred; PGHOST, PGPORT, PGDATABASE, PGUSER and
     * PGPASSWORD are supported for environments that expose components.
     *
     * @return configured database connection manager
     */
    public static ConnectionDB fromEnvironment() {
        String databaseUrl = value("DATABASE_URL");
        String user = value("PGUSER");
        String password = value("PGPASSWORD");

        if (databaseUrl != null) {
            // Si la URL ya incluye "user=" adentro, no pasamos usuario/clave por separado
            if (databaseUrl.contains("user=")) {
                return new ConnectionDB(databaseUrl, "org.postgresql.Driver", null, null);
            }
            // Si la URL está limpia, le pasamos PGUSER y PGPASSWORD
            return new ConnectionDB(databaseUrl, "org.postgresql.Driver", user, password);
        }

        String host = requiredValue("PGHOST");
        String port = valueOrDefault("PGPORT", "5432");
        String database = requiredValue("PGDATABASE");
        String reqUser = requiredValue("PGUSER");
        String reqPassword = requiredValue("PGPASSWORD");

        String url = "jdbc:postgresql://" + host + ":" + port + "/" + database + "?sslmode=require";
        return new ConnectionDB(url, "org.postgresql.Driver", reqUser, reqPassword);
    }

    public Connection connect() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            throw new SQLException("A database connection is already open.");
        }
        if (driver != null && !driver.isBlank()) {
            try {
                Class.forName(driver);
            } catch (ClassNotFoundException exception) {
                throw new SQLException("JDBC driver not found: " + driver, exception);
            }
        }
        connection = DriverManager.getConnection(url, user, password);
        return connection;
    }

    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            connection = null;
        }
    }

    private static String value(String name) {
        String systemValue = System.getProperty(name);
        if (systemValue != null && !systemValue.isBlank()) {
            return systemValue;
        }
        String environmentValue = System.getenv(name);
        if (environmentValue != null && !environmentValue.isBlank()) {
            return environmentValue;
        }
        return dotenvValue(name);
    }

    private static String requiredValue(String name) {
        String value = value(name);
        if (value == null) {
            throw new IllegalStateException(
                    "Missing database configuration. Set DATABASE_URL or " + name
                            + " and the other PG* variables.");
        }
        return value;
    }

    private static String valueOrDefault(String name, String defaultValue) {
        String value = value(name);
        return value == null ? defaultValue : value;
    }

    private static String dotenvValue(String name) {
        Path dotenv = Path.of(".env");
        if (!Files.exists(dotenv)) {
            return null;
        }
        try {
            for (String line : Files.readAllLines(dotenv)) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")
                        || !trimmed.startsWith(name + "=")) {
                    continue;
                }
                String value = trimmed.substring(name.length() + 1).trim();
                if (value.length() >= 2 && value.startsWith("\"")
                        && value.endsWith("\"")) {
                    value = value.substring(1, value.length() - 1);
                }
                return value.isBlank() ? null : value;
            }
            return null;
        } catch (IOException exception) {
            throw new IllegalStateException("Could not read .env configuration file.", exception);
        }
    }
}
