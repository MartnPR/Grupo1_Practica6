package pkBusinessLogic;

public class PagarTarjetaOTransferencia {

    private double monto;
    private boolean transferenciaVerificada;
    private boolean tarjetaAprobada;

    public PagarTarjetaOTransferencia() {
        this.monto = 0;
        this.transferenciaVerificada = false;
        this.tarjetaAprobada = false;
    }

    public void establecerMonto(double monto) {

        if (monto <= 0) {
            throw new IllegalArgumentException(
                    "El monto debe ser mayor que cero."
            );
        }

        this.monto = monto;
    }

    public void establecerTransferenciaVerificada(
            boolean transferenciaVerificada) {

        this.transferenciaVerificada =
                transferenciaVerificada;
    }

    public void establecerTarjetaAprobada(
            boolean tarjetaAprobada) {

        this.tarjetaAprobada = tarjetaAprobada;
    }

    public boolean pagarConTransferencia() {

        if (monto <= 0) {
            return false;
        }

        /*
         * True únicamente cuando el cajero haya verificado la transferencia
         */
        return transferenciaVerificada;
    }

    public boolean pagarConTarjeta() {

        if (monto <= 0) {
            return false;
        }

        generarPagoPorPayPhone();

        return tarjetaAprobada;
    }

    private void generarPagoPorPayPhone() {

        /*
         * Aquí iría la integración con PayPhone
         *
         * PayPhone deberá indicar si la transacción fue aprobada o rechazad
         */
    }
}