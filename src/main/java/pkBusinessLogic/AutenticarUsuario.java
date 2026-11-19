package pkBusinessLogic;

import pkDataAccess.pkDAO.UsuarioPoliComedorDAO;
import pkDataAccess.pkDTO.UsuarioPoliComedor;

public class AutenticarUsuario {

    private final UsuarioPoliComedorDAO usuarioDAO;
    private static final Object ADMIN_LOCK = new Object();

    public AutenticarUsuario() {
        this.usuarioDAO = new UsuarioPoliComedorDAO();
    }

    public boolean autenticar(
            String metodo,
            String usuario,
            String pasw) {
        if (metodo == null
                || usuario == null
                || usuario.isBlank()) {
            return false;
        }

        String metodoNormalizado = metodo.trim().toLowerCase();
        switch (metodoNormalizado) {
            case "estudiante":
                return autenticarPorCodigoEstudiante(usuario, pasw);
            case "profesor":
                return autenticarProfesor(usuario, pasw);
            default:
                String tipoSeleccionado = normalizarTipo(metodo);
                if ("usuario".equals(tipoSeleccionado)
                        || "administrador".equals(tipoSeleccionado)
                        || "cajero".equals(tipoSeleccionado)) {
                    return autenticarPorCredencial(
                            usuario, pasw, tipoSeleccionado);
                }
                return false;
        }
    }

    public boolean registrarCuenta(
            String credencial,
            String nombre,
            String apellido,
            String pasw) {
        synchronized (ADMIN_LOCK) {
            return usuarioDAO.crearCuenta(
                    credencial, nombre, apellido, pasw);
        }
    }

    public boolean registrarCuenta(
            String credencial,
            String nombre,
            String apellido,
            String pasw,
            String tipoUsuario) {
        String tipoNormalizado = normalizarTipo(tipoUsuario);
        if (!"usuario".equals(tipoNormalizado)
                && !"administrador".equals(tipoNormalizado)
                && !"cajero".equals(tipoNormalizado)) {
            return false;
        }
        synchronized (ADMIN_LOCK) {
            return usuarioDAO.crearCuenta(
                    credencial, nombre, apellido, pasw, tipoNormalizado);
        }
    }

    private boolean autenticarPorCodigoEstudiante(
            String codigoUnico,
            String pasw) {
        if (pasw == null || pasw.isBlank()) {
            return false;
        }
        UsuarioPoliComedor encontrado =
                usuarioDAO.buscarPorCodigoUnico(codigoUnico);
        return encontrado != null
                && pasw.equals(encontrado.getContrasenia())
                && coincideTipo(encontrado, "usuario");
    }

    private boolean autenticarProfesor(
            String cedula,
            String pasw) {
        if (pasw == null || pasw.isBlank()) {
            return false;
        }
        UsuarioPoliComedor encontrado =
                usuarioDAO.buscarPorCedula(cedula);
        return encontrado != null
                && pasw.equals(encontrado.getContrasenia())
                && coincideTipo(encontrado, "usuario");
    }

    private boolean autenticarPorCredencial(
            String idUsuario,
            String pasw,
            String tipoSeleccionado) {
        if (pasw == null || pasw.isBlank()) {
            return false;
        }
        UsuarioPoliComedor encontrado =
                usuarioDAO.buscarPorId(idUsuario.trim());
        return encontrado != null
                && pasw.equals(encontrado.getContrasenia())
                && coincideTipo(encontrado, tipoSeleccionado);
    }

    private boolean coincideTipo(
            UsuarioPoliComedor usuario,
            String tipoSeleccionado) {
        String tipoRegistrado = usuario.getTipo();
        if (tipoRegistrado == null) {
            tipoRegistrado = usuario.getIdUsuarioTipo();
        }
        if ((tipoRegistrado == null || tipoRegistrado.isBlank())
                && "usuario".equals(tipoSeleccionado)) {
            return true;
        }
        return tipoSeleccionado.equals(normalizarTipo(tipoRegistrado));
    }

    private String normalizarTipo(String tipo) {
        if (tipo == null) {
            return "";
        }
        String valor = tipo.trim().toLowerCase();
        if (valor.equals("personal")
                || valor.equals("estudiante")
                || valor.equals("usuario")
                || valor.equals("1")) {
            return "usuario";
        }
        if (valor.equals("admin")
                || valor.equals("administrador")
                || valor.equals("2")) {
            return "administrador";
        }
        if (valor.equals("cashier")
                || valor.equals("cajero")
                || valor.equals("3")) {
            return "cajero";
        }
        if (valor.equals("profesor")) {
            return "usuario";
        }
        return valor;
    }
}
