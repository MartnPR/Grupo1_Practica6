package pkDataAccess.pkDAO;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import pkDataAccess.pkDTO.Factura;
import pkDataAccess.pkIDataAccess.IFacturaDataHelper;

/**
 * DAO encargado de las operaciones de persistencia de las facturas.
 */
public class FacturaDAO implements IFacturaDataHelper {

    private final ConnectionDB connectionDB;
    private static final String COLUMNS = "ID_FACTURA, FECHA, NUMERO_PEDIDO, "
            + "METODO_PAGO, TOTAL, DESCUENTO";

    /** Crea un DAO usando la configuración predeterminada de Food4All. */
    public FacturaDAO() {
        this(new ConnectionDB("jdbc:oracle:thin:@localhost:1521:xe",
                "oracle.jdbc.OracleDriver", "food4all", "food4all"));
    }

    /**
     * Crea un DAO usando una conexión configurable.
     *
     * @param connectionDB administrador de la conexión JDBC
     */
    public FacturaDAO(ConnectionDB connectionDB) {
        if (connectionDB == null) {
            throw new IllegalArgumentException("connectionDB must not be null");
        }
        this.connectionDB = connectionDB;
    }

    /** Inserta una factura en la base de datos. */
    @Override
    public void agregar(Factura object) {
        execute("INSERT INTO FACTURA (" + COLUMNS + ") VALUES (?, ?, ?, ?, ?, ?)",
                object.getIdFactura(), date(object.getFecha()),
                object.getNumeroPedido(), object.getMetodoPago(),
                object.getTotal(), object.getDescuento());
    }

    /** Busca una factura por su identificador. */
    @Override
    public Factura buscarPorId(String id) {
        List<Factura> result = query(
                "SELECT " + COLUMNS + " FROM FACTURA WHERE ID_FACTURA = ?", id);
        return result.isEmpty() ? null : result.get(0);
    }

    /** Obtiene todas las facturas ordenadas por identificador. */
    @Override
    public List<Factura> leerTodos() {
        return query("SELECT " + COLUMNS
                + " FROM FACTURA ORDER BY ID_FACTURA", null);
    }

    /** Actualiza los datos de una factura. */
    @Override
    public boolean actualizar(Factura object) {
        return execute("UPDATE FACTURA SET FECHA = ?, NUMERO_PEDIDO = ?, "
                + "METODO_PAGO = ?, TOTAL = ?, DESCUENTO = ? "
                + "WHERE ID_FACTURA = ?",
                date(object.getFecha()), object.getNumeroPedido(),
                object.getMetodoPago(), object.getTotal(),
                object.getDescuento(), object.getIdFactura()) > 0;
    }

    /** Elimina una factura y devuelve el objeto eliminado. */
    @Override
    public Factura eliminar(String id) {
        Factura result = buscarPorId(id);
        if (result != null) {
            execute("DELETE FROM FACTURA WHERE ID_FACTURA = ?", id);
        }
        return result;
    }

    /** Busca facturas por método de pago. */
    @Override
    public List<Factura> buscarPorMetodoPago(String metodo) {
        return query("SELECT " + COLUMNS
                + " FROM FACTURA WHERE METODO_PAGO = ?", metodo);
    }

    /** Busca facturas por fecha. */
    @Override
    public List<Factura> buscarPorFecha(java.util.Date fecha) {
        return query("SELECT " + COLUMNS
                + " FROM FACTURA WHERE FECHA = ?", date(fecha));
    }

    /**
     * Obtiene la cantidad total de facturas registradas.
     *
     * @return cantidad de facturas
     */
    public int getCount() {
        return aggregate("SELECT COUNT(*) FROM FACTURA");
    }

    /**
     * Obtiene el total mínimo registrado.
     *
     * @return total mínimo convertido a entero
     */
    public int getMin() {
        return aggregate("SELECT MIN(TOTAL) FROM FACTURA");
    }

    /**
     * Obtiene el total máximo registrado.
     *
     * @return total máximo convertido a entero
     */
    public int getMax() {
        return aggregate("SELECT MAX(TOTAL) FROM FACTURA");
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

    /** Ejecuta una consulta y convierte sus filas en objetos Factura. */
    private List<Factura> query(String sql, Object parameter) {
        List<Factura> result = new ArrayList<>();

        try (Connection connection = connectionDB.connect();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            if (parameter != null) {
                statement.setObject(1, parameter);
            }
            try (ResultSet rows = statement.executeQuery()) {
                while (rows.next()) {
                    Factura item = new Factura();
                    item.setIdFactura(rows.getString("ID_FACTURA"));
                    item.setFecha(rows.getDate("FECHA"));
                    item.setNumeroPedido(rows.getInt("NUMERO_PEDIDO"));
                    item.setMetodoPago(rows.getString("METODO_PAGO"));
                    item.setTotal(rows.getDouble("TOTAL"));
                    item.setDescuento(rows.getDouble("DESCUENTO"));
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

    /** Convierte una fecha Java a una fecha SQL. */
    private Date date(java.util.Date value) {
        return value == null ? null : new Date(value.getTime());
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
