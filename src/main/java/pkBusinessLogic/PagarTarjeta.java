package pkBusinessLogic;

import java.awt.Desktop;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.sun.net.httpserver.HttpServer;

public class PagarTarjeta {

    private int monto;

    private String token;
    private String storeId;

    private String telefono;
    private String idTransaccion;
    private volatile String estadoPago;
    private HttpServer servidorRespuesta;
    private String paymentId;
    private String payWithCardUrl;
    private final HttpClient clienteHttp =
            HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(15))
                    .build();
    private static final String PREPARAR_URL =
            "https://pay.payphonetodoesposible.com/api/button/Prepare";
    private static final String CONFIRMAR_URL =
            "https://pay.payphonetodoesposible.com/api/button/V2/Confirm";

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

    public boolean isConfigurado() {
        return token != null && !token.isBlank()
                && storeId != null && !storeId.isBlank();
    }

    public void configurar(String token, String storeId) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException(
                    "El token de Payphone es obligatorio.");
        }
        if (storeId == null || storeId.isBlank()) {
            throw new IllegalArgumentException(
                    "El StoreID de Payphone es obligatorio.");
        }
        this.token = token.trim();
        this.storeId = storeId.trim();
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
        try {
            iniciarServidorRespuesta();
            HttpRequest preparacion = HttpRequest.newBuilder(
                            URI.create(PREPARAR_URL))
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(
                            crearCuerpoPreparacion(),
                            StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> respuesta =
                    clienteHttp.send(
                            preparacion,
                            HttpResponse.BodyHandlers.ofString());
            if (respuesta.statusCode() < 200
                    || respuesta.statusCode() >= 300) {
                estadoPago = "RECHAZADO";
                throw new IllegalStateException(
                        "PayPhone rechazó la preparación del pago.");
            }

            paymentId = obtenerJsonString(
                    respuesta.body(), "paymentId");
            payWithCardUrl = obtenerJsonString(
                    respuesta.body(), "payWithCard");
            if (paymentId == null || paymentId.isBlank()
                    || payWithCardUrl == null
                    || payWithCardUrl.isBlank()) {
                estadoPago = "RECHAZADO";
                throw new IllegalStateException(
                        "PayPhone no devolvió la URL de pago con tarjeta.");
            }

            estadoPago = "PENDIENTE";
            abrirEnNavegador(payWithCardUrl);
            esperarRespuestaPayPhone();
            return pagoAprobado();
        } catch (java.io.IOException | InterruptedException exception) {
            estadoPago = "RECHAZADO";
            if (exception instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new IllegalStateException(
                    "No se pudo completar la comunicación con PayPhone.",
                    exception);
        } finally {
            cerrarServidorRespuesta();
        }
    }

    private String crearCuerpoPreparacion() {
        String telefonoJson =
                telefono == null || telefono.isBlank()
                        ? ""
                        : ",\"phoneNumber\":\""
                                + escaparJson(telefono) + "\"";
        int puerto = servidorRespuesta.getAddress().getPort();
        String base = "http://127.0.0.1:" + puerto;
        return "{"
                + "\"amount\":" + monto + ","
                + "\"amountWithoutTax\":" + monto + ","
                + "\"amountWithTax\":0,"
                + "\"tax\":0,"
                + "\"service\":0,"
                + "\"tip\":0,"
                + "\"currency\":\"USD\","
                + "\"clientTransactionId\":\""
                + escaparJson(idTransaccion) + "\","
                + "\"storeId\":\""
                + escaparJson(storeId) + "\","
                + "\"reference\":\"Compra PoliComedor\","
                + "\"responseUrl\":\"" + base
                + "/payphone/response\","
                + "\"cancellationUrl\":\"" + base
                + "/payphone/cancel\""
                + telefonoJson
                + "}";
    }

    private void iniciarServidorRespuesta()
            throws java.io.IOException {
        servidorRespuesta = HttpServer.create(
                new InetSocketAddress(
                        java.net.InetAddress.getByName("127.0.0.1"),
                        0),
                0);
        servidorRespuesta.createContext(
                "/payphone/response",
                exchange -> {
                    try {
                        confirmarPagoDesdeRespuesta(
                                exchange.getRequestURI().getQuery());
                        responderCallback(
                                exchange,
                                pagoAprobado()
                                        ? "Pago aprobado. Puede cerrar esta ventana."
                                        : "El pago no fue aprobado. Puede cerrar esta ventana.");
                    } catch (RuntimeException error) {
                        estadoPago = "RECHAZADO";
                        responderCallback(
                                exchange,
                                "No se pudo confirmar el pago.");
                    } finally {
                        synchronized (this) {
                            notifyAll();
                        }
                    }
                });
        servidorRespuesta.createContext(
                "/payphone/cancel",
                exchange -> {
                    estadoPago = "CANCELADO";
                    responderCallback(
                            exchange,
                            "Pago cancelado. Puede cerrar esta ventana.");
                    synchronized (this) {
                        notifyAll();
                    }
                });
        servidorRespuesta.start();
    }

    private void cerrarServidorRespuesta() {
        if (servidorRespuesta != null) {
            servidorRespuesta.stop(0);
            servidorRespuesta = null;
        }
    }

    private void responderCallback(
            com.sun.net.httpserver.HttpExchange exchange,
            String mensaje)
            throws java.io.IOException {
        byte[] bytes =
                mensaje.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set(
                "Content-Type",
                "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(200, bytes.length);
        try (java.io.OutputStream output =
                exchange.getResponseBody()) {
            output.write(bytes);
        }
    }

    private void esperarRespuestaPayPhone()
            throws InterruptedException {
        synchronized (this) {
            long limite =
                    System.currentTimeMillis() + 5 * 60 * 1000;
            while ("PENDIENTE".equals(estadoPago)
                    && System.currentTimeMillis() < limite) {
                wait(1000);
            }
            if ("PENDIENTE".equals(estadoPago)) {
                estadoPago = "RECHAZADO";
            }
        }
    }

    private void confirmarPagoDesdeRespuesta(String query) {
        if (query == null || query.isBlank()) {
            estadoPago = "RECHAZADO";
            return;
        }

        String idPayPhone = parametroQuery(query, "id");
        String clientTransactionId =
                parametroQuery(query, "clientTransactionId");
        if (idPayPhone == null
                || clientTransactionId == null
                || !clientTransactionId.equals(idTransaccion)) {
            estadoPago = "RECHAZADO";
            return;
        }

        String cuerpo = "{"
                + "\"id\":" + idPayPhone + ","
                + "\"clientTxId\":\""
                + escaparJson(clientTransactionId) + "\"}";
        try {
            HttpRequest confirmacion = HttpRequest.newBuilder(
                            URI.create(CONFIRMAR_URL))
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(
                            cuerpo,
                            StandardCharsets.UTF_8))
                    .build();
            HttpResponse<String> respuesta =
                    HttpClient.newHttpClient().send(
                            confirmacion,
                            HttpResponse.BodyHandlers.ofString());
            if (respuesta.statusCode() >= 200
                    && respuesta.statusCode() < 300) {
                String estado = obtenerJsonString(
                        respuesta.body(), "transactionStatus");
                int codigo = obtenerJsonInt(
                        respuesta.body(), "statusCode");
                estadoPago = codigo == 3
                        || "Approved".equalsIgnoreCase(estado)
                        ? "APROBADO"
                        : "RECHAZADO";
            } else {
                estadoPago = "RECHAZADO";
            }
        } catch (java.io.IOException
                | InterruptedException exception) {
            estadoPago = "RECHAZADO";
            if (exception instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void abrirEnNavegador(String url) {
        try {
            if (Desktop.isDesktopSupported()
                    && Desktop.getDesktop().isSupported(
                            Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(URI.create(url));
                return;
            }
        } catch (java.io.IOException
                | UnsupportedOperationException
                | IllegalArgumentException ignored) {
        }
        throw new IllegalStateException(
                "No se pudo abrir el navegador para PayPhone.");
    }

    private String parametroQuery(String query, String clave) {
        for (String parte : query.split("&")) {
            String[] datos = parte.split("=", 2);
            if (datos.length == 2
                    && datos[0].equals(clave)) {
                try {
                    return java.net.URLDecoder.decode(
                            datos[1],
                            StandardCharsets.UTF_8);
                } catch (IllegalArgumentException exception) {
                    return datos[1];
                }
            }
        }
        return null;
    }

    private String escaparJson(String valor) {
        if (valor == null) {
            return "";
        }
        return valor.replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }

    private String obtenerJsonString(String json, String clave) {
        Pattern patron = Pattern.compile(
                "\"" + Pattern.quote(clave)
                        + "\"\\s*:\\s*\"([^\"]*)\"");
        Matcher coincidencia = patron.matcher(json);
        return coincidencia.find()
                ? coincidencia.group(1)
                : null;
    }

    private int obtenerJsonInt(String json, String clave) {
        Pattern patron = Pattern.compile(
                "\"" + Pattern.quote(clave)
                        + "\"\\s*:\\s*(-?[0-9]+)");
        Matcher coincidencia = patron.matcher(json);
        return coincidencia.find()
                ? Integer.parseInt(coincidencia.group(1))
                : 0;
    }
}