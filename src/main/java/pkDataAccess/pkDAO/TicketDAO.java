package pkDataAccess.pkDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import pkDataAccess.pkDTO.Ticket;
import pkDataAccess.pkIDataAccess.ITicketDataHelper;

/**
 * DAO encargado de las operaciones de persistencia de los tickets.
 */
public class TicketDAO implements ITicketDataHelper {

    private final ConnectionDB connectionDB;

    /**
     * Crea un DAO usando la configuración predeterminada de Food4All.
     */
    public TicketDAO() {
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
    public TicketDAO(ConnectionDB connectionDB) {
        if (connectionDB == null) {
            throw new IllegalArgumentException("connectionDB must not be null");
        }
        this.connectionDB = connectionDB;
    }

    /**
     * Inserta un ticket en la base de datos.
     *
     * @param object ticket que se desea insertar
     */
    @Override
    public void agregar(Ticket object) {
        execute("INSERT INTO TICKET "
                + "(ID_TICKET, FECHA_GENERACION, VALIDO, CODIGO_QR) "
                + "VALUES (?, ?, ?, ?)",
                object.getIdTicket(),
                object.getFechaGeneracion(),
                object.isValido(),
                object.getCodigoQR() == null
                        ? null
                        : object.getCodigoQR().toString()
                                .getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }

    /**
     * Busca un ticket por su identificador.
     *
     * @param id identificador del ticket
     * @return ticket encontrado o {@code null} si no existe
     */
    @Override
    public Ticket buscarPorId(Integer id) {
        List<Ticket> result = query(
                "SELECT ID_TICKET, FECHA_GENERACION, VALIDO "
                        + "FROM TICKET WHERE ID_TICKET = ?",
                id);
        return result.isEmpty() ? null : result.get(0);
    }

    /**
     * Obtiene todos los tickets ordenados por identificador.
     *
     * @return lista de tickets registrados
     */
    @Override
    public List<Ticket> leerTodos() {
        return query("SELECT ID_TICKET, FECHA_GENERACION, VALIDO "
                + "FROM TICKET ORDER BY ID_TICKET", null);
    }

    /**
     * Actualiza un ticket existente.
     *
     * @param object ticket con los valores actualizados
     * @return {@code true} si se actualizó un registro
     */
    @Override
    public boolean actualizar(Ticket object) {
        return execute("UPDATE TICKET SET FECHA_GENERACION = ?, VALIDO = ? "
                + "WHERE ID_TICKET = ?",
                object.getFechaGeneracion(),
                object.isValido(),
                object.getIdTicket()) > 0;
    }

    /**
     * Elimina un ticket y devuelve el objeto eliminado.
     *
     * @param id identificador del ticket
     * @return ticket eliminado o {@code null} si no existe
     */
    @Override
    public Ticket eliminar(Integer id) {
        Ticket result = buscarPorId(id);
        if (result != null) {
            execute("DELETE FROM TICKET WHERE ID_TICKET = ?", id);
        }
        return result;
    }

    /**
     * Busca tickets según su estado de validez.
     *
     * @param valido estado de validez que se desea consultar
     * @return tickets que coinciden con el estado indicado
     */
    @Override
    public List<Ticket> buscarPorValido(boolean valido) {
        return query("SELECT ID_TICKET, FECHA_GENERACION, VALIDO "
                + "FROM TICKET WHERE VALIDO = ? ORDER BY ID_TICKET", valido);
    }

    /**
     * Obtiene la cantidad total de tickets registrados.
     *
     * @return cantidad de tickets
     */
    public int getCount() {
        return aggregate("SELECT COUNT(*) FROM TICKET");
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
     * Ejecuta una consulta y convierte sus filas en objetos Ticket.
     *
     * @param sql sentencia SQL
     * @param parameter parámetro opcional de la consulta
     * @return tickets obtenidos
     */
    private List<Ticket> query(String sql, Object parameter) {
        List<Ticket> result = new ArrayList<>();

        try (Connection connection = connectionDB.connect();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            if (parameter != null) {
                statement.setObject(1, parameter);
            }

            try (ResultSet rows = statement.executeQuery()) {
                while (rows.next()) {
                    Ticket item = new Ticket();
                    item.setIdTicket(rows.getInt("ID_TICKET"));
                    item.setFechaGeneracion(rows.getTimestamp("FECHA_GENERACION"));
                    item.setValido(rows.getBoolean("VALIDO"));
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
        return new IllegalStateException(
                "Database operation failed: " + sql, exception);
    }
}
