package pkGUI.boundary;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.Objects;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import pkBusinessLogic.PagarEfectivo;
import pkBusinessLogic.PagarTarjeta;
import pkBusinessLogic.SeleccionarAlimento;

import java.util.concurrent.atomic.AtomicInteger;

import pkBusinessLogic.GestorFactura;
import pkBusinessLogic.GestorTicket;
import pkDataAccess.pkDTO.Factura;

public final class PantallaPago extends PantallaBase {

    private final FormularioPago formulario;

    private final SeleccionarAlimento controlAlimentos;

    private final PagarTarjeta pagoTarjeta;

    private final PagarEfectivo pagoEfectivo;
    
    private final GestorFactura gestorFactura;

    private final GestorTicket gestorTicket;

    private static final AtomicInteger SECUENCIA_PEDIDO =
            new AtomicInteger(1);

    public PantallaPago(
            SeleccionarAlimento controlAlimentos,
            PagarTarjeta pagoTarjeta,
            PagarEfectivo pagoEfectivo,
            GestorFactura gestorFactura,
            GestorTicket gestorTicket) {

        super("Pago");

        this.formulario =
                new FormularioPago("Efectivo", "");

        this.controlAlimentos =
                Objects.requireNonNull(controlAlimentos);

        this.pagoTarjeta =
                Objects.requireNonNull(pagoTarjeta);

        this.pagoEfectivo =
                Objects.requireNonNull(pagoEfectivo);
        
        this.gestorFactura =
                Objects.requireNonNull(gestorFactura);

        this.gestorTicket =
                Objects.requireNonNull(gestorTicket);

        formulario.setAccionCambioPago(
                this::cambiarMetodoPago
        );

        JButton procesar =
                new JButton("Procesar compra");

        procesar.addActionListener(
                event -> procesarCompra()
        );

        JPanel acciones =
                new JPanel(
                        new FlowLayout(FlowLayout.LEADING)
                );

        acciones.add(procesar);

        contenido().add(
                new JLabel(
                        "Selecciona el método de pago."
                ),
                BorderLayout.NORTH
        );

        contenido().add(
                formulario,
                BorderLayout.CENTER
        );

        contenido().add(
                acciones,
                BorderLayout.SOUTH
        );
    }

    public void procesarCompra() {

        if (!controlAlimentos.hayAlimentosSeleccionados()) {

            mostrarEstado(
                    "Selecciona al menos un alimento "
                    + "antes de continuar."
            );

            return;
        }

        String metodo =
                formulario.obtenerOpcionPago();

        if ("Efectivo".equals(metodo)) {

            procesarPagoEfectivo();
            return;
        }

        if ("Tarjeta".equals(metodo)) {

            iniciarPagoTarjeta();
            return;
        }

        mostrarEstado(
                "Método de pago no reconocido."
        );
    }

    private void procesarPagoEfectivo() {

        String textoEfectivo =
                formulario.obtenerDatosPago();

        if (textoEfectivo == null
                || textoEfectivo.isBlank()) {

            mostrarEstado(
                    "Ingresa el efectivo recibido."
            );

            return;
        }

        try {

            String valorNormalizado =
                    textoEfectivo
                            .trim()
                            .replace(",", ".");

            double efectivoRecibido =
                    Double.parseDouble(valorNormalizado);

            double total =
                    controlAlimentos.mostrarTotal();

            pagoEfectivo.establecerDatosPago(
                    total,
                    efectivoRecibido
            );

            if (!pagoEfectivo.pagarConEfectivo()) {

                mostrarEstado(
                        "Efectivo insuficiente. "
                        + "Total a pagar: $%.2f"
                                .formatted(total)
                );

                return;
            }

            String recibo =
                    pagoEfectivo.generarRecibo();

            try {

                finalizarCompra(
                        "EFECTIVO"
                );

                mostrarEstado(
                        recibo
                        + " | Factura y ticket generados correctamente."
                );

            } catch (RuntimeException error) {

                mostrarEstado(
                        recibo
                        + " | Advertencia: el pago fue aprobado, "
                        + "pero no se pudieron generar todos "
                        + "los comprobantes."
                );

                System.err.println(
                        mensajeError(error)
                );
            }


        } catch (NumberFormatException error) {

            mostrarEstado(
                    "Ingresa un valor numérico válido "
                    + "para el efectivo recibido."
            );

        } catch (RuntimeException error) {

            mostrarEstado(
                    "%s No se completó el pago."
                            .formatted(
                                    mensajeError(error)
                            )
            );
        }
    }

    
    private void finalizarCompra(String metodoPago) {

        double total =
                controlAlimentos.mostrarTotal();

        double descuento =
                controlAlimentos.mostrarDescuentos();

        int numeroPedido =
                SECUENCIA_PEDIDO.getAndIncrement();

        Factura factura =
                gestorFactura.generarFactura(
                        numeroPedido,
                        metodoPago,
                        total,
                        descuento
                );

        gestorFactura.convertirAPDF(factura);

        gestorTicket.prepararFactura(factura);

        gestorTicket.generarTicketParaVista();

        controlAlimentos.vaciarCarrito();
    }
    
    
    private void cambiarMetodoPago(String metodo) {

        if ("Tarjeta".equals(metodo)) {

            mostrarEstado(
                    "Ingresa el número asociado "
                    + "a PayPhone."
            );

        } else {

            mostrarEstado(
                    "Ingresa el efectivo recibido."
            );
        }
    }

    private void iniciarPagoTarjeta() {

        if (!controlAlimentos.hayAlimentosSeleccionados()) {

            mostrarEstado(
                    "Selecciona al menos un alimento "
                    + "antes de pagar."
            );

            return;
        }

        if (!pagoTarjeta.isConfigurado()) {

            mostrarEstado(
                    "PayPhone aún no está configurado "
                    + "para Food4All."
            );

            return;
        }

        String telefono =
                formulario.obtenerDatosPago();

        if (telefono == null
                || telefono.isBlank()) {

            mostrarEstado(
                    "Ingresa el número asociado "
                    + "a PayPhone."
            );

            return;
        }

        SwingWorker<Boolean, Void> worker =
                new SwingWorker<>() {

            @Override
            protected Boolean doInBackground() {

                pagoTarjeta.establecerMonto(
                        controlAlimentos.mostrarTotal()
                );

                return pagoTarjeta.solicitarPago(
                        telefono
                );
            }

            @Override
            protected void done() {

                try {

                    boolean aprobado = get();

                    mostrarEstado(
                            aprobado
                                    ? "Pago aprobado."
                                    : "El pago no fue aprobado."
                    );

                } catch (
                        java.util.concurrent.ExecutionException error) {

                    Throwable causa =
                            error.getCause();

                    mostrarEstado(
                            causa instanceof RuntimeException
                                    ? mensajeError(
                                            (RuntimeException) causa
                                    )
                                    : "No se pudo completar el pago."
                    );

                } catch (InterruptedException error) {

                    Thread.currentThread().interrupt();

                    mostrarEstado(
                            "La operación de pago fue interrumpida."
                    );
                }
            }
        };

        mostrarEstado(
                "Solicitando pago a PayPhone..."
        );

        worker.execute();
    }

    public void mostrarDatosPago() {

        mostrarEstado(
                "Método seleccionado: %s."
                        .formatted(
                                formulario.obtenerOpcionPago()
                        )
        );
    }

    public SeleccionarAlimento getControlAlimentos() {
        return controlAlimentos;
    }

    public PagarTarjeta getPagoTarjeta() {
        return pagoTarjeta;
    }
}