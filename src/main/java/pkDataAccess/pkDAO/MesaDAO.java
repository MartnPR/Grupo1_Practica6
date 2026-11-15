package pkDataAccess.pkDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import pkDataAccess.pkDTO.Mesa;
import pkDataAccess.pkIDataAccess.IMesaDataHelper;

/**
 * DAO encargado de las operaciones de persistencia de las mesas.
 */
public class MesaDAO implements IMesaDataHelper {

    private final ConnectionDB connectionDB;

    /**
     * Crea un DAO usando la configuración predeterminada de Food4All.
     */
    public MesaDAO() {
        this(ConnectionDB.fromEnvironment());
    }

    /**
     * Crea un DAO usando una conexión configurable.
     *
     * @param connectionDB administrador de la conexión JDBC
     */
    public MesaDAO(ConnectionDB connectionDB) {
        if (connectionDB == null) {
            throw new IllegalArgumentException("connectionDB must not be null");
        }
        this.connectionDB = connectionDB;
    }

    /**
     * Inserta una mesa en la base de datos.
     *
     * @param object mesa que se desea insertar
     */
    @Override
    public void agregar(Mesa object) {
        execute("INSERT INTO MESA (ID_MESA, RESERVADA) VALUES (?, ?)",
                object.getIdMesa(), object.isReservada());
    }

    /**
     * Busca una mesa por su identificador.
     *
     * @param id identificador de la mesa
     * @return mesa encontrada o {@code null} si no existe
     */
    @Override
    public Mesa buscarPorId(Integer id) {
        List<Mesa> result = query(
                "SELECT ID_MESA, RESERVADA FROM MESA WHERE ID_MESA = ?", id);
        return result.isEmpty() ? null : result.get(0);
    }

    /**
     * Obtiene todas las mesas ordenadas por identificador.
     *
     * @return lista de mesas registradas
     */
    @Override
    public List<Mesa> leerTodos() {
        return query("SELECT ID_MESA, RESERVADA FROM MESA ORDER BY ID_MESA", null);
    }

    /**
     * Actualiza el estado de una mesa.
     *
     * @param object mesa con el estado actualizado
     * @return {@code true} si se actualizó un registro
     */
    @Override
    public boolean actualizar(Mesa object) {
        return execute("UPDATE MESA SET RESERVADA = ? WHERE ID_MESA = ?",
                object.isReservada(), object.getIdMesa()) > 0;
    }

    /**
     * Elimina una mesa y devuelve el objeto eliminado.
     *
     * @param id identificador de la mesa
     * @return mesa eliminada o {@code null} si no existe
     */
    @Override
    public Mesa eliminar(Integer id) {
        Mesa result = buscarPorId(id);
        if (result != null) {
            execute("DELETE FROM MESA WHERE ID_MESA = ?", id);
        }
        return result;
    }

    /**
     * Busca mesas según su estado de reserva.
     *
     * @param reservado estado que se desea consultar
     * @return mesas que coinciden con el estado indicado
     */
    @Override
    public List<Mesa> buscarPorEstado(boolean reservado) {
        return query("SELECT ID_MESA, RESERVADA FROM MESA "
                + "WHERE RESERVADA = ? ORDER BY ID_MESA", reservado);
    }

    /**
     * Obtiene la cantidad total de mesas registradas.
     *
     * @return cantidad de mesas
     */
    public int getCount() {
        return aggregate("SELECT COUNT(*) FROM MESA");
    }

    /**
     * Ejecuta una sentencia de modificación parametrizada.
     *
     * @param sql sentencia SQL
     * @param parameters valores de los parámetros
     * @return cantidad de filas afectadas
     */
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

    /**
     * Ejecuta una consulta y convierte sus filas en objetos Mesa.
     *
     * @param sql sentencia SQL
     * @param parameter parámetro opcional de la consulta
     * @return mesas obtenidas
     */
    private List<Mesa> query(String sql, Object parameter) {
        List<Mesa> result = new ArrayList<>();

        try (Connection connection = connectionDB.connect();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            if (parameter != null) {
                statement.setObject(1, parameter);
            }

            try (ResultSet rows = statement.executeQuery()) {
                while (rows.next()) {
                    result.add(new Mesa(
                            rows.getInt("ID_MESA"),
                            rows.getBoolean("RESERVADA")));
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
        return new IllegalStateException(
                "Database operation failed: " + sql, exception);
    }
}
