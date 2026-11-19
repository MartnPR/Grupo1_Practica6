package pkDataAccess.pkDAO;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import pkDataAccess.pkDTO.Factura;
import pkDataAccess.pkDTO.LineaFactura;
import pkDataAccess.pkIDataAccess.IFacturaDataHelper;

/**
 * DAO encargado de las operaciones de persistencia de las facturas.
 */
public class FacturaDAO implements IFacturaDataHelper {

    private final ConnectionDB connectionDB;
    private static final String COLUMNS = "ID_FACTURA, FECHA, NUMERO_PEDIDO, "
            + "METODO_PAGO, TOTAL, DESCUENTO";
    private static final String LINE_COLUMNS = "NUM_LINEA_COMPRA, CANTIDAD, "
            + "PRECIO_UNITARIO, IDFACTURA";

    /** Crea un DAO usando la configuración predeterminada de Food4All. */
    public FacturaDAO() {
        this(ConnectionDB.fromEnvironment());
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

    /** Inserta una factura y sus líneas en una única transacción. */
    @Override
    public void agregar(Factura object) {
        try {
            connectionDB.inTransaction(() -> {
                insertHeader(object);
                insertLines(object);
                return null;
            });
        } catch (SQLException exception) {
            throw error("insert FACTURA and LINEA_FACTURA", exception);
        }
    }

    /** Busca una factura por su identificador, incluyendo sus líneas. */
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

    /** Actualiza una factura y reemplaza sus líneas en una transacción. */
    @Override
    public boolean actualizar(Factura object) {
        try {
            return connectionDB.inTransaction(() -> {
            int updated;
            updated = execute("UPDATE FACTURA SET FECHA = ?, NUMERO_PEDIDO = ?, "
                    + "METODO_PAGO = ?, TOTAL = ?, DESCUENTO = ? "
                    + "WHERE ID_FACTURA = ?", date(object.getFecha()),
                    object.getNumeroPedido(), object.getMetodoPago(),
                    object.getTotal(), object.getDescuento(),
                    object.getIdFactura());
            if (updated > 0) {
                deleteLines(object.getIdFactura());
                insertLines(object);
            }
            return updated > 0;
            });
        } catch (SQLException exception) {
            throw error("update FACTURA and LINEA_FACTURA", exception);
        }
    }

    /** Elimina primero las líneas dependientes y luego la factura. */
    @Override
    public Factura eliminar(String id) {
        Factura result = buscarPorId(id);
        if (result != null) {
            try {
                connectionDB.inTransaction(() -> {
                    deleteLines(id);
                    execute("DELETE FROM FACTURA WHERE ID_FACTURA = ?", id);
                    return null;
                });
            } catch (SQLException exception) {
                throw error("delete FACTURA and LINEA_FACTURA", exception);
            }
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

    /** Obtiene la cantidad total de facturas registradas. */
    public int getCount() {
        return aggregate("SELECT COUNT(*) FROM FACTURA");
    }

    /** Obtiene el total mínimo registrado. */
    public int getMin() {
        return aggregate("SELECT MIN(TOTAL) FROM FACTURA");
    }

    /** Obtiene el total máximo registrado. */
    public int getMax() {
        return aggregate("SELECT MAX(TOTAL) FROM FACTURA");
    }

    private void insertHeader(Factura object) throws SQLException {
        execute("INSERT INTO FACTURA (" + COLUMNS + ") VALUES (?, ?, ?, ?, ?, ?)",
                object.getIdFactura(), date(object.getFecha()),
                object.getNumeroPedido(), object.getMetodoPago(),
                object.getTotal(), object.getDescuento());
    }

    private void insertLines(Factura object)
            throws SQLException {
        if (object.getLineaFactura() == null || object.getLineaFactura().isEmpty()) {
            return;
        }
        List<Object[]> parameters = new ArrayList<>();
        for (LineaFactura line : object.getLineaFactura()) {
            line.setIdFactura(object.getIdFactura());
            parameters.add(new Object[] {line.getNumLineaFactura(), line.getCantidad(),
                    line.getPrecioUnitario(), object.getIdFactura()});
        }
        connectionDB.executeBatch("INSERT INTO LINEA_FACTURA (" + LINE_COLUMNS
                + ") VALUES (?, ?, ?, ?)", parameters);
    }

    private void deleteLines(String idFactura)
            throws SQLException {
        execute("DELETE FROM LINEA_FACTURA WHERE IDFACTURA = ?", idFactura);
    }

    private List<LineaFactura> queryLines(String idFactura)
            throws SQLException {
        return connectionDB.query("SELECT " + LINE_COLUMNS
                + " FROM LINEA_FACTURA WHERE IDFACTURA = ? "
                + "ORDER BY NUM_LINEA_COMPRA", rows -> {
                    LineaFactura line = new LineaFactura();
                    line.setNumLineaFactura(rows.getInt("NUM_LINEA_COMPRA"));
                    line.setCantidad(rows.getInt("CANTIDAD"));
                    line.setPrecioUnitario(rows.getDouble("PRECIO_UNITARIO"));
                    line.setIdFactura(rows.getString("IDFACTURA"));
                    return line;
                }, idFactura);
    }

    private int execute(String sql, Object... parameters) {
        try {
            return connectionDB.executeUpdate(sql, parameters);
        } catch (SQLException exception) {
            throw error(sql, exception);
        }
    }

    private List<Factura> query(String sql, Object parameter) {
        List<Factura> result = new ArrayList<>();
        try {
            result = connectionDB.query(sql, rows -> {
                Factura item = new Factura();
                item.setIdFactura(rows.getString("ID_FACTURA"));
                item.setFecha(rows.getDate("FECHA"));
                item.setNumeroPedido(rows.getInt("NUMERO_PEDIDO"));
                item.setMetodoPago(rows.getString("METODO_PAGO"));
                item.setTotal(rows.getDouble("TOTAL"));
                item.setDescuento(rows.getDouble("DESCUENTO"));
                item.setLineaFactura(queryLines(item.getIdFactura()));
                return item;
            }, parameter);
        } catch (SQLException exception) {
            throw error(sql, exception);
        }
        return result;
    }

    private int aggregate(String sql) {
        try {
            return connectionDB.aggregate(sql);
        } catch (SQLException exception) {
            throw error(sql, exception);
        }
    }

    private Date date(java.util.Date value) {
        return value == null ? null : new Date(value.getTime());
    }

    private IllegalStateException error(String sql, SQLException exception) {
        return new IllegalStateException(
                "Database operation failed: " + sql, exception);
    }
}
