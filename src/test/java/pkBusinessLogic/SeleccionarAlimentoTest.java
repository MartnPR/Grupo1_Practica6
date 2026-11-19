package pkBusinessLogic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import pkDataAccess.pkDAO.AlimentoDAO;
import pkDataAccess.pkDAO.ConnectionDB;
import pkDataAccess.pkDTO.Alimento;

class SeleccionarAlimentoTest {

    @Test
    void unaBusquedaDebeDevolverSoloLosTiposCoincidentes() {
        AlimentoDAO dao = new AlimentoDAO(
                new ConnectionDB("jdbc:postgresql://localhost/test", "test", "test")) {
            @Override
            public List<Alimento> buscarPorTipo(String categoria) {
                return "entrada".equalsIgnoreCase(categoria)
                        ? List.of(alimento(2, "Sopa de pollo", "ENTRADA", 15))
                        : List.of();
            }

            @Override
            public List<Alimento> leerTodos() {
                return List.of(
                        alimento(1, "Fritada", "PLATO FUERTE", 15),
                        alimento(2, "Sopa de pollo", "ENTRADA", 15),
                        alimento(3, "Seco de pollo", "PlATO FUERTE", 10));
            }
        };

        SeleccionarAlimento seleccion = new SeleccionarAlimento(dao);

        List<String> resultado =
                seleccion.listarAlimentosPorTipoParaVista("entrada");
        assertEquals(1, resultado.size());
        assertTrue(resultado.get(0).startsWith("2 | Sopa de pollo"));
        assertTrue(resultado.get(0).contains("ENTRADA"));
    }

    @Test
    void unaBusquedaDebeEncontrarUnAlimentoPorSuNombre() {
        AlimentoDAO dao = new AlimentoDAO(
                new ConnectionDB("jdbc:postgresql://localhost/test", "test", "test")) {
            @Override
            public List<Alimento> buscarPorTipo(String criterio) {
                if ("fritada".equalsIgnoreCase(criterio)) {
                    return List.of(alimento(1, "Fritada", "PLATO FUERTE", 15));
                }
                return List.of();
            }
        };

        SeleccionarAlimento seleccion = new SeleccionarAlimento(dao);
        List<String> resultado =
                seleccion.listarAlimentosPorTipoParaVista("fritada");

        assertEquals(1, resultado.size());
        assertTrue(resultado.get(0).startsWith("1 | Fritada"));
    }

    private static Alimento alimento(
            int id, String nombre, String tipo, int stock) {
        Alimento item = new Alimento();
        item.setIdAlimento(id);
        item.setNombre(nombre);
        item.setTipo(tipo);
        item.setCantidadEnStock(stock);
        item.setPrecioUnitario(0);
        return item;
    }
}
