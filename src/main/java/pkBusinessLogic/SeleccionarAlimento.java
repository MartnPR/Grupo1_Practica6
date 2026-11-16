package pkBusinessLogic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pkDataAccess.pkDAO.AlimentoDAO;
import pkDataAccess.pkDTO.Alimento;

public class SeleccionarAlimento {

    private final AlimentoDAO alimentoDAO;
    private final List<Alimento> alimentosSeleccionados;
    private double descuento;

    public SeleccionarAlimento(
            AlimentoDAO alimentoDAO) {

        if (alimentoDAO == null) {
            throw new IllegalArgumentException(
                    "AlimentoDAO no puede ser nulo."
            );
        }

        this.alimentoDAO = alimentoDAO;
        this.alimentosSeleccionados = new ArrayList<>();
        this.descuento = 0;
    }

    public int consultarStockAlimento(
            Alimento alimento) {

        if (alimento == null) {
            return 0;
        }

        Alimento alimentoEncontrado =
                alimentoDAO.buscarPorId(
                        alimento.getIdAlimento()
                );

        if (alimentoEncontrado == null) {
            return 0;
        }

        return alimentoEncontrado
                .getCantidadEnStock();
    }

    public List<Alimento> listarAlimentosPorTipo(
            String tipo) {

        if (tipo == null || tipo.isBlank()) {
            return List.of();
        }

        return alimentoDAO.buscarPorTipo(tipo);
    }

    public List<Alimento> listarAlimentosDisponibles() {

        List<Alimento> alimentos =
                alimentoDAO.leerTodos();

        List<Alimento> disponibles =
                new ArrayList<>();

        for (Alimento alimento : alimentos) {

            if (alimento.getCantidadEnStock() > 0) {
                disponibles.add(alimento);
            }
        }

        return disponibles;
    }

    public boolean agregarAlimento(
            Alimento alimento) {

        if (alimento == null) {
            return false;
        }

        if (alimento.getCantidadEnStock() <= 0) {
            return false;
        }

        alimentosSeleccionados.add(alimento);

        return true;
    }

    public boolean eliminarAlimento(
            Alimento alimento) {

        if (alimento == null) {
            return false;
        }

        return alimentosSeleccionados.remove(alimento);
    }

    public List<Alimento>
            listarAlimentosSeleccionados() {

        return Collections.unmodifiableList(
                alimentosSeleccionados
        );
    }

    public double mostrarSubtotal() {

        double subtotal = 0;

        for (Alimento alimento
                : alimentosSeleccionados) {

            subtotal +=
                    alimento.getPrecioUnitario();
        }

        return subtotal;
    }

    public void aplicarDescuento(
            double porcentaje) {

        if (porcentaje < 0 || porcentaje > 100) {

            throw new IllegalArgumentException(
                    "El descuento debe estar entre 0 y 100."
            );
        }

        this.descuento = porcentaje;
    }

    public double mostrarDescuentos() {

        return mostrarSubtotal()
                * descuento / 100.0;
    }

    public double mostrarTotal() {

        return mostrarSubtotal()
                - mostrarDescuentos();
    }

    public void vaciarCarrito() {

        alimentosSeleccionados.clear();
        descuento = 0;
    }

    public double getDescuento() {

        return descuento;
    }
}