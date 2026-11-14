package pkGUI.boundary;

import com.google.zxing.common.BitMatrix;
import pkBusinessLogic.GestorTicket;
import pkDataAccess.pkDTO.Factura;
import pkDataAccess.pkDTO.Ticket;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.Objects;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public final class PantallaTicket extends PantallaBase {
    private final GestorTicket controlTicket;
    private final JLabel detalle = new JLabel(
            "Aún no se ha generado un ticket.", SwingConstants.CENTER);
    private Ticket ticketActual;

    public PantallaTicket(GestorTicket controlTicket) {
        super("Ticket");
        this.controlTicket = Objects.requireNonNull(controlTicket);
        JButton generar = new JButton("Generar ticket");
        generar.addActionListener(event -> ejecutarSeguro(this::generarTicket));
        JButton validar = new JButton("Validar ticket");
        validar.addActionListener(event -> validarTicket());
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEADING));
        acciones.add(generar);
        acciones.add(validar);
        contenido().add(new JLabel(
                "Los tickets se guardan en el almacenamiento configurado."),
                BorderLayout.NORTH);
        contenido().add(detalle, BorderLayout.CENTER);
        contenido().add(acciones, BorderLayout.SOUTH);
    }

    private void generarTicket() {
        ticketActual = controlTicket.generarTicket(new Factura());
        detalle.setText("Ticket #%d — %s".formatted(
                ticketActual.getIdTicket(),
                ticketActual.getFechaGeneracion()));
        detalle.setIcon(ticketActual.getCodigoQR() == null ? null : new QrIcon(ticketActual.getCodigoQR()));
        detalle.setVerticalTextPosition(SwingConstants.BOTTOM);
        detalle.setHorizontalTextPosition(SwingConstants.CENTER);
        detalle.setToolTipText("Código QR del ticket");
        mostrarEstado("Ticket generado y guardado.");
    }

    private void validarTicket() {
        if (ticketActual == null) {
            mostrarEstado("Genera un ticket antes de validarlo.");
            return;
        }
        ejecutarSeguro(() -> mostrarEstado(controlTicket.validarTicket(ticketActual)
                ? "Ticket válido." : "Ticket no válido."));
    }

    public GestorTicket getControlTicket() {
        return controlTicket;
    }

    public void mostrarTicket() {
        setVisible(true);
    }

    private static final class QrIcon implements Icon {
        private final BitMatrix matrix;

        private QrIcon(BitMatrix matrix) {
            this.matrix = matrix;
        }

        @Override
        public int getIconWidth() {
            return matrix.getWidth();
        }

        @Override
        public int getIconHeight() {
            return matrix.getHeight();
        }

        @Override
        public void paintIcon(Component component, Graphics graphics, int x, int y) {
            graphics.setColor(Color.WHITE);
            graphics.fillRect(x, y, getIconWidth(), getIconHeight());
            graphics.setColor(Color.BLACK);
            for (int row = 0; row < matrix.getHeight(); row++) {
                for (int column = 0; column < matrix.getWidth(); column++) {
                    if (matrix.get(column, row)) {
                        graphics.fillRect(x + column, y + row, 1, 1);
                    }
                }
            }
        }
    }
}
