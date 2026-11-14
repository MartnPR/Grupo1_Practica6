package pkBusinessLogic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import pkDataAccess.pkDTO.Factura;
import pkDataAccess.pkDTO.Ticket;

public class GestorTicket {

    private static final Path CARPETA_QR =
            Path.of("tickets", "qr");

    /*
     * Temporal mientras TicketDAO/BD no genere IDs
     */
    private static final AtomicInteger SECUENCIA =
            new AtomicInteger(1);

    public GestorTicket() {
    }

    public Ticket generarTicket(Factura factura) {

        if (factura == null) {
            throw new IllegalArgumentException(
                    "No se puede generar un ticket sin factura."
            );
        }

        Ticket ticket = new Ticket();

        ticket.setIdTicket(
                SECUENCIA.getAndIncrement()
        );

        ticket.setFechaGeneracion(new Date());
        ticket.setValido(true);
        ticket.setFactura(factura);

        BitMatrix codigoQR =
                generarMatrizQR(ticket, factura);

        ticket.setCodigoQR(codigoQR);

        /*
         * Mantiene también la relación Factura -> Ticket
         */
        factura.setTicket(ticket);

        guardarQRComoImagen(ticket);

        return ticket;
    }

    private BitMatrix generarMatrizQR(
            Ticket ticket,
            Factura factura) {

        String contenidoQR =
                "FOOD4ALL"
                + "|TICKET=" + ticket.getIdTicket()
                + "|FACTURA=" + factura.getIdFactura();

        QRCodeWriter escritor =
                new QRCodeWriter();

        try {

            return escritor.encode(
                    contenidoQR,
                    BarcodeFormat.QR_CODE,
                    300,
                    300
            );

        } catch (WriterException e) {

            throw new IllegalStateException(
                    "No se pudo generar el código QR.",
                    e
            );
        }
    }

    public Path guardarQRComoImagen(Ticket ticket) {

        if (ticket == null
                || ticket.getCodigoQR() == null) {

            throw new IllegalArgumentException(
                    "El ticket no posee un código QR."
            );
        }

        try {

            Files.createDirectories(CARPETA_QR);

            Path ruta =
                    CARPETA_QR.resolve(
                            "Ticket_"
                            + ticket.getIdTicket()
                            + ".png"
                    );

            MatrixToImageWriter.writeToPath(
                    ticket.getCodigoQR(),
                    "PNG",
                    ruta
            );

            return ruta;

        } catch (IOException e) {

            throw new IllegalStateException(
                    "No se pudo guardar el código QR.",
                    e
            );
        }
    }

    public boolean validarTicket(Ticket ticket) {

        return ticket != null
                && ticket.getIdTicket() != null
                && ticket.getCodigoQR() != null
                && ticket.isValido();
    }

    public void utilizarTicket(Ticket ticket) {

        if (!validarTicket(ticket)) {
            throw new IllegalStateException(
                    "El ticket no es válido."
            );
        }

        /*
         * Después de retirar la comida, el ticket deja de ser válido
         */
        ticket.setValido(false);
    }
}