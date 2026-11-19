package pkGUI.boundary;

import pkBusinessLogic.SeleccionarAlimento;
import pkBusinessLogic.PagarTarjeta;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.SwingWorker;
import java.util.Objects;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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
        formulario.setAccionCambioPago(this::iniciarPagoDesdeSeleccion);
        JButton procesar = new JButton("Procesar compra");
        procesar.addActionListener(event -> procesarCompra());
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEADING));
        acciones.add(procesar);
        contenido().add(new JLabel(
                "Selecciona Tarjeta para abrir PayPhone."),
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
            if ("Tarjeta".equals(metodo)) {
                iniciarPagoTarjeta();
                return;
            }
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

    private void iniciarPagoDesdeSeleccion(String metodo) {
        if ("Tarjeta".equals(metodo)) {
            iniciarPagoTarjeta();
        }
    }

    private void iniciarPagoTarjeta() {
        if (!controlAlimentos.hayAlimentosSeleccionados()) {
            mostrarEstado(
                    "Selecciona al menos un alimento antes de pagar.");
            return;
        }

        if (!pagoTarjeta.isConfigurado()) {
            JPanel credenciales = new JPanel();
            credenciales.setLayout(new java.awt.GridLayout(0, 2, 8, 8));
            javax.swing.JTextField token =
                    new javax.swing.JTextField(30);
            javax.swing.JTextField storeId =
                    new javax.swing.JTextField(20);
            credenciales.add(new JLabel("Token PayPhone:"));
            credenciales.add(token);
            credenciales.add(new JLabel("StoreID:"));
            credenciales.add(storeId);

            int opcion = JOptionPane.showConfirmDialog(
                    this,
                    credenciales,
                    "Configurar PayPhone",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);
            if (opcion != JOptionPane.OK_OPTION
                    || token.getText().trim().isBlank()
                    || storeId.getText().trim().isBlank()) {
                mostrarEstado(
                        "Ingresa el token y StoreID para abrir PayPhone.");
                return;
            }
            try {
                pagoTarjeta.configurar(
                        token.getText(),
                        storeId.getText());
            } catch (RuntimeException error) {
                mostrarEstado(mensajeError(error));
                return;
            }
        }

        String telefono = formulario.obtenerDatosPago();

        SwingWorker<Boolean, Void> worker =
                new SwingWorker<>() {
                    @Override
                    protected Boolean doInBackground() {
                        pagoTarjeta.establecerMonto(
                                controlAlimentos.mostrarTotal());
                        return pagoTarjeta.solicitarPago(telefono);
                    }

                    @Override
                    protected void done() {
                        try {
                            mostrarEstado(get()
                                    ? "Pago aprobado."
                                    : "El pago no fue aprobado.");
                        } catch (java.util.concurrent.ExecutionException
                                | InterruptedException error) {
                            if (error instanceof InterruptedException) {
                                Thread.currentThread().interrupt();
                            }
                            Throwable causa =
                                    error instanceof java.util.concurrent.ExecutionException
                                            ? error.getCause()
                                            : error;
                            mostrarEstado(
                                    causa instanceof RuntimeException
                                            ? mensajeError(
                                                    (RuntimeException) causa)
                                            : "No se pudo completar el pago.");
                        }
                    }
                };
        mostrarEstado("Abriendo PayPhone...");
        worker.execute();
    }

    public SeleccionarAlimento getControlAlimentos() {
        return controlAlimentos;
    }

    public PagarTarjeta getPagoTarjeta() {
        return pagoTarjeta;
    }
}
