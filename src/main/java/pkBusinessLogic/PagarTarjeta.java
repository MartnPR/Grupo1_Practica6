package pkBusinessLogic;

import java.util.UUID;

public class PagarTarjeta {

    private int monto;

    private String token;
    private String storeId;

    private String telefono;
    private String idTransaccion;
    private String estadoPago;

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

        /*
         * Próxima implementación:
         *
         * POST:
         * https://pay.payphonetodoesposible.com/api/Sale
         *
         * Authorization:
         * Bearer <token>
         *
         * Content-Type:
         * application/json
         *
         * Datos principales:
         *
         * phoneNumber
         * countryCode = 593
         * amount
         * clientTransactionId
         * reference
         * storeId
         * currency = USD
         */

        return true;
    }
}