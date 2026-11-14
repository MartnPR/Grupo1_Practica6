package pkDataAccess.pkDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
        this(new ConnectionDB("jdbc:oracle:thin:@localhost:1521:xe",
                "oracle.jdbc.OracleDriver", "food4all", "food4all"));
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
                codigo);
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
        try (Connection connection = connectionDB.connect();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int i = 0; i < parameters.length; i++) {
                statement.setObject(i + 1, parameters[i]);
            }
            return statement.executeUpdate();
        } catch (SQLException exception) {
            throw error(sql, exception);
        } finally {
            closeConnection();
        }
    }

    /** Ejecuta una consulta y convierte sus filas en objetos de usuario. */
    private List<UsuarioPoliComedor> query(String sql, Object parameter) {
        List<UsuarioPoliComedor> result = new ArrayList<>();

        try (Connection connection = connectionDB.connect();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            if (parameter != null) {
                statement.setObject(1, parameter);
            }

            try (ResultSet rows = statement.executeQuery()) {
                while (rows.next()) {
                    UsuarioPoliComedor item = new UsuarioPoliComedor();
                    item.setIdUsuario(rows.getString("ID_USUARIO"));
                    item.setNombre(rows.getString("NOMBRE"));
                    item.setApellido(rows.getString("APELLIDO"));
                    item.setContrasenia(rows.getString("CONTRASENIA"));
                    item.setTipo(rows.getString("TIPO_USUARIO"));

                    String codigo = rows.getString("CODIGO_UNICO");
                    if (item.getApellido() == null) {
                        item.setApellido(codigo == null
                                ? rows.getString("CEDULA") : codigo);
                    }
                    result.add(item);
                }
            }
        } catch (SQLException exception) {
            throw error(sql, exception);
        } finally {
            closeConnection();
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
        try (Connection connection = connectionDB.connect();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet rows = statement.executeQuery()) {
            return rows.next() ? rows.getInt(1) : 0;
        } catch (SQLException exception) {
            throw error(sql, exception);
        } finally {
            closeConnection();
        }
    }

    /** Cierra la conexión administrada por el DAO. */
    private void closeConnection() {
        try {
            connectionDB.close();
        } catch (SQLException exception) {
            throw error("close connection", exception);
        }
    }

    /** Construye una excepción uniforme para errores de base de datos. */
    private IllegalStateException error(String sql, SQLException exception) {
        return new IllegalStateException(
                "Database operation failed: " + sql, exception);
    }
}
