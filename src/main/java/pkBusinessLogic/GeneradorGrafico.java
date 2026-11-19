package pkBusinessLogic;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import pkDataAccess.pkDAO.FacturaDAO;
import pkDataAccess.pkDTO.Factura;
import pkDataAccess.pkDTO.Reporte;
import java.awt.Color;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

public class GeneradorGrafico {

    private final FacturaDAO facturaDAO;
    private static final Path CARPETA_REPORTES =
            Path.of("reportes");

    public GeneradorGrafico() {

        this.facturaDAO =
                new FacturaDAO();
    }

    public Map<String, Integer>
            generarDiagramaBarraPedidos() {

        List<Factura> facturas =
                facturaDAO.leerTodos();

        Map<String, Integer> pedidosPorMetodo =
                new LinkedHashMap<>();

        if (facturas == null) {
            return pedidosPorMetodo;
        }

        for (Factura factura : facturas) {

            String metodo =
                    factura.getMetodoPago();

            if (metodo == null || metodo.isBlank()) {
                metodo = "NO ESPECIFICADO";
            }

            pedidosPorMetodo.put(
                    metodo,
                    pedidosPorMetodo.getOrDefault(
                            metodo,
                            0
                    ) + 1
            );
        }

        return pedidosPorMetodo;
    }

    public Map<String, Double>
            generarDiagramaPastel() {

        List<Factura> facturas =
                facturaDAO.leerTodos();

        Map<String, Double> ventasPorMetodo =
                new LinkedHashMap<>();

        if (facturas == null) {
            return ventasPorMetodo;
        }

        for (Factura factura : facturas) {

            String metodo =
                    factura.getMetodoPago();

            if (metodo == null || metodo.isBlank()) {
                metodo = "NO ESPECIFICADO";
            }

            double valor =
                    factura.getTotal()
                    - factura.getDescuento();

            ventasPorMetodo.put(
                    metodo,
                    ventasPorMetodo.getOrDefault(
                            metodo,
                            0.0
                    ) + valor
            );
        }

        return ventasPorMetodo;
    }

    public Reporte generarReporte() {

        List<Factura> facturas =
                facturaDAO.leerTodos();

        int cantidadPedidos = 0;
        double totalVentas = 0;

        if (facturas != null) {

            cantidadPedidos =
                    facturas.size();

            for (Factura factura : facturas) {

                totalVentas +=
                        factura.getTotal()
                        - factura.getDescuento();
            }
        }

        Reporte reporte = new Reporte();
        reporte.setFechaGeneracion(new java.util.Date());
        reporte.setDescripcion(
                "totalVentas=" + totalVentas
                        + "; totalReservas=0");
        reporte.setTotalVentas(totalVentas);
        reporte.setTotalReservas(0);

        return reporte;
    }

    public Path generarReportePdf(
            Reporte reporte,
            boolean incluirGraficos) {
        if (reporte == null) {
            throw new IllegalArgumentException(
                    "No se puede generar un PDF sin datos del reporte.");
        }

        try {
            Files.createDirectories(CARPETA_REPORTES);
            String numero = reporte.getIdReporte()
                    .replace("Reporte ", "")
                    .trim();
            Path archivo = CARPETA_REPORTES.resolve(
                    "Reporte_" + numero + ".pdf");

            PDType1Font fontRegular =
                    new PDType1Font(Standard14Fonts.FontName.TIMES_ROMAN);
            PDType1Font fontBold =
                    new PDType1Font(Standard14Fonts.FontName.TIMES_BOLD);

            try (PDDocument document = new PDDocument()) {
                PDPage page = new PDPage(PDRectangle.A4);
                document.addPage(page);

                try (PDPageContentStream content =
                        new PDPageContentStream(document, page)) {
                    float ancho = page.getMediaBox().getWidth();

                    content.beginText();
                    content.setFont(fontBold, 20);
                    content.newLineAtOffset(50, 770);
                    content.showText(reporte.getIdReporte());
                    content.endText();

                    content.beginText();
                    content.setFont(fontRegular, 12);
                    content.newLineAtOffset(50, 745);
                    content.showText("Franja horaria: "
                            + reporte.getFranjaHoraria());
                    content.newLineAtOffset(0, -18);
                    content.showText("Identificador unico: "
                            + reporte.getIdReporte());
                    content.endText();

                    dibujarTabla(
                            content,
                            50,
                            690,
                            ancho - 100,
                            reporte,
                            leerTotalVentas(reporte),
                            leerTotalReservas(reporte),
                            fontRegular,
                            fontBold);

                    if (incluirGraficos) {
                        dibujarBarras(
                                content,
                                50,
                                430,
                                ancho - 100,
                                reporte,
                                leerTotalVentas(reporte),
                                leerTotalReservas(reporte),
                                fontRegular,
                                fontBold);
                    }
                }
                document.save(archivo.toFile());
            }
            return archivo;
        } catch (java.io.IOException exception) {
            throw new IllegalStateException(
                    "No se pudo generar el reporte PDF.", exception);
        }
    }

