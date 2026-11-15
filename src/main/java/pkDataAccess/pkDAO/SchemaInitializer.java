package pkDataAccess.pkDAO;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Applies the PostgreSQL schema to the configured Neon database.
 */
public final class SchemaInitializer {

    private SchemaInitializer() {
    }

    public static void main(String[] args) throws IOException, SQLException {
        Path schemaPath = args.length == 0
                ? Path.of("storage", "Food4All.sql")
                : Path.of(args[0]);
        String schema = Files.readString(schemaPath)
                .replaceAll("(?m)--.*$", "");

        ConnectionDB connectionDB = ConnectionDB.fromEnvironment();
        try {
            Connection connection = connectionDB.connect();
            try {
                for (String statement : schema.split(";")) {
                    if (!statement.isBlank()) {
                        connection.createStatement().execute(statement);
                    }
                }
            } finally {
                connectionDB.close();
            }
        } catch (SQLException | RuntimeException exception) {
            throw new SQLException("Could not apply schema from " + schemaPath, exception);
        }
    }
}
