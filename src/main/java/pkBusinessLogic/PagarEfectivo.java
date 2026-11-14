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

        this.totalPagar = totalPagar;
        this.efectivoRecibido = efectivoRecibido;
    }

    public boolean pagarConEfectivo() {

        if (totalPagar <= 0) {
            return false;
        }

        return efectivoRecibido >= totalPagar;
    }

    private double calcularVuelto() {

        if (!pagarConEfectivo()) {
            return 0;
        }

        return efectivoRecibido - totalPagar;
    }

    public String generarRecibo() {

        if (!pagarConEfectivo()) {
            return "Pago en efectivo no realizado.";
        }

        return "Pago realizado correctamente."
                + "\nTotal: $" + totalPagar
                + "\nEfectivo recibido: $" + efectivoRecibido
                + "\nVuelto: $" + calcularVuelto();
    }
}