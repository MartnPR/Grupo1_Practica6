package pkBusinessLogic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.UUID;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import pkDataAccess.Factura;

public class GestorFactura {

    private static final Path CARPETA_FACTURAS =
            Path.of("facturas");

    public GestorFactura() {
    }

    public Factura generarFactura(
            int numeroPedido,
            String metodoPago,
            double total,
            double descuento) {

        if (numeroPedido <= 0) {
            throw new IllegalArgumentException(
                    "El número de pedido no es válido."
            );
        }

        if (metodoPago == null
                || metodoPago.isBlank()) {

            throw new IllegalArgumentException(
                    "El método de pago es obligatorio."
            );
        }

        if (total < 0) {
            throw new IllegalArgumentException(
                    "El total no puede ser negativo."
            );
        }

        if (descuento < 0 || descuento > total) {
            throw new IllegalArgumentException(
                    "El descuento no es válido."
            );
        }

        Factura factura = new Factura();

        factura.setIdFactura(
                "FAC-"
                + UUID.randomUUID()
                      .toString()
                      .substring(0, 8)
                      .toUpperCase()
        );

        factura.setFecha(new Date());
        factura.setNumeroPedido(numeroPedido);
        factura.setMetodoPago(metodoPago);
        factura.setTotal(total);
        factura.setDescuento(descuento);

        return factura;
    }

    public String imprimirFacturaVirtual(
            Factura factura) {

        validarFactura(factura);

        return "========== FOOD4ALL ==========\n"
                + "Factura: "
                + factura.getIdFactura()
                + "\nFecha: "
                + factura.getFecha()
                + "\nPedido: "
                + factura.getNumeroPedido()
                + "\nMétodo de pago: "
                + factura.getMetodoPago()
                + "\nSubtotal: $"
                + factura.getTotal()
                + "\nDescuento: $"
                + factura.getDescuento()
                + "\nTOTAL: $"
                + calcularTotalFinal(factura)
                + "\n==============================";
    }

    public Path convertirAPDF(Factura factura) {

        validarFactura(factura);

        try {

            Files.createDirectories(
                    CARPETA_FACTURAS
            );

            Path ruta =
                    CARPETA_FACTURAS.resolve(
                            "Factura_"
                            + factura.getIdFactura()
                            + ".pdf"
                    );

            try (PDDocument documento =
                         new PDDocument()) {

                PDPage pagina =
                        new PDPage();

                documento.addPage(pagina);

                PDType1Font titulo =
                        new PDType1Font(
                                Standard14Fonts.FontName
                                        .HELVETICA_BOLD
                        );

                PDType1Font texto =
                        new PDType1Font(
                                Standard14Fonts.FontName
                                        .HELVETICA
                        );

                try (PDPageContentStream contenido =
                             new PDPageContentStream(
                                     documento,
                                     pagina
                             )) {

                    contenido.beginText();

                    contenido.setFont(
                            titulo,
                            20
                    );

                    contenido.newLineAtOffset(
                            220,
                            750
                    );

                    contenido.showText(
                            "FOOD4ALL"
                    );

                    contenido.endText();

                    contenido.beginText();

                    contenido.setFont(
                            texto,
                            12
                    );

                    contenido.setLeading(20);

                    contenido.newLineAtOffset(
                            70,
                            700
                    );

                    escribirLinea(
                            contenido,
                            "Factura: "
                            + factura.getIdFactura()
                    );

                    escribirLinea(
                            contenido,
                            "Fecha: "
                            + factura.getFecha()
                    );

                    escribirLinea(
                            contenido,
                            "Pedido: "
                            + factura.getNumeroPedido()
                    );

                    escribirLinea(
                            contenido,
                            "Metodo de pago: "
                            + factura.getMetodoPago()
                    );

                    escribirLinea(
                            contenido,
                            "Subtotal: $"
                            + factura.getTotal()
                    );

                    escribirLinea(
                            contenido,
                            "Descuento: $"
                            + factura.getDescuento()
                    );

                    escribirLinea(
                            contenido,
                            "TOTAL: $"
                            + calcularTotalFinal(factura)
                    );

                    contenido.endText();
                }

                documento.save(
                        ruta.toFile()
                );
            }

            return ruta;

        } catch (IOException e) {

            throw new IllegalStateException(
                    "No se pudo generar el PDF.",
                    e
            );
        }
    }

    private double calcularTotalFinal(
            Factura factura) {

        return factura.getTotal()
                - factura.getDescuento();
    }

    private void validarFactura(
            Factura factura) {

        if (factura == null) {

            throw new IllegalArgumentException(
                    "La factura no puede ser nula."
            );
        }
    }

    private void escribirLinea(
            PDPageContentStream contenido,
            String texto)
            throws IOException {

        contenido.showText(texto);
        contenido.newLine();
    }
}