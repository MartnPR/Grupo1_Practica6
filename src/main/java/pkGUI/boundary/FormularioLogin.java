package pkGUI.boundary;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public final class FormularioLogin extends JPanel {
    private final JTextField credencial = new JTextField(18);
    private final JPasswordField password = new JPasswordField(18);

    public FormularioLogin() {
        super(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.anchor = GridBagConstraints.WEST;
        addField(constraints, 0, "Credencial:", credencial);
        addField(constraints, 1, "Password:", password);
    }

    private void addField(GridBagConstraints constraints, int row, String label, java.awt.Component field) {
        constraints.gridx = 0;
        constraints.gridy = row;
        add(new JLabel(label), constraints);
        constraints.gridx = 1;
        add(field, constraints);
    }

    public boolean validarCampos() {
        return !obtenerCredencial().isBlank()
                && password.getPassword().length > 0;
    }

    public String obtenerCredencial() {
        return credencial.getText().trim();
    }

    public String obtenerPassword() {
        return new String(password.getPassword());
    }

    public void limpiar() {
        credencial.setText("");
        password.setText("");
    }

    public void mostrar() {
        setVisible(true);
    }

    public void cerrar() {
        setVisible(false);
    }
}
