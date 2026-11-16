package pkBusinessLogic;

public class PagarEfectivo {

    private double totalPagar;
    private double efectivoRecibido;

    public PagarEfectivo() {

        this.totalPagar = 0;
        this.efectivoRecibido = 0;
    }

    public void establecerDatosPago(
            double totalPagar,
            double efectivoRecibido) {

        if (totalPagar <= 0) {

            throw new IllegalArgumentException(
                    "El total debe ser mayor que cero."
            );
        }

        if (efectivoRecibido < 0) {

            throw new IllegalArgumentException(
                    "El efectivo recibido no puede ser negativo."
            );
        }

        this.totalPagar = totalPagar;
        this.efectivoRecibido = efectivoRecibido;
    }

    public boolean pagarConEfectivo() {

        return totalPagar > 0
                && efectivoRecibido >= totalPagar;
    }

    private double calcularVuelto() {

        if (!pagarConEfectivo()) {
            return 0;
        }

        return efectivoRecibido - totalPagar;
    }

    public String generarRecibo() {

        if (!pagarConEfectivo()) {

            return "Pago en efectivo pendiente "
                    + "o dinero insuficiente.";
        }

        return "Pago aprobado"
                + "\nTotal: $" + totalPagar
                + "\nEfectivo: $" + efectivoRecibido
                + "\nVuelto: $" + calcularVuelto();
    }
}