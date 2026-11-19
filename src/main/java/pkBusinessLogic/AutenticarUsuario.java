package pkBusinessLogic;

import pkDataAccess.pkDAO.UsuarioPoliComedorDAO;
import pkDataAccess.pkDTO.UsuarioPoliComedor;

public class AutenticarUsuario {

    private final UsuarioPoliComedorDAO usuarioDAO;

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

        switch (metodo.toLowerCase()) {
            case "estudiante":
                return autenticarEstudiante(usuario);
            case "personal":
                return autenticarPersonal(usuario, pasw);
            case "profesor":
                return autenticarProfesor(usuario, pasw);
            default:
                return false;
        }
    }

    public boolean registrarCuenta(
            String credencial,
            String nombre,
            String apellido,
            String pasw) {
        return usuarioDAO.crearCuenta(
                credencial, nombre, apellido, pasw);
    }

    private boolean autenticarEstudiante(String codigoUnico) {
        UsuarioPoliComedor encontrado =
                usuarioDAO.buscarPorCodigoUnico(codigoUnico);
        return encontrado != null;
    }

    private boolean autenticarPersonal(
            String idUsuario,
            String pasw) {
        if (pasw == null || pasw.isBlank()) {
            return false;
        }
        UsuarioPoliComedor encontrado =
                usuarioDAO.buscarPorId(idUsuario.trim());
        return encontrado != null
                && pasw.equals(encontrado.getContrasenia());
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
                && pasw.equals(encontrado.getContrasenia());
    }
}
