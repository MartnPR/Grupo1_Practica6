package pkDataAccess.pkDAO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import pkDataAccess.pkDTO.UsuarioPoliComedor;
import pkDataAccess.pkDTO.UsuarioTipoDTO;
import pkDataAccess.pkIDataAccess.IUsuarioDataHelper;

public class UsuarioPoliComedorDAO implements IUsuarioDataHelper {

    private final ConnectionDB connectionDB;
    private static final Object ADMIN_LOCK = new Object();
    private static final String COLUMNS = "u.idusuario AS ID_USUARIO, "
            + "u.nombre AS NOMBRE, u.apellido AS APELLIDO, "
            + "u.contrasenia AS CONTRASENIA, "
            + "u.idusuariotipo AS ID_USUARIO_TIPO, "
            + "u.idusuariotipo AS TIPO_USUARIO";
    private static final String FROM = " FROM usuario u";
    private static final String SUBTIPOS = " LEFT JOIN estudiante e "
            + "ON e.idusuario = u.idusuario "
            + "LEFT JOIN profesor p ON p.idusuario = u.idusuario";

    public UsuarioPoliComedorDAO() {
        this(ConnectionDB.fromEnvironment());
    }

    public UsuarioPoliComedorDAO(ConnectionDB connectionDB) {
        if (connectionDB == null) {
            throw new IllegalArgumentException("connectionDB must not be null");
        }
        this.connectionDB = connectionDB;
    }

    @Override
    public void agregar(UsuarioPoliComedor object) {
        execute("INSERT INTO usuario "
                + "(idusuario, nombre, apellido, contrasenia, idusuariotipo) "
                + "VALUES (?, ?, ?, ?, ?)",
                object.getIdUsuario(), object.getNombre(), object.getApellido(),
                object.getContrasenia(), object.getIdUsuarioTipo());
    }

    @Override
    public UsuarioPoliComedor buscarPorId(String id) {
        List<UsuarioPoliComedor> result = query(
                "SELECT " + COLUMNS + FROM
                        + " WHERE u.idusuario = ?",
                id);
        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    public List<UsuarioPoliComedor> leerTodos() {
        return query("SELECT " + COLUMNS + FROM
                + " ORDER BY u.idusuario", null);
    }

    @Override
    public boolean actualizar(UsuarioPoliComedor object) {
        return execute("UPDATE usuario SET nombre = ?, apellido = ?, "
                + "contrasenia = ?, idusuariotipo = ? "
                + "WHERE idusuario = ?",
                object.getNombre(), object.getApellido(), object.getContrasenia(),
                object.getIdUsuarioTipo(), object.getIdUsuario()) > 0;
    }

    @Override
    public UsuarioPoliComedor eliminar(String id) {
        UsuarioPoliComedor result = buscarPorId(id);
        if (result != null) {
            execute("DELETE FROM usuario WHERE idusuario = ?", id);
        }
        return result;
    }

    @Override
    public UsuarioPoliComedor buscarPorTipo(String tipo) {
        List<UsuarioPoliComedor> result = query(
                "SELECT " + COLUMNS + FROM
                        + " WHERE LOWER(TRIM(u.idusuariotipo)) = LOWER(TRIM(?))",
                tipo);
        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    public UsuarioPoliComedor buscarPorCodigoUnico(String codigo) {
        List<UsuarioPoliComedor> result = query(
                "SELECT " + COLUMNS + FROM + SUBTIPOS
                        + " WHERE e.codigo_unico = ?",
                codigoUnico(codigo));
        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    public UsuarioPoliComedor buscarPorCedula(String cedula) {
        List<UsuarioPoliComedor> result = query(
                "SELECT " + COLUMNS + FROM + SUBTIPOS
                        + " WHERE p.cedula = ?",
                cedula);
        return result.isEmpty() ? null : result.get(0);
    }

    public boolean crearCuenta(
            String credencial,
            String nombre,
            String apellido,
            String contrasenia) {
        return crearCuenta(
                credencial, nombre, apellido, contrasenia, "usuario");
    }

    public boolean crearCuenta(
            String credencial,
            String nombre,
            String apellido,
            String contrasenia,
            String tipoUsuario) {
        if (credencial == null || credencial.isBlank()
                || nombre == null || nombre.isBlank()
                || apellido == null || apellido.isBlank()
                || contrasenia == null || contrasenia.isBlank()) {
            return false;
        }
        if (credencial.trim().length() > 16
                || nombre.trim().length() > 100
                || apellido.trim().length() > 100
                || contrasenia.length() > 100
                || buscarPorId(credencial.trim()) != null) {
            return false;
        }
        UsuarioTipoDAO usuarioTipoDAO = new UsuarioTipoDAO(connectionDB);
        UsuarioTipoDTO tipo = usuarioTipoDAO.buscarPorDescripcion(tipoUsuario);
        if (tipo == null) {
            tipo = usuarioTipoDAO.buscarPorId(tipoUsuario);
        }
        if (tipo == null) {
            return false;
        }
        synchronized (ADMIN_LOCK) {
            if ("administrador".equalsIgnoreCase(tipo.getDescripcion().trim())
                    && contarPorTipo(tipo.getIdUsuarioTipo()) > 0) {
                return false;
            }

            return execute(
                    "INSERT INTO usuario "
                            + "(idusuario, nombre, apellido, contrasenia, idusuariotipo) "
                            + "VALUES (?, ?, ?, ?, ?)",
                    credencial.trim(),
                    nombre.trim(),
                    apellido.trim(),
                    contrasenia,
                    tipo.getIdUsuarioTipo()) > 0;
        }
    }

    public int getCount() {
        return aggregate("SELECT COUNT(*) FROM usuario");
    }

    public int contarPorTipo(String idUsuarioTipo) {
        return aggregate(
                "SELECT COUNT(*) FROM usuario "
                        + "WHERE idusuariotipo = ?",
                idUsuarioTipo);
    }

    private int execute(String sql, Object... parameters) {
        try {
            return connectionDB.executeUpdate(sql, parameters);
        } catch (SQLException exception) {
            throw error(sql, exception);
        }
    }

    private List<UsuarioPoliComedor> query(String sql, Object parameter) {
        List<UsuarioPoliComedor> result = new ArrayList<>();
        try {
            Object[] parameters = parameter instanceof Object[] values
                    ? values
                    : new Object[]{parameter};
            result = connectionDB.query(sql, rows -> {
                UsuarioPoliComedor item = new UsuarioPoliComedor();
                item.setIdUsuario(rows.getString("ID_USUARIO"));
                item.setNombre(rows.getString("NOMBRE"));
                item.setApellido(rows.getString("APELLIDO"));
                item.setContrasenia(rows.getString("CONTRASENIA"));
                item.setIdUsuarioTipo(rows.getString("ID_USUARIO_TIPO"));
                item.setUsuarioTipo(new UsuarioTipoDTO(
                        rows.getString("ID_USUARIO_TIPO"),
                        rows.getString("TIPO_USUARIO")));
                return item;
            }, parameters);
        } catch (SQLException exception) {
            throw error(sql, exception);
        }
        return result;
    }

    private int aggregate(String sql, Object... parameters) {
        try {
            return connectionDB.aggregate(sql, parameters);
        } catch (SQLException exception) {
            throw error(sql, exception);
        }
    }

    private Integer codigoUnico(String codigo) {
        try {
            return Integer.valueOf(codigo);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    "codigoUnico must be a valid integer", exception);
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
