package pkDataAccess.pkDAO;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import pkDataAccess.pkDTO.Mesa;
import pkDataAccess.pkDTO.Reserva;
import pkDataAccess.pkDTO.UsuarioPoliComedor;
import pkDataAccess.pkIDataAccess.IReservaDataHelper;

/**
 * DAO encargado de las operaciones de persistencia de las reservas.
 */
public class ReservaDAO implements IReservaDataHelper {

    private final ConnectionDB connectionDB;
    private static final String COLUMNS = "r.IDRESERVA, r.FECHA_CREACION, "
            + "r.FRANJA_HORARIA, r.\"idUsuario\" AS \"idUsuario\", "
            + "r.\"idMesa\" AS \"idMesa\"";

    public ReservaDAO() {
        this(ConnectionDB.fromEnvironment());
    }

    public ReservaDAO(ConnectionDB connectionDB) {
        if (connectionDB == null) {
            throw new IllegalArgumentException("connectionDB must not be null");
        }
        this.connectionDB = connectionDB;
    }

    @Override
    public void agregar(Reserva object) {
        execute("INSERT INTO RESERVA "
                + "(IDRESERVA, FECHA_CREACION, FRANJA_HORARIA, \"idUsuario\", \"idMesa\") "
                + "VALUES (?, ?, ?, ?, ?)",
                object.getIdReserva(), date(object.getFechaCreacion()),
                object.getFranjaHoraria(),
                object.getUsuario() == null ? null : object.getUsuario().getIdUsuario(),
                object.getMesa() == null ? null : object.getMesa().getIdMesa());
    }

    @Override
    public Reserva buscarPorId(Integer id) {
        List<Reserva> result = query(
                "SELECT " + COLUMNS + " FROM RESERVA r WHERE r.IDRESERVA = ?", id);
        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    public List<Reserva> leerTodos() {
        return query("SELECT " + COLUMNS
                + " FROM RESERVA r ORDER BY r.IDRESERVA", null);
    }

    @Override
    public boolean actualizar(Reserva object) {
        return execute("UPDATE RESERVA SET FECHA_CREACION = ?, "
                + "FRANJA_HORARIA = ?, \"idUsuario\" = ?, \"idMesa\" = ? "
                + "WHERE IDRESERVA = ?",
                date(object.getFechaCreacion()), object.getFranjaHoraria(),
                object.getUsuario() == null ? null : object.getUsuario().getIdUsuario(),
                object.getMesa() == null ? null : object.getMesa().getIdMesa(),
                object.getIdReserva()) > 0;
    }

    @Override
    public Reserva eliminar(Integer id) {
        Reserva result = buscarPorId(id);
        if (result != null) {
            execute("DELETE FROM RESERVA WHERE IDRESERVA = ?", id);
        }
        return result;
    }

    @Override
    public List<Reserva> buscarPorFecha(java.util.Date fecha) {
        return query("SELECT " + COLUMNS
                + " FROM RESERVA r WHERE r.FECHA_CREACION = ?", date(fecha));
    }

    @Override
    public List<Reserva> buscarPorEstudiante(String codigo) {
        return query("SELECT " + COLUMNS
                + " FROM RESERVA r JOIN ESTUDIANTE e ON e.IDUSUARIO = r.\"idUsuario\" "
                + "WHERE e.CODIGO_UNICO = ?", codigoUnico(codigo));
    }

    @Override
    public List<Reserva> buscarPorFranjaHoraria(String franja) {
        return query("SELECT " + COLUMNS
                + " FROM RESERVA r WHERE r.FRANJA_HORARIA = ?", franja);
    }

    @Override
    public List<Reserva> buscarPorProfesor(String cedula) {
        return query("SELECT " + COLUMNS
                + " FROM RESERVA r JOIN PROFESOR p ON p.IDUSUARIO = r.\"idUsuario\" "
                + "WHERE p.CEDULA = ?", cedula);
    }

    public int getCount() {
        return aggregate("SELECT COUNT(*) FROM RESERVA");
    }

    private int execute(String sql, Object... parameters) {
        try {
            return connectionDB.executeUpdate(sql, parameters);
        } catch (SQLException exception) {
            throw error(sql, exception);
        }
    }

    private List<Reserva> query(String sql, Object parameter) {
        List<Reserva> result = new ArrayList<>();
        try {
            result = connectionDB.query(sql, rows -> {
                    Reserva item = new Reserva();
                    item.setIdReserva(rows.getInt("IDRESERVA"));
                    item.setFechaCreacion(rows.getDate("FECHA_CREACION"));
                    item.setFranjaHoraria(rows.getString("FRANJA_HORARIA"));

                    UsuarioPoliComedor usuario = new UsuarioPoliComedor();
                    usuario.setIdUsuario(rows.getString("idUsuario"));
                    item.setUsuario(usuario);

                    if (rows.getObject("idMesa") != null) {
                        Mesa mesa = new Mesa();
                        mesa.setIdMesa(rows.getInt("idMesa"));
                        item.setMesa(mesa);
                    }
                    return item;
                }, parameter);
        } catch (SQLException exception) {
            throw error(sql, exception);
        }
        return result;
    }

    private Integer codigoUnico(String codigo) {
        try {
            return Integer.valueOf(codigo);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    "codigoUnico must be a valid integer", exception);
        }
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
