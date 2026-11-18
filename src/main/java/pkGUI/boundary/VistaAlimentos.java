package pkGUI.boundary;

import java.awt.BorderLayout;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public final class VistaAlimentos extends JPanel {
    private List<String> menuAlimentos;
    private final DefaultListModel<String> modelo = new DefaultListModel<>();
    private final JList<String> lista = new JList<>(modelo);
    private final JLabel seleccionado = new JLabel("Selecciona un alimento.");
    private Consumer<String> accionSeleccion = ignored -> { };

    public VistaAlimentos() {
        this(List.of());
    }

    public VistaAlimentos(List<String> menuAlimentos) {
        super(new BorderLayout(8, 8));
        this.menuAlimentos = List.copyOf(menuAlimentos);
        lista.addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                String alimento = lista.getSelectedValue();
                seleccionado.setText(alimento == null
                        ? "Selecciona un alimento."
                        : alimento);
            }
        });
        add(new JLabel("Catálogo de alimentos"), BorderLayout.NORTH);
        add(new JScrollPane(lista), BorderLayout.CENTER);
        add(seleccionado, BorderLayout.SOUTH);
        actualizarAlimentos(menuAlimentos);
    }

    public List<String> getMenuAlimentos() {
        return menuAlimentos;
    }

    public void actualizarAlimentos(List<String> alimentos) {
        menuAlimentos = List.copyOf(alimentos);
        modelo.clear();
        menuAlimentos.forEach(modelo::addElement);
    }

    public void setAccionSeleccion(Consumer<String> accionSeleccion) {
        this.accionSeleccion = Objects.requireNonNull(accionSeleccion);
    }

    public void ocultar() {
        setVisible(false);
    }

    public void mostrar() {
        setVisible(true);
    }

    public String obtenerSeleccion() {
        return lista.getSelectedValue();
    }

    public void seleccionarAlimento() {
        String alimento = lista.getSelectedValue();
        if (alimento != null) {
            accionSeleccion.accept(alimento);
        }
    }
}
