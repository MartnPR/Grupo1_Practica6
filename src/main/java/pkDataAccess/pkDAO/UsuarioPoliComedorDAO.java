package pkDataAccess.pkDAO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import pkDataAccess.pkDTO.UsuarioPoliComedor;
import pkDataAccess.pkIDataAccess.IUsuarioDataHelper;

/**
 * DAO encargado de las operaciones de persistencia de los usuarios.
 */
public class UsuarioPoliComedorDAO implements IUsuarioDataHelper {

    private final ConnectionDB connectionDB;
    private static final String COLUMNS = "u.ID_USUARIO, u.NOMBRE, u.APELLIDO, "
            + "u.CONTRASENIA, u.TIPO_USUARIO, e.CODIGO_UNICO, p.CEDULA";
    private static final String FROM = " FROM USUARIO u "
            + "LEFT JOIN ESTUDIANTE e ON e.ID_USUARIO = u.ID_USUARIO "
            + "LEFT JOIN PROFESOR p ON p.ID_USUARIO = u.ID_USUARIO";

    /** Crea un DAO usando la configuración predeterminada de Food4All. */
    public UsuarioPoliComedorDAO() {
        this(ConnectionDB.fromEnvironment());
    }

    /**
     * Crea un DAO usando una conexión configurable.
     *
     * @param connectionDB administrador de la conexión JDBC
     */
    public UsuarioPoliComedorDAO(ConnectionDB connectionDB) {
        if (connectionDB == null) {
            throw new IllegalArgumentException("connectionDB must not be null");
        }
        this.connectionDB = connectionDB;
    }

    /** Inserta un usuario en la tabla de la superclase. */
    @Override
    public void agregar(UsuarioPoliComedor object) {
        execute("INSERT INTO USUARIO "
                + "(ID_USUARIO, NOMBRE, APELLIDO, CONTRASENIA, TIPO_USUARIO) "
                + "VALUES (?, ?, ?, ?, ?)",
                object.getIdUsuario(), object.getNombre(), object.getApellido(),
                object.getContrasenia(), object.getTipo());
    }

    /** Busca un usuario por su identificador. */
    @Override
    public UsuarioPoliComedor buscarPorId(String id) {
        List<UsuarioPoliComedor> result = query(
                "SELECT " + COLUMNS + FROM
                        + " WHERE u.ID_USUARIO = ?",
                id);
        return result.isEmpty() ? null : result.get(0);
    }

    /** Obtiene todos los usuarios ordenados por identificador. */
    @Override
    public List<UsuarioPoliComedor> leerTodos() {
        return query("SELECT " + COLUMNS + FROM
                + " ORDER BY u.ID_USUARIO", null);
    }

    /** Actualiza los datos comunes de un usuario. */
    @Override
    public boolean actualizar(UsuarioPoliComedor object) {
        return execute("UPDATE USUARIO SET NOMBRE = ?, APELLIDO = ?, "
                + "CONTRASENIA = ?, TIPO_USUARIO = ? WHERE ID_USUARIO = ?",
                object.getNombre(), object.getApellido(), object.getContrasenia(),
                object.getTipo(), object.getIdUsuario()) > 0;
    }

    /** Elimina un usuario y devuelve el objeto eliminado. */
    @Override
    public UsuarioPoliComedor eliminar(String id) {
        UsuarioPoliComedor result = buscarPorId(id);
        if (result != null) {
            execute("DELETE FROM USUARIO WHERE ID_USUARIO = ?", id);
        }
        return result;
    }

    /** Busca el primer usuario que coincide con un tipo. */
    @Override
    public UsuarioPoliComedor buscarPorTipo(String tipo) {
        List<UsuarioPoliComedor> result = query(
                "SELECT " + COLUMNS + FROM
                        + " WHERE u.TIPO_USUARIO = ?",
                tipo);
        return result.isEmpty() ? null : result.get(0);
    }

    /** Busca un usuario por el código único de estudiante. */
    @Override
    public UsuarioPoliComedor buscarPorCodigoUnico(String codigo) {
        List<UsuarioPoliComedor> result = query(
                "SELECT " + COLUMNS + FROM
                        + " WHERE e.CODIGO_UNICO = ?",
                codigoUnico(codigo));
        return result.isEmpty() ? null : result.get(0);
    }

    /** Busca un usuario por la cédula de profesor. */
    @Override
    public UsuarioPoliComedor buscarPorCedula(String cedula) {
        List<UsuarioPoliComedor> result = query(
                "SELECT " + COLUMNS + FROM
                        + " WHERE p.CEDULA = ?",
                cedula);
        return result.isEmpty() ? null : result.get(0);
    }

    /**
     * Obtiene la cantidad total de usuarios registrados.
     *
     * @return cantidad de usuarios
     */
    public int getCount() {
        return aggregate("SELECT COUNT(*) FROM USUARIO");
    }

    /** Ejecuta una sentencia de modificación parametrizada. */
    private int execute(String sql, Object... parameters) {
        try {
            return connectionDB.executeUpdate(sql, parameters);
        } catch (SQLException exception) {
            throw error(sql, exception);
        }
    }

    /** Ejecuta una consulta y convierte sus filas en objetos de usuario. */
    private List<UsuarioPoliComedor> query(String sql, Object parameter) {
        List<UsuarioPoliComedor> result = new ArrayList<>();

        try {
            result = connectionDB.query(sql, rows -> {
                    UsuarioPoliComedor item = new UsuarioPoliComedor();
                    item.setIdUsuario(rows.getString("ID_USUARIO"));
                    item.setNombre(rows.getString("NOMBRE"));
                    item.setApellido(rows.getString("APELLIDO"));
                    item.setContrasenia(rows.getString("CONTRASENIA"));
                    item.setTipo(rows.getString("TIPO_USUARIO"));

                    return item;
                }, parameter);
        } catch (SQLException exception) {
            throw error(sql, exception);
        }
        return result;
    }

    /**
     * Ejecuta una consulta agregada que devuelve un entero.
     *
     * @param sql sentencia SQL agregada
     * @return valor agregado
     */
    private int aggregate(String sql) {
        try {
            return connectionDB.aggregate(sql);
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

    /** Cierra la conexión administrada por el DAO. */
    /** Construye una excepción uniforme para errores de base de datos. */
    private IllegalStateException error(String sql, SQLException exception) {
        return new IllegalStateException(
                "Database operation failed: " + sql, exception);
    }
}
