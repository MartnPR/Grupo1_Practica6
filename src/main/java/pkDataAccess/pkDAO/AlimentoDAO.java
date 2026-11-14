package pkDataAccess.pkDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import pkDataAccess.pkDTO.Alimento;
import pkDataAccess.pkIDataAccess.IAlimentoDataHelper;

/**
 * DAO encargado de las operaciones de persistencia de los alimentos.
 */
public class AlimentoDAO implements IAlimentoDataHelper {

    private final ConnectionDB connectionDB;

    /**
     * Crea un DAO usando la configuración predeterminada de Food4All.
     */
    public AlimentoDAO() {
        this(new ConnectionDB(
                "jdbc:oracle:thin:@localhost:1521:xe",
                "oracle.jdbc.OracleDriver",
                "food4all",
                "food4all"));
    }

    /**
     * Crea un DAO usando una conexión configurable.
     *
     * @param connectionDB administrador de la conexión JDBC
     */
    public AlimentoDAO(ConnectionDB connectionDB) {
        if (connectionDB == null) {
            throw new IllegalArgumentException("connectionDB must not be null");
        }
        this.connectionDB = connectionDB;
    }

    /**
     * Inserta un alimento en la base de datos.
     *
     * @param object alimento que se desea insertar
     */
    @Override
    public void agregar(Alimento object) {
        String sql = "INSERT INTO ALIMENTO "
                + "(ID_ALIMENTO, NOMBRE, TIPO, DESCRIPCION, CANTIDAD_EN_STOCK, PRECIO_UNITARIO) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = connectionDB.connect();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, object.getIdAlimento());
            statement.setString(2, object.getNombre());
            statement.setString(3, object.getTipo());
            statement.setString(4, object.getDescripcion());
            statement.setInt(5, object.getCantidadEnStock());
            statement.setDouble(6, object.getPrecioUnitario());
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw error(sql, exception);
        } finally {
            closeConnection();
        }
    }

    /**
     * Busca un alimento por su identificador.
     *
     * @param id identificador del alimento
     * @return alimento encontrado o {@code null} si no existe
     */
    @Override
    public Alimento buscarPorId(Integer id) {
        String sql = "SELECT ID_ALIMENTO, NOMBRE, TIPO, DESCRIPCION, "
                + "CANTIDAD_EN_STOCK, PRECIO_UNITARIO "
                + "FROM ALIMENTO WHERE ID_ALIMENTO = ?";
        List<Alimento> result = query(sql, id);
        return result.isEmpty() ? null : result.get(0);
    }

    /**
     * Obtiene todos los alimentos ordenados por identificador.
     *
     * @return lista de alimentos registrados
     */
    @Override
    public List<Alimento> leerTodos() {
        return query("SELECT ID_ALIMENTO, NOMBRE, TIPO, DESCRIPCION, "
                + "CANTIDAD_EN_STOCK, PRECIO_UNITARIO "
                + "FROM ALIMENTO ORDER BY ID_ALIMENTO", null);
    }

    /**
     * Actualiza los datos de un alimento existente.
     *
     * @param object alimento con los valores actualizados
     * @return {@code true} si se actualizó un registro
     */
    @Override
    public boolean actualizar(Alimento object) {
        String sql = "UPDATE ALIMENTO SET NOMBRE = ?, TIPO = ?, DESCRIPCION = ?, "
                + "CANTIDAD_EN_STOCK = ?, PRECIO_UNITARIO = ? WHERE ID_ALIMENTO = ?";

        try (Connection connection = connectionDB.connect();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, object.getNombre());
            statement.setString(2, object.getTipo());
            statement.setString(3, object.getDescripcion());
            statement.setInt(4, object.getCantidadEnStock());
            statement.setDouble(5, object.getPrecioUnitario());
            statement.setInt(6, object.getIdAlimento());
            return statement.executeUpdate() > 0;
        } catch (SQLException exception) {
            throw error(sql, exception);
        } finally {
            closeConnection();
        }
    }

    /**
     * Elimina un alimento y devuelve el objeto eliminado.
     *
     * @param id identificador del alimento
     * @return alimento eliminado o {@code null} si no existe
     */
    @Override
    public Alimento eliminar(Integer id) {
        Alimento result = buscarPorId(id);
        if (result == null) {
            return null;
        }

        String sql = "DELETE FROM ALIMENTO WHERE ID_ALIMENTO = ?";
        try (Connection connection = connectionDB.connect();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
            return result;
        } catch (SQLException exception) {
            throw error(sql, exception);
        } finally {
            closeConnection();
        }
    }

    /**
     * Busca alimentos por tipo.
     *
     * @param categoria tipo de alimento
     * @return alimentos que pertenecen al tipo indicado
     */
    @Override
    public List<Alimento> buscarPorTipo(String categoria) {
        return query("SELECT ID_ALIMENTO, NOMBRE, TIPO, DESCRIPCION, "
                + "CANTIDAD_EN_STOCK, PRECIO_UNITARIO FROM ALIMENTO "
                + "WHERE TIPO = ? ORDER BY ID_ALIMENTO", categoria);
    }

    /**
     * Obtiene la cantidad total de alimentos registrados.
     *
     * @return cantidad de alimentos
     */
    public int getCount() {
        return aggregate("SELECT COUNT(*) FROM ALIMENTO");
    }

    /**
     * Obtiene el precio unitario mínimo registrado.
     *
     * @return precio mínimo convertido a entero
     */
    public int getMin() {
        return aggregate("SELECT MIN(PRECIO_UNITARIO) FROM ALIMENTO");
    }

    /**
     * Obtiene el precio unitario máximo registrado.
     *
     * @return precio máximo convertido a entero
     */
    public int getMax() {
        return aggregate("SELECT MAX(PRECIO_UNITARIO) FROM ALIMENTO");
    }

    /**
     * Ejecuta una consulta y convierte sus filas en objetos Alimento.
     *
     * @param sql sentencia SQL
     * @param parameter parámetro opcional de la consulta
     * @return alimentos obtenidos
     */
    private List<Alimento> query(String sql, Object parameter) {
        List<Alimento> result = new ArrayList<>();

        try (Connection connection = connectionDB.connect();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            if (parameter != null) {
                statement.setObject(1, parameter);
            }

            try (ResultSet rows = statement.executeQuery()) {
                while (rows.next()) {
                    Alimento item = new Alimento();
                    item.setIdAlimento(rows.getInt("ID_ALIMENTO"));
                    item.setNombre(rows.getString("NOMBRE"));
                    item.setTipo(rows.getString("TIPO"));
                    item.setDescripcion(rows.getString("DESCRIPCION"));
                    item.setCantidadEnStock(rows.getInt("CANTIDAD_EN_STOCK"));
                    item.setPrecioUnitario(rows.getDouble("PRECIO_UNITARIO"));
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

    /**
     * Cierra la conexión administrada por el DAO.
     */
    private void closeConnection() {
        try {
            connectionDB.close();
        } catch (SQLException exception) {
            throw error("close connection", exception);
        }
    }

    /**
     * Construye una excepción uniforme para errores de base de datos.
     *
     * @param sql sentencia que produjo el error
     * @param exception excepción original
     * @return excepción de ejecución con contexto
     */
    private IllegalStateException error(String sql, SQLException exception) {
        return new IllegalStateException("Database operation failed: " + sql, exception);
    }
}
