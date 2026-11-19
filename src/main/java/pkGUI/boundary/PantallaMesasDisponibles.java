package pkGUI.boundary;

import pkBusinessLogic.ReservarMesa;
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
import javax.swing.JTextField;

public final class PantallaMesasDisponibles extends PantallaBase {
    private List<String> mesas;
    private final ReservarMesa controlMesas;
    private final DefaultListModel<String> modelo = new DefaultListModel<>();
    private final JList<String> lista = new JList<>(modelo);
    private final JTextField franjaHoraria = new JTextField(18);
    private String credencialUsuario;

    public PantallaMesasDisponibles(ReservarMesa controlMesas) {
        super("Mesas disponibles");
        this.mesas = List.of();
        this.controlMesas = Objects.requireNonNull(controlMesas);
        JButton consultar = new JButton("Consultar disponibilidad");
        consultar.addActionListener(event -> consultarDisponibilidad());
        JButton actualizar = new JButton("Cargar mesas");
        actualizar.addActionListener(event -> actualizarMesas());
        JButton volver = new JButton("Volver al menú");
        volver.addActionListener(event -> navegar("menu"));
        JButton reservar = new JButton("Reservar");
        reservar.addActionListener(event -> reservarMesa());
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEADING));
        acciones.add(consultar);
        acciones.add(actualizar);
        acciones.add(reservar);
        acciones.add(volver);
        JPanel franja = new JPanel(new FlowLayout(FlowLayout.LEADING));
        franja.add(new JLabel("Franja horaria:"));
        franja.add(franjaHoraria);
        contenido().add(new JLabel(
                "Carga las mesas para consultar su disponibilidad."),
                BorderLayout.NORTH);
        contenido().add(new JScrollPane(lista), BorderLayout.CENTER);
        JPanel inferior = new JPanel(new BorderLayout());
        inferior.add(franja, BorderLayout.NORTH);
        inferior.add(acciones, BorderLayout.SOUTH);
        contenido().add(inferior, BorderLayout.SOUTH);
        actualizarMesas(this.mesas);
        lista.addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                seleccionarMesa();
            }
        });
    }

    public List<String> getMesas() {
        return mesas;
    }

    private void actualizarMesas(List<String> mesas) {
        this.mesas = List.copyOf(mesas);
        modelo.clear();
        this.mesas.forEach(modelo::addElement);
    }

    public ReservarMesa getControlMesas() {
        return controlMesas;
    }

    public void setCredencialUsuario(String credencialUsuario) {
        this.credencialUsuario = credencialUsuario;
    }

    public void seleccionarMesa() {
        String mesa = lista.getSelectedValue();
        if (mesa != null) {
            mostrarEstado("Mesa seleccionada: %s".formatted(mesa));
        }
    }

    public void MesasDisponibles() {
        actualizarMesas();
        setVisible(true);
    }

    public void actualizarMesas() {
        try {
            actualizarMesas(controlMesas.listarMesasParaVista());
            mostrarEstado("%d mesas cargadas.".formatted(mesas.size()));
        } catch (RuntimeException error) {
            mostrarEstado(mensajeError(error));
        }
    }

    public void consultarDisponibilidad() {
        String seleccionada = lista.getSelectedValue();
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

    private void reservarMesa() {
        String seleccionada = lista.getSelectedValue();
        if (seleccionada == null) {
            mostrarEstado("Selecciona una mesa.");
            return;
        }
        if (credencialUsuario == null || credencialUsuario.isBlank()) {
            mostrarEstado(
                    "No se pudo identificar al usuario autenticado.");
            return;
        }
        if (franjaHoraria.getText().trim().isBlank()) {
            mostrarEstado("Ingresa la franja horaria.");
            return;
        }
        try {
            boolean reservada = controlMesas.reservarMesa(
                    seleccionada,
                    credencialUsuario,
                    franjaHoraria.getText());
            if (reservada) {
                mostrarEstado("Mesa reservada correctamente.");
                actualizarMesas();
            } else {
                mostrarEstado(
                        "No se pudo reservar la mesa. Verifica disponibilidad y datos.");
            }
        } catch (RuntimeException error) {
            mostrarEstado(mensajeError(error));
        }
    }
}
