package pkGUI.boundary;

import pkBusinessLogic.SeleccionarAlimento;
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
        this.menu = new VistaAlimentos();
        this.busqueda = new FormularioBusqueda();
        this.controlAlimentos = Objects.requireNonNull(controlAlimentos);
        busqueda.setAccionBusqueda(this::cargarMenu);
        menu.setAccionSeleccion(this::seleccionarAlimento);
        JButton cargar = new JButton("Buscar alimentos");
        cargar.addActionListener(event -> cargarMenu());
        JButton seleccionar = new JButton("Seleccionar alimento");
        seleccionar.addActionListener(event -> menu.seleccionarAlimento());
        JButton pagar = new JButton("Ir a pago");
        pagar.addActionListener(event -> navegar("pago"));
        JPanel controles = new JPanel(new FlowLayout(FlowLayout.LEADING));
        controles.add(cargar);
        controles.add(seleccionar);
        controles.add(pagar);
        JPanel busquedaPanel = new JPanel(new BorderLayout(8, 8));
        busquedaPanel.add(busqueda, BorderLayout.CENTER);
        busquedaPanel.add(controles, BorderLayout.EAST);
        contenido().add(busquedaPanel, BorderLayout.NORTH);
        contenido().add(menu, BorderLayout.CENTER);
        cargarMenu();
    }

    public void cargarMenu() {
        solicitarBusqueda();
    }

    public boolean solicitarBusqueda() {
        try {
            List<String> filtrados = controlAlimentos
                    .listarAlimentosPorTipoParaVista(
                            busqueda.obtenerCriterioBusqueda());
            menu.actualizarAlimentos(filtrados);
            mostrarEstado("%d alimentos encontrados."
                    .formatted(filtrados.size()));
            return true;
        } catch (RuntimeException error) {
            mostrarEstado(mensajeError(error));
            return false;
        }
    }

    private void seleccionarAlimento(String opcion) {
        try {
            if (controlAlimentos.seleccionarAlimento(opcion)) {
                mostrarEstado(controlAlimentos.resumenCarritoParaVista());
            } else {
                mostrarEstado("El alimento ya no está disponible.");
            }
        } catch (RuntimeException error) {
            mostrarEstado(mensajeError(error));
        }
    }

    public void mostrar() {
        menu.mostrar();
        setVisible(true);
    }

    public SeleccionarAlimento getControlAlimentos() {
        return controlAlimentos;
    }

    public List<String> getMenuAlimentos() {
        return menu.getMenuAlimentos();
    }

    public FormularioBusqueda getFormularioBusqueda() {
        return busqueda;
    }
}
