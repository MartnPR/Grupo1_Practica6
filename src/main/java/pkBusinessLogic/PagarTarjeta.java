package pkBusinessLogic;

import java.util.UUID;

public class PagarTarjeta {

    private int monto;

    private String token;
    private String storeId;

    private String telefono;
    private String idTransaccion;
    private String estadoPago;

    public PagarTarjeta() {
        this.token = System.getenv("PAYPHONE_TOKEN");
        this.storeId = System.getenv("PAYPHONE_STORE_ID");
        this.estadoPago = "NO_CONFIGURADO";
    }

    public PagarTarjeta(
            String token,
            String storeId) {

        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException(
                    "El token de Payphone es obligatorio."
            );
        }

        if (storeId == null || storeId.isBlank()) {
            throw new IllegalArgumentException(
                    "El StoreID de Payphone es obligatorio."
            );
        }

        this.token = token;
        this.storeId = storeId;

        this.estadoPago = "NO_INICIADO";
    }

    public void establecerMonto(double monto) {

        if (monto <= 0) {
            throw new IllegalArgumentException(
                    "El monto debe ser mayor que cero."
            );
        }

        // Payphone recibe valores monetarios en centavos
        this.monto = (int) Math.round(monto * 100);
    }

    public boolean solicitarPago(String telefono) {

        if (telefono == null || telefono.isBlank()) {
            return false;
        }

        if (monto <= 0) {
            return false;
        }

        if (token == null || token.isBlank()
                || storeId == null || storeId.isBlank()) {
            throw new IllegalStateException(
                    "La pasarela PayPhone no está configurada.");
        }

        this.telefono = telefono;
        this.idTransaccion =
                generarIdTransaccion();

        this.estadoPago = "PENDIENTE";

        return generarPagoPorPayPhone();
    }

    public String consultarEstadoPago() {

        /*
         * Aquí se implementará el GET de API Sale de Payphone
         * Estados esperados:
         *
         * PENDIENTE
         * APROBADO
         * RECHAZADO
         */

        return estadoPago;
    }

    public boolean pagoAprobado() {

        return "APROBADO".equalsIgnoreCase(
                consultarEstadoPago()
        );
    }

    private String generarIdTransaccion() {

        /*
         * Generar un identificador propio para relacionar Food4All con Payphone
         */

        return "F4A-"
                + UUID.randomUUID()
                      .toString()
                      .substring(0, 8)
                      .toUpperCase();
    }

    private boolean generarPagoPorPayPhone() {
        estadoPago = "NO_DISPONIBLE";
        throw new UnsupportedOperationException(
                "La integración de cobro PayPhone aún no está implementada.");
    }
}