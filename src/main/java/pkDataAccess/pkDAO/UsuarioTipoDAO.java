package pkDataAccess.pkDAO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import pkDataAccess.pkDTO.UsuarioTipoDTO;
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
    public void agregar(UsuarioTipoDTO object) {
        execute("INSERT INTO usuario_tipo "
                + "(idusuariotipo, descripcion) VALUES (?, ?)",
                object.getIdUsuarioTipo(), object.getDescripcion());
    }

    @Override
    public UsuarioTipoDTO buscarPorId(String id) {
        List<UsuarioTipoDTO> result = query(
                "SELECT idusuariotipo AS ID_USUARIO_TIPO, "
                        + "descripcion AS DESCRIPCION "
                        + "FROM usuario_tipo WHERE idusuariotipo = ?",
                id);
        return result.isEmpty() ? tipoConocido(id) : result.get(0);
    }

    public UsuarioTipoDTO buscarPorDescripcion(String descripcion) {
        List<UsuarioTipoDTO> result = query(
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
    public List<UsuarioTipoDTO> leerTodos() {
        List<UsuarioTipoDTO> result = query(
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
    public boolean actualizar(UsuarioTipoDTO object) {
        return execute(
                "UPDATE usuario_tipo SET descripcion = ? "
                        + "WHERE idusuariotipo = ?",
                object.getDescripcion(), object.getIdUsuarioTipo()) > 0;
    }

    @Override
    public UsuarioTipoDTO eliminar(String id) {
        UsuarioTipoDTO result = buscarPorId(id);
        if (result != null) {
            execute("DELETE FROM usuario_tipo WHERE idusuariotipo = ?", id);
        }
        return result;
    }

    private List<UsuarioTipoDTO> query(String sql, Object parameter) {
        try {
            return connectionDB.query(sql, rows -> new UsuarioTipoDTO(
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

    private UsuarioTipoDTO tipoConocido(String valor) {
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
            return new UsuarioTipoDTO("1", "Usuario");
        }
        if ("administrador".equals(tipo)
                || "admin".equals(tipo)
                || "2".equals(tipo)) {
            return new UsuarioTipoDTO("2", "Administrador");
        }
        if ("cajero".equals(tipo)
                || "cashier".equals(tipo)
                || "3".equals(tipo)) {
            return new UsuarioTipoDTO("3", "Cajero");
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
