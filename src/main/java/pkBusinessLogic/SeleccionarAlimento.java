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

    public Alimento consultarStockAlimento(
            Alimento alimento) {

        if (alimento == null) {
            return null;
        }

        
         
         
         return alimentoDAO.buscarPorId(
              alimento.getIdAlimento()
         );
        
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

        
         return alimentoDAO.leerTodos();
    }

    public List<Alimento> listarAlimentosTipo(String tipo) {

        if (tipo == null || tipo.isBlank()) {
            return List.of();
        }

        
        return alimentoDAO.buscarPorTipo(tipo);
        
    }
}