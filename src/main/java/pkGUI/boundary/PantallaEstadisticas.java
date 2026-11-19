package pkGUI.boundary;

import pkBusinessLogic.CalcularEstadistica;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.Objects;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public final class PantallaEstadisticas extends PantallaBase {
    private final CalcularEstadistica calculador;
    private final JLabel resultados =
            new JLabel("Presiona consultar estadísticas.");

    public PantallaEstadisticas(CalcularEstadistica calculador) {
        super("Estadísticas");
        this.calculador = Objects.requireNonNull(calculador);
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEADING));
        JButton consultar = new JButton("Consultar estadísticas");
        consultar.addActionListener(event -> consultarEstadisticas());
        acciones.add(consultar);
        contenido().add(resultados, BorderLayout.CENTER);
        contenido().add(acciones, BorderLayout.SOUTH);
    }

    private void consultarEstadisticas() {
        try {
            double ventas = calculador.calcularTotalVentas();
            int pedidos = calculador.calcularTotalComidasVendidas();
            int reservas = calculador.calcularTotalReservasHechas();
            resultados.setText(
                    "<html>Total ventas: " + ventas
                            + "<br>Total pedidos/comidas vendidas: " + pedidos
                            + "<br>Total reservas: " + reservas + "</html>");
            mostrarEstado("Estadísticas consultadas.");
        } catch (RuntimeException error) {
            mostrarEstado(mensajeError(error));
        }
    }

    public CalcularEstadistica getCalculador() {
        return calculador;
    }
}
