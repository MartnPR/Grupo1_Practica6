package pkGUI.boundary;

import java.awt.BorderLayout;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import pkBusinessLogic.GestorTicket;
import pkDataAccess.pkDTO.Ticket;

public final class VistaPedidosCajero extends JPanel {
    private final DefaultListModel<String> pedidos = new DefaultListModel<>();
    private final JList<String> lista = new JList<>(pedidos);
    private final JLabel detalleTicket = new JLabel("Selecciona un pedido para ver sus datos.");
    private final GestorTicket gestorTicket;
    private final Map<String, Ticket> tickets = new LinkedHashMap<>();

    public VistaPedidosCajero(GestorTicket gestorTicket) {
        super(new BorderLayout(8, 8));
        this.gestorTicket = gestorTicket;
        add(new JLabel("Tickets válidos disponibles."), BorderLayout.NORTH);
        add(new JScrollPane(lista), BorderLayout.CENTER);
        add(detalleTicket, BorderLayout.SOUTH);
        listarPedidos();
        lista.addListSelectionListener(event -> mostrarDatosTicket());
    }

    public void listarPedidos() {
        setVisible(true);
        pedidos.clear();
        tickets.clear();
        try {
            for (Ticket ticket : gestorTicket.listarTicketsValidos()) {
                String texto = "Ticket #" + ticket.getIdTicket()
                        + " - Factura " + ticket.getIdFactura()
                        + " - " + ticket.getFechaGeneracion();
                pedidos.addElement(texto);
                tickets.put(texto, ticket);
            }
            detalleTicket.setText(pedidos.isEmpty()
                    ? "No hay tickets válidos."
                    : "Selecciona un ticket para validarlo.");
        } catch (RuntimeException error) {
            detalleTicket.setText("No se pudieron cargar los tickets: "
                    + error.getMessage());
        }
    }

    public void mostrarDatosTicket() {
        setVisible(true);
        String pedido = lista.getSelectedValue();
        detalleTicket.setText(pedido == null
                ? "No hay un pedido seleccionado."
                : "Pedido seleccionado: " + pedido);
    }

    public void ocultar() {
        setVisible(false);
    }

    public String pedidoSeleccionado() {
        return lista.getSelectedValue();
    }

    public Ticket ticketSeleccionado() {
        String seleccionado = lista.getSelectedValue();
        return seleccionado == null ? null : tickets.get(seleccionado);
    }
}
