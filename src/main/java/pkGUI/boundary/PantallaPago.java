package pkGUI.boundary;

import pkBusinessLogic.SeleccionarAlimento;
import pkBusinessLogic.PagarTarjeta;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.Objects;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public final class PantallaPago extends PantallaBase {
    private final FormularioPago formulario;
    private final SeleccionarAlimento controlAlimentos;
    private final PagarTarjeta pagoTarjeta;

    public PantallaPago(
            SeleccionarAlimento controlAlimentos,
            PagarTarjeta pagoTarjeta) {
        super("Pago");
        this.formulario = new FormularioPago("Efectivo", "");
        this.controlAlimentos = Objects.requireNonNull(controlAlimentos);
        this.pagoTarjeta = Objects.requireNonNull(pagoTarjeta);
        JButton procesar = new JButton("Procesar compra");
        procesar.addActionListener(event -> procesarCompra());
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEADING));
        acciones.add(procesar);
        contenido().add(new JLabel(
                "La pasarela PayPhone aún no está integrada; no se realizará ningún cargo."),
                BorderLayout.NORTH);
        contenido().add(formulario, BorderLayout.CENTER);
        contenido().add(acciones, BorderLayout.SOUTH);
    }

    public void procesarCompra() {
        if (!controlAlimentos.hayAlimentosSeleccionados()) {
            mostrarEstado("Selecciona al menos un alimento antes de continuar.");
            return;
        }

        String metodo = formulario.obtenerOpcionPago();
        if (!"Tarjeta".equals(metodo)) {
            mostrarEstado("El método %s aún no está conectado al proceso de compra."
                    .formatted(metodo));
            return;
        }

        try {
            pagoTarjeta.establecerMonto(controlAlimentos.mostrarTotal());
            boolean aprobado = pagoTarjeta.solicitarPago(
                    formulario.obtenerDatosPago());
            mostrarEstado(aprobado
                    ? "Pago aprobado."
                    : "El pago no fue aprobado. No se completó la compra.");
        } catch (RuntimeException error) {
            mostrarEstado("%s No se completó la compra."
                    .formatted(mensajeError(error)));
        }
    }

    public void mostrarDatosPago() {
        mostrarEstado("Método seleccionado: %s. No se realizó ningún cobro."
                .formatted(formulario.obtenerOpcionPago()));
    }

    public SeleccionarAlimento getControlAlimentos() {
        return controlAlimentos;
    }

    public PagarTarjeta getPagoTarjeta() {
        return pagoTarjeta;
    }
}
