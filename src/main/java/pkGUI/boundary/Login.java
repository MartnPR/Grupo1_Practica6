package pkGUI.boundary;

import pkBusinessLogic.AutenticarUsuario;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.Objects;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public final class Login extends PantallaBase {
    private final FormularioLogin formulario = new FormularioLogin();
    private final AutenticarUsuario autenticacion;
    private final CardLayout formulariosLayout = new CardLayout();
    private final JPanel formularios = new JPanel(formulariosLayout);
    private final JTextField nombreRegistro = new JTextField(18);
    private final JTextField apellidoRegistro = new JTextField(18);
    private final JTextField credencialRegistro = new JTextField(18);
    private final JPasswordField passwordRegistro = new JPasswordField(18);

    public Login(AutenticarUsuario autenticacion) {
        super("Iniciar sesión");
        this.autenticacion = Objects.requireNonNull(autenticacion);
        formularios.add(crearPanelLogin(), "login");
        formularios.add(crearPanelRegistro(), "registro");
        contenido().add(new JLabel(
                "Ingresa con la credencial y password de tu cuenta."),
                BorderLayout.NORTH);
        contenido().add(formularios, BorderLayout.CENTER);
    }

    private JPanel crearPanelLogin() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEADING));
        JButton ingresar = new JButton("Iniciar sesión");
        ingresar.addActionListener(event -> iniciarSesion());
        JButton registrarse = new JButton("Crear cuenta");
        registrarse.addActionListener(
                event -> formulariosLayout.show(formularios, "registro"));
        acciones.add(ingresar);
        acciones.add(registrarse);
        panel.add(formulario, BorderLayout.CENTER);
        panel.add(acciones, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel crearPanelRegistro() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        JPanel campos = new JPanel(new GridLayout(0, 2, 8, 8));
        campos.add(new JLabel("Nombre:"));
        campos.add(nombreRegistro);
        campos.add(new JLabel("Apellido:"));
        campos.add(apellidoRegistro);
        campos.add(new JLabel("Credencial:"));
        campos.add(credencialRegistro);
        campos.add(new JLabel("Password:"));
        campos.add(passwordRegistro);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEADING));
        JButton registrar = new JButton("Validar registro");
        registrar.addActionListener(event -> registrarCuenta());
        JButton volver = new JButton("Volver al inicio de sesión");
        volver.addActionListener(
                event -> formulariosLayout.show(formularios, "login"));
        acciones.add(registrar);
        acciones.add(volver);
        panel.add(new JLabel("Registro de demostración."), BorderLayout.NORTH);
        panel.add(campos, BorderLayout.CENTER);
        panel.add(acciones, BorderLayout.SOUTH);
        return panel;
    }

    private void iniciarSesion() {
        if (!formulario.validarCampos()) {
            mostrarEstado("Ingresa tu credencial y password.");
            return;
        }

        try {
            boolean autenticado = autenticacion.autenticar(
                    "Personal",
                    formulario.obtenerCredencial(),
                    formulario.obtenerPassword());
            if (autenticado) {
                mostrarEstado("Sesión iniciada.");
                navegar("ingreso");
            } else {
                mostrarEstado("Credencial o password incorrectos.");
            }
        } catch (RuntimeException error) {
            mostrarEstado(mensajeError(error));
        } finally {
            formulario.limpiar();
        }
    }

    private void registrarCuenta() {
        try {
            if (nombreRegistro.getText().isBlank()
                    || apellidoRegistro.getText().isBlank()
                    || credencialRegistro.getText().isBlank()
                    || passwordRegistro.getPassword().length == 0) {
                mostrarEstado("Completa todos los campos del registro.");
                return;
            }
            if (nombreRegistro.getText().length() > 100
                    || apellidoRegistro.getText().length() > 100
                    || credencialRegistro.getText().trim().length() > 16
                    || passwordRegistro.getPassword().length > 100) {
                mostrarEstado("Revisa la longitud de los campos.");
                return;
            }
            mostrarEstado(
                    "No hay un servicio persistente de registro; no se creó la cuenta.");
        } finally {
            passwordRegistro.setText("");
        }
    }

    public AutenticarUsuario getAutenticacion() {
        return autenticacion;
    }

    public void mostrar() {
        setVisible(true);
    }

    public void cerrar() {
        setVisible(false);
    }
}
