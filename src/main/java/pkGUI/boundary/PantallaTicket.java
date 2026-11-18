package pkGUI.boundary;

import pkBusinessLogic.GestorTicket;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.nio.file.Path;
import java.util.Objects;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public final class PantallaTicket extends PantallaBase {
    private final GestorTicket controlTicket;
    private final JLabel detalle = new JLabel("Aún no se ha generado un ticket.");

    public PantallaTicket(GestorTicket controlTicket) {
        super("Ticket");
        this.controlTicket = Objects.requireNonNull(controlTicket);
        JButton generar = new JButton("Generar ticket");
        generar.addActionListener(event -> ejecutarSeguro(this::generarTicket));
        JButton validar = new JButton("Validar ticket");
        validar.addActionListener(event -> validarTicket());
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEADING));
        acciones.add(generar);
        acciones.add(validar);
        contenido().add(new JLabel(
                "Los tickets se guardan en el almacenamiento configurado."),
                BorderLayout.NORTH);
        contenido().add(detalle, BorderLayout.CENTER);
        contenido().add(acciones, BorderLayout.SOUTH);
    }

    public void generarTicket() {
        controlTicket.generarTicketParaVista();
        actualizarDetalle();
        mostrarEstado("Ticket generado y guardado.");
    }

    public void validarTicket() {
        if (!controlTicket.tieneTicketActual()) {
            mostrarEstado("Genera un ticket antes de validarlo.");
            return;
        }
        ejecutarSeguro(() -> mostrarEstado(controlTicket.validarTicketActual()
                ? "Ticket válido." : "Ticket no válido."));
    }

    public GestorTicket getControlTicket() {
        return controlTicket;
    }

    public void mostrarTicket() {
        actualizarDetalle();
        setVisible(true);
    }

    private void actualizarDetalle() {
        detalle.setText(controlTicket.mostrarTicketActual());
        Path rutaQr = controlTicket.obtenerRutaQrActual();
        detalle.setIcon(rutaQr == null ? null : new ImageIcon(rutaQr.toString()));
    }
}
