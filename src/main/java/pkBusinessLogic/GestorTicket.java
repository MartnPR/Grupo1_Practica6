package pkBusinessLogic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import pkDataAccess.pkDAO.TicketDAO;
import pkDataAccess.pkDTO.Factura;
import pkDataAccess.pkDTO.Ticket;
import java.util.List;

public class GestorTicket {

    private static final Path CARPETA_QR =
            Path.of("tickets", "qr");

    private Factura facturaPendiente;
    private Ticket ticketActual;
    private Path rutaQrActual;

    private final TicketDAO ticketDAO;

    public GestorTicket() {
        this.ticketDAO = new TicketDAO();
    }

    public void prepararFactura(Factura factura) {

        if (factura == null) {
            throw new IllegalArgumentException(
                    "No se puede preparar un ticket sin factura."
            );
        }

        this.facturaPendiente = factura;
    }

    public String generarTicketParaVista() {

        if (facturaPendiente == null) {
            throw new IllegalStateException(
                    "No hay una compra confirmada "
                    + "para generar un ticket."
            );
        }

        Ticket ticket =
                generarTicket(facturaPendiente);

        facturaPendiente = null;

        return resumenTicket(ticket);
    }

    public boolean validarTicketActual() {
        return validarTicket(ticketActual);
    }

    public List<Ticket> listarTicketsValidos() {
        return ticketDAO.buscarPorValido(true);
    }

    public boolean validarTicketSeleccionado(Ticket ticket) {
        if (!validarTicket(ticket)) {
            return false;
        }
        utilizarTicket(ticket);
        return true;
    }

    public boolean tieneTicketActual() {
        return ticketActual != null;
    }

    public String mostrarTicketActual() {

        if (ticketActual == null) {
            return "Aún no se ha generado un ticket.";
        }

        return resumenTicket(ticketActual);
    }

    public Path obtenerRutaQrActual() {
        return rutaQrActual;
    }

    public Ticket generarTicket(Factura factura) {

        if (factura == null) {
            throw new IllegalArgumentException(
                    "No se puede generar un ticket sin factura."
            );
        }

        if (factura.getIdFactura() == null
                || factura.getIdFactura().isBlank()) {

            throw new IllegalArgumentException(
                    "La factura debe tener un identificador "
                    + "antes de generar el ticket."
            );
        }

        Ticket ticket = new Ticket();

        ticket.setIdTicket(
                ticketDAO.getNextId()
        );

        ticket.setFechaGeneracion(
                new Date()
        );

        ticket.setValido(true);

        // FK entre Ticket y Factura
        ticket.setIdFactura(
                factura.getIdFactura()
        );

        BitMatrix codigoQR =
                generarMatrizQR(
                        ticket,
                        factura
                );

        ticket.setCodigoQR(
                codigoQR
        );

        this.ticketActual = ticket;

        // Relación desde Factura hacia Ticket
        factura.setTicket(ticket);

        this.rutaQrActual =
                guardarQRComoImagen(ticket);

        // Persistencia del ticket
        ticketDAO.agregar(ticket);

        return ticket;
    }

    private String resumenTicket(
            Ticket ticket) {

        return "Ticket #%d — %s".formatted(
                ticket.getIdTicket(),
                ticket.getFechaGeneracion()
        );
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

    public Path guardarQRComoImagen(
            Ticket ticket) {

        if (ticket == null
                || ticket.getCodigoQR() == null) {

            throw new IllegalArgumentException(
                    "El ticket no posee un código QR."
            );
        }

        try {

            Files.createDirectories(
                    CARPETA_QR
            );

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

    public boolean validarTicket(
            Ticket ticket) {

        if (ticket == null
                || ticket.getIdTicket() == null) {

            return false;
        }

        Ticket ticketPersistido =
                ticketDAO.buscarPorId(
                        ticket.getIdTicket()
                );

        return ticketPersistido != null
                && ticketPersistido.isValido();
    }

    public void utilizarTicket(
            Ticket ticket) {

        if (!validarTicket(ticket)) {

            throw new IllegalStateException(
                    "El ticket no es válido."
            );
        }

        ticket.setValido(false);

        ticketDAO.actualizar(ticket);
    }
}