package pkGUI.boundary;

import pkBusinessLogic.AutenticarUsuario;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.Objects;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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
    private String tipoSeleccionado;
    private final JLabel encabezadoAcceso =
            new JLabel("Selecciona el tipo de cuenta.");

    public Login(AutenticarUsuario autenticacion) {
        super("Iniciar sesión");
        this.autenticacion = Objects.requireNonNull(autenticacion);
        formularios.add(crearPanelSeleccion(), "seleccion");
        formularios.add(crearPanelLogin(), "login");
        formularios.add(crearPanelRegistro(), "registro");
        formulariosLayout.show(formularios, "seleccion");
        contenido().add(encabezadoAcceso, BorderLayout.NORTH);
        contenido().add(formularios, BorderLayout.CENTER);
    }

    private JPanel crearPanelSeleccion() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 20));
        JButton administrador =
                new JButton("Administrador");
        administrador.addActionListener(
                event -> seleccionarTipo("Administrador"));
        JButton cajero = new JButton("Cajero");
        cajero.addActionListener(event -> seleccionarTipo("Cajero"));
        JButton usuario =
                new JButton("Profesor o estudiante");
        usuario.addActionListener(
                event -> seleccionarTipo("Profesor o estudiante"));
        panel.add(administrador);
        panel.add(cajero);
        panel.add(usuario);
        return panel;
    }

    private void seleccionarTipo(String tipo) {
        tipoSeleccionado = tipo;
        formulario.limpiar();
        mostrarEstado("");
        if ("Profesor o estudiante".equals(tipo)) {
            encabezadoAcceso.setText(
                    "Acceso para profesor o estudiante - usuario Policomedor.");
        } else {
            encabezadoAcceso.setText(
                    "Acceso como " + tipo + ".");
        }
        formulariosLayout.show(formularios, "login");
    }

    private void volverSeleccion() {
        tipoSeleccionado = null;
        encabezadoAcceso.setText("Selecciona el tipo de cuenta.");
        formulariosLayout.show(formularios, "seleccion");
    }

    private JPanel crearPanelLogin() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEADING));
        JButton ingresar = new JButton("Iniciar sesión");
        ingresar.addActionListener(event -> iniciarSesion());
        JButton registrarse = new JButton("Crear cuenta");
        registrarse.addActionListener(
                event -> formulariosLayout.show(formularios, "registro"));
        JButton volver = new JButton("Cambiar tipo");
        volver.addActionListener(
                event -> volverSeleccion());
        acciones.add(ingresar);
        acciones.add(registrarse);
        acciones.add(volver);
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
        JButton registrar = new JButton("Crear cuenta");
        registrar.addActionListener(event -> registrarCuenta());
        JButton volver = new JButton("Volver al inicio de sesión");
        volver.addActionListener(
                event -> formulariosLayout.show(formularios, "login"));
        acciones.add(registrar);
        acciones.add(volver);
        panel.add(new JLabel("Crea una cuenta para iniciar sesión."), BorderLayout.NORTH);
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
            String tipoAutenticacion =
                    "Profesor o estudiante".equals(tipoSeleccionado)
                            ? "Usuario"
                            : tipoSeleccionado;
            boolean autenticado = autenticacion.autenticar(
                    tipoAutenticacion,
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
            String nombre = nombreRegistro.getText().trim();
            String apellido = apellidoRegistro.getText().trim();
            String credencial = credencialRegistro.getText().trim();
            String password = new String(passwordRegistro.getPassword());
            if (nombre.isBlank()
                    || apellido.isBlank()
                    || credencial.isBlank()
                    || password.isBlank()) {
                mostrarEstado("Completa todos los campos del registro.");
                return;
            }
            if (nombre.length() > 100
                    || apellido.length() > 100
                    || credencial.length() > 16
                    || password.length() > 100) {
                mostrarEstado("Revisa la longitud de los campos.");
                return;
            }
            String tipoRegistro =
                    "Profesor o estudiante".equals(tipoSeleccionado)
                            ? "Usuario"
                            : tipoSeleccionado;
            if (autenticacion.registrarCuenta(
                    credencial, nombre, apellido, password, tipoRegistro)) {
                mostrarEstado("Cuenta creada. Ya puedes iniciar sesión.");
                nombreRegistro.setText("");
                apellidoRegistro.setText("");
                credencialRegistro.setText("");
                formulariosLayout.show(formularios, "login");
            } else {
                if ("Administrador".equals(tipoSeleccionado)) {
                    JOptionPane.showMessageDialog(
                            this,
                            "No se peude crear un nuevo administrador, pues ya existe uno",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    formulariosLayout.show(formularios, "login");
                    return;
                }
                mostrarEstado(
                        "No se pudo crear la cuenta. Revisa los datos o usa otra credencial.");
            }
        } catch (RuntimeException error) {
            mostrarEstado(mensajeError(error));
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
