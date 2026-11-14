package pkDataAccess.pkDAO;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import pkDataAccess.pkDTO.Reserva;
import pkDataAccess.pkIDataAccess.IReservaDataHelper;

/**
 * DAO encargado de las operaciones de persistencia de las reservas.
 */
public class ReservaDAO implements IReservaDataHelper {

    private final ConnectionDB connectionDB;
    private static final String COLUMNS = "ID_RESERVA, FECHA_CREACION, "
            + "FRANJA_HORARIA, CODIGO_ESTUDIANTE, CEDULA_PROFESOR";

    /**
     * Crea un DAO usando la configuración predeterminada de Food4All.
     */
    public ReservaDAO() {
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
    public ReservaDAO(ConnectionDB connectionDB) {
        if (connectionDB == null) {
            throw new IllegalArgumentException("connectionDB must not be null");
        }
        this.connectionDB = connectionDB;
    }

    /**
     * Inserta una reserva en la base de datos.
     *
     * @param object reserva que se desea insertar
     */
    @Override
    public void agregar(Reserva object) {
        execute("INSERT INTO RESERVA (" + COLUMNS + ") VALUES (?, ?, ?, ?, ?)",
                object.getIdReserva(),
                date(object.getFechaCreacion()),
                object.getFranjaHoraria(),
                object.getCodigoEstudiante(),
                object.getCedulaProfesor());
    }

    /**
     * Busca una reserva por su identificador.
     *
     * @param id identificador de la reserva
     * @return reserva encontrada o {@code null} si no existe
     */
    @Override
    public Reserva buscarPorId(Integer id) {
        List<Reserva> result = query(
                "SELECT " + COLUMNS + " FROM RESERVA WHERE ID_RESERVA = ?", id);
        return result.isEmpty() ? null : result.get(0);
    }

    /**
     * Obtiene todas las reservas ordenadas por identificador.
     *
     * @return lista de reservas registradas
     */
    @Override
    public List<Reserva> leerTodos() {
        return query("SELECT " + COLUMNS
                + " FROM RESERVA ORDER BY ID_RESERVA", null);
    }

    /**
     * Actualiza los datos de una reserva.
     *
     * @param object reserva con los valores actualizados
     * @return {@code true} si se actualizó un registro
     */
    @Override
    public boolean actualizar(Reserva object) {
        return execute("UPDATE RESERVA SET FECHA_CREACION = ?, "
                + "FRANJA_HORARIA = ?, CODIGO_ESTUDIANTE = ?, "
                + "CEDULA_PROFESOR = ? WHERE ID_RESERVA = ?",
                date(object.getFechaCreacion()),
                object.getFranjaHoraria(),
                object.getCodigoEstudiante(),
                object.getCedulaProfesor(),
                object.getIdReserva()) > 0;
    }

    /**
     * Elimina una reserva y devuelve el objeto eliminado.
     *
     * @param id identificador de la reserva
     * @return reserva eliminada o {@code null} si no existe
     */
    @Override
    public Reserva eliminar(Integer id) {
        Reserva result = buscarPorId(id);
        if (result != null) {
            execute("DELETE FROM RESERVA WHERE ID_RESERVA = ?", id);
        }
        return result;
    }

    /**
     * Busca reservas creadas en una fecha específica.
     *
     * @param fecha fecha de creación
     * @return reservas de la fecha indicada
     */
    @Override
    public List<Reserva> buscarPorFecha(java.util.Date fecha) {
        return query("SELECT " + COLUMNS
                + " FROM RESERVA WHERE FECHA_CREACION = ?", date(fecha));
    }

    /**
     * Busca reservas asociadas a un estudiante.
     *
     * @param codigo código del estudiante
     * @return reservas del estudiante indicado
     */
    @Override
    public List<Reserva> buscarPorEstudiante(String codigo) {
        return query("SELECT " + COLUMNS
                + " FROM RESERVA WHERE CODIGO_ESTUDIANTE = ?", codigo);
    }

    /**
     * Busca reservas por franja horaria.
     *
     * @param franja franja horaria de la reserva
     * @return reservas de la franja indicada
     */
    @Override
    public List<Reserva> buscarPorFranjaHoraria(String franja) {
        return query("SELECT " + COLUMNS
                + " FROM RESERVA WHERE FRANJA_HORARIA = ?", franja);
    }

    /**
     * Busca reservas asociadas a un profesor.
     *
     * @param cedula cédula del profesor
     * @return reservas del profesor indicado
     */
    @Override
    public List<Reserva> buscarPorProfesor(String cedula) {
        return query("SELECT " + COLUMNS
                + " FROM RESERVA WHERE CEDULA_PROFESOR = ?", cedula);
    }

    /**
     * Obtiene la cantidad total de reservas registradas.
     *
     * @return cantidad de reservas
     */
    public int getCount() {
        return aggregate("SELECT COUNT(*) FROM RESERVA");
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
            set(statement, parameters);
            return statement.executeUpdate();
        } catch (SQLException exception) {
            throw error(sql, exception);
        } finally {
            closeConnection();
        }
    }

    /**
     * Ejecuta una consulta y convierte sus filas en objetos Reserva.
     *
     * @param sql sentencia SQL
     * @param parameter parámetro opcional de la consulta
     * @return reservas obtenidas
     */
    private List<Reserva> query(String sql, Object parameter) {
        List<Reserva> result = new ArrayList<>();

        try (Connection connection = connectionDB.connect();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            if (parameter != null) {
                statement.setObject(1, parameter);
            }

            try (ResultSet rows = statement.executeQuery()) {
                while (rows.next()) {
                    Reserva item = new Reserva();
                    item.setIdReserva(rows.getInt("ID_RESERVA"));
                    item.setFechaCreacion(rows.getDate("FECHA_CREACION"));
                    item.setFranjaHoraria(rows.getString("FRANJA_HORARIA"));
                    item.setCodigoEstudiante(rows.getString("CODIGO_ESTUDIANTE"));
                    item.setCedulaProfesor(rows.getString("CEDULA_PROFESOR"));
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
     * Asigna los parámetros de una sentencia preparada.
     *
     * @param statement sentencia preparada
     * @param parameters valores que se asignarán
     * @throws SQLException si ocurre un error al asignar un parámetro
     */
    private void set(PreparedStatement statement, Object[] parameters)
            throws SQLException {
        for (int i = 0; i < parameters.length; i++) {
            statement.setObject(i + 1, parameters[i]);
        }
    }

    /**
     * Convierte una fecha Java a una fecha SQL.
     *
     * @param value fecha Java
     * @return fecha SQL o {@code null}
     */
    private Date date(java.util.Date value) {
        return value == null ? null : new Date(value.getTime());
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
