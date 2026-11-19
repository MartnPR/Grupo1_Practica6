package pkGUI.boundary;

import pkBusinessLogic.CalcularEstadistica;
import pkBusinessLogic.GeneradorGrafico;
import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import pkDataAccess.pkDTO.Reporte;

public final class PantallaEstadisticas extends PantallaBase {
    private final CalcularEstadistica calculador;
    private final GeneradorGrafico generadorGrafico;
    private final JComboBox<String> franjaHoraria;
    private final JCheckBox incluirGraficos;
    private Path ultimoPdf;
    private final JLabel resultados =
            new JLabel("Presiona consultar estadísticas.");

    public PantallaEstadisticas(CalcularEstadistica calculador) {
        super("Estadísticas");
        this.calculador = Objects.requireNonNull(calculador);
        this.generadorGrafico = new GeneradorGrafico();
        List<String> franjas = this.calculador.consultarFranjasHorarias();
        this.franjaHoraria =
                new JComboBox<>(franjas.toArray(String[]::new));
        this.incluirGraficos = new JCheckBox("Incluir gráficos", true);
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEADING));
        JButton consultar = new JButton("Consultar estadísticas");
        consultar.addActionListener(event -> consultarEstadisticas());
        JButton generarReporte = new JButton("Generar Reporte");
        generarReporte.addActionListener(event -> generarReporte());
        JButton abrirPdf = new JButton("Abrir PDF");
        abrirPdf.addActionListener(event -> abrirUltimoPdf());
        acciones.add(consultar);
        acciones.add(franjaHoraria);
        acciones.add(incluirGraficos);
        acciones.add(generarReporte);
        acciones.add(abrirPdf);
        contenido().add(resultados, BorderLayout.CENTER);
        contenido().add(acciones, BorderLayout.SOUTH);
    }

    private void consultarEstadisticas() {
        try {
            String franja = (String) franjaHoraria.getSelectedItem();
            if (franja == null) {
                resultados.setText("No existen franjas horarias registradas.");
                return;
            }
            double ventas = calculador.calcularTotalVentas(franja);
            int reservas = calculador.calcularTotalReservasHechas(franja);
            resultados.setText(
                    "<html>Total ventas: " + ventas
                            + "<br>Total reservas: " + reservas + "</html>");
            mostrarEstado("Estadísticas consultadas.");
        } catch (RuntimeException error) {
            mostrarEstado(mensajeError(error));
        }
    }

    private void generarReporte() {
        String franja = (String) franjaHoraria.getSelectedItem();
        if (franja == null) {
            mostrarEstado("No existen franjas horarias registradas.");
            return;
        }
        try {
            Reporte reporte = calculador.generarReporte(
                    franja,
                    incluirGraficos.isSelected());
            ultimoPdf = generadorGrafico.generarReportePdf(
                    reporte,
                    incluirGraficos.isSelected());
            double ventas = calculador.calcularTotalVentas(franja);
            int reservas = calculador.calcularTotalReservasHechas(franja);
            resultados.setText(
                    "<html>Reporte generado: " + reporte.getIdReporte()
                            + "<br>Franja: " + reporte.getFranjaHoraria()
                            + "<br>totalVentas: " + ventas
                            + "<br>totalReservas: "
                            + reservas + "</html>");
            mostrarEstado("Reporte guardado y PDF generado: "
                    + ultimoPdf.toAbsolutePath());
        } catch (RuntimeException error) {
            mostrarEstado(mensajeError(error));
        }
    }

    private void abrirUltimoPdf() {
        if (ultimoPdf == null) {
            mostrarEstado("Genera un reporte antes de abrir el PDF.");
            return;
        }
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(ultimoPdf.toFile());
            } else {
                mostrarEstado("PDF generado en: "
                        + ultimoPdf.toAbsolutePath());
            }
        } catch (IOException error) {
            mostrarEstado("PDF generado en: "
                    + ultimoPdf.toAbsolutePath());
        }
    }

    public CalcularEstadistica getCalculador() {
        return calculador;
    }
}
