package pkDataAccess.pkDAO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import pkDataAccess.pkDTO.UsuarioTipo;
import pkDataAccess.pkIDataAccess.IUsuarioTipoDataHelper;

public class UsuarioTipoDAO implements IUsuarioTipoDataHelper {
    private final ConnectionDB connectionDB;

    public UsuarioTipoDAO() {
        this(ConnectionDB.fromEnvironment());
    }

    public UsuarioTipoDAO(ConnectionDB connectionDB) {
        if (connectionDB == null) {
            throw new IllegalArgumentException("connectionDB must not be null");
        }
        this.connectionDB = connectionDB;
    }

    @Override
    public void agregar(UsuarioTipo object) {
        execute("INSERT INTO usuario_tipo "
                + "(idusuariotipo, descripcion) VALUES (?, ?)",
                object.getIdUsuarioTipo(), object.getDescripcion());
    }

    @Override
    public UsuarioTipo buscarPorId(String id) {
        List<UsuarioTipo> result = query(
                "SELECT idusuariotipo AS ID_USUARIO_TIPO, "
                        + "descripcion AS DESCRIPCION "
                        + "FROM usuario_tipo WHERE idusuariotipo = ?",
                id);
        return result.isEmpty() ? tipoConocido(id) : result.get(0);
    }

    public UsuarioTipo buscarPorDescripcion(String descripcion) {
        List<UsuarioTipo> result = query(
                "SELECT idusuariotipo AS ID_USUARIO_TIPO, "
                        + "descripcion AS DESCRIPCION "
                        + "FROM usuario_tipo "
                        + "WHERE LOWER(TRIM(descripcion)) = LOWER(TRIM(?))",
                descripcion);
        if (!result.isEmpty()) {
            return result.get(0);
        }
        return tipoConocido(descripcion);
    }

    @Override
    public List<UsuarioTipo> leerTodos() {
        List<UsuarioTipo> result = query(
                "SELECT idusuariotipo AS ID_USUARIO_TIPO, "
                        + "descripcion AS DESCRIPCION "
                        + "FROM usuario_tipo ORDER BY idusuariotipo",
                null);
        return result.isEmpty()
                ? List.of(
                        tipoConocido("usuario"),
                        tipoConocido("administrador"),
                        tipoConocido("cajero"))
                : result;
    }

    @Override
    public boolean actualizar(UsuarioTipo object) {
        return execute(
                "UPDATE usuario_tipo SET descripcion = ? "
                        + "WHERE idusuariotipo = ?",
                object.getDescripcion(), object.getIdUsuarioTipo()) > 0;
    }

    @Override
    public UsuarioTipo eliminar(String id) {
        UsuarioTipo result = buscarPorId(id);
        if (result != null) {
            execute("DELETE FROM usuario_tipo WHERE idusuariotipo = ?", id);
        }
        return result;
    }

    private List<UsuarioTipo> query(String sql, Object parameter) {
        try {
            return connectionDB.query(sql, rows -> new UsuarioTipo(
                    rows.getString("ID_USUARIO_TIPO"),
                    rows.getString("DESCRIPCION")),
                    parameter);
        } catch (SQLException exception) {
            if ("42P01".equals(exception.getSQLState())
                    || "42501".equals(exception.getSQLState())) {
                return new ArrayList<>();
            }
            throw error(sql, exception);
        }
    }

    private UsuarioTipo tipoConocido(String valor) {
        if (valor == null) {
            return null;
        }
        String tipo = valor.trim().toLowerCase();
        if ("usuario".equals(tipo)
                || "personal".equals(tipo)
                || "estudiante".equals(tipo)
                || "profesor".equals(tipo)
                || "profesor o estudiante".equals(tipo)
                || "1".equals(tipo)) {
            return new UsuarioTipo("1", "Usuario");
        }
        if ("administrador".equals(tipo)
                || "admin".equals(tipo)
                || "2".equals(tipo)) {
            return new UsuarioTipo("2", "Administrador");
        }
        if ("cajero".equals(tipo)
                || "cashier".equals(tipo)
                || "3".equals(tipo)) {
            return new UsuarioTipo("3", "Cajero");
        }
        return null;
    }

    private int execute(String sql, Object... parameters) {
        try {
            return connectionDB.executeUpdate(sql, parameters);
        } catch (SQLException exception) {
            throw error(sql, exception);
        }
    }

    private IllegalStateException error(String sql, SQLException exception) {
        return new IllegalStateException(
                "Database operation failed: " + sql
                        + ". SQLState=" + exception.getSQLState()
                        + ". " + exception.getMessage(),
                exception);
    }
}
