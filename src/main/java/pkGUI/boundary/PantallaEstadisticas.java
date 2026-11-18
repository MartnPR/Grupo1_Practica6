package pkGUI.boundary;

import pkBusinessLogic.CalcularEstadistica;
import pkBusinessLogic.GeneradorGrafico;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;
import java.util.Objects;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public final class PantallaEstadisticas extends PantallaBase {
    private final CalcularEstadistica calculador;
    private final GeneradorGrafico graficos;
    private final JComboBox<String> selectorMesa;
    private final JLabel contextoMesa = new JLabel();

    public PantallaEstadisticas(CalcularEstadistica calculador, GeneradorGrafico graficos) {
        this(calculador, graficos, List.of());
    }

    public PantallaEstadisticas(
            CalcularEstadistica calculador,
            GeneradorGrafico graficos,
            List<String> mesas) {
        super("Estadísticas");
        this.calculador = Objects.requireNonNull(calculador);
        this.graficos = Objects.requireNonNull(graficos);
        selectorMesa = new JComboBox<>(List.copyOf(mesas).toArray(String[]::new));
        selectorMesa.addActionListener(event -> actualizarContextoMesa());
        JPanel selector = new JPanel(new FlowLayout(FlowLayout.LEADING));
        selector.add(new JLabel("Mesa para el contexto de estadísticas:"));
        selector.add(selectorMesa);
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEADING));
        JButton reporte = new JButton("Generar reporte");
        reporte.addActionListener(event -> ejecutarOperacion(
                () -> calculador.generarReporte().toString()));
        JButton pedidos = new JButton("Gráfico de pedidos");
        pedidos.addActionListener(event -> ejecutarOperacion(() -> {
            graficos.generarDiagramaBarraPedidos();
            return "Gráfico de pedidos generado.";
        }));
        JButton reservas = new JButton("Gráfico de reservas");
        reservas.addActionListener(event -> ejecutarOperacion(() -> {
            graficos.generarDiagramaPastel();
            return "Gráfico de reservas generado.";
        }));
        JButton verMesas = new JButton("Mesas disponibles");
        verMesas.addActionListener(event -> MesasDisponibles());
        acciones.add(reporte);
        acciones.add(pedidos);
        acciones.add(reservas);
        acciones.add(verMesas);
        contenido().add(new JLabel(
                "La Mesa seleccionada queda disponible en seleccionMesa(). "
                        + "Los contratos actuales de estadísticas generan resultados globales."),
                BorderLayout.NORTH);
        JPanel controles = new JPanel(new BorderLayout());
        controles.add(selector, BorderLayout.NORTH);
        controles.add(contextoMesa, BorderLayout.CENTER);
        controles.add(acciones, BorderLayout.SOUTH);
        contenido().add(controles, BorderLayout.CENTER);
        actualizarContextoMesa();
    }

    private void ejecutarOperacion(java.util.function.Supplier<String> operacion) {
        try {
            mostrarEstado(operacion.get());
        } catch (RuntimeException error) {
            mostrarEstado(mensajeError(error));
        }
    }

    public void MesasDisponibles() {
        navegar("mesas");
    }

    public String seleccionMesa() {
        return (String) selectorMesa.getSelectedItem();
    }

    private void actualizarContextoMesa() {
        String mesa = seleccionMesa();
        contextoMesa.setText(mesa == null
                ? "No hay mesas para seleccionar."
                : "Contexto seleccionado: %s (las estadísticas son globales).".formatted(mesa));
    }

    public CalcularEstadistica getCalculador() {
        return calculador;
    }

    public GeneradorGrafico getGraficos() {
        return graficos;
    }
}
