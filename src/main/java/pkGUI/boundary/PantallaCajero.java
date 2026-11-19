package pkGUI.boundary;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.Objects;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import pkBusinessLogic.GestorTicket;
import pkDataAccess.pkDTO.Ticket;

/**
 * Pantalla de operaciones básicas del cajero.
 */
public final class PantallaCajero extends PantallaBase {

    private final VistaPedidosCajero vistaPedidos;
    private final GestorTicket gestorTicket;

    /**
     * Construye la pantalla del cajero.
     *
     * @param gestorTicket gestor de tickets
     * @param pagos servicio de pagos en efectivo
     * @param gestorFactura gestor de facturas
     */
    public PantallaCajero(GestorTicket gestorTicket) {
        super("Caja");
        this.gestorTicket = Objects.requireNonNull(gestorTicket);
        this.vistaPedidos = new VistaPedidosCajero(this.gestorTicket);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEADING));
        JButton validar = new JButton("Validar ticket");
        validar.addActionListener(event -> validarTicket());
        JButton actualizar = new JButton("Actualizar listado");
        actualizar.addActionListener(event -> vistaPedidos.listarPedidos());
        acciones.add(validar);
        acciones.add(actualizar);

        contenido().add(new JLabel("Operaciones del cajero."), BorderLayout.NORTH);
        contenido().add(vistaPedidos, BorderLayout.CENTER);
        contenido().add(acciones, BorderLayout.SOUTH);
    }

    private void validarTicket() {
        Ticket seleccionado = vistaPedidos.ticketSeleccionado();
        if (seleccionado == null) {
            mostrarEstado("Selecciona un ticket válido.");
            return;
        }
        try {
            if (gestorTicket.validarTicketSeleccionado(seleccionado)) {
                mostrarEstado("Ticket #" + seleccionado.getIdTicket()
                        + " validado.");
                vistaPedidos.listarPedidos();
            } else {
                mostrarEstado("El ticket seleccionado no se pudo validar.");
            }
        } catch (RuntimeException error) {
            mostrarEstado(mensajeError(error));
        }
    }

    /**
     * Obtiene el gestor de tickets de la pantalla.
     *
     * @return gestor de tickets
     */
    public GestorTicket getGestorTicket() {
        return gestorTicket;
    }

}
