package pkGUI.boundary;

import pkBusinessLogic.SeleccionarAlimento;
import pkDataAccess.pkDTO.Alimento;
import java.util.Locale;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;
import java.util.Objects;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public final class PantallaComedor extends PantallaBase {
    private final VistaAlimentos menu;
    private final FormularioBusqueda busqueda;
    private final SeleccionarAlimento controlAlimentos;

    public PantallaComedor(SeleccionarAlimento controlAlimentos) {
        super("Comedor");
        this.menu = new VistaAlimentos(List.<Alimento>of());
        this.busqueda = new FormularioBusqueda();
        this.controlAlimentos = Objects.requireNonNull(controlAlimentos);
        busqueda.setAccionBusqueda(this::cargarMenu);
        JButton cargar = new JButton("Buscar alimentos");
        cargar.addActionListener(event -> cargarMenu());
        JButton pagar = new JButton("Ir a pago");
        pagar.addActionListener(event -> navegar("pago"));
        JPanel controles = new JPanel(new FlowLayout(FlowLayout.LEADING));
        controles.add(cargar);
        controles.add(pagar);
        JPanel busquedaPanel = new JPanel(new BorderLayout(8, 8));
        busquedaPanel.add(busqueda, BorderLayout.CENTER);
        busquedaPanel.add(controles, BorderLayout.EAST);
        contenido().add(busquedaPanel, BorderLayout.NORTH);
        contenido().add(menu, BorderLayout.CENTER);
        cargarMenu();
    }

    private void cargarMenu() {
        try {
            List<Alimento> filtrados = solicitarBusqueda();
            menu.actualizarAlimentos(filtrados);
            mostrarEstado("%d alimentos encontrados en el catálogo local."
                    .formatted(filtrados.size()));
        } catch (RuntimeException error) {
            mostrarEstado(mensajeError(error));
        }
    }


    public List<Alimento> solicitarBusqueda() {
        String criterio = busqueda.obtenerCriterioBusqueda().toLowerCase(Locale.ROOT);
        return controlAlimentos.listarAlimentosDisponibles().stream()
                .filter(alimento -> alimento.getNombre().toLowerCase(Locale.ROOT).contains(criterio)
                        || alimento.getTipo().toLowerCase(Locale.ROOT).contains(criterio)
                        || alimento.getDescripcion().toLowerCase(Locale.ROOT).contains(criterio))
                .toList();
    }

    public void mostrar() {
        menu.mostrar();
        setVisible(true);
    }

    public SeleccionarAlimento getControlAlimentos() {
        return controlAlimentos;
    }

    public List<Alimento> getMenuAlimentos() {
        return menu.getMenuAlimentos();
    }

    public FormularioBusqueda getFormularioBusqueda() {
        return busqueda;
    }
}
