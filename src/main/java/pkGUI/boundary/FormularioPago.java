package pkGUI.boundary;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.function.Consumer;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public final class FormularioPago extends JPanel {

    private String opcionPago;
    private String datosPago;

    private final JComboBox<String> opcion =
            new JComboBox<>(new String[] {"Efectivo", "Tarjeta"});

    private final JTextField datos =
            new JTextField(20);

    private final JLabel etiquetaDatos =
            new JLabel("Efectivo recibido:");

    private Consumer<String> accionCambioPago =
            ignored -> { };

    public FormularioPago(
            String opcionPago,
            String datosPago) {

        super(new GridBagLayout());

        this.opcionPago = opcionPago;
        this.datosPago = datosPago;

        opcion.setSelectedItem(opcionPago);
        datos.setText(datosPago);

        opcion.addActionListener(event -> {
            String metodo = obtenerOpcionPago();

            actualizarEtiqueta(metodo);
            datos.setText("");

            accionCambioPago.accept(metodo);
        });

        GridBagConstraints constraints =
                new GridBagConstraints();

        constraints.insets =
                new Insets(5, 5, 5, 5);

        constraints.anchor =
                GridBagConstraints.WEST;

        constraints.gridx = 0;
        constraints.gridy = 0;

        add(
                new JLabel("Método de pago:"),
                constraints
        );

        constraints.gridx = 1;
        add(opcion, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;

        add(etiquetaDatos, constraints);

        constraints.gridx = 1;
        add(datos, constraints);

        actualizarEtiqueta(opcionPago);
    }

    private void actualizarEtiqueta(String metodo) {

        if ("Efectivo".equals(metodo)) {
            etiquetaDatos.setText("Efectivo recibido:");
        } else {
            etiquetaDatos.setText("Teléfono PayPhone:");
        }
    }

    public String obtenerOpcionPago() {

        opcionPago =
                (String) opcion.getSelectedItem();

        return opcionPago;
    }

    public String obtenerDatosPago() {

        datosPago =
                datos.getText().trim();

        return datosPago;
    }

    public void actualizar(
            String opcionPago,
            String datosPago) {

        this.opcionPago = opcionPago;
        this.datosPago = datosPago;

        opcion.setSelectedItem(opcionPago);
        datos.setText(datosPago);

        actualizarEtiqueta(opcionPago);
    }

    public void setAccionCambioPago(
            Consumer<String> accionCambioPago) {

        this.accionCambioPago =
                accionCambioPago == null
                        ? ignored -> { }
                        : accionCambioPago;
    }
}