    private void dibujarTabla(
            PDPageContentStream content,
            float x,
            float y,
            float ancho,
            Reporte reporte,
            double totalVentas,
            int totalReservas,
            PDType1Font fontRegular,
            PDType1Font fontBold) throws java.io.IOException {
        float filaAltura = 30;
        float columna = ancho / 2;

        content.setStrokingColor(Color.BLACK);
        content.setLineWidth(1);
        content.addRect(x, y - filaAltura * 2, ancho, filaAltura * 2);
        content.moveTo(x + columna, y);
        content.lineTo(x + columna, y - filaAltura * 2);
        content.moveTo(x, y - filaAltura);
        content.lineTo(x + ancho, y - filaAltura);
        content.stroke();

        content.beginText();
        content.setFont(fontBold, 11);
        content.newLineAtOffset(x + 8, y - 20);
        content.showText("Indicador");
        content.newLineAtOffset(columna - 8, 0);
        content.showText("Valor");
        content.endText();

        content.beginText();
        content.setFont(fontRegular, 11);
        content.newLineAtOffset(x + 8, y - 50);
        content.showText("totalVentas");
        content.newLineAtOffset(columna - 8, 0);
        content.showText(formatear(totalVentas));
        content.newLineAtOffset(8 - columna, -30);
        content.showText("totalReservas");
        content.newLineAtOffset(columna - 8, 0);
        content.showText(String.valueOf(totalReservas));
        content.endText();
    }

    private void dibujarBarras(
            PDPageContentStream content,
            float x,
            float y,
            float ancho,
            Reporte reporte,
            double totalVentas,
            int totalReservas,
            PDType1Font fontRegular,
            PDType1Font fontBold) throws java.io.IOException {
        content.beginText();
        content.setFont(fontBold, 12);
        content.newLineAtOffset(x, y + 20);
        content.showText("Diagrama de barras");
        content.endText();

        double ventas = Math.max(totalVentas, 0);
        int reservas = Math.max(totalReservas, 0);
        double maximo = Math.max(ventas, reservas);
        if (maximo == 0) {
            maximo = 1;
        }

        float base = y - 20;
        float alturaMaxima = 180;
        float barraVentas =
                (float) (ventas / maximo * alturaMaxima);
        float barraReservas =
                (float) (reservas / maximo * alturaMaxima);

        content.setStrokingColor(Color.DARK_GRAY);
        content.setNonStrokingColor(Color.LIGHT_GRAY);
        content.addRect(x + 70, base, 90, barraVentas);
        content.fill();
        content.setNonStrokingColor(Color.GRAY);
        content.addRect(x + 210, base, 90, barraReservas);
        content.fill();
        content.setStrokingColor(Color.BLACK);
        content.moveTo(x + 40, base);
        content.lineTo(x + ancho - 40, base);
        content.stroke();

        content.beginText();
        content.setFont(fontRegular, 10);
        content.newLineAtOffset(x + 70, base - 16);
        content.showText("totalVentas");
        content.newLineAtOffset(140, 0);
        content.showText("totalReservas");
        content.endText();
    }

    private String formatear(double valor) {
        return String.format(Locale.US, "%.2f", valor);
    }

    private double leerTotalVentas(Reporte reporte) {
        if (reporte.getTotalVentas() != 0
                || reporte.getDescripcion() == null) {
            return reporte.getTotalVentas();
        }
        return leerDouble(reporte.getDescripcion(), "totalVentas");
    }

    private int leerTotalReservas(Reporte reporte) {
        if (reporte.getTotalReservas() != 0
                || reporte.getDescripcion() == null) {
            return reporte.getTotalReservas();
        }
        return (int) leerDouble(reporte.getDescripcion(), "totalReservas");
    }

    private double leerDouble(String descripcion, String nombre) {
        if (descripcion == null) {
            return 0;
        }
        for (String parte : descripcion.split(";")) {
            String[] datos = parte.split("=", 2);
            if (datos.length == 2 && datos[0].equals(nombre)) {
                try {
                    return Double.parseDouble(datos[1]);
                } catch (NumberFormatException exception) {
                    return 0;
                }
            }
        }
        return 0;
    }
}