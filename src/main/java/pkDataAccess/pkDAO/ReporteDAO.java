package pkDataAccess.pkDAO;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import pkDataAccess.pkDTO.Reporte;
import pkDataAccess.pkIDataAccess.IReporteDataHelper;

/**
 * DAO encargado de las operaciones de persistencia de los reportes.
 */
public class ReporteDAO implements IReporteDataHelper {

    private final ConnectionDB connectionDB;
    private static final String COLUMNS = "ID_REPORTE, FECHA_GENERACION, "
            + "FRANJA_HORARIA, GRAFICOS, DESCRIPCION, TOTAL_VENTAS, "
            + "TOTAL_PEDIDOS, TOTAL_RESERVAS";

    /** Crea un DAO usando la configuración predeterminada de Food4All. */
    public ReporteDAO() {
        this(ConnectionDB.fromEnvironment());
    }

    /**
     * Crea un DAO usando una conexión configurable.
     *
     * @param connectionDB administrador de la conexión JDBC
     */
    public ReporteDAO(ConnectionDB connectionDB) {
        if (connectionDB == null) {
            throw new IllegalArgumentException("connectionDB must not be null");
        }
        this.connectionDB = connectionDB;
    }

    /** Inserta un reporte en la base de datos. */
    @Override
    public void agregar(Reporte object) {
        execute("INSERT INTO REPORTE (ID_REPORTE, FECHA_GENERACION, "
                + "FRANJA_HORARIA, GRAFICOS, DESCRIPCION, TOTAL_VENTAS, "
                + "TOTAL_PEDIDOS, TOTAL_RESERVAS) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                object.getIdReporte(), date(object.getFechaGeneracion()),
                object.getFranjaHoraria(), object.isGraficos(),
                object.getDescripcion(), object.getTotalVentas(),
                object.getTotalPedidos(), object.getTotalReservas());
    }

    /** Busca un reporte por su identificador. */
    @Override
    public Reporte buscarPorId(String id) {
        List<Reporte> result = query(
                "SELECT " + COLUMNS + " FROM REPORTE WHERE ID_REPORTE = ?", id);
        return result.isEmpty() ? null : result.get(0);
    }

    /** Obtiene todos los reportes ordenados por identificador. */
    @Override
    public List<Reporte> leerTodos() {
        return query("SELECT " + COLUMNS
                + " FROM REPORTE ORDER BY ID_REPORTE", null);
    }

    /** Actualiza los datos de un reporte. */
    @Override
    public boolean actualizar(Reporte object) {
        return execute("UPDATE REPORTE SET FECHA_GENERACION = ?, "
                + "FRANJA_HORARIA = ?, GRAFICOS = ?, DESCRIPCION = ?, "
                + "TOTAL_VENTAS = ?, TOTAL_PEDIDOS = ?, TOTAL_RESERVAS = ? "
                + "WHERE ID_REPORTE = ?",
                date(object.getFechaGeneracion()), object.getFranjaHoraria(),
                object.isGraficos(), object.getDescripcion(),
                object.getTotalVentas(), object.getTotalPedidos(),
                object.getTotalReservas(),
                object.getIdReporte()) > 0;
    }

    /** Elimina un reporte y devuelve el objeto eliminado. */
    @Override
    public Reporte eliminar(String id) {
        Reporte result = buscarPorId(id);
        if (result != null) {
            execute("DELETE FROM REPORTE WHERE ID_REPORTE = ?", id);
        }
        return result;
    }

    /** Busca reportes por fecha de generación. */
    @Override
    public List<Reporte> buscarPorFecha(java.util.Date fecha) {
        return query("SELECT " + COLUMNS
                + " FROM REPORTE WHERE FECHA_GENERACION = ?", date(fecha));
    }

    /** Busca reportes por franja horaria. */
    @Override
    public List<Reporte> buscarPorFranjaHoraria(String franja) {
        return query("SELECT " + COLUMNS
                + " FROM REPORTE WHERE FRANJA_HORARIA = ?", franja);
    }

    /**
     * Obtiene la cantidad total de reportes registrados.
     *
     * @return cantidad de reportes
     */
    public int getCount() {
        return aggregate("SELECT COUNT(*) FROM REPORTE");
    }

    /** Ejecuta una sentencia de modificación parametrizada. */
    private int execute(String sql, Object... parameters) {
        try {
            return connectionDB.executeUpdate(sql, parameters);
        } catch (SQLException exception) {
            throw error(sql, exception);
        }
    }

    /** Ejecuta una consulta y convierte sus filas en objetos Reporte. */
    private List<Reporte> query(String sql, Object parameter) {
        List<Reporte> result = new ArrayList<>();

        try {
            result = connectionDB.query(sql, rows -> {
                    Reporte item = new Reporte();
                    item.setIdReporte(rows.getString("ID_REPORTE"));
                    item.setFechaGeneracion(rows.getDate("FECHA_GENERACION"));
                    item.setFranjaHoraria(rows.getString("FRANJA_HORARIA"));
                    item.setGraficos(rows.getBoolean("GRAFICOS"));
                    item.setDescripcion(rows.getString("DESCRIPCION"));
                    item.setTotalVentas(rows.getDouble("TOTAL_VENTAS"));
                    item.setTotalPedidos(rows.getInt("TOTAL_PEDIDOS"));
                    item.setTotalReservas(rows.getInt("TOTAL_RESERVAS"));
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

    /** Convierte una fecha Java a una fecha SQL. */
    private Date date(java.util.Date value) {
        return value == null ? null : new Date(value.getTime());
    }

    /** Cierra la conexión administrada por el DAO. */
    /** Construye una excepción uniforme para errores de base de datos. */
    private IllegalStateException error(String sql, SQLException exception) {
        return new IllegalStateException(
                "Database operation failed: " + sql, exception);
    }
}
