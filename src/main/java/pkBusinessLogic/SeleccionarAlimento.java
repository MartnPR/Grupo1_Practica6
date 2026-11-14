package pkBusinessLogic;

import java.util.Date;
import java.util.List;

import pkDataAccess.pkDTO.Alimento;
import pkDataAccess.pkDAO.AlimentoDAO;

public class SeleccionarAlimento {

    private final AlimentoDAO alimentoDAO;

    public SeleccionarAlimento(
            AlimentoDAO alimentoDAO) {

        if (alimentoDAO == null) {
            throw new IllegalArgumentException(
                    "AlimentoDAO no puede ser nulo."
            );
        }

        this.alimentoDAO = alimentoDAO;
    }

    public int consultarStockAlimento(
            Alimento alimento) {

        if (alimento == null) {
            return 0;
        }

        /*
         * Ajustar cuando haya AlimentoDAO:
         *
         * return alimentoDAO.consultarStock(
         *     alimento.getIdAlimento()
         * );
         */

        return 0;
    }

    public List<Alimento> listarAlimentosPorTipo(
            String tipo) {

        if (tipo == null || tipo.isBlank()) {
            return List.of();
        }

        /*
         * return alimentoDAO.buscarPorTipo(tipo);
         */

        return List.of();
    }

    public List<Alimento> listarAlimentosDisponibles() {

        /*
         * return alimentoDAO.buscarDisponibles();
         */

        return List.of();
    }

    public List<Alimento> listarAlimentosMenuDia(
            Date fecha) {

        if (fecha == null) {
            return List.of();
        }

        /*
         * return alimentoDAO.buscarMenuPorFecha(fecha);
         */

        return List.of();
    }
}