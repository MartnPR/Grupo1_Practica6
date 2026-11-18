package pkGUI.boundary;

import java.awt.BorderLayout;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public final class VistaPedidosCajero extends JPanel {
    private final DefaultListModel<String> pedidos = new DefaultListModel<>();
    private final JList<String> lista = new JList<>(pedidos);
    private final JLabel detalleTicket = new JLabel("Selecciona un pedido para ver sus datos.");

    public VistaPedidosCajero() {
        super(new BorderLayout(8, 8));
        add(new JLabel("No hay pedidos disponibles."), BorderLayout.NORTH);
        add(new JScrollPane(lista), BorderLayout.CENTER);
        add(detalleTicket, BorderLayout.SOUTH);
        listarPedidos();
    }

    public void listarPedidos() {
        setVisible(true);
        pedidos.clear();
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
}
