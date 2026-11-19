package pkGUI.boundary;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.nio.file.Path;
import java.util.Objects;

import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import pkBusinessLogic.GestorTicket;

import java.awt.Image;

import javax.swing.SwingConstants;

public final class PantallaTicket extends PantallaBase {

    private final GestorTicket controlTicket;

    private final JLabel detalle =
            new JLabel("Aún no se ha generado un ticket.");

    public PantallaTicket(GestorTicket controlTicket) {

        super("Ticket");

        this.controlTicket =
                Objects.requireNonNull(controlTicket);

        JButton validar =
                new JButton("Validar ticket");

        validar.addActionListener(
                event -> validarTicket()
        );

        JPanel acciones =
                new JPanel(
                        new FlowLayout(FlowLayout.LEADING)
                );

        acciones.add(validar);

        contenido().add(
                new JLabel(
                        "Los tickets se guardan "
                        + "en el almacenamiento configurado."
                ),
                BorderLayout.NORTH
        );

        contenido().add(
                detalle,
                BorderLayout.CENTER
        );

        contenido().add(
                acciones,
                BorderLayout.SOUTH
        );
    }

    public void validarTicket() {

        if (!controlTicket.tieneTicketActual()) {

            mostrarEstado(
                    "No existe un ticket actual para validar."
            );

            return;
        }

        ejecutarSeguro(() ->
                mostrarEstado(
                        controlTicket.validarTicketActual()
                                ? "Ticket válido."
                                : "Ticket no válido."
                )
        );
    }

    public GestorTicket getControlTicket() {
        return controlTicket;
    }

    public void mostrarTicket() {

        actualizarDetalle();
    }

    private void actualizarDetalle() {

        detalle.setText(
                controlTicket.mostrarTicketActual()
        );

        detalle.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        detalle.setVerticalAlignment(
                SwingConstants.CENTER
        );

        detalle.setHorizontalTextPosition(
                SwingConstants.CENTER
        );

        detalle.setVerticalTextPosition(
                SwingConstants.TOP
        );

        Path rutaQr =
                controlTicket.obtenerRutaQrActual();

        if (rutaQr == null) {

            detalle.setIcon(null);
            return;
        }

        ImageIcon iconoOriginal =
                new ImageIcon(
                        rutaQr.toString()
                );

        Image imagenEscalada =
                iconoOriginal
                        .getImage()
                        .getScaledInstance(
                                220,
                                220,
                                Image.SCALE_SMOOTH
                        );

        detalle.setIcon(
                new ImageIcon(imagenEscalada)
        );
    }
}