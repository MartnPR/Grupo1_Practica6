package pkDataAccess.pkDAO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import pkDataAccess.pkDTO.Alimento;
import pkDataAccess.pkIDataAccess.IAlimentoDataHelper;

public class AlimentoDAO implements IAlimentoDataHelper {

    private final ConnectionDB connectionDB;

    public AlimentoDAO() {
        this(ConnectionDB.fromEnvironment());
    }

    public AlimentoDAO(ConnectionDB connectionDB) {
        if (connectionDB == null) {
            throw new IllegalArgumentException("connectionDB must not be null");
        }
        this.connectionDB = connectionDB;
    }

    @Override
    public void agregar(Alimento object) {
        execute("INSERT INTO alimento "
                + "(idalimento, nombre, tipo, descripcion, "
                + "cantidad_en_stock, precio_unitario) "
                + "VALUES (?, ?, ?, ?, ?, ?)",
                object.getIdAlimento(), object.getNombre(), object.getTipo(),
                object.getDescripcion(), object.getCantidadEnStock(),
                object.getPrecioUnitario());
    }

    @Override
    public Alimento buscarPorId(Integer id) {
        String sql = "SELECT idalimento AS ID_ALIMENTO, nombre AS NOMBRE, "
                + "tipo AS TIPO, descripcion AS DESCRIPCION, "
                + "cantidad_en_stock AS CANTIDAD_EN_STOCK, "
                + "precio_unitario AS PRECIO_UNITARIO "
                + "FROM alimento WHERE idalimento = ?";
        List<Alimento> result = query(sql, id);
        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    public List<Alimento> leerTodos() {
        return query("SELECT idalimento AS ID_ALIMENTO, nombre AS NOMBRE, "
                + "tipo AS TIPO, descripcion AS DESCRIPCION, "
                + "cantidad_en_stock AS CANTIDAD_EN_STOCK, "
                + "precio_unitario AS PRECIO_UNITARIO "
                + "FROM alimento ORDER BY idalimento", null);
    }

    @Override
    public boolean actualizar(Alimento object) {
        return execute("UPDATE alimento SET nombre = ?, tipo = ?, descripcion = ?, "
                + "cantidad_en_stock = ?, precio_unitario = ? "
                + "WHERE idalimento = ?",
                object.getNombre(), object.getTipo(), object.getDescripcion(),
                object.getCantidadEnStock(), object.getPrecioUnitario(),
                object.getIdAlimento()) > 0;
    }

    @Override
    public Alimento eliminar(Integer id) {
        Alimento result = buscarPorId(id);
        if (result == null) {
            return null;
        }
        execute("DELETE FROM alimento WHERE idalimento = ?", id);
        return result;
    }

    @Override
    public List<Alimento> buscarPorTipo(String categoria) {
        if (categoria == null || categoria.isBlank()) {
            return leerTodos();
        }
        String criterio = categoria.trim();
        String criterioEscapeado = criterio
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
        String columnas = "SELECT idalimento AS ID_ALIMENTO, nombre AS NOMBRE, "
                + "tipo AS TIPO, descripcion AS DESCRIPCION, "
                + "cantidad_en_stock AS CANTIDAD_EN_STOCK, "
                + "precio_unitario AS PRECIO_UNITARIO ";

        List<Alimento> exactos = query(columnas
                + "FROM alimento "
                + "WHERE UPPER(nombre) = UPPER(?) "
                + "OR UPPER(tipo) = UPPER(?) "
                + "ORDER BY idalimento",
                new Object[]{criterio, criterio});
        if (!exactos.isEmpty()) {
            return exactos;
        }

        return query(columnas
                + "FROM alimento "
                + "WHERE UPPER(nombre) LIKE UPPER(?) ESCAPE '\\' "
                + "OR UPPER(tipo) LIKE UPPER(?) ESCAPE '\\' "
                + "ORDER BY idalimento",
                new Object[]{
                        "%" + criterioEscapeado + "%",
                        "%" + criterioEscapeado + "%"});
    }

    public int getCount() {
        return aggregate("SELECT COUNT(*) FROM alimento");
    }

    public int getMin() {
        return aggregate("SELECT MIN(precio_unitario) FROM alimento");
    }

    public int getMax() {
        return aggregate("SELECT MAX(precio_unitario) FROM alimento");
    }

    private List<Alimento> query(String sql, Object parameter) {
        List<Alimento> result = new ArrayList<>();
        try {
            Object[] parameters = parameter instanceof Object[] values
                    ? values
                    : new Object[]{parameter};
            result = connectionDB.query(sql, rows -> {
                Alimento item = new Alimento();
                item.setIdAlimento(rows.getInt("ID_ALIMENTO"));
                item.setNombre(rows.getString("NOMBRE"));
                item.setTipo(rows.getString("TIPO"));
                item.setDescripcion(rows.getString("DESCRIPCION"));
                item.setCantidadEnStock(rows.getInt("CANTIDAD_EN_STOCK"));
                item.setPrecioUnitario(rows.getDouble("PRECIO_UNITARIO"));
                return item;
            }, parameters);
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

    private int execute(String sql, Object... parameters) {
        try {
            return connectionDB.executeUpdate(sql, parameters);
        } catch (SQLException exception) {
            throw error(sql, exception);
        }
    }

    private IllegalStateException error(String sql, SQLException exception) {
        return new IllegalStateException(
                "Database operation failed: " + sql, exception);
    }
}
