package pkDataAccess.pkDAO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.google.zxing.common.BitMatrix;
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
        this(ConnectionDB.fromEnvironment());
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
                + "(IDTICKET, FECHA_GENERACION, VALIDO, CODIGO_QR, IDFACTURA) "
                + "VALUES (?, ?, ?, ?, ?)",
                object.getIdTicket(),
                object.getFechaGeneracion(),
                object.isValido(),
                object.getCodigoQR() == null
                        ? null
                        : object.getCodigoQR().toString()
                                .getBytes(java.nio.charset.StandardCharsets.UTF_8),
                object.getIdFactura());
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
                "SELECT IDTICKET, FECHA_GENERACION, VALIDO, CODIGO_QR, IDFACTURA "
                        + "FROM TICKET WHERE IDTICKET = ?",
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
        return query("SELECT IDTICKET, FECHA_GENERACION, VALIDO, CODIGO_QR, IDFACTURA "
                + "FROM TICKET ORDER BY IDTICKET", null);
    }

    /**
     * Actualiza un ticket existente.
     *
     * @param object ticket con los valores actualizados
     * @return {@code true} si se actualizó un registro
     */
    @Override
    public boolean actualizar(Ticket object) {
        return execute("UPDATE TICKET SET FECHA_GENERACION = ?, VALIDO = ?, "
                + "CODIGO_QR = ?, IDFACTURA = ? "
                + "WHERE IDTICKET = ?",
                object.getFechaGeneracion(),
                object.isValido(),
                object.getCodigoQR() == null
                        ? null
                        : object.getCodigoQR().toString()
                                .getBytes(java.nio.charset.StandardCharsets.UTF_8),
                object.getIdFactura(),
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
            execute("DELETE FROM TICKET WHERE IDTICKET = ?", id);
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
        return query("SELECT IDTICKET, FECHA_GENERACION, VALIDO, CODIGO_QR, IDFACTURA "
                + "FROM TICKET WHERE VALIDO = ? ORDER BY IDTICKET", valido);
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
        try {
            return connectionDB.executeUpdate(sql, parameters);
        } catch (SQLException exception) {
            throw error(sql, exception);
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

        try {
            result = connectionDB.query(sql, rows -> {
                    Ticket item = new Ticket();
                    item.setIdTicket(rows.getInt("IDTICKET"));
                    item.setFechaGeneracion(rows.getTimestamp("FECHA_GENERACION"));
                    item.setValido(rows.getBoolean("VALIDO"));
                    item.setIdFactura(rows.getString("IDFACTURA"));
                    item.setCodigoQR(decodeCodigoQR(rows.getBytes("CODIGO_QR")));
                    return item;
                }, parameter);
        } catch (SQLException exception) {
            throw error(sql, exception);
        }
        return result;
    }

    private BitMatrix decodeCodigoQR(byte[] value) {
        if (value == null) {
            return null;
        }
        String serialized = new String(value, java.nio.charset.StandardCharsets.UTF_8);
        String[] rows = serialized.split("\\R", -1);
        if (rows.length > 0 && rows[rows.length - 1].isEmpty()) {
            rows = java.util.Arrays.copyOf(rows, rows.length - 1);
        }
        if (rows.length == 0 || rows[0].length() % 2 != 0) {
            throw new IllegalStateException("Invalid CODIGO_QR data");
        }
        int width = rows[0].length() / 2;
        BitMatrix matrix = new BitMatrix(width, rows.length);
        for (int y = 0; y < rows.length; y++) {
            if (rows[y].length() != rows[0].length()) {
                throw new IllegalStateException("Invalid CODIGO_QR data");
            }
            for (int x = 0; x < width; x++) {
                if (rows[y].charAt(x * 2) == 'X') {
                    matrix.set(x, y);
                }
            }
        }
        return matrix;
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

    /**
     * Cierra la conexión administrada por el DAO.
     */
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
