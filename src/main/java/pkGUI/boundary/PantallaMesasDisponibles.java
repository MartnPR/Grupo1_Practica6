package pkGUI.boundary;

import pkBusinessLogic.ReservarMesa;
import pkDataAccess.pkDAO.MesaDAO;
import pkDataAccess.pkDTO.Mesa;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;
import java.util.Objects;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public final class PantallaMesasDisponibles extends PantallaBase {
    private List<Mesa> mesas;
    private final ReservarMesa controlMesas;
    private final MesaDAO mesaDAO;
    private final DefaultListModel<Mesa> modelo = new DefaultListModel<>();
    private final JList<Mesa> lista = new JList<>(modelo);

    public PantallaMesasDisponibles(
            List<Mesa> mesas,
            ReservarMesa controlMesas,
            MesaDAO mesaDAO) {
        super("Mesas disponibles");
        this.mesas = List.copyOf(mesas);
        this.controlMesas = Objects.requireNonNull(controlMesas);
        this.mesaDAO = Objects.requireNonNull(mesaDAO);
        lista.setCellRenderer((list, mesa, index, isSelected, cellHasFocus) -> {
            JLabel item = new JLabel("Mesa %d — %s".formatted(
                    mesa.getIdMesa(), mesa.isReservada() ? "reservada" : "disponible"));
            if (isSelected) {
                item.setOpaque(true);
                item.setBackground(list.getSelectionBackground());
                item.setForeground(list.getSelectionForeground());
            }
            return item;
        });
        JButton consultar = new JButton("Consultar disponibilidad");
        consultar.addActionListener(event -> consultarDisponibilidad());
        JButton actualizar = new JButton("Cargar mesas");
        actualizar.addActionListener(event -> cargarMesas());
        JButton volver = new JButton("Volver al menú");
        volver.addActionListener(event -> navegar("menu"));
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEADING));
        acciones.add(consultar);
        acciones.add(actualizar);
        acciones.add(volver);
        contenido().add(new JLabel(
                "Carga las mesas para consultar su disponibilidad."),
                BorderLayout.NORTH);
        contenido().add(new JScrollPane(lista), BorderLayout.CENTER);
        contenido().add(acciones, BorderLayout.SOUTH);
        actualizarMesas(this.mesas);
    }

    public List<Mesa> getMesas() {
        return mesas;
    }

    public void actualizarMesas(List<Mesa> mesas) {
        this.mesas = List.copyOf(mesas);
        modelo.clear();
        this.mesas.forEach(modelo::addElement);
    }

    public ReservarMesa getControlMesas() {
        return controlMesas;
    }

    public Mesa seleccionMesa() {
        return lista.getSelectedValue();
    }

    public void MesasDisponibles() {
        cargarMesas();
        setVisible(true);
    }

    private void cargarMesas() {
        try {
            actualizarMesas(mesaDAO.leerTodos());
            mostrarEstado("%d mesas cargadas.".formatted(mesas.size()));
        } catch (RuntimeException error) {
            mostrarEstado(mensajeError(error));
        }
    }

    private void consultarDisponibilidad() {
        Mesa seleccionada = seleccionMesa();
        if (seleccionada == null) {
            mostrarEstado("Selecciona una mesa.");
            return;
        }
        try {
            mostrarEstado(controlMesas.consultarDisponibilidadMesa(seleccionada)
                    ? "Mesa disponible." : "Mesa no disponible.");
        } catch (RuntimeException error) {
            mostrarEstado(mensajeError(error));
        }
    }
}
