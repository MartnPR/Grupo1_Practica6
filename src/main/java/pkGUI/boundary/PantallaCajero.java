package pkGUI.boundary;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.Objects;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import pkBusinessLogic.GestorFactura;
import pkBusinessLogic.GestorTicket;
import pkBusinessLogic.PagarEfectivo;

/**
 * Pantalla de operaciones básicas del cajero.
 */
public final class PantallaCajero extends PantallaBase {

    private final VistaPedidosCajero vistaPedidos;
    private final GestorTicket gestorTicket;
    private final PagarEfectivo pagos;
    private final GestorFactura gestorFactura;

    /**
     * Construye la pantalla del cajero.
     *
     * @param gestorTicket gestor de tickets
     * @param pagos servicio de pagos en efectivo
     * @param gestorFactura gestor de facturas
     */
    public PantallaCajero(
            GestorTicket gestorTicket,
            PagarEfectivo pagos,
            GestorFactura gestorFactura) {
        super("Caja");
        this.vistaPedidos = new VistaPedidosCajero();
        this.gestorTicket = Objects.requireNonNull(gestorTicket);
        this.pagos = Objects.requireNonNull(pagos);
        this.gestorFactura = Objects.requireNonNull(gestorFactura);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEADING));
        JButton ticket = new JButton("Generar ticket");
        ticket.addActionListener(event -> generarTicket());
        JButton pago = new JButton("Registrar pago en efectivo");
        pago.addActionListener(event -> registrarPago());
        JButton factura = new JButton("Generar factura");
        factura.addActionListener(event -> generarFactura());
        acciones.add(ticket);
        acciones.add(pago);
        acciones.add(factura);

        contenido().add(new JLabel("Operaciones del cajero."), BorderLayout.NORTH);
        contenido().add(vistaPedidos, BorderLayout.CENTER);
        contenido().add(acciones, BorderLayout.SOUTH);
    }

    public void generarTicket() {
        mostrarEstado(mensajePedidoNoDisponible(
                "No se puede generar un ticket sin un pedido cargado."));
    }

    public void generarFactura() {
        mostrarEstado(mensajePedidoNoDisponible(
                "No se puede generar una factura sin el detalle de un pedido."));
    }

    private void registrarPago() {
        mostrarEstado(mensajePedidoNoDisponible(
                "No se puede registrar un pago sin un pedido cargado."));
    }

    private String mensajePedidoNoDisponible(String accion) {
        return vistaPedidos.pedidoSeleccionado() == null
                ? "No hay pedidos cargados. " + accion
                : accion;
    }

    /**
     * Obtiene el gestor de tickets de la pantalla.
     *
     * @return gestor de tickets
     */
    public GestorTicket getGestorTicket() {
        return gestorTicket;
    }

    /**
     * Obtiene el servicio de pagos.
     *
     * @return servicio de pagos
     */
    public PagarEfectivo getPagos() {
        return pagos;
    }

    /**
     * Obtiene el gestor de facturas.
     *
     * @return gestor de facturas
     */
    public GestorFactura getGestorFactura() {
        return gestorFactura;
    }
}
