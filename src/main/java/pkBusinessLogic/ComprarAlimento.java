package pkBusinessLogic;

import pkDataAccess.AlimentoDAO;
import pkEntity.Compra;

public class ComprarAlimento {

    private final AlimentoDAO alimentoDAO;

    private Compra compraActual;
    private double descuento;

    public ComprarAlimento(AlimentoDAO alimentoDAO) {

        if (alimentoDAO == null) {
            throw new IllegalArgumentException(
                    "AlimentoDAO no puede ser nulo."
            );
        }

        this.alimentoDAO = alimentoDAO;
        this.descuento = 0;
    }

    public Compra iniciarCompra() {

        compraActual = new Compra();

        return compraActual;
    }

    public String verCarritoCompra(Compra compra) {

        if (compra == null) {
            return "El carrito está vacío.";
        }

        return compra.toString();
    }

    public void leerCuponDescuento(
            String codigo,
            double descuento) {

        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException(
                    "Código de cupón inválido."
            );
        }

        if (descuento < 0 || descuento > 100) {
            throw new IllegalArgumentException(
                    "El descuento debe estar "
                    + "entre 0 y 100."
            );
        }

        this.descuento = descuento;
    }

    public double getDescuento() {
        return descuento;
    }
}